package com.github.yajatkaul.mega_showdown.render.layerEntities;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface LayerEntityInterface {
    double animSeconds = 0.0;
    long lastTimeNs = -1L;

    default void render(PokemonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

    }
}
