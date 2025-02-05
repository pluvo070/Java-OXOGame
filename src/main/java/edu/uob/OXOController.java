package edu.uob;

import java.io.Serial;
import java.io.Serializable;

// 用于控制游戏逻辑,改变和操纵 OXOModel 中的游戏状态
public class OXOController implements Serializable {
    @Serial private static final long serialVersionUID = 1;
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    /* 处理用户的输入, 如 "a1" 则在棋盘左上角添加对应用户的图形(大小写不敏感)
       先添加哪个玩家, 那个玩家就先开始游戏
       不在这里检查输入是否合法(下周在MoveException里做) */
    public void handleIncomingCommand(String command) throws OXOMoveException {
        if(gameModel.isGameOver()){return;} // 如果游戏结束,不再接受指令
        char row = command.charAt(0);
        char col = command.charAt(1);
        int i = row - 'a';
        int j = col - '1';
        // 检测当前棋盘方格是否已经被占用, 放在异常处理 MoveException 里做
        if(gameModel.getCellOwner(i, j) == null){
            int currentPlayerNumber = gameModel.getCurrentPlayerNumber();
            OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerNumber);
            gameModel.setCellOwner(i, j, currentPlayer);
            int playerArrLength = gameModel.getNumberOfPlayers();
            gameModel.setCurrentPlayerNumber((currentPlayerNumber + 1) % playerArrLength);
        }else{
            throw new OXOMoveException("Cell already taken");
        }
        // 检查是否游戏胜利
        if (checkWin()) {
            gameModel.setGameOver(true);
        }
        // 检查游戏是否平局
        if(checkBoardFilled() && !gameModel.isGameOver()){
            gameModel.setGameDrawn(true); // 设置平局状态
            // 平局后让然可以扩大棋盘继续游戏, 因此不设置gameover=true
        }
    }

    // (根据用户的鼠标点击)增删行列
    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        gameModel.removeRow();
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        gameModel.removeColumn();
    }

    // 增删胜利条件
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}

    // (按下ESC) 重置游戏, 让Model棋盘恢复为初始状态, 但是棋盘大小不变
    public void reset() {
        // 1. 清空棋盘: 将格子设为空
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
        gameModel.setCurrentPlayerNumber(0); // 设置当前玩家为第一个玩家
        gameModel.setWinner(null); // 清除赢家
        gameModel.setGameDrawn(false); // 清除平局状态
        gameModel.setGameOver(false); // 清除结束状态
    }

    // 检测是否有玩家获得胜利, 若有则设置Model中的win为当前玩家
    public boolean checkWin() {
        int winShold = gameModel.getWinThreshold();
        // 遍历棋盘, 检查行
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                OXOPlayer playerInThisBox = gameModel.getCellOwner(i, j);
                if (playerInThisBox == null) {
                    continue;
                }
                if (checkLine(i, j, 0, winShold, playerInThisBox, 1, 0)) { // 给行一个步长
                    gameModel.setWinner(playerInThisBox);
                    return true;
                }
            }
        }
        // 遍历棋盘, 检查列
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                OXOPlayer playerInThisBox = gameModel.getCellOwner(i, j);
                if (playerInThisBox == null) {
                    continue;
                }
                if (checkLine(i, j, 0, winShold, playerInThisBox, 0, 1)) { // 给列一个步长
                    gameModel.setWinner(playerInThisBox);
                    return true;
                }
            }
        }
        // 遍历棋盘, 检查斜线(以右上角为例, i-1, j + 1)
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                OXOPlayer playerInThisBox = gameModel.getCellOwner(i, j);
                if (playerInThisBox == null) {
                    continue;
                }
                if (checkLine(i, j, 0, winShold, playerInThisBox, -1, 1)) { // 斜方向的一个步长
                    gameModel.setWinner(playerInThisBox);
                    return true;
                }
            }
        }
        // 遍历棋盘, 检查另一条斜线(以右下角为例, i+1, j+1)
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                OXOPlayer playerInThisBox = gameModel.getCellOwner(i, j);
                if (playerInThisBox == null) {
                    continue;
                }
                if (checkLine(i, j, 0, winShold, playerInThisBox, 1, 1)) {
                    gameModel.setWinner(playerInThisBox);
                    return true;
                }
            }
        }
        return false;
    }

    // 胜利检测的辅助函数
    // 递归调用: 其中i代表行方向的增量, j代表列方向的增量
    private boolean checkLine(int row, int col, int count, int winShold, OXOPlayer playerInThisBox, int i, int j) {
        int index = gameModel.getIndex(row, col);
        // 递归结束条件
        if (row < 0 || row >= gameModel.getNumberOfRows() || col < 0 || col >= gameModel.getNumberOfColumns()) {
            return count >= winShold;
        }
        // 对当前状态的处理
        if (gameModel.getCellOwner(row, col) == playerInThisBox) { // 当前各自玩家等于上一个格子的玩家
            count++;
        } else {
            return count >= winShold;
        }
        // 递归调用
        return checkLine(row + i, col + j, count, winShold, playerInThisBox, i, j);
    }

    // 平局检测的辅助函数: 检查是否棋盘已经填满
    private boolean checkBoardFilled() {
        int row = gameModel.getNumberOfRows();
        int col = gameModel.getNumberOfColumns();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (gameModel.getCellOwner(i, j) == null) {
                    return false;
                }
            }
        }
        return true;
    }


}
