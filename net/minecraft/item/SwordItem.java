package net.minecraft.item;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.BlockView;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.Material;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;

public class SwordItem extends ToolItem
{
    private final float weaponDamage;
    private final float weaponCooldownSpeed;
    
    public SwordItem(final ToolMaterial material, final int baseDamage, final float cooldownSpeed, final Settings settings) {
        super(material, settings);
        this.weaponCooldownSpeed = cooldownSpeed;
        this.weaponDamage = baseDamage + material.getAttackDamage();
    }
    
    public float getWeaponDamage() {
        return this.weaponDamage;
    }
    
    @Override
    public boolean beforeBlockBreak(final BlockState blockState, final World world, final BlockPos position, final PlayerEntity player) {
        return !player.isCreative();
    }
    
    @Override
    public float getBlockBreakingSpeed(final ItemStack stack, final BlockState blockState) {
        final Block block3 = blockState.getBlock();
        if (block3 == Blocks.aP) {
            return 15.0f;
        }
        final Material material4 = blockState.getMaterial();
        if (material4 == Material.PLANT || material4 == Material.REPLACEABLE_PLANT || material4 == Material.UNUSED_PLANT || blockState.matches(BlockTags.C) || material4 == Material.PUMPKIN) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    @Override
    public boolean onEntityDamaged(final ItemStack stack, final LivingEntity target, final LivingEntity livingEntity) {
        stack.<LivingEntity>applyDamage(1, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        return true;
    }
    
    @Override
    public boolean onBlockBroken(final ItemStack stack, final World world, final BlockState state, final BlockPos pos, final LivingEntity livingEntity) {
        if (state.getHardness(world, pos) != 0.0f) {
            stack.<LivingEntity>applyDamage(2, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        }
        return true;
    }
    
    @Override
    public boolean isEffectiveOn(final BlockState blockState) {
        return blockState.getBlock() == Blocks.aP;
    }
    
    @Override
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equiptmentSlot) {
        final Multimap<String, EntityAttributeModifier> multimap2 = super.getAttributeModifiers(equiptmentSlot);
        if (equiptmentSlot == EquipmentSlot.HAND_MAIN) {
            multimap2.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(SwordItem.MODIFIER_DAMAGE, "Weapon modifier", this.weaponDamage, EntityAttributeModifier.Operation.a));
            multimap2.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(SwordItem.MODIFIER_SWING_SPEED, "Weapon modifier", this.weaponCooldownSpeed, EntityAttributeModifier.Operation.a));
        }
        return multimap2;
    }
}
