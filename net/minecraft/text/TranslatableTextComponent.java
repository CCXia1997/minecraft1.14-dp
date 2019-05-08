package net.minecraft.text;

import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;
import java.util.function.Function;
import com.google.common.collect.Streams;
import java.util.stream.Stream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.IllegalFormatException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.regex.Pattern;
import java.util.List;
import net.minecraft.util.Language;

public class TranslatableTextComponent extends AbstractTextComponent implements TextComponentWithSelectors
{
    private static final Language EMPTY_LANGUAGE;
    private static final Language LANGUAGE;
    private final String key;
    private final Object[] params;
    private final Object lock;
    private long languageTimeLoaded;
    protected final List<TextComponent> translatedText;
    public static final Pattern PARAM_PATTERN;
    
    public TranslatableTextComponent(final String string, final Object... arr) {
        this.lock = new Object();
        this.languageTimeLoaded = -1L;
        this.translatedText = Lists.newArrayList();
        this.key = string;
        this.params = arr;
        for (int integer3 = 0; integer3 < arr.length; ++integer3) {
            final Object object4 = arr[integer3];
            if (object4 instanceof TextComponent) {
                final TextComponent textComponent5 = ((TextComponent)object4).copy();
                this.params[integer3] = textComponent5;
                textComponent5.getStyle().setParent(this.getStyle());
            }
            else if (object4 == null) {
                this.params[integer3] = "null";
            }
        }
    }
    
    @VisibleForTesting
    synchronized void updateTranslation() {
        synchronized (this.lock) {
            final long long2 = TranslatableTextComponent.LANGUAGE.getTimeLoaded();
            if (long2 == this.languageTimeLoaded) {
                return;
            }
            this.languageTimeLoaded = long2;
            this.translatedText.clear();
        }
        try {
            this.setTranslatedText(TranslatableTextComponent.LANGUAGE.translate(this.key));
        }
        catch (ComponentTranslationException componentTranslationException1) {
            this.translatedText.clear();
            try {
                this.setTranslatedText(TranslatableTextComponent.EMPTY_LANGUAGE.translate(this.key));
            }
            catch (ComponentTranslationException componentTranslationException2) {
                throw componentTranslationException1;
            }
        }
    }
    
    protected void setTranslatedText(final String text) {
        final Matcher matcher2 = TranslatableTextComponent.PARAM_PATTERN.matcher(text);
        try {
            int integer3 = 0;
            int integer4;
            int integer6;
            for (integer4 = 0; matcher2.find(integer4); integer4 = integer6) {
                final int integer5 = matcher2.start();
                integer6 = matcher2.end();
                if (integer5 > integer4) {
                    final TextComponent textComponent7 = new StringTextComponent(String.format(text.substring(integer4, integer5)));
                    textComponent7.getStyle().setParent(this.getStyle());
                    this.translatedText.add(textComponent7);
                }
                final String string7 = matcher2.group(2);
                final String string8 = text.substring(integer5, integer6);
                if ("%".equals(string7) && "%%".equals(string8)) {
                    final TextComponent textComponent8 = new StringTextComponent("%");
                    textComponent8.getStyle().setParent(this.getStyle());
                    this.translatedText.add(textComponent8);
                }
                else {
                    if (!"s".equals(string7)) {
                        throw new ComponentTranslationException(this, "Unsupported format: '" + string8 + "'");
                    }
                    final String string9 = matcher2.group(1);
                    final int integer7 = (string9 != null) ? (Integer.parseInt(string9) - 1) : integer3++;
                    if (integer7 < this.params.length) {
                        this.translatedText.add(this.getArgument(integer7));
                    }
                }
            }
            if (integer4 < text.length()) {
                final TextComponent textComponent9 = new StringTextComponent(String.format(text.substring(integer4)));
                textComponent9.getStyle().setParent(this.getStyle());
                this.translatedText.add(textComponent9);
            }
        }
        catch (IllegalFormatException illegalFormatException3) {
            throw new ComponentTranslationException(this, illegalFormatException3);
        }
    }
    
    private TextComponent getArgument(final int index) {
        if (index >= this.params.length) {
            throw new ComponentTranslationException(this, index);
        }
        final Object object2 = this.params[index];
        TextComponent textComponent3;
        if (object2 instanceof TextComponent) {
            textComponent3 = (TextComponent)object2;
        }
        else {
            textComponent3 = new StringTextComponent((object2 == null) ? "null" : object2.toString());
            textComponent3.getStyle().setParent(this.getStyle());
        }
        return textComponent3;
    }
    
    @Override
    public TextComponent setStyle(final Style style) {
        super.setStyle(style);
        for (final Object object5 : this.params) {
            if (object5 instanceof TextComponent) {
                ((TextComponent)object5).getStyle().setParent(this.getStyle());
            }
        }
        if (this.languageTimeLoaded > -1L) {
            for (final TextComponent textComponent3 : this.translatedText) {
                textComponent3.getStyle().setParent(style);
            }
        }
        return this;
    }
    
    @Override
    public Stream<TextComponent> stream() {
        this.updateTranslation();
        return Streams.concat(new Stream[] { this.translatedText.stream(), this.siblings.stream() }).<TextComponent>flatMap(TextComponent::stream);
    }
    
    @Override
    public String getText() {
        this.updateTranslation();
        final StringBuilder stringBuilder1 = new StringBuilder();
        for (final TextComponent textComponent3 : this.translatedText) {
            stringBuilder1.append(textComponent3.getText());
        }
        return stringBuilder1.toString();
    }
    
    @Override
    public TranslatableTextComponent copyShallow() {
        final Object[] arr1 = new Object[this.params.length];
        for (int integer2 = 0; integer2 < this.params.length; ++integer2) {
            if (this.params[integer2] instanceof TextComponent) {
                arr1[integer2] = ((TextComponent)this.params[integer2]).copy();
            }
            else {
                arr1[integer2] = this.params[integer2];
            }
        }
        return new TranslatableTextComponent(this.key, arr1);
    }
    
    @Override
    public TextComponent resolve(@Nullable final ServerCommandSource source, @Nullable final Entity entity) throws CommandSyntaxException {
        final Object[] arr3 = new Object[this.params.length];
        for (int integer4 = 0; integer4 < arr3.length; ++integer4) {
            final Object object5 = this.params[integer4];
            if (object5 instanceof TextComponent) {
                arr3[integer4] = TextFormatter.resolveAndStyle(source, (TextComponent)object5, entity);
            }
            else {
                arr3[integer4] = object5;
            }
        }
        return new TranslatableTextComponent(this.key, arr3);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TranslatableTextComponent) {
            final TranslatableTextComponent translatableTextComponent2 = (TranslatableTextComponent)o;
            return Arrays.equals(this.params, translatableTextComponent2.params) && this.key.equals(translatableTextComponent2.key) && super.equals(o);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int integer1 = super.hashCode();
        integer1 = 31 * integer1 + this.key.hashCode();
        integer1 = 31 * integer1 + Arrays.hashCode(this.params);
        return integer1;
    }
    
    @Override
    public String toString() {
        return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.params) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
    
    public String getKey() {
        return this.key;
    }
    
    public Object[] getParams() {
        return this.params;
    }
    
    static {
        EMPTY_LANGUAGE = new Language();
        LANGUAGE = Language.getInstance();
        PARAM_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    }
}
