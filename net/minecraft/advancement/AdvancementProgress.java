package net.minecraft.advancement;

import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.util.Date;
import java.util.List;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.PacketByteBuf;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Maps;
import net.minecraft.advancement.criterion.CriterionProgress;
import java.util.Map;

public class AdvancementProgress implements Comparable<AdvancementProgress>
{
    private final Map<String, CriterionProgress> criteriaProgresses;
    private String[][] requirements;
    
    public AdvancementProgress() {
        this.criteriaProgresses = Maps.newHashMap();
        this.requirements = new String[0][];
    }
    
    public void init(final Map<String, AdvancementCriterion> criteria, final String[][] arr) {
        final Set<String> set3 = criteria.keySet();
        this.criteriaProgresses.entrySet().removeIf(entry -> !set3.contains(entry.getKey()));
        for (final String string5 : set3) {
            if (!this.criteriaProgresses.containsKey(string5)) {
                this.criteriaProgresses.put(string5, new CriterionProgress());
            }
        }
        this.requirements = arr;
    }
    
    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        }
        for (final String[] arr4 : this.requirements) {
            boolean boolean5 = false;
            for (final String string9 : arr4) {
                final CriterionProgress criterionProgress10 = this.getCriterionProgress(string9);
                if (criterionProgress10 != null && criterionProgress10.isObtained()) {
                    boolean5 = true;
                    break;
                }
            }
            if (!boolean5) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAnyObtained() {
        for (final CriterionProgress criterionProgress2 : this.criteriaProgresses.values()) {
            if (criterionProgress2.isObtained()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean obtain(final String string) {
        final CriterionProgress criterionProgress2 = this.criteriaProgresses.get(string);
        if (criterionProgress2 != null && !criterionProgress2.isObtained()) {
            criterionProgress2.obtain();
            return true;
        }
        return false;
    }
    
    public boolean reset(final String string) {
        final CriterionProgress criterionProgress2 = this.criteriaProgresses.get(string);
        if (criterionProgress2 != null && criterionProgress2.isObtained()) {
            criterionProgress2.reset();
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "AdvancementProgress{criteria=" + this.criteriaProgresses + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
    }
    
    public void toPacket(final PacketByteBuf packetByteBuf) {
        packetByteBuf.writeVarInt(this.criteriaProgresses.size());
        for (final Map.Entry<String, CriterionProgress> entry3 : this.criteriaProgresses.entrySet()) {
            packetByteBuf.writeString(entry3.getKey());
            entry3.getValue().toPacket(packetByteBuf);
        }
    }
    
    public static AdvancementProgress fromPacket(final PacketByteBuf buf) {
        final AdvancementProgress advancementProgress2 = new AdvancementProgress();
        for (int integer3 = buf.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            advancementProgress2.criteriaProgresses.put(buf.readString(32767), CriterionProgress.fromPacket(buf));
        }
        return advancementProgress2;
    }
    
    @Nullable
    public CriterionProgress getCriterionProgress(final String string) {
        return this.criteriaProgresses.get(string);
    }
    
    @Environment(EnvType.CLIENT)
    public float getProgressBarPercentage() {
        if (this.criteriaProgresses.isEmpty()) {
            return 0.0f;
        }
        final float float1 = (float)this.requirements.length;
        final float float2 = (float)this.countObtainedRequirements();
        return float2 / float1;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public String getProgressBarFraction() {
        if (this.criteriaProgresses.isEmpty()) {
            return null;
        }
        final int integer1 = this.requirements.length;
        if (integer1 <= 1) {
            return null;
        }
        final int integer2 = this.countObtainedRequirements();
        return integer2 + "/" + integer1;
    }
    
    @Environment(EnvType.CLIENT)
    private int countObtainedRequirements() {
        int integer1 = 0;
        for (final String[] arr5 : this.requirements) {
            boolean boolean6 = false;
            for (final String string10 : arr5) {
                final CriterionProgress criterionProgress11 = this.getCriterionProgress(string10);
                if (criterionProgress11 != null && criterionProgress11.isObtained()) {
                    boolean6 = true;
                    break;
                }
            }
            if (boolean6) {
                ++integer1;
            }
        }
        return integer1;
    }
    
    public Iterable<String> getUnobtainedCriteria() {
        final List<String> list1 = Lists.newArrayList();
        for (final Map.Entry<String, CriterionProgress> entry3 : this.criteriaProgresses.entrySet()) {
            if (!entry3.getValue().isObtained()) {
                list1.add(entry3.getKey());
            }
        }
        return list1;
    }
    
    public Iterable<String> getObtainedCriteria() {
        final List<String> list1 = Lists.newArrayList();
        for (final Map.Entry<String, CriterionProgress> entry3 : this.criteriaProgresses.entrySet()) {
            if (entry3.getValue().isObtained()) {
                list1.add(entry3.getKey());
            }
        }
        return list1;
    }
    
    @Nullable
    public Date getEarliestProgressObtainDate() {
        Date date1 = null;
        for (final CriterionProgress criterionProgress3 : this.criteriaProgresses.values()) {
            if (criterionProgress3.isObtained() && (date1 == null || criterionProgress3.getObtainedDate().before(date1))) {
                date1 = criterionProgress3.getObtainedDate();
            }
        }
        return date1;
    }
    
    public int a(final AdvancementProgress advancementProgress) {
        final Date date2 = this.getEarliestProgressObtainDate();
        final Date date3 = advancementProgress.getEarliestProgressObtainDate();
        if (date2 == null && date3 != null) {
            return 1;
        }
        if (date2 != null && date3 == null) {
            return -1;
        }
        if (date2 == null && date3 == null) {
            return 0;
        }
        return date2.compareTo(date3);
    }
    
    public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress>
    {
        public JsonElement a(final AdvancementProgress entry, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            final JsonObject jsonObject5 = new JsonObject();
            for (final Map.Entry<String, CriterionProgress> entry2 : entry.criteriaProgresses.entrySet()) {
                final CriterionProgress criterionProgress8 = entry2.getValue();
                if (criterionProgress8.isObtained()) {
                    jsonObject5.add(entry2.getKey(), criterionProgress8.toJson());
                }
            }
            if (!jsonObject5.entrySet().isEmpty()) {
                jsonObject4.add("criteria", jsonObject5);
            }
            jsonObject4.addProperty("done", entry.isDone());
            return jsonObject4;
        }
        
        public AdvancementProgress a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(functionJson, "advancement");
            final JsonObject jsonObject5 = JsonHelper.getObject(jsonObject4, "criteria", new JsonObject());
            final AdvancementProgress advancementProgress6 = new AdvancementProgress();
            for (final Map.Entry<String, JsonElement> entry8 : jsonObject5.entrySet()) {
                final String string9 = entry8.getKey();
                advancementProgress6.criteriaProgresses.put(string9, CriterionProgress.obtainedAt(JsonHelper.asString(entry8.getValue(), string9)));
            }
            return advancementProgress6;
        }
    }
}
