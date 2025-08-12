// package com.koala.simplegreenhouses.datagen;

// import com.koala.simplegreenhouses.SimpleGreenhouses;
// import com.koala.simplegreenhouses.blocks.BlocksRegistrar;
// import com.koala.simplegreenhouses.blocks.GhGlassBlock;

// import net.minecraft.data.DataGenerator;
// import net.minecraft.data.PackOutput;
// import net.neoforged.bus.api.SubscribeEvent;
// import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
// import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
// import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
// import net.neoforged.neoforge.common.data.ExistingFileHelper;
// import net.neoforged.neoforge.data.event.GatherDataEvent;

// public class SgBlockStateProvider extends BlockStateProvider {
//     // Parameter values are provided by GatherDataEvent.
//     public SgBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
//         // Replace "examplemod" with your own mod id.
//         super(output, SimpleGreenhouses.MODID, existingFileHelper);
//     }

//     @Override
//     protected void registerStatesAndModels() {
//         // Get a variant block state builder.
//         VariantBlockStateBuilder variantBuilder = getVariantBuilder(BlocksRegistrar.GH_GLASS_BLOCK.get());
//         // Set models of all state to the linked model
//         variantBuilder.forAllStates(state -> {
//             return ConfiguredModel.builder()
//                     .modelFile(models().getExistingFile(state.getValue(GhGlassBlock.GLASS_MODEL).getLoc))
//                     .build();
//         });
//     }

//     @SubscribeEvent // on the mod event bus
//     public static void gatherData(GatherDataEvent event) {
//         DataGenerator generator = event.getGenerator();
//         PackOutput output = generator.getPackOutput();
//         ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

//         // other providers here
//         generator.addProvider(
//             event.includeClient(),
//             new SgBlockStateProvider(output, existingFileHelper)
//         );
//     }
// }