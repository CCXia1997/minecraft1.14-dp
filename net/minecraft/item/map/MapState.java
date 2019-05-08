package net.minecraft.item.map;

import java.util.AbstractList;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.item.FilledMapItem;
import net.minecraft.network.Packet;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.TextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Map;
import java.util.List;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.PersistentState;

public class MapState extends PersistentState
{
    public int xCenter;
    public int zCenter;
    public DimensionType dimension;
    public boolean showIcons;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors;
    public boolean locked;
    public final List<PlayerUpdateTracker> updateTrackers;
    private final Map<PlayerEntity, PlayerUpdateTracker> updateTrackersByPlayer;
    private final Map<String, MapBannerMarker> banners;
    public final Map<String, MapIcon> icons;
    private final Map<String, MapFrameMarker> frames;
    
    public MapState(final String key) {
        super(key);
        this.colors = new byte[16384];
        this.updateTrackers = Lists.newArrayList();
        this.updateTrackersByPlayer = Maps.newHashMap();
        this.banners = Maps.newHashMap();
        this.icons = Maps.newLinkedHashMap();
        this.frames = Maps.newHashMap();
    }
    
    public void init(final int integer1, final int integer2, final int scale, final boolean showIcons, final boolean unlimitedTracking, final DimensionType dimensionType) {
        this.scale = (byte)scale;
        this.calculateCenter(integer1, integer2, this.scale);
        this.dimension = dimensionType;
        this.showIcons = showIcons;
        this.unlimitedTracking = unlimitedTracking;
        this.markDirty();
    }
    
    public void calculateCenter(final double double1, final double double3, final int integer5) {
        final int integer6 = 128 * (1 << integer5);
        final int integer7 = MathHelper.floor((double1 + 64.0) / integer6);
        final int integer8 = MathHelper.floor((double3 + 64.0) / integer6);
        this.xCenter = integer7 * integer6 + integer6 / 2 - 64;
        this.zCenter = integer8 * integer6 + integer6 / 2 - 64;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        this.dimension = DimensionType.byRawId(compoundTag.getInt("dimension"));
        this.xCenter = compoundTag.getInt("xCenter");
        this.zCenter = compoundTag.getInt("zCenter");
        this.scale = (byte)MathHelper.clamp(compoundTag.getByte("scale"), 0, 4);
        this.showIcons = (!compoundTag.containsKey("trackingPosition", 1) || compoundTag.getBoolean("trackingPosition"));
        this.unlimitedTracking = compoundTag.getBoolean("unlimitedTracking");
        this.locked = compoundTag.getBoolean("locked");
        this.colors = compoundTag.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        final ListTag listTag2 = compoundTag.getList("banners", 10);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            final MapBannerMarker mapBannerMarker4 = MapBannerMarker.fromNbt(listTag2.getCompoundTag(integer3));
            this.banners.put(mapBannerMarker4.getKey(), mapBannerMarker4);
            this.addIcon(mapBannerMarker4.getType(), null, mapBannerMarker4.getKey(), mapBannerMarker4.getPos().getX(), mapBannerMarker4.getPos().getZ(), 180.0, mapBannerMarker4.getName());
        }
        final ListTag listTag3 = compoundTag.getList("frames", 10);
        for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
            final MapFrameMarker mapFrameMarker5 = MapFrameMarker.fromNbt(listTag3.getCompoundTag(integer4));
            this.frames.put(mapFrameMarker5.getKey(), mapFrameMarker5);
            this.addIcon(MapIcon.Type.b, null, "frame-" + mapFrameMarker5.getEntityId(), mapFrameMarker5.getPos().getX(), mapFrameMarker5.getPos().getZ(), mapFrameMarker5.getRotation(), null);
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        compoundTag.putInt("dimension", this.dimension.getRawId());
        compoundTag.putInt("xCenter", this.xCenter);
        compoundTag.putInt("zCenter", this.zCenter);
        compoundTag.putByte("scale", this.scale);
        compoundTag.putByteArray("colors", this.colors);
        compoundTag.putBoolean("trackingPosition", this.showIcons);
        compoundTag.putBoolean("unlimitedTracking", this.unlimitedTracking);
        compoundTag.putBoolean("locked", this.locked);
        final ListTag listTag2 = new ListTag();
        for (final MapBannerMarker mapBannerMarker4 : this.banners.values()) {
            ((AbstractList<CompoundTag>)listTag2).add(mapBannerMarker4.getNbt());
        }
        compoundTag.put("banners", listTag2);
        final ListTag listTag3 = new ListTag();
        for (final MapFrameMarker mapFrameMarker5 : this.frames.values()) {
            ((AbstractList<CompoundTag>)listTag3).add(mapFrameMarker5.getNbt());
        }
        compoundTag.put("frames", listTag3);
        return compoundTag;
    }
    
    public void copyFrom(final MapState state) {
        this.locked = true;
        this.xCenter = state.xCenter;
        this.zCenter = state.zCenter;
        this.banners.putAll(state.banners);
        this.icons.putAll(state.icons);
        System.arraycopy(state.colors, 0, this.colors, 0, state.colors.length);
        this.markDirty();
    }
    
    public void update(final PlayerEntity player, final ItemStack stack) {
        if (!this.updateTrackersByPlayer.containsKey(player)) {
            final PlayerUpdateTracker playerUpdateTracker3 = new PlayerUpdateTracker(player);
            this.updateTrackersByPlayer.put(player, playerUpdateTracker3);
            this.updateTrackers.add(playerUpdateTracker3);
        }
        if (!player.inventory.contains(stack)) {
            this.icons.remove(player.getName().getString());
        }
        for (int integer3 = 0; integer3 < this.updateTrackers.size(); ++integer3) {
            final PlayerUpdateTracker playerUpdateTracker4 = this.updateTrackers.get(integer3);
            final String string5 = playerUpdateTracker4.player.getName().getString();
            if (playerUpdateTracker4.player.removed || (!playerUpdateTracker4.player.inventory.contains(stack) && !stack.isHeldInItemFrame())) {
                this.updateTrackersByPlayer.remove(playerUpdateTracker4.player);
                this.updateTrackers.remove(playerUpdateTracker4);
                this.icons.remove(string5);
            }
            else if (!stack.isHeldInItemFrame() && playerUpdateTracker4.player.dimension == this.dimension && this.showIcons) {
                this.addIcon(MapIcon.Type.a, playerUpdateTracker4.player.world, string5, playerUpdateTracker4.player.x, playerUpdateTracker4.player.z, playerUpdateTracker4.player.yaw, null);
            }
        }
        if (stack.isHeldInItemFrame() && this.showIcons) {
            final ItemFrameEntity itemFrameEntity3 = stack.getHoldingItemFrame();
            final BlockPos blockPos4 = itemFrameEntity3.getDecorationBlockPos();
            final MapFrameMarker mapFrameMarker5 = this.frames.get(MapFrameMarker.getKey(blockPos4));
            if (mapFrameMarker5 != null && itemFrameEntity3.getEntityId() != mapFrameMarker5.getEntityId() && this.frames.containsKey(mapFrameMarker5.getKey())) {
                this.icons.remove("frame-" + mapFrameMarker5.getEntityId());
            }
            final MapFrameMarker mapFrameMarker6 = new MapFrameMarker(blockPos4, itemFrameEntity3.facing.getHorizontal() * 90, itemFrameEntity3.getEntityId());
            this.addIcon(MapIcon.Type.b, player.world, "frame-" + itemFrameEntity3.getEntityId(), blockPos4.getX(), blockPos4.getZ(), itemFrameEntity3.facing.getHorizontal() * 90, null);
            this.frames.put(mapFrameMarker6.getKey(), mapFrameMarker6);
        }
        final CompoundTag compoundTag3 = stack.getTag();
        if (compoundTag3 != null && compoundTag3.containsKey("Decorations", 9)) {
            final ListTag listTag4 = compoundTag3.getList("Decorations", 10);
            for (int integer4 = 0; integer4 < listTag4.size(); ++integer4) {
                final CompoundTag compoundTag4 = listTag4.getCompoundTag(integer4);
                if (!this.icons.containsKey(compoundTag4.getString("id"))) {
                    this.addIcon(MapIcon.Type.byId(compoundTag4.getByte("type")), player.world, compoundTag4.getString("id"), compoundTag4.getDouble("x"), compoundTag4.getDouble("z"), compoundTag4.getDouble("rot"), null);
                }
            }
        }
    }
    
    public static void addDecorationsTag(final ItemStack itemStack, final BlockPos blockPos, final String id, final MapIcon.Type type) {
        ListTag listTag5;
        if (itemStack.hasTag() && itemStack.getTag().containsKey("Decorations", 9)) {
            listTag5 = itemStack.getTag().getList("Decorations", 10);
        }
        else {
            listTag5 = new ListTag();
            itemStack.setChildTag("Decorations", listTag5);
        }
        final CompoundTag compoundTag6 = new CompoundTag();
        compoundTag6.putByte("type", type.getId());
        compoundTag6.putString("id", id);
        compoundTag6.putDouble("x", blockPos.getX());
        compoundTag6.putDouble("z", blockPos.getZ());
        compoundTag6.putDouble("rot", 180.0);
        ((AbstractList<CompoundTag>)listTag5).add(compoundTag6);
        if (type.hasTintColor()) {
            final CompoundTag compoundTag7 = itemStack.getOrCreateSubCompoundTag("display");
            compoundTag7.putInt("MapColor", type.getTintColor());
        }
    }
    
    private void addIcon(MapIcon.Type type, @Nullable final IWorld world, final String key, final double x, final double z, double rotation, @Nullable final TextComponent text) {
        final int integer11 = 1 << this.scale;
        final float float12 = (float)(x - this.xCenter) / integer11;
        final float float13 = (float)(z - this.zCenter) / integer11;
        byte byte14 = (byte)(float12 * 2.0f + 0.5);
        byte byte15 = (byte)(float13 * 2.0f + 0.5);
        final int integer12 = 63;
        byte byte16;
        if (float12 >= -63.0f && float13 >= -63.0f && float12 <= 63.0f && float13 <= 63.0f) {
            rotation += ((rotation < 0.0) ? -8.0 : 8.0);
            byte16 = (byte)(rotation * 16.0 / 360.0);
            if (this.dimension == DimensionType.b && world != null) {
                final int integer13 = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
                byte16 = (byte)(integer13 * integer13 * 34187121 + integer13 * 121 >> 15 & 0xF);
            }
        }
        else {
            if (type != MapIcon.Type.a) {
                this.icons.remove(key);
                return;
            }
            final int integer13 = 320;
            if (Math.abs(float12) < 320.0f && Math.abs(float13) < 320.0f) {
                type = MapIcon.Type.g;
            }
            else {
                if (!this.unlimitedTracking) {
                    this.icons.remove(key);
                    return;
                }
                type = MapIcon.Type.h;
            }
            byte16 = 0;
            if (float12 <= -63.0f) {
                byte14 = -128;
            }
            if (float13 <= -63.0f) {
                byte15 = -128;
            }
            if (float12 >= 63.0f) {
                byte14 = 127;
            }
            if (float13 >= 63.0f) {
                byte15 = 127;
            }
        }
        this.icons.put(key, new MapIcon(type, byte14, byte15, byte16, text));
    }
    
    @Nullable
    public Packet<?> getPlayerMarkerPacket(final ItemStack map, final BlockView world, final PlayerEntity pos) {
        final PlayerUpdateTracker playerUpdateTracker4 = this.updateTrackersByPlayer.get(pos);
        if (playerUpdateTracker4 == null) {
            return null;
        }
        return playerUpdateTracker4.getPacket(map);
    }
    
    public void markDirty(final int x, final int z) {
        this.markDirty();
        for (final PlayerUpdateTracker playerUpdateTracker4 : this.updateTrackers) {
            playerUpdateTracker4.markDirty(x, z);
        }
    }
    
    public PlayerUpdateTracker getPlayerSyncData(final PlayerEntity player) {
        PlayerUpdateTracker playerUpdateTracker2 = this.updateTrackersByPlayer.get(player);
        if (playerUpdateTracker2 == null) {
            playerUpdateTracker2 = new PlayerUpdateTracker(player);
            this.updateTrackersByPlayer.put(player, playerUpdateTracker2);
            this.updateTrackers.add(playerUpdateTracker2);
        }
        return playerUpdateTracker2;
    }
    
    public void addBanner(final IWorld world, final BlockPos pos) {
        final float float3 = pos.getX() + 0.5f;
        final float float4 = pos.getZ() + 0.5f;
        final int integer5 = 1 << this.scale;
        final float float5 = (float3 - this.xCenter) / integer5;
        final float float6 = (float4 - this.zCenter) / integer5;
        final int integer6 = 63;
        boolean boolean9 = false;
        if (float5 >= -63.0f && float6 >= -63.0f && float5 <= 63.0f && float6 <= 63.0f) {
            final MapBannerMarker mapBannerMarker10 = MapBannerMarker.fromWorldBlock(world, pos);
            if (mapBannerMarker10 == null) {
                return;
            }
            boolean boolean10 = true;
            if (this.banners.containsKey(mapBannerMarker10.getKey()) && this.banners.get(mapBannerMarker10.getKey()).equals(mapBannerMarker10)) {
                this.banners.remove(mapBannerMarker10.getKey());
                this.icons.remove(mapBannerMarker10.getKey());
                boolean10 = false;
                boolean9 = true;
            }
            if (boolean10) {
                this.banners.put(mapBannerMarker10.getKey(), mapBannerMarker10);
                this.addIcon(mapBannerMarker10.getType(), world, mapBannerMarker10.getKey(), float3, float4, 180.0, mapBannerMarker10.getName());
                boolean9 = true;
            }
            if (boolean9) {
                this.markDirty();
            }
        }
    }
    
    public void removeBanner(final BlockView world, final int x, final int z) {
        final Iterator<MapBannerMarker> iterator4 = this.banners.values().iterator();
        while (iterator4.hasNext()) {
            final MapBannerMarker mapBannerMarker5 = iterator4.next();
            if (mapBannerMarker5.getPos().getX() == x && mapBannerMarker5.getPos().getZ() == z) {
                final MapBannerMarker mapBannerMarker6 = MapBannerMarker.fromWorldBlock(world, mapBannerMarker5.getPos());
                if (mapBannerMarker5.equals(mapBannerMarker6)) {
                    continue;
                }
                iterator4.remove();
                this.icons.remove(mapBannerMarker5.getKey());
            }
        }
    }
    
    public void removeFrame(final BlockPos pos, final int id) {
        this.icons.remove("frame-" + id);
        this.frames.remove(MapFrameMarker.getKey(pos));
    }
    
    public class PlayerUpdateTracker
    {
        public final PlayerEntity player;
        private boolean dirty;
        private int startX;
        private int startZ;
        private int endX;
        private int endZ;
        private int emptyPacketsRequested;
        public int b;
        
        public PlayerUpdateTracker(final PlayerEntity playerEntity) {
            this.dirty = true;
            this.endX = 127;
            this.endZ = 127;
            this.player = playerEntity;
        }
        
        @Nullable
        public Packet<?> getPacket(final ItemStack itemStack) {
            if (this.dirty) {
                this.dirty = false;
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(itemStack), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, this.startX, this.startZ, this.endX + 1 - this.startX, this.endZ + 1 - this.startZ);
            }
            if (this.emptyPacketsRequested++ % 5 == 0) {
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(itemStack), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0);
            }
            return null;
        }
        
        public void markDirty(final int x, final int z) {
            if (this.dirty) {
                this.startX = Math.min(this.startX, x);
                this.startZ = Math.min(this.startZ, z);
                this.endX = Math.max(this.endX, x);
                this.endZ = Math.max(this.endZ, z);
            }
            else {
                this.dirty = true;
                this.startX = x;
                this.startZ = z;
                this.endX = x;
                this.endZ = z;
            }
        }
    }
}
