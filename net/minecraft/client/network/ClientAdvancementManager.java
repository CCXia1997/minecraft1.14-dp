package net.minecraft.client.network;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import java.util.Iterator;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.Advancement;
import java.util.Map;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientAdvancementManager
{
    private static final Logger LOGGER;
    private final MinecraftClient client;
    private final AdvancementManager manager;
    private final Map<Advancement, AdvancementProgress> advancementProgresses;
    @Nullable
    private Listener listener;
    @Nullable
    private Advancement selectedTab;
    
    public ClientAdvancementManager(final MinecraftClient minecraftClient) {
        this.manager = new AdvancementManager();
        this.advancementProgresses = Maps.newHashMap();
        this.client = minecraftClient;
    }
    
    public void onAdvancements(final AdvancementUpdateS2CPacket advancementUpdateS2CPacket) {
        if (advancementUpdateS2CPacket.shouldClearCurrent()) {
            this.manager.clear();
            this.advancementProgresses.clear();
        }
        this.manager.removeAll(advancementUpdateS2CPacket.getAdvancementIdsToRemove());
        this.manager.load(advancementUpdateS2CPacket.getAdvancementsToEarn());
        for (final Map.Entry<Identifier, AdvancementProgress> entry3 : advancementUpdateS2CPacket.getAdvancementsToProgress().entrySet()) {
            final Advancement advancement4 = this.manager.get(entry3.getKey());
            if (advancement4 != null) {
                final AdvancementProgress advancementProgress5 = entry3.getValue();
                advancementProgress5.init(advancement4.getCriteria(), advancement4.getRequirements());
                this.advancementProgresses.put(advancement4, advancementProgress5);
                if (this.listener != null) {
                    this.listener.setProgress(advancement4, advancementProgress5);
                }
                if (advancementUpdateS2CPacket.shouldClearCurrent() || !advancementProgress5.isDone() || advancement4.getDisplay() == null || !advancement4.getDisplay().shouldShowToast()) {
                    continue;
                }
                this.client.getToastManager().add(new AdvancementToast(advancement4));
            }
            else {
                ClientAdvancementManager.LOGGER.warn("Server informed client about progress for unknown advancement {}", entry3.getKey());
            }
        }
    }
    
    public AdvancementManager getManager() {
        return this.manager;
    }
    
    public void selectTab(@Nullable final Advancement advancement, final boolean boolean2) {
        final ClientPlayNetworkHandler clientPlayNetworkHandler3 = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler3 != null && advancement != null && boolean2) {
            clientPlayNetworkHandler3.sendPacket(AdvancementTabC2SPacket.open(advancement));
        }
        if (this.selectedTab != advancement) {
            this.selectedTab = advancement;
            if (this.listener != null) {
                this.listener.selectTab(advancement);
            }
        }
    }
    
    public void setListener(@Nullable final Listener listener) {
        this.listener = listener;
        this.manager.setListener(listener);
        if (listener != null) {
            for (final Map.Entry<Advancement, AdvancementProgress> entry3 : this.advancementProgresses.entrySet()) {
                listener.setProgress(entry3.getKey(), entry3.getValue());
            }
            listener.selectTab(this.selectedTab);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public interface Listener extends AdvancementManager.Listener
    {
        void setProgress(final Advancement arg1, final AdvancementProgress arg2);
        
        void selectTab(@Nullable final Advancement arg1);
    }
}
