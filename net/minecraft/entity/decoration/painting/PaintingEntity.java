package net.minecraft.entity.decoration.painting;

import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;

public class PaintingEntity extends AbstractDecorationEntity
{
    public PaintingMotive motive;
    
    public PaintingEntity(final EntityType<? extends PaintingEntity> type, final World world) {
        super(type, world);
    }
    
    public PaintingEntity(final World world, final BlockPos pos, final Direction direction) {
        super(EntityType.PAINTING, world, pos);
        final List<PaintingMotive> list4 = Lists.newArrayList();
        int integer5 = 0;
        for (final PaintingMotive paintingMotive7 : Registry.MOTIVE) {
            this.motive = paintingMotive7;
            this.setFacing(direction);
            if (this.i()) {
                list4.add(paintingMotive7);
                final int integer6 = paintingMotive7.getWidth() * paintingMotive7.getHeight();
                if (integer6 <= integer5) {
                    continue;
                }
                integer5 = integer6;
            }
        }
        if (!list4.isEmpty()) {
            final Iterator<PaintingMotive> iterator6 = list4.iterator();
            while (iterator6.hasNext()) {
                final PaintingMotive paintingMotive7 = iterator6.next();
                if (paintingMotive7.getWidth() * paintingMotive7.getHeight() < integer5) {
                    iterator6.remove();
                }
            }
            this.motive = list4.get(this.random.nextInt(list4.size()));
        }
        this.setFacing(direction);
    }
    
    @Environment(EnvType.CLIENT)
    public PaintingEntity(final World world, final BlockPos blockPos, final Direction direction, final PaintingMotive paintingMotive) {
        this(world, blockPos, direction);
        this.motive = paintingMotive;
        this.setFacing(direction);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putString("Motive", Registry.MOTIVE.getId(this.motive).toString());
        super.writeCustomDataToTag(tag);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.motive = Registry.MOTIVE.get(Identifier.create(tag.getString("Motive")));
        super.readCustomDataFromTag(tag);
    }
    
    @Override
    public int getWidthPixels() {
        return this.motive.getWidth();
    }
    
    @Override
    public int getHeightPixels() {
        return this.motive.getHeight();
    }
    
    @Override
    public void onBreak(@Nullable final Entity entity) {
        if (!this.world.getGameRules().getBoolean("doEntityDrops")) {
            return;
        }
        this.playSound(SoundEvents.hh, 1.0f, 1.0f);
        if (entity instanceof PlayerEntity) {
            final PlayerEntity playerEntity2 = (PlayerEntity)entity;
            if (playerEntity2.abilities.creativeMode) {
                return;
            }
        }
        this.dropItem(Items.ko);
    }
    
    @Override
    public void onPlace() {
        this.playSound(SoundEvents.hi, 1.0f, 1.0f);
    }
    
    @Override
    public void setPositionAndAngles(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.setPosition(x, y, z);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
        final BlockPos blockPos11 = this.blockPos.add(x - this.x, y - this.y, z - this.z);
        this.setPosition(blockPos11.getX(), blockPos11.getY(), blockPos11.getZ());
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new PaintingSpawnS2CPacket(this);
    }
}
