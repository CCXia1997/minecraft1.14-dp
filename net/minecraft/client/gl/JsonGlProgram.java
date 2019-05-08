package net.minecraft.client.gl;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.client.texture.Texture;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.resource.Resource;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import com.mojang.blaze3d.platform.GLX;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.io.Reader;
import net.minecraft.util.JsonHelper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.util.Identifier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resource.ResourceManager;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class JsonGlProgram implements GlProgram, AutoCloseable
{
    private static final Logger LOGGER;
    private static final Uniform dummyUniform;
    private static JsonGlProgram activeProgram;
    private static int activeProgramRef;
    private final Map<String, Object> samplerBinds;
    private final List<String> samplerNames;
    private final List<Integer> samplerShaderLocs;
    private final List<GlUniform> uniformData;
    private final List<Integer> uniformLocs;
    private final Map<String, GlUniform> uniformByName;
    private final int programRef;
    private final String name;
    private final boolean useCullFace;
    private boolean uniformStateDirty;
    private final GlBlendState blendState;
    private final List<Integer> attribLocs;
    private final List<String> attribNames;
    private final GlShader vertexShader;
    private final GlShader fragmentShader;
    
    public JsonGlProgram(final ResourceManager resource, final String name) throws IOException {
        this.samplerBinds = Maps.newHashMap();
        this.samplerNames = Lists.newArrayList();
        this.samplerShaderLocs = Lists.newArrayList();
        this.uniformData = Lists.newArrayList();
        this.uniformLocs = Lists.newArrayList();
        this.uniformByName = Maps.newHashMap();
        final Identifier identifier3 = new Identifier("shaders/program/" + name + ".json");
        this.name = name;
        Resource resource2 = null;
        try {
            resource2 = resource.getResource(identifier3);
            final JsonObject jsonObject5 = JsonHelper.deserialize(new InputStreamReader(resource2.getInputStream(), StandardCharsets.UTF_8));
            final String string6 = JsonHelper.getString(jsonObject5, "vertex");
            final String string7 = JsonHelper.getString(jsonObject5, "fragment");
            final JsonArray jsonArray8 = JsonHelper.getArray(jsonObject5, "samplers", null);
            if (jsonArray8 != null) {
                int integer9 = 0;
                for (final JsonElement jsonElement11 : jsonArray8) {
                    try {
                        this.addSampler(jsonElement11);
                    }
                    catch (Exception exception12) {
                        final ShaderParseException shaderParseException13 = ShaderParseException.wrap(exception12);
                        shaderParseException13.addFaultyElement("samplers[" + integer9 + "]");
                        throw shaderParseException13;
                    }
                    ++integer9;
                }
            }
            final JsonArray jsonArray9 = JsonHelper.getArray(jsonObject5, "attributes", null);
            if (jsonArray9 != null) {
                int integer10 = 0;
                this.attribLocs = Lists.newArrayListWithCapacity(jsonArray9.size());
                this.attribNames = Lists.newArrayListWithCapacity(jsonArray9.size());
                for (final JsonElement jsonElement12 : jsonArray9) {
                    try {
                        this.attribNames.add(JsonHelper.asString(jsonElement12, "attribute"));
                    }
                    catch (Exception exception13) {
                        final ShaderParseException shaderParseException14 = ShaderParseException.wrap(exception13);
                        shaderParseException14.addFaultyElement("attributes[" + integer10 + "]");
                        throw shaderParseException14;
                    }
                    ++integer10;
                }
            }
            else {
                this.attribLocs = null;
                this.attribNames = null;
            }
            final JsonArray jsonArray10 = JsonHelper.getArray(jsonObject5, "uniforms", null);
            if (jsonArray10 != null) {
                int integer11 = 0;
                for (final JsonElement jsonElement13 : jsonArray10) {
                    try {
                        this.addUniform(jsonElement13);
                    }
                    catch (Exception exception14) {
                        final ShaderParseException shaderParseException15 = ShaderParseException.wrap(exception14);
                        shaderParseException15.addFaultyElement("uniforms[" + integer11 + "]");
                        throw shaderParseException15;
                    }
                    ++integer11;
                }
            }
            this.blendState = deserializeBlendState(JsonHelper.getObject(jsonObject5, "blend", null));
            this.useCullFace = JsonHelper.getBoolean(jsonObject5, "cull", true);
            this.vertexShader = getShader(resource, GlShader.Type.VERTEX, string6);
            this.fragmentShader = getShader(resource, GlShader.Type.FRAGMENT, string7);
            this.programRef = GlProgramManager.getInstance().createProgram();
            GlProgramManager.getInstance().linkProgram(this);
            this.finalizeUniformsAndSamplers();
            if (this.attribNames != null) {
                for (final String string8 : this.attribNames) {
                    final int integer12 = GLX.glGetAttribLocation(this.programRef, string8);
                    this.attribLocs.add(integer12);
                }
            }
        }
        catch (Exception exception15) {
            final ShaderParseException shaderParseException16 = ShaderParseException.wrap(exception15);
            shaderParseException16.addFaultyFile(identifier3.getPath());
            throw shaderParseException16;
        }
        finally {
            IOUtils.closeQuietly((Closeable)resource2);
        }
        this.markUniformsDirty();
    }
    
    public static GlShader getShader(final ResourceManager resourceManager, final GlShader.Type type, final String name) throws IOException {
        GlShader glShader4 = type.getLoadedShaders().get(name);
        if (glShader4 == null) {
            final Identifier identifier5 = new Identifier("shaders/program/" + name + type.getFileExtension());
            final Resource resource6 = resourceManager.getResource(identifier5);
            try {
                glShader4 = GlShader.createFromResource(type, name, resource6.getInputStream());
            }
            finally {
                IOUtils.closeQuietly((Closeable)resource6);
            }
        }
        return glShader4;
    }
    
    public static GlBlendState deserializeBlendState(final JsonObject json) {
        if (json == null) {
            return new GlBlendState();
        }
        int integer2 = 32774;
        int integer3 = 1;
        int integer4 = 0;
        int integer5 = 1;
        int integer6 = 0;
        boolean boolean7 = true;
        boolean boolean8 = false;
        if (JsonHelper.hasString(json, "func")) {
            integer2 = GlBlendState.getFuncFromString(json.get("func").getAsString());
            if (integer2 != 32774) {
                boolean7 = false;
            }
        }
        if (JsonHelper.hasString(json, "srcrgb")) {
            integer3 = GlBlendState.getComponentFromString(json.get("srcrgb").getAsString());
            if (integer3 != 1) {
                boolean7 = false;
            }
        }
        if (JsonHelper.hasString(json, "dstrgb")) {
            integer4 = GlBlendState.getComponentFromString(json.get("dstrgb").getAsString());
            if (integer4 != 0) {
                boolean7 = false;
            }
        }
        if (JsonHelper.hasString(json, "srcalpha")) {
            integer5 = GlBlendState.getComponentFromString(json.get("srcalpha").getAsString());
            if (integer5 != 1) {
                boolean7 = false;
            }
            boolean8 = true;
        }
        if (JsonHelper.hasString(json, "dstalpha")) {
            integer6 = GlBlendState.getComponentFromString(json.get("dstalpha").getAsString());
            if (integer6 != 0) {
                boolean7 = false;
            }
            boolean8 = true;
        }
        if (boolean7) {
            return new GlBlendState();
        }
        if (boolean8) {
            return new GlBlendState(integer3, integer4, integer5, integer6, integer2);
        }
        return new GlBlendState(integer3, integer4, integer2);
    }
    
    @Override
    public void close() {
        for (final GlUniform glUniform2 : this.uniformData) {
            glUniform2.close();
        }
        GlProgramManager.getInstance().deleteProgram(this);
    }
    
    public void disable() {
        GLX.glUseProgram(0);
        JsonGlProgram.activeProgramRef = -1;
        JsonGlProgram.activeProgram = null;
        for (int integer1 = 0; integer1 < this.samplerShaderLocs.size(); ++integer1) {
            if (this.samplerBinds.get(this.samplerNames.get(integer1)) != null) {
                GlStateManager.activeTexture(GLX.GL_TEXTURE0 + integer1);
                GlStateManager.bindTexture(0);
            }
        }
    }
    
    public void enable() {
        this.uniformStateDirty = false;
        JsonGlProgram.activeProgram = this;
        this.blendState.enable();
        if (this.programRef != JsonGlProgram.activeProgramRef) {
            GLX.glUseProgram(this.programRef);
            JsonGlProgram.activeProgramRef = this.programRef;
        }
        if (this.useCullFace) {
            GlStateManager.enableCull();
        }
        else {
            GlStateManager.disableCull();
        }
        for (int integer1 = 0; integer1 < this.samplerShaderLocs.size(); ++integer1) {
            if (this.samplerBinds.get(this.samplerNames.get(integer1)) != null) {
                GlStateManager.activeTexture(GLX.GL_TEXTURE0 + integer1);
                GlStateManager.enableTexture();
                final Object object2 = this.samplerBinds.get(this.samplerNames.get(integer1));
                int integer2 = -1;
                if (object2 instanceof GlFramebuffer) {
                    integer2 = ((GlFramebuffer)object2).colorAttachment;
                }
                else if (object2 instanceof Texture) {
                    integer2 = ((Texture)object2).getGlId();
                }
                else if (object2 instanceof Integer) {
                    integer2 = (int)object2;
                }
                if (integer2 != -1) {
                    GlStateManager.bindTexture(integer2);
                    GLX.glUniform1i(GLX.glGetUniformLocation(this.programRef, this.samplerNames.get(integer1)), integer1);
                }
            }
        }
        for (final GlUniform glUniform2 : this.uniformData) {
            glUniform2.upload();
        }
    }
    
    @Override
    public void markUniformsDirty() {
        this.uniformStateDirty = true;
    }
    
    @Nullable
    public GlUniform getUniformByName(final String name) {
        return this.uniformByName.get(name);
    }
    
    public Uniform getUniformByNameOrDummy(final String name) {
        final GlUniform glUniform2 = this.getUniformByName(name);
        return (glUniform2 == null) ? JsonGlProgram.dummyUniform : glUniform2;
    }
    
    private void finalizeUniformsAndSamplers() {
        for (int integer1 = 0, integer2 = 0; integer1 < this.samplerNames.size(); ++integer1, ++integer2) {
            final String string3 = this.samplerNames.get(integer1);
            final int integer3 = GLX.glGetUniformLocation(this.programRef, string3);
            if (integer3 == -1) {
                JsonGlProgram.LOGGER.warn("Shader {}could not find sampler named {} in the specified shader program.", this.name, string3);
                this.samplerBinds.remove(string3);
                this.samplerNames.remove(integer2);
                --integer2;
            }
            else {
                this.samplerShaderLocs.add(integer3);
            }
        }
        for (final GlUniform glUniform2 : this.uniformData) {
            final String string3 = glUniform2.getName();
            final int integer3 = GLX.glGetUniformLocation(this.programRef, string3);
            if (integer3 == -1) {
                JsonGlProgram.LOGGER.warn("Could not find uniform named {} in the specified shader program.", string3);
            }
            else {
                this.uniformLocs.add(integer3);
                glUniform2.setLoc(integer3);
                this.uniformByName.put(string3, glUniform2);
            }
        }
    }
    
    private void addSampler(final JsonElement jsonElement) {
        final JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "sampler");
        final String string3 = JsonHelper.getString(jsonObject2, "name");
        if (!JsonHelper.hasString(jsonObject2, "file")) {
            this.samplerBinds.put(string3, null);
            this.samplerNames.add(string3);
            return;
        }
        this.samplerNames.add(string3);
    }
    
    public void bindSampler(final String samplerName, final Object object) {
        if (this.samplerBinds.containsKey(samplerName)) {
            this.samplerBinds.remove(samplerName);
        }
        this.samplerBinds.put(samplerName, object);
        this.markUniformsDirty();
    }
    
    private void addUniform(final JsonElement jsonElement) throws ShaderParseException {
        final JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "uniform");
        final String string3 = JsonHelper.getString(jsonObject2, "name");
        final int integer4 = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject2, "type"));
        final int integer5 = JsonHelper.getInt(jsonObject2, "count");
        final float[] arr6 = new float[Math.max(integer5, 16)];
        final JsonArray jsonArray7 = JsonHelper.getArray(jsonObject2, "values");
        if (jsonArray7.size() != integer5 && jsonArray7.size() > 1) {
            throw new ShaderParseException("Invalid amount of values specified (expected " + integer5 + ", found " + jsonArray7.size() + ")");
        }
        int integer6 = 0;
        for (final JsonElement jsonElement2 : jsonArray7) {
            try {
                arr6[integer6] = JsonHelper.asFloat(jsonElement2, "value");
            }
            catch (Exception exception11) {
                final ShaderParseException shaderParseException12 = ShaderParseException.wrap(exception11);
                shaderParseException12.addFaultyElement("values[" + integer6 + "]");
                throw shaderParseException12;
            }
            ++integer6;
        }
        if (integer5 > 1 && jsonArray7.size() == 1) {
            while (integer6 < integer5) {
                arr6[integer6] = arr6[0];
                ++integer6;
            }
        }
        final int integer7 = (integer5 > 1 && integer5 <= 4 && integer4 < 8) ? (integer5 - 1) : 0;
        final GlUniform glUniform10 = new GlUniform(string3, integer4 + integer7, integer5, this);
        if (integer4 <= 3) {
            glUniform10.set((int)arr6[0], (int)arr6[1], (int)arr6[2], (int)arr6[3]);
        }
        else if (integer4 <= 7) {
            glUniform10.setForDataType(arr6[0], arr6[1], arr6[2], arr6[3]);
        }
        else {
            glUniform10.set(arr6);
        }
        this.uniformData.add(glUniform10);
    }
    
    @Override
    public GlShader getVertexShader() {
        return this.vertexShader;
    }
    
    @Override
    public GlShader getFragmentShader() {
        return this.fragmentShader;
    }
    
    @Override
    public int getProgramRef() {
        return this.programRef;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        dummyUniform = new Uniform();
        JsonGlProgram.activeProgramRef = -1;
    }
}
