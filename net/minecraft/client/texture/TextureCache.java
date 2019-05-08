package net.minecraft.client.texture;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.MinecraftClient;
import com.google.common.collect.Lists;
import net.minecraft.util.DyeColor;
import net.minecraft.block.entity.BannerPattern;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureCache
{
    public static final Manager BANNER;
    public static final Manager SHIELD;
    public static final Identifier DEFAULT_SHIELD;
    public static final Identifier DEFAULT_BANNER;
    
    static {
        BANNER = new Manager("banner_", new Identifier("textures/entity/banner_base.png"), "textures/entity/banner/");
        SHIELD = new Manager("shield_", new Identifier("textures/entity/shield_base.png"), "textures/entity/shield/");
        DEFAULT_SHIELD = new Identifier("textures/entity/shield_base_nopattern.png");
        DEFAULT_BANNER = new Identifier("textures/entity/banner/base.png");
    }
    
    @Environment(EnvType.CLIENT)
    public static class Manager
    {
        private final Map<String, Entry> cacheMap;
        private final Identifier filename;
        private final String baseDir;
        private final String id;
        
        public Manager(final String string1, final Identifier identifier, final String string3) {
            this.cacheMap = Maps.newLinkedHashMap();
            this.id = string1;
            this.filename = identifier;
            this.baseDir = string3;
        }
        
        @Nullable
        public Identifier get(String cacheKey, final List<BannerPattern> patterns, final List<DyeColor> colors) {
            if (cacheKey.isEmpty()) {
                return null;
            }
            if (patterns.isEmpty() || colors.isEmpty()) {
                return MissingSprite.getMissingSpriteId();
            }
            cacheKey = this.id + cacheKey;
            Entry entry4 = this.cacheMap.get(cacheKey);
            if (entry4 == null) {
                if (this.cacheMap.size() >= 256 && !this.removeOldEntries()) {
                    return TextureCache.DEFAULT_BANNER;
                }
                final List<String> list5 = Lists.newArrayList();
                for (final BannerPattern bannerPattern7 : patterns) {
                    list5.add(this.baseDir + bannerPattern7.getName() + ".png");
                }
                entry4 = new Entry();
                entry4.filename = new Identifier(cacheKey);
                MinecraftClient.getInstance().getTextureManager().registerTexture(entry4.filename, new BannerTexture(this.filename, list5, colors));
                this.cacheMap.put(cacheKey, entry4);
            }
            entry4.lastRequestTimeMillis = SystemUtil.getMeasuringTimeMs();
            return entry4.filename;
        }
        
        private boolean removeOldEntries() {
            final long long1 = SystemUtil.getMeasuringTimeMs();
            final Iterator<String> iterator3 = this.cacheMap.keySet().iterator();
            while (iterator3.hasNext()) {
                final String string4 = iterator3.next();
                final Entry entry5 = this.cacheMap.get(string4);
                if (long1 - entry5.lastRequestTimeMillis > 5000L) {
                    MinecraftClient.getInstance().getTextureManager().destroyTexture(entry5.filename);
                    iterator3.remove();
                    return true;
                }
            }
            return this.cacheMap.size() < 256;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class Entry
    {
        public long lastRequestTimeMillis;
        public Identifier filename;
        
        private Entry() {
        }
    }
}
