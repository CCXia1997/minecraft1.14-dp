package net.minecraft.command;

import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;

public class TextComponentBuiltInExceptionProvider implements BuiltInExceptionProvider
{
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_LOW_EXCEPTION;
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_HIGH_EXCEPTION;
    private static final Dynamic2CommandExceptionType FLOAT_TOO_LOW_EXCEPTION;
    private static final Dynamic2CommandExceptionType FLOAT_TOO_HIGH_EXCEPTION;
    private static final Dynamic2CommandExceptionType INTEGER_TOO_LOW_EXCEPTION;
    private static final Dynamic2CommandExceptionType INTEGER_TOO_HIGH_EXCEPTION;
    private static final Dynamic2CommandExceptionType LONG_TOO_LOW_EXCEPTION;
    private static final Dynamic2CommandExceptionType LONG_TOO_HIGH_EXCEPTION;
    private static final DynamicCommandExceptionType EXPECTED_LITERAL_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_START_QUOTE_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_END_QUOTE_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_ESCAPE_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_BOOLEAN_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_INTEGER_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_INTEGER_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_LONG_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_LONG_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_DOUBLE_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_DOUBLE_EXCEPTION;
    private static final DynamicCommandExceptionType INVALID_FLOAT_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_FLOAT_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_BOOLEAN_EXCEPTION;
    private static final DynamicCommandExceptionType EXPECTED_EXCEPTION;
    private static final SimpleCommandExceptionType UNKNOWN_COMMAND_EXCEPTION;
    private static final SimpleCommandExceptionType UNKNOWN_ARGUMENT_EXCEPTION;
    private static final SimpleCommandExceptionType EXPECTED_SEPARATOR_EXCEPTION;
    private static final DynamicCommandExceptionType COMMAND_EXCEPTION;
    
    public Dynamic2CommandExceptionType doubleTooLow() {
        return TextComponentBuiltInExceptionProvider.DOUBLE_TOO_LOW_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType doubleTooHigh() {
        return TextComponentBuiltInExceptionProvider.DOUBLE_TOO_HIGH_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType floatTooLow() {
        return TextComponentBuiltInExceptionProvider.FLOAT_TOO_LOW_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType floatTooHigh() {
        return TextComponentBuiltInExceptionProvider.FLOAT_TOO_HIGH_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType integerTooLow() {
        return TextComponentBuiltInExceptionProvider.INTEGER_TOO_LOW_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType integerTooHigh() {
        return TextComponentBuiltInExceptionProvider.INTEGER_TOO_HIGH_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType longTooLow() {
        return TextComponentBuiltInExceptionProvider.LONG_TOO_LOW_EXCEPTION;
    }
    
    public Dynamic2CommandExceptionType longTooHigh() {
        return TextComponentBuiltInExceptionProvider.LONG_TOO_HIGH_EXCEPTION;
    }
    
    public DynamicCommandExceptionType literalIncorrect() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_LITERAL_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedStartOfQuote() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_START_QUOTE_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedEndOfQuote() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_END_QUOTE_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerInvalidEscape() {
        return TextComponentBuiltInExceptionProvider.INVALID_ESCAPE_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerInvalidBool() {
        return TextComponentBuiltInExceptionProvider.INVALID_BOOLEAN_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerInvalidInt() {
        return TextComponentBuiltInExceptionProvider.INVALID_INTEGER_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedInt() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_INTEGER_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerInvalidLong() {
        return TextComponentBuiltInExceptionProvider.INVALID_LONG_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedLong() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_LONG_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerInvalidDouble() {
        return TextComponentBuiltInExceptionProvider.INVALID_DOUBLE_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedDouble() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_DOUBLE_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerInvalidFloat() {
        return TextComponentBuiltInExceptionProvider.INVALID_FLOAT_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedFloat() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_FLOAT_EXCEPTION;
    }
    
    public SimpleCommandExceptionType readerExpectedBool() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_BOOLEAN_EXCEPTION;
    }
    
    public DynamicCommandExceptionType readerExpectedSymbol() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_EXCEPTION;
    }
    
    public SimpleCommandExceptionType dispatcherUnknownCommand() {
        return TextComponentBuiltInExceptionProvider.UNKNOWN_COMMAND_EXCEPTION;
    }
    
    public SimpleCommandExceptionType dispatcherUnknownArgument() {
        return TextComponentBuiltInExceptionProvider.UNKNOWN_ARGUMENT_EXCEPTION;
    }
    
    public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
        return TextComponentBuiltInExceptionProvider.EXPECTED_SEPARATOR_EXCEPTION;
    }
    
    public DynamicCommandExceptionType dispatcherParseException() {
        return TextComponentBuiltInExceptionProvider.COMMAND_EXCEPTION;
    }
    
    static {
        DOUBLE_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.double.low", new Object[] { object2, object1 }));
        DOUBLE_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.double.big", new Object[] { object2, object1 }));
        FLOAT_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.float.low", new Object[] { object2, object1 }));
        FLOAT_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.float.big", new Object[] { object2, object1 }));
        INTEGER_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.integer.low", new Object[] { object2, object1 }));
        INTEGER_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.integer.big", new Object[] { object2, object1 }));
        LONG_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.long.low", new Object[] { object2, object1 }));
        LONG_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("argument.long.big", new Object[] { object2, object1 }));
        final TranslatableTextComponent translatableTextComponent;
        EXPECTED_LITERAL_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.literal.incorrect", new Object[] { object });
            return translatableTextComponent;
        });
        EXPECTED_START_QUOTE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.quote.expected.start", new Object[0]));
        EXPECTED_END_QUOTE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.quote.expected.end", new Object[0]));
        final TranslatableTextComponent translatableTextComponent2;
        INVALID_ESCAPE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.quote.escape", new Object[] { object });
            return translatableTextComponent2;
        });
        final TranslatableTextComponent translatableTextComponent3;
        INVALID_BOOLEAN_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.bool.invalid", new Object[] { object });
            return translatableTextComponent3;
        });
        final TranslatableTextComponent translatableTextComponent4;
        INVALID_INTEGER_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.int.invalid", new Object[] { object });
            return translatableTextComponent4;
        });
        EXPECTED_INTEGER_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.int.expected", new Object[0]));
        final TranslatableTextComponent translatableTextComponent5;
        INVALID_LONG_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.long.invalid", new Object[] { object });
            return translatableTextComponent5;
        });
        EXPECTED_LONG_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.long.expected", new Object[0]));
        final TranslatableTextComponent translatableTextComponent6;
        INVALID_DOUBLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.double.invalid", new Object[] { object });
            return translatableTextComponent6;
        });
        EXPECTED_DOUBLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.double.expected", new Object[0]));
        final TranslatableTextComponent translatableTextComponent7;
        INVALID_FLOAT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.float.invalid", new Object[] { object });
            return translatableTextComponent7;
        });
        EXPECTED_FLOAT_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.float.expected", new Object[0]));
        EXPECTED_BOOLEAN_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("parsing.bool.expected", new Object[0]));
        final TranslatableTextComponent translatableTextComponent8;
        EXPECTED_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("parsing.expected", new Object[] { object });
            return translatableTextComponent8;
        });
        UNKNOWN_COMMAND_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("command.unknown.command", new Object[0]));
        UNKNOWN_ARGUMENT_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("command.unknown.argument", new Object[0]));
        EXPECTED_SEPARATOR_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("command.expected.separator", new Object[0]));
        final TranslatableTextComponent translatableTextComponent9;
        COMMAND_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("command.exception", new Object[] { object });
            return translatableTextComponent9;
        });
    }
}
