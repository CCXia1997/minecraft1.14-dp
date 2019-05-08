package net.minecraft.client.gui.ingame;

import com.mojang.brigadier.Message;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.Vec2f;
import net.minecraft.client.util.Rect2i;
import javax.annotation.Nullable;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.TextComponent;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import net.minecraft.text.TextFormat;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import java.util.Collection;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.StringReader;
import java.util.regex.Matcher;
import com.google.common.base.Strings;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import com.google.common.collect.Lists;
import net.minecraft.client.util.NarratorManager;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.ParseResults;
import java.util.List;
import net.minecraft.client.gui.widget.TextFieldWidget;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class ChatScreen extends Screen
{
    private static final Pattern WHITESPACE_PATTERN;
    private String f;
    private int g;
    protected TextFieldWidget chatField;
    private String h;
    protected final List<String> b;
    protected int commandExceptionsX;
    protected int commandExceptionsWidth;
    private ParseResults<CommandSource> parseResults;
    private CompletableFuture<Suggestions> suggestionsFuture;
    private SuggestionWindow suggestionsWindow;
    private boolean l;
    private boolean suggestionsTemporarilyDisabled;
    
    public ChatScreen(final String string) {
        super(NarratorManager.a);
        this.f = "";
        this.g = -1;
        this.h = "";
        this.b = Lists.newArrayList();
        this.h = string;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.g = this.minecraft.inGameHud.getChatHud().b().size();
        (this.chatField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.translate("chat.editBox"))).setMaxLength(256);
        this.chatField.setHasBorder(false);
        this.chatField.setText(this.h);
        this.chatField.setRenderTextProvider(this::getRenderText);
        this.chatField.setChangedListener(this::onChatFieldChanged);
        this.children.add(this.chatField);
        this.updateCommand();
        this.setInitialFocus(this.chatField);
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.chatField.getText();
        this.init(client, width, height);
        this.setText(string4);
        this.updateCommand();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        this.minecraft.inGameHud.getChatHud().c();
    }
    
    @Override
    public void tick() {
        this.chatField.tick();
    }
    
    private void onChatFieldChanged(final String string) {
        final String string2 = this.chatField.getText();
        this.l = !string2.equals(this.h);
        this.updateCommand();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (this.suggestionsWindow != null && this.suggestionsWindow.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 258) {
            this.l = true;
            this.openSuggestionsWindow();
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) {
            this.minecraft.openScreen(null);
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            final String string4 = this.chatField.getText().trim();
            if (!string4.isEmpty()) {
                this.sendMessage(string4);
            }
            this.minecraft.openScreen(null);
            return true;
        }
        if (keyCode == 265) {
            this.a(-1);
            return true;
        }
        if (keyCode == 264) {
            this.a(1);
            return true;
        }
        if (keyCode == 266) {
            this.minecraft.inGameHud.getChatHud().a(this.minecraft.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        }
        if (keyCode == 267) {
            this.minecraft.inGameHud.getChatHud().a(-this.minecraft.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        }
        return false;
    }
    
    public void openSuggestionsWindow() {
        if (this.suggestionsFuture != null && this.suggestionsFuture.isDone()) {
            int integer1 = 0;
            final Suggestions suggestions2 = this.suggestionsFuture.join();
            if (!suggestions2.getList().isEmpty()) {
                for (final Suggestion suggestion4 : suggestions2.getList()) {
                    integer1 = Math.max(integer1, this.font.getStringWidth(suggestion4.getText()));
                }
                final int integer2 = MathHelper.clamp(this.chatField.k(suggestions2.getRange().getStart()), 0, this.width - integer1);
                this.suggestionsWindow = new SuggestionWindow(integer2, this.height - 12, integer1, suggestions2);
            }
        }
    }
    
    private static int getLastWhitespaceIndex(final String string) {
        if (Strings.isNullOrEmpty(string)) {
            return 0;
        }
        int integer2 = 0;
        final Matcher matcher3 = ChatScreen.WHITESPACE_PATTERN.matcher(string);
        while (matcher3.find()) {
            integer2 = matcher3.end();
        }
        return integer2;
    }
    
    private void updateCommand() {
        final String string1 = this.chatField.getText();
        if (this.parseResults != null && !this.parseResults.getReader().getString().equals(string1)) {
            this.parseResults = null;
        }
        if (!this.suggestionsTemporarilyDisabled) {
            this.chatField.setSuggestion(null);
            this.suggestionsWindow = null;
        }
        this.b.clear();
        final StringReader stringReader2 = new StringReader(string1);
        if (stringReader2.canRead() && stringReader2.peek() == '/') {
            stringReader2.skip();
            final CommandDispatcher<CommandSource> commandDispatcher3 = this.minecraft.player.networkHandler.getCommandDispatcher();
            if (this.parseResults == null) {
                this.parseResults = (ParseResults<CommandSource>)commandDispatcher3.parse(stringReader2, this.minecraft.player.networkHandler.getCommandSource());
            }
            final int integer4 = this.chatField.getCursor();
            if (integer4 >= 1 && (this.suggestionsWindow == null || !this.suggestionsTemporarilyDisabled)) {
                (this.suggestionsFuture = (CompletableFuture<Suggestions>)commandDispatcher3.getCompletionSuggestions((ParseResults)this.parseResults, integer4)).thenRun(() -> {
                    if (!(!this.suggestionsFuture.isDone())) {
                        this.updateSuggestionsAndExceptions();
                    }
                });
            }
        }
        else {
            final String string2 = string1;
            final int integer4 = getLastWhitespaceIndex(string2);
            final Collection<String> collection5 = this.minecraft.player.networkHandler.getCommandSource().getPlayerNames();
            this.suggestionsFuture = CommandSource.suggestMatching(collection5, new SuggestionsBuilder(string2, integer4));
        }
    }
    
    private void updateSuggestionsAndExceptions() {
        if (this.suggestionsFuture.join().isEmpty() && !this.parseResults.getExceptions().isEmpty() && this.chatField.getCursor() == this.chatField.getText().length()) {
            int integer1 = 0;
            for (final Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> entry3 : this.parseResults.getExceptions().entrySet()) {
                final CommandSyntaxException commandSyntaxException4 = entry3.getValue();
                if (commandSyntaxException4.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                    ++integer1;
                }
                else {
                    this.b.add(commandSyntaxException4.getMessage());
                }
            }
            if (integer1 > 0) {
                this.b.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
        }
        this.commandExceptionsX = 0;
        this.commandExceptionsWidth = this.width;
        if (this.b.isEmpty()) {
            this.a(TextFormat.h);
        }
        this.suggestionsWindow = null;
        if (this.l && this.minecraft.options.autoSuggestions) {
            this.openSuggestionsWindow();
        }
    }
    
    private String getRenderText(final String string, final int cursorPosition) {
        if (this.parseResults != null) {
            return getRenderText(this.parseResults, string, cursorPosition);
        }
        return string;
    }
    
    public static String getRenderText(final ParseResults<CommandSource> parseResults, final String typedText, final int cursorPosition) {
        final TextFormat[] arr4 = { TextFormat.l, TextFormat.o, TextFormat.k, TextFormat.n, TextFormat.g };
        final String string5 = TextFormat.h.toString();
        final StringBuilder stringBuilder6 = new StringBuilder(string5);
        int integer7 = 0;
        int integer8 = -1;
        final CommandContextBuilder<CommandSource> commandContextBuilder9 = (CommandContextBuilder<CommandSource>)parseResults.getContext().getLastChild();
        for (final ParsedArgument<CommandSource, ?> parsedArgument11 : commandContextBuilder9.getArguments().values()) {
            if (++integer8 >= arr4.length) {
                integer8 = 0;
            }
            final int integer9 = Math.max(parsedArgument11.getRange().getStart() - cursorPosition, 0);
            if (integer9 >= typedText.length()) {
                break;
            }
            final int integer10 = Math.min(parsedArgument11.getRange().getEnd() - cursorPosition, typedText.length());
            if (integer10 <= 0) {
                continue;
            }
            stringBuilder6.append(typedText, integer7, integer9);
            stringBuilder6.append(arr4[integer8]);
            stringBuilder6.append(typedText, integer9, integer10);
            stringBuilder6.append(string5);
            integer7 = integer10;
        }
        if (parseResults.getReader().canRead()) {
            final int integer11 = Math.max(parseResults.getReader().getCursor() - cursorPosition, 0);
            if (integer11 < typedText.length()) {
                final int integer12 = Math.min(integer11 + parseResults.getReader().getRemainingLength(), typedText.length());
                stringBuilder6.append(typedText, integer7, integer11);
                stringBuilder6.append(TextFormat.m);
                stringBuilder6.append(typedText, integer11, integer12);
                integer7 = integer12;
            }
        }
        stringBuilder6.append(typedText, integer7, typedText.length());
        return stringBuilder6.toString();
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, double amount) {
        if (amount > 1.0) {
            amount = 1.0;
        }
        if (amount < -1.0) {
            amount = -1.0;
        }
        if (this.suggestionsWindow != null && this.suggestionsWindow.a(amount)) {
            return true;
        }
        if (!Screen.hasShiftDown()) {
            amount *= 7.0;
        }
        this.minecraft.inGameHud.getChatHud().a(amount);
        return true;
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (this.suggestionsWindow != null && this.suggestionsWindow.a((int)mouseX, (int)mouseY, button)) {
            return true;
        }
        if (button == 0) {
            final TextComponent textComponent6 = this.minecraft.inGameHud.getChatHud().getTextComponentAt(mouseX, mouseY);
            if (textComponent6 != null && this.handleComponentClicked(textComponent6)) {
                return true;
            }
        }
        return this.chatField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void insertText(final String text, final boolean boolean2) {
        if (boolean2) {
            this.chatField.setText(text);
        }
        else {
            this.chatField.addText(text);
        }
    }
    
    public void a(final int integer) {
        int integer2 = this.g + integer;
        final int integer3 = this.minecraft.inGameHud.getChatHud().b().size();
        integer2 = MathHelper.clamp(integer2, 0, integer3);
        if (integer2 == this.g) {
            return;
        }
        if (integer2 == integer3) {
            this.g = integer3;
            this.chatField.setText(this.f);
            return;
        }
        if (this.g == integer3) {
            this.f = this.chatField.getText();
        }
        this.chatField.setText(this.minecraft.inGameHud.getChatHud().b().get(integer2));
        this.suggestionsWindow = null;
        this.g = integer2;
        this.l = false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.setFocused(this.chatField);
        this.chatField.a(true);
        DrawableHelper.fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.chatField.render(mouseX, mouseY, delta);
        if (this.suggestionsWindow != null) {
            this.suggestionsWindow.draw(mouseX, mouseY);
        }
        else {
            int integer4 = 0;
            for (final String string6 : this.b) {
                DrawableHelper.fill(this.commandExceptionsX - 1, this.height - 14 - 13 - 12 * integer4, this.commandExceptionsX + this.commandExceptionsWidth + 1, this.height - 2 - 13 - 12 * integer4, -16777216);
                this.font.drawWithShadow(string6, (float)this.commandExceptionsX, (float)(this.height - 14 - 13 + 2 - 12 * integer4), -1);
                ++integer4;
            }
        }
        final TextComponent textComponent4 = this.minecraft.inGameHud.getChatHud().getTextComponentAt(mouseX, mouseY);
        if (textComponent4 != null && textComponent4.getStyle().getHoverEvent() != null) {
            this.renderComponentHoverEffect(textComponent4, mouseX, mouseY);
        }
        super.render(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void a(final TextFormat textFormat) {
        final CommandContextBuilder<CommandSource> commandContextBuilder2 = (CommandContextBuilder<CommandSource>)this.parseResults.getContext();
        final SuggestionContext<CommandSource> suggestionContext3 = (SuggestionContext<CommandSource>)commandContextBuilder2.findSuggestionContext(this.chatField.getCursor());
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
            this.b.addAll(list5);
            this.commandExceptionsX = MathHelper.clamp(this.chatField.k(suggestionContext3.startPos), 0, this.width - integer6);
            this.commandExceptionsWidth = integer6;
        }
    }
    
    @Nullable
    private static String b(final String string1, final String string2) {
        if (string2.startsWith(string1)) {
            return string2.substring(string1.length());
        }
        return null;
    }
    
    private void setText(final String text) {
        this.chatField.setText(text);
    }
    
    static {
        WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    }
    
    @Environment(EnvType.CLIENT)
    class SuggestionWindow
    {
        private final Rect2i b;
        private final Suggestions suggestions;
        private final String typedText;
        private int e;
        private int selectedSuggestionIndex;
        private Vec2f lastMousePos;
        private boolean h;
        
        private SuggestionWindow(final int integer2, final int integer3, final int integer4, final Suggestions suggestions) {
            this.lastMousePos = Vec2f.ZERO;
            this.b = new Rect2i(integer2 - 1, integer3 - 3 - Math.min(suggestions.getList().size(), 10) * 12, integer4 + 1, Math.min(suggestions.getList().size(), 10) * 12);
            this.suggestions = suggestions;
            this.typedText = ChatScreen.this.chatField.getText();
            this.setSelectedSuggestionIndex(0);
        }
        
        public void draw(final int mouseX, final int mouseY) {
            final int integer3 = Math.min(this.suggestions.getList().size(), 10);
            final int integer4 = -5592406;
            final boolean boolean5 = this.e > 0;
            final boolean boolean6 = this.suggestions.getList().size() > this.e + integer3;
            final boolean boolean7 = boolean5 || boolean6;
            final boolean boolean8 = this.lastMousePos.x != mouseX || this.lastMousePos.y != mouseY;
            if (boolean8) {
                this.lastMousePos = new Vec2f((float)mouseX, (float)mouseY);
            }
            if (boolean7) {
                DrawableHelper.fill(this.b.getX(), this.b.getY() - 1, this.b.getX() + this.b.getWidth(), this.b.getY(), -805306368);
                DrawableHelper.fill(this.b.getX(), this.b.getY() + this.b.getHeight(), this.b.getX() + this.b.getWidth(), this.b.getY() + this.b.getHeight() + 1, -805306368);
                if (boolean5) {
                    for (int integer5 = 0; integer5 < this.b.getWidth(); ++integer5) {
                        if (integer5 % 2 == 0) {
                            DrawableHelper.fill(this.b.getX() + integer5, this.b.getY() - 1, this.b.getX() + integer5 + 1, this.b.getY(), -1);
                        }
                    }
                }
                if (boolean6) {
                    for (int integer5 = 0; integer5 < this.b.getWidth(); ++integer5) {
                        if (integer5 % 2 == 0) {
                            DrawableHelper.fill(this.b.getX() + integer5, this.b.getY() + this.b.getHeight(), this.b.getX() + integer5 + 1, this.b.getY() + this.b.getHeight() + 1, -1);
                        }
                    }
                }
            }
            boolean boolean9 = false;
            for (int integer6 = 0; integer6 < integer3; ++integer6) {
                final Suggestion suggestion11 = this.suggestions.getList().get(integer6 + this.e);
                DrawableHelper.fill(this.b.getX(), this.b.getY() + 12 * integer6, this.b.getX() + this.b.getWidth(), this.b.getY() + 12 * integer6 + 12, -805306368);
                if (mouseX > this.b.getX() && mouseX < this.b.getX() + this.b.getWidth() && mouseY > this.b.getY() + 12 * integer6 && mouseY < this.b.getY() + 12 * integer6 + 12) {
                    if (boolean8) {
                        this.setSelectedSuggestionIndex(integer6 + this.e);
                    }
                    boolean9 = true;
                }
                ChatScreen.this.font.drawWithShadow(suggestion11.getText(), (float)(this.b.getX() + 1), (float)(this.b.getY() + 2 + 12 * integer6), (integer6 + this.e == this.selectedSuggestionIndex) ? -256 : -5592406);
            }
            if (boolean9) {
                final Message message10 = this.suggestions.getList().get(this.selectedSuggestionIndex).getTooltip();
                if (message10 != null) {
                    ChatScreen.this.renderTooltip(TextFormatter.message(message10).getFormattedText(), mouseX, mouseY);
                }
            }
        }
        
        public boolean a(final int integer1, final int integer2, final int integer3) {
            if (!this.b.contains(integer1, integer2)) {
                return false;
            }
            final int integer4 = (integer2 - this.b.getY()) / 12 + this.e;
            if (integer4 >= 0 && integer4 < this.suggestions.getList().size()) {
                this.setSelectedSuggestionIndex(integer4);
                this.useSuggestion();
            }
            return true;
        }
        
        public boolean a(final double double1) {
            final int integer3 = (int)(ChatScreen.this.minecraft.mouse.getX() * ChatScreen.this.minecraft.window.getScaledWidth() / ChatScreen.this.minecraft.window.getWidth());
            final int integer4 = (int)(ChatScreen.this.minecraft.mouse.getY() * ChatScreen.this.minecraft.window.getScaledHeight() / ChatScreen.this.minecraft.window.getHeight());
            if (this.b.contains(integer3, integer4)) {
                this.e = MathHelper.clamp((int)(this.e - double1), 0, Math.max(this.suggestions.getList().size() - 10, 0));
                return true;
            }
            return false;
        }
        
        public boolean handleKeyPress(final int keyCode, final int scanCode, final int modifiers) {
            if (keyCode == 265) {
                this.scrollSelectedSuggestion(-1);
                this.h = false;
                return true;
            }
            if (keyCode == 264) {
                this.scrollSelectedSuggestion(1);
                this.h = false;
                return true;
            }
            if (keyCode == 258) {
                if (this.h) {
                    this.scrollSelectedSuggestion(Screen.hasShiftDown() ? -1 : 1);
                }
                this.useSuggestion();
                return true;
            }
            if (keyCode == 256) {
                this.close();
                return true;
            }
            return false;
        }
        
        public void scrollSelectedSuggestion(final int offset) {
            this.setSelectedSuggestionIndex(this.selectedSuggestionIndex + offset);
            final int integer2 = this.e;
            final int integer3 = this.e + 10 - 1;
            if (this.selectedSuggestionIndex < integer2) {
                this.e = MathHelper.clamp(this.selectedSuggestionIndex, 0, Math.max(this.suggestions.getList().size() - 10, 0));
            }
            else if (this.selectedSuggestionIndex > integer3) {
                this.e = MathHelper.clamp(this.selectedSuggestionIndex + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
            }
        }
        
        public void setSelectedSuggestionIndex(final int selectedSuggestionIndex) {
            this.selectedSuggestionIndex = selectedSuggestionIndex;
            if (this.selectedSuggestionIndex < 0) {
                this.selectedSuggestionIndex += this.suggestions.getList().size();
            }
            if (this.selectedSuggestionIndex >= this.suggestions.getList().size()) {
                this.selectedSuggestionIndex -= this.suggestions.getList().size();
            }
            final Suggestion suggestion2 = this.suggestions.getList().get(this.selectedSuggestionIndex);
            ChatScreen.this.chatField.setSuggestion(b(ChatScreen.this.chatField.getText(), suggestion2.apply(this.typedText)));
        }
        
        public void useSuggestion() {
            final Suggestion suggestion1 = this.suggestions.getList().get(this.selectedSuggestionIndex);
            ChatScreen.this.suggestionsTemporarilyDisabled = true;
            ChatScreen.this.setText(suggestion1.apply(this.typedText));
            final int integer2 = suggestion1.getRange().getStart() + suggestion1.getText().length();
            ChatScreen.this.chatField.setCursor(integer2);
            ChatScreen.this.chatField.j(integer2);
            this.setSelectedSuggestionIndex(this.selectedSuggestionIndex);
            ChatScreen.this.suggestionsTemporarilyDisabled = false;
            this.h = true;
        }
        
        public void close() {
            ChatScreen.this.suggestionsWindow = null;
        }
    }
}
