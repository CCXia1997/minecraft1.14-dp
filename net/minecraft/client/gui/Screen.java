package net.minecraft.client.gui;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import java.io.File;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import java.util.Locale;
import java.net.URISyntaxException;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.nbt.Tag;
import com.google.gson.JsonSyntaxException;
import net.minecraft.text.TextFormat;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.Lists;
import java.net.URI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.minecraft.text.TextComponent;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable
{
    private static final Logger LOGGER;
    private static final Set<String> ALLOWED_PROTOCOLS;
    protected final TextComponent title;
    protected final List<Element> children;
    @Nullable
    protected MinecraftClient minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractButtonWidget> buttons;
    public boolean passEvents;
    protected TextRenderer font;
    private URI clickedLink;
    
    protected Screen(final TextComponent title) {
        this.children = Lists.newArrayList();
        this.buttons = Lists.newArrayList();
        this.title = title;
    }
    
    public TextComponent getTitle() {
        return this.title;
    }
    
    public String getNarrationMessage() {
        return this.getTitle().getString();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        for (int integer4 = 0; integer4 < this.buttons.size(); ++integer4) {
            this.buttons.get(integer4).render(mouseX, mouseY, delta);
        }
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (keyCode == 258) {
            final boolean boolean4 = !hasShiftDown();
            if (!this.changeFocus(boolean4)) {
                this.changeFocus(boolean4);
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    public void onClose() {
        this.minecraft.openScreen(null);
    }
    
    protected <T extends AbstractButtonWidget> T addButton(final T button) {
        this.buttons.add(button);
        this.children.add(button);
        return button;
    }
    
    protected void renderTooltip(final ItemStack stack, final int x, final int y) {
        this.renderTooltip(this.getTooltipFromItem(stack), x, y);
    }
    
    public List<String> getTooltipFromItem(final ItemStack stack) {
        final List<TextComponent> list2 = stack.getTooltipText(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
        final List<String> list3 = Lists.newArrayList();
        for (final TextComponent textComponent5 : list2) {
            list3.add(textComponent5.getFormattedText());
        }
        return list3;
    }
    
    public void renderTooltip(final String text, final int x, final int y) {
        this.renderTooltip(Arrays.<String>asList(text), x, y);
    }
    
    public void renderTooltip(final List<String> text, final int x, final int y) {
        if (text.isEmpty()) {
            return;
        }
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        int integer4 = 0;
        for (final String string6 : text) {
            final int integer5 = this.font.getStringWidth(string6);
            if (integer5 > integer4) {
                integer4 = integer5;
            }
        }
        int integer6 = x + 12;
        int integer7 = y - 12;
        final int integer5 = integer4;
        int integer8 = 8;
        if (text.size() > 1) {
            integer8 += 2 + (text.size() - 1) * 10;
        }
        if (integer6 + integer4 > this.width) {
            integer6 -= 28 + integer4;
        }
        if (integer7 + integer8 + 6 > this.height) {
            integer7 = this.height - integer8 - 6;
        }
        this.blitOffset = 300;
        this.itemRenderer.zOffset = 300.0f;
        final int integer9 = -267386864;
        this.fillGradient(integer6 - 3, integer7 - 4, integer6 + integer5 + 3, integer7 - 3, -267386864, -267386864);
        this.fillGradient(integer6 - 3, integer7 + integer8 + 3, integer6 + integer5 + 3, integer7 + integer8 + 4, -267386864, -267386864);
        this.fillGradient(integer6 - 3, integer7 - 3, integer6 + integer5 + 3, integer7 + integer8 + 3, -267386864, -267386864);
        this.fillGradient(integer6 - 4, integer7 - 3, integer6 - 3, integer7 + integer8 + 3, -267386864, -267386864);
        this.fillGradient(integer6 + integer5 + 3, integer7 - 3, integer6 + integer5 + 4, integer7 + integer8 + 3, -267386864, -267386864);
        final int integer10 = 1347420415;
        final int integer11 = 1344798847;
        this.fillGradient(integer6 - 3, integer7 - 3 + 1, integer6 - 3 + 1, integer7 + integer8 + 3 - 1, 1347420415, 1344798847);
        this.fillGradient(integer6 + integer5 + 2, integer7 - 3 + 1, integer6 + integer5 + 3, integer7 + integer8 + 3 - 1, 1347420415, 1344798847);
        this.fillGradient(integer6 - 3, integer7 - 3, integer6 + integer5 + 3, integer7 - 3 + 1, 1347420415, 1347420415);
        this.fillGradient(integer6 - 3, integer7 + integer8 + 2, integer6 + integer5 + 3, integer7 + integer8 + 3, 1344798847, 1344798847);
        for (int integer12 = 0; integer12 < text.size(); ++integer12) {
            final String string7 = text.get(integer12);
            this.font.drawWithShadow(string7, (float)integer6, (float)integer7, -1);
            if (integer12 == 0) {
                integer7 += 2;
            }
            integer7 += 10;
        }
        this.blitOffset = 0;
        this.itemRenderer.zOffset = 0.0f;
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        GuiLighting.enable();
        GlStateManager.enableRescaleNormal();
    }
    
    protected void renderComponentHoverEffect(final TextComponent component, final int x, final int y) {
        if (component == null || component.getStyle().getHoverEvent() == null) {
            return;
        }
        final HoverEvent hoverEvent4 = component.getStyle().getHoverEvent();
        if (hoverEvent4.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack5 = ItemStack.EMPTY;
            try {
                final Tag tag6 = StringNbtReader.parse(hoverEvent4.getValue().getString());
                if (tag6 instanceof CompoundTag) {
                    itemStack5 = ItemStack.fromTag((CompoundTag)tag6);
                }
            }
            catch (CommandSyntaxException ex2) {}
            if (itemStack5.isEmpty()) {
                this.renderTooltip(TextFormat.m + "Invalid Item!", x, y);
            }
            else {
                this.renderTooltip(itemStack5, x, y);
            }
        }
        else if (hoverEvent4.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.options.advancedItemTooltips) {
                try {
                    final CompoundTag compoundTag5 = StringNbtReader.parse(hoverEvent4.getValue().getString());
                    final List<String> list6 = Lists.newArrayList();
                    final TextComponent textComponent7 = TextComponent.Serializer.fromJsonString(compoundTag5.getString("name"));
                    if (textComponent7 != null) {
                        list6.add(textComponent7.getFormattedText());
                    }
                    if (compoundTag5.containsKey("type", 8)) {
                        final String string8 = compoundTag5.getString("type");
                        list6.add("Type: " + string8);
                    }
                    list6.add(compoundTag5.getString("id"));
                    this.renderTooltip(list6, x, y);
                }
                catch (JsonSyntaxException | CommandSyntaxException ex3) {
                    final Exception ex;
                    final Exception exception5 = ex;
                    this.renderTooltip(TextFormat.m + "Invalid Entity!", x, y);
                }
            }
        }
        else if (hoverEvent4.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.textRenderer.wrapStringToWidthAsList(hoverEvent4.getValue().getFormattedText(), Math.max(this.width / 2, 200)), x, y);
        }
        GlStateManager.disableLighting();
    }
    
    protected void insertText(final String text, final boolean boolean2) {
    }
    
    public boolean handleComponentClicked(final TextComponent textComponent) {
        if (textComponent == null) {
            return false;
        }
        final ClickEvent clickEvent2 = textComponent.getStyle().getClickEvent();
        if (hasShiftDown()) {
            if (textComponent.getStyle().getInsertion() != null) {
                this.insertText(textComponent.getStyle().getInsertion(), false);
            }
        }
        else if (clickEvent2 != null) {
            if (clickEvent2.getAction() == ClickEvent.Action.OPEN_URL) {
                if (!this.minecraft.options.chatLinks) {
                    return false;
                }
                try {
                    final URI uRI3 = new URI(clickEvent2.getValue());
                    final String string4 = uRI3.getScheme();
                    if (string4 == null) {
                        throw new URISyntaxException(clickEvent2.getValue(), "Missing protocol");
                    }
                    if (!Screen.ALLOWED_PROTOCOLS.contains(string4.toLowerCase(Locale.ROOT))) {
                        throw new URISyntaxException(clickEvent2.getValue(), "Unsupported protocol: " + string4.toLowerCase(Locale.ROOT));
                    }
                    if (this.minecraft.options.chatLinksPrompt) {
                        this.clickedLink = uRI3;
                        this.minecraft.openScreen(new ConfirmChatLinkScreen(this::confirmLink, clickEvent2.getValue(), false));
                    }
                    else {
                        this.openLink(uRI3);
                    }
                }
                catch (URISyntaxException uRISyntaxException3) {
                    Screen.LOGGER.error("Can't open url for {}", clickEvent2, uRISyntaxException3);
                }
            }
            else if (clickEvent2.getAction() == ClickEvent.Action.OPEN_FILE) {
                final URI uRI3 = new File(clickEvent2.getValue()).toURI();
                this.openLink(uRI3);
            }
            else if (clickEvent2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                this.insertText(clickEvent2.getValue(), true);
            }
            else if (clickEvent2.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.sendMessage(clickEvent2.getValue(), false);
            }
            else {
                Screen.LOGGER.error("Don't know how to handle {}", clickEvent2);
            }
            return true;
        }
        return false;
    }
    
    public void sendMessage(final String message) {
        this.sendMessage(message, true);
    }
    
    public void sendMessage(final String message, final boolean toHud) {
        if (toHud) {
            this.minecraft.inGameHud.getChatHud().a(message);
        }
        this.minecraft.player.sendChatMessage(message);
    }
    
    public void init(final MinecraftClient client, final int width, final int height) {
        this.minecraft = client;
        this.itemRenderer = client.getItemRenderer();
        this.font = client.textRenderer;
        this.width = width;
        this.height = height;
        this.buttons.clear();
        this.children.clear();
        this.setFocused(null);
        this.init();
    }
    
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public List<? extends Element> children() {
        return this.children;
    }
    
    protected void init() {
    }
    
    public void tick() {
    }
    
    public void removed() {
    }
    
    public void renderBackground() {
        this.renderBackground(0);
    }
    
    public void renderBackground(final int alpha) {
        if (this.minecraft.world != null) {
            this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else {
            this.renderDirtBackground(alpha);
        }
    }
    
    public void renderDirtBackground(final int alpha) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tessellator tessellator2 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder3 = tessellator2.getBufferBuilder();
        this.minecraft.getTextureManager().bindTexture(Screen.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float4 = 32.0f;
        bufferBuilder3.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder3.vertex(0.0, this.height, 0.0).texture(0.0, this.height / 32.0f + alpha).color(64, 64, 64, 255).next();
        bufferBuilder3.vertex(this.width, this.height, 0.0).texture(this.width / 32.0f, this.height / 32.0f + alpha).color(64, 64, 64, 255).next();
        bufferBuilder3.vertex(this.width, 0.0, 0.0).texture(this.width / 32.0f, alpha).color(64, 64, 64, 255).next();
        bufferBuilder3.vertex(0.0, 0.0, 0.0).texture(0.0, alpha).color(64, 64, 64, 255).next();
        tessellator2.draw();
    }
    
    public boolean isPauseScreen() {
        return true;
    }
    
    private void confirmLink(final boolean open) {
        if (open) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.minecraft.openScreen(this);
    }
    
    private void openLink(final URI link) {
        SystemUtil.getOperatingSystem().open(link);
    }
    
    public static boolean hasControlDown() {
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 347);
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 345);
    }
    
    public static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344);
    }
    
    public static boolean hasAltDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 342) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 346);
    }
    
    public static boolean isCut(final int code) {
        return code == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public static boolean isPaste(final int code) {
        return code == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public static boolean isCopy(final int code) {
        return code == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public static boolean isSelectAll(final int code) {
        return code == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    
    public void resize(final MinecraftClient client, final int width, final int height) {
        this.init(client, width, height);
    }
    
    public static void wrapScreenError(final Runnable task, final String errorTitle, final String screenName) {
        try {
            task.run();
        }
        catch (Throwable throwable4) {
            final CrashReport crashReport5 = CrashReport.create(throwable4, errorTitle);
            final CrashReportSection crashReportSection6 = crashReport5.addElement("Affected screen");
            crashReportSection6.add("Screen name", () -> screenName);
            throw new CrashException(crashReport5);
        }
    }
    
    protected boolean isValidCharacterForName(final String name, final char character, final int cursorPos) {
        final int integer4 = name.indexOf(58);
        final int integer5 = name.indexOf(47);
        if (character == ':') {
            return (integer5 == -1 || cursorPos <= integer5) && integer4 == -1;
        }
        if (character == '/') {
            return cursorPos > integer4;
        }
        return character == '_' || character == '-' || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '.';
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return true;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
    }
}
