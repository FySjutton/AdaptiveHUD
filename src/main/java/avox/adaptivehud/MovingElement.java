package avox.adaptivehud;

public abstract class MovingElement {
    public AnchorPoint anchorPoint;

    public int x;
    public int y;

    // The estimated width of the element, the width it will have on the moving screen
    public abstract void getEstimatedWidth();
    // Relative coords to anchor point
    public abstract int getRelativeX();
    public abstract int getRelativeY();
}