package net.minecraft.client.resource.language;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.Sets;
import java.util.SortedSet;
import net.minecraft.util.Language;
import com.google.common.collect.Lists;
import net.minecraft.resource.ResourceManager;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourcePack;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class LanguageManager implements SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    protected static final TranslationStorage STORAGE;
    private String currentLanguageCode;
    private final Map<String, LanguageDefinition> languageDefs;
    
    public LanguageManager(final String string) {
        this.languageDefs = Maps.newHashMap();
        this.currentLanguageCode = string;
        I18n.setLanguage(LanguageManager.STORAGE);
    }
    
    public void reloadResources(final List<ResourcePack> list) {
        this.languageDefs.clear();
        for (final ResourcePack resourcePack3 : list) {
            try {
                final LanguageResourceMetadata languageResourceMetadata4 = resourcePack3.<LanguageResourceMetadata>parseMetadata((ResourceMetadataReader<LanguageResourceMetadata>)LanguageResourceMetadata.READER);
                if (languageResourceMetadata4 == null) {
                    continue;
                }
                for (final LanguageDefinition languageDefinition6 : languageResourceMetadata4.getLanguageDefinitions()) {
                    if (!this.languageDefs.containsKey(languageDefinition6.getCode())) {
                        this.languageDefs.put(languageDefinition6.getCode(), languageDefinition6);
                    }
                }
            }
            catch (RuntimeException | IOException ex2) {
                final Exception ex;
                final Exception exception4 = ex;
                LanguageManager.LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", resourcePack3.getName(), exception4);
            }
        }
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        final List<String> list2 = Lists.<String>newArrayList("en_us");
        if (!"en_us".equals(this.currentLanguageCode)) {
            list2.add(this.currentLanguageCode);
        }
        LanguageManager.STORAGE.load(manager, list2);
        Language.load(LanguageManager.STORAGE.translations);
    }
    
    public boolean isRightToLeft() {
        return this.getLanguage() != null && this.getLanguage().isRightToLeft();
    }
    
    public void setLanguage(final LanguageDefinition languageDefinition) {
        this.currentLanguageCode = languageDefinition.getCode();
    }
    
    public LanguageDefinition getLanguage() {
        final String string1 = this.languageDefs.containsKey(this.currentLanguageCode) ? this.currentLanguageCode : "en_us";
        return this.languageDefs.get(string1);
    }
    
    public SortedSet<LanguageDefinition> getAllLanguages() {
        return Sets.newTreeSet(this.languageDefs.values());
    }
    
    public LanguageDefinition getLanguage(final String code) {
        return this.languageDefs.get(code);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        STORAGE = new TranslationStorage();
    }
}
