package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class TntBlock extends Block
{
    public static final BooleanProperty UNSTABLE;
    
    public TntBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)TntBlock.UNSTABLE, false));
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        if (world.isReceivingRedstonePower(pos)) {
            primeTnt(world, pos);
            world.clearBlockState(pos, false);
        }
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isReceivingRedstonePower(pos)) {
            primeTnt(world, pos);
            world.clearBlockState(pos, false);
        }
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        if (!world.isClient() && !player.isCreative() && state.<Boolean>get((Property<Boolean>)TntBlock.UNSTABLE)) {
            primeTnt(world, pos);
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public void onDestroyedByExplosion(final World world, final BlockPos pos, final Explosion explosion) {
        if (world.isClient) {
            return;
        }
        final PrimedTntEntity primedTntEntity4 = new PrimedTntEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, explosion.getCausingEntity());
        primedTntEntity4.setFuse((short)(world.random.nextInt(primedTntEntity4.getFuseTimer() / 4) + primedTntEntity4.getFuseTimer() / 8));
        world.spawnEntity(primedTntEntity4);
    }
    
    public static void primeTnt(final World world, final BlockPos blockPos) {
        primeTnt(world, blockPos, null);
    }
    
    private static void primeTnt(final World world, final BlockPos blockPos, @Nullable final LivingEntity livingEntity) {
        if (world.isClient) {
            return;
        }
        final PrimedTntEntity primedTntEntity4 = new PrimedTntEntity(world, blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f, livingEntity);
        world.spawnEntity(primedTntEntity4);
        world.playSound(null, primedTntEntity4.x, primedTntEntity4.y, primedTntEntity4.z, SoundEvents.lD, SoundCategory.e, 1.0f, 1.0f);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final ItemStack itemStack7 = player.getStackInHand(hand);
        final Item item8 = itemStack7.getItem();
        if (item8 == Items.jd || item8 == Items.nC) {
            primeTnt(world, pos, player);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            if (item8 == Items.jd) {
                itemStack7.<PlayerEntity>applyDamage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }
            else {
                itemStack7.subtractAmount(1);
            }
            return true;
        }
        return super.activate(state, world, pos, player, hand, blockHitResult);
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (!world.isClient && entity instanceof ProjectileEntity) {
            final ProjectileEntity projectileEntity5 = (ProjectileEntity)entity;
            final Entity entity2 = projectileEntity5.getOwner();
            if (projectileEntity5.isOnFire()) {
                primeTnt(world, pos, (entity2 instanceof LivingEntity) ? ((LivingEntity)entity2) : null);
                world.clearBlockState(pos, false);
            }
        }
    }
    
    @Override
    public boolean shouldDropItemsOnExplosion(final Explosion explosion) {
        return false;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(TntBlock.UNSTABLE);
    }
    
    static {
        UNSTABLE = Properties.UNSTABLE;
    }
}
