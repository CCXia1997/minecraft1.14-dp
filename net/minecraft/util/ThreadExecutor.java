package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import java.util.function.BooleanSupplier;
import java.util.concurrent.locks.LockSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import com.google.common.collect.Queues;
import java.util.Queue;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.Executor;

public abstract class ThreadExecutor<R extends Runnable> implements Actor<R>, Executor
{
    private final String name;
    private static final Logger LOGGER;
    private final Queue<R> taskQueue;
    private int waitCount;
    
    protected ThreadExecutor(final String string) {
        this.taskQueue = Queues.newConcurrentLinkedQueue();
        this.name = string;
    }
    
    protected abstract R prepareRunnable(final Runnable arg1);
    
    protected abstract boolean canRun(final R arg1);
    
    public boolean isOnThread() {
        return Thread.currentThread() == this.getThread();
    }
    
    protected abstract Thread getThread();
    
    protected boolean shouldRunAsync() {
        return !this.isOnThread();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Environment(EnvType.CLIENT)
    public <V> CompletableFuture<V> executeFuture(final Supplier<V> supplier) {
        if (this.shouldRunAsync()) {
            return CompletableFuture.<V>supplyAsync(supplier, this);
        }
        return CompletableFuture.<V>completedFuture(supplier.get());
    }
    
    private CompletableFuture<Object> executeFuture(final Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }
    
    public void executeSync(final Runnable runnable) {
        if (!this.isOnThread()) {
            this.executeFuture(runnable).join();
        }
        else {
            runnable.run();
        }
    }
    
    @Override
    public void send(final R message) {
        this.taskQueue.add(message);
        LockSupport.unpark(this.getThread());
    }
    
    @Override
    public void execute(final Runnable runnable) {
        if (this.shouldRunAsync()) {
            this.send(this.prepareRunnable(runnable));
        }
        else {
            runnable.run();
        }
    }
    
    @Environment(EnvType.CLIENT)
    protected void clear() {
        this.taskQueue.clear();
    }
    
    protected void executeTaskQueue() {
        while (this.executeQueuedTask()) {}
    }
    
    protected boolean executeQueuedTask() {
        final R runnable1 = this.taskQueue.peek();
        if (runnable1 == null) {
            return false;
        }
        if (this.waitCount == 0 && !this.canRun(runnable1)) {
            return false;
        }
        this.runSafely(this.taskQueue.remove());
        return true;
    }
    
    public void waitFor(final BooleanSupplier booleanSupplier) {
        ++this.waitCount;
        try {
            while (!booleanSupplier.getAsBoolean()) {
                if (!this.executeQueuedTask()) {
                    LockSupport.parkNanos("waiting for tasks", 1000L);
                }
            }
        }
        finally {
            --this.waitCount;
        }
    }
    
    protected void runSafely(final R runnable) {
        try {
            runnable.run();
        }
        catch (Exception exception2) {
            ThreadExecutor.LOGGER.fatal("Error executing task on {}", this.getName(), exception2);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
