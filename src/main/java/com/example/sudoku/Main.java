package com.example.sudoku;

import com.example.sudoku.view.GameStage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class serving as the entry point for the Sudoku application.
 * Extends the {@link javafx.application.Application} class to leverage
 * JavaFX framework capabilities.
 */
public class Main extends Application {

    /**
     * Main method used to launch the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     * @author Sebastian Calvo
     * @version 1.0
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and displays the primary stage of the application.
     * This method is invoked automatically by the JavaFX runtime after
     * the application has been launched.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set
     */
    @Override
    public void start(Stage primaryStage){
        new GameStage();
    }
}
