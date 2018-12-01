package info.quadtree.ld43.action;

import info.quadtree.ld43.Creature;
import info.quadtree.ld43.Item;

public class EatAction extends BaseAction {
    Item targetItem;

    int amtEaten = 0;

    public EatAction(Creature actor, Item targetItem) {
        super(actor);
        this.targetItem = targetItem;
    }

    @Override
    public boolean tick() {
        if (actor.canAct()){
            amtEaten++;
            actor.ticksTillNextAction = 3;

            if (amtEaten >= targetItem.weight * 4){
                actor.finishEating(targetItem);
                return false;
            }
        }

        return true;
    }
}
