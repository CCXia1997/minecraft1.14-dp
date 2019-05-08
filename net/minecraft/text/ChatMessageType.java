package net.minecraft.text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ChatMessageType
{
    a((byte)0, false), 
    b((byte)1, true), 
    c((byte)2, true);
    
    private final byte id;
    private final boolean interruptsNarration;
    
    private ChatMessageType(final byte id, final boolean boolean2) {
        this.id = id;
        this.interruptsNarration = boolean2;
    }
    
    public byte getId() {
        return this.id;
    }
    
    public static ChatMessageType byId(final byte id) {
        for (final ChatMessageType chatMessageType5 : values()) {
            if (id == chatMessageType5.id) {
                return chatMessageType5;
            }
        }
        return ChatMessageType.a;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean interruptsNarration() {
        return this.interruptsNarration;
    }
}
