package net.minecraft.server.dedicated.gui;

import net.minecraft.util.SystemUtil;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;
import java.text.DecimalFormat;
import javax.swing.JComponent;

public class PlayerStatsGui extends JComponent
{
    private static final DecimalFormat AVG_TICK_FORMAT;
    private final int[] memoryUsePercentage;
    private int memoryusePctPos;
    private final String[] lines;
    private final MinecraftServer server;
    private final Timer timer;
    
    public PlayerStatsGui(final MinecraftServer minecraftServer) {
        this.memoryUsePercentage = new int[256];
        this.lines = new String[11];
        this.server = minecraftServer;
        this.setPreferredSize(new Dimension(456, 246));
        this.setMinimumSize(new Dimension(456, 246));
        this.setMaximumSize(new Dimension(456, 246));
        (this.timer = new Timer(500, actionEvent -> this.update())).start();
        this.setBackground(Color.BLACK);
    }
    
    private void update() {
        final long long1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.lines[0] = "Memory use: " + long1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        this.lines[1] = "Avg tick: " + PlayerStatsGui.AVG_TICK_FORMAT.format(this.average(this.server.lastTickLengths) * 1.0E-6) + " ms";
        this.memoryUsePercentage[this.memoryusePctPos++ & 0xFF] = (int)(long1 * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }
    
    private double average(final long[] arr) {
        long long2 = 0L;
        for (final long long3 : arr) {
            long2 += long3;
        }
        return long2 / (double)arr.length;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        graphics.setColor(new Color(16777215));
        graphics.fillRect(0, 0, 456, 246);
        for (int integer2 = 0; integer2 < 256; ++integer2) {
            final int integer3 = this.memoryUsePercentage[integer2 + this.memoryusePctPos & 0xFF];
            graphics.setColor(new Color(integer3 + 28 << 16));
            graphics.fillRect(integer2, 100 - integer3, 1, integer3);
        }
        graphics.setColor(Color.BLACK);
        for (int integer2 = 0; integer2 < this.lines.length; ++integer2) {
            final String string3 = this.lines[integer2];
            if (string3 != null) {
                graphics.drawString(string3, 32, 116 + integer2 * 16);
            }
        }
    }
    
    public void stop() {
        this.timer.stop();
    }
    
    static {
        AVG_TICK_FORMAT = SystemUtil.<DecimalFormat>consume(new DecimalFormat("########0.000"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    }
}
