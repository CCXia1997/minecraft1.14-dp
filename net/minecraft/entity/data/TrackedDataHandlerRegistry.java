package net.minecraft.entity.data;

import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityPose;
import java.util.OptionalInt;
import net.minecraft.village.VillagerData;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerRotation;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import java.util.Optional;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Int2ObjectBiMap;

public class TrackedDataHandlerRegistry
{
    private static final Int2ObjectBiMap<TrackedDataHandler<?>> t;
    public static final TrackedDataHandler<Byte> BYTE;
    public static final TrackedDataHandler<Integer> INTEGER;
    public static final TrackedDataHandler<Float> FLOAT;
    public static final TrackedDataHandler<String> STRING;
    public static final TrackedDataHandler<TextComponent> TEXT_COMPONENT;
    public static final TrackedDataHandler<Optional<TextComponent>> OPTIONAL_TEXT_COMPONENT;
    public static final TrackedDataHandler<ItemStack> ITEM_STACK;
    public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE;
    public static final TrackedDataHandler<Boolean> BOOLEAN;
    public static final TrackedDataHandler<ParticleParameters> PARTICLE;
    public static final TrackedDataHandler<EulerRotation> ROTATION;
    public static final TrackedDataHandler<BlockPos> BLOCK_POS;
    public static final TrackedDataHandler<Optional<BlockPos>> OPTIONA_BLOCK_POS;
    public static final TrackedDataHandler<Direction> FACING;
    public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID;
    public static final TrackedDataHandler<CompoundTag> TAG_COMPOUND;
    public static final TrackedDataHandler<VillagerData> VILLAGER_DATA;
    public static final TrackedDataHandler<OptionalInt> r;
    public static final TrackedDataHandler<EntityPose> ENTITY_POSE;
    
    public static void register(final TrackedDataHandler<?> handler) {
        TrackedDataHandlerRegistry.t.add(handler);
    }
    
    @Nullable
    public static TrackedDataHandler<?> get(final int id) {
        return TrackedDataHandlerRegistry.t.get(id);
    }
    
    public static int getId(final TrackedDataHandler<?> handler) {
        return TrackedDataHandlerRegistry.t.getId(handler);
    }
    
    static {
        t = new Int2ObjectBiMap<TrackedDataHandler<?>>(16);
        BYTE = new TrackedDataHandler<Byte>() {
            @Override
            public void write(final PacketByteBuf data, final Byte byte2) {
                data.writeByte(byte2);
            }
            
            @Override
            public Byte read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readByte();
            }
            
            @Override
            public Byte copy(final Byte byte1) {
                return byte1;
            }
        };
        INTEGER = new TrackedDataHandler<Integer>() {
            @Override
            public void write(final PacketByteBuf data, final Integer integer) {
                data.writeVarInt(integer);
            }
            
            @Override
            public Integer read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readVarInt();
            }
            
            @Override
            public Integer copy(final Integer integer) {
                return integer;
            }
        };
        FLOAT = new TrackedDataHandler<Float>() {
            @Override
            public void write(final PacketByteBuf data, final Float float2) {
                data.writeFloat(float2);
            }
            
            @Override
            public Float read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readFloat();
            }
            
            @Override
            public Float copy(final Float float1) {
                return float1;
            }
        };
        STRING = new TrackedDataHandler<String>() {
            @Override
            public void write(final PacketByteBuf data, final String string) {
                data.writeString(string);
            }
            
            @Override
            public String read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readString(32767);
            }
            
            @Override
            public String copy(final String string) {
                return string;
            }
        };
        TEXT_COMPONENT = new TrackedDataHandler<TextComponent>() {
            @Override
            public void write(final PacketByteBuf data, final TextComponent textComponent) {
                data.writeTextComponent(textComponent);
            }
            
            @Override
            public TextComponent read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readTextComponent();
            }
            
            @Override
            public TextComponent copy(final TextComponent textComponent) {
                return textComponent.copy();
            }
        };
        OPTIONAL_TEXT_COMPONENT = new TrackedDataHandler<Optional<TextComponent>>() {
            @Override
            public void write(final PacketByteBuf data, final Optional<TextComponent> optional) {
                if (optional.isPresent()) {
                    data.writeBoolean(true);
                    data.writeTextComponent(optional.get());
                }
                else {
                    data.writeBoolean(false);
                }
            }
            
            @Override
            public Optional<TextComponent> read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readBoolean() ? Optional.<TextComponent>of(packetByteBuf.readTextComponent()) : Optional.<TextComponent>empty();
            }
            
            @Override
            public Optional<TextComponent> copy(final Optional<TextComponent> optional) {
                return optional.isPresent() ? Optional.<TextComponent>of(optional.get().copy()) : Optional.<TextComponent>empty();
            }
        };
        ITEM_STACK = new TrackedDataHandler<ItemStack>() {
            @Override
            public void write(final PacketByteBuf data, final ItemStack itemStack) {
                data.writeItemStack(itemStack);
            }
            
            @Override
            public ItemStack read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readItemStack();
            }
            
            @Override
            public ItemStack copy(final ItemStack itemStack) {
                return itemStack.copy();
            }
        };
        OPTIONAL_BLOCK_STATE = new TrackedDataHandler<Optional<BlockState>>() {
            @Override
            public void write(final PacketByteBuf data, final Optional<BlockState> optional) {
                if (optional.isPresent()) {
                    data.writeVarInt(Block.getRawIdFromState(optional.get()));
                }
                else {
                    data.writeVarInt(0);
                }
            }
            
            @Override
            public Optional<BlockState> read(final PacketByteBuf packetByteBuf) {
                final int integer2 = packetByteBuf.readVarInt();
                if (integer2 == 0) {
                    return Optional.<BlockState>empty();
                }
                return Optional.<BlockState>of(Block.getStateFromRawId(integer2));
            }
            
            @Override
            public Optional<BlockState> copy(final Optional<BlockState> optional) {
                return optional;
            }
        };
        BOOLEAN = new TrackedDataHandler<Boolean>() {
            @Override
            public void write(final PacketByteBuf data, final Boolean boolean2) {
                data.writeBoolean(boolean2);
            }
            
            @Override
            public Boolean read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readBoolean();
            }
            
            @Override
            public Boolean copy(final Boolean boolean1) {
                return boolean1;
            }
        };
        PARTICLE = new TrackedDataHandler<ParticleParameters>() {
            @Override
            public void write(final PacketByteBuf data, final ParticleParameters particleParameters) {
                data.writeVarInt(Registry.PARTICLE_TYPE.getRawId(particleParameters.getType()));
                particleParameters.write(data);
            }
            
            @Override
            public ParticleParameters read(final PacketByteBuf packetByteBuf) {
                return this.<ParticleParameters>a(packetByteBuf, Registry.PARTICLE_TYPE.get(packetByteBuf.readVarInt()));
            }
            
            private <T extends ParticleParameters> T a(final PacketByteBuf packetByteBuf, final ParticleType<T> particleType) {
                return particleType.getParametersFactory().read(particleType, packetByteBuf);
            }
            
            @Override
            public ParticleParameters copy(final ParticleParameters particleParameters) {
                return particleParameters;
            }
        };
        ROTATION = new TrackedDataHandler<EulerRotation>() {
            @Override
            public void write(final PacketByteBuf data, final EulerRotation eulerRotation) {
                data.writeFloat(eulerRotation.getX());
                data.writeFloat(eulerRotation.getY());
                data.writeFloat(eulerRotation.getZ());
            }
            
            @Override
            public EulerRotation read(final PacketByteBuf packetByteBuf) {
                return new EulerRotation(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
            }
            
            @Override
            public EulerRotation copy(final EulerRotation eulerRotation) {
                return eulerRotation;
            }
        };
        BLOCK_POS = new TrackedDataHandler<BlockPos>() {
            @Override
            public void write(final PacketByteBuf data, final BlockPos blockPos) {
                data.writeBlockPos(blockPos);
            }
            
            @Override
            public BlockPos read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readBlockPos();
            }
            
            @Override
            public BlockPos copy(final BlockPos blockPos) {
                return blockPos;
            }
        };
        OPTIONA_BLOCK_POS = new TrackedDataHandler<Optional<BlockPos>>() {
            @Override
            public void write(final PacketByteBuf data, final Optional<BlockPos> optional) {
                data.writeBoolean(optional.isPresent());
                if (optional.isPresent()) {
                    data.writeBlockPos(optional.get());
                }
            }
            
            @Override
            public Optional<BlockPos> read(final PacketByteBuf packetByteBuf) {
                if (!packetByteBuf.readBoolean()) {
                    return Optional.<BlockPos>empty();
                }
                return Optional.<BlockPos>of(packetByteBuf.readBlockPos());
            }
            
            @Override
            public Optional<BlockPos> copy(final Optional<BlockPos> optional) {
                return optional;
            }
        };
        FACING = new TrackedDataHandler<Direction>() {
            @Override
            public void write(final PacketByteBuf data, final Direction direction) {
                data.writeEnumConstant(direction);
            }
            
            @Override
            public Direction read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.<Direction>readEnumConstant(Direction.class);
            }
            
            @Override
            public Direction copy(final Direction direction) {
                return direction;
            }
        };
        OPTIONAL_UUID = new TrackedDataHandler<Optional<UUID>>() {
            @Override
            public void write(final PacketByteBuf data, final Optional<UUID> optional) {
                data.writeBoolean(optional.isPresent());
                if (optional.isPresent()) {
                    data.writeUuid(optional.get());
                }
            }
            
            @Override
            public Optional<UUID> read(final PacketByteBuf packetByteBuf) {
                if (!packetByteBuf.readBoolean()) {
                    return Optional.<UUID>empty();
                }
                return Optional.<UUID>of(packetByteBuf.readUuid());
            }
            
            @Override
            public Optional<UUID> copy(final Optional<UUID> optional) {
                return optional;
            }
        };
        TAG_COMPOUND = new TrackedDataHandler<CompoundTag>() {
            @Override
            public void write(final PacketByteBuf data, final CompoundTag compoundTag) {
                data.writeCompoundTag(compoundTag);
            }
            
            @Override
            public CompoundTag read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.readCompoundTag();
            }
            
            @Override
            public CompoundTag copy(final CompoundTag compoundTag) {
                return compoundTag.copy();
            }
        };
        VILLAGER_DATA = new TrackedDataHandler<VillagerData>() {
            @Override
            public void write(final PacketByteBuf data, final VillagerData villagerData) {
                data.writeVarInt(Registry.VILLAGER_TYPE.getRawId(villagerData.getType()));
                data.writeVarInt(Registry.VILLAGER_PROFESSION.getRawId(villagerData.getProfession()));
                data.writeVarInt(villagerData.getLevel());
            }
            
            @Override
            public VillagerData read(final PacketByteBuf packetByteBuf) {
                return new VillagerData(Registry.VILLAGER_TYPE.get(packetByteBuf.readVarInt()), Registry.VILLAGER_PROFESSION.get(packetByteBuf.readVarInt()), packetByteBuf.readVarInt());
            }
            
            @Override
            public VillagerData copy(final VillagerData villagerData) {
                return villagerData;
            }
        };
        r = new TrackedDataHandler<OptionalInt>() {
            @Override
            public void write(final PacketByteBuf data, final OptionalInt optionalInt) {
                data.writeVarInt(optionalInt.orElse(-1) + 1);
            }
            
            @Override
            public OptionalInt read(final PacketByteBuf packetByteBuf) {
                final int integer2 = packetByteBuf.readVarInt();
                return (integer2 == 0) ? OptionalInt.empty() : OptionalInt.of(integer2 - 1);
            }
            
            @Override
            public OptionalInt copy(final OptionalInt optionalInt) {
                return optionalInt;
            }
        };
        ENTITY_POSE = new TrackedDataHandler<EntityPose>() {
            @Override
            public void write(final PacketByteBuf data, final EntityPose entityPose) {
                data.writeEnumConstant(entityPose);
            }
            
            @Override
            public EntityPose read(final PacketByteBuf packetByteBuf) {
                return packetByteBuf.<EntityPose>readEnumConstant(EntityPose.class);
            }
            
            @Override
            public EntityPose copy(final EntityPose entityPose) {
                return entityPose;
            }
        };
        register(TrackedDataHandlerRegistry.BYTE);
        register(TrackedDataHandlerRegistry.INTEGER);
        register(TrackedDataHandlerRegistry.FLOAT);
        register(TrackedDataHandlerRegistry.STRING);
        register(TrackedDataHandlerRegistry.TEXT_COMPONENT);
        register(TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
        register(TrackedDataHandlerRegistry.ITEM_STACK);
        register(TrackedDataHandlerRegistry.BOOLEAN);
        register(TrackedDataHandlerRegistry.ROTATION);
        register(TrackedDataHandlerRegistry.BLOCK_POS);
        register(TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
        register(TrackedDataHandlerRegistry.FACING);
        register(TrackedDataHandlerRegistry.OPTIONAL_UUID);
        register(TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
        register(TrackedDataHandlerRegistry.TAG_COMPOUND);
        register(TrackedDataHandlerRegistry.PARTICLE);
        register(TrackedDataHandlerRegistry.VILLAGER_DATA);
        register(TrackedDataHandlerRegistry.r);
        register(TrackedDataHandlerRegistry.ENTITY_POSE);
    }
}
