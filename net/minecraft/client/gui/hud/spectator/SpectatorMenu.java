package net.minecraft.client.gui.hud.spectator;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpectatorMenu
{
    private static final SpectatorMenuCommand CLOSE_COMMAND;
    private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND;
    private static final SpectatorMenuCommand NEXT_PAGE_COMMAND;
    private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND;
    public static final SpectatorMenuCommand BLANK_COMMAND;
    private final SpectatorMenuCloseCallback closeCallback;
    private final List<SpectatorMenuState> stateStack;
    private SpectatorMenuCommandGroup currentGroup;
    private int selectedSlot;
    private int page;
    
    public SpectatorMenu(final SpectatorMenuCloseCallback closeCallback) {
        this.stateStack = Lists.newArrayList();
        this.selectedSlot = -1;
        this.currentGroup = new RootSpectatorCommandGroup();
        this.closeCallback = closeCallback;
    }
    
    public SpectatorMenuCommand getCommand(final int slot) {
        final int integer2 = slot + this.page * 6;
        if (this.page > 0 && slot == 0) {
            return SpectatorMenu.PREVIOUS_PAGE_COMMAND;
        }
        if (slot == 7) {
            if (integer2 < this.currentGroup.getCommands().size()) {
                return SpectatorMenu.NEXT_PAGE_COMMAND;
            }
            return SpectatorMenu.DISABLED_NEXT_PAGE_COMMAND;
        }
        else {
            if (slot == 8) {
                return SpectatorMenu.CLOSE_COMMAND;
            }
            if (integer2 < 0 || integer2 >= this.currentGroup.getCommands().size()) {
                return SpectatorMenu.BLANK_COMMAND;
            }
            return MoreObjects.<SpectatorMenuCommand>firstNonNull(this.currentGroup.getCommands().get(integer2), SpectatorMenu.BLANK_COMMAND);
        }
    }
    
    public List<SpectatorMenuCommand> getCommands() {
        final List<SpectatorMenuCommand> list1 = Lists.newArrayList();
        for (int integer2 = 0; integer2 <= 8; ++integer2) {
            list1.add(this.getCommand(integer2));
        }
        return list1;
    }
    
    public SpectatorMenuCommand getSelectedCommand() {
        return this.getCommand(this.selectedSlot);
    }
    
    public SpectatorMenuCommandGroup getCurrentGroup() {
        return this.currentGroup;
    }
    
    public void setSelectedSlot(final int integer) {
        final SpectatorMenuCommand spectatorMenuCommand2 = this.getCommand(integer);
        if (spectatorMenuCommand2 != SpectatorMenu.BLANK_COMMAND) {
            if (this.selectedSlot == integer && spectatorMenuCommand2.enabled()) {
                spectatorMenuCommand2.use(this);
            }
            else {
                this.selectedSlot = integer;
            }
        }
    }
    
    public void close() {
        this.closeCallback.close(this);
    }
    
    public int getSelectedSlot() {
        return this.selectedSlot;
    }
    
    public void selectElement(final SpectatorMenuCommandGroup spectatorMenuCommandGroup) {
        this.stateStack.add(this.getCurrentState());
        this.currentGroup = spectatorMenuCommandGroup;
        this.selectedSlot = -1;
        this.page = 0;
    }
    
    public SpectatorMenuState getCurrentState() {
        return new SpectatorMenuState(this.currentGroup, this.getCommands(), this.selectedSlot);
    }
    
    static {
        CLOSE_COMMAND = new CloseSpectatorMenuCommand();
        PREVIOUS_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(-1, true);
        NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, true);
        DISABLED_NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, false);
        BLANK_COMMAND = new SpectatorMenuCommand() {
            @Override
            public void use(final SpectatorMenu menu) {
            }
            
            @Override
            public TextComponent getName() {
                return new StringTextComponent("");
            }
            
            @Override
            public void renderIcon(final float brightness, final int alpha) {
            }
            
            @Override
            public boolean enabled() {
                return false;
            }
        };
    }
    
    @Environment(EnvType.CLIENT)
    static class CloseSpectatorMenuCommand implements SpectatorMenuCommand
    {
        private CloseSpectatorMenuCommand() {
        }
        
        @Override
        public void use(final SpectatorMenu menu) {
            menu.close();
        }
        
        @Override
        public TextComponent getName() {
            return new TranslatableTextComponent("spectatorMenu.close", new Object[0]);
        }
        
        @Override
        public void renderIcon(final float brightness, final int alpha) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
            DrawableHelper.blit(0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }
        
        @Override
        public boolean enabled() {
            return true;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class ChangePageSpectatorMenuCommand implements SpectatorMenuCommand
    {
        private final int direction;
        private final boolean enabled;
        
        public ChangePageSpectatorMenuCommand(final int direction, final boolean enabled) {
            this.direction = direction;
            this.enabled = enabled;
        }
        
        @Override
        public void use(final SpectatorMenu menu) {
            menu.page += this.direction;
        }
        
        @Override
        public TextComponent getName() {
            if (this.direction < 0) {
                return new TranslatableTextComponent("spectatorMenu.previous_page", new Object[0]);
            }
            return new TranslatableTextComponent("spectatorMenu.next_page", new Object[0]);
        }
        
        @Override
        public void renderIcon(final float brightness, final int alpha) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
            if (this.direction < 0) {
                DrawableHelper.blit(0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            }
            else {
                DrawableHelper.blit(0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }
        
        @Override
        public boolean enabled() {
            return this.enabled;
        }
    }
}
