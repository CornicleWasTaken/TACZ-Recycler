package com.samik.taczrecycler.compat.jei;

import com.samik.taczrecycler.TACZRecyclerMod;
import com.samik.taczrecycler.recipe.RecyclingRecipe;
import com.samik.taczrecycler.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RecyclingRecipeCategory implements IRecipeCategory<RecyclingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(TACZRecyclerMod.MOD_ID, "recycling");
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(TACZRecyclerMod.MOD_ID, "textures/gui/recycler_gui.png");

    public static final RecipeType<RecyclingRecipe> RECYCLING_TYPE =
            new RecipeType<>(UID, RecyclingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RecyclingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RECYCLER.get()));
    }

    @Override
    public RecipeType<RecyclingRecipe> getRecipeType() {
        return RECYCLING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.tacz_recycler.recycler");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecyclingRecipe recipe, IFocusGroup focuses) {
        // Build a display stack representing the input
        ItemStack inputStack = recipe.getInput().getItems()[0].copy(); // Get the base item (e.g., ammo, gun)
        
        // Apply the required NBT so it renders correctly in JEI
        if (recipe.getNbtMatches() != null && !recipe.getNbtMatches().isEmpty()) {
            inputStack.setTag(recipe.getNbtMatches().copy());
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 56, 34).addItemStack(inputStack);

        if (recipe.getResults().size() > 0) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 18)
                    .addItemStack(recipe.getResults().get(0).item)
                    .addTooltipCallback((recipeSlotView, tooltip) -> 
                            tooltip.add(Component.literal(String.format("Chance: %.0f%%", recipe.getResults().get(0).chance * 100))));
        }
        if (recipe.getResults().size() > 1) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 34)
                    .addItemStack(recipe.getResults().get(1).item)
                    .addTooltipCallback((recipeSlotView, tooltip) -> 
                            tooltip.add(Component.literal(String.format("Chance: %.0f%%", recipe.getResults().get(1).chance * 100))));
        }
        if (recipe.getResults().size() > 2) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 50)
                    .addItemStack(recipe.getResults().get(2).item)
                    .addTooltipCallback((recipeSlotView, tooltip) -> 
                            tooltip.add(Component.literal(String.format("Chance: %.0f%%", recipe.getResults().get(2).chance * 100))));
        }
    }
}
