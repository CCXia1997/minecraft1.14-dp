package net.minecraft.client.gl;

import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import com.mojang.blaze3d.platform.GLX;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlProgramManager
{
    private static final Logger LOGGER;
    private static GlProgramManager INSTANCE;
    
    public static void init() {
        GlProgramManager.INSTANCE = new GlProgramManager();
    }
    
    public static GlProgramManager getInstance() {
        return GlProgramManager.INSTANCE;
    }
    
    private GlProgramManager() {
    }
    
    public void deleteProgram(final GlProgram glProgram) {
        glProgram.getFragmentShader().release();
        glProgram.getVertexShader().release();
        GLX.glDeleteProgram(glProgram.getProgramRef());
    }
    
    public int createProgram() throws IOException {
        final int integer1 = GLX.glCreateProgram();
        if (integer1 <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + integer1 + ")");
        }
        return integer1;
    }
    
    public void linkProgram(final GlProgram glProgram) throws IOException {
        glProgram.getFragmentShader().attachTo(glProgram);
        glProgram.getVertexShader().attachTo(glProgram);
        GLX.glLinkProgram(glProgram.getProgramRef());
        final int integer2 = GLX.glGetProgrami(glProgram.getProgramRef(), GLX.GL_LINK_STATUS);
        if (integer2 == 0) {
            GlProgramManager.LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", glProgram.getVertexShader().getName(), glProgram.getFragmentShader().getName());
            GlProgramManager.LOGGER.warn(GLX.glGetProgramInfoLog(glProgram.getProgramRef(), 32768));
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
