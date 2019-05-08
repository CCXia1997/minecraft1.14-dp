package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.util.Identifier;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.timer.TimerCallback;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.tag.Tag;
import net.minecraft.server.function.CommandFunction;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.arguments.TimeArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.FunctionArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ScheduleCommand
{
    private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("schedule").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("function").then(CommandManager.argument("function", (com.mojang.brigadier.arguments.ArgumentType<Object>)FunctionArgumentType.create()).suggests((SuggestionProvider)FunctionCommand.SUGGESTION_PROVIDER).then(CommandManager.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)TimeArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger(commandContext, "time")))))));
    }
    
    private static int execute(final ServerCommandSource source, final Either<CommandFunction, Tag<CommandFunction>> function, final int time) throws CommandSyntaxException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifne            11
        //     4: getstatic       net/minecraft/server/command/ScheduleCommand.SAME_TICK_EXCEPTION:Lcom/mojang/brigadier/exceptions/SimpleCommandExceptionType;
        //     7: invokevirtual   com/mojang/brigadier/exceptions/SimpleCommandExceptionType.create:()Lcom/mojang/brigadier/exceptions/CommandSyntaxException;
        //    10: athrow         
        //    11: aload_0         /* source */
        //    12: invokevirtual   net/minecraft/server/command/ServerCommandSource.getWorld:()Lnet/minecraft/server/world/ServerWorld;
        //    15: invokevirtual   net/minecraft/server/world/ServerWorld.getTime:()J
        //    18: iload_2         /* time */
        //    19: i2l            
        //    20: ladd           
        //    21: lstore_3        /* long4 */
        //    22: aload_1         /* function */
        //    23: aload_0         /* source */
        //    24: lload_3         /* long4 */
        //    25: iload_2         /* time */
        //    26: invokedynamic   BootstrapMethod #2, accept:(Lnet/minecraft/server/command/ServerCommandSource;JI)Ljava/util/function/Consumer;
        //    31: invokevirtual   com/mojang/datafixers/util/Either.ifLeft:(Ljava/util/function/Consumer;)Lcom/mojang/datafixers/util/Either;
        //    34: aload_0         /* source */
        //    35: lload_3         /* long4 */
        //    36: iload_2         /* time */
        //    37: invokedynamic   BootstrapMethod #3, accept:(Lnet/minecraft/server/command/ServerCommandSource;JI)Ljava/util/function/Consumer;
        //    42: invokevirtual   com/mojang/datafixers/util/Either.ifRight:(Ljava/util/function/Consumer;)Lcom/mojang/datafixers/util/Either;
        //    45: pop            
        //    46: lload_3         /* long4 */
        //    47: ldc2_w          2147483647
        //    50: invokestatic    java/lang/Math.floorMod:(JJ)J
        //    53: l2i            
        //    54: ireturn        
        //    Exceptions:
        //  throws com.mojang.brigadier.exceptions.CommandSyntaxException
        //    Signature:
        //  (Lnet/minecraft/server/command/ServerCommandSource;Lcom/mojang/datafixers/util/Either<Lnet/minecraft/server/function/CommandFunction;Lnet/minecraft/tag/Tag<Lnet/minecraft/server/function/CommandFunction;>;>;I)I
        //    StackMapTable: 00 01 0B
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
    
    static {
        SAME_TICK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.schedule.same_tick", new Object[0]));
    }
}
