package com.tangchen.snake;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.tangchen.snake.Const.*;

/**
 * Created by TangChen on 17/10/24.
 */

public class Snake {
    public static Snake instance;
    public SnakePoint headPoint = new SnakePoint();
    public SnakePoint tailPoint = new SnakePoint();
    public SnakeView snakeView;
    public int INTERVAL = 900;
    public List<SnakePoint> snakePoints = new ArrayList<>();

    private Snake() {

    }

    public static Snake getInstance() {
        if (instance == null) {
            instance = new Snake();
        }
        return instance;
    }

    public void bindView(SnakeView snakeView) {
        this.snakeView = snakeView;
    }

    //    蛇身运动算法
    private void motionSnake() {
        SnakePoint nextPoint;

        if (snakeView.moveDirection == RIGHT) {
            nextPoint = new SnakePoint(headPoint.x + 1, headPoint.y);
        } else if (snakeView.moveDirection == LEFT) {
            nextPoint = new SnakePoint(headPoint.x - 1, headPoint.y);
        } else if (snakeView.moveDirection == TOP) {
            nextPoint = new SnakePoint(headPoint.x, headPoint.y + 1);
        } else {
            nextPoint = new SnakePoint(headPoint.x, headPoint.y - 1);
        }

        for (SnakePoint point : snakePoints) {
            if (point.equals(nextPoint)) {
                for (int i = 0; i < snakePoints.size(); i++) {
                    if (snakePoints.get(i).equals(nextPoint)) {
                        snakePoints = snakePoints.subList(i, snakePoints.size());
                    }
                }
            }
        }

        snakePoints.add(nextPoint);

        if (nextPoint.equals(snakeView.food)) {
            snakeView.isEat = true;
            INTERVAL *= 0.95;
        } else {
            snakePoints.remove(tailPoint);
        }

        tailPoint = snakePoints.get(Const.TAILPOINT);
        headPoint = snakePoints.get(snakePoints.size() - 1);
    }

    private boolean hitWall() {
        switch (snakeView.moveDirection) {
            case RIGHT:
                if (snakeView.mapArray[headPoint.x + 1][headPoint.y] == isWall) {
                    Log.d("hit wall", "true");
                    return true;
                }
                break;
            case TOP:
                if (snakeView.mapArray[headPoint.x][headPoint.y + 1] == isWall) {
                    Log.d("hit wall", "true");
                    return true;
                }
                break;
            case LEFT:
                if (snakeView.mapArray[headPoint.x - 1][headPoint.y] == isWall) {
                    Log.d("hit wall", "true");
                    return true;
                }
                break;
            case BOTTOM:
                if (snakeView.mapArray[headPoint.x][headPoint.y - 1] == isWall) {
                    Log.d("hit wall", "true");
                    return true;
                }
        }

        return false;
    }

    Handler myHandler = new Handler() {
        //接收到消息后处理
        public void handleMessage(Message msg) {
            switch ((String) msg.obj) {
                case "refresh":
                    Log.d("refresh", "r");
                    snakeView.invalidate();//刷新界面
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class GameThread implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
//                当没撞墙时，每次循环调用一次运动方法，将整个蛇身向某个方向运动
                while (!hitWall()) {
                    motionSnake();
                    Message message = new Message();
                    message.obj = "refresh";
                    myHandler.sendMessage(message);
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                snakeView.resetSnake();
            }
        }
    }
}
