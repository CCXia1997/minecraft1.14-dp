package net.minecraft.server.rcon;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.UncaughtExceptionHandler;
import com.google.common.collect.Lists;
import java.net.ServerSocket;
import java.net.DatagramSocket;
import java.util.List;
import net.minecraft.server.dedicated.DedicatedServer;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;

public abstract class RconBase implements Runnable
{
    private static final Logger h;
    private static final AtomicInteger i;
    protected boolean running;
    protected final DedicatedServer server;
    protected final String description;
    protected Thread thread;
    protected final int e = 5;
    protected final List<DatagramSocket> sockets;
    protected final List<ServerSocket> serverSockets;
    
    protected RconBase(final DedicatedServer dedicatedServer, final String string) {
        this.sockets = Lists.newArrayList();
        this.serverSockets = Lists.newArrayList();
        this.server = dedicatedServer;
        this.description = string;
        if (this.server.isDebuggingEnabled()) {
            this.warn("Debugging is enabled, performance maybe reduced!");
        }
    }
    
    public synchronized void start() {
        (this.thread = new Thread(this, this.description + " #" + RconBase.i.incrementAndGet())).setUncaughtExceptionHandler(new UncaughtExceptionHandler(RconBase.h));
        this.thread.start();
        this.running = true;
    }
    
    public synchronized void stop() {
        this.running = false;
        if (null == this.thread) {
            return;
        }
        int integer1 = 0;
        while (this.thread.isAlive()) {
            try {
                this.thread.join(1000L);
                ++integer1;
                if (5 <= integer1) {
                    this.warn("Waited " + integer1 + " seconds attempting force stop!");
                    this.forceClose(true);
                }
                else {
                    if (!this.thread.isAlive()) {
                        continue;
                    }
                    this.warn("Thread " + this + " (" + this.thread.getState() + ") failed to exit after " + integer1 + " second(s)");
                    this.warn("Stack:");
                    for (final StackTraceElement stackTraceElement5 : this.thread.getStackTrace()) {
                        this.warn(stackTraceElement5.toString());
                    }
                    this.thread.interrupt();
                }
            }
            catch (InterruptedException ex) {}
        }
        this.forceClose(true);
        this.thread = null;
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    protected void log(final String string) {
        this.server.log(string);
    }
    
    protected void info(final String string) {
        this.server.info(string);
    }
    
    protected void warn(final String string) {
        this.server.warn(string);
    }
    
    protected void logError(final String string) {
        this.server.logError(string);
    }
    
    protected int getCurrentPlayerCount() {
        return this.server.getCurrentPlayerCount();
    }
    
    protected void registerSocket(final DatagramSocket datagramSocket) {
        this.log("registerSocket: " + datagramSocket);
        this.sockets.add(datagramSocket);
    }
    
    protected boolean closeSocket(final DatagramSocket socket, final boolean boolean2) {
        this.log("closeSocket: " + socket);
        if (null == socket) {
            return false;
        }
        boolean boolean3 = false;
        if (!socket.isClosed()) {
            socket.close();
            boolean3 = true;
        }
        if (boolean2) {
            this.sockets.remove(socket);
        }
        return boolean3;
    }
    
    protected boolean closeSocket(final ServerSocket serverSocket) {
        return this.closeSocket(serverSocket, true);
    }
    
    protected boolean closeSocket(final ServerSocket socket, final boolean boolean2) {
        this.log("closeSocket: " + socket);
        if (null == socket) {
            return false;
        }
        boolean boolean3 = false;
        try {
            if (!socket.isClosed()) {
                socket.close();
                boolean3 = true;
            }
        }
        catch (IOException iOException4) {
            this.warn("IO: " + iOException4.getMessage());
        }
        if (boolean2) {
            this.serverSockets.remove(socket);
        }
        return boolean3;
    }
    
    protected void forceClose() {
        this.forceClose(false);
    }
    
    protected void forceClose(final boolean boolean1) {
        int integer2 = 0;
        for (final DatagramSocket datagramSocket4 : this.sockets) {
            if (this.closeSocket(datagramSocket4, false)) {
                ++integer2;
            }
        }
        this.sockets.clear();
        for (final ServerSocket serverSocket4 : this.serverSockets) {
            if (this.closeSocket(serverSocket4, false)) {
                ++integer2;
            }
        }
        this.serverSockets.clear();
        if (boolean1 && 0 < integer2) {
            this.warn("Force closed " + integer2 + " sockets");
        }
    }
    
    static {
        h = LogManager.getLogger();
        i = new AtomicInteger(0);
    }
}
