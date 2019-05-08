package net.minecraft.item;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import java.util.Set;

public class MiningToolItem extends ToolItem
{
    private final Set<Block> effectiveBlocks;
    protected final float blockBreakingSpeed;
    protected final float attackDamage;
    protected final float attackSpeed;
    
    protected MiningToolItem(final float attackDamage, final float attackSpeed, final ToolMaterial toolType, final Set<Block> effectiveBlocks, final Settings settings) {
        super(toolType, settings);
        this.effectiveBlocks = effectiveBlocks;
        this.blockBreakingSpeed = toolType.getBlockBreakingSpeed();
        this.attackDamage = attackDamage + toolType.getAttackDamage();
        this.attackSpeed = attackSpeed;
    }
    
    @Override
    public float getBlockBreakingSpeed(final ItemStack stack, final BlockState blockState) {
        return this.effectiveBlocks.contains(blockState.getBlock()) ? this.blockBreakingSpeed : 1.0f;
    }
    
    @Override
    public boolean onEntityDamaged(final ItemStack stack, final LivingEntity target, final LivingEntity livingEntity) {
        stack.<LivingEntity>applyDamage(2, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        return true;
    }
    
    @Override
    public boolean onBlockBroken(final ItemStack stack, final World world, final BlockState state, final BlockPos pos, final LivingEntity livingEntity) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            stack.<LivingEntity>applyDamage(1, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        }
        return true;
    }
    
    @Override
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equiptmentSlot) {
        final Multimap<String, EntityAttributeModifier> multimap2 = super.getAttributeModifiers(equiptmentSlot);
        if (equiptmentSlot == EquipmentSlot.HAND_MAIN) {
            multimap2.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(MiningToolItem.MODIFIER_DAMAGE, "Tool modifier", this.attackDamage, EntityAttributeModifier.Operation.a));
            multimap2.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(MiningToolItem.MODIFIER_SWING_SPEED, "Tool modifier", this.attackSpeed, EntityAttributeModifier.Operation.a));
        }
        return multimap2;
    }
}
