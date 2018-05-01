package agents;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TraCIException;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class VehicleAgent extends Agent {

    private String vehicleId;

    protected void setup() {
        parseArguments();
        setupBehaviours();
    }

    private void parseArguments() {
        Object[] args = getArguments();
        if (args != null) {
            vehicleId = (String) args[0];
//            longitude = (double) args[2];
        }
    }

    private void setupBehaviours() {
//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                try {
//                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//                    SimpleMessage sm = new SimpleMessage(this.getAgent().getAID().getLocalName(), CustomGuiEvent.ADD_AGENT);
//                    msg.setContentObject(sm);
//                    msg.addReceiver(new AID(DefaultAgentName.CUSTOM_GUI_AGENT, AID.ISLOCALNAME));
//                    send(msg);
//                } catch (IOException e) {
//                    System.out.println("Exception in InformingAgent");
//                }
//            }
//        });
//
//

//        addBehaviour(new TickerBehaviour(this, 1) {
//            @Override
//            protected void onTick() {
//
//            }
//        });


//        addBehaviour(new CyclicBehaviour(this) {
//            public void action() {
//                ACLMessage msg = receive();
//                if (msg != null) {
//                    addBehaviour(new OneShotBehaviour() {
//                        @Override
//                        public void action() {
//                            try {
//                                ACLMessage msghg = new ACLMessage(ACLMessage.INFORM);
//                                SimpleMessage sm = new SimpleMessage(this.getAgent().getAID().getLocalName(), CustomGuiEvent.DELETE_AGENT);
//                                msg.setContentObject(sm);
//                                msg.addReceiver(new AID(DefaultAgentName.CUSTOM_GUI_AGENT, AID.ISLOCALNAME));
//                                send(msg);
//                                doDelete();
//                            } catch (IOException e) {
//                                System.out.println("Exception in InformingAgent");
//                            }
//                        }
//                    });
//                }
//                block();
//            }
//        });

    }
}

