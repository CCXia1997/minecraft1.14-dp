package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class PressurePlateBlock extends AbstractPressurePlateBlock
{
    public static final BooleanProperty POWERED;
    private final Type type;
    
    protected PressurePlateBlock(final Type type, final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)PressurePlateBlock.POWERED, false));
        this.type = type;
    }
    
    @Override
    protected int getRedstoneOutput(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)PressurePlateBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    protected BlockState setRedstoneOutput(final BlockState state, final int rsOut) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)PressurePlateBlock.POWERED, rsOut > 0);
    }
    
    @Override
    protected void playPressSound(final IWorld world, final BlockPos pos) {
        if (this.material == Material.WOOD) {
            world.playSound(null, pos, SoundEvents.nQ, SoundCategory.e, 0.3f, 0.8f);
        }
        else {
            world.playSound(null, pos, SoundEvents.lt, SoundCategory.e, 0.3f, 0.6f);
        }
    }
    
    @Override
    protected void playDepressSound(final IWorld world, final BlockPos pos) {
        if (this.material == Material.WOOD) {
            world.playSound(null, pos, SoundEvents.nP, SoundCategory.e, 0.3f, 0.7f);
        }
        else {
            world.playSound(null, pos, SoundEvents.ls, SoundCategory.e, 0.3f, 0.5f);
        }
    }
    
    @Override
    protected int getRedstoneOutput(final World world, final BlockPos pos) {
        final BoundingBox boundingBox3 = PressurePlateBlock.BOX.offset(pos);
        List<? extends Entity> list4 = null;
        switch (this.type) {
            case WOOD: {
                list4 = world.getEntities((Entity)null, boundingBox3);
                break;
            }
            case STONE: {
                list4 = world.getEntities(LivingEntity.class, boundingBox3);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!list4.isEmpty()) {
            for (final Entity entity6 : list4) {
                if (!entity6.canAvoidTraps()) {
                    return 15;
                }
            }
        }
        return 0;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PressurePlateBlock.POWERED);
    }
    
    static {
        POWERED = Properties.POWERED;
    }
    
    public enum Type
    {
        WOOD, 
        STONE;
    }
}
