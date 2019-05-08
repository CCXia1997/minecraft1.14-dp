package net.minecraft.block;

import net.minecraft.util.MaterialPredicate;
import java.util.function.Predicate;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.item.Items;
import java.util.Iterator;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.BlockPattern;

public class WitherSkullBlock extends SkullBlock
{
    @Nullable
    private static BlockPattern witherBossPattern;
    @Nullable
    private static BlockPattern witherDispenserPattern;
    
    protected WitherSkullBlock(final Settings settings) {
        super(Type.WITHER_SKELETON, settings);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof SkullBlockEntity) {
            onPlaced(world, pos, (SkullBlockEntity)blockEntity6);
        }
    }
    
    public static void onPlaced(final World world, final BlockPos pos, final SkullBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        final Block block4 = blockEntity.getCachedState().getBlock();
        final boolean boolean5 = block4 == Blocks.eW || block4 == Blocks.eX;
        if (!boolean5 || pos.getY() < 2 || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        final BlockPattern blockPattern6 = getWitherBossPattern();
        final BlockPattern.Result result7 = blockPattern6.searchAround(world, pos);
        if (result7 == null) {
            return;
        }
        for (int integer8 = 0; integer8 < blockPattern6.getWidth(); ++integer8) {
            for (int integer9 = 0; integer9 < blockPattern6.getHeight(); ++integer9) {
                final CachedBlockPosition cachedBlockPosition10 = result7.translate(integer8, integer9, 0);
                world.setBlockState(cachedBlockPosition10.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                world.playLevelEvent(2001, cachedBlockPosition10.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition10.getBlockState()));
            }
        }
        final WitherEntity witherEntity8 = EntityType.WITHER.create(world);
        final BlockPos blockPos9 = result7.translate(1, 2, 0).getBlockPos();
        witherEntity8.setPositionAndAngles(blockPos9.getX() + 0.5, blockPos9.getY() + 0.55, blockPos9.getZ() + 0.5, (result7.getForwards().getAxis() == Direction.Axis.X) ? 0.0f : 90.0f, 0.0f);
        witherEntity8.aK = ((result7.getForwards().getAxis() == Direction.Axis.X) ? 0.0f : 90.0f);
        witherEntity8.l();
        for (final ServerPlayerEntity serverPlayerEntity11 : world.<ServerPlayerEntity>getEntities(ServerPlayerEntity.class, witherEntity8.getBoundingBox().expand(50.0))) {
            Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity11, witherEntity8);
        }
        world.spawnEntity(witherEntity8);
        for (int integer10 = 0; integer10 < blockPattern6.getWidth(); ++integer10) {
            for (int integer11 = 0; integer11 < blockPattern6.getHeight(); ++integer11) {
                world.updateNeighbors(result7.translate(integer10, integer11, 0).getBlockPos(), Blocks.AIR);
            }
        }
    }
    
    public static boolean canDispense(final World world, final BlockPos pos, final ItemStack stack) {
        return stack.getItem() == Items.WITHER_SKELETON_SKULL && pos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient && getWitherDispenserPattern().searchAround(world, pos) != null;
    }
    
    private static BlockPattern getWitherBossPattern() {
        if (WitherSkullBlock.witherBossPattern == null) {
            WitherSkullBlock.witherBossPattern = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.cK))).where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.eW).or(BlockStatePredicate.forBlock(Blocks.eX)))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return WitherSkullBlock.witherBossPattern;
    }
    
    private static BlockPattern getWitherDispenserPattern() {
        if (WitherSkullBlock.witherDispenserPattern == null) {
            WitherSkullBlock.witherDispenserPattern = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.cK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return WitherSkullBlock.witherDispenserPattern;
    }
}
