package net.minecraft.command;

import net.minecraft.text.TextFormatter;
import net.minecraft.text.TextComponent;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.Lists;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import java.util.function.Function;
import net.minecraft.util.NumberRange;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;

public class EntitySelector
{
    private final int count;
    private final boolean includeNonPlayers;
    private final boolean localWorldOnly;
    private final Predicate<Entity> basePredicate;
    private final NumberRange.FloatRange distance;
    private final Function<Vec3d, Vec3d> positionOffset;
    @Nullable
    private final BoundingBox box;
    private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
    private final boolean senderOnly;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID uuid;
    @Nullable
    private final EntityType<?> type;
    private final boolean checkPermissions;
    
    public EntitySelector(final int count, final boolean includeNonPlayers, final boolean localWorldOnly, final Predicate<Entity> basePredicate, final NumberRange.FloatRange distance, final Function<Vec3d, Vec3d> positionOffset, @Nullable final BoundingBox box, final BiConsumer<Vec3d, List<? extends Entity>> sorter, final boolean senderOnly, @Nullable final String playerName, @Nullable final UUID uuid, @Nullable final EntityType<?> type, final boolean checkPermissions) {
        this.count = count;
        this.includeNonPlayers = includeNonPlayers;
        this.localWorldOnly = localWorldOnly;
        this.basePredicate = basePredicate;
        this.distance = distance;
        this.positionOffset = positionOffset;
        this.box = box;
        this.sorter = sorter;
        this.senderOnly = senderOnly;
        this.playerName = playerName;
        this.uuid = uuid;
        this.type = type;
        this.checkPermissions = checkPermissions;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public boolean includesNonPlayers() {
        return this.includeNonPlayers;
    }
    
    public boolean isSenderOnly() {
        return this.senderOnly;
    }
    
    public boolean isLocalWorldOnly() {
        return this.localWorldOnly;
    }
    
    private void check(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        if (this.checkPermissions && !serverCommandSource.hasPermissionLevel(2)) {
            throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
        }
    }
    
    public Entity getEntity(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.check(serverCommandSource);
        final List<? extends Entity> list2 = this.getEntities(serverCommandSource);
        if (list2.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        if (list2.size() > 1) {
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
        }
        return (Entity)list2.get(0);
    }
    
    public List<? extends Entity> getEntities(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.check(serverCommandSource);
        if (!this.includeNonPlayers) {
            return this.getPlayers(serverCommandSource);
        }
        if (this.playerName != null) {
            final ServerPlayerEntity serverPlayerEntity2 = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
            if (serverPlayerEntity2 == null) {
                return Collections.emptyList();
            }
            return Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity2);
        }
        else {
            if (this.uuid != null) {
                for (final ServerWorld serverWorld3 : serverCommandSource.getMinecraftServer().getWorlds()) {
                    final Entity entity4 = serverWorld3.getEntity(this.uuid);
                    if (entity4 != null) {
                        return Lists.<Entity>newArrayList(entity4);
                    }
                }
                return Collections.emptyList();
            }
            final Vec3d vec3d2 = this.positionOffset.apply(serverCommandSource.getPosition());
            final Predicate<Entity> predicate3 = this.getPositionPredicate(vec3d2);
            if (!this.senderOnly) {
                final List<Entity> list4 = Lists.newArrayList();
                if (this.isLocalWorldOnly()) {
                    this.appendEntitiesFromWorld(list4, serverCommandSource.getWorld(), vec3d2, predicate3);
                }
                else {
                    for (final ServerWorld serverWorld4 : serverCommandSource.getMinecraftServer().getWorlds()) {
                        this.appendEntitiesFromWorld(list4, serverWorld4, vec3d2, predicate3);
                    }
                }
                return this.getEntities(vec3d2, list4);
            }
            if (serverCommandSource.getEntity() != null && predicate3.test(serverCommandSource.getEntity())) {
                return Lists.<Entity>newArrayList(serverCommandSource.getEntity());
            }
            return Collections.emptyList();
        }
    }
    
    private void appendEntitiesFromWorld(final List<Entity> list, final ServerWorld serverWorld, final Vec3d vec3d, final Predicate<Entity> predicate) {
        if (this.box != null) {
            list.addAll(serverWorld.getEntities(this.type, this.box.offset(vec3d), predicate));
        }
        else {
            list.addAll(serverWorld.getEntities(this.type, predicate));
        }
    }
    
    public ServerPlayerEntity getPlayer(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.check(serverCommandSource);
        final List<ServerPlayerEntity> list2 = this.getPlayers(serverCommandSource);
        if (list2.size() != 1) {
            throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list2.get(0);
    }
    
    public List<ServerPlayerEntity> getPlayers(final ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.check(serverCommandSource);
        if (this.playerName != null) {
            final ServerPlayerEntity serverPlayerEntity2 = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
            if (serverPlayerEntity2 == null) {
                return Collections.<ServerPlayerEntity>emptyList();
            }
            return Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity2);
        }
        else if (this.uuid != null) {
            final ServerPlayerEntity serverPlayerEntity2 = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.uuid);
            if (serverPlayerEntity2 == null) {
                return Collections.<ServerPlayerEntity>emptyList();
            }
            return Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity2);
        }
        else {
            final Vec3d vec3d2 = this.positionOffset.apply(serverCommandSource.getPosition());
            final Predicate<Entity> predicate3 = this.getPositionPredicate(vec3d2);
            if (this.senderOnly) {
                if (serverCommandSource.getEntity() instanceof ServerPlayerEntity) {
                    final ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)serverCommandSource.getEntity();
                    if (predicate3.test(serverPlayerEntity3)) {
                        return Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity3);
                    }
                }
                return Collections.<ServerPlayerEntity>emptyList();
            }
            List<ServerPlayerEntity> list4;
            if (this.isLocalWorldOnly()) {
                list4 = serverCommandSource.getWorld().getPlayers(predicate3::test);
            }
            else {
                list4 = Lists.newArrayList();
                for (final ServerPlayerEntity serverPlayerEntity4 : serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList()) {
                    if (predicate3.test(serverPlayerEntity4)) {
                        list4.add(serverPlayerEntity4);
                    }
                }
            }
            return this.<ServerPlayerEntity>getEntities(vec3d2, list4);
        }
    }
    
    private Predicate<Entity> getPositionPredicate(final Vec3d vec3d) {
        Predicate<Entity> predicate2 = this.basePredicate;
        if (this.box != null) {
            final BoundingBox boundingBox3 = this.box.offset(vec3d);
            predicate2 = predicate2.and(entity -> boundingBox3.intersects(entity.getBoundingBox()));
        }
        if (!this.distance.isDummy()) {
            predicate2 = predicate2.and(entity -> this.distance.matchesSquared(entity.squaredDistanceTo(vec3d)));
        }
        return predicate2;
    }
    
    private <T extends Entity> List<T> getEntities(final Vec3d vec3d, final List<T> list) {
        if (list.size() > 1) {
            this.sorter.accept(vec3d, list);
        }
        return list.subList(0, Math.min(this.count, list.size()));
    }
    
    public static TextComponent getNames(final List<? extends Entity> list) {
        return TextFormatter.join(list, Entity::getDisplayName);
    }
}
