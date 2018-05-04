package ru.spbau.dkaznacheev.pairs;

import javafx.scene.control.Button;

/**
 * Custom button with a number attached to it.
 */
public class PairButton extends Button {

    public PairButton(int number) {
        this.number = number;
        }

    /**
     * Number on the button.
     */
    public final int number;
}
