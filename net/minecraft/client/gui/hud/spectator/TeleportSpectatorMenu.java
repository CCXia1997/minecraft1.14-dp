package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.ComparisonChain;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Iterator;
import net.minecraft.world.GameMode;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.minecraft.client.network.PlayerListEntry;
import com.google.common.collect.Ordering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand
{
    private static final Ordering<PlayerListEntry> a;
    private final List<SpectatorMenuCommand> elements;
    
    public TeleportSpectatorMenu() {
        this(TeleportSpectatorMenu.a.<PlayerListEntry>sortedCopy(MinecraftClient.getInstance().getNetworkHandler().getPlayerList()));
    }
    
    public TeleportSpectatorMenu(final Collection<PlayerListEntry> collection) {
        this.elements = Lists.newArrayList();
        for (final PlayerListEntry playerListEntry3 : TeleportSpectatorMenu.a.<PlayerListEntry>sortedCopy(collection)) {
            if (playerListEntry3.getGameMode() != GameMode.e) {
                this.elements.add(new TeleportToSpecificPlayerSpectatorCommand(playerListEntry3.getProfile()));
            }
        }
    }
    
    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }
    
    @Override
    public TextComponent getPrompt() {
        return new TranslatableTextComponent("spectatorMenu.teleport.prompt", new Object[0]);
    }
    
    @Override
    public void use(final SpectatorMenu menu) {
        menu.selectElement(this);
    }
    
    @Override
    public TextComponent getName() {
        return new TranslatableTextComponent("spectatorMenu.teleport", new Object[0]);
    }
    
    @Override
    public void renderIcon(final float brightness, final int alpha) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
        DrawableHelper.blit(0, 0, 0.0f, 0.0f, 16, 16, 256, 256);
    }
    
    @Override
    public boolean enabled() {
        return !this.elements.isEmpty();
    }
    
    static {
        a = Ordering.<PlayerListEntry>from((playerListEntry1, playerListEntry2) -> ComparisonChain.start().compare(playerListEntry1.getProfile().getId(), playerListEntry2.getProfile().getId()).result());
    }
}
