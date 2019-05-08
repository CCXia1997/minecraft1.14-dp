package net.minecraft.text;

import com.mojang.brigadier.Message;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.function.Function;
import java.util.Collection;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;

public class TextFormatter
{
    public static TextComponent style(final TextComponent component, final Style style) {
        if (style.isEmpty()) {
            return component;
        }
        if (component.getStyle().isEmpty()) {
            return component.setStyle(style.clone());
        }
        return new StringTextComponent("").append(component).setStyle(style.clone());
    }
    
    public static TextComponent resolveAndStyle(@Nullable final ServerCommandSource source, final TextComponent component, @Nullable final Entity entity) throws CommandSyntaxException {
        final TextComponent textComponent4 = (component instanceof TextComponentWithSelectors) ? ((TextComponentWithSelectors)component).resolve(source, entity) : component.copyShallow();
        for (final TextComponent textComponent5 : component.getSiblings()) {
            textComponent4.append(resolveAndStyle(source, textComponent5, entity));
        }
        return style(textComponent4, component.getStyle());
    }
    
    public static TextComponent profile(final GameProfile gameProfile) {
        if (gameProfile.getName() != null) {
            return new StringTextComponent(gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            return new StringTextComponent(gameProfile.getId().toString());
        }
        return new StringTextComponent("(unknown)");
    }
    
    public static TextComponent sortedJoin(final Collection<String> collection) {
        return TextFormatter.<String>sortedJoin(collection, string -> new StringTextComponent(string).applyFormat(TextFormat.k));
    }
    
    public static <T extends Comparable<T>> TextComponent sortedJoin(final Collection<T> collection, final Function<T, TextComponent> function) {
        if (collection.isEmpty()) {
            return new StringTextComponent("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        final List<T> list3 = Lists.newArrayList(collection);
        list3.sort(Comparable::compareTo);
        return TextFormatter.join((Collection<Object>)collection, (Function<Object, TextComponent>)function);
    }
    
    public static <T> TextComponent join(final Collection<T> collection, final Function<T, TextComponent> function) {
        if (collection.isEmpty()) {
            return new StringTextComponent("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        final TextComponent textComponent3 = new StringTextComponent("");
        boolean boolean4 = true;
        for (final T object6 : collection) {
            if (!boolean4) {
                textComponent3.append(new StringTextComponent(", ").applyFormat(TextFormat.h));
            }
            textComponent3.append(function.apply(object6));
            boolean4 = false;
        }
        return textComponent3;
    }
    
    public static TextComponent bracketed(final TextComponent textComponent) {
        return new StringTextComponent("[").append(textComponent).append("]");
    }
    
    public static TextComponent message(final Message message) {
        if (message instanceof TextComponent) {
            return (TextComponent)message;
        }
        return new StringTextComponent(message.getString());
    }
}
