package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class ModalScreenCloser implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {
        closeModal();

        return false;
    }

    private void closeModal() {
        LD43.s.modalScreen.dispose();
        LD43.s.modalScreen = null;

        Gdx.input.setInputProcessor(LD43.s.mp);

        if (!LD43.s.bgm.isPlaying()){
            LD43.s.bgm.setVolume(LD43.MUSIC_VOLUME);
            LD43.s.bgm.play();
            LD43.s.loopMusic = true;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        closeModal();

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
