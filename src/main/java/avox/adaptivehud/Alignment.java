package avox.adaptivehud;

import net.minecraft.client.MinecraftClient;

public class Alignment {
    public final MinecraftClient client = MinecraftClient.getInstance();
    public AnchorPoint anchorPoint;
    public SelfAlign selfAlign;
    public HudElement element;

    public Alignment(AnchorPoint anchorPoint, SelfAlign selfAlign) {
        this.anchorPoint = anchorPoint;
        this.selfAlign = selfAlign;
    }

    private int getAlignX(X xAlign, int width) {
        return switch (xAlign) {
            case LEFT -> 0;
            case CENTER -> width / 2;
            case RIGHT -> width;
        };
    }

    private int getAlignY(Y yAlign, int height) {
        return switch (yAlign) {
            case TOP -> 0;
            case MIDDLE -> height / 2;
            case BOTTOM -> height;
        };
    }

    public Position getScreenOrigin() {
        return new Position(
            getAlignX(anchorPoint.x, client.getWindow().getScaledWidth()),
            getAlignY(anchorPoint.y, client.getWindow().getScaledHeight())
        );
    }

    public Position getElementOrigin() {
        Position actualCords = getActualCords();
        return new Position(
            actualCords.x() + getAlignX(selfAlign.x, element.getEstimatedWidth()),
            actualCords.y() + getAlignY(selfAlign.y, element.getEstimatedHeight())
        );
    }
    
    public Position getRelativeCords(Position cord, Position size) {
        return new Position(
            cord.x() - getAlignX(anchorPoint.x, client.getWindow().getScaledWidth()) + getAlignX(selfAlign.x, size.x()),
            cord.y() - getAlignY(anchorPoint.y, client.getWindow().getScaledHeight() + getAlignY(selfAlign.y, size.y()))
        );
    }

    public Position getActualCords() {
        return new Position( // CHANGE TO ACTUAL WIDTH; NOT ESTIMATED
            element.relativeX + getAlignX(anchorPoint.x, client.getWindow().getScaledWidth()),
            element.relativeY + getAlignY(anchorPoint.y, client.getWindow().getScaledHeight())
        );
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

    public static class AnchorPoint {
        public AnchorPoint.Mode mode;
        public X x;
        public Y y;

        public AnchorPoint(Mode mode, X anchorPointX, Y anchorPointY) {
            this.mode = mode;
            x = anchorPointX;
            y = anchorPointY;
        }

        public AnchorPoint() {
            this.mode = Mode.AUTO;

            this.x = X.LEFT;
            this.y = Y.TOP;
        }
        
        public enum Mode {
            AUTO,
            PREVIEW,
            ELEMENT
        }
    }

    public static class SelfAlign {
        public Mode mode;
        public X x;
        public Y y;

        public SelfAlign(Mode mode, X anchorPointX, Y anchorPointY) {
            this.mode = mode;
            x = anchorPointX;
            y = anchorPointY;
        }

        public SelfAlign() {
            this.mode = Mode.AUTO;

            this.x = X.LEFT;
            this.y = Y.TOP;
        }

        public enum Mode {
            AUTO,
            PREVIEW,
            ELEMENT
        }
    }
}
