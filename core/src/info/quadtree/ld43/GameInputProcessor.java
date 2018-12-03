package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import info.quadtree.ld43.action.MoveAction;

public class GameInputProcessor implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.NUMPAD_1){ LD43.s.gameState.pc.move(-1, -1); return true; }
        if (keycode == Input.Keys.NUMPAD_2){ LD43.s.gameState.pc.move(0, -1); return true; }
        if (keycode == Input.Keys.NUMPAD_3){ LD43.s.gameState.pc.move(1, -1); return true; }
        if (keycode == Input.Keys.NUMPAD_4){ LD43.s.gameState.pc.move(-1, 0); return true; }
        if (keycode == Input.Keys.NUMPAD_5){ LD43.s.gameState.pc.stand(); return true; }
        if (keycode == Input.Keys.NUMPAD_6){ LD43.s.gameState.pc.move(1, 0); return true; }
        if (keycode == Input.Keys.NUMPAD_7){ LD43.s.gameState.pc.move(-1, 1); return true; }
        if (keycode == Input.Keys.NUMPAD_8){ LD43.s.gameState.pc.move(0, 1); return true; }
        if (keycode == Input.Keys.NUMPAD_9){ LD43.s.gameState.pc.move(1, 1); return true; }

        if (keycode == Input.Keys.ESCAPE){
            LD43.s.gameState.selectedSpell = null;
        }

        if (keycode == Input.Keys.R){
            LD43.s.resetGameState();
        }

        if (LD43.CHEATS){
            if (keycode == Input.Keys.F) LD43.s.gameState.pc.food += 1000;
            if (keycode == Input.Keys.X) LD43.s.gameState.pc.gainXP(400);
            if (keycode == Input.Keys.B) LD43.s.gameState.pc.pos = LD43.s.gameState.worldMap.shiftToClear(LD43.s.gameState.worldMap.endSpot);
            if (keycode == Input.Keys.S){
                LD43.s.gameState.pc.statMagic += 50;
                for (Spell s : Spell.values()){
                    LD43.s.gameState.pc.inventory.add(Items.createSpellBook(s));
                }
            }
            if (keycode == Input.Keys.T){
                Util.takeScreenshot.run();
            }
        }

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
        TilePos rp = LD43.s.cam.screenToReal(new Vector2(screenX, screenY));

        if (rp.equals(LD43.s.gameState.pc.pos)){
            LD43.s.gameState.pc.stand();
        }

        if (LD43.s.gameState.selectedSpell == null) {
            if (LD43.s.gameState.worldMap.isPassable(rp)) {
                LD43.s.gameState.pc.currentAction = new MoveAction(LD43.s.gameState.pc, rp);
            }
        } else {
            LD43.s.gameState.pc.castSpell(LD43.s.gameState.selectedSpell, rp);
            LD43.s.gameState.selectedSpell = null;
        }

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
        TilePos rp = LD43.s.cam.screenToReal(new Vector2(screenX, screenY));

        //System.err.println(rp);

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
