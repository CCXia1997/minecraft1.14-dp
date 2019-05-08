package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ThreadExecutor;

@Environment(EnvType.CLIENT)
public class SoundExecutor extends ThreadExecutor<Runnable>
{
    private Thread thread;
    private volatile boolean stopped;
    
    public SoundExecutor() {
        super("Sound executor");
        this.thread = this.createThread();
    }
    
    private Thread createThread() {
        final Thread thread1 = new Thread(this::waitForStop);
        thread1.setDaemon(true);
        thread1.setName("Sound engine");
        thread1.start();
        return thread1;
    }
    
    @Override
    protected Runnable prepareRunnable(final Runnable runnable) {
        return runnable;
    }
    
    @Override
    protected boolean canRun(final Runnable runnable) {
        return !this.stopped;
    }
    
    @Override
    protected Thread getThread() {
        return this.thread;
    }
    
    private void waitForStop() {
        while (!this.stopped) {
            this.waitFor(() -> this.stopped);
        }
    }
    
    public void restart() {
        this.stopped = true;
        this.thread.interrupt();
        try {
            this.thread.join();
        }
        catch (InterruptedException interruptedException1) {
            Thread.currentThread().interrupt();
        }
        this.clear();
        this.stopped = false;
        this.thread = this.createThread();
    }
}
