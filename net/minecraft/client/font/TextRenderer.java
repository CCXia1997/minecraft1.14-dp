package net.minecraft.client.font;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.text.TextFormat;
import com.google.common.collect.Lists;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.ArabicShaping;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.texture.TextureManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextRenderer implements AutoCloseable
{
    public final int fontHeight = 9;
    public final Random random;
    private final TextureManager textureManager;
    private final FontStorage fontStorage;
    private boolean rightToLeft;
    
    public TextRenderer(final TextureManager textureManager, final FontStorage fontStorage) {
        this.random = new Random();
        this.textureManager = textureManager;
        this.fontStorage = fontStorage;
    }
    
    public void setFonts(final List<Font> fonts) {
        this.fontStorage.setFonts(fonts);
    }
    
    @Override
    public void close() {
        this.fontStorage.close();
    }
    
    public int drawWithShadow(final String string, final float x, final float y, final int color) {
        GlStateManager.enableAlphaTest();
        return this.draw(string, x, y, color, true);
    }
    
    public int draw(final String string, final float x, final float y, final int color) {
        GlStateManager.enableAlphaTest();
        return this.draw(string, x, y, color, false);
    }
    
    public String mirror(final String string) {
        try {
            final Bidi bidi2 = new Bidi(new ArabicShaping(8).shape(string), 127);
            bidi2.setReorderingMode(0);
            return bidi2.writeReordered(2);
        }
        catch (ArabicShapingException ex) {
            return string;
        }
    }
    
    private int draw(String str, float x, final float y, int color, final boolean withShadow) {
        if (str == null) {
            return 0;
        }
        if (this.rightToLeft) {
            str = this.mirror(str);
        }
        if ((color & 0xFC000000) == 0x0) {
            color |= 0xFF000000;
        }
        if (withShadow) {
            this.drawLayer(str, x, y, color, true);
        }
        x = this.drawLayer(str, x, y, color, false);
        return (int)x + (withShadow ? 1 : 0);
    }
    
    private float drawLayer(final String str, float x, final float y, final int color, final boolean isShadow) {
        final float float6 = isShadow ? 0.25f : 1.0f;
        final float float7 = (color >> 16 & 0xFF) / 255.0f * float6;
        final float float8 = (color >> 8 & 0xFF) / 255.0f * float6;
        final float float9 = (color & 0xFF) / 255.0f * float6;
        float float10 = float7;
        float float11 = float8;
        float float12 = float9;
        final float float13 = (color >> 24 & 0xFF) / 255.0f;
        final Tessellator tessellator14 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder15 = tessellator14.getBufferBuilder();
        Identifier identifier16 = null;
        bufferBuilder15.begin(7, VertexFormats.POSITION_UV_COLOR);
        boolean boolean17 = false;
        boolean boolean18 = false;
        boolean boolean19 = false;
        boolean boolean20 = false;
        boolean boolean21 = false;
        final List<Rectangle> list22 = Lists.newArrayList();
        for (int integer23 = 0; integer23 < str.length(); ++integer23) {
            final char character24 = str.charAt(integer23);
            if (character24 == '§' && integer23 + 1 < str.length()) {
                final TextFormat textFormat25 = TextFormat.bySectionSignCode(str.charAt(integer23 + 1));
                if (textFormat25 != null) {
                    if (textFormat25.affectsGlyphWidth()) {
                        boolean17 = false;
                        boolean18 = false;
                        boolean21 = false;
                        boolean20 = false;
                        boolean19 = false;
                        float10 = float7;
                        float11 = float8;
                        float12 = float9;
                    }
                    if (textFormat25.getColor() != null) {
                        final int integer24 = textFormat25.getColor();
                        float10 = (integer24 >> 16 & 0xFF) / 255.0f * float6;
                        float11 = (integer24 >> 8 & 0xFF) / 255.0f * float6;
                        float12 = (integer24 & 0xFF) / 255.0f * float6;
                    }
                    else if (textFormat25 == TextFormat.q) {
                        boolean17 = true;
                    }
                    else if (textFormat25 == TextFormat.r) {
                        boolean18 = true;
                    }
                    else if (textFormat25 == TextFormat.s) {
                        boolean21 = true;
                    }
                    else if (textFormat25 == TextFormat.t) {
                        boolean20 = true;
                    }
                    else if (textFormat25 == TextFormat.u) {
                        boolean19 = true;
                    }
                }
                ++integer23;
            }
            else {
                final Glyph glyph25 = this.fontStorage.getGlyph(character24);
                final GlyphRenderer glyphRenderer26 = (boolean17 && character24 != ' ') ? this.fontStorage.getObfuscatedGlyphRenderer(glyph25) : this.fontStorage.getGlyphRenderer(character24);
                final Identifier identifier17 = glyphRenderer26.getId();
                if (identifier17 != null) {
                    if (identifier16 != identifier17) {
                        tessellator14.draw();
                        this.textureManager.bindTexture(identifier17);
                        bufferBuilder15.begin(7, VertexFormats.POSITION_UV_COLOR);
                        identifier16 = identifier17;
                    }
                    final float float14 = boolean18 ? glyph25.getBoldOffset() : 0.0f;
                    final float float15 = isShadow ? glyph25.getShadowOffset() : 0.0f;
                    this.drawGlyph(glyphRenderer26, boolean18, boolean19, float14, x + float15, y + float15, bufferBuilder15, float10, float11, float12, float13);
                }
                final float float14 = glyph25.getAdvance(boolean18);
                final float float15 = isShadow ? 1.0f : 0.0f;
                if (boolean21) {
                    list22.add(new Rectangle(x + float15 - 1.0f, y + float15 + 4.5f, x + float15 + float14, y + float15 + 4.5f - 1.0f, float10, float11, float12, float13));
                }
                if (boolean20) {
                    list22.add(new Rectangle(x + float15 - 1.0f, y + float15 + 9.0f, x + float15 + float14, y + float15 + 9.0f - 1.0f, float10, float11, float12, float13));
                }
                x += float14;
            }
        }
        tessellator14.draw();
        if (!list22.isEmpty()) {
            GlStateManager.disableTexture();
            bufferBuilder15.begin(7, VertexFormats.POSITION_COLOR);
            for (final Rectangle rectangle24 : list22) {
                rectangle24.draw(bufferBuilder15);
            }
            tessellator14.draw();
            GlStateManager.enableTexture();
        }
        return x;
    }
    
    private void drawGlyph(final GlyphRenderer glyphRenderer, final boolean bold, final boolean strikethrough, final float boldOffset, final float x, final float y, final BufferBuilder buffer, final float red, final float green, final float blue, final float alpha) {
        glyphRenderer.draw(this.textureManager, strikethrough, x, y, buffer, red, green, blue, alpha);
        if (bold) {
            glyphRenderer.draw(this.textureManager, strikethrough, x + boldOffset, y, buffer, red, green, blue, alpha);
        }
    }
    
    public int getStringWidth(final String string) {
        if (string == null) {
            return 0;
        }
        float float2 = 0.0f;
        boolean boolean3 = false;
        for (int integer4 = 0; integer4 < string.length(); ++integer4) {
            final char character5 = string.charAt(integer4);
            if (character5 == '§' && integer4 < string.length() - 1) {
                final TextFormat textFormat6 = TextFormat.bySectionSignCode(string.charAt(++integer4));
                if (textFormat6 == TextFormat.r) {
                    boolean3 = true;
                }
                else if (textFormat6 != null && textFormat6.affectsGlyphWidth()) {
                    boolean3 = false;
                }
            }
            else {
                float2 += this.fontStorage.getGlyph(character5).getAdvance(boolean3);
            }
        }
        return MathHelper.ceil(float2);
    }
    
    public float getCharWidth(final char character) {
        if (character == '§') {
            return 0.0f;
        }
        return this.fontStorage.getGlyph(character).getAdvance(false);
    }
    
    public String trimToWidth(final String string, final int width) {
        return this.trimToWidth(string, width, false);
    }
    
    public String trimToWidth(final String string, final int width, final boolean rightToLeft) {
        final StringBuilder stringBuilder4 = new StringBuilder();
        float float5 = 0.0f;
        final int integer6 = rightToLeft ? (string.length() - 1) : 0;
        final int integer7 = rightToLeft ? -1 : 1;
        boolean boolean8 = false;
        boolean boolean9 = false;
        for (int integer8 = integer6; integer8 >= 0 && integer8 < string.length() && float5 < width; integer8 += integer7) {
            final char character11 = string.charAt(integer8);
            if (boolean8) {
                boolean8 = false;
                final TextFormat textFormat12 = TextFormat.bySectionSignCode(character11);
                if (textFormat12 == TextFormat.r) {
                    boolean9 = true;
                }
                else if (textFormat12 != null && textFormat12.affectsGlyphWidth()) {
                    boolean9 = false;
                }
            }
            else if (character11 == '§') {
                boolean8 = true;
            }
            else {
                float5 += this.getCharWidth(character11);
                if (boolean9) {
                    ++float5;
                }
            }
            if (float5 > width) {
                break;
            }
            if (rightToLeft) {
                stringBuilder4.insert(0, character11);
            }
            else {
                stringBuilder4.append(character11);
            }
        }
        return stringBuilder4.toString();
    }
    
    private String trimEndNewlines(String string) {
        while (string != null && string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }
    
    public void drawStringBounded(String str, final int x, final int y, final int maxWidth, final int integer5) {
        str = this.trimEndNewlines(str);
        this.renderStringBounded(str, x, y, maxWidth, integer5);
    }
    
    private void renderStringBounded(final String str, final int x, int y, final int maxWidth, final int integer5) {
        final List<String> list6 = this.wrapStringToWidthAsList(str, maxWidth);
        for (final String string8 : list6) {
            float float9 = (float)x;
            if (this.rightToLeft) {
                final int integer6 = this.getStringWidth(this.mirror(string8));
                float9 += maxWidth - integer6;
            }
            this.draw(string8, float9, (float)y, integer5, false);
            y += 9;
        }
    }
    
    public int getStringBoundedHeight(final String str, final int integer) {
        return 9 * this.wrapStringToWidthAsList(str, integer).size();
    }
    
    public void setRightToLeft(final boolean boolean1) {
        this.rightToLeft = boolean1;
    }
    
    public List<String> wrapStringToWidthAsList(final String text, final int integer) {
        return Arrays.<String>asList(this.wrapStringToWidth(text, integer).split("\n"));
    }
    
    public String wrapStringToWidth(String text, final int integer) {
        String string3;
        int integer2;
        String string4;
        boolean boolean7;
        for (string3 = ""; !text.isEmpty(); text = TextFormat.getFormatAtEnd(string4) + text.substring(integer2 + (boolean7 ? 1 : 0)), string3 = string3 + string4 + "\n") {
            integer2 = this.getCharacterCountForWidth(text, integer);
            if (text.length() <= integer2) {
                return string3 + text;
            }
            string4 = text.substring(0, integer2);
            final char character6 = text.charAt(integer2);
            boolean7 = (character6 == ' ' || character6 == '\n');
        }
        return string3;
    }
    
    public int getCharacterCountForWidth(final String text, final int offset) {
        final int integer3 = Math.max(1, offset);
        final int integer4 = text.length();
        float float5 = 0.0f;
        int integer5 = 0;
        int integer6 = -1;
        boolean boolean8 = false;
        boolean boolean9 = true;
        while (integer5 < integer4) {
            final char character10 = text.charAt(integer5);
            Label_0178: {
                switch (character10) {
                    case '§': {
                        if (integer5 < integer4 - 1) {
                            final TextFormat textFormat11 = TextFormat.bySectionSignCode(text.charAt(++integer5));
                            if (textFormat11 == TextFormat.r) {
                                boolean8 = true;
                            }
                            else if (textFormat11 != null && textFormat11.affectsGlyphWidth()) {
                                boolean8 = false;
                            }
                        }
                        break Label_0178;
                    }
                    case '\n': {
                        --integer5;
                        break Label_0178;
                    }
                    case ' ': {
                        integer6 = integer5;
                        break;
                    }
                }
                if (float5 != 0.0f) {
                    boolean9 = false;
                }
                float5 += this.getCharWidth(character10);
                if (boolean8) {
                    ++float5;
                }
            }
            if (character10 == '\n') {
                integer6 = ++integer5;
                break;
            }
            if (float5 > integer3) {
                if (boolean9) {
                    ++integer5;
                    break;
                }
                break;
            }
            else {
                ++integer5;
            }
        }
        if (integer5 != integer4 && integer6 != -1 && integer6 < integer5) {
            return integer6;
        }
        return integer5;
    }
    
    public int findWordEdge(final String string, final int direction, final int position, final boolean skipWhitespaceToRightOfWord) {
        int integer5 = position;
        final boolean boolean6 = direction < 0;
        for (int integer6 = Math.abs(direction), integer7 = 0; integer7 < integer6; ++integer7) {
            if (boolean6) {
                while (skipWhitespaceToRightOfWord && integer5 > 0 && (string.charAt(integer5 - 1) == ' ' || string.charAt(integer5 - 1) == '\n')) {
                    --integer5;
                }
                while (integer5 > 0 && string.charAt(integer5 - 1) != ' ' && string.charAt(integer5 - 1) != '\n') {
                    --integer5;
                }
            }
            else {
                final int integer8 = string.length();
                final int integer9 = string.indexOf(32, integer5);
                final int integer10 = string.indexOf(10, integer5);
                if (integer9 == -1 && integer10 == -1) {
                    integer5 = -1;
                }
                else if (integer9 != -1 && integer10 != -1) {
                    integer5 = Math.min(integer9, integer10);
                }
                else if (integer9 != -1) {
                    integer5 = integer9;
                }
                else {
                    integer5 = integer10;
                }
                if (integer5 == -1) {
                    integer5 = integer8;
                }
                else {
                    while (skipWhitespaceToRightOfWord && integer5 < integer8 && (string.charAt(integer5) == ' ' || string.charAt(integer5) == '\n')) {
                        ++integer5;
                    }
                }
            }
        }
        return integer5;
    }
    
    public boolean isRightToLeft() {
        return this.rightToLeft;
    }
    
    @Environment(EnvType.CLIENT)
    static class Rectangle
    {
        protected final float xMin;
        protected final float yMin;
        protected final float xMax;
        protected final float yMax;
        protected final float red;
        protected final float green;
        protected final float blue;
        protected final float alpha;
        
        private Rectangle(final float xMin, final float yMin, final float xMax, final float yMax, final float red, final float green, final float blue, final float alpha) {
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
        
        public void draw(final BufferBuilder bufferBuilder) {
            bufferBuilder.vertex(this.xMin, this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
            bufferBuilder.vertex(this.xMax, this.yMin, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
            bufferBuilder.vertex(this.xMax, this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
            bufferBuilder.vertex(this.xMin, this.yMax, 0.0).color(this.red, this.green, this.blue, this.alpha).next();
        }
    }
}
