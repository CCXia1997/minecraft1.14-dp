package net.minecraft.network.listener;

import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;

public interface ServerPlayPacketListener extends PacketListener
{
    void onHandSwing(final HandSwingC2SPacket arg1);
    
    void onChatMessage(final ChatMessageC2SPacket arg1);
    
    void onClientStatus(final ClientStatusC2SPacket arg1);
    
    void onClientSettings(final ClientSettingsC2SPacket arg1);
    
    void onConfirmTransaction(final GuiActionConfirmC2SPacket arg1);
    
    void onButtonClick(final ButtonClickC2SPacket arg1);
    
    void onClickWindow(final ClickWindowC2SPacket arg1);
    
    void onCraftRequest(final CraftRequestC2SPacket arg1);
    
    void onGuiClose(final GuiCloseC2SPacket arg1);
    
    void onCustomPayload(final CustomPayloadC2SPacket arg1);
    
    void onPlayerInteractEntity(final PlayerInteractEntityC2SPacket arg1);
    
    void onKeepAlive(final KeepAliveC2SPacket arg1);
    
    void onPlayerMove(final PlayerMoveC2SPacket arg1);
    
    void onPlayerAbilities(final UpdatePlayerAbilitiesC2SPacket arg1);
    
    void onPlayerAction(final PlayerActionC2SPacket arg1);
    
    void onClientCommand(final ClientCommandC2SPacket arg1);
    
    void onPlayerInput(final PlayerInputC2SPacket arg1);
    
    void onUpdateSelectedSlot(final UpdateSelectedSlotC2SPacket arg1);
    
    void onCreativeInventoryAction(final CreativeInventoryActionC2SPacket arg1);
    
    void onSignUpdate(final UpdateSignC2SPacket arg1);
    
    void onPlayerInteractBlock(final PlayerInteractBlockC2SPacket arg1);
    
    void onPlayerInteractItem(final PlayerInteractItemC2SPacket arg1);
    
    void onSpectatorTeleport(final SpectatorTeleportC2SPacket arg1);
    
    void onResourcePackStatus(final ResourcePackStatusC2SPacket arg1);
    
    void onBoatPaddleState(final BoatPaddleStateC2SPacket arg1);
    
    void onVehicleMove(final VehicleMoveC2SPacket arg1);
    
    void onTeleportConfirm(final TeleportConfirmC2SPacket arg1);
    
    void onRecipeBookData(final RecipeBookDataC2SPacket arg1);
    
    void onAdvancementTab(final AdvancementTabC2SPacket arg1);
    
    void onRequestCommandCompletions(final RequestCommandCompletionsC2SPacket arg1);
    
    void onUpdateCommandBlock(final UpdateCommandBlockC2SPacket arg1);
    
    void onUpdateCommandBlockMinecart(final UpdateCommandBlockMinecartC2SPacket arg1);
    
    void onPickFromInventory(final PickFromInventoryC2SPacket arg1);
    
    void onRenameItem(final RenameItemC2SPacket arg1);
    
    void onUpdateBeacon(final UpdateBeaconC2SPacket arg1);
    
    void onStructureBlockUpdate(final UpdateStructureBlockC2SPacket arg1);
    
    void onVillagerTradeSelect(final SelectVillagerTradeC2SPacket arg1);
    
    void onBookUpdate(final BookUpdateC2SPacket arg1);
    
    void onQueryEntityNbt(final QueryEntityNbtC2SPacket arg1);
    
    void onQueryBlockNbt(final QueryBlockNbtC2SPacket arg1);
    
    void onJigsawUpdate(final UpdateJigsawC2SPacket arg1);
    
    void onUpdateDifficulty(final UpdateDifficultyC2SPacket arg1);
    
    void onUpdateDifficultyLock(final UpdateDifficultyLockC2SPacket arg1);
}
