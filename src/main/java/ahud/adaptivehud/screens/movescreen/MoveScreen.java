package ahud.adaptivehud.screens.movescreen;

import ahud.adaptivehud.ConfigFiles;
import ahud.adaptivehud.renderhud.CoordCalculators;
import ahud.adaptivehud.renderhud.RenderHUD;
import ahud.adaptivehud.Tools;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.AdaptiveHUD.renderElements;
import static ahud.adaptivehud.ConfigFiles.configFile;
import static ahud.adaptivehud.ConfigFiles.elementArray;

@Environment(EnvType.CLIENT)
public class MoveScreen extends Screen {
    private final Screen parent;
    private final List<Object[]> posList;

    private double offsetX;
    private double offsetY;
    private int height;
    private int width;
    private Object[] dragInf;
    private JsonObject dragged;
    private final ArrayList<Integer> snapPointsX;
    private final ArrayList<Integer> snapPointsY;
    private boolean shiftPressed = false;
    private double snapX = 0;
    private double snapY = 0;

    private int itemAlignX = 0;
    private int itemAlignY = 0;
    private int alignX = 0;
    private int alignY = 0;

    private boolean autoSave;

    private final int itemAlignLineColor = new Tools().parseColor(configFile.getAsJsonObject().get("item_align_lines_color").getAsString());
    private final int snappingLineColor = new Tools().parseColor(configFile.getAsJsonObject().get("snapping_lines_color").getAsString());

    public MoveScreen(Screen parent, boolean autoSave) {
        super(Text.of("AdaptiveHUD"));
        this.parent = parent;
        this.posList = new RenderHUD(true).generatePositions();
        this.snapPointsX = new ArrayList<>();
        this.snapPointsY = new ArrayList<>();
        this.autoSave = autoSave;
        renderElements = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        new RenderHUD(false).renderCustomHud(context);

        if (dragged != null) {
            int xPos = new CoordCalculators().getActualCords(dragged, dragged.get("posX").getAsInt() + alignX, client.getWindow().getScaledWidth(), width, 0, "X");
            int yPos = new CoordCalculators().getActualCords(dragged, dragged.get("posY").getAsInt() + alignY, client.getWindow().getScaledHeight(), height, 0, "Y");

            context.fill(itemAlignX, itemAlignY, xPos, itemAlignY + 2, itemAlignLineColor);
            context.fill(xPos, itemAlignY, xPos + 2, yPos, itemAlignLineColor);

            if (snapX != 0) {
                context.fill((int) snapX, 0, (int) snapX + 1, client.currentScreen.height, snappingLineColor);
            }
            if (snapY != 0) {
                context.fill(0, (int) snapY, client.currentScreen.width, (int) snapY + 1, snappingLineColor);
            }
        }
    }

    @Override
    public void close() {
        renderElements = true;
        if (this.autoSave) {
            new ConfigFiles().saveElementFiles(elementArray, new ArrayList<>());
        }
        client.setScreen(parent);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragged != null) {
            boolean foundX = false;
            boolean foundY = false;
            if (!shiftPressed) {
                for (int x : this.snapPointsX) {
                    if (Math.abs(mouseX - offsetX - x) < 5) {
                        dragged.addProperty("posX", new CoordCalculators().getRelativeCords(dragged, x, client.getWindow().getScaledWidth(), width, "X"));
                        snapX = x;
                        foundX = true;
                    } else if (Math.abs(mouseX - offsetX + width - x) < 5) {
                        dragged.addProperty("posX", new CoordCalculators().getRelativeCords(dragged, x - width, client.getWindow().getScaledWidth(), width, "X"));
                        snapX = x;
                        foundX = true;
                    }
                }
                for (int y : this.snapPointsY) {
                    if (Math.abs(mouseY - offsetY - y) < 5) {
                        dragged.addProperty("posY", new CoordCalculators().getRelativeCords(dragged, y, client.getWindow().getScaledHeight(), height, "Y"));
                        snapY = y;
                        foundY = true;
                    } else if (Math.abs(mouseY - offsetY + height - y) < 5) {
                        dragged.addProperty("posY", new CoordCalculators().getRelativeCords(dragged, y - height, client.getWindow().getScaledHeight(), height, "Y"));
                        snapY = y;
                        foundY = true;
                    }
                }
            }

            if (!foundX) {
                snapX = 0;
                dragged.addProperty("posX", new CoordCalculators().getRelativeCords(dragged, (int) ((mouseX - offsetX)), client.getWindow().getScaledWidth(), width, "X"));
            }
            if (!foundY) {
                snapY = 0;
                dragged.addProperty("posY", new CoordCalculators().getRelativeCords(dragged, (int) ((mouseY - offsetY)), client.getWindow().getScaledHeight(), height, "Y"));
            }
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Object[] x : posList) {
                if ((mouseX >= (int) x[1] && mouseX <= (int) x[3]) && (mouseY >= (int) x[2] && mouseY <= (int) x[4])) {
                    dragged = ((JsonElement) x[0]).getAsJsonObject();
                    offsetX = mouseX - (int) x[1];
                    offsetY = mouseY - (int) x[2];
                    height = (int) x[4] - (int) x[2];
                    width = (int) x[3] - (int) x[1];
                    dragInf = x;

                    this.snapPointsX.clear();
                    this.snapPointsX.add(0);
                    this.snapPointsX.add(client.currentScreen.width);
                    this.snapPointsY.clear();
                    this.snapPointsY.add(0);
                    this.snapPointsY.add(client.currentScreen.height);
                    for (Object[] y : this.posList) {
                        if (y != x) {
                            this.snapPointsX.add((int) y[1]);
                            this.snapPointsX.add((int) y[3]);

                            this.snapPointsY.add((int) y[2]);
                            this.snapPointsY.add((int) y[4]);
                        }
                    }

                    JsonObject align = dragged.get("alignment").getAsJsonObject();
                    int PitemAlignX = align.get("itemAlignX").getAsInt();
                    if (PitemAlignX == 0) {
                        itemAlignX = 0;
                    } else if (PitemAlignX == 1) {
                        itemAlignX = client.currentScreen.width / 2;
                    } else {
                        itemAlignX = client.currentScreen.width;
                    }
                    int PitemAlignY = align.get("itemAlignY").getAsInt();
                    if (PitemAlignY == 0) {
                        itemAlignY = 0;
                    } else if (PitemAlignY == 1) {
                        itemAlignY = client.currentScreen.height / 2 - 1;
                    } else {
                        itemAlignY = client.currentScreen.height - 2;
                    }
                    int PalignX = align.get("textAlignX").getAsInt();
                    if (PalignX == 0) {
                        alignX = 0;
                    } else if (PalignX == 1) {
                        alignX = width / 2;
                    } else {
                        alignX = width;
                    }
                    int PalignY = align.get("textAlignY").getAsInt();
                    if (PalignY == 0) {
                        alignY = 0;
                    } else if (PalignY == 1) {
                        alignY = height / 2;
                    } else {
                        alignY = height;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragged != null) {
            int actX = new CoordCalculators().getActualCords(dragged, dragged.get("posX").getAsInt(), client.getWindow().getScaledWidth(), width, 0, "X");
            int actY = new CoordCalculators().getActualCords(dragged, dragged.get("posY").getAsInt(), client.getWindow().getScaledHeight(), height, 0, "Y");
            dragInf[1] = actX;
            dragInf[2] = actY;
            dragInf[3] = actX + width;
            dragInf[4] = actY + height;
            dragged = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            shiftPressed = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            shiftPressed = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        // Reloads the position of all elements in case the window is resized, the elements will still move, but you can atleast now click on them.
        super.resize(client, width, height);
        for (Object[] x : posList) {
            JsonObject elm = (JsonObject) x[0];
            int lengthX = (int) x[3] - (int) x[1];
            int lengthY = (int) x[4] - (int) x[2];
            int posX = new CoordCalculators().getActualCords(elm, elm.get("posX").getAsInt(), width, lengthX, 0, "X");
            int posY = new CoordCalculators().getActualCords(elm, elm.get("posY").getAsInt(), height, lengthY, 0, "Y");

            x[1] = posX;
            x[2] = posY;
            x[3] = posX + lengthX;
            x[4] = posY + lengthY;
        }
    }
}