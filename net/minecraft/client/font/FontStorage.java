package net.minecraft.client.font;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.texture.Texture;
import java.util.Set;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.TextureManager;
import java.util.Random;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable
{
    private static final Logger LOGGER;
    private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER;
    private static final Glyph SPACE;
    private static final Random RANDOM;
    private final TextureManager textureManager;
    private final Identifier id;
    private GlyphRenderer blankGlyphRenderer;
    private final List<Font> fonts;
    private final Char2ObjectMap<GlyphRenderer> glyphRendererCache;
    private final Char2ObjectMap<Glyph> glyphCache;
    private final Int2ObjectMap<CharList> charactersByWidth;
    private final List<GlyphAtlasTexture> glyphAtlases;
    
    public FontStorage(final TextureManager textureManager, final Identifier id) {
        this.fonts = Lists.newArrayList();
        this.glyphRendererCache = (Char2ObjectMap<GlyphRenderer>)new Char2ObjectOpenHashMap();
        this.glyphCache = (Char2ObjectMap<Glyph>)new Char2ObjectOpenHashMap();
        this.charactersByWidth = (Int2ObjectMap<CharList>)new Int2ObjectOpenHashMap();
        this.glyphAtlases = Lists.newArrayList();
        this.textureManager = textureManager;
        this.id = id;
    }
    
    public void setFonts(final List<Font> fonts) {
        for (final Font font3 : this.fonts) {
            font3.close();
        }
        this.fonts.clear();
        this.closeGlyphAtlases();
        this.glyphAtlases.clear();
        this.glyphRendererCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankGlyphRenderer = this.getGlyphRenderer(BlankGlyph.INSTANCE);
        final Set<Font> set2 = Sets.newHashSet();
        for (char character3 = '\0'; character3 < '\uffff'; ++character3) {
            for (final Font font4 : fonts) {
                final Glyph glyph6 = (character3 == ' ') ? FontStorage.SPACE : font4.getGlyph(character3);
                if (glyph6 != null) {
                    set2.add(font4);
                    if (glyph6 != BlankGlyph.INSTANCE) {
                        ((CharList)this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph6.getAdvance(false)), integer -> new CharArrayList())).add(character3);
                        break;
                    }
                    break;
                }
            }
        }
        fonts.stream().filter(set2::contains).forEach(this.fonts::add);
    }
    
    @Override
    public void close() {
        this.closeGlyphAtlases();
    }
    
    public void closeGlyphAtlases() {
        for (final GlyphAtlasTexture glyphAtlasTexture2 : this.glyphAtlases) {
            glyphAtlasTexture2.close();
        }
    }
    
    public Glyph getGlyph(final char c) {
        return (Glyph)this.glyphCache.computeIfAbsent(c, integer -> (integer == 32) ? FontStorage.SPACE : this.getRenderableGlyph((char)integer));
    }
    
    private RenderableGlyph getRenderableGlyph(final char c) {
        for (final Font font3 : this.fonts) {
            final RenderableGlyph renderableGlyph4 = font3.getGlyph(c);
            if (renderableGlyph4 != null) {
                return renderableGlyph4;
            }
        }
        return BlankGlyph.INSTANCE;
    }
    
    public GlyphRenderer getGlyphRenderer(final char c) {
        return (GlyphRenderer)this.glyphRendererCache.computeIfAbsent(c, integer -> (integer == 32) ? FontStorage.EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph((char)integer)));
    }
    
    private GlyphRenderer getGlyphRenderer(final RenderableGlyph c) {
        for (final GlyphAtlasTexture glyphAtlasTexture3 : this.glyphAtlases) {
            final GlyphRenderer glyphRenderer4 = glyphAtlasTexture3.getGlyphRenderer(c);
            if (glyphRenderer4 != null) {
                return glyphRenderer4;
            }
        }
        final GlyphAtlasTexture glyphAtlasTexture4 = new GlyphAtlasTexture(new Identifier(this.id.getNamespace(), this.id.getPath() + "/" + this.glyphAtlases.size()), c.hasColor());
        this.glyphAtlases.add(glyphAtlasTexture4);
        this.textureManager.registerTexture(glyphAtlasTexture4.getId(), glyphAtlasTexture4);
        final GlyphRenderer glyphRenderer5 = glyphAtlasTexture4.getGlyphRenderer(c);
        return (glyphRenderer5 == null) ? this.blankGlyphRenderer : glyphRenderer5;
    }
    
    public GlyphRenderer getObfuscatedGlyphRenderer(final Glyph glyph) {
        final CharList charList2 = (CharList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
        if (charList2 != null && !charList2.isEmpty()) {
            return this.getGlyphRenderer(charList2.get(FontStorage.RANDOM.nextInt(charList2.size())));
        }
        return this.blankGlyphRenderer;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
        SPACE = (() -> 4.0f);
        RANDOM = new Random();
    }
}
