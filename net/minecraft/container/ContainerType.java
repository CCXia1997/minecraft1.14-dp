package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ContainerType<T extends Container>
{
    public static final ContainerType<GenericContainer> a;
    public static final ContainerType<GenericContainer> b;
    public static final ContainerType<GenericContainer> GENERIC_9X3;
    public static final ContainerType<GenericContainer> d;
    public static final ContainerType<GenericContainer> e;
    public static final ContainerType<GenericContainer> GENERIC_9X6;
    public static final ContainerType<Generic3x3Container> GENERIC_3X3;
    public static final ContainerType<AnvilContainer> ANVIL;
    public static final ContainerType<BeaconContainer> BEACON;
    public static final ContainerType<BlastFurnaceContainer> BLAST_FURNACE;
    public static final ContainerType<BrewingStandContainer> BREWING_STAND;
    public static final ContainerType<CraftingTableContainer> CRAFTING;
    public static final ContainerType<EnchantingTableContainer> ENCHANTMENT;
    public static final ContainerType<FurnaceContainer> FURNACE;
    public static final ContainerType<GrindstoneContainer> GRINDSTONE;
    public static final ContainerType<HopperContainer> HOPPER;
    public static final ContainerType<LecternContainer> LECTERN;
    public static final ContainerType<LoomContainer> LOOM;
    public static final ContainerType<MerchantContainer> MERCHANT;
    public static final ContainerType<ShulkerBoxContainer> SHULKER_BOX;
    public static final ContainerType<SmokerContainer> SMOKER;
    public static final ContainerType<CartographyTableContainer> CARTOGRAPHY;
    public static final ContainerType<StonecutterContainer> w;
    private final Factory<T> factory;
    
    private static <T extends Container> ContainerType<T> register(final String id, final Factory<T> factory) {
        return Registry.<ContainerType<T>>register(Registry.CONTAINER, id, new ContainerType<T>(factory));
    }
    
    private ContainerType(final Factory<T> factory) {
        this.factory = factory;
    }
    
    @Environment(EnvType.CLIENT)
    public T create(final int syncId, final PlayerInventory playerInventory) {
        return this.factory.create(syncId, playerInventory);
    }
    
    static {
        a = ContainerType.<GenericContainer>register("generic_9x1", GenericContainer::createGeneric9x1);
        b = ContainerType.<GenericContainer>register("generic_9x2", GenericContainer::createGeneric9x2);
        GENERIC_9X3 = ContainerType.<GenericContainer>register("generic_9x3", GenericContainer::createGeneric9x3);
        d = ContainerType.<GenericContainer>register("generic_9x4", GenericContainer::createGeneric9x4);
        e = ContainerType.<GenericContainer>register("generic_9x5", GenericContainer::createGeneric9x5);
        GENERIC_9X6 = ContainerType.<GenericContainer>register("generic_9x6", GenericContainer::createGeneric9x6);
        GENERIC_3X3 = ContainerType.<Generic3x3Container>register("generic_3x3", Generic3x3Container::new);
        ANVIL = ContainerType.<AnvilContainer>register("anvil", AnvilContainer::new);
        BEACON = ContainerType.<BeaconContainer>register("beacon", BeaconContainer::new);
        BLAST_FURNACE = ContainerType.<BlastFurnaceContainer>register("blast_furnace", BlastFurnaceContainer::new);
        BREWING_STAND = ContainerType.<BrewingStandContainer>register("brewing_stand", BrewingStandContainer::new);
        CRAFTING = ContainerType.<CraftingTableContainer>register("crafting", CraftingTableContainer::new);
        ENCHANTMENT = ContainerType.<EnchantingTableContainer>register("enchantment", EnchantingTableContainer::new);
        FURNACE = ContainerType.<FurnaceContainer>register("furnace", FurnaceContainer::new);
        GRINDSTONE = ContainerType.<GrindstoneContainer>register("grindstone", GrindstoneContainer::new);
        HOPPER = ContainerType.<HopperContainer>register("hopper", HopperContainer::new);
        LECTERN = ContainerType.<LecternContainer>register("lectern", (integer, playerInventory) -> new LecternContainer(integer));
        LOOM = ContainerType.<LoomContainer>register("loom", LoomContainer::new);
        MERCHANT = ContainerType.<MerchantContainer>register("merchant", MerchantContainer::new);
        SHULKER_BOX = ContainerType.<ShulkerBoxContainer>register("shulker_box", ShulkerBoxContainer::new);
        SMOKER = ContainerType.<SmokerContainer>register("smoker", SmokerContainer::new);
        CARTOGRAPHY = ContainerType.<CartographyTableContainer>register("cartography", CartographyTableContainer::new);
        w = ContainerType.<StonecutterContainer>register("stonecutter", StonecutterContainer::new);
    }
    
    interface Factory<T extends Container>
    {
        @Environment(EnvType.CLIENT)
        T create(final int arg1, final PlayerInventory arg2);
    }
}
