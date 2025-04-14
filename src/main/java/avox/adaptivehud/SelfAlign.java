package avox.adaptivehud;

import net.minecraft.client.MinecraftClient;

public class SelfAlign {
    public SelfAlign.Mode mode;
    public SelfAlign.X x;
    public SelfAlign.Y y;

    public SelfAlign(SelfAlign.Mode mode, SelfAlign.X selfAlignX, SelfAlign.Y selfAlignY) {
        this.mode = mode;
        x = selfAlignX;
        y = selfAlignY;
    }

    public SelfAlign() {
        this.mode = Mode.AUTO;

        this.x = X.LEFT;
        this.y = Y.TOP;
    }

    public int getX() {
        if (x.equals(X.LEFT)) {
            return 0;
        } else if (x.equals(X.CENTER)) {
            return MinecraftClient.getInstance().getWindow().getScaledWidth() / 2;
        } else {
            return MinecraftClient.getInstance().getWindow().getScaledWidth();
        }
    }

    public int getY() {
        if (y.equals(Y.TOP)) {
            return 0;
        } else if (y.equals(Y.MIDDLE)) {
            return MinecraftClient.getInstance().getWindow().getScaledHeight() / 2;
        } else {
            return MinecraftClient.getInstance().getWindow().getScaledHeight();
        }
    }

    public enum Mode {
        AUTO,
        CUSTOM
    }

    public enum X {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum Y {
        TOP,
        MIDDLE,
        BOTTOM
    }
}
