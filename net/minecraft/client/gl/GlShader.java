package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.InputStream;
import com.mojang.blaze3d.platform.GLX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlShader
{
    private final Type shaderType;
    private final String name;
    private final int shaderRef;
    private int refCount;
    
    private GlShader(final Type shaderType, final int shaderRef, final String name) {
        this.shaderType = shaderType;
        this.shaderRef = shaderRef;
        this.name = name;
    }
    
    public void attachTo(final GlProgram glProgram) {
        ++this.refCount;
        GLX.glAttachShader(glProgram.getProgramRef(), this.shaderRef);
    }
    
    public void release() {
        --this.refCount;
        if (this.refCount <= 0) {
            GLX.glDeleteShader(this.shaderRef);
            this.shaderType.getLoadedShaders().remove(this.name);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public static GlShader createFromResource(final Type type, final String name, final InputStream sourceCode) throws IOException {
        final String string4 = TextureUtil.readResourceAsString(sourceCode);
        if (string4 == null) {
            throw new IOException("Could not load program " + type.getName());
        }
        final int integer5 = GLX.glCreateShader(type.getGlType());
        GLX.glShaderSource(integer5, string4);
        GLX.glCompileShader(integer5);
        if (GLX.glGetShaderi(integer5, GLX.GL_COMPILE_STATUS) == 0) {
            final String string5 = StringUtils.trim(GLX.glGetShaderInfoLog(integer5, 32768));
            throw new IOException("Couldn't compile " + type.getName() + " program: " + string5);
        }
        final GlShader glShader6 = new GlShader(type, integer5, name);
        type.getLoadedShaders().put(name, glShader6);
        return glShader6;
    }
    
    @Environment(EnvType.CLIENT)
    public enum Type
    {
        VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER), 
        FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);
        
        private final String name;
        private final String fileExtension;
        private final int glType;
        private final Map<String, GlShader> loadedShaders;
        
        private Type(final String string1, final String string2, final int integer3) {
            this.loadedShaders = Maps.newHashMap();
            this.name = string1;
            this.fileExtension = string2;
            this.glType = integer3;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getFileExtension() {
            return this.fileExtension;
        }
        
        private int getGlType() {
            return this.glType;
        }
        
        public Map<String, GlShader> getLoadedShaders() {
            return this.loadedShaders;
        }
    }
}
