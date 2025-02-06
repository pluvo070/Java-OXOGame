package edu.uob;

import java.io.Serial;

// 自定义编译器异常类 OXOMoveException 继承自 Exception
public class OXOMoveException extends Exception {
    @Serial private static final long serialVersionUID = 1;

    // 构造函数，接收错误信息
    public OXOMoveException(String message) {
        super(message);
    }

    // 枚举类型，表示行或列
    public enum RowOrColumn { ROW, COLUMN }

    // 子异常类：超出单元格范围
    public static class OutsideCellRangeException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public OutsideCellRangeException(RowOrColumn dimension, int pos) {
            super("Position " + pos + " is out of range for " + dimension.name());
        }
    }

    // 子异常类：标识符长度无效
    public static class InvalidIdentifierLengthException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidIdentifierLengthException(int length) {
            super("Identifier of size " + length + " is invalid");
        }
    }

    // 子异常类：标识符字符无效
    public static class InvalidIdentifierCharacterException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidIdentifierCharacterException(RowOrColumn problemDimension, char character) {
            super(character + " is not a valid character for a " + problemDimension.name());
        }
    }

    // 子异常类：单元格已被占用
    public static class CellAlreadyTakenException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public CellAlreadyTakenException(int row, int column) {
            super("Cell [" + row + "," + column + "] has already been claimed");
        }
    }
}
