package info.quadtree.ld43;

import java.util.Random;

public class Util {
    static Random rand = new Random();

    public static int randInt(int maxEx){
        return rand.nextInt(maxEx);
    }
}
