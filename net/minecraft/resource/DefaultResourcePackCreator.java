package net.minecraft.resource;

import java.util.Map;

public class DefaultResourcePackCreator implements ResourcePackCreator
{
    private final DefaultResourcePack pack;
    
    public DefaultResourcePackCreator() {
        this.pack = new DefaultResourcePack(new String[] { "minecraft" });
    }
    
    @Override
    public <T extends ResourcePackContainer> void registerContainer(final Map<String, T> registry, final ResourcePackContainer.Factory<T> factory) {
        final T resourcePackContainer3 = ResourcePackContainer.<T>of("vanilla", false, () -> this.pack, factory, ResourcePackContainer.SortingDirection.b);
        if (resourcePackContainer3 != null) {
            registry.put("vanilla", resourcePackContainer3);
        }
    }
}
