package info.quadtree.ld43;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TilePos {
    public final int x;
    public final int y;

    private static Map<Integer, Map<Integer, TilePos>> tileCache;

    public static TilePos create(int x, int y){
        if (tileCache == null) tileCache = new HashMap<>();

        if (!tileCache.containsKey(x)) tileCache.put(x, new HashMap<>());

        if (!tileCache.get(x).containsKey(y)) tileCache.get(x).put(y, new TilePos(x,y));

        return tileCache.get(x).get(y);
    }

    private TilePos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int manhattanDistance(TilePos other){
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    public TilePos add(int x, int y){
        return create(this.x + x, this.y + y);
    }

    @Override
    public String toString() {
        return "TilePos{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TilePos tilePos = (TilePos) o;
        return x == tilePos.x &&
                y == tilePos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
