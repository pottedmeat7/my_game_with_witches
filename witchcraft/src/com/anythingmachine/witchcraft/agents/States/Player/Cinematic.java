package com.anythingmachine.witchcraft.agents.States.Player;

import com.anythingmachine.aiengine.PlayerStateMachine;

public class Cinematic extends PlayerState {

	public Cinematic(PlayerStateMachine sm, PlayerStateEnum name) {
		super(sm,name);
	}
	

	public void update(float dt) {
		checkGround();
		
		sm.phyState.correctCBody(-8, 64, 0);

		sm.animate.setFlipX(sm.facingleft);

		addWindToCape(dt);
	}
			
	public void switchPower() {
	}
		
	public void transistionIn() {
		if ( parent.name != this.name )
			this.parent = sm.state;
		else
			parent = sm.getState(PlayerStateEnum.IDLE);
		sm.facingleft = false;
		sm.phyState.body.stop();
		sm.animate.bindPose();
		sm.animate.setCurrent("idle", true);
	}

	public void transistionOut() {
		
	}
	
}