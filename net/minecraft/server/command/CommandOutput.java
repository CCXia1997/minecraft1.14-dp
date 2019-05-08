package net.minecraft.server.command;

import net.minecraft.text.TextComponent;

public interface CommandOutput
{
    public static final CommandOutput DUMMY = new CommandOutput() {
        @Override
        public void sendMessage(final TextComponent message) {
        }
        
        @Override
        public boolean sendCommandFeedback() {
            return false;
        }
        
        @Override
        public boolean shouldTrackOutput() {
            return false;
        }
        
        @Override
        public boolean shouldBroadcastConsoleToOps() {
            return false;
        }
    };
    
    void sendMessage(final TextComponent arg1);
    
    boolean sendCommandFeedback();
    
    boolean shouldTrackOutput();
    
    boolean shouldBroadcastConsoleToOps();
}
