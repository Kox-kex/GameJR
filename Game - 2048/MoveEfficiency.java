package com.javarush.task.task35.task3513;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private  Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        Integer a1 = this.numberOfEmptyTiles;
        Integer o1 = o.numberOfEmptyTiles;
        Integer sa1 = this.score;
        Integer so1 = o.score;
        int a = a1.compareTo(o1);
        if (a == 0) {
            a = sa1.compareTo(so1);
        }
        return a;
    }
}
