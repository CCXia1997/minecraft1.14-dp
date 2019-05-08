package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.texture.Texture;
import org.apache.commons.lang3.Validate;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.SharedConstants;
import java.net.UnknownHostException;
import net.minecraft.text.TextFormat;
import com.google.common.hash.Hashing;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.resource.language.I18n;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import net.minecraft.util.UncaughtExceptionLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.widget.EntryListWidget;
import java.util.Iterator;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.util.NarratorManager;
import java.util.function.Consumer;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.minecraft.util.Identifier;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends AlwaysSelectedEntryListWidget<Entry>
{
    private static final Logger LOGGER;
    private static final ThreadPoolExecutor b;
    private static final Identifier c;
    private static final Identifier d;
    private final MultiplayerScreen e;
    private final List<ServerItem> f;
    private final Entry g;
    private final List<LanServerListEntry> h;
    
    public MultiplayerServerListWidget(final MultiplayerScreen multiplayerScreen, final MinecraftClient minecraftClient, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7) {
        super(minecraftClient, integer3, integer4, integer5, integer6, integer7);
        this.f = Lists.newArrayList();
        this.g = new b();
        this.h = Lists.newArrayList();
        this.e = multiplayerScreen;
    }
    
    private void e() {
        this.clearEntries();
        this.f.forEach(this::addEntry);
        this.addEntry(this.g);
        this.h.forEach(this::addEntry);
    }
    
    public void a(final Entry entry) {
        super.setSelected(entry);
        if (this.getSelected() instanceof ServerItem) {
            NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.select", new Object[] { ((EntryListWidget<ServerItem>)this).getSelected().server.name }).getString());
        }
    }
    
    @Override
    protected void moveSelection(final int amount) {
        final int integer2 = this.children().indexOf(((EntryListWidget<Object>)this).getSelected());
        final int integer3 = MathHelper.clamp(integer2 + amount, 0, this.getItemCount() - 1);
        final Entry entry4 = this.children().get(integer3);
        super.setSelected(entry4);
        if (!(entry4 instanceof b)) {
            this.ensureVisible(entry4);
            this.e.updateButtonActivationStates();
            return;
        }
        if (amount > 0 && integer3 == this.getItemCount() - 1) {
            return;
        }
        if (amount < 0 && integer3 == 0) {
            return;
        }
        this.moveSelection(amount);
    }
    
    public void a(final ServerList serverList) {
        this.f.clear();
        for (int integer2 = 0; integer2 < serverList.size(); ++integer2) {
            this.f.add(new ServerItem(this.e, serverList.get(integer2)));
        }
        this.e();
    }
    
    public void a(final List<LanServerEntry> list) {
        this.h.clear();
        for (final LanServerEntry lanServerEntry3 : list) {
            this.h.add(new LanServerListEntry(this.e, lanServerEntry3));
        }
        this.e();
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }
    
    @Override
    protected boolean isFocused() {
        return this.e.getFocused() == this;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        b = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(MultiplayerServerListWidget.LOGGER)).build());
        c = new Identifier("textures/misc/unknown_server.png");
        d = new Identifier("textures/gui/server_selection.png");
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry>
    {
    }
    
    @Environment(EnvType.CLIENT)
    public static class b extends Entry
    {
        private final MinecraftClient a;
        
        public b() {
            this.a = MinecraftClient.getInstance();
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            final int n = integer2 + height / 2;
            this.a.textRenderer.getClass();
            final int integer4 = n - 9 / 2;
            this.a.textRenderer.draw(I18n.translate("lanServer.scanning"), (float)(this.a.currentScreen.width / 2 - this.a.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2), (float)integer4, 16777215);
            String string11 = null;
            switch ((int)(SystemUtil.getMeasuringTimeMs() / 300L % 4L)) {
                default: {
                    string11 = "O o o";
                    break;
                }
                case 1:
                case 3: {
                    string11 = "o O o";
                    break;
                }
                case 2: {
                    string11 = "o o O";
                    break;
                }
            }
            final TextRenderer textRenderer = this.a.textRenderer;
            final String string12 = string11;
            final float x = (float)(this.a.currentScreen.width / 2 - this.a.textRenderer.getStringWidth(string11) / 2);
            final int n2 = integer4;
            this.a.textRenderer.getClass();
            textRenderer.draw(string12, x, (float)(n2 + 9), 8421504);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class LanServerListEntry extends Entry
    {
        private final MultiplayerScreen screen;
        protected final MinecraftClient client;
        protected final LanServerEntry server;
        private long time;
        
        protected LanServerListEntry(final MultiplayerScreen multiplayerScreen, final LanServerEntry lanServerEntry) {
            this.screen = multiplayerScreen;
            this.server = lanServerEntry;
            this.client = MinecraftClient.getInstance();
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            this.client.textRenderer.draw(I18n.translate("lanServer.title"), (float)(integer3 + 32 + 3), (float)(integer2 + 1), 16777215);
            this.client.textRenderer.draw(this.server.getMotd(), (float)(integer3 + 32 + 3), (float)(integer2 + 12), 8421504);
            if (this.client.options.hideServerAddress) {
                this.client.textRenderer.draw(I18n.translate("selectServer.hiddenAddress"), (float)(integer3 + 32 + 3), (float)(integer2 + 12 + 11), 3158064);
            }
            else {
                this.client.textRenderer.draw(this.server.getAddressPort(), (float)(integer3 + 32 + 3), (float)(integer2 + 12 + 11), 3158064);
            }
        }
        
        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            this.screen.selectEntry(this);
            if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = SystemUtil.getMeasuringTimeMs();
            return false;
        }
        
        public LanServerEntry getLanServerEntry() {
            return this.server;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public class ServerItem extends Entry
    {
        private final MultiplayerScreen screen;
        private final MinecraftClient client;
        private final ServerEntry server;
        private final Identifier iconLocation;
        private String f;
        private NativeImageBackedTexture iconTexture;
        private long time;
        
        protected ServerItem(final MultiplayerScreen screen, final ServerEntry server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
            this.iconLocation = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(server.address) + "/icon");
            this.iconTexture = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconLocation);
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            if (!this.server.online) {
                this.server.online = true;
                this.server.ping = -2L;
                this.server.label = "";
                this.server.playerCountLabel = "";
                MultiplayerServerListWidget.b.submit(() -> {
                    try {
                        this.screen.c().a(this.server);
                    }
                    catch (UnknownHostException unknownHostException1) {
                        this.server.ping = -1L;
                        this.server.label = TextFormat.e + I18n.translate("multiplayer.status.cannot_resolve");
                    }
                    catch (Exception exception1) {
                        this.server.ping = -1L;
                        this.server.label = TextFormat.e + I18n.translate("multiplayer.status.cannot_connect");
                    }
                    return;
                });
            }
            final boolean boolean10 = this.server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
            final boolean boolean11 = this.server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
            final boolean boolean12 = boolean10 || boolean11;
            this.client.textRenderer.draw(this.server.name, (float)(integer3 + 32 + 3), (float)(integer2 + 1), 16777215);
            final List<String> list13 = this.client.textRenderer.wrapStringToWidthAsList(this.server.label, width - 32 - 2);
            for (int integer4 = 0; integer4 < Math.min(list13.size(), 2); ++integer4) {
                final TextRenderer textRenderer = this.client.textRenderer;
                final String string17 = list13.get(integer4);
                final float x = (float)(integer3 + 32 + 3);
                final int n = integer2 + 12;
                this.client.textRenderer.getClass();
                textRenderer.draw(string17, x, (float)(n + 9 * integer4), 8421504);
            }
            final String string14 = boolean12 ? (TextFormat.e + this.server.version) : this.server.playerCountLabel;
            final int integer5 = this.client.textRenderer.getStringWidth(string14);
            this.client.textRenderer.draw(string14, (float)(integer3 + width - integer5 - 15 - 2), (float)(integer2 + 1), 8421504);
            int integer6 = 0;
            String string15 = null;
            int integer7;
            String string16;
            if (boolean12) {
                integer7 = 5;
                string16 = I18n.translate(boolean10 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
                string15 = this.server.playerListSummary;
            }
            else if (this.server.online && this.server.ping != -2L) {
                if (this.server.ping < 0L) {
                    integer7 = 5;
                }
                else if (this.server.ping < 150L) {
                    integer7 = 0;
                }
                else if (this.server.ping < 300L) {
                    integer7 = 1;
                }
                else if (this.server.ping < 600L) {
                    integer7 = 2;
                }
                else if (this.server.ping < 1000L) {
                    integer7 = 3;
                }
                else {
                    integer7 = 4;
                }
                if (this.server.ping < 0L) {
                    string16 = I18n.translate("multiplayer.status.no_connection");
                }
                else {
                    string16 = this.server.ping + "ms";
                    string15 = this.server.playerListSummary;
                }
            }
            else {
                integer6 = 1;
                integer7 = (int)(SystemUtil.getMeasuringTimeMs() / 100L + index * 2 & 0x7L);
                if (integer7 > 4) {
                    integer7 = 8 - integer7;
                }
                string16 = I18n.translate("multiplayer.status.pinging");
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
            DrawableHelper.blit(integer3 + width - 15, integer2, (float)(integer6 * 10), (float)(176 + integer7 * 8), 10, 8, 256, 256);
            if (this.server.getIcon() != null && !this.server.getIcon().equals(this.f)) {
                this.f = this.server.getIcon();
                this.c();
                this.screen.getServerList().saveFile();
            }
            if (this.iconTexture != null) {
                this.a(integer3, integer2, this.iconLocation);
            }
            else {
                this.a(integer3, integer2, MultiplayerServerListWidget.c);
            }
            final int integer8 = mouseX - integer3;
            final int integer9 = mouseY - integer2;
            if (integer8 >= width - 15 && integer8 <= width - 5 && integer9 >= 0 && integer9 <= 8) {
                this.screen.setTooltip(string16);
            }
            else if (integer8 >= width - integer5 - 15 - 2 && integer8 <= width - 15 - 2 && integer9 >= 0 && integer9 <= 8) {
                this.screen.setTooltip(string15);
            }
            if (this.client.options.touchscreen || hovering) {
                this.client.getTextureManager().bindTexture(MultiplayerServerListWidget.d);
                DrawableHelper.fill(integer3, integer2, integer3 + 32, integer2 + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int integer10 = mouseX - integer3;
                final int integer11 = mouseY - integer2;
                if (this.b()) {
                    if (integer10 < 32 && integer10 > 16) {
                        DrawableHelper.blit(integer3, integer2, 0.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        DrawableHelper.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (index > 0) {
                    if (integer10 < 16 && integer11 < 16) {
                        DrawableHelper.blit(integer3, integer2, 96.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        DrawableHelper.blit(integer3, integer2, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (index < this.screen.getServerList().size() - 1) {
                    if (integer10 < 16 && integer11 > 16) {
                        DrawableHelper.blit(integer3, integer2, 64.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        DrawableHelper.blit(integer3, integer2, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }
        
        protected void a(final int integer1, final int integer2, final Identifier identifier) {
            this.client.getTextureManager().bindTexture(identifier);
            GlStateManager.enableBlend();
            DrawableHelper.blit(integer1, integer2, 0.0f, 0.0f, 32, 32, 32, 32);
            GlStateManager.disableBlend();
        }
        
        private boolean b() {
            return true;
        }
        
        private void c() {
            final String string1 = this.server.getIcon();
            if (string1 == null) {
                this.client.getTextureManager().destroyTexture(this.iconLocation);
                if (this.iconTexture != null && this.iconTexture.getImage() != null) {
                    this.iconTexture.getImage().close();
                }
                this.iconTexture = null;
            }
            else {
                try {
                    final NativeImage nativeImage2 = NativeImage.fromBase64(string1);
                    Validate.validState(nativeImage2.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(nativeImage2.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    if (this.iconTexture == null) {
                        this.iconTexture = new NativeImageBackedTexture(nativeImage2);
                    }
                    else {
                        this.iconTexture.setImage(nativeImage2);
                        this.iconTexture.upload();
                    }
                    this.client.getTextureManager().registerTexture(this.iconLocation, this.iconTexture);
                }
                catch (Throwable throwable2) {
                    MultiplayerServerListWidget.LOGGER.error("Invalid icon for server {} ({})", this.server.name, this.server.address, throwable2);
                    this.server.setIcon(null);
                }
            }
        }
        
        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            final double double6 = mouseX - EntryListWidget.this.getRowLeft();
            final double double7 = mouseY - EntryListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
            if (double6 <= 32.0) {
                if (double6 < 32.0 && double6 > 16.0 && this.b()) {
                    this.screen.selectEntry(this);
                    this.screen.connect();
                    return true;
                }
                final int integer10 = this.screen.serverListWidget.children().indexOf(this);
                if (double6 < 16.0 && double7 < 16.0 && integer10 > 0) {
                    final int integer11 = Screen.hasShiftDown() ? 0 : (integer10 - 1);
                    this.screen.getServerList().swapEntries(integer10, integer11);
                    if (this.screen.serverListWidget.getSelected() == this) {
                        this.screen.selectEntry(this);
                    }
                    this.screen.serverListWidget.a(this.screen.getServerList());
                    return true;
                }
                if (double6 < 16.0 && double7 > 16.0 && integer10 < this.screen.getServerList().size() - 1) {
                    final ServerList serverList11 = this.screen.getServerList();
                    final int integer12 = Screen.hasShiftDown() ? (serverList11.size() - 1) : (integer10 + 1);
                    serverList11.swapEntries(integer10, integer12);
                    if (this.screen.serverListWidget.getSelected() == this) {
                        this.screen.selectEntry(this);
                    }
                    this.screen.serverListWidget.a(serverList11);
                    return true;
                }
            }
            this.screen.selectEntry(this);
            if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = SystemUtil.getMeasuringTimeMs();
            return false;
        }
        
        public ServerEntry getServer() {
            return this.server;
        }
    }
}
