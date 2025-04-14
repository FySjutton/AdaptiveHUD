package avox.adaptivehud;

import net.minecraft.client.MinecraftClient;

public class AnchorPoint {
    public AnchorPoint.Mode mode;
    public AnchorPoint.X x;
    public AnchorPoint.Y y;

    public AnchorPoint(AnchorPoint.Mode mode, AnchorPoint.X anchorPointX, AnchorPoint.Y anchorPointY) {
        this.mode = mode;
        x = anchorPointX;
        y = anchorPointY;
    }

    public AnchorPoint() {
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
        PREVIEW,
        ELEMENT
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
