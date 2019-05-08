package net.minecraft.client.render.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.util.math.MathHelper;
import net.minecraft.tag.FluidTags;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FluidRenderer
{
    private final Sprite[] lavaSprites;
    private final Sprite[] waterSprites;
    private Sprite waterOverlaySprite;
    
    public FluidRenderer() {
        this.lavaSprites = new Sprite[2];
        this.waterSprites = new Sprite[2];
    }
    
    protected void onResourceReload() {
        final SpriteAtlasTexture spriteAtlasTexture1 = MinecraftClient.getInstance().getSpriteAtlas();
        this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.B.getDefaultState()).getSprite();
        this.lavaSprites[1] = spriteAtlasTexture1.getSprite(ModelLoader.LAVA_FLOW);
        this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.A.getDefaultState()).getSprite();
        this.waterSprites[1] = spriteAtlasTexture1.getSprite(ModelLoader.WATER_FLOW);
        this.waterOverlaySprite = spriteAtlasTexture1.getSprite(ModelLoader.WATER_OVERLAY);
    }
    
    private static boolean isSameFluid(final BlockView world, final BlockPos pos, final Direction side, final FluidState state) {
        final BlockPos blockPos5 = pos.offset(side);
        final FluidState fluidState6 = world.getFluidState(blockPos5);
        return fluidState6.getFluid().matchesType(state.getFluid());
    }
    
    private static boolean a(final BlockView blockView, final BlockPos blockPos, final Direction direction, final float float4) {
        final BlockPos blockPos2 = blockPos.offset(direction);
        final BlockState blockState6 = blockView.getBlockState(blockPos2);
        if (blockState6.isFullBoundsCubeForCulling()) {
            final VoxelShape voxelShape7 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, float4, 1.0);
            final VoxelShape voxelShape8 = blockState6.j(blockView, blockPos2);
            return VoxelShapes.a(voxelShape7, voxelShape8, direction);
        }
        return false;
    }
    
    public boolean tesselate(final ExtendedBlockView world, final BlockPos pos, final BufferBuilder bufferBuilder, final FluidState state) {
        final boolean boolean5 = state.matches(FluidTags.b);
        final Sprite[] arr6 = boolean5 ? this.lavaSprites : this.waterSprites;
        final int integer7 = boolean5 ? 16777215 : BiomeColors.waterColorAt(world, pos);
        final float float8 = (integer7 >> 16 & 0xFF) / 255.0f;
        final float float9 = (integer7 >> 8 & 0xFF) / 255.0f;
        final float float10 = (integer7 & 0xFF) / 255.0f;
        final boolean boolean6 = !isSameFluid(world, pos, Direction.UP, state);
        final boolean boolean7 = !isSameFluid(world, pos, Direction.DOWN, state) && !a(world, pos, Direction.DOWN, 0.8888889f);
        final boolean boolean8 = !isSameFluid(world, pos, Direction.NORTH, state);
        final boolean boolean9 = !isSameFluid(world, pos, Direction.SOUTH, state);
        final boolean boolean10 = !isSameFluid(world, pos, Direction.WEST, state);
        final boolean boolean11 = !isSameFluid(world, pos, Direction.EAST, state);
        if (!boolean6 && !boolean7 && !boolean11 && !boolean10 && !boolean8 && !boolean9) {
            return false;
        }
        boolean boolean12 = false;
        final float float11 = 0.5f;
        final float float12 = 1.0f;
        final float float13 = 0.8f;
        final float float14 = 0.6f;
        float float15 = this.getNorthWestCornerFluidHeight(world, pos, state.getFluid());
        float float16 = this.getNorthWestCornerFluidHeight(world, pos.south(), state.getFluid());
        float float17 = this.getNorthWestCornerFluidHeight(world, pos.east().south(), state.getFluid());
        float float18 = this.getNorthWestCornerFluidHeight(world, pos.east(), state.getFluid());
        final double double26 = pos.getX();
        final double double27 = pos.getY();
        final double double28 = pos.getZ();
        final float float19 = 0.001f;
        if (boolean6 && !a(world, pos, Direction.UP, Math.min(Math.min(float15, float16), Math.min(float17, float18)))) {
            boolean12 = true;
            float15 -= 0.001f;
            float16 -= 0.001f;
            float17 -= 0.001f;
            float18 -= 0.001f;
            final Vec3d vec3d41 = state.getVelocity(world, pos);
            float float20;
            float float21;
            float float22;
            float float23;
            float float24;
            float float25;
            float float26;
            float float27;
            if (vec3d41.x == 0.0 && vec3d41.z == 0.0) {
                final Sprite sprite42 = arr6[0];
                float20 = sprite42.getU(0.0);
                float21 = sprite42.getV(0.0);
                float22 = float20;
                float23 = sprite42.getV(16.0);
                float24 = sprite42.getU(16.0);
                float25 = float23;
                float26 = float24;
                float27 = float21;
            }
            else {
                final Sprite sprite42 = arr6[1];
                final float float28 = (float)MathHelper.atan2(vec3d41.z, vec3d41.x) - 1.5707964f;
                final float float29 = MathHelper.sin(float28) * 0.25f;
                final float float30 = MathHelper.cos(float28) * 0.25f;
                final float float31 = 8.0f;
                float20 = sprite42.getU(8.0f + (-float30 - float29) * 16.0f);
                float21 = sprite42.getV(8.0f + (-float30 + float29) * 16.0f);
                float22 = sprite42.getU(8.0f + (-float30 + float29) * 16.0f);
                float23 = sprite42.getV(8.0f + (float30 + float29) * 16.0f);
                float24 = sprite42.getU(8.0f + (float30 + float29) * 16.0f);
                float25 = sprite42.getV(8.0f + (float30 - float29) * 16.0f);
                float26 = sprite42.getU(8.0f + (float30 - float29) * 16.0f);
                float27 = sprite42.getV(8.0f + (-float30 - float29) * 16.0f);
            }
            final float float32 = (float20 + float22 + float24 + float26) / 4.0f;
            final float float28 = (float21 + float23 + float25 + float27) / 4.0f;
            final float float29 = arr6[0].getWidth() / (arr6[0].getMaxU() - arr6[0].getMinU());
            final float float30 = arr6[0].getHeight() / (arr6[0].getMaxV() - arr6[0].getMinV());
            final float float31 = 4.0f / Math.max(float30, float29);
            float20 = MathHelper.lerp(float31, float20, float32);
            float22 = MathHelper.lerp(float31, float22, float32);
            float24 = MathHelper.lerp(float31, float24, float32);
            float26 = MathHelper.lerp(float31, float26, float32);
            float21 = MathHelper.lerp(float31, float21, float28);
            float23 = MathHelper.lerp(float31, float23, float28);
            float25 = MathHelper.lerp(float31, float25, float28);
            float27 = MathHelper.lerp(float31, float27, float28);
            final int integer8 = this.a(world, pos);
            final int integer9 = integer8 >> 16 & 0xFFFF;
            final int integer10 = integer8 & 0xFFFF;
            final float float33 = 1.0f * float8;
            final float float34 = 1.0f * float9;
            final float float35 = 1.0f * float10;
            bufferBuilder.vertex(double26 + 0.0, double27 + float15, double28 + 0.0).color(float33, float34, float35, 1.0f).texture(float20, float21).texture(integer9, integer10).next();
            bufferBuilder.vertex(double26 + 0.0, double27 + float16, double28 + 1.0).color(float33, float34, float35, 1.0f).texture(float22, float23).texture(integer9, integer10).next();
            bufferBuilder.vertex(double26 + 1.0, double27 + float17, double28 + 1.0).color(float33, float34, float35, 1.0f).texture(float24, float25).texture(integer9, integer10).next();
            bufferBuilder.vertex(double26 + 1.0, double27 + float18, double28 + 0.0).color(float33, float34, float35, 1.0f).texture(float26, float27).texture(integer9, integer10).next();
            if (state.b(world, pos.up())) {
                bufferBuilder.vertex(double26 + 0.0, double27 + float15, double28 + 0.0).color(float33, float34, float35, 1.0f).texture(float20, float21).texture(integer9, integer10).next();
                bufferBuilder.vertex(double26 + 1.0, double27 + float18, double28 + 0.0).color(float33, float34, float35, 1.0f).texture(float26, float27).texture(integer9, integer10).next();
                bufferBuilder.vertex(double26 + 1.0, double27 + float17, double28 + 1.0).color(float33, float34, float35, 1.0f).texture(float24, float25).texture(integer9, integer10).next();
                bufferBuilder.vertex(double26 + 0.0, double27 + float16, double28 + 1.0).color(float33, float34, float35, 1.0f).texture(float22, float23).texture(integer9, integer10).next();
            }
        }
        if (boolean7) {
            final float float20 = arr6[0].getMinU();
            final float float22 = arr6[0].getMaxU();
            final float float24 = arr6[0].getMinV();
            final float float26 = arr6[0].getMaxV();
            final int integer11 = this.a(world, pos.down());
            final int integer12 = integer11 >> 16 & 0xFFFF;
            final int integer13 = integer11 & 0xFFFF;
            final float float27 = 0.5f * float8;
            final float float36 = 0.5f * float9;
            final float float32 = 0.5f * float10;
            bufferBuilder.vertex(double26, double27, double28 + 1.0).color(float27, float36, float32, 1.0f).texture(float20, float26).texture(integer12, integer13).next();
            bufferBuilder.vertex(double26, double27, double28).color(float27, float36, float32, 1.0f).texture(float20, float24).texture(integer12, integer13).next();
            bufferBuilder.vertex(double26 + 1.0, double27, double28).color(float27, float36, float32, 1.0f).texture(float22, float24).texture(integer12, integer13).next();
            bufferBuilder.vertex(double26 + 1.0, double27, double28 + 1.0).color(float27, float36, float32, 1.0f).texture(float22, float26).texture(integer12, integer13).next();
            boolean12 = true;
        }
        for (int integer14 = 0; integer14 < 4; ++integer14) {
            float float22;
            float float24;
            double double29;
            double double30;
            double double31;
            double double32;
            Direction direction44;
            boolean boolean13;
            if (integer14 == 0) {
                float22 = float15;
                float24 = float18;
                double29 = double26;
                double30 = double26 + 1.0;
                double31 = double28 + 0.0010000000474974513;
                double32 = double28 + 0.0010000000474974513;
                direction44 = Direction.NORTH;
                boolean13 = boolean8;
            }
            else if (integer14 == 1) {
                float22 = float17;
                float24 = float16;
                double29 = double26 + 1.0;
                double30 = double26;
                double31 = double28 + 1.0 - 0.0010000000474974513;
                double32 = double28 + 1.0 - 0.0010000000474974513;
                direction44 = Direction.SOUTH;
                boolean13 = boolean9;
            }
            else if (integer14 == 2) {
                float22 = float16;
                float24 = float15;
                double29 = double26 + 0.0010000000474974513;
                double30 = double26 + 0.0010000000474974513;
                double31 = double28 + 1.0;
                double32 = double28;
                direction44 = Direction.WEST;
                boolean13 = boolean10;
            }
            else {
                float22 = float18;
                float24 = float17;
                double29 = double26 + 1.0 - 0.0010000000474974513;
                double30 = double26 + 1.0 - 0.0010000000474974513;
                double31 = double28;
                double32 = double28 + 1.0;
                direction44 = Direction.EAST;
                boolean13 = boolean11;
            }
            if (boolean13 && !a(world, pos, direction44, Math.max(float22, float24))) {
                boolean12 = true;
                final BlockPos blockPos46 = pos.offset(direction44);
                Sprite sprite43 = arr6[1];
                if (!boolean5) {
                    final Block block48 = world.getBlockState(blockPos46).getBlock();
                    if (block48 == Blocks.ao || block48 instanceof StainedGlassBlock) {
                        sprite43 = this.waterOverlaySprite;
                    }
                }
                final float float37 = sprite43.getU(0.0);
                final float float38 = sprite43.getU(8.0);
                final float float33 = sprite43.getV((1.0f - float22) * 16.0f * 0.5f);
                final float float34 = sprite43.getV((1.0f - float24) * 16.0f * 0.5f);
                final float float35 = sprite43.getV(8.0);
                final int integer15 = this.a(world, blockPos46);
                final int integer16 = integer15 >> 16 & 0xFFFF;
                final int integer17 = integer15 & 0xFFFF;
                final float float39 = (integer14 < 2) ? 0.8f : 0.6f;
                final float float40 = 1.0f * float39 * float8;
                final float float41 = 1.0f * float39 * float9;
                final float float42 = 1.0f * float39 * float10;
                bufferBuilder.vertex(double29, double27 + float22, double31).color(float40, float41, float42, 1.0f).texture(float37, float33).texture(integer16, integer17).next();
                bufferBuilder.vertex(double30, double27 + float24, double32).color(float40, float41, float42, 1.0f).texture(float38, float34).texture(integer16, integer17).next();
                bufferBuilder.vertex(double30, double27 + 0.0, double32).color(float40, float41, float42, 1.0f).texture(float38, float35).texture(integer16, integer17).next();
                bufferBuilder.vertex(double29, double27 + 0.0, double31).color(float40, float41, float42, 1.0f).texture(float37, float35).texture(integer16, integer17).next();
                if (sprite43 != this.waterOverlaySprite) {
                    bufferBuilder.vertex(double29, double27 + 0.0, double31).color(float40, float41, float42, 1.0f).texture(float37, float35).texture(integer16, integer17).next();
                    bufferBuilder.vertex(double30, double27 + 0.0, double32).color(float40, float41, float42, 1.0f).texture(float38, float35).texture(integer16, integer17).next();
                    bufferBuilder.vertex(double30, double27 + float24, double32).color(float40, float41, float42, 1.0f).texture(float38, float34).texture(integer16, integer17).next();
                    bufferBuilder.vertex(double29, double27 + float22, double31).color(float40, float41, float42, 1.0f).texture(float37, float33).texture(integer16, integer17).next();
                }
            }
        }
        return boolean12;
    }
    
    private int a(final ExtendedBlockView extendedBlockView, final BlockPos blockPos) {
        final int integer3 = extendedBlockView.getLightmapIndex(blockPos, 0);
        final int integer4 = extendedBlockView.getLightmapIndex(blockPos.up(), 0);
        final int integer5 = integer3 & 0xFF;
        final int integer6 = integer4 & 0xFF;
        final int integer7 = integer3 >> 16 & 0xFF;
        final int integer8 = integer4 >> 16 & 0xFF;
        return ((integer5 > integer6) ? integer5 : integer6) | ((integer7 > integer8) ? integer7 : integer8) << 16;
    }
    
    private float getNorthWestCornerFluidHeight(final BlockView world, final BlockPos pos, final Fluid fluid) {
        int integer4 = 0;
        float float5 = 0.0f;
        for (int integer5 = 0; integer5 < 4; ++integer5) {
            final BlockPos blockPos7 = pos.add(-(integer5 & 0x1), 0, -(integer5 >> 1 & 0x1));
            if (world.getFluidState(blockPos7.up()).getFluid().matchesType(fluid)) {
                return 1.0f;
            }
            final FluidState fluidState8 = world.getFluidState(blockPos7);
            if (fluidState8.getFluid().matchesType(fluid)) {
                final float float6 = fluidState8.getHeight(world, blockPos7);
                if (float6 >= 0.8f) {
                    float5 += float6 * 10.0f;
                    integer4 += 10;
                }
                else {
                    float5 += float6;
                    ++integer4;
                }
            }
            else if (!world.getBlockState(blockPos7).getMaterial().isSolid()) {
                ++integer4;
            }
        }
        return float5 / integer4;
    }
}
