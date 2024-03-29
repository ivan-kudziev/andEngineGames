package by.kipind.game.olympicgames.sprite;

import java.util.Arrays;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;
import by.kipind.game.olympicgames.ResourcesManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class Player extends AnimatedSprite {
    final String LOG_TAG = "myLogs";

    // ---------------------------------------------
    // VARIABLES
    // ---------------------------------------------

    public Body body;
    private boolean canRun = false;
    private boolean isFinish = false;

    private int footContacts = 0;
    private float speed = 0;
    private float speedBeforJump = 0;
    private Long frameDuration = 0l;

    private int[] animFrame = new int[] { 1, 2, 3, 4, 5, 6 };

    // private long[] spriteFameDuration = new long[animFrame.length];

    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------

    public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld) {
	super(pX, pY, (ITiledTextureRegion)ResourcesManager.getInstance().gameGraf.get("player_region"), vbo);
	this.setHeight(this.getHeight()*0.7f);
	this.setWidth(this.getWidth()*0.7f);
	
	createPhysics(camera, physicsWorld);
	camera.setChaseEntity(this);

	// Arrays.fill(spriteFameDuration, 1000);
    }

    // ---------------------------------------------
    // CLASS LOGIC
    // ---------------------------------------------

    public float getSpeed() {
	return speed;

    }

    public void setSpeed(float speed) {
	if (speed > 1) {
	    this.speed = speed;
	} else {
	    this.speed = 1;
	}
    }

    public void onFinish() {
	this.isFinish = true;

    }

    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
	body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

	body.setUserData("player");
	body.getFixtureList().get(0).setFriction(0.25f);
	body.setFixedRotation(true);

	physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	    @Override
	    public void onUpdate(float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);
		camera.onUpdate(0.1f);
		setSpeed(body.getLinearVelocity().x);

		if (getSpeed() <= 1.1) {
		    stopAnimation(0);
		    canRun = false;
		    // onStop();
		}

		if (canRun) {
		    if (getSpeed() > 11) {
			if (frameDuration != 70l) {
			    frameDuration = 70l;
			    changeFrameDuration(frameDuration, animFrame, getCurrentTileIndex());
			}
		    } else if (getSpeed() > 8) {
			if (frameDuration != 90l) {
			    frameDuration = 90l;
			    changeFrameDuration(frameDuration, animFrame, getCurrentTileIndex());
			}
		    } else {
			if (frameDuration != 140l) {
			    frameDuration = 140l;
			    changeFrameDuration(frameDuration, animFrame, getCurrentTileIndex());
			}
		    }
		} else {
		   
		}
	    }

	});
    }

    public boolean isFinish() {
	return isFinish;
    }

    public void setFinish(boolean isFinish) {
	this.isFinish = isFinish;
    }

    private void onStop() {

    }

    public void setRunning() {
    }

    public void powFunctionRun() {
	if (isFinish ||(footContacts  !=1)) {
	    return;
	}
	if (getCurrentTileIndex() == 0) {
	    this.setCurrentTileIndex(1);
	    this.frameDuration = 0l;
	    body.applyLinearImpulse(1f, 0, body.getPosition().x, body.getPosition().y);
	} else {
	    body.applyLinearImpulse((float) Math.pow(0.1, this.getSpeed() / 50), 0, body.getPosition().x, body.getPosition().y);
	}
	canRun = true;
    }

    public void run() {
	if (isFinish || footContacts  !=1) {
	    return;
	}
	if (getCurrentTileIndex() == 0) {
	    this.setCurrentTileIndex(1);
	    this.frameDuration = 0l;
	    body.applyLinearImpulse(5f, 0, body.getPosition().x, body.getPosition().y);
	} else {
	    body.applyLinearImpulse(3f, 0, body.getPosition().x, body.getPosition().y);
	}
	canRun = true;
    }

    public void increaseFootContacts() {
	footContacts++;
	this.setCurrentTileIndex(1);
	this.frameDuration = 0l;
	body.setLinearVelocity(speedBeforJump,0f);
	canRun=true;
	
    }

    public void decreaseFootContacts() {
	footContacts--;
	stopAnimation(3);
	//body.applyLinearImpulse(0, 3f, body.getPosition().x, body.getPosition().y);
	
	//body.setLinearVelocity(this.getWidth()/64f, 3f);
	canRun=false;
    }

    public void jumpUp() {
	if (footContacts  !=1) {
	    return;
	}
	//body.applyLinearImpulse(0, 3f, body.getPosition().x, body.getPosition().y);
	speedBeforJump=speed;
	body.setLinearVelocity(this.getWidth()/7f, this.getHeight()/13f);
	
    }

    public void changeFrameDuration(Long frameDuration, int[] spriteFrames, int currentFrame) {

	Log.d(LOG_TAG, "err---->" + frameDuration);

	int iter = currentFrame, i = 0;
	int[] spriteFramesMod = new int[spriteFrames.length];
	long[] spriteFameDuration = new long[spriteFrames.length];

	Arrays.fill(spriteFameDuration, frameDuration);

	do {
	    spriteFramesMod[i] = spriteFrames[iter];

	    if (iter == spriteFrames.length - 1) {
		iter = 0;
	    } else {
		iter++;
		i++;
	    }

	} while (iter != currentFrame);

	animate(spriteFameDuration, spriteFramesMod, true);

    }

    public void setSpeedBeforJump(float speedBeforJump) {
        this.speedBeforJump = speedBeforJump;
    }
}
