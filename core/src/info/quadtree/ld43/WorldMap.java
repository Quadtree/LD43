package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.*;

public class WorldMap implements IndexedGraph<TilePos> {

    public static final int DENSITY_TILE_SIZE = 16;

    public enum TerrainType {
        Floor("floor1"),
        HorizontalWall("wall1"),
        VerticalWall("wall1"),
        CornerWall("wall1"),
        ClosedDoor("wall1"),
        OpenDoor("wall1");

        public final String graphic;

        TerrainType(String graphic) {
            this.graphic = graphic;
        }
    }

    final static int WORLD_HEIGHT = 128;
    final static int WORLD_WIDTH = 48;

    transient IndexedAStarPathFinder<TilePos> pathFinder;

    TerrainType[][] terrain;
    boolean[][] tileSeen;

    transient TilePos currentPathFindTarget = null;

    transient Map<TilePos, Map<TilePos, Boolean>> losCache;

    Map<TilePos, Integer> densityTiles = new HashMap<>();

    public List<TilePos> findPath(TilePos start, TilePos end){
        start = start.nor();
        end = end.nor();
        currentPathFindTarget = end;
        if (pathFinder == null) pathFinder = new IndexedAStarPathFinder<TilePos>(this);

        GraphPath<TilePos> outPath = new DefaultGraphPath<>();

        pathFinder.searchNodePath(start, end, TilePos::manhattanDistance, outPath);

        ArrayList<TilePos> ret = new ArrayList<>();
        outPath.forEach(ret::add);
        return ret;
    }

    public TilePos getOpenSpace(){
        while(true){
            TilePos ret = TilePos.create(
                    Util.randInt(WORLD_WIDTH),
                    Util.randInt(WORLD_HEIGHT)
            );

            if (isPassable(ret)) return ret;
        }
    }

    public TilePos getMinDensityOpenSpace(){
        TilePos minDensityTile = null;
        int bestDensity = 1000000;

        for (Map.Entry<TilePos, Integer> potentialDensityTile : densityTiles.entrySet()){
            if (potentialDensityTile.getValue() > 0 && potentialDensityTile.getValue() < bestDensity){
                minDensityTile = potentialDensityTile.getKey();
                bestDensity = potentialDensityTile.getValue();
            }
        }

        if (minDensityTile != null) {
            System.out.println("Best density tile is " + minDensityTile);
            // we assume we got one
            for (int i = 0; i < 10000; ++i) {
                TilePos ret = TilePos.create(
                        minDensityTile.x * DENSITY_TILE_SIZE + Util.randInt(DENSITY_TILE_SIZE),
                        minDensityTile.y * DENSITY_TILE_SIZE + Util.randInt(DENSITY_TILE_SIZE)
                );

                if (isPassable(ret)) return ret;
            }
        }

        System.err.println("Fallback!!!");
        return getOpenSpace();
    }

    public boolean isPassable(TilePos tp){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return false;
        return terrain[tp.x][tp.y] == TerrainType.Floor;
    }

    void setTile(TilePos tp, TerrainType tt){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return;

        if (!terrain[tp.x][tp.y].equals(tt)){
            if (tt == TerrainType.Floor){
                TilePos densityTile = TilePos.create(tp.x / DENSITY_TILE_SIZE, tp.y / DENSITY_TILE_SIZE);
                densityTiles.put(densityTile, densityTiles.getOrDefault(densityTile, 0) + 1);
            }

            terrain[tp.x][tp.y] = tt;
        }
    }

    public WorldMap(){
        terrain = new TerrainType[WORLD_WIDTH][];
        tileSeen = new boolean[WORLD_WIDTH][];
        for (int i=0;i<WORLD_WIDTH;++i){
            terrain[i] = new TerrainType[WORLD_HEIGHT];
            tileSeen[i] = new boolean[WORLD_HEIGHT];
            for (int j=0;j<WORLD_HEIGHT;++j){
                terrain[i][j] = TerrainType.CornerWall;
                tileSeen[i][j] = false;
            }
        }

        terrain[WORLD_WIDTH / 2][1] = TerrainType.Floor;

        for (int i=0;i<100;++i){
            TilePos nxt = getMinDensityOpenSpace();
            if (Util.randInt(4) == 0){
                TilePos farExt = TilePos.create(Util.randInt(15), Util.randInt(15));

                for (int x=nxt.x;x<nxt.x+farExt.x;++x){
                    for (int y=nxt.y;y<nxt.y+farExt.y;++y){
                        setTile(TilePos.create(x,y), TerrainType.Floor);
                    }
                }
            } else {
                TilePos delta = TilePos.create(Util.randInt(3) - 1, Util.randInt(3) - 1);
                if (delta.manhattanDistance() > 1){
                    if (Util.randInt(2) == 0)
                        delta = TilePos.create(delta.x, 0);
                    else
                        delta = TilePos.create(0, delta.y);
                }

                while(Util.randInt(20) != 0){
                    nxt = nxt.add(delta);
                    setTile(nxt, TerrainType.Floor);
                }
            }
        }

        Pixmap debugPixmap = new Pixmap(WORLD_WIDTH, WORLD_HEIGHT, Pixmap.Format.RGBA8888);
        for (int i=0;i<WORLD_WIDTH;++i) {
            for (int j = 0; j < WORLD_HEIGHT; ++j) {
                if (isPassable(TilePos.create(i,j)))
                    debugPixmap.drawPixel(i,WORLD_HEIGHT - j, Color.rgba8888(Color.WHITE));
                else
                    debugPixmap.drawPixel(i,WORLD_HEIGHT - j, Color.rgba8888(Color.BLACK));
            }
        }
        PixmapIO.writePNG(Gdx.files.absolute("C:/tmp/debug_pixmap.png"), debugPixmap);
    }

    public void render(){
        for (int i=0;i<WORLD_WIDTH;++i){
            for (int j=0;j<WORLD_HEIGHT;++j){

                boolean withinLOS = canSee(LD43.s.gameState.pc.pos, TilePos.create(i,j), 1.1f);
                if (withinLOS) tileSeen[i][j] = true;

                if (tileSeen[i][j] || withinLOS){
                    LD43.s.cam.drawOnTile(terrain[i][j].graphic, TilePos.create(i,j), withinLOS ? Color.WHITE : Color.GRAY);
                }
            }
        }
    }

    @Override
    public int getIndex(TilePos node) {
        return node.x + node.y * WORLD_WIDTH;
    }

    @Override
    public int getNodeCount() {
        return WORLD_WIDTH * WORLD_HEIGHT;
    }

    @Override
    public Array<Connection<TilePos>> getConnections(TilePos fromNode) {
        Array<Connection<TilePos>> ret = new Array<>();

        for (int dx=-1;dx<2;++dx){
            for (int dy=-1;dy<2;++dy){
                TilePos np = fromNode.add(dx,dy);
                if (isPassable(np) && (np.equals(currentPathFindTarget) || !getCreatureOnTile(np).isPresent())) ret.add(new DefaultConnection<>(fromNode, np));
            }
        }

        return ret;
    }

    public boolean canSee(TilePos start, TilePos end, float within){
        /*if (losCache == null) losCache = new HashMap<>();
        if (!losCache.containsKey(start)) losCache.put(start, new HashMap<>());
        if (!losCache.get(start).containsKey(end)){
            losCache.get(start).put(end, canSeeImpl(start, end, within));
        }

        return losCache.get(start).get(end);*/

        // todo: Optimize me
        return canSeeImpl(start, end, within);
    }

    private boolean canSeeImpl(TilePos start, TilePos end, float within) {
        Vector2 pos = new Vector2(start.x, start.y);
        Vector2 endVec = new Vector2(end.x, end.y);
        Vector2 delta = endVec.cpy().sub(pos).nor();

        int steps = Math.round(pos.dst(endVec) - within);

        for (int i=0;i<steps;++i){
            pos.x += delta.x;
            pos.y += delta.y;

            if (!isPassable(TilePos.create(Math.round(pos.x), Math.round(pos.y)))) return false;
        }

        return true;
    }

    public Optional<Creature> getCreatureOnTile(TilePos tp){
        return LD43.s.gameState.creatures.stream().filter(it -> it.pos.equals(tp)).findAny();
    }
}
