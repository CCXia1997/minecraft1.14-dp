package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import java.util.Collections;
import com.mojang.datafixers.util.Either;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.tag.Tag;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class FunctionArgumentType implements ArgumentType<FunctionArgument>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION;
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION;
    
    public static FunctionArgumentType create() {
        return new FunctionArgumentType();
    }
    
    public FunctionArgument a(final StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            final Identifier identifier2 = Identifier.parse(stringReader);
            return new FunctionArgument() {
                @Override
                public Collection<CommandFunction> getFunctions(final CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    final Tag<CommandFunction> tag2 = getFunctionTag(commandContext, identifier2);
                    return tag2.values();
                }
                
                @Override
                public Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(final CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    return (Either<CommandFunction, Tag<CommandFunction>>)Either.right(getFunctionTag(commandContext, identifier2));
                }
            };
        }
        final Identifier identifier2 = Identifier.parse(stringReader);
        return new FunctionArgument() {
            @Override
            public Collection<CommandFunction> getFunctions(final CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Collections.<CommandFunction>singleton(getFunction(commandContext, identifier2));
            }
            
            @Override
            public Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(final CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return (Either<CommandFunction, Tag<CommandFunction>>)Either.left(getFunction(commandContext, identifier2));
            }
        };
    }
    
    private static CommandFunction getFunction(final CommandContext<ServerCommandSource> context, final Identifier id) throws CommandSyntaxException {
        return ((ServerCommandSource)context.getSource()).getMinecraftServer().getCommandFunctionManager().getFunction(id).<Throwable>orElseThrow(() -> FunctionArgumentType.UNKNOWN_FUNCTION_EXCEPTION.create(id.toString()));
    }
    
    private static Tag<CommandFunction> getFunctionTag(final CommandContext<ServerCommandSource> context, final Identifier id) throws CommandSyntaxException {
        final Tag<CommandFunction> tag3 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getCommandFunctionManager().getTags().get(id);
        if (tag3 == null) {
            throw FunctionArgumentType.UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
        }
        return tag3;
    }
    
    public static Collection<CommandFunction> getFunctions(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return ((FunctionArgument)context.getArgument(name, (Class)FunctionArgument.class)).getFunctions(context);
    }
    
    public static Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(final CommandContext<ServerCommandSource> context, final String name) throws CommandSyntaxException {
        return ((FunctionArgument)context.getArgument(name, (Class)FunctionArgument.class)).getFunctionOrTag(context);
    }
    
    public Collection<String> getExamples() {
        return FunctionArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "foo:bar", "#foo");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.function.tag.unknown", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("arguments.function.unknown", new Object[] { object });
            return translatableTextComponent2;
        });
    }
    
    public interface FunctionArgument
    {
        Collection<CommandFunction> getFunctions(final CommandContext<ServerCommandSource> arg1) throws CommandSyntaxException;
        
        Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(final CommandContext<ServerCommandSource> arg1) throws CommandSyntaxException;
    }
}
