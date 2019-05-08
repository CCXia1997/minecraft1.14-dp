package net.minecraft.recipe;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.cooking.CampfireCookingRecipe;
import net.minecraft.recipe.cooking.SmokingRecipe;
import net.minecraft.recipe.cooking.BlastingRecipe;
import net.minecraft.recipe.cooking.SmeltingRecipe;
import net.minecraft.recipe.cooking.CookingRecipeSerializer;
import net.minecraft.recipe.crafting.SuspiciousStewRecipe;
import net.minecraft.recipe.crafting.ShulkerBoxColoringRecipe;
import net.minecraft.recipe.crafting.ShieldDecorationRecipe;
import net.minecraft.recipe.crafting.BannerDuplicateRecipe;
import net.minecraft.recipe.crafting.TippedArrowRecipe;
import net.minecraft.recipe.crafting.FireworkStarFadeRecipe;
import net.minecraft.recipe.crafting.FireworkStarRecipe;
import net.minecraft.recipe.crafting.FireworkRocketRecipe;
import net.minecraft.recipe.crafting.MapExtendingRecipe;
import net.minecraft.recipe.crafting.MapCloningRecipe;
import net.minecraft.recipe.crafting.BookCloningRecipe;
import net.minecraft.recipe.crafting.ArmorDyeRecipe;
import net.minecraft.recipe.crafting.ShapelessRecipe;
import net.minecraft.recipe.crafting.ShapedRecipe;

public interface RecipeSerializer<T extends Recipe<?>>
{
    public static final RecipeSerializer<ShapedRecipe> SHAPED = RecipeSerializer.<ShapedRecipe.Serializer, Recipe>register("crafting_shaped", new ShapedRecipe.Serializer());
    public static final RecipeSerializer<ShapelessRecipe> SHAPELESS = RecipeSerializer.<ShapelessRecipe.Serializer, Recipe>register("crafting_shapeless", new ShapelessRecipe.Serializer());
    public static final SpecialRecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = RecipeSerializer.<SpecialRecipeSerializer<ArmorDyeRecipe>, Recipe>register("crafting_special_armordye", new SpecialRecipeSerializer<ArmorDyeRecipe>(ArmorDyeRecipe::new));
    public static final SpecialRecipeSerializer<BookCloningRecipe> BOOK_CLONING = RecipeSerializer.<SpecialRecipeSerializer<BookCloningRecipe>, Recipe>register("crafting_special_bookcloning", new SpecialRecipeSerializer<BookCloningRecipe>(BookCloningRecipe::new));
    public static final SpecialRecipeSerializer<MapCloningRecipe> MAP_CLONING = RecipeSerializer.<SpecialRecipeSerializer<MapCloningRecipe>, Recipe>register("crafting_special_mapcloning", new SpecialRecipeSerializer<MapCloningRecipe>(MapCloningRecipe::new));
    public static final SpecialRecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = RecipeSerializer.<SpecialRecipeSerializer<MapExtendingRecipe>, Recipe>register("crafting_special_mapextending", new SpecialRecipeSerializer<MapExtendingRecipe>(MapExtendingRecipe::new));
    public static final SpecialRecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = RecipeSerializer.<SpecialRecipeSerializer<FireworkRocketRecipe>, Recipe>register("crafting_special_firework_rocket", new SpecialRecipeSerializer<FireworkRocketRecipe>(FireworkRocketRecipe::new));
    public static final SpecialRecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = RecipeSerializer.<SpecialRecipeSerializer<FireworkStarRecipe>, Recipe>register("crafting_special_firework_star", new SpecialRecipeSerializer<FireworkStarRecipe>(FireworkStarRecipe::new));
    public static final SpecialRecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = RecipeSerializer.<SpecialRecipeSerializer<FireworkStarFadeRecipe>, Recipe>register("crafting_special_firework_star_fade", new SpecialRecipeSerializer<FireworkStarFadeRecipe>(FireworkStarFadeRecipe::new));
    public static final SpecialRecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = RecipeSerializer.<SpecialRecipeSerializer<TippedArrowRecipe>, Recipe>register("crafting_special_tippedarrow", new SpecialRecipeSerializer<TippedArrowRecipe>(TippedArrowRecipe::new));
    public static final SpecialRecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = RecipeSerializer.<SpecialRecipeSerializer<BannerDuplicateRecipe>, Recipe>register("crafting_special_bannerduplicate", new SpecialRecipeSerializer<BannerDuplicateRecipe>(BannerDuplicateRecipe::new));
    public static final SpecialRecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = RecipeSerializer.<SpecialRecipeSerializer<ShieldDecorationRecipe>, Recipe>register("crafting_special_shielddecoration", new SpecialRecipeSerializer<ShieldDecorationRecipe>(ShieldDecorationRecipe::new));
    public static final SpecialRecipeSerializer<ShulkerBoxColoringRecipe> SHULKER_BOX = RecipeSerializer.<SpecialRecipeSerializer<ShulkerBoxColoringRecipe>, Recipe>register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<ShulkerBoxColoringRecipe>(ShulkerBoxColoringRecipe::new));
    public static final SpecialRecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = RecipeSerializer.<SpecialRecipeSerializer<SuspiciousStewRecipe>, Recipe>register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<SuspiciousStewRecipe>(SuspiciousStewRecipe::new));
    public static final CookingRecipeSerializer<SmeltingRecipe> SMELTING = RecipeSerializer.<CookingRecipeSerializer<SmeltingRecipe>, Recipe>register("smelting", new CookingRecipeSerializer<SmeltingRecipe>(SmeltingRecipe::new, 200));
    public static final CookingRecipeSerializer<BlastingRecipe> BLASTING = RecipeSerializer.<CookingRecipeSerializer<BlastingRecipe>, Recipe>register("blasting", new CookingRecipeSerializer<BlastingRecipe>(BlastingRecipe::new, 100));
    public static final CookingRecipeSerializer<SmokingRecipe> SMOKING = RecipeSerializer.<CookingRecipeSerializer<SmokingRecipe>, Recipe>register("smoking", new CookingRecipeSerializer<SmokingRecipe>(SmokingRecipe::new, 100));
    public static final CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeSerializer.<CookingRecipeSerializer<CampfireCookingRecipe>, Recipe>register("campfire_cooking", new CookingRecipeSerializer<CampfireCookingRecipe>(CampfireCookingRecipe::new, 100));
    public static final RecipeSerializer<StonecuttingRecipe> s = RecipeSerializer.<CuttingRecipe.Serializer<StonecuttingRecipe>, Recipe>register("stonecutting", new CuttingRecipe.Serializer<StonecuttingRecipe>(StonecuttingRecipe::new));
    
    T read(final Identifier arg1, final JsonObject arg2);
    
    T read(final Identifier arg1, final PacketByteBuf arg2);
    
    void write(final PacketByteBuf arg1, final T arg2);
    
    default <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(final String id, final S serializer) {
        return Registry.<S>register(Registry.RECIPE_SERIALIZER, id, serializer);
    }
}
