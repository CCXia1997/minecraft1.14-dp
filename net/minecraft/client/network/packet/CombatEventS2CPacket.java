package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class CombatEventS2CPacket implements Packet<ClientPlayPacketListener>
{
    public Type type;
    public int entityId;
    public int attackerEntityId;
    public int timeSinceLastAttack;
    public TextComponent deathMessage;
    
    public CombatEventS2CPacket() {
    }
    
    public CombatEventS2CPacket(final DamageTracker damageTracker, final Type type) {
        this(damageTracker, type, new StringTextComponent(""));
    }
    
    public CombatEventS2CPacket(final DamageTracker damageTracker, final Type type, final TextComponent textComponent) {
        this.type = type;
        final LivingEntity livingEntity4 = damageTracker.getBiggestAttacker();
        switch (type) {
            case END: {
                this.timeSinceLastAttack = damageTracker.getTimeSinceLastAttack();
                this.attackerEntityId = ((livingEntity4 == null) ? -1 : livingEntity4.getEntityId());
                break;
            }
            case DEATH: {
                this.entityId = damageTracker.getEntity().getEntityId();
                this.attackerEntityId = ((livingEntity4 == null) ? -1 : livingEntity4.getEntityId());
                this.deathMessage = textComponent;
                break;
            }
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.type = buf.<Type>readEnumConstant(Type.class);
        if (this.type == Type.END) {
            this.timeSinceLastAttack = buf.readVarInt();
            this.attackerEntityId = buf.readInt();
        }
        else if (this.type == Type.DEATH) {
            this.entityId = buf.readVarInt();
            this.attackerEntityId = buf.readInt();
            this.deathMessage = buf.readTextComponent();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.type);
        if (this.type == Type.END) {
            buf.writeVarInt(this.timeSinceLastAttack);
            buf.writeInt(this.attackerEntityId);
        }
        else if (this.type == Type.DEATH) {
            buf.writeVarInt(this.entityId);
            buf.writeInt(this.attackerEntityId);
            buf.writeTextComponent(this.deathMessage);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onCombatEvent(this);
    }
    
    @Override
    public boolean isErrorFatal() {
        return this.type == Type.DEATH;
    }
    
    public enum Type
    {
        BEGIN, 
        END, 
        DEATH;
    }
}
