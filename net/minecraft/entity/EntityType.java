package net.minecraft.entity;

import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import org.apache.logging.log4j.LogManager;
import net.minecraft.tag.Tag;
import net.minecraft.nbt.ListTag;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.shape.VoxelShape;
import java.util.stream.Stream;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.Direction;
import java.util.Collections;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import java.util.Optional;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.vehicle.TNTMinecartEntity;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity>
{
    private static final Logger LOGGER;
    public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD;
    public static final EntityType<ArmorStandEntity> ARMOR_STAND;
    public static final EntityType<ArrowEntity> ARROW;
    public static final EntityType<BatEntity> BAT;
    public static final EntityType<BlazeEntity> BLAZE;
    public static final EntityType<BoatEntity> BOAT;
    public static final EntityType<CatEntity> CAT;
    public static final EntityType<CaveSpiderEntity> CAVE_SPIDER;
    public static final EntityType<ChickenEntity> CHICKEN;
    public static final EntityType<CodEntity> COD;
    public static final EntityType<CowEntity> COW;
    public static final EntityType<CreeperEntity> CREEPER;
    public static final EntityType<DonkeyEntity> DONKEY;
    public static final EntityType<DolphinEntity> DOLPHIN;
    public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL;
    public static final EntityType<DrownedEntity> DROWNED;
    public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN;
    public static final EntityType<EnderCrystalEntity> END_CRYSTAL;
    public static final EntityType<EnderDragonEntity> ENDER_DRAGON;
    public static final EntityType<EndermanEntity> ENDERMAN;
    public static final EntityType<EndermiteEntity> ENDERMITE;
    public static final EntityType<EvokerFangsEntity> EVOKER_FANGS;
    public static final EntityType<EvokerEntity> EVOKER;
    public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB;
    public static final EntityType<EnderEyeEntity> EYE_OF_ENDER;
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK;
    public static final EntityType<FireworkEntity> FIREWORK_ROCKET;
    public static final EntityType<FoxEntity> B;
    public static final EntityType<GhastEntity> GHAST;
    public static final EntityType<GiantEntity> GIANT;
    public static final EntityType<GuardianEntity> GUARDIAN;
    public static final EntityType<HorseEntity> HORSE;
    public static final EntityType<HuskEntity> HUSK;
    public static final EntityType<IllusionerEntity> ILLUSIONER;
    public static final EntityType<ItemEntity> ITEM;
    public static final EntityType<ItemFrameEntity> ITEM_FRAME;
    public static final EntityType<FireballEntity> FIREBALL;
    public static final EntityType<LeadKnotEntity> LEASH_KNOT;
    public static final EntityType<LlamaEntity> LLAMA;
    public static final EntityType<LlamaSpitEntity> LLAMA_SPIT;
    public static final EntityType<MagmaCubeEntity> MAGMA_CUBE;
    public static final EntityType<MinecartEntity> MINECART;
    public static final EntityType<ChestMinecartEntity> CHEST_MINECART;
    public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART;
    public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART;
    public static final EntityType<HopperMinecartEntity> HOPPER_MINECART;
    public static final EntityType<MobSpawnerMinecartEntity> SPAWNER_MINECART;
    public static final EntityType<TNTMinecartEntity> TNT_MINECART;
    public static final EntityType<MuleEntity> MULE;
    public static final EntityType<MooshroomEntity> MOOSHROOM;
    public static final EntityType<OcelotEntity> OCELOT;
    public static final EntityType<PaintingEntity> PAINTING;
    public static final EntityType<PandaEntity> PANDA;
    public static final EntityType<ParrotEntity> PARROT;
    public static final EntityType<PigEntity> PIG;
    public static final EntityType<PufferfishEntity> PUFFERFISH;
    public static final EntityType<ZombiePigmanEntity> ZOMBIE_PIGMAN;
    public static final EntityType<PolarBearEntity> POLAR_BEAR;
    public static final EntityType<PrimedTntEntity> TNT;
    public static final EntityType<RabbitEntity> RABBIT;
    public static final EntityType<SalmonEntity> SALMON;
    public static final EntityType<SheepEntity> SHEEP;
    public static final EntityType<ShulkerEntity> SHULKER;
    public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET;
    public static final EntityType<SilverfishEntity> SILVERFISH;
    public static final EntityType<SkeletonEntity> SKELETON;
    public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE;
    public static final EntityType<SlimeEntity> SLIME;
    public static final EntityType<SmallFireballEntity> SMALL_FIREBALL;
    public static final EntityType<SnowmanEntity> SNOW_GOLEM;
    public static final EntityType<SnowballEntity> SNOWBALL;
    public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW;
    public static final EntityType<SpiderEntity> SPIDER;
    public static final EntityType<SquidEntity> SQUID;
    public static final EntityType<StrayEntity> STRAY;
    public static final EntityType<TraderLlamaEntity> ax;
    public static final EntityType<TropicalFishEntity> TROPICAL_FISH;
    public static final EntityType<TurtleEntity> TURTLE;
    public static final EntityType<ThrownEggEntity> EGG;
    public static final EntityType<ThrownEnderpearlEntity> ENDER_PEARL;
    public static final EntityType<ThrownExperienceBottleEntity> EXPERIENCE_BOTTLE;
    public static final EntityType<ThrownPotionEntity> POTION;
    public static final EntityType<TridentEntity> TRIDENT;
    public static final EntityType<VexEntity> VEX;
    public static final EntityType<VillagerEntity> VILLAGER;
    public static final EntityType<IronGolemEntity> IRON_GOLEM;
    public static final EntityType<VindicatorEntity> VINDICATOR;
    public static final EntityType<PillagerEntity> PILLAGER;
    public static final EntityType<WanderingTraderEntity> aK;
    public static final EntityType<WitchEntity> WITCH;
    public static final EntityType<WitherEntity> WITHER;
    public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON;
    public static final EntityType<ExplodingWitherSkullEntity> WITHER_SKULL;
    public static final EntityType<WolfEntity> WOLF;
    public static final EntityType<ZombieEntity> ZOMBIE;
    public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE;
    public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER;
    public static final EntityType<PhantomEntity> PHANTOM;
    public static final EntityType<RavagerEntity> RAVAGER;
    public static final EntityType<LightningEntity> LIGHTNING_BOLT;
    public static final EntityType<PlayerEntity> PLAYER;
    public static final EntityType<FishHookEntity> FISHING_BOBBER;
    private final EntityFactory<T> factory;
    private final EntityCategory category;
    private final boolean saveable;
    private final boolean summonable;
    private final boolean fireImmune;
    @Nullable
    private String translationKey;
    @Nullable
    private TextComponent textComponent;
    @Nullable
    private Identifier lootTableId;
    @Nullable
    private final Type<?> dataFixerType;
    private final EntitySize size;
    
    private static <T extends Entity> EntityType<T> register(final String string, final Builder<T> builder) {
        return Registry.<EntityType<T>>register(Registry.ENTITY_TYPE, string, builder.build(string));
    }
    
    public static Identifier getId(final EntityType<?> entityType) {
        return Registry.ENTITY_TYPE.getId(entityType);
    }
    
    public static Optional<EntityType<?>> get(final String string) {
        return Registry.ENTITY_TYPE.getOrEmpty(Identifier.create(string));
    }
    
    public EntityType(final EntityFactory<T> factory, final EntityCategory category, final boolean saveable, final boolean summonable, final boolean fireImmune, @Nullable final Type<?> dataFixerType, final EntitySize size) {
        this.factory = factory;
        this.category = category;
        this.saveable = saveable;
        this.summonable = summonable;
        this.fireImmune = fireImmune;
        this.dataFixerType = dataFixerType;
        this.size = size;
    }
    
    @Nullable
    public Entity spawnFromItemStack(final World world, @Nullable final ItemStack itemStack, @Nullable final PlayerEntity playerEntity, final BlockPos blockPos, final SpawnType spawnType, final boolean boolean6, final boolean boolean7) {
        return this.spawn(world, (itemStack == null) ? null : itemStack.getTag(), (itemStack != null && itemStack.hasDisplayName()) ? itemStack.getDisplayName() : null, playerEntity, blockPos, spawnType, boolean6, boolean7);
    }
    
    @Nullable
    public T spawn(final World world, @Nullable final CompoundTag compoundTag, @Nullable final TextComponent textComponent, @Nullable final PlayerEntity playerEntity, final BlockPos blockPos, final SpawnType spawnType, final boolean boolean7, final boolean boolean8) {
        final T entity9 = this.create(world, compoundTag, textComponent, playerEntity, blockPos, spawnType, boolean7, boolean8);
        world.spawnEntity(entity9);
        return entity9;
    }
    
    @Nullable
    public T create(final World world, @Nullable final CompoundTag compoundTag, @Nullable final TextComponent textComponent, @Nullable final PlayerEntity playerEntity, final BlockPos blockPos, final SpawnType spawnType, final boolean boolean7, final boolean boolean8) {
        final T entity9 = this.create(world);
        if (entity9 == null) {
            return null;
        }
        double double10;
        if (boolean7) {
            entity9.setPosition(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
            double10 = getOriginY(world, blockPos, boolean8, entity9.getBoundingBox());
        }
        else {
            double10 = 0.0;
        }
        entity9.setPositionAndAngles(blockPos.getX() + 0.5, blockPos.getY() + double10, blockPos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0f), 0.0f);
        if (entity9 instanceof MobEntity) {
            final MobEntity mobEntity12 = (MobEntity)entity9;
            mobEntity12.headYaw = mobEntity12.yaw;
            mobEntity12.aK = mobEntity12.yaw;
            mobEntity12.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity12)), spawnType, null, compoundTag);
            mobEntity12.playAmbientSound();
        }
        if (textComponent != null && entity9 instanceof LivingEntity) {
            entity9.setCustomName(textComponent);
        }
        loadFromEntityTag(world, playerEntity, entity9, compoundTag);
        return entity9;
    }
    
    protected static double getOriginY(final ViewableWorld viewableWorld, final BlockPos blockPos, final boolean boolean3, final BoundingBox boundingBox) {
        BoundingBox boundingBox2 = new BoundingBox(blockPos);
        if (boolean3) {
            boundingBox2 = boundingBox2.stretch(0.0, -1.0, 0.0);
        }
        final Stream<VoxelShape> stream6 = viewableWorld.getCollisionShapes(null, boundingBox2, Collections.<Entity>emptySet());
        return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, stream6, boolean3 ? -2.0 : -1.0);
    }
    
    public static void loadFromEntityTag(final World world, @Nullable final PlayerEntity playerEntity, @Nullable final Entity entity, @Nullable final CompoundTag compoundTag) {
        if (compoundTag == null || !compoundTag.containsKey("EntityTag", 10)) {
            return;
        }
        final MinecraftServer minecraftServer5 = world.getServer();
        if (minecraftServer5 == null || entity == null) {
            return;
        }
        if (!world.isClient && entity.bS() && (playerEntity == null || !minecraftServer5.getPlayerManager().isOperator(playerEntity.getGameProfile()))) {
            return;
        }
        final CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
        final UUID uUID7 = entity.getUuid();
        compoundTag2.copyFrom(compoundTag.getCompound("EntityTag"));
        entity.setUuid(uUID7);
        entity.fromTag(compoundTag2);
    }
    
    public boolean isSaveable() {
        return this.saveable;
    }
    
    public boolean isSummonable() {
        return this.summonable;
    }
    
    public boolean isFireImmune() {
        return this.fireImmune;
    }
    
    public EntityCategory getCategory() {
        return this.category;
    }
    
    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("entity", Registry.ENTITY_TYPE.getId(this));
        }
        return this.translationKey;
    }
    
    public TextComponent getTextComponent() {
        if (this.textComponent == null) {
            this.textComponent = new TranslatableTextComponent(this.getTranslationKey(), new Object[0]);
        }
        return this.textComponent;
    }
    
    public Identifier getLootTableId() {
        if (this.lootTableId == null) {
            final Identifier identifier1 = Registry.ENTITY_TYPE.getId(this);
            this.lootTableId = new Identifier(identifier1.getNamespace(), "entities/" + identifier1.getPath());
        }
        return this.lootTableId;
    }
    
    public float getWidth() {
        return this.size.width;
    }
    
    public float getHeight() {
        return this.size.height;
    }
    
    @Nullable
    public T create(final World world) {
        return this.factory.create(this, world);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static Entity createInstanceFromId(final int integer, final World world) {
        return newInstance(world, Registry.ENTITY_TYPE.get(integer));
    }
    
    public static Optional<Entity> getEntityFromTag(final CompoundTag compoundTag, final World world) {
        return SystemUtil.<Entity>ifPresentOrElse(fromTag(compoundTag).<Entity>map(entityType -> entityType.create(world)), entity -> entity.fromTag(compoundTag), () -> EntityType.LOGGER.warn("Skipping Entity with id {}", compoundTag.getString("id")));
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    private static Entity newInstance(final World world, @Nullable final EntityType<?> type) {
        return (Entity)((type == null) ? null : type.create(world));
    }
    
    public BoundingBox createSimpleBoundingBox(final double double1, final double double3, final double double5) {
        final float float7 = this.getWidth() / 2.0f;
        return new BoundingBox(double1 - float7, double3, double5 - float7, double1 + float7, double3 + this.getHeight(), double5 + float7);
    }
    
    public EntitySize getDefaultSize() {
        return this.size;
    }
    
    public static Optional<EntityType<?>> fromTag(final CompoundTag compoundTag) {
        return Registry.ENTITY_TYPE.getOrEmpty(new Identifier(compoundTag.getString("id")));
    }
    
    @Nullable
    public static Entity loadEntityWithPassengers(final CompoundTag compoundTag, final World world, final Function<Entity, Entity> entityProcessor) {
        ListTag listTag5;
        int integer6;
        Entity entity2;
        return loadEntityFromTag(compoundTag, world).map(entityProcessor).<Entity>map(entity -> {
            if (compoundTag.containsKey("Passengers", 9)) {
                for (listTag5 = compoundTag.getList("Passengers", 10), integer6 = 0; integer6 < listTag5.size(); ++integer6) {
                    entity2 = loadEntityWithPassengers(listTag5.getCompoundTag(integer6), world, entityProcessor);
                    if (entity2 != null) {
                        entity2.startRiding(entity, true);
                    }
                }
            }
            return entity;
        }).orElse(null);
    }
    
    private static Optional<Entity> loadEntityFromTag(final CompoundTag compoundTag, final World world) {
        try {
            return getEntityFromTag(compoundTag, world);
        }
        catch (RuntimeException runtimeException3) {
            EntityType.LOGGER.warn("Exception loading entity: ", (Throwable)runtimeException3);
            return Optional.<Entity>empty();
        }
    }
    
    public int getMaxTrackDistance() {
        if (this == EntityType.PLAYER) {
            return 32;
        }
        if (this == EntityType.END_CRYSTAL) {
            return 16;
        }
        if (this == EntityType.ENDER_DRAGON || this == EntityType.TNT || this == EntityType.FALLING_BLOCK || this == EntityType.ITEM_FRAME || this == EntityType.LEASH_KNOT || this == EntityType.PAINTING || this == EntityType.ARMOR_STAND || this == EntityType.EXPERIENCE_ORB || this == EntityType.AREA_EFFECT_CLOUD || this == EntityType.EVOKER_FANGS) {
            return 10;
        }
        if (this == EntityType.FISHING_BOBBER || this == EntityType.ARROW || this == EntityType.SPECTRAL_ARROW || this == EntityType.TRIDENT || this == EntityType.SMALL_FIREBALL || this == EntityType.DRAGON_FIREBALL || this == EntityType.FIREBALL || this == EntityType.WITHER_SKULL || this == EntityType.SNOWBALL || this == EntityType.LLAMA_SPIT || this == EntityType.ENDER_PEARL || this == EntityType.EYE_OF_ENDER || this == EntityType.EGG || this == EntityType.POTION || this == EntityType.EXPERIENCE_BOTTLE || this == EntityType.FIREWORK_ROCKET || this == EntityType.ITEM) {
            return 4;
        }
        return 5;
    }
    
    public int getTrackTickInterval() {
        if (this == EntityType.PLAYER || this == EntityType.EVOKER_FANGS) {
            return 2;
        }
        if (this == EntityType.EYE_OF_ENDER) {
            return 4;
        }
        if (this == EntityType.FISHING_BOBBER) {
            return 5;
        }
        if (this == EntityType.SMALL_FIREBALL || this == EntityType.DRAGON_FIREBALL || this == EntityType.FIREBALL || this == EntityType.WITHER_SKULL || this == EntityType.SNOWBALL || this == EntityType.LLAMA_SPIT || this == EntityType.ENDER_PEARL || this == EntityType.EGG || this == EntityType.POTION || this == EntityType.EXPERIENCE_BOTTLE || this == EntityType.FIREWORK_ROCKET || this == EntityType.TNT) {
            return 10;
        }
        if (this == EntityType.ARROW || this == EntityType.SPECTRAL_ARROW || this == EntityType.TRIDENT || this == EntityType.ITEM || this == EntityType.FALLING_BLOCK || this == EntityType.EXPERIENCE_ORB) {
            return 20;
        }
        if (this == EntityType.ITEM_FRAME || this == EntityType.LEASH_KNOT || this == EntityType.PAINTING || this == EntityType.AREA_EFFECT_CLOUD || this == EntityType.END_CRYSTAL) {
            return Integer.MAX_VALUE;
        }
        return 3;
    }
    
    public boolean alwaysUpdateVelocity() {
        return this != EntityType.PLAYER && this != EntityType.LLAMA_SPIT && this != EntityType.WITHER && this != EntityType.BAT && this != EntityType.ITEM_FRAME && this != EntityType.LEASH_KNOT && this != EntityType.PAINTING && this != EntityType.END_CRYSTAL && this != EntityType.EVOKER_FANGS;
    }
    
    public boolean isTaggedWith(final Tag<EntityType<?>> tag) {
        return tag.contains(this);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        AREA_EFFECT_CLOUD = EntityType.<AreaEffectCloudEntity>register("area_effect_cloud", Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, EntityCategory.e).c().setSize(6.0f, 0.5f));
        ARMOR_STAND = EntityType.<ArmorStandEntity>register("armor_stand", Builder.<ArmorStandEntity>create(ArmorStandEntity::new, EntityCategory.e).setSize(0.5f, 1.975f));
        ARROW = EntityType.<ArrowEntity>register("arrow", Builder.<ArrowEntity>create(ArrowEntity::new, EntityCategory.e).setSize(0.5f, 0.5f));
        BAT = EntityType.<BatEntity>register("bat", Builder.<BatEntity>create(BatEntity::new, EntityCategory.c).setSize(0.5f, 0.9f));
        BLAZE = EntityType.<BlazeEntity>register("blaze", Builder.<BlazeEntity>create(BlazeEntity::new, EntityCategory.a).c().setSize(0.6f, 1.8f));
        BOAT = EntityType.<BoatEntity>register("boat", Builder.<BoatEntity>create(BoatEntity::new, EntityCategory.e).setSize(1.375f, 0.5625f));
        CAT = EntityType.<CatEntity>register("cat", Builder.<CatEntity>create(CatEntity::new, EntityCategory.b).setSize(0.6f, 0.7f));
        CAVE_SPIDER = EntityType.<CaveSpiderEntity>register("cave_spider", Builder.<CaveSpiderEntity>create(CaveSpiderEntity::new, EntityCategory.a).setSize(0.7f, 0.5f));
        CHICKEN = EntityType.<ChickenEntity>register("chicken", Builder.<ChickenEntity>create(ChickenEntity::new, EntityCategory.b).setSize(0.4f, 0.7f));
        COD = EntityType.<CodEntity>register("cod", Builder.<CodEntity>create(CodEntity::new, EntityCategory.d).setSize(0.5f, 0.3f));
        COW = EntityType.<CowEntity>register("cow", Builder.<CowEntity>create(CowEntity::new, EntityCategory.b).setSize(0.9f, 1.4f));
        CREEPER = EntityType.<CreeperEntity>register("creeper", Builder.<CreeperEntity>create(CreeperEntity::new, EntityCategory.a).setSize(0.6f, 1.7f));
        DONKEY = EntityType.<DonkeyEntity>register("donkey", Builder.<DonkeyEntity>create(DonkeyEntity::new, EntityCategory.b).setSize(1.3964844f, 1.5f));
        DOLPHIN = EntityType.<DolphinEntity>register("dolphin", Builder.<DolphinEntity>create(DolphinEntity::new, EntityCategory.d).setSize(0.9f, 0.6f));
        DRAGON_FIREBALL = EntityType.<DragonFireballEntity>register("dragon_fireball", Builder.<DragonFireballEntity>create(DragonFireballEntity::new, EntityCategory.e).setSize(1.0f, 1.0f));
        DROWNED = EntityType.<DrownedEntity>register("drowned", Builder.<DrownedEntity>create(DrownedEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        ELDER_GUARDIAN = EntityType.<ElderGuardianEntity>register("elder_guardian", Builder.<ElderGuardianEntity>create(ElderGuardianEntity::new, EntityCategory.a).setSize(1.9975f, 1.9975f));
        END_CRYSTAL = EntityType.<EnderCrystalEntity>register("end_crystal", Builder.<EnderCrystalEntity>create(EnderCrystalEntity::new, EntityCategory.e).setSize(2.0f, 2.0f));
        ENDER_DRAGON = EntityType.<EnderDragonEntity>register("ender_dragon", Builder.<EnderDragonEntity>create(EnderDragonEntity::new, EntityCategory.a).c().setSize(16.0f, 8.0f));
        ENDERMAN = EntityType.<EndermanEntity>register("enderman", Builder.<EndermanEntity>create(EndermanEntity::new, EntityCategory.a).setSize(0.6f, 2.9f));
        ENDERMITE = EntityType.<EndermiteEntity>register("endermite", Builder.<EndermiteEntity>create(EndermiteEntity::new, EntityCategory.a).setSize(0.4f, 0.3f));
        EVOKER_FANGS = EntityType.<EvokerFangsEntity>register("evoker_fangs", Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, EntityCategory.e).setSize(0.5f, 0.8f));
        EVOKER = EntityType.<EvokerEntity>register("evoker", Builder.<EvokerEntity>create(EvokerEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        EXPERIENCE_ORB = EntityType.<ExperienceOrbEntity>register("experience_orb", Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, EntityCategory.e).setSize(0.5f, 0.5f));
        EYE_OF_ENDER = EntityType.<EnderEyeEntity>register("eye_of_ender", Builder.<EnderEyeEntity>create(EnderEyeEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        FALLING_BLOCK = EntityType.<FallingBlockEntity>register("falling_block", Builder.<FallingBlockEntity>create(FallingBlockEntity::new, EntityCategory.e).setSize(0.98f, 0.98f));
        FIREWORK_ROCKET = EntityType.<FireworkEntity>register("firework_rocket", Builder.<FireworkEntity>create(FireworkEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        B = EntityType.<FoxEntity>register("fox", Builder.<FoxEntity>create(FoxEntity::new, EntityCategory.b).setSize(0.6f, 0.7f));
        GHAST = EntityType.<GhastEntity>register("ghast", Builder.<GhastEntity>create(GhastEntity::new, EntityCategory.a).c().setSize(4.0f, 4.0f));
        GIANT = EntityType.<GiantEntity>register("giant", Builder.<GiantEntity>create(GiantEntity::new, EntityCategory.a).setSize(3.6f, 12.0f));
        GUARDIAN = EntityType.<GuardianEntity>register("guardian", Builder.<GuardianEntity>create(GuardianEntity::new, EntityCategory.a).setSize(0.85f, 0.85f));
        HORSE = EntityType.<HorseEntity>register("horse", Builder.<HorseEntity>create(HorseEntity::new, EntityCategory.b).setSize(1.3964844f, 1.6f));
        HUSK = EntityType.<HuskEntity>register("husk", Builder.<HuskEntity>create(HuskEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        ILLUSIONER = EntityType.<IllusionerEntity>register("illusioner", Builder.<IllusionerEntity>create(IllusionerEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        ITEM = EntityType.<ItemEntity>register("item", Builder.<ItemEntity>create(ItemEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        ITEM_FRAME = EntityType.<ItemFrameEntity>register("item_frame", Builder.<ItemFrameEntity>create(ItemFrameEntity::new, EntityCategory.e).setSize(0.5f, 0.5f));
        FIREBALL = EntityType.<FireballEntity>register("fireball", Builder.<FireballEntity>create(FireballEntity::new, EntityCategory.e).setSize(1.0f, 1.0f));
        LEASH_KNOT = EntityType.<LeadKnotEntity>register("leash_knot", Builder.<LeadKnotEntity>create(LeadKnotEntity::new, EntityCategory.e).disableSaving().setSize(0.5f, 0.5f));
        LLAMA = EntityType.<LlamaEntity>register("llama", Builder.<LlamaEntity>create(LlamaEntity::new, EntityCategory.b).setSize(0.9f, 1.87f));
        LLAMA_SPIT = EntityType.<LlamaSpitEntity>register("llama_spit", Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        MAGMA_CUBE = EntityType.<MagmaCubeEntity>register("magma_cube", Builder.<MagmaCubeEntity>create(MagmaCubeEntity::new, EntityCategory.a).c().setSize(2.04f, 2.04f));
        MINECART = EntityType.<MinecartEntity>register("minecart", Builder.<MinecartEntity>create(MinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        CHEST_MINECART = EntityType.<ChestMinecartEntity>register("chest_minecart", Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        COMMAND_BLOCK_MINECART = EntityType.<CommandBlockMinecartEntity>register("command_block_minecart", Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        FURNACE_MINECART = EntityType.<FurnaceMinecartEntity>register("furnace_minecart", Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        HOPPER_MINECART = EntityType.<HopperMinecartEntity>register("hopper_minecart", Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        SPAWNER_MINECART = EntityType.<MobSpawnerMinecartEntity>register("spawner_minecart", Builder.<MobSpawnerMinecartEntity>create(MobSpawnerMinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        TNT_MINECART = EntityType.<TNTMinecartEntity>register("tnt_minecart", Builder.<TNTMinecartEntity>create(TNTMinecartEntity::new, EntityCategory.e).setSize(0.98f, 0.7f));
        MULE = EntityType.<MuleEntity>register("mule", Builder.<MuleEntity>create(MuleEntity::new, EntityCategory.b).setSize(1.3964844f, 1.6f));
        MOOSHROOM = EntityType.<MooshroomEntity>register("mooshroom", Builder.<MooshroomEntity>create(MooshroomEntity::new, EntityCategory.b).setSize(0.9f, 1.4f));
        OCELOT = EntityType.<OcelotEntity>register("ocelot", Builder.<OcelotEntity>create(OcelotEntity::new, EntityCategory.b).setSize(0.6f, 0.7f));
        PAINTING = EntityType.<PaintingEntity>register("painting", Builder.<PaintingEntity>create(PaintingEntity::new, EntityCategory.e).setSize(0.5f, 0.5f));
        PANDA = EntityType.<PandaEntity>register("panda", Builder.<PandaEntity>create(PandaEntity::new, EntityCategory.b).setSize(1.3f, 1.25f));
        PARROT = EntityType.<ParrotEntity>register("parrot", Builder.<ParrotEntity>create(ParrotEntity::new, EntityCategory.b).setSize(0.5f, 0.9f));
        PIG = EntityType.<PigEntity>register("pig", Builder.<PigEntity>create(PigEntity::new, EntityCategory.b).setSize(0.9f, 0.9f));
        PUFFERFISH = EntityType.<PufferfishEntity>register("pufferfish", Builder.<PufferfishEntity>create(PufferfishEntity::new, EntityCategory.d).setSize(0.7f, 0.7f));
        ZOMBIE_PIGMAN = EntityType.<ZombiePigmanEntity>register("zombie_pigman", Builder.<ZombiePigmanEntity>create(ZombiePigmanEntity::new, EntityCategory.a).c().setSize(0.6f, 1.95f));
        POLAR_BEAR = EntityType.<PolarBearEntity>register("polar_bear", Builder.<PolarBearEntity>create(PolarBearEntity::new, EntityCategory.b).setSize(1.4f, 1.4f));
        TNT = EntityType.<PrimedTntEntity>register("tnt", Builder.<PrimedTntEntity>create(PrimedTntEntity::new, EntityCategory.e).c().setSize(0.98f, 0.98f));
        RABBIT = EntityType.<RabbitEntity>register("rabbit", Builder.<RabbitEntity>create(RabbitEntity::new, EntityCategory.b).setSize(0.4f, 0.5f));
        SALMON = EntityType.<SalmonEntity>register("salmon", Builder.<SalmonEntity>create(SalmonEntity::new, EntityCategory.d).setSize(0.7f, 0.4f));
        SHEEP = EntityType.<SheepEntity>register("sheep", Builder.<SheepEntity>create(SheepEntity::new, EntityCategory.b).setSize(0.9f, 1.3f));
        SHULKER = EntityType.<ShulkerEntity>register("shulker", Builder.<ShulkerEntity>create(ShulkerEntity::new, EntityCategory.a).c().setSize(1.0f, 1.0f));
        SHULKER_BULLET = EntityType.<ShulkerBulletEntity>register("shulker_bullet", Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, EntityCategory.e).setSize(0.3125f, 0.3125f));
        SILVERFISH = EntityType.<SilverfishEntity>register("silverfish", Builder.<SilverfishEntity>create(SilverfishEntity::new, EntityCategory.a).setSize(0.4f, 0.3f));
        SKELETON = EntityType.<SkeletonEntity>register("skeleton", Builder.<SkeletonEntity>create(SkeletonEntity::new, EntityCategory.a).setSize(0.6f, 1.99f));
        SKELETON_HORSE = EntityType.<SkeletonHorseEntity>register("skeleton_horse", Builder.<SkeletonHorseEntity>create(SkeletonHorseEntity::new, EntityCategory.b).setSize(1.3964844f, 1.6f));
        SLIME = EntityType.<SlimeEntity>register("slime", Builder.<SlimeEntity>create(SlimeEntity::new, EntityCategory.a).setSize(2.04f, 2.04f));
        SMALL_FIREBALL = EntityType.<SmallFireballEntity>register("small_fireball", Builder.<SmallFireballEntity>create(SmallFireballEntity::new, EntityCategory.e).setSize(0.3125f, 0.3125f));
        SNOW_GOLEM = EntityType.<SnowmanEntity>register("snow_golem", Builder.<SnowmanEntity>create(SnowmanEntity::new, EntityCategory.b).setSize(0.7f, 1.9f));
        SNOWBALL = EntityType.<SnowballEntity>register("snowball", Builder.<SnowballEntity>create(SnowballEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        SPECTRAL_ARROW = EntityType.<SpectralArrowEntity>register("spectral_arrow", Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, EntityCategory.e).setSize(0.5f, 0.5f));
        SPIDER = EntityType.<SpiderEntity>register("spider", Builder.<SpiderEntity>create(SpiderEntity::new, EntityCategory.a).setSize(1.4f, 0.9f));
        SQUID = EntityType.<SquidEntity>register("squid", Builder.<SquidEntity>create(SquidEntity::new, EntityCategory.d).setSize(0.8f, 0.8f));
        STRAY = EntityType.<StrayEntity>register("stray", Builder.<StrayEntity>create(StrayEntity::new, EntityCategory.a).setSize(0.6f, 1.99f));
        ax = EntityType.<TraderLlamaEntity>register("trader_llama", Builder.<TraderLlamaEntity>create(TraderLlamaEntity::new, EntityCategory.b).setSize(0.9f, 1.87f));
        TROPICAL_FISH = EntityType.<TropicalFishEntity>register("tropical_fish", Builder.<TropicalFishEntity>create(TropicalFishEntity::new, EntityCategory.d).setSize(0.5f, 0.4f));
        TURTLE = EntityType.<TurtleEntity>register("turtle", Builder.<TurtleEntity>create(TurtleEntity::new, EntityCategory.b).setSize(1.2f, 0.4f));
        EGG = EntityType.<ThrownEggEntity>register("egg", Builder.<ThrownEggEntity>create(ThrownEggEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        ENDER_PEARL = EntityType.<ThrownEnderpearlEntity>register("ender_pearl", Builder.<ThrownEnderpearlEntity>create(ThrownEnderpearlEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        EXPERIENCE_BOTTLE = EntityType.<ThrownExperienceBottleEntity>register("experience_bottle", Builder.<ThrownExperienceBottleEntity>create(ThrownExperienceBottleEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        POTION = EntityType.<ThrownPotionEntity>register("potion", Builder.<ThrownPotionEntity>create(ThrownPotionEntity::new, EntityCategory.e).setSize(0.25f, 0.25f));
        TRIDENT = EntityType.<TridentEntity>register("trident", Builder.<TridentEntity>create(TridentEntity::new, EntityCategory.e).setSize(0.5f, 0.5f));
        VEX = EntityType.<VexEntity>register("vex", Builder.<VexEntity>create(VexEntity::new, EntityCategory.a).c().setSize(0.4f, 0.8f));
        VILLAGER = EntityType.<VillagerEntity>register("villager", Builder.<VillagerEntity>create(VillagerEntity::new, EntityCategory.b).setSize(0.6f, 1.95f));
        IRON_GOLEM = EntityType.<IronGolemEntity>register("iron_golem", Builder.<IronGolemEntity>create(IronGolemEntity::new, EntityCategory.b).setSize(1.4f, 2.7f));
        VINDICATOR = EntityType.<VindicatorEntity>register("vindicator", Builder.<VindicatorEntity>create(VindicatorEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        PILLAGER = EntityType.<PillagerEntity>register("pillager", Builder.<PillagerEntity>create(PillagerEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        aK = EntityType.<WanderingTraderEntity>register("wandering_trader", Builder.<WanderingTraderEntity>create(WanderingTraderEntity::new, EntityCategory.b).setSize(0.6f, 1.95f));
        WITCH = EntityType.<WitchEntity>register("witch", Builder.<WitchEntity>create(WitchEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        WITHER = EntityType.<WitherEntity>register("wither", Builder.<WitherEntity>create(WitherEntity::new, EntityCategory.a).c().setSize(0.9f, 3.5f));
        WITHER_SKELETON = EntityType.<WitherSkeletonEntity>register("wither_skeleton", Builder.<WitherSkeletonEntity>create(WitherSkeletonEntity::new, EntityCategory.a).c().setSize(0.7f, 2.4f));
        WITHER_SKULL = EntityType.<ExplodingWitherSkullEntity>register("wither_skull", Builder.<ExplodingWitherSkullEntity>create(ExplodingWitherSkullEntity::new, EntityCategory.e).setSize(0.3125f, 0.3125f));
        WOLF = EntityType.<WolfEntity>register("wolf", Builder.<WolfEntity>create(WolfEntity::new, EntityCategory.b).setSize(0.6f, 0.85f));
        ZOMBIE = EntityType.<ZombieEntity>register("zombie", Builder.<ZombieEntity>create(ZombieEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        ZOMBIE_HORSE = EntityType.<ZombieHorseEntity>register("zombie_horse", Builder.<ZombieHorseEntity>create(ZombieHorseEntity::new, EntityCategory.b).setSize(1.3964844f, 1.6f));
        ZOMBIE_VILLAGER = EntityType.<ZombieVillagerEntity>register("zombie_villager", Builder.<ZombieVillagerEntity>create(ZombieVillagerEntity::new, EntityCategory.a).setSize(0.6f, 1.95f));
        PHANTOM = EntityType.<PhantomEntity>register("phantom", Builder.<PhantomEntity>create(PhantomEntity::new, EntityCategory.a).setSize(0.9f, 0.5f));
        RAVAGER = EntityType.<RavagerEntity>register("ravager", Builder.<RavagerEntity>create(RavagerEntity::new, EntityCategory.a).setSize(1.95f, 2.2f));
        LIGHTNING_BOLT = EntityType.<LightningEntity>register("lightning_bolt", (Builder<LightningEntity>)Builder.<T>create(EntityCategory.e).disableSaving().setSize(0.0f, 0.0f));
        PLAYER = EntityType.<PlayerEntity>register("player", (Builder<PlayerEntity>)Builder.<T>create(EntityCategory.e).disableSaving().disableSummon().setSize(0.6f, 1.8f));
        FISHING_BOBBER = EntityType.<FishHookEntity>register("fishing_bobber", (Builder<FishHookEntity>)Builder.<T>create(EntityCategory.e).disableSaving().disableSummon().setSize(0.25f, 0.25f));
    }
    
    public static class Builder<T extends Entity>
    {
        private final EntityFactory<T> function;
        private final EntityCategory entityClass;
        private boolean saveable;
        private boolean summonable;
        private boolean e;
        private EntitySize size;
        
        private Builder(final EntityFactory<T> entityFactory, final EntityCategory entityCategory) {
            this.saveable = true;
            this.summonable = true;
            this.size = EntitySize.resizeable(0.6f, 1.8f);
            this.function = entityFactory;
            this.entityClass = entityCategory;
        }
        
        public static <T extends Entity> Builder<T> create(final EntityFactory<T> entityFactory, final EntityCategory entityCategory) {
            return new Builder<T>(entityFactory, entityCategory);
        }
        
        public static <T extends Entity> Builder<T> create(final EntityCategory entityCategory) {
            return new Builder<T>((entityType, world) -> null, entityCategory);
        }
        
        public Builder<T> setSize(final float width, final float height) {
            this.size = EntitySize.resizeable(width, height);
            return this;
        }
        
        public Builder<T> disableSummon() {
            this.summonable = false;
            return this;
        }
        
        public Builder<T> disableSaving() {
            this.saveable = false;
            return this;
        }
        
        public Builder<T> c() {
            this.e = true;
            return this;
        }
        
        public EntityType<T> build(final String string) {
            Type<?> type2 = null;
            if (this.saveable) {
                try {
                    type2 = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(TypeReferences.ENTITY_TREE, string);
                }
                catch (IllegalStateException illegalStateException3) {
                    if (SharedConstants.isDevelopment) {
                        throw illegalStateException3;
                    }
                    EntityType.LOGGER.warn("No data fixer registered for entity {}", string);
                }
            }
            return new EntityType<T>(this.function, this.entityClass, this.saveable, this.summonable, this.e, type2, this.size);
        }
    }
    
    public interface EntityFactory<T extends Entity>
    {
        T create(final EntityType<T> arg1, final World arg2);
    }
}
