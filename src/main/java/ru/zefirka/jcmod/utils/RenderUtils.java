package ru.zefirka.jcmod.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import ru.zefirka.jcmod.JCMod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderUtils {
    private static final int GL_FRONT_AND_BACK = 1032;
    private static final int GL_LINE = 6913;
    private static final int GL_FILL = 6914;
    private static final int GL_LINES = 1;
    private static final float OPACITY = 1;

    public static final List<RenderBlockProps> syncRenderList = Collections.synchronizedList(new ArrayList<>());

    @SuppressWarnings("deprecation")
    public static void renderBlocks(RenderWorldLastEvent event) {
        Vector3d view = JCMod.MINECRAFT.gameRenderer.getMainCamera().getPosition();

        MatrixStack stack = event.getMatrixStack();
        stack.translate(-view.x, -view.y, -view.z); // translate

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(stack.last().pose());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Profile.BLOCKS.apply(); // Sets GL state for block drawing

        syncRenderList.forEach(blockProps -> {
            if (blockProps == null) {
                return;
            }
            RenderSystem.pushMatrix();
            RenderSystem.translated(blockProps.getPos().getX(), blockProps.getPos().getY(), blockProps.getPos().getZ());
            buffer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR );
            renderBlock(buffer, blockProps);
            tessellator.end();
            RenderSystem.popMatrix();
        } );
        Profile.BLOCKS.clean();
        RenderSystem.popMatrix();
    }

    private static void renderBlock(IVertexBuilder buffer, RenderBlockProps props) {
        final float red = (props.getColor() >> 16 & 0xff) / 255f;
        final float green = (props.getColor() >> 8 & 0xff) / 255f;
        final float blue = (props.getColor() & 0xff) / 255f;

        //.vertex?
        buffer.vertex(0, 1, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 1, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 1, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 1, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 1, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 1, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 1, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 1, 0).color(red, green, blue, OPACITY).endVertex();

        // BOTTOM
        buffer.vertex(1, 0, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 0, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 0, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 0, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 0, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 0, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 0, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 0, 0).color(red, green, blue, OPACITY).endVertex();

        // Edgevertex
        buffer.vertex(1, 0, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 1, 1).color(red, green, blue, OPACITY).endVertex();

        // Edgevertex
        buffer.vertex(1, 0, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(1, 1, 0).color(red, green, blue, OPACITY).endVertex();

        // Edgevertex
        buffer.vertex(0, 0, 1).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 1, 1).color(red, green, blue, OPACITY).endVertex();

        // Edgevertex
        buffer.vertex(0, 0, 0).color(red, green, blue, OPACITY).endVertex();
        buffer.vertex(0, 1, 0).color(red, green, blue, OPACITY).endVertex();
    }


    /**
     * OpenGL Profiles used for rendering blocks and entities
     */
    private enum Profile
    {
        BLOCKS {
            @Override
            public void apply()
            {
                RenderSystem.disableTexture();
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask( false );
                RenderSystem.polygonMode( GL_FRONT_AND_BACK, GL_LINE );
                RenderSystem.blendFunc( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA );
                RenderSystem.enableBlend();
                RenderSystem.lineWidth(2f);
            }

            @Override
            public void clean()
            {
                RenderSystem.polygonMode( GL_FRONT_AND_BACK, GL_FILL );
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask( true );
                RenderSystem.enableTexture();
            }
        };

        Profile() {}
        protected abstract void apply();
        protected abstract void clean();
    }
}

