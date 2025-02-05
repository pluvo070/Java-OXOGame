package edu.uob;

import java.io.Serial;

// 自定义异常类 OXOMoveException 继承自 Exception
public class OXOMoveException extends Exception {
    @Serial private static final long serialVersionUID = 1;

    // 构造函数，接收错误信息
    public OXOMoveException(String message) {
        super(message);
    }

    // 枚举类型，表示行或列
    public enum RowOrColumn { ROW, COLUMN }

    // 子类：超出单元格范围异常
    public static class OutsideCellRangeException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public OutsideCellRangeException(RowOrColumn dimension, int pos) {
            super("Position " + pos + " is out of range for " + dimension.name());
        }
    }

    // 子类：无效标识符长度异常
    public static class InvalidIdentifierLengthException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidIdentifierLengthException(int length) {
            super("Identifier of size " + length + " is invalid");
        }
    }

    // 子类：无效标识符字符异常
    public static class InvalidIdentifierCharacterException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidIdentifierCharacterException(RowOrColumn problemDimension, char character) {
            super(character + " is not a valid character for a " + problemDimension.name());
        }
    }

    // 子类：单元格已被占用异常
    public static class CellAlreadyTakenException extends OXOMoveException {
        @Serial private static final long serialVersionUID = 1;
        public CellAlreadyTakenException(int row, int column) {
            super("Cell [" + row + "," + column + "] has already been claimed");
        }
    }
}
