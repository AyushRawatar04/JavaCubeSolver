package com.carlos.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import com.carlos.cube.RubiksCube;

public class Cube3D extends Group {
    private static final double SIZE = 50;
    private static final double STICKER_THICKNESS = 1.0;
    private static final double GAP = 1.5;
    
    private Box[][] stickers = new Box[6][9];
    private Rotate xRotate = new Rotate(30, Rotate.X_AXIS);
    private Rotate yRotate = new Rotate(45, Rotate.Y_AXIS);
    private Timeline victorySpin;
    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;

    public Cube3D() {
        createStickers();
        this.getTransforms().addAll(xRotate, yRotate);
        setupAnimation();
    }

    private void setupAnimation() {
        victorySpin = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            yRotate.setAngle(yRotate.getAngle() + 0.8);
            xRotate.setAngle(xRotate.getAngle() + 0.3);
        }));
        victorySpin.setCycleCount(Animation.INDEFINITE);
    }

    public void startVictorySpin() {
        victorySpin.play();
    }

    public void stopVictorySpin() {
        victorySpin.stop();
        xRotate.setAngle(30);
        yRotate.setAngle(45);
    }

    public void setupInteraction(javafx.scene.Scene scene) {
        scene.setOnMousePressed(event -> {
            stopVictorySpin();
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = xRotate.getAngle();
            anchorAngleY = yRotate.getAngle();
        });
        scene.setOnMouseDragged(event -> {
            xRotate.setAngle(anchorAngleX - (event.getSceneY() - anchorY) * 0.4);
            yRotate.setAngle(anchorAngleY + (event.getSceneX() - anchorX) * 0.4);
        });
    }

    private void createStickers() {
        try {
            for (int f = 0; f < 6; f++) {
                for (int i = 0; i < 9; i++) {
                    Box sticker = new Box(SIZE, SIZE, STICKER_THICKNESS);
                    positionSticker(sticker, f, i);
                    stickers[f][i] = sticker;
                    this.getChildren().add(sticker);
                }
            }
            updateColors(new RubiksCube());
        } catch (Throwable t) {
            System.err.println("Warning: JavaFX 3D shape creation not supported on this platform: " + t.getMessage());
        }
    }

    private void positionSticker(Box b, int face, int idx) {
        double row = (idx / 3) - 1;
        double col = (idx % 3) - 1;
        double offset = (SIZE + GAP);
        double dist = (SIZE * 1.5) + (GAP * 1.0);

        switch (face) {
            case 0: // UP
                b.setTranslateY(-dist); b.setTranslateX(col * offset); b.setTranslateZ(row * offset);
                b.setRotationAxis(Rotate.X_AXIS); b.setRotate(90); break;
            case 3: // DOWN
                b.setTranslateY(dist); b.setTranslateX(col * offset); b.setTranslateZ(-row * offset);
                b.setRotationAxis(Rotate.X_AXIS); b.setRotate(90); break;
            case 2: // FRONT
                b.setTranslateZ(-dist); b.setTranslateX(col * offset); b.setTranslateY(row * offset); break;
            case 5: // BACK
                b.setTranslateZ(dist); b.setTranslateX(-col * offset); b.setTranslateY(row * offset); break;
            case 4: // LEFT
                b.setTranslateX(-dist); b.setTranslateZ(-col * offset); b.setTranslateY(row * offset);
                b.setRotationAxis(Rotate.Y_AXIS); b.setRotate(90); break;
            case 1: // RIGHT
                b.setTranslateX(dist); b.setTranslateZ(col * offset); b.setTranslateY(row * offset);
                b.setRotationAxis(Rotate.Y_AXIS); b.setRotate(90); break;
        }
    }

    public synchronized void updateColors(RubiksCube logicalCube) {
        try {
            char[][] faces = logicalCube.getFaces();
            for (int f = 0; f < 6; f++) {
                for (int i = 0; i < 9; i++) {
                    PhongMaterial mat = new PhongMaterial();
                    mat.setDiffuseColor(getFXColor(faces[f][i]));
                    mat.setSpecularColor(Color.WHITE);
                    if (f < stickers.length && i < stickers[f].length) {
                        stickers[f][i].setMaterial(mat);
                    }
                }
            }
        } catch (Throwable t) {
            System.err.println("Warning: JavaFX 3D materials not supported on this platform: " + t.getMessage());
        }
    }

    private Color getFXColor(char c) {
        switch (c) {
            case 'w': return Color.web("#DDDDDD");
            case 'r': return Color.web("#D50000");
            case 'g': return Color.web("#00C853");
            case 'y': return Color.web("#FFD600");
            case 'o': return Color.web("#E65100");
            case 'b': return Color.web("#0D47A1");
            case 'k': return Color.web("#1A1A1A"); // Professional Deep Black
            default: return Color.web("#1A1A1A"); // Default to black for unknown
        }
    }
}
