package net.minecraft.datafixers.fixes;

import java.util.Objects;
import com.mojang.datafixers.schemas.Schema;

public class EntityTippedArrowFix extends EntityRenameFix
{
    public EntityTippedArrowFix(final Schema schema, final boolean boolean2) {
        super("EntityTippedArrowFix", schema, boolean2);
    }
    
    @Override
    protected String rename(final String string) {
        return Objects.equals(string, "TippedArrow") ? "Arrow" : string;
    }
}
