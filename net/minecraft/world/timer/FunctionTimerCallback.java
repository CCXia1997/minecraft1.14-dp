package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import net.minecraft.server.MinecraftServer;

public class FunctionTimerCallback implements TimerCallback<MinecraftServer>
{
    private final Identifier name;
    
    public FunctionTimerCallback(final Identifier identifier) {
        this.name = identifier;
    }
    
    @Override
    public void call(final MinecraftServer server, final Timer<MinecraftServer> events, final long time) {
        final CommandFunctionManager commandFunctionManager5 = server.getCommandFunctionManager();
        final CommandFunctionManager commandFunctionManager6;
        commandFunctionManager5.getFunction(this.name).ifPresent(commandFunction -> commandFunctionManager6.execute(commandFunction, commandFunctionManager6.getFunctionCommandSource()));
    }
    
    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTimerCallback>
    {
        public Serializer() {
            super(new Identifier("function"), FunctionTimerCallback.class);
        }
        
        @Override
        public void serialize(final CompoundTag tag, final FunctionTimerCallback callback) {
            tag.putString("Name", callback.name.toString());
        }
        
        @Override
        public FunctionTimerCallback deserialize(final CompoundTag tag) {
            final Identifier identifier2 = new Identifier(tag.getString("Name"));
            return new FunctionTimerCallback(identifier2);
        }
    }
}
