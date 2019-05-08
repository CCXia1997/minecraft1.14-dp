package net.minecraft.text;

import java.util.Objects;
import com.google.common.collect.Streams;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;

public abstract class AbstractTextComponent implements TextComponent
{
    protected final List<TextComponent> siblings;
    private Style style;
    
    public AbstractTextComponent() {
        this.siblings = Lists.newArrayList();
    }
    
    @Override
    public TextComponent append(final TextComponent textComponent) {
        textComponent.getStyle().setParent(this.getStyle());
        this.siblings.add(textComponent);
        return this;
    }
    
    @Override
    public List<TextComponent> getSiblings() {
        return this.siblings;
    }
    
    @Override
    public TextComponent setStyle(final Style style) {
        this.style = style;
        for (final TextComponent textComponent3 : this.siblings) {
            textComponent3.getStyle().setParent(this.getStyle());
        }
        return this;
    }
    
    @Override
    public Style getStyle() {
        if (this.style == null) {
            this.style = new Style();
            for (final TextComponent textComponent2 : this.siblings) {
                textComponent2.getStyle().setParent(this.style);
            }
        }
        return this.style;
    }
    
    @Override
    public Stream<TextComponent> stream() {
        return Streams.<TextComponent>concat(new Stream[] { Stream.<AbstractTextComponent>of(this), this.siblings.stream().flatMap(TextComponent::stream) });
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof AbstractTextComponent) {
            final AbstractTextComponent abstractTextComponent2 = (AbstractTextComponent)o;
            return this.siblings.equals(abstractTextComponent2.siblings) && this.getStyle().equals(abstractTextComponent2.getStyle());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getStyle(), this.siblings);
    }
    
    @Override
    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
