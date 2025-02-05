package edu.uob;

import java.io.Serial;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/* 使用 Java 的 AWT（Abstract Window Toolkit） 库构建 GUI（图形用户界面）
   包括游戏逻辑控制、用户输入处理，以及窗口的交互功能。*/
public final class OXOGame extends Frame implements WindowListener, ActionListener, MouseListener, KeyListener {
    @Serial private static final long serialVersionUID = 1;
    private static Font FONT = new Font("SansSerif", Font.PLAIN, 14); // 设置统一字体

    OXOController controller; // 控制游戏逻辑
    TextField inputBox; // 供用户输入命令的文本框
    OXOView view; // 绘制游戏界面

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on"); // 抗锯齿字体
        System.setProperty("swing.aatext", "true"); // 文本渲染
        new  OXOGame(250, 300); // 创建窗口对象
    }

    // 构造方法
    public OXOGame(int width, int height) {
        super("OXO Board"); // 窗口标题
        // 初始化游戏模型: 棋盘3*3, 并添加玩家X和O
        OXOModel model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        // 创建控制器, 负责解析输入命令, 并更新 OXOModel
        controller = new OXOController(model);
        // 创建文本输入框
        inputBox = new TextField("");
        inputBox.addActionListener(this); // 监听回车键
        inputBox.setFont(FONT);
        inputBox.addKeyListener(this); //监听按键输入
        // 创建游戏棋盘
        view = new OXOView(model); // 绘制棋盘
        view.addMouseListener(this); // 监听鼠标点击
        view.addKeyListener(this); // 监听按键输入
        // 组装页面
        Panel contentPane = new Panel(); // 容器
        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputBox, BorderLayout.SOUTH); // 输入框放在底部
        contentPane.add(view, BorderLayout.CENTER); // 游戏棋盘放在中心
        this.setLayout(new GridLayout(1, 1)); // 单元格布局
        this.add(contentPane);
        this.setSize(width, height); // 窗口大小
        this.setVisible(true); // 显示窗口
        this.addWindowListener(this); // 监听窗口事件
    }

    // 设置窗口边距
    public Insets getInsets() {
        return new Insets(30, 7, 7, 7);
    }

    /* 处理用户输入:
       1. 获取输入框内容，交给 OXOController 解析
       2. OXOMoveException 处理非法输入 */
    public void actionPerformed(ActionEvent event) {
        try {
            String command = inputBox.getText();
            inputBox.setText("");
            controller.handleIncomingCommand(command);
            view.repaint();
        } catch (OXOMoveException exception) {
            System.out.println("Game move exception: " + exception);
        }
    }

    /* 监听鼠标事件:
       左键点击增加, 右键点击减少
       点击行号增删行, 点击列号增删列, 点击左上区域同时增删行列 */
    public void mousePressed(MouseEvent event) {
        if (event.getX() < 35) {
            if (event.isPopupTrigger()) controller.removeRow();
            else if (event.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) controller.removeRow();
            else controller.addRow();
        }
        if (event.getY() < 35) {
            if (event.isPopupTrigger()) controller.removeColumn();
            else if (event.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) controller.removeColumn();
            else controller.addColumn();
        }
        view.repaint();
    }

    /* 监听键盘事件:
       1. 防止用户输入 = 或 -（这些符号用于调整胜利条件）
          = 增加胜利条件（如 3 变 4）
          - 减少胜利条件（如 4 变 3）
       2. ESC 键（VK_ESCAPE）重置游戏 */
    public void keyPressed(KeyEvent event) {
        inputBox.setText(inputBox.getText().replace("=",""));
        inputBox.setText(inputBox.getText().replace("-",""));
        view.repaint();
    }

    public void keyReleased(KeyEvent event) {
        inputBox.setText(inputBox.getText().replace("=",""));
        inputBox.setText(inputBox.getText().replace("-",""));
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE) controller.reset();
        view.repaint();
    }

    public void keyTyped(KeyEvent event) {
        if (event.getKeyChar() == '=') controller.increaseWinThreshold();
        if (event.getKeyChar() == '-') controller.decreaseWinThreshold();
        view.repaint();
    }

    public void mouseClicked(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void windowActivated(WindowEvent event) {}
    public void windowDeactivated(WindowEvent event) {}
    public void windowDeiconified(WindowEvent event) {}
    public void windowIconified(WindowEvent event) {}
    public void windowClosed(WindowEvent event) {}
    public void windowOpened(WindowEvent event) {}

    // 监听窗口事件, 关闭时退出程序
    public void windowClosing(WindowEvent e) {
        this.dispose();
        System.exit(0);
    }
}
