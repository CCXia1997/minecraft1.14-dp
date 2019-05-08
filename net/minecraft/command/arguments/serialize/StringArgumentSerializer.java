package net.minecraft.command.arguments.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import com.mojang.brigadier.arguments.StringArgumentType;

public class StringArgumentSerializer implements ArgumentSerializer<StringArgumentType>
{
    @Override
    public void toPacket(final StringArgumentType stringArgumentType, final PacketByteBuf packetByteBuf) {
        packetByteBuf.writeEnumConstant(stringArgumentType.getType());
    }
    
    @Override
    public StringArgumentType fromPacket(final PacketByteBuf packetByteBuf) {
        final StringArgumentType.StringType stringType2 = packetByteBuf.<StringArgumentType.StringType>readEnumConstant(StringArgumentType.StringType.class);
        switch (stringType2) {
            case SINGLE_WORD: {
                return StringArgumentType.word();
            }
            case QUOTABLE_PHRASE: {
                return StringArgumentType.string();
            }
            default: {
                return StringArgumentType.greedyString();
            }
        }
    }
    
    @Override
    public void toJson(final StringArgumentType stringArgumentType, final JsonObject jsonObject) {
        switch (stringArgumentType.getType()) {
            case SINGLE_WORD: {
                jsonObject.addProperty("type", "word");
                break;
            }
            case QUOTABLE_PHRASE: {
                jsonObject.addProperty("type", "phrase");
                break;
            }
            default: {
                jsonObject.addProperty("type", "greedy");
                break;
            }
        }
    }
}
