package info.quadtree.ld43;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Util {
    static Random rand = new Random();

    public static int randInt(int maxEx){
        return rand.nextInt(maxEx);
    }

    public static <T> void indexIterate(List<T> list, Consumer<T> consumer){
        for (int i=0;i<list.size();++i){
            consumer.accept(list.get(i));
        }
    }
}
