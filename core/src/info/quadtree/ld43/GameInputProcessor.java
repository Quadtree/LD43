package info.quadtree.ld43;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GameInputProcessor implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.NUMPAD_1){ LD43.s.gameState.pc.move(-1, -1); return true; }
        if (keycode == Input.Keys.NUMPAD_2){ LD43.s.gameState.pc.move(0, -1); return true; }
        if (keycode == Input.Keys.NUMPAD_3){ LD43.s.gameState.pc.move(1, -1); return true; }
        if (keycode == Input.Keys.NUMPAD_4){ LD43.s.gameState.pc.move(-1, 0); return true; }
        //if (keycode == Input.Keys.NUMPAD_5){ LD43.s.gameState.pc.move(-1, 1); return true; }
        if (keycode == Input.Keys.NUMPAD_6){ LD43.s.gameState.pc.move(1, 0); return true; }
        if (keycode == Input.Keys.NUMPAD_7){ LD43.s.gameState.pc.move(-1, 1); return true; }
        if (keycode == Input.Keys.NUMPAD_8){ LD43.s.gameState.pc.move(0, 1); return true; }
        if (keycode == Input.Keys.NUMPAD_9){ LD43.s.gameState.pc.move(1, 1); return true; }

        return false;
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