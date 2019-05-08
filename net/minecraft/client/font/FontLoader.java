package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.minecraft.resource.ResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface FontLoader
{
    @Nullable
    Font load(final ResourceManager arg1);
}
