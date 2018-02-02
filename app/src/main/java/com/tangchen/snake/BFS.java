package com.tangchen.snake;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by TangChen on 18/1/31.
 */

public class BFS {
    private Snake snake;
    private List<PathPoint> path;
    private SnakePoint headPoint, foodPoint;

    private BFS() {

    }

    private static BFS instance;

    public static BFS getInstance() {
        if (instance == null)
            instance = new BFS();

        return instance;
    }

    public int getDirection() {
        PathPoint point = path.get(Const.FIRSTPOINT);
        SnakePoint head = snake.getHeadPoint();

        path.remove(Const.FIRSTPOINT);
        int offsetX = point.x - head.x;
        int offsetY = point.y - head.y;


        if (offsetX != 0) {
            if (offsetX > 0)
                return Const.RIGHT;
            else
                return Const.LEFT;
        } else {
            if (offsetY > 0)
                return Const.TOP;
            else
                return Const.BOTTOM;
        }
    }

    private boolean newPath(SnakePoint pathPoint, boolean isNewPath) {
        headPoint = snake.getHeadPoint();
        foodPoint = pathPoint;

        int[][] mapArray = snake.mapArray;
        List<PathPoint> bfsPoints = new ArrayList<>();
        Queue<PathPoint> queue = new LinkedList<>();
        boolean[][] isVisit = new boolean[Const.mapSize][Const.mapSize];
        PathPoint head = new PathPoint(headPoint.x, headPoint.y, null);
        isVisit[headPoint.x][headPoint.y] = true;
        queue.add(head);

        while (!queue.isEmpty()) {
            PathPoint curPoint = queue.poll();
            PathPoint nextPoint;

            if ((mapArray[curPoint.x - 1][curPoint.y] == Const.isEmpty || mapArray[curPoint.x - 1][curPoint.y] == Const.isFood)
                    && !isVisit[curPoint.x - 1][curPoint.y]) {
                nextPoint = new PathPoint(curPoint.x - 1, curPoint.y, curPoint);
                isVisit[curPoint.x - 1][curPoint.y] = true;
                bfsPoints.add(nextPoint);
                queue.add(nextPoint);
            }

            if ((mapArray[curPoint.x][curPoint.y - 1] == Const.isEmpty || mapArray[curPoint.x][curPoint.y - 1] == Const.isFood)
                    && !isVisit[curPoint.x][curPoint.y - 1]) {
                nextPoint = new PathPoint(curPoint.x, curPoint.y - 1, curPoint);
                isVisit[curPoint.x][curPoint.y - 1] = true;
                bfsPoints.add(nextPoint);
                queue.add(nextPoint);
            }

            if ((mapArray[curPoint.x + 1][curPoint.y] == Const.isEmpty || mapArray[curPoint.x + 1][curPoint.y] == Const.isFood)
                    && !isVisit[curPoint.x + 1][curPoint.y]) {
                nextPoint = new PathPoint(curPoint.x + 1, curPoint.y, curPoint);
                isVisit[curPoint.x + 1][curPoint.y] = true;
                bfsPoints.add(nextPoint);
                queue.add(nextPoint);
            }

            if ((mapArray[curPoint.x][curPoint.y + 1] == Const.isEmpty || mapArray[curPoint.x][curPoint.y + 1] == Const.isFood)
                    && !isVisit[curPoint.x][curPoint.y + 1]) {
                nextPoint = new PathPoint(curPoint.x, curPoint.y + 1, curPoint);
                isVisit[curPoint.x][curPoint.y + 1] = true;
                bfsPoints.add(nextPoint);
                queue.add(nextPoint);
            }
        }

        for (PathPoint point : bfsPoints) {
            if (foodPoint.equals(point)) {
                if(!isNewPath)
                    return true;
                path = new LinkedList<>();
                snakePath(bfsPoints, point);
                return true;
            }
        }

        return false;
    }

    public boolean makeNewPath() {
       return newPath(snake.food, true);
    }

    public boolean checkUseful(SnakePoint newFoodPoint) {
        return newPath(newFoodPoint, false);
    }

    private void snakePath(List<PathPoint> bfsPoints, PathPoint curPoint) {
        for (PathPoint point : bfsPoints) {
            if (point.equals(curPoint)) {
                bfsPoints.remove(curPoint);
                snakePath(bfsPoints, curPoint.pre);
                path.add(curPoint);
                break;
            }
        }
    }

    public void bind(Snake controller) {
        this.snake = controller;
    }

    public class PathPoint extends SnakePoint {
        PathPoint pre;

        public PathPoint(int x, int y, PathPoint pre) {
            super(x, y);
            this.pre = pre;
        }

        @Override
        public boolean equals(Object obj) {
            PathPoint compare = (PathPoint) obj;
            return this.x == compare.x && this.y == compare.y;
        }
    }
}