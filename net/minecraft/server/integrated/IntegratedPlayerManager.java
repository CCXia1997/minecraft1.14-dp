package net.minecraft.server.integrated;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.PlayerManager;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager
{
    private CompoundTag userData;
    
    public IntegratedPlayerManager(final IntegratedServer integratedServer) {
        super(integratedServer, 8);
        this.setViewDistance(10, 8);
    }
    
    @Override
    protected void savePlayerData(final ServerPlayerEntity player) {
        if (player.getName().getString().equals(this.getServer().getUserName())) {
            this.userData = player.toTag(new CompoundTag());
        }
        super.savePlayerData(player);
    }
    
    @Override
    public TextComponent checkCanJoin(final SocketAddress socketAddress, final GameProfile gameProfile) {
        if (gameProfile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(gameProfile.getName()) != null) {
            return new TranslatableTextComponent("multiplayer.disconnect.name_taken", new Object[0]);
        }
        return super.checkCanJoin(socketAddress, gameProfile);
    }
    
    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }
    
    @Override
    public CompoundTag getUserData() {
        return this.userData;
    }
}
