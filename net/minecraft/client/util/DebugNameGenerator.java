package net.minecraft.client.util;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DebugNameGenerator
{
    private static final String[] ADJECTIVES;
    private static final String[] NOUNS;
    
    public static String getDebugName(final UUID uUID) {
        final Random random2 = createRandomForUUID(uUID);
        return getRandom(random2, DebugNameGenerator.ADJECTIVES) + getRandom(random2, DebugNameGenerator.NOUNS);
    }
    
    private static String getRandom(final Random random, final String[] arr) {
        return arr[random.nextInt(arr.length)];
    }
    
    private static Random createRandomForUUID(final UUID uUID) {
        return new Random(uUID.hashCode() >> 2);
    }
    
    static {
        ADJECTIVES = new String[] { "Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook" };
        NOUNS = new String[] { "Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue" };
    }
}
