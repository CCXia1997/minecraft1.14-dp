package net.minecraft.resource;

import java.util.function.Function;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import net.minecraft.text.Style;
import net.minecraft.text.event.HoverEvent;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringTextComponent;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.TextComponent;
import java.util.function.Supplier;
import net.minecraft.resource.metadata.PackResourceMetadata;
import org.apache.logging.log4j.Logger;

public class ResourcePackContainer implements AutoCloseable
{
    private static final Logger LOGGER;
    private static final PackResourceMetadata BROKEN_PACK_META;
    private final String name;
    private final Supplier<ResourcePack> packCreator;
    private final TextComponent displayName;
    private final TextComponent description;
    private final ResourcePackCompatibility compatibility;
    private final SortingDirection direction;
    private final boolean notSorting;
    private final boolean tillEnd;
    
    @Nullable
    public static <T extends ResourcePackContainer> T of(final String name, final boolean boolean2, final Supplier<ResourcePack> supplier, final Factory<T> factory, final SortingDirection sortingDirection) {
        try (final ResourcePack resourcePack6 = supplier.get()) {
            PackResourceMetadata packResourceMetadata8 = resourcePack6.<PackResourceMetadata>parseMetadata((ResourceMetadataReader<PackResourceMetadata>)PackResourceMetadata.READER);
            if (boolean2 && packResourceMetadata8 == null) {
                ResourcePackContainer.LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                packResourceMetadata8 = ResourcePackContainer.BROKEN_PACK_META;
            }
            if (packResourceMetadata8 != null) {
                return factory.create(name, boolean2, supplier, resourcePack6, packResourceMetadata8, sortingDirection);
            }
            ResourcePackContainer.LOGGER.warn("Couldn't find pack meta for pack {}", name);
        }
        catch (IOException iOException6) {
            ResourcePackContainer.LOGGER.warn("Couldn't get pack info for: {}", iOException6.toString());
        }
        return null;
    }
    
    public ResourcePackContainer(final String name, final boolean notSorting, final Supplier<ResourcePack> packCreator, final TextComponent displayName, final TextComponent description, final ResourcePackCompatibility compatibility, final SortingDirection direction, final boolean tillEnd) {
        this.name = name;
        this.packCreator = packCreator;
        this.displayName = displayName;
        this.description = description;
        this.compatibility = compatibility;
        this.notSorting = notSorting;
        this.direction = direction;
        this.tillEnd = tillEnd;
    }
    
    public ResourcePackContainer(final String name, final boolean notSorting, final Supplier<ResourcePack> packCreator, final ResourcePack pack, final PackResourceMetadata metadata, final SortingDirection direction) {
        this(name, notSorting, packCreator, new StringTextComponent(pack.getName()), metadata.getDescription(), ResourcePackCompatibility.from(metadata.getPackFormat()), direction, false);
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getDisplayName() {
        return this.displayName;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getDescription() {
        return this.description;
    }
    
    public TextComponent getInformationText(final boolean boolean1) {
        final HoverEvent hoverEvent;
        final Object o;
        return TextFormatter.bracketed(new StringTextComponent(this.name)).modifyStyle(style -> {
            style.setColor(boolean1 ? TextFormat.k : TextFormat.m).setInsertion(StringArgumentType.escapeIfRequired(this.name));
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("").append(this.displayName).append("\n").append(this.description));
            ((Style)o).setHoverEvent(hoverEvent);
        });
    }
    
    public ResourcePackCompatibility getCompatibility() {
        return this.compatibility;
    }
    
    public ResourcePack createResourcePack() {
        return this.packCreator.get();
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean canBeSorted() {
        return this.notSorting;
    }
    
    public boolean sortsTillEnd() {
        return this.tillEnd;
    }
    
    public SortingDirection getSortingDirection() {
        return this.direction;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourcePackContainer)) {
            return false;
        }
        final ResourcePackContainer resourcePackContainer2 = (ResourcePackContainer)o;
        return this.name.equals(resourcePackContainer2.name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public void close() {
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BROKEN_PACK_META = new PackResourceMetadata(new TranslatableTextComponent("resourcePack.broken_assets", new Object[0]).applyFormat(TextFormat.m, TextFormat.u), SharedConstants.getGameVersion().getPackVersion());
    }
    
    public enum SortingDirection
    {
        a, 
        b;
        
        public <T, P extends ResourcePackContainer> int locate(final List<T> list, final T object, final Function<T, P> converter, final boolean inverse) {
            final SortingDirection sortingDirection5 = inverse ? this.inverse() : this;
            if (sortingDirection5 == SortingDirection.b) {
                int integer6;
                for (integer6 = 0; integer6 < list.size(); ++integer6) {
                    final P resourcePackContainer7 = converter.apply(list.get(integer6));
                    if (!resourcePackContainer7.sortsTillEnd() || resourcePackContainer7.getSortingDirection() != this) {
                        break;
                    }
                }
                list.add(integer6, object);
                return integer6;
            }
            int integer6;
            for (integer6 = list.size() - 1; integer6 >= 0; --integer6) {
                final P resourcePackContainer7 = converter.apply(list.get(integer6));
                if (!resourcePackContainer7.sortsTillEnd() || resourcePackContainer7.getSortingDirection() != this) {
                    break;
                }
            }
            list.add(integer6 + 1, object);
            return integer6 + 1;
        }
        
        public SortingDirection inverse() {
            return (this == SortingDirection.a) ? SortingDirection.b : SortingDirection.a;
        }
    }
    
    @FunctionalInterface
    public interface Factory<T extends ResourcePackContainer>
    {
        @Nullable
        T create(final String arg1, final boolean arg2, final Supplier<ResourcePack> arg3, final ResourcePack arg4, final PackResourceMetadata arg5, final SortingDirection arg6);
    }
}
