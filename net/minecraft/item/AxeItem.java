package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.block.PillarBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.block.Material;
import net.minecraft.block.BlockState;
import java.util.Map;
import net.minecraft.block.Block;
import java.util.Set;

public class AxeItem extends MiningToolItem
{
    private static final Set<Block> EFFECTIVE_BLOCKS;
    protected static final Map<Block, Block> BLOCK_TRANSFORMATIONS_MAP;
    
    protected AxeItem(final ToolMaterial material, final float attackDamage, final float attackSpeed, final Settings settings) {
        super(attackDamage, attackSpeed, material, AxeItem.EFFECTIVE_BLOCKS, settings);
    }
    
    @Override
    public float getBlockBreakingSpeed(final ItemStack stack, final BlockState blockState) {
        final Material material3 = blockState.getMaterial();
        if (material3 == Material.WOOD || material3 == Material.PLANT || material3 == Material.REPLACEABLE_PLANT || material3 == Material.BAMBOO) {
            return this.blockBreakingSpeed;
        }
        return super.getBlockBreakingSpeed(stack, blockState);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        final Block block5 = AxeItem.BLOCK_TRANSFORMATIONS_MAP.get(blockState4.getBlock());
        if (block5 != null) {
            final PlayerEntity playerEntity2 = usageContext.getPlayer();
            world2.playSound(playerEntity2, blockPos3, SoundEvents.E, SoundCategory.e, 1.0f, 1.0f);
            if (!world2.isClient) {
                world2.setBlockState(blockPos3, ((AbstractPropertyContainer<O, BlockState>)block5.getDefaultState()).<Direction.Axis, Comparable>with(PillarBlock.AXIS, (Comparable)blockState4.<V>get((Property<V>)PillarBlock.AXIS)), 11);
                if (playerEntity2 != null) {
                    usageContext.getItemStack().<PlayerEntity>applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(usageContext.getHand()));
                }
            }
            return ActionResult.a;
        }
        return ActionResult.PASS;
    }
    
    static {
        EFFECTIVE_BLOCKS = Sets.<Block>newHashSet(Blocks.n, Blocks.o, Blocks.p, Blocks.q, Blocks.r, Blocks.s, Blocks.bH, Blocks.U, Blocks.V, Blocks.W, Blocks.X, Blocks.Y, Blocks.Z, Blocks.I, Blocks.J, Blocks.K, Blocks.L, Blocks.M, Blocks.N, Blocks.bP, Blocks.cI, Blocks.cN, Blocks.cO, Blocks.dC, Blocks.ce, Blocks.lI, Blocks.eO, Blocks.eP, Blocks.eQ, Blocks.eR, Blocks.eT, Blocks.eS, Blocks.cq, Blocks.cr, Blocks.cs, Blocks.ct, Blocks.cv, Blocks.cu);
        BLOCK_TRANSFORMATIONS_MAP = new ImmutableMap.Builder<Block, Block>().put(Blocks.U, Blocks.aa).put(Blocks.I, Blocks.T).put(Blocks.Z, Blocks.af).put(Blocks.N, Blocks.S).put(Blocks.Y, Blocks.ae).put(Blocks.M, Blocks.R).put(Blocks.W, Blocks.ac).put(Blocks.K, Blocks.P).put(Blocks.X, Blocks.ad).put(Blocks.L, Blocks.Q).put(Blocks.V, Blocks.ab).put(Blocks.J, Blocks.O).build();
    }
}
