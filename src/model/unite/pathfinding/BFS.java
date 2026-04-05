package model.unite.pathfinding;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.LinkedList;

import model.Terrain;
import model.Case;
import model.Route;

public class BFS {
    
public static List<int[]> trouver(Terrain terrain, 
                                    int sx, int sy,
                                    int tx, int ty) {
    if (sx == tx && sy == ty) return List.of();

    int n = terrain.getTaille();
    boolean[][] visited = new boolean[n][n];
    int[][][] parent  = new int[n][n][2];
    for (var row : parent)
      for (var c : row) { c[0] = -1; c[1] = -1; }

    // FIFO queue — garantie de trouver le chemin le plus court en nombre de cases
    Queue<int[]> queue = new ArrayDeque<>();
    queue.add(new int[]{sx, sy});
    visited[sx][sy] = true;

    // 4 directions
    int[][] dirs = {{0,-1},{0,1},{1,0},{-1,0}};

    while (!queue.isEmpty()) {
      int[] cur = queue.poll(); // dequeue front
      int cx = cur[0], cy = cur[1];

      if (cx == tx && cy == ty)
        return reconstruct(parent, sx, sy, tx, ty);

      for (int[] d : dirs) {
        int nx = cx+d[0], ny = cy+d[1];
        if (nx<0||ny<0||nx>=n||ny>=n) continue;
        if (visited[nx][ny]) continue;

        // Passable = empty OR target cell OR Route
        Case c = terrain.getCase(nx, ny);
        boolean passable =
          c.estVide() || (c.aBatiment() && c.getBatiment() instanceof Route) || (nx==tx && ny==ty);
        if (!passable) continue;

        visited[nx][ny] = true;
        parent[nx][ny] = new int[]{cx, cy};
        queue.add(new int[]{nx, ny}); // enqueue back
      }
    }
    return List.of(); // no path
    }

    private static List<int[]> reconstruct(
      int[][][] parent,
      int sx, int sy, int tx, int ty) {

    LinkedList<int[]> path = new LinkedList<>();
    int cx = tx, cy = ty;
    while (cx != sx || cy != sy) {
      path.addFirst(new int[]{cx, cy});
      int[] p = parent[cx][cy];
      cx = p[0]; cy = p[1];
    }
    return path; // excludes start cell
    }
}
