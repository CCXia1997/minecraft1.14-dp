package net.minecraft.client.render.model.json;

import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Quaternion;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelTransformation
{
    public static final ModelTransformation NONE;
    public static float globalTranslationX;
    public static float globalTranslationY;
    public static float globalTranslationZ;
    public static float globalRotationX;
    public static float globalRotationY;
    public static float globalRotationZ;
    public static float globalScaleOffsetX;
    public static float globalScaleOffsetY;
    public static float globalScaleOffsetZ;
    public final Transformation thirdPersonLeftHand;
    public final Transformation thirdPersonRightHand;
    public final Transformation firstPersonLeftHand;
    public final Transformation firstPersonRightHand;
    public final Transformation head;
    public final Transformation gui;
    public final Transformation ground;
    public final Transformation fixed;
    
    private ModelTransformation() {
        this(Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE);
    }
    
    public ModelTransformation(final ModelTransformation other) {
        this.thirdPersonLeftHand = other.thirdPersonLeftHand;
        this.thirdPersonRightHand = other.thirdPersonRightHand;
        this.firstPersonLeftHand = other.firstPersonLeftHand;
        this.firstPersonRightHand = other.firstPersonRightHand;
        this.head = other.head;
        this.gui = other.gui;
        this.ground = other.ground;
        this.fixed = other.fixed;
    }
    
    public ModelTransformation(final Transformation thirdPersonLeftHand, final Transformation thirdPersonRightHand, final Transformation firstPersonLeftHand, final Transformation firstPersonRightHand, final Transformation head, final Transformation gui, final Transformation ground, final Transformation fixed) {
        this.thirdPersonLeftHand = thirdPersonLeftHand;
        this.thirdPersonRightHand = thirdPersonRightHand;
        this.firstPersonLeftHand = firstPersonLeftHand;
        this.firstPersonRightHand = firstPersonRightHand;
        this.head = head;
        this.gui = gui;
        this.ground = ground;
        this.fixed = fixed;
    }
    
    public void applyGl(final Type type) {
        applyGl(this.getTransformation(type), false);
    }
    
    public static void applyGl(final Transformation transform, final boolean leftyFlip) {
        if (transform == Transformation.NONE) {
            return;
        }
        final int integer3 = leftyFlip ? -1 : 1;
        GlStateManager.translatef(integer3 * (ModelTransformation.globalTranslationX + transform.translation.x()), ModelTransformation.globalTranslationY + transform.translation.y(), ModelTransformation.globalTranslationZ + transform.translation.z());
        final float float4 = ModelTransformation.globalRotationX + transform.rotation.x();
        float float5 = ModelTransformation.globalRotationY + transform.rotation.y();
        float float6 = ModelTransformation.globalRotationZ + transform.rotation.z();
        if (leftyFlip) {
            float5 = -float5;
            float6 = -float6;
        }
        GlStateManager.multMatrix(new Matrix4f(new Quaternion(float4, float5, float6, true)));
        GlStateManager.scalef(ModelTransformation.globalScaleOffsetX + transform.scale.x(), ModelTransformation.globalScaleOffsetY + transform.scale.y(), ModelTransformation.globalScaleOffsetZ + transform.scale.z());
    }
    
    public Transformation getTransformation(final Type type) {
        switch (type) {
            case b: {
                return this.thirdPersonLeftHand;
            }
            case c: {
                return this.thirdPersonRightHand;
            }
            case d: {
                return this.firstPersonLeftHand;
            }
            case e: {
                return this.firstPersonRightHand;
            }
            case f: {
                return this.head;
            }
            case g: {
                return this.gui;
            }
            case h: {
                return this.ground;
            }
            case FIXED: {
                return this.fixed;
            }
            default: {
                return Transformation.NONE;
            }
        }
    }
    
    public boolean isTransformationDefined(final Type type) {
        return this.getTransformation(type) != Transformation.NONE;
    }
    
    static {
        NONE = new ModelTransformation();
    }
    
    @Environment(EnvType.CLIENT)
    public enum Type
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g, 
        h, 
        FIXED;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelTransformation>
    {
        protected Deserializer() {
        }
        
        public ModelTransformation a(final JsonElement functionJson, final java.lang.reflect.Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final Transformation transformation5 = this.parseModelTransformation(context, jsonObject4, "thirdperson_righthand");
            Transformation transformation6 = this.parseModelTransformation(context, jsonObject4, "thirdperson_lefthand");
            if (transformation6 == Transformation.NONE) {
                transformation6 = transformation5;
            }
            final Transformation transformation7 = this.parseModelTransformation(context, jsonObject4, "firstperson_righthand");
            Transformation transformation8 = this.parseModelTransformation(context, jsonObject4, "firstperson_lefthand");
            if (transformation8 == Transformation.NONE) {
                transformation8 = transformation7;
            }
            final Transformation transformation9 = this.parseModelTransformation(context, jsonObject4, "head");
            final Transformation transformation10 = this.parseModelTransformation(context, jsonObject4, "gui");
            final Transformation transformation11 = this.parseModelTransformation(context, jsonObject4, "ground");
            final Transformation transformation12 = this.parseModelTransformation(context, jsonObject4, "fixed");
            return new ModelTransformation(transformation6, transformation5, transformation8, transformation7, transformation9, transformation10, transformation11, transformation12);
        }
        
        private Transformation parseModelTransformation(final JsonDeserializationContext ctx, final JsonObject json, final String key) {
            if (json.has(key)) {
                return ctx.<Transformation>deserialize(json.get(key), Transformation.class);
            }
            return Transformation.NONE;
        }
    }
}
