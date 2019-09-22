package com.upgrade.tdm;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class ShapeFactory {
    private ShapeFactory() {}

    public static Body createRectangle (final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, final float density) {

        // Define body
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set (position.x / Game_TDM.PPM, position.y / Game_TDM.PPM);
        bodyDef.type = type;
        final Body body = world.createBody(bodyDef);

        //Define fixture
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / Game_TDM.PPM, size.y / Game_TDM.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;

        // Add fixture to the body and dispose of the shape
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

}
