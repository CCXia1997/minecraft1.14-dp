package net.minecraft.client.network;

import java.nio.charset.StandardCharsets;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.io.IOException;
import net.minecraft.util.UncaughtExceptionLogger;
import java.net.MulticastSocket;
import java.util.Iterator;
import net.minecraft.server.LanServerPinger;
import java.net.InetAddress;
import java.util.Collections;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanServerQueryManager
{
    private static final AtomicInteger THREAD_ID;
    private static final Logger LOGGER;
    
    static {
        THREAD_ID = new AtomicInteger(0);
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public static class LanServerEntryList
    {
        private final List<LanServerEntry> serverEntries;
        private boolean dirty;
        
        public LanServerEntryList() {
            this.serverEntries = Lists.newArrayList();
        }
        
        public synchronized boolean needsUpdate() {
            return this.dirty;
        }
        
        public synchronized void markClean() {
            this.dirty = false;
        }
        
        public synchronized List<LanServerEntry> getServers() {
            return Collections.<LanServerEntry>unmodifiableList(this.serverEntries);
        }
        
        public synchronized void addServer(final String string, final InetAddress inetAddress) {
            final String string2 = LanServerPinger.parseAnnouncementMotd(string);
            String string3 = LanServerPinger.parseAnnouncementAddressPort(string);
            if (string3 == null) {
                return;
            }
            string3 = inetAddress.getHostAddress() + ":" + string3;
            boolean boolean5 = false;
            for (final LanServerEntry lanServerEntry7 : this.serverEntries) {
                if (lanServerEntry7.getAddressPort().equals(string3)) {
                    lanServerEntry7.updateLastTime();
                    boolean5 = true;
                    break;
                }
            }
            if (!boolean5) {
                this.serverEntries.add(new LanServerEntry(string2, string3));
                this.dirty = true;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class LanServerDetector extends Thread
    {
        private final LanServerEntryList entryList;
        private final InetAddress multicastAddress;
        private final MulticastSocket socket;
        
        public LanServerDetector(final LanServerEntryList lanServerEntryList) throws IOException {
            super("LanServerDetector #" + LanServerQueryManager.THREAD_ID.incrementAndGet());
            this.entryList = lanServerEntryList;
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LanServerQueryManager.LOGGER));
            this.socket = new MulticastSocket(4445);
            this.multicastAddress = InetAddress.getByName("224.0.2.60");
            this.socket.setSoTimeout(5000);
            this.socket.joinGroup(this.multicastAddress);
        }
        
        @Override
        public void run() {
            final byte[] arr2 = new byte[1024];
            while (!this.isInterrupted()) {
                final DatagramPacket datagramPacket1 = new DatagramPacket(arr2, arr2.length);
                try {
                    this.socket.receive(datagramPacket1);
                }
                catch (SocketTimeoutException socketTimeoutException3) {
                    continue;
                }
                catch (IOException iOException3) {
                    LanServerQueryManager.LOGGER.error("Couldn't ping server", (Throwable)iOException3);
                    break;
                }
                final String string3 = new String(datagramPacket1.getData(), datagramPacket1.getOffset(), datagramPacket1.getLength(), StandardCharsets.UTF_8);
                LanServerQueryManager.LOGGER.debug("{}: {}", datagramPacket1.getAddress(), string3);
                this.entryList.addServer(string3, datagramPacket1.getAddress());
            }
            try {
                this.socket.leaveGroup(this.multicastAddress);
            }
            catch (IOException ex) {}
            this.socket.close();
        }
    }
}
