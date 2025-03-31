package com.example.sudoku.view;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.Node;

//It's only used for the opacity animation of the grid for now :C
public class Animation {

    // Animation of pulse for the opacity, infinite
    public static void opacityPulse(Node node, double durationSeconds, double fromOpacity, double toOpacity) {
        FadeTransition fade = new FadeTransition(Duration.seconds(durationSeconds), node);
        fade.setFromValue(fromOpacity);    // Opacidad inicial
        fade.setToValue(toOpacity);        // Opacidad final (más transparente)
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);         // Para que regrese al valor inicial
        fade.play();
    }

    //Animation for the information label
    public static void showAndHide(Label label, String text){

    }
}
