package avox.adaptivehud.anchor;

import net.minecraft.client.MinecraftClient;

public class AnchorPoint {
    public AnchorMode mode;
    public AnchorPointX x;
    public AnchorPointY y;

    public AnchorPoint(AnchorMode mode, AnchorPointX anchorPointX, AnchorPointY anchorPointY) {
        this.mode = mode;
        x = anchorPointX;
        y = anchorPointY;
    }

    public AnchorPoint() {
        this.mode = AnchorMode.Auto;
        this.x = AnchorPointX.Left;
        this.y = AnchorPointY.Top;
    }

    public int getX() {
        if (x.equals(AnchorPointX.Left)) {
            return 0;
        } else if (x.equals(AnchorPointX.Center)) {
            return MinecraftClient.getInstance().getWindow().getScaledWidth() / 2;
        } else {
            return MinecraftClient.getInstance().getWindow().getScaledWidth();
        }
    }

    public int getY() {
        if (y.equals(AnchorPointY.Top)) {
            return 0;
        } else if (y.equals(AnchorPointY.Middle)) {
            return MinecraftClient.getInstance().getWindow().getScaledHeight() / 2;
        } else {
            return MinecraftClient.getInstance().getWindow().getScaledHeight();
        }
    }
}
