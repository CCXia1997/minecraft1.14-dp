package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;

public class WriteAndReadFix extends DataFix
{
    private final String name;
    private final DSL.TypeReference type;
    
    public WriteAndReadFix(final Schema outSchema, final String name, final DSL.TypeReference typeReference) {
        super(outSchema, true);
        this.name = name;
        this.type = typeReference;
    }
    
    protected TypeRewriteRule makeRule() {
        return this.writeAndRead(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type));
    }
}
