package com.anythingmachine.aiengine;

import java.util.ArrayList;

import com.anythingmachine.agents.States.NPC.NPCState;
import com.anythingmachine.agents.States.Transistions.ActionEnum;

public class UtilityAI {
	private ArrayList<Goal> goals;
	private ArrayList<Action> actions;
	private float actionTime;

	public UtilityAI(float timebetweenactions) {
		goals = new ArrayList<Goal>();
		actions = new ArrayList<Action>();
		actionTime = timebetweenactions;
	}

	public Action ChooseAction(NPCState state) {
		ActionEnum[] possibleActions = state.getPossibleActions();
		if (possibleActions.length > 0) {
			Action bestAction = actions.get(0);
			float thisValue;
			float bestValue = Integer.MAX_VALUE;
			int size = actions.size();
			int length = possibleActions.length;
			for (int i = 0; i < size; i++) {
				for ( int j = 0; j<length; j++) {
					if ( actions.get(i).name == possibleActions[j] ) {
						thisValue = calculateDiscontentment(actions.get(i));

						if (thisValue < bestValue) {
							bestValue = thisValue;
							bestAction = actions.get(i);
						}
						j = length;
					}
				}
			}
			if ( bestValue > 0 ) 
				return bestAction;
		} else {
			System.out.println("no possible actions");
		}
		return null;
	}

	public void takeAction(Action action) {
//		Gdx.app.log("ai has chosen to " + action.name, " resulting state is: " + action.getAIState());
		for (Goal g : goals) {
			g.insistence += action.getGoalChange(g.name);
		}
	}

	public float calculateDiscontentment(Action action) {
		float discontentment = 0;
		float newValue = 0;

		// Iterate through all goals
		for (Goal g : goals) {
			float value = action.getGoalChange(g.name);
			// find new insistence if this action was performed
			newValue = g.insistence + value;
			// optimization to break when positive infinity
			if (newValue < 0)
				newValue = 0;
			discontentment += newValue * newValue;
		}

		return discontentment;
	}

	public float getActionTime() {
		return actionTime;
	}
	
	public void setActionTime(float val) {
		actionTime = val;
	}
	
	public void addGoal(Goal g) {
		goals.add(g);
	}

	public void addAction(Action action) {
		actions.add(action);
	}
}
