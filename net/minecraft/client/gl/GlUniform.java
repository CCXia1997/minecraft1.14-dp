package net.minecraft.client.gl;

import org.apache.logging.log4j.LogManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.util.math.Matrix4f;
import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlUniform extends Uniform implements AutoCloseable
{
    private static final Logger LOGGER;
    private int loc;
    private final int count;
    private final int dataType;
    private final IntBuffer intData;
    private final FloatBuffer floatData;
    private final String name;
    private boolean stateDirty;
    private final GlProgram program;
    
    public GlUniform(final String name, final int dataType, final int count, final GlProgram program) {
        this.name = name;
        this.count = count;
        this.dataType = dataType;
        this.program = program;
        if (dataType <= 3) {
            this.intData = MemoryUtil.memAllocInt(count);
            this.floatData = null;
        }
        else {
            this.intData = null;
            this.floatData = MemoryUtil.memAllocFloat(count);
        }
        this.loc = -1;
        this.markStateDirty();
    }
    
    @Override
    public void close() {
        if (this.intData != null) {
            MemoryUtil.memFree((Buffer)this.intData);
        }
        if (this.floatData != null) {
            MemoryUtil.memFree((Buffer)this.floatData);
        }
    }
    
    private void markStateDirty() {
        this.stateDirty = true;
        if (this.program != null) {
            this.program.markUniformsDirty();
        }
    }
    
    public static int getTypeIndex(final String typeName) {
        int integer2 = -1;
        if ("int".equals(typeName)) {
            integer2 = 0;
        }
        else if ("float".equals(typeName)) {
            integer2 = 4;
        }
        else if (typeName.startsWith("matrix")) {
            if (typeName.endsWith("2x2")) {
                integer2 = 8;
            }
            else if (typeName.endsWith("3x3")) {
                integer2 = 9;
            }
            else if (typeName.endsWith("4x4")) {
                integer2 = 10;
            }
        }
        return integer2;
    }
    
    public void setLoc(final int integer) {
        this.loc = integer;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public void set(final float value1) {
        this.floatData.position(0);
        this.floatData.put(0, value1);
        this.markStateDirty();
    }
    
    @Override
    public void set(final float value1, final float value2) {
        this.floatData.position(0);
        this.floatData.put(0, value1);
        this.floatData.put(1, value2);
        this.markStateDirty();
    }
    
    @Override
    public void set(final float value1, final float value2, final float value3) {
        this.floatData.position(0);
        this.floatData.put(0, value1);
        this.floatData.put(1, value2);
        this.floatData.put(2, value3);
        this.markStateDirty();
    }
    
    @Override
    public void set(final float value1, final float value2, final float value3, final float value4) {
        this.floatData.position(0);
        this.floatData.put(value1);
        this.floatData.put(value2);
        this.floatData.put(value3);
        this.floatData.put(value4);
        this.floatData.flip();
        this.markStateDirty();
    }
    
    @Override
    public void setForDataType(final float value1, final float value2, final float value3, final float value4) {
        this.floatData.position(0);
        if (this.dataType >= 4) {
            this.floatData.put(0, value1);
        }
        if (this.dataType >= 5) {
            this.floatData.put(1, value2);
        }
        if (this.dataType >= 6) {
            this.floatData.put(2, value3);
        }
        if (this.dataType >= 7) {
            this.floatData.put(3, value4);
        }
        this.markStateDirty();
    }
    
    @Override
    public void set(final int value1, final int value2, final int value3, final int value4) {
        this.intData.position(0);
        if (this.dataType >= 0) {
            this.intData.put(0, value1);
        }
        if (this.dataType >= 1) {
            this.intData.put(1, value2);
        }
        if (this.dataType >= 2) {
            this.intData.put(2, value3);
        }
        if (this.dataType >= 3) {
            this.intData.put(3, value4);
        }
        this.markStateDirty();
    }
    
    @Override
    public void set(final float[] values) {
        if (values.length < this.count) {
            GlUniform.LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, values.length);
            return;
        }
        this.floatData.position(0);
        this.floatData.put(values);
        this.floatData.position(0);
        this.markStateDirty();
    }
    
    @Override
    public void set(final Matrix4f values) {
        this.floatData.position(0);
        values.putIntoBuffer(this.floatData);
        this.markStateDirty();
    }
    
    public void upload() {
        if (!this.stateDirty) {}
        this.stateDirty = false;
        if (this.dataType <= 3) {
            this.uploadInts();
        }
        else if (this.dataType <= 7) {
            this.uploadFloats();
        }
        else {
            if (this.dataType > 10) {
                GlUniform.LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", this.dataType);
                return;
            }
            this.uploadMatrix();
        }
    }
    
    private void uploadInts() {
        this.floatData.clear();
        switch (this.dataType) {
            case 0: {
                GLX.glUniform1(this.loc, this.intData);
                break;
            }
            case 1: {
                GLX.glUniform2(this.loc, this.intData);
                break;
            }
            case 2: {
                GLX.glUniform3(this.loc, this.intData);
                break;
            }
            case 3: {
                GLX.glUniform4(this.loc, this.intData);
                break;
            }
            default: {
                GlUniform.LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.count);
                break;
            }
        }
    }
    
    private void uploadFloats() {
        this.floatData.clear();
        switch (this.dataType) {
            case 4: {
                GLX.glUniform1(this.loc, this.floatData);
                break;
            }
            case 5: {
                GLX.glUniform2(this.loc, this.floatData);
                break;
            }
            case 6: {
                GLX.glUniform3(this.loc, this.floatData);
                break;
            }
            case 7: {
                GLX.glUniform4(this.loc, this.floatData);
                break;
            }
            default: {
                GlUniform.LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.count);
                break;
            }
        }
    }
    
    private void uploadMatrix() {
        this.floatData.clear();
        switch (this.dataType) {
            case 8: {
                GLX.glUniformMatrix2(this.loc, false, this.floatData);
                break;
            }
            case 9: {
                GLX.glUniformMatrix3(this.loc, false, this.floatData);
                break;
            }
            case 10: {
                GLX.glUniformMatrix4(this.loc, false, this.floatData);
                break;
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
