package net.minecraft.data.report;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.google.gson.JsonElement;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.datafixers.Schemas;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import java.nio.file.Paths;
import net.minecraft.util.UserCache;
import net.minecraft.server.MinecraftServer;
import java.io.File;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.UUID;
import java.net.Proxy;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import net.minecraft.data.DataProvider;

public class CommandSyntaxProvider implements DataProvider
{
    private static final Gson b;
    private final DataGenerator root;
    
    public CommandSyntaxProvider(final DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        final YggdrasilAuthenticationService yggdrasilAuthenticationService2 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        final MinecraftSessionService minecraftSessionService3 = yggdrasilAuthenticationService2.createMinecraftSessionService();
        final GameProfileRepository gameProfileRepository4 = yggdrasilAuthenticationService2.createProfileRepository();
        final File file5 = new File(this.root.getOutput().toFile(), "tmp");
        final UserCache userCache6 = new UserCache(gameProfileRepository4, new File(file5, MinecraftServer.USER_CACHE_FILE.getName()));
        final ServerPropertiesLoader serverPropertiesLoader7 = new ServerPropertiesLoader(Paths.get("server.properties"));
        final MinecraftServer minecraftServer8 = new MinecraftDedicatedServer(file5, serverPropertiesLoader7, Schemas.getFixer(), yggdrasilAuthenticationService2, minecraftSessionService3, gameProfileRepository4, userCache6, WorldGenerationProgressLogger::new, serverPropertiesLoader7.getPropertiesHandler().levelName);
        final Path path9 = this.root.getOutput().resolve("reports/commands.json");
        final CommandDispatcher<ServerCommandSource> commandDispatcher10 = minecraftServer8.getCommandManager().getDispatcher();
        DataProvider.writeToPath(CommandSyntaxProvider.b, dataCache, ArgumentTypes.<ServerCommandSource>toJson(commandDispatcher10, (com.mojang.brigadier.tree.CommandNode<ServerCommandSource>)commandDispatcher10.getRoot()), path9);
    }
    
    @Override
    public String getName() {
        return "Command Syntax";
    }
    
    static {
        b = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
}
