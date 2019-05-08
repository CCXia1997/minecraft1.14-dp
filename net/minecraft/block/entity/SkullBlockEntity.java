package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import java.util.UUID;
import net.minecraft.util.ChatUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.util.UserCache;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Tickable;

public class SkullBlockEntity extends BlockEntity implements Tickable
{
    private GameProfile owner;
    private int ticksPowered;
    private boolean isPowered;
    private static UserCache userCache;
    private static MinecraftSessionService sessionService;
    
    public SkullBlockEntity() {
        super(BlockEntityType.SKULL);
    }
    
    public static void setUserCache(final UserCache value) {
        SkullBlockEntity.userCache = value;
    }
    
    public static void setSessionService(final MinecraftSessionService value) {
        SkullBlockEntity.sessionService = value;
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (this.owner != null) {
            final CompoundTag compoundTag2 = new CompoundTag();
            TagHelper.serializeProfile(compoundTag2, this.owner);
            compoundTag.put("Owner", compoundTag2);
        }
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("Owner", 10)) {
            this.setOwnerAndType(TagHelper.deserializeProfile(compoundTag.getCompound("Owner")));
        }
        else if (compoundTag.containsKey("ExtraType", 8)) {
            final String string2 = compoundTag.getString("ExtraType");
            if (!ChatUtil.isEmpty(string2)) {
                this.setOwnerAndType(new GameProfile((UUID)null, string2));
            }
        }
    }
    
    @Override
    public void tick() {
        final Block block1 = this.getCachedState().getBlock();
        if (block1 == Blocks.fe || block1 == Blocks.ff) {
            if (this.world.isReceivingRedstonePower(this.pos)) {
                this.isPowered = true;
                ++this.ticksPowered;
            }
            else {
                this.isPowered = false;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float getTicksPowered(final float tickDelta) {
        if (this.isPowered) {
            return this.ticksPowered + tickDelta;
        }
        return (float)this.ticksPowered;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public GameProfile getOwner() {
        return this.owner;
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 4, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    public void setOwnerAndType(@Nullable final GameProfile gameProfile) {
        this.owner = gameProfile;
        this.loadOwnerProperties();
    }
    
    private void loadOwnerProperties() {
        this.owner = loadProperties(this.owner);
        this.markDirty();
    }
    
    public static GameProfile loadProperties(final GameProfile profile) {
        if (profile == null || ChatUtil.isEmpty(profile.getName())) {
            return profile;
        }
        if (profile.isComplete() && profile.getProperties().containsKey("textures")) {
            return profile;
        }
        if (SkullBlockEntity.userCache == null || SkullBlockEntity.sessionService == null) {
            return profile;
        }
        GameProfile gameProfile2 = SkullBlockEntity.userCache.findByName(profile.getName());
        if (gameProfile2 == null) {
            return profile;
        }
        final Property property3 = Iterables.<Property>getFirst(gameProfile2.getProperties().get("textures"), (Property)null);
        if (property3 == null) {
            gameProfile2 = SkullBlockEntity.sessionService.fillProfileProperties(gameProfile2, true);
        }
        return gameProfile2;
    }
}
