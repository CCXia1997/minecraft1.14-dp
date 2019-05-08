package net.minecraft.client.gui.menu;

import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.StringTextComponent;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.client.MinecraftClient;
import java.nio.file.Path;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ingame.UpdateWorldScreen;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.FileUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class EditLevelScreen extends Screen
{
    private ButtonWidget saveButton;
    private final BooleanConsumer callback;
    private TextFieldWidget levelNameTextField;
    private final String levelName;
    
    public EditLevelScreen(final BooleanConsumer callback, final String levelName) {
        super(new TranslatableTextComponent("selectWorld.edit.title", new Object[0]));
        this.callback = callback;
        this.levelName = levelName;
    }
    
    @Override
    public void tick() {
        this.levelNameTextField.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        final LevelStorage levelStorage2;
        final ButtonWidget buttonWidget2 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.translate("selectWorld.edit.resetIcon"), buttonWidget -> {
            levelStorage2 = this.minecraft.getLevelStorage();
            FileUtils.deleteQuietly(levelStorage2.resolveFile(this.levelName, "icon.png"));
            buttonWidget.active = false;
            return;
        }));
        final LevelStorage levelStorage3;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.translate("selectWorld.edit.openFolder"), buttonWidget -> {
            levelStorage3 = this.minecraft.getLevelStorage();
            SystemUtil.getOperatingSystem().open(levelStorage3.resolveFile(this.levelName, "icon.png").getParentFile());
            return;
        }));
        final LevelStorage levelStorage4;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.translate("selectWorld.edit.backup"), buttonWidget -> {
            levelStorage4 = this.minecraft.getLevelStorage();
            backupLevel(levelStorage4, this.levelName);
            this.callback.accept(false);
            return;
        }));
        final LevelStorage levelStorage5;
        final Path path3;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.translate("selectWorld.edit.backupFolder"), buttonWidget -> {
            levelStorage5 = this.minecraft.getLevelStorage();
            path3 = levelStorage5.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path3) ? path3.toRealPath() : path3, new FileAttribute[0]);
            }
            catch (IOException iOException4) {
                throw new RuntimeException(iOException4);
            }
            SystemUtil.getOperatingSystem().open(path3.toFile());
            return;
        }));
        final MinecraftClient minecraft;
        final BackupPromptScreen screen;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.translate("selectWorld.edit.optimize"), buttonWidget -> {
            minecraft = this.minecraft;
            new BackupPromptScreen(this, (boolean1, boolean2) -> {
                if (boolean1) {
                    backupLevel(this.minecraft.getLevelStorage(), this.levelName);
                }
                this.minecraft.openScreen(new UpdateWorldScreen(this.callback, this.levelName, this.minecraft.getLevelStorage(), boolean2));
                return;
            }, new TranslatableTextComponent("optimizeWorld.confirm.title", new Object[0]), new TranslatableTextComponent("optimizeWorld.confirm.description", new Object[0]), true);
            minecraft.openScreen(screen);
            return;
        }));
        this.saveButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save"), buttonWidget -> this.commit()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel"), buttonWidget -> this.callback.accept(false)));
        buttonWidget2.active = this.minecraft.getLevelStorage().resolveFile(this.levelName, "icon.png").isFile();
        final LevelStorage levelStorage6 = this.minecraft.getLevelStorage();
        final LevelProperties levelProperties3 = levelStorage6.getLevelProperties(this.levelName);
        final String string2 = (levelProperties3 == null) ? "" : levelProperties3.getLevelName();
        (this.levelNameTextField = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, I18n.translate("selectWorld.enterName"))).setText(string2);
        this.levelNameTextField.setChangedListener(string -> this.saveButton.active = !string.trim().isEmpty());
        this.children.add(this.levelNameTextField);
        this.setInitialFocus(this.levelNameTextField);
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.levelNameTextField.getText();
        this.init(client, width, height);
        this.levelNameTextField.setText(string4);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    private void commit() {
        final LevelStorage levelStorage1 = this.minecraft.getLevelStorage();
        levelStorage1.renameLevel(this.levelName, this.levelNameTextField.getText().trim());
        this.callback.accept(true);
    }
    
    public static void backupLevel(final LevelStorage level, final String name) {
        final ToastManager toastManager3 = MinecraftClient.getInstance().getToastManager();
        long long4 = 0L;
        IOException iOException6 = null;
        try {
            long4 = level.backupLevel(name);
        }
        catch (IOException iOException7) {
            iOException6 = iOException7;
        }
        TextComponent textComponent7;
        TextComponent textComponent8;
        if (iOException6 != null) {
            textComponent7 = new TranslatableTextComponent("selectWorld.edit.backupFailed", new Object[0]);
            textComponent8 = new StringTextComponent(iOException6.getMessage());
        }
        else {
            textComponent7 = new TranslatableTextComponent("selectWorld.edit.backupCreated", new Object[] { name });
            textComponent8 = new TranslatableTextComponent("selectWorld.edit.backupSize", new Object[] { MathHelper.ceil(long4 / 1048576.0) });
        }
        toastManager3.add(new SystemToast(SystemToast.Type.c, textComponent7, textComponent8));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
        this.drawString(this.font, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
        this.levelNameTextField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}
