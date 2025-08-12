package com.koala.simplegreenhouses.datagen;

import java.util.List;
import java.util.Set;

import com.koala.simplegreenhouses.blocks.BlocksRegistrar;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class SgLootProvider {

    @SubscribeEvent // on the mod event bus
    public static void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Set.of(), List.of(new SubProviderEntry(
                        SgBlockLootSubProvided::new,
                        LootContextParamSets.BLOCK // it makes sense to use BLOCK here
                )), event.getLookupProvider()));
    }

    public static class SgBlockLootSubProvided extends BlockLootSubProvider {
        public SgBlockLootSubProvided(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
        }

        // The contents of this Iterable are used for validation.
        // We return an Iterable over our block registry's values here.
        @Override
        protected Iterable<Block> getKnownBlocks() {
            // The contents of our DeferredRegister.
            return BlocksRegistrar.BLOCKS.getEntries()
                    .stream()
                    // Cast to Block here, otherwise it will be a ? extends Block and Java will
                    // complain.
                    .map(e -> (Block) e.value())
                    .toList();
        }

        // Actually add our loot tables.
        @Override
        protected void generate() {
            this.dropSelf(BlocksRegistrar.RICH_SOIL_BLOCK.get());
            this.dropSelf(BlocksRegistrar.GH_CONTROLLER_BLOCK.get());
            this.dropSelf(BlocksRegistrar.GH_GLASS_BLOCK.get());
        }
    }
}