package net.minecraft.world;

import net.minecraft.state.AbstractPropertyContainer;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.state.property.Property;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.server.world.ChunkTicketType;
import org.apache.logging.log4j.util.Supplier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.util.math.ColumnPos;
import java.util.Map;
import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.PortalBlock;
import org.apache.logging.log4j.Logger;

public class PortalForcer
{
    private static final Logger LOGGER;
    private static final PortalBlock PORTAL_BLOCK;
    private final ServerWorld world;
    private final Random random;
    private final Map<ColumnPos, TicketInfo> ticketInfos;
    private final Object2LongMap<ColumnPos> activePortals;
    
    public PortalForcer(final ServerWorld world) {
        this.ticketInfos = Maps.newHashMapWithExpectedSize(4096);
        this.activePortals = (Object2LongMap<ColumnPos>)new Object2LongOpenHashMap();
        this.world = world;
        this.random = new Random(world.getSeed());
    }
    
    public boolean usePortal(final Entity entity, final float float2) {
        final Vec3d vec3d3 = entity.bw();
        final Direction direction4 = entity.bx();
        final BlockPattern.TeleportTarget teleportTarget5 = this.getPortal(new BlockPos(entity), entity.getVelocity(), direction4, vec3d3.x, vec3d3.y, entity instanceof PlayerEntity);
        if (teleportTarget5 == null) {
            return false;
        }
        final Vec3d vec3d4 = teleportTarget5.pos;
        final Vec3d vec3d5 = teleportTarget5.velocity;
        entity.setVelocity(vec3d5);
        entity.yaw = float2 + teleportTarget5.yaw;
        if (entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)entity).networkHandler.requestTeleport(vec3d4.x, vec3d4.y, vec3d4.z, entity.yaw, entity.pitch);
            ((ServerPlayerEntity)entity).networkHandler.syncWithPlayerPosition();
        }
        else {
            entity.setPositionAndAngles(vec3d4.x, vec3d4.y, vec3d4.z, entity.yaw, entity.pitch);
        }
        return true;
    }
    
    @Nullable
    public BlockPattern.TeleportTarget getPortal(final BlockPos pos, final Vec3d vec3d, final Direction direction, final double x, final double y, final boolean canActivate) {
        final int integer9 = 128;
        boolean boolean10 = true;
        BlockPos blockPos11 = null;
        final ColumnPos columnPos12 = new ColumnPos(pos);
        if (!canActivate && this.activePortals.containsKey(columnPos12)) {
            return null;
        }
        final TicketInfo ticketInfo13 = this.ticketInfos.get(columnPos12);
        if (ticketInfo13 != null) {
            blockPos11 = ticketInfo13.pos;
            ticketInfo13.lastUsedTime = this.world.getTime();
            boolean10 = false;
        }
        else {
            double double14 = Double.MAX_VALUE;
            for (int integer10 = -128; integer10 <= 128; ++integer10) {
                for (int integer11 = -128; integer11 <= 128; ++integer11) {
                    BlockPos blockPos13;
                    for (BlockPos blockPos12 = pos.add(integer10, this.world.getEffectiveHeight() - 1 - pos.getY(), integer11); blockPos12.getY() >= 0; blockPos12 = blockPos13) {
                        blockPos13 = blockPos12.down();
                        if (this.world.getBlockState(blockPos12).getBlock() == PortalForcer.PORTAL_BLOCK) {
                            for (blockPos13 = blockPos12.down(); this.world.getBlockState(blockPos13).getBlock() == PortalForcer.PORTAL_BLOCK; blockPos13 = blockPos12.down()) {
                                blockPos12 = blockPos13;
                            }
                            final double double15 = blockPos12.getSquaredDistance(pos);
                            if (double14 < 0.0 || double15 < double14) {
                                double14 = double15;
                                blockPos11 = blockPos12;
                            }
                        }
                    }
                }
            }
        }
        if (blockPos11 == null) {
            final long long14 = this.world.getTime() + 300L;
            this.activePortals.put(columnPos12, long14);
            return null;
        }
        if (boolean10) {
            this.ticketInfos.put(columnPos12, new TicketInfo(blockPos11, this.world.getTime()));
            PortalForcer.LOGGER.debug("Adding nether portal ticket for {}:{}", new Supplier[] { this.world.getDimension()::getType, () -> columnPos12 });
            this.world.getChunkManager().<ColumnPos>addTicket(ChunkTicketType.f, new ChunkPos(blockPos11), 3, columnPos12);
        }
        final BlockPattern.Result result14 = PortalForcer.PORTAL_BLOCK.findPortal(this.world, blockPos11);
        return result14.a(direction, blockPos11, y, vec3d, x);
    }
    
    public boolean createPortal(final Entity entity) {
        final int integer2 = 16;
        double double3 = -1.0;
        final int integer3 = MathHelper.floor(entity.x);
        final int integer4 = MathHelper.floor(entity.y);
        final int integer5 = MathHelper.floor(entity.z);
        int integer6 = integer3;
        int integer7 = integer4;
        int integer8 = integer5;
        int integer9 = 0;
        final int integer10 = this.random.nextInt(4);
        final BlockPos.Mutable mutable13 = new BlockPos.Mutable();
        for (int integer11 = integer3 - 16; integer11 <= integer3 + 16; ++integer11) {
            final double double4 = integer11 + 0.5 - entity.x;
            for (int integer12 = integer5 - 16; integer12 <= integer5 + 16; ++integer12) {
                final double double5 = integer12 + 0.5 - entity.z;
            Label_0463:
                for (int integer13 = this.world.getEffectiveHeight() - 1; integer13 >= 0; --integer13) {
                    if (this.world.isAir(mutable13.set(integer11, integer13, integer12))) {
                        while (integer13 > 0 && this.world.isAir(mutable13.set(integer11, integer13 - 1, integer12))) {
                            --integer13;
                        }
                        for (int integer14 = integer10; integer14 < integer10 + 4; ++integer14) {
                            int integer15 = integer14 % 2;
                            int integer16 = 1 - integer15;
                            if (integer14 % 4 >= 2) {
                                integer15 = -integer15;
                                integer16 = -integer16;
                            }
                            for (int integer17 = 0; integer17 < 3; ++integer17) {
                                for (int integer18 = 0; integer18 < 4; ++integer18) {
                                    for (int integer19 = -1; integer19 < 4; ++integer19) {
                                        final int integer20 = integer11 + (integer18 - 1) * integer15 + integer17 * integer16;
                                        final int integer21 = integer13 + integer19;
                                        final int integer22 = integer12 + (integer18 - 1) * integer16 - integer17 * integer15;
                                        mutable13.set(integer20, integer21, integer22);
                                        if (integer19 < 0 && !this.world.getBlockState(mutable13).getMaterial().isSolid()) {
                                            continue Label_0463;
                                        }
                                        if (integer19 >= 0 && !this.world.isAir(mutable13)) {
                                            continue Label_0463;
                                        }
                                    }
                                }
                            }
                            final double double6 = integer13 + 0.5 - entity.y;
                            final double double7 = double4 * double4 + double6 * double6 + double5 * double5;
                            if (double3 < 0.0 || double7 < double3) {
                                double3 = double7;
                                integer6 = integer11;
                                integer7 = integer13;
                                integer8 = integer12;
                                integer9 = integer14 % 4;
                            }
                        }
                    }
                }
            }
        }
        if (double3 < 0.0) {
            for (int integer11 = integer3 - 16; integer11 <= integer3 + 16; ++integer11) {
                final double double4 = integer11 + 0.5 - entity.x;
                for (int integer12 = integer5 - 16; integer12 <= integer5 + 16; ++integer12) {
                    final double double5 = integer12 + 0.5 - entity.z;
                Label_0837:
                    for (int integer13 = this.world.getEffectiveHeight() - 1; integer13 >= 0; --integer13) {
                        if (this.world.isAir(mutable13.set(integer11, integer13, integer12))) {
                            while (integer13 > 0 && this.world.isAir(mutable13.set(integer11, integer13 - 1, integer12))) {
                                --integer13;
                            }
                            for (int integer14 = integer10; integer14 < integer10 + 2; ++integer14) {
                                final int integer15 = integer14 % 2;
                                final int integer16 = 1 - integer15;
                                for (int integer17 = 0; integer17 < 4; ++integer17) {
                                    for (int integer18 = -1; integer18 < 4; ++integer18) {
                                        final int integer19 = integer11 + (integer17 - 1) * integer15;
                                        final int integer20 = integer13 + integer18;
                                        final int integer21 = integer12 + (integer17 - 1) * integer16;
                                        mutable13.set(integer19, integer20, integer21);
                                        if (integer18 < 0 && !this.world.getBlockState(mutable13).getMaterial().isSolid()) {
                                            continue Label_0837;
                                        }
                                        if (integer18 >= 0 && !this.world.isAir(mutable13)) {
                                            continue Label_0837;
                                        }
                                    }
                                }
                                final double double6 = integer13 + 0.5 - entity.y;
                                final double double7 = double4 * double4 + double6 * double6 + double5 * double5;
                                if (double3 < 0.0 || double7 < double3) {
                                    double3 = double7;
                                    integer6 = integer11;
                                    integer7 = integer13;
                                    integer8 = integer12;
                                    integer9 = integer14 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        int integer11 = integer9;
        final int integer23 = integer6;
        int integer24 = integer7;
        int integer12 = integer8;
        int integer25 = integer11 % 2;
        int integer26 = 1 - integer25;
        if (integer11 % 4 >= 2) {
            integer25 = -integer25;
            integer26 = -integer26;
        }
        if (double3 < 0.0) {
            integer7 = (integer24 = MathHelper.clamp(integer7, 70, this.world.getEffectiveHeight() - 10));
            for (int integer13 = -1; integer13 <= 1; ++integer13) {
                for (int integer14 = 1; integer14 < 3; ++integer14) {
                    for (int integer15 = -1; integer15 < 3; ++integer15) {
                        final int integer16 = integer23 + (integer14 - 1) * integer25 + integer13 * integer26;
                        final int integer17 = integer24 + integer15;
                        final int integer18 = integer12 + (integer14 - 1) * integer26 - integer13 * integer25;
                        final boolean boolean26 = integer15 < 0;
                        mutable13.set(integer16, integer17, integer18);
                        this.world.setBlockState(mutable13, boolean26 ? Blocks.bJ.getDefaultState() : Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        for (int integer13 = -1; integer13 < 3; ++integer13) {
            for (int integer14 = -1; integer14 < 4; ++integer14) {
                if (integer13 == -1 || integer13 == 2 || integer14 == -1 || integer14 == 3) {
                    mutable13.set(integer23 + integer13 * integer25, integer24 + integer14, integer12 + integer13 * integer26);
                    this.world.setBlockState(mutable13, Blocks.bJ.getDefaultState(), 3);
                }
            }
        }
        final BlockState blockState20 = ((AbstractPropertyContainer<O, BlockState>)PortalForcer.PORTAL_BLOCK.getDefaultState()).<Direction.Axis, Direction.Axis>with(PortalBlock.AXIS, (integer25 == 0) ? Direction.Axis.Z : Direction.Axis.X);
        for (int integer14 = 0; integer14 < 2; ++integer14) {
            for (int integer15 = 0; integer15 < 3; ++integer15) {
                mutable13.set(integer23 + integer14 * integer25, integer24 + integer15, integer12 + integer14 * integer26);
                this.world.setBlockState(mutable13, blockState20, 18);
            }
        }
        return true;
    }
    
    public void tick(final long time) {
        if (time % 100L == 0L) {
            this.removeOldActivePortals(time);
            this.removeOldTickets(time);
        }
    }
    
    private void removeOldActivePortals(final long time) {
        final LongIterator longIterator3 = this.activePortals.values().iterator();
        while (longIterator3.hasNext()) {
            final long long4 = longIterator3.nextLong();
            if (long4 <= time) {
                longIterator3.remove();
            }
        }
    }
    
    private void removeOldTickets(final long time) {
        final long long3 = time - 300L;
        final Iterator<Map.Entry<ColumnPos, TicketInfo>> iterator5 = this.ticketInfos.entrySet().iterator();
        while (iterator5.hasNext()) {
            final Map.Entry<ColumnPos, TicketInfo> entry6 = iterator5.next();
            final TicketInfo ticketInfo7 = entry6.getValue();
            if (ticketInfo7.lastUsedTime < long3) {
                final ColumnPos columnPos8 = entry6.getKey();
                PortalForcer.LOGGER.debug("Removing nether portal ticket for {}:{}", new Supplier[] { this.world.getDimension()::getType, () -> columnPos8 });
                this.world.getChunkManager().<ColumnPos>removeTicket(ChunkTicketType.f, new ChunkPos(ticketInfo7.pos), 3, columnPos8);
                iterator5.remove();
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        PORTAL_BLOCK = (PortalBlock)Blocks.cM;
    }
    
    static class TicketInfo
    {
        public final BlockPos pos;
        public long lastUsedTime;
        
        public TicketInfo(final BlockPos pos, final long lastUsedTime) {
            this.pos = pos;
            this.lastUsedTime = lastUsedTime;
        }
    }
}
