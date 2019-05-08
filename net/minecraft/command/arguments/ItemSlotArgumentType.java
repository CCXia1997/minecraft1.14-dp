package net.minecraft.command.arguments;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.entity.EquipmentSlot;
import java.util.HashMap;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ItemSlotArgumentType implements ArgumentType<Integer>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION;
    private static final Map<String, Integer> slotNamesToSlotCommandId;
    
    public static ItemSlotArgumentType create() {
        return new ItemSlotArgumentType();
    }
    
    public static int getItemSlot(final CommandContext<ServerCommandSource> context, final String name) {
        return (int)context.getArgument(name, (Class)Integer.class);
    }
    
    public Integer a(final StringReader stringReader) throws CommandSyntaxException {
        final String string2 = stringReader.readUnquotedString();
        if (!ItemSlotArgumentType.slotNamesToSlotCommandId.containsKey(string2)) {
            throw ItemSlotArgumentType.UNKNOWN_SLOT_EXCEPTION.create(string2);
        }
        return ItemSlotArgumentType.slotNamesToSlotCommandId.get(string2);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(ItemSlotArgumentType.slotNamesToSlotCommandId.keySet(), builder);
    }
    
    public Collection<String> getExamples() {
        return ItemSlotArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("container.5", "12", "weapon");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("slot.unknown", new Object[] { object });
            return translatableTextComponent;
        });
        int integer2;
        int integer3;
        int integer4;
        int integer5;
        int integer6;
        int integer7;
        slotNamesToSlotCommandId = SystemUtil.<Map<String, Integer>>consume(Maps.newHashMap(), hashMap -> {
            for (integer2 = 0; integer2 < 54; ++integer2) {
                hashMap.put("container." + integer2, integer2);
            }
            for (integer3 = 0; integer3 < 9; ++integer3) {
                hashMap.put("hotbar." + integer3, integer3);
            }
            for (integer4 = 0; integer4 < 27; ++integer4) {
                hashMap.put("inventory." + integer4, 9 + integer4);
            }
            for (integer5 = 0; integer5 < 27; ++integer5) {
                hashMap.put("enderchest." + integer5, 200 + integer5);
            }
            for (integer6 = 0; integer6 < 8; ++integer6) {
                hashMap.put("villager." + integer6, 300 + integer6);
            }
            for (integer7 = 0; integer7 < 15; ++integer7) {
                hashMap.put("horse." + integer7, 500 + integer7);
            }
            hashMap.put("weapon", 98);
            hashMap.put("weapon.mainhand", 98);
            hashMap.put("weapon.offhand", 99);
            hashMap.put("armor.head", 100 + EquipmentSlot.HEAD.getEntitySlotId());
            hashMap.put("armor.chest", 100 + EquipmentSlot.CHEST.getEntitySlotId());
            hashMap.put("armor.legs", 100 + EquipmentSlot.LEGS.getEntitySlotId());
            hashMap.put("armor.feet", 100 + EquipmentSlot.FEET.getEntitySlotId());
            hashMap.put("horse.saddle", 400);
            hashMap.put("horse.armor", 401);
            hashMap.put("horse.chest", 499);
        });
    }
}
