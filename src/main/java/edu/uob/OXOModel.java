package edu.uob;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

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

    /* ====================== 成员变量 ===================== */

    //private OXOPlayer[][] cells; // 存储网格, 数组实现
    private final ArrayList<OXOPlayer> cells;// 存储网格, 集合实现

    //private OXOPlayer[] players; // 存储玩家(标准2个)
    private final ArrayList<OXOPlayer> players; // 存储玩家(标准2个), 用集合实现, 可以动态增删

    private int currentPlayerNumber; // 当前玩家
    private OXOPlayer winner; // 设置赢家, 会自动在屏幕上显示赢家(在GUI相关处已写好)
    private boolean gameDrawn; // 记录是否平局, 会自动在屏幕上显示平局(在GUI相关处已写好)
    private int winThreshold; // 胜利所需连续单元格(标准3个)

    private int numberOfRows;
    private int numberOfColumns;
    private static final int maxNumberOfRows = 9;
    private static final int maxNumberOfColumns = 9;
    private static final int minNumberOfRows = 3;
    private static final int minNumberOfColumns = 3;

    private boolean gameOver; // 用于表示游戏是否结束

    private static final int minNumberOfPlayers = 2;
    private static final int maxNumberOfPlayers = 10;

    /* =================== 构造函数: 构造棋盘和玩家 ================== */
    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.winThreshold = winThresh;
        // 创建棋盘集合并初始化棋盘大小(添加空对象)
        this.cells = new ArrayList<>();
        for (int i = 0; i < numberOfRows * numberOfColumns; i++) {
            cells.add(null);
        }
        // 创建玩家集合并添加两个玩家
        this.players = new ArrayList<>();
        players.add(new OXOPlayer('X'));
        players.add(new OXOPlayer('O'));
        gameOver = false;
    }

    /* ======================== 添加玩家数量 ====================== */
    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, player);
                return;
            }
        }
    }

    /* =================== 添加/删除板的长度和高度 ==================*/
    public void addColumn(){
        if(gameOver || numberOfColumns == maxNumberOfColumns){ return; }
        /* 方法1: 从第一行开始添加, 每隔[列数+1]个添加一次
        for (int i = numberOfColumns-1; i < cells.size(); i += (numberOfColumns+1)) {
            cells.add(i+1, null);
        }*//* 注意: 这里 i+1 是因为 [add 指定索引的重载函数] 会在当前索引处插入元素,
                不 +1 相当于在最后一列之前插入新一列
                而 [add不指定索引的重载函数] 直接在末尾追加, 这两者不一样 */

        // 方法2: 遍历行, 计算每一行末尾的索引, +1空位
        for (int i = 0; i < numberOfRows; i++){
            int index = getIndex(i, numberOfColumns); // 在当前行的末尾的索引
            cells.add(index, null);
        }
        numberOfColumns++;
    }
    public void addRow(){ // 在最后添加新的一行
        if(gameOver || numberOfRows == maxNumberOfRows){ return; }
        // 不需要计算索引, 直接使用一个参数的add方法, 在末尾增加列数个元素
        for (int i = 0; i < numberOfColumns; i++) {
            cells.add(null); // 一共添加[列的数量]次
        }
        numberOfRows++;
    }
    /*  !!!!!! 有内容的行列不可以被删除 !!!!!! */
    public void removeColumn(){
        if(gameOver || numberOfColumns == minNumberOfColumns){ return; }
        /* 先检测这一列的元素是否都为空
           从第一行开始, 获取每一行末尾元素索引进行检测 */
        for (int i = 0; i < numberOfRows; i++) {
            int index = getIndex(i, numberOfColumns - 1);
            if (cells.get(index) != null) {
                return;
            }
        }
        /* 方法1: 从最后一个开始删除, 每隔[列数]个元素就删除一次
           从后往前删除, 确保前面即将删除的元素的索引不变 */
        for(int i = cells.size()-1; i >= numberOfColumns -1; i -= numberOfColumns){
            cells.remove(i);
        }
        numberOfColumns--;
        /* 方法2: 从最后一行开始, 获取每一行末尾元素索引删除 */
    }
    public void removeRow(){ // 删除最后一行
        if(gameOver || numberOfRows == minNumberOfRows){ return; }
        // 计算需要删除的所有范围, 从最后一个开始往前删除
        int start = getIndex(numberOfRows - 1, 0);
        int end = getIndex(numberOfRows - 1, numberOfColumns - 1);
        // 先判断这一行是否有元素
        for (int i = end; i >= start; i--) {
            if(cells.get(i)!=null){return;}
        }
        // 从最后一个开始往前删除
        for (int i = end; i >= start; i--) {
            cells.remove(i);
        }
        numberOfRows--;
    }

    /* =================== Get / Set 方法 ================== */
    // 获取玩家个数
    public int getNumberOfPlayers() {
        return players.size();
    }

    // 根据玩家编号获取玩家
    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
    }

    // 设置/获取赢家
    public OXOPlayer getWinner() {return winner;}
    public void setWinner(OXOPlayer player) {winner = player;}

    // 设置/获取当前玩家序号
    public int getCurrentPlayerNumber() {return currentPlayerNumber;}
    public void setCurrentPlayerNumber(int playerNumber) {currentPlayerNumber = playerNumber;}

    // 获得棋盘大小
    public int getNumberOfRows() {return this.numberOfRows;}
    public int getNumberOfColumns() {return this.numberOfColumns;}

    // 设置/获取某个格子的拥有者(格子里填的是什么字符)
    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        int index = getIndex(rowNumber, colNumber);
        return cells.get(index); // get是集合的方法: 通过索引获取元素
    }
    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        int index = getIndex(rowNumber, colNumber);
        cells.set(index, player); // set是集合的方法: 通过索引设置元素
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }
    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean isDrawn) { // 设置为平局
        gameDrawn = isDrawn;
    }
    public boolean isGameDrawn() { // 就是 get方法 (判断是否是平局? 返回T/F)
        return gameDrawn;
    }
    // 设置和获取游戏状态是否结束
    public void setGameOver(boolean flag) {
        this.gameOver = flag;
    }
    public boolean isGameOver() {
        return gameOver;
    }

    /* ======================= 辅助方法 ======================*/
    // 行列 -> 在集合中的索引
    public int getIndex(int rowNumber, int colNumber) {
        return rowNumber * numberOfColumns + colNumber;
    }

    /* ======================= 增删玩家数量 ======================*/
    public void addOnePlayer(OXOPlayer player) {
        if (players.size() >= maxNumberOfPlayers) return;
        players.add(player);
    }
    public void removeOnePlayer(OXOPlayer player) {
        if (players.size() <= minNumberOfPlayers) return;
        // 删除所有该玩家的棋子
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (getCellOwner(i, j) == player) {
                    setCellOwner(i, j, null);
                }
            }
        }
        // 删除该玩家
        players.remove(player);
    }

}
