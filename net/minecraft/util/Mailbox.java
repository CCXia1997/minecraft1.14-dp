package net.minecraft.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.google.common.collect.Queues;
import java.util.stream.IntStream;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;

public interface Mailbox<T, F>
{
    @Nullable
    F poll();
    
    boolean add(final T arg1);
    
    boolean isEmpty();
    
    public static final class QueueMailbox<T> implements Mailbox<T, T>
    {
        private final Queue<T> queue;
        
        public QueueMailbox(final Queue<T> queue) {
            this.queue = queue;
        }
        
        @Nullable
        @Override
        public T poll() {
            return this.queue.poll();
        }
        
        @Override
        public boolean add(final T object) {
            return this.queue.add(object);
        }
        
        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
    }
    
    public static final class PrioritizedMessage implements Runnable
    {
        private final int priority;
        private final Runnable runnable;
        
        public PrioritizedMessage(final int priority, final Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }
        
        @Override
        public void run() {
            this.runnable.run();
        }
        
        public int getPriority() {
            return this.priority;
        }
    }
    
    public static final class PrioritizedQueueMailbox implements Mailbox<PrioritizedMessage, Runnable>
    {
        private final List<Queue<Runnable>> queues;
        
        public PrioritizedQueueMailbox(final int priorityCount) {
            this.queues = IntStream.range(0, priorityCount).mapToObj(integer -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
        }
        
        @Nullable
        @Override
        public Runnable poll() {
            for (final Queue<Runnable> queue2 : this.queues) {
                final Runnable runnable3 = queue2.poll();
                if (runnable3 != null) {
                    return runnable3;
                }
            }
            return null;
        }
        
        @Override
        public boolean add(final PrioritizedMessage prioritizedMessage) {
            final int integer2 = prioritizedMessage.getPriority();
            this.queues.get(integer2).add(prioritizedMessage);
            return true;
        }
        
        @Override
        public boolean isEmpty() {
            return this.queues.stream().allMatch(Collection::isEmpty);
        }
    }
}
