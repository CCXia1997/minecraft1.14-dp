package net.minecraft.realms;

import org.apache.logging.log4j.LogManager;
import net.minecraft.text.TextComponent;
import net.minecraft.client.gui.menu.NoticeScreen;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.SharedConstants;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.MinecraftClient;
import com.mojang.datafixers.util.Either;
import java.lang.reflect.Constructor;
import net.minecraft.realms.pluginapi.RealmsPlugin;
import javax.annotation.Nullable;
import java.util.Optional;
import net.minecraft.realms.pluginapi.LoadedRealmsPlugin;
import net.minecraft.client.gui.Screen;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsBridge extends RealmsScreen
{
    private static final Logger LOGGER;
    private Screen previousScreen;
    
    public void switchToRealms(final Screen screen) {
        this.previousScreen = screen;
        final Optional<LoadedRealmsPlugin> optional2 = this.tryLoadRealms();
        if (optional2.isPresent()) {
            Realms.setScreen(optional2.get().getMainScreen(this));
        }
        else {
            this.showMissingRealmsErrorScreen();
        }
    }
    
    @Nullable
    public RealmsScreenProxy getNotificationScreen(final Screen screen) {
        this.previousScreen = screen;
        return this.tryLoadRealms().<RealmsScreenProxy>map(loadedRealmsPlugin -> loadedRealmsPlugin.getNotificationsScreen(this).getProxy()).orElse(null);
    }
    
    private Optional<LoadedRealmsPlugin> tryLoadRealms() {
        try {
            final Class<?> class1 = Class.forName("com.mojang.realmsclient.plugin.RealmsPluginImpl");
            final Constructor<?> constructor2 = class1.getDeclaredConstructor(new Class[0]);
            constructor2.setAccessible(true);
            final Object object3 = constructor2.newInstance(new Object[0]);
            final RealmsPlugin realmsPlugin4 = (RealmsPlugin)object3;
            final Either<LoadedRealmsPlugin, String> either5 = realmsPlugin4.tryLoad(Realms.getMinecraftVersionString());
            final Optional<String> optional6 = (Optional<String>)either5.right();
            if (optional6.isPresent()) {
                RealmsBridge.LOGGER.error("Failed to load Realms module: {}", optional6.get());
                return Optional.<LoadedRealmsPlugin>empty();
            }
            return (Optional<LoadedRealmsPlugin>)either5.left();
        }
        catch (ClassNotFoundException classNotFoundException1) {
            RealmsBridge.LOGGER.error("Realms module missing");
        }
        catch (Exception exception1) {
            RealmsBridge.LOGGER.error("Failed to load Realms module", (Throwable)exception1);
        }
        return Optional.<LoadedRealmsPlugin>empty();
    }
    
    @Override
    public void init() {
        MinecraftClient.getInstance().openScreen(this.previousScreen);
    }
    
    private void showMissingRealmsErrorScreen() {
        MinecraftClient.getInstance().openScreen(new NoticeScreen(() -> MinecraftClient.getInstance().openScreen(this.previousScreen), new StringTextComponent(""), new TranslatableTextComponent(SharedConstants.getGameVersion().isStable() ? "realms.missing.module.error.text" : "realms.missing.snapshot.error.text", new Object[0])));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
