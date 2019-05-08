package net.minecraft.entity.raid;

import java.util.AbstractList;
import java.util.Locale;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import net.minecraft.text.TextFormat;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import javax.annotation.Nullable;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.stat.Stats;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.function.Predicate;
import net.minecraft.world.World;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.boss.BossBar;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;
import java.util.Set;
import java.util.Map;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.ItemStack;

public class Raid
{
    public static final ItemStack OMINOUS_BANNER;
    private static final TranslatableTextComponent EVENT_TEXT;
    private static final TranslatableTextComponent VICTORY_SUFFIX_TEXT;
    private static final TranslatableTextComponent DEFEAT_SUFFIX_TEXT;
    private static final TextComponent VICTORY_TITLE;
    private static final TextComponent DEFEAT_TITLE;
    private final Map<Integer, RaiderEntity> waveToCaptain;
    private final Map<Integer, Set<RaiderEntity>> waveToRaiders;
    private final Set<UUID> heroesOfTheVillage;
    private long ticksActive;
    private final BlockPos center;
    private final ServerWorld world;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int badOmenLevel;
    private boolean active;
    private int wavesSpawned;
    private final ServerBossBar bar;
    private int postRaidTicks;
    private int preRaidTicks;
    private final Random random;
    private final int waveCount;
    private Status status;
    private int finishCooldown;
    private Optional<BlockPos> preCalculatedRavagerSpawnLocation;
    
    public Raid(final int id, final ServerWorld serverWorld, final BlockPos blockPos) {
        this.waveToCaptain = Maps.newHashMap();
        this.waveToRaiders = Maps.newHashMap();
        this.heroesOfTheVillage = Sets.newHashSet();
        this.bar = new ServerBossBar(Raid.EVENT_TEXT, BossBar.Color.c, BossBar.Style.c);
        this.random = new Random();
        this.preCalculatedRavagerSpawnLocation = Optional.<BlockPos>empty();
        this.id = id;
        this.world = serverWorld;
        this.active = true;
        this.preRaidTicks = 300;
        this.bar.setPercent(0.0f);
        this.center = blockPos;
        this.waveCount = this.getMaxWaves(serverWorld.getDifficulty());
        this.status = Status.a;
    }
    
    public Raid(final ServerWorld serverWorld, final CompoundTag compoundTag) {
        this.waveToCaptain = Maps.newHashMap();
        this.waveToRaiders = Maps.newHashMap();
        this.heroesOfTheVillage = Sets.newHashSet();
        this.bar = new ServerBossBar(Raid.EVENT_TEXT, BossBar.Color.c, BossBar.Style.c);
        this.random = new Random();
        this.preCalculatedRavagerSpawnLocation = Optional.<BlockPos>empty();
        this.world = serverWorld;
        this.id = compoundTag.getInt("Id");
        this.started = compoundTag.getBoolean("Started");
        this.active = compoundTag.getBoolean("Active");
        this.ticksActive = compoundTag.getLong("TicksActive");
        this.badOmenLevel = compoundTag.getInt("BadOmenLevel");
        this.wavesSpawned = compoundTag.getInt("GroupsSpawned");
        this.preRaidTicks = compoundTag.getInt("PreRaidTicks");
        this.postRaidTicks = compoundTag.getInt("PostRaidTicks");
        this.totalHealth = compoundTag.getFloat("TotalHealth");
        this.center = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
        this.waveCount = compoundTag.getInt("NumGroups");
        this.status = fromName(compoundTag.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (compoundTag.containsKey("HeroesOfTheVillage", 9)) {
            final ListTag listTag3 = compoundTag.getList("HeroesOfTheVillage", 10);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                final CompoundTag compoundTag2 = listTag3.getCompoundTag(integer4);
                final UUID uUID6 = compoundTag2.getUuid("UUID");
                this.heroesOfTheVillage.add(uUID6);
            }
        }
    }
    
    public boolean isFinished() {
        return this.hasWon() || this.hasLost();
    }
    
    public boolean isPreRaid() {
        return this.hasSpawned() && this.getRaiderCount() == 0 && this.preRaidTicks > 0;
    }
    
    public boolean hasSpawned() {
        return this.wavesSpawned > 0;
    }
    
    public boolean hasStopped() {
        return this.status == Status.d;
    }
    
    public boolean hasWon() {
        return this.status == Status.b;
    }
    
    public boolean hasLost() {
        return this.status == Status.c;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public boolean hasStarted() {
        return this.started;
    }
    
    public int getGroupsSpawned() {
        return this.wavesSpawned;
    }
    
    private Predicate<ServerPlayerEntity> isInRaidDistance() {
        final boolean b;
        return serverPlayerEntity -> {
            if (serverPlayerEntity.isAlive()) {
                if (serverPlayerEntity.getServerWorld().isNearOccupiedPointOfInterest(new BlockPos(serverPlayerEntity), 2)) {
                    return b;
                }
            }
            return b;
        };
    }
    
    private void updateBarToPlayers() {
        final Set<ServerPlayerEntity> set1 = Sets.newHashSet();
        for (final ServerPlayerEntity serverPlayerEntity3 : this.world.getPlayers(this.isInRaidDistance())) {
            this.bar.addPlayer(serverPlayerEntity3);
            set1.add(serverPlayerEntity3);
        }
        final Set<ServerPlayerEntity> set2 = Sets.newHashSet(this.bar.getPlayers());
        set2.removeAll(set1);
        for (final ServerPlayerEntity serverPlayerEntity4 : set2) {
            this.bar.removePlayer(serverPlayerEntity4);
        }
    }
    
    public int getMaxAcceptableBadOmenLevel() {
        return 5;
    }
    
    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }
    
    public void start(final PlayerEntity player) {
        if (player.hasStatusEffect(StatusEffects.E)) {
            this.badOmenLevel += player.getStatusEffect(StatusEffects.E).getAmplifier() + 1;
            this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxAcceptableBadOmenLevel());
        }
        player.removeStatusEffect(StatusEffects.E);
    }
    
    public void invalidate() {
        this.active = false;
        this.bar.clearPlayers();
        this.status = Status.d;
    }
    
    public void tick() {
        if (this.hasStopped()) {
            return;
        }
        if (this.status == Status.a) {
            final boolean boolean1 = this.active;
            this.active = this.world.isBlockLoaded(this.center);
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                this.invalidate();
                return;
            }
            if (boolean1 != this.active) {
                this.bar.setVisible(this.active);
            }
            if (!this.active) {
                return;
            }
            if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
                if (this.wavesSpawned > 0) {
                    this.status = Status.c;
                }
                else {
                    this.invalidate();
                }
                return;
            }
            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
                this.invalidate();
                return;
            }
            final int integer2 = this.getRaiderCount();
            if (integer2 == 0 && this.shouldSpawnMoreGroups()) {
                if (this.preRaidTicks > 0) {
                    if (!this.preCalculatedRavagerSpawnLocation.isPresent() && this.preRaidTicks % 5 == 0) {
                        int integer3 = 0;
                        if (this.preRaidTicks < 100) {
                            integer3 = 1;
                        }
                        else if (this.preRaidTicks < 40) {
                            integer3 = 2;
                        }
                        this.preCalculatedRavagerSpawnLocation = this.preCalculateRavagerSpawnLocation(integer3);
                    }
                    if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
                        this.updateBarToPlayers();
                    }
                    --this.preRaidTicks;
                    this.bar.setPercent(MathHelper.clamp((300 - this.preRaidTicks) / 300.0f, 0.0f, 1.0f));
                }
                else if (this.preRaidTicks == 0 && this.wavesSpawned > 0) {
                    this.preRaidTicks = 300;
                    this.bar.setName(Raid.EVENT_TEXT);
                    return;
                }
            }
            if (this.ticksActive % 20L == 0L) {
                this.updateBarToPlayers();
                this.removeObsoleteRaiders();
                if (integer2 > 0) {
                    if (integer2 <= 2) {
                        this.bar.setName(Raid.EVENT_TEXT.copyShallow().append(" - ").append(new TranslatableTextComponent("event.minecraft.raid.raiders_remaining", new Object[] { integer2 })));
                    }
                    else {
                        this.bar.setName(Raid.EVENT_TEXT);
                    }
                }
                else {
                    this.bar.setName(Raid.EVENT_TEXT);
                }
            }
            boolean boolean2 = false;
            int integer4 = 0;
            while (this.canSpawnRaiders()) {
                final BlockPos blockPos5 = this.preCalculatedRavagerSpawnLocation.isPresent() ? this.preCalculatedRavagerSpawnLocation.get() : this.getRavagerSpawnLocation(integer4, 20);
                if (blockPos5 != null) {
                    this.started = true;
                    this.spawnNextWave(blockPos5);
                    if (!boolean2) {
                        this.playRaidHorn(blockPos5);
                        boolean2 = true;
                    }
                }
                else {
                    ++integer4;
                }
                if (integer4 > 3) {
                    this.invalidate();
                    break;
                }
            }
            if (this.hasStarted() && !this.shouldSpawnMoreGroups() && integer2 == 0) {
                if (this.postRaidTicks < 40) {
                    ++this.postRaidTicks;
                }
                else {
                    this.status = Status.b;
                    for (final UUID uUID6 : this.heroesOfTheVillage) {
                        final Entity entity7 = this.world.getEntity(uUID6);
                        if (entity7 instanceof LivingEntity && !entity7.isSpectator()) {
                            final LivingEntity livingEntity8 = (LivingEntity)entity7;
                            livingEntity8.addPotionEffect(new StatusEffectInstance(StatusEffects.F, 48000, this.badOmenLevel - 1, false, false, true));
                            if (!(livingEntity8 instanceof ServerPlayerEntity)) {
                                continue;
                            }
                            final ServerPlayerEntity serverPlayerEntity9 = (ServerPlayerEntity)livingEntity8;
                            serverPlayerEntity9.incrementStat(Stats.az);
                            Criterions.HERO_OF_THE_VILLAGE.handle(serverPlayerEntity9);
                        }
                    }
                }
            }
            this.markDirty();
        }
        else if (this.isFinished()) {
            ++this.finishCooldown;
            if (this.finishCooldown >= 600) {
                this.invalidate();
                return;
            }
            if (this.finishCooldown % 20 == 0) {
                this.updateBarToPlayers();
                this.bar.setVisible(true);
                if (this.hasWon()) {
                    this.bar.setPercent(0.0f);
                    this.bar.setName(Raid.VICTORY_TITLE);
                }
                else {
                    this.bar.setName(Raid.DEFEAT_TITLE);
                }
            }
        }
    }
    
    private Optional<BlockPos> preCalculateRavagerSpawnLocation(final int proximity) {
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            final BlockPos blockPos3 = this.getRavagerSpawnLocation(proximity, 1);
            if (blockPos3 != null) {
                return Optional.<BlockPos>of(blockPos3);
            }
        }
        return Optional.<BlockPos>empty();
    }
    
    private boolean shouldSpawnMoreGroups() {
        if (this.hasExtraWave()) {
            return !this.hasSpawnedExtraWave();
        }
        return !this.hasSpawnedFinalWave();
    }
    
    private boolean hasSpawnedFinalWave() {
        return this.getGroupsSpawned() == this.waveCount;
    }
    
    private boolean hasExtraWave() {
        return this.badOmenLevel > 1;
    }
    
    private boolean hasSpawnedExtraWave() {
        return this.getGroupsSpawned() > this.waveCount;
    }
    
    private boolean isSpawningExtraWave() {
        return this.hasSpawnedFinalWave() && this.getRaiderCount() == 0 && this.hasExtraWave();
    }
    
    private void removeObsoleteRaiders() {
        final Iterator<Set<RaiderEntity>> iterator1 = this.waveToRaiders.values().iterator();
        final Set<RaiderEntity> set2 = Sets.newHashSet();
        while (iterator1.hasNext()) {
            final Set<RaiderEntity> set3 = iterator1.next();
            for (final RaiderEntity raiderEntity5 : set3) {
                if (raiderEntity5.removed || raiderEntity5.dimension != this.world.getDimension().getType()) {
                    raiderEntity5.setOutOfRaidCounter(30);
                }
                else if (raiderEntity5.age <= 600) {
                    continue;
                }
                if (!RaidManager.isLivingAroundVillage(raiderEntity5, this.center, 32) && raiderEntity5.getDespawnCounter() > 2400) {
                    raiderEntity5.setOutOfRaidCounter(raiderEntity5.getOutOfRaidCounter() + 1);
                }
                if (raiderEntity5.getOutOfRaidCounter() >= 30) {
                    set2.add(raiderEntity5);
                }
            }
        }
        for (final RaiderEntity raiderEntity6 : set2) {
            this.removeFromWave(raiderEntity6, true);
        }
    }
    
    private void playRaidHorn(final BlockPos blockPos) {
        final float float2 = 13.0f;
        final int integer3 = 64;
        for (final PlayerEntity playerEntity5 : this.world.getPlayers()) {
            final Vec3d vec3d6 = new Vec3d(playerEntity5.x, playerEntity5.y, playerEntity5.z);
            final Vec3d vec3d7 = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            final float float3 = MathHelper.sqrt((vec3d7.x - vec3d6.x) * (vec3d7.x - vec3d6.x) + (vec3d7.z - vec3d6.z) * (vec3d7.z - vec3d6.z));
            final double double9 = vec3d6.x + 13.0f / float3 * (vec3d7.x - vec3d6.x);
            final double double10 = vec3d6.z + 13.0f / float3 * (vec3d7.z - vec3d6.z);
            if (float3 <= 64.0f || RaidManager.isLivingAroundVillage(playerEntity5, this.center, 32)) {
                ((ServerPlayerEntity)playerEntity5).networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.jn, SoundCategory.g, double9, playerEntity5.y, double10, 64.0f, 1.0f));
            }
        }
    }
    
    private void spawnNextWave(final BlockPos blockPos) {
        boolean boolean2 = false;
        final int integer3 = this.wavesSpawned + 1;
        this.totalHealth = 0.0f;
        final LocalDifficulty localDifficulty4 = this.world.getLocalDifficulty(blockPos);
        final boolean boolean3 = this.isSpawningExtraWave();
        for (final Member member9 : Member.VALUES) {
            final int integer4 = this.getCount(member9, integer3, boolean3) + this.getBonusCount(member9, this.random, integer3, localDifficulty4, boolean3);
            int integer5 = 0;
            for (int integer6 = 0; integer6 < integer4; ++integer6) {
                final RaiderEntity raiderEntity13 = member9.type.create(this.world);
                this.addRaider(integer3, raiderEntity13, blockPos, false);
                if (!boolean2 && raiderEntity13.canLead()) {
                    raiderEntity13.setPatrolLeader(true);
                    this.setWaveCaptain(integer3, raiderEntity13);
                    boolean2 = true;
                }
                if (member9.type == EntityType.RAVAGER) {
                    RaiderEntity raiderEntity14 = null;
                    if (integer3 == this.getMaxWaves(Difficulty.NORMAL)) {
                        raiderEntity14 = EntityType.PILLAGER.create(this.world);
                    }
                    else if (integer3 >= this.getMaxWaves(Difficulty.HARD)) {
                        if (integer5 == 0) {
                            raiderEntity14 = EntityType.EVOKER.create(this.world);
                        }
                        else {
                            raiderEntity14 = EntityType.VINDICATOR.create(this.world);
                        }
                    }
                    ++integer5;
                    if (raiderEntity14 != null) {
                        this.addRaider(integer3, raiderEntity14, blockPos, false);
                        raiderEntity14.setPositionAndAngles(blockPos, 0.0f, 0.0f);
                        raiderEntity14.startRiding(raiderEntity13);
                    }
                }
            }
        }
        this.preCalculatedRavagerSpawnLocation = Optional.<BlockPos>empty();
        ++this.wavesSpawned;
        this.updateBar();
        this.markDirty();
    }
    
    public void addRaider(final int wave, final RaiderEntity raider, @Nullable final BlockPos pos, final boolean existing) {
        final boolean boolean5 = this.addToWave(wave, raider);
        if (boolean5) {
            raider.setRaid(this);
            raider.setWave(wave);
            raider.setAbleToJoinRaid(true);
            raider.setOutOfRaidCounter(0);
            if (!existing && pos != null) {
                raider.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                raider.initialize(this.world, this.world.getLocalDifficulty(pos), SpawnType.h, null, null);
                raider.addBonusForWave(wave, false);
                raider.onGround = true;
                this.world.spawnEntity(raider);
            }
        }
    }
    
    public void updateBar() {
        this.bar.setPercent(MathHelper.clamp(this.getCurrentRaiderHealth() / this.totalHealth, 0.0f, 1.0f));
    }
    
    public float getCurrentRaiderHealth() {
        float float1 = 0.0f;
        for (final Set<RaiderEntity> set3 : this.waveToRaiders.values()) {
            for (final RaiderEntity raiderEntity5 : set3) {
                float1 += raiderEntity5.getHealth();
            }
        }
        return float1;
    }
    
    private boolean canSpawnRaiders() {
        return this.preRaidTicks == 0 && (this.wavesSpawned < this.waveCount || this.isSpawningExtraWave()) && this.getRaiderCount() == 0;
    }
    
    public int getRaiderCount() {
        return this.waveToRaiders.values().stream().mapToInt(Set::size).sum();
    }
    
    public void removeFromWave(@Nonnull final RaiderEntity raiderEntity, final boolean countHealth) {
        final Set<RaiderEntity> set3 = this.waveToRaiders.get(raiderEntity.getWave());
        if (set3 != null) {
            final boolean boolean4 = set3.remove(raiderEntity);
            if (boolean4) {
                if (countHealth) {
                    this.totalHealth -= raiderEntity.getHealth();
                }
                raiderEntity.setRaid(null);
                this.updateBar();
                this.markDirty();
            }
        }
    }
    
    private void markDirty() {
        this.world.getRaidManager().markDirty();
    }
    
    private static ItemStack getIllagerBanner() {
        final ItemStack itemStack1 = new ItemStack(Items.ov);
        final CompoundTag compoundTag2 = itemStack1.getOrCreateSubCompoundTag("BlockEntityTag");
        final ListTag listTag3 = new BannerPattern.Builder().with(BannerPattern.RHOMBUS, DyeColor.j).with(BannerPattern.STRIPE_BOTTOM, DyeColor.i).with(BannerPattern.STRIPE_CENTER, DyeColor.h).with(BannerPattern.BORDER, DyeColor.i).with(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).with(BannerPattern.HALF_HORIZONTAL_TOP, DyeColor.i).with(BannerPattern.CIRCLE, DyeColor.i).with(BannerPattern.BORDER, DyeColor.BLACK).build();
        compoundTag2.put("Patterns", listTag3);
        itemStack1.setDisplayName(new TranslatableTextComponent("block.minecraft.ominous_banner", new Object[0]).applyFormat(TextFormat.g));
        return itemStack1;
    }
    
    @Nullable
    public RaiderEntity getCaptain(final int wave) {
        return this.waveToCaptain.get(wave);
    }
    
    @Nullable
    private BlockPos getRavagerSpawnLocation(final int proximity, final int tries) {
        final int integer3 = (proximity == 0) ? 2 : (2 - proximity);
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        for (int integer4 = 0; integer4 < tries; ++integer4) {
            final float float9 = this.world.random.nextFloat() * 6.2831855f;
            final int integer5 = this.center.getX() + (int)(MathHelper.cos(float9) * 32.0f * integer3) + this.world.random.nextInt(5);
            final int integer6 = this.center.getZ() + (int)(MathHelper.sin(float9) * 32.0f * integer3) + this.world.random.nextInt(5);
            final int integer7 = this.world.getTop(Heightmap.Type.b, integer5, integer6);
            mutable7.set(integer5, integer7, integer6);
            if (!this.world.isNearOccupiedPointOfInterest(mutable7) || proximity >= 2) {
                if (this.world.isAreaLoaded(mutable7.getX() - 10, mutable7.getY() - 10, mutable7.getZ() - 10, mutable7.getX() + 10, mutable7.getY() + 10, mutable7.getZ() + 10)) {
                    if (SpawnHelper.canSpawn(SpawnRestriction.Location.a, this.world, mutable7, EntityType.RAVAGER) || (this.world.getBlockState(mutable7.down()).getBlock() == Blocks.cA && this.world.getBlockState(mutable7).isAir())) {
                        return mutable7;
                    }
                }
            }
        }
        return null;
    }
    
    private boolean addToWave(final int wave, final RaiderEntity raiderEntity) {
        return this.addToWave(wave, raiderEntity, true);
    }
    
    public boolean addToWave(final int wave, final RaiderEntity raiderEntity, final boolean countHealth) {
        this.waveToRaiders.computeIfAbsent(Integer.valueOf(wave), integer -> Sets.newHashSet());
        final Set<RaiderEntity> set4 = this.waveToRaiders.get(wave);
        RaiderEntity raiderEntity2 = null;
        for (final RaiderEntity raiderEntity3 : set4) {
            if (raiderEntity3.getUuid().equals(raiderEntity.getUuid())) {
                raiderEntity2 = raiderEntity3;
                break;
            }
        }
        if (raiderEntity2 != null) {
            set4.remove(raiderEntity2);
            set4.add(raiderEntity);
        }
        set4.add(raiderEntity);
        if (countHealth) {
            this.totalHealth += raiderEntity.getHealth();
        }
        this.updateBar();
        this.markDirty();
        return true;
    }
    
    public void setWaveCaptain(final int wave, final RaiderEntity raiderEntity) {
        this.waveToCaptain.put(wave, raiderEntity);
        raiderEntity.setEquippedStack(EquipmentSlot.HEAD, Raid.OMINOUS_BANNER);
        raiderEntity.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
    }
    
    public void removeLeader(final int wave) {
        this.waveToCaptain.remove(wave);
    }
    
    public BlockPos getCenter() {
        return this.center;
    }
    
    public int getRaidId() {
        return this.id;
    }
    
    private int getCount(final Member member, final int wave, final boolean extra) {
        return extra ? member.waveToCount[this.waveCount] : member.waveToCount[wave];
    }
    
    private int getBonusCount(final Member member, final Random random, final int wave, final LocalDifficulty localDifficulty, final boolean extra) {
        final Difficulty difficulty6 = localDifficulty.getGlobalDifficulty();
        final boolean boolean7 = difficulty6 == Difficulty.EASY;
        final boolean boolean8 = difficulty6 == Difficulty.NORMAL;
        int integer9 = 0;
        switch (member) {
            case WITCH: {
                if (!boolean7 && wave > 2 && wave != 4) {
                    integer9 = 1;
                    break;
                }
                return 0;
            }
            case PILLAGER:
            case VINDICATOR: {
                if (boolean7) {
                    integer9 = random.nextInt(2);
                    break;
                }
                if (boolean8) {
                    integer9 = 1;
                    break;
                }
                integer9 = 2;
                break;
            }
            case RAVAGER: {
                integer9 = ((!boolean7 && extra) ? 1 : 0);
                break;
            }
            default: {
                return 0;
            }
        }
        return (integer9 > 0) ? random.nextInt(integer9 + 1) : 0;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public CompoundTag toTag(final CompoundTag compoundTag) {
        compoundTag.putInt("Id", this.id);
        compoundTag.putBoolean("Started", this.started);
        compoundTag.putBoolean("Active", this.active);
        compoundTag.putLong("TicksActive", this.ticksActive);
        compoundTag.putInt("BadOmenLevel", this.badOmenLevel);
        compoundTag.putInt("GroupsSpawned", this.wavesSpawned);
        compoundTag.putInt("PreRaidTicks", this.preRaidTicks);
        compoundTag.putInt("PostRaidTicks", this.postRaidTicks);
        compoundTag.putFloat("TotalHealth", this.totalHealth);
        compoundTag.putInt("NumGroups", this.waveCount);
        compoundTag.putString("Status", this.status.getName());
        compoundTag.putInt("CX", this.center.getX());
        compoundTag.putInt("CY", this.center.getY());
        compoundTag.putInt("CZ", this.center.getZ());
        final ListTag listTag2 = new ListTag();
        for (final UUID uUID4 : this.heroesOfTheVillage) {
            final CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putUuid("UUID", uUID4);
            ((AbstractList<CompoundTag>)listTag2).add(compoundTag2);
        }
        compoundTag.put("HeroesOfTheVillage", listTag2);
        return compoundTag;
    }
    
    public int getMaxWaves(final Difficulty difficulty) {
        switch (difficulty) {
            case EASY: {
                return 3;
            }
            case NORMAL: {
                return 5;
            }
            case HARD: {
                return 7;
            }
            default: {
                return 0;
            }
        }
    }
    
    public float getEnchantmentChance() {
        final int integer1 = this.getBadOmenLevel();
        if (integer1 == 2) {
            return 0.1f;
        }
        if (integer1 == 3) {
            return 0.25f;
        }
        if (integer1 == 4) {
            return 0.5f;
        }
        if (integer1 == 5) {
            return 0.75f;
        }
        return 0.0f;
    }
    
    public void addHero(final Entity entity) {
        this.heroesOfTheVillage.add(entity.getUuid());
    }
    
    static {
        OMINOUS_BANNER = getIllagerBanner();
        EVENT_TEXT = new TranslatableTextComponent("event.minecraft.raid", new Object[0]);
        VICTORY_SUFFIX_TEXT = new TranslatableTextComponent("event.minecraft.raid.victory", new Object[0]);
        DEFEAT_SUFFIX_TEXT = new TranslatableTextComponent("event.minecraft.raid.defeat", new Object[0]);
        VICTORY_TITLE = Raid.EVENT_TEXT.copyShallow().append(" - ").append(Raid.VICTORY_SUFFIX_TEXT);
        DEFEAT_TITLE = Raid.EVENT_TEXT.copyShallow().append(" - ").append(Raid.DEFEAT_SUFFIX_TEXT);
    }
    
    enum Status
    {
        a, 
        b, 
        c, 
        d;
        
        private static final Status[] VALUES;
        
        private static Status fromName(final String string) {
            for (final Status status5 : Status.VALUES) {
                if (string.equalsIgnoreCase(status5.name())) {
                    return status5;
                }
            }
            return Status.a;
        }
        
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        static {
            VALUES = values();
        }
    }
    
    enum Member
    {
        VINDICATOR(EntityType.VINDICATOR, new int[] { 0, 0, 2, 0, 1, 4, 2, 5 }), 
        EVOKER(EntityType.EVOKER, new int[] { 0, 0, 0, 0, 0, 1, 1, 2 }), 
        PILLAGER(EntityType.PILLAGER, new int[] { 0, 4, 3, 3, 4, 4, 4, 2 }), 
        WITCH(EntityType.WITCH, new int[] { 0, 0, 0, 0, 3, 0, 0, 1 }), 
        RAVAGER(EntityType.RAVAGER, new int[] { 0, 0, 0, 1, 0, 1, 0, 2 });
        
        private static final Member[] VALUES;
        private final EntityType<? extends RaiderEntity> type;
        private final int[] waveToCount;
        
        private Member(final EntityType<? extends RaiderEntity> entityType, final int[] arr) {
            this.type = entityType;
            this.waveToCount = arr;
        }
        
        static {
            VALUES = values();
        }
    }
}
