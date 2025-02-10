// 自动测试的脚本文件

package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/*import edu.uob.OXOController;*/
/*import edu.uob.OXOModel;*/
import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
/*import org.junit.jupiter.api.Test;*/
import java.time.Duration;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // 每个测试用例运行前都会调用此方法，以创建一个3x3的标准棋盘
  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // 发送命令到控制器的工具方法
  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // 尝试将命令发送到控制器，如果处理时间过长则超时
      // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // 测试基本的移动和单元格占用功能
  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() /*throws OXOMoveException*/ {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // 测试基本的胜利检测功能
  // Test out basic win detection
  @Test
  void testBasicWin() /*throws OXOMoveException*/ {
    // 获取当前轮到的玩家（他们应该是最终的赢家）
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // 进行一系列移动
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    // A1, A2, A3应当构成第一个玩家的胜利
    // 检查第一个玩家是否为赢家
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  // 测试抛出异常的情况
  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidIdentifierException() /*throws OXOMoveException*/ {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
  }

  /* ====================== 自定义脚本 ====================== */
  /* ----- 测试异常 -----*/
  // 1. 检测超出范围
  @Test
  void testOutsideCellRangeException() /*throws OXOMoveException*/ {
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `d1`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("d1"), failedTestComment);
  }
  // 2. 检测字符无效
  @Test
  void testInvalidIdentifierCharacterException() /*throws OXOMoveException*/ {
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `11`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("11"), failedTestComment);
  }
  // 3. 检测格子已被占用
  @Test
  void testCellAlreadyTakenException() /*throws OXOMoveException*/ {
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    String failedTestComment = "Controller failed to throw a CellAlreadyTakenException for command `B1`";
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("B1"), failedTestComment);
  }

  /* ----- 测试增删获胜阈值 ----- */
  @Test
  void testChangeWinThreshold() {
    controller.addRow();
    controller.addColumn();
    String failedTestComment1 = "Not expected WinThreshold";
    controller.increaseWinThreshold(); // ->4
    assertEquals(4, model.getWinThreshold(), failedTestComment1);
    controller.decreaseWinThreshold(); // ->3
    assertEquals(3, model.getWinThreshold(), failedTestComment1);
    //OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    controller.increaseWinThreshold(); // 游戏开始后可以增加->4
    assertEquals(4, model.getWinThreshold(), failedTestComment1);
    controller.decreaseWinThreshold(); // 游戏开始后不能减少->4
    assertEquals(4, model.getWinThreshold(), failedTestComment1);
    sendCommandToController("c1"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("d2"); // Second player
    /*    1  2  3  4
     * a  x  o  x
     * b  x  o
     * c  x  o
     * d     o      */
    String failedTestComment2 = "O is not the winner";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment2); // 检测获胜者
    controller.increaseWinThreshold(); // 游戏结束后不可以增加->4
    assertEquals(4, model.getWinThreshold(), failedTestComment1);
    controller.decreaseWinThreshold(); // 游戏开始后不可以减少->4
    assertEquals(4, model.getWinThreshold(), failedTestComment1);
    controller.reset(); // 重置游戏后获胜阈值不变
    String failedTestComment3 = "Something wrong in reset";
    assertEquals(4, model.getWinThreshold(), failedTestComment3);
  }

  /* ----- 测试更改玩家数量 ----- */
  @Test
  void testChangeNumOfPlayer() {
    controller.addPlayer(new OXOPlayer('A'));
    controller.addPlayer(new OXOPlayer('B'));
    controller.addRow();
    controller.addColumn();
    OXOPlayer playerX = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // X
    OXOPlayer playerO = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("b1"); // O
    OXOPlayer playerA = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c1"); // A
    OXOPlayer playerB = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("d1"); // B
    String failedTestComment = "Not expected character. Failed to add Players.";
    assertEquals(playerX, model.getCellOwner(0, 0), failedTestComment);
    assertEquals(playerO, model.getCellOwner(1, 0), failedTestComment);
    assertEquals(playerA, model.getCellOwner(2, 0), failedTestComment);
    assertEquals(playerB, model.getCellOwner(3, 0), failedTestComment);
    /*    1  2  3  4
     * a  X
     * b  O
     * c  A
     * d  B        */
    controller.removePlayer(playerA);
    // 清除A的全部棋子
    String failedTestComment2 = "Failed to remove Player A";
    assertEquals(null, model.getCellOwner(2, 0), failedTestComment2);
    // 玩家个数-1
    assertEquals(3, model.getNumberOfPlayers(), failedTestComment2);
  }

}
