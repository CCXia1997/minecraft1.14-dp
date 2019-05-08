package net.minecraft.client.gui.menu;

import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.options.ResourcePackOptionsScreen;
import net.minecraft.client.gui.menu.options.ChatOptionsScreen;
import net.minecraft.client.gui.menu.options.LanguageOptionsScreen;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import net.minecraft.client.gui.menu.options.VideoOptionsScreen;
import net.minecraft.client.gui.menu.options.AudioOptionsScreen;
import net.minecraft.client.gui.menu.options.SkinOptionsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GameOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen
{
    private static final GameOption[] OPTIONS;
    private final Screen parent;
    private final GameOptions settings;
    private ButtonWidget difficultyButton;
    private LockButtonWidget lockDifficultyButton;
    private Difficulty difficulty;
    
    public SettingsScreen(final Screen parent, final GameOptions gameOptions) {
        super(new TranslatableTextComponent("options.title", new Object[0]));
        this.parent = parent;
        this.settings = gameOptions;
    }
    
    @Override
    protected void init() {
        int integer1 = 0;
        for (final GameOption gameOption5 : SettingsScreen.OPTIONS) {
            final int integer2 = this.width / 2 - 155 + integer1 % 2 * 160;
            final int integer3 = this.height / 6 - 12 + 24 * (integer1 >> 1);
            this.<AbstractButtonWidget>addButton(gameOption5.createOptionButton(this.minecraft.options, integer2, integer3, 150));
            ++integer1;
        }
        if (this.minecraft.world != null) {
            this.difficulty = this.minecraft.world.getDifficulty();
            this.difficultyButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + integer1 % 2 * 160, this.height / 6 - 12 + 24 * (integer1 >> 1), 150, 20, this.getDifficultyButtonText(this.difficulty), buttonWidget -> {
                this.difficulty = Difficulty.getDifficulty(this.difficulty.getId() + 1);
                this.minecraft.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(this.difficulty));
                this.difficultyButton.setMessage(this.getDifficultyButtonText(this.difficulty));
                return;
            }));
            if (this.minecraft.isIntegratedServerRunning() && !this.minecraft.world.getLevelProperties().isHardcore()) {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                final MinecraftClient minecraft;
                final TranslatableTextComponent textComponent2;
                final Object[] arr;
                final TranslatableTextComponent translatableTextComponent;
                final Object o;
                final TextComponent textComponent3;
                final String string;
                final Screen screen;
                final BooleanConsumer booleanConsumer;
                (this.lockDifficultyButton = this.<LockButtonWidget>addButton(new LockButtonWidget(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, buttonWidget -> {
                    minecraft = this.minecraft;
                    // new(net.minecraft.client.gui.menu.YesNoScreen.class)
                    this::tmp;
                    textComponent2 = new TranslatableTextComponent("difficulty.lock.title", new Object[0]);
                    // new(net.minecraft.text.TranslatableTextComponent.class)
                    arr = new Object[] { null };
                    new TranslatableTextComponent("options.difficulty." + this.minecraft.world.getLevelProperties().getDifficulty().getTranslationKey(), new Object[0]);
                    arr[o] = translatableTextComponent;
                    new TranslatableTextComponent(string, arr);
                    new YesNoScreen(booleanConsumer, textComponent2, textComponent3);
                    minecraft.openScreen(screen);
                    return;
                }))).setLocked(this.minecraft.world.getLevelProperties().isDifficultyLocked());
                this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
                this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
            }
            else {
                this.difficultyButton.active = false;
            }
        }
        else {
            this.<OptionButtonWidget>addButton(new OptionButtonWidget(this.width / 2 - 155 + integer1 % 2 * 160, this.height / 6 - 12 + 24 * (integer1 >> 1), 150, 20, GameOption.REALMS_NOTIFICATIONS, GameOption.REALMS_NOTIFICATIONS.getDisplayString(this.settings), buttonWidget -> {
                GameOption.REALMS_NOTIFICATIONS.set(this.settings);
                this.settings.write();
                buttonWidget.setMessage(GameOption.REALMS_NOTIFICATIONS.getDisplayString(this.settings));
                return;
            }));
        }
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation"), buttonWidget -> this.minecraft.openScreen(new SkinOptionsScreen(this))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds"), buttonWidget -> this.minecraft.openScreen(new AudioOptionsScreen(this, this.settings))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.video"), buttonWidget -> this.minecraft.openScreen(new VideoOptionsScreen(this, this.settings))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.controls"), buttonWidget -> this.minecraft.openScreen(new ControlsOptionsScreen(this, this.settings))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.language"), buttonWidget -> this.minecraft.openScreen(new LanguageOptionsScreen(this, this.settings, this.minecraft.getLanguageManager()))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title"), buttonWidget -> this.minecraft.openScreen(new ChatOptionsScreen(this, this.settings))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack"), buttonWidget -> this.minecraft.openScreen(new ResourcePackOptionsScreen(this))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.accessibility.title"), buttonWidget -> this.minecraft.openScreen(new AccessibilityScreen(this, this.settings))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }
    
    public String getDifficultyButtonText(final Difficulty difficulty) {
        return new TranslatableTextComponent("options.difficulty", new Object[0]).append(": ").append(difficulty.toTextComponent()).getFormattedText();
    }
    
    private void tmp(final boolean difficultyLocked) {
        this.minecraft.openScreen(this);
        if (difficultyLocked && this.minecraft.world != null) {
            this.minecraft.getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
            this.lockDifficultyButton.setLocked(true);
            this.lockDifficultyButton.active = false;
            this.difficultyButton.active = false;
        }
    }
    
    @Override
    public void removed() {
        this.settings.write();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        OPTIONS = new GameOption[] { GameOption.FOV };
    }
}
