package net.minecraft.util;

import com.mojang.datafixers.types.DynamicOps;

public interface DynamicSerializable
{
     <T> T serialize(final DynamicOps<T> arg1);
}
