package net.minecraft.entity.mob;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TraderOfferList;
import net.minecraft.entity.passive.VillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.village.VillagerType;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import net.minecraft.village.VillagerData;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.village.VillagerDataContainer;

public class ZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer
{
    private static final TrackedData<Boolean> CONVERTING;
    private static final TrackedData<VillagerData> VILLAGER_DATA;
    private int conversionTimer;
    private UUID converter;
    private CompoundTag offerData;
    private int xp;
    
    public ZombieVillagerEntity(final EntityType<? extends ZombieVillagerEntity> type, final World world) {
        super(type, world);
        this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(ZombieVillagerEntity.CONVERTING, false);
        this.dataTracker.<VillagerData>startTracking(ZombieVillagerEntity.VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.a, 1));
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.put("VillagerData", this.getVillagerData().<Tag>serialize((com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE));
        if (this.offerData != null) {
            tag.put("Offers", this.offerData);
        }
        tag.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
        if (this.converter != null) {
            tag.putUuid("ConversionPlayer", this.converter);
        }
        tag.putInt("Xp", this.xp);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic((DynamicOps)NbtOps.INSTANCE, tag.getTag("VillagerData"))));
        }
        if (tag.containsKey("Offers", 10)) {
            this.offerData = tag.getCompound("Offers");
        }
        if (tag.containsKey("ConversionTime", 99) && tag.getInt("ConversionTime") > -1) {
            this.setConverting(tag.hasUuid("ConversionPlayer") ? tag.getUuid("ConversionPlayer") : null, tag.getInt("ConversionTime"));
        }
        if (tag.containsKey("Xp", 3)) {
            this.xp = tag.getInt("Xp");
        }
        else {
            this.xp = VillagerData.getLowerLevelExperience(this.getVillagerData().getLevel());
        }
    }
    
    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive() && this.isConverting()) {
            final int integer1 = this.getConversionRate();
            this.conversionTimer -= integer1;
            if (this.conversionTimer <= 0) {
                this.finishConversion((ServerWorld)this.world);
            }
        }
        super.tick();
    }
    
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.kp && this.hasStatusEffect(StatusEffects.r)) {
            if (!player.abilities.creativeMode) {
                itemStack3.subtractAmount(1);
            }
            if (!this.world.isClient) {
                this.setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean canConvertInWater() {
        return false;
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return !this.isConverting();
    }
    
    public boolean isConverting() {
        return this.getDataTracker().<Boolean>get(ZombieVillagerEntity.CONVERTING);
    }
    
    protected void setConverting(@Nullable final UUID uUID, final int integer) {
        this.converter = uUID;
        this.conversionTimer = integer;
        this.getDataTracker().<Boolean>set(ZombieVillagerEntity.CONVERTING, true);
        this.removeStatusEffect(StatusEffects.r);
        this.addPotionEffect(new StatusEffectInstance(StatusEffects.e, integer, Math.min(this.world.getDifficulty().getId() - 1, 0)));
        this.world.sendEntityStatus(this, (byte)16);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 16) {
            if (!this.isSilent()) {
                this.world.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, SoundEvents.ol, this.getSoundCategory(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            return;
        }
        super.handleStatus(status);
    }
    
    protected void finishConversion(final ServerWorld serverWorld) {
        final VillagerEntity villagerEntity2 = EntityType.VILLAGER.create(serverWorld);
        villagerEntity2.setPositionAndAngles(this);
        villagerEntity2.setVillagerData(this.getVillagerData());
        if (this.offerData != null) {
            villagerEntity2.setRecipes(new TraderOfferList(this.offerData));
        }
        villagerEntity2.setExperience(this.xp);
        villagerEntity2.initialize(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(villagerEntity2)), SpawnType.i, null, null);
        if (this.isChild()) {
            villagerEntity2.setBreedingAge(-24000);
        }
        this.remove();
        villagerEntity2.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            villagerEntity2.setCustomName(this.getCustomName());
            villagerEntity2.setCustomNameVisible(this.isCustomNameVisible());
        }
        serverWorld.spawnEntity(villagerEntity2);
        if (this.converter != null) {
            final PlayerEntity playerEntity3 = serverWorld.getPlayerByUuid(this.converter);
            if (playerEntity3 instanceof ServerPlayerEntity) {
                Criterions.CURED_ZOMBIE_VILLAGER.handle((ServerPlayerEntity)playerEntity3, this, villagerEntity2);
                serverWorld.handleInteraction(EntityInteraction.a, playerEntity3, villagerEntity2);
            }
        }
        villagerEntity2.addPotionEffect(new StatusEffectInstance(StatusEffects.i, 200, 0));
        serverWorld.playLevelEvent(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
    }
    
    protected int getConversionRate() {
        int integer1 = 1;
        if (this.random.nextFloat() < 0.01f) {
            int integer2 = 0;
            final BlockPos.Mutable mutable3 = new BlockPos.Mutable();
            for (int integer3 = (int)this.x - 4; integer3 < (int)this.x + 4 && integer2 < 14; ++integer3) {
                for (int integer4 = (int)this.y - 4; integer4 < (int)this.y + 4 && integer2 < 14; ++integer4) {
                    for (int integer5 = (int)this.z - 4; integer5 < (int)this.z + 4 && integer2 < 14; ++integer5) {
                        final Block block7 = this.world.getBlockState(mutable3.set(integer3, integer4, integer5)).getBlock();
                        if (block7 == Blocks.dA || block7 instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3f) {
                                ++integer1;
                            }
                            ++integer2;
                        }
                    }
                }
            }
        }
        return integer1;
    }
    
    @Override
    protected float getSoundPitch() {
        if (this.isChild()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 2.0f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }
    
    public SoundEvent getAmbientSound() {
        return SoundEvents.oj;
    }
    
    public SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.on;
    }
    
    public SoundEvent getDeathSound() {
        return SoundEvents.om;
    }
    
    public SoundEvent getStepSound() {
        return SoundEvents.oo;
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
    
    public void setOfferData(final CompoundTag compoundTag) {
        this.offerData = compoundTag;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    public void setVillagerData(final VillagerData villagerData) {
        final VillagerData villagerData2 = this.getVillagerData();
        if (villagerData2.getProfession() != villagerData.getProfession()) {
            this.offerData = null;
        }
        this.dataTracker.<VillagerData>set(ZombieVillagerEntity.VILLAGER_DATA, villagerData);
    }
    
    @Override
    public VillagerData getVillagerData() {
        return this.dataTracker.<VillagerData>get(ZombieVillagerEntity.VILLAGER_DATA);
    }
    
    public void setXp(final int xp) {
        this.xp = xp;
    }
    
    static {
        CONVERTING = DataTracker.<Boolean>registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        VILLAGER_DATA = DataTracker.<VillagerData>registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    }
}
