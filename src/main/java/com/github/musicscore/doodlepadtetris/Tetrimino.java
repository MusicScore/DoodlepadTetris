package com.github.musicscore.doodlepadtetris;

import java.awt.*;

public class Tetrimino {

    public enum Shape {
        I,
        O,
        T,
        J,
        L,
        S,
        Z
    }

    private static Color[] color = {
            new Color(47, 154, 211),
            new Color(255, 254, 0),
            new Color(174, 0, 211),
            new Color(211, 129, 0),
            new Color(0, 0, 211),
            new Color(15, 184, 0),
            new Color(204, 4, 0)
    };

    private Shape identify;
    private int[][] shape;

    public Tetrimino() {
        this(Shape.values()[(int)Math.floor(Math.random() * Shape.values().length)]);
    }

    public Tetrimino(Shape shape) {
        identify = shape;
        switch (shape) {
            case I:
                this.shape = new int[][] {
                        {-1, 0},
                        { 0, 0},
                        { 1, 0},
                        { 2, 0}
                };
                break;
            case O:
                this.shape = new int[][] {
                        {0, 0},
                        {1, 0},
                        {1, 1},
                        {0, 1}
                };
                break;
            case T:
                this.shape = new int[][] {
                        {-1, 0},
                        { 0, 0},
                        { 1, 0},
                        { 0, 1}
                };
                break;
            case J:
                this.shape = new int[][] {
                        {-1, 0},
                        { 0, 0},
                        { 1, 0},
                        { 1, 1},
                };
                break;
            case L:
                this.shape = new int[][] {
                        {-1, 1},
                        {-1, 0},
                        { 0, 0},
                        { 1, 0},
                };
                break;
            case S:
                this.shape = new int[][] {
                        { 1, 0},
                        { 0, 0},
                        { 0, 1},
                        {-1, 1}
                };
                break;
            case Z:
                this.shape = new int[][]{
                        {-1, 0},
                        { 0, 0},
                        { 0, 1},
                        { 1, 1}
                };
                break;
            default:
                this.shape = new int[][] {
                        {-999, -999}
                };
        }
    }

    public static Color getShapeColor(Shape shape) {
        return color[shape.ordinal()];
    }

    public Shape getShape() {
        return identify;
    }

    public Color getColor() {
        return getShapeColor(identify);
    }

    public int[][] getCoordinateSet() {
        return shape;
    }

    public int[] getCoordinate(int index) {
        return shape[index];
    }

    public int[] getLowestPoint() {
        int[] lowest = {0, 0};
        for (int[] coord : shape) {
            if (coord[1] > lowest[1]) {
                lowest = coord;
            }
        }
        return lowest;
    }

    public void rotateClockwise() {
        if (this.identify == Shape.O) {
            return;
        }
        int index = 0;
        for (int[] point : shape) {
            shape[index] = new int[] {point[1], -point[0]};
            index++;
        }
    }

    public void rotateCounterclockwise() {
        if (this.identify == Shape.O) {
            return;
        }
        int index = 0;
        for (int[] point : shape) {
            shape[index] = new int[] {-point[1], point[0]};
            index++;
        }
    }

    public Tetrimino clone() {
        Tetrimino clone = new Tetrimino(identify);
        clone.shape = this.shape;
        return clone;
    }

}
