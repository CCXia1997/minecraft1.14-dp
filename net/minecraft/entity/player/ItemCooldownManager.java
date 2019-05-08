package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import java.util.Map;

public class ItemCooldownManager
{
    private final Map<Item, Entry> entries;
    private int tick;
    
    public ItemCooldownManager() {
        this.entries = Maps.newHashMap();
    }
    
    public boolean isCoolingDown(final Item item) {
        return this.getCooldownProgress(item, 0.0f) > 0.0f;
    }
    
    public float getCooldownProgress(final Item item, final float partialTicks) {
        final Entry entry3 = this.entries.get(item);
        if (entry3 != null) {
            final float float4 = (float)(entry3.endTick - entry3.startTick);
            final float float5 = entry3.endTick - (this.tick + partialTicks);
            return MathHelper.clamp(float5 / float4, 0.0f, 1.0f);
        }
        return 0.0f;
    }
    
    public void update() {
        ++this.tick;
        if (!this.entries.isEmpty()) {
            final Iterator<Map.Entry<Item, Entry>> iterator1 = this.entries.entrySet().iterator();
            while (iterator1.hasNext()) {
                final Map.Entry<Item, Entry> entry2 = iterator1.next();
                if (entry2.getValue().endTick <= this.tick) {
                    iterator1.remove();
                    this.onCooldownUpdate(entry2.getKey());
                }
            }
        }
    }
    
    public void set(final Item item, final int duration) {
        this.entries.put(item, new Entry(this.tick, this.tick + duration));
        this.onCooldownUpdate(item, duration);
    }
    
    @Environment(EnvType.CLIENT)
    public void remove(final Item item) {
        this.entries.remove(item);
        this.onCooldownUpdate(item);
    }
    
    protected void onCooldownUpdate(final Item item, final int integer) {
    }
    
    protected void onCooldownUpdate(final Item item) {
    }
    
    class Entry
    {
        private final int startTick;
        private final int endTick;
        
        private Entry(final int startTick, final int endTick) {
            this.startTick = startTick;
            this.endTick = endTick;
        }
    }
}
