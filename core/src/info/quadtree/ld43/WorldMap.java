package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.*;
import java.util.stream.Collectors;

public class WorldMap implements IndexedGraph<TilePos> {

    public static final int DENSITY_TILE_SIZE = 24;

    public enum TerrainType {
        Floor("floor1"),
        HorizontalWall("wall1"),
        VerticalWall("wall1"),
        CornerWall("wall1"),
        ClosedDoor("door_closed"),
        OpenDoor("door_open");

        public final String graphic;

        TerrainType(String graphic) {
            this.graphic = graphic;
        }
    }

    final static int WORLD_HEIGHT = 384;
    final static int WORLD_WIDTH = 48;

    transient IndexedAStarPathFinder<TilePos> pathFinder;

    TerrainType[][] terrain;
    boolean[][] tileSeen;
    byte[][] jaggednessLevelGrid;

    transient TilePos currentPathFindTarget = null;

    transient Map<TilePos, Map<TilePos, Boolean>> losCache;

    Map<TilePos, Integer> densityTiles;
    ArrayList<TilePos> corridorEndPoints;
    ArrayList<Rectangle> previousRooms;

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
            //System.out.println("Best density tile is " + minDensityTile + " " + bestDensity);
            // we assume we got one
            for (int i = 0; i < 10000; ++i) {
                TilePos ret = TilePos.create(
                        minDensityTile.x * DENSITY_TILE_SIZE + Util.randInt(DENSITY_TILE_SIZE),
                        minDensityTile.y * DENSITY_TILE_SIZE + Util.randInt(DENSITY_TILE_SIZE)
                );

                if (isPassable(ret)) return ret;
            }
        }

        //System.err.println("Fallback!!!");
        return getOpenSpace();
    }

    public boolean isPassable(TilePos tp){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return false;
        return terrain[tp.x][tp.y] == TerrainType.Floor || terrain[tp.x][tp.y] == TerrainType.OpenDoor || terrain[tp.x][tp.y] == TerrainType.ClosedDoor;
    }

    public boolean isOpenable(TilePos tp){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return false;
        return terrain[tp.x][tp.y] == TerrainType.ClosedDoor;
    }

    public boolean isLOSPassable(TilePos tp){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return false;
        return terrain[tp.x][tp.y] == TerrainType.Floor || terrain[tp.x][tp.y] == TerrainType.OpenDoor;
    }

    void setTile(TilePos tp, TerrainType tt, Float jaggednessLevel){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return;

        if (tt == TerrainType.Floor && (tp.x >= WORLD_WIDTH - 1 || tp.x < 1 || tp.y >= WORLD_HEIGHT - 1 || tp.y < 1)) return;

        if (!terrain[tp.x][tp.y].equals(tt)){
            if (tt == TerrainType.Floor){
                TilePos densityTile = TilePos.create(tp.x / DENSITY_TILE_SIZE, tp.y / DENSITY_TILE_SIZE);
                densityTiles.put(densityTile, densityTiles.getOrDefault(densityTile, 0) + 1);
            }

            terrain[tp.x][tp.y] = tt;
            if (jaggednessLevel != null) jaggednessLevelGrid[tp.x][tp.y] = (byte)(jaggednessLevel * 100);
        }
    }

    public WorldMap(){
        for(int i=0;i<10000;++i) {
            generateMap();

            int minTile = 100000;
            int maxTile = 0;

            for (Map.Entry<TilePos, Integer> ent : densityTiles.entrySet()){
                minTile = Math.min(ent.getValue(), minTile);
                maxTile = Math.max(ent.getValue(), maxTile);
            }

            System.out.println("maxTile=" + maxTile + " minTile=" + minTile);

            if (true || maxTile < 200){
                drawDebugPixmap(0);
                return;
            }
        }

        System.err.println("Couldn't generate world we liked");
    }

    class TilePosPair
    {
        public TilePosPair(TilePos a, TilePos b) {
            this.a = a;
            this.b = b;
        }

        TilePos a;
        TilePos b;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TilePosPair that = (TilePosPair) o;
            return Objects.equals(a, that.a) &&
                    Objects.equals(b, that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    private ArrayList<TilePosPair> connectedRooms;

    private boolean areRoomsConnected(TilePos a, TilePos b){
        return connectedRooms.contains(new TilePosPair(a,b)) || connectedRooms.contains(new TilePosPair(b,a));
    }

    int conRadius;

    public TilePos startSpot;
    public TilePos endSpot;

    private void generateMap() {
        startSpot = null;
        endSpot = null;
        connectedRooms = new ArrayList<>();
        previousRooms = new ArrayList<>();
        densityTiles = new HashMap<>();
        corridorEndPoints = new ArrayList<>();
        terrain = new TerrainType[WORLD_WIDTH][];
        tileSeen = new boolean[WORLD_WIDTH][];
        jaggednessLevelGrid = new byte[WORLD_WIDTH][];
        for (int i=0;i<WORLD_WIDTH;++i){
            terrain[i] = new TerrainType[WORLD_HEIGHT];
            tileSeen[i] = new boolean[WORLD_HEIGHT];
            jaggednessLevelGrid[i] = new byte[WORLD_HEIGHT];
            for (int j=0;j<WORLD_HEIGHT;++j){
                terrain[i][j] = TerrainType.CornerWall;
                tileSeen[i][j] = false;
                jaggednessLevelGrid[i][j] = 0;
            }
        }

        ArrayList<TilePos> roomCenters = new ArrayList<>();

        for (int i=0;i<40;++i){
            TilePos roomTopLeft = TilePos.create(Util.randInt(WORLD_WIDTH), Util.randInt(WORLD_HEIGHT));
            TilePos roomSize = TilePos.create(Util.randInt(15)+3, Util.randInt(15)+3);

            TilePos roomCenter = roomTopLeft.add(roomSize.x / 2, roomSize.y / 2);

            float jaggedness = computeJagedness(roomTopLeft);

            float[] maxDists = new float[16];
            maxDists[0] = roomSize.x / 2f * Util.randGaussian(2f - jaggedness * 4, jaggedness);
            for (int j=1;j<16;++j){
                maxDists[j] = maxDists[j - 1] * Util.randGaussian(1f, jaggedness);
            }
            float aspectRatio = (float)roomSize.y / roomSize.x;

            for (int x=roomTopLeft.x;x<roomTopLeft.x+roomSize.x;++x){
                for (int y=roomTopLeft.y;y<roomTopLeft.y+roomSize.y;++y){
                    float xDist = x - roomCenter.x;
                    float yDist = (y - roomCenter.y) * aspectRatio;

                    float effDistance = (float)Math.sqrt(xDist * xDist + yDist * yDist);

                    float ang = MathUtils.atan2(yDist, xDist);
                    if (ang < 0) ang += MathUtils.PI;
                    float maxDist = maxDists[MathUtils.clamp((int)(ang / MathUtils.PI2 * maxDists.length), 0, 15)];

                    if (effDistance < maxDist) setTile(TilePos.create(x,y), TerrainType.Floor, jaggedness);
                }
            }

            roomCenters.add(roomCenter);
        }

        startSpot = roomCenters.stream().filter(this::isPassable).min(Comparator.comparingInt(it -> it.y)).get();
        endSpot = roomCenters.stream().filter(this::isPassable).max(Comparator.comparingInt(it -> it.y)).get();

        int n = 0;
        conRadius = 40;

        while(findPath(startSpot, endSpot).size() == 0){
            TilePos roomToAddConn = roomCenters.get(Util.randInt(roomCenters.size()));

            List<TilePos> trgRooms = roomCenters.stream()
                    .filter(it -> !areRoomsConnected(roomToAddConn, it))
                    .filter(it -> it.manhattanDistance(roomToAddConn) < conRadius)
                    .collect(Collectors.toList());

            if (trgRooms.size() == 0) continue;

            TilePos trgRoom = trgRooms.get(Util.randInt(trgRooms.size()));

            System.out.println("Connecting " + roomToAddConn + " --> " + trgRoom + " || " + connectedRooms.size());

            boolean xMode = MathUtils.randomBoolean();
            boolean yMode = MathUtils.randomBoolean();

            float jaggedness = computeJagedness(trgRoom);

            TilePos cp = roomToAddConn;
            while(!cp.equals(trgRoom)){
                if (!MathUtils.randomBoolean(jaggedness)) {
                    if (xMode) {
                        if (trgRoom.x < cp.x) cp = TilePos.create(cp.x - 1, cp.y);
                        if (trgRoom.x > cp.x) cp = TilePos.create(cp.x + 1, cp.y);
                        setTile(cp, TerrainType.Floor, jaggedness);
                    }
                    if (yMode) {
                        if (trgRoom.y < cp.y) cp = TilePos.create(cp.x, cp.y - 1);
                        if (trgRoom.y > cp.y) cp = TilePos.create(cp.x, cp.y + 1);
                        setTile(cp, TerrainType.Floor, jaggedness);
                    }
                } else {
                    cp = TilePos.create(cp.x + MathUtils.random(-1, 1), cp.y);
                    setTile(cp, TerrainType.Floor, jaggedness);
                    cp = TilePos.create(cp.x, cp.y + MathUtils.random(-1, 1));
                    setTile(cp, TerrainType.Floor, jaggedness);
                }

                if (Util.randInt(10) == 0) xMode = MathUtils.randomBoolean();
                if (Util.randInt(10) == 0) yMode = MathUtils.randomBoolean();

                //System.out.println(cp + " --> " + trgRoom.get());


            }
            connectedRooms.add(new TilePosPair(roomToAddConn, trgRoom));
            //drawDebugPixmap(n++);
            conRadius++;
        }

        for (int x=1;x<WORLD_WIDTH - 1;++x){
            for (int y=1;y<WORLD_HEIGHT - 1;++y){

                TilePos ctp = TilePos.create(x,y);

                if (isPassable(ctp) && !MathUtils.randomBoolean(jaggednessLevelGrid[x][y] / 50f)){
                    if (isPassable(ctp.add(-1, 1)) &&
                        isPassable(ctp.add(0, 1)) &&
                        isPassable(ctp.add(1, 1)) &&
                        !isPassable(ctp.add(-1, 0)) &&
                        !isPassable(ctp.add(1, 0))){
                        setTile(ctp, TerrainType.ClosedDoor, null);
                    }

                    if (isPassable(ctp.add(-1, -1)) &&
                        isPassable(ctp.add(0, -1)) &&
                        isPassable(ctp.add(1, -1)) &&
                        !isPassable(ctp.add(-1, 0)) &&
                        !isPassable(ctp.add(1, 0))){
                        setTile(ctp, TerrainType.ClosedDoor, null);
                    }

                    if (isPassable(ctp.add(-1, 1)) &&
                        isPassable(ctp.add(-1, 0)) &&
                        isPassable(ctp.add(-1, -1)) &&
                        !isPassable(ctp.add(0, -1)) &&
                        !isPassable(ctp.add(0, 1))){
                        setTile(ctp, TerrainType.ClosedDoor, null);
                    }

                    if (isPassable(ctp.add(1, 1)) &&
                        isPassable(ctp.add(1, 0)) &&
                        isPassable(ctp.add(1, -1)) &&
                        !isPassable(ctp.add(0, -1)) &&
                        !isPassable(ctp.add(0, 1))){
                        setTile(ctp, TerrainType.ClosedDoor, null);
                    }
                }
            }
        }
    }

    private float computeJagedness(TilePos trgRoom) {
        float jaggedness = (MathUtils.random() * 0.5f + 0.5f) * 0.5f * ((trgRoom.y / (float)WORLD_HEIGHT) + 0.15f);
        System.err.println(jaggedness);
        //jaggedness +=  * 0.45f;
        jaggedness = Math.min(jaggedness, 0.8f);
        return jaggedness;
    }

    public void drawDebugPixmap(int n) {
        Pixmap debugPixmap = new Pixmap(WORLD_WIDTH, WORLD_HEIGHT, Pixmap.Format.RGBA8888);
        for (int i=0;i<WORLD_WIDTH;++i) {
            for (int j = 0; j < WORLD_HEIGHT; ++j) {
                if (isPassable(TilePos.create(i,j)))
                    debugPixmap.drawPixel(i,WORLD_HEIGHT - j, Color.rgba8888(getTileColor(TilePos.create(i,j))));
                else
                    debugPixmap.drawPixel(i,WORLD_HEIGHT - j, Color.rgba8888(Color.BLACK));
            }
        }
        PixmapIO.writePNG(Gdx.files.absolute("C:/tmp/debug_pixmap_"+n+".png"), debugPixmap);
    }

    public TilePos generateCorridorDelta() {
        TilePos delta = TilePos.create(Util.randInt(3) - 1, Util.randInt(3) - 1);
        if (delta.manhattanDistance() > 1){
            if (Util.randInt(2) == 0)
                delta = TilePos.create(delta.x, 0);
            else
                delta = TilePos.create(0, delta.y);
        }
        return delta;
    }

    public void render(){
        for (int i=0;i<WORLD_WIDTH;++i){
            for (int j=0;j<WORLD_HEIGHT;++j){

                boolean withinLOS = canSee(LD43.s.gameState.pc.pos, TilePos.create(i,j), 1.1f);
                if (withinLOS) tileSeen[i][j] = true;

                if (tileSeen[i][j] || withinLOS){
                    Color c = getTileColor(TilePos.create(i,j));

                    if (!withinLOS){
                        c = new Color(
                                c.r * 0.5f,
                                c.g * 0.5f,
                                c.b * 0.5f,
                                1f
                        );
                    }

                    LD43.s.cam.drawOnTile(terrain[i][j].graphic, TilePos.create(i,j), c);
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

            if (!isLOSPassable(TilePos.create(Math.round(pos.x), Math.round(pos.y)))) return false;
        }

        return true;
    }

    public Optional<Creature> getCreatureOnTile(TilePos tp){
        return LD43.s.gameState.creatures.stream().filter(it -> it.pos.equals(tp)).findAny();
    }

    final Color c1 = new Color(1,1,1,1);
    final Color c2 = new Color(0xe1955bff);
    final Color c3 = new Color(0xac7ac4ff);

    final float c1bp = 5f;
    final float c2bp = 20f;
    final float c3bp = 35f;

    public Color getTileColor(TilePos tp){
        int x = MathUtils.clamp(tp.x, 0, WORLD_WIDTH - 1);
        int y = MathUtils.clamp(tp.y, 0, WORLD_HEIGHT - 1);

        byte j = jaggednessLevelGrid[x][y];

        if (j < c1bp){
            return c1;
        } else if (j <= c2bp) {
            float a = (j - c1bp) / (c3bp - c2bp);
            return new Color(
                    MathUtils.lerp(c1.r, c2.r, a),
                    MathUtils.lerp(c1.g, c2.g, a),
                    MathUtils.lerp(c1.b, c2.b, a),
                    1f
            );
        } else {
            float a = (j - c2bp) / 15f;
            return new Color(
                    MathUtils.lerp(c2.r, c3.r, a),
                    MathUtils.lerp(c2.g, c3.g, a),
                    MathUtils.lerp(c2.b, c3.b, a),
                    1f
            );
        }
    }
}
