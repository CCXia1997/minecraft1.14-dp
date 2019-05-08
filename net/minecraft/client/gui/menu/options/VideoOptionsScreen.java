package net.minecraft.client.gui.menu.options;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.options.GameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends Screen
{
    private final Screen parent;
    private final GameOptions options;
    private ButtonListWidget list;
    private static final GameOption[] OPTIONS;
    private int mipmapLevels;
    
    public VideoOptionsScreen(final Screen parent, final GameOptions options) {
        super(new TranslatableTextComponent("options.videoTitle", new Object[0]));
        this.parent = parent;
        this.options = options;
    }
    
    @Override
    protected void init() {
        this.mipmapLevels = this.options.mipmapLevels;
        (this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25)).a(GameOption.FULLSCREEN_RESOLUTION);
        this.list.addAll(VideoOptionsScreen.OPTIONS);
        this.children.add(this.list);
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), buttonWidget -> {
            this.minecraft.options.write();
            this.minecraft.window.f();
            this.minecraft.openScreen(this.parent);
        }));
    }
    
    @Override
    public void removed() {
        if (this.options.mipmapLevels != this.mipmapLevels) {
            this.minecraft.getSpriteAtlas().setMipLevel(this.options.mipmapLevels);
            this.minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            this.minecraft.getSpriteAtlas().setFilter(false, this.options.mipmapLevels > 0);
            this.minecraft.reloadResourcesConcurrently();
        }
        this.minecraft.options.write();
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final int integer6 = this.options.guiScale;
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (this.options.guiScale != integer6) {
                this.minecraft.onResolutionChanged();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final int integer6 = this.options.guiScale;
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (this.list.mouseReleased(mouseX, mouseY, button)) {
            if (this.options.guiScale != integer6) {
                this.minecraft.onResolutionChanged();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.list.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        OPTIONS = new GameOption[] { GameOption.GRAPHICS, GameOption.RENDER_DISTANCE, GameOption.AO, GameOption.FRAMERATE_LIMIT, GameOption.VSYNC, GameOption.VIEW_BOBBING, GameOption.GUI_SCALE, GameOption.ATTACK_INDICATOR, GameOption.GAMMA, GameOption.CLOUDS, GameOption.FULLSCREEN, GameOption.PARTICLES, GameOption.MIPMAP_LEVELS, GameOption.ENTITY_SHADOWS, GameOption.BIOME_BLEND_RADIUS };
    }
}
