package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.util.LowercaseMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;

public class EntityAttributeContainer extends AbstractEntityAttributeContainer
{
    private final Set<EntityAttributeInstance> trackedAttributes;
    protected final Map<String, EntityAttributeInstance> instancesByName;
    
    public EntityAttributeContainer() {
        this.trackedAttributes = Sets.newHashSet();
        this.instancesByName = new LowercaseMap<EntityAttributeInstance>();
    }
    
    @Override
    public EntityAttributeInstanceImpl get(final EntityAttribute attribute) {
        return (EntityAttributeInstanceImpl)super.get(attribute);
    }
    
    @Override
    public EntityAttributeInstanceImpl get(final String name) {
        EntityAttributeInstance entityAttributeInstance2 = super.get(name);
        if (entityAttributeInstance2 == null) {
            entityAttributeInstance2 = this.instancesByName.get(name);
        }
        return (EntityAttributeInstanceImpl)entityAttributeInstance2;
    }
    
    @Override
    public EntityAttributeInstance register(final EntityAttribute attribute) {
        final EntityAttributeInstance entityAttributeInstance2 = super.register(attribute);
        if (attribute instanceof ClampedEntityAttribute && ((ClampedEntityAttribute)attribute).getName() != null) {
            this.instancesByName.put(((ClampedEntityAttribute)attribute).getName(), entityAttributeInstance2);
        }
        return entityAttributeInstance2;
    }
    
    @Override
    protected EntityAttributeInstance createInstance(final EntityAttribute attribute) {
        return new EntityAttributeInstanceImpl(this, attribute);
    }
    
    @Override
    public void add(final EntityAttributeInstance instance) {
        if (instance.getAttribute().isTracked()) {
            this.trackedAttributes.add(instance);
        }
        for (final EntityAttribute entityAttribute3 : this.attributeHierarchy.get(instance.getAttribute())) {
            final EntityAttributeInstanceImpl entityAttributeInstanceImpl4 = this.get(entityAttribute3);
            if (entityAttributeInstanceImpl4 != null) {
                entityAttributeInstanceImpl4.invalidateCache();
            }
        }
    }
    
    public Set<EntityAttributeInstance> getTrackedAttributes() {
        return this.trackedAttributes;
    }
    
    public Collection<EntityAttributeInstance> buildTrackedAttributesCollection() {
        final Set<EntityAttributeInstance> set1 = Sets.newHashSet();
        for (final EntityAttributeInstance entityAttributeInstance3 : this.values()) {
            if (entityAttributeInstance3.getAttribute().isTracked()) {
                set1.add(entityAttributeInstance3);
            }
        }
        return set1;
    }
}
