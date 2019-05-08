package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.arguments.ArgumentType;

public class EntitySummonArgumentType implements ArgumentType<Identifier>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION;
    
    public static EntitySummonArgumentType create() {
        return new EntitySummonArgumentType();
    }
    
    public static Identifier getEntitySummon(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        return validate((Identifier)commandContext.getArgument(string, (Class)Identifier.class));
    }
    
    private static Identifier validate(final Identifier identifier) throws CommandSyntaxException {
        Registry.ENTITY_TYPE.getOrEmpty(identifier).filter(EntityType::isSummonable).<Throwable>orElseThrow(() -> EntitySummonArgumentType.NOT_FOUND_EXCEPTION.create(identifier));
        return identifier;
    }
    
    public Identifier a(final StringReader stringReader) throws CommandSyntaxException {
        return validate(Identifier.parse(stringReader));
    }
    
    public Collection<String> getExamples() {
        return EntitySummonArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("minecraft:pig", "cow");
        final TranslatableTextComponent translatableTextComponent;
        NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("entity.notFound", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
