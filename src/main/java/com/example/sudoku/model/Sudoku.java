package com.example.sudoku.model;

import java.util.*;

public class Sudoku {
    //Size of the game grid
    private final int SIZE = 6;

    //Horizontal size of the blocks inside the grid
    private final int BLOCK_ROWS = 2;

    //Vertical size of the blocks inside the grid
    private final int BLOCK_COLS = 3;

    //Grid, actually an ArrayList
    private ArrayList<ArrayList<Integer>> board;

    //Playable Grid with the positions erased except two for each block
    private ArrayList<ArrayList<Integer>> playableBoard;

    //Auxiliar Grid used to check if the user has a possibility of solving the puzzle with the already positioned numbers
    private ArrayList<ArrayList<Integer>> auxiliarBoard;

    //String used to save information about validations
    private String status = "";

    //String used to save information about resolvability
    private String resolvabilityStatus = "";

    //Constructor. It initializes the board with a 6x6 ArrayList full of 0's and the playableBoard empty.
    public Sudoku() {
        board = new ArrayList<>();
        playableBoard = new ArrayList<>();
        auxiliarBoard = new ArrayList<>();

        //Repeat 6 times
        for (int i = 0; i < SIZE; i++) {
            //We add a row to the board with the form [0,0,0,0,0,0]
            board.add(new ArrayList<>(Collections.nCopies(SIZE, 0)));
        }
    }

    //Just a wrapper for fillCell, starting in row = 0 and col = 0
    public boolean generateSolvedBoard() {
        return fillCell(0, 0);
    }

    //The function that fills the board with a valid solution using backtracing
    private boolean fillCell(int row, int col) {
        //If the row equals 6, we already iterated for all the rows and columns in the grid, thefore, it was successfully filled.
        if (row == SIZE) {
            printBoard(board);
            return true;
        }

        /*We are iterating from rows to columns. That means:
            row 0 -> Column 0,1,2,3,4,5
            row 1 -> Column 0,1,2,3,4,5
            row 2 -> ...
        Then, if the col is 5 (the last column, SIZE - 1) we should go to the next row (row + 1) to continue the iteration,
        if not, the row is the same (row = row). The columns should add up to 5 and then restart to 0.
        */
        //Ternary operator: int a = condition? value if true : value if false
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;

        //We create a list of the possible numbers we could use and then shuffle them to get different order in the numbers we try for the filling of the cell
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        Collections.shuffle(numbers);

        //We iterate for all the numbers in the list
        for (int num : numbers) {

            //If the number is valid (there's no repetition of that number in the row, column and block) we put it in the cell it corresponds
            //acording of the actual row and column (ArrayList(row).ArrayList(col).set(number);)
            if (isValid(row, col, num, board)) {
                board.get(row).set(col, num);

                /*Here's backtracing. We try to ubicate a number in the next cell (ArrayList(nextRow).ArrayList(nextCol)) that
                is valid. If we can't, that means the number we put before it makes a solution impossible, so we erase it and try again.*/
                if (fillCell(nextRow, nextCol)) {
                    status = "";
                    return true;
                }
                board.get(row).set(col, 0); // reset the slot and try again until we find a number which makes possible to ubicate a valid number in the next cell.
            }
        }

        //If the for ends and there was no number that could solve the puzzle, we return false so it tries back with the last correct option
        return false;
    }

    //Function that tries to solve the playable board every time the user inserts a new valid number. If the board becomes unsolvable,
    //it returns false. It works similar to the algorythm used to generate a solved board because it is practically that.
    public boolean isSolvable(int row, int col) {
        //Same algorithm as the generator
        if (row == SIZE) {
            printBoard(auxiliarBoard);

            //We clone the new solved puzzle into the solved puzzle grid
            board.clear();
            for (List<Integer> rowOnAuxiliar : auxiliarBoard) {
                board.add(new ArrayList<>(rowOnAuxiliar));
            }

            return true;
        }

        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;

        //Skip the positions that already have a number
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

    //Checks if the board is solved
    public boolean isSolved(){
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = playableBoard.get(row).get(col);

                // If there's a 0, it is not solved
                if (value == 0)
                    return false;

                // We remove it temporally so we can check using the function isValid
                playableBoard.get(row).set(col, 0);
                boolean isValid = isValid(row, col, value, playableBoard);
                playableBoard.get(row).set(col, value);

                if (!isValid)
                    return false;
            }
        }
        return true;
    }

    //Checks if the number we are trying to put in the grid[row][col] is valid.
    //We use this function in two different context: One to generate a valid board, and the other to check if the user put a valid number.
    //So it is required to specify which one of the boards (board or playableBoard) is going to be used
    public boolean isValid(int row, int col, int num, ArrayList<ArrayList<Integer>> boardToCheck) {
        // Check row. Self-explanatory
        if (boardToCheck.get(row).contains(num)){
            status = "Mismo numero (" + String.valueOf(num) + ") en la fila!";
            return false;
        }

        // Check column. Self-explanatory
        for (int r = 0; r < SIZE; r++) {
            if (boardToCheck.get(r).get(col) == num){
                status = "Mismo numero (" + String.valueOf(num) + ") en la columna!";
                return false;
            }
        }

        // Get the starting row and column of the 2x3 block that contains the cell in the position [row][col]
        /*We use bucketization. In Java, when you divide an int / int you only obtain the integer part of the division. It allows us to
        make some sorting depending on a range we specify (BLOCK_ROWS and BLOCK_COLS). in this case, we know the blocks start in the
        rows multiple of 2, and in the columns multiple of 3. So, imagine we have a cell in the position row = 5 and col = 2, when we do
        the divisions:
            (5 / 2) * 2 = 2 * 2 = 4 (The starting row of the third vertical block)
            (2 / 3) * 3 = 0 * 3 = 0 (The starting column of the first horizontal block)

        And that basically applies for all the other combinations. It will always give us the starting row and column of the block that contains the cell.*/
        int blockStartRow = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int blockStartCol = (col / BLOCK_COLS) * BLOCK_COLS;

        //Check that the number is not in any of the cells that compose the block
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

    public void removeCellsToCreatePuzzle() {
        Random rand = new Random();

        //We make a deep copy of the solved board in the playable grid (data copy, not references)
        playableBoard.clear();
        for (List<Integer> row : board) {
            playableBoard.add(new ArrayList<>(row));
        }

        //We iterate for each one of the blocks in the grid
        for (int blockRow = 0; blockRow < SIZE; blockRow += BLOCK_ROWS) {
            for (int blockCol = 0; blockCol < SIZE; blockCol += BLOCK_COLS) {
                // Create a list of all the (row,col) positions of the block
                List<int[]> positions = new ArrayList<>();
                for (int r = 0; r < BLOCK_ROWS; r++) {
                    for (int c = 0; c < BLOCK_COLS; c++) {
                        positions.add(new int[]{blockRow + r, blockCol + c});
                    }
                }

                //Shuffle the list of positions
                Collections.shuffle(positions);

                // We create a set that will contain the first 2 randomized positions we want to save in the block
                Set<int[]> toKeep = new HashSet<>();
                toKeep.add(positions.get(0));
                toKeep.add(positions.get(1));

                //Erase all the positions that are not in the set (The positions that we don't want to save)
                for (int[] pos : positions) {
                    if (!toKeep.contains(pos)) {
                        playableBoard.get(pos[0]).set(pos[1], 0); // 0 as "empty" cell
                    }
                }
            }
        }
    }

    //Getter
    public ArrayList<ArrayList<Integer>> getPlayableSudoku(){
        return playableBoard;
    }

    //Getter
    public ArrayList<ArrayList<Integer>> getSolvedSudoku(){
        return board;
    }

    //Getter
    public ArrayList<ArrayList<Integer>> getAuxiliarSudoku(){
        return auxiliarBoard;
    }

    //Getter
    public int getSize(){
        return SIZE;
    }

    //Getter
    public String getStatus(){
        return status;
    }

    //Getter
    public String getResolvabilityStatus(){
        return resolvabilityStatus;
    }

    //This just prints the sudoku in the terminal (For debug purposses)
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