package com.anythingmachine.witchcraft.ParticleEngine;

import java.util.ArrayList;

import com.anythingmachine.physicsEngine.Particle;
import com.anythingmachine.witchcraft.WitchCraft;
import com.anythingmachine.witchcraft.Util.Util;
import com.anythingmachine.witchcraft.Util.Util.EntityType;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Arrow extends Particle {
	private Body collisionBody;
	private Sprite sprite;

	public Arrow(Vector3 pos, Vector3 vel) {
		super(pos, vel);
		this.type = EntityType.ARROW;
		buildCollisionBody();
		WitchCraft.rk4System.addParticle(this);
		sprite = ((TextureAtlas) WitchCraft.assetManager
				.get("data/spine/characters.atlas"))
				.createSprite("archer_xcf-aroow");
		sprite.scale(-0.4f);
	}

	public void destroy() {
		WitchCraft.world.destroyBody(collisionBody);
	}

	public ArrayList<Particle> getParticles() {
		ArrayList<Particle> list = new ArrayList<Particle>();
		list.add(this);
		return list;
	}

	public void draw(Batch batch) {
		if (!WitchCraft.cam.inBigBounds(pos)) {
			stable = true;
		} else {
			collisionBody.setTransform(pos.x * Util.PIXEL_TO_BOX, pos.y
					* Util.PIXEL_TO_BOX, 0);
			sprite.setPosition(pos.x, pos.y);
			sprite.draw(batch);
		}
	}

	public Vector2 getPos2D() {
		return new Vector2(pos.x, pos.y);
	}

	public Vector3 getVel() {
		return vel;
	}

	public Vector2 getVel2D() {
		return new Vector2(vel.x, vel.y);
	}

	public void setPos(float x, float y, float z) {
		this.pos.set(x, y, z);
	}

	public void pointAtTarget(Vector3 target, float speed) {
		Vector3 dir = new Vector3(target.x - pos.x, target.y - pos.y, 0);
		dir.scl(1 / dir.len());
		this.vel.set(dir.x * speed, Math.max(0.125f, dir.y) * speed, 0);
		float costheta = Util.dot(vel, new Vector3(1, 0, 0)) / vel.len();
		sprite.setRotation((float) Math.acos((double) costheta)
				* Util.RAD_TO_DEG);
		sprite.setPosition(pos.x, pos.y);
		collisionBody.setTransform(pos.x * Util.PIXEL_TO_BOX, pos.y
				* Util.PIXEL_TO_BOX, (float) Math.acos((double) costheta));
	}

	public void setVel(float x, float y, float z) {
		this.vel.set(x, y, z);
		float costheta = Util.dot(vel, new Vector3(1, 0, 0)) / vel.len();
		sprite.setOrigin(0, 0);
		sprite.setRotation((float) Math.acos((double) costheta)
				* Util.RAD_TO_DEG);
		sprite.setPosition(pos.x, pos.y);
		collisionBody.setTransform(pos.x * Util.PIXEL_TO_BOX, pos.y
				* Util.PIXEL_TO_BOX, (float) Math.acos((double) costheta));
	}

	public void addPos(float x, float y) {
		this.pos.add(x, y, 0);
	}

	public void apply2DImpulse(float x, float y) {
		this.externalForce.set(x, y, 0);
	}

	public void applyImpulse(Vector3 force) {
		this.externalForce = force;
	}

	@Override
	public Vector3 accel(Vector3 pos, Vector3 vel, float t) {
		Vector3 result = new Vector3(0, Util.GRAVITY, 0);
		return result;
	}

	@Override
	public void integratePos(Vector3 dxdp, float dt) {
		this.pos.add(Util.sclVec(dxdp, dt));
	}

	@Override
	public void integrateVel(Vector3 dvdp, float dt) {
		this.vel.add(Util.sclVec(dvdp, dt));
		externalForce = new Vector3(0, 0, 0);
		float costheta = Util.dot(vel, new Vector3(1, 0, 0)) / vel.len();
		sprite.setPosition(0, 0);
		sprite.setRotation((float) Math.acos((double) costheta)
				* Util.RAD_TO_DEG);
		sprite.setPosition(pos.x, pos.y);
		collisionBody.setTransform(pos.x * Util.PIXEL_TO_BOX, pos.y
				* Util.PIXEL_TO_BOX, (float) Math.acos((double) costheta));
	}

	public boolean isStable() {
		return stable;
	}

	private void buildCollisionBody() {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(new Vector2(this.getPos().x, this.getPos().y));
		collisionBody = WitchCraft.world.createBody(def);
		PolygonShape shape = new PolygonShape();
		collisionBody.setBullet(true);
		shape.setAsBox(32 * Util.PIXEL_TO_BOX, 16 * Util.PIXEL_TO_BOX);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.isSensor = true;
		fixture.density = 1f;
		fixture.filter.categoryBits = Util.CATEGORY_PLAYER;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		collisionBody.createFixture(fixture);
		collisionBody.setUserData(this);
		shape.dispose();
	}
}
