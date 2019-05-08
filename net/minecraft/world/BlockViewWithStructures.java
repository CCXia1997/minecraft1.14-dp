package net.minecraft.world;

import java.util.Map;
import it.unimi.dsi.fastutil.longs.LongSet;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;

public interface BlockViewWithStructures extends BlockView
{
    @Nullable
    StructureStart getStructureStart(final String arg1);
    
    void setStructureStart(final String arg1, final StructureStart arg2);
    
    LongSet getStructureReferences(final String arg1);
    
    void addStructureReference(final String arg1, final long arg2);
    
    Map<String, LongSet> getStructureReferences();
    
    void setStructureReferences(final Map<String, LongSet> arg1);
}
