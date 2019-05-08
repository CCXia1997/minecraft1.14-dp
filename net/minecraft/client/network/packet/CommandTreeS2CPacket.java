package net.minecraft.client.network.packet;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import javax.annotation.Nullable;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.arguments.ArgumentTypes;
import com.mojang.brigadier.builder.ArgumentBuilder;
import java.util.Map;
import java.util.Collection;
import com.mojang.brigadier.tree.CommandNode;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayDeque;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class CommandTreeS2CPacket implements Packet<ClientPlayPacketListener>
{
    private RootCommandNode<CommandSource> commandTree;
    
    public CommandTreeS2CPacket() {
    }
    
    public CommandTreeS2CPacket(final RootCommandNode<CommandSource> commandTree) {
        this.commandTree = commandTree;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        final CommandNodeData[] arr2 = new CommandNodeData[buf.readVarInt()];
        final Deque<CommandNodeData> deque3 = new ArrayDeque<CommandNodeData>(arr2.length);
        for (int integer4 = 0; integer4 < arr2.length; ++integer4) {
            deque3.add(arr2[integer4] = this.readCommandNode(buf));
        }
        while (!deque3.isEmpty()) {
            boolean boolean4 = false;
            final Iterator<CommandNodeData> iterator5 = deque3.iterator();
            while (iterator5.hasNext()) {
                final CommandNodeData commandNodeData6 = iterator5.next();
                if (commandNodeData6.build(arr2)) {
                    iterator5.remove();
                    boolean4 = true;
                }
            }
            if (!boolean4) {
                throw new IllegalStateException("Server sent an impossible command tree");
            }
        }
        this.commandTree = (RootCommandNode<CommandSource>)arr2[buf.readVarInt()].node;
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        final Map<CommandNode<CommandSource>, Integer> map2 = Maps.newHashMap();
        final Deque<CommandNode<CommandSource>> deque3 = new ArrayDeque<CommandNode<CommandSource>>();
        deque3.add((CommandNode<CommandSource>)this.commandTree);
        while (!deque3.isEmpty()) {
            final CommandNode<CommandSource> commandNode4 = deque3.pollFirst();
            if (map2.containsKey(commandNode4)) {
                continue;
            }
            final int integer5 = map2.size();
            map2.put(commandNode4, integer5);
            deque3.addAll(commandNode4.getChildren());
            if (commandNode4.getRedirect() == null) {
                continue;
            }
            deque3.add((CommandNode<CommandSource>)commandNode4.getRedirect());
        }
        final CommandNode<CommandSource>[] arr4 = new CommandNode[map2.size()];
        for (final Map.Entry<CommandNode<CommandSource>, Integer> entry6 : map2.entrySet()) {
            arr4[entry6.getValue()] = entry6.getKey();
        }
        buf.writeVarInt(arr4.length);
        for (final CommandNode<CommandSource> commandNode5 : arr4) {
            this.writeNode(buf, commandNode5, map2);
        }
        buf.writeVarInt(map2.get(this.commandTree));
    }
    
    private CommandNodeData readCommandNode(final PacketByteBuf buf) {
        final byte byte2 = buf.readByte();
        final int[] arr3 = buf.readIntArray();
        final int integer4 = ((byte2 & 0x8) != 0x0) ? buf.readVarInt() : 0;
        final ArgumentBuilder<CommandSource, ?> argumentBuilder5 = this.readArgumentBuilder(buf, byte2);
        return new CommandNodeData((ArgumentBuilder)argumentBuilder5, byte2, integer4, arr3);
    }
    
    @Nullable
    private ArgumentBuilder<CommandSource, ?> readArgumentBuilder(final PacketByteBuf buf, final byte byte2) {
        final int integer3 = byte2 & 0x3;
        if (integer3 == 2) {
            final String string4 = buf.readString(32767);
            final ArgumentType<?> argumentType5 = ArgumentTypes.fromPacket(buf);
            if (argumentType5 == null) {
                return null;
            }
            final RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder6 = RequiredArgumentBuilder.argument(string4, (ArgumentType)argumentType5);
            if ((byte2 & 0x10) != 0x0) {
                requiredArgumentBuilder6.suggests((SuggestionProvider)SuggestionProviders.byId(buf.readIdentifier()));
            }
            return requiredArgumentBuilder6;
        }
        else {
            if (integer3 == 1) {
                return LiteralArgumentBuilder.literal(buf.readString(32767));
            }
            return null;
        }
    }
    
    private void writeNode(final PacketByteBuf buf, final CommandNode<CommandSource> node, final Map<CommandNode<CommandSource>, Integer> map) {
        byte byte4 = 0;
        if (node.getRedirect() != null) {
            byte4 |= 0x8;
        }
        if (node.getCommand() != null) {
            byte4 |= 0x4;
        }
        if (node instanceof RootCommandNode) {
            byte4 |= 0x0;
        }
        else if (node instanceof ArgumentCommandNode) {
            byte4 |= 0x2;
            if (((ArgumentCommandNode)node).getCustomSuggestions() != null) {
                byte4 |= 0x10;
            }
        }
        else {
            if (!(node instanceof LiteralCommandNode)) {
                throw new UnsupportedOperationException("Unknown node type " + node);
            }
            byte4 |= 0x1;
        }
        buf.writeByte(byte4);
        buf.writeVarInt(node.getChildren().size());
        for (final CommandNode<CommandSource> commandNode6 : node.getChildren()) {
            buf.writeVarInt(map.get(commandNode6));
        }
        if (node.getRedirect() != null) {
            buf.writeVarInt(map.get(node.getRedirect()));
        }
        if (node instanceof ArgumentCommandNode) {
            final ArgumentCommandNode<CommandSource, ?> argumentCommandNode5 = node;
            buf.writeString(argumentCommandNode5.getName());
            ArgumentTypes.<ArgumentType>toPacket(buf, argumentCommandNode5.getType());
            if (argumentCommandNode5.getCustomSuggestions() != null) {
                buf.writeIdentifier(SuggestionProviders.computeName((SuggestionProvider<CommandSource>)argumentCommandNode5.getCustomSuggestions()));
            }
        }
        else if (node instanceof LiteralCommandNode) {
            buf.writeString(((LiteralCommandNode)node).getLiteral());
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onCommandTree(this);
    }
    
    @Environment(EnvType.CLIENT)
    public RootCommandNode<CommandSource> getCommandTree() {
        return this.commandTree;
    }
    
    static class CommandNodeData
    {
        @Nullable
        private final ArgumentBuilder<CommandSource, ?> argumentBuilder;
        private final byte flags;
        private final int redirectNodeIndex;
        private final int[] childNodeIndices;
        private CommandNode<CommandSource> node;
        
        private CommandNodeData(@Nullable final ArgumentBuilder<CommandSource, ?> argumentBuilder, final byte flags, final int redirectNodeIndex, final int[] childNodeIndices) {
            this.argumentBuilder = argumentBuilder;
            this.flags = flags;
            this.redirectNodeIndex = redirectNodeIndex;
            this.childNodeIndices = childNodeIndices;
        }
        
        public boolean build(final CommandNodeData[] previousNodes) {
            if (this.node == null) {
                if (this.argumentBuilder == null) {
                    this.node = (CommandNode<CommandSource>)new RootCommandNode();
                }
                else {
                    if ((this.flags & 0x8) != 0x0) {
                        if (previousNodes[this.redirectNodeIndex].node == null) {
                            return false;
                        }
                        this.argumentBuilder.redirect((CommandNode)previousNodes[this.redirectNodeIndex].node);
                    }
                    if ((this.flags & 0x4) != 0x0) {
                        this.argumentBuilder.executes(commandContext -> 0);
                    }
                    this.node = (CommandNode<CommandSource>)this.argumentBuilder.build();
                }
            }
            for (final int integer5 : this.childNodeIndices) {
                if (previousNodes[integer5].node == null) {
                    return false;
                }
            }
            for (final int integer5 : this.childNodeIndices) {
                final CommandNode<CommandSource> commandNode6 = previousNodes[integer5].node;
                if (!(commandNode6 instanceof RootCommandNode)) {
                    this.node.addChild((CommandNode)commandNode6);
                }
            }
            return true;
        }
    }
}
