package net.minecraft.client.gui.menu.options;

import net.minecraft.resource.ResourcePackContainerManager;
import java.util.Iterator;
import net.minecraft.client.resource.ClientResourcePackContainer;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import net.minecraft.client.gui.widget.ResourcePackListWidget;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.AvailableResourcePackListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class ResourcePackOptionsScreen extends Screen
{
    private final Screen parent;
    private AvailableResourcePackListWidget availableList;
    private SelectedResourcePackListWidget selectedList;
    private boolean d;
    
    public ResourcePackOptionsScreen(final Screen parent) {
        super(new TranslatableTextComponent("resourcePack.title", new Object[0]));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, I18n.translate("resourcePack.openFolder"), buttonWidget -> SystemUtil.getOperatingSystem().open(this.minecraft.getResourcePackDir())));
        List<ClientResourcePackContainer> list2;
        final Iterator<ResourcePackListWidget.ResourcePackItem> iterator;
        ResourcePackListWidget.ResourcePackItem resourcePackItem4;
        final Iterator<ClientResourcePackContainer> iterator2;
        ClientResourcePackContainer clientResourcePackContainer4;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
            if (this.d) {
                list2 = Lists.newArrayList();
                this.selectedList.children().iterator();
                while (iterator.hasNext()) {
                    resourcePackItem4 = iterator.next();
                    list2.add(resourcePackItem4.e());
                }
                Collections.reverse(list2);
                this.minecraft.I().setEnabled(list2);
                this.minecraft.options.resourcePacks.clear();
                this.minecraft.options.incompatibleResourcePacks.clear();
                list2.iterator();
                while (iterator2.hasNext()) {
                    clientResourcePackContainer4 = iterator2.next();
                    if (!clientResourcePackContainer4.sortsTillEnd()) {
                        this.minecraft.options.resourcePacks.add(clientResourcePackContainer4.getName());
                        if (!clientResourcePackContainer4.getCompatibility().isCompatible()) {
                            this.minecraft.options.incompatibleResourcePacks.add(clientResourcePackContainer4.getName());
                        }
                        else {
                            continue;
                        }
                    }
                }
                this.minecraft.options.write();
                this.minecraft.openScreen(this.parent);
                this.minecraft.reloadResources();
            }
            else {
                this.minecraft.openScreen(this.parent);
            }
            return;
        }));
        final AvailableResourcePackListWidget availableResourcePackListWidget1 = this.availableList;
        final SelectedResourcePackListWidget selectedResourcePackListWidget2 = this.selectedList;
        (this.availableList = new AvailableResourcePackListWidget(this.minecraft, 200, this.height)).setLeftPos(this.width / 2 - 4 - 200);
        if (availableResourcePackListWidget1 != null) {
            this.availableList.children().addAll(availableResourcePackListWidget1.children());
        }
        this.children.add(this.availableList);
        (this.selectedList = new SelectedResourcePackListWidget(this.minecraft, 200, this.height)).setLeftPos(this.width / 2 + 4);
        if (selectedResourcePackListWidget2 != null) {
            this.selectedList.children().addAll(selectedResourcePackListWidget2.children());
        }
        this.children.add(this.selectedList);
        if (!this.d) {
            this.availableList.children().clear();
            this.selectedList.children().clear();
            final ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager3 = this.minecraft.I();
            resourcePackContainerManager3.callCreators();
            final List<ClientResourcePackContainer> list3 = Lists.newArrayList(resourcePackContainerManager3.getAlphabeticallyOrderedContainers());
            list3.removeAll(resourcePackContainerManager3.getEnabledContainers());
            for (final ClientResourcePackContainer clientResourcePackContainer5 : list3) {
                this.availableList.addEntry(new ResourcePackListWidget.ResourcePackItem(this.availableList, this, clientResourcePackContainer5));
            }
            for (final ClientResourcePackContainer clientResourcePackContainer5 : Lists.<ClientResourcePackContainer>reverse(Lists.newArrayList(resourcePackContainerManager3.getEnabledContainers()))) {
                this.selectedList.addEntry(new ResourcePackListWidget.ResourcePackItem(this.selectedList, this, clientResourcePackContainer5));
            }
        }
    }
    
    public void select(final ResourcePackListWidget.ResourcePackItem resourcePackItem) {
        this.availableList.children().remove(resourcePackItem);
        resourcePackItem.a(this.selectedList);
        this.a();
    }
    
    public void remove(final ResourcePackListWidget.ResourcePackItem resourcePackItem) {
        this.selectedList.children().remove(resourcePackItem);
        this.availableList.addEntry(resourcePackItem);
        this.a();
    }
    
    public boolean c(final ResourcePackListWidget.ResourcePackItem resourcePackItem) {
        return this.selectedList.children().contains(resourcePackItem);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderDirtBackground(0);
        this.availableList.render(mouseX, mouseY, delta);
        this.selectedList.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.render(mouseX, mouseY, delta);
    }
    
    public void a() {
        this.d = true;
    }
}
