package com.example.sudoku.model;

import java.util.*;

/**
 * The {@code Sudoku} class models a 6x6 Sudoku puzzle using a 2x3 block layout.
 * <p>
 * This class maintains three board representations:
 * <ol>
 *   <li>{@code board}: the fully solved puzzle represented as an {@link ArrayList} of {@link ArrayList} of {@link Integer}.</li>
 *   <li>{@code playableBoard}: a puzzle board for gameplay with most cells removed except two for each block.</li>
 *   <li>{@code auxiliarBoard}: an auxiliary board used to check if the puzzle remains solvable given the current entries.</li>
 * </ol>
 * It also stores status messages related to validation and puzzle resolvability.
 * </p>
 */
public class Sudoku {
    private final int SIZE = 6;
    private final int BLOCK_ROWS = 2;
    private final int BLOCK_COLS = 3;
    private ArrayList<ArrayList<Integer>> board;
    private ArrayList<ArrayList<Integer>> playableBoard;
    private ArrayList<ArrayList<Integer>> auxiliarBoard;
    private String status = "";
    private String resolvabilityStatus = "";

    /**
     * Constructs a {@code Sudoku} object and initializes the board with a 6x6 grid filled with 0's.
     */
    public Sudoku() {
        board = new ArrayList<>();
        playableBoard = new ArrayList<>();
        auxiliarBoard = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            board.add(new ArrayList<>(Collections.nCopies(SIZE, 0)));
        }
    }

    /**
     * Generates a solved Sudoku board.
     * <p>
     * This method serves as a wrapper for {@link #fillCell(int, int)} starting at row 0 and column 0.
     * </p>
     *
     * @return {@code true} if the board is successfully solved, {@code false} otherwise.
     * @author Sebastian Calvo
     * @version 1.3
     */
    public boolean generateSolvedBoard() {
        return fillCell(0, 0);
    }

    /**
     * Recursively fills the Sudoku board with a valid solution using backtracking.
     * <p>
     * The method shuffles the list of numbers before trying them for each cell. If a valid number is found for a cell,
     * it proceeds recursively. If the assignment leads to an unsolvable state, the method backtracks.
     * </p>
     *
     * @param row the current row index to fill.
     * @param col the current column index to fill.
     * @return {@code true} if the board is filled successfully; {@code false} if no valid number leads to a solution.
     * @author Sebastian Calvo
     * @version 1.3
     */
    private boolean fillCell(int row, int col) {
        if (row == SIZE) {
            printBoard(board);
            return true;
        }
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        Collections.shuffle(numbers);
        for (int num : numbers) {
            if (isValid(row, col, num, board)) {
                board.get(row).set(col, num);
                if (fillCell(nextRow, nextCol)) {
                    status = "";
                    return true;
                }
                board.get(row).set(col, 0);
            }
        }
        return false;
    }

    /**
     * Attempts to solve the playable board after a new number is inserted.
     * <p>
     * This method uses a backtracking algorithm similar to the generation algorithm. It checks whether the current
     * state of {@code playableBoard} can lead to a valid solution. If a solution is found, the solved board is stored
     * into {@code board}.
     * </p>
     *
     * @param row the current row index to check.
     * @param col the current column index to check.
     * @return {@code true} if the board remains solvable; {@code false} otherwise.
     * @author Sebastian Calvo
     * @version 1.3
     */
    public boolean isSolvable(int row, int col) {
        if (row == SIZE) {
            printBoard(auxiliarBoard);
            board.clear();
            for (List<Integer> rowOnAuxiliar : auxiliarBoard) {
                board.add(new ArrayList<>(rowOnAuxiliar));
            }
            return true;
        }
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;
        if (playableBoard.get(row).get(col) != 0) {
            return isSolvable(nextRow, nextCol);
        }
        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num, auxiliarBoard)) {
                auxiliarBoard.get(row).set(col, num);
                if (isSolvable(nextRow, nextCol)){
                    resolvabilityStatus = "";
                    return true;
                }
                auxiliarBoard.get(row).set(col, 0);
            }
        }
        resolvabilityStatus = "Los números actuales hacen imposible resolver el sudoku!";
        return false;
    }

    /**
     * Checks whether the playable board is completely and correctly solved.
     * <p>
     * The method verifies that no cell is empty and that each number placement adheres to Sudoku rules.
     * </p>
     *
     * @return {@code true} if the board is solved; {@code false} otherwise.
     */
    public boolean isSolved(){
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = playableBoard.get(row).get(col);
                if (value == 0)
                    return false;
                playableBoard.get(row).set(col, 0);
                boolean isValid = isValid(row, col, value, playableBoard);
                playableBoard.get(row).set(col, value);
                if (!isValid)
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks if placing a given number at the specified cell is valid.
     * <p>
     * This method verifies that the number is not already present in the corresponding row, column, or block.
     * It is used both for board generation and for validating user input.
     * </p>
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @param num the number to be placed.
     * @param boardToCheck the board in which to validate the placement.
     * @return {@code true} if the placement is valid; {@code false} otherwise.
     * @author Santiago Arias
     * @version 1.2
     */
    public boolean isValid(int row, int col, int num, ArrayList<ArrayList<Integer>> boardToCheck) {
        if (boardToCheck.get(row).contains(num)){
            status = "Mismo numero (" + String.valueOf(num) + ") en la fila!";
            return false;
        }
        for (int r = 0; r < SIZE; r++) {
            if (boardToCheck.get(r).get(col) == num){
                status = "Mismo numero (" + String.valueOf(num) + ") en la columna!";
                return false;
            }
        }
        int blockStartRow = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int blockStartCol = (col / BLOCK_COLS) * BLOCK_COLS;
        for (int r = 0; r < BLOCK_ROWS; r++) {
            for (int c = 0; c < BLOCK_COLS; c++) {
                if (boardToCheck.get(blockStartRow + r).get(blockStartCol + c) == num) {
                    status = "Mismo numero (" + String.valueOf(num) + ") en el bloque!";
                    return false;
                }
            }
        }
        status = "";
        return true;
    }

    /**
     * Removes cells from the solved board to create a playable puzzle.
     * <p>
     * This method makes a deep copy of the solved board into {@code playableBoard} and then, for each block,
     * removes all but two cells chosen at random.
     * </p>
     */
    public void removeCellsToCreatePuzzle() {
        Random rand = new Random();
        playableBoard.clear();
        for (List<Integer> row : board) {
            playableBoard.add(new ArrayList<>(row));
        }
        for (int blockRow = 0; blockRow < SIZE; blockRow += BLOCK_ROWS) {
            for (int blockCol = 0; blockCol < SIZE; blockCol += BLOCK_COLS) {
                List<int[]> positions = new ArrayList<>();
                for (int r = 0; r < BLOCK_ROWS; r++) {
                    for (int c = 0; c < BLOCK_COLS; c++) {
                        positions.add(new int[]{blockRow + r, blockCol + c});
                    }
                }
                Collections.shuffle(positions);
                Set<int[]> toKeep = new HashSet<>();
                toKeep.add(positions.get(0));
                toKeep.add(positions.get(1));
                for (int[] pos : positions) {
                    if (!toKeep.contains(pos)) {
                        playableBoard.get(pos[0]).set(pos[1], 0);
                    }
                }
            }
        }
    }

    /**
     * Returns the playable Sudoku board.
     *
     * @return the playable board as an {@link ArrayList} of {@link ArrayList} of {@link Integer}.
     */
    public ArrayList<ArrayList<Integer>> getPlayableSudoku(){
        return playableBoard;
    }

    /**
     * Returns the solved Sudoku board.
     *
     * @return the solved board as an {@link ArrayList} of {@link ArrayList} of {@link Integer}.
     */
    public ArrayList<ArrayList<Integer>> getSolvedSudoku(){
        return board;
    }

    /**
     * Returns the auxiliary Sudoku board used for checking solvability.
     *
     * @return the auxiliary board as an {@link ArrayList} of {@link ArrayList} of {@link Integer}.
     */
    public ArrayList<ArrayList<Integer>> getAuxiliarSudoku(){
        return auxiliarBoard;
    }

    /**
     * Returns the size of the Sudoku board.
     *
     * @return the size of the board.
     */
    public int getSize(){
        return SIZE;
    }

    /**
     * Returns the status message from the last validation check.
     *
     * @return the status message.
     */
    public String getStatus(){
        return status;
    }

    /**
     * Returns the resolvability status indicating whether the current board configuration can lead to a solution.
     *
     * @return the resolvability status message.
     */
    public String getResolvabilityStatus(){
        return resolvabilityStatus;
    }

    /**
     * Prints the specified Sudoku board to the terminal.
     * <p>
     * Each row is printed on a new line and empty cells are represented by a period ('.').
     * </p>
     *
     * @param boardToPrint the board to be printed.
     */
    public void printBoard(ArrayList<ArrayList<Integer>> boardToPrint) {
        for (ArrayList<Integer> row : boardToPrint) {
            for (int num : row) {
                System.out.print((num == 0 ? "." : num) + " ");
            }
            System.out.println();
        }
        System.out.println("- - - - - -");
    }
}