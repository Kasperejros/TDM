package com.upgrade.tdm.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.upgrade.tdm.Game_TDM;
import com.upgrade.tdm.ShapeFactory;

public class PlayScreen implements Screen {

    private final SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Body player;


    public PlayScreen() {
        batch = new SpriteBatch();
        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.zoom = 6f;
        viewport = new FitViewport(640 / Game_TDM.PPM, 480 / Game_TDM.PPM, camera);
        player = ShapeFactory.createRectangle(new Vector2(0,0), new Vector2(64,128), BodyDef.BodyType.DynamicBody, world, 0.4f);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        draw();

    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        debugRenderer.render(world, camera.combined);
    }

    private void update(final float delta) {
        camera.position.set(player.getPosition(), 0);
        camera.update();

        world.step(delta, 6, 2); // TODO check what those variables do

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
