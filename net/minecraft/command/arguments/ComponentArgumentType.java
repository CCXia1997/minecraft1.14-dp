package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.text.TextComponent;
import com.mojang.brigadier.arguments.ArgumentType;

public class ComponentArgumentType implements ArgumentType<TextComponent>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION;
    
    private ComponentArgumentType() {
    }
    
    public static TextComponent getComponent(final CommandContext<ServerCommandSource> context, final String name) {
        return (TextComponent)context.getArgument(name, (Class)TextComponent.class);
    }
    
    public static ComponentArgumentType create() {
        return new ComponentArgumentType();
    }
    
    public TextComponent a(final StringReader stringReader) throws CommandSyntaxException {
        try {
            final TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(stringReader);
            if (textComponent2 == null) {
                throw ComponentArgumentType.INVALID_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, "empty");
            }
            return textComponent2;
        }
        catch (JsonParseException jsonParseException2) {
            final String string3 = (jsonParseException2.getCause() != null) ? jsonParseException2.getCause().getMessage() : jsonParseException2.getMessage();
            throw ComponentArgumentType.INVALID_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, string3);
        }
    }
    
    public Collection<String> getExamples() {
        return ComponentArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
        final TranslatableTextComponent translatableTextComponent;
        INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.component.invalid", new Object[] { object });
            return translatableTextComponent;
        });
    }
}
