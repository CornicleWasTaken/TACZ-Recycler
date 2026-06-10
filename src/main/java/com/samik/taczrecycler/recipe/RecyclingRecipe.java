package com.samik.taczrecycler.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.samik.taczrecycler.TACZRecyclerMod;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecyclingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final CompoundTag nbtMatches;
    private final List<Result> results;
    private final int processingTime;
    private final boolean ignoreNbt; // Added flag to optionally ignore NBT for un-dictated items

    public RecyclingRecipe(ResourceLocation id, Ingredient input, CompoundTag nbtMatches, List<Result> results, int processingTime, boolean ignoreNbt) {
        this.id = id;
        this.input = input;
        this.nbtMatches = nbtMatches;
        this.results = results;
        this.processingTime = processingTime;
        this.ignoreNbt = ignoreNbt;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        ItemStack inputStack = pContainer.getItem(0);
        if (!input.test(inputStack)) {
            return false;
        }

        if (ignoreNbt) {
            return true;
        }

        if (nbtMatches != null && !nbtMatches.isEmpty()) {
            if (!inputStack.hasTag()) return false;
            CompoundTag stackTag = inputStack.getTag();
            for (String key : nbtMatches.getAllKeys()) {
                if (!stackTag.contains(key) || !stackTag.get(key).equals(nbtMatches.get(key))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public Ingredient getInput() {
        return input;
    }

    public CompoundTag getNbtMatches() {
        return nbtMatches;
    }

    public List<Result> getResults() {
        return results;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public static class Result {
        public final ItemStack item;
        public final float chance;

        public Result(ItemStack item, float chance) {
            this.item = item;
            this.chance = chance;
        }
    }

    public static class Type implements RecipeType<RecyclingRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "recycling";
    }

    public static class Serializer implements RecipeSerializer<RecyclingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(TACZRecyclerMod.MOD_ID, "recycling");

        @Override
        public RecyclingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            
            CompoundTag nbtMatches = new CompoundTag();
            if (pSerializedRecipe.has("nbt")) {
                JsonObject nbtObj = GsonHelper.getAsJsonObject(pSerializedRecipe, "nbt");
                for (String key : nbtObj.keySet()) {
                    JsonElement el = nbtObj.get(key);
                    if (el.isJsonPrimitive()) {
                        if (el.getAsJsonPrimitive().isNumber()) {
                            nbtMatches.putInt(key, el.getAsInt());
                        } else if (el.getAsJsonPrimitive().isBoolean()) {
                            nbtMatches.putBoolean(key, el.getAsBoolean());
                        } else {
                            nbtMatches.putString(key, el.getAsString());
                        }
                    }
                }
            }

            boolean ignoreNbt = GsonHelper.getAsBoolean(pSerializedRecipe, "ignoreNbt", false);

            JsonArray resultsArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "results");
            List<Result> results = new ArrayList<>();
            for (JsonElement element : resultsArray) {
                JsonObject resultObj = element.getAsJsonObject();
                ResourceLocation itemLoc = new ResourceLocation(GsonHelper.getAsString(resultObj, "item"));
                int count = GsonHelper.getAsInt(resultObj, "count", 1);
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(itemLoc), count);
                float chance = GsonHelper.getAsFloat(resultObj, "chance", 1.0f);
                results.add(new Result(stack, chance));
            }

            int processingTime = GsonHelper.getAsInt(pSerializedRecipe, "processingTime", 400);

            return new RecyclingRecipe(pRecipeId, input, nbtMatches, results, processingTime, ignoreNbt);
        }

        @Override
        public @Nullable RecyclingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            CompoundTag nbtMatches = pBuffer.readNbt();
            
            int resultsSize = pBuffer.readInt();
            List<Result> results = new ArrayList<>();
            for (int i = 0; i < resultsSize; i++) {
                results.add(new Result(pBuffer.readItem(), pBuffer.readFloat()));
            }
            
            int processingTime = pBuffer.readInt();
            boolean ignoreNbt = pBuffer.readBoolean();

            return new RecyclingRecipe(pRecipeId, input, nbtMatches, results, processingTime, ignoreNbt);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, RecyclingRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeNbt(pRecipe.nbtMatches);
            
            pBuffer.writeInt(pRecipe.results.size());
            for (Result result : pRecipe.results) {
                pBuffer.writeItem(result.item);
                pBuffer.writeFloat(result.chance);
            }
            
            pBuffer.writeInt(pRecipe.processingTime);
            pBuffer.writeBoolean(pRecipe.ignoreNbt);
        }
    }
}