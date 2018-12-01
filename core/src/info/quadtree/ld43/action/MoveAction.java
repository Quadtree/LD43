package info.quadtree.ld43.action;

import info.quadtree.ld43.Creature;
import info.quadtree.ld43.LD43;
import info.quadtree.ld43.TilePos;

import java.util.List;

public class MoveAction extends BaseAction {
    TilePos dest;

    public MoveAction(Creature actor, TilePos dest) {
        super(actor);
        this.dest = dest;
    }

    @Override
    public boolean tick() {
        super.tick();

        List<TilePos> moves = LD43.s.gameState.worldMap.findPath(actor.pos, dest);

        if (moves.size() > 1){
            actor.move(moves.get(1).x - actor.pos.x, moves.get(1).y - actor.pos.y);
            return true;
        } else {
            return false;
        }
    }
}
