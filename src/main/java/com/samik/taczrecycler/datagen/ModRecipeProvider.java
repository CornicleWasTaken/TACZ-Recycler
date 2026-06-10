package com.samik.taczrecycler.datagen;

import com.samik.taczrecycler.recipe.RecyclingRecipeBuilder;
import com.samik.taczrecycler.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        
        // --- Crafting Recipe for Recycler Block ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RECYCLER.get())
                .pattern("ICI")
                .pattern("WWW")
                .pattern("W W")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('W', ItemTags.LOGS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(pWriter);

        ResourceLocation taczAmmo = new ResourceLocation("tacz", "ammo");
        ResourceLocation taczGun = new ResourceLocation("tacz", "modern_kinetic_gun");
        ResourceLocation taczAttachment = new ResourceLocation("tacz", "attachment");

        // --- Specific Ammo (Strictly TACZ Unidict compatibility) ---
        
        // 1. Pistol Ammo
        new RecyclingRecipeBuilder(taczAmmo, 200)
                .addNbtMatch("AmmoId", "tacz_unidict:pistol")
                .addResult(Items.GUNPOWDER, 0.15f)
                .addResult(Items.COPPER_INGOT, 0.25f)
                .save(pWriter, new ResourceLocation("tacz_recycler", "pistol_ammo_recycling"));

        // 2. Rifle Ammo
        new RecyclingRecipeBuilder(taczAmmo, 200)
                .addNbtMatch("AmmoId", "tacz_unidict:rifle")
                .addResult(Items.GUNPOWDER, 0.25f)
                .addResult(Items.COPPER_INGOT, 0.35f)
                .save(pWriter, new ResourceLocation("tacz_recycler", "rifle_ammo_recycling"));

        // 3. Sniper Ammo
        new RecyclingRecipeBuilder(taczAmmo, 200)
                .addNbtMatch("AmmoId", "tacz_unidict:sniper")
                .addResult(Items.GUNPOWDER, 0.35f)
                .addResult(Items.COPPER_INGOT, 0.25f)
                .addResult(Items.LAPIS_LAZULI, 0.15f)
                .save(pWriter, new ResourceLocation("tacz_recycler", "sniper_ammo_recycling"));

        // 4. Shotgun Shells
        new RecyclingRecipeBuilder(taczAmmo, 200)
                .addNbtMatch("AmmoId", "tacz_unidict:shot")
                .addResult(Items.GUNPOWDER, 0.20f)
                .addResult(Items.COPPER_INGOT, 0.15f)
                .addResult(Items.IRON_NUGGET, 0.15f)
                .save(pWriter, new ResourceLocation("tacz_recycler", "shotgun_ammo_recycling"));

        // 5. General Barrel
        new RecyclingRecipeBuilder(taczAmmo, 200)
                .addNbtMatch("AmmoId", "tacz_unidict:barrel")
                .addResult(Items.COPPER_INGOT, 1.25f, 1)
                .addResult(Items.GUNPOWDER, 1.0f, 1)
                .addResult(Items.IRON_INGOT, 0.25f, 1)
                .save(pWriter, new ResourceLocation("tacz_recycler", "barrel_ammo_recycling"));

        // 6. Fuel Tanks
        new RecyclingRecipeBuilder(taczAmmo, 200)
                .addNbtMatch("AmmoId", "tacz_unidict:fuel_tank")
                .addResult(Items.IRON_INGOT, 1.0f, 1)
                .addResult(Items.REDSTONE, 3.5f, 3)
                .addResult(Items.BLAZE_POWDER, 0.5f, 1)
                .save(pWriter, new ResourceLocation("tacz_recycler", "fuel_tank_ammo_recycling"));

        // --- Generic Gun Recycling ---
        // Catch-all for ANY TACZ Gun (ignoring NBT)
        new RecyclingRecipeBuilder(taczGun, 200)
                .ignoreNbt()
                .addResult(Items.IRON_INGOT, 1.0f, 2)
                .addResult(Items.COPPER_INGOT, 0.75f, 1)
                .addResult(Items.GUNPOWDER, 0.25f, 1)
                .save(pWriter, new ResourceLocation("tacz_recycler", "generic_gun_recycling"));

        // --- Generic Attachment Recycling ---
        // Catch-all for ANY TACZ Attachment (ignoring NBT)
        new RecyclingRecipeBuilder(taczAttachment, 200)
                .ignoreNbt()
                .addResult(Items.IRON_NUGGET, 1.0f, 4)
                .addResult(Items.COPPER_INGOT, 0.5f, 2)
                .addResult(Items.REDSTONE, 0.25f, 1)
                .save(pWriter, new ResourceLocation("tacz_recycler", "generic_attachment_recycling"));
    }
}
