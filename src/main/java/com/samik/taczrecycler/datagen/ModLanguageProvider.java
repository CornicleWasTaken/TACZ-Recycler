package com.samik.taczrecycler.datagen;

import com.samik.taczrecycler.TACZRecyclerMod;
import com.samik.taczrecycler.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, TACZRecyclerMod.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.RECYCLER.get(), "TACZ Recycler");
    }
}
