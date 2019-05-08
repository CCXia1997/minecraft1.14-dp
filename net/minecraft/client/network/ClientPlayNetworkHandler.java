package net.minecraft.client.network;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.options.ServerList;
import javax.annotation.Nullable;
import java.util.Collection;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.village.TraderOfferList;
import net.minecraft.container.MerchantContainer;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.LightType;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.network.packet.CraftResponseS2CPacket;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.util.math.MutableIntBoundingBox;
import com.google.common.collect.Lists;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.gui.WrittenBookScreen;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.options.ServerEntry;
import java.io.UnsupportedEncodingException;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.tag.EntityTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.tag.BlockTags;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.client.gui.StatsListener;
import net.minecraft.stat.Stat;
import net.minecraft.client.network.packet.StatisticsS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.client.search.SearchableContainer;
import java.util.function.Consumer;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.search.SearchManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.item.map.MapState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.gui.menu.DemoScreen;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.world.GameMode;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.gui.CommandBlockScreen;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.container.Container;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.item.ItemGroup;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryScreen;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.gui.ContainerScreenRegistry;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.gui.container.HorseScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.HorseContainer;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.gui.ingame.DeathScreen;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.client.audio.GuardianAttackSoundInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.menu.DisconnectedScreen;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.entity.data.DataTracker;
import java.util.List;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.entity.LightningEntity;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.RidingMinecartSoundInstance;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TNTMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.world.World;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.util.PacketByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.client.gui.menu.DownloadingTerrainScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.stat.StatHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.network.Packet;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import com.google.common.collect.Maps;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Random;
import net.minecraft.tag.TagManager;
import java.util.UUID;
import java.util.Map;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ClientPlayPacketListener;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandler implements ClientPlayPacketListener
{
    private static final Logger LOGGER;
    private final ClientConnection connection;
    private final GameProfile profile;
    private final Screen loginScreen;
    private MinecraftClient client;
    private ClientWorld world;
    private boolean g;
    private final Map<UUID, PlayerListEntry> playerListEntries;
    private final ClientAdvancementManager advancementHandler;
    private final ClientCommandSource commandSource;
    private TagManager tagManager;
    private final QueryHandler queryHandler;
    private int chunkLoadDistance;
    private final Random random;
    private CommandDispatcher<CommandSource> commandDispatcher;
    private final RecipeManager recipeManager;
    private final UUID sessionId;
    
    public ClientPlayNetworkHandler(final MinecraftClient client, final Screen screen, final ClientConnection clientConnection, final GameProfile gameProfile) {
        this.playerListEntries = Maps.newHashMap();
        this.tagManager = new TagManager();
        this.queryHandler = new QueryHandler(this);
        this.chunkLoadDistance = 3;
        this.random = new Random();
        this.commandDispatcher = (CommandDispatcher<CommandSource>)new CommandDispatcher();
        this.recipeManager = new RecipeManager();
        this.sessionId = UUID.randomUUID();
        this.client = client;
        this.loginScreen = screen;
        this.connection = clientConnection;
        this.profile = gameProfile;
        this.advancementHandler = new ClientAdvancementManager(client);
        this.commandSource = new ClientCommandSource(this, client);
    }
    
    public ClientCommandSource getCommandSource() {
        return this.commandSource;
    }
    
    public void clearWorld() {
        this.world = null;
    }
    
    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }
    
    @Override
    public void onGameJoin(final GameJoinS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
        this.chunkLoadDistance = packet.getChunkLoadDistance();
        this.world = new ClientWorld(this, new LevelInfo(0L, packet.getGameMode(), false, packet.isHardcore(), packet.getGeneratorType()), packet.getDimension(), this.chunkLoadDistance, this.client.getProfiler(), this.client.worldRenderer);
        this.client.joinWorld(this.world);
        if (this.client.player == null) {
            this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook(this.world.getRecipeManager()));
            this.client.player.yaw = -180.0f;
            if (this.client.getServer() != null) {
                this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
            }
        }
        this.client.debugRenderer.a();
        this.client.player.X();
        final int integer2 = packet.getEntityId();
        this.world.addPlayer(integer2, this.client.player);
        this.client.player.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities(this.client.player);
        this.client.cameraEntity = this.client.player;
        this.client.player.dimension = packet.getDimension();
        this.client.openScreen(new DownloadingTerrainScreen());
        this.client.player.setEntityId(integer2);
        this.client.player.setReducedDebugInfo(packet.hasReducedDebugInfo());
        this.client.interactionManager.setGameMode(packet.getGameMode());
        this.client.options.onPlayerModelPartChange();
        this.connection.send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
        this.client.getGame().onStartGameSession();
    }
    
    @Override
    public void onEntitySpawn(final EntitySpawnS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final double double2 = packet.getX();
        final double double3 = packet.getY();
        final double double4 = packet.getZ();
        final EntityType<?> entityType9 = packet.getEntityTypeId();
        Entity entity8;
        if (entityType9 == EntityType.CHEST_MINECART) {
            entity8 = new ChestMinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.FURNACE_MINECART) {
            entity8 = new FurnaceMinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.TNT_MINECART) {
            entity8 = new TNTMinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.SPAWNER_MINECART) {
            entity8 = new MobSpawnerMinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.HOPPER_MINECART) {
            entity8 = new HopperMinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.COMMAND_BLOCK_MINECART) {
            entity8 = new CommandBlockMinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.MINECART) {
            entity8 = new MinecartEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.FISHING_BOBBER) {
            final Entity entity9 = this.world.getEntityById(packet.getEntityData());
            if (entity9 instanceof PlayerEntity) {
                entity8 = new FishHookEntity(this.world, (PlayerEntity)entity9, double2, double3, double4);
            }
            else {
                entity8 = null;
            }
        }
        else if (entityType9 == EntityType.ARROW) {
            entity8 = new ArrowEntity(this.world, double2, double3, double4);
            final Entity entity9 = this.world.getEntityById(packet.getEntityData());
            if (entity9 != null) {
                ((ProjectileEntity)entity8).setOwner(entity9);
            }
        }
        else if (entityType9 == EntityType.SPECTRAL_ARROW) {
            entity8 = new SpectralArrowEntity(this.world, double2, double3, double4);
            final Entity entity9 = this.world.getEntityById(packet.getEntityData());
            if (entity9 != null) {
                ((ProjectileEntity)entity8).setOwner(entity9);
            }
        }
        else if (entityType9 == EntityType.TRIDENT) {
            entity8 = new TridentEntity(this.world, double2, double3, double4);
            final Entity entity9 = this.world.getEntityById(packet.getEntityData());
            if (entity9 != null) {
                ((ProjectileEntity)entity8).setOwner(entity9);
            }
        }
        else if (entityType9 == EntityType.SNOWBALL) {
            entity8 = new SnowballEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.LLAMA_SPIT) {
            entity8 = new LlamaSpitEntity(this.world, double2, double3, double4, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
        }
        else if (entityType9 == EntityType.ITEM_FRAME) {
            entity8 = new ItemFrameEntity(this.world, new BlockPos(double2, double3, double4), Direction.byId(packet.getEntityData()));
        }
        else if (entityType9 == EntityType.LEASH_KNOT) {
            entity8 = new LeadKnotEntity(this.world, new BlockPos(double2, double3, double4));
        }
        else if (entityType9 == EntityType.ENDER_PEARL) {
            entity8 = new ThrownEnderpearlEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.EYE_OF_ENDER) {
            entity8 = new EnderEyeEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.FIREWORK_ROCKET) {
            entity8 = new FireworkEntity(this.world, double2, double3, double4, ItemStack.EMPTY);
        }
        else if (entityType9 == EntityType.FIREBALL) {
            entity8 = new FireballEntity(this.world, double2, double3, double4, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
        }
        else if (entityType9 == EntityType.DRAGON_FIREBALL) {
            entity8 = new DragonFireballEntity(this.world, double2, double3, double4, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
        }
        else if (entityType9 == EntityType.SMALL_FIREBALL) {
            entity8 = new SmallFireballEntity(this.world, double2, double3, double4, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
        }
        else if (entityType9 == EntityType.WITHER_SKULL) {
            entity8 = new ExplodingWitherSkullEntity(this.world, double2, double3, double4, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
        }
        else if (entityType9 == EntityType.SHULKER_BULLET) {
            entity8 = new ShulkerBulletEntity(this.world, double2, double3, double4, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
        }
        else if (entityType9 == EntityType.EGG) {
            entity8 = new ThrownEggEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.EVOKER_FANGS) {
            entity8 = new EvokerFangsEntity(this.world, double2, double3, double4, 0.0f, 0, null);
        }
        else if (entityType9 == EntityType.POTION) {
            entity8 = new ThrownPotionEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.EXPERIENCE_BOTTLE) {
            entity8 = new ThrownExperienceBottleEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.BOAT) {
            entity8 = new BoatEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.TNT) {
            entity8 = new PrimedTntEntity(this.world, double2, double3, double4, null);
        }
        else if (entityType9 == EntityType.ARMOR_STAND) {
            entity8 = new ArmorStandEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.END_CRYSTAL) {
            entity8 = new EnderCrystalEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.ITEM) {
            entity8 = new ItemEntity(this.world, double2, double3, double4);
        }
        else if (entityType9 == EntityType.FALLING_BLOCK) {
            entity8 = new FallingBlockEntity(this.world, double2, double3, double4, Block.getStateFromRawId(packet.getEntityData()));
        }
        else if (entityType9 == EntityType.AREA_EFFECT_CLOUD) {
            entity8 = new AreaEffectCloudEntity(this.world, double2, double3, double4);
        }
        else {
            entity8 = null;
        }
        if (entity8 != null) {
            final int integer10 = packet.getId();
            entity8.b(double2, double3, double4);
            entity8.pitch = packet.getPitch() * 360 / 256.0f;
            entity8.yaw = packet.getYaw() * 360 / 256.0f;
            entity8.setEntityId(integer10);
            entity8.setUuid(packet.getUuid());
            this.world.addEntity(integer10, entity8);
            if (entity8 instanceof AbstractMinecartEntity) {
                this.client.getSoundManager().play(new RidingMinecartSoundInstance((AbstractMinecartEntity)entity8));
            }
        }
    }
    
    @Override
    public void onExperienceOrbSpawn(final ExperienceOrbSpawnS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final double double2 = packet.getX();
        final double double3 = packet.getY();
        final double double4 = packet.getZ();
        final Entity entity8 = new ExperienceOrbEntity(this.world, double2, double3, double4, packet.getExperience());
        entity8.b(double2, double3, double4);
        entity8.yaw = 0.0f;
        entity8.pitch = 0.0f;
        entity8.setEntityId(packet.getId());
        this.world.addEntity(packet.getId(), entity8);
    }
    
    @Override
    public void onEntitySpawnGlobal(final EntitySpawnGlobalS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final double double2 = packet.getX();
        final double double3 = packet.getY();
        final double double4 = packet.getZ();
        if (packet.getEntityTypeId() == 1) {
            final LightningEntity lightningEntity8 = new LightningEntity(this.world, double2, double3, double4, false);
            lightningEntity8.b(double2, double3, double4);
            lightningEntity8.yaw = 0.0f;
            lightningEntity8.pitch = 0.0f;
            lightningEntity8.setEntityId(packet.getId());
            this.world.addLightning(lightningEntity8);
        }
    }
    
    @Override
    public void onPaintingSpawn(final PaintingSpawnS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PaintingEntity paintingEntity2 = new PaintingEntity(this.world, packet.getPos(), packet.getFacing(), packet.getMotive());
        paintingEntity2.setEntityId(packet.getId());
        paintingEntity2.setUuid(packet.getPaintingUuid());
        this.world.addEntity(packet.getId(), paintingEntity2);
    }
    
    @Override
    public void onVelocityUpdate(final EntityVelocityUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getId());
        if (entity2 == null) {
            return;
        }
        entity2.setVelocityClient(packet.getVelocityX() / 8000.0, packet.getVelocityY() / 8000.0, packet.getVelocityZ() / 8000.0);
    }
    
    @Override
    public void onEntityTrackerUpdate(final EntityTrackerUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.id());
        if (entity2 != null && packet.getTrackedValues() != null) {
            entity2.getDataTracker().writeUpdatedEntries(packet.getTrackedValues());
        }
    }
    
    @Override
    public void onPlayerSpawn(final PlayerSpawnS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final double double2 = packet.getX();
        final double double3 = packet.getY();
        final double double4 = packet.getZ();
        final float float8 = packet.getYaw() * 360 / 256.0f;
        final float float9 = packet.getPitch() * 360 / 256.0f;
        final int integer10 = packet.getId();
        final OtherClientPlayerEntity otherClientPlayerEntity11 = new OtherClientPlayerEntity(this.client.world, this.getPlayerListEntry(packet.getPlayerUuid()).getProfile());
        otherClientPlayerEntity11.setEntityId(integer10);
        otherClientPlayerEntity11.prevX = double2;
        otherClientPlayerEntity11.prevRenderX = double2;
        otherClientPlayerEntity11.prevY = double3;
        otherClientPlayerEntity11.prevRenderY = double3;
        otherClientPlayerEntity11.prevZ = double4;
        otherClientPlayerEntity11.b(double2, double3, otherClientPlayerEntity11.prevRenderZ = double4);
        otherClientPlayerEntity11.setPositionAnglesAndUpdate(double2, double3, double4, float8, float9);
        this.world.addPlayer(integer10, otherClientPlayerEntity11);
        final List<DataTracker.Entry<?>> list12 = packet.getTrackedValues();
        if (list12 != null) {
            otherClientPlayerEntity11.getDataTracker().writeUpdatedEntries(list12);
        }
    }
    
    @Override
    public void onEntityPosition(final EntityPositionS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getId());
        if (entity2 == null) {
            return;
        }
        final double double3 = packet.getX();
        final double double4 = packet.getY();
        final double double5 = packet.getZ();
        entity2.b(double3, double4, double5);
        if (!entity2.isLogicalSideForUpdatingMovement()) {
            final float float9 = packet.getYaw() * 360 / 256.0f;
            final float float10 = packet.getPitch() * 360 / 256.0f;
            if (Math.abs(entity2.x - double3) >= 0.03125 || Math.abs(entity2.y - double4) >= 0.015625 || Math.abs(entity2.z - double5) >= 0.03125) {
                entity2.setPositionAndRotations(double3, double4, double5, float9, float10, 3, true);
            }
            else {
                entity2.setPositionAndRotations(entity2.x, entity2.y, entity2.z, float9, float10, 0, true);
            }
            entity2.onGround = packet.isOnGround();
        }
    }
    
    @Override
    public void onHeldItemChange(final HeldItemChangeS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (PlayerInventory.isValidHotbarIndex(packet.getSlot())) {
            this.client.player.inventory.selectedSlot = packet.getSlot();
        }
    }
    
    @Override
    public void onEntityUpdate(final EntityS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = packet.getEntity(this.world);
        if (entity2 == null) {
            return;
        }
        final Entity entity3 = entity2;
        entity3.ac += packet.getDeltaXShort();
        final Entity entity4 = entity2;
        entity4.ad += packet.getDeltaYShort();
        final Entity entity5 = entity2;
        entity5.ae += packet.getDeltaZShort();
        final Vec3d vec3d3 = EntityS2CPacket.a(entity2.ac, entity2.ad, entity2.ae);
        if (!entity2.isLogicalSideForUpdatingMovement()) {
            final float float4 = packet.hasRotation() ? (packet.getYaw() * 360 / 256.0f) : entity2.yaw;
            final float float5 = packet.hasRotation() ? (packet.getPitch() * 360 / 256.0f) : entity2.pitch;
            entity2.setPositionAndRotations(vec3d3.x, vec3d3.y, vec3d3.z, float4, float5, 3, false);
            entity2.onGround = packet.isOnGround();
        }
    }
    
    @Override
    public void onEntitySetHeadYaw(final EntitySetHeadYawS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = packet.getEntity(this.world);
        if (entity2 == null) {
            return;
        }
        final float float3 = packet.getHeadYaw() * 360 / 256.0f;
        entity2.a(float3, 3);
    }
    
    @Override
    public void onEntitiesDestroy(final EntitiesDestroyS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        for (int integer2 = 0; integer2 < packet.getEntityIds().length; ++integer2) {
            final int integer3 = packet.getEntityIds()[integer2];
            this.world.removeEntity(integer3);
        }
    }
    
    @Override
    public void onPlayerPositionLook(final PlayerPositionLookS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PlayerEntity playerEntity2 = this.client.player;
        double double3 = packet.getX();
        double double4 = packet.getY();
        double double5 = packet.getZ();
        float float9 = packet.getYaw();
        float float10 = packet.getPitch();
        final Vec3d vec3d11 = playerEntity2.getVelocity();
        double double6 = vec3d11.x;
        double double7 = vec3d11.y;
        double double8 = vec3d11.z;
        if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X)) {
            final PlayerEntity playerEntity3 = playerEntity2;
            playerEntity3.prevRenderX += double3;
            double3 += playerEntity2.x;
        }
        else {
            playerEntity2.prevRenderX = double3;
            double6 = 0.0;
        }
        if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y)) {
            final PlayerEntity playerEntity4 = playerEntity2;
            playerEntity4.prevRenderY += double4;
            double4 += playerEntity2.y;
        }
        else {
            playerEntity2.prevRenderY = double4;
            double7 = 0.0;
        }
        if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Z)) {
            final PlayerEntity playerEntity5 = playerEntity2;
            playerEntity5.prevRenderZ += double5;
            double5 += playerEntity2.z;
        }
        else {
            playerEntity2.prevRenderZ = double5;
            double8 = 0.0;
        }
        playerEntity2.setVelocity(double6, double7, double8);
        if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
            float10 += playerEntity2.pitch;
        }
        if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            float9 += playerEntity2.yaw;
        }
        playerEntity2.setPositionAnglesAndUpdate(double3, double4, double5, float9, float10);
        this.connection.send(new TeleportConfirmC2SPacket(packet.getTeleportId()));
        this.connection.send(new PlayerMoveC2SPacket.Both(playerEntity2.x, playerEntity2.getBoundingBox().minY, playerEntity2.z, playerEntity2.yaw, playerEntity2.pitch, false));
        if (!this.g) {
            this.client.player.prevX = this.client.player.x;
            this.client.player.prevY = this.client.player.y;
            this.client.player.prevZ = this.client.player.z;
            this.g = true;
            this.client.openScreen(null);
        }
    }
    
    @Override
    public void onChunkDeltaUpdate(final ChunkDeltaUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        for (final ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord5 : packet.getRecords()) {
            this.world.setBlockStateWithoutNeighborUpdates(chunkDeltaRecord5.getBlockPos(), chunkDeltaRecord5.getState());
        }
    }
    
    @Override
    public void onChunkData(final ChunkDataS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final int integer2 = packet.getX();
        final int integer3 = packet.getZ();
        final WorldChunk worldChunk4 = this.world.getChunkManager().loadChunkFromPacket(this.world, integer2, integer3, packet.getReadBuffer(), packet.getHeightmaps(), packet.getVerticalStripBitmask(), packet.isFullChunk());
        if (worldChunk4 != null) {
            this.world.addEntitiesToChunk(worldChunk4);
        }
        for (int integer4 = 0; integer4 < 16; ++integer4) {
            this.world.scheduleBlockRenders(integer2, integer4, integer3);
        }
        for (final CompoundTag compoundTag6 : packet.getBlockEntityTagList()) {
            final BlockPos blockPos7 = new BlockPos(compoundTag6.getInt("x"), compoundTag6.getInt("y"), compoundTag6.getInt("z"));
            final BlockEntity blockEntity8 = this.world.getBlockEntity(blockPos7);
            if (blockEntity8 != null) {
                blockEntity8.fromTag(compoundTag6);
            }
        }
    }
    
    @Override
    public void onUnloadChunk(final UnloadChunkS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final int integer2 = packet.getX();
        final int integer3 = packet.getZ();
        this.world.getChunkManager().unload(integer2, integer3);
        for (int integer4 = 0; integer4 < 16; ++integer4) {
            this.world.scheduleBlockRenders(integer2, integer4, integer3);
        }
    }
    
    @Override
    public void onBlockUpdate(final BlockUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.world.setBlockStateWithoutNeighborUpdates(packet.getPos(), packet.getState());
    }
    
    @Override
    public void onDisconnect(final DisconnectS2CPacket packet) {
        this.connection.disconnect(packet.getReason());
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
        this.client.disconnect();
        if (this.loginScreen != null) {
            if (this.loginScreen instanceof RealmsScreenProxy) {
                this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.loginScreen).getScreen(), "disconnect.lost", reason).getProxy());
            }
            else {
                this.client.openScreen(new DisconnectedScreen(this.loginScreen, "disconnect.lost", reason));
            }
        }
        else {
            this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new MainMenuScreen()), "disconnect.lost", reason));
        }
    }
    
    public void sendPacket(final Packet<?> packet) {
        this.connection.send(packet);
    }
    
    @Override
    public void onItemPickupAnimation(final ItemPickupAnimationS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getEntityId());
        LivingEntity livingEntity3 = (LivingEntity)this.world.getEntityById(packet.getCollectorEntityId());
        if (livingEntity3 == null) {
            livingEntity3 = this.client.player;
        }
        if (entity2 != null) {
            if (entity2 instanceof ExperienceOrbEntity) {
                this.world.playSound(entity2.x, entity2.y, entity2.z, SoundEvents.dd, SoundCategory.h, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            }
            else {
                this.world.playSound(entity2.x, entity2.y, entity2.z, SoundEvents.fF, SoundCategory.h, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            if (entity2 instanceof ItemEntity) {
                ((ItemEntity)entity2).getStack().setAmount(packet.getStackAmount());
            }
            this.client.particleManager.addParticle(new ItemPickupParticle(this.world, entity2, livingEntity3, 0.5f));
            this.world.removeEntity(packet.getEntityId());
        }
    }
    
    @Override
    public void onChatMessage(final ChatMessageS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.inGameHud.addChatMessage(packet.getLocation(), packet.getMessage());
    }
    
    @Override
    public void onEntityAnimation(final EntityAnimationS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getId());
        if (entity2 == null) {
            return;
        }
        if (packet.getAnimationId() == 0) {
            final LivingEntity livingEntity3 = (LivingEntity)entity2;
            livingEntity3.swingHand(Hand.a);
        }
        else if (packet.getAnimationId() == 3) {
            final LivingEntity livingEntity3 = (LivingEntity)entity2;
            livingEntity3.swingHand(Hand.b);
        }
        else if (packet.getAnimationId() == 1) {
            entity2.aX();
        }
        else if (packet.getAnimationId() == 2) {
            final PlayerEntity playerEntity3 = (PlayerEntity)entity2;
            playerEntity3.wakeUp(false, false, false);
        }
        else if (packet.getAnimationId() == 4) {
            this.client.particleManager.addEmitter(entity2, ParticleTypes.g);
        }
        else if (packet.getAnimationId() == 5) {
            this.client.particleManager.addEmitter(entity2, ParticleTypes.r);
        }
    }
    
    @Override
    public void onMobSpawn(final MobSpawnS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final double double2 = packet.getX();
        final double double3 = packet.getY();
        final double double4 = packet.getZ();
        final float float8 = packet.getVelocityX() * 360 / 256.0f;
        final float float9 = packet.getVelocityY() * 360 / 256.0f;
        final LivingEntity livingEntity10 = (LivingEntity)EntityType.createInstanceFromId(packet.getEntityTypeId(), this.client.world);
        if (livingEntity10 != null) {
            livingEntity10.b(double2, double3, double4);
            livingEntity10.aK = packet.getVelocityZ() * 360 / 256.0f;
            livingEntity10.headYaw = packet.getVelocityZ() * 360 / 256.0f;
            if (livingEntity10 instanceof EnderDragonEntity) {
                final EnderDragonPart[] arr11 = ((EnderDragonEntity)livingEntity10).dT();
                for (int integer12 = 0; integer12 < arr11.length; ++integer12) {
                    arr11[integer12].setEntityId(integer12 + packet.getId());
                }
            }
            livingEntity10.setEntityId(packet.getId());
            livingEntity10.setUuid(packet.getUuid());
            livingEntity10.setPositionAnglesAndUpdate(double2, double3, double4, float8, float9);
            livingEntity10.setVelocity(packet.getYaw() / 8000.0f, packet.getPitch() / 8000.0f, packet.getHeadPitch() / 8000.0f);
            this.world.addEntity(packet.getId(), livingEntity10);
            final List<DataTracker.Entry<?>> list11 = packet.getTrackedValues();
            if (list11 != null) {
                livingEntity10.getDataTracker().writeUpdatedEntries(list11);
            }
        }
        else {
            ClientPlayNetworkHandler.LOGGER.warn("Skipping Entity with id {}", packet.getEntityTypeId());
        }
    }
    
    @Override
    public void onWorldTimeUpdate(final WorldTimeUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.world.setTime(packet.getTime());
        this.client.world.setTimeOfDay(packet.getTimeOfDay());
    }
    
    @Override
    public void onPlayerSpawnPosition(final PlayerSpawnPositionS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.player.setPlayerSpawn(packet.getPos(), true);
        this.client.world.getLevelProperties().setSpawnPos(packet.getPos());
    }
    
    @Override
    public void onEntityPassengersSet(final EntityPassengersSetS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getId());
        if (entity2 == null) {
            ClientPlayNetworkHandler.LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        final boolean boolean3 = entity2.y(this.client.player);
        entity2.removeAllPassengers();
        for (final int integer7 : packet.getPassengerIds()) {
            final Entity entity3 = this.world.getEntityById(integer7);
            if (entity3 != null) {
                entity3.startRiding(entity2, true);
                if (entity3 == this.client.player && !boolean3) {
                    this.client.inGameHud.setOverlayMessage(I18n.translate("mount.onboard", this.client.options.keySneak.getLocalizedName()), false);
                }
            }
        }
    }
    
    @Override
    public void onEntityAttach(final EntityAttachS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getAttachedEntityId());
        if (entity2 instanceof MobEntity) {
            ((MobEntity)entity2).setHoldingEntityId(packet.getHoldingEntityId());
        }
    }
    
    private static ItemStack a(final PlayerEntity playerEntity) {
        for (final Hand hand5 : Hand.values()) {
            final ItemStack itemStack6 = playerEntity.getStackInHand(hand5);
            if (itemStack6.getItem() == Items.pd) {
                return itemStack6;
            }
        }
        return new ItemStack(Items.pd);
    }
    
    @Override
    public void onEntityStatus(final EntityStatusS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = packet.getEntity(this.world);
        if (entity2 != null) {
            if (packet.getStatus() == 21) {
                this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity2));
            }
            else if (packet.getStatus() == 35) {
                final int integer3 = 40;
                this.client.particleManager.addEmitter(entity2, ParticleTypes.V, 30);
                this.world.playSound(entity2.x, entity2.y, entity2.z, SoundEvents.lE, entity2.getSoundCategory(), 1.0f, 1.0f, false);
                if (entity2 == this.client.player) {
                    this.client.gameRenderer.showFloatingItem(a(this.client.player));
                }
            }
            else {
                entity2.handleStatus(packet.getStatus());
            }
        }
    }
    
    @Override
    public void onHealthUpdate(final HealthUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.player.updateHealth(packet.getHealth());
        this.client.player.getHungerManager().setFoodLevel(packet.getFood());
        this.client.player.getHungerManager().setSaturationLevelClient(packet.getSaturation());
    }
    
    @Override
    public void onExperienceBarUpdate(final ExperienceBarUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.player.a(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
    }
    
    @Override
    public void onPlayerRespawn(final PlayerRespawnS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final DimensionType dimensionType2 = packet.getDimension();
        final ClientPlayerEntity clientPlayerEntity3 = this.client.player;
        final int integer4 = clientPlayerEntity3.getEntityId();
        if (dimensionType2 != clientPlayerEntity3.dimension) {
            this.g = false;
            final Scoreboard scoreboard5 = this.world.getScoreboard();
            (this.world = new ClientWorld(this, new LevelInfo(0L, packet.getGameMode(), false, this.client.world.getLevelProperties().isHardcore(), packet.getGeneratorType()), packet.getDimension(), this.chunkLoadDistance, this.client.getProfiler(), this.client.worldRenderer)).setScoreboard(scoreboard5);
            this.client.joinWorld(this.world);
            this.client.openScreen(new DownloadingTerrainScreen());
        }
        this.world.setDefaultSpawnClient();
        this.world.finishRemovingEntities();
        final String string5 = clientPlayerEntity3.getServerBrand();
        this.client.cameraEntity = null;
        final ClientPlayerEntity clientPlayerEntity4 = this.client.interactionManager.createPlayer(this.world, clientPlayerEntity3.getStats(), clientPlayerEntity3.getRecipeBook());
        clientPlayerEntity4.setEntityId(integer4);
        clientPlayerEntity4.dimension = dimensionType2;
        this.client.player = clientPlayerEntity4;
        this.client.cameraEntity = clientPlayerEntity4;
        clientPlayerEntity4.getDataTracker().writeUpdatedEntries(clientPlayerEntity3.getDataTracker().getAllEntries());
        clientPlayerEntity4.X();
        clientPlayerEntity4.setServerBrand(string5);
        this.world.addPlayer(integer4, clientPlayerEntity4);
        clientPlayerEntity4.yaw = -180.0f;
        clientPlayerEntity4.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities(clientPlayerEntity4);
        clientPlayerEntity4.setReducedDebugInfo(clientPlayerEntity3.getReducedDebugInfo());
        if (this.client.currentScreen instanceof DeathScreen) {
            this.client.openScreen(null);
        }
        this.client.interactionManager.setGameMode(packet.getGameMode());
    }
    
    @Override
    public void onExplosion(final ExplosionS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Explosion explosion2 = new Explosion(this.client.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks());
        explosion2.affectWorld(true);
        this.client.player.setVelocity(this.client.player.getVelocity().add(packet.getPlayerVelocityX(), packet.getPlayerVelocityY(), packet.getPlayerVelocityZ()));
    }
    
    @Override
    public void onGuiOpen(final GuiOpenS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getHorseId());
        if (entity2 instanceof HorseBaseEntity) {
            final ClientPlayerEntity clientPlayerEntity3 = this.client.player;
            final HorseBaseEntity horseBaseEntity4 = (HorseBaseEntity)entity2;
            final BasicInventory basicInventory5 = new BasicInventory(packet.getSlotCount());
            final HorseContainer horseContainer6 = new HorseContainer(packet.getId(), clientPlayerEntity3.inventory, basicInventory5, horseBaseEntity4);
            clientPlayerEntity3.container = horseContainer6;
            this.client.openScreen(new HorseScreen(horseContainer6, clientPlayerEntity3.inventory, horseBaseEntity4));
        }
    }
    
    @Override
    public void onOpenContainer(final OpenContainerPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        ContainerScreenRegistry.openScreen(packet.getContainerType(), this.client, packet.getSyncId(), packet.getName());
    }
    
    @Override
    public void onGuiSlotUpdate(final GuiSlotUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PlayerEntity playerEntity2 = this.client.player;
        final ItemStack itemStack3 = packet.getItemStack();
        final int integer4 = packet.getSlot();
        this.client.getTutorialManager().onSlotUpdate(itemStack3);
        if (packet.getId() == -1) {
            if (!(this.client.currentScreen instanceof CreativePlayerInventoryScreen)) {
                playerEntity2.inventory.setCursorStack(itemStack3);
            }
        }
        else if (packet.getId() == -2) {
            playerEntity2.inventory.setInvStack(integer4, itemStack3);
        }
        else {
            boolean boolean5 = false;
            if (this.client.currentScreen instanceof CreativePlayerInventoryScreen) {
                final CreativePlayerInventoryScreen creativePlayerInventoryScreen6 = (CreativePlayerInventoryScreen)this.client.currentScreen;
                boolean5 = (creativePlayerInventoryScreen6.c() != ItemGroup.INVENTORY.getIndex());
            }
            if (packet.getId() == 0 && packet.getSlot() >= 36 && integer4 < 45) {
                if (!itemStack3.isEmpty()) {
                    final ItemStack itemStack4 = playerEntity2.playerContainer.getSlot(integer4).getStack();
                    if (itemStack4.isEmpty() || itemStack4.getAmount() < itemStack3.getAmount()) {
                        itemStack3.setUpdateCooldown(5);
                    }
                }
                playerEntity2.playerContainer.setStackInSlot(integer4, itemStack3);
            }
            else if (packet.getId() == playerEntity2.container.syncId && (packet.getId() != 0 || !boolean5)) {
                playerEntity2.container.setStackInSlot(integer4, itemStack3);
            }
        }
    }
    
    @Override
    public void onGuiActionConfirm(final ConfirmGuiActionS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        Container container2 = null;
        final PlayerEntity playerEntity3 = this.client.player;
        if (packet.getId() == 0) {
            container2 = playerEntity3.playerContainer;
        }
        else if (packet.getId() == playerEntity3.container.syncId) {
            container2 = playerEntity3.container;
        }
        if (container2 != null && !packet.wasAccepted()) {
            this.sendPacket(new GuiActionConfirmC2SPacket(packet.getId(), packet.getActionId(), true));
        }
    }
    
    @Override
    public void onInventory(final InventoryS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PlayerEntity playerEntity2 = this.client.player;
        if (packet.getGuiId() == 0) {
            playerEntity2.playerContainer.updateSlotStacks(packet.getSlotStacks());
        }
        else if (packet.getGuiId() == playerEntity2.container.syncId) {
            playerEntity2.container.updateSlotStacks(packet.getSlotStacks());
        }
    }
    
    @Override
    public void onSignEditorOpen(final SignEditorOpenS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        BlockEntity blockEntity2 = this.world.getBlockEntity(packet.getPos());
        if (!(blockEntity2 instanceof SignBlockEntity)) {
            blockEntity2 = new SignBlockEntity();
            blockEntity2.setWorld(this.world);
            blockEntity2.setPos(packet.getPos());
        }
        this.client.player.openEditSignScreen((SignBlockEntity)blockEntity2);
    }
    
    @Override
    public void onBlockEntityUpdate(final BlockEntityUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (this.client.world.isBlockLoaded(packet.getPos())) {
            final BlockEntity blockEntity2 = this.client.world.getBlockEntity(packet.getPos());
            final int integer3 = packet.getActionId();
            final boolean boolean4 = integer3 == 2 && blockEntity2 instanceof CommandBlockBlockEntity;
            if ((integer3 == 1 && blockEntity2 instanceof MobSpawnerBlockEntity) || boolean4 || (integer3 == 3 && blockEntity2 instanceof BeaconBlockEntity) || (integer3 == 4 && blockEntity2 instanceof SkullBlockEntity) || (integer3 == 6 && blockEntity2 instanceof BannerBlockEntity) || (integer3 == 7 && blockEntity2 instanceof StructureBlockBlockEntity) || (integer3 == 8 && blockEntity2 instanceof EndGatewayBlockEntity) || (integer3 == 9 && blockEntity2 instanceof SignBlockEntity) || (integer3 == 11 && blockEntity2 instanceof BedBlockEntity) || (integer3 == 5 && blockEntity2 instanceof ConduitBlockEntity) || (integer3 == 12 && blockEntity2 instanceof JigsawBlockEntity) || (integer3 == 13 && blockEntity2 instanceof CampfireBlockEntity)) {
                blockEntity2.fromTag(packet.getCompoundTag());
            }
            if (boolean4 && this.client.currentScreen instanceof CommandBlockScreen) {
                ((CommandBlockScreen)this.client.currentScreen).g();
            }
        }
    }
    
    @Override
    public void onGuiUpdate(final GuiUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PlayerEntity playerEntity2 = this.client.player;
        if (playerEntity2.container != null && playerEntity2.container.syncId == packet.getId()) {
            playerEntity2.container.setProperties(packet.getPropertyId(), packet.getValue());
        }
    }
    
    @Override
    public void onEquipmentUpdate(final EntityEquipmentUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getId());
        if (entity2 != null) {
            entity2.setEquippedStack(packet.getSlot(), packet.getStack());
        }
    }
    
    @Override
    public void onGuiClose(final GuiCloseS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.player.closeScreen();
    }
    
    @Override
    public void onBlockAction(final BlockActionS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.world.addBlockAction(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
    }
    
    @Override
    public void onBlockDestroyProgress(final BlockBreakingProgressS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.world.setBlockBreakingProgress(packet.getEntityId(), packet.getPos(), packet.getProgress());
    }
    
    @Override
    public void onGameStateChange(final GameStateChangeS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PlayerEntity playerEntity2 = this.client.player;
        final int integer3 = packet.getReason();
        final float float4 = packet.getValue();
        final int integer4 = MathHelper.floor(float4 + 0.5f);
        if (integer3 >= 0 && integer3 < GameStateChangeS2CPacket.REASON_MESSAGES.length && GameStateChangeS2CPacket.REASON_MESSAGES[integer3] != null) {
            playerEntity2.addChatMessage(new TranslatableTextComponent(GameStateChangeS2CPacket.REASON_MESSAGES[integer3], new Object[0]), false);
        }
        if (integer3 == 1) {
            this.world.getLevelProperties().setRaining(true);
            this.world.setRainGradient(0.0f);
        }
        else if (integer3 == 2) {
            this.world.getLevelProperties().setRaining(false);
            this.world.setRainGradient(1.0f);
        }
        else if (integer3 == 3) {
            this.client.interactionManager.setGameMode(GameMode.byId(integer4));
        }
        else if (integer3 == 4) {
            if (integer4 == 0) {
                this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.a));
                this.client.openScreen(new DownloadingTerrainScreen());
            }
            else if (integer4 == 1) {
                this.client.openScreen(new EndCreditsScreen(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.a))));
            }
        }
        else if (integer3 == 5) {
            final GameOptions gameOptions6 = this.client.options;
            if (float4 == 0.0f) {
                this.client.openScreen(new DemoScreen());
            }
            else if (float4 == 101.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableTextComponent("demo.help.movement", new Object[] { gameOptions6.keyForward.getLocalizedName(), gameOptions6.keyLeft.getLocalizedName(), gameOptions6.keyBack.getLocalizedName(), gameOptions6.keyRight.getLocalizedName() }));
            }
            else if (float4 == 102.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableTextComponent("demo.help.jump", new Object[] { gameOptions6.keyJump.getLocalizedName() }));
            }
            else if (float4 == 103.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableTextComponent("demo.help.inventory", new Object[] { gameOptions6.keyInventory.getLocalizedName() }));
            }
            else if (float4 == 104.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableTextComponent("demo.day.6", new Object[] { gameOptions6.keyScreenshot.getLocalizedName() }));
            }
        }
        else if (integer3 == 6) {
            this.world.playSound(playerEntity2, playerEntity2.x, playerEntity2.y + playerEntity2.getStandingEyeHeight(), playerEntity2.z, SoundEvents.C, SoundCategory.h, 0.18f, 0.45f);
        }
        else if (integer3 == 7) {
            this.world.setRainGradient(float4);
        }
        else if (integer3 == 8) {
            this.world.setThunderGradient(float4);
        }
        else if (integer3 == 9) {
            this.world.playSound(playerEntity2, playerEntity2.x, playerEntity2.y, playerEntity2.z, SoundEvents.jg, SoundCategory.g, 1.0f, 1.0f);
        }
        else if (integer3 == 10) {
            this.world.addParticle(ParticleTypes.q, playerEntity2.x, playerEntity2.y, playerEntity2.z, 0.0, 0.0, 0.0);
            this.world.playSound(playerEntity2, playerEntity2.x, playerEntity2.y, playerEntity2.z, SoundEvents.cm, SoundCategory.f, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void onMapUpdate(final MapUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final MapRenderer mapRenderer2 = this.client.gameRenderer.getMapRenderer();
        final String string3 = FilledMapItem.getMapStorageName(packet.getId());
        MapState mapState4 = this.client.world.getMapState(string3);
        if (mapState4 == null) {
            mapState4 = new MapState(string3);
            if (mapRenderer2.getTexture(string3) != null) {
                final MapState mapState5 = mapRenderer2.getState(mapRenderer2.getTexture(string3));
                if (mapState5 != null) {
                    mapState4 = mapState5;
                }
            }
            this.client.world.putMapState(mapState4);
        }
        packet.apply(mapState4);
        mapRenderer2.updateTexture(mapState4);
    }
    
    @Override
    public void onWorldEvent(final WorldEventS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (packet.isGlobal()) {
            this.client.world.playGlobalEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
        }
        else {
            this.client.world.playLevelEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
        }
    }
    
    @Override
    public void onAdvancements(final AdvancementUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.advancementHandler.onAdvancements(packet);
    }
    
    @Override
    public void onSelectAdvancementTab(final SelectAdvancementTabS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Identifier identifier2 = packet.getTabId();
        if (identifier2 == null) {
            this.advancementHandler.selectTab(null, false);
        }
        else {
            final Advancement advancement3 = this.advancementHandler.getManager().get(identifier2);
            this.advancementHandler.selectTab(advancement3, false);
        }
    }
    
    @Override
    public void onCommandTree(final CommandTreeS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.commandDispatcher = (CommandDispatcher<CommandSource>)new CommandDispatcher((RootCommandNode)packet.getCommandTree());
    }
    
    @Override
    public void onStopSound(final StopSoundS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.getSoundManager().stopSounds(packet.getSoundId(), packet.getCategory());
    }
    
    @Override
    public void onCommandSuggestions(final CommandSuggestionsS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.commandSource.onCommandSuggestions(packet.getCompletionId(), packet.getSuggestions());
    }
    
    @Override
    public void onSynchronizeRecipes(final SynchronizeRecipesS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.recipeManager.clear();
        for (final Recipe<?> recipe3 : packet.getRecipes()) {
            this.recipeManager.add(recipe3);
        }
        final SearchableContainer<RecipeResultCollection> searchableContainer2 = this.client.<RecipeResultCollection>getSearchableContainer(SearchManager.RECIPE_OUTPUT);
        searchableContainer2.clear();
        final ClientRecipeBook clientRecipeBook3 = this.client.player.getRecipeBook();
        clientRecipeBook3.reload();
        clientRecipeBook3.getOrderedResults().forEach(searchableContainer2::add);
        searchableContainer2.reload();
    }
    
    @Override
    public void onLookAt(final LookAtS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Vec3d vec3d2 = packet.getTargetPosition(this.world);
        if (vec3d2 != null) {
            this.client.player.lookAt(packet.getSelfAnchor(), vec3d2);
        }
    }
    
    @Override
    public void onTagQuery(final TagQueryResponseS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (!this.queryHandler.handleQueryResponse(packet.getTransactionId(), packet.getTag())) {
            ClientPlayNetworkHandler.LOGGER.debug("Got unhandled response to tag query {}", packet.getTransactionId());
        }
    }
    
    @Override
    public void onStatistics(final StatisticsS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        for (final Map.Entry<Stat<?>, Integer> entry3 : packet.getStatMap().entrySet()) {
            final Stat<?> stat4 = entry3.getKey();
            final int integer5 = entry3.getValue();
            this.client.player.getStats().setStat(this.client.player, stat4, integer5);
        }
        if (this.client.currentScreen instanceof StatsListener) {
            ((StatsListener)this.client.currentScreen).onStatsReady();
        }
    }
    
    @Override
    public void onUnlockRecipes(final UnlockRecipesS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final ClientRecipeBook clientRecipeBook2 = this.client.player.getRecipeBook();
        clientRecipeBook2.setGuiOpen(packet.isGuiOpen());
        clientRecipeBook2.setFilteringCraftable(packet.isFilteringCraftable());
        clientRecipeBook2.setFurnaceGuiOpen(packet.isFurnaceGuiOpen());
        clientRecipeBook2.setFurnaceFilteringCraftable(packet.isFurnaceFilteringCraftable());
        final UnlockRecipesS2CPacket.Action action3 = packet.getAction();
        switch (action3) {
            case c: {
                for (final Identifier identifier5 : packet.getRecipeIdsToChange()) {
                    this.recipeManager.get(identifier5).ifPresent(clientRecipeBook2::remove);
                }
                break;
            }
            case a: {
                for (final Identifier identifier5 : packet.getRecipeIdsToChange()) {
                    this.recipeManager.get(identifier5).ifPresent(clientRecipeBook2::add);
                }
                for (final Identifier identifier5 : packet.getRecipeIdsToInit()) {
                    this.recipeManager.get(identifier5).ifPresent(clientRecipeBook2::display);
                }
                break;
            }
            case b: {
                for (final Identifier identifier5 : packet.getRecipeIdsToChange()) {
                    final RecipeBook recipeBook;
                    this.recipeManager.get(identifier5).ifPresent(recipe -> {
                        recipeBook.add(recipe);
                        recipeBook.display(recipe);
                        RecipeToast.show(this.client.getToastManager(), recipe);
                        return;
                    });
                }
                break;
            }
        }
        clientRecipeBook2.getOrderedResults().forEach(recipeResultCollection -> recipeResultCollection.initialize(clientRecipeBook2));
        if (this.client.currentScreen instanceof RecipeBookProvider) {
            ((RecipeBookProvider)this.client.currentScreen).refreshRecipeBook();
        }
    }
    
    @Override
    public void onEntityPotionEffect(final EntityPotionEffectS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getEntityId());
        if (!(entity2 instanceof LivingEntity)) {
            return;
        }
        final StatusEffect statusEffect3 = StatusEffect.byRawId(packet.getEffectId());
        if (statusEffect3 == null) {
            return;
        }
        final StatusEffectInstance statusEffectInstance4 = new StatusEffectInstance(statusEffect3, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon());
        statusEffectInstance4.setPermanent(packet.isPermanent());
        ((LivingEntity)entity2).addPotionEffect(statusEffectInstance4);
    }
    
    @Override
    public void onSynchronizeTags(final SynchronizeTagsS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.tagManager = packet.getTagManager();
        if (!this.connection.isLocal()) {
            BlockTags.setContainer(this.tagManager.blocks());
            ItemTags.setContainer(this.tagManager.items());
            FluidTags.setContainer(this.tagManager.fluids());
            EntityTags.setContainer(this.tagManager.entities());
        }
        this.client.<ItemStack>getSearchableContainer(SearchManager.ITEM_TAG).reload();
    }
    
    @Override
    public void onCombatEvent(final CombatEventS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (packet.type == CombatEventS2CPacket.Type.DEATH) {
            final Entity entity2 = this.world.getEntityById(packet.entityId);
            if (entity2 == this.client.player) {
                this.client.openScreen(new DeathScreen(packet.deathMessage, this.world.getLevelProperties().isHardcore()));
            }
        }
    }
    
    @Override
    public void onDifficulty(final DifficultyS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.world.getLevelProperties().setDifficulty(packet.getDifficulty());
        this.client.world.getLevelProperties().setDifficultyLocked(packet.isDifficultyLocked());
    }
    
    @Override
    public void onSetCameraEntity(final SetCameraEntityS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = packet.getEntity(this.world);
        if (entity2 != null) {
            this.client.setCameraEntity(entity2);
        }
    }
    
    @Override
    public void onWorldBorder(final WorldBorderS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        packet.apply(this.world.getWorldBorder());
    }
    
    @Override
    public void onTitle(final TitleS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final TitleS2CPacket.Action action2 = packet.getAction();
        String string3 = null;
        String string4 = null;
        final String string5 = (packet.getText() != null) ? packet.getText().getFormattedText() : "";
        switch (action2) {
            case a: {
                string3 = string5;
                break;
            }
            case b: {
                string4 = string5;
                break;
            }
            case c: {
                this.client.inGameHud.setOverlayMessage(string5, false);
                return;
            }
            case RESET: {
                this.client.inGameHud.setTitles("", "", -1, -1, -1);
                this.client.inGameHud.setDefaultTitleFade();
                return;
            }
        }
        this.client.inGameHud.setTitles(string3, string4, packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
    }
    
    @Override
    public void onPlayerListHeader(final PlayerListHeaderS2CPacket packet) {
        this.client.inGameHud.getPlayerListWidget().setHeader(packet.getHeader().getFormattedText().isEmpty() ? null : packet.getHeader());
        this.client.inGameHud.getPlayerListWidget().setFooter(packet.getFooter().getFormattedText().isEmpty() ? null : packet.getFooter());
    }
    
    @Override
    public void onRemoveEntityEffect(final RemoveEntityEffectS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = packet.getEntity(this.world);
        if (entity2 instanceof LivingEntity) {
            ((LivingEntity)entity2).removePotionEffect(packet.getEffectType());
        }
    }
    
    @Override
    public void onPlayerList(final PlayerListS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        for (final PlayerListS2CPacket.Entry entry3 : packet.getEntries()) {
            if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE) {
                this.playerListEntries.remove(entry3.getProfile().getId());
            }
            else {
                PlayerListEntry playerListEntry4 = this.playerListEntries.get(entry3.getProfile().getId());
                if (packet.getAction() == PlayerListS2CPacket.Action.ADD) {
                    playerListEntry4 = new PlayerListEntry(entry3);
                    this.playerListEntries.put(playerListEntry4.getProfile().getId(), playerListEntry4);
                }
                if (playerListEntry4 == null) {
                    continue;
                }
                switch (packet.getAction()) {
                    case ADD: {
                        playerListEntry4.setGameMode(entry3.getGameMode());
                        playerListEntry4.setLatency(entry3.getLatency());
                        playerListEntry4.setDisplayName(entry3.getDisplayName());
                        continue;
                    }
                    case UPDATE_GAMEMODE: {
                        playerListEntry4.setGameMode(entry3.getGameMode());
                        continue;
                    }
                    case UPDATE_LATENCY: {
                        playerListEntry4.setLatency(entry3.getLatency());
                        continue;
                    }
                    case UPDATE_DISPLAY_NAME: {
                        playerListEntry4.setDisplayName(entry3.getDisplayName());
                        continue;
                    }
                }
            }
        }
    }
    
    @Override
    public void onKeepAlive(final KeepAliveS2CPacket packet) {
        this.sendPacket(new KeepAliveC2SPacket(packet.getId()));
    }
    
    @Override
    public void onPlayerAbilities(final PlayerAbilitiesS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final PlayerEntity playerEntity2 = this.client.player;
        playerEntity2.abilities.flying = packet.isFlying();
        playerEntity2.abilities.creativeMode = packet.isCreativeMode();
        playerEntity2.abilities.invulnerable = packet.isInvulnerable();
        playerEntity2.abilities.allowFlying = packet.allowFlying();
        playerEntity2.abilities.setFlySpeed(packet.getFlySpeed());
        playerEntity2.abilities.setWalkSpeed(packet.getFovModifier());
    }
    
    @Override
    public void onPlaySound(final PlaySoundS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.world.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
    }
    
    @Override
    public void onPlaySoundFromEntity(final PlaySoundFromEntityS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getEntityId());
        if (entity2 == null) {
            return;
        }
        this.client.world.playSoundFromEntity(this.client.player, entity2, packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
    }
    
    @Override
    public void onPlaySoundId(final PlaySoundIdS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.getSoundManager().play(new PositionedSoundInstance(packet.getSoundId(), packet.getCategory(), packet.getVolume(), packet.getPitch(), false, 0, SoundInstance.AttenuationType.LINEAR, (float)packet.getX(), (float)packet.getY(), (float)packet.getZ(), false));
    }
    
    @Override
    public void onResourcePackSend(final ResourcePackSendS2CPacket packet) {
        final String string2 = packet.getURL();
        final String string3 = packet.getSHA1();
        if (!this.validateResourcePackUrl(string2)) {
            return;
        }
        if (string2.startsWith("level://")) {
            try {
                final String string4 = URLDecoder.decode(string2.substring("level://".length()), StandardCharsets.UTF_8.toString());
                final File file5 = new File(this.client.runDirectory, "saves");
                final File file6 = new File(file5, string4);
                if (file6.isFile()) {
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.d);
                    final CompletableFuture<?> completableFuture7 = this.client.getResourcePackDownloader().loadServerPack(file6);
                    this.a(completableFuture7);
                    return;
                }
            }
            catch (UnsupportedEncodingException ex) {}
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.c);
            return;
        }
        final ServerEntry serverEntry4 = this.client.getCurrentServerEntry();
        if (serverEntry4 != null && serverEntry4.getResourcePack() == ServerEntry.ResourcePackState.ENABLED) {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.d);
            this.a(this.client.getResourcePackDownloader().download(string2, string3));
        }
        else if (serverEntry4 == null || serverEntry4.getResourcePack() == ServerEntry.ResourcePackState.PROMPT) {
            final MinecraftClient client;
            final Screen screen;
            final Object o;
            final Object o2;
            this.client.execute(() -> {
                client = this.client;
                new YesNoScreen(boolean3 -> {
                    this.client = MinecraftClient.getInstance();
                    final ServerEntry serverEntry4 = this.client.getCurrentServerEntry();
                    if (boolean3) {
                        if (serverEntry4 != null) {
                            serverEntry4.setResourcePackState(ServerEntry.ResourcePackState.ENABLED);
                        }
                        this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.d);
                        this.a(this.client.getResourcePackDownloader().download(o, o2));
                    }
                    else {
                        if (serverEntry4 != null) {
                            serverEntry4.setResourcePackState(ServerEntry.ResourcePackState.DISABLED);
                        }
                        this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.b);
                    }
                    ServerList.updateServerListEntry(serverEntry4);
                    this.client.openScreen(null);
                }, new TranslatableTextComponent("multiplayer.texturePrompt.line1", new Object[0]), new TranslatableTextComponent("multiplayer.texturePrompt.line2", new Object[0]));
                client.openScreen(screen);
            });
        }
        else {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.b);
        }
    }
    
    private boolean validateResourcePackUrl(final String string) {
        try {
            final URI uRI2 = new URI(string);
            final String string2 = uRI2.getScheme();
            final boolean boolean4 = "level".equals(string2);
            if (!"http".equals(string2) && !"https".equals(string2) && !boolean4) {
                throw new URISyntaxException(string, "Wrong protocol");
            }
            if (boolean4 && (string.contains("..") || !string.endsWith("/resources.zip"))) {
                throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException uRISyntaxException2) {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.c);
            return false;
        }
        return true;
    }
    
    private void a(final CompletableFuture<?> completableFuture) {
        completableFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.a)).exceptionally(throwable -> {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.c);
            return null;
        });
    }
    
    private void sendResourcePackStatus(final ResourcePackStatusC2SPacket.Status status) {
        this.connection.send(new ResourcePackStatusC2SPacket(status));
    }
    
    @Override
    public void onBossBar(final BossBarS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.client.inGameHud.getBossBarHud().handlePacket(packet);
    }
    
    @Override
    public void onCooldownUpdate(final CooldownUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (packet.getCooldown() == 0) {
            this.client.player.getItemCooldownManager().remove(packet.getItem());
        }
        else {
            this.client.player.getItemCooldownManager().set(packet.getItem(), packet.getCooldown());
        }
    }
    
    @Override
    public void onVehicleMove(final VehicleMoveS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.client.player.getTopmostVehicle();
        if (entity2 != this.client.player && entity2.isLogicalSideForUpdatingMovement()) {
            entity2.setPositionAnglesAndUpdate(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
            this.connection.send(new VehicleMoveC2SPacket(entity2));
        }
    }
    
    @Override
    public void onOpenWrittenBook(final OpenWrittenBookS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final ItemStack itemStack2 = this.client.player.getStackInHand(packet.getHand());
        if (itemStack2.getItem() == Items.nE) {
            this.client.openScreen(new WrittenBookScreen(new WrittenBookScreen.WrittenBookContents(itemStack2)));
        }
    }
    
    @Override
    public void onCustomPayload(final CustomPayloadS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Identifier identifier2 = packet.getChannel();
        PacketByteBuf packetByteBuf3 = null;
        try {
            packetByteBuf3 = packet.getData();
            if (CustomPayloadS2CPacket.BRAND.equals(identifier2)) {
                this.client.player.setServerBrand(packetByteBuf3.readString(32767));
            }
            else if (CustomPayloadS2CPacket.DEBUG_PATH.equals(identifier2)) {
                final int integer4 = packetByteBuf3.readInt();
                final float float5 = packetByteBuf3.readFloat();
                final Path path6 = Path.fromBuffer(packetByteBuf3);
                this.client.debugRenderer.pathfindingDebugRenderer.addPath(integer4, path6, float5);
            }
            else if (CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier2)) {
                final long long4 = packetByteBuf3.readVarLong();
                final BlockPos blockPos6 = packetByteBuf3.readBlockPos();
                ((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).a(long4, blockPos6);
            }
            else if (CustomPayloadS2CPacket.DEBUG_CAVES.equals(identifier2)) {
                final BlockPos blockPos7 = packetByteBuf3.readBlockPos();
                final int integer5 = packetByteBuf3.readInt();
                final List<BlockPos> list6 = Lists.newArrayList();
                final List<Float> list7 = Lists.newArrayList();
                for (int integer6 = 0; integer6 < integer5; ++integer6) {
                    list6.add(packetByteBuf3.readBlockPos());
                    list7.add(packetByteBuf3.readFloat());
                }
                this.client.debugRenderer.caveDebugRenderer.a(blockPos7, list6, list7);
            }
            else if (CustomPayloadS2CPacket.DEBUG_STRUCTURES.equals(identifier2)) {
                final int integer4 = packetByteBuf3.readInt();
                final MutableIntBoundingBox mutableIntBoundingBox5 = new MutableIntBoundingBox(packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt());
                final int integer7 = packetByteBuf3.readInt();
                final List<MutableIntBoundingBox> list8 = Lists.newArrayList();
                final List<Boolean> list9 = Lists.newArrayList();
                for (int integer8 = 0; integer8 < integer7; ++integer8) {
                    list8.add(new MutableIntBoundingBox(packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt()));
                    list9.add(packetByteBuf3.readBoolean());
                }
                this.client.debugRenderer.structureDebugRenderer.a(mutableIntBoundingBox5, list8, list9, integer4);
            }
            else if (CustomPayloadS2CPacket.DEBUG_WORLDGEN_ATTEMPT.equals(identifier2)) {
                ((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer).a(packetByteBuf3.readBlockPos(), packetByteBuf3.readFloat(), packetByteBuf3.readFloat(), packetByteBuf3.readFloat(), packetByteBuf3.readFloat(), packetByteBuf3.readFloat());
            }
            else if (CustomPayloadS2CPacket.DEBUG_VILLAGE_SECTIONS.equals(identifier2)) {
                for (int integer4 = packetByteBuf3.readInt(), integer5 = 0; integer5 < integer4; ++integer5) {
                    this.client.debugRenderer.pointsOfInterestDebugRenderer.a(packetByteBuf3.readChunkSectionPos());
                }
                for (int integer5 = packetByteBuf3.readInt(), integer7 = 0; integer7 < integer5; ++integer7) {
                    this.client.debugRenderer.pointsOfInterestDebugRenderer.b(packetByteBuf3.readChunkSectionPos());
                }
            }
            else if (CustomPayloadS2CPacket.DEBUG_POI_ADDED.equals(identifier2)) {
                final BlockPos blockPos7 = packetByteBuf3.readBlockPos();
                final String string5 = packetByteBuf3.readString();
                final int integer7 = packetByteBuf3.readInt();
                final PointOfInterestDebugRenderer.b b7 = new PointOfInterestDebugRenderer.b(blockPos7, string5, integer7);
                this.client.debugRenderer.pointsOfInterestDebugRenderer.a(b7);
            }
            else if (CustomPayloadS2CPacket.DEBUG_POI_REMOVED.equals(identifier2)) {
                final BlockPos blockPos7 = packetByteBuf3.readBlockPos();
                this.client.debugRenderer.pointsOfInterestDebugRenderer.removePointOfInterest(blockPos7);
            }
            else if (CustomPayloadS2CPacket.DEBUG_POI_TICKET_COUNT.equals(identifier2)) {
                final BlockPos blockPos7 = packetByteBuf3.readBlockPos();
                final int integer5 = packetByteBuf3.readInt();
                this.client.debugRenderer.pointsOfInterestDebugRenderer.a(blockPos7, integer5);
            }
            else if (CustomPayloadS2CPacket.DEBUG_GOAL_SELECTOR.equals(identifier2)) {
                final int integer4 = packetByteBuf3.readInt();
                final BlockPos blockPos8 = packetByteBuf3.readBlockPos();
                final int integer7 = packetByteBuf3.readInt();
                final int integer9 = packetByteBuf3.readInt();
                final List<GoalSelectorDebugRenderer.a> list10 = Lists.newArrayList();
                for (int integer8 = 0; integer8 < integer9; ++integer8) {
                    final int integer10 = packetByteBuf3.readInt();
                    final boolean boolean11 = packetByteBuf3.readBoolean();
                    final String string6 = packetByteBuf3.readString(255);
                    list10.add(new GoalSelectorDebugRenderer.a(blockPos8, integer10, string6, boolean11));
                }
                this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(integer7, list10);
            }
            else if (CustomPayloadS2CPacket.DEBUG_BRAIN.equals(identifier2)) {
                final double double4 = packetByteBuf3.readDouble();
                final double double5 = packetByteBuf3.readDouble();
                final double double6 = packetByteBuf3.readDouble();
                final Position position10 = new PositionImpl(double4, double5, double6);
                final UUID uUID11 = packetByteBuf3.readUuid();
                final int integer11 = packetByteBuf3.readInt();
                final String string7 = packetByteBuf3.readString();
                final PointOfInterestDebugRenderer.a a14 = new PointOfInterestDebugRenderer.a(uUID11, integer11, string7, position10);
                for (int integer12 = packetByteBuf3.readInt(), integer13 = 0; integer13 < integer12; ++integer13) {
                    final String string8 = packetByteBuf3.readString();
                    a14.e.add(string8);
                }
                for (int integer13 = packetByteBuf3.readInt(), integer14 = 0; integer14 < integer13; ++integer14) {
                    final String string9 = packetByteBuf3.readString();
                    a14.f.add(string9);
                }
                for (int integer14 = packetByteBuf3.readInt(), integer15 = 0; integer15 < integer14; ++integer15) {
                    final String string10 = packetByteBuf3.readString();
                    a14.g.add(string10);
                }
                for (int integer15 = packetByteBuf3.readInt(), integer16 = 0; integer16 < integer15; ++integer16) {
                    final BlockPos blockPos9 = packetByteBuf3.readBlockPos();
                    a14.h.add(blockPos9);
                }
                this.client.debugRenderer.pointsOfInterestDebugRenderer.addPointOfInterest(a14);
            }
            else {
                ClientPlayNetworkHandler.LOGGER.warn("Unknown custom packed identifier: {}", identifier2);
            }
        }
        finally {
            if (packetByteBuf3 != null) {
                packetByteBuf3.release();
            }
        }
    }
    
    @Override
    public void onScoreboardObjectiveUpdate(final ScoreboardObjectiveUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Scoreboard scoreboard2 = this.world.getScoreboard();
        final String string3 = packet.getName();
        if (packet.getMode() == 0) {
            scoreboard2.addObjective(string3, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType());
        }
        else if (scoreboard2.containsObjective(string3)) {
            final ScoreboardObjective scoreboardObjective4 = scoreboard2.getNullableObjective(string3);
            if (packet.getMode() == 1) {
                scoreboard2.removeObjective(scoreboardObjective4);
            }
            else if (packet.getMode() == 2) {
                scoreboardObjective4.setRenderType(packet.getType());
                scoreboardObjective4.setDisplayName(packet.getDisplayName());
            }
        }
    }
    
    @Override
    public void onScoreboardPlayerUpdate(final ScoreboardPlayerUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Scoreboard scoreboard2 = this.world.getScoreboard();
        final String string3 = packet.getObjectiveName();
        switch (packet.getUpdateMode()) {
            case a: {
                final ScoreboardObjective scoreboardObjective4 = scoreboard2.getObjective(string3);
                final ScoreboardPlayerScore scoreboardPlayerScore5 = scoreboard2.getPlayerScore(packet.getPlayerName(), scoreboardObjective4);
                scoreboardPlayerScore5.setScore(packet.getScore());
                break;
            }
            case b: {
                scoreboard2.resetPlayerScore(packet.getPlayerName(), scoreboard2.getNullableObjective(string3));
                break;
            }
        }
    }
    
    @Override
    public void onScoreboardDisplay(final ScoreboardDisplayS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Scoreboard scoreboard2 = this.world.getScoreboard();
        final String string3 = packet.getName();
        final ScoreboardObjective scoreboardObjective4 = (string3 == null) ? null : scoreboard2.getObjective(string3);
        scoreboard2.setObjectiveSlot(packet.getSlot(), scoreboardObjective4);
    }
    
    @Override
    public void onTeam(final TeamS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Scoreboard scoreboard2 = this.world.getScoreboard();
        Team team3;
        if (packet.getMode() == 0) {
            team3 = scoreboard2.addTeam(packet.getTeamName());
        }
        else {
            team3 = scoreboard2.getTeam(packet.getTeamName());
        }
        if (packet.getMode() == 0 || packet.getMode() == 2) {
            team3.setDisplayName(packet.getDisplayName());
            team3.setColor(packet.getPlayerPrefix());
            team3.setFriendlyFlagsBitwise(packet.getFlags());
            final AbstractTeam.VisibilityRule visibilityRule4 = AbstractTeam.VisibilityRule.getRule(packet.getNameTagVisibilityRule());
            if (visibilityRule4 != null) {
                team3.setNameTagVisibilityRule(visibilityRule4);
            }
            final AbstractTeam.CollisionRule collisionRule5 = AbstractTeam.CollisionRule.getRule(packet.getCollisionRule());
            if (collisionRule5 != null) {
                team3.setCollisionRule(collisionRule5);
            }
            team3.setPrefix(packet.getPrefix());
            team3.setSuffix(packet.getSuffix());
        }
        if (packet.getMode() == 0 || packet.getMode() == 3) {
            for (final String string5 : packet.getPlayerList()) {
                scoreboard2.addPlayerToTeam(string5, team3);
            }
        }
        if (packet.getMode() == 4) {
            for (final String string5 : packet.getPlayerList()) {
                scoreboard2.removePlayerFromTeam(string5, team3);
            }
        }
        if (packet.getMode() == 1) {
            scoreboard2.removeTeam(team3);
        }
    }
    
    @Override
    public void onParticle(final ParticleS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        if (packet.getCount() == 0) {
            final double double2 = packet.getSpeed() * packet.getOffsetX();
            final double double3 = packet.getSpeed() * packet.getOffsetY();
            final double double4 = packet.getSpeed() * packet.getOffsetZ();
            try {
                this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX(), packet.getY(), packet.getZ(), double2, double3, double4);
            }
            catch (Throwable throwable8) {
                ClientPlayNetworkHandler.LOGGER.warn("Could not spawn particle effect {}", packet.getParameters());
            }
        }
        else {
            for (int integer2 = 0; integer2 < packet.getCount(); ++integer2) {
                final double double5 = this.random.nextGaussian() * packet.getOffsetX();
                final double double6 = this.random.nextGaussian() * packet.getOffsetY();
                final double double7 = this.random.nextGaussian() * packet.getOffsetZ();
                final double double8 = this.random.nextGaussian() * packet.getSpeed();
                final double double9 = this.random.nextGaussian() * packet.getSpeed();
                final double double10 = this.random.nextGaussian() * packet.getSpeed();
                try {
                    this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX() + double5, packet.getY() + double6, packet.getZ() + double7, double8, double9, double10);
                }
                catch (Throwable throwable9) {
                    ClientPlayNetworkHandler.LOGGER.warn("Could not spawn particle effect {}", packet.getParameters());
                    return;
                }
            }
        }
    }
    
    @Override
    public void onEntityAttributes(final EntityAttributesS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Entity entity2 = this.world.getEntityById(packet.getEntityId());
        if (entity2 == null) {
            return;
        }
        if (!(entity2 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity2 + ")");
        }
        final AbstractEntityAttributeContainer abstractEntityAttributeContainer3 = ((LivingEntity)entity2).getAttributeContainer();
        for (final EntityAttributesS2CPacket.Entry entry5 : packet.getEntries()) {
            EntityAttributeInstance entityAttributeInstance6 = abstractEntityAttributeContainer3.get(entry5.getId());
            if (entityAttributeInstance6 == null) {
                entityAttributeInstance6 = abstractEntityAttributeContainer3.register(new ClampedEntityAttribute(null, entry5.getId(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }
            entityAttributeInstance6.setBaseValue(entry5.getBaseValue());
            entityAttributeInstance6.clearModifiers();
            for (final EntityAttributeModifier entityAttributeModifier8 : entry5.getModifiers()) {
                entityAttributeInstance6.addModifier(entityAttributeModifier8);
            }
        }
    }
    
    @Override
    public void onCraftResponse(final CraftResponseS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Container container2 = this.client.player.container;
        if (container2.syncId != packet.getSyncId() || !container2.isRestricted(this.client.player)) {
            return;
        }
        RecipeBookGui recipeBookGui3;
        final Container container3;
        this.recipeManager.get(packet.getRecipeId()).ifPresent(recipe -> {
            if (this.client.currentScreen instanceof RecipeBookProvider) {
                recipeBookGui3 = ((RecipeBookProvider)this.client.currentScreen).getRecipeBookGui();
                recipeBookGui3.showGhostRecipe(recipe, container3.slotList);
            }
        });
    }
    
    @Override
    public void onLightUpdate(final LightUpdateS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final int integer2 = packet.getChunkX();
        final int integer3 = packet.getChunkZ();
        final LightingProvider lightingProvider4 = this.world.getChunkManager().getLightingProvider();
        final int integer4 = packet.getSkyLightMask();
        final int integer5 = packet.getFilledSkyLightMask();
        final Iterator<byte[]> iterator7 = packet.getSkyLightUpdates().iterator();
        this.a(integer2, integer3, lightingProvider4, LightType.SKY, integer4, integer5, iterator7);
        final int integer6 = packet.getBlockLightMask();
        final int integer7 = packet.getFilledBlockLightMask();
        final Iterator<byte[]> iterator8 = packet.getBlockLightUpdates().iterator();
        this.a(integer2, integer3, lightingProvider4, LightType.BLOCK, integer6, integer7, iterator8);
    }
    
    @Override
    public void onSetTradeOffers(final SetTradeOffersPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        final Container container2 = this.client.player.container;
        if (packet.getSyncId() == container2.syncId && container2 instanceof MerchantContainer) {
            ((MerchantContainer)container2).setOffers(new TraderOfferList(packet.getOffers().toTag()));
            ((MerchantContainer)container2).setExperienceFromServer(packet.getExperience());
            ((MerchantContainer)container2).setLevelProgress(packet.getLevelProgress());
            ((MerchantContainer)container2).setCanLevel(packet.isLevelled());
        }
    }
    
    @Override
    public void handleChunkLoadDistance(final ChunkLoadDistanceS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.chunkLoadDistance = packet.getDistance();
        this.world.getChunkManager().updateLoadDistance(packet.getDistance());
    }
    
    @Override
    public void handleChunkRenderDistanceCenter(final ChunkRenderDistanceCenterS2CPacket packet) {
        NetworkThreadUtils.<ClientPlayNetworkHandler>forceMainThread((Packet<ClientPlayNetworkHandler>)packet, this, this.client);
        this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
    }
    
    private void a(final int integer1, final int integer2, final LightingProvider lightingProvider, final LightType lightType, final int integer5, final int integer6, final Iterator<byte[]> iterator) {
        for (int integer7 = 0; integer7 < 18; ++integer7) {
            final int integer8 = -1 + integer7;
            final boolean boolean10 = (integer5 & 1 << integer7) != 0x0;
            final boolean boolean11 = (integer6 & 1 << integer7) != 0x0;
            if (boolean10 || boolean11) {
                lightingProvider.queueData(lightType, ChunkSectionPos.from(integer1, integer8, integer2), boolean10 ? new ChunkNibbleArray(iterator.next().clone()) : new ChunkNibbleArray());
                this.world.scheduleBlockRenders(integer1, integer8, integer2);
            }
        }
    }
    
    public ClientConnection getClientConnection() {
        return this.connection;
    }
    
    public Collection<PlayerListEntry> getPlayerList() {
        return this.playerListEntries.values();
    }
    
    @Nullable
    public PlayerListEntry getPlayerListEntry(final UUID uuid) {
        return this.playerListEntries.get(uuid);
    }
    
    @Nullable
    public PlayerListEntry getPlayerListEntry(final String profileName) {
        for (final PlayerListEntry playerListEntry3 : this.playerListEntries.values()) {
            if (playerListEntry3.getProfile().getName().equals(profileName)) {
                return playerListEntry3;
            }
        }
        return null;
    }
    
    public GameProfile getProfile() {
        return this.profile;
    }
    
    public ClientAdvancementManager getAdvancementHandler() {
        return this.advancementHandler;
    }
    
    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }
    
    public ClientWorld getWorld() {
        return this.world;
    }
    
    public TagManager getTagManager() {
        return this.tagManager;
    }
    
    public QueryHandler getQueryHandler() {
        return this.queryHandler;
    }
    
    public UUID getSessionId() {
        return this.sessionId;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
