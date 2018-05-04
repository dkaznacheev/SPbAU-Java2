package ru.spbau.dkaznacheev.pairs;


/**
 * Game model.
 */
public class Game {

    /**
     * Field size.
     */
    private final Integer fieldSize;
    
    /**
     * Number of found pairs.
     */
    private Integer foundPairs;

    public Game(Integer fieldSize) {
        this.fieldSize = fieldSize;
        this.foundPairs = 0;
    }

    /**
     * Add a new found pair.
     */
    public void addPair() {
        this.foundPairs++;
    }

    /**
     * Check if the game is over.
     * @return if it is over
     */
    public boolean isWon() {
        return foundPairs == fieldSize * fieldSize / 2;
    }
}
