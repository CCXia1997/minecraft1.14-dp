package net.minecraft.entity.attribute;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.Set;
import java.util.Map;

public class EntityAttributeInstanceImpl implements EntityAttributeInstance
{
    private final AbstractEntityAttributeContainer container;
    private final EntityAttribute attribute;
    private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> modifiersByOperation;
    private final Map<String, Set<EntityAttributeModifier>> modifiersByName;
    private final Map<UUID, EntityAttributeModifier> modifiersByUuid;
    private double baseValue;
    private boolean needsRefresh;
    private double cachedValue;
    
    public EntityAttributeInstanceImpl(final AbstractEntityAttributeContainer container, final EntityAttribute attribute) {
        this.modifiersByOperation = Maps.newEnumMap(EntityAttributeModifier.Operation.class);
        this.modifiersByName = Maps.newHashMap();
        this.modifiersByUuid = Maps.newHashMap();
        this.needsRefresh = true;
        this.container = container;
        this.attribute = attribute;
        this.baseValue = attribute.getDefaultValue();
        for (final EntityAttributeModifier.Operation operation6 : EntityAttributeModifier.Operation.values()) {
            this.modifiersByOperation.put(operation6, Sets.newHashSet());
        }
    }
    
    @Override
    public EntityAttribute getAttribute() {
        return this.attribute;
    }
    
    @Override
    public double getBaseValue() {
        return this.baseValue;
    }
    
    @Override
    public void setBaseValue(final double baseValue) {
        if (baseValue == this.getBaseValue()) {
            return;
        }
        this.baseValue = baseValue;
        this.invalidateCache();
    }
    
    @Override
    public Collection<EntityAttributeModifier> getModifiers(final EntityAttributeModifier.Operation operation) {
        return this.modifiersByOperation.get(operation);
    }
    
    @Override
    public Collection<EntityAttributeModifier> getModifiers() {
        final Set<EntityAttributeModifier> set1 = Sets.newHashSet();
        for (final EntityAttributeModifier.Operation operation5 : EntityAttributeModifier.Operation.values()) {
            set1.addAll(this.getModifiers(operation5));
        }
        return set1;
    }
    
    @Nullable
    @Override
    public EntityAttributeModifier getModifier(final UUID uuid) {
        return this.modifiersByUuid.get(uuid);
    }
    
    @Override
    public boolean hasModifier(final EntityAttributeModifier modifier) {
        return this.modifiersByUuid.get(modifier.getId()) != null;
    }
    
    @Override
    public void addModifier(final EntityAttributeModifier modifier) {
        if (this.getModifier(modifier.getId()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        final Set<EntityAttributeModifier> set2 = this.modifiersByName.computeIfAbsent(modifier.getName(), string -> Sets.newHashSet());
        this.modifiersByOperation.get(modifier.getOperation()).add(modifier);
        set2.add(modifier);
        this.modifiersByUuid.put(modifier.getId(), modifier);
        this.invalidateCache();
    }
    
    protected void invalidateCache() {
        this.needsRefresh = true;
        this.container.add(this);
    }
    
    @Override
    public void removeModifier(final EntityAttributeModifier modifier) {
        for (final EntityAttributeModifier.Operation operation5 : EntityAttributeModifier.Operation.values()) {
            this.modifiersByOperation.get(operation5).remove(modifier);
        }
        final Set<EntityAttributeModifier> set2 = this.modifiersByName.get(modifier.getName());
        if (set2 != null) {
            set2.remove(modifier);
            if (set2.isEmpty()) {
                this.modifiersByName.remove(modifier.getName());
            }
        }
        this.modifiersByUuid.remove(modifier.getId());
        this.invalidateCache();
    }
    
    @Override
    public void removeModifier(final UUID uuid) {
        final EntityAttributeModifier entityAttributeModifier2 = this.getModifier(uuid);
        if (entityAttributeModifier2 != null) {
            this.removeModifier(entityAttributeModifier2);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void clearModifiers() {
        Collection<EntityAttributeModifier> collection1 = this.getModifiers();
        if (collection1 == null) {
            return;
        }
        collection1 = Lists.newArrayList(collection1);
        for (final EntityAttributeModifier entityAttributeModifier3 : collection1) {
            this.removeModifier(entityAttributeModifier3);
        }
    }
    
    @Override
    public double getValue() {
        if (this.needsRefresh) {
            this.cachedValue = this.computeValue();
            this.needsRefresh = false;
        }
        return this.cachedValue;
    }
    
    private double computeValue() {
        double double1 = this.getBaseValue();
        for (final EntityAttributeModifier entityAttributeModifier4 : this.getAllModifiers(EntityAttributeModifier.Operation.a)) {
            double1 += entityAttributeModifier4.getAmount();
        }
        double double2 = double1;
        for (final EntityAttributeModifier entityAttributeModifier5 : this.getAllModifiers(EntityAttributeModifier.Operation.b)) {
            double2 += double1 * entityAttributeModifier5.getAmount();
        }
        for (final EntityAttributeModifier entityAttributeModifier5 : this.getAllModifiers(EntityAttributeModifier.Operation.c)) {
            double2 *= 1.0 + entityAttributeModifier5.getAmount();
        }
        return this.attribute.clamp(double2);
    }
    
    private Collection<EntityAttributeModifier> getAllModifiers(final EntityAttributeModifier.Operation operation) {
        final Set<EntityAttributeModifier> set2 = Sets.newHashSet(this.getModifiers(operation));
        for (EntityAttribute entityAttribute3 = this.attribute.getParent(); entityAttribute3 != null; entityAttribute3 = entityAttribute3.getParent()) {
            final EntityAttributeInstance entityAttributeInstance4 = this.container.get(entityAttribute3);
            if (entityAttributeInstance4 != null) {
                set2.addAll(entityAttributeInstance4.getModifiers(operation));
            }
        }
        return set2;
    }
}
