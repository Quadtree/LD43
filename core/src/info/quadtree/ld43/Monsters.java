package info.quadtree.ld43;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Monsters {
    static List<Consumer<TilePos>> spawners = Arrays.asList(
            Monsters::spawnSlimeAt,
            Monsters::spawnImpAt,
            Monsters::spawnImpAt,
            Monsters::spawnGiantBeetleAt,
            Monsters::spawnGiantBeetleAt,
            Monsters::spawnLesserDemonAt,
            Monsters::spawnGrkAt,
            Monsters::spawnGrkAt,
            Monsters::spawnDarkSpiritAt,
            Monsters::spawnGreaterDemonAt
    );

    public static void spawnMonsertAt(TilePos pos){
        byte jaggedness = LD43.s.gameState.worldMap.jaggednessLevelGrid[pos.x][pos.y];

        int trgPos = MathUtils.clamp(jaggedness / 5 + MathUtils.random(-2, 2), 0, spawners.size() - 1);

        spawners.get(trgPos).accept(pos);
    }

    public static void spawnSlimeAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 10;
        ret.statSpeed = 5;
        ret.statEndurance = 10;
        ret.graphicName = "slime";
        ret.xp = 10;
        ret.name = "Slime";

        ret.corpseFood = 800;
        ret.corpseToxicity = 60;
        ret.corpseWeight = 20;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnImpAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 6;
        ret.statSpeed = 25;
        ret.statEndurance = 5;
        ret.graphicName = "imp";
        ret.xp = 15;
        ret.name = "Imp";

        ret.corpseFood = 300;
        ret.corpseToxicity = 10;
        ret.corpseWeight = 10;
        ret.naturalRangedAttack = true;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnLesserDemonAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 30;
        ret.statSpeed = 12;
        ret.statEndurance = 15;
        ret.graphicName = "lesser_demon";
        ret.xp = 30;
        ret.name = "Lesser Demon";

        ret.corpseFood = 600;
        ret.corpseToxicity = 15;
        ret.corpseWeight = 15;
        ret.naturalArmor = 3;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnGrkAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 20;
        ret.statSpeed = 10;
        ret.statEndurance = 20;
        ret.graphicName = "grk";
        ret.xp = 35;
        ret.name = "Grk";

        ret.corpseFood = 900;
        ret.corpseToxicity = 0;
        ret.corpseWeight = 22;
        ret.naturalArmor = 5;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnGiantBeetleAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 10;
        ret.statSpeed = 12;
        ret.statEndurance = 15;
        ret.graphicName = "giant_beetle";
        ret.xp = 25;
        ret.name = "Giant Beetle";

        ret.corpseFood = 600;
        ret.corpseToxicity = 20;
        ret.corpseWeight = 15;
        ret.naturalArmor = 5;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnDarkSpiritAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 40;
        ret.statSpeed = 30;
        ret.statEndurance = 10;
        ret.graphicName = "hero";
        ret.xp = 100;
        ret.name = "Former Sacrifice";

        ret.naturalRangedAttack = true;
        ret.tint = Color.GRAY;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnGreaterDemonAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 60;
        ret.statSpeed = 30;
        ret.statEndurance = 30;
        ret.graphicName = "greater_demon";
        ret.xp = 150;
        ret.name = "Greater Demon";

        ret.corpseFood = 900;
        ret.corpseToxicity = 0;
        ret.corpseWeight = 22;
        ret.naturalArmor = 5;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }

    public static void spawnEndBossAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 100;
        ret.statSpeed = 20;
        ret.statEndurance = 100;
        ret.graphicName = "demigod";
        ret.xp = 0;
        ret.name = LD43.EVIL_GOD_NAME;

        ret.corpseFood = 2000;
        ret.corpseToxicity = 1000;
        ret.corpseWeight = 80;
        ret.naturalArmor = 3;
        ret.endBoss = true;
        ret.tint = Color.PURPLE;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }
}
