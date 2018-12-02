package info.quadtree.ld43.vfx;

public class BaseVisualEffect {
    public Runnable onComplete;

    public BaseVisualEffect(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    public void render(){

    }

    public boolean keep(){
        return true;
    }
}
