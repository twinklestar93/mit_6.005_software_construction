/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Upon initialization, every square on board is in untouched state. client can perform dig, flag, deflag, look
 * operation on every square until client hit a bomb, in which case, the client is game over, however, for other
 * players, the bomb is removed away and the square state is changed to dug. If the square is dug, the board
 * count the neighbors that have a bomb.
 */
public class Board {

    private final Square[][] grid;
    private final int width;
    private final int height;

    // Abstraction function
    //    AF(grid, width, height) = a Minesweeper board consisted of squares,
    //                              with # of columns and # of rows

    // Rep invariant
    //    height, width > 0
    //    # of grid columns = width
    //    # of grid rows = height

    // Rep exposure
    //    grid, height and width are private and final
    //    methods use defensive copy to avoid sharing the rep with clients

    // Thread safety:
    //    threadsafe by monitor pattern: all accesses to rep are guarded by
    //    the object's lock.
    // Deadlock can't occur.

    public void checkRep() {
        assert height > 0 && width > 0;
        assert height == grid.length;
        for (int i = 0; i < height; i++) {
            assert width == grid[i].length;
        }
    }

    /**
     * make a board with random bomb position
     * @param width width of the board, correspond to the # of rows
     * @param height height of the board, correspond to the # of columns
     * @param bombProbability the probability that a square contains a bomb
     * @throws IllegalArgumentException
     */
    public Board(int width, int height, double bombProbability) {
        if (width < 0 || height < 0 || bombProbability < 0 || bombProbability > 1) {
            throw new IllegalArgumentException("argument is illegal");
        }

        grid = new Square[width][height];

        this.width = width;
        this.height = height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (Math.random() < bombProbability) {
                    grid[i][j] = new Square(true);
                } else {
                    grid[i][j] = new Square(false);
                }
            }
        }
        checkRep();
    }

    /**
     * make a board from a file
     * @param file
     * @throws java.io.IOException if could not read the file
     */
    public Board(File file) throws IOException{
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            // check formatting
            if (!lines.get(0).matches("[0-9]+ [0-9]+")) {
                throw new RuntimeException("the file is improperly formatted.");
            }

            // get size
            String[] size = lines.get(0).split(" ");
            width = Integer.valueOf(size[0]);
            height = Integer.valueOf(size[1]);
            grid = new Square[height][width];

            // check formatting
            lines.remove(0);
            if (height != lines.size()) {
                throw new RuntimeException("the file is not properly formatted");
            }

            for (String line : lines) {
                if (line.split(" ").length != width || !line.matches("((0|1) )*(0|1)")) {
                    throw new RuntimeException("the file is not properly formatted");
                }
            }

            // fill the grid
            for (int i = 0; i < height; i++) {
                String[] boardRow = lines.get(i).split(" ");
                for (int j = 0; j < width; j++) {
                    if (boardRow[j].equals("1")) {
                        grid[i][j] = new Square(true);
                    } else {
                        grid[i][j] = new Square(false);
                    }
                }
            }
            checkRep();
        } catch (IOException ioException) {
            throw new IOException("can not read the file: ", ioException);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * get the SquareState of grid[x][y]
     * @param x column # of a square, 0 <= x < width
     * @param y row # of a square, 0 <= y < height
     * @return SquareState the state of grid[x][y]
     */
    public synchronized Square.SquareState getSquareState(int x, int y) {
        return grid[y][x].getState();
    }

    /**
     * flag an untouched square
     *
     * @param x column # of a square, 0 <= x < width
     * @param y row # of a square, 0 <= y < height
     */
    public synchronized void flag(int x, int y) {
        Square square = grid[y][x];
        square.setState(Square.SquareState.FLAGGED);
    }

    /**
     * remove flag from flagged square
     *
     * @param x column # of a square, 0 <= x < width
     * @param y row # of a square, 0 <= y < height
     */
    public synchronized void deflag(int x, int y) {
        Square square = grid[y][x];
        square.setState(Square.SquareState.UNTOUCHED);
    }

    /**
     * If the square has no neighbor squares with bombs,
     * then for each untouched neighbor square,
     * change said square to dug and repeat this step.
     *
     * @param x column # of a square, 0 <= x < width
     * @param y row # of a square, 0 <= y < height
     */
    private void digNeighbors(int x, int y) {
        int[][] neighbors = {{x-1, y-1}, {x-1, y}, {x-1, y+1},
                {x, y-1}, {x, y+1}, {x+1, y-1}, {x+1, y}, {x+1, y+1}};
        if (adjacentBomb(x, y) == 0) {
            for (int[] neighbor : neighbors) {
                if ((neighbor[0] >= 0 && neighbor[0] < width) && (neighbor[1] >= 0 && neighbor[1] < height)) {
                    Square square = grid[neighbor[1]][neighbor[0]];
                    if (!square.getState().equals(Square.SquareState.DUG)) {
                        square.setState(Square.SquareState.DUG);
                        digNeighbors(neighbor[0], neighbor[1]);
                    }
                }
            }
        }
    }

    /**
     * count of adjacent bombs
     * @param x column # of a square, 0 <= x < width
     * @param y row # of a square, 0 <= y < height
     * @return count of bombs
     */
    private int adjacentBomb(int x, int y) {
        int[][] neighbors = {{x - 1, y - 1}, {x - 1, y}, {x - 1, y + 1},
                {x, y - 1}, {x, y + 1}, {x + 1, y - 1}, {x + 1, y}, {x + 1, y + 1}};
        int bombCount = 0;
        for (int[] neighbor : neighbors) {
            if ((neighbor[0] >= 0 && neighbor[0] < width) && (neighbor[1] >= 0 && neighbor[1] < height)) {
                Square square = grid[neighbor[1]][neighbor[0]];
                if (square.isBomb()) {
                    bombCount += 1;
                }
            }
        }
        return bombCount;
    }

    /**
     * Dig an untouched square
     *
     * @param x column # of a square, 0 <= x < width
     * @param y row # of a square, 0 <= y < height
     * @return true if square is a bomb, false otherwise
     */
    public synchronized boolean dig(int x, int y) {
        Square square = grid[y][x];
        if (square.isBomb()) {
            square.setState(Square.SquareState.DUG);
            digNeighbors(x, y);
            checkRep();
            return true;
        } else {
            square.setState(Square.SquareState.DUG);
            digNeighbors(x, y);
            checkRep();
            return false;
        }
    }

    public synchronized String toString() {
        StringBuffer board = new StringBuffer();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Square square = grid[i][j];
                if (square.getState() == Square.SquareState.DUG
                && adjacentBomb(j, i) > 0) {
                    board.append(adjacentBomb(j, i));
                } else {
                    board.append(square.toString());
                }
                board.append(" ");
            }
            board.setLength(board.length()-1);  // remove last character
            board.append("\n");
        }
        return board.toString();
    }


}

/**
 * a square inside the board, every square is in one of three states: DUG, UNTOUCHED, FLAGGED.
 * each square either contains a bomb, or it does not contain a bomb.
 */
class Square {

    public enum SquareState {DUG, UNTOUCHED, FLAGGED};

    private boolean isBomb;
    private SquareState state;

    // Abstract function
    //    AF(isBomb, state) = the square contains a bomb iff isBomb
    //                        and the state is in (dug, untouched, flagged)

    // Rep invariant
    //    a square that is dug cann't contain a bomb

    // Rep exposure
    //    isBomb field is a private immutable type boolean
    //    squareState is private

    private void checkRep() {
        if (state.equals(SquareState.DUG)) {
            assert !isBomb;
        }
    }

    /**
     * make an untouched square
     *
     * @param isBomb boolean indicates whether the square contains a bomb or not
     */
    public Square(boolean isBomb) {
        this.isBomb = isBomb;
        this.state = SquareState.UNTOUCHED;
        checkRep();
    }

    /**
     * whether the square contains a bomb
     * @return true iff the square contains a bomb
     */
    public boolean isBomb() {
        return isBomb;
    }

    public SquareState getState() {
        return state;
    }

    /**
     * set the state of the square
     * @param state of the square, must be DUG or FLAGGED
     *              remove the bomb if set to DUG so other player can go on
     */
    public void setState(SquareState state) {
        if (state.equals(SquareState.DUG)) {
            isBomb = false;
            this.state = state;
            checkRep();
        } else {
            this.state = state;
            checkRep();
        }
    }

    @Override
    public String toString() {
        switch (state) {
            case DUG: return " ";
            case FLAGGED: return "F";
            case UNTOUCHED: return "-";
            default: throw new RuntimeException("can't get here");
        }
    }
}
