package net.minecraft.particle;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import java.util.Locale;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class DustParticleParameters implements ParticleParameters
{
    public static final DustParticleParameters RED;
    public static final Factory<DustParticleParameters> PARAMETERS_FACTORY;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    
    public DustParticleParameters(final float red, final float green, final float blue, final float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = MathHelper.clamp(alpha, 0.01f, 4.0f);
    }
    
    @Override
    public void write(final PacketByteBuf buf) {
        buf.writeFloat(this.red);
        buf.writeFloat(this.green);
        buf.writeFloat(this.blue);
        buf.writeFloat(this.alpha);
    }
    
    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue, this.alpha);
    }
    
    @Override
    public ParticleType<DustParticleParameters> getType() {
        return ParticleTypes.o;
    }
    
    @Environment(EnvType.CLIENT)
    public float getRed() {
        return this.red;
    }
    
    @Environment(EnvType.CLIENT)
    public float getGreen() {
        return this.green;
    }
    
    @Environment(EnvType.CLIENT)
    public float getBlue() {
        return this.blue;
    }
    
    @Environment(EnvType.CLIENT)
    public float getAlpha() {
        return this.alpha;
    }
    
    static {
        RED = new DustParticleParameters(1.0f, 0.0f, 0.0f, 1.0f);
        PARAMETERS_FACTORY = new Factory<DustParticleParameters>() {
            @Override
            public DustParticleParameters read(final ParticleType<DustParticleParameters> type, final StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                final float float3 = (float)reader.readDouble();
                reader.expect(' ');
                final float float4 = (float)reader.readDouble();
                reader.expect(' ');
                final float float5 = (float)reader.readDouble();
                reader.expect(' ');
                final float float6 = (float)reader.readDouble();
                return new DustParticleParameters(float3, float4, float5, float6);
            }
            
            @Override
            public DustParticleParameters read(final ParticleType<DustParticleParameters> type, final PacketByteBuf buf) {
                return new DustParticleParameters(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
            }
        };
    }
}
