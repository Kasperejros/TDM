package com.upgrade.tdm.screens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.upgrade.tdm.MapLoader;
import com.upgrade.tdm.ShapeFactory;

public class PlayScreen implements Screen {

    private final SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Body player;
    private final MapLoader mapLoader;
    private final RayHandler rayHandler;
    private final PointLight light;

    private static final int MOVE_DIRECTION_NONE = 0;
    private static final int MOVE_DIRECTION_FORWARD = 1;
    private static final int MOVE_DIRECTION_BACKWARD = 2;

    private static final int TURN_DIRECTION_NONE = 0;
    private static final int TURN_DIRECTION_LEFT = 1;
    private static final int TURN_DIRECTION_RIGHT = 2;

    private static final float DRIFT_FACTOR = 0.0f;
    private static final float TURN_SPEED = 2.0f;
    private static final float MOVE_SPEED = 12.0f;
    private static final float MAX_SPEED = 24.0f;

    private int moveDirection = MOVE_DIRECTION_NONE;
    private int turnDirection = TURN_DIRECTION_NONE;


    public PlayScreen() {
        batch = new SpriteBatch();

        world = new World(new Vector2(0, 0), true);  // Gravity set for 0,0 as it its the top down game

        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();
        camera.zoom = 6f;
        viewport = new FitViewport(640 / Game_TDM.PPM, 480 / Game_TDM.PPM, camera);

        mapLoader = new MapLoader(world);
        player = mapLoader.getPlayer();

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
        rayHandler.setBlurNum(3);

        light = new PointLight(rayHandler, 64, new Color(1,1,1,1), 16f, 0f, 0f);
        light.attachToBody(player);

        player.setLinearDamping(0.5f);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        processInput();
        update(delta);
        handleDrift();
        draw();

    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        debugRenderer.render(world, camera.combined);
        rayHandler.setCombinedMatrix(camera);
        rayHandler.render();
    }

    private void update(final float delta) {
        Vector2 baseVector = new Vector2(0,0);



        camera.position.set(player.getPosition(), 0);
        camera.update();

        world.step(delta, 6, 2); // TODO check what those variables do
        rayHandler.update();
    }

    private void processInput() {

        Vector2 baseVector = new Vector2(0,0);

        if (turnDirection == TURN_DIRECTION_RIGHT){
            player.setAngularVelocity(-TURN_SPEED);
        } else if (turnDirection == TURN_DIRECTION_LEFT){
            player.setAngularVelocity(TURN_SPEED);
        } else if (turnDirection == TURN_DIRECTION_NONE && player.getAngularVelocity() != 0){
            player.setAngularVelocity(0.0f);
        }

        if (moveDirection == MOVE_DIRECTION_FORWARD){
            baseVector.set(0, MOVE_SPEED);
        } else if (moveDirection == MOVE_DIRECTION_BACKWARD) {
            baseVector.set(0, -MOVE_SPEED);
        }

        if (!baseVector.isZero() && player.getLinearVelocity().len() < MAX_SPEED){
            player.applyForceToCenter(player.getWorldVector(baseVector), true);
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            moveDirection = MOVE_DIRECTION_FORWARD;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            moveDirection = MOVE_DIRECTION_BACKWARD;
        } else {
            moveDirection = MOVE_DIRECTION_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            turnDirection = TURN_DIRECTION_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            turnDirection = TURN_DIRECTION_RIGHT;
        } else {
            turnDirection = TURN_DIRECTION_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            camera.zoom -= 0.4f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)){
            camera.zoom += 0.4f;
        }
    }

    private Vector2 getForwardVelocity(){
        Vector2 currentNormal = player.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(player.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 getLateralVelocity(){
        Vector2 currentNormal = player.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(player.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 multiply(float a, Vector2 v){
        return new Vector2(a * v.x, a * v.y);
    }

    private void handleDrift() {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        player.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * DRIFT_FACTOR, forwardSpeed.y + lateralSpeed.y * DRIFT_FACTOR);
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
        mapLoader.dispose();
        rayHandler.dispose();
    }
}
