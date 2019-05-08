package net.minecraft.server.dedicated.gui;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Vector;
import net.minecraft.server.MinecraftServer;
import javax.swing.JList;

public class PlayerListGui extends JList<String>
{
    private final MinecraftServer server;
    private int tick;
    
    public PlayerListGui(final MinecraftServer minecraftServer) {
        (this.server = minecraftServer).addServerGuiTickable(this::tick);
    }
    
    public void tick() {
        if (this.tick++ % 20 == 0) {
            final Vector<String> vector1 = new Vector<String>();
            for (int integer2 = 0; integer2 < this.server.getPlayerManager().getPlayerList().size(); ++integer2) {
                vector1.add(this.server.getPlayerManager().getPlayerList().get(integer2).getGameProfile().getName());
            }
            this.setListData(vector1);
        }
    }
}
