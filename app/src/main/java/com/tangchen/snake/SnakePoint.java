package com.tangchen.snake;

/**
 * Created by TangChen on 17/10/24.
 */

public class SnakePoint {
    public int x, y;

    public SnakePoint() {

    }

    public SnakePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        SnakePoint compare = (SnakePoint) obj;
        return this.x == compare.x && this.y == compare.y;
    }
}
