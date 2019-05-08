package net.minecraft.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.predicate.NbtPredicate;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.DataCommand;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class EntityDataObject implements DataCommandObject
{
    private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION;
    public static final Function<String, DataCommand.ObjectType> a;
    private final Entity c;
    
    public EntityDataObject(final Entity entity) {
        this.c = entity;
    }
    
    @Override
    public void setTag(final CompoundTag compoundTag) throws CommandSyntaxException {
        if (this.c instanceof PlayerEntity) {
            throw EntityDataObject.INVALID_ENTITY_EXCEPTION.create();
        }
        final UUID uUID2 = this.c.getUuid();
        this.c.fromTag(compoundTag);
        this.c.setUuid(uUID2);
    }
    
    @Override
    public CompoundTag getTag() {
        return NbtPredicate.entityToTag(this.c);
    }
    
    @Override
    public TextComponent getModifiedFeedback() {
        return new TranslatableTextComponent("commands.data.entity.modified", new Object[] { this.c.getDisplayName() });
    }
    
    @Override
    public TextComponent getQueryFeedback(final Tag tag) {
        return new TranslatableTextComponent("commands.data.entity.query", new Object[] { this.c.getDisplayName(), tag.toTextComponent() });
    }
    
    @Override
    public TextComponent getGetFeedback(final NbtPathArgumentType.NbtPath nbtPath, final double double2, final int integer4) {
        return new TranslatableTextComponent("commands.data.entity.get", new Object[] { nbtPath, this.c.getDisplayName(), String.format(Locale.ROOT, "%.2f", double2), integer4 });
    }
    
    static {
        INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.data.entity.invalid", new Object[0]));
        a = (string -> new DataCommand.ObjectType() {
            final /* synthetic */ String a;
            
            @Override
            public DataCommandObject getObject(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                return new EntityDataObject(EntityArgumentType.getEntity(context, string));
            }
            
            @Override
            public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(final ArgumentBuilder<ServerCommandSource, ?> argument, final Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
                return argument.then(CommandManager.literal("entity").then((ArgumentBuilder)argumentAdder.apply(CommandManager.argument(string, (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entity()))));
            }
        });
    }
}
