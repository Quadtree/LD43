package info.quadtree.ld43;

public enum Spell {
    AstralBolt("Astral Bolt", 5, false, true, (spell, caster, target) -> Spell.damagingSpellAttack(caster, target, spell, 80)),
    Heal("Heal", 15, true, true, (spell, caster, target) -> {
        int healAmt = (int)(Util.randInt(200) * caster.getPowerMultiplier());
        caster.healedFor(healAmt);

        LD43.s.gameState.addCombatLogMessage(caster.pos, caster.name + " heals themselves for " + healAmt);
    })
    ;
    String name;
    int spCost;
    boolean selfCastOnly;
    boolean requiresTargetOnSpace;
    SpellEffect effect;

    interface SpellEffect {
        void cast(Spell spell, Creature caster, TilePos target);
    }

    private static void damagingSpellAttack(Creature caster, TilePos target, Spell spell, int maxDamage){
        int damage = Math.round(Util.randInt(maxDamage) * caster.getPowerMultiplier());

        LD43.s.gameState.worldMap.getCreatureOnTile(target).ifPresent(it -> {
            LD43.s.gameState.addCombatLogMessage(it.pos, caster.name + " casts " + spell.name + " on " + it.name + " dealing " + damage + " damage");
            it.takeDamage(damage);
        });
    }

    Spell(String name, int spCost, boolean selfCastOnly, boolean requiresTargetOnSpace, SpellEffect effect) {
        this.name = name;
        this.spCost = spCost;
        this.selfCastOnly = selfCastOnly;
        this.requiresTargetOnSpace = requiresTargetOnSpace;
        this.effect = effect;
    }
}
