package net.minecraft.advancement.criterion;

import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

public interface CriterionConditions
{
    Identifier getId();
    
    default JsonElement toJson() {
        return JsonNull.INSTANCE;
    }
}
