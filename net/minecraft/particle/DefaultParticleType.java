package net.minecraft.particle;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;

public class DefaultParticleType extends ParticleType<DefaultParticleType> implements ParticleParameters
{
    private static final Factory<DefaultParticleType> PARAMETER_FACTORY;
    
    protected DefaultParticleType(final boolean boolean1) {
        super(boolean1, DefaultParticleType.PARAMETER_FACTORY);
    }
    
    @Override
    public ParticleType<DefaultParticleType> getType() {
        return this;
    }
    
    @Override
    public void write(final PacketByteBuf buf) {
    }
    
    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this).toString();
    }
    
    static {
        PARAMETER_FACTORY = new Factory<DefaultParticleType>() {
            @Override
            public DefaultParticleType read(final ParticleType<DefaultParticleType> type, final StringReader reader) throws CommandSyntaxException {
                return (DefaultParticleType)type;
            }
            
            @Override
            public DefaultParticleType read(final ParticleType<DefaultParticleType> type, final PacketByteBuf buf) {
                return (DefaultParticleType)type;
            }
        };
    }
}
