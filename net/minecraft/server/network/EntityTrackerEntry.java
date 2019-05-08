package net.minecraft.server.network;

import org.apache.logging.log4j.LogManager;
import java.util.Set;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import java.util.Collection;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import java.util.Iterator;
import net.minecraft.item.map.MapState;
import net.minecraft.item.ItemStack;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.item.FilledMapItem;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.util.math.MathHelper;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.Packet;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry
{
    private static final Logger LOGGER;
    private final ServerWorld b;
    private final Entity entity;
    private final int tickInterval;
    private final boolean alwaysUpdateVelocity;
    private final Consumer<Packet<?>> f;
    private long lastX;
    private long lastY;
    private long lastZ;
    private int lastYaw;
    private int lastPitch;
    private int lastHeadPitch;
    private Vec3d m;
    private int n;
    private int o;
    private List<Entity> lastPassengers;
    private boolean q;
    private boolean lastOnGround;
    
    public EntityTrackerEntry(final ServerWorld serverWorld, final Entity entity, final int tickInterval, final boolean boolean4, final Consumer<Packet<?>> consumer) {
        this.m = Vec3d.ZERO;
        this.lastPassengers = Collections.<Entity>emptyList();
        this.b = serverWorld;
        this.f = consumer;
        this.entity = entity;
        this.tickInterval = tickInterval;
        this.alwaysUpdateVelocity = boolean4;
        this.d();
        this.lastYaw = MathHelper.floor(entity.yaw * 256.0f / 360.0f);
        this.lastPitch = MathHelper.floor(entity.pitch * 256.0f / 360.0f);
        this.lastHeadPitch = MathHelper.floor(entity.getHeadYaw() * 256.0f / 360.0f);
        this.lastOnGround = entity.onGround;
    }
    
    public void a() {
        final List<Entity> list1 = this.entity.getPassengerList();
        if (!list1.equals(this.lastPassengers)) {
            this.lastPassengers = list1;
            this.f.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity instanceof ItemFrameEntity && this.n % 10 == 0) {
            final ItemFrameEntity itemFrameEntity2 = (ItemFrameEntity)this.entity;
            final ItemStack itemStack3 = itemFrameEntity2.getHeldItemStack();
            if (itemStack3.getItem() instanceof FilledMapItem) {
                final MapState mapState4 = FilledMapItem.getOrCreateMapState(itemStack3, this.b);
                for (final ServerPlayerEntity serverPlayerEntity6 : this.b.getPlayers()) {
                    mapState4.update(serverPlayerEntity6, itemStack3);
                    final Packet<?> packet7 = ((FilledMapItem)itemStack3.getItem()).createMapPacket(itemStack3, this.b, serverPlayerEntity6);
                    if (packet7 != null) {
                        serverPlayerEntity6.networkHandler.sendPacket(packet7);
                    }
                }
            }
            this.c();
        }
        if (this.n % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
            if (this.entity.hasVehicle()) {
                final int integer2 = MathHelper.floor(this.entity.yaw * 256.0f / 360.0f);
                final int integer3 = MathHelper.floor(this.entity.pitch * 256.0f / 360.0f);
                final boolean boolean4 = Math.abs(integer2 - this.lastYaw) >= 1 || Math.abs(integer3 - this.lastPitch) >= 1;
                if (boolean4) {
                    this.f.accept(new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)integer2, (byte)integer3, this.entity.onGround));
                    this.lastYaw = integer2;
                    this.lastPitch = integer3;
                }
                this.d();
                this.c();
                this.q = true;
            }
            else {
                ++this.o;
                final int integer2 = MathHelper.floor(this.entity.yaw * 256.0f / 360.0f);
                final int integer3 = MathHelper.floor(this.entity.pitch * 256.0f / 360.0f);
                final Vec3d vec3d4 = new Vec3d(this.entity.x, this.entity.y, this.entity.z).subtract(EntityS2CPacket.a(this.lastX, this.lastY, this.lastZ));
                final boolean boolean5 = vec3d4.lengthSquared() >= 7.62939453125E-6;
                Packet<?> packet8 = null;
                final boolean boolean6 = boolean5 || this.n % 60 == 0;
                final boolean boolean7 = Math.abs(integer2 - this.lastYaw) >= 1 || Math.abs(integer3 - this.lastPitch) >= 1;
                if (this.n > 0 || this.entity instanceof ProjectileEntity) {
                    final long long9 = EntityS2CPacket.a(vec3d4.x);
                    final long long10 = EntityS2CPacket.a(vec3d4.y);
                    final long long11 = EntityS2CPacket.a(vec3d4.z);
                    final boolean boolean8 = long9 < -32768L || long9 > 32767L || long10 < -32768L || long10 > 32767L || long11 < -32768L || long11 > 32767L;
                    if (boolean8 || this.o > 400 || this.q || this.lastOnGround != this.entity.onGround) {
                        this.lastOnGround = this.entity.onGround;
                        this.o = 0;
                        packet8 = new EntityPositionS2CPacket(this.entity);
                    }
                    else if ((boolean6 && boolean7) || this.entity instanceof ProjectileEntity) {
                        packet8 = new EntityS2CPacket.RotateAndMoveRelative(this.entity.getEntityId(), (short)long9, (short)long10, (short)long11, (byte)integer2, (byte)integer3, this.entity.onGround);
                    }
                    else if (boolean6) {
                        packet8 = new EntityS2CPacket.MoveRelative(this.entity.getEntityId(), (short)long9, (short)long10, (short)long11, this.entity.onGround);
                    }
                    else if (boolean7) {
                        packet8 = new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)integer2, (byte)integer3, this.entity.onGround);
                    }
                }
                if ((this.alwaysUpdateVelocity || this.entity.velocityDirty || (this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying())) && this.n > 0) {
                    final Vec3d vec3d5 = this.entity.getVelocity();
                    final double double10 = vec3d5.squaredDistanceTo(this.m);
                    if (double10 > 1.0E-7 || (double10 > 0.0 && vec3d5.lengthSquared() == 0.0)) {
                        this.m = vec3d5;
                        this.f.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.m));
                    }
                }
                if (packet8 != null) {
                    this.f.accept(packet8);
                }
                this.c();
                if (boolean6) {
                    this.d();
                }
                if (boolean7) {
                    this.lastYaw = integer2;
                    this.lastPitch = integer3;
                }
                this.q = false;
            }
            final int integer2 = MathHelper.floor(this.entity.getHeadYaw() * 256.0f / 360.0f);
            if (Math.abs(integer2 - this.lastHeadPitch) >= 1) {
                this.f.accept(new EntitySetHeadYawS2CPacket(this.entity, (byte)integer2));
                this.lastHeadPitch = integer2;
            }
            this.entity.velocityDirty = false;
        }
        ++this.n;
        if (this.entity.velocityModified) {
            this.a(new EntityVelocityUpdateS2CPacket(this.entity));
            this.entity.velocityModified = false;
        }
    }
    
    public void stopTracking(final ServerPlayerEntity serverPlayerEntity) {
        this.entity.onStoppedTrackingBy(serverPlayerEntity);
        serverPlayerEntity.onStoppedTracking(this.entity);
    }
    
    public void startTracking(final ServerPlayerEntity serverPlayerEntity) {
        this.sendPackets(serverPlayerEntity.networkHandler::sendPacket);
        this.entity.onStartedTrackingBy(serverPlayerEntity);
        serverPlayerEntity.onStartedTracking(this.entity);
    }
    
    public void sendPackets(final Consumer<Packet<?>> sender) {
        if (this.entity.removed) {
            EntityTrackerEntry.LOGGER.warn("Fetching addPacket for removed entity");
        }
        final Packet<?> packet2 = this.entity.createSpawnPacket();
        this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0f / 360.0f);
        sender.accept(packet2);
        if (!this.entity.getDataTracker().isEmpty()) {
            sender.accept(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), this.entity.getDataTracker(), true));
        }
        boolean boolean3 = this.alwaysUpdateVelocity;
        if (this.entity instanceof LivingEntity) {
            final EntityAttributeContainer entityAttributeContainer4 = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributeContainer();
            final Collection<EntityAttributeInstance> collection5 = entityAttributeContainer4.buildTrackedAttributesCollection();
            if (!collection5.isEmpty()) {
                sender.accept(new EntityAttributesS2CPacket(this.entity.getEntityId(), collection5));
            }
            if (((LivingEntity)this.entity).isFallFlying()) {
                boolean3 = true;
            }
        }
        this.m = this.entity.getVelocity();
        if (boolean3 && !(packet2 instanceof MobSpawnS2CPacket)) {
            sender.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.m));
        }
        if (this.entity instanceof LivingEntity) {
            for (final EquipmentSlot equipmentSlot7 : EquipmentSlot.values()) {
                final ItemStack itemStack8 = ((LivingEntity)this.entity).getEquippedStack(equipmentSlot7);
                if (!itemStack8.isEmpty()) {
                    sender.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getEntityId(), equipmentSlot7, itemStack8));
                }
            }
        }
        if (this.entity instanceof LivingEntity) {
            final LivingEntity livingEntity4 = (LivingEntity)this.entity;
            for (final StatusEffectInstance statusEffectInstance6 : livingEntity4.getStatusEffects()) {
                sender.accept(new EntityPotionEffectS2CPacket(this.entity.getEntityId(), statusEffectInstance6));
            }
        }
        if (!this.entity.getPassengerList().isEmpty()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity.hasVehicle()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
        }
    }
    
    private void c() {
        final DataTracker dataTracker1 = this.entity.getDataTracker();
        if (dataTracker1.isDirty()) {
            this.a(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), dataTracker1, false));
        }
        if (this.entity instanceof LivingEntity) {
            final EntityAttributeContainer entityAttributeContainer2 = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributeContainer();
            final Set<EntityAttributeInstance> set3 = entityAttributeContainer2.getTrackedAttributes();
            if (!set3.isEmpty()) {
                this.a(new EntityAttributesS2CPacket(this.entity.getEntityId(), set3));
            }
            set3.clear();
        }
    }
    
    private void d() {
        this.lastX = EntityS2CPacket.a(this.entity.x);
        this.lastY = EntityS2CPacket.a(this.entity.y);
        this.lastZ = EntityS2CPacket.a(this.entity.z);
    }
    
    public Vec3d b() {
        return EntityS2CPacket.a(this.lastX, this.lastY, this.lastZ);
    }
    
    private void a(final Packet<?> packet) {
        this.f.accept(packet);
        if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
