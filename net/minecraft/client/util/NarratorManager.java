package net.minecraft.client.util;

import org.apache.logging.log4j.LogManager;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.SharedConstants;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ChatMessageType;
import com.mojang.text2speech.Narrator;
import org.apache.logging.log4j.Logger;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ClientChatListener;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener
{
    public static final TextComponent a;
    private static final Logger c;
    public static final NarratorManager INSTANCE;
    private final Narrator narrator;
    
    public NarratorManager() {
        this.narrator = Narrator.getNarrator();
    }
    
    @Override
    public void onChatMessage(final ChatMessageType messageType, final TextComponent message) {
        final NarratorOption narratorOption3 = MinecraftClient.getInstance().options.narrator;
        if (narratorOption3 == NarratorOption.a || !this.narrator.active()) {
            return;
        }
        if (narratorOption3 == NarratorOption.b || (narratorOption3 == NarratorOption.c && messageType == ChatMessageType.a) || (narratorOption3 == NarratorOption.d && messageType == ChatMessageType.b)) {
            TextComponent textComponent4;
            if (message instanceof TranslatableTextComponent && "chat.type.text".equals(((TranslatableTextComponent)message).getKey())) {
                textComponent4 = new TranslatableTextComponent("chat.type.text.narrate", ((TranslatableTextComponent)message).getParams());
            }
            else {
                textComponent4 = message;
            }
            this.narrate(messageType.interruptsNarration(), textComponent4.getString());
        }
    }
    
    public void a(final String string) {
        final NarratorOption narratorOption2 = MinecraftClient.getInstance().options.narrator;
        if (this.narrator.active() && narratorOption2 != NarratorOption.a && narratorOption2 != NarratorOption.c && !string.isEmpty()) {
            this.narrator.clear();
            this.narrate(true, string);
        }
    }
    
    private void narrate(final boolean interrupt, final String message) {
        if (SharedConstants.isDevelopment) {
            NarratorManager.c.debug("Narrating: {}", message);
        }
        this.narrator.say(message, interrupt);
    }
    
    public void addToast(final NarratorOption narratorOption) {
        this.narrator.clear();
        this.narrator.say(new TranslatableTextComponent("options.narrator", new Object[0]).getString() + " : " + new TranslatableTextComponent(narratorOption.getTranslationKey(), new Object[0]).getString(), true);
        final ToastManager toastManager2 = MinecraftClient.getInstance().getToastManager();
        if (this.narrator.active()) {
            if (narratorOption == NarratorOption.a) {
                SystemToast.show(toastManager2, SystemToast.Type.b, new TranslatableTextComponent("narrator.toast.disabled", new Object[0]), null);
            }
            else {
                SystemToast.show(toastManager2, SystemToast.Type.b, new TranslatableTextComponent("narrator.toast.enabled", new Object[0]), new TranslatableTextComponent(narratorOption.getTranslationKey(), new Object[0]));
            }
        }
        else {
            SystemToast.show(toastManager2, SystemToast.Type.b, new TranslatableTextComponent("narrator.toast.disabled", new Object[0]), new TranslatableTextComponent("options.narrator.notavailable", new Object[0]));
        }
    }
    
    public boolean isActive() {
        return this.narrator.active();
    }
    
    public void clear() {
        this.narrator.clear();
    }
    
    public void c() {
        this.narrator.destroy();
    }
    
    static {
        a = new StringTextComponent("");
        c = LogManager.getLogger();
        INSTANCE = new NarratorManager();
    }
}
