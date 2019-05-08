package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import net.minecraft.util.UncaughtExceptionLogger;
import java.net.DatagramSocket;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanServerPinger extends Thread
{
    private static final AtomicInteger THREAD_ID;
    private static final Logger LOGGER;
    private final String motd;
    private final DatagramSocket socket;
    private boolean isRunning;
    private final String addressPort;
    
    public LanServerPinger(final String motd, final String string2) throws IOException {
        super("LanServerPinger #" + LanServerPinger.THREAD_ID.incrementAndGet());
        this.isRunning = true;
        this.motd = motd;
        this.addressPort = string2;
        this.setDaemon(true);
        this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LanServerPinger.LOGGER));
        this.socket = new DatagramSocket();
    }
    
    @Override
    public void run() {
        final String string1 = createAnnouncement(this.motd, this.addressPort);
        final byte[] arr2 = string1.getBytes(StandardCharsets.UTF_8);
        while (!this.isInterrupted() && this.isRunning) {
            try {
                final InetAddress inetAddress3 = InetAddress.getByName("224.0.2.60");
                final DatagramPacket datagramPacket4 = new DatagramPacket(arr2, arr2.length, inetAddress3, 4445);
                this.socket.send(datagramPacket4);
            }
            catch (IOException iOException3) {
                LanServerPinger.LOGGER.warn("LanServerPinger: {}", iOException3.getMessage());
                break;
            }
            try {
                Thread.sleep(1500L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    @Override
    public void interrupt() {
        super.interrupt();
        this.isRunning = false;
    }
    
    public static String createAnnouncement(final String motd, final String addressPort) {
        return "[MOTD]" + motd + "[/MOTD][AD]" + addressPort + "[/AD]";
    }
    
    public static String parseAnnouncementMotd(final String announcement) {
        final int integer2 = announcement.indexOf("[MOTD]");
        if (integer2 < 0) {
            return "missing no";
        }
        final int integer3 = announcement.indexOf("[/MOTD]", integer2 + "[MOTD]".length());
        if (integer3 < integer2) {
            return "missing no";
        }
        return announcement.substring(integer2 + "[MOTD]".length(), integer3);
    }
    
    public static String parseAnnouncementAddressPort(final String announcement) {
        final int integer2 = announcement.indexOf("[/MOTD]");
        if (integer2 < 0) {
            return null;
        }
        final int integer3 = announcement.indexOf("[/MOTD]", integer2 + "[/MOTD]".length());
        if (integer3 >= 0) {
            return null;
        }
        final int integer4 = announcement.indexOf("[AD]", integer2 + "[/MOTD]".length());
        if (integer4 < 0) {
            return null;
        }
        final int integer5 = announcement.indexOf("[/AD]", integer4 + "[AD]".length());
        if (integer5 < integer4) {
            return null;
        }
        return announcement.substring(integer4 + "[AD]".length(), integer5);
    }
    
    static {
        THREAD_ID = new AtomicInteger(0);
        LOGGER = LogManager.getLogger();
    }
}
