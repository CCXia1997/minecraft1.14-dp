package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class JigsawBlockEntity extends BlockEntity
{
    private Identifier attachmentType;
    private Identifier targetPool;
    private String finalState;
    
    public JigsawBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.attachmentType = new Identifier("empty");
        this.targetPool = new Identifier("empty");
        this.finalState = "minecraft:air";
    }
    
    public JigsawBlockEntity() {
        this(BlockEntityType.JIGSAW);
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getAttachmentType() {
        return this.attachmentType;
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getTargetPool() {
        return this.targetPool;
    }
    
    @Environment(EnvType.CLIENT)
    public String getFinalState() {
        return this.finalState;
    }
    
    public void setAttachmentType(final Identifier value) {
        this.attachmentType = value;
    }
    
    public void setTargetPool(final Identifier value) {
        this.targetPool = value;
    }
    
    public void setFinalState(final String value) {
        this.finalState = value;
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putString("attachement_type", this.attachmentType.toString());
        compoundTag.putString("target_pool", this.targetPool.toString());
        compoundTag.putString("final_state", this.finalState);
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.attachmentType = new Identifier(compoundTag.getString("attachement_type"));
        this.targetPool = new Identifier(compoundTag.getString("target_pool"));
        this.finalState = compoundTag.getString("final_state");
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 12, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
}
