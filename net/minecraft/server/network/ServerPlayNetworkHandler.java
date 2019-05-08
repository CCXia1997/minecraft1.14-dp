package net.minecraft.server.network;

import net.minecraft.state.AbstractPropertyContainer;
import org.apache.logging.log4j.LogManager;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.container.CraftingContainer;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.container.Slot;
import net.minecraft.util.DefaultedList;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.item.ElytraItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.options.ChatVisibility;
import javax.annotation.Nullable;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import java.util.Iterator;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.text.ChatMessageType;
import net.minecraft.text.TextFormat;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.world.BlockView;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import java.util.Set;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import java.util.Collections;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.GameMode;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.container.Container;
import net.minecraft.container.MerchantContainer;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.container.BeaconContainer;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.SharedConstants;
import net.minecraft.container.AnvilContainer;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.util.ChatUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.block.CommandBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.recipe.Recipe;
import java.util.function.Consumer;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Doubles;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.util.SystemUtil;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.network.listener.PacketListener;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener
{
    private static final Logger LOGGER;
    public final ClientConnection client;
    private final MinecraftServer server;
    public ServerPlayerEntity player;
    private int ticks;
    private long lastKeepAliveTime;
    private boolean waitingForKeepAlive;
    private long keepAliveId;
    private int messageCooldown;
    private int creativeItemDropThreshold;
    private final Int2ShortMap transactions;
    private double lastTickX;
    private double lastTickY;
    private double lastTickZ;
    private double updatedX;
    private double updatedY;
    private double updatedZ;
    private Entity topmostRiddenEntity;
    private double lastTickRiddenX;
    private double lastTickRiddenY;
    private double lastTickRiddenZ;
    private double updatedRiddenX;
    private double updatedRiddenY;
    private double updatedRiddenZ;
    private Vec3d requestedTeleportPos;
    private int requestedTeleportId;
    private int teleportRequestTick;
    private boolean floating;
    private int floatingTicks;
    private boolean ridingEntity;
    private int vehicleFloatingTicks;
    private int movePacketsCount;
    private int lastTickMovePacketsCount;
    
    public ServerPlayNetworkHandler(final MinecraftServer minecraftServer, final ClientConnection clientConnection, final ServerPlayerEntity serverPlayerEntity) {
        this.transactions = (Int2ShortMap)new Int2ShortOpenHashMap();
        this.server = minecraftServer;
        (this.client = clientConnection).setPacketListener(this);
        this.player = serverPlayerEntity;
        serverPlayerEntity.networkHandler = this;
    }
    
    public void tick() {
        this.syncWithPlayerPosition();
        this.player.i();
        this.player.setPositionAnglesAndUpdate(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.yaw, this.player.pitch);
        ++this.ticks;
        this.lastTickMovePacketsCount = this.movePacketsCount;
        if (this.floating) {
            if (++this.floatingTicks > 80) {
                ServerPlayNetworkHandler.LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
                this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.flying", new Object[0]));
                return;
            }
        }
        else {
            this.floating = false;
            this.floatingTicks = 0;
        }
        this.topmostRiddenEntity = this.player.getTopmostVehicle();
        if (this.topmostRiddenEntity == this.player || this.topmostRiddenEntity.getPrimaryPassenger() != this.player) {
            this.topmostRiddenEntity = null;
            this.ridingEntity = false;
            this.vehicleFloatingTicks = 0;
        }
        else {
            this.lastTickRiddenX = this.topmostRiddenEntity.x;
            this.lastTickRiddenY = this.topmostRiddenEntity.y;
            this.lastTickRiddenZ = this.topmostRiddenEntity.z;
            this.updatedRiddenX = this.topmostRiddenEntity.x;
            this.updatedRiddenY = this.topmostRiddenEntity.y;
            this.updatedRiddenZ = this.topmostRiddenEntity.z;
            if (this.ridingEntity && this.player.getTopmostVehicle().getPrimaryPassenger() == this.player) {
                if (++this.vehicleFloatingTicks > 80) {
                    ServerPlayNetworkHandler.LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
                    this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.flying", new Object[0]));
                    return;
                }
            }
            else {
                this.ridingEntity = false;
                this.vehicleFloatingTicks = 0;
            }
        }
        this.server.getProfiler().push("keepAlive");
        final long long1 = SystemUtil.getMeasuringTimeMs();
        if (long1 - this.lastKeepAliveTime >= 15000L) {
            if (this.waitingForKeepAlive) {
                this.disconnect(new TranslatableTextComponent("disconnect.timeout", new Object[0]));
            }
            else {
                this.waitingForKeepAlive = true;
                this.lastKeepAliveTime = long1;
                this.keepAliveId = long1;
                this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
            }
        }
        this.server.getProfiler().pop();
        if (this.messageCooldown > 0) {
            --this.messageCooldown;
        }
        if (this.creativeItemDropThreshold > 0) {
            --this.creativeItemDropThreshold;
        }
        if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && SystemUtil.getMeasuringTimeMs() - this.player.getLastActionTime() > this.server.getPlayerIdleTimeout() * 1000 * 60) {
            this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.idling", new Object[0]));
        }
    }
    
    public void syncWithPlayerPosition() {
        this.lastTickX = this.player.x;
        this.lastTickY = this.player.y;
        this.lastTickZ = this.player.z;
        this.updatedX = this.player.x;
        this.updatedY = this.player.y;
        this.updatedZ = this.player.z;
    }
    
    public ClientConnection getConnection() {
        return this.client;
    }
    
    private boolean isServerOwner() {
        return this.server.isOwner(this.player.getGameProfile());
    }
    
    public void disconnect(final TextComponent textComponent) {
        this.client.send(new DisconnectS2CPacket(textComponent), (future -> this.client.disconnect(textComponent)));
        this.client.disableAutoRead();
        this.server.executeSync(this.client::handleDisconnection);
    }
    
    @Override
    public void onPlayerInput(final PlayerInputC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.a(packet.getSideways(), packet.getForward(), packet.isJumping(), packet.isSneaking());
    }
    
    private static boolean validatePlayerMove(final PlayerMoveC2SPacket playerMoveC2SPacket) {
        return !Doubles.isFinite(playerMoveC2SPacket.getX(0.0)) || !Doubles.isFinite(playerMoveC2SPacket.getY(0.0)) || !Doubles.isFinite(playerMoveC2SPacket.getZ(0.0)) || !Floats.isFinite(playerMoveC2SPacket.getPitch(0.0f)) || !Floats.isFinite(playerMoveC2SPacket.getYaw(0.0f)) || (Math.abs(playerMoveC2SPacket.getX(0.0)) > 3.0E7 || Math.abs(playerMoveC2SPacket.getY(0.0)) > 3.0E7 || Math.abs(playerMoveC2SPacket.getZ(0.0)) > 3.0E7);
    }
    
    private static boolean validateVehicleMove(final VehicleMoveC2SPacket vehicleMoveC2SPacket) {
        return !Doubles.isFinite(vehicleMoveC2SPacket.getX()) || !Doubles.isFinite(vehicleMoveC2SPacket.getY()) || !Doubles.isFinite(vehicleMoveC2SPacket.getZ()) || !Floats.isFinite(vehicleMoveC2SPacket.getPitch()) || !Floats.isFinite(vehicleMoveC2SPacket.getYaw());
    }
    
    @Override
    public void onVehicleMove(final VehicleMoveC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (validateVehicleMove(packet)) {
            this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
            return;
        }
        final Entity entity2 = this.player.getTopmostVehicle();
        if (entity2 != this.player && entity2.getPrimaryPassenger() == this.player && entity2 == this.topmostRiddenEntity) {
            final ServerWorld serverWorld3 = this.player.getServerWorld();
            final double double4 = entity2.x;
            final double double5 = entity2.y;
            final double double6 = entity2.z;
            final double double7 = packet.getX();
            final double double8 = packet.getY();
            final double double9 = packet.getZ();
            final float float16 = packet.getYaw();
            final float float17 = packet.getPitch();
            double double10 = double7 - this.lastTickRiddenX;
            double double11 = double8 - this.lastTickRiddenY;
            double double12 = double9 - this.lastTickRiddenZ;
            final double double13 = entity2.getVelocity().lengthSquared();
            double double14 = double10 * double10 + double11 * double11 + double12 * double12;
            if (double14 - double13 > 100.0 && !this.isServerOwner()) {
                ServerPlayNetworkHandler.LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity2.getName().getString(), this.player.getName().getString(), double10, double11, double12);
                this.client.send(new VehicleMoveS2CPacket(entity2));
                return;
            }
            final boolean boolean28 = serverWorld3.doesNotCollide(entity2, entity2.getBoundingBox().contract(0.0625));
            double10 = double7 - this.updatedRiddenX;
            double11 = double8 - this.updatedRiddenY - 1.0E-6;
            double12 = double9 - this.updatedRiddenZ;
            entity2.move(MovementType.b, new Vec3d(double10, double11, double12));
            final double double15 = double11;
            double10 = double7 - entity2.x;
            double11 = double8 - entity2.y;
            if (double11 > -0.5 || double11 < 0.5) {
                double11 = 0.0;
            }
            double12 = double9 - entity2.z;
            double14 = double10 * double10 + double11 * double11 + double12 * double12;
            boolean boolean29 = false;
            if (double14 > 0.0625) {
                boolean29 = true;
                ServerPlayNetworkHandler.LOGGER.warn("{} moved wrongly!", entity2.getName().getString());
            }
            entity2.setPositionAnglesAndUpdate(double7, double8, double9, float16, float17);
            final boolean boolean30 = serverWorld3.doesNotCollide(entity2, entity2.getBoundingBox().contract(0.0625));
            if (boolean28 && (boolean29 || !boolean30)) {
                entity2.setPositionAnglesAndUpdate(double4, double5, double6, float16, float17);
                this.client.send(new VehicleMoveS2CPacket(entity2));
                return;
            }
            this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
            this.player.k(this.player.x - double4, this.player.y - double5, this.player.z - double6);
            this.ridingEntity = (double15 >= -0.03125 && !this.server.isFlightEnabled() && !serverWorld3.isAreaNotEmpty(entity2.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)));
            this.updatedRiddenX = entity2.x;
            this.updatedRiddenY = entity2.y;
            this.updatedRiddenZ = entity2.z;
        }
    }
    
    @Override
    public void onTeleportConfirm(final TeleportConfirmC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (packet.getTeleportId() == this.requestedTeleportId) {
            this.player.setPositionAnglesAndUpdate(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
            this.updatedX = this.requestedTeleportPos.x;
            this.updatedY = this.requestedTeleportPos.y;
            this.updatedZ = this.requestedTeleportPos.z;
            if (this.player.isInTeleportationState()) {
                this.player.onTeleportationDone();
            }
            this.requestedTeleportPos = null;
        }
    }
    
    @Override
    public void onRecipeBookData(final RecipeBookDataC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (packet.getMode() == RecipeBookDataC2SPacket.Mode.a) {
            this.server.getRecipeManager().get(packet.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
        }
        else if (packet.getMode() == RecipeBookDataC2SPacket.Mode.b) {
            this.player.getRecipeBook().setGuiOpen(packet.isGuiOpen());
            this.player.getRecipeBook().setFilteringCraftable(packet.isFilteringCraftable());
            this.player.getRecipeBook().setFurnaceGuiOpen(packet.isFurnaceGuiOpen());
            this.player.getRecipeBook().setFurnaceFilteringCraftable(packet.isFurnaceFilteringCraftable());
            this.player.getRecipeBook().setBlastFurnaceGuiOpen(packet.isBlastFurnaceGuiOpen());
            this.player.getRecipeBook().setBlastFurnaceFilteringCraftable(packet.isBlastFurnaceFilteringCraftable());
            this.player.getRecipeBook().setSmokerGuiOpen(packet.isSmokerGuiOpen());
            this.player.getRecipeBook().setSmokerFilteringCraftable(packet.isSmokerGuiFilteringCraftable());
        }
    }
    
    @Override
    public void onAdvancementTab(final AdvancementTabC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (packet.getAction() == AdvancementTabC2SPacket.Action.a) {
            final Identifier identifier2 = packet.getTabToOpen();
            final Advancement advancement3 = this.server.getAdvancementManager().get(identifier2);
            if (advancement3 != null) {
                this.player.getAdvancementManager().setDisplayTab(advancement3);
            }
        }
    }
    
    @Override
    public void onRequestCommandCompletions(final RequestCommandCompletionsC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final StringReader stringReader2 = new StringReader(packet.getPartialCommand());
        if (stringReader2.canRead() && stringReader2.peek() == '/') {
            stringReader2.skip();
        }
        final ParseResults<ServerCommandSource> parseResults3 = (ParseResults<ServerCommandSource>)this.server.getCommandManager().getDispatcher().parse(stringReader2, this.player.getCommandSource());
        this.server.getCommandManager().getDispatcher().getCompletionSuggestions((ParseResults)parseResults3).thenAccept(suggestions -> this.client.send(new CommandSuggestionsS2CPacket(packet.getCompletionId(), suggestions)));
    }
    
    @Override
    public void onUpdateCommandBlock(final UpdateCommandBlockC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendMessage(new TranslatableTextComponent("advMode.notEnabled", new Object[0]));
            return;
        }
        if (!this.player.isCreativeLevelTwoOp()) {
            this.player.sendMessage(new TranslatableTextComponent("advMode.notAllowed", new Object[0]));
            return;
        }
        CommandBlockExecutor commandBlockExecutor2 = null;
        CommandBlockBlockEntity commandBlockBlockEntity3 = null;
        final BlockPos blockPos4 = packet.getBlockPos();
        final BlockEntity blockEntity5 = this.player.world.getBlockEntity(blockPos4);
        if (blockEntity5 instanceof CommandBlockBlockEntity) {
            commandBlockBlockEntity3 = (CommandBlockBlockEntity)blockEntity5;
            commandBlockExecutor2 = commandBlockBlockEntity3.getCommandExecutor();
        }
        final String string6 = packet.getCommand();
        final boolean boolean7 = packet.shouldTrackOutput();
        if (commandBlockExecutor2 != null) {
            final Direction direction8 = this.player.world.getBlockState(blockPos4).<Direction>get((Property<Direction>)CommandBlock.FACING);
            switch (packet.getType()) {
                case a: {
                    final BlockState blockState9 = Blocks.iz.getDefaultState();
                    this.player.world.setBlockState(blockPos4, (((AbstractPropertyContainer<O, BlockState>)blockState9).with((Property<Comparable>)CommandBlock.FACING, direction8)).<Comparable, Boolean>with((Property<Comparable>)CommandBlock.CONDITIONAL, packet.isConditional()), 2);
                    break;
                }
                case b: {
                    final BlockState blockState9 = Blocks.iy.getDefaultState();
                    this.player.world.setBlockState(blockPos4, (((AbstractPropertyContainer<O, BlockState>)blockState9).with((Property<Comparable>)CommandBlock.FACING, direction8)).<Comparable, Boolean>with((Property<Comparable>)CommandBlock.CONDITIONAL, packet.isConditional()), 2);
                    break;
                }
                default: {
                    final BlockState blockState9 = Blocks.ej.getDefaultState();
                    this.player.world.setBlockState(blockPos4, (((AbstractPropertyContainer<O, BlockState>)blockState9).with((Property<Comparable>)CommandBlock.FACING, direction8)).<Comparable, Boolean>with((Property<Comparable>)CommandBlock.CONDITIONAL, packet.isConditional()), 2);
                    break;
                }
            }
            blockEntity5.validate();
            this.player.world.setBlockEntity(blockPos4, blockEntity5);
            commandBlockExecutor2.setCommand(string6);
            commandBlockExecutor2.shouldTrackOutput(boolean7);
            if (!boolean7) {
                commandBlockExecutor2.setLastOutput(null);
            }
            commandBlockBlockEntity3.setAuto(packet.isAlwaysActive());
            commandBlockExecutor2.markDirty();
            if (!ChatUtil.isEmpty(string6)) {
                this.player.sendMessage(new TranslatableTextComponent("advMode.setCommand.success", new Object[] { string6 }));
            }
        }
    }
    
    @Override
    public void onUpdateCommandBlockMinecart(final UpdateCommandBlockMinecartC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendMessage(new TranslatableTextComponent("advMode.notEnabled", new Object[0]));
            return;
        }
        if (!this.player.isCreativeLevelTwoOp()) {
            this.player.sendMessage(new TranslatableTextComponent("advMode.notAllowed", new Object[0]));
            return;
        }
        final CommandBlockExecutor commandBlockExecutor2 = packet.getMinecartCommandExecutor(this.player.world);
        if (commandBlockExecutor2 != null) {
            commandBlockExecutor2.setCommand(packet.getCommand());
            commandBlockExecutor2.shouldTrackOutput(packet.shouldTrackOutput());
            if (!packet.shouldTrackOutput()) {
                commandBlockExecutor2.setLastOutput(null);
            }
            commandBlockExecutor2.markDirty();
            this.player.sendMessage(new TranslatableTextComponent("advMode.setCommand.success", new Object[] { packet.getCommand() }));
        }
    }
    
    @Override
    public void onPickFromInventory(final PickFromInventoryC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.inventory.swapSlotWithHotbar(packet.getSlot());
        this.player.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot)));
        this.player.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-2, packet.getSlot(), this.player.inventory.getInvStack(packet.getSlot())));
        this.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
    }
    
    @Override
    public void onRenameItem(final RenameItemC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (this.player.container instanceof AnvilContainer) {
            final AnvilContainer anvilContainer2 = (AnvilContainer)this.player.container;
            final String string3 = SharedConstants.stripInvalidChars(packet.getItemName());
            if (string3.length() <= 35) {
                anvilContainer2.setNewItemName(string3);
            }
        }
    }
    
    @Override
    public void onUpdateBeacon(final UpdateBeaconC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (this.player.container instanceof BeaconContainer) {
            ((BeaconContainer)this.player.container).setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
        }
    }
    
    @Override
    public void onStructureBlockUpdate(final UpdateStructureBlockC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        final BlockPos blockPos2 = packet.getPos();
        final BlockState blockState3 = this.player.world.getBlockState(blockPos2);
        final BlockEntity blockEntity4 = this.player.world.getBlockEntity(blockPos2);
        if (blockEntity4 instanceof StructureBlockBlockEntity) {
            final StructureBlockBlockEntity structureBlockBlockEntity5 = (StructureBlockBlockEntity)blockEntity4;
            structureBlockBlockEntity5.setMode(packet.getMode());
            structureBlockBlockEntity5.setStructureName(packet.getStructureName());
            structureBlockBlockEntity5.setOffset(packet.getOffset());
            structureBlockBlockEntity5.setSize(packet.getSize());
            structureBlockBlockEntity5.setMirror(packet.getMirror());
            structureBlockBlockEntity5.setRotation(packet.getRotation());
            structureBlockBlockEntity5.setMetadata(packet.getMetadata());
            structureBlockBlockEntity5.setIgnoreEntities(packet.getIgnoreEntities());
            structureBlockBlockEntity5.setShowAir(packet.shouldShowAir());
            structureBlockBlockEntity5.setShowBoundingBox(packet.shouldShowBoundingBox());
            structureBlockBlockEntity5.setIntegrity(packet.getIntegrity());
            structureBlockBlockEntity5.setSeed(packet.getSeed());
            if (structureBlockBlockEntity5.hasStructureName()) {
                final String string6 = structureBlockBlockEntity5.getStructureName();
                if (packet.getAction() == StructureBlockBlockEntity.Action.b) {
                    if (structureBlockBlockEntity5.saveStructure()) {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.save_success", new Object[] { string6 }), false);
                    }
                    else {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.save_failure", new Object[] { string6 }), false);
                    }
                }
                else if (packet.getAction() == StructureBlockBlockEntity.Action.c) {
                    if (!structureBlockBlockEntity5.isStructureAvailable()) {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_not_found", new Object[] { string6 }), false);
                    }
                    else if (structureBlockBlockEntity5.loadStructure()) {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_success", new Object[] { string6 }), false);
                    }
                    else {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_prepare", new Object[] { string6 }), false);
                    }
                }
                else if (packet.getAction() == StructureBlockBlockEntity.Action.d) {
                    if (structureBlockBlockEntity5.detectStructureSize()) {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.size_success", new Object[] { string6 }), false);
                    }
                    else {
                        this.player.addChatMessage(new TranslatableTextComponent("structure_block.size_failure", new Object[0]), false);
                    }
                }
            }
            else {
                this.player.addChatMessage(new TranslatableTextComponent("structure_block.invalid_structure_name", new Object[] { packet.getStructureName() }), false);
            }
            structureBlockBlockEntity5.markDirty();
            this.player.world.updateListeners(blockPos2, blockState3, blockState3, 3);
        }
    }
    
    @Override
    public void onJigsawUpdate(final UpdateJigsawC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        final BlockPos blockPos2 = packet.getPos();
        final BlockState blockState3 = this.player.world.getBlockState(blockPos2);
        final BlockEntity blockEntity4 = this.player.world.getBlockEntity(blockPos2);
        if (blockEntity4 instanceof JigsawBlockEntity) {
            final JigsawBlockEntity jigsawBlockEntity5 = (JigsawBlockEntity)blockEntity4;
            jigsawBlockEntity5.setAttachmentType(packet.getAttachmentType());
            jigsawBlockEntity5.setTargetPool(packet.getTargetPool());
            jigsawBlockEntity5.setFinalState(packet.getFinalState());
            jigsawBlockEntity5.markDirty();
            this.player.world.updateListeners(blockPos2, blockState3, blockState3, 3);
        }
    }
    
    @Override
    public void onVillagerTradeSelect(final SelectVillagerTradeC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final int integer2 = packet.b();
        final Container container3 = this.player.container;
        if (container3 instanceof MerchantContainer) {
            final MerchantContainer merchantContainer4 = (MerchantContainer)container3;
            merchantContainer4.setRecipeIndex(integer2);
            merchantContainer4.switchTo(integer2);
        }
    }
    
    @Override
    public void onBookUpdate(final BookUpdateC2SPacket packet) {
        final ItemStack itemStack2 = packet.getBook();
        if (itemStack2.isEmpty()) {
            return;
        }
        if (!WritableBookItem.isValidBook(itemStack2.getTag())) {
            return;
        }
        final ItemStack itemStack3 = this.player.getStackInHand(packet.getHand());
        if (itemStack2.getItem() == Items.nD && itemStack3.getItem() == Items.nD) {
            if (packet.wasSigned()) {
                final ItemStack itemStack4 = new ItemStack(Items.nE);
                final CompoundTag compoundTag5 = itemStack3.getTag();
                if (compoundTag5 != null) {
                    itemStack4.setTag(compoundTag5.copy());
                }
                itemStack4.setChildTag("author", new StringTag(this.player.getName().getString()));
                itemStack4.setChildTag("title", new StringTag(itemStack2.getTag().getString("title")));
                final ListTag listTag6 = itemStack2.getTag().getList("pages", 8);
                for (int integer7 = 0; integer7 < listTag6.size(); ++integer7) {
                    String string8 = listTag6.getString(integer7);
                    final TextComponent textComponent9 = new StringTextComponent(string8);
                    string8 = TextComponent.Serializer.toJsonString(textComponent9);
                    listTag6.d(integer7, new StringTag(string8));
                }
                itemStack4.setChildTag("pages", listTag6);
                this.player.setStackInHand(packet.getHand(), itemStack4);
            }
            else {
                itemStack3.setChildTag("pages", itemStack2.getTag().getList("pages", 8));
            }
        }
    }
    
    @Override
    public void onQueryEntityNbt(final QueryEntityNbtC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (!this.player.allowsPermissionLevel(2)) {
            return;
        }
        final Entity entity2 = this.player.getServerWorld().getEntityById(packet.getEntityId());
        if (entity2 != null) {
            final CompoundTag compoundTag3 = entity2.toTag(new CompoundTag());
            this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(packet.getTransactionId(), compoundTag3));
        }
    }
    
    @Override
    public void onQueryBlockNbt(final QueryBlockNbtC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (!this.player.allowsPermissionLevel(2)) {
            return;
        }
        final BlockEntity blockEntity2 = this.player.getServerWorld().getBlockEntity(packet.getPos());
        final CompoundTag compoundTag3 = (blockEntity2 != null) ? blockEntity2.toTag(new CompoundTag()) : null;
        this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(packet.getTransactionId(), compoundTag3));
    }
    
    @Override
    public void onPlayerMove(final PlayerMoveC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (validatePlayerMove(packet)) {
            this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_player_movement", new Object[0]));
            return;
        }
        final ServerWorld serverWorld2 = this.server.getWorld(this.player.dimension);
        if (this.player.notInAnyWorld) {
            return;
        }
        if (this.ticks == 0) {
            this.syncWithPlayerPosition();
        }
        if (this.requestedTeleportPos != null) {
            if (this.ticks - this.teleportRequestTick > 20) {
                this.teleportRequestTick = this.ticks;
                this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
            }
            return;
        }
        this.teleportRequestTick = this.ticks;
        if (this.player.hasVehicle()) {
            this.player.setPositionAnglesAndUpdate(this.player.x, this.player.y, this.player.z, packet.getYaw(this.player.yaw), packet.getPitch(this.player.pitch));
            this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
            return;
        }
        final double double3 = this.player.x;
        final double double4 = this.player.y;
        final double double5 = this.player.z;
        final double double6 = this.player.y;
        final double double7 = packet.getX(this.player.x);
        final double double8 = packet.getY(this.player.y);
        final double double9 = packet.getZ(this.player.z);
        final float float17 = packet.getYaw(this.player.yaw);
        final float float18 = packet.getPitch(this.player.pitch);
        double double10 = double7 - this.lastTickX;
        double double11 = double8 - this.lastTickY;
        double double12 = double9 - this.lastTickZ;
        final double double13 = this.player.getVelocity().lengthSquared();
        double double14 = double10 * double10 + double11 * double11 + double12 * double12;
        if (this.player.isSleeping()) {
            if (double14 > 1.0) {
                this.requestTeleport(this.player.x, this.player.y, this.player.z, packet.getYaw(this.player.yaw), packet.getPitch(this.player.pitch));
            }
            return;
        }
        ++this.movePacketsCount;
        int integer29 = this.movePacketsCount - this.lastTickMovePacketsCount;
        if (integer29 > 5) {
            ServerPlayNetworkHandler.LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), integer29);
            integer29 = 1;
        }
        if (!this.player.isInTeleportationState()) {
            if (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.isFallFlying()) {
                final float float19 = this.player.isFallFlying() ? 300.0f : 100.0f;
                if (double14 - double13 > float19 * integer29 && !this.isServerOwner()) {
                    ServerPlayNetworkHandler.LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), double10, double11, double12);
                    this.requestTeleport(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
                    return;
                }
            }
        }
        final boolean boolean30 = serverWorld2.doesNotCollide(this.player, this.player.getBoundingBox().contract(0.0625));
        double10 = double7 - this.updatedX;
        double11 = double8 - this.updatedY;
        double12 = double9 - this.updatedZ;
        if (this.player.onGround && !packet.isOnGround() && double11 > 0.0) {
            this.player.jump();
        }
        this.player.move(MovementType.b, new Vec3d(double10, double11, double12));
        this.player.onGround = packet.isOnGround();
        final double double15 = double11;
        double10 = double7 - this.player.x;
        double11 = double8 - this.player.y;
        if (double11 > -0.5 || double11 < 0.5) {
            double11 = 0.0;
        }
        double12 = double9 - this.player.z;
        double14 = double10 * double10 + double11 * double11 + double12 * double12;
        boolean boolean31 = false;
        if (!this.player.isInTeleportationState() && double14 > 0.0625 && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameMode() != GameMode.e) {
            boolean31 = true;
            ServerPlayNetworkHandler.LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
        }
        this.player.setPositionAnglesAndUpdate(double7, double8, double9, float17, float18);
        this.player.k(this.player.x - double3, this.player.y - double4, this.player.z - double5);
        if (!this.player.noClip && !this.player.isSleeping()) {
            final boolean boolean32 = serverWorld2.doesNotCollide(this.player, this.player.getBoundingBox().contract(0.0625));
            if (boolean30 && (boolean31 || !boolean32)) {
                this.requestTeleport(double3, double4, double5, float17, float18);
                return;
            }
        }
        this.floating = (double15 >= -0.03125 && this.player.interactionManager.getGameMode() != GameMode.e && !this.server.isFlightEnabled() && !this.player.abilities.allowFlying && !this.player.hasStatusEffect(StatusEffects.y) && !this.player.isFallFlying() && !serverWorld2.isAreaNotEmpty(this.player.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)));
        this.player.onGround = packet.isOnGround();
        this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
        this.player.a(this.player.y - double6, packet.isOnGround());
        this.updatedX = this.player.x;
        this.updatedY = this.player.y;
        this.updatedZ = this.player.z;
    }
    
    public void requestTeleport(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.teleportRequest(x, y, z, yaw, pitch, Collections.<PlayerPositionLookS2CPacket.Flag>emptySet());
    }
    
    public void teleportRequest(final double x, final double y, final double z, final float yaw, final float pitch, final Set<PlayerPositionLookS2CPacket.Flag> set9) {
        final double double10 = set9.contains(PlayerPositionLookS2CPacket.Flag.X) ? this.player.x : 0.0;
        final double double11 = set9.contains(PlayerPositionLookS2CPacket.Flag.Y) ? this.player.y : 0.0;
        final double double12 = set9.contains(PlayerPositionLookS2CPacket.Flag.Z) ? this.player.z : 0.0;
        final float float16 = set9.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT) ? this.player.yaw : 0.0f;
        final float float17 = set9.contains(PlayerPositionLookS2CPacket.Flag.X_ROT) ? this.player.pitch : 0.0f;
        this.requestedTeleportPos = new Vec3d(x, y, z);
        if (++this.requestedTeleportId == Integer.MAX_VALUE) {
            this.requestedTeleportId = 0;
        }
        this.teleportRequestTick = this.ticks;
        this.player.setPositionAnglesAndUpdate(x, y, z, yaw, pitch);
        this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x - double10, y - double11, z - double12, yaw - float16, pitch - float17, set9, this.requestedTeleportId));
    }
    
    @Override
    public void onPlayerAction(final PlayerActionC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final ServerWorld serverWorld2 = this.server.getWorld(this.player.dimension);
        final BlockPos blockPos3 = packet.getPos();
        this.player.updateLastActionTime();
        switch (packet.getAction()) {
            case g: {
                if (!this.player.isSpectator()) {
                    final ItemStack itemStack4 = this.player.getStackInHand(Hand.b);
                    this.player.setStackInHand(Hand.b, this.player.getStackInHand(Hand.a));
                    this.player.setStackInHand(Hand.a, itemStack4);
                }
            }
            case e: {
                if (!this.player.isSpectator()) {
                    this.player.dropSelectedItem(false);
                }
            }
            case d: {
                if (!this.player.isSpectator()) {
                    this.player.dropSelectedItem(true);
                }
            }
            case f: {
                this.player.stopUsingItem();
            }
            case a:
            case b:
            case c: {
                final double double4 = this.player.x - (blockPos3.getX() + 0.5);
                final double double5 = this.player.y - (blockPos3.getY() + 0.5) + 1.5;
                final double double6 = this.player.z - (blockPos3.getZ() + 0.5);
                final double double7 = double4 * double4 + double5 * double5 + double6 * double6;
                if (double7 > 36.0) {
                    return;
                }
                if (blockPos3.getY() >= this.server.getWorldHeight()) {
                    return;
                }
                if (packet.getAction() == PlayerActionC2SPacket.Action.a) {
                    if (!this.server.isSpawnProtected(serverWorld2, blockPos3, this.player) && serverWorld2.getWorldBorder().contains(blockPos3)) {
                        this.player.interactionManager.a(blockPos3, packet.getDirection());
                    }
                    else {
                        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld2, blockPos3));
                    }
                }
                else {
                    if (packet.getAction() == PlayerActionC2SPacket.Action.c) {
                        this.player.interactionManager.a(blockPos3);
                    }
                    else if (packet.getAction() == PlayerActionC2SPacket.Action.b) {
                        this.player.interactionManager.e();
                    }
                    if (!serverWorld2.getBlockState(blockPos3).isAir()) {
                        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld2, blockPos3));
                    }
                }
            }
            default: {
                throw new IllegalArgumentException("Invalid player action");
            }
        }
    }
    
    @Override
    public void onPlayerInteractBlock(final PlayerInteractBlockC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final ServerWorld serverWorld2 = this.server.getWorld(this.player.dimension);
        final Hand hand3 = packet.getHand();
        final ItemStack itemStack4 = this.player.getStackInHand(hand3);
        final BlockHitResult blockHitResult5 = packet.getHitY();
        final BlockPos blockPos6 = blockHitResult5.getBlockPos();
        final Direction direction7 = blockHitResult5.getSide();
        this.player.updateLastActionTime();
        if (blockPos6.getY() < this.server.getWorldHeight() - 1 || (direction7 != Direction.UP && blockPos6.getY() < this.server.getWorldHeight())) {
            if (this.requestedTeleportPos == null && this.player.squaredDistanceTo(blockPos6.getX() + 0.5, blockPos6.getY() + 0.5, blockPos6.getZ() + 0.5) < 64.0 && !this.server.isSpawnProtected(serverWorld2, blockPos6, this.player) && serverWorld2.getWorldBorder().contains(blockPos6)) {
                this.player.interactionManager.interactBlock(this.player, serverWorld2, itemStack4, hand3, blockHitResult5);
            }
        }
        else {
            final TextComponent textComponent8 = new TranslatableTextComponent("build.tooHigh", new Object[] { this.server.getWorldHeight() }).applyFormat(TextFormat.m);
            this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(textComponent8, ChatMessageType.c));
        }
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld2, blockPos6));
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld2, blockPos6.offset(direction7)));
    }
    
    @Override
    public void onPlayerInteractItem(final PlayerInteractItemC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final ServerWorld serverWorld2 = this.server.getWorld(this.player.dimension);
        final Hand hand3 = packet.getHand();
        final ItemStack itemStack4 = this.player.getStackInHand(hand3);
        this.player.updateLastActionTime();
        if (itemStack4.isEmpty()) {
            return;
        }
        this.player.interactionManager.interactItem(this.player, serverWorld2, itemStack4, hand3);
    }
    
    @Override
    public void onSpectatorTeleport(final SpectatorTeleportC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (this.player.isSpectator()) {
            for (final ServerWorld serverWorld3 : this.server.getWorlds()) {
                final Entity entity4 = packet.getTarget(serverWorld3);
                if (entity4 != null) {
                    this.player.teleport(serverWorld3, entity4.x, entity4.y, entity4.z, entity4.yaw, entity4.pitch);
                }
            }
        }
    }
    
    @Override
    public void onResourcePackStatus(final ResourcePackStatusC2SPacket packet) {
    }
    
    @Override
    public void onBoatPaddleState(final BoatPaddleStateC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final Entity entity2 = this.player.getVehicle();
        if (entity2 instanceof BoatEntity) {
            ((BoatEntity)entity2).setPaddleState(packet.isLeftPaddling(), packet.isRightPaddling());
        }
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
        ServerPlayNetworkHandler.LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
        this.server.forcePlayerSampleUpdate();
        this.server.getPlayerManager().sendToAll(new TranslatableTextComponent("multiplayer.player.left", new Object[] { this.player.getDisplayName() }).applyFormat(TextFormat.o));
        this.player.n();
        this.server.getPlayerManager().remove(this.player);
        if (this.isServerOwner()) {
            ServerPlayNetworkHandler.LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.stop(false);
        }
    }
    
    public void sendPacket(final Packet<?> packet) {
        this.sendPacket(packet, null);
    }
    
    public void sendPacket(final Packet<?> packet, @Nullable final GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        if (packet instanceof ChatMessageS2CPacket) {
            final ChatMessageS2CPacket chatMessageS2CPacket3 = (ChatMessageS2CPacket)packet;
            final ChatVisibility chatVisibility4 = this.player.getClientChatVisibility();
            if (chatVisibility4 == ChatVisibility.HIDDEN && chatMessageS2CPacket3.getLocation() != ChatMessageType.c) {
                return;
            }
            if (chatVisibility4 == ChatVisibility.COMMANDS && !chatMessageS2CPacket3.isNonChat()) {
                return;
            }
        }
        try {
            this.client.send(packet, genericFutureListener);
        }
        catch (Throwable throwable3) {
            final CrashReport crashReport4 = CrashReport.create(throwable3, "Sending packet");
            final CrashReportSection crashReportSection5 = crashReport4.addElement("Packet being sent");
            crashReportSection5.add("Packet class", () -> packet.getClass().getCanonicalName());
            throw new CrashException(crashReport4);
        }
    }
    
    @Override
    public void onUpdateSelectedSlot(final UpdateSelectedSlotC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (packet.getSelectedSlot() < 0 || packet.getSelectedSlot() >= PlayerInventory.getHotbarSize()) {
            ServerPlayNetworkHandler.LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
            return;
        }
        this.player.inventory.selectedSlot = packet.getSelectedSlot();
        this.player.updateLastActionTime();
    }
    
    @Override
    public void onChatMessage(final ChatMessageC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
            this.sendPacket(new ChatMessageS2CPacket(new TranslatableTextComponent("chat.cannotSend", new Object[0]).applyFormat(TextFormat.m)));
            return;
        }
        this.player.updateLastActionTime();
        String string2 = packet.getChatMessage();
        string2 = StringUtils.normalizeSpace(string2);
        for (int integer3 = 0; integer3 < string2.length(); ++integer3) {
            if (!SharedConstants.isValidChar(string2.charAt(integer3))) {
                this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.illegal_characters", new Object[0]));
                return;
            }
        }
        if (string2.startsWith("/")) {
            this.executeCommand(string2);
        }
        else {
            final TextComponent textComponent3 = new TranslatableTextComponent("chat.type.text", new Object[] { this.player.getDisplayName(), string2 });
            this.server.getPlayerManager().broadcastChatMessage(textComponent3, false);
        }
        this.messageCooldown += 20;
        if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
            this.disconnect(new TranslatableTextComponent("disconnect.spam", new Object[0]));
        }
    }
    
    private void executeCommand(final String string) {
        this.server.getCommandManager().execute(this.player.getCommandSource(), string);
    }
    
    @Override
    public void onHandSwing(final HandSwingC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        this.player.swingHand(packet.getHand());
    }
    
    @Override
    public void onClientCommand(final ClientCommandC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        switch (packet.getMode()) {
            case a: {
                this.player.setSneaking(true);
                break;
            }
            case b: {
                this.player.setSneaking(false);
                break;
            }
            case d: {
                this.player.setSprinting(true);
                break;
            }
            case e: {
                this.player.setSprinting(false);
                break;
            }
            case c: {
                if (this.player.isSleeping()) {
                    this.player.wakeUp(false, true, true);
                    this.requestedTeleportPos = new Vec3d(this.player.x, this.player.y, this.player.z);
                    break;
                }
                break;
            }
            case f: {
                if (this.player.getVehicle() instanceof JumpingMount) {
                    final JumpingMount jumpingMount2 = (JumpingMount)this.player.getVehicle();
                    final int integer3 = packet.getMountJumpHeight();
                    if (jumpingMount2.canJump() && integer3 > 0) {
                        jumpingMount2.startJumping(integer3);
                    }
                    break;
                }
                break;
            }
            case g: {
                if (this.player.getVehicle() instanceof JumpingMount) {
                    final JumpingMount jumpingMount2 = (JumpingMount)this.player.getVehicle();
                    jumpingMount2.stopJumping();
                    break;
                }
                break;
            }
            case h: {
                if (this.player.getVehicle() instanceof HorseBaseEntity) {
                    ((HorseBaseEntity)this.player.getVehicle()).openInventory(this.player);
                    break;
                }
                break;
            }
            case i: {
                if (!this.player.onGround && this.player.getVelocity().y < 0.0 && !this.player.isFallFlying() && !this.player.isInsideWater()) {
                    final ItemStack itemStack2 = this.player.getEquippedStack(EquipmentSlot.CHEST);
                    if (itemStack2.getItem() == Items.oX && ElytraItem.isUsable(itemStack2)) {
                        this.player.J();
                    }
                    break;
                }
                this.player.K();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid client command!");
            }
        }
    }
    
    @Override
    public void onPlayerInteractEntity(final PlayerInteractEntityC2SPacket rpacket) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)rpacket, this, this.player.getServerWorld());
        final ServerWorld serverWorld2 = this.server.getWorld(this.player.dimension);
        final Entity entity3 = rpacket.getEntity(serverWorld2);
        this.player.updateLastActionTime();
        if (entity3 != null) {
            final boolean boolean4 = this.player.canSee(entity3);
            double double5 = 36.0;
            if (!boolean4) {
                double5 = 9.0;
            }
            if (this.player.squaredDistanceTo(entity3) < double5) {
                if (rpacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.a) {
                    final Hand hand7 = rpacket.getHand();
                    this.player.interact(entity3, hand7);
                }
                else if (rpacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.c) {
                    final Hand hand7 = rpacket.getHand();
                    entity3.interactAt(this.player, rpacket.getHitPosition(), hand7);
                }
                else if (rpacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.b) {
                    if (entity3 instanceof ItemEntity || entity3 instanceof ExperienceOrbEntity || entity3 instanceof ProjectileEntity || entity3 == this.player) {
                        this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                        this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
                        return;
                    }
                    this.player.attack(entity3);
                }
            }
        }
    }
    
    @Override
    public void onClientStatus(final ClientStatusC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        final ClientStatusC2SPacket.Mode mode2 = packet.getMode();
        switch (mode2) {
            case a: {
                if (this.player.notInAnyWorld) {
                    this.player.notInAnyWorld = false;
                    this.player = this.server.getPlayerManager().respawnPlayer(this.player, DimensionType.a, true);
                    Criterions.CHANGED_DIMENSION.handle(this.player, DimensionType.c, DimensionType.a);
                    break;
                }
                if (this.player.getHealth() > 0.0f) {
                    return;
                }
                this.player = this.server.getPlayerManager().respawnPlayer(this.player, DimensionType.a, false);
                if (this.server.isHardcore()) {
                    this.player.setGameMode(GameMode.e);
                    this.player.getServerWorld().getGameRules().put("spectatorsGenerateChunks", "false", this.server);
                    break;
                }
                break;
            }
            case b: {
                this.player.getStatHandler().sendStats(this.player);
                break;
            }
        }
    }
    
    @Override
    public void onGuiClose(final GuiCloseC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.m();
    }
    
    @Override
    public void onClickWindow(final ClickWindowC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        if (this.player.container.syncId == packet.getSyncId() && this.player.container.isRestricted(this.player)) {
            if (this.player.isSpectator()) {
                final DefaultedList<ItemStack> defaultedList2 = DefaultedList.<ItemStack>create();
                for (int integer3 = 0; integer3 < this.player.container.slotList.size(); ++integer3) {
                    defaultedList2.add(this.player.container.slotList.get(integer3).getStack());
                }
                this.player.onContainerRegistered(this.player.container, defaultedList2);
            }
            else {
                final ItemStack itemStack2 = this.player.container.onSlotClick(packet.getSlot(), packet.getButton(), packet.getActionType(), this.player);
                if (ItemStack.areEqual(packet.getStack(), itemStack2)) {
                    this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getTransactionId(), true));
                    this.player.e = true;
                    this.player.container.sendContentUpdates();
                    this.player.l();
                    this.player.e = false;
                }
                else {
                    this.transactions.put(this.player.container.syncId, packet.getTransactionId());
                    this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getTransactionId(), false));
                    this.player.container.setPlayerRestriction(this.player, false);
                    final DefaultedList<ItemStack> defaultedList3 = DefaultedList.<ItemStack>create();
                    for (int integer4 = 0; integer4 < this.player.container.slotList.size(); ++integer4) {
                        final ItemStack itemStack3 = this.player.container.slotList.get(integer4).getStack();
                        defaultedList3.add(itemStack3.isEmpty() ? ItemStack.EMPTY : itemStack3);
                    }
                    this.player.onContainerRegistered(this.player.container, defaultedList3);
                }
            }
        }
    }
    
    @Override
    public void onCraftRequest(final CraftRequestC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        if (this.player.isSpectator() || this.player.container.syncId != packet.getSyncId() || !this.player.container.isRestricted(this.player) || !(this.player.container instanceof CraftingContainer)) {
            return;
        }
        this.server.getRecipeManager().get(packet.getRecipe()).ifPresent(recipe -> ((CraftingContainer)this.player.container).fillInputSlots(packet.shouldCraftAll(), recipe, this.player));
    }
    
    @Override
    public void onButtonClick(final ButtonClickC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        if (this.player.container.syncId == packet.getSyncId() && this.player.container.isRestricted(this.player) && !this.player.isSpectator()) {
            this.player.container.onButtonClick(this.player, packet.getButtonId());
            this.player.container.sendContentUpdates();
        }
    }
    
    @Override
    public void onCreativeInventoryAction(final CreativeInventoryActionC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        if (this.player.interactionManager.isCreative()) {
            final boolean boolean2 = packet.getSlot() < 0;
            final ItemStack itemStack3 = packet.getItemStack();
            final CompoundTag compoundTag4 = itemStack3.getSubCompoundTag("BlockEntityTag");
            if (!itemStack3.isEmpty() && compoundTag4 != null && compoundTag4.containsKey("x") && compoundTag4.containsKey("y") && compoundTag4.containsKey("z")) {
                final BlockPos blockPos5 = new BlockPos(compoundTag4.getInt("x"), compoundTag4.getInt("y"), compoundTag4.getInt("z"));
                final BlockEntity blockEntity6 = this.player.world.getBlockEntity(blockPos5);
                if (blockEntity6 != null) {
                    final CompoundTag compoundTag5 = blockEntity6.toTag(new CompoundTag());
                    compoundTag5.remove("x");
                    compoundTag5.remove("y");
                    compoundTag5.remove("z");
                    itemStack3.setChildTag("BlockEntityTag", compoundTag5);
                }
            }
            final boolean boolean3 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
            final boolean boolean4 = itemStack3.isEmpty() || (itemStack3.getDamage() >= 0 && itemStack3.getAmount() <= 64 && !itemStack3.isEmpty());
            if (boolean3 && boolean4) {
                if (itemStack3.isEmpty()) {
                    this.player.playerContainer.setStackInSlot(packet.getSlot(), ItemStack.EMPTY);
                }
                else {
                    this.player.playerContainer.setStackInSlot(packet.getSlot(), itemStack3);
                }
                this.player.playerContainer.setPlayerRestriction(this.player, true);
                this.player.playerContainer.sendContentUpdates();
            }
            else if (boolean2 && boolean4 && this.creativeItemDropThreshold < 200) {
                this.creativeItemDropThreshold += 20;
                final ItemEntity itemEntity7 = this.player.dropItem(itemStack3, true);
                if (itemEntity7 != null) {
                    itemEntity7.f();
                }
            }
        }
    }
    
    @Override
    public void onConfirmTransaction(final GuiActionConfirmC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        final int integer2 = this.player.container.syncId;
        if (integer2 == packet.getWindowId() && this.transactions.getOrDefault(integer2, (short)(packet.getSyncId() + 1)) == packet.getSyncId() && !this.player.container.isRestricted(this.player) && !this.player.isSpectator()) {
            this.player.container.setPlayerRestriction(this.player, true);
        }
    }
    
    @Override
    public void onSignUpdate(final UpdateSignC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        final ServerWorld serverWorld2 = this.server.getWorld(this.player.dimension);
        final BlockPos blockPos3 = packet.getPos();
        if (serverWorld2.isBlockLoaded(blockPos3)) {
            final BlockState blockState4 = serverWorld2.getBlockState(blockPos3);
            final BlockEntity blockEntity5 = serverWorld2.getBlockEntity(blockPos3);
            if (!(blockEntity5 instanceof SignBlockEntity)) {
                return;
            }
            final SignBlockEntity signBlockEntity6 = (SignBlockEntity)blockEntity5;
            if (!signBlockEntity6.isEditable() || signBlockEntity6.getEditor() != this.player) {
                this.server.warn("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
                return;
            }
            final String[] arr7 = packet.getText();
            for (int integer8 = 0; integer8 < arr7.length; ++integer8) {
                signBlockEntity6.setTextOnRow(integer8, new StringTextComponent(TextFormat.stripFormatting(arr7[integer8])));
            }
            signBlockEntity6.markDirty();
            serverWorld2.updateListeners(blockPos3, blockState4, blockState4, 3);
        }
    }
    
    @Override
    public void onKeepAlive(final KeepAliveC2SPacket packet) {
        if (this.waitingForKeepAlive && packet.getId() == this.keepAliveId) {
            final int integer2 = (int)(SystemUtil.getMeasuringTimeMs() - this.lastKeepAliveTime);
            this.player.f = (this.player.f * 3 + integer2) / 4;
            this.waitingForKeepAlive = false;
        }
        else if (!this.isServerOwner()) {
            this.disconnect(new TranslatableTextComponent("disconnect.timeout", new Object[0]));
        }
    }
    
    @Override
    public void onPlayerAbilities(final UpdatePlayerAbilitiesC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.abilities.flying = (packet.isFlying() && this.player.abilities.allowFlying);
    }
    
    @Override
    public void onClientSettings(final ClientSettingsC2SPacket packet) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)packet, this, this.player.getServerWorld());
        this.player.setClientSettings(packet);
    }
    
    @Override
    public void onCustomPayload(final CustomPayloadC2SPacket packet) {
    }
    
    @Override
    public void onUpdateDifficulty(final UpdateDifficultyC2SPacket updateDifficultyC2SPacket) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)updateDifficultyC2SPacket, this, this.player.getServerWorld());
        if (!this.player.allowsPermissionLevel(2) && !this.isServerOwner()) {
            return;
        }
        this.server.setDifficulty(updateDifficultyC2SPacket.getDifficulty(), false);
    }
    
    @Override
    public void onUpdateDifficultyLock(final UpdateDifficultyLockC2SPacket updateDifficultyLockC2SPacket) {
        NetworkThreadUtils.<ServerPlayNetworkHandler>forceMainThread((Packet<ServerPlayNetworkHandler>)updateDifficultyLockC2SPacket, this, this.player.getServerWorld());
        if (!this.player.allowsPermissionLevel(2) && !this.isServerOwner()) {
            return;
        }
        this.server.setDifficultyLocked(updateDifficultyLockC2SPacket.isDifficultyLocked());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
