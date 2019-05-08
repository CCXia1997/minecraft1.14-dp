package net.minecraft.client.world;

import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.tag.TagManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.TickScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.network.Packet;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.EntityTrackingSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.tag.BlockTags;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.world.GameMode;
import java.util.Random;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.entity.LightningEntity;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.LightType;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.entity.BlockEntity;
import java.util.Collection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.util.math.MathHelper;
import java.util.Iterator;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.player.PlayerEntity;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import com.google.common.collect.Iterables;
import java.util.function.BooleanSupplier;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import com.google.common.collect.Lists;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.item.map.MapState;
import java.util.Map;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ClientWorld extends World
{
    private final List<Entity> globalEntities;
    private final Int2ObjectMap<Entity> regularEntities;
    private final ClientPlayNetworkHandler netHandler;
    private final WorldRenderer worldRenderer;
    private final MinecraftClient client;
    private final List<AbstractClientPlayerEntity> players;
    private int ticksUntilCaveAmbientSound;
    private Scoreboard scoreboard;
    private final Map<String, MapState> mapStates;
    
    public ClientWorld(final ClientPlayNetworkHandler netHandler, final LevelInfo levelInfo, final DimensionType dimensionType, final int integer, final Profiler profiler, final WorldRenderer worldRenderer) {
        super(new LevelProperties(levelInfo, "MpServer"), dimensionType, (world, dimension) -> new ClientChunkManager(world, integer), profiler, true);
        this.globalEntities = Lists.newArrayList();
        this.regularEntities = (Int2ObjectMap<Entity>)new Int2ObjectOpenHashMap();
        this.client = MinecraftClient.getInstance();
        this.players = Lists.newArrayList();
        this.ticksUntilCaveAmbientSound = this.random.nextInt(12000);
        this.scoreboard = new Scoreboard();
        this.mapStates = Maps.newHashMap();
        this.netHandler = netHandler;
        this.worldRenderer = worldRenderer;
        this.setSpawnPos(new BlockPos(8, 64, 8));
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.chunkManager.tick(booleanSupplier);
        this.tickCaveAmbientSound();
        this.getProfiler().pop();
    }
    
    public Iterable<Entity> getEntities() {
        return Iterables.<Entity>concat(this.regularEntities.values(), this.globalEntities);
    }
    
    public void tickEntities() {
        final Profiler profiler1 = this.getProfiler();
        profiler1.push("entities");
        profiler1.push("global");
        for (int integer2 = 0; integer2 < this.globalEntities.size(); ++integer2) {
            final Entity entity2 = this.globalEntities.get(integer2);
            this.tickEntity(entity -> {
                ++entity.age;
                entity.tick();
                return;
            }, entity2);
            if (entity2.removed) {
                this.globalEntities.remove(integer2--);
            }
        }
        profiler1.swap("regular");
        final ObjectIterator<Int2ObjectMap.Entry<Entity>> objectIterator2 = (ObjectIterator<Int2ObjectMap.Entry<Entity>>)this.regularEntities.int2ObjectEntrySet().iterator();
        while (objectIterator2.hasNext()) {
            final Int2ObjectMap.Entry<Entity> entry3 = (Int2ObjectMap.Entry<Entity>)objectIterator2.next();
            final Entity entity3 = (Entity)entry3.getValue();
            if (entity3.hasVehicle()) {
                continue;
            }
            profiler1.push("tick");
            if (!entity3.removed) {
                this.tickEntity(this::tickEntity, entity3);
            }
            profiler1.pop();
            profiler1.push("remove");
            if (entity3.removed) {
                objectIterator2.remove();
                this.finishRemovingEntity(entity3);
            }
            profiler1.pop();
        }
        profiler1.pop();
        this.tickBlockEntities();
        profiler1.pop();
    }
    
    public void tickEntity(final Entity entity) {
        if (!(entity instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity)) {
            return;
        }
        entity.prevRenderX = entity.x;
        entity.prevRenderY = entity.y;
        entity.prevRenderZ = entity.z;
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
        if (entity.Y) {
            ++entity.age;
            this.getProfiler().push(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString());
            entity.tick();
            this.getProfiler().pop();
        }
        this.checkChunk(entity);
        if (entity.Y) {
            for (final Entity entity2 : entity.getPassengerList()) {
                this.tickPassenger(entity, entity2);
            }
        }
    }
    
    public void tickPassenger(final Entity entity, final Entity passenger) {
        if (passenger.removed || passenger.getVehicle() != entity) {
            passenger.stopRiding();
            return;
        }
        if (!(passenger instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(passenger)) {
            return;
        }
        passenger.prevRenderX = passenger.x;
        passenger.prevRenderY = passenger.y;
        passenger.prevRenderZ = passenger.z;
        passenger.prevYaw = passenger.yaw;
        passenger.prevPitch = passenger.pitch;
        if (passenger.Y) {
            ++passenger.age;
            passenger.tickRiding();
        }
        this.checkChunk(passenger);
        if (passenger.Y) {
            for (final Entity entity2 : passenger.getPassengerList()) {
                this.tickPassenger(passenger, entity2);
            }
        }
    }
    
    public void checkChunk(final Entity entity) {
        this.getProfiler().push("chunkCheck");
        final int integer2 = MathHelper.floor(entity.x / 16.0);
        final int integer3 = MathHelper.floor(entity.y / 16.0);
        final int integer4 = MathHelper.floor(entity.z / 16.0);
        if (!entity.Y || entity.chunkX != integer2 || entity.chunkY != integer3 || entity.chunkZ != integer4) {
            if (entity.Y && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
                this.getChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
            }
            if (entity.bT() || this.isChunkLoaded(integer2, integer4)) {
                this.getChunk(integer2, integer4).addEntity(entity);
            }
            else {
                entity.Y = false;
            }
        }
        this.getProfiler().pop();
    }
    
    public void unloadBlockEntities(final WorldChunk chunk) {
        this.unloadedBlockEntities.addAll(chunk.getBlockEntities().values());
        this.chunkManager.getLightingProvider().suppressLight(chunk.getPos(), false);
    }
    
    @Override
    public boolean isChunkLoaded(final int chunkX, final int chunkZ) {
        return true;
    }
    
    private void tickCaveAmbientSound() {
        if (this.client.player == null) {
            return;
        }
        if (this.ticksUntilCaveAmbientSound > 0) {
            --this.ticksUntilCaveAmbientSound;
            return;
        }
        final BlockPos blockPos1 = new BlockPos(this.client.player);
        final BlockPos blockPos2 = blockPos1.add(4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1));
        final double double3 = blockPos1.getSquaredDistance(blockPos2);
        if (double3 >= 4.0 && double3 <= 256.0) {
            final BlockState blockState5 = this.getBlockState(blockPos2);
            if (blockState5.isAir() && this.getLightLevel(blockPos2, 0) <= this.random.nextInt(8) && this.getLightLevel(LightType.SKY, blockPos2) <= 0) {
                this.playSound(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5, SoundEvents.a, SoundCategory.i, 0.7f, 0.8f + this.random.nextFloat() * 0.2f, false);
                this.ticksUntilCaveAmbientSound = this.random.nextInt(12000) + 6000;
            }
        }
    }
    
    public int getRegularEntityCount() {
        return this.regularEntities.size();
    }
    
    public void addLightning(final LightningEntity lightning) {
        this.globalEntities.add(lightning);
    }
    
    public void addPlayer(final int id, final AbstractClientPlayerEntity player) {
        this.addEntityPrivate(id, player);
        this.players.add(player);
    }
    
    public void addEntity(final int id, final Entity entity) {
        this.addEntityPrivate(id, entity);
    }
    
    private void addEntityPrivate(final int id, final Entity entity) {
        this.removeEntity(id);
        this.regularEntities.put(id, entity);
        this.getChunkManager().getChunk(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, true).addEntity(entity);
    }
    
    public void removeEntity(final int integer) {
        final Entity entity2 = (Entity)this.regularEntities.remove(integer);
        if (entity2 != null) {
            entity2.remove();
            this.finishRemovingEntity(entity2);
        }
    }
    
    private void finishRemovingEntity(final Entity entity) {
        entity.detach();
        if (entity.Y) {
            this.getChunk(entity.chunkX, entity.chunkZ).remove(entity);
        }
        this.players.remove(entity);
    }
    
    public void addEntitiesToChunk(final WorldChunk chunk) {
        for (final Int2ObjectMap.Entry<Entity> entry3 : this.regularEntities.int2ObjectEntrySet()) {
            final Entity entity4 = (Entity)entry3.getValue();
            final int integer5 = MathHelper.floor(entity4.x / 16.0);
            final int integer6 = MathHelper.floor(entity4.z / 16.0);
            if (integer5 == chunk.getPos().x && integer6 == chunk.getPos().z) {
                chunk.addEntity(entity4);
            }
        }
    }
    
    @Nullable
    @Override
    public Entity getEntityById(final int integer) {
        return (Entity)this.regularEntities.get(integer);
    }
    
    public void setBlockStateWithoutNeighborUpdates(final BlockPos blockPos, final BlockState blockState) {
        this.setBlockState(blockPos, blockState, 19);
    }
    
    @Override
    public void disconnect() {
        this.netHandler.getClientConnection().disconnect(new TranslatableTextComponent("multiplayer.status.quitting", new Object[0]));
    }
    
    public void doRandomBlockDisplayTicks(final int xCenter, final int yCenter, final int integer3) {
        final int integer4 = 32;
        final Random random5 = new Random();
        final ItemStack itemStack6 = this.client.player.getMainHandStack();
        final boolean boolean7 = this.client.interactionManager.getCurrentGameMode() == GameMode.c && !itemStack6.isEmpty() && itemStack6.getItem() == Blocks.gg.getItem();
        final BlockPos.Mutable mutable8 = new BlockPos.Mutable();
        for (int integer5 = 0; integer5 < 667; ++integer5) {
            this.randomBlockDisplayTick(xCenter, yCenter, integer3, 16, random5, boolean7, mutable8);
            this.randomBlockDisplayTick(xCenter, yCenter, integer3, 32, random5, boolean7, mutable8);
        }
    }
    
    public void randomBlockDisplayTick(final int xCenter, final int yCenter, final int zCenter, final int radius, final Random random, final boolean spawnBarrierParticles, final BlockPos.Mutable mutable) {
        final int integer8 = xCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
        final int integer9 = yCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
        final int integer10 = zCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
        mutable.set(integer8, integer9, integer10);
        final BlockState blockState11 = this.getBlockState(mutable);
        blockState11.getBlock().randomDisplayTick(blockState11, this, mutable, random);
        final FluidState fluidState12 = this.getFluidState(mutable);
        if (!fluidState12.isEmpty()) {
            fluidState12.randomDisplayTick(this, mutable, random);
            final ParticleParameters particleParameters13 = fluidState12.getParticle();
            if (particleParameters13 != null && this.random.nextInt(10) == 0) {
                final boolean boolean14 = Block.isSolidFullSquare(blockState11, this, mutable, Direction.DOWN);
                final BlockPos blockPos15 = mutable.down();
                this.addParticle(blockPos15, this.getBlockState(blockPos15), particleParameters13, boolean14);
            }
        }
        if (spawnBarrierParticles && blockState11.getBlock() == Blocks.gg) {
            this.addParticle(ParticleTypes.c, integer8 + 0.5f, integer9 + 0.5f, integer10 + 0.5f, 0.0, 0.0, 0.0);
        }
    }
    
    private void addParticle(final BlockPos pos, final BlockState state, final ParticleParameters parameters, final boolean boolean4) {
        if (!state.getFluidState().isEmpty()) {
            return;
        }
        final VoxelShape voxelShape5 = state.getCollisionShape(this, pos);
        final double double6 = voxelShape5.getMaximum(Direction.Axis.Y);
        if (double6 < 1.0) {
            if (boolean4) {
                this.addParticle(pos.getX(), pos.getX() + 1, pos.getZ(), pos.getZ() + 1, pos.getY() + 1 - 0.05, parameters);
            }
        }
        else if (!state.matches(BlockTags.L)) {
            final double double7 = voxelShape5.getMinimum(Direction.Axis.Y);
            if (double7 > 0.0) {
                this.addParticle(pos, parameters, voxelShape5, pos.getY() + double7 - 0.05);
            }
            else {
                final BlockPos blockPos10 = pos.down();
                final BlockState blockState11 = this.getBlockState(blockPos10);
                final VoxelShape voxelShape6 = blockState11.getCollisionShape(this, blockPos10);
                final double double8 = voxelShape6.getMaximum(Direction.Axis.Y);
                if (double8 < 1.0 && blockState11.getFluidState().isEmpty()) {
                    this.addParticle(pos, parameters, voxelShape5, pos.getY() - 0.05);
                }
            }
        }
    }
    
    private void addParticle(final BlockPos pos, final ParticleParameters parameters, final VoxelShape shape, final double y) {
        this.addParticle(pos.getX() + shape.getMinimum(Direction.Axis.X), pos.getX() + shape.getMaximum(Direction.Axis.X), pos.getZ() + shape.getMinimum(Direction.Axis.Z), pos.getZ() + shape.getMaximum(Direction.Axis.Z), y, parameters);
    }
    
    private void addParticle(final double minX, final double maxX, final double minZ, final double maxZ, final double y, final ParticleParameters parameters) {
        this.addParticle(parameters, MathHelper.lerp(this.random.nextDouble(), minX, maxX), y, MathHelper.lerp(this.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
    }
    
    public void finishRemovingEntities() {
        final ObjectIterator<Int2ObjectMap.Entry<Entity>> objectIterator1 = (ObjectIterator<Int2ObjectMap.Entry<Entity>>)this.regularEntities.int2ObjectEntrySet().iterator();
        while (objectIterator1.hasNext()) {
            final Int2ObjectMap.Entry<Entity> entry2 = (Int2ObjectMap.Entry<Entity>)objectIterator1.next();
            final Entity entity3 = (Entity)entry2.getValue();
            if (entity3.removed) {
                objectIterator1.remove();
                this.finishRemovingEntity(entity3);
            }
        }
    }
    
    @Override
    public CrashReportSection addDetailsToCrashReport(final CrashReport report) {
        final CrashReportSection crashReportSection2 = super.addDetailsToCrashReport(report);
        crashReportSection2.add("Server brand", () -> this.client.player.getServerBrand());
        crashReportSection2.add("Server type", () -> (this.client.getServer() == null) ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        return crashReportSection2;
    }
    
    @Override
    public void playSound(@Nullable final PlayerEntity player, final double x, final double y, final double z, final SoundEvent sound, final SoundCategory category, final float volume, final float pitch) {
        if (player == this.client.player) {
            this.playSound(x, y, z, sound, category, volume, pitch, false);
        }
    }
    
    @Override
    public void playSoundFromEntity(@Nullable final PlayerEntity playerEntity, final Entity entity, final SoundEvent soundEvent, final SoundCategory soundCategory, final float float5, final float float6) {
        if (playerEntity == this.client.player) {
            this.client.getSoundManager().play(new EntityTrackingSoundInstance(soundEvent, soundCategory, entity));
        }
    }
    
    public void playSound(final BlockPos pos, final SoundEvent sound, final SoundCategory category, final float volume, final float pitch, final boolean useDistance) {
        this.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, category, volume, pitch, useDistance);
    }
    
    @Override
    public void playSound(final double x, final double y, final double z, final SoundEvent sound, final SoundCategory soundCategory, final float float9, final float float10, final boolean boolean11) {
        final double double12 = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
        final PositionedSoundInstance positionedSoundInstance14 = new PositionedSoundInstance(sound, soundCategory, float9, float10, (float)x, (float)y, (float)z);
        if (boolean11 && double12 > 100.0) {
            final double double13 = Math.sqrt(double12) / 40.0;
            this.client.getSoundManager().play(positionedSoundInstance14, (int)(double13 * 20.0));
        }
        else {
            this.client.getSoundManager().play(positionedSoundInstance14);
        }
    }
    
    @Override
    public void addFireworkParticle(final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, @Nullable final CompoundTag tag) {
        this.client.particleManager.addParticle(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, tag));
    }
    
    @Override
    public void sendPacket(final Packet<?> packet) {
        this.netHandler.sendPacket(packet);
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        return this.netHandler.getRecipeManager();
    }
    
    public void setScoreboard(final Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    @Override
    public void setTimeOfDay(long long1) {
        if (long1 < 0L) {
            long1 = -long1;
            this.getGameRules().put("doDaylightCycle", "false", null);
        }
        else {
            this.getGameRules().put("doDaylightCycle", "true", null);
        }
        super.setTimeOfDay(long1);
    }
    
    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return DummyClientTickScheduler.get();
    }
    
    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return DummyClientTickScheduler.get();
    }
    
    @Override
    public ClientChunkManager getChunkManager() {
        return (ClientChunkManager)super.getChunkManager();
    }
    
    @Nullable
    @Override
    public MapState getMapState(final String id) {
        return this.mapStates.get(id);
    }
    
    @Override
    public void putMapState(final MapState mapState) {
        this.mapStates.put(mapState.getId(), mapState);
    }
    
    @Override
    public int getNextMapId() {
        return 0;
    }
    
    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    @Override
    public TagManager getTagManager() {
        return this.netHandler.getTagManager();
    }
    
    @Override
    public void updateListeners(final BlockPos blockPos, final BlockState blockState2, final BlockState blockState3, final int integer) {
        this.worldRenderer.updateBlock(this, blockPos, blockState2, blockState3, integer);
    }
    
    @Override
    public void scheduleBlockRender(final BlockPos blockPos) {
        this.worldRenderer.scheduleBlockRenders(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
    
    public void scheduleBlockRenders(final int integer1, final int integer2, final int integer3) {
        this.worldRenderer.scheduleBlockRenders(integer1, integer2, integer3);
    }
    
    @Override
    public void setBlockBreakingProgress(final int integer1, final BlockPos blockPos, final int integer3) {
        this.worldRenderer.setBlockBreakingProgress(integer1, blockPos, integer3);
    }
    
    @Override
    public void playGlobalEvent(final int type, final BlockPos pos, final int data) {
        this.worldRenderer.playGlobalEvent(type, pos, data);
    }
    
    @Override
    public void playLevelEvent(@Nullable final PlayerEntity playerEntity, final int integer2, final BlockPos blockPos, final int integer4) {
        try {
            this.worldRenderer.playLevelEvent(playerEntity, integer2, blockPos, integer4);
        }
        catch (Throwable throwable5) {
            final CrashReport crashReport6 = CrashReport.create(throwable5, "Playing level event");
            final CrashReportSection crashReportSection7 = crashReport6.addElement("Level event being played");
            crashReportSection7.add("Block coordinates", CrashReportSection.createPositionString(blockPos));
            crashReportSection7.add("Event source", playerEntity);
            crashReportSection7.add("Event type", integer2);
            crashReportSection7.add("Event data", integer4);
            throw new CrashException(crashReport6);
        }
    }
    
    @Override
    public void addParticle(final ParticleParameters parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Override
    public void addParticle(final ParticleParameters parameters, final boolean alwaysSpawn, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Override
    public void addImportantParticle(final ParticleParameters parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this.worldRenderer.addParticle(parameters, false, true, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Override
    public void addImportantParticle(final ParticleParameters parameters, final boolean alwaysSpawn, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || alwaysSpawn, true, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Override
    public List<AbstractClientPlayerEntity> getPlayers() {
        return this.players;
    }
}
