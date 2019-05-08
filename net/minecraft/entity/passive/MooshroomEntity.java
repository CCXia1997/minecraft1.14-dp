package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.block.FlowerBlock;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ItemTags;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.LightningEntity;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.data.TrackedData;

public class MooshroomEntity extends CowEntity
{
    private static final TrackedData<String> TYPE;
    private StatusEffect stewEffect;
    private int stewEffectDuration;
    private UUID lightningId;
    
    public MooshroomEntity(final EntityType<? extends MooshroomEntity> type, final World world) {
        super(type, world);
        this.spawningGround = Blocks.dL;
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
        final UUID uUID2 = lightning.getUuid();
        if (!uUID2.equals(this.lightningId)) {
            this.setType((this.getMooshroomType() == Type.a) ? Type.b : Type.a);
            this.lightningId = uUID2;
            this.playSound(SoundEvents.gv, 2.0f, 1.0f);
        }
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<String>startTracking(MooshroomEntity.TYPE, Type.a.name);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.jA && this.getBreedingAge() >= 0 && !player.abilities.creativeMode) {
            itemStack3.subtractAmount(1);
            boolean boolean5 = false;
            ItemStack itemStack4;
            if (this.stewEffect != null) {
                boolean5 = true;
                itemStack4 = new ItemStack(Items.pz);
                SuspiciousStewItem.addEffectToStew(itemStack4, this.stewEffect, this.stewEffectDuration);
                this.stewEffect = null;
                this.stewEffectDuration = 0;
            }
            else {
                itemStack4 = new ItemStack(Items.jB);
            }
            if (itemStack3.isEmpty()) {
                player.setStackInHand(hand, itemStack4);
            }
            else if (!player.inventory.insertStack(itemStack4)) {
                player.dropItem(itemStack4, false);
            }
            SoundEvent soundEvent6;
            if (boolean5) {
                soundEvent6 = SoundEvents.gy;
            }
            else {
                soundEvent6 = SoundEvents.gx;
            }
            this.playSound(soundEvent6, 1.0f, 1.0f);
            return true;
        }
        if (itemStack3.getItem() == Items.lW && this.getBreedingAge() >= 0) {
            this.world.addParticle(ParticleTypes.w, this.x, this.y + this.getHeight() / 2.0f, this.z, 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.remove();
                final CowEntity cowEntity4 = EntityType.COW.create(this.world);
                cowEntity4.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
                cowEntity4.setHealth(this.getHealth());
                cowEntity4.aK = this.aK;
                if (this.hasCustomName()) {
                    cowEntity4.setCustomName(this.getCustomName());
                }
                this.world.spawnEntity(cowEntity4);
                for (int integer5 = 0; integer5 < 5; ++integer5) {
                    this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y + this.getHeight(), this.z, new ItemStack(this.getMooshroomType().mushroomState.getBlock())));
                }
                itemStack3.<PlayerEntity>applyDamage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                this.playSound(SoundEvents.gz, 1.0f, 1.0f);
            }
            return true;
        }
        if (this.getMooshroomType() == Type.b && itemStack3.getItem().matches(ItemTags.E)) {
            if (this.stewEffect != null) {
                for (int integer6 = 0; integer6 < 2; ++integer6) {
                    this.world.addParticle(ParticleTypes.Q, this.x + this.random.nextFloat() / 2.0f, this.y + this.getHeight() / 2.0f, this.z + this.random.nextFloat() / 2.0f, 0.0, this.random.nextFloat() / 5.0f, 0.0);
                }
            }
            else {
                final Pair<StatusEffect, Integer> pair4 = this.getStewEffectFrom(itemStack3);
                if (!player.abilities.creativeMode) {
                    itemStack3.subtractAmount(1);
                }
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.world.addParticle(ParticleTypes.p, this.x + this.random.nextFloat() / 2.0f, this.y + this.getHeight() / 2.0f, this.z + this.random.nextFloat() / 2.0f, 0.0, this.random.nextFloat() / 5.0f, 0.0);
                }
                this.stewEffect = (StatusEffect)pair4.getLeft();
                this.stewEffectDuration = (int)pair4.getRight();
                this.playSound(SoundEvents.gw, 2.0f, 1.0f);
            }
        }
        return super.interactMob(player, hand);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putString("Type", this.getMooshroomType().name);
        if (this.stewEffect != null) {
            tag.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
            tag.putInt("EffectDuration", this.stewEffectDuration);
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setType(fromName(tag.getString("Type")));
        if (tag.containsKey("EffectId", 1)) {
            this.stewEffect = StatusEffect.byRawId(tag.getByte("EffectId"));
        }
        if (tag.containsKey("EffectDuration", 3)) {
            this.stewEffectDuration = tag.getInt("EffectDuration");
        }
    }
    
    private Pair<StatusEffect, Integer> getStewEffectFrom(final ItemStack itemStack) {
        final FlowerBlock flowerBlock2 = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
        return (Pair<StatusEffect, Integer>)Pair.of(flowerBlock2.getEffectInStew(), flowerBlock2.getEffectInStewDuration());
    }
    
    private void setType(final Type type) {
        this.dataTracker.<String>set(MooshroomEntity.TYPE, type.name);
    }
    
    public Type getMooshroomType() {
        return fromName(this.dataTracker.<String>get(MooshroomEntity.TYPE));
    }
    
    @Override
    public MooshroomEntity createChild(final PassiveEntity mate) {
        final MooshroomEntity mooshroomEntity2 = EntityType.MOOSHROOM.create(this.world);
        mooshroomEntity2.setType(this.chooseBabyType((MooshroomEntity)mate));
        return mooshroomEntity2;
    }
    
    private Type chooseBabyType(final MooshroomEntity mooshroomEntity) {
        final Type type2 = this.getMooshroomType();
        final Type type3 = mooshroomEntity.getMooshroomType();
        Type type4;
        if (type2 == type3 && this.random.nextInt(1024) == 0) {
            type4 = ((type2 == Type.b) ? Type.a : Type.b);
        }
        else {
            type4 = (this.random.nextBoolean() ? type2 : type3);
        }
        return type4;
    }
    
    static {
        TYPE = DataTracker.<String>registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
    }
    
    public enum Type
    {
        a("red", Blocks.bC.getDefaultState()), 
        b("brown", Blocks.bB.getDefaultState());
        
        private final String name;
        private final BlockState mushroomState;
        
        private Type(final String string1, final BlockState blockState) {
            this.name = string1;
            this.mushroomState = blockState;
        }
        
        @Environment(EnvType.CLIENT)
        public BlockState getMushroomState() {
            return this.mushroomState;
        }
        
        private static Type fromName(final String string) {
            for (final Type type5 : values()) {
                if (type5.name.equals(string)) {
                    return type5;
                }
            }
            return Type.a;
        }
    }
}
