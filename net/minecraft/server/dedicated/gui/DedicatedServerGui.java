package net.minecraft.server.dedicated.gui;

import org.apache.logging.log4j.LogManager;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import net.minecraft.util.UncaughtExceptionLogger;
import com.mojang.util.QueueLogAppender;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import net.minecraft.server.MinecraftServer;
import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import com.google.common.collect.Lists;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Collection;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.apache.logging.log4j.Logger;
import java.awt.Font;
import javax.swing.JComponent;

public class DedicatedServerGui extends JComponent
{
    private static final Font FONT_MONOSPACE;
    private static final Logger LOGGER;
    private final MinecraftDedicatedServer server;
    private Thread consoleUpdateThread;
    private final Collection<Runnable> stopTasks;
    private final AtomicBoolean stopped;
    
    public static DedicatedServerGui create(final MinecraftDedicatedServer minecraftDedicatedServer) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {}
        final JFrame jFrame2 = new JFrame("Minecraft server");
        final DedicatedServerGui dedicatedServerGui3 = new DedicatedServerGui(minecraftDedicatedServer);
        jFrame2.setDefaultCloseOperation(2);
        jFrame2.add(dedicatedServerGui3);
        jFrame2.pack();
        jFrame2.setLocationRelativeTo(null);
        jFrame2.setVisible(true);
        jFrame2.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                if (!dedicatedServerGui3.stopped.getAndSet(true)) {
                    jFrame2.setTitle("Minecraft server - shutting down!");
                    minecraftDedicatedServer.stop(true);
                    DedicatedServerGui.this.runStopTasks();
                }
            }
        });
        dedicatedServerGui3.addStopTask(jFrame2::dispose);
        dedicatedServerGui3.start();
        return dedicatedServerGui3;
    }
    
    private DedicatedServerGui(final MinecraftDedicatedServer minecraftDedicatedServer) {
        this.stopTasks = Lists.newArrayList();
        this.stopped = new AtomicBoolean();
        this.server = minecraftDedicatedServer;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());
        try {
            this.add(this.createLogPanel(), "Center");
            this.add(this.createStatsPanel(), "West");
        }
        catch (Exception exception2) {
            DedicatedServerGui.LOGGER.error("Couldn't build server GUI", (Throwable)exception2);
        }
    }
    
    public void addStopTask(final Runnable task) {
        this.stopTasks.add(task);
    }
    
    private JComponent createStatsPanel() {
        final JPanel jPanel1 = new JPanel(new BorderLayout());
        final PlayerStatsGui playerStatsGui2 = new PlayerStatsGui(this.server);
        this.stopTasks.add(playerStatsGui2::stop);
        jPanel1.add(playerStatsGui2, "North");
        jPanel1.add(this.createPlaysPanel(), "Center");
        jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jPanel1;
    }
    
    private JComponent createPlaysPanel() {
        final JList<?> jList1 = new PlayerListGui(this.server);
        final JScrollPane jScrollPane2 = new JScrollPane(jList1, 22, 30);
        jScrollPane2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jScrollPane2;
    }
    
    private JComponent createLogPanel() {
        final JPanel jPanel1 = new JPanel(new BorderLayout());
        final JTextArea jTextArea2 = new JTextArea();
        final JScrollPane jScrollPane3 = new JScrollPane(jTextArea2, 22, 30);
        jTextArea2.setEditable(false);
        jTextArea2.setFont(DedicatedServerGui.FONT_MONOSPACE);
        final JTextField jTextField4 = new JTextField();
        final JTextComponent textComponent;
        final String string3;
        jTextField4.addActionListener(actionEvent -> {
            string3 = textComponent.getText().trim();
            if (!string3.isEmpty()) {
                this.server.enqueueCommand(string3, this.server.getCommandSource());
            }
            textComponent.setText("");
            return;
        });
        jTextArea2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
            }
        });
        jPanel1.add(jScrollPane3, "Center");
        jPanel1.add(jTextField4, "South");
        jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        String string4;
        final Object o;
        final JTextArea textArea;
        final JScrollPane scrollPane;
        (this.consoleUpdateThread = new Thread(() -> {
            while (true) {
                string4 = QueueLogAppender.getNextLogEvent("ServerGuiConsole");
                if (o != null) {
                    this.appendToConsole(textArea, scrollPane, string4);
                }
                else {
                    break;
                }
            }
            return;
        })).setUncaughtExceptionHandler(new UncaughtExceptionLogger(DedicatedServerGui.LOGGER));
        this.consoleUpdateThread.setDaemon(true);
        return jPanel1;
    }
    
    public void start() {
        this.consoleUpdateThread.start();
    }
    
    public void stop() {
        if (!this.stopped.getAndSet(true)) {
            this.runStopTasks();
        }
    }
    
    private void runStopTasks() {
        this.stopTasks.forEach(Runnable::run);
    }
    
    public void appendToConsole(final JTextArea textArea, final JScrollPane scrollPane, final String string) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.appendToConsole(textArea, scrollPane, string));
            return;
        }
        final Document document4 = textArea.getDocument();
        final JScrollBar jScrollBar5 = scrollPane.getVerticalScrollBar();
        boolean boolean6 = false;
        if (scrollPane.getViewport().getView() == textArea) {
            boolean6 = (jScrollBar5.getValue() + jScrollBar5.getSize().getHeight() + DedicatedServerGui.FONT_MONOSPACE.getSize() * 4 > jScrollBar5.getMaximum());
        }
        try {
            document4.insertString(document4.getLength(), string, null);
        }
        catch (BadLocationException ex) {}
        if (boolean6) {
            jScrollBar5.setValue(Integer.MAX_VALUE);
        }
    }
    
    static {
        FONT_MONOSPACE = new Font("Monospaced", 0, 12);
        LOGGER = LogManager.getLogger();
    }
}
