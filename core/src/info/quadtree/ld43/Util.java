package info.quadtree.ld43;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Util {
    static Random rand = new Random();

    public static int randInt(int maxEx){
        return rand.nextInt(maxEx);
    }

    public static <T> void indexIterate(List<T> list, Consumer<T> consumer){
        for (int i=0;i<list.size();++i){
            int startSize = list.size();
            consumer.accept(list.get(i));
            if (list.size() < startSize){
                i -= startSize - list.size();
            }
        }
    }

    public static int minAll(int... ints){
        return Arrays.stream(ints).min().getAsInt();
    }

    public static float randGaussian(float center, float stddev){
        return (float)rand.nextGaussian() * stddev + center;
    }



    public static Label createDynamicLabel(Supplier<String> txt){
        Label ret = new Label("???", LD43.s.defaultLabelStyle){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setText(txt.get());

                super.draw(batch, parentAlpha);
            }
        };
        return ret;
    }
}
