package com.example.bitmap;

// stores the elements of the grid in an array
// contains a boolean indicating if the game has ended or not
public class Board {

    private char[] elts;
    private char currentPlayer;
    private boolean ended;

    // constructor
    // creates an array of 9 elements of the board
    public Board() {
        elts = new char[9];
        newGame();
    }

    // checks if the game has ended
    public boolean isEnded() {
        return ended;
    }

    // sets the mark of the currentPlayer on the grid at a given (x,y) position
    public char play(int x, int y) {
        if (!ended && elts[3 * y + x] == ' ') {
            elts[3 * y + x] = currentPlayer;
            changePlayer();
        }
        return checkEnd();
    }

    // changes the current player for the next play
    public void changePlayer() {
        currentPlayer = (currentPlayer == 'X' ? 'O' : 'X');
    }

    // getter method
    // returns the position
    public char getElt(int x, int y) {
        return elts[3 * y + x];
    }

    // reinitializes the board
    // sets the current player to "X"
    // assigns false to the ended variable
    public void newGame() {
        for (int i = 0; i < elts.length; i++) {
            elts[i] = ' ';
        }
        currentPlayer = 'X';
        ended = false;
    }

    // checks the board for winning combinations
    // updates the boolean ended variable to false
    public char checkEnd() {
        for (int i = 0; i < 3; i++) {
            if (getElt(i, 0) != ' ' &&
                    getElt(i, 0) == getElt(i, 1) &&
                    getElt(i, 1) == getElt(i, 2)) {
                ended = true;
                return getElt(i, 0);
            }
            if (getElt(0, i) != ' ' &&
                    getElt(0, i) == getElt(1, i) &&
                    getElt(1, i) == getElt(2, i)) {
                ended = true;
                return getElt(0, i);
            }
        }
        if (getElt(0, 0) != ' ' &&
                getElt(0, 0) == getElt(1, 1) &&
                getElt(1, 1) == getElt(2, 2)) {
            ended = true;
            return getElt(0, 0);
        }
        if (getElt(2, 0) != ' ' &&
                getElt(2, 0) == getElt(1, 1) &&
                getElt(1, 1) == getElt(0, 2)) {
            ended = true;
            return getElt(2, 0);
        }
        for (int i = 0; i < 9; i++) {
            if (elts[i] == ' ')
                return ' ';
        }
        return 'T';
    }
}