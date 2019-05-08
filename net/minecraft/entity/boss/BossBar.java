package net.minecraft.entity.boss;

import net.minecraft.text.TextFormat;
import net.minecraft.text.TextComponent;
import java.util.UUID;

public abstract class BossBar
{
    private final UUID uuid;
    protected TextComponent name;
    protected float percent;
    protected Color color;
    protected Style style;
    protected boolean darkenSky;
    protected boolean dragonMusic;
    protected boolean thickenFog;
    
    public BossBar(final UUID uuid, final TextComponent name, final Color color, final Style style) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.style = style;
        this.percent = 1.0f;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public TextComponent getName() {
        return this.name;
    }
    
    public void setName(final TextComponent name) {
        this.name = name;
    }
    
    public float getPercent() {
        return this.percent;
    }
    
    public void setPercent(final float percentage) {
        this.percent = percentage;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public Style getOverlay() {
        return this.style;
    }
    
    public void setOverlay(final Style style) {
        this.style = style;
    }
    
    public boolean getDarkenSky() {
        return this.darkenSky;
    }
    
    public BossBar setDarkenSky(final boolean darkenSky) {
        this.darkenSky = darkenSky;
        return this;
    }
    
    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }
    
    public BossBar setDragonMusic(final boolean dragonMusic) {
        this.dragonMusic = dragonMusic;
        return this;
    }
    
    public BossBar setThickenFog(final boolean thickenFog) {
        this.thickenFog = thickenFog;
        return this;
    }
    
    public boolean getThickenFog() {
        return this.thickenFog;
    }
    
    public enum Color
    {
        a("pink", TextFormat.m), 
        b("blue", TextFormat.j), 
        c("red", TextFormat.e), 
        d("green", TextFormat.k), 
        e("yellow", TextFormat.o), 
        f("purple", TextFormat.b), 
        g("white", TextFormat.p);
        
        private final String name;
        private final TextFormat format;
        
        private Color(final String name, final TextFormat textFormat) {
            this.name = name;
            this.format = textFormat;
        }
        
        public TextFormat getTextFormat() {
            return this.format;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Color byName(final String name) {
            for (final Color color5 : values()) {
                if (color5.name.equals(name)) {
                    return color5;
                }
            }
            return Color.g;
        }
    }
    
    public enum Style
    {
        a("progress"), 
        b("notched_6"), 
        c("notched_10"), 
        d("notched_12"), 
        e("notched_20");
        
        private final String name;
        
        private Style(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Style byName(final String name) {
            for (final Style style5 : values()) {
                if (style5.name.equals(name)) {
                    return style5;
                }
            }
            return Style.a;
        }
    }
}
