package net.minecraft.util;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.nbt.Tag;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import java.util.Iterator;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Logger;

public final class TagHelper
{
    private static final Logger LOGGER;
    
    @Nullable
    public static GameProfile deserializeProfile(final CompoundTag tag) {
        String string2 = null;
        String string3 = null;
        if (tag.containsKey("Name", 8)) {
            string2 = tag.getString("Name");
        }
        Label_0040: {
            if (!tag.containsKey("Id", 8)) {
                break Label_0040;
            }
            string3 = tag.getString("Id");
            try {
                UUID uUID4;
                try {
                    uUID4 = UUID.fromString(string3);
                }
                catch (Throwable throwable5) {
                    uUID4 = null;
                }
                final GameProfile gameProfile5 = new GameProfile(uUID4, string2);
                if (tag.containsKey("Properties", 10)) {
                    final CompoundTag compoundTag6 = tag.getCompound("Properties");
                    for (final String string4 : compoundTag6.getKeys()) {
                        final ListTag listTag9 = compoundTag6.getList(string4, 10);
                        for (int integer10 = 0; integer10 < listTag9.size(); ++integer10) {
                            final CompoundTag compoundTag7 = listTag9.getCompoundTag(integer10);
                            final String string5 = compoundTag7.getString("Value");
                            if (compoundTag7.containsKey("Signature", 8)) {
                                gameProfile5.getProperties().put(string4, new Property(string4, string5, compoundTag7.getString("Signature")));
                            }
                            else {
                                gameProfile5.getProperties().put(string4, new Property(string4, string5));
                            }
                        }
                    }
                }
                return gameProfile5;
            }
            catch (Throwable t) {
                return null;
            }
        }
    }
    
    public static CompoundTag serializeProfile(final CompoundTag tag, final GameProfile profile) {
        if (!ChatUtil.isEmpty(profile.getName())) {
            tag.putString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            tag.putString("Id", profile.getId().toString());
        }
        if (!profile.getProperties().isEmpty()) {
            final CompoundTag compoundTag3 = new CompoundTag();
            for (final String string5 : profile.getProperties().keySet()) {
                final ListTag listTag6 = new ListTag();
                for (final Property property8 : profile.getProperties().get(string5)) {
                    final CompoundTag compoundTag4 = new CompoundTag();
                    compoundTag4.putString("Value", property8.getValue());
                    if (property8.hasSignature()) {
                        compoundTag4.putString("Signature", property8.getSignature());
                    }
                    ((AbstractList<CompoundTag>)listTag6).add(compoundTag4);
                }
                compoundTag3.put(string5, listTag6);
            }
            tag.put("Properties", compoundTag3);
        }
        return tag;
    }
    
    @VisibleForTesting
    public static boolean areTagsEqual(@Nullable final Tag tag1, @Nullable final Tag tag2, final boolean deepCheck) {
        if (tag1 == tag2) {
            return true;
        }
        if (tag1 == null) {
            return true;
        }
        if (tag2 == null) {
            return false;
        }
        if (!tag1.getClass().equals(tag2.getClass())) {
            return false;
        }
        if (tag1 instanceof CompoundTag) {
            final CompoundTag compoundTag4 = (CompoundTag)tag1;
            final CompoundTag compoundTag5 = (CompoundTag)tag2;
            for (final String string7 : compoundTag4.getKeys()) {
                final Tag tag3 = compoundTag4.getTag(string7);
                if (!areTagsEqual(tag3, compoundTag5.getTag(string7), deepCheck)) {
                    return false;
                }
            }
            return true;
        }
        if (!(tag1 instanceof ListTag) || !deepCheck) {
            return tag1.equals(tag2);
        }
        final ListTag listTag4 = (ListTag)tag1;
        final ListTag listTag5 = (ListTag)tag2;
        if (listTag4.isEmpty()) {
            return listTag5.isEmpty();
        }
        for (int integer6 = 0; integer6 < listTag4.size(); ++integer6) {
            final Tag tag4 = listTag4.k(integer6);
            boolean boolean8 = false;
            for (int integer7 = 0; integer7 < listTag5.size(); ++integer7) {
                if (areTagsEqual(tag4, listTag5.k(integer7), deepCheck)) {
                    boolean8 = true;
                    break;
                }
            }
            if (!boolean8) {
                return false;
            }
        }
        return true;
    }
    
    public static CompoundTag serializeUuid(final UUID uuid) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putLong("M", uuid.getMostSignificantBits());
        compoundTag2.putLong("L", uuid.getLeastSignificantBits());
        return compoundTag2;
    }
    
    public static UUID deserializeUuid(final CompoundTag tag) {
        return new UUID(tag.getLong("M"), tag.getLong("L"));
    }
    
    public static BlockPos deserializeBlockPos(final CompoundTag tag) {
        return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }
    
    public static CompoundTag serializeBlockPos(final BlockPos pos) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putInt("X", pos.getX());
        compoundTag2.putInt("Y", pos.getY());
        compoundTag2.putInt("Z", pos.getZ());
        return compoundTag2;
    }
    
    public static BlockState deserializeBlockState(final CompoundTag tag) {
        if (!tag.containsKey("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        final Block block2 = Registry.BLOCK.get(new Identifier(tag.getString("Name")));
        BlockState blockState3 = block2.getDefaultState();
        if (tag.containsKey("Properties", 10)) {
            final CompoundTag compoundTag4 = tag.getCompound("Properties");
            final StateFactory<Block, BlockState> stateFactory5 = block2.getStateFactory();
            for (final String string7 : compoundTag4.getKeys()) {
                final net.minecraft.state.property.Property<?> property8 = stateFactory5.getProperty(string7);
                if (property8 != null) {
                    blockState3 = TagHelper.withProperty(blockState3, property8, string7, compoundTag4, tag);
                }
            }
        }
        return blockState3;
    }
    
    private static <S extends PropertyContainer<S>, T extends Comparable<T>> S withProperty(final S state, final net.minecraft.state.property.Property<T> property, final String string, final CompoundTag compoundTag4, final CompoundTag compoundTag5) {
        final Optional<T> optional6 = property.getValue(compoundTag4.getString(string));
        if (optional6.isPresent()) {
            return state.<T, T>with(property, optional6.get());
        }
        TagHelper.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", string, compoundTag4.getString(string), compoundTag5.toString());
        return state;
    }
    
    public static CompoundTag serializeBlockState(final BlockState blockState) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("Name", Registry.BLOCK.getId(blockState.getBlock()).toString());
        final ImmutableMap<net.minecraft.state.property.Property<?>, Comparable<?>> immutableMap3 = blockState.getEntries();
        if (!immutableMap3.isEmpty()) {
            final CompoundTag compoundTag3 = new CompoundTag();
            for (final Map.Entry<net.minecraft.state.property.Property<?>, Comparable<?>> entry6 : immutableMap3.entrySet()) {
                final net.minecraft.state.property.Property<?> property7 = entry6.getKey();
                compoundTag3.putString(property7.getName(), TagHelper.getPropertyValueAsString(property7, entry6.getValue()));
            }
            compoundTag2.put("Properties", compoundTag3);
        }
        return compoundTag2;
    }
    
    private static <T extends Comparable<T>> String getPropertyValueAsString(final net.minecraft.state.property.Property<T> property, final Comparable<?> value) {
        return property.getValueAsString((T)value);
    }
    
    public static CompoundTag update(final DataFixer dataFixer, final DataFixTypes dataFixTypes, final CompoundTag compoundTag, final int integer) {
        return update(dataFixer, dataFixTypes, compoundTag, integer, SharedConstants.getGameVersion().getWorldVersion());
    }
    
    public static CompoundTag update(final DataFixer dataFixer, final DataFixTypes dataFixTypes, final CompoundTag compoundTag, final int integer4, final int integer5) {
        return (CompoundTag)dataFixer.update(dataFixTypes.getTypeReference(), new Dynamic((DynamicOps)NbtOps.INSTANCE, compoundTag), integer4, integer5).getValue();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
