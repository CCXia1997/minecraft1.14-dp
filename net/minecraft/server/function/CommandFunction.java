package net.minecraft.server.function;

import java.util.Optional;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.Identifier;

public class CommandFunction
{
    private final Element[] elements;
    private final Identifier id;
    
    public CommandFunction(final Identifier identifier, final Element[] arr) {
        this.id = identifier;
        this.elements = arr;
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    public Element[] getElements() {
        return this.elements;
    }
    
    public static CommandFunction create(final Identifier identifier, final CommandFunctionManager commandFunctionManager, final List<String> fileLines) {
        final List<Element> list4 = Lists.newArrayListWithCapacity(fileLines.size());
        for (int integer5 = 0; integer5 < fileLines.size(); ++integer5) {
            final int integer6 = integer5 + 1;
            final String string7 = fileLines.get(integer5).trim();
            final StringReader stringReader8 = new StringReader(string7);
            if (stringReader8.canRead()) {
                if (stringReader8.peek() != '#') {
                    if (stringReader8.peek() == '/') {
                        stringReader8.skip();
                        if (stringReader8.peek() == '/') {
                            throw new IllegalArgumentException("Unknown or invalid command '" + string7 + "' on line " + integer6 + " (if you intended to make a comment, use '#' not '//')");
                        }
                        final String string8 = stringReader8.readUnquotedString();
                        throw new IllegalArgumentException("Unknown or invalid command '" + string7 + "' on line " + integer6 + " (did you mean '" + string8 + "'? Do not use a preceding forwards slash.)");
                    }
                    else {
                        try {
                            final ParseResults<ServerCommandSource> parseResults9 = (ParseResults<ServerCommandSource>)commandFunctionManager.getServer().getCommandManager().getDispatcher().parse(stringReader8, commandFunctionManager.getFunctionCommandSource());
                            if (parseResults9.getReader().canRead()) {
                                if (parseResults9.getExceptions().size() == 1) {
                                    throw (CommandSyntaxException)parseResults9.getExceptions().values().iterator().next();
                                }
                                if (parseResults9.getContext().getRange().isEmpty()) {
                                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults9.getReader());
                                }
                                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults9.getReader());
                            }
                            else {
                                list4.add(new CommandElement(parseResults9));
                            }
                        }
                        catch (CommandSyntaxException commandSyntaxException9) {
                            throw new IllegalArgumentException("Whilst parsing command on line " + integer6 + ": " + commandSyntaxException9.getMessage());
                        }
                    }
                }
            }
        }
        return new CommandFunction(identifier, list4.<Element>toArray(new Element[0]));
    }
    
    public static class CommandElement implements Element
    {
        private final ParseResults<ServerCommandSource> parsed;
        
        public CommandElement(final ParseResults<ServerCommandSource> parseResults) {
            this.parsed = parseResults;
        }
        
        @Override
        public void execute(final CommandFunctionManager commandFunctionManager, final ServerCommandSource serverCommandSource, final ArrayDeque<CommandFunctionManager.Entry> arrayDeque, final int integer) throws CommandSyntaxException {
            commandFunctionManager.getDispatcher().execute(new ParseResults(this.parsed.getContext().withSource(serverCommandSource), this.parsed.getReader(), this.parsed.getExceptions()));
        }
        
        @Override
        public String toString() {
            return this.parsed.getReader().getString();
        }
    }
    
    public static class FunctionElement implements Element
    {
        private final LazyContainer function;
        
        public FunctionElement(final CommandFunction commandFunction) {
            this.function = new LazyContainer(commandFunction);
        }
        
        @Override
        public void execute(final CommandFunctionManager commandFunctionManager, final ServerCommandSource serverCommandSource, final ArrayDeque<CommandFunctionManager.Entry> arrayDeque, final int integer) {
            final Element[] arr6;
            final int integer2;
            final int integer3;
            int integer4;
            this.function.get(commandFunctionManager).ifPresent(commandFunction -> {
                arr6 = commandFunction.getElements();
                integer2 = integer - arrayDeque.size();
                integer3 = Math.min(arr6.length, integer2);
                for (integer4 = integer3 - 1; integer4 >= 0; --integer4) {
                    arrayDeque.addFirst(new CommandFunctionManager.Entry(commandFunctionManager, serverCommandSource, arr6[integer4]));
                }
            });
        }
        
        @Override
        public String toString() {
            return "function " + this.function.getId();
        }
    }
    
    public static class LazyContainer
    {
        public static final LazyContainer EMPTY;
        @Nullable
        private final Identifier id;
        private boolean initialized;
        private Optional<CommandFunction> function;
        
        public LazyContainer(@Nullable final Identifier identifier) {
            this.function = Optional.<CommandFunction>empty();
            this.id = identifier;
        }
        
        public LazyContainer(final CommandFunction commandFunction) {
            this.function = Optional.<CommandFunction>empty();
            this.initialized = true;
            this.id = null;
            this.function = Optional.<CommandFunction>of(commandFunction);
        }
        
        public Optional<CommandFunction> get(final CommandFunctionManager commandFunctionManager) {
            if (!this.initialized) {
                if (this.id != null) {
                    this.function = commandFunctionManager.getFunction(this.id);
                }
                this.initialized = true;
            }
            return this.function;
        }
        
        @Nullable
        public Identifier getId() {
            return this.function.<Identifier>map(commandFunction -> commandFunction.id).orElse(this.id);
        }
        
        static {
            EMPTY = new LazyContainer((Identifier)null);
        }
    }
    
    public interface Element
    {
        void execute(final CommandFunctionManager arg1, final ServerCommandSource arg2, final ArrayDeque<CommandFunctionManager.Entry> arg3, final int arg4) throws CommandSyntaxException;
    }
}
