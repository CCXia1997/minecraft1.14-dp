package net.minecraft.entity.projectile;

import java.util.AbstractList;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.registry.Registry;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.potion.PotionUtil;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import com.google.common.collect.Sets;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.Set;
import net.minecraft.potion.Potion;
import net.minecraft.entity.data.TrackedData;

public class ArrowEntity extends ProjectileEntity
{
    private static final TrackedData<Integer> COLOR;
    private Potion potion;
    private final Set<StatusEffectInstance> effects;
    private boolean colorSet;
    
    public ArrowEntity(final EntityType<? extends ArrowEntity> type, final World world) {
        super(type, world);
        this.potion = Potions.a;
        this.effects = Sets.newHashSet();
    }
    
    public ArrowEntity(final World world, final double x, final double double4, final double double6) {
        super(EntityType.ARROW, x, double4, double6, world);
        this.potion = Potions.a;
        this.effects = Sets.newHashSet();
    }
    
    public ArrowEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.ARROW, livingEntity, world);
        this.potion = Potions.a;
        this.effects = Sets.newHashSet();
    }
    
    public void initFromStack(final ItemStack itemStack) {
        if (itemStack.getItem() == Items.oU) {
            this.potion = PotionUtil.getPotion(itemStack);
            final Collection<StatusEffectInstance> collection2 = PotionUtil.getCustomPotionEffects(itemStack);
            if (!collection2.isEmpty()) {
                for (final StatusEffectInstance statusEffectInstance4 : collection2) {
                    this.effects.add(new StatusEffectInstance(statusEffectInstance4));
                }
            }
            final int integer3 = c(itemStack);
            if (integer3 == -1) {
                this.initColor();
            }
            else {
                this.setColor(integer3);
            }
        }
        else if (itemStack.getItem() == Items.jg) {
            this.potion = Potions.a;
            this.effects.clear();
            this.dataTracker.<Integer>set(ArrowEntity.COLOR, -1);
        }
    }
    
    public static int c(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getTag();
        if (compoundTag2 != null && compoundTag2.containsKey("CustomPotionColor", 99)) {
            return compoundTag2.getInt("CustomPotionColor");
        }
        return -1;
    }
    
    private void initColor() {
        this.colorSet = false;
        this.dataTracker.<Integer>set(ArrowEntity.COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }
    
    public void addEffect(final StatusEffectInstance statusEffectInstance) {
        this.effects.add(statusEffectInstance);
        this.getDataTracker().<Integer>set(ArrowEntity.COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(ArrowEntity.COLOR, -1);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            }
            else {
                this.spawnParticles(2);
            }
        }
        else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.world.sendEntityStatus(this, (byte)0);
            this.potion = Potions.a;
            this.effects.clear();
            this.dataTracker.<Integer>set(ArrowEntity.COLOR, -1);
        }
    }
    
    private void spawnParticles(final int integer) {
        final int integer2 = this.getColor();
        if (integer2 == -1 || integer <= 0) {
            return;
        }
        final double double3 = (integer2 >> 16 & 0xFF) / 255.0;
        final double double4 = (integer2 >> 8 & 0xFF) / 255.0;
        final double double5 = (integer2 >> 0 & 0xFF) / 255.0;
        for (int integer3 = 0; integer3 < integer; ++integer3) {
            this.world.addParticle(ParticleTypes.u, this.x + (this.random.nextDouble() - 0.5) * this.getWidth(), this.y + this.random.nextDouble() * this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getWidth(), double3, double4, double5);
        }
    }
    
    public int getColor() {
        return this.dataTracker.<Integer>get(ArrowEntity.COLOR);
    }
    
    private void setColor(final int integer) {
        this.colorSet = true;
        this.dataTracker.<Integer>set(ArrowEntity.COLOR, integer);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.potion != Potions.a && this.potion != null) {
            tag.putString("Potion", Registry.POTION.getId(this.potion).toString());
        }
        if (this.colorSet) {
            tag.putInt("Color", this.getColor());
        }
        if (!this.effects.isEmpty()) {
            final ListTag listTag2 = new ListTag();
            for (final StatusEffectInstance statusEffectInstance4 : this.effects) {
                ((AbstractList<CompoundTag>)listTag2).add(statusEffectInstance4.serialize(new CompoundTag()));
            }
            tag.put("CustomPotionEffects", listTag2);
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("Potion", 8)) {
            this.potion = PotionUtil.getPotion(tag);
        }
        for (final StatusEffectInstance statusEffectInstance3 : PotionUtil.getCustomPotionEffects(tag)) {
            this.addEffect(statusEffectInstance3);
        }
        if (tag.containsKey("Color", 99)) {
            this.setColor(tag.getInt("Color"));
        }
        else {
            this.initColor();
        }
    }
    
    @Override
    protected void onHit(final LivingEntity livingEntity) {
        super.onHit(livingEntity);
        for (final StatusEffectInstance statusEffectInstance3 : this.potion.getEffects()) {
            livingEntity.addPotionEffect(new StatusEffectInstance(statusEffectInstance3.getEffectType(), Math.max(statusEffectInstance3.getDuration() / 8, 1), statusEffectInstance3.getAmplifier(), statusEffectInstance3.isAmbient(), statusEffectInstance3.shouldShowParticles()));
        }
        if (!this.effects.isEmpty()) {
            for (final StatusEffectInstance statusEffectInstance3 : this.effects) {
                livingEntity.addPotionEffect(statusEffectInstance3);
            }
        }
    }
    
    @Override
    protected ItemStack asItemStack() {
        if (this.effects.isEmpty() && this.potion == Potions.a) {
            return new ItemStack(Items.jg);
        }
        final ItemStack itemStack1 = new ItemStack(Items.oU);
        PotionUtil.setPotion(itemStack1, this.potion);
        PotionUtil.setCustomPotionEffects(itemStack1, this.effects);
        if (this.colorSet) {
            itemStack1.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
        }
        return itemStack1;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 0) {
            final int integer2 = this.getColor();
            if (integer2 != -1) {
                final double double3 = (integer2 >> 16 & 0xFF) / 255.0;
                final double double4 = (integer2 >> 8 & 0xFF) / 255.0;
                final double double5 = (integer2 >> 0 & 0xFF) / 255.0;
                for (int integer3 = 0; integer3 < 20; ++integer3) {
                    this.world.addParticle(ParticleTypes.u, this.x + (this.random.nextDouble() - 0.5) * this.getWidth(), this.y + this.random.nextDouble() * this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getWidth(), double3, double4, double5);
                }
            }
        }
        else {
            super.handleStatus(status);
        }
    }
    
    static {
        COLOR = DataTracker.<Integer>registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
