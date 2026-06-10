package com.samik.taczrecycler.registry;

import com.samik.taczrecycler.TACZRecyclerMod;
import com.samik.taczrecycler.recipe.RecyclingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TACZRecyclerMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<RecyclingRecipe>> RECYCLING_SERIALIZER =
            SERIALIZERS.register("recycling", () -> RecyclingRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
