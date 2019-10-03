package com.example.bitmap;

import java.util.Random;


public class Board {

    private static final Random RANDOM = new Random();

    private char[] cell;
    private char currentPlayer;
    private boolean gameEnded;


    public Board() {
        cell = new char[9];
        newGame();
    }

    public boolean isEnded() {
        return gameEnded;
    }

    // sets the mark of the currentPlayer on the grid at a given (x,y) position
    public char play(int x, int y) {
        if (!gameEnded && cell[3 * y + x] == ' ') {
            cell[3 * y + x] = currentPlayer;
            changePlayer();
        }
        return checkEnd();
    }

    // change the current player for the next player
    public void changePlayer() {
        currentPlayer = (currentPlayer == 'X' ? 'O' : 'X');
    }


    public char getElt(int x, int y) {
        return cell[3 * y + x];
    }

    public void newGame() {
        for (int i = 0; i < cell.length; i++) {
            cell[i] = ' ';
        }
        currentPlayer = 'X';
        gameEnded = false;
    }

    // checks for winning combinations and marks the game as ended
    public char checkEnd() {
        for (int i = 0; i < 3; i++) {
            if (getElt(i, 0) != ' ' &&
                    getElt(i, 0) == getElt(i, 1) &&
                    getElt(i, 1) == getElt(i, 2)) {
                gameEnded = true;
                return getElt(i, 0);
            }
            if (getElt(0, i) != ' ' &&
                    getElt(0, i) == getElt(1, i) &&
                    getElt(1, i) == getElt(2, i)) {
                gameEnded = true;
                return getElt(0, i);
            }
        }
        if (getElt(0, 0) != ' ' &&
                getElt(0, 0) == getElt(1, 1) &&
                getElt(1, 1) == getElt(2, 2)) {
            gameEnded= true;
            return getElt(0, 0);
        }
        if (getElt(2, 0) != ' ' &&
                getElt(2, 0) == getElt(1, 1) &&
                getElt(1, 1) == getElt(0, 2)) {
            gameEnded = true;
            return getElt(2, 0);
        }
        for (int i = 0; i < 9; i++) {
            if (cell[i] == ' ')
                return ' ';
        }
        return 'T';
    }

    public char computer() {
        if (!gameEnded) {
            int position = -1;
            do {
                position = RANDOM.nextInt(9);
            } while (cell[position] != ' ');
            cell[position] = currentPlayer;
            changePlayer();
        }
        return checkEnd();
    }
}