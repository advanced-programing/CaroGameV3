/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.java;

import java.io.Serializable;

/**
 *
 * @author admin
 */
public class CaroState implements Serializable {

    public int[][] playingMap;
    public boolean gameInProgress;
    public int playerPlayingX;
    public int playerPlayingO;
    public int currentPlayer;
    private int boardSize = NetSenceController.DEFAULT_SIZE;
    public int winner;
    public boolean gameEndedInDraw;
    public boolean playerDisconnected;

    public void applyMessage(int sender, Object message) {
        if (gameInProgress && message instanceof int[] && sender == currentPlayer) {
            int[] move = (int[]) message;
            if (move == null || move.length != 2) {
                return;
            }
            int row = move[0];
            int col = move[1];
            if (row < 0 || row >= boardSize || col < 0 || col >= boardSize || playingMap[row][col] != 0) {
                return;
            }
            playingMap[row][col] = (currentPlayer == playerPlayingX) ? playerPlayingX: playerPlayingO;
            if (checkWinner(row, col)) {
                gameInProgress = false;
                winner = currentPlayer;
            } else if (checkDraw()) {
                gameInProgress = false;
                gameEndedInDraw = true;
            } else {
                currentPlayer = (currentPlayer == playerPlayingX) ? playerPlayingO : playerPlayingX;
            }
        } else if (!gameInProgress && message.equals("newgame")) {
            System.out.println("new game message received.");
            startGame();
            
        }
    }

    private boolean checkWinner(int row, int col) {
        int[][] rc = {{0, -1, 0, 1}, {-1, 0, 1, 0}, {1, -1, -1, 1}, {-1, -1, 1, 1}};
        int i = row, j = col;
        int current = playingMap[row][col];
        for (int direction = 0; direction < 4; direction++) {
            int count = 0;
            i = row;
            j = col;
            while (i > 0 && i < boardSize && j > 0 && j < boardSize && playingMap[i][j] == current) {
                count++;
                if (count == 5) {
                    return true;
                }
                i += rc[direction][0];
                j += rc[direction][1];
            }
            count--;
            i = row;
            j = col;
            while (i > 0 && i < boardSize && j > 0 && j < boardSize && playingMap[i][j] == current) {
                count++;
                if (count == 5) {
                    return true;
                }
                i += rc[direction][2];
                j += rc[direction][3];
            }
        }
        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (playingMap[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startGame() {
        resetBoard(boardSize);
        int xPlr = (Math.random() < 0.5) ? 1 : 2;
        playerPlayingX = xPlr;
        playerPlayingO = 3 - xPlr;
        currentPlayer = playerPlayingX;
        gameEndedInDraw = false;
        winner = -1;
        gameInProgress = true;
    }

    private void resetBoard(int size) {
        if (playingMap != null) {
            playingMap = null;
        }
        playingMap = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                playingMap[i][j] = 0;
            }
        }
    }

    void startFristGame() {
        startGame();
        System.out.println("game start!"); 
    }

}
