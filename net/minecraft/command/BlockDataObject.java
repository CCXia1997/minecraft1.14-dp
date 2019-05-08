package net.minecraft.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.command.DataCommand;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BlockDataObject implements DataCommandObject
{
    private static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION;
    public static final Function<String, DataCommand.ObjectType> a;
    private final BlockEntity blockEntity;
    private final BlockPos pos;
    
    public BlockDataObject(final BlockEntity blockEntity, final BlockPos blockPos) {
        this.blockEntity = blockEntity;
        this.pos = blockPos;
    }
    
    @Override
    public void setTag(final CompoundTag compoundTag) {
        compoundTag.putInt("x", this.pos.getX());
        compoundTag.putInt("y", this.pos.getY());
        compoundTag.putInt("z", this.pos.getZ());
        this.blockEntity.fromTag(compoundTag);
        this.blockEntity.markDirty();
        final BlockState blockState2 = this.blockEntity.getWorld().getBlockState(this.pos);
        this.blockEntity.getWorld().updateListeners(this.pos, blockState2, blockState2, 3);
    }
    
    @Override
    public CompoundTag getTag() {
        return this.blockEntity.toTag(new CompoundTag());
    }
    
    @Override
    public TextComponent getModifiedFeedback() {
        return new TranslatableTextComponent("commands.data.block.modified", new Object[] { this.pos.getX(), this.pos.getY(), this.pos.getZ() });
    }
    
    @Override
    public TextComponent getQueryFeedback(final Tag tag) {
        return new TranslatableTextComponent("commands.data.block.query", new Object[] { this.pos.getX(), this.pos.getY(), this.pos.getZ(), tag.toTextComponent() });
    }
    
    @Override
    public TextComponent getGetFeedback(final NbtPathArgumentType.NbtPath nbtPath, final double double2, final int integer4) {
        return new TranslatableTextComponent("commands.data.block.get", new Object[] { nbtPath, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", double2), integer4 });
    }
    
    static {
        INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.data.block.invalid", new Object[0]));
        a = (string -> new DataCommand.ObjectType() {
            final /* synthetic */ String a;
            
            @Override
            public DataCommandObject getObject(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                final BlockPos blockPos2 = BlockPosArgumentType.getLoadedBlockPos(context, string + "Pos");
                final BlockEntity blockEntity3 = ((ServerCommandSource)context.getSource()).getWorld().getBlockEntity(blockPos2);
                if (blockEntity3 == null) {
                    throw BlockDataObject.INVALID_BLOCK_EXCEPTION.create();
                }
                return new BlockDataObject(blockEntity3, blockPos2);
            }
            
            @Override
            public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(final ArgumentBuilder<ServerCommandSource, ?> argument, final Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
                return argument.then(CommandManager.literal("block").then((ArgumentBuilder)argumentAdder.apply(CommandManager.argument(string + "Pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()))));
            }
        });
    }
}
