package net.minecraft.client.render.model.json;

import java.util.Iterator;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import java.util.Map;
import java.util.Collection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModelGenerator
{
    public static final List<String> LAYERS;
    
    public JsonUnbakedModel create(final Function<Identifier, Sprite> textureGetter, final JsonUnbakedModel blockModel) {
        final Map<String, String> map3 = Maps.newHashMap();
        final List<ModelElement> list4 = Lists.newArrayList();
        for (int integer5 = 0; integer5 < ItemModelGenerator.LAYERS.size(); ++integer5) {
            final String string6 = ItemModelGenerator.LAYERS.get(integer5);
            if (!blockModel.textureExists(string6)) {
                break;
            }
            final String string7 = blockModel.resolveTexture(string6);
            map3.put(string6, string7);
            final Sprite sprite8 = textureGetter.apply(new Identifier(string7));
            list4.addAll(this.a(integer5, string6, sprite8));
        }
        map3.put("particle", blockModel.textureExists("particle") ? blockModel.resolveTexture("particle") : map3.get("layer0"));
        final JsonUnbakedModel jsonUnbakedModel5 = new JsonUnbakedModel(null, list4, map3, false, false, blockModel.getTransformations(), blockModel.getOverrides());
        jsonUnbakedModel5.id = blockModel.id;
        return jsonUnbakedModel5;
    }
    
    private List<ModelElement> a(final int integer, final String string, final Sprite sprite) {
        final Map<Direction, ModelElementFace> map4 = Maps.newHashMap();
        map4.put(Direction.SOUTH, new ModelElementFace(null, integer, string, new ModelElementTexture(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0)));
        map4.put(Direction.NORTH, new ModelElementFace(null, integer, string, new ModelElementTexture(new float[] { 16.0f, 0.0f, 0.0f, 16.0f }, 0)));
        final List<ModelElement> list5 = Lists.newArrayList();
        list5.add(new ModelElement(new Vector3f(0.0f, 0.0f, 7.5f), new Vector3f(16.0f, 16.0f, 8.5f), map4, null, true));
        list5.addAll(this.a(sprite, string, integer));
        return list5;
    }
    
    private List<ModelElement> a(final Sprite sprite, final String string, final int integer) {
        final float float4 = (float)sprite.getWidth();
        final float float5 = (float)sprite.getHeight();
        final List<ModelElement> list6 = Lists.newArrayList();
        for (final a a8 : this.a(sprite)) {
            float float6 = 0.0f;
            float float7 = 0.0f;
            float float8 = 0.0f;
            float float9 = 0.0f;
            float float10 = 0.0f;
            float float11 = 0.0f;
            float float12 = 0.0f;
            float float13 = 0.0f;
            final float float14 = 16.0f / float4;
            final float float15 = 16.0f / float5;
            final float float16 = (float)a8.b();
            final float float17 = (float)a8.c();
            final float float18 = (float)a8.d();
            final b b22 = a8.a();
            switch (b22) {
                case a: {
                    float10 = (float6 = float16);
                    float11 = (float8 = float17 + 1.0f);
                    float12 = (float7 = float18);
                    float9 = float18;
                    float13 = float18 + 1.0f;
                    break;
                }
                case b: {
                    float12 = float18;
                    float13 = float18 + 1.0f;
                    float10 = (float6 = float16);
                    float11 = (float8 = float17 + 1.0f);
                    float7 = float18 + 1.0f;
                    float9 = float18 + 1.0f;
                    break;
                }
                case c: {
                    float10 = (float6 = float18);
                    float8 = float18;
                    float11 = float18 + 1.0f;
                    float13 = (float7 = float16);
                    float12 = (float9 = float17 + 1.0f);
                    break;
                }
                case d: {
                    float10 = float18;
                    float11 = float18 + 1.0f;
                    float6 = float18 + 1.0f;
                    float8 = float18 + 1.0f;
                    float13 = (float7 = float16);
                    float12 = (float9 = float17 + 1.0f);
                    break;
                }
            }
            float6 *= float14;
            float8 *= float14;
            float7 *= float15;
            float9 *= float15;
            float7 = 16.0f - float7;
            float9 = 16.0f - float9;
            float10 *= float14;
            float11 *= float14;
            float12 *= float15;
            float13 *= float15;
            final Map<Direction, ModelElementFace> map23 = Maps.newHashMap();
            map23.put(b22.a(), new ModelElementFace(null, integer, string, new ModelElementTexture(new float[] { float10, float12, float11, float13 }, 0)));
            switch (b22) {
                case a: {
                    list6.add(new ModelElement(new Vector3f(float6, float7, 7.5f), new Vector3f(float8, float7, 8.5f), map23, null, true));
                    continue;
                }
                case b: {
                    list6.add(new ModelElement(new Vector3f(float6, float9, 7.5f), new Vector3f(float8, float9, 8.5f), map23, null, true));
                    continue;
                }
                case c: {
                    list6.add(new ModelElement(new Vector3f(float6, float7, 7.5f), new Vector3f(float6, float9, 8.5f), map23, null, true));
                    continue;
                }
                case d: {
                    list6.add(new ModelElement(new Vector3f(float8, float7, 7.5f), new Vector3f(float8, float9, 8.5f), map23, null, true));
                    continue;
                }
            }
        }
        return list6;
    }
    
    private List<a> a(final Sprite sprite) {
        final int integer2 = sprite.getWidth();
        final int integer3 = sprite.getHeight();
        final List<a> list4 = Lists.newArrayList();
        for (int integer4 = 0; integer4 < sprite.getFrameCount(); ++integer4) {
            for (int integer5 = 0; integer5 < integer3; ++integer5) {
                for (int integer6 = 0; integer6 < integer2; ++integer6) {
                    final boolean boolean8 = !this.a(sprite, integer4, integer6, integer5, integer2, integer3);
                    this.a(b.a, list4, sprite, integer4, integer6, integer5, integer2, integer3, boolean8);
                    this.a(b.b, list4, sprite, integer4, integer6, integer5, integer2, integer3, boolean8);
                    this.a(b.c, list4, sprite, integer4, integer6, integer5, integer2, integer3, boolean8);
                    this.a(b.d, list4, sprite, integer4, integer6, integer5, integer2, integer3, boolean8);
                }
            }
        }
        return list4;
    }
    
    private void a(final b b, final List<a> list, final Sprite sprite, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final boolean boolean9) {
        final boolean boolean10 = this.a(sprite, integer4, integer5 + b.b(), integer6 + b.c(), integer7, integer8) && boolean9;
        if (boolean10) {
            this.a(list, b, integer5, integer6);
        }
    }
    
    private void a(final List<a> list, final b b, final int integer3, final int integer4) {
        a a5 = null;
        for (final a a6 : list) {
            if (a6.a() != b) {
                continue;
            }
            final int integer5 = b.d() ? integer4 : integer3;
            if (a6.d() == integer5) {
                a5 = a6;
                break;
            }
        }
        final int integer6 = b.d() ? integer4 : integer3;
        final int integer7 = b.d() ? integer3 : integer4;
        if (a5 == null) {
            list.add(new a(b, integer7, integer6));
        }
        else {
            a5.a(integer7);
        }
    }
    
    private boolean a(final Sprite sprite, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        return integer3 < 0 || integer4 < 0 || integer3 >= integer5 || integer4 >= integer6 || sprite.a(integer2, integer3, integer4);
    }
    
    static {
        LAYERS = Lists.<String>newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");
    }
    
    @Environment(EnvType.CLIENT)
    enum b
    {
        a(Direction.UP, 0, -1), 
        b(Direction.DOWN, 0, 1), 
        c(Direction.EAST, -1, 0), 
        d(Direction.WEST, 1, 0);
        
        private final Direction e;
        private final int f;
        private final int g;
        
        private b(final Direction direction, final int integer2, final int integer3) {
            this.e = direction;
            this.f = integer2;
            this.g = integer3;
        }
        
        public Direction a() {
            return this.e;
        }
        
        public int b() {
            return this.f;
        }
        
        public int c() {
            return this.g;
        }
        
        private boolean d() {
            return this == b.b || this == b.a;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class a
    {
        private final b a;
        private int b;
        private int c;
        private final int d;
        
        public a(final b b, final int integer2, final int integer3) {
            this.a = b;
            this.b = integer2;
            this.c = integer2;
            this.d = integer3;
        }
        
        public void a(final int integer) {
            if (integer < this.b) {
                this.b = integer;
            }
            else if (integer > this.c) {
                this.c = integer;
            }
        }
        
        public b a() {
            return this.a;
        }
        
        public int b() {
            return this.b;
        }
        
        public int c() {
            return this.c;
        }
        
        public int d() {
            return this.d;
        }
    }
}
