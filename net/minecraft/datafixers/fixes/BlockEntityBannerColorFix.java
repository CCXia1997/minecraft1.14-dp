package net.minecraft.datafixers.fixes;

import java.util.stream.Stream;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class BlockEntityBannerColorFix extends ChoiceFix
{
    public BlockEntityBannerColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
    }
    
    public Dynamic<?> a(Dynamic<?> dynamic) {
        dynamic = dynamic.update("Base", dynamic -> dynamic.createInt(15 - dynamic.asInt(0)));
        dynamic = dynamic.update("Patterns", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)((Dynamic)dynamic).asStreamOpt().map(stream -> stream.map(dynamic -> dynamic.update("Color", dynamic -> dynamic.createInt(15 - dynamic.asInt(0))))).map(dynamic::createList), dynamic));
        return dynamic;
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
}
