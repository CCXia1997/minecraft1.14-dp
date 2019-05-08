package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BaseBowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public abstract class HostileEntity extends MobEntityWithAi implements Monster
{
    protected HostileEntity(final EntityType<? extends HostileEntity> type, final World world) {
        super(type, world);
        this.experiencePoints = 5;
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.f;
    }
    
    @Override
    public void updateState() {
        this.tickHandSwing();
        this.updateDespawnCounter();
        super.updateState();
    }
    
    protected void updateDespawnCounter() {
        final float float1 = this.getBrightnessAtEyes();
        if (float1 > 0.5f) {
            this.despawnCounter += 2;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.eW;
    }
    
    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.eV;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return !this.isInvulnerableTo(source) && super.damage(source, amount);
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.eT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.eS;
    }
    
    @Override
    protected SoundEvent getFallSound(final int integer) {
        if (integer > 4) {
            return SoundEvents.eR;
        }
        return SoundEvents.eU;
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        return 0.5f - world.getBrightness(pos);
    }
    
    protected boolean checkLightLevelForSpawn() {
        final BlockPos blockPos1 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
        if (this.world.getLightLevel(LightType.SKY, blockPos1) > this.random.nextInt(32)) {
            return false;
        }
        final int integer2 = this.world.isThundering() ? this.world.d(blockPos1, 10) : this.world.getLightLevel(blockPos1);
        return integer2 <= this.random.nextInt(8);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL && this.checkLightLevelForSpawn() && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
    }
    
    @Override
    protected boolean canDropLootAndXp() {
        return true;
    }
    
    public boolean isAngryAt(final PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack getArrowType(final ItemStack itemStack) {
        if (itemStack.getItem() instanceof BaseBowItem) {
            final Predicate<ItemStack> predicate2 = ((BaseBowItem)itemStack.getItem()).getHeldProjectilePredicate();
            final ItemStack itemStack2 = BaseBowItem.getItemHeld(this, predicate2);
            return itemStack2.isEmpty() ? new ItemStack(Items.jg) : itemStack2;
        }
        return ItemStack.EMPTY;
    }
}
