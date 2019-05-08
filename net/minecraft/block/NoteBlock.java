package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.Instrument;
import net.minecraft.state.property.EnumProperty;

public class NoteBlock extends Block
{
    public static final EnumProperty<Instrument> INSTRUMENT;
    public static final BooleanProperty POWERED;
    public static final IntegerProperty NOTE;
    
    public NoteBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with(NoteBlock.INSTRUMENT, Instrument.a)).with((Property<Comparable>)NoteBlock.NOTE, 0)).<Comparable, Boolean>with((Property<Comparable>)NoteBlock.POWERED, false));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Instrument, Instrument>with(NoteBlock.INSTRUMENT, Instrument.fromBlockState(ctx.getWorld().getBlockState(ctx.getBlockPos().down())));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Instrument, Instrument>with(NoteBlock.INSTRUMENT, Instrument.fromBlockState(neighborState));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        final boolean boolean7 = world.isReceivingRedstonePower(pos);
        if (boolean7 != state.<Boolean>get((Property<Boolean>)NoteBlock.POWERED)) {
            if (boolean7) {
                this.playNote(world, pos);
            }
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)NoteBlock.POWERED, boolean7), 3);
        }
    }
    
    private void playNote(final World world, final BlockPos pos) {
        if (world.getBlockState(pos.up()).isAir()) {
            world.addBlockAction(pos, this, 0, 0);
        }
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)NoteBlock.NOTE);
        world.setBlockState(pos, state, 3);
        this.playNote(world, pos);
        player.incrementStat(Stats.ae);
        return true;
    }
    
    @Override
    public void onBlockBreakStart(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
        if (world.isClient) {
            return;
        }
        this.playNote(world, pos);
        player.incrementStat(Stats.ad);
    }
    
    @Override
    public boolean onBlockAction(final BlockState state, final World world, final BlockPos pos, final int type, final int data) {
        final int integer6 = state.<Integer>get((Property<Integer>)NoteBlock.NOTE);
        final float float7 = (float)Math.pow(2.0, (integer6 - 12) / 12.0);
        world.playSound(null, pos, state.<Instrument>get(NoteBlock.INSTRUMENT).getSound(), SoundCategory.c, 3.0f, float7);
        world.addParticle(ParticleTypes.M, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, integer6 / 24.0, 0.0, 0.0);
        return true;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(NoteBlock.INSTRUMENT, NoteBlock.POWERED, NoteBlock.NOTE);
    }
    
    static {
        INSTRUMENT = Properties.INSTRUMENT;
        POWERED = Properties.POWERED;
        NOTE = Properties.NOTE;
    }
}
