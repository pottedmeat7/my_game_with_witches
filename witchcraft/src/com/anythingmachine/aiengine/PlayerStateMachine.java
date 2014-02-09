package com.anythingmachine.aiengine;

import java.util.ArrayList;

import com.anythingmachine.input.InputManager;
import com.anythingmachine.physicsEngine.PhysicsState;
import com.anythingmachine.witchcraft.WitchCraft;
import com.anythingmachine.witchcraft.States.Player.PlayerState;
import com.anythingmachine.witchcraft.States.Player.PlayerStateEnum;
import com.anythingmachine.witchcraft.ground.Curve;
import com.anythingmachine.witchcraft.ground.Platform;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.SkeletonData;

public class PlayerStateMachine extends StateMachine {
	public PlayerState state;
	public InputManager input;
	protected PlayerState[] states;
	public PhysicsState phyState;
	public ArrayList<Sprite> powerUi;
	public float uiFadein;
	public PlayerStateEnum power;
	public Bone neck;
	public int curGroundSegment;
	public Curve curCurve;
	public Platform elevatedSegment;
	public String dupeSkin;
	
	public PlayerStateMachine(String name, Vector3 pos, Vector2 scl, boolean flip,
			SkeletonData sd) {
		super(name, pos, scl, flip, sd);
		neck = animate.findBone("neck");
		dupeSkin = "";
		power = PlayerStateEnum.JUMPING;
		uiFadein = -5;

		input = new InputManager();
		states = new PlayerState[PlayerStateEnum.DEAD.getSize()];
	}

	@Override
	public void update(float dt) {
		input.update(dt);

		state.update(dt);

		float delta = Gdx.graphics.getDeltaTime();
		if (state.canAnimate())
			animate.applyTotalTime(true, delta);

		animate.setPos(phyState.getPos(), -8f, 0f);
		animate.updateSkel(dt);

	}

	public void drawUI(Batch batch) {
		if (uiFadein >= 0f) {
			powerUi.get(power.getPowerIndex()).draw(batch, uiFadein);
			uiFadein += WitchCraft.dt * 0.5f;
			if (uiFadein >= 1f)
				uiFadein = -2f;
		} else if (uiFadein < 0 && uiFadein > -4) {
			powerUi.get(power.getPowerIndex()).draw(batch, -(uiFadein + 1));
			uiFadein += WitchCraft.dt * 0.5f;
			if (uiFadein >= -1)
				uiFadein = -5f;
		}
	}

	public void nextPower() {
		power = states[power.getNextPower()].name;
		System.out.println(power);
	}

	public void usePower() {
		state.transistionOut();
		this.state = states[power.getID()];
		state.transistionIn();
	}

	public void addState(PlayerStateEnum type, PlayerState s) {
		states[type.getID()] = s;
	}

	public void setInitialState(PlayerStateEnum name) {
		this.state = states[name.getID()];
	}

	public void setState(PlayerStateEnum name) {
		state.transistionOut();
		this.state = states[name.getID()];
		state.transistionIn();
	}

	public PlayerState getState(PlayerStateEnum name) {
		return states[name.getID()];
	}

	public boolean inState(PlayerStateEnum state) {
		return this.state.name.getID() == state.getID();
	}

	public boolean testtesttestOR(String test1, String test2, String test3) {
		return tests.get(test1) || tests.get(test2) || tests.get(test3);
	}

	public boolean testANDState(String test, PlayerStateEnum state) {
		return tests.get(test) && this.state.name == state;
	}

	public boolean testORState(String test, PlayerStateEnum state) {
		return tests.get(test) || this.state.name == state;
	}

	public boolean testORNotState(String test, PlayerStateEnum state) {
		return tests.get(test) || this.state.name != state;
	}

}