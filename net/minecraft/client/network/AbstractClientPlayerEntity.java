package net.minecraft.client.network;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.Items;
import net.minecraft.entity.attribute.EntityAttributes;
import com.google.common.hash.Hashing;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.ImageFilter;
import java.io.File;
import net.minecraft.client.texture.SkinRemappingImageFilter;
import net.minecraft.util.ChatUtil;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import javax.annotation.Nullable;
import net.minecraft.world.GameMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.world.ClientWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity
{
    private PlayerListEntry cachedScoreboardEntry;
    public float elytraPitch;
    public float elytraYaw;
    public float elytraRoll;
    public final ClientWorld clientWorld;
    
    public AbstractClientPlayerEntity(final ClientWorld world, final GameProfile profile) {
        super(world, profile);
        this.clientWorld = world;
    }
    
    @Override
    public boolean isSpectator() {
        final PlayerListEntry playerListEntry1 = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
        return playerListEntry1 != null && playerListEntry1.getGameMode() == GameMode.e;
    }
    
    @Override
    public boolean isCreative() {
        final PlayerListEntry playerListEntry1 = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
        return playerListEntry1 != null && playerListEntry1.getGameMode() == GameMode.c;
    }
    
    public boolean canRenderCapeTexture() {
        return this.getPlayerListEntry() != null;
    }
    
    @Nullable
    protected PlayerListEntry getPlayerListEntry() {
        if (this.cachedScoreboardEntry == null) {
            this.cachedScoreboardEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
        }
        return this.cachedScoreboardEntry;
    }
    
    public boolean hasSkinTexture() {
        final PlayerListEntry playerListEntry1 = this.getPlayerListEntry();
        return playerListEntry1 != null && playerListEntry1.hasSkinTexture();
    }
    
    public Identifier getSkinTexture() {
        final PlayerListEntry playerListEntry1 = this.getPlayerListEntry();
        return (playerListEntry1 == null) ? DefaultSkinHelper.getTexture(this.getUuid()) : playerListEntry1.getSkinTexture();
    }
    
    @Nullable
    public Identifier getCapeTexture() {
        final PlayerListEntry playerListEntry1 = this.getPlayerListEntry();
        return (playerListEntry1 == null) ? null : playerListEntry1.getCapeTexture();
    }
    
    public boolean canRenderElytraTexture() {
        return this.getPlayerListEntry() != null;
    }
    
    @Nullable
    public Identifier getElytraTexture() {
        final PlayerListEntry playerListEntry1 = this.getPlayerListEntry();
        return (playerListEntry1 == null) ? null : playerListEntry1.getElytraTexture();
    }
    
    public static PlayerSkinTexture loadSkin(final Identifier id, final String playerName) {
        final TextureManager textureManager3 = MinecraftClient.getInstance().getTextureManager();
        Texture texture4 = textureManager3.getTexture(id);
        if (texture4 == null) {
            texture4 = new PlayerSkinTexture(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", ChatUtil.stripTextFormat(playerName)), DefaultSkinHelper.getTexture(PlayerEntity.getOfflinePlayerUuid(playerName)), new SkinRemappingImageFilter());
            textureManager3.registerTexture(id, texture4);
        }
        return (PlayerSkinTexture)texture4;
    }
    
    public static Identifier getSkinId(final String playerName) {
        return new Identifier("skins/" + Hashing.sha1().hashUnencodedChars(ChatUtil.stripTextFormat(playerName)));
    }
    
    public String getModel() {
        final PlayerListEntry playerListEntry1 = this.getPlayerListEntry();
        return (playerListEntry1 == null) ? DefaultSkinHelper.getModel(this.getUuid()) : playerListEntry1.getModel();
    }
    
    public float getSpeed() {
        float float1 = 1.0f;
        if (this.abilities.flying) {
            float1 *= 1.1f;
        }
        final EntityAttributeInstance entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        float1 *= (float)((entityAttributeInstance2.getValue() / this.abilities.getWalkSpeed() + 1.0) / 2.0);
        if (this.abilities.getWalkSpeed() == 0.0f || Float.isNaN(float1) || Float.isInfinite(float1)) {
            float1 = 1.0f;
        }
        if (this.isUsingItem() && this.getActiveItem().getItem() == Items.jf) {
            final int integer3 = this.getItemUseTime();
            float float2 = integer3 / 20.0f;
            if (float2 > 1.0f) {
                float2 = 1.0f;
            }
            else {
                float2 *= float2;
            }
            float1 *= 1.0f - float2 * 0.15f;
        }
        return float1;
    }
}
