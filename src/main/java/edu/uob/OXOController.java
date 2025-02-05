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

    // 处理用户的输入, 如 "a1" 则在棋盘左上角添加对应用户的图形(大小写不敏感)
    // 先添加哪个玩家, 那个玩家就先开始游戏
    // 不在这里检查输入是否合法(下周再做)
    public void handleIncomingCommand(String command) throws OXOMoveException {




    }

    public void addRow() {}
    public void removeRow() {}
    public void addColumn() {}
    public void removeColumn() {}
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}

    // 让用户可以使用 ESC 键来重置游戏(调用Model类方法来清除板)
    public void reset() {}
}
