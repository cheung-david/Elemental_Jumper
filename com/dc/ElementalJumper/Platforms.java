package com.dc.ElementalJumper;

/**
 * Created by David on 29/08/2015.
 */
public class Platforms {
   /* public static final float PLATFORM_WIDTH = 2;
    public static final float PLATFORM_HEIGHT = 0.5f;
    public static final int PLATFORM_TYPE_STATIC = 0;
    public static final int PLATFORM_TYPE_MOVING = 1;
    public static final int PLATFORM_STATE_NORMAL = 0;
    public static final int PLATFORM_STATE_DESTROY = 1;
    public static final float PLATFORM_CRUMBLE_TIME = 0.2f * 4;
    public static final float PLATFORM_VELOCITY = 1;

    int type;
    int state;
    float stateTime;

    public Platforms (int type, float x, float y) {
        super(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        this.type = type;
        this.state = PLATFORM_STATE_NORMAL;
        this.stateTime = 0;
        if (type == PLATFORM_TYPE_MOVING) {
            velocity.x = PLATFORM_VELOCITY;
        }
    }

    public void update (float deltaTime) {
        if (type == PLATFORM_TYPE_MOVING) {
            position.add(velocity.x * deltaTime, 0);
            bounds.x = position.x - PLATFORM_WIDTH / 2;
            bounds.y = position.y - PLATFORM_HEIGHT / 2;

            if (position.x < PLATFORM_WIDTH / 2) {
                velocity.x = -velocity.x;
                position.x = PLATFORM_WIDTH / 2;
            }
            if (position.x > World.WORLD_WIDTH - PLATFORM_WIDTH / 2) {
                velocity.x = -velocity.x;
                position.x = World.WORLD_WIDTH - PLATFORM_WIDTH / 2;
            }
        }

        stateTime += deltaTime;
    }

    public void pulverize () {
        state = PLATFORM_STATE_DESTROY;
        stateTime = 0;
        velocity.x = 0;
    }*/
}
