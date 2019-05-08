package net.minecraft.server.command;

import net.minecraft.command.arguments.DefaultPosArgument;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.server.world.ServerWorld;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class TeleportCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> literalCommandNode2 = (LiteralCommandNode<ServerCommandSource>)dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("teleport").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("location", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, null))).then(CommandManager.argument("rotation", (com.mojang.brigadier.arguments.ArgumentType<Object>)RotationArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), RotationArgumentType.getRotation((CommandContext<ServerCommandSource>)commandContext, "rotation"), null)))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("facingEntity", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entity()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.a)))).then(CommandManager.argument("facingAnchor", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityAnchorArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "facingAnchor")))))))).then(CommandManager.argument("facingLocation", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "facingLocation")))))))).then(CommandManager.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entity()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "destination")))))).then(CommandManager.argument("location", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), Collections.<Entity>singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow()), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), DefaultPosArgument.zero(), null)))).then(CommandManager.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entity()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), Collections.<Entity>singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow()), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "destination")))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).redirect((CommandNode)literalCommandNode2));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<? extends Entity> targets, final Entity destination) {
        for (final Entity entity5 : targets) {
            teleport(source, entity5, source.getWorld(), destination.x, destination.y, destination.z, EnumSet.<PlayerPositionLookS2CPacket.Flag>noneOf(PlayerPositionLookS2CPacket.Flag.class), destination.yaw, destination.pitch, null);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.teleport.success.entity.single", new Object[] { ((Entity)targets.iterator().next()).getDisplayName(), destination.getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.teleport.success.entity.multiple", new Object[] { targets.size(), destination.getDisplayName() }), true);
        }
        return targets.size();
    }
    
    private static int execute(final ServerCommandSource source, final Collection<? extends Entity> targets, final ServerWorld world, final PosArgument location, @Nullable final PosArgument rotation, @Nullable final LookTarget facingLocation) throws CommandSyntaxException {
        final Vec3d vec3d7 = location.toAbsolutePos(source);
        final Vec2f vec2f8 = (rotation == null) ? null : rotation.toAbsoluteRotation(source);
        final Set<PlayerPositionLookS2CPacket.Flag> set9 = EnumSet.<PlayerPositionLookS2CPacket.Flag>noneOf(PlayerPositionLookS2CPacket.Flag.class);
        if (location.isXRelative()) {
            set9.add(PlayerPositionLookS2CPacket.Flag.X);
        }
        if (location.isYRelative()) {
            set9.add(PlayerPositionLookS2CPacket.Flag.Y);
        }
        if (location.isZRelative()) {
            set9.add(PlayerPositionLookS2CPacket.Flag.Z);
        }
        if (rotation == null) {
            set9.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            set9.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
        }
        else {
            if (rotation.isXRelative()) {
                set9.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            }
            if (rotation.isYRelative()) {
                set9.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
            }
        }
        for (final Entity entity11 : targets) {
            if (rotation == null) {
                teleport(source, entity11, world, vec3d7.x, vec3d7.y, vec3d7.z, set9, entity11.yaw, entity11.pitch, facingLocation);
            }
            else {
                teleport(source, entity11, world, vec3d7.x, vec3d7.y, vec3d7.z, set9, vec2f8.y, vec2f8.x, facingLocation);
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.teleport.success.location.single", new Object[] { ((Entity)targets.iterator().next()).getDisplayName(), vec3d7.x, vec3d7.y, vec3d7.z }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.teleport.success.location.multiple", new Object[] { targets.size(), vec3d7.x, vec3d7.y, vec3d7.z }), true);
        }
        return targets.size();
    }
    
    private static void teleport(final ServerCommandSource source, Entity target, final ServerWorld world, final double x, final double y, final double z, final Set<PlayerPositionLookS2CPacket.Flag> movementFlags, final float yaw, final float pitch, @Nullable final LookTarget facingLocation) {
        if (target instanceof ServerPlayerEntity) {
            target.stopRiding();
            if (((ServerPlayerEntity)target).isSleeping()) {
                ((ServerPlayerEntity)target).wakeUp(true, true, false);
            }
            if (world == target.world) {
                ((ServerPlayerEntity)target).networkHandler.teleportRequest(x, y, z, yaw, pitch, movementFlags);
            }
            else {
                ((ServerPlayerEntity)target).teleport(world, x, y, z, yaw, pitch);
            }
            target.setHeadYaw(yaw);
        }
        else {
            final float float14 = MathHelper.wrapDegrees(yaw);
            float float15 = MathHelper.wrapDegrees(pitch);
            float15 = MathHelper.clamp(float15, -90.0f, 90.0f);
            if (world == target.world) {
                target.setPositionAndAngles(x, y, z, float14, float15);
                target.setHeadYaw(float14);
            }
            else {
                target.detach();
                target.dimension = world.dimension.getType();
                final Entity entity16 = target;
                target = (Entity)entity16.getType().create(world);
                if (target == null) {
                    return;
                }
                target.v(entity16);
                target.setPositionAndAngles(x, y, z, float14, float15);
                target.setHeadYaw(float14);
                world.e(target);
                entity16.removed = true;
            }
        }
        if (facingLocation != null) {
            facingLocation.look(source, target);
        }
        if (!(target instanceof LivingEntity) || !((LivingEntity)target).isFallFlying()) {
            target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
            target.onGround = true;
        }
    }
    
    static class LookTarget
    {
        private final Vec3d targetPos;
        private final Entity targetEntity;
        private final EntityAnchorArgumentType.EntityAnchor targetEntityAnchor;
        
        public LookTarget(final Entity entity, final EntityAnchorArgumentType.EntityAnchor entityAnchor) {
            this.targetEntity = entity;
            this.targetEntityAnchor = entityAnchor;
            this.targetPos = entityAnchor.positionAt(entity);
        }
        
        public LookTarget(final Vec3d vec3d) {
            this.targetEntity = null;
            this.targetPos = vec3d;
            this.targetEntityAnchor = null;
        }
        
        public void look(final ServerCommandSource source, final Entity entity) {
            if (this.targetEntity != null) {
                if (entity instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)entity).a(source.getEntityAnchor(), this.targetEntity, this.targetEntityAnchor);
                }
                else {
                    entity.lookAt(source.getEntityAnchor(), this.targetPos);
                }
            }
            else {
                entity.lookAt(source.getEntityAnchor(), this.targetPos);
            }
        }
    }
}
