package com.tangchen.snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import static com.tangchen.snake.Const.*;

/**
 * Created by TangChen on 17/10/24.
 */

public class SnakeView extends View {
    public int[][] mapArray = new int[mapSize][mapSize];
    public int moveDirection;
    private boolean isOnce = true;
    public boolean isEat = false;
    SnakePoint food;
    private Snake snakeInstance = Snake.getInstance();
    int offsetX = getWidth() / mapSize;
    int offsetY = getWidth() / mapSize;

    public SnakeView(Context context) {
        super(context);
    }

    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SnakeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SnakeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //    绘制整张地图，先将数组的边界绘制为墙
    public void initMap() {
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                if (x == 0 || y == 0 || x == mapSize - 1 || y == mapSize - 1) {
                    mapArray[x][y] = isWall;
                } else {
                    mapArray[x][y] = isEmpty;
                }
            }
        }
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initMap(); //每次绘制重置地图

        Paint paint = new Paint();

        for (SnakePoint snackPoint : snakeInstance.snakePoints) {
            mapArray[snackPoint.x][snackPoint.y] = isFull; //遍历整个snakePoints数组，将其内存放的连续坐标都设置为已满（即为蛇身）
        }

        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                Rect r = new Rect(x * offsetX, y * offsetY, offsetX + offsetX * x, offsetY + offsetY * y);
                if (mapArray[x][y] == isEmpty) {
                    paint.setColor(Color.WHITE);
                } else if (mapArray[x][y] == isWall) {
                    paint.setColor(Color.GREEN);
                } else {
                    paint.setColor(Color.RED);
                }

                canvas.drawRect(r, paint); //绘制

            }
        }

        if (isOnce) {
            init();
            new Thread(snakeInstance.new GameThread()).start();
            isOnce = false;
        }

        if (isEat) {
            while (!checkUsable(food = produceFood())) ;
            isEat = false;
        }

        mapArray[food.x][food.y] = isFood;

        Rect r = new Rect(food.x * offsetX, food.y * offsetY, offsetX + offsetX * food.x, offsetY + offsetY * food.y);
        paint.setColor(Color.BLUE);
        canvas.drawRect(r, paint);
    }

    public void resetSnake() {
        initMap();
        snakeInstance.snakePoints.clear();
        snakeInstance.INTERVAL = 1000;
        init();
    }


    /**
     * 初始化，生成一条向右运动，于第五行第五列到第五行第七列的占同三格的蛇
     */
    public void init() {
        moveDirection = RIGHT;

        for (int i = 5; i <= 7; i++) {
            snakeInstance.snakePoints.add(new SnakePoint(i, 5));
        }

//        设置蛇尾为动态数组第一个数据
        snakeInstance.tailPoint = snakeInstance.snakePoints.get(Const.TAILPOINT);
//        蛇头为数组最后一个数据
        snakeInstance.headPoint = snakeInstance.snakePoints.get(snakeInstance.snakePoints.size() - 1);

        while (!checkUsable(food = produceFood())) ;
    }

    private boolean checkUsable(SnakePoint newFoodPoint) {
//        遍历整个蛇身数组，检查生成的食物是否在蛇身或墙内
        for (SnakePoint point : snakeInstance.snakePoints) {
            if (point.equals(newFoodPoint) || mapArray[newFoodPoint.x][newFoodPoint.y] == isWall) {
                return false;
            }
        }

        return true;
    }

    private SnakePoint produceFood() {
//        通过随机数生成xy两个数据
        Random random = new Random();
        int x, y;
        x = random.nextInt(14);
        y = random.nextInt(14);

        return new SnakePoint(x, y);
    }
}
