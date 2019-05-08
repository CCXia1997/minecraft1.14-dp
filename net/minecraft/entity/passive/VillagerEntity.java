package net.minecraft.entity.passive;

import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.text.Style;
import java.util.function.Supplier;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BoundingBox;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.List;
import net.minecraft.village.TradeOffers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.registry.Registry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.TextComponent;
import java.util.Optional;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.ListTag;
import net.minecraft.village.TraderOfferList;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillageGossipType;
import java.util.Iterator;
import net.minecraft.village.TradeOffer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.entity.raid.Raid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import java.util.Collection;
import net.minecraft.entity.ai.brain.Brain;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.village.VillagerProfession;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.village.PointOfInterestType;
import java.util.function.BiPredicate;
import net.minecraft.util.GlobalPos;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.google.common.collect.ImmutableList;
import net.minecraft.village.VillagerGossips;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Set;
import net.minecraft.item.Item;
import java.util.Map;
import net.minecraft.village.VillagerData;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.entity.InteractionObserver;

public class VillagerEntity extends AbstractTraderEntity implements InteractionObserver, VillagerDataContainer
{
    private static final TrackedData<VillagerData> VILLAGER_DATA;
    public static final Map<Item, Integer> ITEM_FOOD_VALUES;
    private static final Set<Item> GATHERABLE_ITEMS;
    private int levelUpTimer;
    private boolean levellingUp;
    @Nullable
    private PlayerEntity lastCustomer;
    @Nullable
    private UUID buddyGolemId;
    private long oneMinAfterLastGolemCheckTimestamp;
    private byte foodLevel;
    private final VillagerGossips gossip;
    private long gossipStartTime;
    private int experience;
    private long lastRestock;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;
    private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS;
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST;
    
    public VillagerEntity(final EntityType<? extends VillagerEntity> type, final World world) {
        this(type, world, VillagerType.PLAINS);
    }
    
    public VillagerEntity(final EntityType<? extends VillagerEntity> entityType, final World world, final VillagerType villagerType) {
        super(entityType, world);
        this.oneMinAfterLastGolemCheckTimestamp = Long.MIN_VALUE;
        this.gossip = new VillagerGossips();
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.a));
        this.brain = this.deserializeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, new CompoundTag()));
    }
    
    @Override
    public Brain<VillagerEntity> getBrain() {
        return (Brain<VillagerEntity>)super.getBrain();
    }
    
    @Override
    protected Brain<?> deserializeBrain(final Dynamic<?> dynamic) {
        final Brain<VillagerEntity> brain2 = new Brain<VillagerEntity>(VillagerEntity.MEMORY_MODULES, VillagerEntity.SENSORS, dynamic);
        this.initBrain(brain2);
        return brain2;
    }
    
    public void reinitializeBrain(final ServerWorld serverWorld) {
        final Brain<VillagerEntity> brain2 = this.getBrain();
        brain2.stopAllTasks(serverWorld, this);
        this.brain = brain2.copy();
        this.initBrain(this.getBrain());
    }
    
    private void initBrain(final Brain<VillagerEntity> brain) {
        final VillagerProfession villagerProfession2 = this.getVillagerData().getProfession();
        final float float3 = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
        if (this.isChild()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.setTaskList(Activity.d, VillagerTaskListProvider.createPlayTasks(float3));
        }
        else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.setTaskList(Activity.c, VillagerTaskListProvider.createWorkTasks(villagerProfession2, float3), ImmutableSet.of(Pair.of(MemoryModuleType.c, MemoryModuleState.a)));
        }
        brain.setTaskList(Activity.a, VillagerTaskListProvider.createCoreTasks(villagerProfession2, float3));
        brain.setTaskList(Activity.f, VillagerTaskListProvider.createMeetTasks(villagerProfession2, float3), ImmutableSet.of(Pair.of(MemoryModuleType.d, MemoryModuleState.a)));
        brain.setTaskList(Activity.e, VillagerTaskListProvider.createRestTasks(villagerProfession2, float3));
        brain.setTaskList(Activity.b, VillagerTaskListProvider.createIdleTasks(villagerProfession2, float3));
        brain.setTaskList(Activity.g, VillagerTaskListProvider.createPanicTasks(villagerProfession2, float3));
        brain.setTaskList(Activity.i, VillagerTaskListProvider.createPreRaidTasks(villagerProfession2, float3));
        brain.setTaskList(Activity.h, VillagerTaskListProvider.createRaidTasks(villagerProfession2, float3));
        brain.setTaskList(Activity.j, VillagerTaskListProvider.createHideTasks(villagerProfession2, float3));
        brain.setCoreActivities(ImmutableSet.<Activity>of(Activity.a));
        brain.setDefaultActivity(Activity.b);
        brain.resetPossibleActivities(Activity.b);
        brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
    }
    
    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.world);
        }
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }
    
    @Override
    protected void mobTick() {
        this.world.getProfiler().push("brain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levellingUp) {
                    this.levelUp();
                    this.levellingUp = false;
                }
                this.addPotionEffect(new StatusEffectInstance(StatusEffects.j, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.e, this.lastCustomer, this);
            this.world.sendEntityStatus(this, (byte)14);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
            final Raid raid1 = ((ServerWorld)this.world).getRaidAt(new BlockPos(this));
            if (raid1 != null && raid1.isActive() && !raid1.isFinished()) {
                this.world.sendEntityStatus(this, (byte)42);
            }
        }
        super.mobTick();
    }
    
    public void resetCustomer() {
        this.setCurrentCustomer(null);
        this.clearCurrentBonus();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.getHeadRollingTimeLeft() > 0) {
            this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
        }
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        final boolean boolean4 = itemStack3.getItem() == Items.or;
        if (boolean4) {
            itemStack3.interactWithEntity(player, this, hand);
            return true;
        }
        if (itemStack3.getItem() == Items.nr || !this.isAlive() || this.hasCustomer() || this.isSleeping()) {
            return super.interactMob(player, hand);
        }
        if (this.isChild()) {
            this.sayNo();
            return super.interactMob(player, hand);
        }
        final boolean boolean5 = this.getOffers().isEmpty();
        if (hand == Hand.a) {
            if (boolean5) {
                this.sayNo();
            }
            player.incrementStat(Stats.Q);
        }
        if (boolean5) {
            return super.interactMob(player, hand);
        }
        if (!this.world.isClient && !this.offers.isEmpty()) {
            this.beginTradeWith(player);
        }
        return true;
    }
    
    private void sayNo() {
        this.setHeadRollingTimeLeft(40);
        if (!this.world.isClient()) {
            this.playSound(SoundEvents.my, this.getSoundVolume(), this.getSoundPitch());
        }
    }
    
    private void beginTradeWith(final PlayerEntity customer) {
        this.prepareRecipesFor(customer);
        this.setCurrentCustomer(customer);
        this.sendOffers(customer, this.getDisplayName(), this.getVillagerData().getLevel());
    }
    
    public void restock() {
        for (final TradeOffer tradeOffer2 : this.getOffers()) {
            tradeOffer2.updatePriceOnDemand();
            tradeOffer2.resetUses();
        }
        this.lastRestock = this.world.getTimeOfDay() % 24000L;
    }
    
    private void prepareRecipesFor(final PlayerEntity player) {
        final int integer2 = this.gossip.getReputationFor(player.getUuid(), villageGossipType -> villageGossipType != VillageGossipType.f);
        if (integer2 != 0) {
            for (final TradeOffer tradeOffer4 : this.getOffers()) {
                tradeOffer4.increaseSpecialPrice(-MathHelper.floor(integer2 * tradeOffer4.getPriceMultiplier()));
            }
        }
        if (player.hasStatusEffect(StatusEffects.F)) {
            final StatusEffectInstance statusEffectInstance3 = player.getStatusEffect(StatusEffects.F);
            final int integer3 = statusEffectInstance3.getAmplifier();
            for (final TradeOffer tradeOffer5 : this.getOffers()) {
                final double double7 = 0.3 + 0.0625 * integer3;
                final int integer4 = (int)Math.floor(double7 * tradeOffer5.getOriginalFirstBuyItem().getAmount());
                tradeOffer5.increaseSpecialPrice(-Math.max(integer4, 1));
            }
        }
    }
    
    private void clearCurrentBonus() {
        for (final TradeOffer tradeOffer2 : this.getOffers()) {
            tradeOffer2.clearSpecialPrice();
        }
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<VillagerData>startTracking(VillagerEntity.VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.a, 1));
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.put("VillagerData", this.getVillagerData().<Tag>serialize((com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE));
        tag.putByte("FoodLevel", this.foodLevel);
        tag.put("Gossips", (Tag)this.gossip.serialize((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
        tag.putInt("Xp", this.experience);
        tag.putLong("LastRestock", this.lastRestock);
        if (this.buddyGolemId != null) {
            tag.putUuid("BuddyGolem", this.buddyGolemId);
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic((DynamicOps)NbtOps.INSTANCE, tag.getTag("VillagerData"))));
        }
        if (tag.containsKey("Offers", 10)) {
            this.offers = new TraderOfferList(tag.getCompound("Offers"));
        }
        if (tag.containsKey("FoodLevel", 1)) {
            this.foodLevel = tag.getByte("FoodLevel");
        }
        final ListTag listTag2 = tag.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, listTag2));
        if (tag.containsKey("Xp", 3)) {
            this.experience = tag.getInt("Xp");
        }
        else {
            final int integer3 = this.getVillagerData().getLevel();
            if (VillagerData.canLevelUp(integer3)) {
                this.experience = VillagerData.getLowerLevelExperience(integer3);
            }
        }
        this.lastRestock = tag.getLong("LastRestock");
        if (tag.hasUuid("BuddyGolem")) {
            this.buddyGolemId = tag.getUuid("BuddyGolem");
        }
        this.setCanPickUpLoot(true);
        this.reinitializeBrain((ServerWorld)this.world);
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return false;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.hasCustomer()) {
            return SoundEvents.mz;
        }
        return SoundEvents.mu;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.mx;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.mw;
    }
    
    public void playWorkSound() {
        final SoundEvent soundEvent1 = this.getVillagerData().getProfession().getWorkStation().getSound();
        if (soundEvent1 != null) {
            this.playSound(soundEvent1, this.getSoundVolume(), this.getSoundPitch());
        }
    }
    
    public void setVillagerData(final VillagerData villagerData) {
        final VillagerData villagerData2 = this.getVillagerData();
        if (villagerData2.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }
        this.dataTracker.<VillagerData>set(VillagerEntity.VILLAGER_DATA, villagerData);
    }
    
    @Override
    public VillagerData getVillagerData() {
        return this.dataTracker.<VillagerData>get(VillagerEntity.VILLAGER_DATA);
    }
    
    @Override
    protected void afterUsing(final TradeOffer tradeOffer) {
        int integer2 = 3 + this.random.nextInt(4);
        this.experience += tradeOffer.getTraderExperience();
        this.lastCustomer = this.getCurrentCustomer();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levellingUp = true;
            integer2 += 5;
        }
        if (tradeOffer.shouldRewardPlayerExperience()) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, integer2));
        }
    }
    
    @Override
    public void setAttacker(@Nullable final LivingEntity livingEntity) {
        if (livingEntity != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.c, livingEntity, this);
            if (this.isAlive() && livingEntity instanceof PlayerEntity) {
                this.world.sendEntityStatus(this, (byte)13);
            }
        }
        super.setAttacker(livingEntity);
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        this.releaseTicketFor(MemoryModuleType.b);
        this.releaseTicketFor(MemoryModuleType.c);
        this.releaseTicketFor(MemoryModuleType.d);
        super.onDeath(damageSource);
    }
    
    public void releaseTicketFor(final MemoryModuleType<GlobalPos> memoryModuleType) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        final MinecraftServer minecraftServer2 = ((ServerWorld)this.world).getServer();
        final ServerWorld serverWorld4;
        final PointOfInterestStorage pointOfInterestStorage5;
        final Optional<PointOfInterestType> optional6;
        final BiPredicate<VillagerEntity, PointOfInterestType> biPredicate7;
        this.brain.<GlobalPos>getOptionalMemory(memoryModuleType).ifPresent(globalPos -> {
            serverWorld4 = minecraftServer2.getWorld(globalPos.getDimension());
            pointOfInterestStorage5 = serverWorld4.getPointOfInterestStorage();
            optional6 = pointOfInterestStorage5.getType(globalPos.getPos());
            biPredicate7 = VillagerEntity.POINTS_OF_INTEREST.get(memoryModuleType);
            if (optional6.isPresent() && biPredicate7.test(this, optional6.get())) {
                pointOfInterestStorage5.releaseTicket(globalPos.getPos());
                DebugRendererInfoManager.sendPointOfInterest(serverWorld4, globalPos.getPos());
            }
        });
    }
    
    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
    }
    
    public void consumeAvailableFood() {
        if (this.foodLevel >= 12 || this.getAvailableFood() == 0) {
            return;
        }
        for (int integer1 = 0; integer1 < this.getInventory().getInvSize(); ++integer1) {
            final ItemStack itemStack2 = this.getInventory().getInvStack(integer1);
            if (!itemStack2.isEmpty()) {
                final Integer integer2 = VillagerEntity.ITEM_FOOD_VALUES.get(itemStack2.getItem());
                if (integer2 != null) {
                    int integer4;
                    for (int integer3 = integer4 = itemStack2.getAmount(); integer4 > 0; --integer4) {
                        this.foodLevel += (byte)integer2;
                        this.getInventory().takeInvStack(integer1, 1);
                        if (this.foodLevel >= 12) {
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public void depleteFood(final int amount) {
        this.foodLevel -= (byte)amount;
    }
    
    public void setRecipes(final TraderOfferList traderOfferList) {
        this.offers = traderOfferList;
    }
    
    private boolean canLevelUp() {
        final int integer1 = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(integer1) && this.experience >= VillagerData.getUpperLevelExperience(integer1);
    }
    
    private void levelUp() {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.fillRecipes();
    }
    
    @Override
    public TextComponent getDisplayName() {
        final AbstractTeam abstractTeam1 = this.getScoreboardTeam();
        final TextComponent textComponent2 = this.getCustomName();
        if (textComponent2 != null) {
            return Team.modifyText(abstractTeam1, textComponent2).modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
        }
        final VillagerProfession villagerProfession3 = this.getVillagerData().getProfession();
        final TextComponent textComponent3 = new TranslatableTextComponent(this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(villagerProfession3).getPath(), new Object[0]).modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
        if (abstractTeam1 != null) {
            textComponent3.applyFormat(abstractTeam1.getColor());
        }
        return textComponent3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 12) {
            this.produceParticles(ParticleTypes.E);
        }
        else if (status == 13) {
            this.produceParticles(ParticleTypes.b);
        }
        else if (status == 14) {
            this.produceParticles(ParticleTypes.C);
        }
        else if (status == 42) {
            this.produceParticles(ParticleTypes.X);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        if (difficulty == SpawnType.e) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.a));
        }
        if (difficulty == SpawnType.n || difficulty == SpawnType.m || difficulty == SpawnType.c) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
        }
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    public VillagerEntity createChild(final PassiveEntity mate) {
        final double double3 = this.random.nextDouble();
        VillagerType villagerType2;
        if (double3 < 0.5) {
            villagerType2 = VillagerType.forBiome(this.world.getBiome(new BlockPos(this)));
        }
        else if (double3 < 0.75) {
            villagerType2 = this.getVillagerData().getType();
        }
        else {
            villagerType2 = ((VillagerEntity)mate).getVillagerData().getType();
        }
        final VillagerEntity villagerEntity5 = new VillagerEntity(EntityType.VILLAGER, this.world, villagerType2);
        villagerEntity5.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity5)), SpawnType.e, null, null);
        return villagerEntity5;
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
        final WitchEntity witchEntity2 = EntityType.WITCH.create(this.world);
        witchEntity2.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        witchEntity2.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(witchEntity2)), SpawnType.i, null, null);
        witchEntity2.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            witchEntity2.setCustomName(this.getCustomName());
            witchEntity2.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.world.spawnEntity(witchEntity2);
        this.remove();
    }
    
    @Override
    protected void loot(final ItemEntity item) {
        final ItemStack itemStack2 = item.getStack();
        final Item item2 = itemStack2.getItem();
        final VillagerProfession villagerProfession4 = this.getVillagerData().getProfession();
        if (VillagerEntity.GATHERABLE_ITEMS.contains(item2) || villagerProfession4.getGatherableItems().contains(item2)) {
            if (villagerProfession4 == VillagerProfession.f && item2 == Items.jP) {
                final int integer5 = itemStack2.getAmount() / 3;
                if (integer5 > 0) {
                    final ItemStack itemStack3 = this.getInventory().add(new ItemStack(Items.jQ, integer5));
                    itemStack2.subtractAmount(integer5 * 3);
                    if (!itemStack3.isEmpty()) {
                        this.dropStack(itemStack3, 0.5f);
                    }
                }
            }
            final ItemStack itemStack4 = this.getInventory().add(itemStack2);
            if (itemStack4.isEmpty()) {
                item.remove();
            }
            else {
                itemStack2.setAmount(itemStack4.getAmount());
            }
        }
    }
    
    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }
    
    public boolean canBreed() {
        final boolean boolean1 = this.getVillagerData().getProfession() == VillagerProfession.f;
        final int integer2 = this.getAvailableFood();
        return boolean1 ? (integer2 < 60) : (integer2 < 12);
    }
    
    private int getAvailableFood() {
        final BasicInventory basicInventory1 = this.getInventory();
        return VillagerEntity.ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> basicInventory1.getInvAmountOf(entry.getKey()) * (int)entry.getValue()).sum();
    }
    
    public boolean hasSeedToPlant() {
        final BasicInventory basicInventory1 = this.getInventory();
        return basicInventory1.containsAnyInInv(ImmutableSet.<Item>of(Items.jO, Items.nJ, Items.nI, Items.oP));
    }
    
    @Override
    protected void fillRecipes() {
        final VillagerData villagerData1 = this.getVillagerData();
        final Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap2 = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData1.getProfession());
        if (int2ObjectMap2 == null || int2ObjectMap2.isEmpty()) {
            return;
        }
        final TradeOffers.Factory[] arr3 = (TradeOffers.Factory[])int2ObjectMap2.get(villagerData1.getLevel());
        if (arr3 == null) {
            return;
        }
        final TraderOfferList traderOfferList4 = this.getOffers();
        this.fillRecipesFromPool(traderOfferList4, arr3, 2);
    }
    
    //与村民交谈，villager应该是要与之交谈的村民
    public void talkWithVillager(final VillagerEntity villager, final long time) {
    	//时间在上一次交谈时间的1分钟以内直接结束，说明交谈的时间间隔应该要大于等于1分钟，不过双方只要有1个满足就可以了
        if ((time >= this.gossipStartTime && time < this.gossipStartTime + 1200L) || (time >= villager.gossipStartTime && time < villager.gossipStartTime + 1200L)) {
            return;
        }
        //满足wantsGolem或者isLackingBuddyGolem的条件
        final boolean boolean4 = this.isLackingBuddyGolem(time);
        if (this.wantsGolem(this) || boolean4) {
        	//这里可以看出是向自己传播一条value为100的铁傀儡流言，golem的maxReputation为100
            this.gossip.startGossip(this.getUuid(), VillageGossipType.f, VillageGossipType.f.maxReputation);
        }
        //从与之交谈的村民的流言列表中中随机获取10条流言，并且重置两方的交谈时间为当前时间
        this.gossip.shareGossipFrom(villager.gossip, this.random, 10);
        this.gossipStartTime = time;
        villager.gossipStartTime = time;
        //如果缺少同伴铁傀儡就尝试生成铁傀儡，说明铁傀儡的生成只能在这个方法的指定条件下生成
        if (boolean4) {
            this.trySpawnGolem();
        }
    }
    
    //尝试生成铁傀儡
    private void trySpawnGolem() {
        final VillagerData villagerData1 = this.getVillagerData();
        //遇到职业为a（无业）和l（傻子）的话就退出生成
        if (villagerData1.getProfession() == VillagerProfession.a || villagerData1.getProfession() == VillagerProfession.l) {
            return;
        }
        final Optional<GolemSpawnCondition> optional2 = this.getBrain().<GolemSpawnCondition>getOptionalMemory(MemoryModuleType.u);
        if (!optional2.isPresent()) {
            return;
        }
        if (!optional2.get().canSpawn(this.world.getTime())) {
            return;
        }
        //判断铁傀儡流言强度大于30的流言数量是否至少为5个？
        final boolean boolean3 = this.gossip.getGossipCount(VillageGossipType.f, double1 -> double1 > 30.0) >= 5L;
        if (!boolean3) {
            return;
        }
        //获取一个区域，以村民为一个角延伸出的80x80x80区域
        final BoundingBox boundingBox4 = this.getBoundingBox().stretch(80.0, 80.0, 80.0);
        //搜索这个区域中的那些wantsGolem的村民加入一个List中
        final List<VillagerEntity> list5 = this.world.<Entity>getEntities(VillagerEntity.class, boundingBox4, this::wantsGolem).stream().limit(5L).collect(Collectors.toList());
        //List里应该要有至少5个村民
        final boolean boolean4 = list5.size() >= 5;
        if (!boolean4) {
            return;
        }
        final IronGolemEntity ironGolemEntity7 = this.spawnIronGolem();
        if (ironGolemEntity7 == null) {
            return;
        }
        final UUID uUID8 = ironGolemEntity7.getUuid();
        for (final VillagerEntity villagerEntity10 : list5) {
            for (final VillagerEntity villagerEntity11 : list5) {
                villagerEntity10.gossip.startGossip(villagerEntity11.getUuid(), VillageGossipType.f, -VillageGossipType.f.maxReputation);
            }
            villagerEntity10.buddyGolemId = uUID8;
        }
    }
    //这里使用到了函数式接口Predicate，传入流言类型判断是否是铁傀儡流言，猜测是铁傀儡流言vaule的总和大于30
    private boolean wantsGolem(final Entity entity) {
        return this.gossip.getReputationFor(entity.getUuid(), villageGossipType -> villageGossipType == VillageGossipType.f) > 30;
    }
    
    //按名字来解释，是否缺少铁傀儡，传入的time应该是当前的世界时间
    private boolean isLackingBuddyGolem(final long time) {
        if (this.buddyGolemId == null) {
            return true;
        }
        //设置一个始终比当前时间快1分钟的时间点，但要在村民拥有伙伴铁傀儡时才能执行
        if (this.oneMinAfterLastGolemCheckTimestamp < time + 1200L) {
            this.oneMinAfterLastGolemCheckTimestamp = time + 1200L;
            final Entity entity3 = ((ServerWorld)this.world).getEntity(this.buddyGolemId);
            //铁傀儡在村民的80格范围外就不会被检测到
            if (entity3 == null || !entity3.isAlive() || this.squaredDistanceTo(entity3) > 6400.0) {
                this.buddyGolemId = null;
                return true;
            }
        }
        return false;
    }
    
    //生成铁傀儡的方法
    @Nullable
    private IronGolemEntity spawnIronGolem() {
    	//获取村民的位置转化为方块坐标
        final BlockPos blockPos1 = new BlockPos(this);
        //尝试生成10次
        for (int integer2 = 0; integer2 < 10; ++integer2) {
        	//方块坐标blockpos1进行（-8~7，-3~2，-8~7）的随机选取，相当于铁傀儡的可能生成范围
            final BlockPos blockPos2 = blockPos1.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
            //判断铁傀儡是否可以在blockpos2坐标生成并执行对应操作，只要成功生成1次循环就结束
            final IronGolemEntity ironGolemEntity4 = EntityType.IRON_GOLEM.create(this.world, null, null, null, blockPos2, SpawnType.f, false, false);
            if (ironGolemEntity4 != null) {
                if (ironGolemEntity4.canSpawn(this.world, SpawnType.f) && ironGolemEntity4.canSpawn(this.world)) {
                    this.world.spawnEntity(ironGolemEntity4);
                    return ironGolemEntity4;
                }
                ironGolemEntity4.remove();
            }
        }
        return null;
    }
    
    @Override
    public void onInteractionWith(final EntityInteraction interaction, final Entity entity) {
        if (interaction == EntityInteraction.a) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.d, 25);
        }
        else if (interaction == EntityInteraction.e) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.e, 2);
        }
        else if (interaction == EntityInteraction.c) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.b, 25);
        }
        else if (interaction == EntityInteraction.d) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.a, 25);
        }
    }
    
    @Override
    public int getExperience() {
        return this.experience;
    }
    
    public void setExperience(final int integer) {
        this.experience = integer;
    }
    
    public long getLastRestock() {
        return this.lastRestock;
    }
    
    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugRendererInfoManager.sendVillagerAiDebugData(this);
    }
    
    @Override
    public void sleep(final BlockPos blockPos) {
        super.sleep(blockPos);
        final GolemSpawnCondition golemSpawnCondition2 = this.getBrain().<GolemSpawnCondition>getOptionalMemory(MemoryModuleType.u).orElseGet(GolemSpawnCondition::new);
        golemSpawnCondition2.setLastSlept(this.world.getTime());
        this.brain.<GolemSpawnCondition>putMemory(MemoryModuleType.u, golemSpawnCondition2);
    }
    
    static {
        VILLAGER_DATA = DataTracker.<VillagerData>registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
        ITEM_FOOD_VALUES = ImmutableMap.<Item, Integer>of(Items.jQ, 4, Items.nJ, 1, Items.nI, 1, Items.oO, 1);
        GATHERABLE_ITEMS = ImmutableSet.<Item>of(Items.jQ, Items.nJ, Items.nI, Items.jP, Items.jO, Items.oO, Items.oP);
        MEMORY_MODULES = ImmutableList.<MemoryModuleType<GlobalPos>>of(MemoryModuleType.b, MemoryModuleType.c, MemoryModuleType.d, (MemoryModuleType<GlobalPos>)MemoryModuleType.f, (MemoryModuleType<GlobalPos>)MemoryModuleType.g, (MemoryModuleType<GlobalPos>)MemoryModuleType.h, (MemoryModuleType<GlobalPos>)MemoryModuleType.i, (MemoryModuleType<GlobalPos>)MemoryModuleType.j, (MemoryModuleType<GlobalPos>)MemoryModuleType.k, (MemoryModuleType<GlobalPos>)MemoryModuleType.l, (MemoryModuleType<GlobalPos>)MemoryModuleType.m, (MemoryModuleType<GlobalPos>)MemoryModuleType.n, MemoryModuleType.o, MemoryModuleType.p, MemoryModuleType.q, MemoryModuleType.r, MemoryModuleType.s, MemoryModuleType.t, MemoryModuleType.e, MemoryModuleType.u, MemoryModuleType.v, MemoryModuleType.w);
        SENSORS = ImmutableList.<SensorType<NearestLivingEntitiesSensor>>of(SensorType.b, (SensorType<NearestLivingEntitiesSensor>)SensorType.c, (SensorType<NearestLivingEntitiesSensor>)SensorType.d, (SensorType<NearestLivingEntitiesSensor>)SensorType.e, (SensorType<NearestLivingEntitiesSensor>)SensorType.f, (SensorType<NearestLivingEntitiesSensor>)SensorType.g, (SensorType<NearestLivingEntitiesSensor>)SensorType.h, (SensorType<NearestLivingEntitiesSensor>)SensorType.i);
        POINTS_OF_INTEREST = ImmutableMap.<MemoryModuleType<GlobalPos>, Object>of(MemoryModuleType.b, (villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.q, MemoryModuleType.c, (villagerEntity, pointOfInterestType) -> villagerEntity.getVillagerData().getProfession().getWorkStation() == pointOfInterestType, MemoryModuleType.d, (villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.r);
    }
    
    public static final class GolemSpawnCondition
    {
        private long lastWorked;
        private long lastSlept;
        
        public void setLastWorked(final long lastWorked) {
            this.lastWorked = lastWorked;
        }
        
        public void setLastSlept(final long lastSlept) {
            this.lastSlept = lastSlept;
        }
        
        private boolean canSpawn(final long time) {
            return time - this.lastSlept < 24000L && time - this.lastWorked < 36000L;
        }
    }
}
