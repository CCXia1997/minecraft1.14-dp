package net.minecraft.client.gui.hud.spectator;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RootSpectatorCommandGroup implements SpectatorMenuCommandGroup
{
    private final List<SpectatorMenuCommand> elements;
    
    public RootSpectatorCommandGroup() {
        (this.elements = Lists.newArrayList()).add(new TeleportSpectatorMenu());
        this.elements.add(new TeamTeleportSpectatorMenu());
    }
    
    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }
    
    @Override
    public TextComponent getPrompt() {
        return new TranslatableTextComponent("spectatorMenu.root.prompt", new Object[0]);
    }
}
