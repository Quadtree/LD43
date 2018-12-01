package info.quadtree.ld43;

public class GameState {
    public WorldMap worldMap;

    public GameState() {
        this.worldMap = new WorldMap();
    }

    public void render(){
        worldMap.render();
    }
}
