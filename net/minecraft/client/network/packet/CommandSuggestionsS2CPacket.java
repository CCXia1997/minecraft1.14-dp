package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import net.minecraft.text.TextFormatter;
import java.io.IOException;
import net.minecraft.text.TextComponent;
import java.util.List;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import net.minecraft.util.PacketByteBuf;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class CommandSuggestionsS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int completionId;
    private Suggestions suggestions;
    
    public CommandSuggestionsS2CPacket() {
    }
    
    public CommandSuggestionsS2CPacket(final int completionId, final Suggestions suggestions) {
        this.completionId = completionId;
        this.suggestions = suggestions;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.completionId = buf.readVarInt();
        final int integer2 = buf.readVarInt();
        final int integer3 = buf.readVarInt();
        final StringRange stringRange4 = StringRange.between(integer2, integer2 + integer3);
        final int integer4 = buf.readVarInt();
        final List<Suggestion> list6 = Lists.newArrayListWithCapacity(integer4);
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            final String string8 = buf.readString(32767);
            final TextComponent textComponent9 = buf.readBoolean() ? buf.readTextComponent() : null;
            list6.add(new Suggestion(stringRange4, string8, (Message)textComponent9));
        }
        this.suggestions = new Suggestions(stringRange4, (List)list6);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.completionId);
        buf.writeVarInt(this.suggestions.getRange().getStart());
        buf.writeVarInt(this.suggestions.getRange().getLength());
        buf.writeVarInt(this.suggestions.getList().size());
        for (final Suggestion suggestion3 : this.suggestions.getList()) {
            buf.writeString(suggestion3.getText());
            buf.writeBoolean(suggestion3.getTooltip() != null);
            if (suggestion3.getTooltip() != null) {
                buf.writeTextComponent(TextFormatter.message(suggestion3.getTooltip()));
            }
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onCommandSuggestions(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getCompletionId() {
        return this.completionId;
    }
    
    @Environment(EnvType.CLIENT)
    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}
