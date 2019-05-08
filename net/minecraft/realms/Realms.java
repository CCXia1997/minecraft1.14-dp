package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.ChatMessageType;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileInputStream;
import java.io.File;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.world.GameMode;
import net.minecraft.client.gui.Screen;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.util.Session;
import java.net.Proxy;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Realms
{
    private static final RepeatedNarrator REPEATED_NARRATOR;
    
    public static boolean isTouchScreen() {
        return MinecraftClient.getInstance().options.touchscreen;
    }
    
    public static Proxy getProxy() {
        return MinecraftClient.getInstance().getNetworkProxy();
    }
    
    public static String sessionId() {
        final Session session1 = MinecraftClient.getInstance().getSession();
        if (session1 == null) {
            return null;
        }
        return session1.getSessionId();
    }
    
    public static String userName() {
        final Session session1 = MinecraftClient.getInstance().getSession();
        if (session1 == null) {
            return null;
        }
        return session1.getUsername();
    }
    
    public static long currentTimeMillis() {
        return SystemUtil.getMeasuringTimeMs();
    }
    
    public static String getSessionId() {
        return MinecraftClient.getInstance().getSession().getSessionId();
    }
    
    public static String getUUID() {
        return MinecraftClient.getInstance().getSession().getUuid();
    }
    
    public static String getName() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }
    
    public static String uuidToName(final String string) {
        return MinecraftClient.getInstance().getSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), (String)null), false).getName();
    }
    
    public static <V> CompletableFuture<V> execute(final Supplier<V> supplier) {
        return MinecraftClient.getInstance().<V>executeFuture(supplier);
    }
    
    public static void execute(final Runnable runnable) {
        MinecraftClient.getInstance().execute(runnable);
    }
    
    public static void setScreen(final RealmsScreen realmsScreen) {
        Realms.execute(() -> {
            setScreenDirect(realmsScreen);
            return null;
        });
    }
    
    public static void setScreenDirect(final RealmsScreen realmsScreen) {
        MinecraftClient.getInstance().openScreen(realmsScreen.getProxy());
    }
    
    public static String getGameDirectoryPath() {
        return MinecraftClient.getInstance().runDirectory.getAbsolutePath();
    }
    
    public static int survivalId() {
        return GameMode.b.getId();
    }
    
    public static int creativeId() {
        return GameMode.c.getId();
    }
    
    public static int adventureId() {
        return GameMode.d.getId();
    }
    
    public static int spectatorId() {
        return GameMode.e.getId();
    }
    
    public static void setConnectedToRealms(final boolean boolean1) {
        MinecraftClient.getInstance().setConnectedToRealms(boolean1);
    }
    
    public static CompletableFuture<?> downloadResourcePack(final String string1, final String string2) {
        return MinecraftClient.getInstance().getResourcePackDownloader().download(string1, string2);
    }
    
    public static void clearResourcePack() {
        MinecraftClient.getInstance().getResourcePackDownloader().clear();
    }
    
    public static boolean getRealmsNotificationsEnabled() {
        return MinecraftClient.getInstance().options.realmsNotifications;
    }
    
    public static boolean inTitleScreen() {
        return MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().currentScreen instanceof MainMenuScreen;
    }
    
    public static void deletePlayerTag(final File file) {
        if (file.exists()) {
            try {
                final CompoundTag compoundTag2 = NbtIo.readCompressed(new FileInputStream(file));
                final CompoundTag compoundTag3 = compoundTag2.getCompound("Data");
                compoundTag3.remove("Player");
                NbtIo.writeCompressed(compoundTag2, new FileOutputStream(file));
            }
            catch (Exception exception2) {
                exception2.printStackTrace();
            }
        }
    }
    
    public static void openUri(final String string) {
        SystemUtil.getOperatingSystem().open(string);
    }
    
    public static void setClipboard(final String string) {
        MinecraftClient.getInstance().keyboard.setClipboard(string);
    }
    
    public static String getMinecraftVersionString() {
        return SharedConstants.getGameVersion().getName();
    }
    
    public static Identifier resourceLocation(final String string) {
        return new Identifier(string);
    }
    
    public static String getLocalizedString(final String string, final Object... arr) {
        return I18n.translate(string, arr);
    }
    
    public static void bind(final String string) {
        final Identifier identifier2 = new Identifier(string);
        MinecraftClient.getInstance().getTextureManager().bindTexture(identifier2);
    }
    
    public static void narrateNow(final String string) {
        final NarratorManager narratorManager2 = NarratorManager.INSTANCE;
        narratorManager2.clear();
        narratorManager2.onChatMessage(ChatMessageType.b, new StringTextComponent(fixNarrationNewlines(string)));
    }
    
    private static String fixNarrationNewlines(final String string) {
        return string.replace("\\n", System.lineSeparator());
    }
    
    public static void narrateNow(final String... arr) {
        narrateNow(Arrays.<String>asList(arr));
    }
    
    public static void narrateNow(final Iterable<String> iterable) {
        narrateNow(joinNarrations(iterable));
    }
    
    public static String joinNarrations(final Iterable<String> iterable) {
        return String.join(System.lineSeparator(), iterable);
    }
    
    public static void narrateRepeatedly(final String string) {
        Realms.REPEATED_NARRATOR.narrate(fixNarrationNewlines(string));
    }
    
    static {
        REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
    }
}
