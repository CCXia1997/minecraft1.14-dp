package net.minecraft.entity.thrown;

import net.minecraft.state.AbstractPropertyContainer;
import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffect;
import javax.annotation.Nullable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.List;
import net.minecraft.potion.Potion;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.potion.Potions;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import org.apache.logging.log4j.Logger;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.FlyingItemEntity;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = awj.class) })
public class ThrownPotionEntity extends ThrownEntity implements FlyingItemEntity
{
    private static final TrackedData<ItemStack> ITEM_STACK;
    private static final Logger LOGGER;
    public static final Predicate<LivingEntity> WATER_HURTS;
    
    public ThrownPotionEntity(final EntityType<? extends ThrownPotionEntity> type, final World world) {
        super(type, world);
    }
    
    public ThrownPotionEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.POTION, livingEntity, world);
    }
    
    public ThrownPotionEntity(final World world, final double x, final double y, final double double6) {
        super(EntityType.POTION, x, y, double6, world);
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<ItemStack>startTracking(ThrownPotionEntity.ITEM_STACK, ItemStack.EMPTY);
    }
    
    @Override
    public ItemStack getStack() {
        final ItemStack itemStack1 = this.getDataTracker().<ItemStack>get(ThrownPotionEntity.ITEM_STACK);
        if (itemStack1.getItem() != Items.oS && itemStack1.getItem() != Items.oV) {
            if (this.world != null) {
                ThrownPotionEntity.LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
            }
            return new ItemStack(Items.oS);
        }
        return itemStack1;
    }
    
    public void setItemStack(final ItemStack itemStack) {
        this.getDataTracker().<ItemStack>set(ThrownPotionEntity.ITEM_STACK, itemStack.copy());
    }
    
    @Override
    protected float getGravity() {
        return 0.05f;
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (this.world.isClient) {
            return;
        }
        final ItemStack itemStack2 = this.getStack();
        final Potion potion3 = PotionUtil.getPotion(itemStack2);
        final List<StatusEffectInstance> list4 = PotionUtil.getPotionEffects(itemStack2);
        final boolean boolean5 = potion3 == Potions.b && list4.isEmpty();
        if (hitResult.getType() == HitResult.Type.BLOCK && boolean5) {
            final BlockHitResult blockHitResult6 = (BlockHitResult)hitResult;
            final Direction direction7 = blockHitResult6.getSide();
            final BlockPos blockPos8 = blockHitResult6.getBlockPos().offset(direction7);
            this.extinguishFire(blockPos8, direction7);
            this.extinguishFire(blockPos8.offset(direction7.getOpposite()), direction7);
            for (final Direction direction8 : Direction.Type.HORIZONTAL) {
                this.extinguishFire(blockPos8.offset(direction8), direction8);
            }
        }
        if (boolean5) {
            this.damageEntitiesHurtByWater();
        }
        else if (!list4.isEmpty()) {
            if (this.isLingering()) {
                this.a(itemStack2, potion3);
            }
            else {
                this.a(list4, (hitResult.getType() == HitResult.Type.ENTITY) ? ((EntityHitResult)hitResult).getEntity() : null);
            }
        }
        final int integer6 = potion3.hasInstantEffect() ? 2007 : 2002;
        this.world.playLevelEvent(integer6, new BlockPos(this), PotionUtil.getColor(itemStack2));
        this.remove();
    }
    
    private void damageEntitiesHurtByWater() {
        final BoundingBox boundingBox1 = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        final List<LivingEntity> list2 = this.world.<LivingEntity>getEntities(LivingEntity.class, boundingBox1, ThrownPotionEntity.WATER_HURTS);
        if (!list2.isEmpty()) {
            for (final LivingEntity livingEntity4 : list2) {
                final double double5 = this.squaredDistanceTo(livingEntity4);
                if (double5 < 16.0 && doesWaterHurt(livingEntity4)) {
                    livingEntity4.damage(DamageSource.DROWN, 1.0f);
                }
            }
        }
    }
    
    private void a(final List<StatusEffectInstance> list, @Nullable final Entity entity) {
        final BoundingBox boundingBox3 = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        final List<LivingEntity> list2 = this.world.<LivingEntity>getEntities(LivingEntity.class, boundingBox3);
        if (!list2.isEmpty()) {
            for (final LivingEntity livingEntity6 : list2) {
                if (!livingEntity6.dt()) {
                    continue;
                }
                final double double7 = this.squaredDistanceTo(livingEntity6);
                if (double7 >= 16.0) {
                    continue;
                }
                double double8 = 1.0 - Math.sqrt(double7) / 4.0;
                if (livingEntity6 == entity) {
                    double8 = 1.0;
                }
                for (final StatusEffectInstance statusEffectInstance12 : list) {
                    final StatusEffect statusEffect13 = statusEffectInstance12.getEffectType();
                    if (statusEffect13.isInstant()) {
                        statusEffect13.applyInstantEffect(this, this.getOwner(), livingEntity6, statusEffectInstance12.getAmplifier(), double8);
                    }
                    else {
                        final int integer14 = (int)(double8 * statusEffectInstance12.getDuration() + 0.5);
                        if (integer14 <= 20) {
                            continue;
                        }
                        livingEntity6.addPotionEffect(new StatusEffectInstance(statusEffect13, integer14, statusEffectInstance12.getAmplifier(), statusEffectInstance12.isAmbient(), statusEffectInstance12.shouldShowParticles()));
                    }
                }
            }
        }
    }
    
    private void a(final ItemStack itemStack, final Potion potion) {
        final AreaEffectCloudEntity areaEffectCloudEntity3 = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
        areaEffectCloudEntity3.setOwner(this.getOwner());
        areaEffectCloudEntity3.setRadius(3.0f);
        areaEffectCloudEntity3.setRadiusStart(-0.5f);
        areaEffectCloudEntity3.setWaitTime(10);
        areaEffectCloudEntity3.setRadiusGrowth(-areaEffectCloudEntity3.getRadius() / areaEffectCloudEntity3.getDuration());
        areaEffectCloudEntity3.setPotion(potion);
        for (final StatusEffectInstance statusEffectInstance5 : PotionUtil.getCustomPotionEffects(itemStack)) {
            areaEffectCloudEntity3.addEffect(new StatusEffectInstance(statusEffectInstance5));
        }
        final CompoundTag compoundTag4 = itemStack.getTag();
        if (compoundTag4 != null && compoundTag4.containsKey("CustomPotionColor", 99)) {
            areaEffectCloudEntity3.setColor(compoundTag4.getInt("CustomPotionColor"));
        }
        this.world.spawnEntity(areaEffectCloudEntity3);
    }
    
    private boolean isLingering() {
        return this.getStack().getItem() == Items.oV;
    }
    
    private void extinguishFire(final BlockPos blockPos, final Direction direction) {
        final BlockState blockState3 = this.world.getBlockState(blockPos);
        final Block block4 = blockState3.getBlock();
        if (block4 == Blocks.bM) {
            this.world.a(null, blockPos.offset(direction), direction.getOpposite());
        }
        else if (block4 == Blocks.lV && blockState3.<Boolean>get((Property<Boolean>)CampfireBlock.LIT)) {
            this.world.playLevelEvent(null, 1009, blockPos, 0);
            this.world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState3).<Comparable, Boolean>with((Property<Comparable>)CampfireBlock.LIT, false));
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        final ItemStack itemStack2 = ItemStack.fromTag(tag.getCompound("Potion"));
        if (itemStack2.isEmpty()) {
            this.remove();
        }
        else {
            this.setItemStack(itemStack2);
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        final ItemStack itemStack2 = this.getStack();
        if (!itemStack2.isEmpty()) {
            tag.put("Potion", itemStack2.toTag(new CompoundTag()));
        }
    }
    
    private static boolean doesWaterHurt(final LivingEntity entityHit) {
        return entityHit instanceof EndermanEntity || entityHit instanceof BlazeEntity;
    }
    
    static {
        ITEM_STACK = DataTracker.<ItemStack>registerData(ThrownPotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        LOGGER = LogManager.getLogger();
        WATER_HURTS = ThrownPotionEntity::doesWaterHurt;
    }
}
