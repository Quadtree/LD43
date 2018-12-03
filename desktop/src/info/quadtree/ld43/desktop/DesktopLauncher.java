package info.quadtree.ld43.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import info.quadtree.ld43.LD43;
import info.quadtree.ld43.Util;

public class DesktopLauncher {
	public static void main (String[] arg) {
		if (LD43.CHEATS){
			Util.takeScreenshot = () -> {
				byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
				for (int i=4;i<pixels.length;i += 4){
					pixels[i - 1] = (byte)255;
				}

				Pixmap pm = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
				BufferUtils.copy(pixels, 0, pm.getPixels(), pixels.length);
				PixmapIO.writePNG(Gdx.files.external(System.currentTimeMillis() + ".png"), pm);
				pm.dispose();
			};

			TexturePacker.processIfModified("../../raw_assets", ".", "main");
		}




		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		if (LwjglApplicationConfiguration.getDesktopDisplayMode().width > 1800){
			config.width = 1800;
			config.height = 1000;
		} else {
			config.width = 1024;
			config.height = 768;
		}

		new LwjglApplication(new LD43(), config);
	}
}
