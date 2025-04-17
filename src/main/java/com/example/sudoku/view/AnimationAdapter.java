package com.example.sudoku.view;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * The {@code AnimationAdapter} class serves as an abstraction layer between the view components
 * and the low-level animation utilities provided by the {@code Animation} class.
 * <p>
 * This adapter enables cleaner integration of animations into the user interface logic
 * and promotes separation of concerns by encapsulating animation behavior.
 * </p>
 *
 * @author Santiago Arias
 * @version 1.5
 */
public class AnimationAdapter {

    /**
     * Applies a pulsing opacity animation to the specified {@code Node}.
     * <p>
     * Internally delegates to {@code Animation.opacityPulse()} to create a continuous fade effect
     * between the given opacity values.
     * </p>
     *
     * @param node the JavaFX {@code Node} to animate.
     * @param durationSeconds the duration of each fade cycle in seconds.
     * @param fromOpacity the minimum opacity value.
     * @param toOpacity the maximum opacity value.
     */
    public void applyPulseEffect(Node node, double durationSeconds, double fromOpacity, double toOpacity) {
        Animation.opacityPulse(node, durationSeconds, fromOpacity, toOpacity);
    }

    /**
     * Displays a temporary message in a {@code Label} using a fade or timing effect.
     * <p>
     * This method is a wrapper around {@code Animation.showAndHide()} and is expected
     * to control visibility transitions for information labels.
     * </p>
     *
     * @param label the {@code Label} to show the message in.
     * @param text the message text to display.
     */
    public void displayTemporaryMessage(Label label, String text) {
        Animation.showAndHide(label, text);
    }
}
