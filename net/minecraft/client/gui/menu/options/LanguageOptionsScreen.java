package net.minecraft.client.gui.menu.options;

import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.NarratorManager;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.options.GameOption;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.options.GameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class LanguageOptionsScreen extends Screen
{
    protected final Screen parent;
    private LanguageSelectionListWidget languageSelectionList;
    private final GameOptions options;
    private final LanguageManager languageManager;
    private OptionButtonWidget forceUnicodeButton;
    private ButtonWidget doneButton;
    
    public LanguageOptionsScreen(final Screen parent, final GameOptions options, final LanguageManager languageManager) {
        super(new TranslatableTextComponent("options.language", new Object[0]));
        this.parent = parent;
        this.options = options;
        this.languageManager = languageManager;
    }
    
    @Override
    protected void init() {
        this.languageSelectionList = new LanguageSelectionListWidget(this.minecraft);
        this.children.add(this.languageSelectionList);
        this.forceUnicodeButton = this.<OptionButtonWidget>addButton(new OptionButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, GameOption.FORCE_UNICODE_FONT, GameOption.FORCE_UNICODE_FONT.getDisplayString(this.options), buttonWidget -> {
            GameOption.FORCE_UNICODE_FONT.set(this.options);
            this.options.write();
            buttonWidget.setMessage(GameOption.FORCE_UNICODE_FONT.getDisplayString(this.options));
            this.minecraft.onResolutionChanged();
            return;
        }));
        final LanguageSelectionListWidget.LanguageItem languageItem2;
        this.doneButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
            languageItem2 = this.languageSelectionList.getSelected();
            if (languageItem2 != null && !languageItem2.languageDefinition.getCode().equals(this.languageManager.getLanguage().getCode())) {
                this.languageManager.setLanguage(languageItem2.languageDefinition);
                this.options.language = languageItem2.languageDefinition.getCode();
                this.minecraft.reloadResources();
                this.font.setRightToLeft(this.languageManager.isRightToLeft());
                this.doneButton.setMessage(I18n.translate("gui.done"));
                this.forceUnicodeButton.setMessage(GameOption.FORCE_UNICODE_FONT.getDisplayString(this.options));
                this.options.write();
            }
            this.minecraft.openScreen(this.parent);
            return;
        }));
        super.init();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.languageSelectionList.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.font, "(" + I18n.translate("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.render(mouseX, mouseY, delta);
    }
    
    @Environment(EnvType.CLIENT)
    class LanguageSelectionListWidget extends AlwaysSelectedEntryListWidget<LanguageItem>
    {
        public LanguageSelectionListWidget(final MinecraftClient client) {
            super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);
            for (final LanguageDefinition languageDefinition4 : LanguageOptionsScreen.this.languageManager.getAllLanguages()) {
                final LanguageItem languageItem5 = new LanguageItem(languageDefinition4);
                this.addEntry(languageItem5);
                if (LanguageOptionsScreen.this.languageManager.getLanguage().getCode().equals(languageDefinition4.getCode())) {
                    this.a(languageItem5);
                }
            }
            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
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
        
        public void a(@Nullable final LanguageItem entry) {
            super.setSelected(entry);
            if (entry != null) {
                NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.select", new Object[] { entry.languageDefinition }).getString());
            }
        }
        
        @Override
        protected void renderBackground() {
            LanguageOptionsScreen.this.renderBackground();
        }
        
        @Override
        protected boolean isFocused() {
            return LanguageOptionsScreen.this.getFocused() == this;
        }
        
        @Environment(EnvType.CLIENT)
        public class LanguageItem extends Entry<LanguageItem>
        {
            private final LanguageDefinition languageDefinition;
            
            public LanguageItem(final LanguageDefinition languageDefinition) {
                this.languageDefinition = languageDefinition;
            }
            
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                LanguageOptionsScreen.this.font.setRightToLeft(true);
                LanguageSelectionListWidget.this.drawCenteredString(LanguageOptionsScreen.this.font, this.languageDefinition.toString(), LanguageSelectionListWidget.this.width / 2, integer2 + 1, 16777215);
                LanguageOptionsScreen.this.font.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
            }
            
            @Override
            public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                }
                return false;
            }
            
            private void onPressed() {
                LanguageSelectionListWidget.this.a(this);
            }
        }
    }
}
