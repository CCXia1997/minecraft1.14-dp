package net.minecraft.client.network;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import java.util.Locale;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import java.util.Collections;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collection;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

@Environment(EnvType.CLIENT)
public class ClientCommandSource implements CommandSource
{
    private final ClientPlayNetworkHandler networkHandler;
    private final MinecraftClient client;
    private int completionId;
    private CompletableFuture<Suggestions> pendingCompletion;
    
    public ClientCommandSource(final ClientPlayNetworkHandler client, final MinecraftClient minecraftClient) {
        this.completionId = -1;
        this.networkHandler = client;
        this.client = minecraftClient;
    }
    
    @Override
    public Collection<String> getPlayerNames() {
        final List<String> list1 = Lists.newArrayList();
        for (final PlayerListEntry playerListEntry3 : this.networkHandler.getPlayerList()) {
            list1.add(playerListEntry3.getProfile().getName());
        }
        return list1;
    }
    
    @Override
    public Collection<String> getEntitySuggestions() {
        if (this.client.hitResult != null && this.client.hitResult.getType() == HitResult.Type.ENTITY) {
            return Collections.<String>singleton(((EntityHitResult)this.client.hitResult).getEntity().getUuidAsString());
        }
        return Collections.emptyList();
    }
    
    @Override
    public Collection<String> getTeamNames() {
        return this.networkHandler.getWorld().getScoreboard().getTeamNames();
    }
    
    @Override
    public Collection<Identifier> getSoundIds() {
        return this.client.getSoundManager().getKeys();
    }
    
    @Override
    public Stream<Identifier> getRecipeIds() {
        return this.networkHandler.getRecipeManager().keys();
    }
    
    @Override
    public boolean hasPermissionLevel(final int level) {
        final ClientPlayerEntity clientPlayerEntity2 = this.client.player;
        return (clientPlayerEntity2 != null) ? clientPlayerEntity2.allowsPermissionLevel(level) : (level == 0);
    }
    
    @Override
    public CompletableFuture<Suggestions> getCompletions(final CommandContext<CommandSource> context, final SuggestionsBuilder builder) {
        if (this.pendingCompletion != null) {
            this.pendingCompletion.cancel(false);
        }
        this.pendingCompletion = new CompletableFuture<Suggestions>();
        final int integer3 = ++this.completionId;
        this.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(integer3, context.getInput()));
        return this.pendingCompletion;
    }
    
    private static String formatDouble(final double double1) {
        return String.format(Locale.ROOT, "%.2f", double1);
    }
    
    private static String formatInt(final int integer) {
        return Integer.toString(integer);
    }
    
    @Override
    public Collection<RelativePosition> getBlockPositionSuggestions() {
        final HitResult hitResult1 = this.client.hitResult;
        if (hitResult1 == null || hitResult1.getType() != HitResult.Type.BLOCK) {
            return super.getBlockPositionSuggestions();
        }
        final BlockPos blockPos2 = ((BlockHitResult)hitResult1).getBlockPos();
        return Collections.<RelativePosition>singleton(new RelativePosition(formatInt(blockPos2.getX()), formatInt(blockPos2.getY()), formatInt(blockPos2.getZ())));
    }
    
    @Override
    public Collection<RelativePosition> getPositionSuggestions() {
        final HitResult hitResult1 = this.client.hitResult;
        if (hitResult1 == null || hitResult1.getType() != HitResult.Type.BLOCK) {
            return super.getPositionSuggestions();
        }
        final Vec3d vec3d2 = hitResult1.getPos();
        return Collections.<RelativePosition>singleton(new RelativePosition(formatDouble(vec3d2.x), formatDouble(vec3d2.y), formatDouble(vec3d2.z)));
    }
    
    public void onCommandSuggestions(final int completionId, final Suggestions suggestions) {
        if (completionId == this.completionId) {
            this.pendingCompletion.complete(suggestions);
            this.pendingCompletion = null;
            this.completionId = -1;
        }
    }
}
