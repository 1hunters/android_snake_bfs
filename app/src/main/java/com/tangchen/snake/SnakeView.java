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

import java.util.List;

/**
 * Created by TangChen on 17/10/24.
 */

public class SnakeView extends View {
    public int moveDirection;
    private Snake controller = Snake.getInstance();

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
    public void initMap(int[][] mapArray) {
        for (int x = 0; x < Const.mapSize; x++) {
            for (int y = 0; y < Const.mapSize; y++) {
                if (x == 0 || y == 0 || x == Const.mapSize - 1 || y == Const.mapSize - 1)
                    mapArray[x][y] = Const.isWall;
                else
                    mapArray[x][y] = Const.isEmpty;
            }
        }
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int[][] mapArray = controller.mapArray;
        Paint paint = new Paint();

        initMap(mapArray); //每次绘制重置地图
        int offsetX = getWidth() / Const.mapSize;
        int offsetY = getWidth() / Const.mapSize;
        SnakePoint headPoint = controller.getHeadPoint();

        List<SnakePoint> snakeBody = controller.snakePoints;

        for (SnakePoint snakePoint : snakeBody)
            mapArray[snakePoint.x][snakePoint.y] = Const.isFull; //遍历整个snakePoints数组，将其内存放的连续坐标都设置为已满（即为蛇身）

        for (int x = 0; x < Const.mapSize; x++) {
            for (int y = 0; y < Const.mapSize; y++) {
                Rect r = new Rect(x * offsetX, y * offsetY, offsetX + offsetX * x, offsetY + offsetY * y);
                if (mapArray[x][y] == Const.isEmpty)
                    paint.setColor(Color.WHITE);
                else if (mapArray[x][y] == Const.isWall)
                    paint.setColor(Color.GREEN);
                else if (headPoint.x == x && headPoint.y == y)
                    paint.setColor(Color.BLACK);
                else
                    paint.setColor(Color.RED);

                canvas.drawRect(r, paint); //绘制
            }
        }
        SnakePoint food = controller.food;
        mapArray[food.x][food.y] = Const.isFood;

        Rect r = new Rect(food.x * offsetX, food.y * offsetY,
                offsetX + offsetX * food.x, offsetY + offsetY * food.y);
        paint.setColor(Color.BLUE);
        canvas.drawRect(r, paint);
    }

//    /**
//     * 初始化，生成一条向右运动，于第五行第五列到第五行第七列的占同三格的蛇
//     */
//    public void init() {
//        moveDirection = Const.RIGHT;
//
//    }

//    public void resetSnake() {
//        initMap(mapArray);
//        snakeInstance.snakePoints.clear();
//        Const.INTERVAL = 900.0;
//        init();
//    }
}