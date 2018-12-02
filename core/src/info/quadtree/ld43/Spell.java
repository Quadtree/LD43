package info.quadtree.ld43;

public enum Spell {
    AstralBolt("Astral Bolt", 5, false, (caster, target) -> Spell.damagingSpellAttack(caster, target, 80))
    ;
    String name;
    int spCost;
    boolean selfCastOnly;
    SpellEffect effect;

    interface SpellEffect {
        void cast(Creature caster, TilePos target);
    }

    private static void damagingSpellAttack(Creature caster, TilePos target, int maxDamage){

    }

    Spell(String name, int spCost, boolean selfCastOnly, SpellEffect effect) {
        this.name = name;
        this.spCost = spCost;
        this.selfCastOnly = selfCastOnly;
        this.effect = effect;
    }
}
