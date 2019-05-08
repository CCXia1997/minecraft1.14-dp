package net.minecraft.client.gui.menu;

import java.io.InputStream;
import net.minecraft.client.texture.Texture;
import org.apache.commons.lang3.Validate;
import net.minecraft.client.texture.NativeImage;
import java.io.FileInputStream;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextFormat;
import org.apache.commons.lang3.StringUtils;
import com.google.common.hash.Hashing;
import net.minecraft.client.texture.NativeImageBackedTexture;
import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.widget.EntryListWidget;
import java.util.Optional;
import net.minecraft.client.resource.language.I18n;
import java.util.Date;
import net.minecraft.client.util.NarratorManager;
import java.util.Iterator;
import net.minecraft.world.level.storage.LevelStorage;
import java.util.Locale;
import java.util.Collections;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.LevelSummary;
import java.util.List;
import net.minecraft.util.Identifier;
import java.text.DateFormat;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LevelListWidget extends AlwaysSelectedEntryListWidget<LevelItem>
{
    private static final Logger LOGGER;
    private static final DateFormat DATE_FORMAT;
    private static final Identifier UNKNOWN_SERVER_LOCATION;
    private static final Identifier WORLD_SELECTION_LOCATION;
    private final LevelSelectScreen parent;
    @Nullable
    private List<LevelSummary> levels;
    
    public LevelListWidget(final LevelSelectScreen parent, final MinecraftClient client, final int width, final int height, final int top, final int bottom, final int itemHeight, final Supplier<String> searchFilter, @Nullable final LevelListWidget list) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        if (list != null) {
            this.levels = list.levels;
        }
        this.filter(searchFilter, false);
    }
    
    public void filter(final Supplier<String> filter, final boolean load) {
        this.clearEntries();
        final LevelStorage levelStorage3 = this.minecraft.getLevelStorage();
        Label_0088: {
            if (this.levels != null) {
                if (!load) {
                    break Label_0088;
                }
            }
            try {
                this.levels = levelStorage3.getLevelList();
            }
            catch (LevelStorageException levelStorageException4) {
                LevelListWidget.LOGGER.error("Couldn't load level list", (Throwable)levelStorageException4);
                this.minecraft.openScreen(new SevereErrorScreen(new TranslatableTextComponent("selectWorld.unable_to_load", new Object[0]), levelStorageException4.getMessage()));
                return;
            }
            Collections.<LevelSummary>sort(this.levels);
        }
        final String string4 = filter.get().toLowerCase(Locale.ROOT);
        for (final LevelSummary levelSummary6 : this.levels) {
            if (levelSummary6.getDisplayName().toLowerCase(Locale.ROOT).contains(string4) || levelSummary6.getName().toLowerCase(Locale.ROOT).contains(string4)) {
                this.addEntry(new LevelItem(this, levelSummary6, this.minecraft.getLevelStorage()));
            }
        }
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }
    
    @Override
    protected boolean isFocused() {
        return this.parent.getFocused() == this;
    }
    
    public void a(@Nullable final LevelItem entry) {
        super.setSelected(entry);
        if (entry != null) {
            final LevelSummary levelSummary2 = entry.level;
            NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.select", new Object[] { new TranslatableTextComponent("narrator.select.world", new Object[] { levelSummary2.getDisplayName(), new Date(levelSummary2.getLastPlayed()), levelSummary2.isHardcore() ? I18n.translate("gameMode.hardcore") : I18n.translate("gameMode." + levelSummary2.getGameMode().getName()), levelSummary2.hasCheats() ? I18n.translate("selectWorld.cheats") : "", levelSummary2.getVersionTextComponent() }) }).getString());
        }
    }
    
    @Override
    protected void moveSelection(final int amount) {
        super.moveSelection(amount);
        this.parent.worldSelected(true);
    }
    
    public Optional<LevelItem> a() {
        return Optional.<LevelItem>ofNullable(this.getSelected());
    }
    
    public LevelSelectScreen getParent() {
        return this.parent;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DATE_FORMAT = new SimpleDateFormat();
        UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
        WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    }
    
    @Environment(EnvType.CLIENT)
    public final class LevelItem extends Entry<LevelItem> implements AutoCloseable
    {
        private final MinecraftClient client;
        private final LevelSelectScreen screen;
        private final LevelSummary level;
        private final Identifier iconLocation;
        private File iconFile;
        @Nullable
        private final NativeImageBackedTexture icon;
        private long time;
        
        public LevelItem(final LevelListWidget levelList, final LevelSummary level, final LevelStorage levelStorage) {
            this.screen = levelList.getParent();
            this.level = level;
            this.client = MinecraftClient.getInstance();
            this.iconLocation = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars(level.getName()) + "/icon");
            this.iconFile = levelStorage.resolveFile(level.getName(), "icon.png");
            if (!this.iconFile.isFile()) {
                this.iconFile = null;
            }
            this.icon = this.getIconTexture();
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            String string10 = this.level.getDisplayName();
            final String string11 = this.level.getName() + " (" + LevelListWidget.DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)string10)) {
                string10 = I18n.translate("selectWorld.world") + " " + (index + 1);
            }
            String string12 = "";
            if (this.level.requiresConversion()) {
                string12 = I18n.translate("selectWorld.conversion") + " " + string12;
            }
            else {
                string12 = I18n.translate("gameMode." + this.level.getGameMode().getName());
                if (this.level.isHardcore()) {
                    string12 = TextFormat.e + I18n.translate("gameMode.hardcore") + TextFormat.RESET;
                }
                if (this.level.hasCheats()) {
                    string12 = string12 + ", " + I18n.translate("selectWorld.cheats");
                }
                final String string13 = this.level.getVersionTextComponent().getFormattedText();
                if (this.level.isDifferentVersion()) {
                    if (this.level.isFutureLevel()) {
                        string12 = string12 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.m + string13 + TextFormat.RESET;
                    }
                    else {
                        string12 = string12 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.u + string13 + TextFormat.RESET;
                    }
                }
                else {
                    string12 = string12 + ", " + I18n.translate("selectWorld.version") + " " + string13;
                }
            }
            this.client.textRenderer.draw(string10, (float)(integer3 + 32 + 3), (float)(integer2 + 1), 16777215);
            final TextRenderer textRenderer = this.client.textRenderer;
            final String string14 = string11;
            final float x = (float)(integer3 + 32 + 3);
            this.client.textRenderer.getClass();
            textRenderer.draw(string14, x, (float)(integer2 + 9 + 3), 8421504);
            final TextRenderer textRenderer2 = this.client.textRenderer;
            final String string15 = string12;
            final float x2 = (float)(integer3 + 32 + 3);
            this.client.textRenderer.getClass();
            final int n = integer2 + 9;
            this.client.textRenderer.getClass();
            textRenderer2.draw(string15, x2, (float)(n + 9 + 3), 8421504);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture((this.icon != null) ? this.iconLocation : LevelListWidget.UNKNOWN_SERVER_LOCATION);
            GlStateManager.enableBlend();
            DrawableHelper.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 32, 32);
            GlStateManager.disableBlend();
            if (this.client.options.touchscreen || hovering) {
                this.client.getTextureManager().bindTexture(LevelListWidget.WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(integer3, integer2, integer3 + 32, integer2 + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int integer4 = mouseX - integer3;
                final int integer5 = (integer4 < 32) ? 32 : 0;
                if (this.level.isDifferentVersion()) {
                    DrawableHelper.blit(integer3, integer2, 32.0f, (float)integer5, 32, 32, 256, 256);
                    if (this.level.isLegacyCustomizedWorld()) {
                        DrawableHelper.blit(integer3, integer2, 96.0f, (float)integer5, 32, 32, 256, 256);
                        if (integer4 < 32) {
                            final TextComponent textComponent15 = new TranslatableTextComponent("selectWorld.tooltip.unsupported", new Object[] { this.level.getVersionTextComponent() }).applyFormat(TextFormat.m);
                            this.screen.setTooltip(this.client.textRenderer.wrapStringToWidth(textComponent15.getFormattedText(), 175));
                        }
                    }
                    else if (this.level.isFutureLevel()) {
                        DrawableHelper.blit(integer3, integer2, 96.0f, (float)integer5, 32, 32, 256, 256);
                        if (integer4 < 32) {
                            this.screen.setTooltip(TextFormat.m + I18n.translate("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormat.m + I18n.translate("selectWorld.tooltip.fromNewerVersion2"));
                        }
                    }
                    else if (!SharedConstants.getGameVersion().isStable()) {
                        DrawableHelper.blit(integer3, integer2, 64.0f, (float)integer5, 32, 32, 256, 256);
                        if (integer4 < 32) {
                            this.screen.setTooltip(TextFormat.g + I18n.translate("selectWorld.tooltip.snapshot1") + "\n" + TextFormat.g + I18n.translate("selectWorld.tooltip.snapshot2"));
                        }
                    }
                }
                else {
                    DrawableHelper.blit(integer3, integer2, 0.0f, (float)integer5, 32, 32, 256, 256);
                }
            }
        }
        
        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            LevelListWidget.this.a(this);
            this.screen.worldSelected(LevelListWidget.this.a().isPresent());
            if (mouseX - EntryListWidget.this.getRowLeft() <= 32.0) {
                this.play();
                return true;
            }
            if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
                this.play();
                return true;
            }
            this.time = SystemUtil.getMeasuringTimeMs();
            return false;
        }
        
        public void play() {
            if (this.level.isOutdatedLevel() || this.level.isLegacyCustomizedWorld()) {
                TextComponent textComponent1 = new TranslatableTextComponent("selectWorld.backupQuestion", new Object[0]);
                TextComponent textComponent2 = new TranslatableTextComponent("selectWorld.backupWarning", new Object[] { this.level.getVersionTextComponent().getFormattedText(), SharedConstants.getGameVersion().getName() });
                if (this.level.isLegacyCustomizedWorld()) {
                    textComponent1 = new TranslatableTextComponent("selectWorld.backupQuestion.customized", new Object[0]);
                    textComponent2 = new TranslatableTextComponent("selectWorld.backupWarning.customized", new Object[0]);
                }
                String string3;
                this.client.openScreen(new BackupPromptScreen(this.screen, (boolean1, boolean2) -> {
                    if (boolean1) {
                        string3 = this.level.getName();
                        EditLevelScreen.backupLevel(this.client.getLevelStorage(), string3);
                    }
                    this.start();
                }, textComponent1, textComponent2, false));
            }
            else if (this.level.isFutureLevel()) {
                this.client.openScreen(new YesNoScreen(boolean1 -> {
                    if (boolean1) {
                        try {
                            this.start();
                        }
                        catch (Exception exception2) {
                            LevelListWidget.LOGGER.error("Failure to open 'future world'", (Throwable)exception2);
                            this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableTextComponent("selectWorld.futureworld.error.title", new Object[0]), new TranslatableTextComponent("selectWorld.futureworld.error.text", new Object[0])));
                        }
                    }
                    else {
                        this.client.openScreen(this.screen);
                    }
                }, new TranslatableTextComponent("selectWorld.versionQuestion", new Object[0]), new TranslatableTextComponent("selectWorld.versionWarning", new Object[] { this.level.getVersionTextComponent().getFormattedText() }), I18n.translate("selectWorld.versionJoinButton"), I18n.translate("gui.cancel")));
            }
            else {
                this.start();
            }
        }
        
        public void delete() {
            this.client.openScreen(new YesNoScreen(boolean1 -> {
                if (boolean1) {
                    this.client.openScreen(new WorkingScreen());
                    final LevelStorage levelStorage2 = this.client.getLevelStorage();
                    levelStorage2.deleteLevel(this.level.getName());
                    LevelListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                }
                this.client.openScreen(this.screen);
            }, new TranslatableTextComponent("selectWorld.deleteQuestion", new Object[0]), new TranslatableTextComponent("selectWorld.deleteWarning", new Object[] { this.level.getDisplayName() }), I18n.translate("selectWorld.deleteButton"), I18n.translate("gui.cancel")));
        }
        
        public void edit() {
            this.client.openScreen(new EditLevelScreen(boolean1 -> {
                if (boolean1) {
                    LevelListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                }
                this.client.openScreen(this.screen);
            }, this.level.getName()));
        }
        
        public void recreate() {
            try {
                this.client.openScreen(new WorkingScreen());
                final NewLevelScreen newLevelScreen1 = new NewLevelScreen(this.screen);
                final WorldSaveHandler worldSaveHandler2 = this.client.getLevelStorage().createSaveHandler(this.level.getName(), null);
                final LevelProperties levelProperties3 = worldSaveHandler2.readProperties();
                if (levelProperties3 != null) {
                    newLevelScreen1.recreateLevel(levelProperties3);
                    if (this.level.isLegacyCustomizedWorld()) {
                        this.client.openScreen(new YesNoScreen(boolean2 -> this.client.openScreen(boolean2 ? newLevelScreen1 : this.screen), new TranslatableTextComponent("selectWorld.recreate.customized.title", new Object[0]), new TranslatableTextComponent("selectWorld.recreate.customized.text", new Object[0]), I18n.translate("gui.proceed"), I18n.translate("gui.cancel")));
                    }
                    else {
                        this.client.openScreen(newLevelScreen1);
                    }
                }
            }
            catch (Exception exception1) {
                LevelListWidget.LOGGER.error("Unable to recreate world", (Throwable)exception1);
                this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableTextComponent("selectWorld.recreate.error.title", new Object[0]), new TranslatableTextComponent("selectWorld.recreate.error.text", new Object[0])));
            }
        }
        
        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.mh, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.client.startIntegratedServer(this.level.getName(), this.level.getDisplayName(), null);
            }
        }
        
        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            final boolean boolean1 = this.iconFile != null && this.iconFile.isFile();
            if (boolean1) {
                try (final InputStream inputStream2 = new FileInputStream(this.iconFile)) {
                    final NativeImage nativeImage4 = NativeImage.fromInputStream(inputStream2);
                    Validate.validState(nativeImage4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(nativeImage4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    final NativeImageBackedTexture nativeImageBackedTexture5 = new NativeImageBackedTexture(nativeImage4);
                    this.client.getTextureManager().registerTexture(this.iconLocation, nativeImageBackedTexture5);
                    return nativeImageBackedTexture5;
                }
                catch (Throwable throwable2) {
                    LevelListWidget.LOGGER.error("Invalid icon for world {}", this.level.getName(), throwable2);
                    this.iconFile = null;
                    return null;
                }
            }
            this.client.getTextureManager().destroyTexture(this.iconLocation);
            return null;
        }
        
        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }
    }
}
