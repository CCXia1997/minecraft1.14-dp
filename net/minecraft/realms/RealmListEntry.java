package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.menu.AlwaysSelectedEntryListWidget;

@Environment(EnvType.CLIENT)
public abstract class RealmListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmListEntry>
{
    @Override
    public abstract void render(final int arg1, final int arg2, final int arg3, final int arg4, final int arg5, final int arg6, final int arg7, final boolean arg8, final float arg9);
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return false;
    }
}
