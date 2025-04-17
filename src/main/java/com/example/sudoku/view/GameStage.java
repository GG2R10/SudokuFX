package com.example.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The {@code GameStage} class extends the JavaFX {@link Stage} to create and display a game stage.
 * <p>
 * This stage is initialized by loading the associated FXML layout, applying a CSS stylesheet,
 * setting a window title, and adding an icon image to the stage.
 * </p>
 *
 * <p>
 * This class is part of the Sudoku game view implementation.
 * </p>
 */
public class GameStage extends Stage {

    /**
     * Constructs a new {@code GameStage} which initializes the stage by loading the FXML layout,
     * setting the scene dimensions, applying the stylesheet, setting the window title,
     * and setting the window icon.
     * <p>
     * In case of an exception during initialization, the stack trace is printed.
     * </p>
     *
     * @author Santiago Arias
     * @version 1.0
     */
    public GameStage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudoku/game.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/sudoku/game.css").toExternalForm());

            this.setTitle("Mini-proyecto 2");
            this.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/sudoku/images/favicon.png")));
            this.setScene(scene);
            this.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
