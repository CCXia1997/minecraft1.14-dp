package net.minecraft.particle;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.PacketByteBuf;

public interface ParticleParameters
{
    ParticleType<?> getType();
    
    void write(final PacketByteBuf arg1);
    
    String asString();
    
    public interface Factory<T extends ParticleParameters>
    {
        T read(final ParticleType<T> arg1, final StringReader arg2) throws CommandSyntaxException;
        
        T read(final ParticleType<T> arg1, final PacketByteBuf arg2);
    }
}
