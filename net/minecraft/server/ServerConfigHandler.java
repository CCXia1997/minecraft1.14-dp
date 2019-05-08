package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import java.text.ParseException;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import com.google.common.collect.Lists;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.util.Date;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import net.minecraft.entity.player.PlayerEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.Agent;
import net.minecraft.util.ChatUtil;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.Collection;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class ServerConfigHandler
{
    private static final Logger LOGGER;
    public static final File BANNED_IPS_FILE;
    public static final File BANNED_PLAYERS_FILE;
    public static final File OPERATORS_FILE;
    public static final File WHITE_LIST_FILE;
    
    static List<String> processSimpleListFile(final File file, final Map<String, String[]> valueMap) throws IOException {
        final List<String> list3 = Files.readLines(file, StandardCharsets.UTF_8);
        for (String string5 : list3) {
            string5 = string5.trim();
            if (!string5.startsWith("#")) {
                if (string5.length() < 1) {
                    continue;
                }
                final String[] arr6 = string5.split("\\|");
                valueMap.put(arr6[0].toLowerCase(Locale.ROOT), arr6);
            }
        }
        return list3;
    }
    
    private static void lookupProfile(final MinecraftServer minecraftServer, final Collection<String> bannedPlayers, final ProfileLookupCallback callback) {
        final String[] arr4 = bannedPlayers.stream().filter(string -> !ChatUtil.isEmpty(string)).<String>toArray(String[]::new);
        if (minecraftServer.isOnlineMode()) {
            minecraftServer.getGameProfileRepo().findProfilesByNames(arr4, Agent.MINECRAFT, callback);
        }
        else {
            for (final String string2 : arr4) {
                final UUID uUID9 = PlayerEntity.getUuidFromProfile(new GameProfile((UUID)null, string2));
                final GameProfile gameProfile10 = new GameProfile(uUID9, string2);
                callback.onProfileLookupSucceeded(gameProfile10);
            }
        }
    }
    
    public static boolean convertBannedPlayers(final MinecraftServer minecraftServer) {
        final BannedPlayerList bannedPlayerList2 = new BannedPlayerList(PlayerManager.BANNED_PLAYERS_FILE);
        if (ServerConfigHandler.BANNED_PLAYERS_FILE.exists() && ServerConfigHandler.BANNED_PLAYERS_FILE.isFile()) {
            if (bannedPlayerList2.getFile().exists()) {
                try {
                    bannedPlayerList2.load();
                }
                catch (FileNotFoundException fileNotFoundException3) {
                    ServerConfigHandler.LOGGER.warn("Could not load existing file {}", bannedPlayerList2.getFile().getName(), fileNotFoundException3);
                }
            }
            try {
                final Map<String, String[]> map3 = Maps.newHashMap();
                processSimpleListFile(ServerConfigHandler.BANNED_PLAYERS_FILE, map3);
                final ProfileLookupCallback profileLookupCallback4 = (ProfileLookupCallback)new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(final GameProfile profile) {
                        minecraftServer.getUserCache().add(profile);
                        final String[] arr2 = map3.get(profile.getName().toLowerCase(Locale.ROOT));
                        if (arr2 == null) {
                            ServerConfigHandler.LOGGER.warn("Could not convert user banlist entry for {}", profile.getName());
                            throw new ServerConfigException("Profile not in the conversionlist");
                        }
                        final Date date3 = (arr2.length > 1) ? stringToDate(arr2[1], null) : null;
                        final String string4 = (arr2.length > 2) ? arr2[2] : null;
                        final Date date4 = (arr2.length > 3) ? stringToDate(arr2[3], null) : null;
                        final String string5 = (arr2.length > 4) ? arr2[4] : null;
                        ((ServerConfigList<K, BannedPlayerEntry>)bannedPlayerList2).add(new BannedPlayerEntry(profile, date3, string4, date4, string5));
                    }
                    
                    public void onProfileLookupFailed(final GameProfile profile, final Exception exception) {
                        ServerConfigHandler.LOGGER.warn("Could not lookup user banlist entry for {}", profile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", (Throwable)exception);
                        }
                    }
                };
                lookupProfile(minecraftServer, map3.keySet(), profileLookupCallback4);
                bannedPlayerList2.save();
                markFileConverted(ServerConfigHandler.BANNED_PLAYERS_FILE);
            }
            catch (IOException iOException3) {
                ServerConfigHandler.LOGGER.warn("Could not read old user banlist to convert it!", (Throwable)iOException3);
                return false;
            }
            catch (ServerConfigException serverConfigException3) {
                ServerConfigHandler.LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException3);
                return false;
            }
            return true;
        }
        return true;
    }
    
    public static boolean convertBannedIps(final MinecraftServer minecraftServer) {
        final BannedIpList bannedIpList2 = new BannedIpList(PlayerManager.BANNED_IPS_FILE);
        if (ServerConfigHandler.BANNED_IPS_FILE.exists() && ServerConfigHandler.BANNED_IPS_FILE.isFile()) {
            if (bannedIpList2.getFile().exists()) {
                try {
                    bannedIpList2.load();
                }
                catch (FileNotFoundException fileNotFoundException3) {
                    ServerConfigHandler.LOGGER.warn("Could not load existing file {}", bannedIpList2.getFile().getName(), fileNotFoundException3);
                }
            }
            try {
                final Map<String, String[]> map3 = Maps.newHashMap();
                processSimpleListFile(ServerConfigHandler.BANNED_IPS_FILE, map3);
                for (final String string5 : map3.keySet()) {
                    final String[] arr6 = map3.get(string5);
                    final Date date7 = (arr6.length > 1) ? stringToDate(arr6[1], null) : null;
                    final String string6 = (arr6.length > 2) ? arr6[2] : null;
                    final Date date8 = (arr6.length > 3) ? stringToDate(arr6[3], null) : null;
                    final String string7 = (arr6.length > 4) ? arr6[4] : null;
                    ((ServerConfigList<K, BannedIpEntry>)bannedIpList2).add(new BannedIpEntry(string5, date7, string6, date8, string7));
                }
                bannedIpList2.save();
                markFileConverted(ServerConfigHandler.BANNED_IPS_FILE);
            }
            catch (IOException iOException3) {
                ServerConfigHandler.LOGGER.warn("Could not parse old ip banlist to convert it!", (Throwable)iOException3);
                return false;
            }
            return true;
        }
        return true;
    }
    
    public static boolean convertOperators(final MinecraftServer minecraftServer) {
        final OperatorList operatorList2 = new OperatorList(PlayerManager.OPERATORS_FILE);
        if (ServerConfigHandler.OPERATORS_FILE.exists() && ServerConfigHandler.OPERATORS_FILE.isFile()) {
            if (operatorList2.getFile().exists()) {
                try {
                    operatorList2.load();
                }
                catch (FileNotFoundException fileNotFoundException3) {
                    ServerConfigHandler.LOGGER.warn("Could not load existing file {}", operatorList2.getFile().getName(), fileNotFoundException3);
                }
            }
            try {
                final List<String> list3 = Files.readLines(ServerConfigHandler.OPERATORS_FILE, StandardCharsets.UTF_8);
                final ProfileLookupCallback profileLookupCallback4 = (ProfileLookupCallback)new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(final GameProfile profile) {
                        minecraftServer.getUserCache().add(profile);
                        ((ServerConfigList<K, OperatorEntry>)operatorList2).add(new OperatorEntry(profile, minecraftServer.getOpPermissionLevel(), false));
                    }
                    
                    public void onProfileLookupFailed(final GameProfile profile, final Exception exception) {
                        ServerConfigHandler.LOGGER.warn("Could not lookup oplist entry for {}", profile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", (Throwable)exception);
                        }
                    }
                };
                lookupProfile(minecraftServer, list3, profileLookupCallback4);
                operatorList2.save();
                markFileConverted(ServerConfigHandler.OPERATORS_FILE);
            }
            catch (IOException iOException3) {
                ServerConfigHandler.LOGGER.warn("Could not read old oplist to convert it!", (Throwable)iOException3);
                return false;
            }
            catch (ServerConfigException serverConfigException3) {
                ServerConfigHandler.LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException3);
                return false;
            }
            return true;
        }
        return true;
    }
    
    public static boolean convertWhitelist(final MinecraftServer minecraftServer) {
        final Whitelist whitelist2 = new Whitelist(PlayerManager.WHITELIST_FILE);
        if (ServerConfigHandler.WHITE_LIST_FILE.exists() && ServerConfigHandler.WHITE_LIST_FILE.isFile()) {
            if (whitelist2.getFile().exists()) {
                try {
                    whitelist2.load();
                }
                catch (FileNotFoundException fileNotFoundException3) {
                    ServerConfigHandler.LOGGER.warn("Could not load existing file {}", whitelist2.getFile().getName(), fileNotFoundException3);
                }
            }
            try {
                final List<String> list3 = Files.readLines(ServerConfigHandler.WHITE_LIST_FILE, StandardCharsets.UTF_8);
                final ProfileLookupCallback profileLookupCallback4 = (ProfileLookupCallback)new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(final GameProfile profile) {
                        minecraftServer.getUserCache().add(profile);
                        ((ServerConfigList<K, WhitelistEntry>)whitelist2).add(new WhitelistEntry(profile));
                    }
                    
                    public void onProfileLookupFailed(final GameProfile profile, final Exception exception) {
                        ServerConfigHandler.LOGGER.warn("Could not lookup user whitelist entry for {}", profile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", (Throwable)exception);
                        }
                    }
                };
                lookupProfile(minecraftServer, list3, profileLookupCallback4);
                whitelist2.save();
                markFileConverted(ServerConfigHandler.WHITE_LIST_FILE);
            }
            catch (IOException iOException3) {
                ServerConfigHandler.LOGGER.warn("Could not read old whitelist to convert it!", (Throwable)iOException3);
                return false;
            }
            catch (ServerConfigException serverConfigException3) {
                ServerConfigHandler.LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException3);
                return false;
            }
            return true;
        }
        return true;
    }
    
    public static String getPlayerUuidByName(final MinecraftServer minecraftServer, final String name) {
        if (ChatUtil.isEmpty(name) || name.length() > 16) {
            return name;
        }
        final GameProfile gameProfile3 = minecraftServer.getUserCache().findByName(name);
        if (gameProfile3 != null && gameProfile3.getId() != null) {
            return gameProfile3.getId().toString();
        }
        if (minecraftServer.isSinglePlayer() || !minecraftServer.isOnlineMode()) {
            return PlayerEntity.getUuidFromProfile(new GameProfile((UUID)null, name)).toString();
        }
        final List<GameProfile> list4 = Lists.newArrayList();
        final ProfileLookupCallback profileLookupCallback5 = (ProfileLookupCallback)new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(final GameProfile profile) {
                minecraftServer.getUserCache().add(profile);
                list4.add(profile);
            }
            
            public void onProfileLookupFailed(final GameProfile profile, final Exception exception) {
                ServerConfigHandler.LOGGER.warn("Could not lookup user whitelist entry for {}", profile.getName(), exception);
            }
        };
        lookupProfile(minecraftServer, Lists.<String>newArrayList(name), profileLookupCallback5);
        if (!list4.isEmpty() && list4.get(0).getId() != null) {
            return list4.get(0).getId().toString();
        }
        return "";
    }
    
    public static boolean convertPlayerFiles(final MinecraftDedicatedServer minecraftServer) {
        final File file2 = getLevelPlayersFolder(minecraftServer);
        final File file3 = new File(file2.getParentFile(), "playerdata");
        final File file4 = new File(file2.getParentFile(), "unknownplayers");
        if (!file2.exists() || !file2.isDirectory()) {
            return true;
        }
        final File[] arr5 = file2.listFiles();
        final List<String> list6 = Lists.newArrayList();
        for (final File file5 : arr5) {
            final String string11 = file5.getName();
            if (string11.toLowerCase(Locale.ROOT).endsWith(".dat")) {
                final String string12 = string11.substring(0, string11.length() - ".dat".length());
                if (!string12.isEmpty()) {
                    list6.add(string12);
                }
            }
        }
        try {
            final String[] arr6 = list6.<String>toArray(new String[list6.size()]);
            final ProfileLookupCallback profileLookupCallback8 = (ProfileLookupCallback)new ProfileLookupCallback() {
                public void onProfileLookupSucceeded(final GameProfile profile) {
                    minecraftServer.getUserCache().add(profile);
                    final UUID uUID2 = profile.getId();
                    if (uUID2 == null) {
                        throw new ServerConfigException("Missing UUID for user profile " + profile.getName());
                    }
                    this.convertPlayerFile(file3, this.getPlayerFileName(profile), uUID2.toString());
                }
                
                public void onProfileLookupFailed(final GameProfile profile, final Exception exception) {
                    ServerConfigHandler.LOGGER.warn("Could not lookup user uuid for {}", profile.getName(), exception);
                    if (exception instanceof ProfileNotFoundException) {
                        final String string3 = this.getPlayerFileName(profile);
                        this.convertPlayerFile(file4, string3, string3);
                        return;
                    }
                    throw new ServerConfigException("Could not request user " + profile.getName() + " from backend systems", (Throwable)exception);
                }
                
                private void convertPlayerFile(final File playerDataFolder, final String fileName, final String uuid) {
                    final File file4 = new File(file2, fileName + ".dat");
                    final File file5 = new File(playerDataFolder, uuid + ".dat");
                    createDirectory(playerDataFolder);
                    if (!file4.renameTo(file5)) {
                        throw new ServerConfigException("Could not convert file for " + fileName);
                    }
                }
                
                private String getPlayerFileName(final GameProfile profile) {
                    String string2 = null;
                    for (final String string3 : arr6) {
                        if (string3 != null && string3.equalsIgnoreCase(profile.getName())) {
                            string2 = string3;
                            break;
                        }
                    }
                    if (string2 == null) {
                        throw new ServerConfigException("Could not find the filename for " + profile.getName() + " anymore");
                    }
                    return string2;
                }
            };
            lookupProfile(minecraftServer, Lists.<String>newArrayList(arr6), profileLookupCallback8);
        }
        catch (ServerConfigException serverConfigException7) {
            ServerConfigHandler.LOGGER.error("Conversion failed, please try again later", (Throwable)serverConfigException7);
            return false;
        }
        return true;
    }
    
    private static void createDirectory(final File directory) {
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return;
            }
            throw new ServerConfigException("Can't create directory " + directory.getName() + " in world save directory.");
        }
        else if (!directory.mkdirs()) {
            throw new ServerConfigException("Can't create directory " + directory.getName() + " in world save directory.");
        }
    }
    
    public static boolean checkSuccess(final MinecraftServer minecraftServer) {
        boolean boolean2 = checkListConversionSuccess();
        boolean2 = (boolean2 && checkPlayerConversionSuccess(minecraftServer));
        return boolean2;
    }
    
    private static boolean checkListConversionSuccess() {
        boolean boolean1 = false;
        if (ServerConfigHandler.BANNED_PLAYERS_FILE.exists() && ServerConfigHandler.BANNED_PLAYERS_FILE.isFile()) {
            boolean1 = true;
        }
        boolean boolean2 = false;
        if (ServerConfigHandler.BANNED_IPS_FILE.exists() && ServerConfigHandler.BANNED_IPS_FILE.isFile()) {
            boolean2 = true;
        }
        boolean boolean3 = false;
        if (ServerConfigHandler.OPERATORS_FILE.exists() && ServerConfigHandler.OPERATORS_FILE.isFile()) {
            boolean3 = true;
        }
        boolean boolean4 = false;
        if (ServerConfigHandler.WHITE_LIST_FILE.exists() && ServerConfigHandler.WHITE_LIST_FILE.isFile()) {
            boolean4 = true;
        }
        if (boolean1 || boolean2 || boolean3 || boolean4) {
            ServerConfigHandler.LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            ServerConfigHandler.LOGGER.warn("** please remove the following files and restart the server:");
            if (boolean1) {
                ServerConfigHandler.LOGGER.warn("* {}", ServerConfigHandler.BANNED_PLAYERS_FILE.getName());
            }
            if (boolean2) {
                ServerConfigHandler.LOGGER.warn("* {}", ServerConfigHandler.BANNED_IPS_FILE.getName());
            }
            if (boolean3) {
                ServerConfigHandler.LOGGER.warn("* {}", ServerConfigHandler.OPERATORS_FILE.getName());
            }
            if (boolean4) {
                ServerConfigHandler.LOGGER.warn("* {}", ServerConfigHandler.WHITE_LIST_FILE.getName());
            }
            return false;
        }
        return true;
    }
    
    private static boolean checkPlayerConversionSuccess(final MinecraftServer minecraftServer) {
        final File file2 = getLevelPlayersFolder(minecraftServer);
        if (file2.exists() && file2.isDirectory() && (file2.list().length > 0 || !file2.delete())) {
            ServerConfigHandler.LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            ServerConfigHandler.LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            ServerConfigHandler.LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", file2.getPath());
            return false;
        }
        return true;
    }
    
    private static File getLevelPlayersFolder(final MinecraftServer minecraftServer) {
        final String string2 = minecraftServer.getLevelName();
        final File file3 = new File(string2);
        return new File(file3, "players");
    }
    
    private static void markFileConverted(final File file) {
        final File file2 = new File(file.getName() + ".converted");
        file.renameTo(file2);
    }
    
    private static Date stringToDate(final String string, final Date fallback) {
        Date date3;
        try {
            date3 = BanEntry.DATE_FORMAT.parse(string);
        }
        catch (ParseException parseException4) {
            date3 = fallback;
        }
        return date3;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BANNED_IPS_FILE = new File("banned-ips.txt");
        BANNED_PLAYERS_FILE = new File("banned-players.txt");
        OPERATORS_FILE = new File("ops.txt");
        WHITE_LIST_FILE = new File("white-list.txt");
    }
    
    static class ServerConfigException extends RuntimeException
    {
        private ServerConfigException(final String title, final Throwable other) {
            super(title, other);
        }
        
        private ServerConfigException(final String title) {
            super(title);
        }
    }
}
