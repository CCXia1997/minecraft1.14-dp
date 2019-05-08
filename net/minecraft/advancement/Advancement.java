package net.minecraft.advancement;

import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.util.PacketByteBuf;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import com.google.common.collect.Maps;
import net.minecraft.text.Style;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Arrays;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.StringTextComponent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import java.util.Set;
import java.util.Map;
import net.minecraft.util.Identifier;

public class Advancement
{
    private final Advancement parent;
    private final AdvancementDisplay display;
    private final AdvancementRewards rewards;
    private final Identifier id;
    private final Map<String, AdvancementCriterion> criteria;
    private final String[][] requirements;
    private final Set<Advancement> children;
    private final TextComponent textComponent;
    
    public Advancement(final Identifier identifier, @Nullable final Advancement advancement, @Nullable final AdvancementDisplay advancementDisplay, final AdvancementRewards advancementRewards, final Map<String, AdvancementCriterion> map, final String[][] arr) {
        this.children = Sets.newLinkedHashSet();
        this.id = identifier;
        this.display = advancementDisplay;
        this.criteria = ImmutableMap.copyOf(map);
        this.parent = advancement;
        this.rewards = advancementRewards;
        this.requirements = arr;
        if (advancement != null) {
            advancement.addChild(this);
        }
        if (advancementDisplay == null) {
            this.textComponent = new StringTextComponent(identifier.toString());
        }
        else {
            final TextComponent textComponent7 = advancementDisplay.getTitle();
            final TextFormat textFormat8 = advancementDisplay.getFrame().getTitleFormat();
            final TextComponent textComponent8 = textComponent7.copy().applyFormat(textFormat8).append("\n").append(advancementDisplay.getDescription());
            final TextComponent textComponent9 = textComponent7.copy().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent8)));
            this.textComponent = new StringTextComponent("[").append(textComponent9).append("]").applyFormat(textFormat8);
        }
    }
    
    public Task createTask() {
        return new Task((this.parent == null) ? null : this.parent.getId(), this.display, this.rewards, (Map)this.criteria, this.requirements);
    }
    
    @Nullable
    public Advancement getParent() {
        return this.parent;
    }
    
    @Nullable
    public AdvancementDisplay getDisplay() {
        return this.display;
    }
    
    public AdvancementRewards getRewards() {
        return this.rewards;
    }
    
    @Override
    public String toString() {
        return "SimpleAdvancement{id=" + this.getId() + ", parent=" + ((this.parent == null) ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
    }
    
    public Iterable<Advancement> getChildren() {
        return this.children;
    }
    
    public Map<String, AdvancementCriterion> getCriteria() {
        return this.criteria;
    }
    
    @Environment(EnvType.CLIENT)
    public int getRequirementCount() {
        return this.requirements.length;
    }
    
    public void addChild(final Advancement advancement) {
        this.children.add(advancement);
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Advancement)) {
            return false;
        }
        final Advancement advancement2 = (Advancement)o;
        return this.id.equals(advancement2.id);
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public String[][] getRequirements() {
        return this.requirements;
    }
    
    public TextComponent getTextComponent() {
        return this.textComponent;
    }
    
    public static class Task
    {
        private Identifier parentId;
        private Advancement parentObj;
        private AdvancementDisplay display;
        private AdvancementRewards rewards;
        private Map<String, AdvancementCriterion> criteria;
        private String[][] requirements;
        private CriteriaMerger merger;
        
        private Task(@Nullable final Identifier identifier, @Nullable final AdvancementDisplay advancementDisplay, final AdvancementRewards advancementRewards, final Map<String, AdvancementCriterion> map, final String[][] arr) {
            this.rewards = AdvancementRewards.NONE;
            this.criteria = Maps.newLinkedHashMap();
            this.merger = CriteriaMerger.AND;
            this.parentId = identifier;
            this.display = advancementDisplay;
            this.rewards = advancementRewards;
            this.criteria = map;
            this.requirements = arr;
        }
        
        private Task() {
            this.rewards = AdvancementRewards.NONE;
            this.criteria = Maps.newLinkedHashMap();
            this.merger = CriteriaMerger.AND;
        }
        
        public static Task create() {
            return new Task();
        }
        
        public Task parent(final Advancement advancement) {
            this.parentObj = advancement;
            return this;
        }
        
        public Task parent(final Identifier identifier) {
            this.parentId = identifier;
            return this;
        }
        
        public Task display(final ItemStack stack, final TextComponent textComponent2, final TextComponent textComponent3, @Nullable final Identifier identifier, final AdvancementFrame advancementFrame, final boolean boolean6, final boolean boolean7, final boolean boolean8) {
            return this.display(new AdvancementDisplay(stack, textComponent2, textComponent3, identifier, advancementFrame, boolean6, boolean7, boolean8));
        }
        
        public Task display(final ItemProvider itemProvider, final TextComponent textComponent2, final TextComponent textComponent3, @Nullable final Identifier identifier, final AdvancementFrame advancementFrame, final boolean boolean6, final boolean boolean7, final boolean boolean8) {
            return this.display(new AdvancementDisplay(new ItemStack(itemProvider.getItem()), textComponent2, textComponent3, identifier, advancementFrame, boolean6, boolean7, boolean8));
        }
        
        public Task display(final AdvancementDisplay advancementDisplay) {
            this.display = advancementDisplay;
            return this;
        }
        
        public Task rewards(final AdvancementRewards.Builder builder) {
            return this.rewards(builder.build());
        }
        
        public Task rewards(final AdvancementRewards advancementRewards) {
            this.rewards = advancementRewards;
            return this;
        }
        
        public Task criterion(final String conditions, final CriterionConditions criterionConditions) {
            return this.criterion(conditions, new AdvancementCriterion(criterionConditions));
        }
        
        public Task criterion(final String criterion, final AdvancementCriterion advancementCriterion) {
            if (this.criteria.containsKey(criterion)) {
                throw new IllegalArgumentException("Duplicate criterion " + criterion);
            }
            this.criteria.put(criterion, advancementCriterion);
            return this;
        }
        
        public Task criteriaMerger(final CriteriaMerger criteriaMerger) {
            this.merger = criteriaMerger;
            return this;
        }
        
        public boolean findParent(final Function<Identifier, Advancement> function) {
            if (this.parentId == null) {
                return true;
            }
            if (this.parentObj == null) {
                this.parentObj = function.apply(this.parentId);
            }
            return this.parentObj != null;
        }
        
        public Advancement build(final Identifier identifier) {
            if (!this.findParent(identifier -> null)) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            }
            if (this.requirements == null) {
                this.requirements = this.merger.createRequirements(this.criteria.keySet());
            }
            return new Advancement(identifier, this.parentObj, this.display, this.rewards, this.criteria, this.requirements);
        }
        
        public Advancement build(final Consumer<Advancement> consumer, final String string) {
            final Advancement advancement3 = this.build(new Identifier(string));
            consumer.accept(advancement3);
            return advancement3;
        }
        
        public JsonObject toJson() {
            if (this.requirements == null) {
                this.requirements = this.merger.createRequirements(this.criteria.keySet());
            }
            final JsonObject jsonObject1 = new JsonObject();
            if (this.parentObj != null) {
                jsonObject1.addProperty("parent", this.parentObj.getId().toString());
            }
            else if (this.parentId != null) {
                jsonObject1.addProperty("parent", this.parentId.toString());
            }
            if (this.display != null) {
                jsonObject1.add("display", this.display.toJson());
            }
            jsonObject1.add("rewards", this.rewards.toJson());
            final JsonObject jsonObject2 = new JsonObject();
            for (final Map.Entry<String, AdvancementCriterion> entry4 : this.criteria.entrySet()) {
                jsonObject2.add(entry4.getKey(), entry4.getValue().toJson());
            }
            jsonObject1.add("criteria", jsonObject2);
            final JsonArray jsonArray3 = new JsonArray();
            for (final String[] arr7 : this.requirements) {
                final JsonArray jsonArray4 = new JsonArray();
                for (final String string12 : arr7) {
                    jsonArray4.add(string12);
                }
                jsonArray3.add(jsonArray4);
            }
            jsonObject1.add("requirements", jsonArray3);
            return jsonObject1;
        }
        
        public void toPacket(final PacketByteBuf packetByteBuf) {
            if (this.parentId == null) {
                packetByteBuf.writeBoolean(false);
            }
            else {
                packetByteBuf.writeBoolean(true);
                packetByteBuf.writeIdentifier(this.parentId);
            }
            if (this.display == null) {
                packetByteBuf.writeBoolean(false);
            }
            else {
                packetByteBuf.writeBoolean(true);
                this.display.toPacket(packetByteBuf);
            }
            AdvancementCriterion.serialize(this.criteria, packetByteBuf);
            packetByteBuf.writeVarInt(this.requirements.length);
            for (final String[] arr5 : this.requirements) {
                packetByteBuf.writeVarInt(arr5.length);
                for (final String string9 : arr5) {
                    packetByteBuf.writeString(string9);
                }
            }
        }
        
        @Override
        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
        }
        
        public static Task fromJson(final JsonObject obj, final JsonDeserializationContext context) {
            final Identifier identifier3 = obj.has("parent") ? new Identifier(JsonHelper.getString(obj, "parent")) : null;
            final AdvancementDisplay advancementDisplay4 = obj.has("display") ? AdvancementDisplay.fromJson(JsonHelper.getObject(obj, "display"), context) : null;
            final AdvancementRewards advancementRewards5 = JsonHelper.<AdvancementRewards>deserialize(obj, "rewards", AdvancementRewards.NONE, context, AdvancementRewards.class);
            final Map<String, AdvancementCriterion> map6 = AdvancementCriterion.fromJson(JsonHelper.getObject(obj, "criteria"), context);
            if (map6.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            }
            final JsonArray jsonArray7 = JsonHelper.getArray(obj, "requirements", new JsonArray());
            String[][] arr8 = new String[jsonArray7.size()][];
            for (int integer9 = 0; integer9 < jsonArray7.size(); ++integer9) {
                final JsonArray jsonArray8 = JsonHelper.asArray(jsonArray7.get(integer9), "requirements[" + integer9 + "]");
                arr8[integer9] = new String[jsonArray8.size()];
                for (int integer10 = 0; integer10 < jsonArray8.size(); ++integer10) {
                    arr8[integer9][integer10] = JsonHelper.asString(jsonArray8.get(integer10), "requirements[" + integer9 + "][" + integer10 + "]");
                }
            }
            if (arr8.length == 0) {
                arr8 = new String[map6.size()][];
                int integer9 = 0;
                for (final String string11 : map6.keySet()) {
                    arr8[integer9++] = new String[] { string11 };
                }
            }
            for (final String[] arr9 : arr8) {
                if (arr9.length == 0 && map6.isEmpty()) {
                    throw new JsonSyntaxException("Requirement entry cannot be empty");
                }
                for (final String string12 : arr9) {
                    if (!map6.containsKey(string12)) {
                        throw new JsonSyntaxException("Unknown required criterion '" + string12 + "'");
                    }
                }
            }
            for (final String string13 : map6.keySet()) {
                boolean boolean11 = false;
                for (final String[] arr10 : arr8) {
                    if (ArrayUtils.contains((Object[])arr10, string13)) {
                        boolean11 = true;
                        break;
                    }
                }
                if (!boolean11) {
                    throw new JsonSyntaxException("Criterion '" + string13 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                }
            }
            return new Task(identifier3, advancementDisplay4, advancementRewards5, map6, arr8);
        }
        
        public static Task fromPacket(final PacketByteBuf buf) {
            final Identifier identifier2 = buf.readBoolean() ? buf.readIdentifier() : null;
            final AdvancementDisplay advancementDisplay3 = buf.readBoolean() ? AdvancementDisplay.fromPacket(buf) : null;
            final Map<String, AdvancementCriterion> map4 = AdvancementCriterion.fromPacket(buf);
            final String[][] arr5 = new String[buf.readVarInt()][];
            for (int integer6 = 0; integer6 < arr5.length; ++integer6) {
                arr5[integer6] = new String[buf.readVarInt()];
                for (int integer7 = 0; integer7 < arr5[integer6].length; ++integer7) {
                    arr5[integer6][integer7] = buf.readString(32767);
                }
            }
            return new Task(identifier2, advancementDisplay3, AdvancementRewards.NONE, map4, arr5);
        }
        
        public Map<String, AdvancementCriterion> getCriteria() {
            return this.criteria;
        }
    }
}
