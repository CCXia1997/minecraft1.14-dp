package net.minecraft.client.gui.hud;

import net.minecraft.text.TextComponent;
import net.minecraft.text.ChatMessageType;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ClientChatListener;

@Environment(EnvType.CLIENT)
public class ChatListenerHud implements ClientChatListener
{
    private final MinecraftClient client;
    
    public ChatListenerHud(final MinecraftClient client) {
        this.client = client;
    }
    
    @Override
    public void onChatMessage(final ChatMessageType messageType, final TextComponent message) {
        this.client.inGameHud.getChatHud().addMessage(message);
    }
}
