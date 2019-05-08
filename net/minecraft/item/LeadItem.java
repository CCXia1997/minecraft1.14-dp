package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;

public class LeadItem extends Item
{
    public LeadItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final Block block4 = world2.getBlockState(blockPos3).getBlock();
        if (block4.matches(BlockTags.G)) {
            final PlayerEntity playerEntity5 = usageContext.getPlayer();
            if (!world2.isClient && playerEntity5 != null) {
                attachNearbyEntities(playerEntity5, world2, blockPos3);
            }
            return ActionResult.a;
        }
        return ActionResult.PASS;
    }
    
    public static boolean attachNearbyEntities(final PlayerEntity player, final World world, final BlockPos pos) {
        LeadKnotEntity leadKnotEntity4 = null;
        boolean boolean5 = false;
        final double double6 = 7.0;
        final int integer8 = pos.getX();
        final int integer9 = pos.getY();
        final int integer10 = pos.getZ();
        final List<MobEntity> list11 = world.<MobEntity>getEntities(MobEntity.class, new BoundingBox(integer8 - 7.0, integer9 - 7.0, integer10 - 7.0, integer8 + 7.0, integer9 + 7.0, integer10 + 7.0));
        for (final MobEntity mobEntity13 : list11) {
            if (mobEntity13.getHoldingEntity() == player) {
                if (leadKnotEntity4 == null) {
                    leadKnotEntity4 = LeadKnotEntity.getOrCreate(world, pos);
                }
                mobEntity13.attachLeash(leadKnotEntity4, true);
                boolean5 = true;
            }
        }
        return boolean5;
    }
}
