package edu.uob;

import java.io.Serial;
import java.io.Serializable;

// 玩家类 有一个成员变量表示玩家的图形
public class OXOPlayer implements Serializable {
    // 表示该类是序列化的, 每个序列化的类都有一个唯一的序列化ID
    @Serial private static final long serialVersionUID = 1;

    private char letter;

    public OXOPlayer(char playingLetter) {
        letter = playingLetter;
    }

    public char getPlayingLetter() {
        return letter;
    }

    public void setPlayingLetter(char letter) {
        this.letter = letter;
    }
}
