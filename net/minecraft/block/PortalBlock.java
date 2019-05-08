package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.block.pattern.CachedBlockPosition;
import com.google.common.cache.LoadingCache;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import javax.annotation.Nullable;
import net.minecraft.world.IWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.EntityType;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.EnumProperty;

public class PortalBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS;
    protected static final VoxelShape X_SHAPE;
    protected static final VoxelShape Z_SHAPE;
    
    public PortalBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Direction.Axis, Direction.Axis>with(PortalBlock.AXIS, Direction.Axis.X));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Direction.Axis>get(PortalBlock.AXIS)) {
            case Z: {
                return PortalBlock.Z_SHAPE;
            }
            default: {
                return PortalBlock.X_SHAPE;
            }
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, BlockPos pos, final Random random) {
        if (world.dimension.hasVisibleSky() && world.getGameRules().getBoolean("doMobSpawning") && random.nextInt(2000) < world.getDifficulty().getId()) {
            while (world.getBlockState(pos).getBlock() == this) {
                pos = pos.down();
            }
            if (world.getBlockState(pos).allowsSpawning(world, pos, EntityType.ZOMBIE_PIGMAN)) {
                final Entity entity5 = EntityType.ZOMBIE_PIGMAN.spawn(world, null, null, null, pos.up(), SpawnType.d, false, false);
                if (entity5 != null) {
                    entity5.portalCooldown = entity5.getDefaultPortalCooldown();
                }
            }
        }
    }
    
    public boolean a(final IWorld iWorld, final BlockPos blockPos) {
        final a a3 = this.b(iWorld, blockPos);
        if (a3 != null) {
            a3.e();
            return true;
        }
        return false;
    }
    
    @Nullable
    public a b(final IWorld iWorld, final BlockPos blockPos) {
        final a a3 = new a(iWorld, blockPos, Direction.Axis.X);
        if (a3.d() && a3.e == 0) {
            return a3;
        }
        final a a4 = new a(iWorld, blockPos, Direction.Axis.Z);
        if (a4.d() && a4.e == 0) {
            return a4;
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final Direction.Axis axis7 = facing.getAxis();
        final Direction.Axis axis8 = state.<Direction.Axis>get(PortalBlock.AXIS);
        final boolean boolean9 = axis8 != axis7 && axis7.isHorizontal();
        if (boolean9 || neighborState.getBlock() == this || new a(world, pos, axis8).f()) {
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
        return Blocks.AIR.getDefaultState();
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
            entity.setInPortal(pos);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (rnd.nextInt(100) == 0) {
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.iX, SoundCategory.e, 0.5f, rnd.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int integer5 = 0; integer5 < 4; ++integer5) {
            double double6 = pos.getX() + rnd.nextFloat();
            final double double7 = pos.getY() + rnd.nextFloat();
            double double8 = pos.getZ() + rnd.nextFloat();
            double double9 = (rnd.nextFloat() - 0.5) * 0.5;
            final double double10 = (rnd.nextFloat() - 0.5) * 0.5;
            double double11 = (rnd.nextFloat() - 0.5) * 0.5;
            final int integer6 = rnd.nextInt(2) * 2 - 1;
            if (world.getBlockState(pos.west()).getBlock() == this || world.getBlockState(pos.east()).getBlock() == this) {
                double8 = pos.getZ() + 0.5 + 0.25 * integer6;
                double11 = rnd.nextFloat() * 2.0f * integer6;
            }
            else {
                double6 = pos.getX() + 0.5 + 0.25 * integer6;
                double9 = rnd.nextFloat() * 2.0f * integer6;
            }
            world.addParticle(ParticleTypes.O, double6, double7, double8, double9, double10, double11);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        switch (rotation) {
            case ROT_270:
            case ROT_90: {
                switch (state.<Direction.Axis>get(PortalBlock.AXIS)) {
                    case X: {
                        return ((AbstractPropertyContainer<O, BlockState>)state).<Direction.Axis, Direction.Axis>with(PortalBlock.AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return ((AbstractPropertyContainer<O, BlockState>)state).<Direction.Axis, Direction.Axis>with(PortalBlock.AXIS, Direction.Axis.X);
                    }
                    default: {
                        return state;
                    }
                }
                break;
            }
            default: {
                return state;
            }
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PortalBlock.AXIS);
    }
    
    public BlockPattern.Result findPortal(final IWorld world, final BlockPos pos) {
        Direction.Axis axis3 = Direction.Axis.Z;
        a a4 = new a(world, pos, Direction.Axis.X);
        final LoadingCache<BlockPos, CachedBlockPosition> loadingCache5 = BlockPattern.makeCache(world, true);
        if (!a4.d()) {
            axis3 = Direction.Axis.X;
            a4 = new a(world, pos, Direction.Axis.Z);
        }
        if (!a4.d()) {
            return new BlockPattern.Result(pos, Direction.NORTH, Direction.UP, loadingCache5, 1, 1, 1);
        }
        final int[] arr6 = new int[Direction.AxisDirection.values().length];
        final Direction direction7 = a4.c.rotateYCounterclockwise();
        final BlockPos blockPos8 = a4.f.up(a4.a() - 1);
        for (final Direction.AxisDirection axisDirection12 : Direction.AxisDirection.values()) {
            final BlockPattern.Result result13 = new BlockPattern.Result((direction7.getDirection() == axisDirection12) ? blockPos8 : blockPos8.offset(a4.c, a4.b() - 1), Direction.get(axisDirection12, axis3), Direction.UP, loadingCache5, a4.b(), a4.a(), 1);
            for (int integer14 = 0; integer14 < a4.b(); ++integer14) {
                for (int integer15 = 0; integer15 < a4.a(); ++integer15) {
                    final CachedBlockPosition cachedBlockPosition16 = result13.translate(integer14, integer15, 1);
                    if (!cachedBlockPosition16.getBlockState().isAir()) {
                        final int[] array = arr6;
                        final int ordinal = axisDirection12.ordinal();
                        ++array[ordinal];
                    }
                }
            }
        }
        Direction.AxisDirection axisDirection13 = Direction.AxisDirection.POSITIVE;
        for (final Direction.AxisDirection axisDirection14 : Direction.AxisDirection.values()) {
            if (arr6[axisDirection14.ordinal()] < arr6[axisDirection13.ordinal()]) {
                axisDirection13 = axisDirection14;
            }
        }
        return new BlockPattern.Result((direction7.getDirection() == axisDirection13) ? blockPos8 : blockPos8.offset(a4.c, a4.b() - 1), Direction.get(axisDirection13, axis3), Direction.UP, loadingCache5, a4.b(), a4.a(), 1);
    }
    
    static {
        AXIS = Properties.AXIS_XZ;
        X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
        Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    }
    
    public static class a
    {
        private final IWorld a;
        private final Direction.Axis b;
        private final Direction c;
        private final Direction d;
        private int e;
        @Nullable
        private BlockPos f;
        private int g;
        private int h;
        
        public a(final IWorld iWorld, BlockPos blockPos, final Direction.Axis axis) {
            this.a = iWorld;
            this.b = axis;
            if (axis == Direction.Axis.X) {
                this.d = Direction.EAST;
                this.c = Direction.WEST;
            }
            else {
                this.d = Direction.NORTH;
                this.c = Direction.SOUTH;
            }
            for (BlockPos blockPos2 = blockPos; blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.a(iWorld.getBlockState(blockPos.down())); blockPos = blockPos.down()) {}
            final int integer5 = this.a(blockPos, this.d) - 1;
            if (integer5 >= 0) {
                this.f = blockPos.offset(this.d, integer5);
                this.h = this.a(this.f, this.c);
                if (this.h < 2 || this.h > 21) {
                    this.f = null;
                    this.h = 0;
                }
            }
            if (this.f != null) {
                this.g = this.c();
            }
        }
        
        protected int a(final BlockPos blockPos, final Direction direction) {
            int integer3;
            for (integer3 = 0; integer3 < 22; ++integer3) {
                final BlockPos blockPos2 = blockPos.offset(direction, integer3);
                if (!this.a(this.a.getBlockState(blockPos2))) {
                    break;
                }
                if (this.a.getBlockState(blockPos2.down()).getBlock() != Blocks.bJ) {
                    break;
                }
            }
            final Block block4 = this.a.getBlockState(blockPos.offset(direction, integer3)).getBlock();
            if (block4 == Blocks.bJ) {
                return integer3;
            }
            return 0;
        }
        
        public int a() {
            return this.g;
        }
        
        public int b() {
            return this.h;
        }
        
        protected int c() {
            this.g = 0;
        Label_0189:
            while (this.g < 21) {
                for (int integer1 = 0; integer1 < this.h; ++integer1) {
                    final BlockPos blockPos2 = this.f.offset(this.c, integer1).up(this.g);
                    final BlockState blockState3 = this.a.getBlockState(blockPos2);
                    if (!this.a(blockState3)) {
                        break Label_0189;
                    }
                    Block block4 = blockState3.getBlock();
                    if (block4 == Blocks.cM) {
                        ++this.e;
                    }
                    if (integer1 == 0) {
                        block4 = this.a.getBlockState(blockPos2.offset(this.d)).getBlock();
                        if (block4 != Blocks.bJ) {
                            break Label_0189;
                        }
                    }
                    else if (integer1 == this.h - 1) {
                        block4 = this.a.getBlockState(blockPos2.offset(this.c)).getBlock();
                        if (block4 != Blocks.bJ) {
                            break Label_0189;
                        }
                    }
                }
                ++this.g;
            }
            for (int integer1 = 0; integer1 < this.h; ++integer1) {
                if (this.a.getBlockState(this.f.offset(this.c, integer1).up(this.g)).getBlock() != Blocks.bJ) {
                    this.g = 0;
                    break;
                }
            }
            if (this.g > 21 || this.g < 3) {
                this.f = null;
                this.h = 0;
                return this.g = 0;
            }
            return this.g;
        }
        
        protected boolean a(final BlockState blockState) {
            final Block block2 = blockState.getBlock();
            return blockState.isAir() || block2 == Blocks.bM || block2 == Blocks.cM;
        }
        
        public boolean d() {
            return this.f != null && this.h >= 2 && this.h <= 21 && this.g >= 3 && this.g <= 21;
        }
        
        public void e() {
            for (int integer1 = 0; integer1 < this.h; ++integer1) {
                final BlockPos blockPos2 = this.f.offset(this.c, integer1);
                for (int integer2 = 0; integer2 < this.g; ++integer2) {
                    this.a.setBlockState(blockPos2.up(integer2), ((AbstractPropertyContainer<O, BlockState>)Blocks.cM.getDefaultState()).<Direction.Axis, Direction.Axis>with(PortalBlock.AXIS, this.b), 18);
                }
            }
        }
        
        private boolean g() {
            return this.e >= this.h * this.g;
        }
        
        public boolean f() {
            return this.d() && this.g();
        }
    }
}
