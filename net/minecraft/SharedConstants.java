package net.minecraft;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.command.TextComponentBuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.bridge.game.GameVersion;
import io.netty.util.ResourceLeakDetector;

public class SharedConstants
{
    public static final ResourceLeakDetector.Level RESOURCE_LEAK_DETECTOR_DISABLED;
    public static boolean isDevelopment;
    public static final char[] INVALID_CHARS_LEVEL_NAME;
    private static GameVersion gameVersion;
    
    public static boolean isValidChar(final char chr) {
        return chr != '§' && chr >= ' ' && chr != '\u007f';
    }
    
    public static String stripInvalidChars(final String s) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (final char character6 : s.toCharArray()) {
            if (isValidChar(character6)) {
                stringBuilder2.append(character6);
            }
        }
        return stringBuilder2.toString();
    }
    
    @Environment(EnvType.CLIENT)
    public static String stripSupplementaryChars(final String s) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (int integer3 = 0; integer3 < s.length(); integer3 = s.offsetByCodePoints(integer3, 1)) {
            final int integer4 = s.codePointAt(integer3);
            if (!Character.isSupplementaryCodePoint(integer4)) {
                stringBuilder2.appendCodePoint(integer4);
            }
            else {
                stringBuilder2.append('\ufffd');
            }
        }
        return stringBuilder2.toString();
    }
    
    public static GameVersion getGameVersion() {
        if (SharedConstants.gameVersion == null) {
            SharedConstants.gameVersion = MinecraftVersion.create();
        }
        return SharedConstants.gameVersion;
    }
    
    static {
        RESOURCE_LEAK_DETECTOR_DISABLED = ResourceLeakDetector.Level.DISABLED;
        INVALID_CHARS_LEVEL_NAME = new char[] { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
        ResourceLeakDetector.setLevel(SharedConstants.RESOURCE_LEAK_DETECTOR_DISABLED);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = (BuiltInExceptionProvider)new TextComponentBuiltInExceptionProvider();
        NamespacedStringType.ENSURE_NAMESPACE = SchemaIdentifierNormalize::normalize;
    }
}
