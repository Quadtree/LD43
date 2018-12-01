package info.quadtree.ld43.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.user.client.Window;
import info.quadtree.ld43.LD43;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Window.getClientWidth(), Window.getClientHeight());
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LD43();
        }
}