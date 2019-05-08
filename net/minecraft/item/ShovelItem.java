package net.minecraft.item;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.ActionResult;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import java.util.Map;
import net.minecraft.block.Block;
import java.util.Set;

public class ShovelItem extends MiningToolItem
{
    private static final Set<Block> EFFECTIVE_BLOCKS;
    protected static final Map<Block, BlockState> BLOCK_TRANSFORMATIONS_MAP;
    
    public ShovelItem(final ToolMaterial toolMaterial, final float float2, final float float3, final Settings settings) {
        super(float2, float3, toolMaterial, ShovelItem.EFFECTIVE_BLOCKS, settings);
    }
    
    @Override
    public boolean isEffectiveOn(final BlockState blockState) {
        final Block block2 = blockState.getBlock();
        return block2 == Blocks.cA || block2 == Blocks.cC;
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        if (usageContext.getFacing() != Direction.DOWN && world2.getBlockState(blockPos3.up()).isAir()) {
            final BlockState blockState4 = ShovelItem.BLOCK_TRANSFORMATIONS_MAP.get(world2.getBlockState(blockPos3).getBlock());
            if (blockState4 != null) {
                final PlayerEntity playerEntity2 = usageContext.getPlayer();
                world2.playSound(playerEntity2, blockPos3, SoundEvents.jW, SoundCategory.e, 1.0f, 1.0f);
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
    
    static {
        EFFECTIVE_BLOCKS = Sets.<Block>newHashSet(Blocks.cE, Blocks.j, Blocks.k, Blocks.l, Blocks.bV, Blocks.i, Blocks.E, Blocks.dL, Blocks.C, Blocks.D, Blocks.cC, Blocks.cA, Blocks.cK, Blocks.iw, Blocks.jE, Blocks.jF, Blocks.jG, Blocks.jH, Blocks.jI, Blocks.jJ, Blocks.jK, Blocks.jL, Blocks.jM, Blocks.jN, Blocks.jO, Blocks.jP, Blocks.jQ, Blocks.jR, Blocks.jS, Blocks.jT);
        BLOCK_TRANSFORMATIONS_MAP = Maps.newHashMap(ImmutableMap.<Block, BlockState>of(Blocks.i, Blocks.iw.getDefaultState()));
    }
}
