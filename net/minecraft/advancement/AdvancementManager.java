package net.minecraft.advancement;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;
import com.google.common.base.Functions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class AdvancementManager
{
    private static final Logger LOGGER;
    private final Map<Identifier, Advancement> advancements;
    private final Set<Advancement> roots;
    private final Set<Advancement> dependents;
    private Listener listener;
    
    public AdvancementManager() {
        this.advancements = Maps.newHashMap();
        this.roots = Sets.newLinkedHashSet();
        this.dependents = Sets.newLinkedHashSet();
    }
    
    @Environment(EnvType.CLIENT)
    private void remove(final Advancement advancement) {
        for (final Advancement advancement2 : advancement.getChildren()) {
            this.remove(advancement2);
        }
        AdvancementManager.LOGGER.info("Forgot about advancement {}", advancement.getId());
        this.advancements.remove(advancement.getId());
        if (advancement.getParent() == null) {
            this.roots.remove(advancement);
            if (this.listener != null) {
                this.listener.onRootRemoved(advancement);
            }
        }
        else {
            this.dependents.remove(advancement);
            if (this.listener != null) {
                this.listener.onDependentRemoved(advancement);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void removeAll(final Set<Identifier> set) {
        for (final Identifier identifier3 : set) {
            final Advancement advancement4 = this.advancements.get(identifier3);
            if (advancement4 == null) {
                AdvancementManager.LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier3);
            }
            else {
                this.remove(advancement4);
            }
        }
    }
    
    public void load(final Map<Identifier, Advancement.Task> map) {
        final Function<Identifier, Advancement> function2 = Functions.forMap(this.advancements, null);
        while (!map.isEmpty()) {
            boolean boolean3 = false;
            final Iterator<Map.Entry<Identifier, Advancement.Task>> iterator4 = map.entrySet().iterator();
            while (iterator4.hasNext()) {
                final Map.Entry<Identifier, Advancement.Task> entry5 = iterator4.next();
                final Identifier identifier6 = entry5.getKey();
                final Advancement.Task task7 = entry5.getValue();
                if (task7.findParent(function2)) {
                    final Advancement advancement8 = task7.build(identifier6);
                    this.advancements.put(identifier6, advancement8);
                    boolean3 = true;
                    iterator4.remove();
                    if (advancement8.getParent() == null) {
                        this.roots.add(advancement8);
                        if (this.listener == null) {
                            continue;
                        }
                        this.listener.onRootAdded(advancement8);
                    }
                    else {
                        this.dependents.add(advancement8);
                        if (this.listener == null) {
                            continue;
                        }
                        this.listener.onDependentAdded(advancement8);
                    }
                }
            }
            if (!boolean3) {
                for (final Map.Entry<Identifier, Advancement.Task> entry5 : map.entrySet()) {
                    AdvancementManager.LOGGER.error("Couldn't load advancement {}: {}", entry5.getKey(), entry5.getValue());
                }
                break;
            }
        }
        AdvancementManager.LOGGER.info("Loaded {} advancements", this.advancements.size());
    }
    
    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.dependents.clear();
        if (this.listener != null) {
            this.listener.onClear();
        }
    }
    
    public Iterable<Advancement> getRoots() {
        return this.roots;
    }
    
    public Collection<Advancement> getAdvancements() {
        return this.advancements.values();
    }
    
    @Nullable
    public Advancement get(final Identifier identifier) {
        return this.advancements.get(identifier);
    }
    
    @Environment(EnvType.CLIENT)
    public void setListener(@Nullable final Listener listener) {
        this.listener = listener;
        if (listener != null) {
            for (final Advancement advancement3 : this.roots) {
                listener.onRootAdded(advancement3);
            }
            for (final Advancement advancement3 : this.dependents) {
                listener.onDependentAdded(advancement3);
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public interface Listener
    {
        void onRootAdded(final Advancement arg1);
        
        @Environment(EnvType.CLIENT)
        void onRootRemoved(final Advancement arg1);
        
        void onDependentAdded(final Advancement arg1);
        
        @Environment(EnvType.CLIENT)
        void onDependentRemoved(final Advancement arg1);
        
        void onClear();
    }
}
