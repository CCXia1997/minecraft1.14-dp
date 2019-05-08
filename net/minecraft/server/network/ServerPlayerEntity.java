package net.minecraft.server.network;

import org.apache.logging.log4j.LogManager;
import net.minecraft.text.Style;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.text.ChatMessageType;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.Recipe;
import java.util.Collection;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.util.DefaultedList;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.Hand;
import net.minecraft.container.HorseContainer;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.village.TraderOfferList;
import net.minecraft.container.Container;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import java.util.OptionalInt;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.scoreboard.Team;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.AbstractTeam;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.item.MapItem;
import java.util.Iterator;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Lists;
import net.minecraft.world.World;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.advancement.PlayerAdvancementTracker;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;
import net.minecraft.container.ContainerListener;
import net.minecraft.entity.player.PlayerEntity;

public class ServerPlayerEntity extends PlayerEntity implements ContainerListener
{
    private static final Logger LOGGER;
    private String clientLanguage;
    public ServerPlayNetworkHandler networkHandler;
    public final MinecraftServer server;
    public final ServerPlayerInteractionManager interactionManager;
    private final List<Integer> removedEntities;
    private final PlayerAdvancementTracker advancementManager;
    private final ServerStatHandler statHandler;
    private float bZ;
    private int ca;
    private int cb;
    private int cc;
    private int cd;
    private int ce;
    private float cf;
    private int cg;
    private boolean ch;
    private int ci;
    private int cj;
    private ChatVisibility clientChatVisibility;
    private boolean cl;
    private long lastActionTime;
    private Entity cameraEntity;
    private boolean inTeleportationState;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook;
    private Vec3d cr;
    private int cs;
    private boolean ct;
    @Nullable
    private Vec3d enteredNetherPos;
    private ChunkSectionPos cameraPosition;
    private int containerSyncId;
    public boolean e;
    public int f;
    public boolean notInAnyWorld;
    
    public ServerPlayerEntity(final MinecraftServer minecraftServer, final ServerWorld serverWorld, final GameProfile gameProfile, final ServerPlayerInteractionManager serverPlayerInteractionManager) {
        super(serverWorld, gameProfile);
        this.clientLanguage = "en_US";
        this.removedEntities = Lists.newLinkedList();
        this.bZ = Float.MIN_VALUE;
        this.ca = Integer.MIN_VALUE;
        this.cb = Integer.MIN_VALUE;
        this.cc = Integer.MIN_VALUE;
        this.cd = Integer.MIN_VALUE;
        this.ce = Integer.MIN_VALUE;
        this.cf = -1.0E8f;
        this.cg = -99999999;
        this.ch = true;
        this.ci = -99999999;
        this.cj = 60;
        this.cl = true;
        this.lastActionTime = SystemUtil.getMeasuringTimeMs();
        this.cameraPosition = ChunkSectionPos.from(0, 0, 0);
        serverPlayerInteractionManager.player = this;
        this.interactionManager = serverPlayerInteractionManager;
        this.server = minecraftServer;
        this.recipeBook = new ServerRecipeBook(minecraftServer.getRecipeManager());
        this.statHandler = minecraftServer.getPlayerManager().createStatHandler(this);
        this.advancementManager = minecraftServer.getPlayerManager().getAdvancementManager(this);
        this.stepHeight = 1.0f;
        this.a(serverWorld);
    }
    
    private void a(final ServerWorld serverWorld) {
        final BlockPos blockPos2 = serverWorld.getSpawnPos();
        if (serverWorld.dimension.hasSkyLight() && serverWorld.getLevelProperties().getGameMode() != GameMode.d) {
            int integer3 = Math.max(0, this.server.getSpawnRadius(serverWorld));
            final int integer4 = MathHelper.floor(serverWorld.getWorldBorder().contains(blockPos2.getX(), blockPos2.getZ()));
            if (integer4 < integer3) {
                integer3 = integer4;
            }
            if (integer4 <= 1) {
                integer3 = 1;
            }
            final int integer5 = (integer3 * 2 + 1) * (integer3 * 2 + 1);
            final int integer6 = this.s(integer5);
            final int integer7 = new Random().nextInt(integer5);
            for (int integer8 = 0; integer8 < integer5; ++integer8) {
                final int integer9 = (integer7 + integer6 * integer8) % integer5;
                final int integer10 = integer9 % (integer3 * 2 + 1);
                final int integer11 = integer9 / (integer3 * 2 + 1);
                final BlockPos blockPos3 = serverWorld.getDimension().getTopSpawningBlockPosition(blockPos2.getX() + integer10 - integer3, blockPos2.getZ() + integer11 - integer3, false);
                if (blockPos3 != null) {
                    this.setPositionAndAngles(blockPos3, 0.0f, 0.0f);
                    if (serverWorld.doesNotCollide(this)) {
                        break;
                    }
                }
            }
        }
        else {
            this.setPositionAndAngles(blockPos2, 0.0f, 0.0f);
            while (!serverWorld.doesNotCollide(this) && this.y < 255.0) {
                this.setPosition(this.x, this.y + 1.0, this.z);
            }
        }
    }
    
    private int s(final int integer) {
        return (integer <= 16) ? (integer - 1) : 17;
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("playerGameType", 99)) {
            if (this.getServer().shouldForceGameMode()) {
                this.interactionManager.setGameMode(this.getServer().getDefaultGameMode());
            }
            else {
                this.interactionManager.setGameMode(GameMode.byId(tag.getInt("playerGameType")));
            }
        }
        if (tag.containsKey("enteredNetherPosition", 10)) {
            final CompoundTag compoundTag2 = tag.getCompound("enteredNetherPosition");
            this.enteredNetherPos = new Vec3d(compoundTag2.getDouble("x"), compoundTag2.getDouble("y"), compoundTag2.getDouble("z"));
        }
        this.seenCredits = tag.getBoolean("seenCredits");
        if (tag.containsKey("recipeBook", 10)) {
            this.recipeBook.fromTag(tag.getCompound("recipeBook"));
        }
        if (this.isSleeping()) {
            this.wakeUp();
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("playerGameType", this.interactionManager.getGameMode().getId());
        tag.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPos != null) {
            final CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putDouble("x", this.enteredNetherPos.x);
            compoundTag2.putDouble("y", this.enteredNetherPos.y);
            compoundTag2.putDouble("z", this.enteredNetherPos.z);
            tag.put("enteredNetherPosition", compoundTag2);
        }
        final Entity entity2 = this.getTopmostVehicle();
        final Entity entity3 = this.getVehicle();
        if (entity3 != null && entity2 != this && entity2.bX()) {
            final CompoundTag compoundTag3 = new CompoundTag();
            final CompoundTag compoundTag4 = new CompoundTag();
            entity2.saveToTag(compoundTag4);
            compoundTag3.putUuid("Attach", entity3.getUuid());
            compoundTag3.put("Entity", compoundTag4);
            tag.put("RootVehicle", compoundTag3);
        }
        tag.put("recipeBook", this.recipeBook.toTag());
    }
    
    public void setExperiencePoints(final int integer) {
        final float float2 = (float)this.getNextLevelExperience();
        final float float3 = (float2 - 1.0f) / float2;
        this.experienceLevelProgress = MathHelper.clamp(integer / float2, 0.0f, float3);
        this.ci = -1;
    }
    
    public void setExperienceLevel(final int level) {
        this.experience = level;
        this.ci = -1;
    }
    
    @Override
    public void c(final int integer) {
        super.c(integer);
        this.ci = -1;
    }
    
    @Override
    public void a(final ItemStack itemStack, final int integer) {
        super.a(itemStack, integer);
        this.ci = -1;
    }
    
    public void s_() {
        this.container.addListener(this);
    }
    
    @Override
    public void e() {
        super.e();
        this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.BEGIN));
    }
    
    @Override
    public void f() {
        super.f();
        this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.END));
    }
    
    @Override
    protected void onBlockCollision(final BlockState blockState) {
        Criterions.ENTER_BLOCK.handle(this, blockState);
    }
    
    @Override
    protected ItemCooldownManager createCooldownManager() {
        return new ServerItemCooldownManager(this);
    }
    
    @Override
    public void tick() {
        this.interactionManager.update();
        --this.cj;
        if (this.T > 0) {
            --this.T;
        }
        this.container.sendContentUpdates();
        if (!this.world.isClient && !this.container.canUse(this)) {
            this.closeContainer();
            this.container = this.playerContainer;
        }
        while (!this.removedEntities.isEmpty()) {
            final int integer1 = Math.min(this.removedEntities.size(), Integer.MAX_VALUE);
            final int[] arr2 = new int[integer1];
            final Iterator<Integer> iterator3 = this.removedEntities.iterator();
            int integer2 = 0;
            while (iterator3.hasNext() && integer2 < integer1) {
                arr2[integer2++] = iterator3.next();
                iterator3.remove();
            }
            this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(arr2));
        }
        final Entity entity1 = this.getCameraEntity();
        if (entity1 != this) {
            if (entity1.isAlive()) {
                this.setPositionAnglesAndUpdate(entity1.x, entity1.y, entity1.z, entity1.yaw, entity1.pitch);
                this.getServerWorld().getChunkManager().updateCameraPosition(this);
                if (this.isSneaking()) {
                    this.setCameraEntity(this);
                }
            }
            else {
                this.setCameraEntity(this);
            }
        }
        Criterions.TICK.handle(this);
        if (this.cr != null) {
            Criterions.LEVITATION.handle(this, this.cr, this.age - this.cs);
        }
        this.advancementManager.sendUpdate(this);
    }
    
    public void i() {
        try {
            if (!this.isSpectator() || this.world.isBlockLoaded(new BlockPos(this))) {
                super.tick();
            }
            for (int integer1 = 0; integer1 < this.inventory.getInvSize(); ++integer1) {
                final ItemStack itemStack2 = this.inventory.getInvStack(integer1);
                if (itemStack2.getItem().isMap()) {
                    final Packet<?> packet3 = ((MapItem)itemStack2.getItem()).createMapPacket(itemStack2, this.world, this);
                    if (packet3 != null) {
                        this.networkHandler.sendPacket(packet3);
                    }
                }
            }
            if (this.getHealth() != this.cf || this.cg != this.hungerManager.getFoodLevel() || this.hungerManager.getSaturationLevel() == 0.0f != this.ch) {
                this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
                this.cf = this.getHealth();
                this.cg = this.hungerManager.getFoodLevel();
                this.ch = (this.hungerManager.getSaturationLevel() == 0.0f);
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.bZ) {
                this.bZ = this.getHealth() + this.getAbsorptionAmount();
                this.a(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.bZ));
            }
            if (this.hungerManager.getFoodLevel() != this.ca) {
                this.ca = this.hungerManager.getFoodLevel();
                this.a(ScoreboardCriterion.FOOD, MathHelper.ceil((float)this.ca));
            }
            if (this.getBreath() != this.cb) {
                this.cb = this.getBreath();
                this.a(ScoreboardCriterion.AIR, MathHelper.ceil((float)this.cb));
            }
            if (this.getArmor() != this.cc) {
                this.cc = this.getArmor();
                this.a(ScoreboardCriterion.ARMOR, MathHelper.ceil((float)this.cc));
            }
            if (this.experienceLevel != this.ce) {
                this.ce = this.experienceLevel;
                this.a(ScoreboardCriterion.XP, MathHelper.ceil((float)this.ce));
            }
            if (this.experience != this.cd) {
                this.cd = this.experience;
                this.a(ScoreboardCriterion.LEVEL, MathHelper.ceil((float)this.cd));
            }
            if (this.experienceLevel != this.ci) {
                this.ci = this.experienceLevel;
                this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceLevelProgress, this.experienceLevel, this.experience));
            }
            if (this.age % 20 == 0) {
                Criterions.LOCATION.handle(this);
            }
        }
        catch (Throwable throwable1) {
            final CrashReport crashReport2 = CrashReport.create(throwable1, "Ticking player");
            final CrashReportSection crashReportSection3 = crashReport2.addElement("Player being ticked");
            this.populateCrashReport(crashReportSection3);
            throw new CrashException(crashReport2);
        }
    }
    
    private void a(final ScoreboardCriterion scoreboardCriterion, final int integer) {
        this.getScoreboard().forEachScore(scoreboardCriterion, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.setScore(integer));
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        final boolean boolean2 = this.world.getGameRules().getBoolean("showDeathMessages");
        if (boolean2) {
            final TextComponent textComponent3 = this.getDamageTracker().getDeathMessage();
            this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.DEATH, textComponent3), (future -> {
                if (!future.isSuccess()) {
                    final int integer3 = 256;
                    final String string4 = textComponent3.getStringTruncated(256);
                    final TextComponent textComponent2 = new TranslatableTextComponent("death.attack.message_too_long", new Object[] { new StringTextComponent(string4).applyFormat(TextFormat.o) });
                    final TextComponent textComponent3 = new TranslatableTextComponent("death.attack.even_more_magic", new Object[] { this.getDisplayName() }).modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent2)));
                    this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.DEATH, textComponent3));
                }
            }));
            final AbstractTeam abstractTeam4 = this.getScoreboardTeam();
            if (abstractTeam4 == null || abstractTeam4.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
                this.server.getPlayerManager().sendToAll(textComponent3);
            }
            else if (abstractTeam4.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS) {
                this.server.getPlayerManager().sendToTeam(this, textComponent3);
            }
            else if (abstractTeam4.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDDEN_FOR_TEAM) {
                this.server.getPlayerManager().sendToOtherTeams(this, textComponent3);
            }
        }
        else {
            this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.DEATH));
        }
        this.dropShoulderEntities();
        if (!this.isSpectator()) {
            this.drop(damageSource);
        }
        this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
        final LivingEntity livingEntity3 = this.cK();
        if (livingEntity3 != null) {
            this.incrementStat(Stats.h.getOrCreateStat(livingEntity3.getType()));
            livingEntity3.a(this, this.aY, damageSource);
            if (!this.world.isClient && livingEntity3 instanceof WitherEntity) {
                boolean boolean3 = false;
                if (this.world.getGameRules().getBoolean("mobGriefing")) {
                    final BlockPos blockPos5 = new BlockPos(this.x, this.y, this.z);
                    final BlockState blockState6 = Blocks.bz.getDefaultState();
                    if (this.world.getBlockState(blockPos5).isAir() && blockState6.canPlaceAt(this.world, blockPos5)) {
                        this.world.setBlockState(blockPos5, blockState6, 3);
                        boolean3 = true;
                    }
                }
                if (!boolean3) {
                    final ItemEntity itemEntity5 = new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
                    this.world.spawnEntity(itemEntity5);
                }
            }
        }
        this.incrementStat(Stats.L);
        this.resetStat(Stats.i.getOrCreateStat(Stats.l));
        this.resetStat(Stats.i.getOrCreateStat(Stats.m));
        this.extinguish();
        this.setFlag(0, false);
        this.getDamageTracker().update();
    }
    
    @Override
    public void a(final Entity entity, final int integer, final DamageSource damageSource) {
        if (entity == this) {
            return;
        }
        super.a(entity, integer, damageSource);
        this.addScore(integer);
        final String string4 = this.getEntityName();
        final String string5 = entity.getEntityName();
        this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, string4, ScoreboardPlayerScore::incrementScore);
        if (entity instanceof PlayerEntity) {
            this.incrementStat(Stats.O);
            this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, string4, ScoreboardPlayerScore::incrementScore);
        }
        else {
            this.incrementStat(Stats.M);
        }
        this.a(string4, string5, ScoreboardCriterion.TEAM_KILLS);
        this.a(string5, string4, ScoreboardCriterion.KILLED_BY_TEAMS);
        Criterions.PLAYER_KILLED_ENTITY.handle(this, entity, damageSource);
    }
    
    private void a(final String string1, final String string2, final ScoreboardCriterion[] arr) {
        final Team team4 = this.getScoreboard().getPlayerTeam(string2);
        if (team4 != null) {
            final int integer5 = team4.getColor().getId();
            if (integer5 >= 0 && integer5 < arr.length) {
                this.getScoreboard().forEachScore(arr[integer5], string1, ScoreboardPlayerScore::incrementScore);
            }
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        final boolean boolean3 = this.server.isDedicated() && this.ed() && "fall".equals(source.name);
        if (!boolean3 && this.cj > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (source instanceof EntityDamageSource) {
            final Entity entity4 = source.getAttacker();
            if (entity4 instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity4)) {
                return false;
            }
            if (entity4 instanceof ProjectileEntity) {
                final ProjectileEntity projectileEntity5 = (ProjectileEntity)entity4;
                final Entity entity5 = projectileEntity5.getOwner();
                if (entity5 instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity5)) {
                    return false;
                }
            }
        }
        return super.damage(source, amount);
    }
    
    @Override
    public boolean shouldDamagePlayer(final PlayerEntity playerEntity) {
        return this.ed() && super.shouldDamagePlayer(playerEntity);
    }
    
    private boolean ed() {
        return this.server.isPvpEnabled();
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType newDimension) {
        this.inTeleportationState = true;
        final DimensionType dimensionType2 = this.dimension;
        if (dimensionType2 == DimensionType.c && newDimension == DimensionType.a) {
            this.detach();
            this.getServerWorld().removePlayer(this);
            if (!this.notInAnyWorld) {
                this.notInAnyWorld = true;
                this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, this.seenCredits ? 0.0f : 1.0f));
                this.seenCredits = true;
            }
            return this;
        }
        final ServerWorld serverWorld3 = this.server.getWorld(dimensionType2);
        this.dimension = newDimension;
        final ServerWorld serverWorld4 = this.server.getWorld(newDimension);
        final LevelProperties levelProperties5 = this.world.getLevelProperties();
        this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(newDimension, levelProperties5.getGeneratorType(), this.interactionManager.getGameMode()));
        this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties5.getDifficulty(), levelProperties5.isDifficultyLocked()));
        final PlayerManager playerManager6 = this.server.getPlayerManager();
        playerManager6.sendCommandTree(this);
        serverWorld3.removePlayer(this);
        this.removed = false;
        double double7 = this.x;
        double double8 = this.y;
        double double9 = this.z;
        float float13 = this.pitch;
        float float14 = this.yaw;
        final double double10 = 8.0;
        final float float15 = float14;
        serverWorld3.getProfiler().push("moving");
        if (dimensionType2 == DimensionType.a && newDimension == DimensionType.b) {
            this.enteredNetherPos = new Vec3d(this.x, this.y, this.z);
            double7 /= 8.0;
            double9 /= 8.0;
        }
        else if (dimensionType2 == DimensionType.b && newDimension == DimensionType.a) {
            double7 *= 8.0;
            double9 *= 8.0;
        }
        else if (dimensionType2 == DimensionType.a && newDimension == DimensionType.c) {
            final BlockPos blockPos18 = serverWorld4.getForcedSpawnPoint();
            double7 = blockPos18.getX();
            double8 = blockPos18.getY();
            double9 = blockPos18.getZ();
            float14 = 90.0f;
            float13 = 0.0f;
        }
        this.setPositionAndAngles(double7, double8, double9, float14, float13);
        serverWorld3.getProfiler().pop();
        serverWorld3.getProfiler().push("placing");
        final double double11 = Math.min(-2.9999872E7, serverWorld4.getWorldBorder().getBoundWest() + 16.0);
        final double double12 = Math.min(-2.9999872E7, serverWorld4.getWorldBorder().getBoundNorth() + 16.0);
        final double double13 = Math.min(2.9999872E7, serverWorld4.getWorldBorder().getBoundEast() - 16.0);
        final double double14 = Math.min(2.9999872E7, serverWorld4.getWorldBorder().getBoundSouth() - 16.0);
        double7 = MathHelper.clamp(double7, double11, double13);
        double9 = MathHelper.clamp(double9, double12, double14);
        this.setPositionAndAngles(double7, double8, double9, float14, float13);
        if (newDimension == DimensionType.c) {
            final int integer26 = MathHelper.floor(this.x);
            final int integer27 = MathHelper.floor(this.y) - 1;
            final int integer28 = MathHelper.floor(this.z);
            final int integer29 = 1;
            final int integer30 = 0;
            for (int integer31 = -2; integer31 <= 2; ++integer31) {
                for (int integer32 = -2; integer32 <= 2; ++integer32) {
                    for (int integer33 = -1; integer33 < 3; ++integer33) {
                        final int integer34 = integer26 + integer32 * 1 + integer31 * 0;
                        final int integer35 = integer27 + integer33;
                        final int integer36 = integer28 + integer32 * 0 - integer31 * 1;
                        final boolean boolean37 = integer33 < 0;
                        serverWorld4.setBlockState(new BlockPos(integer34, integer35, integer36), boolean37 ? Blocks.bJ.getDefaultState() : Blocks.AIR.getDefaultState());
                    }
                }
            }
            this.setPositionAndAngles(integer26, integer27, integer28, float14, 0.0f);
            this.setVelocity(Vec3d.ZERO);
        }
        else if (!serverWorld4.getPortalForcer().usePortal(this, float15)) {
            serverWorld4.getPortalForcer().createPortal(this);
            serverWorld4.getPortalForcer().usePortal(this, float15);
        }
        serverWorld3.getProfiler().pop();
        this.setWorld(serverWorld4);
        serverWorld4.b(this);
        this.b(serverWorld3);
        this.networkHandler.requestTeleport(this.x, this.y, this.z, float14, float13);
        this.interactionManager.setWorld(serverWorld4);
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        playerManager6.sendWorldInfo(this, serverWorld4);
        playerManager6.e(this);
        for (final StatusEffectInstance statusEffectInstance27 : this.getStatusEffects()) {
            this.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance27));
        }
        this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
        this.ci = -1;
        this.cf = -1.0f;
        this.cg = -1;
        return this;
    }
    
    private void b(final ServerWorld serverWorld) {
        final DimensionType dimensionType2 = serverWorld.dimension.getType();
        final DimensionType dimensionType3 = this.world.dimension.getType();
        Criterions.CHANGED_DIMENSION.handle(this, dimensionType2, dimensionType3);
        if (dimensionType2 == DimensionType.b && dimensionType3 == DimensionType.a && this.enteredNetherPos != null) {
            Criterions.NETHER_TRAVEL.handle(this, this.enteredNetherPos);
        }
        if (dimensionType3 != DimensionType.b) {
            this.enteredNetherPos = null;
        }
    }
    
    @Override
    public boolean a(final ServerPlayerEntity serverPlayerEntity) {
        if (serverPlayerEntity.isSpectator()) {
            return this.getCameraEntity() == this;
        }
        return !this.isSpectator() && super.a(serverPlayerEntity);
    }
    
    private void sendBlockEntityUpdate(final BlockEntity blockEntity) {
        if (blockEntity != null) {
            final BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket2 = blockEntity.toUpdatePacket();
            if (blockEntityUpdateS2CPacket2 != null) {
                this.networkHandler.sendPacket(blockEntityUpdateS2CPacket2);
            }
        }
    }
    
    @Override
    public void sendPickup(final Entity item, final int count) {
        super.sendPickup(item, count);
        this.container.sendContentUpdates();
    }
    
    @Override
    public Either<SleepFailureReason, net.minecraft.util.Void> trySleep(final BlockPos blockPos) {
        return (Either<SleepFailureReason, net.minecraft.util.Void>)super.trySleep(blockPos).ifRight(void1 -> {
            this.incrementStat(Stats.an);
            Criterions.SLEPT_IN_BED.handle(this);
        });
    }
    
    @Override
    public void wakeUp(final boolean boolean1, final boolean boolean2, final boolean boolean3) {
        if (this.isSleeping()) {
            this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
        }
        super.wakeUp(boolean1, boolean2, boolean3);
        if (this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }
    
    @Override
    public boolean startRiding(final Entity entity, final boolean boolean2) {
        final Entity entity2 = this.getVehicle();
        if (!super.startRiding(entity, boolean2)) {
            return false;
        }
        final Entity entity3 = this.getVehicle();
        if (entity3 != entity2 && this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
        }
        return true;
    }
    
    @Override
    public void stopRiding() {
        final Entity entity1 = this.getVehicle();
        super.stopRiding();
        final Entity entity2 = this.getVehicle();
        if (entity2 != entity1 && this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }
    
    @Override
    public boolean isInvulnerableTo(final DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || (this.abilities.invulnerable && damageSource == DamageSource.WITHER);
    }
    
    @Override
    protected void fall(final double heightDifference, final boolean onGround, final BlockState blockState, final BlockPos blockPos) {
    }
    
    @Override
    protected void applyFrostWalker(final BlockPos pos) {
        if (!this.isSpectator()) {
            super.applyFrostWalker(pos);
        }
    }
    
    public void a(final double double1, final boolean boolean3) {
        final int integer4 = MathHelper.floor(this.x);
        final int integer5 = MathHelper.floor(this.y - 0.20000000298023224);
        final int integer6 = MathHelper.floor(this.z);
        BlockPos blockPos7 = new BlockPos(integer4, integer5, integer6);
        if (!this.world.isBlockLoaded(blockPos7)) {
            return;
        }
        BlockState blockState8 = this.world.getBlockState(blockPos7);
        if (blockState8.isAir()) {
            final BlockPos blockPos8 = blockPos7.down();
            final BlockState blockState9 = this.world.getBlockState(blockPos8);
            final Block block11 = blockState9.getBlock();
            if (block11.matches(BlockTags.G) || block11.matches(BlockTags.z) || block11 instanceof FenceGateBlock) {
                blockPos7 = blockPos8;
                blockState8 = blockState9;
            }
        }
        super.fall(double1, boolean3, blockState8, blockPos7);
    }
    
    @Override
    public void openEditSignScreen(final SignBlockEntity signBlockEntity) {
        signBlockEntity.setEditor(this);
        this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(signBlockEntity.getPos()));
    }
    
    private void incrementContainerSyncId() {
        this.containerSyncId = this.containerSyncId % 100 + 1;
    }
    
    @Override
    public OptionalInt openContainer(@Nullable final NameableContainerProvider nameableContainerProvider) {
        if (nameableContainerProvider == null) {
            return OptionalInt.empty();
        }
        if (this.container != this.playerContainer) {
            this.closeContainer();
        }
        this.incrementContainerSyncId();
        final Container container2 = nameableContainerProvider.createMenu(this.containerSyncId, this.inventory, this);
        if (container2 == null) {
            if (this.isSpectator()) {
                this.addChatMessage(new TranslatableTextComponent("container.spectatorCantOpen", new Object[0]).applyFormat(TextFormat.m), true);
            }
            return OptionalInt.empty();
        }
        this.networkHandler.sendPacket(new OpenContainerPacket(container2.syncId, container2.getType(), nameableContainerProvider.getDisplayName()));
        container2.addListener(this);
        this.container = container2;
        return OptionalInt.of(this.containerSyncId);
    }
    
    @Override
    public void sendTradeOffers(final int syncId, final TraderOfferList traderOfferList, final int integer3, final int integer4, final boolean boolean5) {
        this.networkHandler.sendPacket(new SetTradeOffersPacket(syncId, traderOfferList, integer3, integer4, boolean5));
    }
    
    @Override
    public void openHorseInventory(final HorseBaseEntity horseBaseEntity, final Inventory inventory) {
        if (this.container != this.playerContainer) {
            this.closeContainer();
        }
        this.incrementContainerSyncId();
        this.networkHandler.sendPacket(new GuiOpenS2CPacket(this.containerSyncId, inventory.getInvSize(), horseBaseEntity.getEntityId()));
        (this.container = new HorseContainer(this.containerSyncId, this.inventory, inventory, horseBaseEntity)).addListener(this);
    }
    
    @Override
    public void openEditBookScreen(final ItemStack itemStack, final Hand hand) {
        final Item item3 = itemStack.getItem();
        if (item3 == Items.nE) {
            if (WrittenBookItem.resolve(itemStack, this.getCommandSource(), this)) {
                this.container.sendContentUpdates();
            }
            this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
        }
    }
    
    @Override
    public void openCommandBlockScreen(final CommandBlockBlockEntity commandBlockBlockEntity) {
        commandBlockBlockEntity.setNeedsUpdatePacket(true);
        this.sendBlockEntityUpdate(commandBlockBlockEntity);
    }
    
    @Override
    public void onContainerSlotUpdate(final Container container, final int slotId, final ItemStack itemStack) {
        if (container.getSlot(slotId) instanceof CraftingResultSlot) {
            return;
        }
        if (container == this.playerContainer) {
            Criterions.INVENTORY_CHANGED.handle(this, this.inventory);
        }
        if (this.e) {
            return;
        }
        this.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(container.syncId, slotId, itemStack));
    }
    
    public void openContainer(final Container container) {
        this.onContainerRegistered(container, container.getStacks());
    }
    
    @Override
    public void onContainerRegistered(final Container container, final DefaultedList<ItemStack> defaultedList) {
        this.networkHandler.sendPacket(new InventoryS2CPacket(container.syncId, defaultedList));
        this.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }
    
    @Override
    public void onContainerPropertyUpdate(final Container container, final int propertyId, final int integer3) {
        this.networkHandler.sendPacket(new GuiUpdateS2CPacket(container.syncId, propertyId, integer3));
    }
    
    public void closeContainer() {
        this.networkHandler.sendPacket(new GuiCloseS2CPacket(this.container.syncId));
        this.m();
    }
    
    public void l() {
        if (this.e) {
            return;
        }
        this.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }
    
    public void m() {
        this.container.close(this);
        this.container = this.playerContainer;
    }
    
    public void a(final float float1, final float float2, final boolean boolean3, final boolean boolean4) {
        if (this.hasVehicle()) {
            if (float1 >= -1.0f && float1 <= 1.0f) {
                this.sidewaysSpeed = float1;
            }
            if (float2 >= -1.0f && float2 <= 1.0f) {
                this.forwardSpeed = float2;
            }
            this.jumping = boolean3;
            this.setSneaking(boolean4);
        }
    }
    
    @Override
    public void increaseStat(final Stat<?> stat, final int amount) {
        this.statHandler.increaseStat(this, stat, amount);
        this.getScoreboard().forEachScore(stat, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.incrementScore(amount));
    }
    
    @Override
    public void resetStat(final Stat<?> stat) {
        this.statHandler.setStat(this, stat, 0);
        this.getScoreboard().forEachScore(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
    }
    
    @Override
    public int unlockRecipes(final Collection<Recipe<?>> recipes) {
        return this.recipeBook.unlockRecipes(recipes, this);
    }
    
    @Override
    public void unlockRecipes(final Identifier[] ids) {
        final List<Recipe<?>> list2 = Lists.newArrayList();
        for (final Identifier identifier6 : ids) {
            this.server.getRecipeManager().get(identifier6).ifPresent(list2::add);
        }
        this.unlockRecipes(list2);
    }
    
    @Override
    public int lockRecipes(final Collection<Recipe<?>> recipes) {
        return this.recipeBook.lockRecipes(recipes, this);
    }
    
    @Override
    public void addExperience(final int integer) {
        super.addExperience(integer);
        this.ci = -1;
    }
    
    public void n() {
        this.ct = true;
        this.removeAllPassengers();
        if (this.isSleeping()) {
            this.wakeUp(true, false, false);
        }
    }
    
    public boolean o() {
        return this.ct;
    }
    
    public void p() {
        this.cf = -1.0E8f;
    }
    
    @Override
    public void addChatMessage(final TextComponent message, final boolean boolean2) {
        this.networkHandler.sendPacket(new ChatMessageS2CPacket(message, boolean2 ? ChatMessageType.c : ChatMessageType.a));
    }
    
    @Override
    protected void q() {
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
            super.q();
        }
    }
    
    @Override
    public void lookAt(final EntityAnchorArgumentType.EntityAnchor anchor, final Vec3d target) {
        super.lookAt(anchor, target);
        this.networkHandler.sendPacket(new LookAtS2CPacket(anchor, target.x, target.y, target.z));
    }
    
    public void a(final EntityAnchorArgumentType.EntityAnchor entityAnchor1, final Entity entity, final EntityAnchorArgumentType.EntityAnchor entityAnchor3) {
        final Vec3d vec3d4 = entityAnchor3.positionAt(entity);
        super.lookAt(entityAnchor1, vec3d4);
        this.networkHandler.sendPacket(new LookAtS2CPacket(entityAnchor1, entity, entityAnchor3));
    }
    
    public void copyFrom(final ServerPlayerEntity oldPlayer, final boolean alive) {
        if (alive) {
            this.inventory.clone(oldPlayer.inventory);
            this.setHealth(oldPlayer.getHealth());
            this.hungerManager = oldPlayer.hungerManager;
            this.experience = oldPlayer.experience;
            this.experienceLevel = oldPlayer.experienceLevel;
            this.experienceLevelProgress = oldPlayer.experienceLevelProgress;
            this.setScore(oldPlayer.getScore());
            this.lastPortalPosition = oldPlayer.lastPortalPosition;
            this.am = oldPlayer.am;
            this.an = oldPlayer.an;
        }
        else if (this.world.getGameRules().getBoolean("keepInventory") || oldPlayer.isSpectator()) {
            this.inventory.clone(oldPlayer.inventory);
            this.experience = oldPlayer.experience;
            this.experienceLevel = oldPlayer.experienceLevel;
            this.experienceLevelProgress = oldPlayer.experienceLevelProgress;
            this.setScore(oldPlayer.getScore());
        }
        this.enchantmentTableSeed = oldPlayer.enchantmentTableSeed;
        this.enderChestInventory = oldPlayer.enderChestInventory;
        this.getDataTracker().<Byte>set(ServerPlayerEntity.PLAYER_MODEL_BIT_MASK, (Byte)oldPlayer.getDataTracker().<T>get((TrackedData<T>)ServerPlayerEntity.PLAYER_MODEL_BIT_MASK));
        this.ci = -1;
        this.cf = -1.0f;
        this.cg = -1;
        this.recipeBook.copyFrom(oldPlayer.recipeBook);
        this.removedEntities.addAll(oldPlayer.removedEntities);
        this.seenCredits = oldPlayer.seenCredits;
        this.enteredNetherPos = oldPlayer.enteredNetherPos;
        this.setShoulderEntityLeft(oldPlayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(oldPlayer.getShoulderEntityRight());
    }
    
    @Override
    protected void a(final StatusEffectInstance statusEffectInstance) {
        super.a(statusEffectInstance);
        this.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
        if (statusEffectInstance.getEffectType() == StatusEffects.y) {
            this.cs = this.age;
            this.cr = new Vec3d(this.x, this.y, this.z);
        }
        Criterions.EFFECTS_CHANGED.handle(this);
    }
    
    @Override
    protected void a(final StatusEffectInstance statusEffectInstance, final boolean boolean2) {
        super.a(statusEffectInstance, boolean2);
        this.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
        Criterions.EFFECTS_CHANGED.handle(this);
    }
    
    @Override
    protected void b(final StatusEffectInstance statusEffectInstance) {
        super.b(statusEffectInstance);
        this.networkHandler.sendPacket(new RemoveEntityEffectS2CPacket(this.getEntityId(), statusEffectInstance.getEffectType()));
        if (statusEffectInstance.getEffectType() == StatusEffects.y) {
            this.cr = null;
        }
        Criterions.EFFECTS_CHANGED.handle(this);
    }
    
    @Override
    public void requestTeleport(final double double1, final double double3, final double double5) {
        this.networkHandler.requestTeleport(double1, double3, double5, this.yaw, this.pitch);
    }
    
    @Override
    public void addCritParticles(final Entity entity) {
        this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(entity, 4));
    }
    
    @Override
    public void addEnchantedHitParticles(final Entity entity) {
        this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(entity, 5));
    }
    
    @Override
    public void sendAbilitiesUpdate() {
        if (this.networkHandler == null) {
            return;
        }
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        this.updatePotionVisibility();
    }
    
    public ServerWorld getServerWorld() {
        return (ServerWorld)this.world;
    }
    
    @Override
    public void setGameMode(final GameMode gameMode) {
        this.interactionManager.setGameMode(gameMode);
        this.networkHandler.sendPacket(new GameStateChangeS2CPacket(3, (float)gameMode.getId()));
        if (gameMode == GameMode.e) {
            this.dropShoulderEntities();
            this.stopRiding();
        }
        else {
            this.setCameraEntity(this);
        }
        this.sendAbilitiesUpdate();
        this.dg();
    }
    
    @Override
    public boolean isSpectator() {
        return this.interactionManager.getGameMode() == GameMode.e;
    }
    
    @Override
    public boolean isCreative() {
        return this.interactionManager.getGameMode() == GameMode.c;
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
        this.sendChatMessage(message, ChatMessageType.b);
    }
    
    public void sendChatMessage(final TextComponent textComponent, final ChatMessageType chatMessageType) {
        this.networkHandler.sendPacket(new ChatMessageS2CPacket(textComponent, chatMessageType), (future -> {
            if (!future.isSuccess() && (chatMessageType == ChatMessageType.c || chatMessageType == ChatMessageType.b)) {
                final int integer4 = 256;
                final String string5 = textComponent.getStringTruncated(256);
                final TextComponent textComponent2 = new StringTextComponent(string5).applyFormat(TextFormat.o);
                this.networkHandler.sendPacket(new ChatMessageS2CPacket(new TranslatableTextComponent("multiplayer.message_not_delivered", new Object[] { textComponent2 }).applyFormat(TextFormat.m), ChatMessageType.b));
            }
        }));
    }
    
    public String getServerBrand() {
        String string1 = this.networkHandler.client.getAddress().toString();
        string1 = string1.substring(string1.indexOf("/") + 1);
        string1 = string1.substring(0, string1.indexOf(":"));
        return string1;
    }
    
    public void setClientSettings(final ClientSettingsC2SPacket clientSettingsC2SPacket) {
        this.clientLanguage = clientSettingsC2SPacket.getLanguage();
        this.clientChatVisibility = clientSettingsC2SPacket.getChatVisibility();
        this.cl = clientSettingsC2SPacket.e();
        this.getDataTracker().<Byte>set(ServerPlayerEntity.PLAYER_MODEL_BIT_MASK, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
        this.getDataTracker().<Byte>set(ServerPlayerEntity.MAIN_HAND, (byte)((clientSettingsC2SPacket.getMainHand() != AbsoluteHand.a) ? 1 : 0));
    }
    
    public ChatVisibility getClientChatVisibility() {
        return this.clientChatVisibility;
    }
    
    public void a(final String string1, final String string2) {
        this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(string1, string2));
    }
    
    @Override
    protected int getPermissionLevel() {
        return this.server.getPermissionLevel(this.getGameProfile());
    }
    
    public void updateLastActionTime() {
        this.lastActionTime = SystemUtil.getMeasuringTimeMs();
    }
    
    public ServerStatHandler getStatHandler() {
        return this.statHandler;
    }
    
    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }
    
    public void onStoppedTracking(final Entity entity) {
        if (entity instanceof PlayerEntity) {
            this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(new int[] { entity.getEntityId() }));
        }
        else {
            this.removedEntities.add(entity.getEntityId());
        }
    }
    
    public void onStartedTracking(final Entity entity) {
        this.removedEntities.remove(entity.getEntityId());
    }
    
    @Override
    protected void updatePotionVisibility() {
        if (this.isSpectator()) {
            this.clearPotionSwirls();
            this.setInvisible(true);
        }
        else {
            super.updatePotionVisibility();
        }
    }
    
    public Entity getCameraEntity() {
        return (this.cameraEntity == null) ? this : this.cameraEntity;
    }
    
    public void setCameraEntity(final Entity entity) {
        final Entity entity2 = this.getCameraEntity();
        this.cameraEntity = ((entity == null) ? this : entity);
        if (entity2 != this.cameraEntity) {
            this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
            this.requestTeleport(this.cameraEntity.x, this.cameraEntity.y, this.cameraEntity.z);
        }
    }
    
    @Override
    protected void tickPortalCooldown() {
        if (this.portalCooldown > 0 && !this.inTeleportationState) {
            --this.portalCooldown;
        }
    }
    
    @Override
    public void attack(final Entity entity) {
        if (this.interactionManager.getGameMode() == GameMode.e) {
            this.setCameraEntity(entity);
        }
        else {
            super.attack(entity);
        }
    }
    
    public long getLastActionTime() {
        return this.lastActionTime;
    }
    
    @Nullable
    public TextComponent G() {
        return null;
    }
    
    @Override
    public void swingHand(final Hand hand) {
        super.swingHand(hand);
        this.dZ();
    }
    
    public boolean isInTeleportationState() {
        return this.inTeleportationState;
    }
    
    public void onTeleportationDone() {
        this.inTeleportationState = false;
    }
    
    public void J() {
        this.setFlag(7, true);
    }
    
    public void K() {
        this.setFlag(7, true);
        this.setFlag(7, false);
    }
    
    public PlayerAdvancementTracker getAdvancementManager() {
        return this.advancementManager;
    }
    
    public void teleport(final ServerWorld serverWorld, final double double2, final double double4, final double double6, final float float8, final float float9) {
        this.setCameraEntity(this);
        this.stopRiding();
        if (serverWorld == this.world) {
            this.networkHandler.requestTeleport(double2, double4, double6, float8, float9);
        }
        else {
            final ServerWorld serverWorld2 = this.getServerWorld();
            this.dimension = serverWorld.dimension.getType();
            final LevelProperties levelProperties11 = serverWorld.getLevelProperties();
            this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(this.dimension, levelProperties11.getGeneratorType(), this.interactionManager.getGameMode()));
            this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties11.getDifficulty(), levelProperties11.isDifficultyLocked()));
            this.server.getPlayerManager().sendCommandTree(this);
            serverWorld2.removePlayer(this);
            this.removed = false;
            this.setPositionAndAngles(double2, double4, double6, float8, float9);
            this.setWorld(serverWorld);
            serverWorld.a(this);
            this.b(serverWorld2);
            this.networkHandler.requestTeleport(double2, double4, double6, float8, float9);
            this.interactionManager.setWorld(serverWorld);
            this.server.getPlayerManager().sendWorldInfo(this, serverWorld);
            this.server.getPlayerManager().e(this);
        }
    }
    
    public void sendInitialChunkPackets(final ChunkPos chunkPos, final Packet<?> packet2, final Packet<?> packet3) {
        this.networkHandler.sendPacket(packet3);
        this.networkHandler.sendPacket(packet2);
    }
    
    public void sendUnloadChunkPacket(final ChunkPos chunkPos) {
        this.networkHandler.sendPacket(new UnloadChunkS2CPacket(chunkPos.x, chunkPos.z));
    }
    
    public ChunkSectionPos getCameraPosition() {
        return this.cameraPosition;
    }
    
    public void setCameraPosition(final ChunkSectionPos cameraPosition) {
        this.cameraPosition = cameraPosition;
    }
    
    @Override
    public void playSound(final SoundEvent soundEvent, final SoundCategory soundCategory, final float float3, final float float4) {
        this.networkHandler.sendPacket(new PlaySoundS2CPacket(soundEvent, soundCategory, this.x, this.y, this.z, float3, float4));
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new PlayerSpawnS2CPacket(this);
    }
    
    @Override
    public ItemEntity dropItem(final ItemStack stack, final boolean boolean2, final boolean boolean3) {
        final ItemEntity itemEntity4 = super.dropItem(stack, boolean2, boolean3);
        if (itemEntity4 == null) {
            return null;
        }
        this.world.spawnEntity(itemEntity4);
        final ItemStack itemStack5 = itemEntity4.getStack();
        if (boolean3) {
            if (!itemStack5.isEmpty()) {
                this.increaseStat(Stats.f.getOrCreateStat(itemStack5.getItem()), stack.getAmount());
            }
            this.incrementStat(Stats.D);
        }
        return itemEntity4;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
