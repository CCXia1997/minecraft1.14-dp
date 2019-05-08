package net.minecraft.client.gui.menu;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.UncaughtExceptionLogger;
import java.net.UnknownHostException;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import java.net.InetAddress;
import net.minecraft.network.ServerAddress;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextComponent;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class ServerConnectingScreen extends Screen
{
    private static final AtomicInteger a;
    private static final Logger LOGGER;
    private ClientConnection connection;
    private boolean d;
    private final Screen parent;
    private TextComponent status;
    private long g;
    
    public ServerConnectingScreen(final Screen parent, final MinecraftClient minecraftClient, final ServerEntry serverEntry) {
        super(NarratorManager.a);
        this.status = new TranslatableTextComponent("connect.connecting", new Object[0]);
        this.g = -1L;
        this.minecraft = minecraftClient;
        this.parent = parent;
        final ServerAddress serverAddress4 = ServerAddress.parse(serverEntry.address);
        minecraftClient.disconnect();
        minecraftClient.setCurrentServerEntry(serverEntry);
        this.a(serverAddress4.getAddress(), serverAddress4.getPort());
    }
    
    public ServerConnectingScreen(final Screen screen, final MinecraftClient minecraftClient, final String string, final int integer) {
        super(NarratorManager.a);
        this.status = new TranslatableTextComponent("connect.connecting", new Object[0]);
        this.g = -1L;
        this.minecraft = minecraftClient;
        this.parent = screen;
        minecraftClient.disconnect();
        this.a(string, integer);
    }
    
    private void a(final String string, final int integer) {
        ServerConnectingScreen.LOGGER.info("Connecting to {}, {}", string, integer);
        final Thread thread3 = new Thread("Server Connector #" + ServerConnectingScreen.a.incrementAndGet()) {
            @Override
            public void run() {
                InetAddress inetAddress1 = null;
                try {
                    if (ServerConnectingScreen.this.d) {
                        return;
                    }
                    inetAddress1 = InetAddress.getByName(string);
                    ServerConnectingScreen.this.connection = ClientConnection.connect(inetAddress1, integer, ServerConnectingScreen.this.minecraft.options.shouldUseNativeTransport());
                    ServerConnectingScreen.this.connection.setPacketListener(new ClientLoginNetworkHandler(ServerConnectingScreen.this.connection, ServerConnectingScreen.this.minecraft, ServerConnectingScreen.this.parent, textComponent -> ServerConnectingScreen.this.setStatus(textComponent)));
                    ServerConnectingScreen.this.connection.send(new HandshakeC2SPacket(string, integer, NetworkState.LOGIN));
                    ServerConnectingScreen.this.connection.send(new LoginHelloC2SPacket(ServerConnectingScreen.this.minecraft.getSession().getProfile()));
                }
                catch (UnknownHostException unknownHostException2) {
                    if (ServerConnectingScreen.this.d) {
                        return;
                    }
                    ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)unknownHostException2);
                    final MinecraftClient minecraft;
                    final TranslatableTextComponent reason;
                    final Screen screen;
                    final Screen parent;
                    final String title;
                    ServerConnectingScreen.this.minecraft.execute(() -> {
                        minecraft = ServerConnectingScreen.this.minecraft;
                        // new(net.minecraft.client.gui.menu.DisconnectedScreen.class)
                        ServerConnectingScreen.this.parent;
                        new TranslatableTextComponent("disconnect.genericReason", new Object[] { "Unknown host" });
                        new DisconnectedScreen(parent, title, reason);
                        minecraft.openScreen(screen);
                    });
                }
                catch (Exception exception2) {
                    if (ServerConnectingScreen.this.d) {
                        return;
                    }
                    ServerConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)exception2);
                    final String string3 = (inetAddress1 == null) ? exception2.toString() : exception2.toString().replaceAll(inetAddress1 + ":" + integer, "");
                    final MinecraftClient minecraft2;
                    final TextComponent reason2;
                    final Object o;
                    final Screen screen2;
                    final Screen parent2;
                    final String title2;
                    ServerConnectingScreen.this.minecraft.execute(() -> {
                        minecraft2 = ServerConnectingScreen.this.minecraft;
                        // new(net.minecraft.client.gui.menu.DisconnectedScreen.class)
                        ServerConnectingScreen.this.parent;
                        new TranslatableTextComponent("disconnect.genericReason", new Object[] { o });
                        new DisconnectedScreen(parent2, title2, reason2);
                        minecraft2.openScreen(screen2);
                    });
                }
            }
        };
        thread3.setUncaughtExceptionHandler(new UncaughtExceptionLogger(ServerConnectingScreen.LOGGER));
        thread3.start();
    }
    
    private void setStatus(final TextComponent textComponent) {
        this.status = textComponent;
    }
    
    @Override
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
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    protected void init() {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> {
            this.d = true;
            if (this.connection != null) {
                this.connection.disconnect(new TranslatableTextComponent("connect.aborted", new Object[0]));
            }
            this.minecraft.openScreen(this.parent);
        }));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        final long long4 = SystemUtil.getMeasuringTimeMs();
        if (long4 - this.g > 2000L) {
            this.g = long4;
            NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.joining", new Object[0]).getString());
        }
        this.drawCenteredString(this.font, this.status.getFormattedText(), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        a = new AtomicInteger(0);
        LOGGER = LogManager.getLogger();
    }
}
