package net.minecraft.client.resource;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import net.minecraft.resource.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import com.google.common.collect.Lists;
import net.minecraft.client.util.Session;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.List;
import net.minecraft.resource.SupplyingResourceReloadListener;

@Environment(EnvType.CLIENT)
public class SplashTextResourceSupplier extends SupplyingResourceReloadListener<List<String>>
{
    private static final Identifier RESOURCE_ID;
    private static final Random RANDOM;
    private final List<String> splashTexts;
    private final Session d;
    
    public SplashTextResourceSupplier(final Session session) {
        this.splashTexts = Lists.newArrayList();
        this.d = session;
    }
    
    @Override
    protected List<String> load(final ResourceManager resourceManager, final Profiler profiler) {
        try (final Resource resource3 = MinecraftClient.getInstance().getResourceManager().getResource(SplashTextResourceSupplier.RESOURCE_ID);
             final BufferedReader bufferedReader5 = new BufferedReader(new InputStreamReader(resource3.getInputStream(), StandardCharsets.UTF_8))) {
            return bufferedReader5.lines().map(String::trim).filter(string -> string.hashCode() != 125780783).collect(Collectors.toList());
        }
        catch (IOException iOException3) {
            return Collections.<String>emptyList();
        }
    }
    
    @Override
    protected void apply(final List<String> result, final ResourceManager resourceManager, final Profiler profiler) {
        this.splashTexts.clear();
        this.splashTexts.addAll(result);
    }
    
    @Nullable
    public String get() {
        final Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date());
        if (calendar1.get(2) + 1 == 12 && calendar1.get(5) == 24) {
            return "Merry X-mas!";
        }
        if (calendar1.get(2) + 1 == 1 && calendar1.get(5) == 1) {
            return "Happy new year!";
        }
        if (calendar1.get(2) + 1 == 10 && calendar1.get(5) == 31) {
            return "OOoooOOOoooo! Spooky!";
        }
        if (this.splashTexts.isEmpty()) {
            return null;
        }
        if (this.d != null && SplashTextResourceSupplier.RANDOM.nextInt(this.splashTexts.size()) == 42) {
            return this.d.getUsername().toUpperCase(Locale.ROOT) + " IS YOU";
        }
        return this.splashTexts.get(SplashTextResourceSupplier.RANDOM.nextInt(this.splashTexts.size()));
    }
    
    static {
        RESOURCE_ID = new Identifier("texts/splashes.txt");
        RANDOM = new Random();
    }
}
