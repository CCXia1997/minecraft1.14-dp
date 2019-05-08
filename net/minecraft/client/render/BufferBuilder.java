package net.minecraft.client.render;

import org.apache.logging.log4j.LogManager;
import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.Arrays;
import com.google.common.primitives.Floats;
import net.minecraft.client.util.GlAllocationUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BufferBuilder
{
    private static final Logger LOGGER;
    private ByteBuffer bufByte;
    private IntBuffer bufInt;
    private ShortBuffer bufShort;
    private FloatBuffer bufFloat;
    private int vertexCount;
    private VertexFormatElement currentElement;
    private int currentElementId;
    private boolean colorDisabled;
    private int drawMode;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private VertexFormat format;
    private boolean building;
    
    public BufferBuilder(final int size) {
        this.bufByte = GlAllocationUtils.allocateByteBuffer(size * 4);
        this.bufInt = this.bufByte.asIntBuffer();
        this.bufShort = this.bufByte.asShortBuffer();
        this.bufFloat = this.bufByte.asFloatBuffer();
    }
    
    private void grow(final int size) {
        if (this.vertexCount * this.format.getVertexSize() + size <= this.bufByte.capacity()) {
            return;
        }
        final int integer2 = this.bufByte.capacity();
        final int integer3 = integer2 + roundBufferSize(size);
        BufferBuilder.LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", integer2, integer3);
        final int integer4 = this.bufInt.position();
        final ByteBuffer byteBuffer5 = GlAllocationUtils.allocateByteBuffer(integer3);
        this.bufByte.position(0);
        byteBuffer5.put(this.bufByte);
        byteBuffer5.rewind();
        this.bufByte = byteBuffer5;
        this.bufFloat = this.bufByte.asFloatBuffer().asReadOnlyBuffer();
        (this.bufInt = this.bufByte.asIntBuffer()).position(integer4);
        (this.bufShort = this.bufByte.asShortBuffer()).position(integer4 << 1);
    }
    
    private static int roundBufferSize(final int amount) {
        int integer2 = 2097152;
        if (amount == 0) {
            return integer2;
        }
        if (amount < 0) {
            integer2 *= -1;
        }
        final int integer3 = amount % integer2;
        if (integer3 == 0) {
            return amount;
        }
        return amount + integer2 - integer3;
    }
    
    public void sortQuads(final float float1, final float float2, final float float3) {
        final int integer4 = this.vertexCount / 4;
        final float[] arr5 = new float[integer4];
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            arr5[integer5] = getDistanceSq(this.bufFloat, (float)(float1 + this.offsetX), (float)(float2 + this.offsetY), (float)(float3 + this.offsetZ), this.format.getVertexSizeInteger(), integer5 * this.format.getVertexSize());
        }
        final Integer[] arr6 = new Integer[integer4];
        for (int integer6 = 0; integer6 < arr6.length; ++integer6) {
            arr6[integer6] = integer6;
        }
        final Object o;
        Arrays.<Integer>sort(arr6, (integer2, integer3) -> Floats.compare(o[integer3], o[integer2]));
        final BitSet bitSet7 = new BitSet();
        final int integer7 = this.format.getVertexSize();
        final int[] arr7 = new int[integer7];
        for (int integer8 = bitSet7.nextClearBit(0); integer8 < arr6.length; integer8 = bitSet7.nextClearBit(integer8 + 1)) {
            final int integer9 = arr6[integer8];
            if (integer9 != integer8) {
                this.bufInt.limit(integer9 * integer7 + integer7);
                this.bufInt.position(integer9 * integer7);
                this.bufInt.get(arr7);
                for (int integer10 = integer9, integer11 = arr6[integer10]; integer10 != integer8; integer10 = integer11, integer11 = arr6[integer10]) {
                    this.bufInt.limit(integer11 * integer7 + integer7);
                    this.bufInt.position(integer11 * integer7);
                    final IntBuffer intBuffer14 = this.bufInt.slice();
                    this.bufInt.limit(integer10 * integer7 + integer7);
                    this.bufInt.position(integer10 * integer7);
                    this.bufInt.put(intBuffer14);
                    bitSet7.set(integer10);
                }
                this.bufInt.limit(integer8 * integer7 + integer7);
                this.bufInt.position(integer8 * integer7);
                this.bufInt.put(arr7);
            }
            bitSet7.set(integer8);
        }
    }
    
    public State toBufferState() {
        this.bufInt.rewind();
        final int integer1 = this.getCurrentSize();
        this.bufInt.limit(integer1);
        final int[] arr2 = new int[integer1];
        this.bufInt.get(arr2);
        this.bufInt.limit(this.bufInt.capacity());
        this.bufInt.position(integer1);
        return new State(arr2, new VertexFormat(this.format));
    }
    
    private int getCurrentSize() {
        return this.vertexCount * this.format.getVertexSizeInteger();
    }
    
    private static float getDistanceSq(final FloatBuffer buffer, final float x, final float y, final float z, final int integer5, final int integer6) {
        final float float7 = buffer.get(integer6 + integer5 * 0 + 0);
        final float float8 = buffer.get(integer6 + integer5 * 0 + 1);
        final float float9 = buffer.get(integer6 + integer5 * 0 + 2);
        final float float10 = buffer.get(integer6 + integer5 * 1 + 0);
        final float float11 = buffer.get(integer6 + integer5 * 1 + 1);
        final float float12 = buffer.get(integer6 + integer5 * 1 + 2);
        final float float13 = buffer.get(integer6 + integer5 * 2 + 0);
        final float float14 = buffer.get(integer6 + integer5 * 2 + 1);
        final float float15 = buffer.get(integer6 + integer5 * 2 + 2);
        final float float16 = buffer.get(integer6 + integer5 * 3 + 0);
        final float float17 = buffer.get(integer6 + integer5 * 3 + 1);
        final float float18 = buffer.get(integer6 + integer5 * 3 + 2);
        final float float19 = (float7 + float10 + float13 + float16) * 0.25f - x;
        final float float20 = (float8 + float11 + float14 + float17) * 0.25f - y;
        final float float21 = (float9 + float12 + float15 + float18) * 0.25f - z;
        return float19 * float19 + float20 * float20 + float21 * float21;
    }
    
    public void restoreState(final State state) {
        this.bufInt.clear();
        this.grow(state.getRawBuffer().length * 4);
        this.bufInt.put(state.getRawBuffer());
        this.vertexCount = state.getVertexCount();
        this.format = new VertexFormat(state.getFormat());
    }
    
    public void clear() {
        this.vertexCount = 0;
        this.currentElement = null;
        this.currentElementId = 0;
    }
    
    public void begin(final int drawMode, final VertexFormat vertexFormat) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.clear();
        this.drawMode = drawMode;
        this.format = vertexFormat;
        this.currentElement = vertexFormat.getElement(this.currentElementId);
        this.colorDisabled = false;
        this.bufByte.limit(this.bufByte.capacity());
    }
    
    public BufferBuilder texture(final double u, final double v) {
        final int integer5 = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.bufByte.putFloat(integer5, (float)u);
                this.bufByte.putFloat(integer5 + 4, (float)v);
                break;
            }
            case UNSIGNED_INT:
            case INT: {
                this.bufByte.putInt(integer5, (int)u);
                this.bufByte.putInt(integer5 + 4, (int)v);
                break;
            }
            case UNSIGNED_SHORT:
            case SHORT: {
                this.bufByte.putShort(integer5, (short)v);
                this.bufByte.putShort(integer5 + 2, (short)u);
                break;
            }
            case UNSIGNED_BYTE:
            case BYTE: {
                this.bufByte.put(integer5, (byte)v);
                this.bufByte.put(integer5 + 1, (byte)u);
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public BufferBuilder texture(final int u, final int v) {
        final int integer3 = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.bufByte.putFloat(integer3, (float)u);
                this.bufByte.putFloat(integer3 + 4, (float)v);
                break;
            }
            case UNSIGNED_INT:
            case INT: {
                this.bufByte.putInt(integer3, u);
                this.bufByte.putInt(integer3 + 4, v);
                break;
            }
            case UNSIGNED_SHORT:
            case SHORT: {
                this.bufByte.putShort(integer3, (short)v);
                this.bufByte.putShort(integer3 + 2, (short)u);
                break;
            }
            case UNSIGNED_BYTE:
            case BYTE: {
                this.bufByte.put(integer3, (byte)v);
                this.bufByte.put(integer3 + 1, (byte)u);
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void brightness(final int integer1, final int integer2, final int integer3, final int integer4) {
        final int integer5 = (this.vertexCount - 4) * this.format.getVertexSizeInteger() + this.format.getUvOffset(1) / 4;
        final int integer6 = this.format.getVertexSize() >> 2;
        this.bufInt.put(integer5, integer1);
        this.bufInt.put(integer5 + integer6, integer2);
        this.bufInt.put(integer5 + integer6 * 2, integer3);
        this.bufInt.put(integer5 + integer6 * 3, integer4);
    }
    
    public void postPosition(final double x, final double y, final double z) {
        final int integer7 = this.format.getVertexSizeInteger();
        final int integer8 = (this.vertexCount - 4) * integer7;
        for (int integer9 = 0; integer9 < 4; ++integer9) {
            final int integer10 = integer8 + integer9 * integer7;
            final int integer11 = integer10 + 1;
            final int integer12 = integer11 + 1;
            this.bufInt.put(integer10, Float.floatToRawIntBits((float)(x + this.offsetX) + Float.intBitsToFloat(this.bufInt.get(integer10))));
            this.bufInt.put(integer11, Float.floatToRawIntBits((float)(y + this.offsetY) + Float.intBitsToFloat(this.bufInt.get(integer11))));
            this.bufInt.put(integer12, Float.floatToRawIntBits((float)(z + this.offsetZ) + Float.intBitsToFloat(this.bufInt.get(integer12))));
        }
    }
    
    private int getColorIndex(final int integer) {
        return ((this.vertexCount - integer) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
    }
    
    public void multiplyColor(final float red, final float green, final float blue, final int index) {
        final int integer5 = this.getColorIndex(index);
        int integer6 = -1;
        if (!this.colorDisabled) {
            integer6 = this.bufInt.get(integer5);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                final int integer7 = (int)((integer6 & 0xFF) * red);
                final int integer8 = (int)((integer6 >> 8 & 0xFF) * green);
                final int integer9 = (int)((integer6 >> 16 & 0xFF) * blue);
                integer6 &= 0xFF000000;
                integer6 |= (integer9 << 16 | integer8 << 8 | integer7);
            }
            else {
                final int integer7 = (int)((integer6 >> 24 & 0xFF) * red);
                final int integer8 = (int)((integer6 >> 16 & 0xFF) * green);
                final int integer9 = (int)((integer6 >> 8 & 0xFF) * blue);
                integer6 &= 0xFF;
                integer6 |= (integer7 << 24 | integer8 << 16 | integer9 << 8);
            }
        }
        this.bufInt.put(integer5, integer6);
    }
    
    private void setColor(final int color, final int index) {
        final int integer3 = this.getColorIndex(index);
        final int integer4 = color >> 16 & 0xFF;
        final int integer5 = color >> 8 & 0xFF;
        final int integer6 = color & 0xFF;
        this.setColor(integer3, integer4, integer5, integer6);
    }
    
    public void setColor(final float red, final float green, final float blue, final int index) {
        final int integer5 = this.getColorIndex(index);
        final int integer6 = clamp((int)(red * 255.0f), 0, 255);
        final int integer7 = clamp((int)(green * 255.0f), 0, 255);
        final int integer8 = clamp((int)(blue * 255.0f), 0, 255);
        this.setColor(integer5, integer6, integer7, integer8);
    }
    
    private static int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    private void setColor(final int colorIndex, final int red, final int blue, final int index) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.bufInt.put(colorIndex, 0xFF000000 | index << 16 | blue << 8 | red);
        }
        else {
            this.bufInt.put(colorIndex, red << 24 | blue << 16 | index << 8 | 0xFF);
        }
    }
    
    public void disableColor() {
        this.colorDisabled = true;
    }
    
    public BufferBuilder color(final float red, final float green, final float blue, final float float4) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(float4 * 255.0f));
    }
    
    public BufferBuilder color(final int red, final int green, final int blue, final int integer4) {
        if (this.colorDisabled) {
            return this;
        }
        final int integer5 = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.bufByte.putFloat(integer5, red / 255.0f);
                this.bufByte.putFloat(integer5 + 4, green / 255.0f);
                this.bufByte.putFloat(integer5 + 8, blue / 255.0f);
                this.bufByte.putFloat(integer5 + 12, integer4 / 255.0f);
                break;
            }
            case UNSIGNED_INT:
            case INT: {
                this.bufByte.putFloat(integer5, (float)red);
                this.bufByte.putFloat(integer5 + 4, (float)green);
                this.bufByte.putFloat(integer5 + 8, (float)blue);
                this.bufByte.putFloat(integer5 + 12, (float)integer4);
                break;
            }
            case UNSIGNED_SHORT:
            case SHORT: {
                this.bufByte.putShort(integer5, (short)red);
                this.bufByte.putShort(integer5 + 2, (short)green);
                this.bufByte.putShort(integer5 + 4, (short)blue);
                this.bufByte.putShort(integer5 + 6, (short)integer4);
                break;
            }
            case UNSIGNED_BYTE:
            case BYTE: {
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.bufByte.put(integer5, (byte)red);
                    this.bufByte.put(integer5 + 1, (byte)green);
                    this.bufByte.put(integer5 + 2, (byte)blue);
                    this.bufByte.put(integer5 + 3, (byte)integer4);
                    break;
                }
                this.bufByte.put(integer5, (byte)integer4);
                this.bufByte.put(integer5 + 1, (byte)blue);
                this.bufByte.put(integer5 + 2, (byte)green);
                this.bufByte.put(integer5 + 3, (byte)red);
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void putVertexData(final int[] arr) {
        this.grow(arr.length * 4 + this.format.getVertexSize());
        this.bufInt.position(this.getCurrentSize());
        this.bufInt.put(arr);
        this.vertexCount += arr.length / this.format.getVertexSizeInteger();
    }
    
    public void next() {
        ++this.vertexCount;
        this.grow(this.format.getVertexSize());
    }
    
    public BufferBuilder vertex(final double x, final double y, final double z) {
        final int integer7 = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.bufByte.putFloat(integer7, (float)(x + this.offsetX));
                this.bufByte.putFloat(integer7 + 4, (float)(y + this.offsetY));
                this.bufByte.putFloat(integer7 + 8, (float)(z + this.offsetZ));
                break;
            }
            case UNSIGNED_INT:
            case INT: {
                this.bufByte.putInt(integer7, Float.floatToRawIntBits((float)(x + this.offsetX)));
                this.bufByte.putInt(integer7 + 4, Float.floatToRawIntBits((float)(y + this.offsetY)));
                this.bufByte.putInt(integer7 + 8, Float.floatToRawIntBits((float)(z + this.offsetZ)));
                break;
            }
            case UNSIGNED_SHORT:
            case SHORT: {
                this.bufByte.putShort(integer7, (short)(x + this.offsetX));
                this.bufByte.putShort(integer7 + 2, (short)(y + this.offsetY));
                this.bufByte.putShort(integer7 + 4, (short)(z + this.offsetZ));
                break;
            }
            case UNSIGNED_BYTE:
            case BYTE: {
                this.bufByte.put(integer7, (byte)(x + this.offsetX));
                this.bufByte.put(integer7 + 1, (byte)(y + this.offsetY));
                this.bufByte.put(integer7 + 2, (byte)(z + this.offsetZ));
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void postNormal(final float x, final float y, final float z) {
        final int integer4 = (byte)(x * 127.0f) & 0xFF;
        final int integer5 = (byte)(y * 127.0f) & 0xFF;
        final int integer6 = (byte)(z * 127.0f) & 0xFF;
        final int integer7 = integer4 | integer5 << 8 | integer6 << 16;
        final int integer8 = this.format.getVertexSize() >> 2;
        final int integer9 = (this.vertexCount - 4) * integer8 + this.format.getNormalOffset() / 4;
        this.bufInt.put(integer9, integer7);
        this.bufInt.put(integer9 + integer8, integer7);
        this.bufInt.put(integer9 + integer8 * 2, integer7);
        this.bufInt.put(integer9 + integer8 * 3, integer7);
    }
    
    private void nextElement() {
        ++this.currentElementId;
        this.currentElementId %= this.format.getElementCount();
        this.currentElement = this.format.getElement(this.currentElementId);
        if (this.currentElement.getType() == VertexFormatElement.Type.PADDING) {
            this.nextElement();
        }
    }
    
    public BufferBuilder normal(final float x, final float y, final float z) {
        final int integer4 = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.bufByte.putFloat(integer4, x);
                this.bufByte.putFloat(integer4 + 4, y);
                this.bufByte.putFloat(integer4 + 8, z);
                break;
            }
            case UNSIGNED_INT:
            case INT: {
                this.bufByte.putInt(integer4, (int)x);
                this.bufByte.putInt(integer4 + 4, (int)y);
                this.bufByte.putInt(integer4 + 8, (int)z);
                break;
            }
            case UNSIGNED_SHORT:
            case SHORT: {
                this.bufByte.putShort(integer4, (short)((int)x * 32767 & 0xFFFF));
                this.bufByte.putShort(integer4 + 2, (short)((int)y * 32767 & 0xFFFF));
                this.bufByte.putShort(integer4 + 4, (short)((int)z * 32767 & 0xFFFF));
                break;
            }
            case UNSIGNED_BYTE:
            case BYTE: {
                this.bufByte.put(integer4, (byte)((int)x * 127 & 0xFF));
                this.bufByte.put(integer4 + 1, (byte)((int)y * 127 & 0xFF));
                this.bufByte.put(integer4 + 2, (byte)((int)z * 127 & 0xFF));
                break;
            }
        }
        this.nextElement();
        return this;
    }
    
    public void setOffset(final double x, final double y, final double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }
    
    public void end() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        this.building = false;
        this.bufByte.position(0);
        this.bufByte.limit(this.getCurrentSize() * 4);
    }
    
    public ByteBuffer getByteBuffer() {
        return this.bufByte;
    }
    
    public VertexFormat getVertexFormat() {
        return this.format;
    }
    
    public int getVertexCount() {
        return this.vertexCount;
    }
    
    public int getDrawMode() {
        return this.drawMode;
    }
    
    public void setQuadColor(final int color) {
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            this.setColor(color, integer2 + 1);
        }
    }
    
    public void setQuadColor(final float red, final float green, final float blue) {
        for (int integer4 = 0; integer4 < 4; ++integer4) {
            this.setColor(red, green, blue, integer4 + 1);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public class State
    {
        private final int[] rawBuffer;
        private final VertexFormat format;
        
        public State(final int[] rawBuffer, final VertexFormat format) {
            this.rawBuffer = rawBuffer;
            this.format = format;
        }
        
        public int[] getRawBuffer() {
            return this.rawBuffer;
        }
        
        public int getVertexCount() {
            return this.rawBuffer.length / this.format.getVertexSizeInteger();
        }
        
        public VertexFormat getFormat() {
            return this.format;
        }
    }
}
