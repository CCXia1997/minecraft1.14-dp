package net.minecraft.server.rcon;

import org.apache.logging.log4j.LogManager;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.io.BufferedInputStream;
import net.minecraft.server.dedicated.DedicatedServer;
import java.net.Socket;
import org.apache.logging.log4j.Logger;

public class RconClient extends RconBase
{
    private static final Logger LOGGER;
    private boolean authenticated;
    private Socket socket;
    private final byte[] packetBuffer;
    private final String password;
    
    RconClient(final DedicatedServer dedicatedServer, final String string, final Socket socket) {
        super(dedicatedServer, "RCON Client");
        this.packetBuffer = new byte[1460];
        this.socket = socket;
        try {
            this.socket.setSoTimeout(0);
        }
        catch (Exception exception4) {
            this.running = false;
        }
        this.password = string;
        this.info("Rcon connection from: " + socket.getInetAddress());
    }
    
    @Override
    public void run() {
        try {
            while (this.running) {
                final BufferedInputStream bufferedInputStream1 = new BufferedInputStream(this.socket.getInputStream());
                final int integer2 = bufferedInputStream1.read(this.packetBuffer, 0, 1460);
                if (10 > integer2) {
                    return;
                }
                int integer3 = 0;
                final int integer4 = BufferHelper.getIntLE(this.packetBuffer, 0, integer2);
                if (integer4 != integer2 - 4) {
                    return;
                }
                integer3 += 4;
                final int integer5 = BufferHelper.getIntLE(this.packetBuffer, integer3, integer2);
                integer3 += 4;
                final int integer6 = BufferHelper.getIntLE(this.packetBuffer, integer3);
                integer3 += 4;
                switch (integer6) {
                    case 3: {
                        final String string7 = BufferHelper.getString(this.packetBuffer, integer3, integer2);
                        integer3 += string7.length();
                        if (!string7.isEmpty() && string7.equals(this.password)) {
                            this.authenticated = true;
                            this.a(integer5, 2, "");
                            continue;
                        }
                        this.authenticated = false;
                        this.f();
                        continue;
                    }
                    case 2: {
                        if (this.authenticated) {
                            final String string8 = BufferHelper.getString(this.packetBuffer, integer3, integer2);
                            try {
                                this.a(integer5, this.server.executeRconCommand(string8));
                            }
                            catch (Exception exception9) {
                                this.a(integer5, "Error executing: " + string8 + " (" + exception9.getMessage() + ")");
                            }
                            continue;
                        }
                        this.f();
                        continue;
                    }
                    default: {
                        this.a(integer5, String.format("Unknown request %s", Integer.toHexString(integer6)));
                        continue;
                    }
                }
            }
        }
        catch (SocketTimeoutException ex) {}
        catch (IOException ex2) {}
        catch (Exception exception10) {
            RconClient.LOGGER.error("Exception whilst parsing RCON input", (Throwable)exception10);
        }
        finally {
            this.close();
        }
    }
    
    private void a(final int integer1, final int integer2, final String string) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream(1248);
        final DataOutputStream dataOutputStream5 = new DataOutputStream(byteArrayOutputStream4);
        final byte[] arr6 = string.getBytes("UTF-8");
        dataOutputStream5.writeInt(Integer.reverseBytes(arr6.length + 10));
        dataOutputStream5.writeInt(Integer.reverseBytes(integer1));
        dataOutputStream5.writeInt(Integer.reverseBytes(integer2));
        dataOutputStream5.write(arr6);
        dataOutputStream5.write(0);
        dataOutputStream5.write(0);
        this.socket.getOutputStream().write(byteArrayOutputStream4.toByteArray());
    }
    
    private void f() throws IOException {
        this.a(-1, 2, "");
    }
    
    private void a(final int integer, String string) throws IOException {
        int integer2 = string.length();
        do {
            final int integer3 = (4096 <= integer2) ? 4096 : integer2;
            this.a(integer, 0, string.substring(0, integer3));
            string = string.substring(integer3);
            integer2 = string.length();
        } while (0 != integer2);
    }
    
    @Override
    public void stop() {
        super.stop();
        this.close();
    }
    
    private void close() {
        if (null == this.socket) {
            return;
        }
        try {
            this.socket.close();
        }
        catch (IOException iOException1) {
            this.warn("IO: " + iOException1.getMessage());
        }
        this.socket = null;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
