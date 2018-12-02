package info.quadtree.ld43;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum Spell {
    AstralBolt("Astral Bolt", Color.BLUE, 5, false, true, (spell, power, caster, target) -> Spell.damagingSpellAttack(caster, power, target, spell, 80)),
    Heal("Healing", Color.GREEN, 15, true, true, (Spell spell, float power, Creature caster, TilePos target) -> {
        int healAmt = (int)(Util.randInt(200) * power);
        caster.healedFor(healAmt);

        LD43.s.gameState.addCombatLogMessage(caster.pos, caster.name + " heals themselves for " + healAmt);
    }),
    Fireball("Fireball", Color.ORANGE, 20, false, false, (spell,power, caster, target) -> {
        if (target == null) throw new RuntimeException("Target can't be null");
        LD43.s.gameState.addCombatLogMessage(caster.pos, caster.name + " casts fireball");

        List<Creature> hit = LD43.s.gameState.creatures.stream()
                .filter(it -> it.pos.dst2(target) <= 2)
                .filter(it -> LD43.s.gameState.worldMap.canSee(target, it.pos, 0))
                .collect(Collectors.toList());

        hit.forEach(it -> {
            int damage = Math.round(Util.randInt(150) * power);
            LD43.s.gameState.addCombatLogMessage(it.pos, it.name + " takes " + damage + " damage");
            it.takeDamage(damage);
        });
    }),
    Invisibility("Invisibility", Color.SKY, 25, true, false, (spell,power, caster, target) -> {
        caster.invisibleTime = 300;
        LD43.s.gameState.addCombatLogMessage(caster.pos, caster.name + " becomes invisible");
    }),
    Sleep("Sleep", Color.WHITE, 12, false, true, (spell, power, caster, target) -> {
        LD43.s.gameState.worldMap.getCreatureOnTile(target).ifPresent(it -> {
            if (!it.isImmuneToSleep){
                LD43.s.gameState.addCombatLogMessage(it.pos, caster.name + " casts " + spell.name + " on " + it.name);
                it.sleepTime = 500;
            } else {
                LD43.s.gameState.addCombatLogMessage(it.pos, caster.name + " casts " + spell.name + " on " + it.name + " but it is immune!");
            }
        });
    }),
    Haste("Haste", Color.YELLOW, 14, true, false, ((spell, power, caster, target) -> {
        caster.hasteTime = 700;
        LD43.s.gameState.addCombatLogMessage(caster.pos, caster.name + " is moving faster");
    })),
    Slow("Slow", Color.MAROON, 14, false, true, ((spell, power, caster, target) -> {
        LD43.s.gameState.worldMap.getCreatureOnTile(target).ifPresent(it -> {
            LD43.s.gameState.addCombatLogMessage(it.pos, caster.name + " casts " + spell.name + " on " + it.name);
            it.slowTime = 700;
        });
    }))
    ;
    String name;
    int spCost;
    boolean selfCastOnly;
    boolean requiresTargetOnSpace;
    SpellEffect effect;
    Color color;

    interface SpellEffect {
        void cast(Spell spell, float power, Creature caster, TilePos target);
    }

    private static void damagingSpellAttack(Creature caster, float power, TilePos target, Spell spell, int maxDamage){
        int damage = Math.round(Util.randInt(maxDamage) * power);

        LD43.s.gameState.worldMap.getCreatureOnTile(target).ifPresent(it -> {
            LD43.s.gameState.addCombatLogMessage(it.pos, caster.name + " casts " + spell.name + " on " + it.name + " dealing " + damage + " damage");
            it.takeDamage(damage);
        });
    }

    Spell(String name, Color color, int spCost, boolean selfCastOnly, boolean requiresTargetOnSpace, SpellEffect effect) {
        this.name = name;
        this.spCost = spCost;
        this.selfCastOnly = selfCastOnly;
        this.requiresTargetOnSpace = requiresTargetOnSpace;
        this.effect = effect;
        this.color = color;
    }
}
