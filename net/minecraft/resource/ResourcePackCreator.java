package net.minecraft.resource;

import java.util.Map;

public interface ResourcePackCreator
{
     <T extends ResourcePackContainer> void registerContainer(final Map<String, T> arg1, final ResourcePackContainer.Factory<T> arg2);
}
