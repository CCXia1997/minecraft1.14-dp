package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.math.Direction;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.entity.SkullBlockEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.util.TagHelper;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.EquipmentSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel> extends FeatureRenderer<T, M>
{
    public HeadFeatureRenderer(final FeatureRendererContext<T, M> context) {
        super(context);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack9.isEmpty()) {
            return;
        }
        final Item item10 = itemStack9.getItem();
        GlStateManager.pushMatrix();
        if (entity.isInSneakingPose()) {
            GlStateManager.translatef(0.0f, 0.2f, 0.0f);
        }
        final boolean boolean11 = entity instanceof VillagerEntity || entity instanceof ZombieVillagerEntity;
        if (entity.isChild() && !(entity instanceof VillagerEntity)) {
            final float float9 = 2.0f;
            final float float10 = 1.4f;
            GlStateManager.translatef(0.0f, 0.5f * float8, 0.0f);
            GlStateManager.scalef(0.7f, 0.7f, 0.7f);
            GlStateManager.translatef(0.0f, 16.0f * float8, 0.0f);
        }
        this.getModel().setHeadAngle(0.0625f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (item10 instanceof BlockItem && ((BlockItem)item10).getBlock() instanceof AbstractSkullBlock) {
            final float float9 = 1.1875f;
            GlStateManager.scalef(1.1875f, -1.1875f, -1.1875f);
            if (boolean11) {
                GlStateManager.translatef(0.0f, 0.0625f, 0.0f);
            }
            GameProfile gameProfile13 = null;
            if (itemStack9.hasTag()) {
                final CompoundTag compoundTag14 = itemStack9.getTag();
                if (compoundTag14.containsKey("SkullOwner", 10)) {
                    gameProfile13 = TagHelper.deserializeProfile(compoundTag14.getCompound("SkullOwner"));
                }
                else if (compoundTag14.containsKey("SkullOwner", 8)) {
                    final String string15 = compoundTag14.getString("SkullOwner");
                    if (!StringUtils.isBlank((CharSequence)string15)) {
                        gameProfile13 = SkullBlockEntity.loadProperties(new GameProfile((UUID)null, string15));
                        compoundTag14.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile13));
                    }
                }
            }
            SkullBlockEntityRenderer.INSTANCE.render(-0.5f, 0.0f, -0.5f, null, 180.0f, ((AbstractSkullBlock)((BlockItem)item10).getBlock()).getSkullType(), gameProfile13, -1, float2);
        }
        else if (!(item10 instanceof ArmorItem) || ((ArmorItem)item10).getSlotType() != EquipmentSlot.HEAD) {
            final float float9 = 0.625f;
            GlStateManager.translatef(0.0f, -0.25f, 0.0f);
            GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.scalef(0.625f, -0.625f, -0.625f);
            if (boolean11) {
                GlStateManager.translatef(0.0f, 0.1875f, 0.0f);
            }
            MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(entity, itemStack9, ModelTransformation.Type.f);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
