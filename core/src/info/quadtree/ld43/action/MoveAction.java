package info.quadtree.ld43.action;

import info.quadtree.ld43.Creature;
import info.quadtree.ld43.LD43;
import info.quadtree.ld43.TilePos;

import java.util.List;
import java.util.Optional;

public class MoveAction extends BaseAction {
    TilePos dest;

    public MoveAction(Creature actor, TilePos dest) {
        super(actor);
        this.dest = dest;
    }

    @Override
    public boolean tick() {
        super.tick();

        if (actor.hasRangedAttack()){
            Optional<Creature> posTrg = LD43.s.gameState.worldMap.getCreatureOnTile(dest);
            if (posTrg.isPresent()){
                if (actor.hostileTowards(posTrg.get())){
                    actor.meleeAttack(posTrg.get());
                    return false;
                }
            }
        }

        List<TilePos> moves = LD43.s.gameState.worldMap.findPath(actor.pos, dest);

        if (moves.size() > 1){
            actor.move(moves.get(1).x - actor.pos.x, moves.get(1).y - actor.pos.y);
            return !actor.justMeleeAttackedDueToMove;
        } else {
            return false;
        }
    }
}
