package net.minecraft.client.network;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.gui.menu.DisconnectedScreen;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.NetworkState;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import net.minecraft.network.Packet;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import java.math.BigInteger;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.TextComponent;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ClientLoginPacketListener;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketListener
{
    private static final Logger LOGGER;
    private final MinecraftClient client;
    @Nullable
    private final Screen parentGui;
    private final Consumer<TextComponent> statusConsumer;
    private final ClientConnection connection;
    private GameProfile profile;
    
    public ClientLoginNetworkHandler(final ClientConnection connection, final MinecraftClient client, @Nullable final Screen parentGui, final Consumer<TextComponent> statusConsumer) {
        this.connection = connection;
        this.client = client;
        this.parentGui = parentGui;
        this.statusConsumer = statusConsumer;
    }
    
    @Override
    public void onHello(final LoginHelloS2CPacket packet) {
        final SecretKey secretKey2 = NetworkEncryptionUtils.generateKey();
        final PublicKey publicKey3 = packet.getPublicKey();
        final String string4 = new BigInteger(NetworkEncryptionUtils.generateServerId(packet.getServerId(), publicKey3, secretKey2)).toString(16);
        final LoginKeyC2SPacket loginKeyC2SPacket5 = new LoginKeyC2SPacket(secretKey2, publicKey3, packet.getNonce());
        this.statusConsumer.accept(new TranslatableTextComponent("connect.authorizing", new Object[0]));
        final String serverId;
        final TextComponent textComponent4;
        final Packet<?> packet2;
        final Object o;
        NetworkUtils.downloadExecutor.submit(() -> {
            textComponent4 = this.joinServerSession(serverId);
            if (textComponent4 != null) {
                if (this.client.getCurrentServerEntry() != null && this.client.getCurrentServerEntry().isLocal()) {
                    ClientLoginNetworkHandler.LOGGER.warn(textComponent4.getString());
                }
                else {
                    this.connection.disconnect(textComponent4);
                    return;
                }
            }
            this.statusConsumer.accept(new TranslatableTextComponent("connect.encrypting", new Object[0]));
            this.connection.send(packet2, (future -> this.connection.setupEncryption(o)));
        });
    }
    
    @Nullable
    private TextComponent joinServerSession(final String serverId) {
        try {
            this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), serverId);
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException2) {
            return new TranslatableTextComponent("disconnect.loginFailedInfo", new Object[] { new TranslatableTextComponent("disconnect.loginFailedInfo.serversUnavailable", new Object[0]) });
        }
        catch (InvalidCredentialsException invalidCredentialsException2) {
            return new TranslatableTextComponent("disconnect.loginFailedInfo", new Object[] { new TranslatableTextComponent("disconnect.loginFailedInfo.invalidSession", new Object[0]) });
        }
        catch (AuthenticationException authenticationException2) {
            return new TranslatableTextComponent("disconnect.loginFailedInfo", new Object[] { authenticationException2.getMessage() });
        }
        return null;
    }
    
    private MinecraftSessionService getSessionService() {
        return this.client.getSessionService();
    }
    
    @Override
    public void onLoginSuccess(final LoginSuccessS2CPacket loginSuccessS2CPacket) {
        this.statusConsumer.accept(new TranslatableTextComponent("connect.joining", new Object[0]));
        this.profile = loginSuccessS2CPacket.getProfile();
        this.connection.setState(NetworkState.PLAY);
        this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentGui, this.connection, this.profile));
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
        if (this.parentGui != null && this.parentGui instanceof RealmsScreenProxy) {
            this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.parentGui).getScreen(), "connect.failed", reason).getProxy());
        }
        else {
            this.client.openScreen(new DisconnectedScreen(this.parentGui, "connect.failed", reason));
        }
    }
    
    @Override
    public void onDisconnect(final LoginDisconnectS2CPacket packet) {
        this.connection.disconnect(packet.getReason());
    }
    
    @Override
    public void onCompression(final LoginCompressionS2CPacket packet) {
        if (!this.connection.isLocal()) {
            this.connection.setMinCompressedSize(packet.getCompressionThreshold());
        }
    }
    
    @Override
    public void onQueryRequest(final LoginQueryRequestS2CPacket packet) {
        this.statusConsumer.accept(new TranslatableTextComponent("connect.negotiating", new Object[0]));
        this.connection.send(new LoginQueryResponseC2SPacket(packet.getQueryId(), null));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
