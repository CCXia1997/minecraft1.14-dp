package net.minecraft.server.rcon;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import com.google.common.collect.Maps;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.DedicatedServer;
import java.net.SocketAddress;
import java.util.Map;
import java.net.ServerSocket;

public class RconServer extends RconBase
{
    private final int port;
    private String hostname;
    private ServerSocket listener;
    private final String password;
    private Map<SocketAddress, RconClient> clients;
    
    public RconServer(final DedicatedServer dedicatedServer) {
        super(dedicatedServer, "RCON Listener");
        final ServerPropertiesHandler serverPropertiesHandler2 = dedicatedServer.getProperties();
        this.port = serverPropertiesHandler2.rconPort;
        this.password = serverPropertiesHandler2.rconPassword;
        this.hostname = dedicatedServer.getHostname();
        if (this.hostname.isEmpty()) {
            this.hostname = "0.0.0.0";
        }
        this.cleanClientList();
        this.listener = null;
    }
    
    private void cleanClientList() {
        this.clients = Maps.newHashMap();
    }
    
    private void removeStoppedClients() {
        final Iterator<Map.Entry<SocketAddress, RconClient>> iterator1 = this.clients.entrySet().iterator();
        while (iterator1.hasNext()) {
            final Map.Entry<SocketAddress, RconClient> entry2 = iterator1.next();
            if (!entry2.getValue().isRunning()) {
                iterator1.remove();
            }
        }
    }
    
    @Override
    public void run() {
        this.info("RCON running on " + this.hostname + ":" + this.port);
        try {
            while (this.running) {
                try {
                    final Socket socket1 = this.listener.accept();
                    socket1.setSoTimeout(500);
                    final RconClient rconClient2 = new RconClient(this.server, this.password, socket1);
                    rconClient2.start();
                    this.clients.put(socket1.getRemoteSocketAddress(), rconClient2);
                    this.removeStoppedClients();
                }
                catch (SocketTimeoutException socketTimeoutException1) {
                    this.removeStoppedClients();
                }
                catch (IOException iOException1) {
                    if (!this.running) {
                        continue;
                    }
                    this.info("IO: " + iOException1.getMessage());
                }
            }
        }
        finally {
            this.closeSocket(this.listener);
        }
    }
    
    @Override
    public void start() {
        if (this.password.isEmpty()) {
            this.warn("No rcon password set in server.properties, rcon disabled!");
            return;
        }
        if (0 >= this.port || 65535 < this.port) {
            this.warn("Invalid rcon port " + this.port + " found in server.properties, rcon disabled!");
            return;
        }
        if (this.running) {
            return;
        }
        try {
            (this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname))).setSoTimeout(500);
            super.start();
        }
        catch (IOException iOException1) {
            this.warn("Unable to initialise rcon on " + this.hostname + ":" + this.port + " : " + iOException1.getMessage());
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        for (final Map.Entry<SocketAddress, RconClient> entry2 : this.clients.entrySet()) {
            entry2.getValue().stop();
        }
        this.closeSocket(this.listener);
        this.cleanClientList();
    }
}
