package com.tangchen.snake;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.tangchen.snake.Const.LEFT;
import static com.tangchen.snake.Const.TOP;

/**
 * Created by TangChen on 17/10/24.
 */

public class Snake {
    private static Snake instance;
    public int[][] mapArray = new int[Const.mapSize][Const.mapSize];
    private SnakePoint headPoint = new SnakePoint();
    private SnakePoint tailPoint = new SnakePoint();
    public SnakePoint food;
    private BFS bfs;
    private SnakeView snakeView;
    List<SnakePoint> snakePoints = new ArrayList<>();

    private Snake() {

    }

    public static Snake getInstance() {
        if (instance == null) {
            instance = new Snake();
        }
        return instance;
    }

    public void bind(SnakeView snakeView, BFS bfs) {
        this.snakeView = snakeView;
        this.bfs = bfs;
    }

    //    蛇身运动算法
    private void motionSnake() {
        SnakePoint nextPoint;

        switch (snakeView.moveDirection) {
            case Const.RIGHT:
                nextPoint = new SnakePoint(headPoint.x + 1, headPoint.y);
                break;
            case LEFT:
                nextPoint = new SnakePoint(headPoint.x - 1, headPoint.y);
                break;
            case TOP:
                nextPoint = new SnakePoint(headPoint.x, headPoint.y + 1);
                break;
            default:
                nextPoint = new SnakePoint(headPoint.x, headPoint.y - 1);
                break;
        }

//        蛇头碰撞到蛇身
//        for (SnakePoint point : snakePoints) {
//            if (point.equals(nextPoint)) {
//                for (int i = 0; i < snakePoints.size(); i++) {
//                    if (snakePoints.get(i).equals(nextPoint))
//                        snakePoints = snakePoints.subList(i, snakePoints.size());
//                }
//            }
//        }

        snakePoints.add(nextPoint);
        headPoint = snakePoints.get(snakePoints.size() - 1);

        if (nextPoint.equals(food)) {
//            snakeView.isEat = true;
            while (!checkUsable(produceFood())) ; //生成一个新的食物

            bfs.path();

//            if (INTERVAL >= 500)
//                INTERVAL *= 0.95;
        } else
            snakePoints.remove(tailPoint);

        tailPoint = snakePoints.get(Const.TAIL_POINT);
    }

//    撞击检测
//    private boolean hitWall() {
//        switch (snakeView.moveDirection) {
//            case Const.RIGHT:
//                if (mapArray[headPoint.x + 1][headPoint.y] == Const.isWall)
//                    return true;
//                break;
//            case Const.TOP:
//                if (mapArray[headPoint.x][headPoint.y + 1] == Const.isWall)
//                    return true;
//                break;
//            case Const.LEFT:
//                if (mapArray[headPoint.x - 1][headPoint.y] == Const.isWall)
//                    return true;
//                break;
//            case Const.BOTTOM:
//                if (mapArray[headPoint.x][headPoint.y - 1] == Const.isWall)
//                    return true;
//        }
//
//        return false;
//    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        //接收到消息后处理
        public void handleMessage(Message msg) {
            switch ((String) msg.obj) {
                case "refresh":
                    snakeView.invalidate();//刷新界面
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void initSnake() {
        for (int i = 5; i <= 7; i++)
            snakePoints.add(new SnakePoint(i, 5));

//        设置蛇尾为动态数组第一个数据
        tailPoint = snakePoints.get(Const.TAIL_POINT);
//        蛇头为数组最后一个数据
        headPoint = snakePoints.get(snakePoints.size() - 1);
        start();
    }

    private void start() {
        snakeView.initMap(mapArray);
        while (!checkUsable(produceFood())) ;
        bfs.path();
        new Thread(new GameThread()).start();
    }

    public SnakePoint getHeadPoint() {
        return headPoint;
    }

    public SnakePoint getTailPoint() {
        return tailPoint;
    }

    public boolean checkUsable(SnakePoint newFoodPoint) {
//        遍历整个蛇身数组，检查生成的食物是否在蛇身或墙内
        for (SnakePoint point : snakePoints) {
            if (point.equals(newFoodPoint) || mapArray[newFoodPoint.x][newFoodPoint.y] == Const.isWall)
                return false;
        }

//        生成食物前检测实物落点是否可达
        if (!bfs.reachable(newFoodPoint))
            return false;

        this.food = newFoodPoint;
        mapArray[food.x][food.y] = Const.isFood;

        return true;
    }

    private SnakePoint produceFood() {
//        通过随机数生成xy两个数据
        Random random = new Random();
        int x, y;
        x = random.nextInt(Const.mapSize);
        y = random.nextInt(Const.mapSize);

        return new SnakePoint(x, y);
    }


    public class GameThread implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                bfs.path(); //每走一次都进行一次BFS来判断下一步方向。去掉该行也可执行，但有时会绕过empty区。
                snakeView.moveDirection = bfs.nextDir();
                motionSnake();
                Message message = new Message();
                message.obj = "refresh";
                myHandler.sendMessage(message);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                //snakeView.resetSnake();
            }
        }
    }
}