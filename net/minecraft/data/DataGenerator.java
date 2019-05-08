package net.minecraft.data;

import net.minecraft.Bootstrap;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.util.List;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.logging.log4j.Logger;

public class DataGenerator
{
    private static final Logger LOGGER;
    private final Collection<Path> inputs;
    private final Path output;
    private final List<DataProvider> providers;
    
    public DataGenerator(final Path output, final Collection<Path> collection) {
        this.providers = Lists.newArrayList();
        this.output = output;
        this.inputs = collection;
    }
    
    public Collection<Path> getInputs() {
        return this.inputs;
    }
    
    public Path getOutput() {
        return this.output;
    }
    
    public void run() throws IOException {
        final DataCache dataCache1 = new DataCache(this.output, "cache");
        dataCache1.ignore(this.getOutput().resolve("version.json"));
        final Stopwatch stopwatch2 = Stopwatch.createStarted();
        final Stopwatch stopwatch3 = Stopwatch.createUnstarted();
        for (final DataProvider dataProvider5 : this.providers) {
            DataGenerator.LOGGER.info("Starting provider: {}", dataProvider5.getName());
            stopwatch3.start();
            dataProvider5.run(dataCache1);
            stopwatch3.stop();
            DataGenerator.LOGGER.info("{} finished after {} ms", dataProvider5.getName(), stopwatch3.elapsed(TimeUnit.MILLISECONDS));
            stopwatch3.reset();
        }
        DataGenerator.LOGGER.info("All providers took: {} ms", stopwatch2.elapsed(TimeUnit.MILLISECONDS));
        dataCache1.write();
    }
    
    public void install(final DataProvider dataProvider) {
        this.providers.add(dataProvider);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        Bootstrap.initialize();
    }
}
