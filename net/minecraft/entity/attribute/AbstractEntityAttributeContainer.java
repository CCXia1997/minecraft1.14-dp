package net.minecraft.entity.attribute;

import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import com.google.common.collect.HashMultimap;
import net.minecraft.util.LowercaseMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;

public abstract class AbstractEntityAttributeContainer
{
    protected final Map<EntityAttribute, EntityAttributeInstance> instancesByKey;
    protected final Map<String, EntityAttributeInstance> instancesById;
    protected final Multimap<EntityAttribute, EntityAttribute> attributeHierarchy;
    
    public AbstractEntityAttributeContainer() {
        this.instancesByKey = Maps.newHashMap();
        this.instancesById = new LowercaseMap<EntityAttributeInstance>();
        this.attributeHierarchy = HashMultimap.create();
    }
    
    @Nullable
    public EntityAttributeInstance get(final EntityAttribute attribute) {
        return this.instancesByKey.get(attribute);
    }
    
    @Nullable
    public EntityAttributeInstance get(final String name) {
        return this.instancesById.get(name);
    }
    
    public EntityAttributeInstance register(final EntityAttribute attribute) {
        if (this.instancesById.containsKey(attribute.getId())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        final EntityAttributeInstance entityAttributeInstance2 = this.createInstance(attribute);
        this.instancesById.put(attribute.getId(), entityAttributeInstance2);
        this.instancesByKey.put(attribute, entityAttributeInstance2);
        for (EntityAttribute entityAttribute3 = attribute.getParent(); entityAttribute3 != null; entityAttribute3 = entityAttribute3.getParent()) {
            this.attributeHierarchy.put(entityAttribute3, attribute);
        }
        return entityAttributeInstance2;
    }
    
    protected abstract EntityAttributeInstance createInstance(final EntityAttribute arg1);
    
    public Collection<EntityAttributeInstance> values() {
        return this.instancesById.values();
    }
    
    public void add(final EntityAttributeInstance instance) {
    }
    
    public void a(final Multimap<String, EntityAttributeModifier> multimap) {
        for (final Map.Entry<String, EntityAttributeModifier> entry3 : multimap.entries()) {
            final EntityAttributeInstance entityAttributeInstance4 = this.get(entry3.getKey());
            if (entityAttributeInstance4 != null) {
                entityAttributeInstance4.removeModifier(entry3.getValue());
            }
        }
    }
    
    public void b(final Multimap<String, EntityAttributeModifier> multimap) {
        for (final Map.Entry<String, EntityAttributeModifier> entry3 : multimap.entries()) {
            final EntityAttributeInstance entityAttributeInstance4 = this.get(entry3.getKey());
            if (entityAttributeInstance4 != null) {
                entityAttributeInstance4.removeModifier(entry3.getValue());
                entityAttributeInstance4.addModifier(entry3.getValue());
            }
        }
    }
}
