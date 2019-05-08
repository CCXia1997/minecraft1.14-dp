package net.minecraft.server.rcon;

import java.util.Random;
import java.nio.charset.StandardCharsets;
import java.net.SocketException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import net.minecraft.util.SystemUtil;
import java.io.IOException;
import java.util.Date;
import com.google.common.collect.Maps;
import java.net.UnknownHostException;
import java.net.InetAddress;
import net.minecraft.server.dedicated.DedicatedServer;
import java.net.SocketAddress;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class QueryResponseHandler extends RconBase
{
    private long lastQueryTime;
    private final int queryPort;
    private final int port;
    private final int maxPlayerCount;
    private final String motd;
    private final String levelName;
    private DatagramSocket socket;
    private final byte[] packetBuffer;
    private DatagramPacket currentPacket;
    private final Map<SocketAddress, String> q;
    private String ip;
    private String hostname;
    private final Map<SocketAddress, a> t;
    private final long u;
    private final DataStreamHelper data;
    private long w;
    
    public QueryResponseHandler(final DedicatedServer server) {
        super(server, "Query Listener");
        this.packetBuffer = new byte[1460];
        this.queryPort = server.getProperties().queryPort;
        this.hostname = server.getHostname();
        this.port = server.getPort();
        this.motd = server.getMotd();
        this.maxPlayerCount = server.getMaxPlayerCount();
        this.levelName = server.getLevelName();
        this.w = 0L;
        this.ip = "0.0.0.0";
        if (this.hostname.isEmpty() || this.ip.equals(this.hostname)) {
            this.hostname = "0.0.0.0";
            try {
                final InetAddress inetAddress2 = InetAddress.getLocalHost();
                this.ip = inetAddress2.getHostAddress();
            }
            catch (UnknownHostException unknownHostException2) {
                this.warn("Unable to determine local host IP, please set server-ip in server.properties: " + unknownHostException2.getMessage());
            }
        }
        else {
            this.ip = this.hostname;
        }
        this.q = Maps.newHashMap();
        this.data = new DataStreamHelper(1460);
        this.t = Maps.newHashMap();
        this.u = new Date().getTime();
    }
    
    private void reply(final byte[] buf, final DatagramPacket datagramPacket) throws IOException {
        this.socket.send(new DatagramPacket(buf, buf.length, datagramPacket.getSocketAddress()));
    }
    
    private boolean handle(final DatagramPacket packet) throws IOException {
        final byte[] arr2 = packet.getData();
        final int integer3 = packet.getLength();
        final SocketAddress socketAddress4 = packet.getSocketAddress();
        this.log("Packet len " + integer3 + " [" + socketAddress4 + "]");
        if (3 > integer3 || -2 != arr2[0] || -3 != arr2[1]) {
            this.log("Invalid packet [" + socketAddress4 + "]");
            return false;
        }
        this.log("Packet '" + BufferHelper.toHex(arr2[2]) + "' [" + socketAddress4 + "]");
        switch (arr2[2]) {
            case 9: {
                this.d(packet);
                this.log("Challenge [" + socketAddress4 + "]");
                return true;
            }
            case 0: {
                if (!this.c(packet)) {
                    this.log("Invalid challenge [" + socketAddress4 + "]");
                    return false;
                }
                if (15 == integer3) {
                    this.reply(this.createRulesReply(packet), packet);
                    this.log("Rules [" + socketAddress4 + "]");
                    break;
                }
                final DataStreamHelper dataStreamHelper5 = new DataStreamHelper(1460);
                dataStreamHelper5.write(0);
                dataStreamHelper5.write(this.a(packet.getSocketAddress()));
                dataStreamHelper5.writeBytes(this.motd);
                dataStreamHelper5.writeBytes("SMP");
                dataStreamHelper5.writeBytes(this.levelName);
                dataStreamHelper5.writeBytes(Integer.toString(this.getCurrentPlayerCount()));
                dataStreamHelper5.writeBytes(Integer.toString(this.maxPlayerCount));
                dataStreamHelper5.writeShort((short)this.port);
                dataStreamHelper5.writeBytes(this.ip);
                this.reply(dataStreamHelper5.bytes(), packet);
                this.log("Status [" + socketAddress4 + "]");
                break;
            }
        }
        return true;
    }
    
    private byte[] createRulesReply(final DatagramPacket packet) throws IOException {
        final long long2 = SystemUtil.getMeasuringTimeMs();
        if (long2 < this.w + 5000L) {
            final byte[] arr4 = this.data.bytes();
            final byte[] arr5 = this.a(packet.getSocketAddress());
            arr4[1] = arr5[0];
            arr4[2] = arr5[1];
            arr4[3] = arr5[2];
            arr4[4] = arr5[3];
            return arr4;
        }
        this.w = long2;
        this.data.reset();
        this.data.write(0);
        this.data.write(this.a(packet.getSocketAddress()));
        this.data.writeBytes("splitnum");
        this.data.write(128);
        this.data.write(0);
        this.data.writeBytes("hostname");
        this.data.writeBytes(this.motd);
        this.data.writeBytes("gametype");
        this.data.writeBytes("SMP");
        this.data.writeBytes("game_id");
        this.data.writeBytes("MINECRAFT");
        this.data.writeBytes("version");
        this.data.writeBytes(this.server.getVersion());
        this.data.writeBytes("plugins");
        this.data.writeBytes(this.server.getPlugins());
        this.data.writeBytes("map");
        this.data.writeBytes(this.levelName);
        this.data.writeBytes("numplayers");
        this.data.writeBytes("" + this.getCurrentPlayerCount());
        this.data.writeBytes("maxplayers");
        this.data.writeBytes("" + this.maxPlayerCount);
        this.data.writeBytes("hostport");
        this.data.writeBytes("" + this.port);
        this.data.writeBytes("hostip");
        this.data.writeBytes(this.ip);
        this.data.write(0);
        this.data.write(1);
        this.data.writeBytes("player_");
        this.data.write(0);
        final String[] playerNames;
        final String[] arr6 = playerNames = this.server.getPlayerNames();
        for (final String string8 : playerNames) {
            this.data.writeBytes(string8);
        }
        this.data.write(0);
        return this.data.bytes();
    }
    
    private byte[] a(final SocketAddress socketAddress) {
        return this.t.get(socketAddress).c();
    }
    
    private Boolean c(final DatagramPacket datagramPacket) {
        final SocketAddress socketAddress2 = datagramPacket.getSocketAddress();
        if (!this.t.containsKey(socketAddress2)) {
            return false;
        }
        final byte[] arr3 = datagramPacket.getData();
        if (this.t.get(socketAddress2).a() != BufferHelper.getIntBE(arr3, 7, datagramPacket.getLength())) {
            return false;
        }
        return true;
    }
    
    private void d(final DatagramPacket datagramPacket) throws IOException {
        final a a2 = new a(datagramPacket);
        this.t.put(datagramPacket.getSocketAddress(), a2);
        this.reply(a2.b(), datagramPacket);
    }
    
    private void f() {
        if (!this.running) {
            return;
        }
        final long long1 = SystemUtil.getMeasuringTimeMs();
        if (long1 < this.lastQueryTime + 30000L) {
            return;
        }
        this.lastQueryTime = long1;
        final Iterator<Map.Entry<SocketAddress, a>> iterator3 = this.t.entrySet().iterator();
        while (iterator3.hasNext()) {
            final Map.Entry<SocketAddress, a> entry4 = iterator3.next();
            if (entry4.getValue().a(long1)) {
                iterator3.remove();
            }
        }
    }
    
    @Override
    public void run() {
        this.info("Query running on " + this.hostname + ":" + this.queryPort);
        this.lastQueryTime = SystemUtil.getMeasuringTimeMs();
        this.currentPacket = new DatagramPacket(this.packetBuffer, this.packetBuffer.length);
        try {
            while (this.running) {
                try {
                    this.socket.receive(this.currentPacket);
                    this.f();
                    this.handle(this.currentPacket);
                }
                catch (SocketTimeoutException socketTimeoutException1) {
                    this.f();
                }
                catch (PortUnreachableException ex) {}
                catch (IOException iOException1) {
                    this.handleIoException(iOException1);
                }
            }
        }
        finally {
            this.forceClose();
        }
    }
    
    @Override
    public void start() {
        if (this.running) {
            return;
        }
        if (0 >= this.queryPort || 65535 < this.queryPort) {
            this.warn("Invalid query port " + this.queryPort + " found in server.properties (queries disabled)");
            return;
        }
        if (this.initialize()) {
            super.start();
        }
    }
    
    private void handleIoException(final Exception e) {
        if (!this.running) {
            return;
        }
        this.warn("Unexpected exception, buggy JRE? (" + e + ")");
        if (!this.initialize()) {
            this.logError("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
        }
    }
    
    private boolean initialize() {
        try {
            this.registerSocket(this.socket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.hostname)));
            this.socket.setSoTimeout(500);
            return true;
        }
        catch (SocketException socketException1) {
            this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Socket): " + socketException1.getMessage());
        }
        catch (UnknownHostException unknownHostException1) {
            this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Unknown Host): " + unknownHostException1.getMessage());
        }
        catch (Exception exception1) {
            this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (E): " + exception1.getMessage());
        }
        return false;
    }
    
    class a
    {
        private final long b;
        private final int c;
        private final byte[] d;
        private final byte[] e;
        private final String f;
        
        public a(final DatagramPacket datagramPacket) {
            this.b = new Date().getTime();
            final byte[] arr3 = datagramPacket.getData();
            (this.d = new byte[4])[0] = arr3[3];
            this.d[1] = arr3[4];
            this.d[2] = arr3[5];
            this.d[3] = arr3[6];
            this.f = new String(this.d, StandardCharsets.UTF_8);
            this.c = new Random().nextInt(16777216);
            this.e = String.format("\t%s%d\u0000", this.f, this.c).getBytes(StandardCharsets.UTF_8);
        }
        
        public Boolean a(final long long1) {
            return this.b < long1;
        }
        
        public int a() {
            return this.c;
        }
        
        public byte[] b() {
            return this.e;
        }
        
        public byte[] c() {
            return this.d;
        }
    }
}
