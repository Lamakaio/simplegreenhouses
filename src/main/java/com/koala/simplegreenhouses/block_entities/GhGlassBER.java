package com.koala.simplegreenhouses.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

public class GhGlassBER implements BlockEntityRenderer<GhGlassBlockEntity> {

    BlockRenderDispatcher blockRenderDispatcher;

    // Add the constructor parameter for the lambda below. You may also use it to
    // get some context
    // to be stored in local fields, such as the entity renderer dispatcher, if
    // needed.
    public GhGlassBER(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    // This method is called every frame in order to render the block entity.
    // Parameters are:
    // - blockEntity: The block entity instance being rendered. Uses the generic
    // type passed to the super interface.
    // - partialTick: The amount of time, in fractions of a tick (0.0 to 1.0), that
    // has passed since the last tick.
    // - poseStack: The pose stack to render to.
    // - bufferSource: The buffer source to get vertex buffers from.
    // - packedLight: The light value of the block entity.
    // - packedOverlay: The current overlay value of the block entity, usually
    // OverlayTexture.NO_OVERLAY.
    // - cameraPos: The position of the renderer's camera.
    @Override
    public void render(GhGlassBlockEntity blockEntity, float partialTick, PoseStack stack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        blockRenderDispatcher.getModelRenderer()
                .tesselateBlock(blockEntity.getLevel(), blockRenderDispatcher.getBlockModel(blockEntity.state),
                        blockEntity.state, blockEntity.getBlockPos(), stack,
                        bufferSource.getBuffer(RenderType.translucent()), true, RandomSource.create(),
                        blockEntity.state.getSeed(blockEntity.getBlockPos()), packedOverlay, ModelData.EMPTY,
                        RenderType.translucent());
    }
}
