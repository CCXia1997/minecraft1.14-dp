package net.minecraft.particle;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ItemStringReader;
import com.mojang.brigadier.StringReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.item.ItemStack;

public class ItemStackParticleParameters implements ParticleParameters
{
    public static final Factory<ItemStackParticleParameters> PARAMETERS_FACTORY;
    private final ParticleType<ItemStackParticleParameters> particleType;
    private final ItemStack stack;
    
    public ItemStackParticleParameters(final ParticleType<ItemStackParticleParameters> particleType, final ItemStack stack) {
        this.particleType = particleType;
        this.stack = stack;
    }
    
    @Override
    public void write(final PacketByteBuf buf) {
        buf.writeItemStack(this.stack);
    }
    
    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getItem(), this.stack.getTag()).c();
    }
    
    @Override
    public ParticleType<ItemStackParticleParameters> getType() {
        return this.particleType;
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.stack;
    }
    
    static {
        PARAMETERS_FACTORY = new Factory<ItemStackParticleParameters>() {
            @Override
            public ItemStackParticleParameters read(final ParticleType<ItemStackParticleParameters> type, final StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                final ItemStringReader itemStringReader3 = new ItemStringReader(reader, false).consume();
                final ItemStack itemStack4 = new ItemStackArgument(itemStringReader3.getItem(), itemStringReader3.getTag()).createStack(1, false);
                return new ItemStackParticleParameters(type, itemStack4);
            }
            
            @Override
            public ItemStackParticleParameters read(final ParticleType<ItemStackParticleParameters> type, final PacketByteBuf buf) {
                return new ItemStackParticleParameters(type, buf.readItemStack());
            }
        };
    }
}
