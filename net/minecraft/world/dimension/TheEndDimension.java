package net.minecraft.world.dimension;

import net.minecraft.nbt.Tag;
import java.util.Random;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSourceConfig;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.util.math.BlockPos;

public class TheEndDimension extends Dimension
{
    public static final BlockPos SPAWN_POINT;
    private final EnderDragonFight enderDragonFight;
    
    public TheEndDimension(final World world, final DimensionType type) {
        super(world, type);
        final CompoundTag compoundTag3 = world.getLevelProperties().getWorldData(DimensionType.c);
        this.enderDragonFight = ((world instanceof ServerWorld) ? new EnderDragonFight((ServerWorld)world, compoundTag3.getCompound("DragonFight")) : null);
    }
    
    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        final FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig1 = ChunkGeneratorType.c.createSettings();
        floatingIslandsChunkGeneratorConfig1.setDefaultBlock(Blocks.dW.getDefaultState());
        floatingIslandsChunkGeneratorConfig1.setDefaultFluid(Blocks.AIR.getDefaultState());
        floatingIslandsChunkGeneratorConfig1.withCenter(this.getForcedSpawnPoint());
        return ChunkGeneratorType.c.create(this.world, BiomeSourceType.THE_END.applyConfig(BiomeSourceType.THE_END.getConfig().a(this.world.getSeed())), floatingIslandsChunkGeneratorConfig1);
    }
    
    @Override
    public float getSkyAngle(final long timeOfDay, final float delta) {
        return 0.0f;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    @Override
    public float[] getBackgroundColor(final float skyAngle, final float tickDelta) {
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public Vec3d getFogColor(final float skyAngle, final float tickDelta) {
        final int integer3 = 10518688;
        float float4 = MathHelper.cos(skyAngle * 6.2831855f) * 2.0f + 0.5f;
        float4 = MathHelper.clamp(float4, 0.0f, 1.0f);
        float float5 = 0.627451f;
        float float6 = 0.5019608f;
        float float7 = 0.627451f;
        float5 *= float4 * 0.0f + 0.15f;
        float6 *= float4 * 0.0f + 0.15f;
        float7 *= float4 * 0.0f + 0.15f;
        return new Vec3d(float5, float6, float7);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean c() {
        return false;
    }
    
    @Override
    public boolean canPlayersSleep() {
        return false;
    }
    
    @Override
    public boolean hasVisibleSky() {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getCloudHeight() {
        return 8.0f;
    }
    
    @Nullable
    @Override
    public BlockPos getSpawningBlockInChunk(final ChunkPos chunkPos, final boolean checkMobSpawnValidity) {
        final Random random3 = new Random(this.world.getSeed());
        final BlockPos blockPos4 = new BlockPos(chunkPos.getStartX() + random3.nextInt(15), 0, chunkPos.getEndZ() + random3.nextInt(15));
        return this.world.getTopNonAirState(blockPos4).getMaterial().blocksMovement() ? blockPos4 : null;
    }
    
    @Override
    public BlockPos getForcedSpawnPoint() {
        return TheEndDimension.SPAWN_POINT;
    }
    
    @Nullable
    @Override
    public BlockPos getTopSpawningBlockPosition(final int x, final int z, final boolean checkMobSpawnValidity) {
        return this.getSpawningBlockInChunk(new ChunkPos(x >> 4, z >> 4), checkMobSpawnValidity);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderFog(final int entityX, final int entityZ) {
        return false;
    }
    
    @Override
    public DimensionType getType() {
        return DimensionType.c;
    }
    
    @Override
    public void saveWorldData() {
        final CompoundTag compoundTag1 = new CompoundTag();
        if (this.enderDragonFight != null) {
            compoundTag1.put("DragonFight", this.enderDragonFight.toTag());
        }
        this.world.getLevelProperties().setWorldData(DimensionType.c, compoundTag1);
    }
    
    @Override
    public void update() {
        if (this.enderDragonFight != null) {
            this.enderDragonFight.tick();
        }
    }
    
    @Nullable
    public EnderDragonFight q() {
        return this.enderDragonFight;
    }
    
    static {
        SPAWN_POINT = new BlockPos(100, 50, 0);
    }
}
