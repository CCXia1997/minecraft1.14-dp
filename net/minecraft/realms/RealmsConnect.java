package net.minecraft.realms;

import org.apache.logging.log4j.LogManager;
import java.net.UnknownHostException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.MinecraftClient;
import java.net.InetAddress;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsConnect
{
    private static final Logger LOGGER;
    private final RealmsScreen onlineScreen;
    private volatile boolean aborted;
    private ClientConnection connection;
    
    public RealmsConnect(final RealmsScreen realmsScreen) {
        this.onlineScreen = realmsScreen;
    }
    
    public void connect(final String string, final int integer) {
        Realms.setConnectedToRealms(true);
        Realms.narrateNow(Realms.getLocalizedString("mco.connect.success"));
        new Thread("Realms-connect-task") {
            @Override
            public void run() {
                InetAddress inetAddress1 = null;
                try {
                    inetAddress1 = InetAddress.getByName(string);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection = ClientConnection.connect(inetAddress1, integer, MinecraftClient.getInstance().options.shouldUseNativeTransport());
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.setPacketListener(new ClientLoginNetworkHandler(RealmsConnect.this.connection, MinecraftClient.getInstance(), RealmsConnect.this.onlineScreen.getProxy(), textComponent -> {}));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.send(new HandshakeC2SPacket(string, integer, NetworkState.LOGIN));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.send(new LoginHelloC2SPacket(MinecraftClient.getInstance().getSession().getProfile()));
                }
                catch (UnknownHostException unknownHostException2) {
                    Realms.clearResourcePack();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)unknownHostException2);
                    Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", new Object[] { "Unknown host '" + string + "'" })));
                }
                catch (Exception exception2) {
                    Realms.clearResourcePack();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)exception2);
                    String string3 = exception2.toString();
                    if (inetAddress1 != null) {
                        final String string4 = inetAddress1 + ":" + integer;
                        string3 = string3.replaceAll(string4, "");
                    }
                    Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableTextComponent("disconnect.genericReason", new Object[] { string3 })));
                }
            }
        }.start();
    }
    
    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isOpen()) {
            this.connection.disconnect(new TranslatableTextComponent("disconnect.genericReason", new Object[0]));
            this.connection.handleDisconnection();
        }
    }
    
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            }
            else {
                this.connection.handleDisconnection();
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
