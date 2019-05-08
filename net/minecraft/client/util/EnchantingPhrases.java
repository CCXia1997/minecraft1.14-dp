package net.minecraft.client.util;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.client.font.TextRenderer;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnchantingPhrases
{
    private static final EnchantingPhrases INSTANCE;
    private final Random random;
    private final String[] phrases;
    
    private EnchantingPhrases() {
        this.random = new Random();
        this.phrases = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette".split(" ");
    }
    
    public static EnchantingPhrases getInstance() {
        return EnchantingPhrases.INSTANCE;
    }
    
    public String generatePhrase(final TextRenderer fontRenderer, final int integer) {
        final int integer2 = this.random.nextInt(2) + 3;
        String string4 = "";
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            if (integer3 > 0) {
                string4 += " ";
            }
            string4 += this.phrases[this.random.nextInt(this.phrases.length)];
        }
        final List<String> list5 = fontRenderer.wrapStringToWidthAsList(string4, integer);
        return StringUtils.join((Iterable)((list5.size() >= 2) ? list5.subList(0, 2) : list5), " ");
    }
    
    public void setSeed(final long long1) {
        this.random.setSeed(long1);
    }
    
    static {
        INSTANCE = new EnchantingPhrases();
    }
}
