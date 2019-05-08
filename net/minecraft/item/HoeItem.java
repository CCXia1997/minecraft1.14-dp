package net.minecraft.item;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.ActionResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import java.util.Map;

public class HoeItem extends ToolItem
{
    private final float swingSpeed;
    protected static final Map<Block, BlockState> BLOCK_TRANSFORMATIONS_MAP;
    
    public HoeItem(final ToolMaterial toolMaterial, final float float2, final Settings settings) {
        super(toolMaterial, settings);
        this.swingSpeed = float2;
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        if (usageContext.getFacing() != Direction.DOWN && world2.getBlockState(blockPos3.up()).isAir()) {
            final BlockState blockState4 = HoeItem.BLOCK_TRANSFORMATIONS_MAP.get(world2.getBlockState(blockPos3).getBlock());
            if (blockState4 != null) {
                final PlayerEntity playerEntity2 = usageContext.getPlayer();
                world2.playSound(playerEntity2, blockPos3, SoundEvents.eD, SoundCategory.e, 1.0f, 1.0f);
                if (!world2.isClient) {
                    world2.setBlockState(blockPos3, blockState4, 11);
                    if (playerEntity2 != null) {
                        usageContext.getItemStack().<PlayerEntity>applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(usageContext.getHand()));
                    }
                }
                return ActionResult.a;
            }
        }
        return ActionResult.PASS;
    }
    
    @Override
    public boolean onEntityDamaged(final ItemStack stack, final LivingEntity target, final LivingEntity livingEntity) {
        stack.<LivingEntity>applyDamage(1, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        return true;
    }
    
    @Override
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equiptmentSlot) {
        final Multimap<String, EntityAttributeModifier> multimap2 = super.getAttributeModifiers(equiptmentSlot);
        if (equiptmentSlot == EquipmentSlot.HAND_MAIN) {
            multimap2.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(HoeItem.MODIFIER_DAMAGE, "Weapon modifier", 0.0, EntityAttributeModifier.Operation.a));
            multimap2.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(HoeItem.MODIFIER_SWING_SPEED, "Weapon modifier", this.swingSpeed, EntityAttributeModifier.Operation.a));
        }
        return multimap2;
    }
    
    static {
        BLOCK_TRANSFORMATIONS_MAP = Maps.newHashMap(ImmutableMap.<Block, BlockState>of(Blocks.i, Blocks.bV.getDefaultState(), Blocks.iw, Blocks.bV.getDefaultState(), Blocks.j, Blocks.bV.getDefaultState(), Blocks.k, Blocks.j.getDefaultState()));
    }
}
