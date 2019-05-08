package net.minecraft.command.arguments.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.google.gson.JsonObject;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;
import com.mojang.brigadier.arguments.FloatArgumentType;

public class FloatArgumentSerializer implements ArgumentSerializer<FloatArgumentType>
{
    @Override
    public void toPacket(final FloatArgumentType floatArgumentType, final PacketByteBuf packetByteBuf) {
        final boolean boolean3 = floatArgumentType.getMinimum() != -3.4028235E38f;
        final boolean boolean4 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
        packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(boolean3, boolean4));
        if (boolean3) {
            packetByteBuf.writeFloat(floatArgumentType.getMinimum());
        }
        if (boolean4) {
            packetByteBuf.writeFloat(floatArgumentType.getMaximum());
        }
    }
    
    @Override
    public FloatArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
        final byte byte2 = packetByteBuf.readByte();
        final float float3 = BrigadierArgumentTypes.hasMin(byte2) ? packetByteBuf.readFloat() : -3.4028235E38f;
        final float float4 = BrigadierArgumentTypes.hasMax(byte2) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
        return FloatArgumentType.floatArg(float3, float4);
    }
    
    @Override
    public void toJson(final FloatArgumentType floatArgumentType, final JsonObject jsonObject) {
        if (floatArgumentType.getMinimum() != -3.4028235E38f) {
            jsonObject.addProperty("min", floatArgumentType.getMinimum());
        }
        if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
            jsonObject.addProperty("max", floatArgumentType.getMaximum());
        }
    }
}
