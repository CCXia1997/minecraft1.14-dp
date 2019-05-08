package net.minecraft.server.world;

import org.apache.logging.log4j.LogManager;
import com.mojang.datafixers.util.Either;
import java.util.stream.Stream;
import net.minecraft.util.SystemUtil;
import java.util.stream.Collector;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;
import java.util.function.IntConsumer;
import net.minecraft.world.chunk.ChunkPos;
import java.util.function.IntSupplier;
import com.google.common.collect.Sets;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;
import java.util.List;
import net.minecraft.util.Mailbox;
import net.minecraft.util.MailboxProcessor;
import java.util.Set;
import net.minecraft.util.Void;
import java.util.function.Function;
import net.minecraft.util.Actor;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem implements AutoCloseable, ChunkHolder.LevelUpdateListener
{
    private static final Logger LOGGER;
    private final Map<Actor<?>, LevelPrioritizedQueue<? extends Function<Actor<Void>, ?>>> queues;
    private final Set<Actor<?>> actors;
    private final MailboxProcessor<Mailbox.PrioritizedMessage> sorter;
    
    public ChunkTaskPrioritySystem(final List<Actor<?>> actors, final Executor executor, final int maxQueues) {
        final LevelPrioritizedQueue levelPrioritizedQueue;
        this.queues = actors.stream().collect(Collectors.toMap(Function.identity(), actor -> {
            new LevelPrioritizedQueue(actor.getName() + "_queue", maxQueues);
            return levelPrioritizedQueue;
        }));
        this.actors = Sets.newHashSet(actors);
        this.sorter = new MailboxProcessor<Mailbox.PrioritizedMessage>(new Mailbox.PrioritizedQueueMailbox(4), executor, "sorter");
    }
    
    public static RunnableMessage<Runnable> createRunnableMessage(final Runnable runnable, final long pos, final IntSupplier lastLevelUpdatedToProvider) {
        return new RunnableMessage<Runnable>(actor -> () -> {
            runnable.run();
            actor.send(Void.INSTANCE);
        }, pos, lastLevelUpdatedToProvider);
    }
    
    public static RunnableMessage<Runnable> createExecutorMessage(final ChunkHolder holder, final Runnable runnable) {
        return createRunnableMessage(runnable, holder.getPos().toLong(), holder::getCompletedLevel);
    }
    
    public static SorterMessage createSorterMessage(final Runnable runnable, final long long2, final boolean boolean4) {
        return new SorterMessage(runnable, long2, boolean4);
    }
    
    public <T> Actor<RunnableMessage<T>> createExecutingActor(final Actor<T> actor, final boolean boolean2) {
        return this.sorter.<Actor<RunnableMessage<T>>>createAndSendFutureActor(actor3 -> new Mailbox.PrioritizedMessage(0, () -> {
            this.<T>getQueue(actor);
            actor3.send(Actor.createConsumerActor("chunk priority sorter around " + actor.getName(), runnableMessage -> this.<T>execute(actor, runnableMessage.function, runnableMessage.pos, runnableMessage.lastLevelUpdatedToProvider, boolean2)));
        })).join();
    }
    
    public Actor<SorterMessage> createSortingActor(final Actor<Runnable> actor) {
        return this.sorter.<Actor<SorterMessage>>createAndSendFutureActor(actor2 -> new Mailbox.PrioritizedMessage(0, () -> actor2.send(Actor.createConsumerActor("chunk priority sorter around " + actor.getName(), sorterMessage -> this.<Runnable>sort(actor, sorterMessage.pos, sorterMessage.runnable, sorterMessage.c))))).join();
    }
    
    @Override
    public void updateLevel(final ChunkPos pos, final IntSupplier levelGetter, final int targetLevel, final IntConsumer levelSetter) {
        final int integer5;
        this.sorter.send(new Mailbox.PrioritizedMessage(0, () -> {
            integer5 = levelGetter.getAsInt();
            this.queues.values().forEach(levelPrioritizedQueue -> levelPrioritizedQueue.updateLevel(integer5, pos, targetLevel));
            levelSetter.accept(targetLevel);
        }));
    }
    
    private <T> void sort(final Actor<T> actor, final long long2, final Runnable runnable, final boolean boolean5) {
        final LevelPrioritizedQueue<Function<Actor<Void>, Object>> levelPrioritizedQueue6;
        this.sorter.send(new Mailbox.PrioritizedMessage(1, () -> {
            levelPrioritizedQueue6 = this.getQueue(actor);
            levelPrioritizedQueue6.clearPosition(long2, boolean5);
            if (this.actors.remove(actor)) {
                this.a(levelPrioritizedQueue6, (Actor<Object>)actor);
            }
            runnable.run();
        }));
    }
    
    private <T> void execute(final Actor<T> actor, final Function<Actor<Void>, T> function, final long long3, final IntSupplier lastLevelUpdatedToProvider, final boolean boolean6) {
        final LevelPrioritizedQueue<Function<Actor<Void>, Object>> levelPrioritizedQueue7;
        final int integer8;
        this.sorter.send(new Mailbox.PrioritizedMessage(2, () -> {
            levelPrioritizedQueue7 = this.getQueue(actor);
            integer8 = lastLevelUpdatedToProvider.getAsInt();
            levelPrioritizedQueue7.add(Optional.<Function<Actor<Void>, Object>>of((Function<Actor<Void>, Object>)function), long3, integer8);
            if (boolean6) {
                levelPrioritizedQueue7.add(Optional.<Function<Actor<Void>, Object>>empty(), long3, integer8);
            }
            if (this.actors.remove(actor)) {
                this.a(levelPrioritizedQueue7, (Actor<Object>)actor);
            }
        }));
    }
    
    private <T> void a(final LevelPrioritizedQueue<Function<Actor<Void>, T>> levelPrioritizedQueue, final Actor<T> actor) {
        final Stream<Either<Function<Actor<Void>, Object>, Runnable>> stream3;
        this.sorter.send(new Mailbox.PrioritizedMessage(3, () -> {
            stream3 = (Stream<Either<Function<Actor<Void>, Object>, Runnable>>)levelPrioritizedQueue.poll();
            if (stream3 == null) {
                this.actors.add(actor);
            }
            else {
                SystemUtil.thenCombine(stream3.map(either -> (CompletableFuture)either.map((Function)actor::createAndSendFutureActor, runnable -> {
                    runnable.run();
                    return CompletableFuture.<Void>completedFuture(Void.INSTANCE);
                })).collect(Collectors.toList())).thenAccept(list -> this.a((LevelPrioritizedQueue<Function<Actor<Void>, Object>>)levelPrioritizedQueue, (Actor<Object>)actor));
            }
        }));
    }
    
    private <T> LevelPrioritizedQueue<Function<Actor<Void>, T>> getQueue(final Actor<T> actor) {
        final LevelPrioritizedQueue<? extends Function<Actor<Void>, ?>> levelPrioritizedQueue2 = this.queues.get(actor);
        if (levelPrioritizedQueue2 == null) {
            throw new IllegalArgumentException("No queue for: " + actor);
        }
        return (LevelPrioritizedQueue<Function<Actor<Void>, T>>)levelPrioritizedQueue2;
    }
    
    @Override
    public void close() {
        this.queues.keySet().forEach(Actor::close);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static final class RunnableMessage<T>
    {
        private final Function<Actor<Void>, T> function;
        private final long pos;
        private final IntSupplier lastLevelUpdatedToProvider;
        
        private RunnableMessage(final Function<Actor<Void>, T> function, final long pos, final IntSupplier lastLevelUpdatedToProvider) {
            this.function = function;
            this.pos = pos;
            this.lastLevelUpdatedToProvider = lastLevelUpdatedToProvider;
        }
    }
    
    public static final class SorterMessage
    {
        private final Runnable runnable;
        private final long pos;
        private final boolean c;
        
        private SorterMessage(final Runnable runnable, final long long2, final boolean boolean4) {
            this.runnable = runnable;
            this.pos = long2;
            this.c = boolean4;
        }
    }
}
