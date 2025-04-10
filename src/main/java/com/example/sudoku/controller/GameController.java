package com.example.sudoku.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.*;
import com.example.sudoku.model.Sudoku;
import com.example.sudoku.view.Animation;
import javafx.stage.Stage;

public class GameController {

    @FXML
    private GridPane sudokuGrid;

    @FXML
    private Button helpButton;

    @FXML
    private Label informationLabel;

    @FXML
    private Label resolvabilityInformationLabel;

    private final ArrayList<ArrayList<TextField>> textFields = new ArrayList<>();

    //This is Arias blame. Basically a priorityQueque of the wrong board positions in playable board
    private PriorityQueue<int[]> missplacedPositions = new PriorityQueue<>(
            (a,b) -> Integer.compare(b[2], a[2]) //THe priority is determined by the integer z in the ints (x,y,z)
    );

    private final Sudoku sudoku = new Sudoku();

    //Boolean we'll be listening to activate the win sequence
    private final BooleanProperty gameWon = new SimpleBooleanProperty(false);

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

            //We add them to a list so we can modify them after their creation
            textFields.add(rowList);
        }

        //Configure the textFields
        setTextFieldsFormat();
        setTextFieldsBehaviour();

        //Set pulse animation to the opacity of the hole GridPane
        Animation.opacityPulse(sudokuGrid, 1.5, 0.7, 1);

        //Listen to the win boolean for win sequence activation
        listenToWin();
    }

    private void setTextFieldsFormat(){
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                TextField cell = textFields.get(row).get(col);

                //We save the coordinates of our textField inside it with an object property assigned with setUserData
                cell.setUserData(new int[]{row,col, 0});

                //We assign to our textField the actual number that is in the playableBoard grid.
                cell.setText(String.valueOf(sudoku.getPlayableSudoku().get(row).get(col)));
                if(cell.getText().equals("0")){ cell.setText(null); }
                else { cell.setEditable(false); cell.setStyle("-fx-border-color: #919191; -fx-text-fill: #919191;"); }

                //Borders of different size if it is a border of a 2x3 block
                Insets margin = new Insets(
                        (row % 2 == 0) ? 3 : 0, // top
                        (col % 3 == 2) ? 3 : 0, // right
                        (row % 2 == 1) ? 3 : 0, // bottom
                        (col % 3 == 0) ? 4 : 0  // left
                );
                sudokuGrid.setMargin(cell, margin);

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
            for(int col = 0; col < 6; col++) {
                TextField cell = textFields.get(row).get(col);

                //We get the actual coordinates of this cell (can't use the ones in the for's because a recursion problem)
                int[] position = (int[]) cell.getUserData();
                int tfRow = position[0];
                int tfCol = position[1];

                //We define the behaviour now
                cell.textProperty().addListener((obs, oldValue, newValue) -> {
                    if(!cell.getText().isEmpty()){
                        boolean validNumber = sudoku.isValid(tfRow, tfCol, Integer.parseInt(newValue), sudoku.getPlayableSudoku());

                        informationLabel.setText(sudoku.getStatus());
                        sudoku.getPlayableSudoku().get(tfRow).set(tfCol, Integer.parseInt(newValue));

                        int[] coordinates = (int[]) cell.getUserData();
                        missplacedPositions.remove(coordinates);

                        if(validNumber){
                            //We try to solve the puzzle with the actual numbers to see if the solution the user is proposing is actually possible
                            sudoku.getAuxiliarSudoku().clear();
                            for (List<Integer> rowOnPlayable : sudoku.getPlayableSudoku()) {
                                sudoku.getAuxiliarSudoku().add(new ArrayList<>(rowOnPlayable));
                            }

                            boolean isSolvable = sudoku.isSolvable(0,0);

                            //We set our information label about resolvability after checking it
                            resolvabilityInformationLabel.setText(sudoku.getResolvabilityStatus());

                            //If it is not solvable, we add this
                            if(!isSolvable){
                                coordinates[2] = 2;

                                cell.setStyle("-fx-border-color: rgba(182,0,0,0.65);");
                                cell.setStyle(cell.getStyle() + "-fx-background-color: #770707;");
                            }

                            else {
                                coordinates[2] = 0;

                                cell.setStyle("-fx-border-color: rgba(169,255,0,0.64);");
                                cell.setStyle(cell.getStyle() + "-fx-background-color: transparent;");

                                if(sudoku.isSolved()){
                                    gameWon.set(true);
                                }
                            }
                        }

                        //if the number was not valid, we put the border in red and do a replacePriority change
                        else{
                            coordinates[2] = 1; //We change the priority higher
                            cell.setStyle("-fx-border-color: rgba(182,0,0,0.65);");
                        }

                        //We add the coordinate to missplacedPositions if it has a replacePriority higher than an empty cell
                        if(coordinates[2] != 0) {
                            missplacedPositions.add(coordinates);
                        }
                    }

                    //If the newValue is empty, we need to update the information labels and the cell
                    //and leave as default the actual cell / playableSudoku position
                    else{
                        sudoku.getPlayableSudoku().get(tfRow).set(tfCol, 0);
                        cell.setStyle("-fx-border-color: white; -fx-background-color: transparent;");

                        int[] coordinates = (int[]) cell.getUserData();
                        missplacedPositions.remove(coordinates);
                        coordinates[2] = 0;

                        if (!resolvabilityInformationLabel.getText().isEmpty()) {
                            if(sudoku.isSolvable(0, 0)) resolvabilityInformationLabel.setText(sudoku.getResolvabilityStatus());
                        }

                        if (!informationLabel.getText().isEmpty()){
                            informationLabel.setText("");
                        }
                    }
                });
            }
        }
    }

    //Win sequence function. It listens to gameWon and then does the animation
    private void listenToWin(){
        gameWon.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                for(int row = 0; row < sudoku.getSize(); row++){
                    for(int col = 0; col < sudoku.getSize(); col++){
                        TextField cell = textFields.get(row).get(col);

                        cell.setEditable(false);
                        cell.setStyle(cell.getStyle() + "-fx-border-color: rgba(200,0,255,0.64); -fx-background-color: transparent; -fx-text-fill: white;");
                    }
                }

                informationLabel.setText("Sudoku resuelto!");
                informationLabel.setStyle(informationLabel.getStyle() + "-fx-text-fill: #8cff00;");
                resolvabilityInformationLabel.setText("Bien hecho!");
                resolvabilityInformationLabel.setStyle(resolvabilityInformationLabel.getStyle() + "-fx-text-fill: #8cff00;");
                helpButton.setText("Volver a jugar");
            }
        });
    }

    //Action of the Help Button. Shows a number of the current generated solution in an empty cell
    //His behaviour depends on gameWon. If gameWon is true, it'll execute the win sequence, if not, it will give a hint to the user
    @FXML
    private void handleHelp() {
        if (!gameWon.getValue()) {
            if(missplacedPositions.size() > 0){ //We pick from the missplaced positions to replace them
                int[] coordinate = missplacedPositions.poll();
                int row = coordinate[0];
                int col = coordinate[1];

                textFields.get(row).get(col).setText(Integer.toString(sudoku.getSolvedSudoku().get(row).get(col)));
                textFields.get(row).get(col).setStyle("-fx-border-color: rgba(255,255,0,0.66); -fx-background-color: transparent;");

                missplacedPositions.remove(coordinate);
                return;
            }

            else{ //We pick from the empty positions
                for (int row = 0; row < sudoku.getSize(); row++) {
                    for (int col = 0; col < sudoku.getSize(); col++) {
                        if (sudoku.getPlayableSudoku().get(row).get(col) == 0) {
                            textFields.get(row).get(col).setText(String.valueOf(sudoku.getSolvedSudoku().get(row).get(col)));
                            textFields.get(row).get(col).setStyle("-fx-border-color: rgba(255,255,0,0.66);");
                            return;
                        }
                    }
                }
            }

            //If we make it till here, that means there's no empty place for us to show a hint
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku");
            alert.setHeaderText(null); // Puedes poner un título aquí si quieres
            alert.setContentText("No hay espacios vacíos para poder mostrar una ayuda.");
            alert.showAndWait();
        }

        //If we won, we reset the window, closing the actual one and basically cloning the code in the GameStage constructor
        else {
            try {
                // Get the actual stage
                Stage stage = (Stage) helpButton.getScene().getWindow();
                stage.close(); // And close it

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudoku/game.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 600, 600);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/sudoku/game.css")).toExternalForm());

                Stage gameStage = new Stage();
                gameStage.setTitle("Mini-proyecto 2");
                gameStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/sudoku/images/favicon.png"))));
                gameStage.setScene(scene);
                gameStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}