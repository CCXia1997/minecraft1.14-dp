package net.minecraft.client.gui.widget;

import net.minecraft.client.gui.Element;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.options.GameOptions;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends ElementListWidget<ButtonItem>
{
    public ButtonListWidget(final MinecraftClient client, final int width, final int height, final int top, final int bottom, final int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.centerListVertically = false;
    }
    
    public int a(final GameOption gameOption) {
        return this.addEntry(ButtonItem.a(this.minecraft.options, this.width, gameOption));
    }
    
    public void a(final GameOption gameOption1, @Nullable final GameOption gameOption2) {
        this.addEntry(ButtonItem.a(this.minecraft.options, this.width, gameOption1, gameOption2));
    }
    
    public void addAll(final GameOption[] arr) {
        for (int integer2 = 0; integer2 < arr.length; integer2 += 2) {
            this.a(arr[integer2], (integer2 < arr.length - 1) ? arr[integer2 + 1] : null);
        }
    }
    
    @Override
    public int getRowWidth() {
        return 400;
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }
    
    @Environment(EnvType.CLIENT)
    public static class ButtonItem extends Entry<ButtonItem>
    {
        private final List<AbstractButtonWidget> buttons;
        
        private ButtonItem(final List<AbstractButtonWidget> list) {
            this.buttons = list;
        }
        
        public static ButtonItem a(final GameOptions gameOptions, final int integer, final GameOption gameOption) {
            return new ButtonItem(ImmutableList.<AbstractButtonWidget>of(gameOption.createOptionButton(gameOptions, integer / 2 - 155, 0, 310)));
        }
        
        public static ButtonItem a(final GameOptions gameOptions, final int integer, final GameOption gameOption3, @Nullable final GameOption gameOption4) {
            final AbstractButtonWidget abstractButtonWidget5 = gameOption3.createOptionButton(gameOptions, integer / 2 - 155, 0, 150);
            if (gameOption4 == null) {
                return new ButtonItem(ImmutableList.<AbstractButtonWidget>of(abstractButtonWidget5));
            }
            return new ButtonItem(ImmutableList.<AbstractButtonWidget>of(abstractButtonWidget5, gameOption4.createOptionButton(gameOptions, integer / 2 - 155 + 160, 0, 150)));
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            this.buttons.forEach(abstractButtonWidget -> {
                abstractButtonWidget.y = integer2;
                abstractButtonWidget.render(mouseX, mouseY, delta);
            });
        }
        
        @Override
        public List<? extends Element> children() {
            return this.buttons;
        }
    }
}
