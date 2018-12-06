package com.github.musicscore.doodlepadtetris;

import doodlepad.Pad;
import doodlepad.Rectangle;
import doodlepad.Text;

import java.awt.*;
import java.util.ArrayList;

public class Board extends Pad {

    // Board/Game variables
    private static int leftPad = 40, rightPad = 300, topPad = 40, bottomPad = 40;
    private int width, height, size;
    private int level;
    private int score;
    private int[] tileData;
    private Rectangle[] visualData;
    private Text scoreField;

    // Key-related variables
    private boolean holdDown;

    // Tetrimino variables
    private int x, y;
    private int delay;
    private Tetrimino activePiece, nextPiece;


    // ====================================================
    //   Constructors
    //
    // ====================================================

    public Board(int width, int height, int cellSize, int level) {
        super(width * cellSize + leftPad + rightPad, height * cellSize + topPad + bottomPad);
        this.width = width;
        this.height = height;
        this.level = level;
        size = cellSize;

        // Initialize playing board
        int totalSize = width * height;
        visualData = new Rectangle[totalSize + 4];
        tileData = new int[totalSize];
        for (int i = 0; i < totalSize; i++) {
            visualData[i] = new Rectangle(leftPad + (i % width) * size, topPad + (i / width) * size, size, size);
            visualData[i].setFillColor(10, 10, 10);
            visualData[i].setStrokeColor(40, 40, 40);

            tileData[i] = 0;
        }

        // Render miscellaneous UI components
        // TODO: Create all UI components
        scoreField = new Text("Score: 0", leftPad + (width + 2) * size, topPad, 20);

        visualData[width * height] = new Rectangle(0, 0, size, size);
        visualData[width * height + 1] = new Rectangle(0, 0, size, size);
        visualData[width * height + 2] = new Rectangle(0, 0, size, size);
        visualData[width * height + 3] = new Rectangle(0, 0, size, size);

        // Set the "next" Tetrimino, then allow the game to handle the rest
        nextPiece = new Tetrimino();

        // Start the game
        setTickRate(60);
        startTimer();
    }


    // ====================================================
    //   Instance methods
    //   - Events
    // ====================================================

    public void onTick(long when) {
        super.onTick(when);

        if (activePiece == null) {
            newPiece();
        }
        else {
            if (delay > 30 - level || (delay > (30 - level) / 3 && holdDown)) {
                resetActivePieceTiles(); // Get rid of the data in the active Tetrimino's tiles because it's not
                                         //   supposed to be permanently placed yet.

                if (canMoveActivePieceDown()) {
                    score += (holdDown ? 1 : 0);
                    delay = 0;
                    y++;
                }
                else {
                    if (y <= activePiece.getLowestPoint()[1]) {
                        updateScore();
                        stopTimer();
                        return;
                    }
                    placeActivePieceTiles();



                    for (int y = 0; y < height; y++) {
                        int filled = 0;
                        for (int x = 0; x < width; x++) {
                            if (isOccupied(x, y)){
                                filled++;
                            }
                            System.out.print((tileData[x + y * width] == 0 ? " " : tileData[x + y * width]) + " ");
                        }
                        System.out.println(filled >= 10);
                    }



                    clearLines();
                    newPiece();

                    // Update the score only when the Tetrimino is placed and a new one is dropped.
                    updateScore();
                }
            }
        }

        refreshScreen();
        delay++;
    }

    public void onKeyPressed(String key, String modifier) {
        super.onKeyPressed(key, modifier);

        String input = key.toUpperCase();
        if (input.matches("X")) {
            resetActivePieceTiles();
            activePiece.rotateCounterclockwise();

            boolean allowRotation = true;
            for (int[] coord : activePiece.getCoordinateSet()) {
                if (isOccupied(x + coord[0], y + coord[1]) || x + coord[0] < 0 || x + coord[0] >= width) {
                    allowRotation = false;
                    break;
                }
            }
            if (!allowRotation) {
                activePiece.rotateClockwise();
            }
        }
        else if (input.matches("Z")) {
            resetActivePieceTiles();
            activePiece.rotateClockwise();

            boolean allowRotation = true;
            for (int[] coord : activePiece.getCoordinateSet()) {
                if (isOccupied(x + coord[0], y + coord[1]) || x + coord[0] < 0 || x + coord[0] >= width) {
                    allowRotation = false;
                    break;
                }
            }
            if (!allowRotation) {
                activePiece.rotateCounterclockwise();
            }
        }
        else if (input.matches("LEFT")) {
            resetActivePieceTiles();
            boolean canMove = true;
            for (int[] coord : activePiece.getCoordinateSet()) {
                if (x + coord[0] - 1 < 0 || isOccupied(x + coord[0] - 1, y + coord[1])) {
                    canMove = false;
                    break;
                }
            }
            if (!canMove) {
                placeActivePieceTiles();
                return;
            }
            x--;
        }
        else if (input.matches("RIGHT")) {
            resetActivePieceTiles();
            boolean canMove = true;
            for (int[] coord : activePiece.getCoordinateSet()) {
                if (x + coord[0] + 1 >= width || isOccupied(x + coord[0] + 1, y + coord[1])) {
                    canMove = false;
                    break;
                }
            }
            if (!canMove) {
                placeActivePieceTiles();
                return;
            }
            x++;
        }
        else if (input.matches("DOWN")) {
            holdDown = true;
        }
    }

    public void onKeyReleased(String key, String modifier) {
        if (key.toUpperCase().matches("DOWN")) {
            holdDown = false;
        }
    }


    // ====================================================
    //   Instance methods
    //   - Rendering
    // ====================================================

    private void refreshScreen() {
        if (activePiece != null) {
            placeActivePieceTiles();
        }

        int index = 0;
        for (Rectangle rect : visualData) {
            if (index >= width * height) {
                break;
            }
            rect.setFillColor(tileData[index] == 0 ? new Color(24, 24, 24) :
                    Tetrimino.getShapeColor(Tetrimino.Shape.values()[tileData[index] - 1]));
            index++;
        }
    }

    private void resetActivePieceTiles() {
        for (int[] coord : activePiece.getCoordinateSet()) {
            tileData[x + coord[0] + ((y + coord[1]) * width)] = 0;
        }
    }

    private void placeActivePieceTiles() {
        for (int[] coord : activePiece.getCoordinateSet()) {
            tileData[x + coord[0] + ((y + coord[1]) * width)] = activePiece.getShape().ordinal() + 1;
        }
    }

    private void updateScore() {
        scoreField.setText("Score: " + score);
    }


    // ====================================================
    //   Instance methods
    //   - Piece movement
    // ====================================================

    private boolean canMoveActivePieceDown() {
        if (activePiece == null) {
            return false;
        }

        boolean canGoDown = true;
        for (int[] coord : activePiece.getCoordinateSet()) {
            if (isOccupied(x + coord[0], y + coord[1] + 1)) {
                canGoDown = false;
                break;
            }
        }
        return canGoDown;
    }

    private boolean isOccupied(int x, int y) {
        try {
            return tileData[x + y * width] != 0;
        }
        catch (Exception e) {
            return true;
        }
    }

    private void newPiece() {
        x = (width / 2) - 1;
        y = 1;

        activePiece = nextPiece;
        nextPiece = new Tetrimino();

        int index = 0;
        for (int[] coord : nextPiece.getCoordinateSet()) {
            visualData[width * height + index].setLocation(leftPad + (width + 4 + coord[0]) * size, topPad + (height - 4 + coord[1]) * size);
            visualData[width * height + index].setFillColor(nextPiece.getColor());
            index++;
        }
    }

    private void clearLines() {
        ArrayList<Integer> rows = new ArrayList<>();
        for (int y = height - 1; y > 0; y--) {
            boolean filled = true;
            for (int x = 0; x < width; x++) {
                if (tileData[x + (y * width)] == 0) {
                    filled = false;
                    break;
                }
            }
            if (filled) {
                rows.add(y);
            }
        }

        if (rows.isEmpty()) {
            return;
        }
        for (int row : rows) {
            for (int index = (row + 1) * width - 1; index >= 0; index--) {
                // TODO: Fix clear line logic because it's not working
                tileData[index] = index < width ? 0 : tileData[index - width];
            }
        }
    }

}
