package project;

import java.util.List;

public interface FileIO {
    List<Integer> read();
    void write(int score);
}