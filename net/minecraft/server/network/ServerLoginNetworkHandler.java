package net.minecraft.server.network;

import org.apache.logging.log4j.LogManager;
import io.netty.channel.ChannelFuture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import java.security.PrivateKey;
import net.minecraft.util.UncaughtExceptionLogger;
import javax.annotation.Nullable;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.util.UUID;
import java.math.BigInteger;
import net.minecraft.network.NetworkEncryptionUtils;
import java.util.Arrays;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import org.apache.commons.lang3.Validate;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import javax.crypto.SecretKey;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import java.util.Random;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class ServerLoginNetworkHandler implements ServerLoginPacketListener
{
    private static final AtomicInteger authenticatorThreadId;
    private static final Logger LOGGER;
    private static final Random RANDOM;
    private final byte[] nonce;
    private final MinecraftServer server;
    public final ClientConnection client;
    private State state;
    private int loginTicks;
    private GameProfile profile;
    private final String j = "";
    private SecretKey secretKey;
    private ServerPlayerEntity clientEntity;
    
    public ServerLoginNetworkHandler(final MinecraftServer minecraftServer, final ClientConnection clientConnection) {
        this.nonce = new byte[4];
        this.state = State.a;
        this.server = minecraftServer;
        this.client = clientConnection;
        ServerLoginNetworkHandler.RANDOM.nextBytes(this.nonce);
    }
    
    public void tick() {
        if (this.state == State.e) {
            this.c();
        }
        else if (this.state == State.f) {
            final ServerPlayerEntity serverPlayerEntity1 = this.server.getPlayerManager().getPlayer(this.profile.getId());
            if (serverPlayerEntity1 == null) {
                this.state = State.e;
                this.server.getPlayerManager().onPlayerConnect(this.client, this.clientEntity);
                this.clientEntity = null;
            }
        }
        if (this.loginTicks++ == 600) {
            this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.slow_login", new Object[0]));
        }
    }
    
    public void disconnect(final TextComponent reason) {
        try {
            ServerLoginNetworkHandler.LOGGER.info("Disconnecting {}: {}", this.d(), reason.getString());
            this.client.send(new LoginDisconnectS2CPacket(reason));
            this.client.disconnect(reason);
        }
        catch (Exception exception2) {
            ServerLoginNetworkHandler.LOGGER.error("Error whilst disconnecting player", (Throwable)exception2);
        }
    }
    
    public void c() {
        if (!this.profile.isComplete()) {
            this.profile = this.toOfflineProfile(this.profile);
        }
        final TextComponent textComponent1 = this.server.getPlayerManager().checkCanJoin(this.client.getAddress(), this.profile);
        if (textComponent1 != null) {
            this.disconnect(textComponent1);
        }
        else {
            this.state = State.g;
            if (this.server.getNetworkCompressionThreshold() >= 0 && !this.client.isLocal()) {
                this.client.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), (channelFuture -> this.client.setMinCompressedSize(this.server.getNetworkCompressionThreshold())));
            }
            this.client.send(new LoginSuccessS2CPacket(this.profile));
            final ServerPlayerEntity serverPlayerEntity2 = this.server.getPlayerManager().getPlayer(this.profile.getId());
            if (serverPlayerEntity2 != null) {
                this.state = State.f;
                this.clientEntity = this.server.getPlayerManager().createPlayer(this.profile);
            }
            else {
                this.server.getPlayerManager().onPlayerConnect(this.client, this.server.getPlayerManager().createPlayer(this.profile));
            }
        }
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
        ServerLoginNetworkHandler.LOGGER.info("{} lost connection: {}", this.d(), reason.getString());
    }
    
    public String d() {
        if (this.profile != null) {
            return this.profile + " (" + this.client.getAddress() + ")";
        }
        return String.valueOf(this.client.getAddress());
    }
    
    @Override
    public void onHello(final LoginHelloC2SPacket loginHelloC2SPacket) {
        Validate.validState(this.state == State.a, "Unexpected hello packet", new Object[0]);
        this.profile = loginHelloC2SPacket.getProfile();
        if (this.server.isOnlineMode() && !this.client.isLocal()) {
            this.state = State.b;
            this.client.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic(), this.nonce));
        }
        else {
            this.state = State.e;
        }
    }
    
    @Override
    public void onKey(final LoginKeyC2SPacket loginKeyC2SPacket) {
        Validate.validState(this.state == State.b, "Unexpected key packet", new Object[0]);
        final PrivateKey privateKey2 = this.server.getKeyPair().getPrivate();
        if (!Arrays.equals(this.nonce, loginKeyC2SPacket.decryptNonce(privateKey2))) {
            throw new IllegalStateException("Invalid nonce!");
        }
        this.secretKey = loginKeyC2SPacket.decryptSecretKey(privateKey2);
        this.state = State.c;
        this.client.setupEncryption(this.secretKey);
        final Thread thread3 = new Thread("User Authenticator #" + ServerLoginNetworkHandler.authenticatorThreadId.incrementAndGet()) {
            @Override
            public void run() {
                final GameProfile gameProfile1 = ServerLoginNetworkHandler.this.profile;
                try {
                    final String string2 = new BigInteger(NetworkEncryptionUtils.generateServerId("", ServerLoginNetworkHandler.this.server.getKeyPair().getPublic(), ServerLoginNetworkHandler.this.secretKey)).toString(16);
                    ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server.getSessionService().hasJoinedServer(new GameProfile((UUID)null, gameProfile1.getName()), string2, this.a());
                    if (ServerLoginNetworkHandler.this.profile != null) {
                        ServerLoginNetworkHandler.LOGGER.info("UUID of player {} is {}", ServerLoginNetworkHandler.this.profile.getName(), ServerLoginNetworkHandler.this.profile.getId());
                        ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.e;
                    }
                    else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
                        ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile1);
                        ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.e;
                    }
                    else {
                        ServerLoginNetworkHandler.this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.unverified_username", new Object[0]));
                        ServerLoginNetworkHandler.LOGGER.error("Username '{}' tried to join with an invalid session", gameProfile1.getName());
                    }
                }
                catch (AuthenticationUnavailableException authenticationUnavailableException2) {
                    if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
                        ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile1);
                        ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.e;
                    }
                    else {
                        ServerLoginNetworkHandler.this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.authservers_down", new Object[0]));
                        ServerLoginNetworkHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                }
            }
            
            @Nullable
            private InetAddress a() {
                final SocketAddress socketAddress1 = ServerLoginNetworkHandler.this.client.getAddress();
                return (ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress1 instanceof InetSocketAddress) ? ((InetSocketAddress)socketAddress1).getAddress() : null;
            }
        };
        thread3.setUncaughtExceptionHandler(new UncaughtExceptionLogger(ServerLoginNetworkHandler.LOGGER));
        thread3.start();
    }
    
    @Override
    public void onQueryResponse(final LoginQueryResponseC2SPacket loginQueryResponseC2SPacket) {
        this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.unexpected_query_response", new Object[0]));
    }
    
    protected GameProfile toOfflineProfile(final GameProfile gameProfile) {
        final UUID uUID2 = PlayerEntity.getOfflinePlayerUuid(gameProfile.getName());
        return new GameProfile(uUID2, gameProfile.getName());
    }
    
    static {
        authenticatorThreadId = new AtomicInteger(0);
        LOGGER = LogManager.getLogger();
        RANDOM = new Random();
    }
    
    enum State
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g;
    }
}
