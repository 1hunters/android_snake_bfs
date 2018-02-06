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
    private int[][] map;
    private List<PathPoint> bfsPath;
    private Queue<PathPoint> queue;
    private boolean[][] isVisit;

    private BFS() {
        bfsPath = new ArrayList<>();
        queue = new LinkedList<>();
    }

    private static BFS instance;

    public static BFS getInstance() {
        if (instance == null)
            instance = new BFS();

        return instance;
    }

    public int nextDir() {
        PathPoint point = path.get(Const.FIRST_POINT);
        SnakePoint head = snake.getHeadPoint();

        path.remove(Const.FIRST_POINT);
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

    private boolean search(SnakePoint pathPoint, boolean isNewPath) {
        headPoint = snake.getHeadPoint();
        foodPoint = pathPoint;

        map = snake.mapArray;
        bfsPath.clear();
        queue.clear();
        isVisit = new boolean[Const.mapSize][Const.mapSize];
        PathPoint head = new PathPoint(headPoint.x, headPoint.y, null);
        isVisit[headPoint.x][headPoint.y] = true;
        queue.add(head);

        while (!queue.isEmpty()) {
            PathPoint curPoint = queue.poll();

            if(checkPoint(curPoint.x - 1, curPoint.y))
                add(curPoint.x - 1, curPoint.y, curPoint);

            if(checkPoint(curPoint.x, curPoint.y - 1))
                add(curPoint.x, curPoint.y - 1, curPoint);

            if(checkPoint(curPoint.x + 1, curPoint.y))
                add(curPoint.x + 1, curPoint.y, curPoint);

            if(checkPoint(curPoint.x, curPoint.y + 1))
                add(curPoint.x, curPoint.y + 1, curPoint);
        }

        for (PathPoint point : bfsPath) {
            if (foodPoint.equals(point)) {
                if(!isNewPath)
                    return true;
                path = new LinkedList<>();
                snakePath(bfsPath, point);
                return true;
            }
        }

        return false;
    }

    private boolean checkPoint(int x, int y) {
        return (map[x][y] == Const.isEmpty || map[x][y] == Const.isFood) && !isVisit[x][y];
    }

    private void add(int x, int y, PathPoint curPoint){
        PathPoint nextPoint = new PathPoint(x, y, curPoint);
        isVisit[x][y] = true;
        bfsPath.add(nextPoint);
        queue.add(nextPoint);
    }

    public boolean path() {
       return search(snake.food, true);
    }

    public boolean reachable(SnakePoint newFoodPoint) {
        return search(newFoodPoint, false);
    }

    private void snakePath(List<PathPoint> bfsPath, PathPoint curPoint) {
        for (PathPoint point : bfsPath) {
            if (point.equals(curPoint)) {
                bfsPath.remove(curPoint);
                snakePath(bfsPath, curPoint.pre);
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
    }
}