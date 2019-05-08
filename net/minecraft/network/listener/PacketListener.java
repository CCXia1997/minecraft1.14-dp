package net.minecraft.network.listener;

import net.minecraft.text.TextComponent;

public interface PacketListener
{
    void onDisconnected(final TextComponent arg1);
}
