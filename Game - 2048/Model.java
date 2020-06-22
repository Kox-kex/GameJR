package com.javarush.task.task35.task3513;

import java.util.*;

//будет содержать игровую логику и хранить игровое поле
//Он будет ответственен за все манипуляции производимые с игровым полем
public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
     int maxTile;
     int score;
     private Stack<Tile[][]> previousStates = new Stack();
    private Stack<Integer> previousScores = new Stack();
    private boolean isSaveNeeded = true;

    public Model() {
        this.maxTile = 0;
        this.score = 0;
        resetGameTiles();
    }

    public boolean hasBoardChanged() {
        Tile[][] tmp = previousStates.peek();
        int sum = 0;
        int sumPrev = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                sum += gameTiles[i][j].value;
                sumPrev += tmp[i][j].value;
            }
        }
        return sum != sumPrev;
    }

    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency;
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        } else {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }
        rollback();
        return moveEfficiency;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));

        priorityQueue.peek().getMove().move();
    }



    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0: left();
            break;
            case 1: right();
                break;
            case 2: up();
                break;
            case 3: down();
                break;
            default: break;
        }
    }

    public void rollback() {
        if (!previousScores.empty() && !previousStates.empty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    private void saveState(Tile[][] game) {
        Tile[][] gameClone = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        //глубокое клонирование массива
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                gameClone[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(gameClone);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    protected void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    public void addTile() {
            List<Tile> list = getEmptyTiles();
        if (!getEmptyTiles().isEmpty()) {

            Tile randomTile = list.get((int) (Math.random() * list.size()));
            for (int i = 0; i < gameTiles.length; i++) {
                for (int j = 0; j < gameTiles[i].length; j++) {
                    if (gameTiles[i][j].equals(randomTile)) {
                        gameTiles[i][j].value = Math.random() < 0.9 ? 2 : 4;
                    }
                }
            }
        }
    }

    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) {
            return true;
        }
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length - 1; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                    return true;
                }
            }
        }
        for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 0; i < gameTiles.length - 1; i++) {
                if (gameTiles[i][j].value == gameTiles[i + 1][j].value) {
                    return true;
                }
            }
        }
        return false;
    }


    private List getEmptyTiles () {
        List<Tile> list = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                if (gameTiles[i][j].isEmpty()) list.add(gameTiles[i][j]);
            }
        } return list;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean merge = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            if (tiles[i].value != 0 && (tiles[i].value == tiles[i + 1].value)) {
                tiles[i].value = tiles[i].value + tiles[i + 1].value;
                if (tiles[i].value > maxTile) {
                    maxTile = tiles[i].value;
                }
                score += tiles[i].value;
                tiles[i + 1].value = 0;
                merge = true;
                compressTiles(tiles);
            }
        }
        return merge;
    }

    protected void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
            isSaveNeeded = true;
        }
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }

        if (isChanged) addTile();

    }

    protected void down() {
        saveState(gameTiles);
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
        left();
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
    }

    protected void right() {
        if (isSaveNeeded) {
            saveState(gameTiles);
            isSaveNeeded = true;
        }
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            reverse(gameTiles[i]);
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
            reverse(gameTiles[i]);
        }

        if (isChanged) addTile();
    }

    protected void up() {
        saveState(gameTiles);
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
        right();
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
        rotate90Clockwise(gameTiles, FIELD_WIDTH);
    }

    protected void reverse(Tile[] tiles) {
        Tile a;
        for (int j = 0; j < gameTiles[j].length/2; j++){
            a = tiles[j];
            tiles[j] = tiles[tiles.length - 1 - j];
            tiles[tiles.length - 1 - j]= a;

        }
    }

    protected void rotate90Clockwise(Tile a[][], int N) {
        for (int i = 0; i < N / 2; i++)

        {

            for (int j = i; j < N - i - 1; j++)

            {
                Tile temp = a[i][j];

                a[i][j] = a[N - 1 - j][i];

                a[N - 1 - j][i] = a[N - 1 - i][N - 1 - j];

                a[N - 1 - i][N - 1 - j] = a[j][N - 1 - i];

                a[j][N - 1 - i] = temp;

            }

        }
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean changes = false;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value == 0 && i < tiles.length - 1 && tiles[i + 1].value != 0) {
                Tile temp = tiles[i];
                tiles[i] = tiles[i + 1];
                tiles[i + 1] = temp;
                i = -1;
                changes = true;
            }
        }
        return changes;


    }
}
