package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderer extends MobEntityRenderer<RabbitEntity, RabbitEntityModel<RabbitEntity>>
{
    private static final Identifier BROWN_SKIN;
    private static final Identifier WHITE_SKIN;
    private static final Identifier BLACK_SKIN;
    private static final Identifier GOLD_SKIN;
    private static final Identifier SALT_SKIN;
    private static final Identifier WHITE_SPOTTED_SKIN;
    private static final Identifier TOAST_SKIN;
    private static final Identifier CAERBANNOG_SKIN;
    
    public RabbitEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new RabbitEntityModel(), 0.3f);
    }
    
    protected Identifier getTexture(final RabbitEntity rabbitEntity) {
        final String string2 = TextFormat.stripFormatting(rabbitEntity.getName().getString());
        if (string2 != null && "Toast".equals(string2)) {
            return RabbitEntityRenderer.TOAST_SKIN;
        }
        switch (rabbitEntity.getRabbitType()) {
            default: {
                return RabbitEntityRenderer.BROWN_SKIN;
            }
            case 1: {
                return RabbitEntityRenderer.WHITE_SKIN;
            }
            case 2: {
                return RabbitEntityRenderer.BLACK_SKIN;
            }
            case 4: {
                return RabbitEntityRenderer.GOLD_SKIN;
            }
            case 5: {
                return RabbitEntityRenderer.SALT_SKIN;
            }
            case 3: {
                return RabbitEntityRenderer.WHITE_SPOTTED_SKIN;
            }
            case 99: {
                return RabbitEntityRenderer.CAERBANNOG_SKIN;
            }
        }
    }
    
    static {
        BROWN_SKIN = new Identifier("textures/entity/rabbit/brown.png");
        WHITE_SKIN = new Identifier("textures/entity/rabbit/white.png");
        BLACK_SKIN = new Identifier("textures/entity/rabbit/black.png");
        GOLD_SKIN = new Identifier("textures/entity/rabbit/gold.png");
        SALT_SKIN = new Identifier("textures/entity/rabbit/salt.png");
        WHITE_SPOTTED_SKIN = new Identifier("textures/entity/rabbit/white_splotched.png");
        TOAST_SKIN = new Identifier("textures/entity/rabbit/toast.png");
        CAERBANNOG_SKIN = new Identifier("textures/entity/rabbit/caerbannog.png");
    }
}
