package net.minecraft.client.gui.widget;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.YesNoScreen;
import java.util.List;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.gui.menu.options.ResourcePackOptionsScreen;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.menu.AlwaysSelectedEntryListWidget;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends AlwaysSelectedEntryListWidget<ResourcePackItem>
{
    private static final Identifier RESOURCE_PACKS_LOCATION;
    private static final TextComponent INCOMPATIBLE;
    private static final TextComponent INCOMPATIBLE_CONFIRM;
    protected final MinecraftClient a;
    private final TextComponent e;
    
    public ResourcePackListWidget(final MinecraftClient minecraftClient, final int integer2, final int integer3, final TextComponent textComponent) {
        super(minecraftClient, integer2, integer3, 32, integer3 - 55 + 4, 36);
        this.a = minecraftClient;
        this.centerListVertically = false;
        final boolean renderHeader = true;
        minecraftClient.textRenderer.getClass();
        this.setRenderHeader(renderHeader, (int)(9.0f * 1.5f));
        this.e = textComponent;
    }
    
    @Override
    protected void renderHeader(final int x, final int y, final Tessellator tessellator) {
        final TextComponent textComponent4 = new StringTextComponent("").append(this.e).applyFormat(TextFormat.t, TextFormat.r);
        this.a.textRenderer.draw(textComponent4.getFormattedText(), (float)(x + this.width / 2 - this.a.textRenderer.getStringWidth(textComponent4.getFormattedText()) / 2), (float)Math.min(this.top + 3, y), 16777215);
    }
    
    @Override
    public int getRowWidth() {
        return this.width;
    }
    
    @Override
    protected int getScrollbarPosition() {
        return this.right - 6;
    }
    
    public void addEntry(final ResourcePackItem resourcePackItem) {
        this.addEntry(resourcePackItem);
        resourcePackItem.c = this;
    }
    
    static {
        RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
        INCOMPATIBLE = new TranslatableTextComponent("resourcePack.incompatible", new Object[0]);
        INCOMPATIBLE_CONFIRM = new TranslatableTextComponent("resourcePack.incompatible.confirm.title", new Object[0]);
    }
    
    @Environment(EnvType.CLIENT)
    public static class ResourcePackItem extends Entry<ResourcePackItem>
    {
        private ResourcePackListWidget c;
        protected final MinecraftClient a;
        protected final ResourcePackOptionsScreen b;
        private final ClientResourcePackContainer d;
        
        public ResourcePackItem(final ResourcePackListWidget resourcePackListWidget, final ResourcePackOptionsScreen resourcePackOptionsScreen, final ClientResourcePackContainer clientResourcePackContainer) {
            this.b = resourcePackOptionsScreen;
            this.a = MinecraftClient.getInstance();
            this.d = clientResourcePackContainer;
            this.c = resourcePackListWidget;
        }
        
        public void a(final SelectedResourcePackListWidget selectedResourcePackListWidget) {
            this.e().getSortingDirection().<ResourcePackItem, ClientResourcePackContainer>locate(selectedResourcePackListWidget.children(), this, ResourcePackItem::e, true);
            this.c = selectedResourcePackListWidget;
        }
        
        protected void a() {
            this.d.drawIcon(this.a.getTextureManager());
        }
        
        protected ResourcePackCompatibility b() {
            return this.d.getCompatibility();
        }
        
        protected String c() {
            return this.d.getDescription().getFormattedText();
        }
        
        protected String d() {
            return this.d.getDisplayName().getFormattedText();
        }
        
        public ClientResourcePackContainer e() {
            return this.d;
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            final ResourcePackCompatibility resourcePackCompatibility10 = this.b();
            if (!resourcePackCompatibility10.isCompatible()) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.fill(integer3 - 1, integer2 - 1, integer3 + width - 9, integer2 + height + 1, -8978432);
            }
            this.a();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 32, 32);
            String string11 = this.d();
            String string12 = this.c();
            if (this.f() && (this.a.options.touchscreen || hovering)) {
                this.a.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
                DrawableHelper.fill(integer3, integer2, integer3 + 32, integer2 + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int integer4 = mouseX - integer3;
                final int integer5 = mouseY - integer2;
                if (!resourcePackCompatibility10.isCompatible()) {
                    string11 = ResourcePackListWidget.INCOMPATIBLE.getFormattedText();
                    string12 = resourcePackCompatibility10.getNotification().getFormattedText();
                }
                if (this.g()) {
                    if (integer4 < 32) {
                        DrawableHelper.blit(integer3, integer2, 0.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        DrawableHelper.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                else {
                    if (this.h()) {
                        if (integer4 < 16) {
                            DrawableHelper.blit(integer3, integer2, 32.0f, 32.0f, 32, 32, 256, 256);
                        }
                        else {
                            DrawableHelper.blit(integer3, integer2, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.i()) {
                        if (integer4 < 32 && integer4 > 16 && integer5 < 16) {
                            DrawableHelper.blit(integer3, integer2, 96.0f, 32.0f, 32, 32, 256, 256);
                        }
                        else {
                            DrawableHelper.blit(integer3, integer2, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.j()) {
                        if (integer4 < 32 && integer4 > 16 && integer5 > 16) {
                            DrawableHelper.blit(integer3, integer2, 64.0f, 32.0f, 32, 32, 256, 256);
                        }
                        else {
                            DrawableHelper.blit(integer3, integer2, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            final int integer4 = this.a.textRenderer.getStringWidth(string11);
            if (integer4 > 157) {
                string11 = this.a.textRenderer.trimToWidth(string11, 157 - this.a.textRenderer.getStringWidth("...")) + "...";
            }
            this.a.textRenderer.drawWithShadow(string11, (float)(integer3 + 32 + 2), (float)(integer2 + 1), 16777215);
            final List<String> list14 = this.a.textRenderer.wrapStringToWidthAsList(string12, 157);
            for (int integer6 = 0; integer6 < 2 && integer6 < list14.size(); ++integer6) {
                this.a.textRenderer.drawWithShadow(list14.get(integer6), (float)(integer3 + 32 + 2), (float)(integer2 + 12 + 10 * integer6), 8421504);
            }
        }
        
        protected boolean f() {
            return !this.d.sortsTillEnd() || !this.d.canBeSorted();
        }
        
        protected boolean g() {
            return !this.b.c(this);
        }
        
        protected boolean h() {
            return this.b.c(this) && !this.d.canBeSorted();
        }
        
        protected boolean i() {
            final List<ResourcePackItem> list1 = this.c.children();
            final int integer2 = list1.indexOf(this);
            return integer2 > 0 && !list1.get(integer2 - 1).d.sortsTillEnd();
        }
        
        protected boolean j() {
            final List<ResourcePackItem> list1 = this.c.children();
            final int integer2 = list1.indexOf(this);
            return integer2 >= 0 && integer2 < list1.size() - 1 && !list1.get(integer2 + 1).d.sortsTillEnd();
        }
        
        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            final double double6 = mouseX - this.c.getRowLeft();
            final double double7 = mouseY - this.c.getRowTop(this.c.children().indexOf(this));
            if (this.f() && double6 <= 32.0) {
                if (this.g()) {
                    this.k().a();
                    final ResourcePackCompatibility resourcePackCompatibility10 = this.b();
                    if (resourcePackCompatibility10.isCompatible()) {
                        this.k().select(this);
                    }
                    else {
                        final TextComponent textComponent11 = resourcePackCompatibility10.getConfirmMessage();
                        this.a.openScreen(new YesNoScreen(boolean1 -> {
                            this.a.openScreen(this.k());
                            if (boolean1) {
                                this.k().select(this);
                            }
                        }, ResourcePackListWidget.INCOMPATIBLE_CONFIRM, textComponent11));
                    }
                    return true;
                }
                if (double6 < 16.0 && this.h()) {
                    this.k().remove(this);
                    return true;
                }
                if (double6 > 16.0 && double7 < 16.0 && this.i()) {
                    final List<ResourcePackItem> list10 = this.c.children();
                    final int integer11 = list10.indexOf(this);
                    list10.remove(this);
                    list10.add(integer11 - 1, this);
                    this.k().a();
                    return true;
                }
                if (double6 > 16.0 && double7 > 16.0 && this.j()) {
                    final List<ResourcePackItem> list10 = this.c.children();
                    final int integer11 = list10.indexOf(this);
                    list10.remove(this);
                    list10.add(integer11 + 1, this);
                    this.k().a();
                    return true;
                }
            }
            return false;
        }
        
        public ResourcePackOptionsScreen k() {
            return this.b;
        }
    }
}
