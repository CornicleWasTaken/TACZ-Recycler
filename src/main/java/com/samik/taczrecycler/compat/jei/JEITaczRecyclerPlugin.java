package com.samik.taczrecycler.compat.jei;

import com.samik.taczrecycler.TACZRecyclerMod;
import com.samik.taczrecycler.recipe.RecyclingRecipe;
import com.samik.taczrecycler.registry.ModBlocks;
import com.samik.taczrecycler.screen.RecyclerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEITaczRecyclerPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TACZRecyclerMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RecyclingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RecyclingRecipe> recyclingRecipes = recipeManager.getAllRecipesFor(RecyclingRecipe.Type.INSTANCE);
        registration.addRecipes(RecyclingRecipeCategory.RECYCLING_TYPE, recyclingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RECYCLER.get()), RecyclingRecipeCategory.RECYCLING_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(RecyclerScreen.class, 85, 34, 24, 17,
                RecyclingRecipeCategory.RECYCLING_TYPE);
    }
}
