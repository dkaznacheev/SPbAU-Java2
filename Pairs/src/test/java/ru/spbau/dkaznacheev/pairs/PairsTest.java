package ru.spbau.dkaznacheev.pairs;

import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;
import static ru.spbau.dkaznacheev.pairs.Util.generateRandomList;

public class PairsTest {
    @Test
    public void listTest() {
        int n = 200;
        int[] a = new int[n * n / 2];
        for (int i = 0; i < a.length; i++)
            a[i] = 0;
        List<Integer> l = generateRandomList(n);
        for (Integer i : l) {
            a[i]++;
        }
        for (int i = 0; i < a.length; i++) {
            assertEquals(2, a[i]);
        }

    }

    @Test
    public void gameTest() {
        int n = 8;
        Game game = new Game(n);
        for (int i = 0; i < n * n / 2; i++) {
            assertFalse(game.isWon());
            game.addPair();
        }
        assertTrue(game.isWon());
    }
}