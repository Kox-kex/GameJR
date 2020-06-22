package com.javarush.task.task35.task3513;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//будет следить за нажатием клавиш во время игры
public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private static int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) resetGame();
        else if (!model.canMove()) view.isGameLost = true;
        else if (!view.isGameLost && !view.isGameWon) {
            if (key == KeyEvent.VK_LEFT) model.left();
            if (key == KeyEvent.VK_RIGHT) model.right();
            if (key == KeyEvent.VK_UP) model.up();
            if (key == KeyEvent.VK_DOWN) model.down();
            if (key == KeyEvent.VK_Z) model.rollback();
            if (key == KeyEvent.VK_R) model.randomMove();
            if (key == KeyEvent.VK_A) model.autoMove();
            if (model.maxTile == WINNING_TILE) view.isGameWon = true;
        }
        view.repaint();
    }

    public void resetGame() {
        model.score = 0;
        view.isGameLost = false;
        view.isGameWon = false;
        model.resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore () {
        return model.score;
    }

    public View getView() {
        return this.view;
    }
}
