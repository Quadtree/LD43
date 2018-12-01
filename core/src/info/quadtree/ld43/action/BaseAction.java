package info.quadtree.ld43.action;

import info.quadtree.ld43.Creature;

public class BaseAction {
    Creature actor;

    public BaseAction(Creature actor) {
        this.actor = actor;
    }

    public boolean tick(){ return true;}
}
