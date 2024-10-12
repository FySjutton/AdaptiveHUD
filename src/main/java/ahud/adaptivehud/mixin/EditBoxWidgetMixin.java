package ahud.adaptivehud.mixin;

import ahud.adaptivehud.Tools;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EditBoxWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Stack;

@Mixin(EditBoxWidget.class)
public class EditBoxWidgetMixin {
    @Unique
    private Tools tools = new Tools();
    @Unique
    private Stack<Character> startPositions = new Stack<>();

    @Inject(method = "renderContents", at = @At("HEAD"))
    private void start(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        startPositions = new Stack<>();
        startPositions.add('0');
    }

    // Redirect the calls to context.drawTextWithShadow to your custom method
    @Redirect(
            method = "renderContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
                    ordinal = 0
            )
    )
    private int redirectDrawTextWithShadow(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color) {
        // Call your custom method instead of the original drawTextWithShadow
//        startPositions = new Stack<>();
//        startPositions.add('0');
        int result = tools.colorTextRenderer(instance, textRenderer, text, x, y, startPositions);
//        LOGGER.info("ran 1");
        return result;
    }

    @Redirect(
            method = "renderContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
                    ordinal = 1
            )
    )
    private int redirectDrawTextWithShadow2(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color) {
        // Call your custom method instead of the original drawTextWithShadow
//        LOGGER.info(String.valueOf(startPositions));
        int result = tools.colorTextRenderer(instance, textRenderer, text, x, y, startPositions);
//        LOGGER.info("ran 2");
        return result;
    }

    @Redirect(
            method = "renderContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
                    ordinal = 2
            )
    )
    private int redirectDrawTextWithShadow3(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color) {
        // Call your custom method instead of the original drawTextWithShadow
//        startPositions = new Stack<>();
//        startPositions.add('0');
        int result = tools.colorTextRenderer(instance, textRenderer, text, x, y, startPositions);
//        LOGGER.info("ran 3");
        return result;
    }
}
