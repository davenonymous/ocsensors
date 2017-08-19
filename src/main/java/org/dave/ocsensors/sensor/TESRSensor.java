package org.dave.ocsensors.sensor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.ocsensors.OCSensors;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TESRSensor extends TileEntitySpecialRenderer<TileEntitySensor> {
    private IModel model;
    private IBakedModel bakedModel;

    private IBakedModel getBakedModel() {
        if(bakedModel == null) {
            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(OCSensors.MODID, "block/sensordish"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("ocsensors:blocks/sensor"));
        }

        return bakedModel;
    }

    @Override
    public void render(TileEntitySensor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();


        GlStateManager.translate(0.5, 0.0, 0.5);

        long angle = (System.currentTimeMillis() / 30) % 360;
        GlStateManager.rotate(angle, 0, 1, 0);

        GlStateManager.translate(-0.5, 0.0, -0.5);


        RenderHelper.disableStandardItemLighting();

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        World world = te.getWorld();


        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());


        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                world,
                getBakedModel(),
                world.getBlockState(te.getPos()),
                te.getPos(),
                Tessellator.getInstance().getBuffer(),
                false);
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}
