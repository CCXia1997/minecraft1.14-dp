package net.minecraft.command.arguments.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.google.gson.JsonObject;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class DoubleArgumentSerializer implements ArgumentSerializer<DoubleArgumentType>
{
    @Override
    public void toPacket(final DoubleArgumentType doubleArgumentType, final PacketByteBuf packetByteBuf) {
        final boolean boolean3 = doubleArgumentType.getMinimum() != -1.7976931348623157E308;
        final boolean boolean4 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
        packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(boolean3, boolean4));
        if (boolean3) {
            packetByteBuf.writeDouble(doubleArgumentType.getMinimum());
        }
        if (boolean4) {
            packetByteBuf.writeDouble(doubleArgumentType.getMaximum());
        }
    }
    
    @Override
    public DoubleArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
        final byte byte2 = packetByteBuf.readByte();
        final double double3 = BrigadierArgumentTypes.hasMin(byte2) ? packetByteBuf.readDouble() : -1.7976931348623157E308;
        final double double4 = BrigadierArgumentTypes.hasMax(byte2) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
        return DoubleArgumentType.doubleArg(double3, double4);
    }
    
    @Override
    public void toJson(final DoubleArgumentType doubleArgumentType, final JsonObject jsonObject) {
        if (doubleArgumentType.getMinimum() != -1.7976931348623157E308) {
            jsonObject.addProperty("min", doubleArgumentType.getMinimum());
        }
        if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
            jsonObject.addProperty("max", doubleArgumentType.getMaximum());
        }
    }
}
