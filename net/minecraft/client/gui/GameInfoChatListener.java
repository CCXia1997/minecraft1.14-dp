package net.minecraft.client.gui;

import net.minecraft.text.TextComponent;
import net.minecraft.text.ChatMessageType;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GameInfoChatListener implements ClientChatListener
{
    private final MinecraftClient client;
    
    public GameInfoChatListener(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void onChatMessage(final ChatMessageType messageType, final TextComponent message) {
        this.client.inGameHud.setOverlayMessage(message, false);
    }
}
