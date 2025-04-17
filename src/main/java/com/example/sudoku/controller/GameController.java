package com.example.sudoku.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.*;
import com.example.sudoku.model.Sudoku;
import com.example.sudoku.view.Animation;
import javafx.stage.Stage;

/**
 * The {@code GameController} class manages the user interactions for the Sudoku game.
 * <p>
 * It is responsible for initializing the game grid, handling user input, providing hints,
 * and triggering the win sequence upon puzzle completion. It coordinates between the view components and the
 * underlying {@link Sudoku} model.
 * </p>
 */
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

    private PriorityQueue<int[]> missplacedPositions = new PriorityQueue<>(
            (a, b) -> Integer.compare(b[2], a[2])
    );

    private final Sudoku sudoku = new Sudoku();

    private final BooleanProperty gameWon = new SimpleBooleanProperty(false);

    /**
     * Initializes the game controller by generating a new Sudoku puzzle, building the grid of text fields,
     * configuring their formatting and behavior, applying animations, and setting up the win condition listener.
     *
     */
    @FXML
    public void initialize() {
        sudoku.generateSolvedBoard();
        sudoku.removeCellsToCreatePuzzle();
        sudoku.printBoard(sudoku.getSolvedSudoku());

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

        setTextFieldsFormat();
        setTextFieldsBehaviour();
        Animation.opacityPulse(sudokuGrid, 1.5, 0.7, 1);
        listenToWin();
    }

    /**
     * Configures the formatting of all text fields within the Sudoku grid.
     * <p>
     * This method assigns the grid coordinates to each text field, sets initial text values based on the playable board,
     * adjusts editability and style for pre-filled cells, and applies margin insets for block borders.
     * It also restricts user input to numbers between 1 and 6.
     * </p>
     */
    private void setTextFieldsFormat() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField cell = textFields.get(row).get(col);
                cell.setUserData(new int[]{row, col, 0});
                cell.setText(String.valueOf(sudoku.getPlayableSudoku().get(row).get(col)));
                if (cell.getText().equals("0")) {
                    cell.setText(null);
                } else {
                    cell.setEditable(false);
                    cell.setStyle("-fx-border-color: #919191; -fx-text-fill: #919191;");
                }
                Insets margin = new Insets(
                        (row % 2 == 0) ? 3 : 0,
                        (col % 3 == 2) ? 3 : 0,
                        (row % 2 == 1) ? 3 : 0,
                        (col % 3 == 0) ? 4 : 0
                );
                sudokuGrid.setMargin(cell, margin);
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

    /**
     * Configures the behavior of all text fields in response to user input.
     * <p>
     * This method sets up listeners for text property changes. It validates user input, updates the information labels,
     * and adjusts cell styling based on the validity of the input or solvability of the puzzle.
     * </p>
     */
    private void setTextFieldsBehaviour() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField cell = textFields.get(row).get(col);
                int[] position = (int[]) cell.getUserData();
                int tfRow = position[0];
                int tfCol = position[1];

                cell.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (!cell.getText().isEmpty()) {
                        boolean validNumber = sudoku.isValid(tfRow, tfCol, Integer.parseInt(newValue), sudoku.getPlayableSudoku());
                        informationLabel.setText(sudoku.getStatus());
                        sudoku.getPlayableSudoku().get(tfRow).set(tfCol, Integer.parseInt(newValue));
                        int[] coordinates = (int[]) cell.getUserData();
                        missplacedPositions.remove(coordinates);

                        if (validNumber) {
                            sudoku.getAuxiliarSudoku().clear();
                            for (List<Integer> rowOnPlayable : sudoku.getPlayableSudoku()) {
                                sudoku.getAuxiliarSudoku().add(new ArrayList<>(rowOnPlayable));
                            }
                            boolean isSolvable = sudoku.isSolvable(0, 0);
                            resolvabilityInformationLabel.setText(sudoku.getResolvabilityStatus());

                            if (!isSolvable) {
                                coordinates[2] = 2;
                                cell.setStyle("-fx-border-color: rgba(182,0,0,0.65);");
                                cell.setStyle(cell.getStyle() + "-fx-background-color: #770707;");
                            } else {
                                coordinates[2] = 0;
                                cell.setStyle("-fx-border-color: rgba(169,255,0,0.64);");
                                cell.setStyle(cell.getStyle() + "-fx-background-color: transparent;");
                                if (sudoku.isSolved()) {
                                    gameWon.set(true);
                                }
                            }
                        } else {
                            coordinates[2] = 1;
                            cell.setStyle("-fx-border-color: rgba(182,0,0,0.65);");
                        }

                        if (coordinates[2] != 0) {
                            missplacedPositions.add(coordinates);
                        }
                    } else {
                        sudoku.getPlayableSudoku().get(tfRow).set(tfCol, 0);
                        cell.setStyle("-fx-border-color: white; -fx-background-color: transparent;");
                        int[] coordinates = (int[]) cell.getUserData();
                        missplacedPositions.remove(coordinates);
                        coordinates[2] = 0;
                        if (!resolvabilityInformationLabel.getText().isEmpty()) {
                            if (sudoku.isSolvable(0, 0))
                                resolvabilityInformationLabel.setText(sudoku.getResolvabilityStatus());
                        }
                        if (!informationLabel.getText().isEmpty()) {
                            informationLabel.setText("");
                        }
                    }
                });
            }
        }
    }

    /**
     * Listens for the win condition and triggers the win sequence when the game is solved.
     * <p>
     * Once the win condition is met, all text fields are set to non-editable and styled accordingly, and the information labels
     * and help button text are updated to indicate success.
     * </p>
     *
     * @author Sebastian Calvo
     * @version 1.2.5
     */
    private void listenToWin() {
        gameWon.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                for (int row = 0; row < sudoku.getSize(); row++) {
                    for (int col = 0; col < sudoku.getSize(); col++) {
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

    /**
     * Handles the action when the help button is pressed.
     * <p>
     * If the game is not yet won, this method provides a hint by filling in a cell from the solution. If no hint can be provided,
     * it displays an informational alert. If the game is already won, it resets the game by closing the current stage and loading a new one.
     * </p>
     *
     * @author Sebastian Calvo
     * @version 1.3
     */
    @FXML
    private void handleHelp() {
        if (!gameWon.getValue()) {
            if (missplacedPositions.size() > 0) {
                int[] coordinate = missplacedPositions.poll();
                int row = coordinate[0];
                int col = coordinate[1];
                textFields.get(row).get(col).setText(Integer.toString(sudoku.getSolvedSudoku().get(row).get(col)));
                textFields.get(row).get(col).setStyle("-fx-border-color: rgba(255,255,0,0.66); -fx-background-color: transparent;");
                missplacedPositions.remove(coordinate);
                return;
            } else {
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku");
            alert.setHeaderText(null);
            alert.setContentText("No hay espacios vacíos para poder mostrar una ayuda.");
            alert.showAndWait();
        } else {
            try {
                Stage stage = (Stage) helpButton.getScene().getWindow();
                stage.close();
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