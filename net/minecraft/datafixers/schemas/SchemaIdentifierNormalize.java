package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.util.Identifier;
import com.mojang.datafixers.schemas.Schema;

public class SchemaIdentifierNormalize extends Schema
{
    public SchemaIdentifierNormalize(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public static String normalize(final String string) {
        final Identifier identifier2 = Identifier.create(string);
        if (identifier2 != null) {
            return identifier2.toString();
        }
        return string;
    }
    
    public Type<?> getChoiceType(final DSL.TypeReference typeReference, final String string) {
        return super.getChoiceType(typeReference, normalize(string));
    }
}
