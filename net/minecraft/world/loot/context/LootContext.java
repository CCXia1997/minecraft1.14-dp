package net.minecraft.world.loot.context;

import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;
import net.minecraft.entity.Entity;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.world.loot.LootSupplier;
import java.util.Set;
import net.minecraft.world.loot.LootManager;
import net.minecraft.server.world.ServerWorld;
import java.util.Random;

public class LootContext
{
    private final Random random;
    private final float luck;
    private final ServerWorld world;
    private final LootManager manager;
    private final Set<LootSupplier> suppliers;
    private final Map<LootContextParameter<?>, Object> parameters;
    private final Map<Identifier, Dropper> drops;
    
    private LootContext(final Random random, final float luck, final ServerWorld world, final LootManager manager, final Map<LootContextParameter<?>, Object> parameters, final Map<Identifier, Dropper> drops) {
        this.suppliers = Sets.newLinkedHashSet();
        this.random = random;
        this.luck = luck;
        this.world = world;
        this.manager = manager;
        this.parameters = ImmutableMap.copyOf(parameters);
        this.drops = ImmutableMap.copyOf(drops);
    }
    
    public boolean hasParameter(final LootContextParameter<?> parameter) {
        return this.parameters.containsKey(parameter);
    }
    
    public void drop(final Identifier id, final Consumer<ItemStack> itemDropper) {
        final Dropper dropper3 = this.drops.get(id);
        if (dropper3 != null) {
            dropper3.add(this, itemDropper);
        }
    }
    
    @Nullable
    public <T> T get(final LootContextParameter<T> parameter) {
        return (T)this.parameters.get(parameter);
    }
    
    public boolean addDrop(final LootSupplier supplier) {
        return this.suppliers.add(supplier);
    }
    
    public void removeDrop(final LootSupplier supplier) {
        this.suppliers.remove(supplier);
    }
    
    public LootManager getLootManager() {
        return this.manager;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    public float getLuck() {
        return this.luck;
    }
    
    public ServerWorld getWorld() {
        return this.world;
    }
    
    public static class Builder
    {
        private final ServerWorld world;
        private final Map<LootContextParameter<?>, Object> parameters;
        private final Map<Identifier, Dropper> drops;
        private Random random;
        private float luck;
        
        public Builder(final ServerWorld world) {
            this.parameters = Maps.newIdentityHashMap();
            this.drops = Maps.newHashMap();
            this.world = world;
        }
        
        public Builder setRandom(final Random random) {
            this.random = random;
            return this;
        }
        
        public Builder setRandom(final long seed) {
            if (seed != 0L) {
                this.random = new Random(seed);
            }
            return this;
        }
        
        public Builder setRandom(final long seed, final Random random) {
            if (seed == 0L) {
                this.random = random;
            }
            else {
                this.random = new Random(seed);
            }
            return this;
        }
        
        public Builder setLuck(final float luck) {
            this.luck = luck;
            return this;
        }
        
        public <T> Builder put(final LootContextParameter<T> key, final T value) {
            this.parameters.put(key, value);
            return this;
        }
        
        public <T> Builder putNullable(final LootContextParameter<T> key, @Nullable final T value) {
            if (value == null) {
                this.parameters.remove(key);
            }
            else {
                this.parameters.put(key, value);
            }
            return this;
        }
        
        public Builder putDrop(final Identifier id, final Dropper value) {
            final Dropper dropper3 = this.drops.put(id, value);
            if (dropper3 != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.drops + "'");
            }
            return this;
        }
        
        public ServerWorld getWorld() {
            return this.world;
        }
        
        public <T> T get(final LootContextParameter<T> parameter) {
            final T object2 = (T)this.parameters.get(parameter);
            if (object2 == null) {
                throw new IllegalArgumentException("No parameter " + parameter);
            }
            return object2;
        }
        
        @Nullable
        public <T> T getNullable(final LootContextParameter<T> parameter) {
            return (T)this.parameters.get(parameter);
        }
        
        public LootContext build(final LootContextType type) {
            final Set<LootContextParameter<?>> set2 = Sets.<LootContextParameter<?>>difference(this.parameters.keySet(), type.getAllowed());
            if (!set2.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set2);
            }
            final Set<LootContextParameter<?>> set3 = Sets.<LootContextParameter<?>>difference(type.getRequired(), this.parameters.keySet());
            if (!set3.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameters: " + set3);
            }
            Random random4 = this.random;
            if (random4 == null) {
                random4 = new Random();
            }
            return new LootContext(random4, this.luck, this.world, this.world.getServer().getLootManager(), this.parameters, this.drops, null);
        }
    }
    
    public enum EntityTarget
    {
        THIS("this", LootContextParameters.a), 
        KILLER("killer", LootContextParameters.d), 
        DIRECT_KILLER("direct_killer", LootContextParameters.e), 
        KILLER_PLAYER("killer_player", LootContextParameters.b);
        
        private final String type;
        private final LootContextParameter<? extends Entity> identifier;
        
        private EntityTarget(final String type, final LootContextParameter<? extends Entity> parameter) {
            this.type = type;
            this.identifier = parameter;
        }
        
        public LootContextParameter<? extends Entity> getIdentifier() {
            return this.identifier;
        }
        
        public static EntityTarget fromString(final String type) {
            for (final EntityTarget entityTarget5 : values()) {
                if (entityTarget5.type.equals(type)) {
                    return entityTarget5;
                }
            }
            throw new IllegalArgumentException("Invalid entity target " + type);
        }
        
        public static class Serializer extends TypeAdapter<EntityTarget>
        {
            public void a(final JsonWriter writer, final EntityTarget entity) throws IOException {
                writer.value(entity.type);
            }
            
            public EntityTarget a(final JsonReader reader) throws IOException {
                return EntityTarget.fromString(reader.nextString());
            }
        }
    }
    
    @FunctionalInterface
    public interface Dropper
    {
        void add(final LootContext arg1, final Consumer<ItemStack> arg2);
    }
}
