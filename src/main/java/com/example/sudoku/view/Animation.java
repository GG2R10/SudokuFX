package com.example.sudoku.view;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.Node;

/**
 * The {@code Animation} class provides utility methods to perform animations within the Sudoku game view.
 * <p>
 * Currently, it offers methods for an infinite opacity pulse animation on a Node and a stub for showing and hiding a label.
 * </p>
 */
public class Animation extends AnimationAdapter{

    /**
     * Performs an infinite opacity pulse animation on the provided {@code Node}.
     * <p>
     * The animation gradually transitions the opacity of the node from {@code fromOpacity} to {@code toOpacity} over the specified duration,
     * then reverses the effect continuously.
     * </p>
     *
     * @param node the JavaFX {@code Node} to animate.
     * @param durationSeconds the duration of one transition cycle in seconds.
     * @param fromOpacity the starting opacity value.
     * @param toOpacity the ending opacity value.
     * @author YourName
     * @version 1.0
     */
    public static void opacityPulse(Node node, double durationSeconds, double fromOpacity, double toOpacity) {
        FadeTransition fade = new FadeTransition(Duration.seconds(durationSeconds), node);
        fade.setFromValue(fromOpacity);
        fade.setToValue(toOpacity);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }

    /**
     * A placeholder method intended to animate the display of an information label.
     * <p>
     * This method should display the specified text in the {@code Label} and then hide it after a certain interval.
     * </p>
     *
     * @param label the {@code Label} to be animated.
     * @param text the text to display in the label.
     */
    public static void showAndHide(Label label, String text){

    }
}