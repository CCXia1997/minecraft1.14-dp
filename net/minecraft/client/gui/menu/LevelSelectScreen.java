package net.minecraft.client.gui.menu;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class LevelSelectScreen extends Screen
{
    protected final Screen parent;
    private String tooltipText;
    private ButtonWidget deleteButton;
    private ButtonWidget selectButton;
    private ButtonWidget editButton;
    private ButtonWidget recreateButton;
    protected TextFieldWidget searchBox;
    private LevelListWidget levelList;
    
    public LevelSelectScreen(final Screen parent) {
        super(new TranslatableTextComponent("selectWorld.title", new Object[0]));
        this.parent = parent;
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public void tick() {
        this.searchBox.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        (this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, I18n.translate("selectWorld.search"))).setChangedListener(string -> this.levelList.filter(() -> string, false));
        this.levelList = new LevelListWidget(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.levelList);
        this.children.add(this.searchBox);
        this.children.add(this.levelList);
        this.selectButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select"), buttonWidget -> this.levelList.a().ifPresent(LevelListWidget.LevelItem::play)));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create"), buttonWidget -> this.minecraft.openScreen(new NewLevelScreen(this))));
        this.editButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.edit"), buttonWidget -> this.levelList.a().ifPresent(LevelListWidget.LevelItem::edit)));
        this.deleteButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete"), buttonWidget -> this.levelList.a().ifPresent(LevelListWidget.LevelItem::delete)));
        this.recreateButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate"), buttonWidget -> this.levelList.a().ifPresent(LevelListWidget.LevelItem::recreate)));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.worldSelected(false);
        this.setInitialFocus(this.searchBox);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        return this.searchBox.charTyped(chr, keyCode);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.tooltipText = null;
        this.levelList.render(mouseX, mouseY, delta);
        this.searchBox.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        super.render(mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.tooltipText)), mouseX, mouseY);
        }
    }
    
    public void setTooltip(final String value) {
        this.tooltipText = value;
    }
    
    public void worldSelected(final boolean boolean1) {
        this.selectButton.active = boolean1;
        this.deleteButton.active = boolean1;
        this.editButton.active = boolean1;
        this.recreateButton.active = boolean1;
    }
    
    @Override
    public void removed() {
        if (this.levelList != null) {
            this.levelList.children().forEach(LevelListWidget.LevelItem::close);
        }
    }
}
