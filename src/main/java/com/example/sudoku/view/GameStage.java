// File: GameStage.java
package com.example.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameStage extends Stage {

    public GameStage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudoku/game.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/sudoku/game.css").toExternalForm());

            this.setTitle("Mini-proyecto 2");
            this.setScene(scene);
            this.show();
        } catch (Exception e) {
            e.printStackTrace();
            //Here we could put a message and exit the program, smth
        }
    }
}