package net.minecraft.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.client.gui.container.StonecutterScreen;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.client.gui.container.CartographyTableScreen;
import net.minecraft.container.SmokerContainer;
import net.minecraft.client.gui.container.SmokerScreen;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.client.gui.container.ShulkerBoxScreen;
import net.minecraft.container.MerchantContainer;
import net.minecraft.client.gui.container.VillagerScreen;
import net.minecraft.container.LoomContainer;
import net.minecraft.client.gui.container.LoomScreen;
import net.minecraft.container.LecternContainer;
import net.minecraft.client.gui.container.LecternScreen;
import net.minecraft.container.HopperContainer;
import net.minecraft.client.gui.container.HopperScreen;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.client.gui.container.GrindstoneScreen;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.client.gui.container.FurnaceScreen;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.client.gui.container.EnchantingScreen;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.client.gui.container.CraftingTableScreen;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.client.gui.ingame.BrewingStandScreen;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.client.gui.container.BlastFurnaceScreen;
import net.minecraft.container.BeaconContainer;
import net.minecraft.client.gui.container.BeaconScreen;
import net.minecraft.container.AnvilContainer;
import net.minecraft.client.gui.container.AnvilScreen;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.client.gui.container.ContainerScreen9;
import net.minecraft.container.GenericContainer;
import net.minecraft.client.gui.container.ContainerScreen54;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.util.registry.Registry;
import net.minecraft.container.Container;
import net.minecraft.text.TextComponent;
import net.minecraft.client.MinecraftClient;
import javax.annotation.Nullable;
import net.minecraft.container.ContainerType;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ContainerScreenRegistry
{
    private static final Logger LOGGER;
    private static final Map<ContainerType<?>, GuiFactory<?, ?>> GUI_FACTORIES;
    
    public static <T extends Container> void openScreen(@Nullable final ContainerType<T> containerType, final MinecraftClient client, final int syncId, final TextComponent name) {
        if (containerType == null) {
            ContainerScreenRegistry.LOGGER.warn("Trying to open invalid screen with name: {}", name.getString());
            return;
        }
        final GuiFactory<T, ?> guiFactory5 = ContainerScreenRegistry.<T>getFactory(containerType);
        if (guiFactory5 == null) {
            ContainerScreenRegistry.LOGGER.warn("Failed to create screen for menu type: {}", Registry.CONTAINER.getId(containerType));
            return;
        }
        guiFactory5.openScreen(name, containerType, client, syncId);
    }
    
    @Nullable
    private static <T extends Container> GuiFactory<T, ?> getFactory(final ContainerType<T> containerType) {
        return ContainerScreenRegistry.GUI_FACTORIES.get(containerType);
    }
    
    private static <M extends Container, U extends Screen> void registerGui(final ContainerType<? extends M> containerType, final GuiFactory<M, U> guiFactory) {
        final GuiFactory<?, ?> guiFactory2 = ContainerScreenRegistry.GUI_FACTORIES.put(containerType, guiFactory);
        if (guiFactory2 != null) {
            throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(containerType));
        }
    }
    
    public static boolean checkData() {
        boolean boolean1 = false;
        for (final ContainerType<?> containerType3 : Registry.CONTAINER) {
            if (!ContainerScreenRegistry.GUI_FACTORIES.containsKey(containerType3)) {
                ContainerScreenRegistry.LOGGER.debug("Menu {} has no matching screen", Registry.CONTAINER.getId(containerType3));
                boolean1 = true;
            }
        }
        return boolean1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GUI_FACTORIES = Maps.newHashMap();
        ContainerScreenRegistry.<GenericContainer, ContainerScreen54>registerGui(ContainerType.a, ContainerScreen54::new);
        ContainerScreenRegistry.<GenericContainer, ContainerScreen54>registerGui(ContainerType.b, ContainerScreen54::new);
        ContainerScreenRegistry.<GenericContainer, ContainerScreen54>registerGui(ContainerType.GENERIC_9X3, ContainerScreen54::new);
        ContainerScreenRegistry.<GenericContainer, ContainerScreen54>registerGui(ContainerType.d, ContainerScreen54::new);
        ContainerScreenRegistry.<GenericContainer, ContainerScreen54>registerGui(ContainerType.e, ContainerScreen54::new);
        ContainerScreenRegistry.<GenericContainer, ContainerScreen54>registerGui(ContainerType.GENERIC_9X6, ContainerScreen54::new);
        ContainerScreenRegistry.<Generic3x3Container, ContainerScreen9>registerGui(ContainerType.GENERIC_3X3, ContainerScreen9::new);
        ContainerScreenRegistry.<AnvilContainer, AnvilScreen>registerGui(ContainerType.ANVIL, AnvilScreen::new);
        ContainerScreenRegistry.<BeaconContainer, BeaconScreen>registerGui(ContainerType.BEACON, BeaconScreen::new);
        ContainerScreenRegistry.<BlastFurnaceContainer, BlastFurnaceScreen>registerGui(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        ContainerScreenRegistry.<BrewingStandContainer, BrewingStandScreen>registerGui(ContainerType.BREWING_STAND, BrewingStandScreen::new);
        ContainerScreenRegistry.<CraftingTableContainer, CraftingTableScreen>registerGui(ContainerType.CRAFTING, CraftingTableScreen::new);
        ContainerScreenRegistry.<EnchantingTableContainer, EnchantingScreen>registerGui(ContainerType.ENCHANTMENT, EnchantingScreen::new);
        ContainerScreenRegistry.<FurnaceContainer, FurnaceScreen>registerGui(ContainerType.FURNACE, FurnaceScreen::new);
        ContainerScreenRegistry.<GrindstoneContainer, GrindstoneScreen>registerGui(ContainerType.GRINDSTONE, GrindstoneScreen::new);
        ContainerScreenRegistry.<HopperContainer, HopperScreen>registerGui(ContainerType.HOPPER, HopperScreen::new);
        ContainerScreenRegistry.<LecternContainer, LecternScreen>registerGui(ContainerType.LECTERN, LecternScreen::new);
        ContainerScreenRegistry.<LoomContainer, LoomScreen>registerGui(ContainerType.LOOM, LoomScreen::new);
        ContainerScreenRegistry.<MerchantContainer, VillagerScreen>registerGui(ContainerType.MERCHANT, VillagerScreen::new);
        ContainerScreenRegistry.<ShulkerBoxContainer, ShulkerBoxScreen>registerGui(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
        ContainerScreenRegistry.<SmokerContainer, SmokerScreen>registerGui(ContainerType.SMOKER, SmokerScreen::new);
        ContainerScreenRegistry.<CartographyTableContainer, CartographyTableScreen>registerGui(ContainerType.CARTOGRAPHY, CartographyTableScreen::new);
        ContainerScreenRegistry.<StonecutterContainer, StonecutterScreen>registerGui(ContainerType.w, StonecutterScreen::new);
    }
    
    @Environment(EnvType.CLIENT)
    interface GuiFactory<T extends Container, U extends net.minecraft.client.gui.Screen>
    {
        default void openScreen(final TextComponent name, final ContainerType<T> containerType, final MinecraftClient client, final int syncId) {
            final U screen5 = this.create(containerType.create(syncId, client.player.inventory), client.player.inventory, name);
            client.player.container = ((ContainerProvider)screen5).getContainer();
            client.openScreen((Screen)screen5);
        }
        
        U create(final T arg1, final PlayerInventory arg2, final TextComponent arg3);
    }
}
