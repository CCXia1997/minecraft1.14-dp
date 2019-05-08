package net.minecraft.item.map;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.Tag;
import java.util.Objects;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class MapBannerMarker
{
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final TextComponent name;
    
    public MapBannerMarker(final BlockPos pos, final DyeColor dyeColor, @Nullable final TextComponent name) {
        this.pos = pos;
        this.color = dyeColor;
        this.name = name;
    }
    
    public static MapBannerMarker fromNbt(final CompoundTag tag) {
        final BlockPos blockPos2 = TagHelper.deserializeBlockPos(tag.getCompound("Pos"));
        final DyeColor dyeColor3 = DyeColor.byName(tag.getString("Color"), DyeColor.a);
        final TextComponent textComponent4 = tag.containsKey("Name") ? TextComponent.Serializer.fromJsonString(tag.getString("Name")) : null;
        return new MapBannerMarker(blockPos2, dyeColor3, textComponent4);
    }
    
    @Nullable
    public static MapBannerMarker fromWorldBlock(final BlockView blockView, final BlockPos blockPos) {
        final BlockEntity blockEntity3 = blockView.getBlockEntity(blockPos);
        if (blockEntity3 instanceof BannerBlockEntity) {
            final BannerBlockEntity bannerBlockEntity4 = (BannerBlockEntity)blockEntity3;
            final DyeColor dyeColor5 = bannerBlockEntity4.getColorForState(() -> blockView.getBlockState(blockPos));
            final TextComponent textComponent6 = bannerBlockEntity4.hasCustomName() ? bannerBlockEntity4.getCustomName() : null;
            return new MapBannerMarker(blockPos, dyeColor5, textComponent6);
        }
        return null;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public MapIcon.Type getType() {
        switch (this.color) {
            case a: {
                return MapIcon.Type.k;
            }
            case b: {
                return MapIcon.Type.l;
            }
            case c: {
                return MapIcon.Type.m;
            }
            case d: {
                return MapIcon.Type.n;
            }
            case e: {
                return MapIcon.Type.o;
            }
            case f: {
                return MapIcon.Type.p;
            }
            case g: {
                return MapIcon.Type.q;
            }
            case h: {
                return MapIcon.Type.r;
            }
            case i: {
                return MapIcon.Type.s;
            }
            case j: {
                return MapIcon.Type.t;
            }
            case k: {
                return MapIcon.Type.u;
            }
            case l: {
                return MapIcon.Type.v;
            }
            case m: {
                return MapIcon.Type.w;
            }
            case n: {
                return MapIcon.Type.x;
            }
            case o: {
                return MapIcon.Type.y;
            }
            default: {
                return MapIcon.Type.z;
            }
        }
    }
    
    @Nullable
    public TextComponent getName() {
        return this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MapBannerMarker mapBannerMarker2 = (MapBannerMarker)o;
        return Objects.equals(this.pos, mapBannerMarker2.pos) && this.color == mapBannerMarker2.color && Objects.equals(this.name, mapBannerMarker2.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.pos, this.color, this.name);
    }
    
    public CompoundTag getNbt() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.put("Pos", TagHelper.serializeBlockPos(this.pos));
        compoundTag1.putString("Color", this.color.getName());
        if (this.name != null) {
            compoundTag1.putString("Name", TextComponent.Serializer.toJsonString(this.name));
        }
        return compoundTag1;
    }
    
    public String getKey() {
        return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}
