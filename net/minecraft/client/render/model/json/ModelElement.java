package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.common.collect.Maps;
import java.util.Locale;
import net.minecraft.util.math.MathHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.client.util.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelElement
{
    public final Vector3f from;
    public final Vector3f to;
    public final Map<Direction, ModelElementFace> faces;
    public final ModelRotation rotation;
    public final boolean shade;
    
    public ModelElement(final Vector3f from, final Vector3f to, final Map<Direction, ModelElementFace> faces, @Nullable final ModelRotation rotation, final boolean shade) {
        this.from = from;
        this.to = to;
        this.faces = faces;
        this.rotation = rotation;
        this.shade = shade;
        this.a();
    }
    
    private void a() {
        for (final Map.Entry<Direction, ModelElementFace> entry2 : this.faces.entrySet()) {
            final float[] arr3 = this.a(entry2.getKey());
            entry2.getValue().textureData.setUvs(arr3);
        }
    }
    
    private float[] a(final Direction direction) {
        switch (direction) {
            case DOWN: {
                return new float[] { this.from.x(), 16.0f - this.to.z(), this.to.x(), 16.0f - this.from.z() };
            }
            case UP: {
                return new float[] { this.from.x(), this.from.z(), this.to.x(), this.to.z() };
            }
            default: {
                return new float[] { 16.0f - this.to.x(), 16.0f - this.to.y(), 16.0f - this.from.x(), 16.0f - this.from.y() };
            }
            case SOUTH: {
                return new float[] { this.from.x(), 16.0f - this.to.y(), this.to.x(), 16.0f - this.from.y() };
            }
            case WEST: {
                return new float[] { this.from.z(), 16.0f - this.to.y(), this.to.z(), 16.0f - this.from.y() };
            }
            case EAST: {
                return new float[] { 16.0f - this.to.z(), 16.0f - this.to.y(), 16.0f - this.from.z(), 16.0f - this.from.y() };
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelElement>
    {
        protected Deserializer() {
        }
        
        public ModelElement a(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject4 = jsonElement.getAsJsonObject();
            final Vector3f vector3f5 = this.deserializeFrom(jsonObject4);
            final Vector3f vector3f6 = this.deserializeTo(jsonObject4);
            final ModelRotation modelRotation7 = this.deserializeRotation(jsonObject4);
            final Map<Direction, ModelElementFace> map8 = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject4);
            if (jsonObject4.has("shade") && !JsonHelper.hasBoolean(jsonObject4, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            final boolean boolean9 = JsonHelper.getBoolean(jsonObject4, "shade", true);
            return new ModelElement(vector3f5, vector3f6, map8, modelRotation7, boolean9);
        }
        
        @Nullable
        private ModelRotation deserializeRotation(final JsonObject object) {
            ModelRotation modelRotation2 = null;
            if (object.has("rotation")) {
                final JsonObject jsonObject3 = JsonHelper.getObject(object, "rotation");
                final Vector3f vector3f4 = this.deserializeVec3f(jsonObject3, "origin");
                vector3f4.scale(0.0625f);
                final Direction.Axis axis5 = this.deserializeAxis(jsonObject3);
                final float float6 = this.deserializeRotationAngle(jsonObject3);
                final boolean boolean7 = JsonHelper.getBoolean(jsonObject3, "rescale", false);
                modelRotation2 = new ModelRotation(vector3f4, axis5, float6, boolean7);
            }
            return modelRotation2;
        }
        
        private float deserializeRotationAngle(final JsonObject object) {
            final float float2 = JsonHelper.getFloat(object, "angle");
            if (float2 != 0.0f && MathHelper.abs(float2) != 22.5f && MathHelper.abs(float2) != 45.0f) {
                throw new JsonParseException("Invalid rotation " + float2 + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            return float2;
        }
        
        private Direction.Axis deserializeAxis(final JsonObject object) {
            final String string2 = JsonHelper.getString(object, "axis");
            final Direction.Axis axis3 = Direction.Axis.fromName(string2.toLowerCase(Locale.ROOT));
            if (axis3 == null) {
                throw new JsonParseException("Invalid rotation axis: " + string2);
            }
            return axis3;
        }
        
        private Map<Direction, ModelElementFace> deserializeFacesValidating(final JsonDeserializationContext context, final JsonObject object) {
            final Map<Direction, ModelElementFace> map3 = this.deserializeFaces(context, object);
            if (map3.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return map3;
        }
        
        private Map<Direction, ModelElementFace> deserializeFaces(final JsonDeserializationContext context, final JsonObject object) {
            final Map<Direction, ModelElementFace> map3 = Maps.newEnumMap(Direction.class);
            final JsonObject jsonObject4 = JsonHelper.getObject(object, "faces");
            for (final Map.Entry<String, JsonElement> entry6 : jsonObject4.entrySet()) {
                final Direction direction7 = this.getDirection(entry6.getKey());
                map3.put(direction7, context.<ModelElementFace>deserialize(entry6.getValue(), ModelElementFace.class));
            }
            return map3;
        }
        
        private Direction getDirection(final String name) {
            final Direction direction2 = Direction.byName(name);
            if (direction2 == null) {
                throw new JsonParseException("Unknown facing: " + name);
            }
            return direction2;
        }
        
        private Vector3f deserializeTo(final JsonObject object) {
            final Vector3f vector3f2 = this.deserializeVec3f(object, "to");
            if (vector3f2.x() < -16.0f || vector3f2.y() < -16.0f || vector3f2.z() < -16.0f || vector3f2.x() > 32.0f || vector3f2.y() > 32.0f || vector3f2.z() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f2);
            }
            return vector3f2;
        }
        
        private Vector3f deserializeFrom(final JsonObject object) {
            final Vector3f vector3f2 = this.deserializeVec3f(object, "from");
            if (vector3f2.x() < -16.0f || vector3f2.y() < -16.0f || vector3f2.z() < -16.0f || vector3f2.x() > 32.0f || vector3f2.y() > 32.0f || vector3f2.z() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f2);
            }
            return vector3f2;
        }
        
        private Vector3f deserializeVec3f(final JsonObject object, final String name) {
            final JsonArray jsonArray3 = JsonHelper.getArray(object, name);
            if (jsonArray3.size() != 3) {
                throw new JsonParseException("Expected 3 " + name + " values, found: " + jsonArray3.size());
            }
            final float[] arr4 = new float[3];
            for (int integer5 = 0; integer5 < arr4.length; ++integer5) {
                arr4[integer5] = JsonHelper.asFloat(jsonArray3.get(integer5), name + "[" + integer5 + "]");
            }
            return new Vector3f(arr4[0], arr4[1], arr4[2]);
        }
    }
}
