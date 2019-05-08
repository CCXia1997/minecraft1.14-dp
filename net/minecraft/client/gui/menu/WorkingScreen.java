package net.minecraft.client.gui.menu;

import java.util.Objects;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.client.util.NarratorManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ProgressListener;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class WorkingScreen extends Screen implements ProgressListener
{
    private String title;
    private String task;
    private int progress;
    private boolean done;
    
    public WorkingScreen() {
        super(NarratorManager.a);
        this.title = "";
        this.task = "";
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void a(final TextComponent textComponent) {
        this.b(textComponent);
    }
    
    @Override
    public void b(final TextComponent textComponent) {
        this.title = textComponent.getFormattedText();
        this.c(new TranslatableTextComponent("progress.working", new Object[0]));
    }
    
    @Override
    public void c(final TextComponent textComponent) {
        this.task = textComponent.getFormattedText();
        this.progressStagePercentage(0);
    }
    
    @Override
    public void progressStagePercentage(final int integer) {
        this.progress = integer;
    }
    
    @Override
    public void setDone() {
        this.done = true;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (this.done) {
            if (!this.minecraft.isConnectedToRealms()) {
                this.minecraft.openScreen(null);
            }
            return;
        }
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
        if (!Objects.equals(this.task, "") && this.progress != 0) {
            this.drawCenteredString(this.font, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
        }
        super.render(mouseX, mouseY, delta);
    }
}
