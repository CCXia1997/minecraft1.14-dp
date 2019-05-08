package net.minecraft.particle;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.block.BlockState;

public class BlockStateParticleParameters implements ParticleParameters
{
    public static final Factory<BlockStateParticleParameters> PARAMETERS_FACTORY;
    private final ParticleType<BlockStateParticleParameters> type;
    private final BlockState blockState;
    
    public BlockStateParticleParameters(final ParticleType<BlockStateParticleParameters> type, final BlockState blockState) {
        this.type = type;
        this.blockState = blockState;
    }
    
    @Override
    public void write(final PacketByteBuf buf) {
        buf.writeVarInt(Block.STATE_IDS.getId(this.blockState));
    }
    
    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
    }
    
    @Override
    public ParticleType<BlockStateParticleParameters> getType() {
        return this.type;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockState getBlockState() {
        return this.blockState;
    }
    
    static {
        PARAMETERS_FACTORY = new Factory<BlockStateParticleParameters>() {
            @Override
            public BlockStateParticleParameters read(final ParticleType<BlockStateParticleParameters> type, final StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                return new BlockStateParticleParameters(type, new BlockArgumentParser(reader, false).parse(false).getBlockState());
            }
            
            @Override
            public BlockStateParticleParameters read(final ParticleType<BlockStateParticleParameters> type, final PacketByteBuf buf) {
                return new BlockStateParticleParameters(type, Block.STATE_IDS.get(buf.readVarInt()));
            }
        };
    }
}
