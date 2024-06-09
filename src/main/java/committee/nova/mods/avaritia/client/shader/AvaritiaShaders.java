package committee.nova.mods.avaritia.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.client.shader.CCShaderInstance;
import committee.nova.mods.avaritia.api.client.shader.CCUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

/**
 * Name: Avaritia-forge / AvaritiaShaders
 * Author: cnlimiter
 * CreateTime: 2023/9/18 1:37
 * Description:
 */

@Mod.EventBusSubscriber(modid = Static.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AvaritiaShaders {
    private static final float[] COSMIC_UVS = new float[40];
    private static int renderTime;
    private static float renderFrame;
    public static CCShaderInstance cosmicShader;
    public static CCUniform cosmicTime;
    public static CCUniform cosmicYaw;
    public static CCUniform cosmicPitch;
    public static CCUniform cosmicExternalScale;
    public static CCUniform cosmicOpacity;
    public static CCUniform cosmicUVs;
    public static RenderType COSMIC_RENDER_TYPE = RenderType.create("avaritia:cosmic",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 209715, true, false,
            RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> cosmicShader))
                    .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                    .createCompositeState(true)
    );


    public static void onRegisterShaders(RegisterShadersEvent event){
        event.registerShader(CCShaderInstance.create(event.getResourceProvider(), new ResourceLocation(Static.MOD_ID, "cosmic"), DefaultVertexFormat.BLOCK), e -> {
            cosmicShader = (CCShaderInstance)e;
            cosmicTime = Objects.requireNonNull(cosmicShader.getUniform("time"));
            cosmicYaw = Objects.requireNonNull(cosmicShader.getUniform("yaw"));
            cosmicPitch = Objects.requireNonNull(cosmicShader.getUniform("pitch"));
            cosmicExternalScale = Objects.requireNonNull(cosmicShader.getUniform("externalScale"));
            cosmicOpacity = Objects.requireNonNull(cosmicShader.getUniform("opacity"));
            cosmicUVs = Objects.requireNonNull(cosmicShader.getUniform("cosmicuvs"));
            cosmicTime.set((float)renderTime + renderFrame);
            cosmicShader.onApply(() -> {
                cosmicTime.set((float)renderTime + renderFrame);
            });
        });

    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            //Static.LOGGER.info(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(Static.MOD_ID, "misc/halo")).toString());
            for (int i = 0; i < 10; ++i) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(Static.MOD_ID, "misc/cosmic_" + i));
                //SpriteRegistryHandler.sprites.getTextureLocations().forEach(Static.LOGGER::info);
                AvaritiaShaders.COSMIC_UVS[i * 4] = sprite.getU0();
                AvaritiaShaders.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
                AvaritiaShaders.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
                AvaritiaShaders.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
            }
            if (cosmicUVs != null) cosmicUVs.set(COSMIC_UVS);
        }
    }


    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (!Minecraft.getInstance().isPaused() && event.phase == TickEvent.Phase.END) {
            ++renderTime;
        }
    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (!Minecraft.getInstance().isPaused() && event.phase == TickEvent.Phase.START) {
            renderFrame = event.renderTickTime;
        }
    }
}
