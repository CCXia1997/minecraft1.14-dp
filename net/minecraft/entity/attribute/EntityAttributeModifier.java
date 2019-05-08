package net.minecraft.entity.attribute;

import java.util.Objects;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.UUID;
import java.util.function.Supplier;

public class EntityAttributeModifier
{
    private final double amount;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID uuid;
    private boolean serialize;
    
    public EntityAttributeModifier(final String name, final double amount, final Operation operation4) {
        this(MathHelper.randomUuid((Random)ThreadLocalRandom.current()), () -> name, amount, operation4);
    }
    
    public EntityAttributeModifier(final UUID uuid, final String name, final double amount, final Operation operation5) {
        this(uuid, () -> name, amount, operation5);
    }
    
    public EntityAttributeModifier(final UUID uuid, final Supplier<String> nameGetter, final double amount, final Operation operation5) {
        this.serialize = true;
        this.uuid = uuid;
        this.nameGetter = nameGetter;
        this.amount = amount;
        this.operation = operation5;
    }
    
    public UUID getId() {
        return this.uuid;
    }
    
    public String getName() {
        return this.nameGetter.get();
    }
    
    public Operation getOperation() {
        return this.operation;
    }
    
    public double getAmount() {
        return this.amount;
    }
    
    public boolean shouldSerialize() {
        return this.serialize;
    }
    
    public EntityAttributeModifier setSerialize(final boolean serialize) {
        this.serialize = serialize;
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EntityAttributeModifier entityAttributeModifier2 = (EntityAttributeModifier)o;
        return Objects.equals(this.uuid, entityAttributeModifier2.uuid);
    }
    
    @Override
    public int hashCode() {
        return (this.uuid != null) ? this.uuid.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + this.nameGetter.get() + '\'' + ", id=" + this.uuid + ", serialize=" + this.serialize + '}';
    }
    
    public enum Operation
    {
        a(0), 
        b(1), 
        c(2);
        
        private static final Operation[] VALUES;
        private final int id;
        
        private Operation(final int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
        
        public static Operation fromId(final int id) {
            if (id < 0 || id >= Operation.VALUES.length) {
                throw new IllegalArgumentException("No operation with value " + id);
            }
            return Operation.VALUES[id];
        }
        
        static {
            VALUES = new Operation[] { Operation.a, Operation.b, Operation.c };
        }
    }
}
