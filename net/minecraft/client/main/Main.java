package net.minecraft.client.main;

import org.apache.logging.log4j.LogManager;
import joptsimple.ArgumentAcceptingOptionSpec;
import com.google.gson.Gson;
import java.util.List;
import joptsimple.OptionSet;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;
import java.lang.reflect.Type;
import com.mojang.authlib.properties.PropertyMap;
import com.google.gson.GsonBuilder;
import java.util.Optional;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import joptsimple.OptionSpec;
import net.minecraft.util.SystemUtil;
import java.io.File;
import joptsimple.OptionParser;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Main
{
    private static final Logger LOGGER;
    
    public static void main(final String[] args) {
        final OptionParser optionParser2 = new OptionParser();
        optionParser2.allowsUnrecognizedOptions();
        optionParser2.accepts("demo");
        optionParser2.accepts("fullscreen");
        optionParser2.accepts("checkGlErrors");
        final OptionSpec<String> optionSpec3 = (OptionSpec<String>)optionParser2.accepts("server").withRequiredArg();
        final OptionSpec<Integer> optionSpec4 = (OptionSpec<Integer>)optionParser2.accepts("port").withRequiredArg().ofType((Class)Integer.class).defaultsTo(25565, (Object[])new Integer[0]);
        final OptionSpec<File> optionSpec5 = (OptionSpec<File>)optionParser2.accepts("gameDir").withRequiredArg().ofType((Class)File.class).defaultsTo(new File("."), (Object[])new File[0]);
        final OptionSpec<File> optionSpec6 = (OptionSpec<File>)optionParser2.accepts("assetsDir").withRequiredArg().ofType((Class)File.class);
        final OptionSpec<File> optionSpec7 = (OptionSpec<File>)optionParser2.accepts("resourcePackDir").withRequiredArg().ofType((Class)File.class);
        final OptionSpec<String> optionSpec8 = (OptionSpec<String>)optionParser2.accepts("proxyHost").withRequiredArg();
        final OptionSpec<Integer> optionSpec9 = (OptionSpec<Integer>)optionParser2.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (Object[])new String[0]).ofType((Class)Integer.class);
        final OptionSpec<String> optionSpec10 = (OptionSpec<String>)optionParser2.accepts("proxyUser").withRequiredArg();
        final OptionSpec<String> optionSpec11 = (OptionSpec<String>)optionParser2.accepts("proxyPass").withRequiredArg();
        final OptionSpec<String> optionSpec12 = (OptionSpec<String>)optionParser2.accepts("username").withRequiredArg().defaultsTo(("Player" + SystemUtil.getMeasuringTimeMs() % 1000L), (Object[])new String[0]);
        final OptionSpec<String> optionSpec13 = (OptionSpec<String>)optionParser2.accepts("uuid").withRequiredArg();
        final OptionSpec<String> optionSpec14 = (OptionSpec<String>)optionParser2.accepts("accessToken").withRequiredArg().required();
        final OptionSpec<String> optionSpec15 = (OptionSpec<String>)optionParser2.accepts("version").withRequiredArg().required();
        final OptionSpec<Integer> optionSpec16 = (OptionSpec<Integer>)optionParser2.accepts("width").withRequiredArg().ofType((Class)Integer.class).defaultsTo(854, (Object[])new Integer[0]);
        final OptionSpec<Integer> optionSpec17 = (OptionSpec<Integer>)optionParser2.accepts("height").withRequiredArg().ofType((Class)Integer.class).defaultsTo(480, (Object[])new Integer[0]);
        final OptionSpec<Integer> optionSpec18 = (OptionSpec<Integer>)optionParser2.accepts("fullscreenWidth").withRequiredArg().ofType((Class)Integer.class);
        final OptionSpec<Integer> optionSpec19 = (OptionSpec<Integer>)optionParser2.accepts("fullscreenHeight").withRequiredArg().ofType((Class)Integer.class);
        final OptionSpec<String> optionSpec20 = (OptionSpec<String>)optionParser2.accepts("userProperties").withRequiredArg().defaultsTo("{}", (Object[])new String[0]);
        final OptionSpec<String> optionSpec21 = (OptionSpec<String>)optionParser2.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (Object[])new String[0]);
        final OptionSpec<String> optionSpec22 = (OptionSpec<String>)optionParser2.accepts("assetIndex").withRequiredArg();
        final OptionSpec<String> optionSpec23 = (OptionSpec<String>)optionParser2.accepts("userType").withRequiredArg().defaultsTo("legacy", (Object[])new String[0]);
        final OptionSpec<String> optionSpec24 = (OptionSpec<String>)optionParser2.accepts("versionType").withRequiredArg().defaultsTo("release", (Object[])new String[0]);
        final OptionSpec<String> optionSpec25 = (OptionSpec<String>)optionParser2.nonOptions();
        final OptionSet optionSet26 = optionParser2.parse(args);
        final List<String> list27 = (List<String>)optionSet26.valuesOf((OptionSpec)optionSpec25);
        if (!list27.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list27);
        }
        final String string28 = Main.<String>getOption(optionSet26, optionSpec8);
        Proxy proxy29 = Proxy.NO_PROXY;
        if (string28 != null) {
            try {
                proxy29 = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string28, Main.<Integer>getOption(optionSet26, optionSpec9)));
            }
            catch (Exception ex) {}
        }
        final String string29 = Main.<String>getOption(optionSet26, optionSpec10);
        final String string30 = Main.<String>getOption(optionSet26, optionSpec11);
        if (!proxy29.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string29) && isNotNullOrEmpty(string30)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(string29, string30.toCharArray());
                }
            });
        }
        final int integer32 = Main.<Integer>getOption(optionSet26, optionSpec16);
        final int integer33 = Main.<Integer>getOption(optionSet26, optionSpec17);
        final Optional<Integer> optional34 = Optional.<Integer>ofNullable((Integer)Main.<T>getOption(optionSet26, (joptsimple.OptionSpec<T>)optionSpec18));
        final Optional<Integer> optional35 = Optional.<Integer>ofNullable((Integer)Main.<T>getOption(optionSet26, (joptsimple.OptionSpec<T>)optionSpec19));
        final boolean boolean36 = optionSet26.has("fullscreen");
        final boolean boolean37 = optionSet26.has("demo");
        final String string31 = Main.<String>getOption(optionSet26, optionSpec15);
        final Gson gson39 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        final PropertyMap propertyMap40 = JsonHelper.<PropertyMap>deserialize(gson39, Main.<String>getOption(optionSet26, optionSpec20), PropertyMap.class);
        final PropertyMap propertyMap41 = JsonHelper.<PropertyMap>deserialize(gson39, Main.<String>getOption(optionSet26, optionSpec21), PropertyMap.class);
        final String string32 = Main.<String>getOption(optionSet26, optionSpec24);
        final File file43 = Main.<File>getOption(optionSet26, optionSpec5);
        final File file44 = optionSet26.has((OptionSpec)optionSpec6) ? Main.<File>getOption(optionSet26, optionSpec6) : new File(file43, "assets/");
        final File file45 = optionSet26.has((OptionSpec)optionSpec7) ? Main.<File>getOption(optionSet26, optionSpec7) : new File(file43, "resourcepacks/");
        final String string33 = (String)(optionSet26.has((OptionSpec)optionSpec13) ? optionSpec13.value(optionSet26) : PlayerEntity.getOfflinePlayerUuid((String)optionSpec12.value(optionSet26)).toString());
        final String string34 = optionSet26.has((OptionSpec)optionSpec22) ? ((String)optionSpec22.value(optionSet26)) : null;
        final String string35 = Main.<String>getOption(optionSet26, optionSpec3);
        final Integer integer34 = Main.<Integer>getOption(optionSet26, optionSpec4);
        final Session session50 = new Session((String)optionSpec12.value(optionSet26), string33, (String)optionSpec14.value(optionSet26), (String)optionSpec23.value(optionSet26));
        final RunArgs runArgs51 = new RunArgs(new RunArgs.Network(session50, propertyMap40, propertyMap41, proxy29), new WindowSettings(integer32, integer33, optional34, optional35, boolean36), new RunArgs.Directories(file43, file45, file44, string34), new RunArgs.Game(boolean37, string31, string32), new RunArgs.AutoConnect(string35, integer34));
        final Thread thread52 = new Thread("Client Shutdown Thread") {
            @Override
            public void run() {
                final MinecraftClient minecraftClient1 = MinecraftClient.getInstance();
                if (minecraftClient1 == null) {
                    return;
                }
                final IntegratedServer integratedServer2 = minecraftClient1.getServer();
                if (integratedServer2 != null) {
                    integratedServer2.stop(true);
                }
            }
        };
        thread52.setUncaughtExceptionHandler(new UncaughtExceptionLogger(Main.LOGGER));
        Runtime.getRuntime().addShutdownHook(thread52);
        Thread.currentThread().setName("Client thread");
        new MinecraftClient(runArgs51).start();
    }
    
    private static <T> T getOption(final OptionSet optionSet, final OptionSpec<T> optionSpec) {
        try {
            return (T)optionSet.valueOf((OptionSpec)optionSpec);
        }
        catch (Throwable throwable3) {
            if (optionSpec instanceof ArgumentAcceptingOptionSpec) {
                final ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec4 = (ArgumentAcceptingOptionSpec<T>)optionSpec;
                final List<T> list5 = (List<T>)argumentAcceptingOptionSpec4.defaultValues();
                if (!list5.isEmpty()) {
                    return list5.get(0);
                }
            }
            throw throwable3;
        }
    }
    
    private static boolean isNotNullOrEmpty(final String s) {
        return s != null && !s.isEmpty();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        System.setProperty("java.awt.headless", "true");
    }
}
