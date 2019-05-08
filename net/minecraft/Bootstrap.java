package net.minecraft;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.PrintStreamLogger;
import java.io.OutputStream;
import net.minecraft.util.DebugPrintStreamLogger;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.entity.effect.StatusEffect;
import java.util.TreeSet;
import net.minecraft.util.Language;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
import java.io.PrintStream;

public class Bootstrap
{
    public static final PrintStream SYSOUT;
    private static boolean initialized;
    private static final Logger LOGGER;
    
    public static void initialize() {
        if (Bootstrap.initialized) {
            return;
        }
        Bootstrap.initialized = true;
        if (Registry.REGISTRIES.isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
        }
        FireBlock.registerDefaultFlammables();
        ComposterBlock.registerDefaultCompostableItems();
        if (EntityType.getId(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
        }
        BrewingRecipeRegistry.registerDefaults();
        EntitySelectorOptions.register();
        DispenserBehavior.registerDefaults();
        ArgumentTypes.register();
        setOutputStreams();
    }
    
    private static <T> void collectMissingTranslations(final Registry<T> registry, final Function<T, String> keyExtractor, final Set<String> translationKeys) {
        final Language language4 = Language.getInstance();
        final String string5;
        final Language language5;
        registry.iterator().forEachRemaining(object -> {
            string5 = keyExtractor.apply(object);
            if (!language5.hasTranslation(string5)) {
                translationKeys.add(string5);
            }
        });
    }
    
    public static Set<String> getMissingTranslations() {
        final Set<String> set1 = new TreeSet<String>();
        Bootstrap.<EntityType<?>>collectMissingTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set1);
        Bootstrap.<StatusEffect>collectMissingTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set1);
        Bootstrap.<Item>collectMissingTranslations(Registry.ITEM, Item::getTranslationKey, set1);
        Bootstrap.<Enchantment>collectMissingTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set1);
        Bootstrap.<Biome>collectMissingTranslations(Registry.BIOME, Biome::getTranslationKey, set1);
        Bootstrap.<Block>collectMissingTranslations(Registry.BLOCK, Block::getTranslationKey, set1);
        Bootstrap.<Identifier>collectMissingTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set1);
        return set1;
    }
    
    public static void logMissingTranslations() {
        if (!Bootstrap.initialized) {
            throw new IllegalArgumentException("Not bootstrapped");
        }
        if (SharedConstants.isDevelopment) {
            return;
        }
        getMissingTranslations().forEach(string -> Bootstrap.LOGGER.error("Missing translations: " + string));
    }
    
    private static void setOutputStreams() {
        if (Bootstrap.LOGGER.isDebugEnabled()) {
            System.setErr(new DebugPrintStreamLogger("STDERR", System.err));
            System.setOut(new DebugPrintStreamLogger("STDOUT", Bootstrap.SYSOUT));
        }
        else {
            System.setErr(new PrintStreamLogger("STDERR", System.err));
            System.setOut(new PrintStreamLogger("STDOUT", Bootstrap.SYSOUT));
        }
    }
    
    public static void println(final String str) {
        Bootstrap.SYSOUT.println(str);
    }
    
    static {
        SYSOUT = System.out;
        LOGGER = LogManager.getLogger();
    }
}
