package net.minecraft.client.gui;

import net.minecraft.text.TextComponent;
import net.minecraft.text.ChatMessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClientChatListener
{
    void onChatMessage(final ChatMessageType arg1, final TextComponent arg2);
}
