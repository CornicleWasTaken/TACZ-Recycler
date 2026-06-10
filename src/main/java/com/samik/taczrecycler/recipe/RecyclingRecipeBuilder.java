package com.samik.taczrecycler.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.samik.taczrecycler.TACZRecyclerMod;
import com.samik.taczrecycler.registry.ModRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RecyclingRecipeBuilder implements RecipeBuilder {
    private Ingredient input;
    private ResourceLocation rawInputId;
    private final int processingTime;
    private final CompoundTag nbtMatches = new CompoundTag();
    private final List<Result> results = new ArrayList<>();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private boolean ignoreNbt = false;

    public RecyclingRecipeBuilder(Ingredient input, int processingTime) {
        this.input = input;
        this.processingTime = processingTime;
    }

    public RecyclingRecipeBuilder(ResourceLocation rawInputId, int processingTime) {
        this.rawInputId = rawInputId;
        this.processingTime = processingTime;
    }

    public RecyclingRecipeBuilder ignoreNbt() {
        this.ignoreNbt = true;
        return this;
    }

    public RecyclingRecipeBuilder addNbtMatch(String key, String value) {
        this.nbtMatches.putString(key, value);
        return this;
    }

    public RecyclingRecipeBuilder addNbtMatch(String key, int value) {
        this.nbtMatches.putInt(key, value);
        return this;
    }

    public RecyclingRecipeBuilder addNbtMatch(String key, boolean value) {
        this.nbtMatches.putBoolean(key, value);
        return this;
    }

    public RecyclingRecipeBuilder addResult(Item item, float chance) {
        this.results.add(new Result(item, chance, 1));
        return this;
    }

    public RecyclingRecipeBuilder addResult(Item item, float chance, int count) {
        this.results.add(new Result(item, chance, count));
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return results.isEmpty() ? null : results.get(0).item;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(pRecipeId))
                .requirements(RequirementsStrategy.OR);

        pFinishedRecipeConsumer.accept(new ResultRecipe(pRecipeId, this.input, this.rawInputId, this.processingTime, this.nbtMatches, this.results,
                this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + pRecipeId.getPath()), this.ignoreNbt));
    }

    private record Result(Item item, float chance, int count) {}

    public static class ResultRecipe implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient input;
        private final ResourceLocation rawInputId;
        private final int processingTime;
        private final CompoundTag nbtMatches;
        private final List<Result> results;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final boolean ignoreNbt;

        public ResultRecipe(ResourceLocation id, Ingredient input, ResourceLocation rawInputId, int processingTime, CompoundTag nbtMatches,
                            List<Result> results, Advancement.Builder advancement, ResourceLocation advancementId, boolean ignoreNbt) {
            this.id = id;
            this.input = input;
            this.rawInputId = rawInputId;
            this.processingTime = processingTime;
            this.nbtMatches = nbtMatches;
            this.results = results;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.ignoreNbt = ignoreNbt;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (rawInputId != null) {
                JsonObject ingredientObj = new JsonObject();
                ingredientObj.addProperty("item", rawInputId.toString());
                pJson.add("ingredient", ingredientObj);
            } else {
                pJson.add("ingredient", input.toJson());
            }

            if (!nbtMatches.isEmpty()) {
                JsonObject nbtObj = new JsonObject();
                for (String key : nbtMatches.getAllKeys()) {
                    byte typeId = nbtMatches.getTagType(key);
                    if (typeId == CompoundTag.TAG_STRING) {
                        nbtObj.addProperty(key, nbtMatches.getString(key));
                    } else if (typeId == CompoundTag.TAG_INT) {
                        nbtObj.addProperty(key, nbtMatches.getInt(key));
                    }
                }
                pJson.add("nbt", nbtObj);
            }

            if (ignoreNbt) {
                pJson.addProperty("ignoreNbt", true);
            }

            JsonArray resultsArray = new JsonArray();
            for (Result result : results) {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(result.item).toString());
                if (result.count > 1) {
                    resultObj.addProperty("count", result.count);
                }
                resultObj.addProperty("chance", result.chance);
                resultsArray.add(resultObj);
            }
            pJson.add("results", resultsArray);

            pJson.addProperty("processingTime", processingTime);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.RECYCLING_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }
}
