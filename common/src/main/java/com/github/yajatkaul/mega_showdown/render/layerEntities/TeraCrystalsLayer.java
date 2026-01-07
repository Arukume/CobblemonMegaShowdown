package com.github.yajatkaul.mega_showdown.render.layerEntities;


import com.cobblemon.mod.common.client.render.MatrixWrapper;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockActiveAnimation;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.yajatkaul.mega_showdown.MegaShowdown;
import com.github.yajatkaul.mega_showdown.codec.teraHat.HatCodec;
import com.github.yajatkaul.mega_showdown.render.HatsDataLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kotlin.Unit;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeraCrystalsLayer implements LayerEntityInterface{

    public void render(PokemonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        long now = System.nanoTime();

        if (lastTimeNs != -1L) {
            double deltaSeconds = (now - lastTimeNs) / 1_000_000_000.0;
            animSeconds += deltaSeconds;
        }

        lastTimeNs = now;

        float mega_showdown$teraCrystalDuration = new BedrockActiveAnimation(
                BedrockAnimationRepository.INSTANCE.getAnimation("terastal_transformation", "animation.terastal_transformation.transform")
        ).getDuration();

        if (mega_showdown$teraCrystalState.getAnimationSeconds() >= mega_showdown$teraCrystalDuration) {
            mega_showdown$teraCrystalPlayed = true;
            mega_showdown$teraCrystalPass = false;
            animSeconds = 0.0;
            lastTimeNs = -1L;
            mega_showdown$teraCrystalState.resetAnimation();
            entity.after(3f, () -> {
                mega_showdown$teraCrystalPlayed = false;
                return Unit.INSTANCE;
            });
            return;
        } else if (mega_showdown$teraCrystalState.getAnimationSeconds() >= mega_showdown$teraCrystalDuration - 0.3) {
            mega_showdown$teraCrystalPass = true;
        }

        float ticks = (float) (animSeconds * 20f);

        int age = (int) ticks;
        float pt = ticks - age;

        mega_showdown$teraCrystalState.updateAge(age);
        mega_showdown$teraCrystalState.setPartialTicks(pt);

        mega_showdown$teraCrystalState.setCurrentAspects(mega_showdown$teraCrystalAspects);

        Map<String, MatrixWrapper> locatorStates = clientDelegate.getLocatorStates();
        MatrixWrapper rootLocator = locatorStates.get("root");

        if (rootLocator == null) return;

        HatCodec crystalSize = HatsDataLoader.REGISTRY.get(ResourceLocation.fromNamespaceAndPath(MegaShowdown.MOD_ID, pokemon.getSpecies().getName().toLowerCase(Locale.ROOT)));

        // Get model and texture
        PosableModel model = VaryingModelRepository.INSTANCE.getPoser(mega_showdown$teraCrystalPoserId, mega_showdown$teraCrystalState);
        model.context = mega_showdown$context;
        ResourceLocation texture = VaryingModelRepository.INSTANCE.getTexture(mega_showdown$teraCrystalPoserId, mega_showdown$teraCrystalState);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        model.setBufferProvider(buffer);
        mega_showdown$teraCrystalState.setCurrentModel(model);

        // Setup context
        mega_showdown$context.put(RenderContext.Companion.getASPECTS(), mega_showdown$teraCrystalAspects);
        mega_showdown$context.put(RenderContext.Companion.getTEXTURE(), texture);
        mega_showdown$context.put(RenderContext.Companion.getSPECIES(), mega_showdown$teraCrystalPoserId);
        mega_showdown$context.put(RenderContext.Companion.getPOSABLE_STATE(), mega_showdown$teraCrystalState);

        poseStack.pushPose();

        poseStack.mulPose(rootLocator.getMatrix());
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(0.08, 0.0, 0.0);

        poseStack.scale(1.5f, 1.5f, 1.5f);

        if (crystalSize != null) {
            List<Float> scale = HatCodec.getScaleForHat(pokemon, "msd:tera_crystal", crystalSize);
            poseStack.scale(scale.get(0), scale.get(1), scale.get(2));
        }

        // Apply animations
        model.applyAnimations(
                null,
                mega_showdown$teraCrystalState,
                0F,
                0F,
                ticks,
                0F,
                0F

        );

        // Render
        model.render(mega_showdown$context, poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -0x1);

        model.withLayerContext(
                buffer,
                mega_showdown$teraCrystalState,
                VaryingModelRepository.INSTANCE.getLayers(mega_showdown$teraCrystalPoserId, mega_showdown$teraCrystalState),
                () -> {
                    model.render(mega_showdown$context, poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -0x1);
                    return Unit.INSTANCE;
                }
        );
        model.setDefault();
        poseStack.popPose();
    }
}
