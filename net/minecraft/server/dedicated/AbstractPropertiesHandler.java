package net.minecraft.server.dedicated;

import java.util.Hashtable;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import com.google.common.base.MoreObjects;
import javax.annotation.Nullable;
import java.util.function.IntFunction;
import java.util.function.Function;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPropertiesHandler<T extends AbstractPropertiesHandler<T>>
{
    private static final Logger LOGGER;
    private final Properties properties;
    
    public AbstractPropertiesHandler(final Properties properties) {
        this.properties = properties;
    }
    
    public static Properties load(final Path path) {
        final Properties properties2 = new Properties();
        try (final InputStream inputStream3 = Files.newInputStream(path)) {
            properties2.load(inputStream3);
        }
        catch (IOException iOException3) {
            AbstractPropertiesHandler.LOGGER.error("Failed to load properties from file: " + path);
        }
        return properties2;
    }
    
    public void store(final Path path) {
        try (final OutputStream outputStream2 = Files.newOutputStream(path)) {
            this.properties.store(outputStream2, "Minecraft server properties");
        }
        catch (IOException iOException2) {
            AbstractPropertiesHandler.LOGGER.error("Failed to store properties to file: " + path);
        }
    }
    
    private static <V extends Number> Function<String, V> wrapNumberParsingFunction(final Function<String, V> function) {
        return (Function<String, V>)(string -> {
            try {
                return (V)function.apply(string);
            }
            catch (NumberFormatException numberFormatException3) {
                return null;
            }
        });
    }
    
    protected static <V> Function<String, V> wrapIntParsingFunction(final IntFunction<V> intFunction, final Function<String, V> function) {
        return (Function<String, V>)(string -> {
            try {
                return intFunction.apply(Integer.parseInt(string));
            }
            catch (NumberFormatException numberFormatException4) {
                return function.apply(string);
            }
        });
    }
    
    @Nullable
    private String getStringValue(final String string) {
        return ((Hashtable<K, String>)this.properties).get(string);
    }
    
    @Nullable
    protected <V> V getDeprecated(final String string, final Function<String, V> function) {
        final String string2 = this.getStringValue(string);
        if (string2 == null) {
            return null;
        }
        this.properties.remove(string);
        return function.apply(string2);
    }
    
    protected <V> V get(final String string, final Function<String, V> function2, final Function<V, String> function3, final V object) {
        final String string2 = this.getStringValue(string);
        final V object2 = MoreObjects.<V>firstNonNull((V)((string2 != null) ? function2.apply(string2) : null), object);
        ((Hashtable<String, String>)this.properties).put(string, function3.apply(object2));
        return object2;
    }
    
    protected <V> PropertyAccessor<V> accessor(final String string, final Function<String, V> function2, final Function<V, String> function3, final V object) {
        final String string2 = this.getStringValue(string);
        final V object2 = MoreObjects.<V>firstNonNull((V)((string2 != null) ? function2.apply(string2) : null), object);
        ((Hashtable<String, String>)this.properties).put(string, function3.apply(object2));
        return new PropertyAccessor<V>(string, object2, (Function)function3);
    }
    
    protected <V> V getWithOperation(final String string, final Function<String, V> function2, final UnaryOperator<V> unaryOperator, final Function<V, String> function4, final V object) {
        final Object object2;
        return this.<V>get(string, string -> {
            object2 = function2.apply(string);
            return (object2 != null) ? unaryOperator.apply((V)object2) : null;
        }, function4, object);
    }
    
    protected <V> V getString(final String string, final Function<String, V> function, final V object) {
        return this.<V>get(string, function, Objects::toString, object);
    }
    
    protected <V> PropertyAccessor<V> accessor(final String string, final Function<String, V> function, final V object) {
        return this.<V>accessor(string, function, Objects::toString, object);
    }
    
    protected String getString(final String string1, final String string2) {
        return this.<String>get(string1, Function.<String>identity(), Function.<String>identity(), string2);
    }
    
    @Nullable
    protected String getDeprecatedString(final String string) {
        return this.<String>getDeprecated(string, Function.<String>identity());
    }
    
    protected int getInt(final String string, final int integer) {
        return this.<Integer>getString(string, AbstractPropertiesHandler.wrapNumberParsingFunction((Function<String, V>)Integer::parseInt), integer);
    }
    
    protected PropertyAccessor<Integer> intAccessor(final String string, final int integer) {
        return this.<Integer>accessor(string, AbstractPropertiesHandler.wrapNumberParsingFunction((Function<String, V>)Integer::parseInt), integer);
    }
    
    protected int parseIntWithOperation(final String string, final UnaryOperator<Integer> unaryOperator, final int integer) {
        return this.<Integer>getWithOperation(string, AbstractPropertiesHandler.wrapNumberParsingFunction((Function<String, V>)Integer::parseInt), unaryOperator, Objects::toString, integer);
    }
    
    protected long parseLong(final String string, final long long2) {
        return this.<Long>getString(string, AbstractPropertiesHandler.wrapNumberParsingFunction((Function<String, V>)Long::parseLong), long2);
    }
    
    protected boolean parseBoolean(final String string, final boolean boolean2) {
        return this.<Boolean>getString(string, Boolean::valueOf, boolean2);
    }
    
    protected PropertyAccessor<Boolean> booleanAccessor(final String string, final boolean boolean2) {
        return this.<Boolean>accessor(string, Boolean::valueOf, boolean2);
    }
    
    @Nullable
    protected Boolean getDeprecatedBoolean(final String string) {
        return this.<Boolean>getDeprecated(string, Boolean::valueOf);
    }
    
    protected Properties getProperties() {
        final Properties properties1 = new Properties();
        properties1.putAll(this.properties);
        return properties1;
    }
    
    protected abstract T create(final Properties arg1);
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public class PropertyAccessor<V> implements Supplier<V>
    {
        private final String key;
        private final V value;
        private final Function<V, String> stringify;
        
        private PropertyAccessor(final String string, final V object, final Function<V, String> function) {
            this.key = string;
            this.value = object;
            this.stringify = function;
        }
        
        @Override
        public V get() {
            return this.value;
        }
        
        public T set(final V object) {
            final Properties properties2 = AbstractPropertiesHandler.this.getProperties();
            ((Hashtable<String, String>)properties2).put(this.key, this.stringify.apply(object));
            return AbstractPropertiesHandler.this.create(properties2);
        }
    }
}
