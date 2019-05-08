package net.minecraft.client.gui;

import net.minecraft.text.TextFormat;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.util.ChatUtil;
import java.util.Iterator;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.SharedConstants;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.client.gui.menu.AccessibilityScreen;
import net.minecraft.client.gui.menu.SettingsScreen;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.client.gui.menu.options.LanguageOptionsScreen;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.resource.language.I18n;
import com.mojang.blaze3d.platform.GLX;
import java.util.Random;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MainMenuScreen extends Screen
{
    public static final CubeMapRenderer panoramaCubeMap;
    private static final Identifier panoramaOverlay;
    private static final Identifier d;
    private final boolean e;
    @Nullable
    private String splashText;
    private ButtonWidget buttonResetDemo;
    private final Object mutex;
    public static final String OUTDATED_GL_TEXT;
    private int warningTextWidth;
    private int warningTitleWidth;
    private int warningAlignLeft;
    private int warningAlignTop;
    private int warningAlignRight;
    private int warningAlignBottom;
    private String warningTitle;
    private String warningText;
    private String warningLink;
    private static final Identifier MINECRAFT_TITLE_TEXTURE;
    private static final Identifier EDITION_TITLE_TEXTURE;
    private boolean realmsNotificationsInitialized;
    private Screen realmsNotificationGui;
    private int copyrightTextWidth;
    private int copyrightTextX;
    private final RotatingCubeMapRenderer backgroundRenderer;
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;
    
    public MainMenuScreen() {
        this(false);
    }
    
    public MainMenuScreen(final boolean doBackgroundFade) {
        super(new TranslatableTextComponent("narrator.screen.title", new Object[0]));
        this.mutex = new Object();
        this.warningText = MainMenuScreen.OUTDATED_GL_TEXT;
        this.backgroundRenderer = new RotatingCubeMapRenderer(MainMenuScreen.panoramaCubeMap);
        this.doBackgroundFade = doBackgroundFade;
        this.e = (new Random().nextFloat() < 1.0E-4);
        this.warningTitle = "";
        if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
            this.warningTitle = I18n.translate("title.oldgl1");
            this.warningText = I18n.translate("title.oldgl2");
            this.warningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }
    
    private boolean areRealmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications && this.realmsNotificationGui != null;
    }
    
    @Override
    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.tick();
        }
    }
    
    public static CompletableFuture<Void> a(final TextureManager textureManager, final Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(MainMenuScreen.MINECRAFT_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(MainMenuScreen.EDITION_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(MainMenuScreen.panoramaOverlay, executor), MainMenuScreen.panoramaCubeMap.loadTexturesAsync(textureManager, executor));
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.minecraft.getSplashTextLoader().get();
        }
        this.copyrightTextWidth = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
        this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
        final int integer1 = 24;
        final int integer2 = this.height / 4 + 48;
        if (this.minecraft.isDemo()) {
            this.initWidgetsDemo(integer2, 24);
        }
        else {
            this.initWidgetsNormal(integer2, 24);
        }
        this.<RecipeBookButtonWidget>addButton(new RecipeBookButtonWidget(this.width / 2 - 124, integer2 + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_LOCATION, 256, 256, buttonWidget -> this.minecraft.openScreen(new LanguageOptionsScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), I18n.translate("narrator.button.language")));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, integer2 + 72 + 12, 98, 20, I18n.translate("menu.options"), buttonWidget -> this.minecraft.openScreen(new SettingsScreen(this, this.minecraft.options))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 2, integer2 + 72 + 12, 98, 20, I18n.translate("menu.quit"), buttonWidget -> this.minecraft.scheduleStop()));
        this.<RecipeBookButtonWidget>addButton(new RecipeBookButtonWidget(this.width / 2 + 104, integer2 + 72 + 12, 20, 20, 0, 0, 20, MainMenuScreen.d, 32, 64, buttonWidget -> this.minecraft.openScreen(new AccessibilityScreen(this, this.minecraft.options)), I18n.translate("narrator.button.accessibility")));
        synchronized (this.mutex) {
            this.warningTitleWidth = this.font.getStringWidth(this.warningTitle);
            this.warningTextWidth = this.font.getStringWidth(this.warningText);
            final int integer3 = Math.max(this.warningTitleWidth, this.warningTextWidth);
            this.warningAlignLeft = (this.width - integer3) / 2;
            this.warningAlignTop = integer2 - 24;
            this.warningAlignRight = this.warningAlignLeft + integer3;
            this.warningAlignBottom = this.warningAlignTop + 24;
        }
        this.minecraft.setConnectedToRealms(false);
        if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
            final RealmsBridge realmsBridge3 = new RealmsBridge();
            this.realmsNotificationGui = realmsBridge3.getNotificationScreen(this);
            this.realmsNotificationsInitialized = true;
        }
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.init(this.minecraft, this.width, this.height);
        }
    }
    
    private void initWidgetsNormal(final int y, final int spacingY) {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, I18n.translate("menu.singleplayer"), buttonWidget -> this.minecraft.openScreen(new LevelSelectScreen(this))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, I18n.translate("menu.multiplayer"), buttonWidget -> this.minecraft.openScreen(new MultiplayerScreen(this))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 2, 200, 20, I18n.translate("menu.online"), buttonWidget -> this.switchToRealms()));
    }
    
    private void initWidgetsDemo(final int y, final int spacingY) {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, I18n.translate("menu.playdemo"), buttonWidget -> this.minecraft.startIntegratedServer("Demo_World", "Demo_World", MinecraftServer.WORLD_INFO)));
        final LevelStorage levelStorage2;
        final LevelProperties levelProperties3;
        MinecraftClient minecraft;
        TranslatableTextComponent textComponent2;
        final TranslatableTextComponent textComponent3;
        final Screen screen;
        final BooleanConsumer booleanConsumer;
        this.buttonResetDemo = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, I18n.translate("menu.resetdemo"), buttonWidget -> {
            levelStorage2 = this.minecraft.getLevelStorage();
            levelProperties3 = levelStorage2.getLevelProperties("Demo_World");
            if (levelProperties3 != null) {
                minecraft = this.minecraft;
                // new(net.minecraft.client.gui.menu.YesNoScreen.class)
                this::a;
                textComponent2 = new TranslatableTextComponent("selectWorld.deleteQuestion", new Object[0]);
                new TranslatableTextComponent("selectWorld.deleteWarning", new Object[] { levelProperties3.getLevelName() });
                new YesNoScreen(booleanConsumer, textComponent2, textComponent3, I18n.translate("selectWorld.deleteButton"), I18n.translate("gui.cancel"));
                minecraft.openScreen(screen);
            }
            return;
        }));
        final LevelStorage levelStorage3 = this.minecraft.getLevelStorage();
        final LevelProperties levelProperties4 = levelStorage3.getLevelProperties("Demo_World");
        if (levelProperties4 == null) {
            this.buttonResetDemo.active = false;
        }
    }
    
    private void switchToRealms() {
        final RealmsBridge realmsBridge1 = new RealmsBridge();
        realmsBridge1.switchToRealms(this);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = SystemUtil.getMeasuringTimeMs();
        }
        final float float4 = this.doBackgroundFade ? ((SystemUtil.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0f) : 1.0f;
        DrawableHelper.fill(0, 0, this.width, this.height, -1);
        this.backgroundRenderer.render(delta, MathHelper.clamp(float4, 0.0f, 1.0f));
        final int integer5 = 274;
        final int integer6 = this.width / 2 - 137;
        final int integer7 = 30;
        this.minecraft.getTextureManager().bindTexture(MainMenuScreen.panoramaOverlay);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.doBackgroundFade ? ((float)MathHelper.ceil(MathHelper.clamp(float4, 0.0f, 1.0f))) : 1.0f);
        DrawableHelper.blit(0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        final float float5 = this.doBackgroundFade ? MathHelper.clamp(float4 - 1.0f, 0.0f, 1.0f) : 1.0f;
        final int integer8 = MathHelper.ceil(float5 * 255.0f) << 24;
        if ((integer8 & 0xFC000000) == 0x0) {
            return;
        }
        this.minecraft.getTextureManager().bindTexture(MainMenuScreen.MINECRAFT_TITLE_TEXTURE);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float5);
        if (this.e) {
            this.blit(integer6 + 0, 30, 0, 0, 99, 44);
            this.blit(integer6 + 99, 30, 129, 0, 27, 44);
            this.blit(integer6 + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(integer6 + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(integer6 + 155, 30, 0, 45, 155, 44);
        }
        else {
            this.blit(integer6 + 0, 30, 0, 0, 155, 44);
            this.blit(integer6 + 155, 30, 0, 45, 155, 44);
        }
        this.minecraft.getTextureManager().bindTexture(MainMenuScreen.EDITION_TITLE_TEXTURE);
        DrawableHelper.blit(integer6 + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
        if (this.splashText != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.width / 2 + 90), 70.0f, 0.0f);
            GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            float float6 = 1.8f - MathHelper.abs(MathHelper.sin(SystemUtil.getMeasuringTimeMs() % 1000L / 1000.0f * 6.2831855f) * 0.1f);
            float6 = float6 * 100.0f / (this.font.getStringWidth(this.splashText) + 32);
            GlStateManager.scalef(float6, float6, float6);
            this.drawCenteredString(this.font, this.splashText, 0, -8, 0xFFFF00 | integer8);
            GlStateManager.popMatrix();
        }
        String string10 = "Minecraft " + SharedConstants.getGameVersion().getName();
        if (this.minecraft.isDemo()) {
            string10 += " Demo";
        }
        else {
            string10 += ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : ("/" + this.minecraft.getVersionType()));
        }
        this.drawString(this.font, string10, 2, this.height - 10, 0xFFFFFF | integer8);
        this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightTextX, this.height - 10, 0xFFFFFF | integer8);
        if (mouseX > this.copyrightTextX && mouseX < this.copyrightTextX + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
            DrawableHelper.fill(this.copyrightTextX, this.height - 1, this.copyrightTextX + this.copyrightTextWidth, this.height, 0xFFFFFF | integer8);
        }
        if (this.warningTitle != null && !this.warningTitle.isEmpty()) {
            DrawableHelper.fill(this.warningAlignLeft - 2, this.warningAlignTop - 2, this.warningAlignRight + 2, this.warningAlignBottom - 1, 1428160512);
            this.drawString(this.font, this.warningTitle, this.warningAlignLeft, this.warningAlignTop, 0xFFFFFF | integer8);
            this.drawString(this.font, this.warningText, (this.width - this.warningTextWidth) / 2, this.warningAlignTop + 12, 0xFFFFFF | integer8);
        }
        for (final AbstractButtonWidget abstractButtonWidget12 : this.buttons) {
            abstractButtonWidget12.setAlpha(float5);
        }
        super.render(mouseX, mouseY, delta);
        if (this.areRealmsNotificationsEnabled() && float5 >= 1.0f) {
            this.realmsNotificationGui.render(mouseX, mouseY, delta);
        }
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        synchronized (this.mutex) {
            if (!this.warningTitle.isEmpty() && !ChatUtil.isEmpty(this.warningLink) && mouseX >= this.warningAlignLeft && mouseX <= this.warningAlignRight && mouseY >= this.warningAlignTop && mouseY <= this.warningAlignBottom) {
                final ConfirmChatLinkScreen confirmChatLinkScreen7 = new ConfirmChatLinkScreen(boolean1 -> {
                    if (boolean1) {
                        SystemUtil.getOperatingSystem().open(this.warningLink);
                    }
                    this.minecraft.openScreen(this);
                }, this.warningLink, true);
                this.minecraft.openScreen(confirmChatLinkScreen7);
                return true;
            }
        }
        if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseX > this.copyrightTextX && mouseX < this.copyrightTextX + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
            this.minecraft.openScreen(new EndCreditsScreen(false, Runnables.doNothing()));
        }
        return false;
    }
    
    @Override
    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }
    
    private void a(final boolean boolean1) {
        if (boolean1) {
            final LevelStorage levelStorage2 = this.minecraft.getLevelStorage();
            levelStorage2.deleteLevel("Demo_World");
        }
        this.minecraft.openScreen(this);
    }
    
    static {
        panoramaCubeMap = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
        panoramaOverlay = new Identifier("textures/gui/title/background/panorama_overlay.png");
        d = new Identifier("textures/gui/accessibility.png");
        OUTDATED_GL_TEXT = "Please click " + TextFormat.t + "here" + TextFormat.RESET + " for more information.";
        MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
        EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    }
}
