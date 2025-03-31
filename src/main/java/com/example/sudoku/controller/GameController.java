package com.example.sudoku.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import java.util.*;
import com.example.sudoku.model.Sudoku;

public class GameController {

    @FXML
    private GridPane sudokuGrid;

    private ArrayList<ArrayList<TextField>> textFields = new ArrayList<>();

    private Sudoku sudoku = new Sudoku();

    @FXML
    public void initialize() {
        // Generate a Sudoku and a playable board with only 2 hints per block
        sudoku.generateSolvedBoard();
        sudoku.removeCellsToCreatePuzzle();
        sudoku.printBoard(sudoku.getSolvedSudoku());

        // Create 6x6 grid of TextFields
        for (int row = 0; row < 6; row++) {
            ArrayList<TextField> rowList = new ArrayList<>();

            for (int col = 0; col < 6; col++) {
                TextField cell = new TextField();
                cell.setPrefSize(60, 60);
                cell.getStyleClass().add("sudoku-cell");
                sudokuGrid.add(cell, col, row);
                rowList.add(cell);
            }

            textFields.add(rowList);
        }

        //Configure the textFields
        setTextFieldsFormat();
        setTextFieldsBehaviour();
    }

    private void setTextFieldsFormat(){
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                TextField cell = textFields.get(row).get(col);

                //We save the coordinates of our textField inside it with an object property asigned with setUserData
                cell.setUserData(new int[]{row,col});

                //We assign to our textField the actual number that is in the playableBoard grid.
                cell.setText(String.valueOf(sudoku.getPlayableSudoku().get(row).get(col)));
                if(cell.getText().equals("0")){ cell.setText(null); }
                else { cell.setEditable(false); cell.setStyle("-fx-border-color: blue;"); }

                //Modify it so the user only can type numbers from 1-6
                cell.setTextFormatter(new TextFormatter<>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("[1-6]?")) {
                        return change;
                    } else {
                        return null;
                    }
                }));
            }
        }
    }

    private void setTextFieldsBehaviour(){
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                TextField cell = textFields.get(row).get(col);

                int[] position = (int[]) cell.getUserData();
                int tfRow = position[0];
                int tfCol = position[1];

                cell.textProperty().addListener((obs, oldValue, newValue) -> {
                    if(newValue.matches("[1-6]?") && !cell.getText().equals("") && !cell.getText().equals(null)){
                        boolean validNumber = sudoku.isValid(tfRow, tfCol, Integer.parseInt(newValue), sudoku.getPlayableSudoku());
                        sudoku.getPlayableSudoku().get(tfRow).set(tfCol, Integer.parseInt(newValue));

                        if(validNumber){
                            cell.setStyle("-fx-border-color: green;");
                        }

                        else{
                            cell.setStyle("-fx-border-color: red;");
                        }

                        //We try to solve the puzzle with the actual numbers to see if the solution the user is proposing is actually possible
                        sudoku.getAuxiliarSudoku().clear();
                        for (List<Integer> rowOnPlayable : sudoku.getPlayableSudoku()) {
                            sudoku.getAuxiliarSudoku().add(new ArrayList<>(rowOnPlayable));
                        }

                        if(!sudoku.isSolvable(0, 0)){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Sudoku");
                            alert.setHeaderText(null); // Puedes poner un título aquí si quieres
                            alert.setContentText("Los numeros actuales hacen que el Sudoku sea irresoluble.");
                            alert.showAndWait();
                        }
                    }

                    else{
                        sudoku.getPlayableSudoku().get(tfRow).set(tfCol, 0);
                    }
                });
            }
        }
    }

    //Help button. Shows a number of the current generated solution in an empty cell
    @FXML
    private void handleHelp() {
        for(int row = 0; row < sudoku.getSize(); row++){
            for(int col = 0; col < sudoku.getSize(); col++){
                if(sudoku.getPlayableSudoku().get(row).get(col) == 0){
                    textFields.get(row).get(col).setText(String.valueOf(sudoku.getSolvedSudoku().get(row).get(col)));
                    textFields.get(row).get(col).setStyle("-fx-border-color: yellow;");
                    return;
                }
            }
        }

        //If we make it till here, that means there's no empty place for us to show a hint
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sudoku");
        alert.setHeaderText(null); // Puedes poner un título aquí si quieres
        alert.setContentText("No hay espacios vacios para poder mostrar una ayuda.");
        alert.showAndWait();
    }
}