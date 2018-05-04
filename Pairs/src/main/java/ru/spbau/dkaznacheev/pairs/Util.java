package ru.spbau.dkaznacheev.pairs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for the game.
 */
public class Util {
    /**
     * Generates random list of size n^2 with pairs of integers from 0 to n^2/2.
     * @param n root of size
     * @return generated list
     */
    public static List<Integer> generateRandomList(int n) {
        int size = n * n;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i / 2);
        }
        Collections.shuffle(list);
        return list;
    }
}
