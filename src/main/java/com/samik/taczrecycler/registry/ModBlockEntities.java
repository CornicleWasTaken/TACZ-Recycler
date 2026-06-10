package com.samik.taczrecycler.registry;

import com.samik.taczrecycler.TACZRecyclerMod;
import com.samik.taczrecycler.blockentity.RecyclerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TACZRecyclerMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<RecyclerBlockEntity>> RECYCLER_BE =
            BLOCK_ENTITIES.register("recycler_be", () ->
                    BlockEntityType.Builder.of(RecyclerBlockEntity::new,
                            ModBlocks.RECYCLER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
