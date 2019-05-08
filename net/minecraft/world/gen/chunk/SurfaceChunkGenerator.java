package net.minecraft.world.gen.chunk;

import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.chunk.ChunkSection;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.structure.StructureStart;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.Feature;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import java.util.Random;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.block.BlockState;

public abstract class SurfaceChunkGenerator<T extends ChunkGeneratorConfig> extends ChunkGenerator<T>
{
    private static final float[] h;
    private static final BlockState AIR;
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    protected final ChunkRandom random;
    private final OctavePerlinNoiseSampler o;
    private final OctavePerlinNoiseSampler p;
    private final OctavePerlinNoiseSampler q;
    private final NoiseSampler surfaceDepthNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    
    public SurfaceChunkGenerator(final IWorld world, final BiomeSource biomeSource, final int verticalNoiseResolution, final int horizontalNoiseResolution, final int worldHeight, final T config, final boolean boolean7) {
        super(world, biomeSource, config);
        this.verticalNoiseResolution = horizontalNoiseResolution;
        this.horizontalNoiseResolution = verticalNoiseResolution;
        this.defaultBlock = config.getDefaultBlock();
        this.defaultFluid = config.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = worldHeight / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(this.seed);
        this.o = new OctavePerlinNoiseSampler(this.random, 16);
        this.p = new OctavePerlinNoiseSampler(this.random, 16);
        this.q = new OctavePerlinNoiseSampler(this.random, 8);
        this.surfaceDepthNoise = (boolean7 ? new OctaveSimplexNoiseSampler(this.random, 4) : new OctavePerlinNoiseSampler(this.random, 4));
    }
    
    private double sampleNoise(final int x, final int y, final int z, final double double4, final double double6, final double double8, final double double10) {
        double double11 = 0.0;
        double double12 = 0.0;
        double double13 = 0.0;
        double double14 = 1.0;
        for (int integer20 = 0; integer20 < 16; ++integer20) {
            final double double15 = OctavePerlinNoiseSampler.maintainPrecision(x * double4 * double14);
            final double double16 = OctavePerlinNoiseSampler.maintainPrecision(y * double6 * double14);
            final double double17 = OctavePerlinNoiseSampler.maintainPrecision(z * double4 * double14);
            final double double18 = double6 * double14;
            double11 += this.o.getOctave(integer20).sample(double15, double16, double17, double18, y * double18) / double14;
            double12 += this.p.getOctave(integer20).sample(double15, double16, double17, double18, y * double18) / double14;
            if (integer20 < 8) {
                double13 += this.q.getOctave(integer20).sample(OctavePerlinNoiseSampler.maintainPrecision(x * double8 * double14), OctavePerlinNoiseSampler.maintainPrecision(y * double10 * double14), OctavePerlinNoiseSampler.maintainPrecision(z * double8 * double14), double10 * double14, y * double10 * double14) / double14;
            }
            double14 /= 2.0;
        }
        return MathHelper.clampedLerp(double11 / 512.0, double12 / 512.0, (double13 / 10.0 + 1.0) / 2.0);
    }
    
    protected double[] sampleNoiseColumn(final int x, final int z) {
        final double[] arr3 = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(arr3, x, z);
        return arr3;
    }
    
    protected void sampleNoiseColumn(final double[] buffer, final int x, final int z, final double double4, final double double6, final double double8, final double double10, final int integer12, final int integer13) {
        final double[] arr14 = this.computeNoiseRange(x, z);
        final double double11 = arr14[0];
        final double double12 = arr14[1];
        final double double13 = this.g();
        final double double14 = this.h();
        for (int integer14 = 0; integer14 < this.getNoiseSizeY(); ++integer14) {
            double double15 = this.sampleNoise(x, integer14, z, double4, double6, double8, double10);
            double15 -= this.computeNoiseFalloff(double11, double12, integer14);
            if (integer14 > double13) {
                double15 = MathHelper.clampedLerp(double15, integer13, (integer14 - double13) / integer12);
            }
            else if (integer14 < double14) {
                double15 = MathHelper.clampedLerp(double15, -30.0, (double14 - integer14) / (double14 - 1.0));
            }
            buffer[integer14] = double15;
        }
    }
    
    protected abstract double[] computeNoiseRange(final int arg1, final int arg2);
    
    protected abstract double computeNoiseFalloff(final double arg1, final double arg2, final int arg3);
    
    protected double g() {
        return this.getNoiseSizeY() - 4;
    }
    
    protected double h() {
        return 0.0;
    }
    
    @Override
    public int getHeightOnGround(final int x, final int z, final Heightmap.Type heightmapType) {
        final int integer4 = Math.floorDiv(x, this.horizontalNoiseResolution);
        final int integer5 = Math.floorDiv(z, this.horizontalNoiseResolution);
        final int integer6 = Math.floorMod(x, this.horizontalNoiseResolution);
        final int integer7 = Math.floorMod(z, this.horizontalNoiseResolution);
        final double double8 = integer6 / (double)this.horizontalNoiseResolution;
        final double double9 = integer7 / (double)this.horizontalNoiseResolution;
        final double[][] arr12 = { this.sampleNoiseColumn(integer4, integer5), this.sampleNoiseColumn(integer4, integer5 + 1), this.sampleNoiseColumn(integer4 + 1, integer5), this.sampleNoiseColumn(integer4 + 1, integer5 + 1) };
        final int integer8 = this.getSeaLevel();
        for (int integer9 = this.noiseSizeY - 1; integer9 >= 0; --integer9) {
            final double double10 = arr12[0][integer9];
            final double double11 = arr12[1][integer9];
            final double double12 = arr12[2][integer9];
            final double double13 = arr12[3][integer9];
            final double double14 = arr12[0][integer9 + 1];
            final double double15 = arr12[1][integer9 + 1];
            final double double16 = arr12[2][integer9 + 1];
            final double double17 = arr12[3][integer9 + 1];
            for (int integer10 = this.verticalNoiseResolution - 1; integer10 >= 0; --integer10) {
                final double double18 = integer10 / (double)this.verticalNoiseResolution;
                final double double19 = MathHelper.lerp3(double18, double8, double9, double10, double14, double12, double16, double11, double15, double13, double17);
                final int integer11 = integer9 * this.verticalNoiseResolution + integer10;
                if (double19 > 0.0 || integer11 < integer8) {
                    BlockState blockState37;
                    if (double19 > 0.0) {
                        blockState37 = this.defaultBlock;
                    }
                    else {
                        blockState37 = this.defaultFluid;
                    }
                    if (heightmapType.getBlockPredicate().test(blockState37)) {
                        return integer11 + 1;
                    }
                }
            }
        }
        return 0;
    }
    
    protected abstract void sampleNoiseColumn(final double[] arg1, final int arg2, final int arg3);
    
    public int getNoiseSizeY() {
        return this.noiseSizeY + 1;
    }
    
    @Override
    public void buildSurface(final Chunk chunk) {
        final ChunkPos chunkPos2 = chunk.getPos();
        final int integer3 = chunkPos2.x;
        final int integer4 = chunkPos2.z;
        final ChunkRandom chunkRandom5 = new ChunkRandom();
        chunkRandom5.setSeed(integer3, integer4);
        final ChunkPos chunkPos3 = chunk.getPos();
        final int integer5 = chunkPos3.getStartX();
        final int integer6 = chunkPos3.getStartZ();
        final double double9 = 0.0625;
        final Biome[] arr11 = chunk.getBiomeArray();
        for (int integer7 = 0; integer7 < 16; ++integer7) {
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                final int integer9 = integer5 + integer7;
                final int integer10 = integer6 + integer8;
                final int integer11 = chunk.sampleHeightmap(Heightmap.Type.a, integer7, integer8) + 1;
                final double double10 = this.surfaceDepthNoise.sample(integer9 * 0.0625, integer10 * 0.0625, 0.0625, integer7 * 0.0625);
                arr11[integer8 * 16 + integer7].buildSurface(chunkRandom5, chunk, integer9, integer10, integer11, double10, this.getConfig().getDefaultBlock(), this.getConfig().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
            }
        }
        this.buildBedrock(chunk, chunkRandom5);
    }
    
    protected void buildBedrock(final Chunk chunk, final Random random) {
        final BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        final int integer4 = chunk.getPos().getStartX();
        final int integer5 = chunk.getPos().getStartZ();
        final T chunkGeneratorConfig6 = this.getConfig();
        final int integer6 = chunkGeneratorConfig6.getMinY();
        final int integer7 = chunkGeneratorConfig6.getMaxY();
        for (final BlockPos blockPos10 : BlockPos.iterate(integer4, 0, integer5, integer4 + 15, 0, integer5 + 15)) {
            if (integer7 > 0) {
                for (int integer8 = integer7; integer8 >= integer7 - 4; --integer8) {
                    if (integer8 >= integer7 - random.nextInt(5)) {
                        chunk.setBlockState(mutable3.set(blockPos10.getX(), integer8, blockPos10.getZ()), Blocks.z.getDefaultState(), false);
                    }
                }
            }
            if (integer6 < 256) {
                for (int integer8 = integer6 + 4; integer8 >= integer6; --integer8) {
                    if (integer8 <= integer6 + random.nextInt(5)) {
                        chunk.setBlockState(mutable3.set(blockPos10.getX(), integer8, blockPos10.getZ()), Blocks.z.getDefaultState(), false);
                    }
                }
            }
        }
    }
    
    @Override
    public void populateNoise(final IWorld world, final Chunk chunk) {
        final int integer3 = this.getSeaLevel();
        final ObjectList<PoolStructurePiece> objectList4 = (ObjectList<PoolStructurePiece>)new ObjectArrayList(10);
        final ObjectList<JigsawJunction> objectList5 = (ObjectList<JigsawJunction>)new ObjectArrayList(32);
        final ChunkPos chunkPos6 = chunk.getPos();
        final int integer4 = chunkPos6.x;
        final int integer5 = chunkPos6.z;
        final int integer6 = integer4 << 4;
        final int integer7 = integer5 << 4;
        for (final StructureFeature<?> structureFeature12 : Feature.JIGSAW_STRUCTURES) {
            final String string13 = structureFeature12.getName();
            final LongIterator longIterator14 = chunk.getStructureReferences(string13).iterator();
            while (longIterator14.hasNext()) {
                final long long15 = longIterator14.nextLong();
                final ChunkPos chunkPos7 = new ChunkPos(long15);
                final Chunk chunk2 = world.getChunk(chunkPos7.x, chunkPos7.z);
                final StructureStart structureStart19 = chunk2.getStructureStart(string13);
                if (structureStart19 != null) {
                    if (!structureStart19.hasChildren()) {
                        continue;
                    }
                    for (final StructurePiece structurePiece21 : structureStart19.getChildren()) {
                        if (!structurePiece21.a(chunkPos6, 12)) {
                            continue;
                        }
                        if (!(structurePiece21 instanceof PoolStructurePiece)) {
                            continue;
                        }
                        final PoolStructurePiece poolStructurePiece22 = (PoolStructurePiece)structurePiece21;
                        final StructurePool.Projection projection23 = poolStructurePiece22.getPoolElement().getProjection();
                        if (projection23 == StructurePool.Projection.RIGID) {
                            objectList4.add(poolStructurePiece22);
                        }
                        for (final JigsawJunction jigsawJunction25 : poolStructurePiece22.getJunctions()) {
                            final int integer8 = jigsawJunction25.getSourceX();
                            final int integer9 = jigsawJunction25.getSourceZ();
                            if (integer8 > integer6 - 12 && integer9 > integer7 - 12 && integer8 < integer6 + 15 + 12) {
                                if (integer9 >= integer7 + 15 + 12) {
                                    continue;
                                }
                                objectList5.add(jigsawJunction25);
                            }
                        }
                    }
                }
            }
        }
        final double[][][] arr11 = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];
        for (int integer10 = 0; integer10 < this.noiseSizeZ + 1; ++integer10) {
            this.sampleNoiseColumn(arr11[0][integer10] = new double[this.noiseSizeY + 1], integer4 * this.noiseSizeX, integer5 * this.noiseSizeZ + integer10);
            arr11[1][integer10] = new double[this.noiseSizeY + 1];
        }
        final ProtoChunk protoChunk12 = (ProtoChunk)chunk;
        final Heightmap heightmap13 = protoChunk12.getHeightmap(Heightmap.Type.c);
        final Heightmap heightmap14 = protoChunk12.getHeightmap(Heightmap.Type.a);
        final BlockPos.Mutable mutable15 = new BlockPos.Mutable();
        final ObjectListIterator<PoolStructurePiece> objectListIterator16 = (ObjectListIterator<PoolStructurePiece>)objectList4.iterator();
        final ObjectListIterator<JigsawJunction> objectListIterator17 = (ObjectListIterator<JigsawJunction>)objectList5.iterator();
        for (int integer11 = 0; integer11 < this.noiseSizeX; ++integer11) {
            for (int integer12 = 0; integer12 < this.noiseSizeZ + 1; ++integer12) {
                this.sampleNoiseColumn(arr11[1][integer12], integer4 * this.noiseSizeX + integer11 + 1, integer5 * this.noiseSizeZ + integer12);
            }
            for (int integer12 = 0; integer12 < this.noiseSizeZ; ++integer12) {
                ChunkSection chunkSection20 = protoChunk12.getSection(15);
                chunkSection20.lock();
                for (int integer13 = this.noiseSizeY - 1; integer13 >= 0; --integer13) {
                    final double double22 = arr11[0][integer12][integer13];
                    final double double23 = arr11[0][integer12 + 1][integer13];
                    final double double24 = arr11[1][integer12][integer13];
                    final double double25 = arr11[1][integer12 + 1][integer13];
                    final double double26 = arr11[0][integer12][integer13 + 1];
                    final double double27 = arr11[0][integer12 + 1][integer13 + 1];
                    final double double28 = arr11[1][integer12][integer13 + 1];
                    final double double29 = arr11[1][integer12 + 1][integer13 + 1];
                    for (int integer14 = this.verticalNoiseResolution - 1; integer14 >= 0; --integer14) {
                        final int integer15 = integer13 * this.verticalNoiseResolution + integer14;
                        final int integer16 = integer15 & 0xF;
                        final int integer17 = integer15 >> 4;
                        if (chunkSection20.getYOffset() >> 4 != integer17) {
                            chunkSection20.unlock();
                            chunkSection20 = protoChunk12.getSection(integer17);
                            chunkSection20.lock();
                        }
                        final double double30 = integer14 / (double)this.verticalNoiseResolution;
                        final double double31 = MathHelper.lerp(double30, double22, double26);
                        final double double32 = MathHelper.lerp(double30, double24, double28);
                        final double double33 = MathHelper.lerp(double30, double23, double27);
                        final double double34 = MathHelper.lerp(double30, double25, double29);
                        for (int integer18 = 0; integer18 < this.horizontalNoiseResolution; ++integer18) {
                            final int integer19 = integer6 + integer11 * this.horizontalNoiseResolution + integer18;
                            final int integer20 = integer19 & 0xF;
                            final double double35 = integer18 / (double)this.horizontalNoiseResolution;
                            final double double36 = MathHelper.lerp(double35, double31, double32);
                            final double double37 = MathHelper.lerp(double35, double33, double34);
                            for (int integer21 = 0; integer21 < this.horizontalNoiseResolution; ++integer21) {
                                final int integer22 = integer7 + integer12 * this.horizontalNoiseResolution + integer21;
                                final int integer23 = integer22 & 0xF;
                                final double double38 = integer21 / (double)this.horizontalNoiseResolution;
                                final double double39 = MathHelper.lerp(double38, double36, double37);
                                double double40 = MathHelper.clamp(double39 / 200.0, -1.0, 1.0);
                                double40 = double40 / 2.0 - double40 * double40 * double40 / 24.0;
                                while (objectListIterator16.hasNext()) {
                                    final PoolStructurePiece poolStructurePiece23 = (PoolStructurePiece)objectListIterator16.next();
                                    final MutableIntBoundingBox mutableIntBoundingBox71 = poolStructurePiece23.getBoundingBox();
                                    final int integer24 = Math.max(0, Math.max(mutableIntBoundingBox71.minX - integer19, integer19 - mutableIntBoundingBox71.maxX));
                                    final int integer25 = integer15 - (mutableIntBoundingBox71.minY + poolStructurePiece23.getGroundLevelDelta());
                                    final int integer26 = Math.max(0, Math.max(mutableIntBoundingBox71.minZ - integer22, integer22 - mutableIntBoundingBox71.maxZ));
                                    double40 += a(integer24, integer25, integer26) * 0.8;
                                }
                                objectListIterator16.back(objectList4.size());
                                while (objectListIterator17.hasNext()) {
                                    final JigsawJunction jigsawJunction26 = (JigsawJunction)objectListIterator17.next();
                                    final int integer27 = integer19 - jigsawJunction26.getSourceX();
                                    final int integer24 = integer15 - jigsawJunction26.getSourceGroundY();
                                    final int integer25 = integer22 - jigsawJunction26.getSourceZ();
                                    double40 += a(integer27, integer24, integer25) * 0.4;
                                }
                                objectListIterator17.back(objectList5.size());
                                BlockState blockState70;
                                if (double40 > 0.0) {
                                    blockState70 = this.defaultBlock;
                                }
                                else if (integer15 < integer3) {
                                    blockState70 = this.defaultFluid;
                                }
                                else {
                                    blockState70 = SurfaceChunkGenerator.AIR;
                                }
                                if (blockState70 != SurfaceChunkGenerator.AIR) {
                                    if (blockState70.getLuminance() != 0) {
                                        mutable15.set(integer19, integer15, integer22);
                                        protoChunk12.addLightSource(mutable15);
                                    }
                                    chunkSection20.setBlockState(integer20, integer16, integer23, blockState70, false);
                                    heightmap13.trackUpdate(integer20, integer15, integer23, blockState70);
                                    heightmap14.trackUpdate(integer20, integer15, integer23, blockState70);
                                }
                            }
                        }
                    }
                }
                chunkSection20.unlock();
            }
            final double[][] arr12 = arr11[0];
            arr11[0] = arr11[1];
            arr11[1] = arr12;
        }
    }
    
    private static double a(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer1 + 12;
        final int integer5 = integer2 + 12;
        final int integer6 = integer3 + 12;
        if (integer4 < 0 || integer4 >= 24) {
            return 0.0;
        }
        if (integer5 < 0 || integer5 >= 24) {
            return 0.0;
        }
        if (integer6 < 0 || integer6 >= 24) {
            return 0.0;
        }
        return SurfaceChunkGenerator.h[integer6 * 24 * 24 + integer4 * 24 + integer5];
    }
    
    private static double b(final int integer1, final int integer2, final int integer3) {
        final double double4 = integer1 * integer1 + integer3 * integer3;
        final double double5 = integer2 + 0.5;
        final double double6 = double5 * double5;
        final double double7 = Math.pow(2.718281828459045, -(double6 / 16.0 + double4 / 16.0));
        final double double8 = -double5 * MathHelper.fastInverseSqrt(double6 / 2.0 + double4 / 2.0) / 2.0;
        return double8 * double7;
    }
    
    static {
        int integer2;
        int integer3;
        int integer4;
        h = SystemUtil.<float[]>consume(new float[13824], arr -> {
            for (integer2 = 0; integer2 < 24; ++integer2) {
                for (integer3 = 0; integer3 < 24; ++integer3) {
                    for (integer4 = 0; integer4 < 24; ++integer4) {
                        arr[integer2 * 24 * 24 + integer3 * 24 + integer4] = (float)b(integer3 - 12, integer4 - 12, integer2 - 12);
                    }
                }
            }
            return;
        });
        AIR = Blocks.AIR.getDefaultState();
    }
}
