
// 用于控制游戏逻辑,改变和操纵 OXOModel 中的游戏状态

package edu.uob;

import java.io.Serial;
import java.io.Serializable;

public class OXOController implements Serializable {
    @Serial private static final long serialVersionUID = 1;
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    /* =============== 处理用户的输入 ============= */
    /* 如 "a1" 则在棋盘左上角添加对应用户的图形(大小写不敏感)
       先添加哪个玩家, 那个玩家就先开始游戏 */
    public void handleIncomingCommand(String command) throws OXOMoveException {
        validateCommandLength(command); // 异常处理1: 检测传入字符串长度是否是两位
        if (gameModel.isGameOver()) return;  // 如果游戏结束,不再接受指令
        // 拆分字符串的两个字符
        char row = command.charAt(0);
        char col = command.charAt(1);
        validateCommandCharacters(row, col); // 异常处理2: 检测传入字符串的两个字符值是否正常(第一个是字母, 第二个是数字)
        boolean flag = Character.isUpperCase(row); // 用于存储是大写还是小写字母. 是大写字母则为true
        validateCommandInRange(row, col, flag);// 异常处理3: 检测两个字符是否在行列的范围内
        // 获取字符所表示的在棋盘上对应的字母和数字
        int i;
        if(flag){ //大写字母
            i = row - 'A';
        } else { // 小写字母
            i = row - 'a';
        }
        int j = col - '1';
        validateCellAvailability(i, j);// 异常处理4: 当前格子被占用
        // 将当前玩家的符号添加到对应的格子
        int currentPlayerNumber = gameModel.getCurrentPlayerNumber();
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerNumber);
        gameModel.setCellOwner(i, j, currentPlayer);
        // 切换到下一个玩家
        int playerArrLength = gameModel.getNumberOfPlayers();
        gameModel.setCurrentPlayerNumber((currentPlayerNumber + 1) % playerArrLength);
        // 检查是否游戏胜利
        if (checkWin()) {
            gameModel.setGameOver(true);
        }
        // 检查游戏是否平局
        if (checkBoardFilled() && !gameModel.isGameOver()) {
            gameModel.setGameDrawn(true); // 设置平局状态
            // 平局后让然可以扩大棋盘继续游戏, 因此不设置gameover=true
        }
    }

    // 异常处理1: 检测传入字符串长度是否是两位
    private void validateCommandLength(String command) throws OXOMoveException.InvalidIdentifierLengthException {
        if (command.length() != 2) {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }
    }

    // 异常处理2: 检测传入字符串的两个字符值是否正常(第一个是字母, 第二个是数字)
    private void validateCommandCharacters(char row, char col) throws OXOMoveException.InvalidIdentifierCharacterException {
        if (!Character.isLetter(row)) { // 这是 Character 类的静态方法, 用于判断单个字符是否是字母
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, row);
        }
        if (!Character.isDigit(col)) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.COLUMN, col);
        }
    }

    // 异常处理3: 检测两个字符是否在行列的范围内
    private void validateCommandInRange(char row, char col, boolean flag) throws OXOMoveException.OutsideCellRangeException {
        int width = gameModel.getNumberOfRows();
        int height = gameModel.getNumberOfColumns();

        if(flag){ //大写字母
            if (row < 'A' || row > width + 'A' - 1) {
                throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, row);
            }
        }
        else{ // 小写字母
            if (row < 'a' || row > width + 'a' - 1) {
                throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, row);
            }
        }
        if (col < '1' || col > height + '1' - 1) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN, col);
        }
    }

    // 异常处理4: 检查指定格子是否已被占用
    private void validateCellAvailability(int i, int j) throws OXOMoveException.CellAlreadyTakenException {
        if (gameModel.getCellOwner(i, j) != null) {
            throw new OXOMoveException.CellAlreadyTakenException(i, j);
        }
    }

    /* =============== 交互式操控棋盘 ============= */

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

    // 增减获胜阈值(连续几个棋子可以获胜)
    /* 游戏开始前可以增减, 游戏开始后可以增但不可以减, 有人获胜后不可以增减 */
    public void increaseWinThreshold() {
        if (gameModel.isGameOver()) return; // 有人获胜后不可以改变获胜阈值
        int currentThreshold = gameModel.getWinThreshold(); // 获得现有的阈值
        int maxThreshold = Math.min(gameModel.getNumberOfRows(), gameModel.getNumberOfColumns()); // 获得最大允许的阈值
        if (currentThreshold < maxThreshold) { // 如果没到最大值, 则可以增加
            gameModel.setWinThreshold(currentThreshold + 1);
        }
    }
    public void decreaseWinThreshold() {
        if (gameModel.isGameOver()) return; // 有人获胜后不可以改变获胜阈值
        // 棋盘上有棋子时不可以减少阈值
        if (checkGameStarted()) return;
        // 改变阈值
        int currentThreshold = gameModel.getWinThreshold(); // 获得现有的阈值
        int minThreshold = 3; // 最小阈值是3
        if (currentThreshold > minThreshold) { // 如果没到最小值, 则可以减少
            gameModel.setWinThreshold(currentThreshold - 1);
        }
    }

    // (按下ESC) 重置游戏, 让Model棋盘恢复为初始状态, 但是棋盘大小不变, 获胜阈值不变
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

    /* =============== 非交互式操控棋盘 ============= */
    /* 增加和减少玩家的数量, 不通过交互操控, 只用于在测试文件中进行测试以下函数是否正确 */
    public void addPlayer(OXOPlayer player) {
        gameModel.addOnePlayer(player);
    }
    public void removePlayer(OXOPlayer player) {
        gameModel.removeOnePlayer(player);
    }

    /* =============== 辅助函数 ==============*/
    // 胜利检测的辅助函数
    // 递归调用: 其中i代表行方向的增量, j代表列方向的增量
    private boolean checkLine(int row, int col, int count, int winShold, OXOPlayer playerInThisBox, int i, int j) {
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

    // 平局检测的辅助函数: 检查是否棋盘已经填满, 填满则返回true
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

    // 减少获胜阈值的辅助函数: 检查是否棋盘有棋子, 有则返回true
    private boolean checkGameStarted() {
        int row = gameModel.getNumberOfRows();
        int col = gameModel.getNumberOfColumns();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (gameModel.getCellOwner(i, j) != null) {
                    return true;
                }
            }
        }
        return false;
    }


}
