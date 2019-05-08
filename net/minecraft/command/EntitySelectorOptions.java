package net.minecraft.command;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import java.util.function.BiConsumer;
import java.util.Arrays;
import net.minecraft.world.GameMode;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.entity.LivingEntity;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.Objects;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTags;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.NumberRange;
import net.minecraft.entity.Entity;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.advancement.PlayerAdvancementTracker;
import com.mojang.brigadier.StringReader;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.util.Identifier;
import com.google.common.collect.Maps;
import java.util.Iterator;
import com.mojang.brigadier.Message;
import java.util.Locale;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.text.TextComponent;
import java.util.function.Predicate;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Map;

public class EntitySelectorOptions
{
    private static final Map<String, SelectorOption> options;
    public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION;
    public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION;
    public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION;
    public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION;
    public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION;
    public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION;
    public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION;
    public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION;
    
    private static void putOption(final String name, final SelectorHandler handler, final Predicate<EntitySelectorReader> predicate, final TextComponent description) {
        EntitySelectorOptions.options.put(name, new SelectorOption(handler, (Predicate)predicate, description));
    }
    
    public static void register() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: invokeinterface java/util/Map.isEmpty:()Z
        //     8: ifne            12
        //    11: return         
        //    12: ldc             "name"
        //    14: invokedynamic   BootstrapMethod #0, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //    19: invokedynamic   BootstrapMethod #1, test:()Ljava/util/function/Predicate;
        //    24: new             Lnet/minecraft/text/TranslatableTextComponent;
        //    27: dup            
        //    28: ldc             "argument.entity.options.name.description"
        //    30: iconst_0       
        //    31: anewarray       Ljava/lang/Object;
        //    34: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    37: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //    40: ldc             "distance"
        //    42: invokedynamic   BootstrapMethod #2, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //    47: invokedynamic   BootstrapMethod #3, test:()Ljava/util/function/Predicate;
        //    52: new             Lnet/minecraft/text/TranslatableTextComponent;
        //    55: dup            
        //    56: ldc             "argument.entity.options.distance.description"
        //    58: iconst_0       
        //    59: anewarray       Ljava/lang/Object;
        //    62: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    65: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //    68: ldc             "level"
        //    70: invokedynamic   BootstrapMethod #4, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //    75: invokedynamic   BootstrapMethod #5, test:()Ljava/util/function/Predicate;
        //    80: new             Lnet/minecraft/text/TranslatableTextComponent;
        //    83: dup            
        //    84: ldc             "argument.entity.options.level.description"
        //    86: iconst_0       
        //    87: anewarray       Ljava/lang/Object;
        //    90: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    93: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //    96: ldc             "x"
        //    98: invokedynamic   BootstrapMethod #6, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   103: invokedynamic   BootstrapMethod #7, test:()Ljava/util/function/Predicate;
        //   108: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   111: dup            
        //   112: ldc             "argument.entity.options.x.description"
        //   114: iconst_0       
        //   115: anewarray       Ljava/lang/Object;
        //   118: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   121: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   124: ldc             "y"
        //   126: invokedynamic   BootstrapMethod #8, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   131: invokedynamic   BootstrapMethod #9, test:()Ljava/util/function/Predicate;
        //   136: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   139: dup            
        //   140: ldc             "argument.entity.options.y.description"
        //   142: iconst_0       
        //   143: anewarray       Ljava/lang/Object;
        //   146: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   149: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   152: ldc             "z"
        //   154: invokedynamic   BootstrapMethod #10, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   159: invokedynamic   BootstrapMethod #11, test:()Ljava/util/function/Predicate;
        //   164: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   167: dup            
        //   168: ldc             "argument.entity.options.z.description"
        //   170: iconst_0       
        //   171: anewarray       Ljava/lang/Object;
        //   174: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   177: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   180: ldc             "dx"
        //   182: invokedynamic   BootstrapMethod #12, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   187: invokedynamic   BootstrapMethod #13, test:()Ljava/util/function/Predicate;
        //   192: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   195: dup            
        //   196: ldc             "argument.entity.options.dx.description"
        //   198: iconst_0       
        //   199: anewarray       Ljava/lang/Object;
        //   202: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   205: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   208: ldc             "dy"
        //   210: invokedynamic   BootstrapMethod #14, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   215: invokedynamic   BootstrapMethod #15, test:()Ljava/util/function/Predicate;
        //   220: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   223: dup            
        //   224: ldc             "argument.entity.options.dy.description"
        //   226: iconst_0       
        //   227: anewarray       Ljava/lang/Object;
        //   230: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   233: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   236: ldc             "dz"
        //   238: invokedynamic   BootstrapMethod #16, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   243: invokedynamic   BootstrapMethod #17, test:()Ljava/util/function/Predicate;
        //   248: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   251: dup            
        //   252: ldc             "argument.entity.options.dz.description"
        //   254: iconst_0       
        //   255: anewarray       Ljava/lang/Object;
        //   258: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   261: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   264: ldc             "x_rotation"
        //   266: invokedynamic   BootstrapMethod #18, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   271: invokedynamic   BootstrapMethod #19, test:()Ljava/util/function/Predicate;
        //   276: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   279: dup            
        //   280: ldc             "argument.entity.options.x_rotation.description"
        //   282: iconst_0       
        //   283: anewarray       Ljava/lang/Object;
        //   286: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   289: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   292: ldc             "y_rotation"
        //   294: invokedynamic   BootstrapMethod #20, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   299: invokedynamic   BootstrapMethod #21, test:()Ljava/util/function/Predicate;
        //   304: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   307: dup            
        //   308: ldc             "argument.entity.options.y_rotation.description"
        //   310: iconst_0       
        //   311: anewarray       Ljava/lang/Object;
        //   314: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   317: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   320: ldc             "limit"
        //   322: invokedynamic   BootstrapMethod #22, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   327: invokedynamic   BootstrapMethod #23, test:()Ljava/util/function/Predicate;
        //   332: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   335: dup            
        //   336: ldc_w           "argument.entity.options.limit.description"
        //   339: iconst_0       
        //   340: anewarray       Ljava/lang/Object;
        //   343: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   346: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   349: ldc_w           "sort"
        //   352: invokedynamic   BootstrapMethod #24, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   357: invokedynamic   BootstrapMethod #25, test:()Ljava/util/function/Predicate;
        //   362: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   365: dup            
        //   366: ldc_w           "argument.entity.options.sort.description"
        //   369: iconst_0       
        //   370: anewarray       Ljava/lang/Object;
        //   373: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   376: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   379: ldc_w           "gamemode"
        //   382: invokedynamic   BootstrapMethod #26, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   387: invokedynamic   BootstrapMethod #27, test:()Ljava/util/function/Predicate;
        //   392: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   395: dup            
        //   396: ldc_w           "argument.entity.options.gamemode.description"
        //   399: iconst_0       
        //   400: anewarray       Ljava/lang/Object;
        //   403: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   406: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   409: ldc_w           "team"
        //   412: invokedynamic   BootstrapMethod #28, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   417: invokedynamic   BootstrapMethod #29, test:()Ljava/util/function/Predicate;
        //   422: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   425: dup            
        //   426: ldc_w           "argument.entity.options.team.description"
        //   429: iconst_0       
        //   430: anewarray       Ljava/lang/Object;
        //   433: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   436: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   439: ldc_w           "type"
        //   442: invokedynamic   BootstrapMethod #30, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   447: invokedynamic   BootstrapMethod #31, test:()Ljava/util/function/Predicate;
        //   452: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   455: dup            
        //   456: ldc_w           "argument.entity.options.type.description"
        //   459: iconst_0       
        //   460: anewarray       Ljava/lang/Object;
        //   463: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   466: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   469: ldc_w           "tag"
        //   472: invokedynamic   BootstrapMethod #32, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   477: invokedynamic   BootstrapMethod #33, test:()Ljava/util/function/Predicate;
        //   482: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   485: dup            
        //   486: ldc_w           "argument.entity.options.tag.description"
        //   489: iconst_0       
        //   490: anewarray       Ljava/lang/Object;
        //   493: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   496: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   499: ldc_w           "nbt"
        //   502: invokedynamic   BootstrapMethod #34, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   507: invokedynamic   BootstrapMethod #35, test:()Ljava/util/function/Predicate;
        //   512: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   515: dup            
        //   516: ldc_w           "argument.entity.options.nbt.description"
        //   519: iconst_0       
        //   520: anewarray       Ljava/lang/Object;
        //   523: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   526: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   529: ldc_w           "scores"
        //   532: invokedynamic   BootstrapMethod #36, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   537: invokedynamic   BootstrapMethod #37, test:()Ljava/util/function/Predicate;
        //   542: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   545: dup            
        //   546: ldc_w           "argument.entity.options.scores.description"
        //   549: iconst_0       
        //   550: anewarray       Ljava/lang/Object;
        //   553: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   556: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   559: ldc_w           "advancements"
        //   562: invokedynamic   BootstrapMethod #38, handle:()Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;
        //   567: invokedynamic   BootstrapMethod #39, test:()Ljava/util/function/Predicate;
        //   572: new             Lnet/minecraft/text/TranslatableTextComponent;
        //   575: dup            
        //   576: ldc_w           "argument.entity.options.advancements.description"
        //   579: iconst_0       
        //   580: anewarray       Ljava/lang/Object;
        //   583: invokespecial   net/minecraft/text/TranslatableTextComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   586: invokestatic    net/minecraft/command/EntitySelectorOptions.putOption:(Ljava/lang/String;Lnet/minecraft/command/EntitySelectorOptions$SelectorHandler;Ljava/util/function/Predicate;Lnet/minecraft/text/TextComponent;)V
        //   589: return         
        //    StackMapTable: 00 01 0C
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
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
    
    public static SelectorHandler getHandler(final EntitySelectorReader reader, final String option, final int restoreCursor) throws CommandSyntaxException {
        final SelectorOption selectorOption4 = EntitySelectorOptions.options.get(option);
        if (selectorOption4 == null) {
            reader.getReader().setCursor(restoreCursor);
            throw EntitySelectorOptions.UNKNOWN_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)reader.getReader(), option);
        }
        if (selectorOption4.applicable.test(reader)) {
            return selectorOption4.handler;
        }
        throw EntitySelectorOptions.INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)reader.getReader(), option);
    }
    
    public static void suggestOptions(final EntitySelectorReader entitySelectorReader, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final Map.Entry<String, SelectorOption> entry5 : EntitySelectorOptions.options.entrySet()) {
            if (entry5.getValue().applicable.test(entitySelectorReader) && entry5.getKey().toLowerCase(Locale.ROOT).startsWith(string3)) {
                suggestionsBuilder.suggest(entry5.getKey() + '=', (Message)entry5.getValue().description);
            }
        }
    }
    
    static {
        options = Maps.newHashMap();
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.options.unknown", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.options.inapplicable", new Object[] { object });
            return translatableTextComponent2;
        });
        NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.options.distance.negative", new Object[0]));
        NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.options.level.negative", new Object[0]));
        TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.options.limit.toosmall", new Object[0]));
        final TranslatableTextComponent translatableTextComponent3;
        IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.options.sort.irreversible", new Object[] { object });
            return translatableTextComponent3;
        });
        final TranslatableTextComponent translatableTextComponent4;
        INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.options.mode.invalid", new Object[] { object });
            return translatableTextComponent4;
        });
        final TranslatableTextComponent translatableTextComponent5;
        INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.options.type.invalid", new Object[] { object });
            return translatableTextComponent5;
        });
    }
    
    static class SelectorOption
    {
        public final SelectorHandler handler;
        public final Predicate<EntitySelectorReader> applicable;
        public final TextComponent description;
        
        private SelectorOption(final SelectorHandler selectorHandler, final Predicate<EntitySelectorReader> predicate, final TextComponent textComponent) {
            this.handler = selectorHandler;
            this.applicable = predicate;
            this.description = textComponent;
        }
    }
    
    public interface SelectorHandler
    {
        void handle(final EntitySelectorReader arg1) throws CommandSyntaxException;
    }
}
