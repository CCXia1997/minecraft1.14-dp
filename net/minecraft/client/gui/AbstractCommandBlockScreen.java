package net.minecraft.client.gui;

import com.mojang.brigadier.Message;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.Vec2f;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.font.TextRenderer;
import javax.annotation.Nullable;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import java.util.Collection;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.gui.ingame.ChatScreen;
import java.util.Iterator;
import net.minecraft.text.TextFormat;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.TextComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.CommandBlockExecutor;
import com.google.common.collect.Lists;
import net.minecraft.client.util.NarratorManager;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.ParseResults;
import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen
{
    protected TextFieldWidget consoleCommandTextField;
    protected TextFieldWidget previousOutputTextField;
    protected ButtonWidget doneButton;
    protected ButtonWidget cancelButton;
    protected ButtonWidget toggleTrackingOutputButton;
    protected boolean trackingOutput;
    protected final List<String> g;
    protected int h;
    protected int i;
    protected ParseResults<CommandSource> parsedCommand;
    protected CompletableFuture<Suggestions> k;
    protected a l;
    private boolean suggestionsDisabled;
    
    public AbstractCommandBlockScreen() {
        super(NarratorManager.a);
        this.g = Lists.newArrayList();
    }
    
    @Override
    public void tick() {
        this.consoleCommandTextField.tick();
    }
    
    abstract CommandBlockExecutor getCommandExecutor();
    
    abstract int b();
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.doneButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.commitAndClose()));
        this.cancelButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.onClose()));
        final CommandBlockExecutor commandBlockExecutor2;
        this.toggleTrackingOutputButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 150 - 20, this.b(), 20, 20, "O", buttonWidget -> {
            commandBlockExecutor2 = this.getCommandExecutor();
            commandBlockExecutor2.shouldTrackOutput(!commandBlockExecutor2.isTrackingOutput());
            this.updateTrackedOutput();
            return;
        }));
        (this.consoleCommandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.translate("advMode.command"))).setMaxLength(32500);
        this.consoleCommandTextField.setRenderTextProvider(this::a);
        this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
        this.children.add(this.consoleCommandTextField);
        (this.previousOutputTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.b(), 276, 20, I18n.translate("advMode.previousOutput"))).setMaxLength(32500);
        this.previousOutputTextField.setIsEditable(false);
        this.previousOutputTextField.setText("-");
        this.children.add(this.previousOutputTextField);
        this.setInitialFocus(this.consoleCommandTextField);
        this.consoleCommandTextField.a(true);
        this.updateCommand();
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.consoleCommandTextField.getText();
        this.init(client, width, height);
        this.setCommand(string4);
        this.updateCommand();
    }
    
    protected void updateTrackedOutput() {
        if (this.getCommandExecutor().isTrackingOutput()) {
            this.toggleTrackingOutputButton.setMessage("O");
            this.previousOutputTextField.setText(this.getCommandExecutor().getLastOutput().getString());
        }
        else {
            this.toggleTrackingOutputButton.setMessage("X");
            this.previousOutputTextField.setText("-");
        }
    }
    
    protected void commitAndClose() {
        final CommandBlockExecutor commandBlockExecutor1 = this.getCommandExecutor();
        this.syncSettingsToServer(commandBlockExecutor1);
        if (!commandBlockExecutor1.isTrackingOutput()) {
            commandBlockExecutor1.setLastOutput(null);
        }
        this.minecraft.openScreen(null);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    protected abstract void syncSettingsToServer(final CommandBlockExecutor arg1);
    
    @Override
    public void onClose() {
        this.getCommandExecutor().shouldTrackOutput(this.trackingOutput);
        this.minecraft.openScreen(null);
    }
    
    private void onCommandChanged(final String string) {
        this.updateCommand();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (this.l != null && this.l.b(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.getFocused() == this.consoleCommandTextField && keyCode == 258) {
            this.f();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.commitAndClose();
            return true;
        }
        if (keyCode == 258 && this.getFocused() == this.consoleCommandTextField) {
            this.f();
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return (this.l != null && this.l.a(MathHelper.clamp(amount, -1.0, 1.0))) || super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return (this.l != null && this.l.a((int)mouseX, (int)mouseY, button)) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    protected void updateCommand() {
        final String string1 = this.consoleCommandTextField.getText();
        if (this.parsedCommand != null && !this.parsedCommand.getReader().getString().equals(string1)) {
            this.parsedCommand = null;
        }
        if (!this.suggestionsDisabled) {
            this.consoleCommandTextField.setSuggestion(null);
            this.l = null;
        }
        this.g.clear();
        final CommandDispatcher<CommandSource> commandDispatcher2 = this.minecraft.player.networkHandler.getCommandDispatcher();
        final StringReader stringReader3 = new StringReader(string1);
        if (stringReader3.canRead() && stringReader3.peek() == '/') {
            stringReader3.skip();
        }
        final int integer4 = stringReader3.getCursor();
        if (this.parsedCommand == null) {
            this.parsedCommand = (ParseResults<CommandSource>)commandDispatcher2.parse(stringReader3, this.minecraft.player.networkHandler.getCommandSource());
        }
        final int integer5 = this.consoleCommandTextField.getCursor();
        if (integer5 >= integer4 && (this.l == null || !this.suggestionsDisabled)) {
            (this.k = (CompletableFuture<Suggestions>)commandDispatcher2.getCompletionSuggestions((ParseResults)this.parsedCommand, integer5)).thenRun(() -> {
                if (!(!this.k.isDone())) {
                    this.g();
                }
            });
        }
    }
    
    private void g() {
        if (this.k.join().isEmpty() && !this.parsedCommand.getExceptions().isEmpty() && this.consoleCommandTextField.getCursor() == this.consoleCommandTextField.getText().length()) {
            int integer1 = 0;
            for (final Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> entry3 : this.parsedCommand.getExceptions().entrySet()) {
                final CommandSyntaxException commandSyntaxException4 = entry3.getValue();
                if (commandSyntaxException4.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                    ++integer1;
                }
                else {
                    this.g.add(commandSyntaxException4.getMessage());
                }
            }
            if (integer1 > 0) {
                this.g.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
        }
        this.h = 0;
        this.i = this.width;
        if (this.g.isEmpty()) {
            this.a(TextFormat.h);
        }
        this.l = null;
        if (this.minecraft.options.autoSuggestions) {
            this.f();
        }
    }
    
    private String a(final String string, final int integer) {
        if (this.parsedCommand != null) {
            return ChatScreen.getRenderText(this.parsedCommand, string, integer);
        }
        return string;
    }
    
    private void a(final TextFormat textFormat) {
        final CommandContextBuilder<CommandSource> commandContextBuilder2 = (CommandContextBuilder<CommandSource>)this.parsedCommand.getContext();
        final SuggestionContext<CommandSource> suggestionContext3 = (SuggestionContext<CommandSource>)commandContextBuilder2.findSuggestionContext(this.consoleCommandTextField.getCursor());
        final Map<CommandNode<CommandSource>, String> map4 = (Map<CommandNode<CommandSource>, String>)this.minecraft.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext3.parent, this.minecraft.player.networkHandler.getCommandSource());
        final List<String> list5 = Lists.newArrayList();
        int integer6 = 0;
        for (final Map.Entry<CommandNode<CommandSource>, String> entry8 : map4.entrySet()) {
            if (!(entry8.getKey() instanceof LiteralCommandNode)) {
                list5.add(textFormat + entry8.getValue());
                integer6 = Math.max(integer6, this.font.getStringWidth(entry8.getValue()));
            }
        }
        if (!list5.isEmpty()) {
            this.g.addAll(list5);
            this.h = MathHelper.clamp(this.consoleCommandTextField.k(suggestionContext3.startPos), 0, this.consoleCommandTextField.k(0) + this.consoleCommandTextField.h() - integer6);
            this.i = integer6;
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
        this.drawString(this.font, I18n.translate("advMode.command"), this.width / 2 - 150, 40, 10526880);
        this.consoleCommandTextField.render(mouseX, mouseY, delta);
        int integer4 = 75;
        if (!this.previousOutputTextField.getText().isEmpty()) {
            final int n = integer4;
            final int n2 = 5;
            this.font.getClass();
            integer4 = n + (n2 * 9 + 1 + this.b() - 135);
            this.drawString(this.font, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, integer4 + 4, 10526880);
            this.previousOutputTextField.render(mouseX, mouseY, delta);
        }
        super.render(mouseX, mouseY, delta);
        if (this.l != null) {
            this.l.a(mouseX, mouseY);
        }
        else {
            integer4 = 0;
            for (final String string6 : this.g) {
                DrawableHelper.fill(this.h - 1, 72 + 12 * integer4, this.h + this.i + 1, 84 + 12 * integer4, Integer.MIN_VALUE);
                this.font.drawWithShadow(string6, (float)this.h, (float)(74 + 12 * integer4), -1);
                ++integer4;
            }
        }
    }
    
    public void f() {
        if (this.k != null && this.k.isDone()) {
            final Suggestions suggestions1 = this.k.join();
            if (!suggestions1.isEmpty()) {
                int integer2 = 0;
                for (final Suggestion suggestion4 : suggestions1.getList()) {
                    integer2 = Math.max(integer2, this.font.getStringWidth(suggestion4.getText()));
                }
                final int integer3 = MathHelper.clamp(this.consoleCommandTextField.k(suggestions1.getRange().getStart()), 0, this.consoleCommandTextField.k(0) + this.consoleCommandTextField.h() - integer2);
                this.l = new a(integer3, 72, integer2, suggestions1);
            }
        }
    }
    
    protected void setCommand(final String command) {
        this.consoleCommandTextField.setText(command);
    }
    
    @Nullable
    private static String b(final String string1, final String string2) {
        if (string2.startsWith(string1)) {
            return string2.substring(string1.length());
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    class a
    {
        private final Rect2i b;
        private final Suggestions c;
        private final String d;
        private int e;
        private int f;
        private Vec2f g;
        private boolean h;
        
        private a(final int integer2, final int integer3, final int integer4, final Suggestions suggestions) {
            this.g = Vec2f.ZERO;
            this.b = new Rect2i(integer2 - 1, integer3, integer4 + 1, Math.min(suggestions.getList().size(), 7) * 12);
            this.c = suggestions;
            this.d = AbstractCommandBlockScreen.this.consoleCommandTextField.getText();
            this.b(0);
        }
        
        public void a(final int integer1, final int integer2) {
            final int integer3 = Math.min(this.c.getList().size(), 7);
            final int integer4 = Integer.MIN_VALUE;
            final int integer5 = -5592406;
            final boolean boolean6 = this.e > 0;
            final boolean boolean7 = this.c.getList().size() > this.e + integer3;
            final boolean boolean8 = boolean6 || boolean7;
            final boolean boolean9 = this.g.x != integer1 || this.g.y != integer2;
            if (boolean9) {
                this.g = new Vec2f((float)integer1, (float)integer2);
            }
            if (boolean8) {
                DrawableHelper.fill(this.b.getX(), this.b.getY() - 1, this.b.getX() + this.b.getWidth(), this.b.getY(), Integer.MIN_VALUE);
                DrawableHelper.fill(this.b.getX(), this.b.getY() + this.b.getHeight(), this.b.getX() + this.b.getWidth(), this.b.getY() + this.b.getHeight() + 1, Integer.MIN_VALUE);
                if (boolean6) {
                    for (int integer6 = 0; integer6 < this.b.getWidth(); ++integer6) {
                        if (integer6 % 2 == 0) {
                            DrawableHelper.fill(this.b.getX() + integer6, this.b.getY() - 1, this.b.getX() + integer6 + 1, this.b.getY(), -1);
                        }
                    }
                }
                if (boolean7) {
                    for (int integer6 = 0; integer6 < this.b.getWidth(); ++integer6) {
                        if (integer6 % 2 == 0) {
                            DrawableHelper.fill(this.b.getX() + integer6, this.b.getY() + this.b.getHeight(), this.b.getX() + integer6 + 1, this.b.getY() + this.b.getHeight() + 1, -1);
                        }
                    }
                }
            }
            boolean boolean10 = false;
            for (int integer7 = 0; integer7 < integer3; ++integer7) {
                final Suggestion suggestion12 = this.c.getList().get(integer7 + this.e);
                DrawableHelper.fill(this.b.getX(), this.b.getY() + 12 * integer7, this.b.getX() + this.b.getWidth(), this.b.getY() + 12 * integer7 + 12, Integer.MIN_VALUE);
                if (integer1 > this.b.getX() && integer1 < this.b.getX() + this.b.getWidth() && integer2 > this.b.getY() + 12 * integer7 && integer2 < this.b.getY() + 12 * integer7 + 12) {
                    if (boolean9) {
                        this.b(integer7 + this.e);
                    }
                    boolean10 = true;
                }
                AbstractCommandBlockScreen.this.font.drawWithShadow(suggestion12.getText(), (float)(this.b.getX() + 1), (float)(this.b.getY() + 2 + 12 * integer7), (integer7 + this.e == this.f) ? -256 : -5592406);
            }
            if (boolean10) {
                final Message message11 = this.c.getList().get(this.f).getTooltip();
                if (message11 != null) {
                    AbstractCommandBlockScreen.this.renderTooltip(TextFormatter.message(message11).getFormattedText(), integer1, integer2);
                }
            }
        }
        
        public boolean a(final int integer1, final int integer2, final int integer3) {
            if (!this.b.contains(integer1, integer2)) {
                return false;
            }
            final int integer4 = (integer2 - this.b.getY()) / 12 + this.e;
            if (integer4 >= 0 && integer4 < this.c.getList().size()) {
                this.b(integer4);
                this.a();
            }
            return true;
        }
        
        public boolean a(final double double1) {
            final int integer3 = (int)(AbstractCommandBlockScreen.this.minecraft.mouse.getX() * AbstractCommandBlockScreen.this.minecraft.window.getScaledWidth() / AbstractCommandBlockScreen.this.minecraft.window.getWidth());
            final int integer4 = (int)(AbstractCommandBlockScreen.this.minecraft.mouse.getY() * AbstractCommandBlockScreen.this.minecraft.window.getScaledHeight() / AbstractCommandBlockScreen.this.minecraft.window.getHeight());
            if (this.b.contains(integer3, integer4)) {
                this.e = MathHelper.clamp((int)(this.e - double1), 0, Math.max(this.c.getList().size() - 7, 0));
                return true;
            }
            return false;
        }
        
        public boolean b(final int integer1, final int integer2, final int integer3) {
            if (integer1 == 265) {
                this.a(-1);
                this.h = false;
                return true;
            }
            if (integer1 == 264) {
                this.a(1);
                this.h = false;
                return true;
            }
            if (integer1 == 258) {
                if (this.h) {
                    this.a(Screen.hasShiftDown() ? -1 : 1);
                }
                this.a();
                return true;
            }
            if (integer1 == 256) {
                this.b();
                return true;
            }
            return false;
        }
        
        public void a(final int integer) {
            this.b(this.f + integer);
            final int integer2 = this.e;
            final int integer3 = this.e + 7 - 1;
            if (this.f < integer2) {
                this.e = MathHelper.clamp(this.f, 0, Math.max(this.c.getList().size() - 7, 0));
            }
            else if (this.f > integer3) {
                this.e = MathHelper.clamp(this.f - 7, 0, Math.max(this.c.getList().size() - 7, 0));
            }
        }
        
        public void b(final int integer) {
            this.f = integer;
            if (this.f < 0) {
                this.f += this.c.getList().size();
            }
            if (this.f >= this.c.getList().size()) {
                this.f -= this.c.getList().size();
            }
            final Suggestion suggestion2 = this.c.getList().get(this.f);
            AbstractCommandBlockScreen.this.consoleCommandTextField.setSuggestion(b(AbstractCommandBlockScreen.this.consoleCommandTextField.getText(), suggestion2.apply(this.d)));
        }
        
        public void a() {
            final Suggestion suggestion1 = this.c.getList().get(this.f);
            AbstractCommandBlockScreen.this.suggestionsDisabled = true;
            AbstractCommandBlockScreen.this.setCommand(suggestion1.apply(this.d));
            final int integer2 = suggestion1.getRange().getStart() + suggestion1.getText().length();
            AbstractCommandBlockScreen.this.consoleCommandTextField.setCursor(integer2);
            AbstractCommandBlockScreen.this.consoleCommandTextField.j(integer2);
            this.b(this.f);
            AbstractCommandBlockScreen.this.suggestionsDisabled = false;
            this.h = true;
        }
        
        public void b() {
            AbstractCommandBlockScreen.this.l = null;
        }
    }
}
