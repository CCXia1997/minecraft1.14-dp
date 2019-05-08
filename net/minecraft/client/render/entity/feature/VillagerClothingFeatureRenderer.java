package net.minecraft.client.render.entity.feature;

import net.minecraft.util.SystemUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.Resource;
import java.io.IOException;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.village.VillagerData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.resource.ResourceReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.util.Identifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<T extends LivingEntity, M extends EntityModel> extends FeatureRenderer<T, M> implements SynchronousResourceReloadListener
{
    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID;
    private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat;
    private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat;
    private final ReloadableResourceManager resourceManager;
    private final String entityType;
    
    public VillagerClothingFeatureRenderer(final FeatureRendererContext<T, M> featureRendererContext, final ReloadableResourceManager reloadableResourceManager, final String string) {
        super(featureRendererContext);
        this.villagerTypeToHat = (Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType>)new Object2ObjectOpenHashMap();
        this.professionToHat = (Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType>)new Object2ObjectOpenHashMap();
        this.resourceManager = reloadableResourceManager;
        this.entityType = string;
        reloadableResourceManager.registerListener(this);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (((Entity)entity).isInvisible()) {
            return;
        }
        final VillagerData villagerData9 = ((VillagerDataContainer)entity).getVillagerData();
        final VillagerType villagerType10 = villagerData9.getType();
        final VillagerProfession villagerProfession11 = villagerData9.getProfession();
        final VillagerResourceMetadata.HatType hatType12 = this.<VillagerType>getHatType(this.villagerTypeToHat, "type", Registry.VILLAGER_TYPE, villagerType10);
        final VillagerResourceMetadata.HatType hatType13 = this.<VillagerProfession>getHatType(this.professionToHat, "profession", Registry.VILLAGER_PROFESSION, villagerProfession11);
        final M entityModel14 = this.getModel();
        this.bindTexture(this.findTexture("type", Registry.VILLAGER_TYPE.getId(villagerType10)));
        ((ModelWithHat)entityModel14).setHatVisible(hatType13 == VillagerResourceMetadata.HatType.a || (hatType13 == VillagerResourceMetadata.HatType.b && hatType12 != VillagerResourceMetadata.HatType.c));
        ((net.minecraft.client.render.entity.model.EntityModel<T>)entityModel14).render(entity, float2, float3, float5, float6, float7, float8);
        ((ModelWithHat)entityModel14).setHatVisible(true);
        if (villagerProfession11 != VillagerProfession.a && !((LivingEntity)entity).isChild()) {
            this.bindTexture(this.findTexture("profession", Registry.VILLAGER_PROFESSION.getId(villagerProfession11)));
            ((net.minecraft.client.render.entity.model.EntityModel<T>)entityModel14).render(entity, float2, float3, float5, float6, float7, float8);
            this.bindTexture(this.findTexture("profession_level", (Identifier)VillagerClothingFeatureRenderer.LEVEL_TO_ID.get(MathHelper.clamp(villagerData9.getLevel(), 1, VillagerClothingFeatureRenderer.LEVEL_TO_ID.size()))));
            ((net.minecraft.client.render.entity.model.EntityModel<T>)entityModel14).render(entity, float2, float3, float5, float6, float7, float8);
        }
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
    
    private Identifier findTexture(final String keyType, final Identifier keyId) {
        return new Identifier(keyId.getNamespace(), "textures/entity/" + this.entityType + "/" + keyType + "/" + keyId.getPath() + ".png");
    }
    
    public <K> VillagerResourceMetadata.HatType getHatType(final Object2ObjectMap<K, VillagerResourceMetadata.HatType> hatLookUp, final String keyType, final DefaultedRegistry<K> registry, final K key) {
        Resource resource5;
        VillagerResourceMetadata villagerResourceMetadata7;
        final VillagerResourceMetadata.HatType hatType;
        final Throwable t2;
        return (VillagerResourceMetadata.HatType)hatLookUp.computeIfAbsent(key, object4 -> {
            try {
                resource5 = this.resourceManager.getResource(this.findTexture(keyType, registry.getId(key)));
                try {
                    villagerResourceMetadata7 = resource5.<VillagerResourceMetadata>getMetadata((ResourceMetadataReader<VillagerResourceMetadata>)VillagerResourceMetadata.READER);
                    if (villagerResourceMetadata7 != null) {
                        villagerResourceMetadata7.getHatType();
                        return hatType;
                    }
                }
                catch (Throwable t) {
                    throw t;
                }
                finally {
                    if (resource5 != null) {
                        if (t2 != null) {
                            try {
                                resource5.close();
                            }
                            catch (Throwable t3) {
                                t2.addSuppressed(t3);
                            }
                        }
                        else {
                            resource5.close();
                        }
                    }
                }
            }
            catch (IOException ex) {}
            return VillagerResourceMetadata.HatType.a;
        });
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.professionToHat.clear();
        this.villagerTypeToHat.clear();
    }
    
    static {
        LEVEL_TO_ID = SystemUtil.<Int2ObjectMap>consume((Int2ObjectMap)new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
            int2ObjectOpenHashMap.put(1, new Identifier("stone"));
            int2ObjectOpenHashMap.put(2, new Identifier("iron"));
            int2ObjectOpenHashMap.put(3, new Identifier("gold"));
            int2ObjectOpenHashMap.put(4, new Identifier("emerald"));
            int2ObjectOpenHashMap.put(5, new Identifier("diamond"));
        });
    }
}
