package net.minecraft.advancement;

import net.minecraft.util.registry.Registry;
import com.google.gson.JsonElement;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.Item;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.item.ItemProvider;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;

public class AdvancementDisplay
{
    private final TextComponent title;
    private final TextComponent description;
    private final ItemStack icon;
    private final Identifier background;
    private final AdvancementFrame frame;
    private final boolean showToast;
    private final boolean announceToChat;
    private final boolean hidden;
    private float xPos;
    private float yPos;
    
    public AdvancementDisplay(final ItemStack itemStack, final TextComponent textComponent2, final TextComponent title, @Nullable final Identifier background, final AdvancementFrame frame, final boolean boolean6, final boolean boolean7, final boolean boolean8) {
        this.title = textComponent2;
        this.description = title;
        this.icon = itemStack;
        this.background = background;
        this.frame = frame;
        this.showToast = boolean6;
        this.announceToChat = boolean7;
        this.hidden = boolean8;
    }
    
    public void setPosition(final float xPos, final float float2) {
        this.xPos = xPos;
        this.yPos = float2;
    }
    
    public TextComponent getTitle() {
        return this.title;
    }
    
    public TextComponent getDescription() {
        return this.description;
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getIcon() {
        return this.icon;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Identifier getBackground() {
        return this.background;
    }
    
    public AdvancementFrame getFrame() {
        return this.frame;
    }
    
    @Environment(EnvType.CLIENT)
    public float getX() {
        return this.xPos;
    }
    
    @Environment(EnvType.CLIENT)
    public float getY() {
        return this.yPos;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldShowToast() {
        return this.showToast;
    }
    
    public boolean shouldAnnounceToChat() {
        return this.announceToChat;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public static AdvancementDisplay fromJson(final JsonObject obj, final JsonDeserializationContext context) {
        final TextComponent textComponent3 = JsonHelper.<TextComponent>deserialize(obj, "title", context, TextComponent.class);
        final TextComponent textComponent4 = JsonHelper.<TextComponent>deserialize(obj, "description", context, TextComponent.class);
        if (textComponent3 == null || textComponent4 == null) {
            throw new JsonSyntaxException("Both title and description must be set");
        }
        final ItemStack itemStack5 = iconFromJson(JsonHelper.getObject(obj, "icon"));
        final Identifier identifier6 = obj.has("background") ? new Identifier(JsonHelper.getString(obj, "background")) : null;
        final AdvancementFrame advancementFrame7 = obj.has("frame") ? AdvancementFrame.forName(JsonHelper.getString(obj, "frame")) : AdvancementFrame.TASK;
        final boolean boolean8 = JsonHelper.getBoolean(obj, "show_toast", true);
        final boolean boolean9 = JsonHelper.getBoolean(obj, "announce_to_chat", true);
        final boolean boolean10 = JsonHelper.getBoolean(obj, "hidden", false);
        return new AdvancementDisplay(itemStack5, textComponent3, textComponent4, identifier6, advancementFrame7, boolean8, boolean9, boolean10);
    }
    
    private static ItemStack iconFromJson(final JsonObject jsonObject) {
        if (!jsonObject.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        }
        final Item item2 = JsonHelper.getItem(jsonObject, "item");
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        final ItemStack itemStack3 = new ItemStack(item2);
        if (jsonObject.has("nbt")) {
            try {
                final CompoundTag compoundTag4 = StringNbtReader.parse(JsonHelper.asString(jsonObject.get("nbt"), "nbt"));
                itemStack3.setTag(compoundTag4);
            }
            catch (CommandSyntaxException commandSyntaxException4) {
                throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException4.getMessage());
            }
        }
        return itemStack3;
    }
    
    public void toPacket(final PacketByteBuf packetByteBuf) {
        packetByteBuf.writeTextComponent(this.title);
        packetByteBuf.writeTextComponent(this.description);
        packetByteBuf.writeItemStack(this.icon);
        packetByteBuf.writeEnumConstant(this.frame);
        int integer2 = 0;
        if (this.background != null) {
            integer2 |= 0x1;
        }
        if (this.showToast) {
            integer2 |= 0x2;
        }
        if (this.hidden) {
            integer2 |= 0x4;
        }
        packetByteBuf.writeInt(integer2);
        if (this.background != null) {
            packetByteBuf.writeIdentifier(this.background);
        }
        packetByteBuf.writeFloat(this.xPos);
        packetByteBuf.writeFloat(this.yPos);
    }
    
    public static AdvancementDisplay fromPacket(final PacketByteBuf buf) {
        final TextComponent textComponent2 = buf.readTextComponent();
        final TextComponent textComponent3 = buf.readTextComponent();
        final ItemStack itemStack4 = buf.readItemStack();
        final AdvancementFrame advancementFrame5 = buf.<AdvancementFrame>readEnumConstant(AdvancementFrame.class);
        final int integer6 = buf.readInt();
        final Identifier identifier7 = ((integer6 & 0x1) != 0x0) ? buf.readIdentifier() : null;
        final boolean boolean8 = (integer6 & 0x2) != 0x0;
        final boolean boolean9 = (integer6 & 0x4) != 0x0;
        final AdvancementDisplay advancementDisplay10 = new AdvancementDisplay(itemStack4, textComponent2, textComponent3, identifier7, advancementFrame5, boolean8, false, boolean9);
        advancementDisplay10.setPosition(buf.readFloat(), buf.readFloat());
        return advancementDisplay10;
    }
    
    public JsonElement toJson() {
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("icon", this.iconToJson());
        jsonObject1.add("title", TextComponent.Serializer.toJson(this.title));
        jsonObject1.add("description", TextComponent.Serializer.toJson(this.description));
        jsonObject1.addProperty("frame", this.frame.getId());
        jsonObject1.addProperty("show_toast", this.showToast);
        jsonObject1.addProperty("announce_to_chat", this.announceToChat);
        jsonObject1.addProperty("hidden", this.hidden);
        if (this.background != null) {
            jsonObject1.addProperty("background", this.background.toString());
        }
        return jsonObject1;
    }
    
    private JsonObject iconToJson() {
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("item", Registry.ITEM.getId(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            jsonObject1.addProperty("nbt", this.icon.getTag().toString());
        }
        return jsonObject1;
    }
}
