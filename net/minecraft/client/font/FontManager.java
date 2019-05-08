package net.minecraft.client.font;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.profiler.DummyProfiler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import java.util.Collections;
import com.google.gson.JsonArray;
import java.io.InputStream;
import java.util.Iterator;
import com.google.gson.Gson;
import java.io.IOException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import java.util.List;
import net.minecraft.resource.SupplyingResourceReloadListener;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.client.texture.TextureManager;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FontManager implements AutoCloseable
{
    private static final Logger LOGGER;
    private final Map<Identifier, TextRenderer> textRenderers;
    private final Set<Font> fonts;
    private final TextureManager textureManager;
    private boolean forceUnicodeFont;
    private final ResourceReloadListener resourceReloadListener;
    
    public FontManager(final TextureManager textureManager, final boolean boolean2) {
        this.textRenderers = Maps.newHashMap();
        this.fonts = Sets.newHashSet();
        this.resourceReloadListener = new SupplyingResourceReloadListener<Map<Identifier, List<Font>>>() {
            @Override
            protected Map<Identifier, List<Font>> load(final ResourceManager resourceManager, final Profiler profiler) {
                profiler.startTick();
                final Gson gson3 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                final Map<Identifier, List<Font>> map4 = Maps.newHashMap();
                for (final Identifier identifier6 : resourceManager.findResources("font", string -> string.endsWith(".json"))) {
                    final String string2 = identifier6.getPath();
                    final Identifier identifier7 = new Identifier(identifier6.getNamespace(), string2.substring("font/".length(), string2.length() - ".json".length()));
                    final List<Font> list9 = map4.computeIfAbsent(identifier7, identifier -> Lists.<Font>newArrayList(new BlankFont()));
                    profiler.push(identifier7::toString);
                    try {
                        for (final Resource resource11 : resourceManager.getAllResources(identifier6)) {
                            profiler.push(resource11::getResourcePackName);
                            try (final InputStream inputStream12 = resource11.getInputStream()) {
                                profiler.push("reading");
                                final JsonArray jsonArray14 = JsonHelper.getArray(JsonHelper.<JsonObject>deserialize(gson3, IOUtils.toString(inputStream12, StandardCharsets.UTF_8), JsonObject.class), "providers");
                                profiler.swap("parsing");
                                for (int integer15 = jsonArray14.size() - 1; integer15 >= 0; --integer15) {
                                    final JsonObject jsonObject16 = JsonHelper.asObject(jsonArray14.get(integer15), "providers[" + integer15 + "]");
                                    try {
                                        final String string3 = JsonHelper.getString(jsonObject16, "type");
                                        final FontType fontType18 = FontType.byId(string3);
                                        if (!FontManager.this.forceUnicodeFont || fontType18 == FontType.LEGACY_UNICODE || !identifier7.equals(MinecraftClient.DEFAULT_TEXT_RENDERER_ID)) {
                                            profiler.push(string3);
                                            list9.add(fontType18.createLoader(jsonObject16).load(resourceManager));
                                            profiler.pop();
                                        }
                                    }
                                    catch (RuntimeException runtimeException17) {
                                        FontManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", identifier7, resource11.getResourcePackName(), runtimeException17.getMessage());
                                    }
                                }
                                profiler.pop();
                            }
                            catch (RuntimeException runtimeException18) {
                                FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", identifier7, resource11.getResourcePackName(), runtimeException18.getMessage());
                            }
                            profiler.pop();
                        }
                    }
                    catch (IOException iOException10) {
                        FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", identifier7, iOException10.getMessage());
                    }
                    profiler.push("caching");
                    for (char character10 = '\0'; character10 < '\uffff'; ++character10) {
                        if (character10 != ' ') {
                            for (final Font font12 : Lists.<Font>reverse(list9)) {
                                if (font12.getGlyph(character10) != null) {
                                    break;
                                }
                            }
                        }
                    }
                    profiler.pop();
                    profiler.pop();
                }
                profiler.endTick();
                return map4;
            }
            
            @Override
            protected void apply(final Map<Identifier, List<Font>> result, final ResourceManager resourceManager, final Profiler profiler) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: invokeinterface net/minecraft/util/profiler/Profiler.startTick:()V
                //     6: aload_3         /* profiler */
                //     7: ldc_w           "reloading"
                //    10: invokeinterface net/minecraft/util/profiler/Profiler.push:(Ljava/lang/String;)V
                //    15: aload_0         /* this */
                //    16: getfield        net/minecraft/client/font/FontManager$1.a:Lnet/minecraft/client/font/FontManager;
                //    19: invokestatic    net/minecraft/client/font/FontManager.b:(Lnet/minecraft/client/font/FontManager;)Ljava/util/Map;
                //    22: invokeinterface java/util/Map.keySet:()Ljava/util/Set;
                //    27: invokeinterface java/util/Set.stream:()Ljava/util/stream/Stream;
                //    32: aload_1         /* result */
                //    33: invokeinterface java/util/Map.keySet:()Ljava/util/Set;
                //    38: invokeinterface java/util/Set.stream:()Ljava/util/stream/Stream;
                //    43: invokestatic    java/util/stream/Stream.concat:(Ljava/util/stream/Stream;Ljava/util/stream/Stream;)Ljava/util/stream/Stream;
                //    46: invokeinterface java/util/stream/Stream.distinct:()Ljava/util/stream/Stream;
                //    51: aload_0         /* this */
                //    52: aload_1         /* result */
                //    53: invokedynamic   BootstrapMethod #4, accept:(Lnet/minecraft/client/font/FontManager$1;Ljava/util/Map;)Ljava/util/function/Consumer;
                //    58: invokeinterface java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
                //    63: aload_1         /* result */
                //    64: invokeinterface java/util/Map.values:()Ljava/util/Collection;
                //    69: aload_0         /* this */
                //    70: getfield        net/minecraft/client/font/FontManager$1.a:Lnet/minecraft/client/font/FontManager;
                //    73: invokestatic    net/minecraft/client/font/FontManager.c:(Lnet/minecraft/client/font/FontManager;)Ljava/util/Set;
                //    76: invokedynamic   BootstrapMethod #5, accept:(Ljava/util/Set;)Ljava/util/function/Consumer;
                //    81: invokeinterface java/util/Collection.forEach:(Ljava/util/function/Consumer;)V
                //    86: aload_3         /* profiler */
                //    87: invokeinterface net/minecraft/util/profiler/Profiler.pop:()V
                //    92: aload_3         /* profiler */
                //    93: invokeinterface net/minecraft/util/profiler/Profiler.endTick:()V
                //    98: return         
                //    Signature:
                //  (Ljava/util/Map<Lnet/minecraft/util/Identifier;Ljava/util/List<Lnet/minecraft/client/font/Font;>;>;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Could not infer any expression.
                //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:374)
                //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1164)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:1009)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:554)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
                //     at cuchaz.enigma.SourceProvider.getSources(SourceProvider.java:66)
                //     at cuchaz.enigma.Deobfuscator.decompileClass(Deobfuscator.java:269)
                //     at cuchaz.enigma.Deobfuscator.lambda$decompileClasses$7(Deobfuscator.java:262)
                //     at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                //     at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1374)
                //     at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
                //     at java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:291)
                //     at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:731)
                //     at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
                //     at java.util.concurrent.ForkJoinPool$WorkQueue.execLocalTasks(ForkJoinPool.java:1040)
                //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1058)
                //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
                //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
        };
        this.textureManager = textureManager;
        this.forceUnicodeFont = boolean2;
    }
    
    @Nullable
    public TextRenderer getTextRenderer(final Identifier identifier) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        net/minecraft/client/font/FontManager.textRenderers:Ljava/util/Map;
        //     4: aload_1         /* identifier */
        //     5: aload_0         /* this */
        //     6: invokedynamic   BootstrapMethod #0, apply:(Lnet/minecraft/client/font/FontManager;)Ljava/util/function/Function;
        //    11: invokeinterface java/util/Map.computeIfAbsent:(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;
        //    16: checkcast       Lnet/minecraft/client/font/TextRenderer;
        //    19: areturn        
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:374)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at cuchaz.enigma.SourceProvider.getSources(SourceProvider.java:66)
        //     at cuchaz.enigma.Deobfuscator.decompileClass(Deobfuscator.java:269)
        //     at cuchaz.enigma.Deobfuscator.lambda$decompileClasses$7(Deobfuscator.java:262)
        //     at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
        //     at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1374)
        //     at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
        //     at java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:291)
        //     at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:731)
        //     at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.execLocalTasks(ForkJoinPool.java:1040)
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1058)
        //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setForceUnicodeFont(final boolean forceUnicodeFont, final Executor executor2, final Executor executor3) {
        if (forceUnicodeFont == this.forceUnicodeFont) {
            return;
        }
        this.forceUnicodeFont = forceUnicodeFont;
        final ResourceManager resourceManager4 = MinecraftClient.getInstance().getResourceManager();
        final ResourceReloadListener.Helper helper5 = new ResourceReloadListener.Helper() {
            @Override
            public <T> CompletableFuture<T> waitForAll(final T passedObject) {
                return CompletableFuture.<T>completedFuture(passedObject);
            }
        };
        this.resourceReloadListener.a(helper5, resourceManager4, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor2, executor3);
    }
    
    public ResourceReloadListener getResourceReloadListener() {
        return this.resourceReloadListener;
    }
    
    @Override
    public void close() {
        this.textRenderers.values().forEach(TextRenderer::close);
        this.fonts.forEach(Font::close);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
