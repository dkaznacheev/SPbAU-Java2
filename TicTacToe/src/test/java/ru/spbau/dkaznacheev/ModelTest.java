package ru.spbau.dkaznacheev;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelTest {

    @Test
    public void simpleMultiplayerGameWorks() {
        Model model = new Model(GameType.MULTIPLAYER);
        model.makeTurn(1,1);
        model.makeTurn(1,2);
        model.makeTurn(0,1);
        model.makeTurn(1,0);
        PlayerType result = model.makeTurn(2,1);
        assertEquals(PlayerType.PLAYER_X, result);
    }

    @Test
    public void turnOnNonEmptyFails() {
        Model model = new Model(GameType.MULTIPLAYER);
        model.makeTurn(1,1);
        model.makeTurn(1,1);
        assertEquals(BoardState.STATE_X, model.getBoard().get(1,1 ));
        assertEquals(PlayerType.PLAYER_O, model.getCurrentPlayer());
    }

    @Test
    public void gameEndsInADraw() {
        Model model = new Model(GameType.MULTIPLAYER);
        assertEquals(PlayerType.NOBODY, model.makeTurn(1,1));
        model.makeTurn(2,2);
        model.makeTurn(2,1);
        model.makeTurn(0,1);
        model.makeTurn(0,2);
        model.makeTurn(2, 0);
        model.makeTurn(1, 0);
        assertEquals(PlayerType.NOBODY, model.makeTurn(1,2));
        PlayerType result = model.makeTurn(0,0);
        assertEquals(PlayerType.DRAW, result);
    }

    @Test
    public void hardBotDraws() {
        Model model = new Model(GameType.BOT_HARD);
        model.makeTurn(1,1);
        model.makeTurn(2,0);
        model.makeTurn(0,1);
        model.makeTurn(1,0);
        PlayerType result = model.makeTurn(2,2);
        assertEquals(PlayerType.DRAW, result);
    }

    @Test
    public void hardBotPreventsLoss() {
        Model model = new Model(GameType.BOT_HARD);
        model.makeTurn(1, 1);
        model.makeTurn(1, 2);
        assertEquals(BoardState.STATE_O, model.getBoard().get(1, 0));
    }
}
