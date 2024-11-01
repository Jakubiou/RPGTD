package TD;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameMap {
    public static final int TILE_SIZE = 64;
    private int[][] map;

    public GameMap(String fileName) throws IOException {
        loadMap(fileName);
    }

    private void loadMap(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int rows = 0;
        int cols = 0;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] values = line.split(" ");
            cols = values.length;
            rows++;
        }
        reader.close();

        map = new int[rows][cols];

        reader = new BufferedReader(new FileReader(fileName));
        int row = 0;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] values = line.split(" ");
            for (int col = 0; col < cols; col++) {
                map[row][col] = Integer.parseInt(values[col]);
            }
            row++;
        }
        reader.close();
    }

    public boolean isPlaceable(int row, int col) {
        return map[row][col] == 1;
    }

    public boolean isPath(int row, int col) {
        return map[row][col] == 0;
    }

    public int getRows() {
        return map.length;
    }

    public int getCols() {
        return map.length;
    }
    public int getMapValue(int row, int col) {
        return map[row][col];
    }
}