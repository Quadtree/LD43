package info.quadtree.ld43;

import com.badlogic.gdx.physics.box2d.World;

public class WorldMap {
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

    TerrainType[][] terrain;

    public TilePos getOpenSpace(){
        while(true){
            TilePos ret = TilePos.create(
                    Util.randInt(WORLD_WIDTH),
                    Util.randInt(WORLD_HEIGHT)
            );

            if (isPassable(ret)) return ret;
        }
    }

    public boolean isPassable(TilePos tp){
        return terrain[tp.x][tp.y] == TerrainType.Floor;
    }

    void setTile(TilePos tp, TerrainType tt){
        if (tp.x >= WORLD_WIDTH || tp.x < 0 || tp.y >= WORLD_HEIGHT || tp.y < 0) return;

        terrain[tp.x][tp.y] = tt;
    }

    public WorldMap(){
        terrain = new TerrainType[WORLD_WIDTH][];
        for (int i=0;i<WORLD_WIDTH;++i){
            terrain[i] = new TerrainType[WORLD_HEIGHT];
            for (int j=0;j<WORLD_HEIGHT;++j){
                terrain[i][j] = TerrainType.CornerWall;
            }
        }

        terrain[WORLD_WIDTH / 2][1] = TerrainType.Floor;

        for (int i=0;i<50;++i){
            TilePos nxt = getOpenSpace();
            if (Util.randInt(2) == 0){
                TilePos farExt = TilePos.create(Util.randInt(15), Util.randInt(15));

                for (int x=nxt.x;x<nxt.x+farExt.x;++x){
                    for (int y=nxt.y;y<nxt.y+farExt.y;++y){
                        setTile(TilePos.create(x,y), TerrainType.Floor);
                    }
                }
            }
        }
    }

    public void render(){
        for (int i=0;i<WORLD_WIDTH;++i){
            for (int j=0;j<WORLD_HEIGHT;++j){
                LD43.s.cam.drawOnTile(terrain[i][j].graphic, TilePos.create(i,j));
            }
        }
    }
}
