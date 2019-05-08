package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.RejectedExecutionException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;

public class MailboxProcessor<T> implements Actor<T>, AutoCloseable, Runnable
{
    private static final Logger LOGGER;
    private final AtomicInteger stateFlags;
    public final Mailbox<? super T, ? extends Runnable> mailbox;
    private final Executor executor;
    private final String name;
    
    public static MailboxProcessor<Runnable> create(final Executor executor, final String name) {
        return new MailboxProcessor<Runnable>(new Mailbox.QueueMailbox(new ConcurrentLinkedQueue<>()), executor, name);
    }
    
    public MailboxProcessor(final Mailbox<? super T, ? extends Runnable> mailbox, final Executor executor, final String name) {
        this.stateFlags = new AtomicInteger(0);
        this.executor = executor;
        this.mailbox = mailbox;
        this.name = name;
    }
    
    private boolean lock() {
        int integer1;
        do {
            integer1 = this.stateFlags.get();
            if ((integer1 & 0x3) != 0x0) {
                return false;
            }
        } while (!this.stateFlags.compareAndSet(integer1, integer1 | 0x2));
        return true;
    }
    
    private void unlock() {
        int integer1;
        do {
            integer1 = this.stateFlags.get();
        } while (!this.stateFlags.compareAndSet(integer1, integer1 & 0xFFFFFFFD));
    }
    
    private boolean hasMessages() {
        return (this.stateFlags.get() & 0x1) == 0x0 && !this.mailbox.isEmpty();
    }
    
    @Override
    public void close() {
        int integer1;
        do {
            integer1 = this.stateFlags.get();
        } while (!this.stateFlags.compareAndSet(integer1, integer1 | 0x1));
    }
    
    private boolean isLocked() {
        return (this.stateFlags.get() & 0x2) != 0x0;
    }
    
    private boolean runNext() {
        if (!this.isLocked()) {
            return false;
        }
        final Runnable runnable1 = (Runnable)this.mailbox.poll();
        if (runnable1 == null) {
            return false;
        }
        runnable1.run();
        return true;
    }
    
    @Override
    public void run() {
        try {
            this.run(integer -> integer == 0);
        }
        finally {
            this.unlock();
            this.execute();
        }
    }
    
    @Override
    public void send(final T message) {
        this.mailbox.add(message);
        this.execute();
    }
    
    private void execute() {
        if (this.hasMessages() && this.lock()) {
            try {
                this.executor.execute(this);
            }
            catch (RejectedExecutionException rejectedExecutionException3) {
                try {
                    this.executor.execute(this);
                }
                catch (RejectedExecutionException rejectedExecutionException2) {
                    MailboxProcessor.LOGGER.error("Cound not schedule mailbox", (Throwable)rejectedExecutionException2);
                }
            }
        }
    }
    
    private int run(final Int2BooleanFunction keepRunningProvider) {
        int integer2;
        for (integer2 = 0; keepRunningProvider.get(integer2) && this.runNext(); ++integer2) {}
        return integer2;
    }
    
    @Override
    public String toString() {
        return this.name + " " + this.stateFlags.get() + " " + this.mailbox.isEmpty();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
