package com.koala.simplegreenhouses.datagen;

import java.util.concurrent.CompletableFuture;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.blocks.BlocksRegistrar;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class SgBlockTagsProvider extends BlockTagsProvider {

    private static TagKey<Block> FARMLAND = Tags.Blocks.VILLAGER_FARMLANDS;

    // Get parameters from one of the `GatherDataEvent`s.
    public SgBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SimpleGreenhouses.MODID, existingFileHelper);
    }

    // Add your tag entries here.
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // Create a TagAppender of registry objects for our tag. This could also be e.g. a vanilla or NeoForge tag.
        this.tag(FARMLAND)
            // Add entries. This is a vararg parameter.
            // Key tag providers must provide ResourceKeys here instead of the actual objects.
            .add(BlocksRegistrar.RICH_SOIL_BLOCK.get());
    }

    @SubscribeEvent // on the mod event bus
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // other providers here
        event.getGenerator().addProvider(
            event.includeServer(),
            new SgBlockTagsProvider(output, lookupProvider, existingFileHelper)
        );
    }
}