package net.minecraft.network.listener;

import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.client.network.packet.CraftResponseS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.client.network.packet.StatisticsS2CPacket;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;

public interface ClientPlayPacketListener extends PacketListener
{
    void onEntitySpawn(final EntitySpawnS2CPacket arg1);
    
    void onExperienceOrbSpawn(final ExperienceOrbSpawnS2CPacket arg1);
    
    void onEntitySpawnGlobal(final EntitySpawnGlobalS2CPacket arg1);
    
    void onMobSpawn(final MobSpawnS2CPacket arg1);
    
    void onScoreboardObjectiveUpdate(final ScoreboardObjectiveUpdateS2CPacket arg1);
    
    void onPaintingSpawn(final PaintingSpawnS2CPacket arg1);
    
    void onPlayerSpawn(final PlayerSpawnS2CPacket arg1);
    
    void onEntityAnimation(final EntityAnimationS2CPacket arg1);
    
    void onStatistics(final StatisticsS2CPacket arg1);
    
    void onUnlockRecipes(final UnlockRecipesS2CPacket arg1);
    
    void onBlockDestroyProgress(final BlockBreakingProgressS2CPacket arg1);
    
    void onSignEditorOpen(final SignEditorOpenS2CPacket arg1);
    
    void onBlockEntityUpdate(final BlockEntityUpdateS2CPacket arg1);
    
    void onBlockAction(final BlockActionS2CPacket arg1);
    
    void onBlockUpdate(final BlockUpdateS2CPacket arg1);
    
    void onChatMessage(final ChatMessageS2CPacket arg1);
    
    void onChunkDeltaUpdate(final ChunkDeltaUpdateS2CPacket arg1);
    
    void onMapUpdate(final MapUpdateS2CPacket arg1);
    
    void onGuiActionConfirm(final ConfirmGuiActionS2CPacket arg1);
    
    void onGuiClose(final GuiCloseS2CPacket arg1);
    
    void onInventory(final InventoryS2CPacket arg1);
    
    void onGuiOpen(final GuiOpenS2CPacket arg1);
    
    void onGuiUpdate(final GuiUpdateS2CPacket arg1);
    
    void onGuiSlotUpdate(final GuiSlotUpdateS2CPacket arg1);
    
    void onCustomPayload(final CustomPayloadS2CPacket arg1);
    
    void onDisconnect(final DisconnectS2CPacket arg1);
    
    void onEntityStatus(final EntityStatusS2CPacket arg1);
    
    void onEntityAttach(final EntityAttachS2CPacket arg1);
    
    void onEntityPassengersSet(final EntityPassengersSetS2CPacket arg1);
    
    void onExplosion(final ExplosionS2CPacket arg1);
    
    void onGameStateChange(final GameStateChangeS2CPacket arg1);
    
    void onKeepAlive(final KeepAliveS2CPacket arg1);
    
    void onChunkData(final ChunkDataS2CPacket arg1);
    
    void onUnloadChunk(final UnloadChunkS2CPacket arg1);
    
    void onWorldEvent(final WorldEventS2CPacket arg1);
    
    void onGameJoin(final GameJoinS2CPacket arg1);
    
    void onEntityUpdate(final EntityS2CPacket arg1);
    
    void onPlayerPositionLook(final PlayerPositionLookS2CPacket arg1);
    
    void onParticle(final ParticleS2CPacket arg1);
    
    void onPlayerAbilities(final PlayerAbilitiesS2CPacket arg1);
    
    void onPlayerList(final PlayerListS2CPacket arg1);
    
    void onEntitiesDestroy(final EntitiesDestroyS2CPacket arg1);
    
    void onRemoveEntityEffect(final RemoveEntityEffectS2CPacket arg1);
    
    void onPlayerRespawn(final PlayerRespawnS2CPacket arg1);
    
    void onEntitySetHeadYaw(final EntitySetHeadYawS2CPacket arg1);
    
    void onHeldItemChange(final HeldItemChangeS2CPacket arg1);
    
    void onScoreboardDisplay(final ScoreboardDisplayS2CPacket arg1);
    
    void onEntityTrackerUpdate(final EntityTrackerUpdateS2CPacket arg1);
    
    void onVelocityUpdate(final EntityVelocityUpdateS2CPacket arg1);
    
    void onEquipmentUpdate(final EntityEquipmentUpdateS2CPacket arg1);
    
    void onExperienceBarUpdate(final ExperienceBarUpdateS2CPacket arg1);
    
    void onHealthUpdate(final HealthUpdateS2CPacket arg1);
    
    void onTeam(final TeamS2CPacket arg1);
    
    void onScoreboardPlayerUpdate(final ScoreboardPlayerUpdateS2CPacket arg1);
    
    void onPlayerSpawnPosition(final PlayerSpawnPositionS2CPacket arg1);
    
    void onWorldTimeUpdate(final WorldTimeUpdateS2CPacket arg1);
    
    void onPlaySound(final PlaySoundS2CPacket arg1);
    
    void onPlaySoundFromEntity(final PlaySoundFromEntityS2CPacket arg1);
    
    void onPlaySoundId(final PlaySoundIdS2CPacket arg1);
    
    void onItemPickupAnimation(final ItemPickupAnimationS2CPacket arg1);
    
    void onEntityPosition(final EntityPositionS2CPacket arg1);
    
    void onEntityAttributes(final EntityAttributesS2CPacket arg1);
    
    void onEntityPotionEffect(final EntityPotionEffectS2CPacket arg1);
    
    void onSynchronizeTags(final SynchronizeTagsS2CPacket arg1);
    
    void onCombatEvent(final CombatEventS2CPacket arg1);
    
    void onDifficulty(final DifficultyS2CPacket arg1);
    
    void onSetCameraEntity(final SetCameraEntityS2CPacket arg1);
    
    void onWorldBorder(final WorldBorderS2CPacket arg1);
    
    void onTitle(final TitleS2CPacket arg1);
    
    void onPlayerListHeader(final PlayerListHeaderS2CPacket arg1);
    
    void onResourcePackSend(final ResourcePackSendS2CPacket arg1);
    
    void onBossBar(final BossBarS2CPacket arg1);
    
    void onCooldownUpdate(final CooldownUpdateS2CPacket arg1);
    
    void onVehicleMove(final VehicleMoveS2CPacket arg1);
    
    void onAdvancements(final AdvancementUpdateS2CPacket arg1);
    
    void onSelectAdvancementTab(final SelectAdvancementTabS2CPacket arg1);
    
    void onCraftResponse(final CraftResponseS2CPacket arg1);
    
    void onCommandTree(final CommandTreeS2CPacket arg1);
    
    void onStopSound(final StopSoundS2CPacket arg1);
    
    void onCommandSuggestions(final CommandSuggestionsS2CPacket arg1);
    
    void onSynchronizeRecipes(final SynchronizeRecipesS2CPacket arg1);
    
    void onLookAt(final LookAtS2CPacket arg1);
    
    void onTagQuery(final TagQueryResponseS2CPacket arg1);
    
    void onLightUpdate(final LightUpdateS2CPacket arg1);
    
    void onOpenWrittenBook(final OpenWrittenBookS2CPacket arg1);
    
    void onOpenContainer(final OpenContainerPacket arg1);
    
    void onSetTradeOffers(final SetTradeOffersPacket arg1);
    
    void handleChunkLoadDistance(final ChunkLoadDistanceS2CPacket arg1);
    
    void handleChunkRenderDistanceCenter(final ChunkRenderDistanceCenterS2CPacket arg1);
}
