package net.minecraft.command.arguments;

import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import java.util.List;
import net.minecraft.command.EntitySelectorReader;
import com.google.common.collect.Lists;
import net.minecraft.text.StringTextComponent;
import java.util.Arrays;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class MessageArgumentType implements ArgumentType<MessageFormat>
{
    private static final Collection<String> EXAMPLES;
    
    public static MessageArgumentType create() {
        return new MessageArgumentType();
    }
    
    public static TextComponent getMessage(final CommandContext<ServerCommandSource> command, final String name) throws CommandSyntaxException {
        return ((MessageFormat)command.getArgument(name, (Class)MessageFormat.class)).format((ServerCommandSource)command.getSource(), ((ServerCommandSource)command.getSource()).hasPermissionLevel(2));
    }
    
    public MessageFormat a(final StringReader stringReader) throws CommandSyntaxException {
        return MessageFormat.parse(stringReader, true);
    }
    
    public Collection<String> getExamples() {
        return MessageArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("Hello world!", "foo", "@e", "Hello @p :)");
    }
    
    public static class MessageFormat
    {
        private final String contents;
        private final MessageSelector[] selectors;
        
        public MessageFormat(final String string, final MessageSelector[] arr) {
            this.contents = string;
            this.selectors = arr;
        }
        
        public TextComponent format(final ServerCommandSource serverCommandSource, final boolean boolean2) throws CommandSyntaxException {
            if (this.selectors.length == 0 || !boolean2) {
                return new StringTextComponent(this.contents);
            }
            final TextComponent textComponent3 = new StringTextComponent(this.contents.substring(0, this.selectors[0].getStart()));
            int integer4 = this.selectors[0].getStart();
            for (final MessageSelector messageSelector8 : this.selectors) {
                final TextComponent textComponent4 = messageSelector8.format(serverCommandSource);
                if (integer4 < messageSelector8.getStart()) {
                    textComponent3.append(this.contents.substring(integer4, messageSelector8.getStart()));
                }
                if (textComponent4 != null) {
                    textComponent3.append(textComponent4);
                }
                integer4 = messageSelector8.getEnd();
            }
            if (integer4 < this.contents.length()) {
                textComponent3.append(this.contents.substring(integer4, this.contents.length()));
            }
            return textComponent3;
        }
        
        public static MessageFormat parse(final StringReader stringReader, final boolean boolean2) throws CommandSyntaxException {
            final String string3 = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
            if (!boolean2) {
                stringReader.setCursor(stringReader.getTotalLength());
                return new MessageFormat(string3, new MessageSelector[0]);
            }
            final List<MessageSelector> list4 = Lists.newArrayList();
            final int integer5 = stringReader.getCursor();
            while (stringReader.canRead()) {
                if (stringReader.peek() == '@') {
                    final int integer6 = stringReader.getCursor();
                    EntitySelector entitySelector7;
                    try {
                        final EntitySelectorReader entitySelectorReader8 = new EntitySelectorReader(stringReader);
                        entitySelector7 = entitySelectorReader8.read();
                    }
                    catch (CommandSyntaxException commandSyntaxException8) {
                        if (commandSyntaxException8.getType() == EntitySelectorReader.MISSING_EXCEPTION || commandSyntaxException8.getType() == EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
                            stringReader.setCursor(integer6 + 1);
                            continue;
                        }
                        throw commandSyntaxException8;
                    }
                    list4.add(new MessageSelector(integer6 - integer5, stringReader.getCursor() - integer5, entitySelector7));
                }
                else {
                    stringReader.skip();
                }
            }
            return new MessageFormat(string3, list4.<MessageSelector>toArray(new MessageSelector[list4.size()]));
        }
    }
    
    public static class MessageSelector
    {
        private final int start;
        private final int end;
        private final EntitySelector selector;
        
        public MessageSelector(final int integer1, final int integer2, final EntitySelector entitySelector) {
            this.start = integer1;
            this.end = integer2;
            this.selector = entitySelector;
        }
        
        public int getStart() {
            return this.start;
        }
        
        public int getEnd() {
            return this.end;
        }
        
        @Nullable
        public TextComponent format(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            return EntitySelector.getNames(this.selector.getEntities(serverCommandSource));
        }
    }
}
