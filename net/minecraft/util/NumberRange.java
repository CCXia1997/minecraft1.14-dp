package net.minecraft.util;

import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.function.Supplier;
import java.util.function.Function;
import com.mojang.brigadier.StringReader;
import java.util.function.BiFunction;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public abstract class NumberRange<T extends Number>
{
    public static final SimpleCommandExceptionType EXCEPTION_EMPTY;
    public static final SimpleCommandExceptionType EXCEPTION_SWAPPED;
    protected final T min;
    protected final T max;
    
    protected NumberRange(@Nullable final T min, @Nullable final T max) {
        this.min = min;
        this.max = max;
    }
    
    @Nullable
    public T getMin() {
        return this.min;
    }
    
    @Nullable
    public T getMax() {
        return this.max;
    }
    
    public boolean isDummy() {
        return this.min == null && this.max == null;
    }
    
    public JsonElement serialize() {
        if (this.isDummy()) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        }
        final JsonObject jsonObject1 = new JsonObject();
        if (this.min != null) {
            jsonObject1.addProperty("min", this.min);
        }
        if (this.max != null) {
            jsonObject1.addProperty("max", this.max);
        }
        return jsonObject1;
    }
    
    protected static <T extends Number, R extends NumberRange<T>> R fromJson(@Nullable final JsonElement json, final R defaultValue, final BiFunction<JsonElement, String, T> biFunction, final Factory<T, R> factory) {
        if (json == null || json.isJsonNull()) {
            return defaultValue;
        }
        if (JsonHelper.isNumber(json)) {
            final T number5 = biFunction.apply(json, "value");
            return factory.create(number5, number5);
        }
        final JsonObject jsonObject5 = JsonHelper.asObject(json, "value");
        final T number6 = (T)(jsonObject5.has("min") ? ((T)biFunction.apply(jsonObject5.get("min"), "min")) : null);
        final T number7 = (T)(jsonObject5.has("max") ? ((T)biFunction.apply(jsonObject5.get("max"), "max")) : null);
        return factory.create(number6, number7);
    }
    
    protected static <T extends Number, R extends NumberRange<T>> R parse(final StringReader stringReader, final b<T, R> b, final Function<String, T> function3, final Supplier<DynamicCommandExceptionType> supplier, final Function<T, T> function5) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)stringReader);
        }
        final int integer6 = stringReader.getCursor();
        try {
            final T number7 = NumberRange.<T>applyIfNonNull((T)NumberRange.<T>fromStringReader(stringReader, (Function<String, T>)function3, supplier), function5);
            T number8;
            if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
                stringReader.skip();
                stringReader.skip();
                number8 = NumberRange.<T>applyIfNonNull((T)NumberRange.<T>fromStringReader(stringReader, (Function<String, T>)function3, supplier), function5);
                if (number7 == null && number8 == null) {
                    throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)stringReader);
                }
            }
            else {
                number8 = number7;
            }
            if (number7 == null && number8 == null) {
                throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)stringReader);
            }
            return b.create(stringReader, number7, number8);
        }
        catch (CommandSyntaxException commandSyntaxException7) {
            stringReader.setCursor(integer6);
            throw new CommandSyntaxException(commandSyntaxException7.getType(), commandSyntaxException7.getRawMessage(), commandSyntaxException7.getInput(), integer6);
        }
    }
    
    @Nullable
    private static <T extends Number> T fromStringReader(final StringReader stringReader, final Function<String, T> function, final Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
        final int integer4 = stringReader.getCursor();
        while (stringReader.canRead() && isNextCharValid(stringReader)) {
            stringReader.skip();
        }
        final String string5 = stringReader.getString().substring(integer4, stringReader.getCursor());
        if (string5.isEmpty()) {
            return null;
        }
        try {
            return function.apply(string5);
        }
        catch (NumberFormatException numberFormatException6) {
            throw supplier.get().createWithContext((ImmutableStringReader)stringReader, string5);
        }
    }
    
    private static boolean isNextCharValid(final StringReader reader) {
        final char character2 = reader.peek();
        return (character2 >= '0' && character2 <= '9') || character2 == '-' || (character2 == '.' && (!reader.canRead(2) || reader.peek(1) != '.'));
    }
    
    @Nullable
    private static <T> T applyIfNonNull(@Nullable final T object, final Function<T, T> function) {
        return (object == null) ? null : function.apply(object);
    }
    
    static {
        EXCEPTION_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.range.empty", new Object[0]));
        EXCEPTION_SWAPPED = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.range.swapped", new Object[0]));
    }
    
    public static class IntRange extends NumberRange<Integer>
    {
        public static final IntRange ANY;
        private final Long minSquared;
        private final Long maxSquared;
        
        private static IntRange a(final StringReader stringReader, @Nullable final Integer min, @Nullable final Integer max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw IntRange.EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)stringReader);
            }
            return new IntRange(min, max);
        }
        
        @Nullable
        private static Long squared(@Nullable final Integer value) {
            return (value == null) ? null : Long.valueOf(value * (long)value);
        }
        
        private IntRange(@Nullable final Integer max, @Nullable final Integer integer2) {
            super(max, integer2);
            this.minSquared = squared(max);
            this.maxSquared = squared(integer2);
        }
        
        public static IntRange exactly(final int value) {
            return new IntRange(value, value);
        }
        
        public static IntRange atLeast(final int value) {
            return new IntRange(value, null);
        }
        
        public boolean test(final int integer) {
            return (this.min == null || (int)this.min <= integer) && (this.max == null || (int)this.max >= integer);
        }
        
        public static IntRange fromJson(@Nullable final JsonElement element) {
            return NumberRange.<Integer, IntRange>fromJson(element, IntRange.ANY, JsonHelper::asInt, IntRange::new);
        }
        
        public static IntRange parse(final StringReader stringReader) throws CommandSyntaxException {
            return fromStringReader(stringReader, integer -> integer);
        }
        
        public static IntRange fromStringReader(final StringReader stringReader, final Function<Integer, Integer> function) throws CommandSyntaxException {
            return NumberRange.<Integer, IntRange>parse(stringReader, IntRange::a, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function);
        }
        
        static {
            ANY = new IntRange(null, null);
        }
    }
    
    public static class FloatRange extends NumberRange<Float>
    {
        public static final FloatRange ANY;
        private final Double minSquared;
        private final Double maxSquared;
        
        private static FloatRange create(final StringReader reader, @Nullable final Float min, @Nullable final Float max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw FloatRange.EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)reader);
            }
            return new FloatRange(min, max);
        }
        
        @Nullable
        private static Double squared(@Nullable final Float value) {
            return (value == null) ? null : Double.valueOf(value * (double)value);
        }
        
        private FloatRange(@Nullable final Float max, @Nullable final Float float2) {
            super(max, float2);
            this.minSquared = squared(max);
            this.maxSquared = squared(float2);
        }
        
        public static FloatRange atLeast(final float value) {
            return new FloatRange(value, null);
        }
        
        public boolean matches(final float float1) {
            return (this.min == null || (float)this.min <= float1) && (this.max == null || (float)this.max >= float1);
        }
        
        public boolean matchesSquared(final double double1) {
            return (this.minSquared == null || this.minSquared <= double1) && (this.maxSquared == null || this.maxSquared >= double1);
        }
        
        public static FloatRange fromJson(@Nullable final JsonElement element) {
            return NumberRange.<Float, FloatRange>fromJson(element, FloatRange.ANY, JsonHelper::asFloat, FloatRange::new);
        }
        
        public static FloatRange parse(final StringReader reader) throws CommandSyntaxException {
            return parse(reader, float1 -> float1);
        }
        
        public static FloatRange parse(final StringReader reader, final Function<Float, Float> function) throws CommandSyntaxException {
            return NumberRange.<Float, FloatRange>parse(reader, FloatRange::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function);
        }
        
        static {
            ANY = new FloatRange(null, null);
        }
    }
    
    @FunctionalInterface
    public interface b<T extends Number, R extends NumberRange<T>>
    {
        R create(final StringReader arg1, @Nullable final T arg2, @Nullable final T arg3) throws CommandSyntaxException;
    }
    
    @FunctionalInterface
    public interface Factory<T extends Number, R extends NumberRange<T>>
    {
        R create(@Nullable final T arg1, @Nullable final T arg2);
    }
}
