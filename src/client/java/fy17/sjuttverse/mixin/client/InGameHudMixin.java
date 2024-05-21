package fy17.sjuttverse.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "render", at = @At("RETURN"))
	public void onRender (DrawContext context, float tickDelta, CallbackInfo ci) {

		MinecraftClient client = MinecraftClient.getInstance();
		MatrixStack matrixStack = new MatrixStack();

		Text text = Text.of("FPS: " + client.fpsDebugString.split(" ")[0]);
		client.textRenderer.draw(text, 5.0F, 5.0F, 0xFFFFFF, false, matrixStack.peek().getPositionMatrix(), client.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
	}
}