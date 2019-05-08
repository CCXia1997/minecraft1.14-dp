package net.minecraft.util;

import io.netty.util.ReferenceCounted;
import io.netty.util.ByteProcessor;
import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import io.netty.buffer.ByteBufAllocator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.io.DataInput;
import net.minecraft.nbt.PositionTracker;
import io.netty.buffer.ByteBufInputStream;
import java.io.IOException;
import io.netty.handler.codec.EncoderException;
import java.io.DataOutput;
import net.minecraft.nbt.NbtIo;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import io.netty.handler.codec.DecoderException;
import io.netty.buffer.ByteBuf;

public class PacketByteBuf extends ByteBuf
{
    private final ByteBuf parent;
    
    public PacketByteBuf(final ByteBuf byteBuf) {
        this.parent = byteBuf;
    }
    
    public static int getVarIntSizeBytes(final int integer) {
        for (int integer2 = 1; integer2 < 5; ++integer2) {
            if ((integer & -1 << integer2 * 7) == 0x0) {
                return integer2;
            }
        }
        return 5;
    }
    
    public PacketByteBuf writeByteArray(final byte[] arr) {
        this.writeVarInt(arr.length);
        this.writeBytes(arr);
        return this;
    }
    
    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }
    
    public byte[] readByteArray(final int integer) {
        final int integer2 = this.readVarInt();
        if (integer2 > integer) {
            throw new DecoderException("ByteArray with size " + integer2 + " is bigger than allowed " + integer);
        }
        final byte[] arr3 = new byte[integer2];
        this.readBytes(arr3);
        return arr3;
    }
    
    public PacketByteBuf writeIntArray(final int[] arr) {
        this.writeVarInt(arr.length);
        for (final int integer5 : arr) {
            this.writeVarInt(integer5);
        }
        return this;
    }
    
    public int[] readIntArray() {
        return this.readIntArray(this.readableBytes());
    }
    
    public int[] readIntArray(final int integer) {
        final int integer2 = this.readVarInt();
        if (integer2 > integer) {
            throw new DecoderException("VarIntArray with size " + integer2 + " is bigger than allowed " + integer);
        }
        final int[] arr3 = new int[integer2];
        for (int integer3 = 0; integer3 < arr3.length; ++integer3) {
            arr3[integer3] = this.readVarInt();
        }
        return arr3;
    }
    
    public PacketByteBuf writeLongArray(final long[] arr) {
        this.writeVarInt(arr.length);
        for (final long long5 : arr) {
            this.writeLong(long5);
        }
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public long[] readLongArray(@Nullable final long[] arr) {
        return this.readLongArray(arr, this.readableBytes() / 8);
    }
    
    @Environment(EnvType.CLIENT)
    public long[] readLongArray(@Nullable long[] toArray, final int integer) {
        final int integer2 = this.readVarInt();
        if (toArray == null || toArray.length != integer2) {
            if (integer2 > integer) {
                throw new DecoderException("LongArray with size " + integer2 + " is bigger than allowed " + integer);
            }
            toArray = new long[integer2];
        }
        for (int integer3 = 0; integer3 < toArray.length; ++integer3) {
            toArray[integer3] = this.readLong();
        }
        return toArray;
    }
    
    public BlockPos readBlockPos() {
        return BlockPos.fromLong(this.readLong());
    }
    
    public PacketByteBuf writeBlockPos(final BlockPos blockPos) {
        this.writeLong(blockPos.asLong());
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public ChunkSectionPos readChunkSectionPos() {
        return ChunkSectionPos.from(this.readLong());
    }
    
    public TextComponent readTextComponent() {
        return TextComponent.Serializer.fromJsonString(this.readString(262144));
    }
    
    public PacketByteBuf writeTextComponent(final TextComponent textComponent) {
        return this.writeString(TextComponent.Serializer.toJsonString(textComponent), 262144);
    }
    
    public <T extends Enum<T>> T readEnumConstant(final Class<T> class1) {
        return class1.getEnumConstants()[this.readVarInt()];
    }
    
    public PacketByteBuf writeEnumConstant(final Enum<?> enum1) {
        return this.writeVarInt(enum1.ordinal());
    }
    
    public int readVarInt() {
        int integer1 = 0;
        int integer2 = 0;
        byte byte3;
        do {
            byte3 = this.readByte();
            integer1 |= (byte3 & 0x7F) << integer2++ * 7;
            if (integer2 > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((byte3 & 0x80) == 0x80);
        return integer1;
    }
    
    public long readVarLong() {
        long long1 = 0L;
        int integer3 = 0;
        byte byte4;
        do {
            byte4 = this.readByte();
            long1 |= (long)(byte4 & 0x7F) << integer3++ * 7;
            if (integer3 > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while ((byte4 & 0x80) == 0x80);
        return long1;
    }
    
    public PacketByteBuf writeUuid(final UUID uUID) {
        this.writeLong(uUID.getMostSignificantBits());
        this.writeLong(uUID.getLeastSignificantBits());
        return this;
    }
    
    public UUID readUuid() {
        return new UUID(this.readLong(), this.readLong());
    }
    
    public PacketByteBuf writeVarInt(int integer) {
        while ((integer & 0xFFFFFF80) != 0x0) {
            this.writeByte((integer & 0x7F) | 0x80);
            integer >>>= 7;
        }
        this.writeByte(integer);
        return this;
    }
    
    public PacketByteBuf writeVarLong(long long1) {
        while ((long1 & 0xFFFFFFFFFFFFFF80L) != 0x0L) {
            this.writeByte((int)(long1 & 0x7FL) | 0x80);
            long1 >>>= 7;
        }
        this.writeByte((int)long1);
        return this;
    }
    
    public PacketByteBuf writeCompoundTag(@Nullable final CompoundTag compoundTag) {
        if (compoundTag == null) {
            this.writeByte(0);
        }
        else {
            try {
                NbtIo.write(compoundTag, (DataOutput)new ByteBufOutputStream((ByteBuf)this));
            }
            catch (IOException iOException2) {
                throw new EncoderException((Throwable)iOException2);
            }
        }
        return this;
    }
    
    @Nullable
    public CompoundTag readCompoundTag() {
        final int integer1 = this.readerIndex();
        final byte byte2 = this.readByte();
        if (byte2 == 0) {
            return null;
        }
        this.readerIndex(integer1);
        try {
            return NbtIo.read((DataInput)new ByteBufInputStream((ByteBuf)this), new PositionTracker(2097152L));
        }
        catch (IOException iOException3) {
            throw new EncoderException((Throwable)iOException3);
        }
    }
    
    public PacketByteBuf writeItemStack(final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            this.writeBoolean(false);
        }
        else {
            this.writeBoolean(true);
            final Item item2 = itemStack.getItem();
            this.writeVarInt(Item.getRawIdByItem(item2));
            this.writeByte(itemStack.getAmount());
            CompoundTag compoundTag3 = null;
            if (item2.canDamage() || item2.requiresClientSync()) {
                compoundTag3 = itemStack.getTag();
            }
            this.writeCompoundTag(compoundTag3);
        }
        return this;
    }
    
    public ItemStack readItemStack() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        }
        final int integer1 = this.readVarInt();
        final int integer2 = this.readByte();
        final ItemStack itemStack3 = new ItemStack(Item.byRawId(integer1), integer2);
        itemStack3.setTag(this.readCompoundTag());
        return itemStack3;
    }
    
    @Environment(EnvType.CLIENT)
    public String readString() {
        return this.readString(32767);
    }
    
    public String readString(final int integer) {
        final int integer2 = this.readVarInt();
        if (integer2 > integer * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + integer2 + " > " + integer * 4 + ")");
        }
        if (integer2 < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        final String string3 = this.toString(this.readerIndex(), integer2, StandardCharsets.UTF_8);
        this.readerIndex(this.readerIndex() + integer2);
        if (string3.length() > integer) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + integer2 + " > " + integer + ")");
        }
        return string3;
    }
    
    public PacketByteBuf writeString(final String string) {
        return this.writeString(string, 32767);
    }
    
    public PacketByteBuf writeString(final String string, final int integer) {
        final byte[] arr3 = string.getBytes(StandardCharsets.UTF_8);
        if (arr3.length > integer) {
            throw new EncoderException("String too big (was " + arr3.length + " bytes encoded, max " + integer + ")");
        }
        this.writeVarInt(arr3.length);
        this.writeBytes(arr3);
        return this;
    }
    
    public Identifier readIdentifier() {
        return new Identifier(this.readString(32767));
    }
    
    public PacketByteBuf writeIdentifier(final Identifier identifier) {
        this.writeString(identifier.toString());
        return this;
    }
    
    public Date readDate() {
        return new Date(this.readLong());
    }
    
    public PacketByteBuf writeDate(final Date date) {
        this.writeLong(date.getTime());
        return this;
    }
    
    public BlockHitResult readBlockHitResult() {
        final BlockPos blockPos1 = this.readBlockPos();
        final Direction direction2 = this.<Direction>readEnumConstant(Direction.class);
        final float float3 = this.readFloat();
        final float float4 = this.readFloat();
        final float float5 = this.readFloat();
        final boolean boolean6 = this.readBoolean();
        return new BlockHitResult(new Vec3d(blockPos1.getX() + float3, blockPos1.getY() + float4, blockPos1.getZ() + float5), direction2, blockPos1, boolean6);
    }
    
    public void writeBlockHitResult(final BlockHitResult blockHitResult) {
        final BlockPos blockPos2 = blockHitResult.getBlockPos();
        this.writeBlockPos(blockPos2);
        this.writeEnumConstant(blockHitResult.getSide());
        final Vec3d vec3d3 = blockHitResult.getPos();
        this.writeFloat((float)(vec3d3.x - blockPos2.getX()));
        this.writeFloat((float)(vec3d3.y - blockPos2.getY()));
        this.writeFloat((float)(vec3d3.z - blockPos2.getZ()));
        this.writeBoolean(blockHitResult.d());
    }
    
    public int capacity() {
        return this.parent.capacity();
    }
    
    public ByteBuf capacity(final int integer) {
        return this.parent.capacity(integer);
    }
    
    public int maxCapacity() {
        return this.parent.maxCapacity();
    }
    
    public ByteBufAllocator alloc() {
        return this.parent.alloc();
    }
    
    public ByteOrder order() {
        return this.parent.order();
    }
    
    public ByteBuf order(final ByteOrder byteOrder) {
        return this.parent.order(byteOrder);
    }
    
    public ByteBuf unwrap() {
        return this.parent.unwrap();
    }
    
    public boolean isDirect() {
        return this.parent.isDirect();
    }
    
    public boolean isReadOnly() {
        return this.parent.isReadOnly();
    }
    
    public ByteBuf asReadOnly() {
        return this.parent.asReadOnly();
    }
    
    public int readerIndex() {
        return this.parent.readerIndex();
    }
    
    public ByteBuf readerIndex(final int integer) {
        return this.parent.readerIndex(integer);
    }
    
    public int writerIndex() {
        return this.parent.writerIndex();
    }
    
    public ByteBuf writerIndex(final int integer) {
        return this.parent.writerIndex(integer);
    }
    
    public ByteBuf setIndex(final int integer1, final int integer2) {
        return this.parent.setIndex(integer1, integer2);
    }
    
    public int readableBytes() {
        return this.parent.readableBytes();
    }
    
    public int writableBytes() {
        return this.parent.writableBytes();
    }
    
    public int maxWritableBytes() {
        return this.parent.maxWritableBytes();
    }
    
    public boolean isReadable() {
        return this.parent.isReadable();
    }
    
    public boolean isReadable(final int integer) {
        return this.parent.isReadable(integer);
    }
    
    public boolean isWritable() {
        return this.parent.isWritable();
    }
    
    public boolean isWritable(final int integer) {
        return this.parent.isWritable(integer);
    }
    
    public ByteBuf clear() {
        return this.parent.clear();
    }
    
    public ByteBuf markReaderIndex() {
        return this.parent.markReaderIndex();
    }
    
    public ByteBuf resetReaderIndex() {
        return this.parent.resetReaderIndex();
    }
    
    public ByteBuf markWriterIndex() {
        return this.parent.markWriterIndex();
    }
    
    public ByteBuf resetWriterIndex() {
        return this.parent.resetWriterIndex();
    }
    
    public ByteBuf discardReadBytes() {
        return this.parent.discardReadBytes();
    }
    
    public ByteBuf discardSomeReadBytes() {
        return this.parent.discardSomeReadBytes();
    }
    
    public ByteBuf ensureWritable(final int integer) {
        return this.parent.ensureWritable(integer);
    }
    
    public int ensureWritable(final int integer, final boolean boolean2) {
        return this.parent.ensureWritable(integer, boolean2);
    }
    
    public boolean getBoolean(final int integer) {
        return this.parent.getBoolean(integer);
    }
    
    public byte getByte(final int integer) {
        return this.parent.getByte(integer);
    }
    
    public short getUnsignedByte(final int integer) {
        return this.parent.getUnsignedByte(integer);
    }
    
    public short getShort(final int integer) {
        return this.parent.getShort(integer);
    }
    
    public short getShortLE(final int integer) {
        return this.parent.getShortLE(integer);
    }
    
    public int getUnsignedShort(final int integer) {
        return this.parent.getUnsignedShort(integer);
    }
    
    public int getUnsignedShortLE(final int integer) {
        return this.parent.getUnsignedShortLE(integer);
    }
    
    public int getMedium(final int integer) {
        return this.parent.getMedium(integer);
    }
    
    public int getMediumLE(final int integer) {
        return this.parent.getMediumLE(integer);
    }
    
    public int getUnsignedMedium(final int integer) {
        return this.parent.getUnsignedMedium(integer);
    }
    
    public int getUnsignedMediumLE(final int integer) {
        return this.parent.getUnsignedMediumLE(integer);
    }
    
    public int getInt(final int integer) {
        return this.parent.getInt(integer);
    }
    
    public int getIntLE(final int integer) {
        return this.parent.getIntLE(integer);
    }
    
    public long getUnsignedInt(final int integer) {
        return this.parent.getUnsignedInt(integer);
    }
    
    public long getUnsignedIntLE(final int integer) {
        return this.parent.getUnsignedIntLE(integer);
    }
    
    public long getLong(final int integer) {
        return this.parent.getLong(integer);
    }
    
    public long getLongLE(final int integer) {
        return this.parent.getLongLE(integer);
    }
    
    public char getChar(final int integer) {
        return this.parent.getChar(integer);
    }
    
    public float getFloat(final int integer) {
        return this.parent.getFloat(integer);
    }
    
    public double getDouble(final int integer) {
        return this.parent.getDouble(integer);
    }
    
    public ByteBuf getBytes(final int integer, final ByteBuf byteBuf) {
        return this.parent.getBytes(integer, byteBuf);
    }
    
    public ByteBuf getBytes(final int integer1, final ByteBuf byteBuf, final int integer3) {
        return this.parent.getBytes(integer1, byteBuf, integer3);
    }
    
    public ByteBuf getBytes(final int integer1, final ByteBuf byteBuf, final int integer3, final int integer4) {
        return this.parent.getBytes(integer1, byteBuf, integer3, integer4);
    }
    
    public ByteBuf getBytes(final int integer, final byte[] arr) {
        return this.parent.getBytes(integer, arr);
    }
    
    public ByteBuf getBytes(final int integer1, final byte[] arr, final int integer3, final int integer4) {
        return this.parent.getBytes(integer1, arr, integer3, integer4);
    }
    
    public ByteBuf getBytes(final int integer, final ByteBuffer byteBuffer) {
        return this.parent.getBytes(integer, byteBuffer);
    }
    
    public ByteBuf getBytes(final int integer1, final OutputStream outputStream, final int integer3) throws IOException {
        return this.parent.getBytes(integer1, outputStream, integer3);
    }
    
    public int getBytes(final int integer1, final GatheringByteChannel gatheringByteChannel, final int integer3) throws IOException {
        return this.parent.getBytes(integer1, gatheringByteChannel, integer3);
    }
    
    public int getBytes(final int integer1, final FileChannel fileChannel, final long long3, final int integer5) throws IOException {
        return this.parent.getBytes(integer1, fileChannel, long3, integer5);
    }
    
    public CharSequence getCharSequence(final int integer1, final int integer2, final Charset charset) {
        return this.parent.getCharSequence(integer1, integer2, charset);
    }
    
    public ByteBuf setBoolean(final int integer, final boolean boolean2) {
        return this.parent.setBoolean(integer, boolean2);
    }
    
    public ByteBuf setByte(final int integer1, final int integer2) {
        return this.parent.setByte(integer1, integer2);
    }
    
    public ByteBuf setShort(final int integer1, final int integer2) {
        return this.parent.setShort(integer1, integer2);
    }
    
    public ByteBuf setShortLE(final int integer1, final int integer2) {
        return this.parent.setShortLE(integer1, integer2);
    }
    
    public ByteBuf setMedium(final int integer1, final int integer2) {
        return this.parent.setMedium(integer1, integer2);
    }
    
    public ByteBuf setMediumLE(final int integer1, final int integer2) {
        return this.parent.setMediumLE(integer1, integer2);
    }
    
    public ByteBuf setInt(final int integer1, final int integer2) {
        return this.parent.setInt(integer1, integer2);
    }
    
    public ByteBuf setIntLE(final int integer1, final int integer2) {
        return this.parent.setIntLE(integer1, integer2);
    }
    
    public ByteBuf setLong(final int integer, final long long2) {
        return this.parent.setLong(integer, long2);
    }
    
    public ByteBuf setLongLE(final int integer, final long long2) {
        return this.parent.setLongLE(integer, long2);
    }
    
    public ByteBuf setChar(final int integer1, final int integer2) {
        return this.parent.setChar(integer1, integer2);
    }
    
    public ByteBuf setFloat(final int integer, final float float2) {
        return this.parent.setFloat(integer, float2);
    }
    
    public ByteBuf setDouble(final int integer, final double double2) {
        return this.parent.setDouble(integer, double2);
    }
    
    public ByteBuf setBytes(final int integer, final ByteBuf byteBuf) {
        return this.parent.setBytes(integer, byteBuf);
    }
    
    public ByteBuf setBytes(final int integer1, final ByteBuf byteBuf, final int integer3) {
        return this.parent.setBytes(integer1, byteBuf, integer3);
    }
    
    public ByteBuf setBytes(final int integer1, final ByteBuf byteBuf, final int integer3, final int integer4) {
        return this.parent.setBytes(integer1, byteBuf, integer3, integer4);
    }
    
    public ByteBuf setBytes(final int integer, final byte[] arr) {
        return this.parent.setBytes(integer, arr);
    }
    
    public ByteBuf setBytes(final int integer1, final byte[] arr, final int integer3, final int integer4) {
        return this.parent.setBytes(integer1, arr, integer3, integer4);
    }
    
    public ByteBuf setBytes(final int integer, final ByteBuffer byteBuffer) {
        return this.parent.setBytes(integer, byteBuffer);
    }
    
    public int setBytes(final int integer1, final InputStream inputStream, final int integer3) throws IOException {
        return this.parent.setBytes(integer1, inputStream, integer3);
    }
    
    public int setBytes(final int integer1, final ScatteringByteChannel scatteringByteChannel, final int integer3) throws IOException {
        return this.parent.setBytes(integer1, scatteringByteChannel, integer3);
    }
    
    public int setBytes(final int integer1, final FileChannel fileChannel, final long long3, final int integer5) throws IOException {
        return this.parent.setBytes(integer1, fileChannel, long3, integer5);
    }
    
    public ByteBuf setZero(final int integer1, final int integer2) {
        return this.parent.setZero(integer1, integer2);
    }
    
    public int setCharSequence(final int integer, final CharSequence charSequence, final Charset charset) {
        return this.parent.setCharSequence(integer, charSequence, charset);
    }
    
    public boolean readBoolean() {
        return this.parent.readBoolean();
    }
    
    public byte readByte() {
        return this.parent.readByte();
    }
    
    public short readUnsignedByte() {
        return this.parent.readUnsignedByte();
    }
    
    public short readShort() {
        return this.parent.readShort();
    }
    
    public short readShortLE() {
        return this.parent.readShortLE();
    }
    
    public int readUnsignedShort() {
        return this.parent.readUnsignedShort();
    }
    
    public int readUnsignedShortLE() {
        return this.parent.readUnsignedShortLE();
    }
    
    public int readMedium() {
        return this.parent.readMedium();
    }
    
    public int readMediumLE() {
        return this.parent.readMediumLE();
    }
    
    public int readUnsignedMedium() {
        return this.parent.readUnsignedMedium();
    }
    
    public int readUnsignedMediumLE() {
        return this.parent.readUnsignedMediumLE();
    }
    
    public int readInt() {
        return this.parent.readInt();
    }
    
    public int readIntLE() {
        return this.parent.readIntLE();
    }
    
    public long readUnsignedInt() {
        return this.parent.readUnsignedInt();
    }
    
    public long readUnsignedIntLE() {
        return this.parent.readUnsignedIntLE();
    }
    
    public long readLong() {
        return this.parent.readLong();
    }
    
    public long readLongLE() {
        return this.parent.readLongLE();
    }
    
    public char readChar() {
        return this.parent.readChar();
    }
    
    public float readFloat() {
        return this.parent.readFloat();
    }
    
    public double readDouble() {
        return this.parent.readDouble();
    }
    
    public ByteBuf readBytes(final int integer) {
        return this.parent.readBytes(integer);
    }
    
    public ByteBuf readSlice(final int integer) {
        return this.parent.readSlice(integer);
    }
    
    public ByteBuf readRetainedSlice(final int integer) {
        return this.parent.readRetainedSlice(integer);
    }
    
    public ByteBuf readBytes(final ByteBuf byteBuf) {
        return this.parent.readBytes(byteBuf);
    }
    
    public ByteBuf readBytes(final ByteBuf byteBuf, final int integer) {
        return this.parent.readBytes(byteBuf, integer);
    }
    
    public ByteBuf readBytes(final ByteBuf byteBuf, final int integer2, final int integer3) {
        return this.parent.readBytes(byteBuf, integer2, integer3);
    }
    
    public ByteBuf readBytes(final byte[] arr) {
        return this.parent.readBytes(arr);
    }
    
    public ByteBuf readBytes(final byte[] arr, final int integer2, final int integer3) {
        return this.parent.readBytes(arr, integer2, integer3);
    }
    
    public ByteBuf readBytes(final ByteBuffer byteBuffer) {
        return this.parent.readBytes(byteBuffer);
    }
    
    public ByteBuf readBytes(final OutputStream outputStream, final int integer) throws IOException {
        return this.parent.readBytes(outputStream, integer);
    }
    
    public int readBytes(final GatheringByteChannel gatheringByteChannel, final int integer) throws IOException {
        return this.parent.readBytes(gatheringByteChannel, integer);
    }
    
    public CharSequence readCharSequence(final int integer, final Charset charset) {
        return this.parent.readCharSequence(integer, charset);
    }
    
    public int readBytes(final FileChannel fileChannel, final long long2, final int integer4) throws IOException {
        return this.parent.readBytes(fileChannel, long2, integer4);
    }
    
    public ByteBuf skipBytes(final int integer) {
        return this.parent.skipBytes(integer);
    }
    
    public ByteBuf writeBoolean(final boolean boolean1) {
        return this.parent.writeBoolean(boolean1);
    }
    
    public ByteBuf writeByte(final int integer) {
        return this.parent.writeByte(integer);
    }
    
    public ByteBuf writeShort(final int integer) {
        return this.parent.writeShort(integer);
    }
    
    public ByteBuf writeShortLE(final int integer) {
        return this.parent.writeShortLE(integer);
    }
    
    public ByteBuf writeMedium(final int integer) {
        return this.parent.writeMedium(integer);
    }
    
    public ByteBuf writeMediumLE(final int integer) {
        return this.parent.writeMediumLE(integer);
    }
    
    public ByteBuf writeInt(final int integer) {
        return this.parent.writeInt(integer);
    }
    
    public ByteBuf writeIntLE(final int integer) {
        return this.parent.writeIntLE(integer);
    }
    
    public ByteBuf writeLong(final long long1) {
        return this.parent.writeLong(long1);
    }
    
    public ByteBuf writeLongLE(final long long1) {
        return this.parent.writeLongLE(long1);
    }
    
    public ByteBuf writeChar(final int integer) {
        return this.parent.writeChar(integer);
    }
    
    public ByteBuf writeFloat(final float float1) {
        return this.parent.writeFloat(float1);
    }
    
    public ByteBuf writeDouble(final double double1) {
        return this.parent.writeDouble(double1);
    }
    
    public ByteBuf writeBytes(final ByteBuf byteBuf) {
        return this.parent.writeBytes(byteBuf);
    }
    
    public ByteBuf writeBytes(final ByteBuf byteBuf, final int integer) {
        return this.parent.writeBytes(byteBuf, integer);
    }
    
    public ByteBuf writeBytes(final ByteBuf byteBuf, final int integer2, final int integer3) {
        return this.parent.writeBytes(byteBuf, integer2, integer3);
    }
    
    public ByteBuf writeBytes(final byte[] arr) {
        return this.parent.writeBytes(arr);
    }
    
    public ByteBuf writeBytes(final byte[] arr, final int integer2, final int integer3) {
        return this.parent.writeBytes(arr, integer2, integer3);
    }
    
    public ByteBuf writeBytes(final ByteBuffer byteBuffer) {
        return this.parent.writeBytes(byteBuffer);
    }
    
    public int writeBytes(final InputStream inputStream, final int integer) throws IOException {
        return this.parent.writeBytes(inputStream, integer);
    }
    
    public int writeBytes(final ScatteringByteChannel scatteringByteChannel, final int integer) throws IOException {
        return this.parent.writeBytes(scatteringByteChannel, integer);
    }
    
    public int writeBytes(final FileChannel fileChannel, final long long2, final int integer4) throws IOException {
        return this.parent.writeBytes(fileChannel, long2, integer4);
    }
    
    public ByteBuf writeZero(final int integer) {
        return this.parent.writeZero(integer);
    }
    
    public int writeCharSequence(final CharSequence charSequence, final Charset charset) {
        return this.parent.writeCharSequence(charSequence, charset);
    }
    
    public int indexOf(final int integer1, final int integer2, final byte byte3) {
        return this.parent.indexOf(integer1, integer2, byte3);
    }
    
    public int bytesBefore(final byte byte1) {
        return this.parent.bytesBefore(byte1);
    }
    
    public int bytesBefore(final int integer, final byte byte2) {
        return this.parent.bytesBefore(integer, byte2);
    }
    
    public int bytesBefore(final int integer1, final int integer2, final byte byte3) {
        return this.parent.bytesBefore(integer1, integer2, byte3);
    }
    
    public int forEachByte(final ByteProcessor byteProcessor) {
        return this.parent.forEachByte(byteProcessor);
    }
    
    public int forEachByte(final int integer1, final int integer2, final ByteProcessor byteProcessor) {
        return this.parent.forEachByte(integer1, integer2, byteProcessor);
    }
    
    public int forEachByteDesc(final ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(byteProcessor);
    }
    
    public int forEachByteDesc(final int integer1, final int integer2, final ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(integer1, integer2, byteProcessor);
    }
    
    public ByteBuf copy() {
        return this.parent.copy();
    }
    
    public ByteBuf copy(final int integer1, final int integer2) {
        return this.parent.copy(integer1, integer2);
    }
    
    public ByteBuf slice() {
        return this.parent.slice();
    }
    
    public ByteBuf retainedSlice() {
        return this.parent.retainedSlice();
    }
    
    public ByteBuf slice(final int integer1, final int integer2) {
        return this.parent.slice(integer1, integer2);
    }
    
    public ByteBuf retainedSlice(final int integer1, final int integer2) {
        return this.parent.retainedSlice(integer1, integer2);
    }
    
    public ByteBuf duplicate() {
        return this.parent.duplicate();
    }
    
    public ByteBuf retainedDuplicate() {
        return this.parent.retainedDuplicate();
    }
    
    public int nioBufferCount() {
        return this.parent.nioBufferCount();
    }
    
    public ByteBuffer nioBuffer() {
        return this.parent.nioBuffer();
    }
    
    public ByteBuffer nioBuffer(final int integer1, final int integer2) {
        return this.parent.nioBuffer(integer1, integer2);
    }
    
    public ByteBuffer internalNioBuffer(final int integer1, final int integer2) {
        return this.parent.internalNioBuffer(integer1, integer2);
    }
    
    public ByteBuffer[] nioBuffers() {
        return this.parent.nioBuffers();
    }
    
    public ByteBuffer[] nioBuffers(final int integer1, final int integer2) {
        return this.parent.nioBuffers(integer1, integer2);
    }
    
    public boolean hasArray() {
        return this.parent.hasArray();
    }
    
    public byte[] array() {
        return this.parent.array();
    }
    
    public int arrayOffset() {
        return this.parent.arrayOffset();
    }
    
    public boolean hasMemoryAddress() {
        return this.parent.hasMemoryAddress();
    }
    
    public long memoryAddress() {
        return this.parent.memoryAddress();
    }
    
    public String toString(final Charset charset) {
        return this.parent.toString(charset);
    }
    
    public String toString(final int integer1, final int integer2, final Charset charset) {
        return this.parent.toString(integer1, integer2, charset);
    }
    
    public int hashCode() {
        return this.parent.hashCode();
    }
    
    public boolean equals(final Object object) {
        return this.parent.equals(object);
    }
    
    public int compareTo(final ByteBuf byteBuf) {
        return this.parent.compareTo(byteBuf);
    }
    
    public String toString() {
        return this.parent.toString();
    }
    
    public ByteBuf retain(final int integer) {
        return this.parent.retain(integer);
    }
    
    public ByteBuf retain() {
        return this.parent.retain();
    }
    
    public ByteBuf touch() {
        return this.parent.touch();
    }
    
    public ByteBuf touch(final Object object) {
        return this.parent.touch(object);
    }
    
    public int refCnt() {
        return this.parent.refCnt();
    }
    
    public boolean release() {
        return this.parent.release();
    }
    
    public boolean release(final int integer) {
        return this.parent.release(integer);
    }
}
