package agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class HelloAgent extends Agent {
    protected void setup() {
        setupBehaviours();
    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Agent-1 "+getAID().getName()+" terminating.");
    }

    private void setupBehaviours() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                myAgent.doDelete();
            }
        });
    }
}
