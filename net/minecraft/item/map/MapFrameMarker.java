package net.minecraft.item.map;

import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class MapFrameMarker
{
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;
    
    public MapFrameMarker(final BlockPos pos, final int rotation, final int integer3) {
        this.pos = pos;
        this.rotation = rotation;
        this.entityId = integer3;
    }
    
    public static MapFrameMarker fromNbt(final CompoundTag tag) {
        final BlockPos blockPos2 = TagHelper.deserializeBlockPos(tag.getCompound("Pos"));
        final int integer3 = tag.getInt("Rotation");
        final int integer4 = tag.getInt("EntityId");
        return new MapFrameMarker(blockPos2, integer3, integer4);
    }
    
    public CompoundTag getNbt() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.put("Pos", TagHelper.serializeBlockPos(this.pos));
        compoundTag1.putInt("Rotation", this.rotation);
        compoundTag1.putInt("EntityId", this.entityId);
        return compoundTag1;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public int getRotation() {
        return this.rotation;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public String getKey() {
        return getKey(this.pos);
    }
    
    public static String getKey(final BlockPos pos) {
        return "frame-" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }
}
