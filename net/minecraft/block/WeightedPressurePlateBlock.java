package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.IntegerProperty;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock
{
    public static final IntegerProperty POWER;
    private final int weight;
    
    protected WeightedPressurePlateBlock(final int weight, final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)WeightedPressurePlateBlock.POWER, 0));
        this.weight = weight;
    }
    
    @Override
    protected int getRedstoneOutput(final World world, final BlockPos pos) {
        final int integer3 = Math.min(world.<Entity>getEntities(Entity.class, WeightedPressurePlateBlock.BOX.offset(pos)).size(), this.weight);
        if (integer3 > 0) {
            final float float4 = Math.min(this.weight, integer3) / (float)this.weight;
            return MathHelper.ceil(float4 * 15.0f);
        }
        return 0;
    }
    
    @Override
    protected void playPressSound(final IWorld world, final BlockPos pos) {
        world.playSound(null, pos, SoundEvents.gr, SoundCategory.e, 0.3f, 0.90000004f);
    }
    
    @Override
    protected void playDepressSound(final IWorld world, final BlockPos pos) {
        world.playSound(null, pos, SoundEvents.gq, SoundCategory.e, 0.3f, 0.75f);
    }
    
    @Override
    protected int getRedstoneOutput(final BlockState state) {
        return state.<Integer>get((Property<Integer>)WeightedPressurePlateBlock.POWER);
    }
    
    @Override
    protected BlockState setRedstoneOutput(final BlockState state, final int rsOut) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)WeightedPressurePlateBlock.POWER, rsOut);
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 10;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(WeightedPressurePlateBlock.POWER);
    }
    
    static {
        POWER = Properties.POWER;
    }
}
