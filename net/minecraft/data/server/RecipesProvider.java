package net.minecraft.data.server;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.Set;
import net.minecraft.tag.Tag;
import net.minecraft.state.property.Property;
import java.util.Map;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import net.minecraft.recipe.cooking.CookingRecipeSerializer;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.data.server.recipe.ComplexRecipeJsonFactory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.NumberRange;
import net.minecraft.tag.ItemTags;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Items;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import java.util.function.Consumer;
import java.io.BufferedWriter;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import com.google.gson.JsonObject;
import java.io.IOException;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class RecipesProvider implements DataProvider
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private final DataGenerator root;
    
    public RecipesProvider(final DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        net/minecraft/data/server/RecipesProvider.root:Lnet/minecraft/data/DataGenerator;
        //     4: invokevirtual   net/minecraft/data/DataGenerator.getOutput:()Ljava/nio/file/Path;
        //     7: astore_2        /* path2 */
        //     8: invokestatic    com/google/common/collect/Sets.newHashSet:()Ljava/util/HashSet;
        //    11: astore_3        /* set3 */
        //    12: aload_0         /* this */
        //    13: aload_0         /* this */
        //    14: aload_3         /* set3 */
        //    15: aload_1         /* dataCache */
        //    16: aload_2         /* path2 */
        //    17: invokedynamic   BootstrapMethod #0, accept:(Lnet/minecraft/data/server/RecipesProvider;Ljava/util/Set;Lnet/minecraft/data/DataCache;Ljava/nio/file/Path;)Ljava/util/function/Consumer;
        //    22: invokespecial   net/minecraft/data/server/RecipesProvider.generate:(Ljava/util/function/Consumer;)V
        //    25: aload_0         /* this */
        //    26: aload_1         /* dataCache */
        //    27: invokestatic    net/minecraft/advancement/Advancement$Task.create:()Lnet/minecraft/advancement/Advancement$Task;
        //    30: ldc             "impossible"
        //    32: new             Lnet/minecraft/advancement/criterion/ImpossibleCriterion$Conditions;
        //    35: dup            
        //    36: invokespecial   net/minecraft/advancement/criterion/ImpossibleCriterion$Conditions.<init>:()V
        //    39: invokevirtual   net/minecraft/advancement/Advancement$Task.criterion:(Ljava/lang/String;Lnet/minecraft/advancement/criterion/CriterionConditions;)Lnet/minecraft/advancement/Advancement$Task;
        //    42: invokevirtual   net/minecraft/advancement/Advancement$Task.toJson:()Lcom/google/gson/JsonObject;
        //    45: aload_2         /* path2 */
        //    46: ldc             "data/minecraft/advancements/recipes/root.json"
        //    48: invokeinterface java/nio/file/Path.resolve:(Ljava/lang/String;)Ljava/nio/file/Path;
        //    53: invokespecial   net/minecraft/data/server/RecipesProvider.b:(Lnet/minecraft/data/DataCache;Lcom/google/gson/JsonObject;Ljava/nio/file/Path;)V
        //    56: return         
        //    Exceptions:
        //  throws java.io.IOException
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
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void a(final DataCache dataCache, final JsonObject jsonObject, final Path path) {
        try {
            final String string4 = RecipesProvider.GSON.toJson(jsonObject);
            final String string5 = RecipesProvider.SHA1.hashUnencodedChars(string4).toString();
            if (!Objects.equals(dataCache.getOldSha1(path), string5) || !Files.exists(path)) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (final BufferedWriter bufferedWriter6 = Files.newBufferedWriter(path)) {
                    bufferedWriter6.write(string4);
                }
            }
            dataCache.updateSha1(path, string5);
        }
        catch (IOException iOException4) {
            RecipesProvider.LOGGER.error("Couldn't save recipe {}", path, iOException4);
        }
    }
    
    private void b(final DataCache dataCache, final JsonObject jsonObject, final Path path) {
        try {
            final String string4 = RecipesProvider.GSON.toJson(jsonObject);
            final String string5 = RecipesProvider.SHA1.hashUnencodedChars(string4).toString();
            if (!Objects.equals(dataCache.getOldSha1(path), string5) || !Files.exists(path)) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (final BufferedWriter bufferedWriter6 = Files.newBufferedWriter(path)) {
                    bufferedWriter6.write(string4);
                }
            }
            dataCache.updateSha1(path, string5);
        }
        catch (IOException iOException4) {
            RecipesProvider.LOGGER.error("Couldn't save recipe advancement {}", path, iOException4);
        }
    }
    
    private void generate(final Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonFactory.create(Blocks.Y, 3).input('#', Blocks.M).pattern("##").pattern("##").group("bark").criterion("has_log", this.a((ItemProvider)Blocks.M)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.pb).input('#', Blocks.r).pattern("# #").pattern("###").group("boat").criterion("in_water", this.a(Blocks.A)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.eS).input(Blocks.r).group("wooden_button").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.im, 3).input('#', Blocks.r).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ih, 3).input('#', Items.jz).input('W', Blocks.r).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ic).input('#', Items.jz).input('W', Blocks.r).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.r, 4).input(ItemTags.s).group("planks").criterion("has_logs", this.a(ItemTags.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cu).input('#', Blocks.r).pattern("##").group("wooden_pressure_plate").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hG, 6).input('#', Blocks.r).pattern("###").group("wooden_slab").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gd, 4).input('#', Blocks.r).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dl, 2).input('#', Blocks.r).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", this.a((ItemProvider)Blocks.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fv, 6).input('#', Blocks.cx).input('S', Items.jz).input('X', Items.jk).pattern("XSX").pattern("X#X").pattern("XSX").criterion("has_rail", this.a((ItemProvider)Blocks.cf)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.g, 2).input(Blocks.e).input(Blocks.m).criterion("has_stone", this.a((ItemProvider)Blocks.e)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fg).input('I', Blocks.bE).input('i', Items.jk).pattern("III").pattern(" i ").pattern("iii").criterion("has_iron_block", this.a((ItemProvider)Blocks.bE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ol).input('/', Items.jz).input('_', Blocks.hJ).pattern("///").pattern(" / ").pattern("/_/").criterion("has_stone_slab", this.a((ItemProvider)Blocks.hJ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jg, 4).input('#', Items.jz).input('X', Items.kl).input('Y', Items.jH).pattern("X").pattern("#").pattern("Y").criterion("has_feather", this.a(Items.jH)).criterion("has_flint", this.a(Items.kl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lK, 1).input('P', ItemTags.b).input('S', ItemTags.i).pattern("PSP").pattern("P P").pattern("PSP").criterion("has_planks", this.a(ItemTags.b)).criterion("has_wood_slab", this.a(ItemTags.i)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ek).input('S', Items.nV).input('G', Blocks.ao).input('O', Blocks.bJ).pattern("GGG").pattern("GSG").pattern("OOO").criterion("has_nether_star", this.a(Items.nV)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.oQ).input(Items.jA).input(Items.oO, 6).criterion("has_beetroot", this.a(Items.oO)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.W, 3).input('#', Blocks.K).pattern("##").pattern("##").group("bark").criterion("has_log", this.a((ItemProvider)Blocks.K)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oZ).input('#', Blocks.p).pattern("# #").pattern("###").group("boat").criterion("in_water", this.a(Blocks.A)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.eQ).input(Blocks.p).group("wooden_button").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ik, 3).input('#', Blocks.p).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.if_, 3).input('#', Items.jz).input('W', Blocks.p).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ia).input('#', Items.jz).input('W', Blocks.p).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.p, 4).input(ItemTags.r).group("planks").criterion("has_log", this.a(ItemTags.r)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cs).input('#', Blocks.p).pattern("##").group("wooden_pressure_plate").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hE, 6).input('#', Blocks.p).pattern("###").group("wooden_slab").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.eh, 4).input('#', Blocks.p).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dj, 2).input('#', Blocks.p).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", this.a((ItemProvider)Blocks.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oK).input('#', Blocks.bm).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_black_wool", this.a((ItemProvider)Blocks.bm)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BLACK_BED).input('#', Blocks.bm).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_black_wool", this.a((ItemProvider)Blocks.bm)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BLACK_BED).input(Items.WHITE_BED).input(Items.lz).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "black_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gI, 3).input('#', Blocks.bm).pattern("##").group("carpet").criterion("has_black_wool", this.a((ItemProvider)Blocks.bm)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gI, 8).input('#', Blocks.gt).input('$', Items.lz).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_black_dye", this.a(Items.lz)).offerTo(consumer, "black_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jT, 8).input(Items.lz).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lz).input(Items.lh).group("black_dye").criterion("has_ink_sac", this.a(Items.lh)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lz).input(Blocks.bz).group("black_dye").criterion("has_black_flower", this.a((ItemProvider)Blocks.bz)).offerTo(consumer, "black_dye_from_wither_rose");
        ShapedRecipeJsonFactory.create(Blocks.dg, 8).input('#', Blocks.ao).input('X', Items.lz).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gc, 16).input('#', Blocks.dg).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gc, 8).input('#', Blocks.dB).input('$', Items.lz).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_black_dye", this.a(Items.lz)).offerTo(consumer, "black_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fM, 8).input('#', Blocks.gJ).input('X', Items.lz).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bm).input(Items.lz).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.mp, 2).input(Items.mh).criterion("has_blaze_rod", this.a(Items.mh)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oG).input('#', Blocks.bi).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_blue_wool", this.a((ItemProvider)Blocks.bi)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BLUE_BED).input('#', Blocks.bi).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_blue_wool", this.a((ItemProvider)Blocks.bi)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BLUE_BED).input(Items.WHITE_BED).input(Items.lx).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "blue_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gE, 3).input('#', Blocks.bi).pattern("##").group("carpet").criterion("has_blue_wool", this.a((ItemProvider)Blocks.bi)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gE, 8).input('#', Blocks.gt).input('$', Items.lx).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_blue_dye", this.a(Items.lx)).offerTo(consumer, "blue_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jP, 8).input(Items.lx).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lx).input(Items.ll).group("blue_dye").criterion("has_lapis_lazuli", this.a(Items.ll)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lx).input(Blocks.by).group("blue_dye").criterion("has_blue_flower", this.a((ItemProvider)Blocks.by)).offerTo(consumer, "blue_dye_from_cornflower");
        ShapedRecipeJsonFactory.create(Blocks.kN).input('#', Blocks.gL).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_packed_ice", this.a(NumberRange.IntRange.atLeast(9), Blocks.gL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dc, 8).input('#', Blocks.ao).input('X', Items.lx).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fY, 16).input('#', Blocks.dc).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fY, 8).input('#', Blocks.dB).input('$', Items.lx).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_blue_dye", this.a(Items.lx)).offerTo(consumer, "blue_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fI, 8).input('#', Blocks.gJ).input('X', Items.lx).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bi).input(Items.lx).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kE).input('#', Blocks.n).pattern("# #").pattern("###").group("boat").criterion("in_water", this.a(Blocks.A)).offerTo(consumer);
        final Item item2 = Items.lw;
        ShapedRecipeJsonFactory.create(Blocks.iE).input('X', Items.lw).pattern("XXX").pattern("XXX").pattern("XXX").criterion("has_at_least_9_bonemeal", this.a(NumberRange.IntRange.atLeast(9), item2)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lw, 3).input(Items.lB).group("bonemeal").criterion("has_bone", this.a(Items.lB)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lw, 9).input(Blocks.iE).group("bonemeal").criterion("has_at_least_9_bonemeal", this.a(NumberRange.IntRange.atLeast(9), Items.lw)).criterion("has_bone_block", this.a((ItemProvider)Blocks.iE)).offerTo(consumer, "bone_meal_from_bone_block");
        ShapelessRecipeJsonFactory.create(Items.kS).input(Items.kR, 3).input(Items.kF).criterion("has_paper", this.a(Items.kR)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bH).input('#', ItemTags.b).input('X', Items.kS).pattern("###").pattern("XXX").pattern("###").criterion("has_book", this.a(Items.kS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jf).input('#', Items.jz).input('X', Items.jG).pattern(" #X").pattern("# X").pattern(" #X").criterion("has_string", this.a(Items.jG)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jA, 4).input('#', ItemTags.b).pattern("# #").pattern(" # ").criterion("has_brown_mushroom", this.a((ItemProvider)Blocks.bB)).criterion("has_red_mushroom", this.a((ItemProvider)Blocks.bC)).criterion("has_mushroom_stew", this.a(Items.jB)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jQ).input('#', Items.jP).pattern("###").criterion("has_wheat", this.a(Items.jP)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dS).input('B', Items.mh).input('#', Blocks.m).pattern(" B ").pattern("###").criterion("has_blaze_rod", this.a(Items.mh)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bF).input('#', Items.kL).pattern("##").pattern("##").criterion("has_brick", this.a(Items.kL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hO, 6).input('#', Blocks.bF).pattern("###").criterion("has_brick_block", this.a((ItemProvider)Blocks.bF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dJ, 4).input('#', Blocks.bF).pattern("#  ").pattern("## ").pattern("###").criterion("has_brick_block", this.a((ItemProvider)Blocks.bF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oH).input('#', Blocks.bj).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_brown_wool", this.a((ItemProvider)Blocks.bj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BROWN_BED).input('#', Blocks.bj).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_brown_wool", this.a((ItemProvider)Blocks.bj)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BROWN_BED).input(Items.WHITE_BED).input(Items.ly).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "brown_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gF, 3).input('#', Blocks.bj).pattern("##").group("carpet").criterion("has_brown_wool", this.a((ItemProvider)Blocks.bj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gF, 8).input('#', Blocks.gt).input('$', Items.ly).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_brown_dye", this.a(Items.ly)).offerTo(consumer, "brown_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jQ, 8).input(Items.ly).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ly).input(Items.lk).group("brown_dye").criterion("has_cocoa_beans", this.a(Items.lk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dd, 8).input('#', Blocks.ao).input('X', Items.ly).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fZ, 16).input('#', Blocks.dd).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fZ, 8).input('#', Blocks.dB).input('$', Items.ly).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_brown_dye", this.a(Items.ly)).offerTo(consumer, "brown_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fJ, 8).input('#', Blocks.gJ).input('X', Items.ly).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bj).input(Items.ly).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kx).input('#', Items.jk).pattern("# #").pattern(" # ").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cP).input('A', Items.kG).input('B', Items.lC).input('C', Items.jP).input('E', Items.kW).pattern("AAA").pattern("BEB").pattern("CCC").criterion("has_egg", this.a(Items.kW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lV).input('L', ItemTags.o).input('S', Items.jz).input('C', ItemTags.L).pattern(" S ").pattern("SCS").pattern("LLL").criterion("has_stick", this.a(Items.jz)).criterion("has_coal", this.a(ItemTags.L)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.nU).input('#', Items.kY).input('X', Items.nI).pattern("# ").pattern(" X").criterion("has_carrot", this.a(Items.nI)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dT).input('#', Items.jk).pattern("# #").pattern("# #").pattern("###").criterion("has_water_bucket", this.a(Items.ky)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lZ).input('F', ItemTags.j).input('#', ItemTags.b).pattern("F F").pattern("F F").pattern("###").criterion("has_wooden_fences", this.a(ItemTags.j)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bP).input('#', ItemTags.b).pattern("###").pattern("# #").pattern("###").criterion("has_lots_of_items", new InventoryChangedCriterion.Conditions(NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0])).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kU).input('A', Blocks.bP).input('B', Items.kA).pattern("A").pattern("B").criterion("has_minecart", this.a(Items.kA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fs).input('#', Blocks.hR).pattern("#").pattern("#").criterion("has_chiseled_quartz_block", this.a((ItemProvider)Blocks.fs)).criterion("has_quartz_block", this.a((ItemProvider)Blocks.fr)).criterion("has_quartz_pillar", this.a((ItemProvider)Blocks.ft)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dq).input('#', Blocks.hP).pattern("#").pattern("#").criterion("has_stone_bricks", this.a(ItemTags.c)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cE).input('#', Items.kM).pattern("##").pattern("##").criterion("has_clay_ball", this.a(Items.kM)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kZ).input('#', Items.jl).input('X', Items.kC).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_redstone", this.a(Items.kC)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jh, 9).input(Blocks.gK).criterion("has_at_least_9_coal", this.a(NumberRange.IntRange.atLeast(9), Items.jh)).criterion("has_coal_block", this.a((ItemProvider)Blocks.gK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gK).input('#', Items.jh).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_coal", this.a(NumberRange.IntRange.atLeast(9), Items.jh)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.k, 4).input('D', Blocks.j).input('G', Blocks.E).pattern("DG").pattern("GD").criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hN, 6).input('#', Blocks.m).pattern("###").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.el, 6).input('#', Blocks.m).pattern("###").pattern("###").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fm).input('#', Blocks.cx).input('X', Items.ob).input('I', Blocks.b).pattern(" # ").pattern("#X#").pattern("III").criterion("has_quartz", this.a(Items.ob)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kX).input('#', Items.jk).input('X', Items.kC).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_redstone", this.a(Items.kC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.lU, 8).input('#', Items.jP).input('X', Items.lk).pattern("#X#").criterion("has_cocoa", this.a(Items.lk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bT).input('#', ItemTags.b).pattern("##").pattern("##").criterion("has_planks", this.a(ItemTags.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.py).input('~', Items.jG).input('#', Items.jz).input('&', Items.jk).input('$', Blocks.ed).pattern("#&#").pattern("~$~").pattern(" # ").criterion("has_string", this.a(Items.jG)).criterion("has_stick", this.a(Items.jz)).criterion("has_iron_ingot", this.a(Items.jk)).criterion("has_tripwire_hook", this.a((ItemProvider)Blocks.ed)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lJ).input('#', ItemTags.b).input('@', Items.jG).pattern("@@").pattern("##").criterion("has_string", this.a(Items.jG)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hz).input('#', Blocks.hS).pattern("#").pattern("#").criterion("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).criterion("has_chiseled_red_sandstone", this.a((ItemProvider)Blocks.hz)).criterion("has_cut_red_sandstone", this.a((ItemProvider)Blocks.hA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.at).input('#', Blocks.hK).pattern("#").pattern("#").criterion("has_stone_slab", this.a((ItemProvider)Blocks.hK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oE).input('#', Blocks.bg).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_cyan_wool", this.a((ItemProvider)Blocks.bg)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.CYAN_BED).input('#', Blocks.bg).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_cyan_wool", this.a((ItemProvider)Blocks.bg)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.CYAN_BED).input(Items.WHITE_BED).input(Items.ln).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "cyan_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gC, 3).input('#', Blocks.bg).pattern("##").group("carpet").criterion("has_cyan_wool", this.a((ItemProvider)Blocks.bg)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gC, 8).input('#', Blocks.gt).input('$', Items.ln).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_cyan_dye", this.a(Items.ln)).offerTo(consumer, "cyan_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jN, 8).input(Items.ln).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ln, 2).input(Items.lx).input(Items.lj).criterion("has_green_dye", this.a(Items.lj)).criterion("has_blue_dye", this.a(Items.lx)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.da, 8).input('#', Blocks.ao).input('X', Items.ln).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fW, 16).input('#', Blocks.da).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fW, 8).input('#', Blocks.dB).input('$', Items.ln).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_cyan_dye", this.a(Items.ln)).offerTo(consumer, "cyan_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fG, 8).input('#', Blocks.gJ).input('X', Items.ln).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bg).input(Items.ln).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.Z, 3).input('#', Blocks.N).pattern("##").pattern("##").group("bark").criterion("has_log", this.a((ItemProvider)Blocks.N)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.pc).input('#', Blocks.s).pattern("# #").pattern("###").group("boat").criterion("in_water", this.a(Blocks.A)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.eT).input(Blocks.s).group("wooden_button").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.in, 3).input('#', Blocks.s).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ii, 3).input('#', Items.jz).input('W', Blocks.s).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.id).input('#', Items.jz).input('W', Blocks.s).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.s, 4).input(ItemTags.p).group("planks").criterion("has_logs", this.a(ItemTags.p)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cv).input('#', Blocks.s).pattern("##").group("wooden_pressure_plate").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hH, 6).input('#', Blocks.s).pattern("###").group("wooden_slab").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ge, 4).input('#', Blocks.s).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dm, 2).input('#', Blocks.s).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", this.a((ItemProvider)Blocks.s)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gk).input('S', Items.oe).input('I', Items.lh).pattern("SSS").pattern("SIS").pattern("SSS").criterion("has_prismarine_shard", this.a(Items.oe)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gl, 4).input('#', Blocks.gi).pattern("#  ").pattern("## ").pattern("###").criterion("has_prismarine", this.a((ItemProvider)Blocks.gi)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gm, 4).input('#', Blocks.gj).pattern("#  ").pattern("## ").pattern("###").criterion("has_prismarine_bricks", this.a((ItemProvider)Blocks.gj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gn, 4).input('#', Blocks.gk).pattern("#  ").pattern("## ").pattern("###").criterion("has_dark_prismarine", this.a((ItemProvider)Blocks.gk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fn).input('Q', Items.ob).input('G', Blocks.ao).input('W', Ingredient.fromTag(ItemTags.i)).pattern("GGG").pattern("QQQ").pattern("WWW").criterion("has_quartz", this.a(Items.ob)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.aN, 6).input('R', Items.kC).input('#', Blocks.co).input('X', Items.jk).pattern("X X").pattern("X#X").pattern("XRX").criterion("has_rail", this.a((ItemProvider)Blocks.cf)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jj, 9).input(Blocks.bS).criterion("has_at_least_9_diamond", this.a(NumberRange.IntRange.atLeast(9), Items.jj)).criterion("has_diamond_block", this.a((ItemProvider)Blocks.bS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jy).input('#', Items.jz).input('X', Items.jj).pattern("XX").pattern("X#").pattern(" #").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bS).input('#', Items.jj).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_diamond", this.a(NumberRange.IntRange.atLeast(9), Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kg).input('X', Items.jj).pattern("X X").pattern("X X").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ke).input('X', Items.jj).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kd).input('X', Items.jj).pattern("XXX").pattern("X X").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jM).input('#', Items.jz).input('X', Items.jj).pattern("XX").pattern(" #").pattern(" #").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kf).input('X', Items.jj).pattern("XXX").pattern("X X").pattern("X X").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jx).input('#', Items.jz).input('X', Items.jj).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jw).input('#', Items.jz).input('X', Items.jj).pattern("X").pattern("#").pattern("#").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jv).input('#', Items.jz).input('X', Items.jj).pattern("X").pattern("X").pattern("#").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.e, 2).input('Q', Items.ob).input('C', Blocks.m).pattern("CQ").pattern("QC").criterion("has_quartz", this.a(Items.ob)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ar).input('R', Items.kC).input('#', Blocks.m).input('X', Items.jf).pattern("###").pattern("#X#").pattern("#R#").criterion("has_bow", this.a(Items.jf)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fw).input('R', Items.kC).input('#', Blocks.m).pattern("###").pattern("# #").pattern("#R#").criterion("has_redstone", this.a(Items.kC)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.nF, 9).input(Blocks.ef).criterion("has_at_least_9_emerald", this.a(NumberRange.IntRange.atLeast(9), Items.nF)).criterion("has_emerald_block", this.a((ItemProvider)Blocks.ef)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ef).input('#', Items.nF).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_emerald", this.a(NumberRange.IntRange.atLeast(9), Items.nF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dR).input('B', Items.kS).input('#', Blocks.bJ).input('D', Items.jj).pattern(" B ").pattern("D#D").pattern("###").criterion("has_obsidian", this.a((ItemProvider)Blocks.bJ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ec).input('#', Blocks.bJ).input('E', Items.mt).pattern("###").pattern("#E#").pattern("###").criterion("has_ender_eye", this.a(Items.mt)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.mt).input(Items.mg).input(Items.mp).criterion("has_blaze_powder", this.a(Items.mp)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.iu, 4).input('#', Blocks.dW).pattern("##").pattern("##").criterion("has_end_stone", this.a((ItemProvider)Blocks.dW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oL).input('T', Items.mi).input('E', Items.mt).input('G', Blocks.ao).pattern("GGG").pattern("GEG").pattern("GTG").criterion("has_ender_eye", this.a(Items.mt)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.io, 4).input('#', Items.oN).input('/', Items.mh).pattern("/").pattern("#").criterion("has_chorus_fruit_popped", this.a(Items.oN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cH, 3).input('#', Items.jz).input('W', Blocks.n).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dI).input('#', Items.jz).input('W', Blocks.n).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.mo).input(Items.mn).input(Blocks.bB).input(Items.lC).criterion("has_spider_eye", this.a(Items.mn)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.nC, 3).input(Items.jI).input(Items.mp).input(Ingredient.ofItems(Items.jh, Items.ji)).criterion("has_blaze_powder", this.a(Items.mp)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kY).input('#', Items.jz).input('X', Items.jG).pattern("  #").pattern(" #X").pattern("# X").criterion("has_string", this.a(Items.jG)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jd).input(Items.jk).input(Items.kl).criterion("has_flint", this.a(Items.kl)).criterion("has_obsidian", this.a((ItemProvider)Blocks.bJ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.en).input('#', Items.kL).pattern("# #").pattern(" # ").criterion("has_brick", this.a(Items.kL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bW).input('#', Blocks.m).pattern("###").pattern("# #").pattern("###").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kV).input('A', Blocks.bW).input('B', Items.kA).pattern("A").pattern("B").criterion("has_minecart", this.a(Items.kA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.mm, 3).input('#', Blocks.ao).pattern("# #").pattern(" # ").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dB, 16).input('#', Blocks.ao).pattern("###").pattern("###").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cL).input('#', Items.la).pattern("##").pattern("##").criterion("has_glowstone_dust", this.a(Items.la)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kp).input('#', Items.jl).input('X', Items.je).pattern("###").pattern("#X#").pattern("###").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jF).input('#', Items.jz).input('X', Items.jl).pattern("XX").pattern("X#").pattern(" #").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kk).input('X', Items.jl).pattern("X X").pattern("X X").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.nN).input('#', Items.mj).input('X', Items.nI).pattern("###").pattern("#X#").pattern("###").criterion("has_gold_nugget", this.a(Items.mj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ki).input('X', Items.jl).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kh).input('X', Items.jl).pattern("XXX").pattern("X X").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jN).input('#', Items.jz).input('X', Items.jl).pattern("XX").pattern(" #").pattern(" #").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kj).input('X', Items.jl).pattern("XXX").pattern("X X").pattern("X X").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jE).input('#', Items.jz).input('X', Items.jl).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.aM, 6).input('R', Items.kC).input('#', Items.jz).input('X', Items.jl).pattern("X X").pattern("X#X").pattern("XRX").criterion("has_rail", this.a((ItemProvider)Blocks.cf)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jD).input('#', Items.jz).input('X', Items.jl).pattern("X").pattern("#").pattern("#").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jC).input('#', Items.jz).input('X', Items.jl).pattern("X").pattern("X").pattern("#").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bD).input('#', Items.jl).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_gold_ingot", this.a(NumberRange.IntRange.atLeast(9), Items.jl)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jl, 9).input(Blocks.bD).group("gold_ingot").criterion("has_at_least_9_gold_ingot", this.a(NumberRange.IntRange.atLeast(9), Items.jl)).criterion("has_gold_block", this.a((ItemProvider)Blocks.bD)).offerTo(consumer, "gold_ingot_from_gold_block");
        ShapedRecipeJsonFactory.create(Items.jl).input('#', Items.mj).pattern("###").pattern("###").pattern("###").group("gold_ingot").criterion("has_at_least_9_gold_nugget", this.a(NumberRange.IntRange.atLeast(9), Items.mj)).offerTo(consumer, "gold_ingot_from_nuggets");
        ShapelessRecipeJsonFactory.create(Items.mj, 9).input(Items.jl).criterion("has_at_least_9_gold_nugget", this.a(NumberRange.IntRange.atLeast(9), Items.mj)).criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.c).input(Blocks.e).input(Items.ob).criterion("has_quartz", this.a(Items.ob)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oC).input('#', Blocks.be).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_gray_wool", this.a((ItemProvider)Blocks.be)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GRAY_BED).input('#', Blocks.be).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_gray_wool", this.a((ItemProvider)Blocks.be)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.GRAY_BED).input(Items.WHITE_BED).input(Items.lp).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "gray_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gA, 3).input('#', Blocks.be).pattern("##").group("carpet").criterion("has_gray_wool", this.a((ItemProvider)Blocks.be)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gA, 8).input('#', Blocks.gt).input('$', Items.lp).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_gray_dye", this.a(Items.lp)).offerTo(consumer, "gray_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jL, 8).input(Items.lp).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lp, 2).input(Items.lz).input(Items.lA).criterion("has_white_dye", this.a(Items.lA)).criterion("has_black_dye", this.a(Items.lz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cY, 8).input('#', Blocks.ao).input('X', Items.lp).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fU, 16).input('#', Blocks.cY).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fU, 8).input('#', Blocks.dB).input('$', Items.lp).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_gray_dye", this.a(Items.lp)).offerTo(consumer, "gray_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fE, 8).input('#', Blocks.gJ).input('X', Items.lp).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.be).input(Items.lp).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oI).input('#', Blocks.bk).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_green_wool", this.a((ItemProvider)Blocks.bk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GREEN_BED).input('#', Blocks.bk).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_green_wool", this.a((ItemProvider)Blocks.bk)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.GREEN_BED).input(Items.WHITE_BED).input(Items.lj).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "green_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gG, 3).input('#', Blocks.bk).pattern("##").group("carpet").criterion("has_green_wool", this.a((ItemProvider)Blocks.bk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gG, 8).input('#', Blocks.gt).input('$', Items.lj).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_green_dye", this.a(Items.lj)).offerTo(consumer, "green_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jR, 8).input(Items.lj).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.de, 8).input('#', Blocks.ao).input('X', Items.lj).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ga, 16).input('#', Blocks.de).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ga, 8).input('#', Blocks.dB).input('$', Items.lj).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_green_dye", this.a(Items.lj)).offerTo(consumer, "green_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fK, 8).input('#', Blocks.gJ).input('X', Items.lj).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bk).input(Items.lj).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gs).input('#', Items.jP).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_wheat", this.a(NumberRange.IntRange.atLeast(9), Items.jP)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fl).input('#', Items.jk).pattern("##").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fq).input('C', Blocks.bP).input('I', Items.jk).pattern("I I").pattern("ICI").pattern(" I ").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.od).input('A', Blocks.fq).input('B', Items.kA).pattern("A").pattern("B").criterion("has_minecart", this.a(Items.kA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jc).input('#', Items.jz).input('X', Items.jk).pattern("XX").pattern("X#").pattern(" #").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dA, 16).input('#', Items.jk).pattern("###").pattern("###").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bE).input('#', Items.jk).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_iron_ingot", this.a(NumberRange.IntRange.atLeast(9), Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kc).input('X', Items.jk).pattern("X X").pattern("X X").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ka).input('X', Items.jk).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cp, 3).input('#', Items.jk).pattern("##").pattern("##").pattern("##").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jZ).input('X', Items.jk).pattern("XXX").pattern("X X").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jL).input('#', Items.jz).input('X', Items.jk).pattern("XX").pattern(" #").pattern(" #").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jk, 9).input(Blocks.bE).group("iron_ingot").criterion("has_at_least_9_iron_ingot", this.a(NumberRange.IntRange.atLeast(9), Items.jk)).criterion("has_iron_block", this.a((ItemProvider)Blocks.bE)).offerTo(consumer, "iron_ingot_from_iron_block");
        ShapedRecipeJsonFactory.create(Items.jk).input('#', Items.pf).pattern("###").pattern("###").pattern("###").group("iron_ingot").criterion("has_at_least_9_iron_nugget", this.a(NumberRange.IntRange.atLeast(9), Items.pf)).offerTo(consumer, "iron_ingot_from_nuggets");
        ShapedRecipeJsonFactory.create(Items.kb).input('X', Items.jk).pattern("XXX").pattern("X X").pattern("X X").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.pf, 9).input(Items.jk).criterion("has_at_least_9_iron_nugget", this.a(NumberRange.IntRange.atLeast(9), Items.pf)).criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jb).input('#', Items.jz).input('X', Items.jk).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ja).input('#', Items.jz).input('X', Items.jk).pattern("X").pattern("#").pattern("#").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jm).input('#', Items.jz).input('X', Items.jk).pattern("X").pattern("X").pattern("#").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gh).input('#', Items.jk).pattern("##").pattern("##").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.nG).input('#', Items.jz).input('X', Items.kF).pattern("###").pattern("#X#").pattern("###").criterion("has_leather", this.a(Items.kF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cG).input('#', ItemTags.b).input('X', Items.jj).pattern("###").pattern("#X#").pattern("###").criterion("has_diamond", this.a(Items.jj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.X, 3).input('#', Blocks.L).pattern("##").pattern("##").group("bark").criterion("has_log", this.a((ItemProvider)Blocks.L)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.pa).input('#', Blocks.q).pattern("# #").pattern("###").group("boat").criterion("in_water", this.a(Blocks.A)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.eR).input(Blocks.q).group("wooden_button").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.il, 3).input('#', Blocks.q).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ig, 3).input('#', Items.jz).input('W', Blocks.q).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ib).input('#', Items.jz).input('W', Blocks.q).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.q, 4).input(ItemTags.t).group("planks").criterion("has_log", this.a(ItemTags.t)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ct).input('#', Blocks.q).pattern("##").group("wooden_pressure_plate").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hF, 6).input('#', Blocks.q).pattern("###").group("wooden_slab").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ei, 4).input('#', Blocks.q).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dk, 2).input('#', Blocks.q).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", this.a((ItemProvider)Blocks.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ce, 3).input('#', Items.jz).pattern("# #").pattern("###").pattern("# #").criterion("has_stick", this.a(Items.jz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.aq).input('#', Items.ll).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_lapis", this.a(NumberRange.IntRange.atLeast(9), Items.ll)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ll, 9).input(Blocks.aq).criterion("has_at_least_9_lapis", this.a(NumberRange.IntRange.atLeast(9), Items.ll)).criterion("has_lapis_block", this.a((ItemProvider)Blocks.aq)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oq, 2).input('~', Items.jG).input('O', Items.kT).pattern("~~ ").pattern("~O ").pattern("  ~").criterion("has_slime_ball", this.a(Items.kT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kF).input('#', Items.ok).pattern("##").pattern("##").criterion("has_rabbit_hide", this.a(Items.ok)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jU).input('X', Items.kF).pattern("X X").pattern("X X").criterion("has_leather", this.a(Items.kF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jS).input('X', Items.kF).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_leather", this.a(Items.kF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jR).input('X', Items.kF).pattern("XXX").pattern("X X").criterion("has_leather", this.a(Items.kF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jT).input('X', Items.kF).pattern("XXX").pattern("X X").pattern("X X").criterion("has_leather", this.a(Items.kF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.op).input('X', Items.kF).pattern("X X").pattern("XXX").pattern("X X").criterion("has_leather", this.a(Items.kF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lQ).input('S', ItemTags.i).input('B', Blocks.bH).pattern("SSS").pattern(" B ").pattern(" S ").criterion("has_book", this.a(Items.kS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cn).input('#', Blocks.m).input('X', Items.jz).pattern("X").pattern("#").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oy).input('#', Blocks.ba).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_light_blue_wool", this.a((ItemProvider)Blocks.ba)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LIGHT_BLUE_BED).input('#', Blocks.ba).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_light_blue_wool", this.a((ItemProvider)Blocks.ba)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_BED).input(Items.WHITE_BED).input(Items.lt).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "light_blue_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gw, 3).input('#', Blocks.ba).pattern("##").group("carpet").criterion("has_light_blue_wool", this.a((ItemProvider)Blocks.ba)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gw, 8).input('#', Blocks.gt).input('$', Items.lt).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_light_blue_dye", this.a(Items.lt)).offerTo(consumer, "light_blue_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jH, 8).input(Items.lt).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lt).input(Blocks.bq).group("light_blue_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bq)).offerTo(consumer, "light_blue_dye_from_blue_orchid");
        ShapelessRecipeJsonFactory.create(Items.lt, 2).input(Items.lx).input(Items.lA).group("light_blue_dye").criterion("has_blue_dye", this.a(Items.lx)).criterion("has_white_dye", this.a(Items.lA)).offerTo(consumer, "light_blue_dye_from_blue_white_dye");
        ShapedRecipeJsonFactory.create(Blocks.cU, 8).input('#', Blocks.ao).input('X', Items.lt).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fQ, 16).input('#', Blocks.cU).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fQ, 8).input('#', Blocks.dB).input('$', Items.lt).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_light_blue_dye", this.a(Items.lt)).offerTo(consumer, "light_blue_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fA, 8).input('#', Blocks.gJ).input('X', Items.lt).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.ba).input(Items.lt).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oD).input('#', Blocks.bf).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_light_gray_wool", this.a((ItemProvider)Blocks.bf)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LIGHT_GRAY_BED).input('#', Blocks.bf).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_light_gray_wool", this.a((ItemProvider)Blocks.bf)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_BED).input(Items.WHITE_BED).input(Items.lo).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "light_gray_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gB, 3).input('#', Blocks.bf).pattern("##").group("carpet").criterion("has_light_gray_wool", this.a((ItemProvider)Blocks.bf)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gB, 8).input('#', Blocks.gt).input('$', Items.lo).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_light_gray_dye", this.a(Items.lo)).offerTo(consumer, "light_gray_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jM, 8).input(Items.lo).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lo).input(Blocks.bs).group("light_gray_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bs)).offerTo(consumer, "light_gray_dye_from_azure_bluet");
        ShapelessRecipeJsonFactory.create(Items.lo, 2).input(Items.lp).input(Items.lA).group("light_gray_dye").criterion("has_gray_dye", this.a(Items.lp)).criterion("has_white_dye", this.a(Items.lA)).offerTo(consumer, "light_gray_dye_from_gray_white_dye");
        ShapelessRecipeJsonFactory.create(Items.lo, 3).input(Items.lz).input(Items.lA, 2).group("light_gray_dye").criterion("has_white_dye", this.a(Items.lA)).criterion("has_black_dye", this.a(Items.lz)).offerTo(consumer, "light_gray_dye_from_black_white_dye");
        ShapelessRecipeJsonFactory.create(Items.lo).input(Blocks.bx).group("light_gray_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bx)).offerTo(consumer, "light_gray_dye_from_oxeye_daisy");
        ShapelessRecipeJsonFactory.create(Items.lo).input(Blocks.bv).group("light_gray_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bv)).offerTo(consumer, "light_gray_dye_from_white_tulip");
        ShapedRecipeJsonFactory.create(Blocks.cZ, 8).input('#', Blocks.ao).input('X', Items.lo).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fV, 16).input('#', Blocks.cZ).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fV, 8).input('#', Blocks.dB).input('$', Items.lo).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_light_gray_dye", this.a(Items.lo)).offerTo(consumer, "light_gray_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fF, 8).input('#', Blocks.gJ).input('X', Items.lo).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bf).input(Items.lo).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fk).input('#', Items.jl).pattern("##").criterion("has_gold_ingot", this.a(Items.jl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oA).input('#', Blocks.bc).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_lime_wool", this.a((ItemProvider)Blocks.bc)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LIME_BED).input('#', Blocks.bc).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_lime_wool", this.a((ItemProvider)Blocks.bc)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.LIME_BED).input(Items.WHITE_BED).input(Items.lr).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "lime_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gy, 3).input('#', Blocks.bc).pattern("##").group("carpet").criterion("has_lime_wool", this.a((ItemProvider)Blocks.bc)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gy, 8).input('#', Blocks.gt).input('$', Items.lr).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_lime_dye", this.a(Items.lr)).offerTo(consumer, "lime_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jJ, 8).input(Items.lr).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lr, 2).input(Items.lj).input(Items.lA).criterion("has_green_dye", this.a(Items.lj)).criterion("has_white_dye", this.a(Items.lA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cW, 8).input('#', Blocks.ao).input('X', Items.lr).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fS, 16).input('#', Blocks.cW).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fS, 8).input('#', Blocks.dB).input('$', Items.lr).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_lime_dye", this.a(Items.lr)).offerTo(consumer, "lime_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fC, 8).input('#', Blocks.gJ).input('X', Items.lr).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bc).input(Items.lr).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cO).input('A', Blocks.cN).input('B', Blocks.bK).pattern("A").pattern("B").criterion("has_carved_pumpkin", this.a((ItemProvider)Blocks.cN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ox).input('#', Blocks.aZ).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_magenta_wool", this.a((ItemProvider)Blocks.aZ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.MAGENTA_BED).input('#', Blocks.aZ).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_magenta_wool", this.a((ItemProvider)Blocks.aZ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.MAGENTA_BED).input(Items.WHITE_BED).input(Items.lu).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "magenta_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gv, 3).input('#', Blocks.aZ).pattern("##").group("carpet").criterion("has_magenta_wool", this.a((ItemProvider)Blocks.aZ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gv, 8).input('#', Blocks.gt).input('$', Items.lu).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_magenta_dye", this.a(Items.lu)).offerTo(consumer, "magenta_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jG, 8).input(Items.lu).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lu).input(Blocks.br).group("magenta_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.br)).offerTo(consumer, "magenta_dye_from_allium");
        ShapelessRecipeJsonFactory.create(Items.lu, 4).input(Items.lx).input(Items.li, 2).input(Items.lA).group("magenta_dye").criterion("has_blue_dye", this.a(Items.lx)).criterion("has_rose_red", this.a(Items.li)).criterion("has_white_dye", this.a(Items.lA)).offerTo(consumer, "magenta_dye_from_blue_red_white_dye");
        ShapelessRecipeJsonFactory.create(Items.lu, 3).input(Items.lx).input(Items.li).input(Items.lq).group("magenta_dye").criterion("has_pink_dye", this.a(Items.lq)).criterion("has_blue_dye", this.a(Items.lx)).criterion("has_red_dye", this.a(Items.li)).offerTo(consumer, "magenta_dye_from_blue_red_pink");
        ShapelessRecipeJsonFactory.create(Items.lu, 2).input(Blocks.gN).group("magenta_dye").criterion("has_double_plant", this.a((ItemProvider)Blocks.gN)).offerTo(consumer, "magenta_dye_from_lilac");
        ShapelessRecipeJsonFactory.create(Items.lu, 2).input(Items.lm).input(Items.lq).group("magenta_dye").criterion("has_pink_dye", this.a(Items.lq)).criterion("has_purple_dye", this.a(Items.lm)).offerTo(consumer, "magenta_dye_from_purple_and_pink");
        ShapedRecipeJsonFactory.create(Blocks.cT, 8).input('#', Blocks.ao).input('X', Items.lu).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fP, 16).input('#', Blocks.cT).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fP, 8).input('#', Blocks.dB).input('$', Items.lu).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_magenta_dye", this.a(Items.lu)).offerTo(consumer, "magenta_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fz, 8).input('#', Blocks.gJ).input('X', Items.lu).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.aZ).input(Items.lu).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.iB).input('#', Items.mq).pattern("##").pattern("##").criterion("has_magma_cream", this.a(Items.mq)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.mq).input(Items.mp).input(Items.kT).criterion("has_blaze_powder", this.a(Items.mp)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.nM).input('#', Items.kR).input('X', Items.kX).pattern("###").pattern("#X#").pattern("###").criterion("has_compass", this.a(Items.kX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dC).input('M', Items.lX).pattern("MMM").pattern("MMM").pattern("MMM").criterion("has_melon", this.a(Items.lX)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ma).input(Items.lX).criterion("has_melon", this.a(Items.lX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kA).input('#', Items.jk).pattern("# #").pattern("###").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bI).input(Blocks.m).input(Blocks.dH).criterion("has_vine", this.a((ItemProvider)Blocks.dH)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.em, 6).input('#', Blocks.bI).pattern("###").pattern("###").criterion("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.do_).input(Blocks.dn).input(Blocks.dH).criterion("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jB).input(Blocks.bB).input(Blocks.bC).input(Items.jA).criterion("has_mushroom_stew", this.a(Items.jB)).criterion("has_bowl", this.a(Items.jA)).criterion("has_brown_mushroom", this.a((ItemProvider)Blocks.bB)).criterion("has_red_mushroom", this.a((ItemProvider)Blocks.bC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dN).input('N', Items.oa).pattern("NN").pattern("NN").criterion("has_netherbrick", this.a(Items.oa)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dO, 6).input('#', Blocks.dN).input('-', Items.oa).pattern("#-#").pattern("#-#").criterion("has_nether_brick", this.a((ItemProvider)Blocks.dN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hQ, 6).input('#', Blocks.dN).pattern("###").criterion("has_nether_brick", this.a((ItemProvider)Blocks.dN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dP, 4).input('#', Blocks.dN).pattern("#  ").pattern("## ").pattern("###").criterion("has_nether_brick", this.a((ItemProvider)Blocks.dN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.iC).input('#', Items.mk).pattern("###").pattern("###").pattern("###").criterion("has_nether_wart", this.a(Items.mk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.av).input('#', ItemTags.b).input('X', Items.kC).pattern("###").pattern("#X#").pattern("###").criterion("has_redstone", this.a(Items.kC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.U, 3).input('#', Blocks.I).pattern("##").pattern("##").group("bark").criterion("has_log", this.a((ItemProvider)Blocks.I)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.eO).input(Blocks.n).group("wooden_button").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.n, 4).input(ItemTags.q).group("planks").criterion("has_log", this.a(ItemTags.q)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cq).input('#', Blocks.n).pattern("##").group("wooden_pressure_plate").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hC, 6).input('#', Blocks.n).pattern("###").group("wooden_slab").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bO, 4).input('#', Blocks.n).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dh, 2).input('#', Blocks.n).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.iG).input('Q', Items.ob).input('R', Items.kC).input('#', Blocks.m).pattern("###").pattern("RRQ").pattern("###").criterion("has_quartz", this.a(Items.ob)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ow).input('#', Blocks.aY).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_orange_wool", this.a((ItemProvider)Blocks.aY)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ORANGE_BED).input('#', Blocks.aY).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_orange_wool", this.a((ItemProvider)Blocks.aY)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ORANGE_BED).input(Items.WHITE_BED).input(Items.lv).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "orange_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gu, 3).input('#', Blocks.aY).pattern("##").group("carpet").criterion("has_orange_wool", this.a((ItemProvider)Blocks.aY)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gu, 8).input('#', Blocks.gt).input('$', Items.lv).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_oramge_dye", this.a(Items.lv)).offerTo(consumer, "orange_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jF, 8).input(Items.lv).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lv).input(Blocks.bu).group("orange_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bu)).offerTo(consumer, "orange_dye_from_orange_tulip");
        ShapelessRecipeJsonFactory.create(Items.lv, 2).input(Items.li).input(Items.ls).group("orange_dye").criterion("has_red_dye", this.a(Items.li)).criterion("has_yellow_dye", this.a(Items.ls)).offerTo(consumer, "orange_dye_from_red_yellow");
        ShapedRecipeJsonFactory.create(Blocks.cS, 8).input('#', Blocks.ao).input('X', Items.lv).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fO, 16).input('#', Blocks.cS).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fO, 8).input('#', Blocks.dB).input('$', Items.lv).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_orange_dye", this.a(Items.lv)).offerTo(consumer, "orange_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fy, 8).input('#', Blocks.gJ).input('X', Items.lv).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.aY).input(Items.lv).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ko).input('#', Items.jz).input('X', Ingredient.fromTag(ItemTags.a)).pattern("###").pattern("#X#").pattern("###").criterion("has_wool", this.a(ItemTags.a)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kR, 3).input('#', Blocks.cF).pattern("###").criterion("has_reeds", this.a((ItemProvider)Blocks.cF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ft, 2).input('#', Blocks.fr).pattern("#").pattern("#").criterion("has_chiseled_quartz_block", this.a((ItemProvider)Blocks.fs)).criterion("has_quartz_block", this.a((ItemProvider)Blocks.fr)).criterion("has_quartz_pillar", this.a((ItemProvider)Blocks.ft)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.gL).input(Blocks.cB, 9).criterion("has_at_least_9_ice", this.a(NumberRange.IntRange.atLeast(9), Blocks.cB)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oB).input('#', Blocks.bd).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_pink_wool", this.a((ItemProvider)Blocks.bd)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.PINK_BED).input('#', Blocks.bd).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_pink_wool", this.a((ItemProvider)Blocks.bd)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.PINK_BED).input(Items.WHITE_BED).input(Items.lq).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "pink_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gz, 3).input('#', Blocks.bd).pattern("##").group("carpet").criterion("has_pink_wool", this.a((ItemProvider)Blocks.bd)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gz, 8).input('#', Blocks.gt).input('$', Items.lq).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_pink_dye", this.a(Items.lq)).offerTo(consumer, "pink_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jK, 8).input(Items.lq).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lq, 2).input(Blocks.gP).group("pink_dye").criterion("has_double_plant", this.a((ItemProvider)Blocks.gP)).offerTo(consumer, "pink_dye_from_peony");
        ShapelessRecipeJsonFactory.create(Items.lq).input(Blocks.bw).group("pink_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bw)).offerTo(consumer, "pink_dye_from_pink_tulip");
        ShapelessRecipeJsonFactory.create(Items.lq, 2).input(Items.li).input(Items.lA).group("pink_dye").criterion("has_white_dye", this.a(Items.lA)).criterion("has_red_dye", this.a(Items.li)).offerTo(consumer, "pink_dye_from_red_white_dye");
        ShapedRecipeJsonFactory.create(Blocks.cX, 8).input('#', Blocks.ao).input('X', Items.lq).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fT, 16).input('#', Blocks.cX).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fT, 8).input('#', Blocks.dB).input('$', Items.lq).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_pink_dye", this.a(Items.lq)).offerTo(consumer, "pink_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fD, 8).input('#', Blocks.gJ).input('X', Items.lq).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bd).input(Items.lq).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.aV).input('R', Items.kC).input('#', Blocks.m).input('T', ItemTags.b).input('X', Items.jk).pattern("TTT").pattern("#X#").pattern("#R#").criterion("has_redstone", this.a(Items.kC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.d, 4).input('S', Blocks.c).pattern("SS").pattern("SS").criterion("has_stone", this.a((ItemProvider)Blocks.c)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.f, 4).input('S', Blocks.e).pattern("SS").pattern("SS").criterion("has_stone", this.a((ItemProvider)Blocks.e)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.h, 4).input('S', Blocks.g).pattern("SS").pattern("SS").criterion("has_stone", this.a((ItemProvider)Blocks.g)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gi).input('S', Items.oe).pattern("SS").pattern("SS").criterion("has_prismarine_shard", this.a(Items.oe)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gj).input('S', Items.oe).pattern("SSS").pattern("SSS").pattern("SSS").criterion("has_prismarine_shard", this.a(Items.oe)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.go, 6).input('#', Blocks.gi).pattern("###").criterion("has_prismarine", this.a((ItemProvider)Blocks.gi)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gp, 6).input('#', Blocks.gj).pattern("###").criterion("has_prismarine_bricks", this.a((ItemProvider)Blocks.gj)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gq, 6).input('#', Blocks.gk).pattern("###").criterion("has_dark_prismarine", this.a((ItemProvider)Blocks.gk)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.nW).input(Blocks.cI).input(Items.lC).input(Items.kW).criterion("has_carved_pumpkin", this.a((ItemProvider)Blocks.cN)).criterion("has_pumpkin", this.a((ItemProvider)Blocks.cI)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lZ, 4).input(Blocks.cI).criterion("has_pumpkin", this.a((ItemProvider)Blocks.cI)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oF).input('#', Blocks.bh).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_purple_wool", this.a((ItemProvider)Blocks.bh)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.PURPLE_BED).input('#', Blocks.bh).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_purple_wool", this.a((ItemProvider)Blocks.bh)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.PURPLE_BED).input(Items.WHITE_BED).input(Items.lm).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "purple_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gD, 3).input('#', Blocks.bh).pattern("##").group("carpet").criterion("has_purple_wool", this.a((ItemProvider)Blocks.bh)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gD, 8).input('#', Blocks.gt).input('$', Items.lm).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_purple_dye", this.a(Items.lm)).offerTo(consumer, "purple_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jO, 8).input(Items.lm).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lm, 2).input(Items.lx).input(Items.li).criterion("has_blue_dye", this.a(Items.lx)).criterion("has_red_dye", this.a(Items.li)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.iH).input('#', Blocks.bP).input('-', Items.pe).pattern("-").pattern("#").pattern("-").criterion("has_shulker_shell", this.a(Items.pe)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.db, 8).input('#', Blocks.ao).input('X', Items.lm).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fX, 16).input('#', Blocks.db).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fX, 8).input('#', Blocks.dB).input('$', Items.lm).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_purple_dye", this.a(Items.lm)).offerTo(consumer, "purple_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fH, 8).input('#', Blocks.gJ).input('X', Items.lm).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bh).input(Items.lm).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ir, 4).input('F', Items.oN).pattern("FF").pattern("FF").criterion("has_chorus_fruit_popped", this.a(Items.oN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.is).input('#', Blocks.hU).pattern("#").pattern("#").criterion("has_purpur_block", this.a((ItemProvider)Blocks.ir)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hU, 6).input('#', Ingredient.ofItems(Blocks.ir, Blocks.is)).pattern("###").criterion("has_purpur_block", this.a((ItemProvider)Blocks.ir)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.it, 4).input('#', Ingredient.ofItems(Blocks.ir, Blocks.is)).pattern("#  ").pattern("## ").pattern("###").criterion("has_purpur_block", this.a((ItemProvider)Blocks.ir)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fr).input('#', Items.ob).pattern("##").pattern("##").criterion("has_quartz", this.a(Items.ob)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hR, 6).input('#', Ingredient.ofItems(Blocks.fs, Blocks.fr, Blocks.ft)).pattern("###").criterion("has_chiseled_quartz_block", this.a((ItemProvider)Blocks.fs)).criterion("has_quartz_block", this.a((ItemProvider)Blocks.fr)).criterion("has_quartz_pillar", this.a((ItemProvider)Blocks.ft)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fu, 4).input('#', Ingredient.ofItems(Blocks.fs, Blocks.fr, Blocks.ft)).pattern("#  ").pattern("## ").pattern("###").criterion("has_chiseled_quartz_block", this.a((ItemProvider)Blocks.fs)).criterion("has_quartz_block", this.a((ItemProvider)Blocks.fr)).criterion("has_quartz_pillar", this.a((ItemProvider)Blocks.ft)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.oi).input(Items.nK).input(Items.oh).input(Items.jA).input(Items.nI).input(Blocks.bB).group("rabbit_stew").criterion("has_cooked_rabbit", this.a(Items.oh)).offerTo(consumer, "rabbit_stew_from_brown_mushroom");
        ShapelessRecipeJsonFactory.create(Items.oi).input(Items.nK).input(Items.oh).input(Items.jA).input(Items.nI).input(Blocks.bC).group("rabbit_stew").criterion("has_cooked_rabbit", this.a(Items.oh)).offerTo(consumer, "rabbit_stew_from_red_mushroom");
        ShapedRecipeJsonFactory.create(Blocks.cf, 16).input('#', Items.jz).input('X', Items.jk).pattern("X X").pattern("X#X").pattern("X X").criterion("has_minecart", this.a(Items.kA)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.kC, 9).input(Blocks.fo).criterion("has_redstone_block", this.a((ItemProvider)Blocks.fo)).criterion("has_at_least_9_redstone", this.a(NumberRange.IntRange.atLeast(9), Items.kC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fo).input('#', Items.kC).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_redstone", this.a(NumberRange.IntRange.atLeast(9), Items.kC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dY).input('R', Items.kC).input('G', Blocks.cL).pattern(" R ").pattern("RGR").pattern(" R ").criterion("has_glowstone", this.a((ItemProvider)Blocks.cL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cx).input('#', Items.jz).input('X', Items.kC).pattern("X").pattern("#").criterion("has_redstone", this.a(Items.kC)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oJ).input('#', Blocks.bl).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_red_wool", this.a((ItemProvider)Blocks.bl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.RED_BED).input('#', Blocks.bl).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_red_wool", this.a((ItemProvider)Blocks.bl)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.RED_BED).input(Items.WHITE_BED).input(Items.li).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "red_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gH, 3).input('#', Blocks.bl).pattern("##").group("carpet").criterion("has_red_wool", this.a((ItemProvider)Blocks.bl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gH, 8).input('#', Blocks.gt).input('$', Items.li).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_red_dye", this.a(Items.li)).offerTo(consumer, "red_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jS, 8).input(Items.li).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.li).input(Items.oO).group("red_dye").criterion("has_beetroot", this.a(Items.oO)).offerTo(consumer, "red_dye_from_beetroot");
        ShapelessRecipeJsonFactory.create(Items.li).input(Blocks.bp).group("red_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bp)).offerTo(consumer, "red_dye_from_poppy");
        ShapelessRecipeJsonFactory.create(Items.li, 2).input(Blocks.gO).group("red_dye").criterion("has_double_plant", this.a((ItemProvider)Blocks.gO)).offerTo(consumer, "red_dye_from_rose_bush");
        ShapelessRecipeJsonFactory.create(Items.li).input(Blocks.bt).group("red_dye").criterion("has_red_flower", this.a((ItemProvider)Blocks.bt)).offerTo(consumer, "red_dye_from_tulip");
        ShapedRecipeJsonFactory.create(Blocks.iD).input('W', Items.mk).input('N', Items.oa).pattern("NW").pattern("WN").criterion("has_nether_wart", this.a(Items.mk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hy).input('#', Blocks.D).pattern("##").pattern("##").criterion("has_sand", this.a((ItemProvider)Blocks.D)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hS, 6).input('#', Ingredient.ofItems(Blocks.hy, Blocks.hz)).pattern("###").criterion("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).criterion("has_chiseled_red_sandstone", this.a((ItemProvider)Blocks.hz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hT, 6).input('#', Blocks.hA).pattern("###").criterion("has_cut_red_sandstone", this.a((ItemProvider)Blocks.hA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hB, 4).input('#', Ingredient.ofItems(Blocks.hy, Blocks.hz, Blocks.hA)).pattern("#  ").pattern("## ").pattern("###").criterion("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).criterion("has_chiseled_red_sandstone", this.a((ItemProvider)Blocks.hz)).criterion("has_cut_red_sandstone", this.a((ItemProvider)Blocks.hA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.df, 8).input('#', Blocks.ao).input('X', Items.li).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gb, 16).input('#', Blocks.df).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gb, 8).input('#', Blocks.dB).input('$', Items.li).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_red_dye", this.a(Items.li)).offerTo(consumer, "red_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fL, 8).input('#', Blocks.gJ).input('X', Items.li).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bl).input(Items.li).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cQ).input('#', Blocks.cx).input('X', Items.kC).input('I', Blocks.b).pattern("#X#").pattern("III").criterion("has_redstone_torch", this.a((ItemProvider)Blocks.cx)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.as).input('#', Blocks.C).pattern("##").pattern("##").criterion("has_sand", this.a((ItemProvider)Blocks.C)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hK, 6).input('#', Ingredient.ofItems(Blocks.as, Blocks.at)).pattern("###").criterion("has_sandstone", this.a((ItemProvider)Blocks.as)).criterion("has_chiseled_sandstone", this.a((ItemProvider)Blocks.at)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hL, 6).input('#', Blocks.au).pattern("###").criterion("has_cut_sandstone", this.a((ItemProvider)Blocks.au)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ea, 4).input('#', Ingredient.ofItems(Blocks.as, Blocks.at, Blocks.au)).pattern("#  ").pattern("## ").pattern("###").criterion("has_sandstone", this.a((ItemProvider)Blocks.as)).criterion("has_chiseled_sandstone", this.a((ItemProvider)Blocks.at)).criterion("has_cut_sandstone", this.a((ItemProvider)Blocks.au)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gr).input('S', Items.oe).input('C', Items.of).pattern("SCS").pattern("CCC").pattern("SCS").criterion("has_prismarine_crystals", this.a(Items.of)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.lW).input('#', Items.jk).pattern(" #").pattern("# ").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oW).input('W', ItemTags.b).input('o', Items.jk).pattern("WoW").pattern("WWW").pattern(" W ").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kr, 3).input('#', Items.OAK_PLANKS).input('X', Items.jz).pattern("###").pattern("###").pattern(" X ").criterion("has_oak_planks", this.a(Items.OAK_PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ks, 3).input('#', Items.SPRUCE_PLANKS).input('X', Items.jz).pattern("###").pattern("###").pattern(" X ").criterion("has_spruce_planks", this.a(Items.SPRUCE_PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kt, 3).input('#', Items.BIRCH_PLANKS).input('X', Items.jz).pattern("###").pattern("###").pattern(" X ").criterion("has_birch_planks", this.a(Items.BIRCH_PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kv, 3).input('#', Items.ACACIA_PLANKS).input('X', Items.jz).pattern("###").pattern("###").pattern(" X ").criterion("has_acacia_planks", this.a(Items.ACACIA_PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ku, 3).input('#', Items.JUNGLE_PLANKS).input('X', Items.jz).pattern("###").pattern("###").pattern(" X ").criterion("has_jungle_planks", this.a(Items.JUNGLE_PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.kw, 3).input('#', Items.DARK_OAK_PLANKS).input('X', Items.jz).pattern("###").pattern("###").pattern(" X ").criterion("has_dark_oak_planks", this.a(Items.DARK_OAK_PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gf).input('#', Items.kT).pattern("###").pattern("###").pattern("###").criterion("has_at_least_9_slime_ball", this.a(NumberRange.IntRange.atLeast(9), Items.kT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.kT, 9).input(Blocks.gf).criterion("has_at_least_9_slime_ball", this.a(NumberRange.IntRange.atLeast(9), Items.kT)).criterion("has_slime", this.a((ItemProvider)Blocks.gf)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hA, 4).input('#', Blocks.hy).pattern("##").pattern("##").criterion("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.au, 4).input('#', Blocks.as).pattern("##").pattern("##").criterion("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cC).input('#', Items.kD).pattern("##").pattern("##").criterion("has_snowball", this.a(Items.kD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cA, 6).input('#', Blocks.cC).pattern("###").criterion("has_snowball", this.a(Items.kD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.mu).input('#', Items.mj).input('X', Items.lX).pattern("###").pattern("#X#").pattern("###").criterion("has_melon", this.a(Items.lX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oT, 2).input('#', Items.la).input('X', Items.jg).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_glowstone_dust", this.a(Items.la)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.V, 3).input('#', Blocks.J).pattern("##").pattern("##").group("bark").criterion("has_log", this.a((ItemProvider)Blocks.J)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oY).input('#', Blocks.o).pattern("# #").pattern("###").group("boat").criterion("in_water", this.a(Blocks.A)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.eP).input(Blocks.o).group("wooden_button").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ij, 3).input('#', Blocks.o).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ie, 3).input('#', Items.jz).input('W', Blocks.o).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hZ).input('#', Items.jz).input('W', Blocks.o).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.o, 4).input(ItemTags.u).group("planks").criterion("has_log", this.a(ItemTags.u)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cr).input('#', Blocks.o).pattern("##").group("wooden_pressure_plate").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hD, 6).input('#', Blocks.o).pattern("###").group("wooden_slab").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.eg, 4).input('#', Blocks.o).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.di, 2).input('#', Blocks.o).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", this.a((ItemProvider)Blocks.o)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jz, 4).input('#', ItemTags.b).pattern("#").pattern("#").group("sticks").criterion("has_planks", this.a(ItemTags.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jz, 1).input('#', Blocks.kQ).pattern("#").pattern("#").group("sticks").criterion("has_bamboo", this.a((ItemProvider)Blocks.kQ)).offerTo(consumer, "stick_from_bamboo_item");
        ShapedRecipeJsonFactory.create(Blocks.aO).input('P', Blocks.aV).input('S', Items.kT).pattern("S").pattern("P").criterion("has_slime_ball", this.a(Items.kT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dn, 4).input('#', Blocks.b).pattern("##").pattern("##").criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ju).input('#', Items.jz).input('X', Blocks.m).pattern("XX").pattern("X#").pattern(" #").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hP, 6).input('#', Blocks.dn).pattern("###").criterion("has_stone_bricks", this.a(ItemTags.c)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.dK, 4).input('#', Blocks.dn).pattern("#  ").pattern("## ").pattern("###").criterion("has_stone_bricks", this.a(ItemTags.c)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.cz).input(Blocks.b).criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jK).input('#', Items.jz).input('X', Blocks.m).pattern("XX").pattern(" #").pattern(" #").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jt).input('#', Items.jz).input('X', Blocks.m).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.co).input('#', Blocks.b).pattern("##").criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.js).input('#', Items.jz).input('X', Blocks.m).pattern("X").pattern("#").pattern("#").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hI, 6).input('#', Blocks.b).pattern("###").criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.hJ, 6).input('#', Blocks.hV).pattern("###").criterion("has_smooth_stone", this.a((ItemProvider)Blocks.hV)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cg, 4).input('#', Blocks.m).pattern("#  ").pattern("## ").pattern("###").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jr).input('#', Items.jz).input('X', Blocks.m).pattern("X").pattern("X").pattern("#").criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.aX).input('#', Items.jG).pattern("##").pattern("##").criterion("has_string", this.a(Items.jG)).offerTo(consumer, "white_wool_from_string");
        ShapelessRecipeJsonFactory.create(Items.lC).input(Blocks.cF).criterion("has_reeds", this.a((ItemProvider)Blocks.cF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bG).input('#', Ingredient.ofItems(Blocks.C, Blocks.D)).input('X', Items.jI).pattern("X#X").pattern("#X#").pattern("X#X").criterion("has_gunpowder", this.a(Items.jI)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oc).input('A', Blocks.bG).input('B', Items.kA).pattern("A").pattern("B").criterion("has_minecart", this.a(Items.kA)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.bK, 4).input('#', Items.jz).input('X', Ingredient.ofItems(Items.jh, Items.ji)).pattern("X").pattern("#").criterion("has_stone_pickaxe", this.a(Items.jt)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lU).input('#', Items.TORCH).input('X', Items.pf).pattern("XXX").pattern("X#X").pattern("XXX").criterion("has_iron_nugget", this.a(Items.pf)).criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.fj).input(Blocks.bP).input(Blocks.ed).criterion("has_tripwire_hook", this.a((ItemProvider)Blocks.ed)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ed, 2).input('#', ItemTags.b).input('S', Items.jz).input('I', Items.jk).pattern("I").pattern("S").pattern("#").criterion("has_string", this.a(Items.jG)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.iY).input('X', Items.iZ).pattern("XXX").pattern("X X").criterion("has_scute", this.a(Items.iZ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.jP, 9).input(Blocks.gs).criterion("has_at_least_9_wheat", this.a(NumberRange.IntRange.atLeast(9), Items.jP)).criterion("has_hay_block", this.a((ItemProvider)Blocks.gs)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ov).input('#', Blocks.aX).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.WHITE_BED).input('#', Blocks.aX).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gt, 3).input('#', Blocks.aX).pattern("##").group("carpet").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.jE, 8).input(Items.lA).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lA).input(Items.lw).group("white_dye").criterion("has_bone_meal", this.a(Items.lw)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lA).input(Blocks.bA).group("white_dye").criterion("has_white_flower", this.a((ItemProvider)Blocks.bA)).offerTo(consumer, "white_dye_from_lily_of_the_valley");
        ShapedRecipeJsonFactory.create(Blocks.cR, 8).input('#', Blocks.ao).input('X', Items.lA).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fN, 16).input('#', Blocks.cR).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fN, 8).input('#', Blocks.dB).input('$', Items.lA).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_white_dye", this.a(Items.lA)).offerTo(consumer, "white_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fx, 8).input('#', Blocks.gJ).input('X', Items.lA).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jq).input('#', Items.jz).input('X', ItemTags.b).pattern("XX").pattern("X#").pattern(" #").criterion("has_stick", this.a(Items.jz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.cd, 3).input('#', Blocks.n).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", this.a((ItemProvider)Blocks.n)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jJ).input('#', Items.jz).input('X', ItemTags.b).pattern("XX").pattern(" #").pattern(" #").criterion("has_stick", this.a(Items.jz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jp).input('#', Items.jz).input('X', ItemTags.b).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_stick", this.a(Items.jz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jo).input('#', Items.jz).input('X', ItemTags.b).pattern("X").pattern("#").pattern("#").criterion("has_stick", this.a(Items.jz)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.jn).input('#', Items.jz).input('X', ItemTags.b).pattern("X").pattern("X").pattern("#").criterion("has_stick", this.a(Items.jz)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.nD).input(Items.kS).input(Items.lh).input(Items.jH).criterion("has_book", this.a(Items.kS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.oz).input('#', Blocks.bb).input('|', Items.jz).pattern("###").pattern("###").pattern(" | ").group("banner").criterion("has_yellow_wool", this.a((ItemProvider)Blocks.bb)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.YELLOW_BED).input('#', Blocks.bb).input('X', ItemTags.b).pattern("###").pattern("XXX").group("bed").criterion("has_yellow_wool", this.a((ItemProvider)Blocks.bb)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.YELLOW_BED).input(Items.WHITE_BED).input(Items.ls).group("dyed_bed").criterion("has_bed", this.a(Items.WHITE_BED)).offerTo(consumer, "yellow_bed_from_white_bed");
        ShapedRecipeJsonFactory.create(Blocks.gx, 3).input('#', Blocks.bb).pattern("##").group("carpet").criterion("has_yellow_wool", this.a((ItemProvider)Blocks.bb)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.gx, 8).input('#', Blocks.gt).input('$', Items.ls).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", this.a((ItemProvider)Blocks.gt)).criterion("has_yellow_dye", this.a(Items.ls)).offerTo(consumer, "yellow_carpet_from_white_carpet");
        ShapelessRecipeJsonFactory.create(Blocks.jI, 8).input(Items.ls).input(Blocks.C, 4).input(Blocks.E, 4).group("concrete_powder").criterion("has_sand", this.a((ItemProvider)Blocks.C)).criterion("has_gravel", this.a((ItemProvider)Blocks.E)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ls).input(Blocks.bo).group("yellow_dye").criterion("has_yellow_flower", this.a((ItemProvider)Blocks.bo)).offerTo(consumer, "yellow_dye_from_dandelion");
        ShapelessRecipeJsonFactory.create(Items.ls, 2).input(Blocks.gM).group("yellow_dye").criterion("has_double_plant", this.a((ItemProvider)Blocks.gM)).offerTo(consumer, "yellow_dye_from_sunflower");
        ShapedRecipeJsonFactory.create(Blocks.cV, 8).input('#', Blocks.ao).input('X', Items.ls).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fR, 16).input('#', Blocks.cV).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", this.a((ItemProvider)Blocks.ao)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.fR, 8).input('#', Blocks.dB).input('$', Items.ls).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", this.a((ItemProvider)Blocks.dB)).criterion("has_yellow_dye", this.a(Items.ls)).offerTo(consumer, "yellow_stained_glass_pane_from_glass_pane");
        ShapedRecipeJsonFactory.create(Blocks.fB, 8).input('#', Blocks.gJ).input('X', Items.ls).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", this.a((ItemProvider)Blocks.gJ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.bb).input(Items.ls).input(Blocks.aX).group("wool").criterion("has_white_wool", this.a((ItemProvider)Blocks.aX)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.lY, 9).input(Blocks.jW).criterion("has_at_least_9_dried_kelp", this.a(NumberRange.IntRange.atLeast(9), Items.lY)).criterion("has_dried_kelp_block", this.a((ItemProvider)Blocks.jW)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.jW).input(Items.lY, 9).criterion("has_at_least_9_dried_kelp", this.a(NumberRange.IntRange.atLeast(9), Items.lY)).criterion("has_dried_kelp_block", this.a((ItemProvider)Blocks.jW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.kO).input('#', Items.pw).input('X', Items.px).pattern("###").pattern("#X#").pattern("###").criterion("has_nautilus_core", this.a(Items.px)).criterion("has_nautilus_shell", this.a(Items.pw)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.kV, 4).input('#', Blocks.d).pattern("#  ").pattern("## ").pattern("###").criterion("has_polished_granite", this.a((ItemProvider)Blocks.d)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.kW, 4).input('#', Blocks.hY).pattern("#  ").pattern("## ").pattern("###").criterion("has_smooth_red_sandstone", this.a((ItemProvider)Blocks.hY)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.kX, 4).input('#', Blocks.do_).pattern("#  ").pattern("## ").pattern("###").criterion("has_mossy_stone_bricks", this.a((ItemProvider)Blocks.do_)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.kY, 4).input('#', Blocks.f).pattern("#  ").pattern("## ").pattern("###").criterion("has_polished_diorite", this.a((ItemProvider)Blocks.f)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.kZ, 4).input('#', Blocks.bI).pattern("#  ").pattern("## ").pattern("###").criterion("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.la, 4).input('#', Blocks.iu).pattern("#  ").pattern("## ").pattern("###").criterion("has_end_stone_bricks", this.a((ItemProvider)Blocks.iu)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lb, 4).input('#', Blocks.b).pattern("#  ").pattern("## ").pattern("###").criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lc, 4).input('#', Blocks.hW).pattern("#  ").pattern("## ").pattern("###").criterion("has_smooth_sandstone", this.a((ItemProvider)Blocks.hW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ld, 4).input('#', Blocks.hX).pattern("#  ").pattern("## ").pattern("###").criterion("has_smooth_quartz", this.a((ItemProvider)Blocks.hX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.le, 4).input('#', Blocks.c).pattern("#  ").pattern("## ").pattern("###").criterion("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lf, 4).input('#', Blocks.g).pattern("#  ").pattern("## ").pattern("###").criterion("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lg, 4).input('#', Blocks.iD).pattern("#  ").pattern("## ").pattern("###").criterion("has_red_nether_bricks", this.a((ItemProvider)Blocks.iD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lh, 4).input('#', Blocks.h).pattern("#  ").pattern("## ").pattern("###").criterion("has_polished_andesite", this.a((ItemProvider)Blocks.h)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.li, 4).input('#', Blocks.e).pattern("#  ").pattern("## ").pattern("###").criterion("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lj, 6).input('#', Blocks.d).pattern("###").criterion("has_polished_granite", this.a((ItemProvider)Blocks.d)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lk, 6).input('#', Blocks.hY).pattern("###").criterion("has_smooth_red_sandstone", this.a((ItemProvider)Blocks.hY)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ll, 6).input('#', Blocks.do_).pattern("###").criterion("has_mossy_stone_bricks", this.a((ItemProvider)Blocks.do_)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lm, 6).input('#', Blocks.f).pattern("###").criterion("has_polished_diorite", this.a((ItemProvider)Blocks.f)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ln, 6).input('#', Blocks.bI).pattern("###").criterion("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lo, 6).input('#', Blocks.iu).pattern("###").criterion("has_end_stone_bricks", this.a((ItemProvider)Blocks.iu)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lp, 6).input('#', Blocks.hW).pattern("###").criterion("has_smooth_sandstone", this.a((ItemProvider)Blocks.hW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lq, 6).input('#', Blocks.hX).pattern("###").criterion("has_smooth_quartz", this.a((ItemProvider)Blocks.hX)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lr, 6).input('#', Blocks.c).pattern("###").criterion("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ls, 6).input('#', Blocks.g).pattern("###").criterion("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lt, 6).input('#', Blocks.iD).pattern("###").criterion("has_red_nether_bricks", this.a((ItemProvider)Blocks.iD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lu, 6).input('#', Blocks.h).pattern("###").criterion("has_polished_andesite", this.a((ItemProvider)Blocks.h)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lv, 6).input('#', Blocks.e).pattern("###").criterion("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lw, 6).input('#', Blocks.bF).pattern("###").pattern("###").criterion("has_bricks", this.a((ItemProvider)Blocks.bF)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lx, 6).input('#', Blocks.gi).pattern("###").pattern("###").criterion("has_prismarine", this.a((ItemProvider)Blocks.gi)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ly, 6).input('#', Blocks.hy).pattern("###").pattern("###").criterion("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lz, 6).input('#', Blocks.do_).pattern("###").pattern("###").criterion("has_mossy_stone_bricks", this.a((ItemProvider)Blocks.do_)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lA, 6).input('#', Blocks.c).pattern("###").pattern("###").criterion("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lB, 6).input('#', Blocks.dn).pattern("###").pattern("###").criterion("has_stone_bricks", this.a((ItemProvider)Blocks.dn)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lC, 6).input('#', Blocks.dN).pattern("###").pattern("###").criterion("has_nether_bricks", this.a((ItemProvider)Blocks.dN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lD, 6).input('#', Blocks.g).pattern("###").pattern("###").criterion("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lE, 6).input('#', Blocks.iD).pattern("###").pattern("###").criterion("has_red_nether_bricks", this.a((ItemProvider)Blocks.iD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lF, 6).input('#', Blocks.as).pattern("###").pattern("###").criterion("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lG, 6).input('#', Blocks.iu).pattern("###").pattern("###").criterion("has_end_stone_bricks", this.a((ItemProvider)Blocks.iu)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lH, 6).input('#', Blocks.e).pattern("###").pattern("###").criterion("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.pC).input(Items.kR).input(Items.CREEPER_HEAD).criterion("has_creeper_head", this.a(Items.CREEPER_HEAD)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.pD).input(Items.kR).input(Items.WITHER_SKELETON_SKULL).criterion("has_wither_skeleton_skull", this.a(Items.WITHER_SKELETON_SKULL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.pB).input(Items.kR).input(Blocks.bx).criterion("has_oxeye_daisy", this.a((ItemProvider)Blocks.bx)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.pE).input(Items.kR).input(Items.kq).criterion("has_enchanted_golden_apple", this.a(Items.kq)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lI, 6).input('~', Items.jG).input('I', Blocks.kQ).pattern("I~I").pattern("I I").pattern("I I").criterion("has_bamboo", this.a((ItemProvider)Blocks.kQ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lP).input('I', Items.jz).input('-', Blocks.hI).input('#', ItemTags.b).pattern("I-I").pattern("# #").criterion("has_stone_slab", this.a((ItemProvider)Blocks.hI)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lM).input('#', Blocks.hV).input('X', Blocks.bW).input('I', Items.jk).pattern("III").pattern("IXI").pattern("###").criterion("has_smooth_stone", this.a((ItemProvider)Blocks.hV)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lL).input('#', ItemTags.o).input('X', Blocks.bW).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_furnace", this.a((ItemProvider)Blocks.bW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lN).input('#', ItemTags.b).input('@', Items.kR).pattern("@@").pattern("##").pattern("##").criterion("has_string", this.a(Items.jG)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lR).input('#', ItemTags.b).input('@', Items.jk).pattern("@@").pattern("##").pattern("##").criterion("has_iron_ingot", this.a(Items.jk)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lO).input('#', ItemTags.b).input('@', Items.kl).pattern("@@").pattern("##").pattern("##").criterion("has_flint", this.a(Items.kl)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.lS).input('I', Items.jk).input('#', Blocks.b).pattern(" I ").pattern("###").criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        ComplexRecipeJsonFactory.create(RecipeSerializer.ARMOR_DYE).offerTo(consumer, "armor_dye");
        ComplexRecipeJsonFactory.create(RecipeSerializer.BANNER_DUPLICATE).offerTo(consumer, "banner_duplicate");
        ComplexRecipeJsonFactory.create(RecipeSerializer.BOOK_CLONING).offerTo(consumer, "book_cloning");
        ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_ROCKET).offerTo(consumer, "firework_rocket");
        ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_STAR).offerTo(consumer, "firework_star");
        ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_STAR_FADE).offerTo(consumer, "firework_star_fade");
        ComplexRecipeJsonFactory.create(RecipeSerializer.MAP_CLONING).offerTo(consumer, "map_cloning");
        ComplexRecipeJsonFactory.create(RecipeSerializer.MAP_EXTENDING).offerTo(consumer, "map_extending");
        ComplexRecipeJsonFactory.create(RecipeSerializer.SHIELD_DECORATION).offerTo(consumer, "shield_decoration");
        ComplexRecipeJsonFactory.create(RecipeSerializer.SHULKER_BOX).offerTo(consumer, "shulker_box_coloring");
        ComplexRecipeJsonFactory.create(RecipeSerializer.TIPPED_ARROW).offerTo(consumer, "tipped_arrow");
        ComplexRecipeJsonFactory.create(RecipeSerializer.SUSPICIOUS_STEW).offerTo(consumer, "suspicious_stew");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.nJ), Items.nK, 0.35f, 200).criterion("has_potato", this.a(Items.nJ)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.kM), Items.kL, 0.3f, 200).criterion("has_clay_ball", this.a(Items.kM)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.o), Items.ji, 0.15f, 200).criterion("has_log", this.a(ItemTags.o)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.oM), Items.oN, 0.1f, 200).criterion("has_chorus_fruit", this.a(Items.oM)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.H.getItem()), Items.jh, 0.1f, 200).criterion("has_coal_ore", this.a((ItemProvider)Blocks.H)).offerTo(consumer, "coal_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.mb), Items.mc, 0.35f, 200).criterion("has_beef", this.a(Items.mb)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.md), Items.me, 0.35f, 200).criterion("has_chicken", this.a(Items.md)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.lb), Items.lf, 0.35f, 200).criterion("has_cod", this.a(Items.lb)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.jU), Items.lY, 0.1f, 200).criterion("has_kelp", this.a((ItemProvider)Blocks.jU)).offerTo(consumer, "dried_kelp_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.lc), Items.lg, 0.35f, 200).criterion("has_salmon", this.a(Items.lc)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.ot), Items.ou, 0.35f, 200).criterion("has_mutton", this.a(Items.ot)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.km), Items.kn, 0.35f, 200).criterion("has_porkchop", this.a(Items.km)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.og), Items.oh, 0.35f, 200).criterion("has_rabbit", this.a(Items.og)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.bR.getItem()), Items.jj, 1.0f, 200).criterion("has_diamond_ore", this.a((ItemProvider)Blocks.bR)).offerTo(consumer, "diamond_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.ap.getItem()), Items.ll, 0.2f, 200).criterion("has_lapis_ore", this.a((ItemProvider)Blocks.ap)).offerTo(consumer, "lapis_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.eb.getItem()), Items.nF, 1.0f, 200).criterion("has_emerald_ore", this.a((ItemProvider)Blocks.eb)).offerTo(consumer, "emerald_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.w), Blocks.ao.getItem(), 0.1f, 200).criterion("has_sand", this.a(ItemTags.w)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.F.getItem()), Items.jl, 1.0f, 200).criterion("has_gold_ore", this.a((ItemProvider)Blocks.F)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.kM.getItem()), Items.lr, 0.1f, 200).criterion("has_sea_pickle", this.a((ItemProvider)Blocks.kM)).offerTo(consumer, "lime_dye_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.cD.getItem()), Items.lj, 1.0f, 200).criterion("has_cactus", this.a((ItemProvider)Blocks.cD)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.jE, Items.jD, Items.jF, Items.jN, Items.jC, Items.kh, Items.ki, Items.kj, Items.kk, Items.on), Items.mj, 0.1f, 200).criterion("has_golden_pickaxe", this.a(Items.jE)).criterion("has_golden_shovel", this.a(Items.jD)).criterion("has_golden_axe", this.a(Items.jF)).criterion("has_golden_hoe", this.a(Items.jN)).criterion("has_golden_sword", this.a(Items.jC)).criterion("has_golden_helmet", this.a(Items.kh)).criterion("has_golden_chestplate", this.a(Items.ki)).criterion("has_golden_leggings", this.a(Items.kj)).criterion("has_golden_boots", this.a(Items.kk)).criterion("has_golden_horse_armor", this.a(Items.on)).offerTo(consumer, "gold_nugget_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.jb, Items.ja, Items.jc, Items.jL, Items.jm, Items.jZ, Items.ka, Items.kb, Items.kc, Items.om, Items.jV, Items.jW, Items.jX, Items.jY), Items.pf, 0.1f, 200).criterion("has_iron_pickaxe", this.a(Items.jb)).criterion("has_iron_shovel", this.a(Items.ja)).criterion("has_iron_axe", this.a(Items.jc)).criterion("has_iron_hoe", this.a(Items.jL)).criterion("has_iron_sword", this.a(Items.jm)).criterion("has_iron_helmet", this.a(Items.jZ)).criterion("has_iron_chestplate", this.a(Items.ka)).criterion("has_iron_leggings", this.a(Items.kb)).criterion("has_iron_boots", this.a(Items.kc)).criterion("has_iron_horse_armor", this.a(Items.om)).criterion("has_chainmail_helmet", this.a(Items.jV)).criterion("has_chainmail_chestplate", this.a(Items.jW)).criterion("has_chainmail_leggings", this.a(Items.jX)).criterion("has_chainmail_boots", this.a(Items.jY)).offerTo(consumer, "iron_nugget_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.G.getItem()), Items.jk, 0.7f, 200).criterion("has_iron_ore", this.a(Blocks.G.getItem())).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.cE), Blocks.gJ.getItem(), 0.35f, 200).criterion("has_clay_block", this.a((ItemProvider)Blocks.cE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.cJ), Items.oa, 0.1f, 200).criterion("has_netherrack", this.a((ItemProvider)Blocks.cJ)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fp), Items.ob, 0.2f, 200).criterion("has_nether_quartz_ore", this.a((ItemProvider)Blocks.fp)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.cw), Items.kC, 0.7f, 200).criterion("has_redstone_ore", this.a((ItemProvider)Blocks.cw)).offerTo(consumer, "redstone_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.an), Blocks.am.getItem(), 0.15f, 200).criterion("has_wet_sponge", this.a((ItemProvider)Blocks.an)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.m), Blocks.b.getItem(), 0.1f, 200).criterion("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.b), Blocks.hV.getItem(), 0.1f, 200).criterion("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.as), Blocks.hW.getItem(), 0.1f, 200).criterion("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.hy), Blocks.hY.getItem(), 0.1f, 200).criterion("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fr), Blocks.hX.getItem(), 0.1f, 200).criterion("has_quartz_block", this.a((ItemProvider)Blocks.fr)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.dn), Blocks.dp.getItem(), 0.1f, 200).criterion("has_stone_bricks", this.a((ItemProvider)Blocks.dn)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fM), Blocks.jn.getItem(), 0.1f, 200).criterion("has_black_terracotta", this.a((ItemProvider)Blocks.fM)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fI), Blocks.jj.getItem(), 0.1f, 200).criterion("has_blue_terracotta", this.a((ItemProvider)Blocks.fI)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fJ), Blocks.jk.getItem(), 0.1f, 200).criterion("has_brown_terracotta", this.a((ItemProvider)Blocks.fJ)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fG), Blocks.jh.getItem(), 0.1f, 200).criterion("has_cyan_terracotta", this.a((ItemProvider)Blocks.fG)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fE), Blocks.jf.getItem(), 0.1f, 200).criterion("has_gray_terracotta", this.a((ItemProvider)Blocks.fE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fK), Blocks.jl.getItem(), 0.1f, 200).criterion("has_green_terracotta", this.a((ItemProvider)Blocks.fK)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fA), Blocks.jb.getItem(), 0.1f, 200).criterion("has_light_blue_terracotta", this.a((ItemProvider)Blocks.fA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fF), Blocks.jg.getItem(), 0.1f, 200).criterion("has_light_gray_terracotta", this.a((ItemProvider)Blocks.fF)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fC), Blocks.jd.getItem(), 0.1f, 200).criterion("has_lime_terracotta", this.a((ItemProvider)Blocks.fC)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fz), Blocks.ja.getItem(), 0.1f, 200).criterion("has_magenta_terracotta", this.a((ItemProvider)Blocks.fz)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fy), Blocks.iZ.getItem(), 0.1f, 200).criterion("has_orange_terracotta", this.a((ItemProvider)Blocks.fy)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fD), Blocks.je.getItem(), 0.1f, 200).criterion("has_pink_terracotta", this.a((ItemProvider)Blocks.fD)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fH), Blocks.ji.getItem(), 0.1f, 200).criterion("has_purple_terracotta", this.a((ItemProvider)Blocks.fH)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fL), Blocks.jm.getItem(), 0.1f, 200).criterion("has_red_terracotta", this.a((ItemProvider)Blocks.fL)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fx), Blocks.iY.getItem(), 0.1f, 200).criterion("has_white_terracotta", this.a((ItemProvider)Blocks.fx)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.fB), Blocks.jc.getItem(), 0.1f, 200).criterion("has_yellow_terracotta", this.a((ItemProvider)Blocks.fB)).offerTo(consumer);
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.G.getItem()), Items.jk, 0.7f, 100).criterion("has_iron_ore", this.a(Blocks.G.getItem())).offerTo(consumer, "iron_ingot_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.F.getItem()), Items.jl, 1.0f, 100).criterion("has_gold_ore", this.a((ItemProvider)Blocks.F)).offerTo(consumer, "gold_ingot_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.bR.getItem()), Items.jj, 1.0f, 100).criterion("has_diamond_ore", this.a((ItemProvider)Blocks.bR)).offerTo(consumer, "diamond_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.ap.getItem()), Items.ll, 0.2f, 100).criterion("has_lapis_ore", this.a((ItemProvider)Blocks.ap)).offerTo(consumer, "lapis_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.cw), Items.kC, 0.7f, 100).criterion("has_redstone_ore", this.a((ItemProvider)Blocks.cw)).offerTo(consumer, "redstone_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.H.getItem()), Items.jh, 0.1f, 100).criterion("has_coal_ore", this.a((ItemProvider)Blocks.H)).offerTo(consumer, "coal_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.eb.getItem()), Items.nF, 1.0f, 100).criterion("has_emerald_ore", this.a((ItemProvider)Blocks.eb)).offerTo(consumer, "emerald_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.fp), Items.ob, 0.2f, 100).criterion("has_nether_quartz_ore", this.a((ItemProvider)Blocks.fp)).offerTo(consumer, "quartz_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Items.jE, Items.jD, Items.jF, Items.jN, Items.jC, Items.kh, Items.ki, Items.kj, Items.kk, Items.on), Items.mj, 0.1f, 100).criterion("has_golden_pickaxe", this.a(Items.jE)).criterion("has_golden_shovel", this.a(Items.jD)).criterion("has_golden_axe", this.a(Items.jF)).criterion("has_golden_hoe", this.a(Items.jN)).criterion("has_golden_sword", this.a(Items.jC)).criterion("has_golden_helmet", this.a(Items.kh)).criterion("has_golden_chestplate", this.a(Items.ki)).criterion("has_golden_leggings", this.a(Items.kj)).criterion("has_golden_boots", this.a(Items.kk)).criterion("has_golden_horse_armor", this.a(Items.on)).offerTo(consumer, "gold_nugget_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Items.jb, Items.ja, Items.jc, Items.jL, Items.jm, Items.jZ, Items.ka, Items.kb, Items.kc, Items.om, Items.jV, Items.jW, Items.jX, Items.jY), Items.pf, 0.1f, 100).criterion("has_iron_pickaxe", this.a(Items.jb)).criterion("has_iron_shovel", this.a(Items.ja)).criterion("has_iron_axe", this.a(Items.jc)).criterion("has_iron_hoe", this.a(Items.jL)).criterion("has_iron_sword", this.a(Items.jm)).criterion("has_iron_helmet", this.a(Items.jZ)).criterion("has_iron_chestplate", this.a(Items.ka)).criterion("has_iron_leggings", this.a(Items.kb)).criterion("has_iron_boots", this.a(Items.kc)).criterion("has_iron_horse_armor", this.a(Items.om)).criterion("has_chainmail_helmet", this.a(Items.jV)).criterion("has_chainmail_chestplate", this.a(Items.jW)).criterion("has_chainmail_leggings", this.a(Items.jX)).criterion("has_chainmail_boots", this.a(Items.jY)).offerTo(consumer, "iron_nugget_from_blasting");
        this.a(consumer, "smoking", RecipeSerializer.SMOKING, 100);
        this.a(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.hI, 2).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "stone_slab_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.lb).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "stone_stairs_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.dn).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "stone_bricks_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.hP, 2).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "stone_brick_slab_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.dK).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "stone_brick_stairs_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.dq).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.b), Blocks.lB).create("has_stone", this.a((ItemProvider)Blocks.b)).offerTo(consumer, "stone_brick_walls_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.as), Blocks.au).create("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "cut_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.as), Blocks.hK, 2).create("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.as), Blocks.hL, 2).create("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.au), Blocks.hL, 2).create("has_cut_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.as), Blocks.ea).create("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "sandstone_stairs_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.as), Blocks.lF).create("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "sandstone_wall_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.as), Blocks.at).create("has_sandstone", this.a((ItemProvider)Blocks.as)).offerTo(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hy), Blocks.hA).create("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hy), Blocks.hS, 2).create("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hy), Blocks.hT, 2).create("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hA), Blocks.hT, 2).create("has_cut_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hy), Blocks.hB).create("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hy), Blocks.ly).create("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hy), Blocks.hz).create("has_red_sandstone", this.a((ItemProvider)Blocks.hy)).offerTo(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.fr), Blocks.hR, 2).create("has_quartz_block", this.a((ItemProvider)Blocks.fr)).offerTo(consumer, "quartz_slab_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.fr), Blocks.fu).create("has_quartz_block", this.a((ItemProvider)Blocks.fr)).offerTo(consumer, "quartz_stairs_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.fr), Blocks.ft).create("has_quartz_block", this.a((ItemProvider)Blocks.fr)).offerTo(consumer, "quartz_pillar_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.fr), Blocks.fs).create("has_quartz_block", this.a((ItemProvider)Blocks.fr)).offerTo(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.m), Blocks.cg).create("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.m), Blocks.hN, 2).create("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.m), Blocks.el).create("has_cobblestone", this.a((ItemProvider)Blocks.m)).offerTo(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dn), Blocks.hP, 2).create("has_stone_bricks", this.a((ItemProvider)Blocks.dn)).offerTo(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dn), Blocks.dK).create("has_stone_bricks", this.a((ItemProvider)Blocks.dn)).offerTo(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dn), Blocks.lB).create("has_stone_bricks", this.a((ItemProvider)Blocks.dn)).offerTo(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dn), Blocks.dq).create("has_stone_bricks", this.a((ItemProvider)Blocks.dn)).offerTo(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.bF), Blocks.hO, 2).create("has_bricks", this.a((ItemProvider)Blocks.bF)).offerTo(consumer, "brick_slab_from_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.bF), Blocks.dJ).create("has_bricks", this.a((ItemProvider)Blocks.bF)).offerTo(consumer, "brick_stairs_from_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.bF), Blocks.lw).create("has_bricks", this.a((ItemProvider)Blocks.bF)).offerTo(consumer, "brick_wall_from_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dN), Blocks.hQ, 2).create("has_nether_bricks", this.a((ItemProvider)Blocks.dN)).offerTo(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dN), Blocks.dP).create("has_nether_bricks", this.a((ItemProvider)Blocks.dN)).offerTo(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dN), Blocks.lC).create("has_nether_bricks", this.a((ItemProvider)Blocks.dN)).offerTo(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.iD), Blocks.lt, 2).create("has_nether_bricks", this.a((ItemProvider)Blocks.iD)).offerTo(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.iD), Blocks.lg).create("has_nether_bricks", this.a((ItemProvider)Blocks.iD)).offerTo(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.iD), Blocks.lE).create("has_nether_bricks", this.a((ItemProvider)Blocks.iD)).offerTo(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ir), Blocks.hU, 2).create("has_purpur_block", this.a((ItemProvider)Blocks.ir)).offerTo(consumer, "purpur_slab_from_purpur_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ir), Blocks.it).create("has_purpur_block", this.a((ItemProvider)Blocks.ir)).offerTo(consumer, "purpur_stairs_from_purpur_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ir), Blocks.is).create("has_purpur_block", this.a((ItemProvider)Blocks.ir)).offerTo(consumer, "purpur_pillar_from_purpur_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gi), Blocks.go, 2).create("has_prismarine", this.a((ItemProvider)Blocks.gi)).offerTo(consumer, "prismarine_slab_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gi), Blocks.gl).create("has_prismarine", this.a((ItemProvider)Blocks.gi)).offerTo(consumer, "prismarine_stairs_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gi), Blocks.lx).create("has_prismarine", this.a((ItemProvider)Blocks.gi)).offerTo(consumer, "prismarine_wall_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gj), Blocks.gp, 2).create("has_prismarine_brick", this.a((ItemProvider)Blocks.gj)).offerTo(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gj), Blocks.gm).create("has_prismarine_brick", this.a((ItemProvider)Blocks.gj)).offerTo(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gk), Blocks.gq, 2).create("has_dark_prismarine", this.a((ItemProvider)Blocks.gk)).offerTo(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.gk), Blocks.gn).create("has_dark_prismarine", this.a((ItemProvider)Blocks.gk)).offerTo(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.g), Blocks.ls, 2).create("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer, "andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.g), Blocks.lf).create("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer, "andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.g), Blocks.lD).create("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer, "andesite_wall_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.g), Blocks.h).create("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer, "polished_andesite_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.g), Blocks.lu, 2).create("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer, "polished_andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.g), Blocks.lh).create("has_andesite", this.a((ItemProvider)Blocks.g)).offerTo(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.h), Blocks.lu, 2).create("has_polished_andesite", this.a((ItemProvider)Blocks.h)).offerTo(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.h), Blocks.lh).create("has_polished_andesite", this.a((ItemProvider)Blocks.h)).offerTo(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.c), Blocks.lr, 2).create("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer, "granite_slab_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.c), Blocks.le).create("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer, "granite_stairs_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.c), Blocks.lA).create("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer, "granite_wall_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.c), Blocks.d).create("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer, "polished_granite_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.c), Blocks.lj, 2).create("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer, "polished_granite_slab_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.c), Blocks.kV).create("has_granite", this.a((ItemProvider)Blocks.c)).offerTo(consumer, "polished_granite_stairs_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.d), Blocks.lj, 2).create("has_polished_granite", this.a((ItemProvider)Blocks.d)).offerTo(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.d), Blocks.kV).create("has_polished_granite", this.a((ItemProvider)Blocks.d)).offerTo(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.e), Blocks.lv, 2).create("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer, "diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.e), Blocks.li).create("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer, "diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.e), Blocks.lH).create("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer, "diorite_wall_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.e), Blocks.f).create("has_diorite", this.a((ItemProvider)Blocks.e)).offerTo(consumer, "polished_diorite_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.e), Blocks.lm, 2).create("has_diorite", this.a((ItemProvider)Blocks.f)).offerTo(consumer, "polished_diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.e), Blocks.kY).create("has_diorite", this.a((ItemProvider)Blocks.f)).offerTo(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.f), Blocks.lm, 2).create("has_polished_diorite", this.a((ItemProvider)Blocks.f)).offerTo(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.f), Blocks.kY).create("has_polished_diorite", this.a((ItemProvider)Blocks.f)).offerTo(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.do_), Blocks.ll, 2).create("has_mossy_stone_bricks", this.a((ItemProvider)Blocks.do_)).offerTo(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.do_), Blocks.kX).create("has_mossy_stone_bricks", this.a((ItemProvider)Blocks.do_)).offerTo(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.do_), Blocks.lz).create("has_mossy_stone_bricks", this.a((ItemProvider)Blocks.do_)).offerTo(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.bI), Blocks.ln, 2).create("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.bI), Blocks.kZ).create("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.bI), Blocks.em).create("has_mossy_cobblestone", this.a((ItemProvider)Blocks.bI)).offerTo(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hW), Blocks.lp, 2).create("has_smooth_sandstone", this.a((ItemProvider)Blocks.hW)).offerTo(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hW), Blocks.lc).create("has_mossy_cobblestone", this.a((ItemProvider)Blocks.hW)).offerTo(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hY), Blocks.lk, 2).create("has_smooth_red_sandstone", this.a((ItemProvider)Blocks.hY)).offerTo(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hY), Blocks.kW).create("has_smooth_red_sandstone", this.a((ItemProvider)Blocks.hY)).offerTo(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hX), Blocks.lq, 2).create("has_smooth_quartz", this.a((ItemProvider)Blocks.hX)).offerTo(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hX), Blocks.ld).create("has_smooth_quartz", this.a((ItemProvider)Blocks.hX)).offerTo(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.iu), Blocks.lo, 2).create("has_end_stone_brick", this.a((ItemProvider)Blocks.iu)).offerTo(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.iu), Blocks.la).create("has_end_stone_brick", this.a((ItemProvider)Blocks.iu)).offerTo(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.iu), Blocks.lG).create("has_end_stone_brick", this.a((ItemProvider)Blocks.iu)).offerTo(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dW), Blocks.iu).create("has_end_stone", this.a((ItemProvider)Blocks.dW)).offerTo(consumer, "end_stone_bricks_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dW), Blocks.lo, 2).create("has_end_stone", this.a((ItemProvider)Blocks.dW)).offerTo(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dW), Blocks.la).create("has_end_stone", this.a((ItemProvider)Blocks.dW)).offerTo(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.dW), Blocks.lG).create("has_end_stone", this.a((ItemProvider)Blocks.dW)).offerTo(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.hV), Blocks.hJ, 2).create("has_smooth_stone", this.a((ItemProvider)Blocks.hV)).offerTo(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
    }
    
    private void a(final Consumer<RecipeJsonProvider> consumer, final String string, final CookingRecipeSerializer<?> cookingRecipeSerializer, final int integer) {
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.mb), Items.mc, 0.35f, integer, cookingRecipeSerializer).criterion("has_beef", this.a(Items.mb)).offerTo(consumer, "cooked_beef_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.md), Items.me, 0.35f, integer, cookingRecipeSerializer).criterion("has_chicken", this.a(Items.md)).offerTo(consumer, "cooked_chicken_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.lb), Items.lf, 0.35f, integer, cookingRecipeSerializer).criterion("has_cod", this.a(Items.lb)).offerTo(consumer, "cooked_cod_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Blocks.jU), Items.lY, 0.1f, integer, cookingRecipeSerializer).criterion("has_kelp", this.a((ItemProvider)Blocks.jU)).offerTo(consumer, "dried_kelp_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.lc), Items.lg, 0.35f, integer, cookingRecipeSerializer).criterion("has_salmon", this.a(Items.lc)).offerTo(consumer, "cooked_salmon_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.ot), Items.ou, 0.35f, integer, cookingRecipeSerializer).criterion("has_mutton", this.a(Items.ot)).offerTo(consumer, "cooked_mutton_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.km), Items.kn, 0.35f, integer, cookingRecipeSerializer).criterion("has_porkchop", this.a(Items.km)).offerTo(consumer, "cooked_porkchop_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.nJ), Items.nK, 0.35f, integer, cookingRecipeSerializer).criterion("has_potato", this.a(Items.nJ)).offerTo(consumer, "baked_potato_from_" + string);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.og), Items.oh, 0.35f, integer, cookingRecipeSerializer).criterion("has_rabbit", this.a(Items.og)).offerTo(consumer, "cooked_rabbit_from_" + string);
    }
    
    private EnterBlockCriterion.Conditions a(final Block block) {
        return new EnterBlockCriterion.Conditions(block, null);
    }
    
    private InventoryChangedCriterion.Conditions a(final NumberRange.IntRange intRange, final ItemProvider itemProvider) {
        return this.a(ItemPredicate.Builder.create().item(itemProvider).count(intRange).build());
    }
    
    private InventoryChangedCriterion.Conditions a(final ItemProvider itemProvider) {
        return this.a(ItemPredicate.Builder.create().item(itemProvider).build());
    }
    
    private InventoryChangedCriterion.Conditions a(final Tag<Item> tag) {
        return this.a(ItemPredicate.Builder.create().tag(tag).build());
    }
    
    private InventoryChangedCriterion.Conditions a(final ItemPredicate... arr) {
        return new InventoryChangedCriterion.Conditions(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, arr);
    }
    
    @Override
    public String getName() {
        return "Recipes";
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}
