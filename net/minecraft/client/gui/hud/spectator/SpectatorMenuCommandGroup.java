package net.minecraft.client.gui.hud.spectator;

import net.minecraft.text.TextComponent;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommandGroup
{
    List<SpectatorMenuCommand> getCommands();
    
    TextComponent getPrompt();
}
