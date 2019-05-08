package net.minecraft.entity.attribute;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import org.apache.logging.log4j.Logger;

public class EntityAttributes
{
    private static final Logger LOGGER;
    public static final EntityAttribute MAX_HEALTH;
    public static final EntityAttribute FOLLOW_RANGE;
    public static final EntityAttribute KNOCKBACK_RESISTANCE;
    public static final EntityAttribute MOVEMENT_SPEED;
    public static final EntityAttribute FLYING_SPEED;
    public static final EntityAttribute ATTACK_DAMAGE;
    public static final EntityAttribute ATTACK_KNOCKBACK;
    public static final EntityAttribute ATTACK_SPEED;
    public static final EntityAttribute ARMOR;
    public static final EntityAttribute ARMOR_TOUGHNESS;
    public static final EntityAttribute LUCK;
    
    public static ListTag toTag(final AbstractEntityAttributeContainer container) {
        final ListTag listTag2 = new ListTag();
        for (final EntityAttributeInstance entityAttributeInstance4 : container.values()) {
            ((AbstractList<CompoundTag>)listTag2).add(toTag(entityAttributeInstance4));
        }
        return listTag2;
    }
    
    private static CompoundTag toTag(final EntityAttributeInstance instance) {
        final CompoundTag compoundTag2 = new CompoundTag();
        final EntityAttribute entityAttribute3 = instance.getAttribute();
        compoundTag2.putString("Name", entityAttribute3.getId());
        compoundTag2.putDouble("Base", instance.getBaseValue());
        final Collection<EntityAttributeModifier> collection4 = instance.getModifiers();
        if (collection4 != null && !collection4.isEmpty()) {
            final ListTag listTag5 = new ListTag();
            for (final EntityAttributeModifier entityAttributeModifier7 : collection4) {
                if (entityAttributeModifier7.shouldSerialize()) {
                    ((AbstractList<CompoundTag>)listTag5).add(toTag(entityAttributeModifier7));
                }
            }
            compoundTag2.put("Modifiers", listTag5);
        }
        return compoundTag2;
    }
    
    public static CompoundTag toTag(final EntityAttributeModifier modifier) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("Name", modifier.getName());
        compoundTag2.putDouble("Amount", modifier.getAmount());
        compoundTag2.putInt("Operation", modifier.getOperation().getId());
        compoundTag2.putUuid("UUID", modifier.getId());
        return compoundTag2;
    }
    
    public static void fromTag(final AbstractEntityAttributeContainer container, final ListTag listTag) {
        for (int integer3 = 0; integer3 < listTag.size(); ++integer3) {
            final CompoundTag compoundTag4 = listTag.getCompoundTag(integer3);
            final EntityAttributeInstance entityAttributeInstance5 = container.get(compoundTag4.getString("Name"));
            if (entityAttributeInstance5 == null) {
                EntityAttributes.LOGGER.warn("Ignoring unknown attribute '{}'", compoundTag4.getString("Name"));
            }
            else {
                fromTag(entityAttributeInstance5, compoundTag4);
            }
        }
    }
    
    private static void fromTag(final EntityAttributeInstance instance, final CompoundTag compoundTag) {
        instance.setBaseValue(compoundTag.getDouble("Base"));
        if (compoundTag.containsKey("Modifiers", 9)) {
            final ListTag listTag3 = compoundTag.getList("Modifiers", 10);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                final EntityAttributeModifier entityAttributeModifier5 = createFromTag(listTag3.getCompoundTag(integer4));
                if (entityAttributeModifier5 != null) {
                    final EntityAttributeModifier entityAttributeModifier6 = instance.getModifier(entityAttributeModifier5.getId());
                    if (entityAttributeModifier6 != null) {
                        instance.removeModifier(entityAttributeModifier6);
                    }
                    instance.addModifier(entityAttributeModifier5);
                }
            }
        }
    }
    
    @Nullable
    public static EntityAttributeModifier createFromTag(final CompoundTag tag) {
        final UUID uUID2 = tag.getUuid("UUID");
        try {
            final EntityAttributeModifier.Operation operation3 = EntityAttributeModifier.Operation.fromId(tag.getInt("Operation"));
            return new EntityAttributeModifier(uUID2, tag.getString("Name"), tag.getDouble("Amount"), operation3);
        }
        catch (Exception exception3) {
            EntityAttributes.LOGGER.warn("Unable to create attribute: {}", exception3.getMessage());
            return null;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MAX_HEALTH = new ClampedEntityAttribute(null, "generic.maxHealth", 20.0, 0.0, 1024.0).setName("Max Health").setTracked(true);
        FOLLOW_RANGE = new ClampedEntityAttribute(null, "generic.followRange", 32.0, 0.0, 2048.0).setName("Follow Range");
        KNOCKBACK_RESISTANCE = new ClampedEntityAttribute(null, "generic.knockbackResistance", 0.0, 0.0, 1.0).setName("Knockback Resistance");
        MOVEMENT_SPEED = new ClampedEntityAttribute(null, "generic.movementSpeed", 0.699999988079071, 0.0, 1024.0).setName("Movement Speed").setTracked(true);
        FLYING_SPEED = new ClampedEntityAttribute(null, "generic.flyingSpeed", 0.4000000059604645, 0.0, 1024.0).setName("Flying Speed").setTracked(true);
        ATTACK_DAMAGE = new ClampedEntityAttribute(null, "generic.attackDamage", 2.0, 0.0, 2048.0);
        ATTACK_KNOCKBACK = new ClampedEntityAttribute(null, "generic.attackKnockback", 0.0, 0.0, 5.0);
        ATTACK_SPEED = new ClampedEntityAttribute(null, "generic.attackSpeed", 4.0, 0.0, 1024.0).setTracked(true);
        ARMOR = new ClampedEntityAttribute(null, "generic.armor", 0.0, 0.0, 30.0).setTracked(true);
        ARMOR_TOUGHNESS = new ClampedEntityAttribute(null, "generic.armorToughness", 0.0, 0.0, 20.0).setTracked(true);
        LUCK = new ClampedEntityAttribute(null, "generic.luck", 0.0, -1024.0, 1024.0).setTracked(true);
    }
}
