package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.invoke.MethodHandles;
import org.lwjgl.system.Pointer;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UntrackMemoryUtil
{
    @Nullable
    private static final MethodHandle UNTRACK_METHOD_HANDLE;
    
    public static void untrack(final long long1) {
        if (UntrackMemoryUtil.UNTRACK_METHOD_HANDLE == null) {
            return;
        }
        try {
            UntrackMemoryUtil.UNTRACK_METHOD_HANDLE.invoke(long1);
        }
        catch (Throwable throwable3) {
            throw new RuntimeException(throwable3);
        }
    }
    
    public static void untrack(final Pointer pointer) {
        untrack(pointer.address());
    }
    
    static {
        MethodHandles.Lookup lookup1;
        Class<?> class2;
        Method method3;
        Field field4;
        Object object5;
        final ReflectiveOperationException ex;
        ReflectiveOperationException reflectiveOperationException1;
        UNTRACK_METHOD_HANDLE = GLX.<MethodHandle>make(() -> {
            try {
                lookup1 = MethodHandles.lookup();
                class2 = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
                method3 = class2.getDeclaredMethod("untrack", Long.TYPE);
                method3.setAccessible(true);
                field4 = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
                field4.setAccessible(true);
                object5 = field4.get(null);
                if (class2.isInstance(object5)) {
                    return lookup1.unreflect(method3);
                }
                else {
                    try {
                        return null;
                    }
                    catch (NoSuchMethodException | NoSuchFieldException ex2) {
                        reflectiveOperationException1 = ex;
                        throw new RuntimeException(reflectiveOperationException1);
                    }
                }
            }
            catch (ClassNotFoundException ex3) {}
            catch (NoSuchMethodException ex4) {}
            catch (NoSuchFieldException ex5) {}
            catch (IllegalAccessException ex6) {}
        });
    }
}
