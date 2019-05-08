package net.minecraft.client.gui.widget;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.util.SystemUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.resource.language.I18n;
import com.google.common.base.Predicates;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Consumer;
import net.minecraft.client.font.TextRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;

@Environment(EnvType.CLIENT)
public class TextFieldWidget extends AbstractButtonWidget implements Drawable, Element
{
    private final TextRenderer textRenderer;
    private String text;
    private int maxLength;
    private int focusedTicks;
    private boolean focused;
    private boolean f;
    private boolean editable;
    private boolean h;
    private int i;
    private int cursorMax;
    private int cursorMin;
    private int l;
    private int m;
    private String suggestion;
    private Consumer<String> changedListener;
    private Predicate<String> textPredicate;
    private BiFunction<String, Integer, String> renderTextProvider;
    
    public TextFieldWidget(final TextRenderer textRenderer, final int x, final int y, final int width, final int height, final String string) {
        this(textRenderer, x, y, width, height, null, string);
    }
    
    public TextFieldWidget(final TextRenderer textRenderer, final int x, final int y, final int width, final int height, @Nullable final TextFieldWidget textFieldWidget, final String string) {
        super(x, y, width, height, string);
        this.text = "";
        this.maxLength = 32;
        this.focused = true;
        this.f = true;
        this.editable = true;
        this.l = 14737632;
        this.m = 7368816;
        this.textPredicate = Predicates.alwaysTrue();
        this.renderTextProvider = (BiFunction<String, Integer, String>)((string, integer) -> string);
        this.textRenderer = textRenderer;
        if (textFieldWidget != null) {
            this.setText(textFieldWidget.getText());
        }
    }
    
    public void setChangedListener(final Consumer<String> consumer) {
        this.changedListener = consumer;
    }
    
    public void setRenderTextProvider(final BiFunction<String, Integer, String> renderTextProvider) {
        this.renderTextProvider = renderTextProvider;
    }
    
    public void tick() {
        ++this.focusedTicks;
    }
    
    @Override
    protected String getNarrationMessage() {
        final String string1 = this.getMessage();
        if (string1.isEmpty()) {
            return "";
        }
        return I18n.translate("gui.narrate.editBox", string1, this.text);
    }
    
    public void setText(final String text) {
        if (!this.textPredicate.test(text)) {
            return;
        }
        if (text.length() > this.maxLength) {
            this.text = text.substring(0, this.maxLength);
        }
        else {
            this.text = text;
        }
        this.e();
        this.j(this.cursorMax);
        this.onChanged(text);
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getSelectedText() {
        final int integer1 = (this.cursorMax < this.cursorMin) ? this.cursorMax : this.cursorMin;
        final int integer2 = (this.cursorMax < this.cursorMin) ? this.cursorMin : this.cursorMax;
        return this.text.substring(integer1, integer2);
    }
    
    public void a(final Predicate<String> predicate) {
        this.textPredicate = predicate;
    }
    
    public void addText(final String string) {
        String string2 = "";
        final String string3 = SharedConstants.stripInvalidChars(string);
        final int integer4 = (this.cursorMax < this.cursorMin) ? this.cursorMax : this.cursorMin;
        final int integer5 = (this.cursorMax < this.cursorMin) ? this.cursorMin : this.cursorMax;
        final int integer6 = this.maxLength - this.text.length() - (integer4 - integer5);
        if (!this.text.isEmpty()) {
            string2 += this.text.substring(0, integer4);
        }
        int integer7;
        if (integer6 < string3.length()) {
            string2 += string3.substring(0, integer6);
            integer7 = integer6;
        }
        else {
            string2 += string3;
            integer7 = string3.length();
        }
        if (!this.text.isEmpty() && integer5 < this.text.length()) {
            string2 += this.text.substring(integer5);
        }
        if (!this.textPredicate.test(string2)) {
            return;
        }
        this.text = string2;
        this.setCursor(integer4 + integer7);
        this.j(this.cursorMax);
        this.onChanged(this.text);
    }
    
    private void onChanged(final String string) {
        if (this.changedListener != null) {
            this.changedListener.accept(string);
        }
        this.nextNarration = SystemUtil.getMeasuringTimeMs() + 500L;
    }
    
    private void m(final int integer) {
        if (Screen.hasControlDown()) {
            this.a(integer);
        }
        else {
            this.b(integer);
        }
    }
    
    public void a(final int integer) {
        if (this.text.isEmpty()) {
            return;
        }
        if (this.cursorMin != this.cursorMax) {
            this.addText("");
            return;
        }
        this.b(this.c(integer) - this.cursorMax);
    }
    
    public void b(final int integer) {
        if (this.text.isEmpty()) {
            return;
        }
        if (this.cursorMin != this.cursorMax) {
            this.addText("");
            return;
        }
        final boolean boolean2 = integer < 0;
        final int integer2 = boolean2 ? (this.cursorMax + integer) : this.cursorMax;
        final int integer3 = boolean2 ? this.cursorMax : (this.cursorMax + integer);
        String string5 = "";
        if (integer2 >= 0) {
            string5 = this.text.substring(0, integer2);
        }
        if (integer3 < this.text.length()) {
            string5 += this.text.substring(integer3);
        }
        if (!this.textPredicate.test(string5)) {
            return;
        }
        this.text = string5;
        if (boolean2) {
            this.moveCursor(integer);
        }
        this.onChanged(this.text);
    }
    
    public int c(final int integer) {
        return this.a(integer, this.getCursor());
    }
    
    private int a(final int integer1, final int integer2) {
        return this.a(integer1, integer2, true);
    }
    
    private int a(final int integer1, final int integer2, final boolean boolean3) {
        int integer3 = integer2;
        final boolean boolean4 = integer1 < 0;
        for (int integer4 = Math.abs(integer1), integer5 = 0; integer5 < integer4; ++integer5) {
            if (boolean4) {
                while (boolean3 && integer3 > 0 && this.text.charAt(integer3 - 1) == ' ') {
                    --integer3;
                }
                while (integer3 > 0 && this.text.charAt(integer3 - 1) != ' ') {
                    --integer3;
                }
            }
            else {
                final int integer6 = this.text.length();
                integer3 = this.text.indexOf(32, integer3);
                if (integer3 == -1) {
                    integer3 = integer6;
                }
                else {
                    while (boolean3 && integer3 < integer6 && this.text.charAt(integer3) == ' ') {
                        ++integer3;
                    }
                }
            }
        }
        return integer3;
    }
    
    public void moveCursor(final int integer) {
        this.e(this.cursorMax + integer);
    }
    
    public void e(final int integer) {
        this.setCursor(integer);
        if (!this.h) {
            this.j(this.cursorMax);
        }
        this.onChanged(this.text);
    }
    
    public void setCursor(final int cursor) {
        this.cursorMax = MathHelper.clamp(cursor, 0, this.text.length());
    }
    
    public void d() {
        this.e(0);
    }
    
    public void e() {
        this.e(this.text.length());
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (!this.f()) {
            return false;
        }
        this.h = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.e();
            this.j(0);
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            if (this.editable) {
                this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
            }
            return true;
        }
        if (Screen.isCut(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            if (this.editable) {
                this.addText("");
            }
            return true;
        }
        switch (keyCode) {
            case 263: {
                if (Screen.hasControlDown()) {
                    this.e(this.c(-1));
                }
                else {
                    this.moveCursor(-1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.e(this.c(1));
                }
                else {
                    this.moveCursor(1);
                }
                return true;
            }
            case 259: {
                if (this.editable) {
                    this.m(-1);
                }
                return true;
            }
            case 261: {
                if (this.editable) {
                    this.m(1);
                }
                return true;
            }
            case 268: {
                this.d();
                return true;
            }
            case 269: {
                this.e();
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean f() {
        return this.isVisible() && this.isFocused() && this.l();
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        if (!this.f()) {
            return false;
        }
        if (SharedConstants.isValidChar(chr)) {
            if (this.editable) {
                this.addText(Character.toString(chr));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (!this.isVisible()) {
            return false;
        }
        final boolean boolean6 = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
        if (this.f) {
            this.a(boolean6);
        }
        if (this.isFocused() && boolean6 && button == 0) {
            int integer7 = MathHelper.floor(mouseX) - this.x;
            if (this.focused) {
                integer7 -= 4;
            }
            final String string8 = this.textRenderer.trimToWidth(this.text.substring(this.i), this.h());
            this.e(this.textRenderer.trimToWidth(string8, integer7).length() + this.i);
            return true;
        }
        return false;
    }
    
    public void a(final boolean boolean1) {
        super.setFocused(boolean1);
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        if (!this.isVisible()) {
            return;
        }
        if (this.hasBorder()) {
            DrawableHelper.fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            DrawableHelper.fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }
        final int integer4 = this.editable ? this.l : this.m;
        final int integer5 = this.cursorMax - this.i;
        int integer6 = this.cursorMin - this.i;
        final String string7 = this.textRenderer.trimToWidth(this.text.substring(this.i), this.h());
        final boolean boolean8 = integer5 >= 0 && integer5 <= string7.length();
        final boolean boolean9 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && boolean8;
        final int integer7 = this.focused ? (this.x + 4) : this.x;
        final int integer8 = this.focused ? (this.y + (this.height - 8) / 2) : this.y;
        int integer9 = integer7;
        if (integer6 > string7.length()) {
            integer6 = string7.length();
        }
        if (!string7.isEmpty()) {
            final String string8 = boolean8 ? string7.substring(0, integer5) : string7;
            integer9 = this.textRenderer.drawWithShadow(this.renderTextProvider.apply(string8, this.i), (float)integer9, (float)integer8, integer4);
        }
        final boolean boolean10 = this.cursorMax < this.text.length() || this.text.length() >= this.getMaxLength();
        int integer10 = integer9;
        if (!boolean8) {
            integer10 = ((integer5 > 0) ? (integer7 + this.width) : integer7);
        }
        else if (boolean10) {
            --integer10;
            --integer9;
        }
        if (!string7.isEmpty() && boolean8 && integer5 < string7.length()) {
            this.textRenderer.drawWithShadow(this.renderTextProvider.apply(string7.substring(integer5), this.cursorMax), (float)integer9, (float)integer8, integer4);
        }
        if (!boolean10 && this.suggestion != null) {
            this.textRenderer.drawWithShadow(this.suggestion, (float)(integer10 - 1), (float)integer8, -8355712);
        }
        if (boolean9) {
            if (boolean10) {
                final int left = integer10;
                final int top = integer8 - 1;
                final int right = integer10 + 1;
                final int n = integer8 + 1;
                this.textRenderer.getClass();
                DrawableHelper.fill(left, top, right, n + 9, -3092272);
            }
            else {
                this.textRenderer.drawWithShadow("_", (float)integer10, (float)integer8, integer4);
            }
        }
        if (integer6 != integer5) {
            final int integer11 = integer7 + this.textRenderer.getStringWidth(string7.substring(0, integer6));
            final int integer12 = integer10;
            final int integer13 = integer8 - 1;
            final int integer14 = integer11 - 1;
            final int n2 = integer8 + 1;
            this.textRenderer.getClass();
            this.a(integer12, integer13, integer14, n2 + 9);
        }
    }
    
    private void a(int integer1, int integer2, int integer3, int integer4) {
        if (integer1 < integer3) {
            final int integer5 = integer1;
            integer1 = integer3;
            integer3 = integer5;
        }
        if (integer2 < integer4) {
            final int integer5 = integer2;
            integer2 = integer4;
            integer4 = integer5;
        }
        if (integer3 > this.x + this.width) {
            integer3 = this.x + this.width;
        }
        if (integer1 > this.x + this.width) {
            integer1 = this.x + this.width;
        }
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        GlStateManager.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(GlStateManager.LogicOp.n);
        bufferBuilder6.begin(7, VertexFormats.POSITION);
        bufferBuilder6.vertex(integer1, integer4, 0.0).next();
        bufferBuilder6.vertex(integer3, integer4, 0.0).next();
        bufferBuilder6.vertex(integer3, integer2, 0.0).next();
        bufferBuilder6.vertex(integer1, integer2, 0.0).next();
        tessellator5.draw();
        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }
    
    public void setMaxLength(final int integer) {
        this.maxLength = integer;
        if (this.text.length() > integer) {
            this.onChanged(this.text = this.text.substring(0, integer));
        }
    }
    
    private int getMaxLength() {
        return this.maxLength;
    }
    
    public int getCursor() {
        return this.cursorMax;
    }
    
    private boolean hasBorder() {
        return this.focused;
    }
    
    public void setHasBorder(final boolean hasBorder) {
        this.focused = hasBorder;
    }
    
    public void h(final int integer) {
        this.l = integer;
    }
    
    public void i(final int integer) {
        this.m = integer;
    }
    
    @Override
    public boolean changeFocus(final boolean lookForwards) {
        return this.visible && this.editable && super.changeFocus(lookForwards);
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return this.visible && mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
    }
    
    @Override
    protected void onFocusedChanged(final boolean boolean1) {
        if (boolean1) {
            this.focusedTicks = 0;
        }
    }
    
    private boolean l() {
        return this.editable;
    }
    
    public void setIsEditable(final boolean editable) {
        this.editable = editable;
    }
    
    public int h() {
        return this.hasBorder() ? (this.width - 8) : this.width;
    }
    
    public void j(final int integer) {
        final int integer2 = this.text.length();
        this.cursorMin = MathHelper.clamp(integer, 0, integer2);
        if (this.textRenderer != null) {
            if (this.i > integer2) {
                this.i = integer2;
            }
            final int integer3 = this.h();
            final String string4 = this.textRenderer.trimToWidth(this.text.substring(this.i), integer3);
            final int integer4 = string4.length() + this.i;
            if (this.cursorMin == this.i) {
                this.i -= this.textRenderer.trimToWidth(this.text, integer3, true).length();
            }
            if (this.cursorMin > integer4) {
                this.i += this.cursorMin - integer4;
            }
            else if (this.cursorMin <= this.i) {
                this.i -= this.i - this.cursorMin;
            }
            this.i = MathHelper.clamp(this.i, 0, integer2);
        }
    }
    
    public void d(final boolean boolean1) {
        this.f = boolean1;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public void setSuggestion(@Nullable final String suggestion) {
        this.suggestion = suggestion;
    }
    
    public int k(final int integer) {
        if (integer > this.text.length()) {
            return this.x;
        }
        return this.x + this.textRenderer.getStringWidth(this.text.substring(0, integer));
    }
    
    public void setX(final int x) {
        this.x = x;
    }
}
