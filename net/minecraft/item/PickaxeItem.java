package net.minecraft.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Material;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import java.util.Set;

public class PickaxeItem extends MiningToolItem
{
    private static final Set<Block> EFFECTIVE_BLOCKS;
    
    protected PickaxeItem(final ToolMaterial toolMaterial, final int integer, final float float3, final Settings settings) {
        super((float)integer, float3, toolMaterial, PickaxeItem.EFFECTIVE_BLOCKS, settings);
    }
    
    @Override
    public boolean isEffectiveOn(final BlockState blockState) {
        final Block block2 = blockState.getBlock();
        final int integer3 = this.getType().getMiningLevel();
        if (block2 == Blocks.bJ) {
            return integer3 == 3;
        }
        if (block2 == Blocks.bS || block2 == Blocks.bR || block2 == Blocks.eb || block2 == Blocks.ef || block2 == Blocks.bD || block2 == Blocks.F || block2 == Blocks.cw) {
            return integer3 >= 2;
        }
        if (block2 == Blocks.bE || block2 == Blocks.G || block2 == Blocks.aq || block2 == Blocks.ap) {
            return integer3 >= 1;
        }
        final Material material4 = blockState.getMaterial();
        return material4 == Material.STONE || material4 == Material.METAL || material4 == Material.ANVIL;
    }
    
    @Override
    public float getBlockBreakingSpeed(final ItemStack stack, final BlockState blockState) {
        final Material material3 = blockState.getMaterial();
        if (material3 == Material.METAL || material3 == Material.ANVIL || material3 == Material.STONE) {
            return this.blockBreakingSpeed;
        }
        return super.getBlockBreakingSpeed(stack, blockState);
    }
    
    static {
        EFFECTIVE_BLOCKS = ImmutableSet.<Block>of(Blocks.fv, Blocks.H, Blocks.m, Blocks.aN, Blocks.bS, Blocks.bR, Blocks.aM, Blocks.bD, Blocks.F, Blocks.cB, Blocks.bE, Blocks.G, Blocks.aq, Blocks.ap, Blocks.bI, Blocks.cJ, Blocks.gL, Blocks.kN, Blocks.cf, Blocks.cw, Blocks.as, Blocks.at, Blocks.au, Blocks.hz, Blocks.hA, Blocks.hy, Blocks.b, Blocks.c, Blocks.d, Blocks.e, Blocks.f, Blocks.g, Blocks.h, Blocks.hI, Blocks.hJ, Blocks.hK, Blocks.hM, Blocks.hN, Blocks.hO, Blocks.hP, Blocks.hQ, Blocks.hR, Blocks.hS, Blocks.hU, Blocks.hX, Blocks.hY, Blocks.hW, Blocks.hV, Blocks.cz, Blocks.co, Blocks.lj, Blocks.lk, Blocks.ll, Blocks.lm, Blocks.ln, Blocks.lo, Blocks.lp, Blocks.lq, Blocks.lr, Blocks.ls, Blocks.lt, Blocks.lu, Blocks.lv, Blocks.iH, Blocks.iX, Blocks.iT, Blocks.iU, Blocks.iR, Blocks.iP, Blocks.iV, Blocks.iL, Blocks.iQ, Blocks.iN, Blocks.iK, Blocks.iJ, Blocks.iO, Blocks.iS, Blocks.iW, Blocks.iI, Blocks.iM);
    }
}
