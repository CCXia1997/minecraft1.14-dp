package net.minecraft.client.util;

import com.google.common.collect.Lists;
import net.minecraft.text.StringTextComponent;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextComponentUtil
{
    public static String a(final String string, final boolean forceColor) {
        if (forceColor || MinecraftClient.getInstance().options.chatColors) {
            return string;
        }
        return TextFormat.stripFormatting(string);
    }
    
    public static List<TextComponent> wrapLines(final TextComponent text, final int width, final TextRenderer fontRenderer, final boolean boolean4, final boolean boolean5) {
        int integer6 = 0;
        TextComponent textComponent7 = new StringTextComponent("");
        final List<TextComponent> list8 = Lists.newArrayList();
        final List<TextComponent> list9 = Lists.newArrayList(text);
        for (int integer7 = 0; integer7 < list9.size(); ++integer7) {
            final TextComponent textComponent8 = list9.get(integer7);
            String string12 = textComponent8.getText();
            boolean boolean6 = false;
            if (string12.contains("\n")) {
                final int integer8 = string12.indexOf(10);
                final String string13 = string12.substring(integer8 + 1);
                string12 = string12.substring(0, integer8 + 1);
                final TextComponent textComponent9 = new StringTextComponent(string13).setStyle(textComponent8.getStyle().clone());
                list9.add(integer7 + 1, textComponent9);
                boolean6 = true;
            }
            String string14 = a(textComponent8.getStyle().getFormatString() + string12, boolean5);
            final String string13 = string14.endsWith("\n") ? string14.substring(0, string14.length() - 1) : string14;
            int integer9 = fontRenderer.getStringWidth(string13);
            TextComponent textComponent10 = new StringTextComponent(string13).setStyle(textComponent8.getStyle().clone());
            if (integer6 + integer9 > width) {
                String string15 = fontRenderer.trimToWidth(string14, width - integer6, false);
                String string16 = (string15.length() < string14.length()) ? string14.substring(string15.length()) : null;
                if (string16 != null && !string16.isEmpty()) {
                    int integer10 = (string16.charAt(0) != ' ') ? string15.lastIndexOf(32) : string15.length();
                    if (integer10 >= 0 && fontRenderer.getStringWidth(string14.substring(0, integer10)) > 0) {
                        string15 = string14.substring(0, integer10);
                        if (boolean4) {
                            ++integer10;
                        }
                        string16 = string14.substring(integer10);
                    }
                    else if (integer6 > 0 && !string14.contains(" ")) {
                        string15 = "";
                        string16 = string14;
                    }
                    final TextComponent textComponent11 = new StringTextComponent(string16).setStyle(textComponent8.getStyle().clone());
                    list9.add(integer7 + 1, textComponent11);
                }
                string14 = string15;
                integer9 = fontRenderer.getStringWidth(string14);
                textComponent10 = new StringTextComponent(string14);
                textComponent10.setStyle(textComponent8.getStyle().clone());
                boolean6 = true;
            }
            if (integer6 + integer9 <= width) {
                integer6 += integer9;
                textComponent7.append(textComponent10);
            }
            else {
                boolean6 = true;
            }
            if (boolean6) {
                list8.add(textComponent7);
                integer6 = 0;
                textComponent7 = new StringTextComponent("");
            }
        }
        list8.add(textComponent7);
        return list8;
    }
}
