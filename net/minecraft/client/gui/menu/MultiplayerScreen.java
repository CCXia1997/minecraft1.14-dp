package net.minecraft.client.gui.menu;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import net.minecraft.client.network.LanServerEntry;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.sortme.ServerEntryNetworkPart;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen
{
    private static final Logger LOGGER;
    private final ServerEntryNetworkPart c;
    private final Screen parent;
    protected MultiplayerServerListWidget serverListWidget;
    private ServerList serverList;
    private ButtonWidget buttonEdit;
    private ButtonWidget buttonJoin;
    private ButtonWidget buttonDelete;
    private String tooltipText;
    private ServerEntry selectedEntry;
    private LanServerQueryManager.LanServerEntryList lanServers;
    private LanServerQueryManager.LanServerDetector lanServerDetector;
    private boolean initialized;
    
    public MultiplayerScreen(final Screen parent) {
        super(new TranslatableTextComponent("multiplayer.title", new Object[0]));
        this.c = new ServerEntryNetworkPart();
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboard.enableRepeatEvents(true);
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        }
        else {
            this.initialized = true;
            (this.serverList = new ServerList(this.minecraft)).loadFile();
            this.lanServers = new LanServerQueryManager.LanServerEntryList();
            try {
                (this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers)).start();
            }
            catch (Exception exception1) {
                MultiplayerScreen.LOGGER.warn("Unable to start LAN server detection: {}", exception1.getMessage());
            }
            (this.serverListWidget = new MultiplayerServerListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36)).a(this.serverList);
        }
        this.children.add(this.serverListWidget);
        this.buttonJoin = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select"), buttonWidget -> this.connect()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct"), buttonWidget -> {
            this.selectedEntry = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
            this.minecraft.openScreen(new DirectConnectServerScreen(this::directConnect, this.selectedEntry));
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add"), buttonWidget -> {
            this.selectedEntry = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
            this.minecraft.openScreen(new AddServerScreen(this::addEntry, this.selectedEntry));
            return;
        }));
        final MultiplayerServerListWidget.Entry entry2;
        ServerEntry serverEntry3;
        this.buttonEdit = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit"), buttonWidget -> {
            entry2 = this.serverListWidget.getSelected();
            if (entry2 instanceof MultiplayerServerListWidget.ServerItem) {
                serverEntry3 = ((MultiplayerServerListWidget.ServerItem)entry2).getServer();
                (this.selectedEntry = new ServerEntry(serverEntry3.name, serverEntry3.address, false)).copyFrom(serverEntry3);
                this.minecraft.openScreen(new AddServerScreen(this::editEntry, this.selectedEntry));
            }
            return;
        }));
        final MultiplayerServerListWidget.Entry entry3;
        String string3;
        TextComponent textComponent4;
        final TranslatableTextComponent translatableTextComponent;
        TextComponent textComponent5;
        String string4;
        String string5;
        this.buttonDelete = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete"), buttonWidget -> {
            entry3 = this.serverListWidget.getSelected();
            if (entry3 instanceof MultiplayerServerListWidget.ServerItem) {
                string3 = ((MultiplayerServerListWidget.ServerItem)entry3).getServer().name;
                if (string3 != null) {
                    textComponent4 = new TranslatableTextComponent("selectServer.deleteQuestion", new Object[0]);
                    new TranslatableTextComponent("selectServer.deleteWarning", new Object[] { string3 });
                    textComponent5 = translatableTextComponent;
                    string4 = I18n.translate("selectServer.deleteButton");
                    string5 = I18n.translate("gui.cancel");
                    this.minecraft.openScreen(new YesNoScreen(this::removeEntry, textComponent4, textComponent5, string4, string5));
                }
            }
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh"), buttonWidget -> this.refresh()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.updateButtonActivationStates();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.lanServers.needsUpdate()) {
            final List<LanServerEntry> list1 = this.lanServers.getServers();
            this.lanServers.markClean();
            this.serverListWidget.a(list1);
        }
        this.c.a();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.c.b();
    }
    
    private void refresh() {
        this.minecraft.openScreen(new MultiplayerScreen(this.parent));
    }
    
    private void removeEntry(final boolean confirmedAction) {
        final MultiplayerServerListWidget.Entry entry2 = this.serverListWidget.getSelected();
        if (confirmedAction && entry2 instanceof MultiplayerServerListWidget.ServerItem) {
            this.serverList.remove(((MultiplayerServerListWidget.ServerItem)entry2).getServer());
            this.serverList.saveFile();
            this.serverListWidget.a((MultiplayerServerListWidget.Entry)null);
            this.serverListWidget.a(this.serverList);
        }
        this.minecraft.openScreen(this);
    }
    
    private void editEntry(final boolean confirmedAction) {
        final MultiplayerServerListWidget.Entry entry2 = this.serverListWidget.getSelected();
        if (confirmedAction && entry2 instanceof MultiplayerServerListWidget.ServerItem) {
            final ServerEntry serverEntry3 = ((MultiplayerServerListWidget.ServerItem)entry2).getServer();
            serverEntry3.name = this.selectedEntry.name;
            serverEntry3.address = this.selectedEntry.address;
            serverEntry3.copyFrom(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.a(this.serverList);
        }
        this.minecraft.openScreen(this);
    }
    
    private void addEntry(final boolean confirmedAction) {
        if (confirmedAction) {
            this.serverList.add(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.a((MultiplayerServerListWidget.Entry)null);
            this.serverListWidget.a(this.serverList);
        }
        this.minecraft.openScreen(this);
    }
    
    private void directConnect(final boolean confirmedAction) {
        if (confirmedAction) {
            this.connect(this.selectedEntry);
        }
        else {
            this.minecraft.openScreen(this);
        }
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 294) {
            this.refresh();
            return true;
        }
        if (this.serverListWidget.getSelected() != null && (keyCode == 257 || keyCode == 335)) {
            this.connect();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.tooltipText = null;
        this.renderBackground();
        this.serverListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.tooltipText)), mouseX, mouseY);
        }
    }
    
    public void connect() {
        final MultiplayerServerListWidget.Entry entry1 = this.serverListWidget.getSelected();
        if (entry1 instanceof MultiplayerServerListWidget.ServerItem) {
            this.connect(((MultiplayerServerListWidget.ServerItem)entry1).getServer());
        }
        else if (entry1 instanceof MultiplayerServerListWidget.LanServerListEntry) {
            final LanServerEntry lanServerEntry2 = ((MultiplayerServerListWidget.LanServerListEntry)entry1).getLanServerEntry();
            this.connect(new ServerEntry(lanServerEntry2.getMotd(), lanServerEntry2.getAddressPort(), true));
        }
    }
    
    private void connect(final ServerEntry entry) {
        this.minecraft.openScreen(new ServerConnectingScreen(this, this.minecraft, entry));
    }
    
    public void selectEntry(final MultiplayerServerListWidget.Entry entry) {
        this.serverListWidget.a(entry);
        this.updateButtonActivationStates();
    }
    
    protected void updateButtonActivationStates() {
        this.buttonJoin.active = false;
        this.buttonEdit.active = false;
        this.buttonDelete.active = false;
        final MultiplayerServerListWidget.Entry entry1 = this.serverListWidget.getSelected();
        if (entry1 != null && !(entry1 instanceof MultiplayerServerListWidget.b)) {
            this.buttonJoin.active = true;
            if (entry1 instanceof MultiplayerServerListWidget.ServerItem) {
                this.buttonEdit.active = true;
                this.buttonDelete.active = true;
            }
        }
    }
    
    public ServerEntryNetworkPart c() {
        return this.c;
    }
    
    public void setTooltip(final String text) {
        this.tooltipText = text;
    }
    
    public ServerList getServerList() {
        return this.serverList;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
