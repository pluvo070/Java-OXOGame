package edu.uob;

import java.io.Serial;
import java.io.Serializable;

// 用于维持游戏状态
public class OXOModel implements Serializable {
    // Serializable 接口是在 java 中做 GUI 时必须要实现的

    // 表示该类是序列化的, 每个序列化的类都有一个唯一的序列化ID
    @Serial private static final long serialVersionUID = 1;

    // 棋盘样式
    /*    | 1 | 2 | 3 |
    *   -----------------
    *   a |   |   |   |
    *   b |   |   |   |
    *   c |   |   |   |    */

    // 成员变量
    private OXOPlayer[][] cells; // 存储网格
    private OXOPlayer[] players; // 存储玩家(标准2个)
    private int currentPlayerNumber; // 当前玩家
    private OXOPlayer winner;
    private boolean gameDrawn; // 记录是否平局(棋盘填满但没有赢家)
    private int winThreshold; // 胜利所需连续单元格(标准3个)

    // 构造棋盘和玩家
    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new OXOPlayer[numberOfRows][numberOfColumns];
        players = new OXOPlayer[2];
    }

    // 获取玩家个数
    public int getNumberOfPlayers() {
        return players.length;
    }

    // 添加玩家填满玩家数组
    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.length;
    }

    public int getNumberOfColumns() {
        return cells[0].length;
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells[rowNumber][colNumber];
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells[rowNumber][colNumber] = player;
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean isDrawn) {
        gameDrawn = isDrawn;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}
