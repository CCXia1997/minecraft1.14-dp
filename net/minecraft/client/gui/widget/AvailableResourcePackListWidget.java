package net.minecraft.client.gui.widget;

import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AvailableResourcePackListWidget extends ResourcePackListWidget
{
    public AvailableResourcePackListWidget(final MinecraftClient minecraftClient, final int integer2, final int integer3) {
        super(minecraftClient, integer2, integer3, new TranslatableTextComponent("resourcePack.available.title", new Object[0]));
    }
}
