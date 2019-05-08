package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.tag.Tag;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;
import net.minecraft.server.MinecraftServer;

public class FunctionTagTimerCallback implements TimerCallback<MinecraftServer>
{
    private final Identifier name;
    
    public FunctionTagTimerCallback(final Identifier identifier) {
        this.name = identifier;
    }
    
    @Override
    public void call(final MinecraftServer server, final Timer<MinecraftServer> events, final long time) {
        final CommandFunctionManager commandFunctionManager5 = server.getCommandFunctionManager();
        final Tag<CommandFunction> tag6 = commandFunctionManager5.getTags().getOrCreate(this.name);
        for (final CommandFunction commandFunction8 : tag6.values()) {
            commandFunctionManager5.execute(commandFunction8, commandFunctionManager5.getFunctionCommandSource());
        }
    }
    
    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback>
    {
        public Serializer() {
            super(new Identifier("function_tag"), FunctionTagTimerCallback.class);
        }
        
        @Override
        public void serialize(final CompoundTag tag, final FunctionTagTimerCallback callback) {
            tag.putString("Name", callback.name.toString());
        }
        
        @Override
        public FunctionTagTimerCallback deserialize(final CompoundTag tag) {
            final Identifier identifier2 = new Identifier(tag.getString("Name"));
            return new FunctionTagTimerCallback(identifier2);
        }
    }
}
