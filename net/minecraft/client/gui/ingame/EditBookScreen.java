package net.minecraft.client.gui.ingame;

import net.minecraft.util.SystemUtil;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.WrittenBookScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.Element;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.TextFormat;
import net.minecraft.SharedConstants;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.nbt.Tag;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.nbt.StringTag;
import java.util.ListIterator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Hand;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.BookPageButtonWidget;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class EditBookScreen extends Screen
{
    private final PlayerEntity player;
    private final ItemStack itemStack;
    private boolean dirty;
    private boolean signing;
    private int tickCounter;
    private int currentPage;
    private final List<String> pages;
    private String title;
    private int cursorIndex;
    private int highlightTo;
    private long lastClickTime;
    private int lastClickIndex;
    private BookPageButtonWidget buttonPreviousPage;
    private BookPageButtonWidget buttonNextPage;
    private ButtonWidget buttonDone;
    private ButtonWidget buttonSign;
    private ButtonWidget buttonFinalize;
    private ButtonWidget buttonCancel;
    private final Hand hand;
    
    public EditBookScreen(final PlayerEntity playerEntity, final ItemStack itemStack, final Hand hand) {
        super(NarratorManager.a);
        this.pages = Lists.newArrayList();
        this.title = "";
        this.lastClickIndex = -1;
        this.player = playerEntity;
        this.itemStack = itemStack;
        this.hand = hand;
        final CompoundTag compoundTag4 = itemStack.getTag();
        if (compoundTag4 != null) {
            final ListTag listTag5 = compoundTag4.getList("pages", 8).copy();
            for (int integer6 = 0; integer6 < listTag5.size(); ++integer6) {
                this.pages.add(listTag5.getString(integer6));
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
    }
    
    private int countPages() {
        return this.pages.size();
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.tickCounter;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.buttonSign = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("book.signButton"), buttonWidget -> {
            this.signing = true;
            this.updateButtons();
            return;
        }));
        this.buttonDone = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.done"), buttonWidget -> {
            this.minecraft.openScreen(null);
            this.finalizeBook(false);
            return;
        }));
        this.buttonFinalize = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton"), buttonWidget -> {
            if (this.signing) {
                this.finalizeBook(true);
                this.minecraft.openScreen(null);
            }
            return;
        }));
        this.buttonCancel = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel"), buttonWidget -> {
            if (this.signing) {
                this.signing = false;
            }
            this.updateButtons();
            return;
        }));
        final int integer1 = (this.width - 192) / 2;
        final int integer2 = 2;
        this.buttonPreviousPage = this.<BookPageButtonWidget>addButton(new BookPageButtonWidget(integer1 + 116, 159, true, buttonWidget -> this.openNextPage(), true));
        this.buttonNextPage = this.<BookPageButtonWidget>addButton(new BookPageButtonWidget(integer1 + 43, 159, false, buttonWidget -> this.openPreviousPage(), true));
        this.updateButtons();
    }
    
    private String stripFromatting(final String string) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (final char character6 : string.toCharArray()) {
            if (character6 != '§' && character6 != '\u007f') {
                stringBuilder2.append(character6);
            }
        }
        return stringBuilder2.toString();
    }
    
    private void openPreviousPage() {
        if (this.currentPage > 0) {
            --this.currentPage;
            this.cursorIndex = 0;
            this.highlightTo = this.cursorIndex;
        }
        this.updateButtons();
    }
    
    private void openNextPage() {
        if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
            this.cursorIndex = 0;
            this.highlightTo = this.cursorIndex;
        }
        else {
            this.appendNewPage();
            if (this.currentPage < this.countPages() - 1) {
                ++this.currentPage;
            }
            this.cursorIndex = 0;
            this.highlightTo = this.cursorIndex;
        }
        this.updateButtons();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    private void updateButtons() {
        this.buttonNextPage.visible = (!this.signing && this.currentPage > 0);
        this.buttonPreviousPage.visible = !this.signing;
        this.buttonDone.visible = !this.signing;
        this.buttonSign.visible = !this.signing;
        this.buttonCancel.visible = this.signing;
        this.buttonFinalize.visible = this.signing;
        this.buttonFinalize.active = !this.title.trim().isEmpty();
    }
    
    private void removeEmptyPages() {
        final ListIterator<String> listIterator1 = this.pages.listIterator(this.pages.size());
        while (listIterator1.hasPrevious() && listIterator1.previous().isEmpty()) {
            listIterator1.remove();
        }
    }
    
    private void finalizeBook(final boolean signBook) {
        if (!this.dirty) {
            return;
        }
        this.removeEmptyPages();
        final ListTag listTag2 = new ListTag();
        this.pages.stream().map(StringTag::new).forEach(listTag2::add);
        if (!this.pages.isEmpty()) {
            this.itemStack.setChildTag("pages", listTag2);
        }
        if (signBook) {
            this.itemStack.setChildTag("author", new StringTag(this.player.getGameProfile().getName()));
            this.itemStack.setChildTag("title", new StringTag(this.title.trim()));
        }
        this.minecraft.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, signBook, this.hand));
    }
    
    private void appendNewPage() {
        if (this.countPages() >= 100) {
            return;
        }
        this.pages.add("");
        this.dirty = true;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.signing) {
            return this.keyPressedSignMode(keyCode, scanCode, modifiers);
        }
        return this.keyPressedEditMode(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        if (super.charTyped(chr, keyCode)) {
            return true;
        }
        if (this.signing) {
            if (this.title.length() < 16 && SharedConstants.isValidChar(chr)) {
                this.title += Character.toString(chr);
                this.updateButtons();
                return this.dirty = true;
            }
            return false;
        }
        else {
            if (SharedConstants.isValidChar(chr)) {
                this.writeString(Character.toString(chr));
                return true;
            }
            return false;
        }
    }
    
    private boolean keyPressedEditMode(final int keyCode, final int scanCode, final int modifiers) {
        final String string4 = this.getCurrentPageContent();
        if (Screen.isSelectAll(keyCode)) {
            this.highlightTo = 0;
            this.cursorIndex = string4.length();
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            this.minecraft.keyboard.setClipboard(this.getHighlightedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            this.writeString(this.stripFromatting(TextFormat.stripFormatting(this.minecraft.keyboard.getClipboard().replaceAll("\\r", ""))));
            this.highlightTo = this.cursorIndex;
            return true;
        }
        if (Screen.isCut(keyCode)) {
            this.minecraft.keyboard.setClipboard(this.getHighlightedText());
            this.removeHighlightedText();
            return true;
        }
        switch (keyCode) {
            case 259: {
                this.applyBackspaceKey(string4);
                return true;
            }
            case 261: {
                this.applyDeleteKey(string4);
                return true;
            }
            case 257:
            case 335: {
                this.writeString("\n");
                return true;
            }
            case 263: {
                this.applyLeftArrowKey(string4);
                return true;
            }
            case 262: {
                this.applyRightArrowKey(string4);
                return true;
            }
            case 265: {
                this.applyUpArrowKey(string4);
                return true;
            }
            case 264: {
                this.applyDownArrowKey(string4);
                return true;
            }
            case 266: {
                this.buttonNextPage.onPress();
                return true;
            }
            case 267: {
                this.buttonPreviousPage.onPress();
                return true;
            }
            case 268: {
                this.moveCursorToTop(string4);
                return true;
            }
            case 269: {
                this.moveCursorToBottom(string4);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void applyBackspaceKey(final String content) {
        if (!content.isEmpty()) {
            if (this.highlightTo != this.cursorIndex) {
                this.removeHighlightedText();
            }
            else if (this.cursorIndex > 0) {
                final String string2 = new StringBuilder(content).deleteCharAt(Math.max(0, this.cursorIndex - 1)).toString();
                this.setPageContent(string2);
                this.cursorIndex = Math.max(0, this.cursorIndex - 1);
                this.highlightTo = this.cursorIndex;
            }
        }
    }
    
    private void applyDeleteKey(final String content) {
        if (!content.isEmpty()) {
            if (this.highlightTo != this.cursorIndex) {
                this.removeHighlightedText();
            }
            else if (this.cursorIndex < content.length()) {
                final String string2 = new StringBuilder(content).deleteCharAt(Math.max(0, this.cursorIndex)).toString();
                this.setPageContent(string2);
            }
        }
    }
    
    private void applyLeftArrowKey(final String content) {
        final int integer2 = this.font.isRightToLeft() ? 1 : -1;
        if (Screen.hasControlDown()) {
            this.cursorIndex = this.font.findWordEdge(content, integer2, this.cursorIndex, true);
        }
        else {
            this.cursorIndex = Math.max(0, this.cursorIndex + integer2);
        }
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }
    
    private void applyRightArrowKey(final String content) {
        final int integer2 = this.font.isRightToLeft() ? -1 : 1;
        if (Screen.hasControlDown()) {
            this.cursorIndex = this.font.findWordEdge(content, integer2, this.cursorIndex, true);
        }
        else {
            this.cursorIndex = Math.min(content.length(), this.cursorIndex + integer2);
        }
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }
    
    private void applyUpArrowKey(final String content) {
        if (!content.isEmpty()) {
            final Position position2 = this.getCursorPositionForIndex(content, this.cursorIndex);
            if (position2.y == 0) {
                this.cursorIndex = 0;
                if (!Screen.hasShiftDown()) {
                    this.highlightTo = this.cursorIndex;
                }
            }
            else {
                final int integer4 = position2.x + this.getCharWidthInString(content, this.cursorIndex) / 3;
                final int a = position2.y;
                this.font.getClass();
                final int integer3 = this.getCharacterCountInFrontOfCursor(content, new Position(integer4, a - 9));
                if (integer3 >= 0) {
                    this.cursorIndex = integer3;
                    if (!Screen.hasShiftDown()) {
                        this.highlightTo = this.cursorIndex;
                    }
                }
            }
        }
    }
    
    private void applyDownArrowKey(final String content) {
        if (!content.isEmpty()) {
            final Position position2 = this.getCursorPositionForIndex(content, this.cursorIndex);
            final int integer3 = this.font.getStringBoundedHeight(content + "" + TextFormat.BLACK + "_", 114);
            final int a = position2.y;
            this.font.getClass();
            if (a + 9 == integer3) {
                this.cursorIndex = content.length();
                if (!Screen.hasShiftDown()) {
                    this.highlightTo = this.cursorIndex;
                }
            }
            else {
                final int integer5 = position2.x + this.getCharWidthInString(content, this.cursorIndex) / 3;
                final int a2 = position2.y;
                this.font.getClass();
                final int integer4 = this.getCharacterCountInFrontOfCursor(content, new Position(integer5, a2 + 9));
                if (integer4 >= 0) {
                    this.cursorIndex = integer4;
                    if (!Screen.hasShiftDown()) {
                        this.highlightTo = this.cursorIndex;
                    }
                }
            }
        }
    }
    
    private void moveCursorToTop(final String content) {
        this.cursorIndex = this.getCharacterCountInFrontOfCursor(content, new Position(0, this.getCursorPositionForIndex(content, this.cursorIndex).y));
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }
    
    private void moveCursorToBottom(final String content) {
        this.cursorIndex = this.getCharacterCountInFrontOfCursor(content, new Position(113, this.getCursorPositionForIndex(content, this.cursorIndex).y));
        if (!Screen.hasShiftDown()) {
            this.highlightTo = this.cursorIndex;
        }
    }
    
    private void removeHighlightedText() {
        if (this.highlightTo == this.cursorIndex) {
            return;
        }
        final String string1 = this.getCurrentPageContent();
        final int integer2 = Math.min(this.cursorIndex, this.highlightTo);
        final int integer3 = Math.max(this.cursorIndex, this.highlightTo);
        final String string2 = string1.substring(0, integer2) + string1.substring(integer3);
        this.cursorIndex = integer2;
        this.highlightTo = this.cursorIndex;
        this.setPageContent(string2);
    }
    
    private int getCharWidthInString(final String string, final int index) {
        return (int)this.font.getCharWidth(string.charAt(MathHelper.clamp(index, 0, string.length() - 1)));
    }
    
    private boolean keyPressedSignMode(final int keyCode, final int scanCode, final int modifiers) {
        switch (keyCode) {
            case 259: {
                if (!this.title.isEmpty()) {
                    this.title = this.title.substring(0, this.title.length() - 1);
                    this.updateButtons();
                }
                return true;
            }
            case 257:
            case 335: {
                if (!this.title.isEmpty()) {
                    this.finalizeBook(true);
                    this.minecraft.openScreen(null);
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private String getCurrentPageContent() {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            return this.pages.get(this.currentPage);
        }
        return "";
    }
    
    private void setPageContent(final String newContent) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, newContent);
            this.dirty = true;
        }
    }
    
    private void writeString(final String string) {
        if (this.highlightTo != this.cursorIndex) {
            this.removeHighlightedText();
        }
        final String string2 = this.getCurrentPageContent();
        this.cursorIndex = MathHelper.clamp(this.cursorIndex, 0, string2.length());
        final String string3 = new StringBuilder(string2).insert(this.cursorIndex, string).toString();
        final int integer4 = this.font.getStringBoundedHeight(string3 + "" + TextFormat.BLACK + "_", 114);
        if (integer4 <= 128 && string3.length() < 1024) {
            this.setPageContent(string3);
            final int min = Math.min(this.getCurrentPageContent().length(), this.cursorIndex + string.length());
            this.cursorIndex = min;
            this.highlightTo = min;
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.setFocused(null);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(WrittenBookScreen.BOOK_TEXTURE);
        final int integer4 = (this.width - 192) / 2;
        final int integer5 = 2;
        this.blit(integer4, 2, 0, 0, 192, 192);
        if (this.signing) {
            String string6 = this.title;
            if (this.tickCounter / 6 % 2 == 0) {
                string6 = string6 + "" + TextFormat.BLACK + "_";
            }
            else {
                string6 = string6 + "" + TextFormat.h + "_";
            }
            final String string7 = I18n.translate("book.editTitle");
            final int integer6 = this.getStringWidth(string7);
            this.font.draw(string7, (float)(integer4 + 36 + (114 - integer6) / 2), 34.0f, 0);
            final int integer7 = this.getStringWidth(string6);
            this.font.draw(string6, (float)(integer4 + 36 + (114 - integer7) / 2), 50.0f, 0);
            final String string8 = I18n.translate("book.byAuthor", this.player.getName().getString());
            final int integer8 = this.getStringWidth(string8);
            this.font.draw(TextFormat.i + string8, (float)(integer4 + 36 + (114 - integer8) / 2), 60.0f, 0);
            final String string9 = I18n.translate("book.finalizeWarning");
            this.font.drawStringBounded(string9, integer4 + 36, 82, 114, 0);
        }
        else {
            final String string6 = I18n.translate("book.pageIndicator", this.currentPage + 1, this.countPages());
            final String string7 = this.getCurrentPageContent();
            final int integer6 = this.getStringWidth(string6);
            this.font.draw(string6, (float)(integer4 - integer6 + 192 - 44), 18.0f, 0);
            this.font.drawStringBounded(string7, integer4 + 36, 32, 114, 0);
            this.drawHighlight(string7);
            if (this.tickCounter / 6 % 2 == 0) {
                final Position position9 = this.getCursorPositionForIndex(string7, this.cursorIndex);
                if (this.font.isRightToLeft()) {
                    this.localizePosition(position9);
                    position9.x -= 4;
                }
                this.translateRelativePositionToGlPosition(position9);
                if (this.cursorIndex < string7.length()) {
                    final int b = position9.x;
                    final int top = position9.y - 1;
                    final int right = position9.x + 1;
                    final int a = position9.y;
                    this.font.getClass();
                    DrawableHelper.fill(b, top, right, a + 9, -16777216);
                }
                else {
                    this.font.draw("_", (float)position9.x, (float)position9.y, 0);
                }
            }
        }
        super.render(mouseX, mouseY, delta);
    }
    
    private int getStringWidth(final String string) {
        return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
    }
    
    private int getCharacterCountForWidth(final String string, final int width) {
        return this.font.getCharacterCountForWidth(string, width);
    }
    
    private String getHighlightedText() {
        final String string1 = this.getCurrentPageContent();
        final int integer2 = Math.min(this.cursorIndex, this.highlightTo);
        final int integer3 = Math.max(this.cursorIndex, this.highlightTo);
        return string1.substring(integer2, integer3);
    }
    
    private void drawHighlight(final String content) {
        if (this.highlightTo == this.cursorIndex) {
            return;
        }
        final int integer2 = Math.min(this.cursorIndex, this.highlightTo);
        final int integer3 = Math.max(this.cursorIndex, this.highlightTo);
        String string4 = content.substring(integer2, integer3);
        final int integer4 = this.font.findWordEdge(content, 1, integer3, true);
        String string5 = content.substring(integer2, integer4);
        final Position position7 = this.getCursorPositionForIndex(content, integer2);
        final int b = position7.x;
        final int a = position7.y;
        this.font.getClass();
        final Position position8 = new Position(b, a + 9);
        while (!string4.isEmpty()) {
            int integer5 = this.getCharacterCountForWidth(string5, 114 - position7.x);
            if (string4.length() <= integer5) {
                position8.x = position7.x + this.getStringWidth(string4);
                this.drawHighlightRect(position7, position8);
                break;
            }
            integer5 = Math.min(integer5, string4.length() - 1);
            final String string6 = string4.substring(0, integer5);
            final char character11 = string4.charAt(integer5);
            final boolean boolean12 = character11 == ' ' || character11 == '\n';
            string4 = TextFormat.getFormatAtEnd(string6) + string4.substring(integer5 + (boolean12 ? 1 : 0));
            string5 = TextFormat.getFormatAtEnd(string6) + string5.substring(integer5 + (boolean12 ? 1 : 0));
            position8.x = position7.x + this.getStringWidth(string6 + " ");
            this.drawHighlightRect(position7, position8);
            position7.x = 0;
            final Position position9 = position7;
            final int a2 = position7.y;
            this.font.getClass();
            position9.y = a2 + 9;
            final Position position10 = position8;
            final int a3 = position8.y;
            this.font.getClass();
            position10.y = a3 + 9;
        }
    }
    
    private void drawHighlightRect(final Position position1, final Position position2) {
        final Position position3 = new Position(position1.x, position1.y);
        final Position position4 = new Position(position2.x, position2.y);
        if (this.font.isRightToLeft()) {
            this.localizePosition(position3);
            this.localizePosition(position4);
            final int integer5 = position4.x;
            position4.x = position3.x;
            position3.x = integer5;
        }
        this.translateRelativePositionToGlPosition(position3);
        this.translateRelativePositionToGlPosition(position4);
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        GlStateManager.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(GlStateManager.LogicOp.n);
        bufferBuilder6.begin(7, VertexFormats.POSITION);
        bufferBuilder6.vertex(position3.x, position4.y, 0.0).next();
        bufferBuilder6.vertex(position4.x, position4.y, 0.0).next();
        bufferBuilder6.vertex(position4.x, position3.y, 0.0).next();
        bufferBuilder6.vertex(position3.x, position3.y, 0.0).next();
        tessellator5.draw();
        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }
    
    private Position getCursorPositionForIndex(final String content, final int index) {
        final Position position3 = new Position();
        int integer4 = 0;
        int integer5 = 0;
        String string6 = content;
        while (!string6.isEmpty()) {
            final int integer6 = this.getCharacterCountForWidth(string6, 114);
            if (string6.length() <= integer6) {
                final String string7 = string6.substring(0, Math.min(Math.max(index - integer5, 0), string6.length()));
                position3.x += this.getStringWidth(string7);
                break;
            }
            final String string7 = string6.substring(0, integer6);
            final char character9 = string6.charAt(integer6);
            final boolean boolean10 = character9 == ' ' || character9 == '\n';
            string6 = TextFormat.getFormatAtEnd(string7) + string6.substring(integer6 + (boolean10 ? 1 : 0));
            integer4 += string7.length() + (boolean10 ? 1 : 0);
            if (integer4 - 1 >= index) {
                final String string8 = string7.substring(0, Math.min(Math.max(index - integer5, 0), string7.length()));
                position3.x += this.getStringWidth(string8);
                break;
            }
            final Position position4 = position3;
            final int a = position3.y;
            this.font.getClass();
            position4.y = a + 9;
            integer5 = integer4;
        }
        return position3;
    }
    
    private void localizePosition(final Position position) {
        if (this.font.isRightToLeft()) {
            position.x = 114 - position.x;
        }
    }
    
    private void translateGlPositionToRelativePosition(final Position position) {
        position.x = position.x - (this.width - 192) / 2 - 36;
        position.y -= 32;
    }
    
    private void translateRelativePositionToGlPosition(final Position position) {
        position.x = position.x + (this.width - 192) / 2 + 36;
        position.y += 32;
    }
    
    private int getCharacterCountForStringWidth(final String string, final int width) {
        if (width < 0) {
            return 0;
        }
        float float4 = 0.0f;
        boolean boolean5 = false;
        final String string2 = string + " ";
        for (int integer7 = 0; integer7 < string2.length(); ++integer7) {
            char character8 = string2.charAt(integer7);
            float float5 = this.font.getCharWidth(character8);
            if (character8 == '§' && integer7 < string2.length() - 1) {
                character8 = string2.charAt(++integer7);
                if (character8 == 'l' || character8 == 'L') {
                    boolean5 = true;
                }
                else if (character8 == 'r' || character8 == 'R') {
                    boolean5 = false;
                }
                float5 = 0.0f;
            }
            final float float6 = float4;
            float4 += float5;
            if (boolean5 && float5 > 0.0f) {
                ++float4;
            }
            if (width >= float6 && width < float4) {
                return integer7;
            }
        }
        if (width >= float4) {
            return string2.length() - 1;
        }
        return -1;
    }
    
    private int getCharacterCountInFrontOfCursor(final String content, final Position cursorPosition) {
        final int n = 16;
        this.font.getClass();
        final int integer3 = n * 9;
        if (cursorPosition.y > integer3) {
            return -1;
        }
        int integer4 = Integer.MIN_VALUE;
        this.font.getClass();
        int integer5 = 9;
        int integer6 = 0;
        String string7 = content;
        while (!string7.isEmpty() && integer4 < integer3) {
            final int integer7 = this.getCharacterCountForWidth(string7, 114);
            if (integer7 < string7.length()) {
                final String string8 = string7.substring(0, integer7);
                if (cursorPosition.y >= integer4 && cursorPosition.y < integer5) {
                    final int integer8 = this.getCharacterCountForStringWidth(string8, cursorPosition.x);
                    return (integer8 < 0) ? -1 : (integer6 + integer8);
                }
                final char character10 = string7.charAt(integer7);
                final boolean boolean11 = character10 == ' ' || character10 == '\n';
                string7 = TextFormat.getFormatAtEnd(string8) + string7.substring(integer7 + (boolean11 ? 1 : 0));
                integer6 += string8.length() + (boolean11 ? 1 : 0);
            }
            else if (cursorPosition.y >= integer4 && cursorPosition.y < integer5) {
                final int integer9 = this.getCharacterCountForStringWidth(string7, cursorPosition.x);
                return (integer9 < 0) ? -1 : (integer6 + integer9);
            }
            integer4 = integer5;
            final int n2 = integer5;
            this.font.getClass();
            integer5 = n2 + 9;
        }
        return content.length();
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            final long long6 = SystemUtil.getMeasuringTimeMs();
            final String string8 = this.getCurrentPageContent();
            if (!string8.isEmpty()) {
                final Position position9 = new Position((int)mouseX, (int)mouseY);
                this.translateGlPositionToRelativePosition(position9);
                this.localizePosition(position9);
                final int integer10 = this.getCharacterCountInFrontOfCursor(string8, position9);
                if (integer10 >= 0) {
                    if (integer10 == this.lastClickIndex && long6 - this.lastClickTime < 250L) {
                        if (this.highlightTo == this.cursorIndex) {
                            this.highlightTo = this.font.findWordEdge(string8, -1, integer10, false);
                            this.cursorIndex = this.font.findWordEdge(string8, 1, integer10, false);
                        }
                        else {
                            this.highlightTo = 0;
                            this.cursorIndex = this.getCurrentPageContent().length();
                        }
                    }
                    else {
                        this.cursorIndex = integer10;
                        if (!Screen.hasShiftDown()) {
                            this.highlightTo = this.cursorIndex;
                        }
                    }
                }
                this.lastClickIndex = integer10;
            }
            this.lastClickTime = long6;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (button == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            final String string10 = this.pages.get(this.currentPage);
            final Position position11 = new Position((int)mouseX, (int)mouseY);
            this.translateGlPositionToRelativePosition(position11);
            this.localizePosition(position11);
            final int integer12 = this.getCharacterCountInFrontOfCursor(string10, position11);
            if (integer12 >= 0) {
                this.cursorIndex = integer12;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Environment(EnvType.CLIENT)
    class Position
    {
        private int x;
        private int y;
        
        Position() {
        }
        
        Position(final int integer2, final int integer3) {
            this.x = integer2;
            this.y = integer3;
        }
    }
}
