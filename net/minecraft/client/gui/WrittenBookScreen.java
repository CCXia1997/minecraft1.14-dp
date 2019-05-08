package net.minecraft.client.gui;

import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.TextComponentUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;
import java.util.Collections;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.gui.widget.BookPageButtonWidget;
import net.minecraft.text.TextComponent;
import java.util.List;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WrittenBookScreen extends Screen
{
    public static final Contents EMPTY_PROVIDER;
    public static final Identifier BOOK_TEXTURE;
    private Contents contents;
    private int pageIndex;
    private List<TextComponent> cachedPage;
    private int cachedPageIndex;
    private BookPageButtonWidget lastPageButton;
    private BookPageButtonWidget nextPageButton;
    private final boolean pageTurnSound;
    
    public WrittenBookScreen(final Contents pageProvider) {
        this(pageProvider, true);
    }
    
    public WrittenBookScreen() {
        this(WrittenBookScreen.EMPTY_PROVIDER, false);
    }
    
    private WrittenBookScreen(final Contents contents, final boolean playPageTurnSound) {
        super(NarratorManager.a);
        this.cachedPage = Collections.<TextComponent>emptyList();
        this.cachedPageIndex = -1;
        this.contents = contents;
        this.pageTurnSound = playPageTurnSound;
    }
    
    public void setPageProvider(final Contents pageProvider) {
        this.contents = pageProvider;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getLineCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }
    
    public boolean setPage(final int index) {
        final int integer2 = MathHelper.clamp(index, 0, this.contents.getLineCount() - 1);
        if (integer2 != this.pageIndex) {
            this.pageIndex = integer2;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }
    
    protected boolean jumpToPage(final int page) {
        return this.setPage(page);
    }
    
    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }
    
    protected void addCloseButton() {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(null)));
    }
    
    protected void addPageButtons() {
        final int integer1 = (this.width - 192) / 2;
        final int integer2 = 2;
        this.lastPageButton = this.<BookPageButtonWidget>addButton(new BookPageButtonWidget(integer1 + 116, 159, true, buttonWidget -> this.goToNextPage(), this.pageTurnSound));
        this.nextPageButton = this.<BookPageButtonWidget>addButton(new BookPageButtonWidget(integer1 + 43, 159, false, buttonWidget -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }
    
    private int getPageCount() {
        return this.contents.getLineCount();
    }
    
    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }
    
    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }
    
    private void updatePageButtons() {
        this.lastPageButton.visible = (this.pageIndex < this.getPageCount() - 1);
        this.nextPageButton.visible = (this.pageIndex > 0);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266: {
                this.nextPageButton.onPress();
                return true;
            }
            case 267: {
                this.lastPageButton.onPress();
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(WrittenBookScreen.BOOK_TEXTURE);
        final int integer4 = (this.width - 192) / 2;
        final int integer5 = 2;
        this.blit(integer4, 2, 0, 0, 192, 192);
        final String string6 = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        if (this.cachedPageIndex != this.pageIndex) {
            final TextComponent textComponent7 = this.contents.getLineOrDefault(this.pageIndex);
            this.cachedPage = TextComponentUtil.wrapLines(textComponent7, 114, this.font, true, true);
        }
        this.cachedPageIndex = this.pageIndex;
        final int integer6 = this.getStringWidth(string6);
        this.font.draw(string6, (float)(integer4 - integer6 + 192 - 44), 18.0f, 0);
        final int n = 128;
        this.font.getClass();
        for (int integer7 = Math.min(n / 9, this.cachedPage.size()), integer8 = 0; integer8 < integer7; ++integer8) {
            final TextComponent textComponent8 = this.cachedPage.get(integer8);
            final TextRenderer font = this.font;
            final String formattedText = textComponent8.getFormattedText();
            final float x = (float)(integer4 + 36);
            final int n2 = 32;
            final int n3 = integer8;
            this.font.getClass();
            font.draw(formattedText, x, (float)(n2 + n3 * 9), 0);
        }
        final TextComponent textComponent9 = this.getLineAt(mouseX, mouseY);
        if (textComponent9 != null) {
            this.renderComponentHoverEffect(textComponent9, mouseX, mouseY);
        }
        super.render(mouseX, mouseY, delta);
    }
    
    private int getStringWidth(final String string) {
        return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            final TextComponent textComponent6 = this.getLineAt(mouseX, mouseY);
            if (textComponent6 != null && this.handleComponentClicked(textComponent6)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean handleComponentClicked(final TextComponent textComponent) {
        final ClickEvent clickEvent2 = textComponent.getStyle().getClickEvent();
        if (clickEvent2 == null) {
            return false;
        }
        if (clickEvent2.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            final String string3 = clickEvent2.getValue();
            try {
                final int integer4 = Integer.parseInt(string3) - 1;
                return this.jumpToPage(integer4);
            }
            catch (Exception ex) {
                return false;
            }
        }
        final boolean boolean3 = super.handleComponentClicked(textComponent);
        if (boolean3 && clickEvent2.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.openScreen(null);
        }
        return boolean3;
    }
    
    @Nullable
    public TextComponent getLineAt(final double x, final double y) {
        if (this.cachedPage == null) {
            return null;
        }
        final int integer5 = MathHelper.floor(x - (this.width - 192) / 2 - 36.0);
        final int integer6 = MathHelper.floor(y - 2.0 - 30.0);
        if (integer5 < 0 || integer6 < 0) {
            return null;
        }
        final int n = 128;
        this.font.getClass();
        final int integer7 = Math.min(n / 9, this.cachedPage.size());
        if (integer5 <= 114) {
            final int n2 = integer6;
            this.minecraft.textRenderer.getClass();
            if (n2 < 9 * integer7 + integer7) {
                final int n3 = integer6;
                this.minecraft.textRenderer.getClass();
                final int integer8 = n3 / 9;
                if (integer8 >= 0 && integer8 < this.cachedPage.size()) {
                    final TextComponent textComponent9 = this.cachedPage.get(integer8);
                    int integer9 = 0;
                    for (final TextComponent textComponent10 : textComponent9) {
                        if (textComponent10 instanceof StringTextComponent) {
                            integer9 += this.minecraft.textRenderer.getStringWidth(textComponent10.getFormattedText());
                            if (integer9 > integer5) {
                                return textComponent10;
                            }
                            continue;
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }
    
    public static List<String> getLines(final CompoundTag compoundTag) {
        final ListTag listTag2 = compoundTag.getList("pages", 8).copy();
        final ImmutableList.Builder<String> builder3 = ImmutableList.<String>builder();
        for (int integer4 = 0; integer4 < listTag2.size(); ++integer4) {
            builder3.add(listTag2.getString(integer4));
        }
        return builder3.build();
    }
    
    static {
        EMPTY_PROVIDER = new Contents() {
            @Override
            public int getLineCount() {
                return 0;
            }
            
            @Override
            public TextComponent getLine(final int line) {
                return new StringTextComponent("");
            }
        };
        BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    }
    
    @Environment(EnvType.CLIENT)
    public interface Contents
    {
        int getLineCount();
        
        TextComponent getLine(final int arg1);
        
        default TextComponent getLineOrDefault(final int line) {
            if (line >= 0 && line < this.getLineCount()) {
                return this.getLine(line);
            }
            return new StringTextComponent("");
        }
        
        default Contents create(final ItemStack stack) {
            final Item item2 = stack.getItem();
            if (item2 == Items.nE) {
                return new WrittenBookContents(stack);
            }
            if (item2 == Items.nD) {
                return new WritableBookContents(stack);
            }
            return WrittenBookScreen.EMPTY_PROVIDER;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class WrittenBookContents implements Contents
    {
        private final List<String> lines;
        
        public WrittenBookContents(final ItemStack itemStack) {
            this.lines = getLines(itemStack);
        }
        
        private static List<String> getLines(final ItemStack itemStack) {
            final CompoundTag compoundTag2 = itemStack.getTag();
            if (compoundTag2 != null && WrittenBookItem.isValidBook(compoundTag2)) {
                return WrittenBookScreen.getLines(compoundTag2);
            }
            return ImmutableList.<String>of(new TranslatableTextComponent("book.invalid.tag", new Object[0]).applyFormat(TextFormat.e).getFormattedText());
        }
        
        @Override
        public int getLineCount() {
            return this.lines.size();
        }
        
        @Override
        public TextComponent getLine(final int line) {
            final String string2 = this.lines.get(line);
            try {
                final TextComponent textComponent3 = TextComponent.Serializer.fromJsonString(string2);
                if (textComponent3 != null) {
                    return textComponent3;
                }
            }
            catch (Exception ex) {}
            return new StringTextComponent(string2);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class WritableBookContents implements Contents
    {
        private final List<String> lines;
        
        public WritableBookContents(final ItemStack itemStack) {
            this.lines = getLines(itemStack);
        }
        
        private static List<String> getLines(final ItemStack itemStack) {
            final CompoundTag compoundTag2 = itemStack.getTag();
            return (List<String>)((compoundTag2 != null) ? WrittenBookScreen.getLines(compoundTag2) : ImmutableList.of());
        }
        
        @Override
        public int getLineCount() {
            return this.lines.size();
        }
        
        @Override
        public TextComponent getLine(final int line) {
            return new StringTextComponent(this.lines.get(line));
        }
    }
}
