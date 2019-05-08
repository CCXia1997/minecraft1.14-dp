package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.BedBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.state.property.Property;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;

public class CatSitOnBlockGoal extends MoveToTargetPosGoal
{
    private final CatEntity cat;
    
    public CatSitOnBlockGoal(final CatEntity catEntity, final double double2) {
        super(catEntity, double2, 8);
        this.cat = catEntity;
    }
    
    @Override
    public boolean canStart() {
        return this.cat.isTamed() && !this.cat.isSitting() && super.canStart();
    }
    
    @Override
    public void start() {
        super.start();
        this.cat.getSitGoal().setEnabledWithOwner(false);
    }
    
    @Override
    public void stop() {
        super.stop();
        this.cat.setSitting(false);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.cat.getSitGoal().setEnabledWithOwner(false);
        if (!this.hasReached()) {
            this.cat.setSitting(false);
        }
        else if (!this.cat.isSitting()) {
            this.cat.setSitting(true);
        }
    }
    
    @Override
    protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
        if (!world.isAir(pos.up())) {
            return false;
        }
        final BlockState blockState3 = world.getBlockState(pos);
        final Block block4 = blockState3.getBlock();
        if (block4 == Blocks.bP) {
            return ChestBlockEntity.getPlayersLookingInChestCount(world, pos) < 1;
        }
        return (block4 == Blocks.bW && blockState3.<Boolean>get((Property<Boolean>)FurnaceBlock.LIT)) || (block4.matches(BlockTags.F) && blockState3.<BedPart>get(BedBlock.PART) != BedPart.a);
    }
}
