package net.minecraft.client.options;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import java.io.File;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerList
{
    private static final Logger LOGGER;
    private final MinecraftClient client;
    private final List<ServerEntry> serverEntries;
    
    public ServerList(final MinecraftClient minecraftClient) {
        this.serverEntries = Lists.newArrayList();
        this.client = minecraftClient;
        this.loadFile();
    }
    
    public void loadFile() {
        try {
            this.serverEntries.clear();
            final CompoundTag compoundTag1 = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
            if (compoundTag1 == null) {
                return;
            }
            final ListTag listTag2 = compoundTag1.getList("servers", 10);
            for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
                this.serverEntries.add(ServerEntry.deserialize(listTag2.getCompoundTag(integer3)));
            }
        }
        catch (Exception exception1) {
            ServerList.LOGGER.error("Couldn't load server list", (Throwable)exception1);
        }
    }
    
    public void saveFile() {
        try {
            final ListTag listTag1 = new ListTag();
            for (final ServerEntry serverEntry3 : this.serverEntries) {
                ((AbstractList<CompoundTag>)listTag1).add(serverEntry3.serialize());
            }
            final CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.put("servers", listTag1);
            NbtIo.safeWrite(compoundTag2, new File(this.client.runDirectory, "servers.dat"));
        }
        catch (Exception exception1) {
            ServerList.LOGGER.error("Couldn't save server list", (Throwable)exception1);
        }
    }
    
    public ServerEntry get(final int integer) {
        return this.serverEntries.get(integer);
    }
    
    public void remove(final ServerEntry serverEntry) {
        this.serverEntries.remove(serverEntry);
    }
    
    public void add(final ServerEntry serverEntry) {
        this.serverEntries.add(serverEntry);
    }
    
    public int size() {
        return this.serverEntries.size();
    }
    
    public void swapEntries(final int index1, final int integer2) {
        final ServerEntry serverEntry3 = this.get(index1);
        this.serverEntries.set(index1, this.get(integer2));
        this.serverEntries.set(integer2, serverEntry3);
        this.saveFile();
    }
    
    public void set(final int index, final ServerEntry serverEntry) {
        this.serverEntries.set(index, serverEntry);
    }
    
    public static void updateServerListEntry(final ServerEntry e) {
        final ServerList serverList2 = new ServerList(MinecraftClient.getInstance());
        serverList2.loadFile();
        for (int integer3 = 0; integer3 < serverList2.size(); ++integer3) {
            final ServerEntry serverEntry4 = serverList2.get(integer3);
            if (serverEntry4.name.equals(e.name) && serverEntry4.address.equals(e.address)) {
                serverList2.set(integer3, e);
                break;
            }
        }
        serverList2.saveFile();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
