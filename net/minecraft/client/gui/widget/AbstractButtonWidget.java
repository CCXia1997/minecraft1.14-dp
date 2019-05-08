package net.minecraft.client.gui.widget;

import java.util.Objects;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractButtonWidget extends DrawableHelper implements Drawable, Element
{
    public static final Identifier WIDGETS_LOCATION;
    private static final int NARRATE_DELAY_MOUSE = 750;
    private static final int NARRATE_DELAY_FOCUS = 200;
    protected int width;
    protected int height;
    public int x;
    public int y;
    private String message;
    private boolean wasHovered;
    protected boolean isHovered;
    public boolean active;
    public boolean visible;
    protected float alpha;
    protected long nextNarration;
    private boolean focused;
    
    public AbstractButtonWidget(final int x, final int y, final String text) {
        this(x, y, 200, 20, text);
    }
    
    public AbstractButtonWidget(final int x, final int y, final int width, final int height, final String message) {
        this.active = true;
        this.visible = true;
        this.alpha = 1.0f;
        this.nextNarration = Long.MAX_VALUE;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;
    }
    
    protected int getYImage(final boolean isHovered) {
        int integer2 = 1;
        if (!this.active) {
            integer2 = 0;
        }
        else if (isHovered) {
            integer2 = 2;
        }
        return integer2;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (!this.visible) {
            return;
        }
        this.isHovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
        if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
                if (this.focused) {
                    this.nextNarration = SystemUtil.getMeasuringTimeMs() + 200L;
                }
                else {
                    this.nextNarration = SystemUtil.getMeasuringTimeMs() + 750L;
                }
            }
            else {
                this.nextNarration = Long.MAX_VALUE;
            }
        }
        if (this.visible) {
            this.renderButton(mouseX, mouseY, delta);
        }
        this.narrate();
        this.wasHovered = this.isHovered();
    }
    
    protected void narrate() {
        if (this.active && this.isHovered() && SystemUtil.getMeasuringTimeMs() > this.nextNarration) {
            final String string1 = this.getNarrationMessage();
            if (!string1.isEmpty()) {
                NarratorManager.INSTANCE.a(string1);
                this.nextNarration = Long.MAX_VALUE;
            }
        }
    }
    
    protected String getNarrationMessage() {
        if (this.message.isEmpty()) {
            return "";
        }
        return I18n.translate("gui.narrate.button", this.getMessage());
    }
    
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
        final TextRenderer textRenderer5 = minecraftClient4.textRenderer;
        minecraftClient4.getTextureManager().bindTexture(AbstractButtonWidget.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        final int integer6 = this.getYImage(this.isHovered());
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(this.x, this.y, 0, 46 + integer6 * 20, this.width / 2, this.height);
        this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + integer6 * 20, this.width / 2, this.height);
        this.renderBg(minecraftClient4, mouseX, mouseY);
        int integer7 = 14737632;
        if (!this.active) {
            integer7 = 10526880;
        }
        else if (this.isHovered()) {
            integer7 = 16777120;
        }
        this.drawCenteredString(textRenderer5, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, integer7 | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }
    
    protected void renderBg(final MinecraftClient client, final int mouseX, final int mouseY) {
    }
    
    public void onClick(final double mouseX, final double mouseY) {
    }
    
    public void onRelease(final double mouseX, final double mouseY) {
    }
    
    protected void onDrag(final double mouseX, final double mouseY, final double deltaX, final double deltaY) {
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(button)) {
            final boolean boolean6 = this.clicked(mouseX, mouseY);
            if (boolean6) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        }
        return false;
    }
    
    protected boolean isValidClickButton(final int integer) {
        return integer == 0;
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        }
        return false;
    }
    
    protected boolean clicked(final double mouseX, final double mouseY) {
        return this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
    
    public boolean isHovered() {
        return this.isHovered || this.focused;
    }
    
    @Override
    public boolean changeFocus(final boolean lookForwards) {
        if (!this.active || !this.visible) {
            return false;
        }
        this.onFocusedChanged(this.focused = !this.focused);
        return this.focused;
    }
    
    protected void onFocusedChanged(final boolean boolean1) {
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
    
    public void renderToolTip(final int mouseX, final int mouseY) {
    }
    
    public void playDownSound(final SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.mh, 1.0f));
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int value) {
        this.width = value;
    }
    
    public void setAlpha(final float value) {
        this.alpha = value;
    }
    
    public void setMessage(final String value) {
        if (!Objects.equals(value, this.message)) {
            this.nextNarration = SystemUtil.getMeasuringTimeMs() + 250L;
        }
        this.message = value;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public boolean isFocused() {
        return this.focused;
    }
    
    protected void setFocused(final boolean boolean1) {
        this.focused = boolean1;
    }
    
    static {
        WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
    }
}
