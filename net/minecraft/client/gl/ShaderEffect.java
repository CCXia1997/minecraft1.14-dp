package net.minecraft.client.gl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Texture;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.FileNotFoundException;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonElement;
import java.io.Reader;
import net.minecraft.util.JsonHelper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import java.util.Map;
import java.util.List;
import net.minecraft.resource.ResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ShaderEffect implements AutoCloseable
{
    private final GlFramebuffer mainTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostProcessShader> passes;
    private final Map<String, GlFramebuffer> targetsByName;
    private final List<GlFramebuffer> defaultSizedTargets;
    private Matrix4f projectionMatrix;
    private int width;
    private int height;
    private float time;
    private float lastTickDelta;
    
    public ShaderEffect(final TextureManager textureManager, final ResourceManager resourceManager, final GlFramebuffer framebuffer, final Identifier location) throws IOException, JsonSyntaxException {
        this.passes = Lists.newArrayList();
        this.targetsByName = Maps.newHashMap();
        this.defaultSizedTargets = Lists.newArrayList();
        this.resourceManager = resourceManager;
        this.mainTarget = framebuffer;
        this.time = 0.0f;
        this.lastTickDelta = 0.0f;
        this.width = framebuffer.viewWidth;
        this.height = framebuffer.viewHeight;
        this.name = location.toString();
        this.setupProjectionMatrix();
        this.parseEffect(textureManager, location);
    }
    
    private void parseEffect(final TextureManager textureManager, final Identifier location) throws IOException, JsonSyntaxException {
        Resource resource3 = null;
        try {
            resource3 = this.resourceManager.getResource(location);
            final JsonObject jsonObject4 = JsonHelper.deserialize(new InputStreamReader(resource3.getInputStream(), StandardCharsets.UTF_8));
            if (JsonHelper.hasArray(jsonObject4, "targets")) {
                final JsonArray jsonArray5 = jsonObject4.getAsJsonArray("targets");
                int integer6 = 0;
                for (final JsonElement jsonElement8 : jsonArray5) {
                    try {
                        this.parseTarget(jsonElement8);
                    }
                    catch (Exception exception9) {
                        final ShaderParseException shaderParseException10 = ShaderParseException.wrap(exception9);
                        shaderParseException10.addFaultyElement("targets[" + integer6 + "]");
                        throw shaderParseException10;
                    }
                    ++integer6;
                }
            }
            if (JsonHelper.hasArray(jsonObject4, "passes")) {
                final JsonArray jsonArray5 = jsonObject4.getAsJsonArray("passes");
                int integer6 = 0;
                for (final JsonElement jsonElement8 : jsonArray5) {
                    try {
                        this.parsePass(textureManager, jsonElement8);
                    }
                    catch (Exception exception9) {
                        final ShaderParseException shaderParseException10 = ShaderParseException.wrap(exception9);
                        shaderParseException10.addFaultyElement("passes[" + integer6 + "]");
                        throw shaderParseException10;
                    }
                    ++integer6;
                }
            }
        }
        catch (Exception exception10) {
            final ShaderParseException shaderParseException11 = ShaderParseException.wrap(exception10);
            shaderParseException11.addFaultyFile(location.getPath());
            throw shaderParseException11;
        }
        finally {
            IOUtils.closeQuietly((Closeable)resource3);
        }
    }
    
    private void parseTarget(final JsonElement jsonTarget) throws ShaderParseException {
        if (JsonHelper.isString(jsonTarget)) {
            this.addTarget(jsonTarget.getAsString(), this.width, this.height);
        }
        else {
            final JsonObject jsonObject2 = JsonHelper.asObject(jsonTarget, "target");
            final String string3 = JsonHelper.getString(jsonObject2, "name");
            final int integer4 = JsonHelper.getInt(jsonObject2, "width", this.width);
            final int integer5 = JsonHelper.getInt(jsonObject2, "height", this.height);
            if (this.targetsByName.containsKey(string3)) {
                throw new ShaderParseException(string3 + " is already defined");
            }
            this.addTarget(string3, integer4, integer5);
        }
    }
    
    private void parsePass(final TextureManager textureManager, final JsonElement jsonPass) throws IOException {
        final JsonObject jsonObject3 = JsonHelper.asObject(jsonPass, "pass");
        final String string4 = JsonHelper.getString(jsonObject3, "name");
        final String string5 = JsonHelper.getString(jsonObject3, "intarget");
        final String string6 = JsonHelper.getString(jsonObject3, "outtarget");
        final GlFramebuffer glFramebuffer7 = this.getTarget(string5);
        final GlFramebuffer glFramebuffer8 = this.getTarget(string6);
        if (glFramebuffer7 == null) {
            throw new ShaderParseException("Input target '" + string5 + "' does not exist");
        }
        if (glFramebuffer8 == null) {
            throw new ShaderParseException("Output target '" + string6 + "' does not exist");
        }
        final PostProcessShader postProcessShader9 = this.addPass(string4, glFramebuffer7, glFramebuffer8);
        final JsonArray jsonArray10 = JsonHelper.getArray(jsonObject3, "auxtargets", null);
        if (jsonArray10 != null) {
            int integer11 = 0;
            for (final JsonElement jsonElement13 : jsonArray10) {
                try {
                    final JsonObject jsonObject4 = JsonHelper.asObject(jsonElement13, "auxtarget");
                    final String string7 = JsonHelper.getString(jsonObject4, "name");
                    final String string8 = JsonHelper.getString(jsonObject4, "id");
                    final GlFramebuffer glFramebuffer9 = this.getTarget(string8);
                    if (glFramebuffer9 == null) {
                        final Identifier identifier18 = new Identifier("textures/effect/" + string8 + ".png");
                        Resource resource19 = null;
                        try {
                            resource19 = this.resourceManager.getResource(identifier18);
                        }
                        catch (FileNotFoundException fileNotFoundException20) {
                            throw new ShaderParseException("Render target or texture '" + string8 + "' does not exist");
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)resource19);
                        }
                        textureManager.bindTexture(identifier18);
                        final Texture texture20 = textureManager.getTexture(identifier18);
                        final int integer12 = JsonHelper.getInt(jsonObject4, "width");
                        final int integer13 = JsonHelper.getInt(jsonObject4, "height");
                        final boolean boolean23 = JsonHelper.getBoolean(jsonObject4, "bilinear");
                        if (boolean23) {
                            GlStateManager.texParameter(3553, 10241, 9729);
                            GlStateManager.texParameter(3553, 10240, 9729);
                        }
                        else {
                            GlStateManager.texParameter(3553, 10241, 9728);
                            GlStateManager.texParameter(3553, 10240, 9728);
                        }
                        postProcessShader9.addAuxTarget(string7, texture20.getGlId(), integer12, integer13);
                    }
                    else {
                        postProcessShader9.addAuxTarget(string7, glFramebuffer9, glFramebuffer9.texWidth, glFramebuffer9.texHeight);
                    }
                }
                catch (Exception exception14) {
                    final ShaderParseException shaderParseException15 = ShaderParseException.wrap(exception14);
                    shaderParseException15.addFaultyElement("auxtargets[" + integer11 + "]");
                    throw shaderParseException15;
                }
                ++integer11;
            }
        }
        final JsonArray jsonArray11 = JsonHelper.getArray(jsonObject3, "uniforms", null);
        if (jsonArray11 != null) {
            int integer14 = 0;
            for (final JsonElement jsonElement14 : jsonArray11) {
                try {
                    this.parseUniform(jsonElement14);
                }
                catch (Exception exception15) {
                    final ShaderParseException shaderParseException16 = ShaderParseException.wrap(exception15);
                    shaderParseException16.addFaultyElement("uniforms[" + integer14 + "]");
                    throw shaderParseException16;
                }
                ++integer14;
            }
        }
    }
    
    private void parseUniform(final JsonElement jsonUniform) throws ShaderParseException {
        final JsonObject jsonObject2 = JsonHelper.asObject(jsonUniform, "uniform");
        final String string3 = JsonHelper.getString(jsonObject2, "name");
        final GlUniform glUniform4 = this.passes.get(this.passes.size() - 1).getProgram().getUniformByName(string3);
        if (glUniform4 == null) {
            throw new ShaderParseException("Uniform '" + string3 + "' does not exist");
        }
        final float[] arr5 = new float[4];
        int integer6 = 0;
        final JsonArray jsonArray7 = JsonHelper.getArray(jsonObject2, "values");
        for (final JsonElement jsonElement9 : jsonArray7) {
            try {
                arr5[integer6] = JsonHelper.asFloat(jsonElement9, "value");
            }
            catch (Exception exception10) {
                final ShaderParseException shaderParseException11 = ShaderParseException.wrap(exception10);
                shaderParseException11.addFaultyElement("values[" + integer6 + "]");
                throw shaderParseException11;
            }
            ++integer6;
        }
        switch (integer6) {
            case 1: {
                glUniform4.set(arr5[0]);
                break;
            }
            case 2: {
                glUniform4.set(arr5[0], arr5[1]);
                break;
            }
            case 3: {
                glUniform4.set(arr5[0], arr5[1], arr5[2]);
                break;
            }
            case 4: {
                glUniform4.set(arr5[0], arr5[1], arr5[2], arr5[3]);
                break;
            }
        }
    }
    
    public GlFramebuffer getSecondaryTarget(final String name) {
        return this.targetsByName.get(name);
    }
    
    public void addTarget(final String name, final int width, final int height) {
        final GlFramebuffer glFramebuffer4 = new GlFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        glFramebuffer4.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.targetsByName.put(name, glFramebuffer4);
        if (width == this.width && height == this.height) {
            this.defaultSizedTargets.add(glFramebuffer4);
        }
    }
    
    @Override
    public void close() {
        for (final GlFramebuffer glFramebuffer2 : this.targetsByName.values()) {
            glFramebuffer2.delete();
        }
        for (final PostProcessShader postProcessShader2 : this.passes) {
            postProcessShader2.close();
        }
        this.passes.clear();
    }
    
    public PostProcessShader addPass(final String programName, final GlFramebuffer source, final GlFramebuffer dest) throws IOException {
        final PostProcessShader postProcessShader4 = new PostProcessShader(this.resourceManager, programName, source, dest);
        this.passes.add(this.passes.size(), postProcessShader4);
        return postProcessShader4;
    }
    
    private void setupProjectionMatrix() {
        this.projectionMatrix = Matrix4f.projectionMatrix((float)this.mainTarget.texWidth, (float)this.mainTarget.texHeight, 0.1f, 1000.0f);
    }
    
    public void setupDimensions(final int targetsWidth, final int targetsHeight) {
        this.width = this.mainTarget.texWidth;
        this.height = this.mainTarget.texHeight;
        this.setupProjectionMatrix();
        for (final PostProcessShader postProcessShader4 : this.passes) {
            postProcessShader4.setProjectionMatrix(this.projectionMatrix);
        }
        for (final GlFramebuffer glFramebuffer4 : this.defaultSizedTargets) {
            glFramebuffer4.resize(targetsWidth, targetsHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }
    
    public void render(final float tickDelta) {
        if (tickDelta < this.lastTickDelta) {
            this.time += 1.0f - this.lastTickDelta;
            this.time += tickDelta;
        }
        else {
            this.time += tickDelta - this.lastTickDelta;
        }
        this.lastTickDelta = tickDelta;
        while (this.time > 20.0f) {
            this.time -= 20.0f;
        }
        for (final PostProcessShader postProcessShader3 : this.passes) {
            postProcessShader3.render(this.time / 20.0f);
        }
    }
    
    public final String getName() {
        return this.name;
    }
    
    private GlFramebuffer getTarget(final String name) {
        if (name == null) {
            return null;
        }
        if (name.equals("minecraft:main")) {
            return this.mainTarget;
        }
        return this.targetsByName.get(name);
    }
}
