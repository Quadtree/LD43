package info.quadtree.ld43;

public enum Spell {
    AstralBolt("Astral Bolt", 5, false, (caster, target) -> Spell.damagingSpellAttack(caster, target, Spell.AstralBolt, 80))
    ;
    String name;
    int spCost;
    boolean selfCastOnly;
    SpellEffect effect;

    interface SpellEffect {
        void cast(Creature caster, TilePos target);
    }

    private static void damagingSpellAttack(Creature caster, TilePos target, Spell spell, int maxDamage){
        int damage = Math.round(Util.randInt(maxDamage) * caster.getPowerMultiplier());

        LD43.s.gameState.worldMap.getCreatureOnTile(target).ifPresent(it -> {
            LD43.s.gameState.addCombatLogMessage(it.pos, caster.name + " casts " + spell.name + " on " + it.name + " dealing " + damage + " damage");
            it.takeDamage(damage);
        });
    }

    Spell(String name, int spCost, boolean selfCastOnly, SpellEffect effect) {
        this.name = name;
        this.spCost = spCost;
        this.selfCastOnly = selfCastOnly;
        this.effect = effect;
    }
}
