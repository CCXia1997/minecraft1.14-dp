package net.minecraft.client.render.block;

import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.model.BakedQuad;
import java.util.List;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import java.util.BitSet;
import net.minecraft.util.math.Direction;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.MinecraftClient;
import java.util.Random;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.util.math.BlockPos;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockModelRenderer
{
    private final BlockColorMap colorMap;
    private static final ThreadLocal<Object2IntLinkedOpenHashMap<BlockPos>> brightnessCache;
    private static final ThreadLocal<Boolean> brightnessCacheEnabled;
    
    public BlockModelRenderer(final BlockColorMap blockColorMap) {
        this.colorMap = blockColorMap;
    }
    
    public boolean tesselate(final ExtendedBlockView view, final BakedModel model, final BlockState state, final BlockPos pos, final BufferBuilder buffer, final boolean testSides, final Random random, final long long8) {
        final boolean boolean10 = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();
        try {
            if (boolean10) {
                return this.tesselateSmooth(view, model, state, pos, buffer, testSides, random, long8);
            }
            return this.tesselateFlat(view, model, state, pos, buffer, testSides, random, long8);
        }
        catch (Throwable throwable11) {
            final CrashReport crashReport12 = CrashReport.create(throwable11, "Tesselating block model");
            final CrashReportSection crashReportSection13 = crashReport12.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection13, pos, state);
            crashReportSection13.add("Using AO", boolean10);
            throw new CrashException(crashReport12);
        }
    }
    
    public boolean tesselateSmooth(final ExtendedBlockView view, final BakedModel model, final BlockState state, final BlockPos pos, final BufferBuilder buffer, final boolean testSides, final Random random, final long long8) {
        boolean boolean10 = false;
        final float[] arr11 = new float[Direction.values().length * 2];
        final BitSet bitSet12 = new BitSet(3);
        final AmbientOcclusionCalculator ambientOcclusionCalculator13 = new AmbientOcclusionCalculator();
        for (final Direction direction17 : Direction.values()) {
            random.setSeed(long8);
            final List<BakedQuad> list18 = model.getQuads(state, direction17, random);
            if (!list18.isEmpty()) {
                if (!testSides || Block.shouldDrawSide(state, view, pos, direction17)) {
                    this.tesselateQuadsSmooth(view, state, pos, buffer, list18, arr11, bitSet12, ambientOcclusionCalculator13);
                    boolean10 = true;
                }
            }
        }
        random.setSeed(long8);
        final List<BakedQuad> list19 = model.getQuads(state, null, random);
        if (!list19.isEmpty()) {
            this.tesselateQuadsSmooth(view, state, pos, buffer, list19, arr11, bitSet12, ambientOcclusionCalculator13);
            boolean10 = true;
        }
        return boolean10;
    }
    
    public boolean tesselateFlat(final ExtendedBlockView view, final BakedModel model, final BlockState state, final BlockPos pos, final BufferBuilder buffer, final boolean testSides, final Random random, final long seed) {
        boolean boolean10 = false;
        final BitSet bitSet11 = new BitSet(3);
        for (final Direction direction15 : Direction.values()) {
            random.setSeed(seed);
            final List<BakedQuad> list16 = model.getQuads(state, direction15, random);
            if (!list16.isEmpty()) {
                if (!testSides || Block.shouldDrawSide(state, view, pos, direction15)) {
                    final int integer17 = state.getBlockBrightness(view, pos.offset(direction15));
                    this.tesselateQuadsFlat(view, state, pos, integer17, false, buffer, list16, bitSet11);
                    boolean10 = true;
                }
            }
        }
        random.setSeed(seed);
        final List<BakedQuad> list17 = model.getQuads(state, null, random);
        if (!list17.isEmpty()) {
            this.tesselateQuadsFlat(view, state, pos, -1, true, buffer, list17, bitSet11);
            boolean10 = true;
        }
        return boolean10;
    }
    
    private void tesselateQuadsSmooth(final ExtendedBlockView view, final BlockState state, final BlockPos pos, final BufferBuilder buffer, final List<BakedQuad> quads, final float[] faceShape, final BitSet shapeState, final AmbientOcclusionCalculator ambientOcclusionCalculator) {
        final Vec3d vec3d9 = state.getOffsetPos(view, pos);
        final double double10 = pos.getX() + vec3d9.x;
        final double double11 = pos.getY() + vec3d9.y;
        final double double12 = pos.getZ() + vec3d9.z;
        for (int integer16 = 0, integer17 = quads.size(); integer16 < integer17; ++integer16) {
            final BakedQuad bakedQuad18 = quads.get(integer16);
            this.updateShape(view, state, pos, bakedQuad18.getVertexData(), bakedQuad18.getFace(), faceShape, shapeState);
            ambientOcclusionCalculator.apply(view, state, pos, bakedQuad18.getFace(), faceShape, shapeState);
            buffer.putVertexData(bakedQuad18.getVertexData());
            buffer.brightness(ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[3]);
            if (bakedQuad18.hasColor()) {
                final int integer18 = this.colorMap.getRenderColor(state, view, pos, bakedQuad18.getColorIndex());
                final float float20 = (integer18 >> 16 & 0xFF) / 255.0f;
                final float float21 = (integer18 >> 8 & 0xFF) / 255.0f;
                final float float22 = (integer18 & 0xFF) / 255.0f;
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[0] * float20, ambientOcclusionCalculator.colorMultiplier[0] * float21, ambientOcclusionCalculator.colorMultiplier[0] * float22, 4);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[1] * float20, ambientOcclusionCalculator.colorMultiplier[1] * float21, ambientOcclusionCalculator.colorMultiplier[1] * float22, 3);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[2] * float20, ambientOcclusionCalculator.colorMultiplier[2] * float21, ambientOcclusionCalculator.colorMultiplier[2] * float22, 2);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[3] * float20, ambientOcclusionCalculator.colorMultiplier[3] * float21, ambientOcclusionCalculator.colorMultiplier[3] * float22, 1);
            }
            else {
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], 4);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], 3);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], 2);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], 1);
            }
            buffer.postPosition(double10, double11, double12);
        }
    }
    
    private void updateShape(final ExtendedBlockView extendedBlockView, final BlockState blockState, final BlockPos state, final int[] vertexData, final Direction facing, @Nullable final float[] faceShape, final BitSet bitSet) {
        float float8 = 32.0f;
        float float9 = 32.0f;
        float float10 = 32.0f;
        float float11 = -32.0f;
        float float12 = -32.0f;
        float float13 = -32.0f;
        for (int integer14 = 0; integer14 < 4; ++integer14) {
            final float float14 = Float.intBitsToFloat(vertexData[integer14 * 7]);
            final float float15 = Float.intBitsToFloat(vertexData[integer14 * 7 + 1]);
            final float float16 = Float.intBitsToFloat(vertexData[integer14 * 7 + 2]);
            float8 = Math.min(float8, float14);
            float9 = Math.min(float9, float15);
            float10 = Math.min(float10, float16);
            float11 = Math.max(float11, float14);
            float12 = Math.max(float12, float15);
            float13 = Math.max(float13, float16);
        }
        if (faceShape != null) {
            faceShape[Direction.WEST.getId()] = float8;
            faceShape[Direction.EAST.getId()] = float11;
            faceShape[Direction.DOWN.getId()] = float9;
            faceShape[Direction.UP.getId()] = float12;
            faceShape[Direction.NORTH.getId()] = float10;
            faceShape[Direction.SOUTH.getId()] = float13;
            final int integer14 = Direction.values().length;
            faceShape[Direction.WEST.getId() + integer14] = 1.0f - float8;
            faceShape[Direction.EAST.getId() + integer14] = 1.0f - float11;
            faceShape[Direction.DOWN.getId() + integer14] = 1.0f - float9;
            faceShape[Direction.UP.getId() + integer14] = 1.0f - float12;
            faceShape[Direction.NORTH.getId() + integer14] = 1.0f - float10;
            faceShape[Direction.SOUTH.getId() + integer14] = 1.0f - float13;
        }
        final float float17 = 1.0E-4f;
        final float float14 = 0.9999f;
        switch (facing) {
            case DOWN: {
                bitSet.set(1, float8 >= 1.0E-4f || float10 >= 1.0E-4f || float11 <= 0.9999f || float13 <= 0.9999f);
                bitSet.set(0, (float9 < 1.0E-4f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, state))) && float9 == float12);
                break;
            }
            case UP: {
                bitSet.set(1, float8 >= 1.0E-4f || float10 >= 1.0E-4f || float11 <= 0.9999f || float13 <= 0.9999f);
                bitSet.set(0, (float12 > 0.9999f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, state))) && float9 == float12);
                break;
            }
            case NORTH: {
                bitSet.set(1, float8 >= 1.0E-4f || float9 >= 1.0E-4f || float11 <= 0.9999f || float12 <= 0.9999f);
                bitSet.set(0, (float10 < 1.0E-4f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, state))) && float10 == float13);
                break;
            }
            case SOUTH: {
                bitSet.set(1, float8 >= 1.0E-4f || float9 >= 1.0E-4f || float11 <= 0.9999f || float12 <= 0.9999f);
                bitSet.set(0, (float13 > 0.9999f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, state))) && float10 == float13);
                break;
            }
            case WEST: {
                bitSet.set(1, float9 >= 1.0E-4f || float10 >= 1.0E-4f || float12 <= 0.9999f || float13 <= 0.9999f);
                bitSet.set(0, (float8 < 1.0E-4f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, state))) && float8 == float11);
                break;
            }
            case EAST: {
                bitSet.set(1, float9 >= 1.0E-4f || float10 >= 1.0E-4f || float12 <= 0.9999f || float13 <= 0.9999f);
                bitSet.set(0, (float11 > 0.9999f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, state))) && float8 == float11);
                break;
            }
        }
    }
    
    private void tesselateQuadsFlat(final ExtendedBlockView view, final BlockState state, final BlockPos pos, int brightness, final boolean checkBrightness, final BufferBuilder buffer, final List<BakedQuad> quads, final BitSet bitSet) {
        final Vec3d vec3d9 = state.getOffsetPos(view, pos);
        final double double10 = pos.getX() + vec3d9.x;
        final double double11 = pos.getY() + vec3d9.y;
        final double double12 = pos.getZ() + vec3d9.z;
        for (int integer16 = 0, integer17 = quads.size(); integer16 < integer17; ++integer16) {
            final BakedQuad bakedQuad18 = quads.get(integer16);
            if (checkBrightness) {
                this.updateShape(view, state, pos, bakedQuad18.getVertexData(), bakedQuad18.getFace(), null, bitSet);
                final BlockPos blockPos19 = bitSet.get(0) ? pos.offset(bakedQuad18.getFace()) : pos;
                brightness = state.getBlockBrightness(view, blockPos19);
            }
            buffer.putVertexData(bakedQuad18.getVertexData());
            buffer.brightness(brightness, brightness, brightness, brightness);
            if (bakedQuad18.hasColor()) {
                final int integer18 = this.colorMap.getRenderColor(state, view, pos, bakedQuad18.getColorIndex());
                final float float20 = (integer18 >> 16 & 0xFF) / 255.0f;
                final float float21 = (integer18 >> 8 & 0xFF) / 255.0f;
                final float float22 = (integer18 & 0xFF) / 255.0f;
                buffer.multiplyColor(float20, float21, float22, 4);
                buffer.multiplyColor(float20, float21, float22, 3);
                buffer.multiplyColor(float20, float21, float22, 2);
                buffer.multiplyColor(float20, float21, float22, 1);
            }
            buffer.postPosition(double10, double11, double12);
        }
    }
    
    public void render(final BakedModel model, final float colorMultiplier, final float red, final float green, final float float5) {
        this.render(null, model, colorMultiplier, red, green, float5);
    }
    
    public void render(@Nullable final BlockState state, final BakedModel model, final float colorMultiplier, final float red, final float green, final float float6) {
        final Random random7 = new Random();
        final long long8 = 42L;
        for (final Direction direction13 : Direction.values()) {
            random7.setSeed(42L);
            this.renderQuad(colorMultiplier, red, green, float6, model.getQuads(state, direction13, random7));
        }
        random7.setSeed(42L);
        this.renderQuad(colorMultiplier, red, green, float6, model.getQuads(state, null, random7));
    }
    
    public void render(final BakedModel model, final BlockState state, final float colorMultiplier, final boolean boolean4) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        final int integer5 = this.colorMap.getRenderColor(state, null, null, 0);
        final float float6 = (integer5 >> 16 & 0xFF) / 255.0f;
        final float float7 = (integer5 >> 8 & 0xFF) / 255.0f;
        final float float8 = (integer5 & 0xFF) / 255.0f;
        if (!boolean4) {
            GlStateManager.color4f(colorMultiplier, colorMultiplier, colorMultiplier, 1.0f);
        }
        this.render(state, model, colorMultiplier, float6, float7, float8);
    }
    
    private void renderQuad(final float colorMultiplier, final float red, final float green, final float blue, final List<BakedQuad> list) {
        final Tessellator tessellator6 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder7 = tessellator6.getBufferBuilder();
        for (int integer8 = 0, integer9 = list.size(); integer8 < integer9; ++integer8) {
            final BakedQuad bakedQuad10 = list.get(integer8);
            bufferBuilder7.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
            bufferBuilder7.putVertexData(bakedQuad10.getVertexData());
            if (bakedQuad10.hasColor()) {
                bufferBuilder7.setQuadColor(red * colorMultiplier, green * colorMultiplier, blue * colorMultiplier);
            }
            else {
                bufferBuilder7.setQuadColor(colorMultiplier, colorMultiplier, colorMultiplier);
            }
            final Vec3i vec3i11 = bakedQuad10.getFace().getVector();
            bufferBuilder7.postNormal((float)vec3i11.getX(), (float)vec3i11.getY(), (float)vec3i11.getZ());
            tessellator6.draw();
        }
    }
    
    public static void enableBrightnessCache() {
        BlockModelRenderer.brightnessCacheEnabled.set(true);
    }
    
    public static void disableBrightnessCache() {
        BlockModelRenderer.brightnessCache.get().clear();
        BlockModelRenderer.brightnessCacheEnabled.set(false);
    }
    
    private static int b(final BlockState blockState, final ExtendedBlockView extendedBlockView, final BlockPos blockPos) {
        final Boolean boolean4 = BlockModelRenderer.brightnessCacheEnabled.get();
        Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap5 = null;
        if (boolean4) {
            object2IntLinkedOpenHashMap5 = BlockModelRenderer.brightnessCache.get();
            final int integer6 = object2IntLinkedOpenHashMap5.getInt(blockPos);
            if (integer6 != Integer.MAX_VALUE) {
                return integer6;
            }
        }
        final int integer6 = blockState.getBlockBrightness(extendedBlockView, blockPos);
        if (object2IntLinkedOpenHashMap5 != null) {
            if (object2IntLinkedOpenHashMap5.size() == 50) {
                object2IntLinkedOpenHashMap5.removeFirstInt();
            }
            object2IntLinkedOpenHashMap5.put(blockPos.toImmutable(), integer6);
        }
        return integer6;
    }
    
    static {
        final Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap1;
        brightnessCache = ThreadLocal.<Object2IntLinkedOpenHashMap<BlockPos>>withInitial(() -> {
            object2IntLinkedOpenHashMap1 = new Object2IntLinkedOpenHashMap<BlockPos>(50) {
                protected void rehash(final int integer) {
                }
            };
            object2IntLinkedOpenHashMap1.defaultReturnValue(Integer.MAX_VALUE);
            return object2IntLinkedOpenHashMap1;
        });
        brightnessCacheEnabled = ThreadLocal.<Boolean>withInitial(() -> false);
    }
    
    @Environment(EnvType.CLIENT)
    enum Translation
    {
        DOWN(0, 1, 2, 3), 
        UP(2, 3, 0, 1), 
        NORTH(3, 0, 1, 2), 
        SOUTH(0, 1, 2, 3), 
        WEST(3, 0, 1, 2), 
        EAST(1, 2, 3, 0);
        
        private final int firstCorner;
        private final int secondCorner;
        private final int thirdCorner;
        private final int fourthCorner;
        private static final Translation[] VALUES;
        
        private Translation(final int integer1, final int integer2, final int integer3, final int integer4) {
            this.firstCorner = integer1;
            this.secondCorner = integer2;
            this.thirdCorner = integer3;
            this.fourthCorner = integer4;
        }
        
        public static Translation getTranslations(final Direction direction) {
            return Translation.VALUES[direction.getId()];
        }
        
        static {
            VALUES = SystemUtil.<Translation[]>consume(new Translation[6], arr -> {
                arr[Direction.DOWN.getId()] = Translation.DOWN;
                arr[Direction.UP.getId()] = Translation.UP;
                arr[Direction.NORTH.getId()] = Translation.NORTH;
                arr[Direction.SOUTH.getId()] = Translation.SOUTH;
                arr[Direction.WEST.getId()] = Translation.WEST;
                arr[Direction.EAST.getId()] = Translation.EAST;
            });
        }
    }
    
    @Environment(EnvType.CLIENT)
    class AmbientOcclusionCalculator
    {
        private final float[] colorMultiplier;
        private final int[] brightness;
        
        public AmbientOcclusionCalculator() {
            this.colorMultiplier = new float[4];
            this.brightness = new int[4];
        }
        
        public void apply(final ExtendedBlockView extendedBlockView, final BlockState blockState, final BlockPos blockPos, final Direction direction, final float[] arr, final BitSet bitSet) {
            final BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
            final NeighborData neighborData8 = NeighborData.getData(direction);
            final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
            mutable9.set(blockPos2).setOffset(neighborData8.faces[0]);
            final BlockState blockState2 = extendedBlockView.getBlockState(mutable9);
            final int integer11 = b(blockState2, extendedBlockView, mutable9);
            final float float12 = blockState2.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
            mutable9.set(blockPos2).setOffset(neighborData8.faces[1]);
            final BlockState blockState3 = extendedBlockView.getBlockState(mutable9);
            final int integer12 = b(blockState3, extendedBlockView, mutable9);
            final float float13 = blockState3.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
            mutable9.set(blockPos2).setOffset(neighborData8.faces[2]);
            final BlockState blockState4 = extendedBlockView.getBlockState(mutable9);
            final int integer13 = b(blockState4, extendedBlockView, mutable9);
            final float float14 = blockState4.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
            mutable9.set(blockPos2).setOffset(neighborData8.faces[3]);
            final BlockState blockState5 = extendedBlockView.getBlockState(mutable9);
            final int integer14 = b(blockState5, extendedBlockView, mutable9);
            final float float15 = blockState5.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
            mutable9.set(blockPos2).setOffset(neighborData8.faces[0]).setOffset(direction);
            final boolean boolean22 = extendedBlockView.getBlockState(mutable9).getLightSubtracted(extendedBlockView, mutable9) == 0;
            mutable9.set(blockPos2).setOffset(neighborData8.faces[1]).setOffset(direction);
            final boolean boolean23 = extendedBlockView.getBlockState(mutable9).getLightSubtracted(extendedBlockView, mutable9) == 0;
            mutable9.set(blockPos2).setOffset(neighborData8.faces[2]).setOffset(direction);
            final boolean boolean24 = extendedBlockView.getBlockState(mutable9).getLightSubtracted(extendedBlockView, mutable9) == 0;
            mutable9.set(blockPos2).setOffset(neighborData8.faces[3]).setOffset(direction);
            final boolean boolean25 = extendedBlockView.getBlockState(mutable9).getLightSubtracted(extendedBlockView, mutable9) == 0;
            float float16;
            int integer15;
            if (boolean24 || boolean22) {
                mutable9.set(blockPos2).setOffset(neighborData8.faces[0]).setOffset(neighborData8.faces[2]);
                final BlockState blockState6 = extendedBlockView.getBlockState(mutable9);
                float16 = blockState6.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
                integer15 = b(blockState6, extendedBlockView, mutable9);
            }
            else {
                float16 = float12;
                integer15 = integer11;
            }
            float float17;
            int integer16;
            if (boolean25 || boolean22) {
                mutable9.set(blockPos2).setOffset(neighborData8.faces[0]).setOffset(neighborData8.faces[3]);
                final BlockState blockState6 = extendedBlockView.getBlockState(mutable9);
                float17 = blockState6.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
                integer16 = b(blockState6, extendedBlockView, mutable9);
            }
            else {
                float17 = float12;
                integer16 = integer11;
            }
            float float18;
            int integer17;
            if (boolean24 || boolean23) {
                mutable9.set(blockPos2).setOffset(neighborData8.faces[1]).setOffset(neighborData8.faces[2]);
                final BlockState blockState6 = extendedBlockView.getBlockState(mutable9);
                float18 = blockState6.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
                integer17 = b(blockState6, extendedBlockView, mutable9);
            }
            else {
                float18 = float12;
                integer17 = integer11;
            }
            float float19;
            int integer18;
            if (boolean25 || boolean23) {
                mutable9.set(blockPos2).setOffset(neighborData8.faces[1]).setOffset(neighborData8.faces[3]);
                final BlockState blockState6 = extendedBlockView.getBlockState(mutable9);
                float19 = blockState6.getAmbientOcclusionLightLevel(extendedBlockView, mutable9);
                integer18 = b(blockState6, extendedBlockView, mutable9);
            }
            else {
                float19 = float12;
                integer18 = integer11;
            }
            int integer19 = b(blockState, extendedBlockView, blockPos);
            mutable9.set(blockPos).setOffset(direction);
            final BlockState blockState7 = extendedBlockView.getBlockState(mutable9);
            if (bitSet.get(0) || !blockState7.isFullOpaque(extendedBlockView, mutable9)) {
                integer19 = b(blockState7, extendedBlockView, mutable9);
            }
            final float float20 = bitSet.get(0) ? extendedBlockView.getBlockState(blockPos2).getAmbientOcclusionLightLevel(extendedBlockView, blockPos2) : extendedBlockView.getBlockState(blockPos).getAmbientOcclusionLightLevel(extendedBlockView, blockPos);
            final Translation translation37 = Translation.getTranslations(direction);
            if (!bitSet.get(1) || !neighborData8.nonCubicWeight) {
                final float float21 = (float15 + float12 + float17 + float20) * 0.25f;
                final float float22 = (float14 + float12 + float16 + float20) * 0.25f;
                final float float23 = (float14 + float13 + float18 + float20) * 0.25f;
                final float float24 = (float15 + float13 + float19 + float20) * 0.25f;
                this.brightness[translation37.firstCorner] = this.getAmbientOcclusionBrightness(integer14, integer11, integer16, integer19);
                this.brightness[translation37.secondCorner] = this.getAmbientOcclusionBrightness(integer13, integer11, integer15, integer19);
                this.brightness[translation37.thirdCorner] = this.getAmbientOcclusionBrightness(integer13, integer12, integer17, integer19);
                this.brightness[translation37.fourthCorner] = this.getAmbientOcclusionBrightness(integer14, integer12, integer18, integer19);
                this.colorMultiplier[translation37.firstCorner] = float21;
                this.colorMultiplier[translation37.secondCorner] = float22;
                this.colorMultiplier[translation37.thirdCorner] = float23;
                this.colorMultiplier[translation37.fourthCorner] = float24;
            }
            else {
                final float float21 = (float15 + float12 + float17 + float20) * 0.25f;
                final float float22 = (float14 + float12 + float16 + float20) * 0.25f;
                final float float23 = (float14 + float13 + float18 + float20) * 0.25f;
                final float float24 = (float15 + float13 + float19 + float20) * 0.25f;
                final float float25 = arr[neighborData8.i[0].shape] * arr[neighborData8.i[1].shape];
                final float float26 = arr[neighborData8.i[2].shape] * arr[neighborData8.i[3].shape];
                final float float27 = arr[neighborData8.i[4].shape] * arr[neighborData8.i[5].shape];
                final float float28 = arr[neighborData8.i[6].shape] * arr[neighborData8.i[7].shape];
                final float float29 = arr[neighborData8.j[0].shape] * arr[neighborData8.j[1].shape];
                final float float30 = arr[neighborData8.j[2].shape] * arr[neighborData8.j[3].shape];
                final float float31 = arr[neighborData8.j[4].shape] * arr[neighborData8.j[5].shape];
                final float float32 = arr[neighborData8.j[6].shape] * arr[neighborData8.j[7].shape];
                final float float33 = arr[neighborData8.k[0].shape] * arr[neighborData8.k[1].shape];
                final float float34 = arr[neighborData8.k[2].shape] * arr[neighborData8.k[3].shape];
                final float float35 = arr[neighborData8.k[4].shape] * arr[neighborData8.k[5].shape];
                final float float36 = arr[neighborData8.k[6].shape] * arr[neighborData8.k[7].shape];
                final float float37 = arr[neighborData8.l[0].shape] * arr[neighborData8.l[1].shape];
                final float float38 = arr[neighborData8.l[2].shape] * arr[neighborData8.l[3].shape];
                final float float39 = arr[neighborData8.l[4].shape] * arr[neighborData8.l[5].shape];
                final float float40 = arr[neighborData8.l[6].shape] * arr[neighborData8.l[7].shape];
                this.colorMultiplier[translation37.firstCorner] = float21 * float25 + float22 * float26 + float23 * float27 + float24 * float28;
                this.colorMultiplier[translation37.secondCorner] = float21 * float29 + float22 * float30 + float23 * float31 + float24 * float32;
                this.colorMultiplier[translation37.thirdCorner] = float21 * float33 + float22 * float34 + float23 * float35 + float24 * float36;
                this.colorMultiplier[translation37.fourthCorner] = float21 * float37 + float22 * float38 + float23 * float39 + float24 * float40;
                final int integer20 = this.getAmbientOcclusionBrightness(integer14, integer11, integer16, integer19);
                final int integer21 = this.getAmbientOcclusionBrightness(integer13, integer11, integer15, integer19);
                final int integer22 = this.getAmbientOcclusionBrightness(integer13, integer12, integer17, integer19);
                final int integer23 = this.getAmbientOcclusionBrightness(integer14, integer12, integer18, integer19);
                this.brightness[translation37.firstCorner] = this.getBrightness(integer20, integer21, integer22, integer23, float25, float26, float27, float28);
                this.brightness[translation37.secondCorner] = this.getBrightness(integer20, integer21, integer22, integer23, float29, float30, float31, float32);
                this.brightness[translation37.thirdCorner] = this.getBrightness(integer20, integer21, integer22, integer23, float33, float34, float35, float36);
                this.brightness[translation37.fourthCorner] = this.getBrightness(integer20, integer21, integer22, integer23, float37, float38, float39, float40);
            }
        }
        
        private int getAmbientOcclusionBrightness(int integer1, int integer2, int integer3, final int integer4) {
            if (integer1 == 0) {
                integer1 = integer4;
            }
            if (integer2 == 0) {
                integer2 = integer4;
            }
            if (integer3 == 0) {
                integer3 = integer4;
            }
            return integer1 + integer2 + integer3 + integer4 >> 2 & 0xFF00FF;
        }
        
        private int getBrightness(final int integer1, final int integer2, final int integer3, final int integer4, final float float5, final float float6, final float float7, final float float8) {
            final int integer5 = (int)((integer1 >> 16 & 0xFF) * float5 + (integer2 >> 16 & 0xFF) * float6 + (integer3 >> 16 & 0xFF) * float7 + (integer4 >> 16 & 0xFF) * float8) & 0xFF;
            final int integer6 = (int)((integer1 & 0xFF) * float5 + (integer2 & 0xFF) * float6 + (integer3 & 0xFF) * float7 + (integer4 & 0xFF) * float8) & 0xFF;
            return integer5 << 16 | integer6;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum NeighborOrientation
    {
        DOWN(Direction.DOWN, false), 
        UP(Direction.UP, false), 
        NORTH(Direction.NORTH, false), 
        SOUTH(Direction.SOUTH, false), 
        WEST(Direction.WEST, false), 
        EAST(Direction.EAST, false), 
        FLIP_DOWN(Direction.DOWN, true), 
        FLIP_UP(Direction.UP, true), 
        FLIP_NORTH(Direction.NORTH, true), 
        FLIP_SOUTH(Direction.SOUTH, true), 
        FLIP_WEST(Direction.WEST, true), 
        FLIP_EAST(Direction.EAST, true);
        
        private final int shape;
        
        private NeighborOrientation(final Direction direction, final boolean boolean2) {
            this.shape = direction.getId() + (boolean2 ? Direction.values().length : 0);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum NeighborData
    {
        DOWN(new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH }, 0.5f, true, new NeighborOrientation[] { NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH }, new NeighborOrientation[] { NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH }), 
        UP(new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH }, 1.0f, true, new NeighborOrientation[] { NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH }, new NeighborOrientation[] { NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH }), 
        NORTH(new Direction[] { Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST }, 0.8f, true, new NeighborOrientation[] { NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST }, new NeighborOrientation[] { NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST }, new NeighborOrientation[] { NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST }, new NeighborOrientation[] { NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST }), 
        SOUTH(new Direction[] { Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP }, 0.8f, true, new NeighborOrientation[] { NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST }, new NeighborOrientation[] { NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST }, new NeighborOrientation[] { NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST }, new NeighborOrientation[] { NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST }), 
        WEST(new Direction[] { Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH }, 0.6f, true, new NeighborOrientation[] { NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH }, new NeighborOrientation[] { NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH }), 
        EAST(new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH }, 0.6f, true, new NeighborOrientation[] { NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH }, new NeighborOrientation[] { NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH }, new NeighborOrientation[] { NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH });
        
        private final Direction[] faces;
        private final boolean nonCubicWeight;
        private final NeighborOrientation[] i;
        private final NeighborOrientation[] j;
        private final NeighborOrientation[] k;
        private final NeighborOrientation[] l;
        private static final NeighborData[] m;
        
        private NeighborData(final Direction[] arr, final float float2, final boolean boolean3, final NeighborOrientation[] arr4, final NeighborOrientation[] arr5, final NeighborOrientation[] arr6, final NeighborOrientation[] arr7) {
            this.faces = arr;
            this.nonCubicWeight = boolean3;
            this.i = arr4;
            this.j = arr5;
            this.k = arr6;
            this.l = arr7;
        }
        
        public static NeighborData getData(final Direction direction) {
            return NeighborData.m[direction.getId()];
        }
        
        static {
            m = SystemUtil.<NeighborData[]>consume(new NeighborData[6], arr -> {
                arr[Direction.DOWN.getId()] = NeighborData.DOWN;
                arr[Direction.UP.getId()] = NeighborData.UP;
                arr[Direction.NORTH.getId()] = NeighborData.NORTH;
                arr[Direction.SOUTH.getId()] = NeighborData.SOUTH;
                arr[Direction.WEST.getId()] = NeighborData.WEST;
                arr[Direction.EAST.getId()] = NeighborData.EAST;
            });
        }
    }
}
