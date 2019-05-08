package net.minecraft.entity.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.item.Items;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.entity.attribute.EntityAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import java.util.UUID;

public class HorseEntity extends HorseBaseEntity
{
    private static final UUID bJ;
    private static final TrackedData<Integer> VARIANT;
    private static final String[] HORSE_TEX;
    private static final String[] HORSE_TEX_ID;
    private static final String[] HORSE_MARKING_TEX;
    private static final String[] HORSE_MARKING_TEX_ID;
    private String textureLocation;
    private final String[] textureLayers;
    
    public HorseEntity(final EntityType<? extends HorseEntity> type, final World world) {
        super(type, world);
        this.textureLayers = new String[2];
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(HorseEntity.VARIANT, 0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
        if (!this.items.getInvStack(1).isEmpty()) {
            tag.put("ArmorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
        }
    }
    
    public ItemStack getArmorType() {
        return this.getEquippedStack(EquipmentSlot.CHEST);
    }
    
    private void k(final ItemStack itemStack) {
        this.setEquippedStack(EquipmentSlot.CHEST, itemStack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.containsKey("ArmorItem", 10)) {
            final ItemStack itemStack2 = ItemStack.fromTag(tag.getCompound("ArmorItem"));
            if (!itemStack2.isEmpty() && this.canEquip(itemStack2)) {
                this.items.setInvStack(1, itemStack2);
            }
        }
        this.updateSaddle();
    }
    
    public void setVariant(final int integer) {
        this.dataTracker.<Integer>set(HorseEntity.VARIANT, integer);
        this.clearTextureInfo();
    }
    
    public int getVariant() {
        return this.dataTracker.<Integer>get(HorseEntity.VARIANT);
    }
    
    private void clearTextureInfo() {
        this.textureLocation = null;
    }
    
    @Environment(EnvType.CLIENT)
    private void initTextureInfo() {
        final int integer1 = this.getVariant();
        final int integer2 = (integer1 & 0xFF) % 7;
        final int integer3 = ((integer1 & 0xFF00) >> 8) % 5;
        this.textureLayers[0] = HorseEntity.HORSE_TEX[integer2];
        this.textureLayers[1] = HorseEntity.HORSE_MARKING_TEX[integer3];
        this.textureLocation = "horse/" + HorseEntity.HORSE_TEX_ID[integer2] + HorseEntity.HORSE_MARKING_TEX_ID[integer3];
    }
    
    @Environment(EnvType.CLIENT)
    public String getTextureLocation() {
        if (this.textureLocation == null) {
            this.initTextureInfo();
        }
        return this.textureLocation;
    }
    
    @Environment(EnvType.CLIENT)
    public String[] getTextureLayers() {
        if (this.textureLocation == null) {
            this.initTextureInfo();
        }
        return this.textureLayers;
    }
    
    @Override
    protected void updateSaddle() {
        super.updateSaddle();
        this.setArmorTypeFromStack(this.items.getInvStack(1));
    }
    
    private void setArmorTypeFromStack(final ItemStack itemStack) {
        this.k(itemStack);
        if (!this.world.isClient) {
            this.getAttributeInstance(EntityAttributes.ARMOR).removeModifier(HorseEntity.bJ);
            if (this.canEquip(itemStack)) {
                final int integer2 = ((HorseArmorItem)itemStack.getItem()).getBonus();
                if (integer2 != 0) {
                    this.getAttributeInstance(EntityAttributes.ARMOR).addModifier(new EntityAttributeModifier(HorseEntity.bJ, "Horse armor bonus", integer2, EntityAttributeModifier.Operation.a).setSerialize(false));
                }
            }
        }
    }
    
    @Override
    public void onInvChange(final Inventory inventory) {
        final ItemStack itemStack2 = this.getArmorType();
        super.onInvChange(inventory);
        final ItemStack itemStack3 = this.getArmorType();
        if (this.age > 20 && this.canEquip(itemStack3) && itemStack2 != itemStack3) {
            this.playSound(SoundEvents.eG, 0.5f, 1.0f);
        }
    }
    
    @Override
    protected void playWalkSound(final BlockSoundGroup group) {
        super.playWalkSound(group);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.eH, group.getVolume() * 0.6f, group.getPitch());
        }
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(this.getChildHealthBonus());
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(this.getChildMovementSpeedBonus());
        this.getAttributeInstance(HorseEntity.JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient && this.dataTracker.isDirty()) {
            this.dataTracker.clearDirty();
            this.clearTextureInfo();
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.eE;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.eI;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.eL;
    }
    
    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.eF;
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        final boolean boolean4 = !itemStack3.isEmpty();
        if (boolean4 && itemStack3.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (!this.isChild()) {
            if (this.isTame() && player.isSneaking()) {
                this.openInventory(player);
                return true;
            }
            if (this.hasPassengers()) {
                return super.interactMob(player, hand);
            }
        }
        if (boolean4) {
            if (this.receiveFood(player, itemStack3)) {
                if (!player.abilities.creativeMode) {
                    itemStack3.subtractAmount(1);
                }
                return true;
            }
            if (itemStack3.interactWithEntity(player, this, hand)) {
                return true;
            }
            if (!this.isTame()) {
                this.playAngrySound();
                return true;
            }
            final boolean boolean5 = !this.isChild() && !this.isSaddled() && itemStack3.getItem() == Items.kB;
            if (this.canEquip(itemStack3) || boolean5) {
                this.openInventory(player);
                return true;
            }
        }
        if (this.isChild()) {
            return super.interactMob(player, hand);
        }
        this.putPlayerOnBack(player);
        return true;
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        return other != this && (other instanceof DonkeyEntity || other instanceof HorseEntity) && this.canBreed() && ((HorseBaseEntity)other).canBreed();
    }
    
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        HorseBaseEntity horseBaseEntity2;
        if (mate instanceof DonkeyEntity) {
            horseBaseEntity2 = EntityType.MULE.create(this.world);
        }
        else {
            final HorseEntity horseEntity3 = (HorseEntity)mate;
            horseBaseEntity2 = EntityType.HORSE.create(this.world);
            final int integer5 = this.random.nextInt(9);
            int integer6;
            if (integer5 < 4) {
                integer6 = (this.getVariant() & 0xFF);
            }
            else if (integer5 < 8) {
                integer6 = (horseEntity3.getVariant() & 0xFF);
            }
            else {
                integer6 = this.random.nextInt(7);
            }
            final int integer7 = this.random.nextInt(5);
            if (integer7 < 2) {
                integer6 |= (this.getVariant() & 0xFF00);
            }
            else if (integer7 < 4) {
                integer6 |= (horseEntity3.getVariant() & 0xFF00);
            }
            else {
                integer6 |= (this.random.nextInt(5) << 8 & 0xFF00);
            }
            ((HorseEntity)horseBaseEntity2).setVariant(integer6);
        }
        this.setChildAttributes(mate, horseBaseEntity2);
        return horseBaseEntity2;
    }
    
    @Override
    public boolean canEquip() {
        return true;
    }
    
    @Override
    public boolean canEquip(final ItemStack item) {
        return item.getItem() instanceof HorseArmorItem;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        int integer6;
        if (entityData instanceof a) {
            integer6 = ((a)entityData).a;
        }
        else {
            integer6 = this.random.nextInt(7);
            entityData = new a(integer6);
        }
        this.setVariant(integer6 | this.random.nextInt(5) << 8);
        return entityData;
    }
    
    static {
        bJ = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
        VARIANT = DataTracker.<Integer>registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HORSE_TEX = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png" };
        HORSE_TEX_ID = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb" };
        HORSE_MARKING_TEX = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png" };
        HORSE_MARKING_TEX_ID = new String[] { "", "wo_", "wmo", "wdo", "bdo" };
    }
    
    public static class a implements EntityData
    {
        public final int a;
        
        public a(final int integer) {
            this.a = integer;
        }
    }
}
