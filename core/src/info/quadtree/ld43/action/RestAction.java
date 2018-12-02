package info.quadtree.ld43.action;

import info.quadtree.ld43.Creature;

public class RestAction extends BaseAction {
    public RestAction(Creature actor) {
        super(actor);
    }

    @Override
    public boolean tick() {
        if ((actor.getHp() >= actor.getEffectiveEndurance() && actor.getSp() >= actor.getEffectiveMagic()) || actor.getFood() < 500) return false;

        actor.stand();

        return true;
    }
}
