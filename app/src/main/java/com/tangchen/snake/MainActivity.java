package com.tangchen.snake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    SnakeView snakeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snakeView = (SnakeView) findViewById(R.id.snackView);

        Snake.getInstance().bindView(snakeView);
    }

    float startX, startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float X = event.getX();
        float Y = event.getY();
        float offsetX, offsetY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = X;
                startY = Y;
                break;
            case MotionEvent.ACTION_UP:
                offsetX = X - startX;
                offsetY = Y - startY;

                if (Math.abs(offsetX) - Math.abs(offsetY) > 5) { //在x轴移动量大于y轴，判断为左右移动
                    if (offsetX > 0) { //右移
                        snakeView.moveDirection = Const.RIGHT;
                    } else { //左移
                        snakeView.moveDirection = Const.LEFT;
                    }
                } else if (Math.abs(offsetX) - Math.abs(offsetY) < -5) { //上下移动
                    if (offsetY > 0) { //上移动
                        snakeView.moveDirection = Const.TOP;
                    } else {
                        snakeView.moveDirection = Const.BOTTOM;
                    }
                }

                break;
        }
        return true;
    }
}
