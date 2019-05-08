package net.minecraft.command.arguments.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.google.gson.JsonObject;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class IntegerArgumentSerializer implements ArgumentSerializer<IntegerArgumentType>
{
    @Override
    public void toPacket(final IntegerArgumentType integerArgumentType, final PacketByteBuf packetByteBuf) {
        final boolean boolean3 = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
        final boolean boolean4 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
        packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(boolean3, boolean4));
        if (boolean3) {
            packetByteBuf.writeInt(integerArgumentType.getMinimum());
        }
        if (boolean4) {
            packetByteBuf.writeInt(integerArgumentType.getMaximum());
        }
    }
    
    @Override
    public IntegerArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
        final byte byte2 = packetByteBuf.readByte();
        final int integer3 = BrigadierArgumentTypes.hasMin(byte2) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
        final int integer4 = BrigadierArgumentTypes.hasMax(byte2) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
        return IntegerArgumentType.integer(integer3, integer4);
    }
    
    @Override
    public void toJson(final IntegerArgumentType integerArgumentType, final JsonObject jsonObject) {
        if (integerArgumentType.getMinimum() != Integer.MIN_VALUE) {
            jsonObject.addProperty("min", integerArgumentType.getMinimum());
        }
        if (integerArgumentType.getMaximum() != Integer.MAX_VALUE) {
            jsonObject.addProperty("max", integerArgumentType.getMaximum());
        }
    }
}
