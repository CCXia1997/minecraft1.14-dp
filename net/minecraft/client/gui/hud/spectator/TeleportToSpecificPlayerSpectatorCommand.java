package net.minecraft.client.gui.hud.spectator;

import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import java.util.Map;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TeleportToSpecificPlayerSpectatorCommand implements SpectatorMenuCommand
{
    private final GameProfile gameProfile;
    private final Identifier skinId;
    
    public TeleportToSpecificPlayerSpectatorCommand(final GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        final MinecraftClient minecraftClient2 = MinecraftClient.getInstance();
        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map3 = minecraftClient2.getSkinProvider().getTextures(gameProfile);
        if (map3.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            this.skinId = minecraftClient2.getSkinProvider().loadSkin(map3.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        }
        else {
            this.skinId = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
        }
    }
    
    @Override
    public void use(final SpectatorMenu menu) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(this.gameProfile.getId()));
    }
    
    @Override
    public TextComponent getName() {
        return new StringTextComponent(this.gameProfile.getName());
    }
    
    @Override
    public void renderIcon(final float brightness, final int alpha) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        DrawableHelper.blit(2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
        DrawableHelper.blit(2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
    }
    
    @Override
    public boolean enabled() {
        return true;
    }
}
