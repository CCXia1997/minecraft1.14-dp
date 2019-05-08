package net.minecraft.entity.boss.dragon;

import java.util.AbstractList;
import net.minecraft.predicate.entity.EntityPredicates;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.chunk.ChunkStatus;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Void;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.server.world.ChunkTicketType;
import java.util.Iterator;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPatternBuilder;
import java.util.Collections;
import java.util.Random;
import java.util.Collection;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import net.minecraft.util.TagHelper;
import com.google.common.collect.Lists;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;
import net.minecraft.block.pattern.BlockPattern;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import org.apache.logging.log4j.Logger;

public class EnderDragonFight
{
    private static final Logger LOGGER;
    private static final Predicate<Entity> VALID_ENTITY;
    private final ServerBossBar bossBar;
    private final ServerWorld world;
    private final List<Integer> gateways;
    private final BlockPattern endPortalPattern;
    private int dragonSeenTimer;
    private int endCrystalsAlive;
    private int crystalCountTimer;
    private int playerUpdateTimer;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    private UUID dragonUuid;
    private boolean doLegacyCheck;
    private BlockPos exitPortalLocation;
    private EnderDragonSpawnState dragonSpawnState;
    private int spawnStateTimer;
    private List<EnderCrystalEntity> crystals;
    
    public EnderDragonFight(final ServerWorld world, final CompoundTag compoundTag) {
        this.bossBar = (ServerBossBar)new ServerBossBar(new TranslatableTextComponent("entity.minecraft.ender_dragon", new Object[0]), BossBar.Color.a, BossBar.Style.a).setDragonMusic(true).setThickenFog(true);
        this.gateways = Lists.newArrayList();
        this.doLegacyCheck = true;
        this.world = world;
        if (compoundTag.containsKey("DragonKilled", 99)) {
            if (compoundTag.hasUuid("DragonUUID")) {
                this.dragonUuid = compoundTag.getUuid("DragonUUID");
            }
            this.dragonKilled = compoundTag.getBoolean("DragonKilled");
            this.previouslyKilled = compoundTag.getBoolean("PreviouslyKilled");
            if (compoundTag.getBoolean("IsRespawning")) {
                this.dragonSpawnState = EnderDragonSpawnState.START;
            }
            if (compoundTag.containsKey("ExitPortalLocation", 10)) {
                this.exitPortalLocation = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortalLocation"));
            }
        }
        else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }
        if (compoundTag.containsKey("Gateways", 9)) {
            final ListTag listTag3 = compoundTag.getList("Gateways", 3);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                this.gateways.add(listTag3.getInt(integer4));
            }
        }
        else {
            this.gateways.addAll(ContiguousSet.<Integer>create(Range.closedOpen(0, (C)20), DiscreteDomain.integers()));
            Collections.shuffle(this.gateways, new Random(world.getSeed()));
        }
        this.endPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.make(Blocks.z))).build();
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        if (this.dragonUuid != null) {
            compoundTag1.putUuid("DragonUUID", this.dragonUuid);
        }
        compoundTag1.putBoolean("DragonKilled", this.dragonKilled);
        compoundTag1.putBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.exitPortalLocation != null) {
            compoundTag1.put("ExitPortalLocation", TagHelper.serializeBlockPos(this.exitPortalLocation));
        }
        final ListTag listTag2 = new ListTag();
        for (final int integer4 : this.gateways) {
            ((AbstractList<IntTag>)listTag2).add(new IntTag(integer4));
        }
        compoundTag1.put("Gateways", listTag2);
        return compoundTag1;
    }
    
    public void tick() {
        this.bossBar.setVisible(!this.dragonKilled);
        if (++this.playerUpdateTimer >= 20) {
            this.updatePlayers();
            this.playerUpdateTimer = 0;
        }
        if (!this.bossBar.getPlayers().isEmpty()) {
            this.world.getChunkManager().<Void>addTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Void.INSTANCE);
            final boolean boolean1 = this.loadChunks();
            if (this.doLegacyCheck && boolean1) {
                this.convertFromLegacy();
                this.doLegacyCheck = false;
            }
            if (this.dragonSpawnState != null) {
                if (this.crystals == null && boolean1) {
                    this.dragonSpawnState = null;
                    this.respawnDragon();
                }
                this.dragonSpawnState.run(this.world, this, this.crystals, this.spawnStateTimer++, this.exitPortalLocation);
            }
            if (!this.dragonKilled) {
                if ((this.dragonUuid == null || ++this.dragonSeenTimer >= 1200) && boolean1) {
                    this.checkDragonSeen();
                    this.dragonSeenTimer = 0;
                }
                if (++this.crystalCountTimer >= 100 && boolean1) {
                    this.countAliveCrystals();
                    this.crystalCountTimer = 0;
                }
            }
        }
        else {
            this.world.getChunkManager().<Void>removeTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Void.INSTANCE);
        }
    }
    
    private void convertFromLegacy() {
        EnderDragonFight.LOGGER.info("Scanning for legacy world dragon fight...");
        final boolean boolean1 = this.worldContainsEndPortal();
        if (boolean1) {
            EnderDragonFight.LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        }
        else {
            EnderDragonFight.LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.generateEndPortal(this.previouslyKilled = false);
        }
        final List<EnderDragonEntity> list2 = this.world.getAliveEnderDragons();
        if (list2.isEmpty()) {
            this.dragonKilled = true;
        }
        else {
            final EnderDragonEntity enderDragonEntity3 = list2.get(0);
            this.dragonUuid = enderDragonEntity3.getUuid();
            EnderDragonFight.LOGGER.info("Found that there's a dragon still alive ({})", enderDragonEntity3);
            this.dragonKilled = false;
            if (!boolean1) {
                EnderDragonFight.LOGGER.info("But we didn't have a portal, let's remove it.");
                enderDragonEntity3.remove();
                this.dragonUuid = null;
            }
        }
        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }
    }
    
    private void checkDragonSeen() {
        final List<EnderDragonEntity> list1 = this.world.getAliveEnderDragons();
        if (list1.isEmpty()) {
            EnderDragonFight.LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createDragon();
        }
        else {
            EnderDragonFight.LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUuid = list1.get(0).getUuid();
        }
    }
    
    protected void setSpawnState(final EnderDragonSpawnState enderDragonSpawnState) {
        if (this.dragonSpawnState == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        }
        this.spawnStateTimer = 0;
        if (enderDragonSpawnState == EnderDragonSpawnState.END) {
            this.dragonSpawnState = null;
            this.dragonKilled = false;
            final EnderDragonEntity enderDragonEntity2 = this.createDragon();
            for (final ServerPlayerEntity serverPlayerEntity4 : this.bossBar.getPlayers()) {
                Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity4, enderDragonEntity2);
            }
        }
        else {
            this.dragonSpawnState = enderDragonSpawnState;
        }
    }
    
    private boolean worldContainsEndPortal() {
        for (int integer1 = -8; integer1 <= 8; ++integer1) {
            for (int integer2 = -8; integer2 <= 8; ++integer2) {
                final WorldChunk worldChunk3 = this.world.getChunk(integer1, integer2);
                for (final BlockEntity blockEntity5 : worldChunk3.getBlockEntities().values()) {
                    if (blockEntity5 instanceof EndPortalBlockEntity) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Nullable
    private BlockPattern.Result findEndPortal() {
        for (int integer1 = -8; integer1 <= 8; ++integer1) {
            for (int integer2 = -8; integer2 <= 8; ++integer2) {
                final WorldChunk worldChunk3 = this.world.getChunk(integer1, integer2);
                for (final BlockEntity blockEntity5 : worldChunk3.getBlockEntities().values()) {
                    if (blockEntity5 instanceof EndPortalBlockEntity) {
                        final BlockPattern.Result result6 = this.endPortalPattern.searchAround(this.world, blockEntity5.getPos());
                        if (result6 != null) {
                            final BlockPos blockPos7 = result6.translate(3, 3, 3).getBlockPos();
                            if (this.exitPortalLocation == null && blockPos7.getX() == 0 && blockPos7.getZ() == 0) {
                                this.exitPortalLocation = blockPos7;
                            }
                            return result6;
                        }
                        continue;
                    }
                }
            }
        }
        int integer2;
        for (int integer1 = integer2 = this.world.getTopPosition(Heightmap.Type.e, EndPortalFeature.ORIGIN).getY(); integer2 >= 0; --integer2) {
            final BlockPattern.Result result7 = this.endPortalPattern.searchAround(this.world, new BlockPos(EndPortalFeature.ORIGIN.getX(), integer2, EndPortalFeature.ORIGIN.getZ()));
            if (result7 != null) {
                if (this.exitPortalLocation == null) {
                    this.exitPortalLocation = result7.translate(3, 3, 3).getBlockPos();
                }
                return result7;
            }
        }
        return null;
    }
    
    private boolean loadChunks() {
        for (int integer1 = -8; integer1 <= 8; ++integer1) {
            for (int integer2 = 8; integer2 <= 8; ++integer2) {
                final Chunk chunk3 = this.world.getChunk(integer1, integer2, ChunkStatus.FULL, false);
                if (!(chunk3 instanceof WorldChunk)) {
                    return false;
                }
                final ChunkHolder.LevelType levelType4 = ((WorldChunk)chunk3).getLevelType();
                if (!levelType4.isAfter(ChunkHolder.LevelType.TICKING)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void updatePlayers() {
        final Set<ServerPlayerEntity> set1 = Sets.newHashSet();
        for (final ServerPlayerEntity serverPlayerEntity3 : this.world.getPlayers(EnderDragonFight.VALID_ENTITY)) {
            this.bossBar.addPlayer(serverPlayerEntity3);
            set1.add(serverPlayerEntity3);
        }
        final Set<ServerPlayerEntity> set2 = Sets.newHashSet(this.bossBar.getPlayers());
        set2.removeAll(set1);
        for (final ServerPlayerEntity serverPlayerEntity4 : set2) {
            this.bossBar.removePlayer(serverPlayerEntity4);
        }
    }
    
    private void countAliveCrystals() {
        this.crystalCountTimer = 0;
        this.endCrystalsAlive = 0;
        for (final EndSpikeFeature.Spike spike2 : EndSpikeFeature.getSpikes(this.world)) {
            this.endCrystalsAlive += this.world.<Entity>getEntities(EnderCrystalEntity.class, spike2.getBoundingBox()).size();
        }
        EnderDragonFight.LOGGER.debug("Found {} end crystals still alive", this.endCrystalsAlive);
    }
    
    public void dragonKilled(final EnderDragonEntity dragon) {
        if (dragon.getUuid().equals(this.dragonUuid)) {
            this.bossBar.setPercent(0.0f);
            this.bossBar.setVisible(false);
            this.generateEndPortal(true);
            this.generateNewEndGateway();
            if (!this.previouslyKilled) {
                this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.e, EndPortalFeature.ORIGIN), Blocks.dX.getDefaultState());
            }
            this.previouslyKilled = true;
            this.dragonKilled = true;
        }
    }
    
    private void generateNewEndGateway() {
        if (this.gateways.isEmpty()) {
            return;
        }
        final int integer1 = this.gateways.remove(this.gateways.size() - 1);
        final int integer2 = (int)(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * integer1)));
        final int integer3 = (int)(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * integer1)));
        this.generateEndGateway(new BlockPos(integer2, 75, integer3));
    }
    
    private void generateEndGateway(final BlockPos blockPos) {
        this.world.playLevelEvent(3000, blockPos, 0);
        Feature.aB.generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(), blockPos, EndGatewayFeatureConfig.createConfig());
    }
    
    private void generateEndPortal(final boolean previouslyKilled) {
        final EndPortalFeature endPortalFeature2 = new EndPortalFeature(previouslyKilled);
        if (this.exitPortalLocation == null) {
            this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.f, EndPortalFeature.ORIGIN).down();
            while (this.world.getBlockState(this.exitPortalLocation).getBlock() == Blocks.z && this.exitPortalLocation.getY() > this.world.getSeaLevel()) {
                this.exitPortalLocation = this.exitPortalLocation.down();
            }
        }
        endPortalFeature2.generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(), this.exitPortalLocation, FeatureConfig.DEFAULT);
    }
    
    private EnderDragonEntity createDragon() {
        this.world.getWorldChunk(new BlockPos(0, 128, 0));
        final EnderDragonEntity enderDragonEntity1 = EntityType.ENDER_DRAGON.create(this.world);
        enderDragonEntity1.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
        enderDragonEntity1.setPositionAndAngles(0.0, 128.0, 0.0, this.world.random.nextFloat() * 360.0f, 0.0f);
        this.world.spawnEntity(enderDragonEntity1);
        this.dragonUuid = enderDragonEntity1.getUuid();
        return enderDragonEntity1;
    }
    
    public void updateFight(final EnderDragonEntity dragon) {
        if (dragon.getUuid().equals(this.dragonUuid)) {
            this.bossBar.setPercent(dragon.getHealth() / dragon.getHealthMaximum());
            this.dragonSeenTimer = 0;
            if (dragon.hasCustomName()) {
                this.bossBar.setName(dragon.getDisplayName());
            }
        }
    }
    
    public int getAliveEndCrystals() {
        return this.endCrystalsAlive;
    }
    
    public void crystalDestroyed(final EnderCrystalEntity enderCrystal, final DamageSource source) {
        if (this.dragonSpawnState != null && this.crystals.contains(enderCrystal)) {
            EnderDragonFight.LOGGER.debug("Aborting respawn sequence");
            this.dragonSpawnState = null;
            this.spawnStateTimer = 0;
            this.resetEndCrystals();
            this.generateEndPortal(true);
        }
        else {
            this.countAliveCrystals();
            final Entity entity3 = this.world.getEntity(this.dragonUuid);
            if (entity3 instanceof EnderDragonEntity) {
                ((EnderDragonEntity)entity3).crystalDestroyed(enderCrystal, new BlockPos(enderCrystal), source);
            }
        }
    }
    
    public boolean hasPreviouslyKilled() {
        return this.previouslyKilled;
    }
    
    public void respawnDragon() {
        if (this.dragonKilled && this.dragonSpawnState == null) {
            BlockPos blockPos1 = this.exitPortalLocation;
            if (blockPos1 == null) {
                EnderDragonFight.LOGGER.debug("Tried to respawn, but need to find the portal first.");
                final BlockPattern.Result result2 = this.findEndPortal();
                if (result2 == null) {
                    EnderDragonFight.LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.generateEndPortal(true);
                }
                else {
                    EnderDragonFight.LOGGER.debug("Found the exit portal & temporarily using it.");
                }
                blockPos1 = this.exitPortalLocation;
            }
            final List<EnderCrystalEntity> list2 = Lists.newArrayList();
            final BlockPos blockPos2 = blockPos1.up(1);
            for (final Direction direction5 : Direction.Type.HORIZONTAL) {
                final List<EnderCrystalEntity> list3 = this.world.<EnderCrystalEntity>getEntities(EnderCrystalEntity.class, new BoundingBox(blockPos2.offset(direction5, 2)));
                if (list3.isEmpty()) {
                    return;
                }
                list2.addAll(list3);
            }
            EnderDragonFight.LOGGER.debug("Found all crystals, respawning dragon.");
            this.respawnDragon(list2);
        }
    }
    
    private void respawnDragon(final List<EnderCrystalEntity> list) {
        if (this.dragonKilled && this.dragonSpawnState == null) {
            for (BlockPattern.Result result2 = this.findEndPortal(); result2 != null; result2 = this.findEndPortal()) {
                for (int integer3 = 0; integer3 < this.endPortalPattern.getWidth(); ++integer3) {
                    for (int integer4 = 0; integer4 < this.endPortalPattern.getHeight(); ++integer4) {
                        for (int integer5 = 0; integer5 < this.endPortalPattern.getDepth(); ++integer5) {
                            final CachedBlockPosition cachedBlockPosition6 = result2.translate(integer3, integer4, integer5);
                            if (cachedBlockPosition6.getBlockState().getBlock() == Blocks.z || cachedBlockPosition6.getBlockState().getBlock() == Blocks.dU) {
                                this.world.setBlockState(cachedBlockPosition6.getBlockPos(), Blocks.dW.getDefaultState());
                            }
                        }
                    }
                }
            }
            this.dragonSpawnState = EnderDragonSpawnState.START;
            this.spawnStateTimer = 0;
            this.generateEndPortal(false);
            this.crystals = list;
        }
    }
    
    public void resetEndCrystals() {
        for (final EndSpikeFeature.Spike spike2 : EndSpikeFeature.getSpikes(this.world)) {
            final List<EnderCrystalEntity> list3 = this.world.<EnderCrystalEntity>getEntities(EnderCrystalEntity.class, spike2.getBoundingBox());
            for (final EnderCrystalEntity enderCrystalEntity5 : list3) {
                enderCrystalEntity5.setInvulnerable(false);
                enderCrystalEntity5.setBeamTarget(null);
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        VALID_ENTITY = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maximumDistance(0.0, 128.0, 0.0, 192.0));
    }
}
