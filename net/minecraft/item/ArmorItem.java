package net.minecraft.item;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.property.Property;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.block.dispenser.DispenserBehavior;
import java.util.UUID;

public class ArmorItem extends Item
{
    private static final UUID[] MODIFIERS;
    public static final DispenserBehavior DISPENSER_BEHAVIOR;
    protected final EquipmentSlot slot;
    protected final int protection;
    protected final float toughness;
    protected final ArmorMaterial type;
    
    public static ItemStack dispenseArmor(final BlockPointer block, final ItemStack armor) {
        final BlockPos blockPos3 = block.getBlockPos().offset(block.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
        final List<LivingEntity> list4 = block.getWorld().<LivingEntity>getEntities(LivingEntity.class, new BoundingBox(blockPos3), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.CanPickup(armor)));
        if (list4.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final LivingEntity livingEntity5 = list4.get(0);
        final EquipmentSlot equipmentSlot6 = MobEntity.getPreferredEquipmentSlot(armor);
        final ItemStack itemStack7 = armor.split(1);
        livingEntity5.setEquippedStack(equipmentSlot6, itemStack7);
        if (livingEntity5 instanceof MobEntity) {
            ((MobEntity)livingEntity5).setEquipmentDropChance(equipmentSlot6, 2.0f);
            ((MobEntity)livingEntity5).setPersistent();
        }
        return armor;
    }
    
    public ArmorItem(final ArmorMaterial material, final EquipmentSlot slot, final Settings settings) {
        super(settings.durabilityIfNotSet(material.getDurability(slot)));
        this.type = material;
        this.slot = slot;
        this.protection = material.getProtectionAmount(slot);
        this.toughness = material.getToughness();
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }
    
    public EquipmentSlot getSlotType() {
        return this.slot;
    }
    
    @Override
    public int getEnchantability() {
        return this.type.getEnchantability();
    }
    
    public ArmorMaterial getMaterial() {
        return this.type;
    }
    
    @Override
    public boolean canRepair(final ItemStack tool, final ItemStack ingredient) {
        return this.type.getRepairIngredient().a(ingredient) || super.canRepair(tool, ingredient);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final EquipmentSlot equipmentSlot5 = MobEntity.getPreferredEquipmentSlot(itemStack4);
        final ItemStack itemStack5 = player.getEquippedStack(equipmentSlot5);
        if (itemStack5.isEmpty()) {
            player.setEquippedStack(equipmentSlot5, itemStack4.copy());
            itemStack4.setAmount(0);
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
    
    @Override
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equiptmentSlot) {
        final Multimap<String, EntityAttributeModifier> multimap2 = super.getAttributeModifiers(equiptmentSlot);
        if (equiptmentSlot == this.slot) {
            multimap2.put(EntityAttributes.ARMOR.getId(), new EntityAttributeModifier(ArmorItem.MODIFIERS[equiptmentSlot.getEntitySlotId()], "Armor modifier", this.protection, EntityAttributeModifier.Operation.a));
            multimap2.put(EntityAttributes.ARMOR_TOUGHNESS.getId(), new EntityAttributeModifier(ArmorItem.MODIFIERS[equiptmentSlot.getEntitySlotId()], "Armor toughness", this.toughness, EntityAttributeModifier.Operation.a));
        }
        return multimap2;
    }
    
    public int getProtection() {
        return this.protection;
    }
    
    static {
        MODIFIERS = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };
        DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final ItemStack itemStack3 = ArmorItem.dispenseArmor(pointer, stack);
                return itemStack3.isEmpty() ? super.dispenseStack(pointer, stack) : itemStack3;
            }
        };
    }
}
