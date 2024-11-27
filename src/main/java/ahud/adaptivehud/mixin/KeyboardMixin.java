package ahud.adaptivehud.mixin;

import ahud.adaptivehud.renderhud.element_values.ComplexVars;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;
import static ahud.adaptivehud.AdaptiveHUD.complexVARS;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("TAIL"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == 1) {
            complexVARS.cpsClick(key);
        }
    }
}
