package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.stream.Stream;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.google.common.collect.Lists;
import java.util.Collection;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.StringTextComponent;
import java.util.Iterator;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.BinaryOperator;
import net.minecraft.util.math.Vec2f;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import com.mojang.brigadier.ResultConsumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ServerCommandSource implements CommandSource
{
    public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION;
    public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION;
    private final CommandOutput output;
    private final Vec3d position;
    private final ServerWorld world;
    private final int level;
    private final String simpleName;
    private final TextComponent name;
    private final MinecraftServer minecraftServer;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    private final ResultConsumer<ServerCommandSource> resultConsumer;
    private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
    private final Vec2f rotation;
    
    public ServerCommandSource(final CommandOutput output, final Vec3d pos, final Vec2f rot, final ServerWorld world, final int level, final String simpleName, final TextComponent name, final MinecraftServer server, @Nullable final Entity entity) {
        this(output, pos, rot, world, level, simpleName, name, server, entity, false, (ResultConsumer<ServerCommandSource>)((commandContext, boolean2, integer) -> {}), EntityAnchorArgumentType.EntityAnchor.a);
    }
    
    protected ServerCommandSource(final CommandOutput output, final Vec3d pos, final Vec2f rot, final ServerWorld world, final int level, final String simpleName, final TextComponent name, final MinecraftServer server, @Nullable final Entity entity, final boolean silent, final ResultConsumer<ServerCommandSource> resultConsumer, final EntityAnchorArgumentType.EntityAnchor entityAnchor) {
        this.output = output;
        this.position = pos;
        this.world = world;
        this.silent = silent;
        this.entity = entity;
        this.level = level;
        this.simpleName = simpleName;
        this.name = name;
        this.minecraftServer = server;
        this.resultConsumer = resultConsumer;
        this.entityAnchor = entityAnchor;
        this.rotation = rot;
    }
    
    public ServerCommandSource withEntity(final Entity entity) {
        if (this.entity == entity) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, entity.getName().getString(), entity.getDisplayName(), this.minecraftServer, entity, this.silent, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withPosition(final Vec3d position) {
        if (this.position.equals(position)) {
            return this;
        }
        return new ServerCommandSource(this.output, position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withRotation(final Vec2f rotation) {
        if (this.rotation.equals(rotation)) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withConsumer(final ResultConsumer<ServerCommandSource> resultConsumer) {
        if (this.resultConsumer.equals(resultConsumer)) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource mergeConsumers(final ResultConsumer<ServerCommandSource> resultConsumer, final BinaryOperator<ResultConsumer<ServerCommandSource>> binaryOperator) {
        final ResultConsumer<ServerCommandSource> resultConsumer2 = (ResultConsumer<ServerCommandSource>)binaryOperator.apply(this.resultConsumer, resultConsumer);
        return this.withConsumer(resultConsumer2);
    }
    
    public ServerCommandSource withSilent() {
        if (this.silent) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, true, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withLevel(final int level) {
        if (level == this.level) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withMaxLevel(final int level) {
        if (level <= this.level) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withEntityAnchor(final EntityAnchorArgumentType.EntityAnchor anchor) {
        if (anchor == this.entityAnchor) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, anchor);
    }
    
    public ServerCommandSource withWorld(final ServerWorld world) {
        if (world == this.world) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }
    
    public ServerCommandSource withLookingAt(final Entity entity, final EntityAnchorArgumentType.EntityAnchor anchor) throws CommandSyntaxException {
        return this.withLookingAt(anchor.positionAt(entity));
    }
    
    public ServerCommandSource withLookingAt(final Vec3d position) throws CommandSyntaxException {
        final Vec3d vec3d2 = this.entityAnchor.positionAt(this);
        final double double3 = position.x - vec3d2.x;
        final double double4 = position.y - vec3d2.y;
        final double double5 = position.z - vec3d2.z;
        final double double6 = MathHelper.sqrt(double3 * double3 + double5 * double5);
        final float float11 = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(double4, double6) * 57.2957763671875)));
        final float float12 = MathHelper.wrapDegrees((float)(MathHelper.atan2(double5, double3) * 57.2957763671875) - 90.0f);
        return this.withRotation(new Vec2f(float11, float12));
    }
    
    public TextComponent getDisplayName() {
        return this.name;
    }
    
    public String getName() {
        return this.simpleName;
    }
    
    @Override
    public boolean hasPermissionLevel(final int level) {
        return this.level >= level;
    }
    
    public Vec3d getPosition() {
        return this.position;
    }
    
    public ServerWorld getWorld() {
        return this.world;
    }
    
    @Nullable
    public Entity getEntity() {
        return this.entity;
    }
    
    public Entity getEntityOrThrow() throws CommandSyntaxException {
        if (this.entity == null) {
            throw ServerCommandSource.REQUIRES_ENTITY_EXCEPTION.create();
        }
        return this.entity;
    }
    
    public ServerPlayerEntity getPlayer() throws CommandSyntaxException {
        if (!(this.entity instanceof ServerPlayerEntity)) {
            throw ServerCommandSource.REQUIRES_PLAYER_EXCEPTION.create();
        }
        return (ServerPlayerEntity)this.entity;
    }
    
    public Vec2f getRotation() {
        return this.rotation;
    }
    
    public MinecraftServer getMinecraftServer() {
        return this.minecraftServer;
    }
    
    public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
        return this.entityAnchor;
    }
    
    public void sendFeedback(final TextComponent message, final boolean broadcastToOps) {
        if (this.output.sendCommandFeedback() && !this.silent) {
            this.output.sendMessage(message);
        }
        if (broadcastToOps && this.output.shouldBroadcastConsoleToOps() && !this.silent) {
            this.sendToOps(message);
        }
    }
    
    private void sendToOps(final TextComponent message) {
        final TextComponent textComponent2 = new TranslatableTextComponent("chat.type.admin", new Object[] { this.getDisplayName(), message }).applyFormat(TextFormat.h, TextFormat.u);
        if (this.minecraftServer.getGameRules().getBoolean("sendCommandFeedback")) {
            for (final ServerPlayerEntity serverPlayerEntity4 : this.minecraftServer.getPlayerManager().getPlayerList()) {
                if (serverPlayerEntity4 != this.output && this.minecraftServer.getPlayerManager().isOperator(serverPlayerEntity4.getGameProfile())) {
                    serverPlayerEntity4.sendMessage(textComponent2);
                }
            }
        }
        if (this.output != this.minecraftServer && this.minecraftServer.getGameRules().getBoolean("logAdminCommands")) {
            this.minecraftServer.sendMessage(textComponent2);
        }
    }
    
    public void sendError(final TextComponent message) {
        if (this.output.shouldTrackOutput() && !this.silent) {
            this.output.sendMessage(new StringTextComponent("").append(message).applyFormat(TextFormat.m));
        }
    }
    
    public void onCommandComplete(final CommandContext<ServerCommandSource> context, final boolean success, final int result) {
        if (this.resultConsumer != null) {
            this.resultConsumer.onCommandComplete((CommandContext)context, success, result);
        }
    }
    
    @Override
    public Collection<String> getPlayerNames() {
        return Lists.<String>newArrayList(this.minecraftServer.getPlayerNames());
    }
    
    @Override
    public Collection<String> getTeamNames() {
        return this.minecraftServer.getScoreboard().getTeamNames();
    }
    
    @Override
    public Collection<Identifier> getSoundIds() {
        return Registry.SOUND_EVENT.getIds();
    }
    
    @Override
    public Stream<Identifier> getRecipeIds() {
        return this.minecraftServer.getRecipeManager().keys();
    }
    
    @Override
    public CompletableFuture<Suggestions> getCompletions(final CommandContext<CommandSource> context, final SuggestionsBuilder builder) {
        return null;
    }
    
    static {
        REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("permissions.requires.player", new Object[0]));
        REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("permissions.requires.entity", new Object[0]));
    }
}
