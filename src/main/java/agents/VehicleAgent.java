package agents;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TraCIException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.DefaultAgentMessages;
import utils.DefaultAgentName;
import utils.SimpleMessage;

import java.io.IOException;

public class VehicleAgent extends Agent {

    private String vehicleId;
    private SumoTraciConnection conn;
    private boolean isSlowing = false;

    protected void setup() {
        parseArguments();
        addToSimulation();
        setupBehaviours();
    }

    private void parseArguments() {
        Object[] args = getArguments();
        if (args != null) {
            vehicleId = (String) args[0];
            conn = (SumoTraciConnection) args[1];
        }
    }

    private void addToSimulation() {
//        try {
//            int simtime = (int) conn.do_job_get(Simulation.getCurrentTime());
//            conn.do_job_set(Vehicle.add(vehicleId, "car", "s1", simtime, 0, 5, (byte) 0));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

        addBehaviour(new TickerBehaviour(this, 50) {
            @Override
            protected void onTick() {
                try {
                    double currentSpeed = (double) conn.do_job_get(Vehicle.getSpeed(vehicleId));

                    if (currentSpeed > 10.0 && !isSlowing) {
                        System.out.println(vehicleId + " zwalnia z " + currentSpeed);
                        conn.do_job_set(Vehicle.slowDown(vehicleId, 5, 1000));
                    } else if (currentSpeed < 5.1) {
                        System.out.println(vehicleId + " przyspiesza ");
                        isSlowing = false;
                    }
                } catch (IllegalStateException i) {
//                    System.out.println("Connection is closed");
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        SimpleMessage sm = (SimpleMessage) msg.getContentObject();
                        parseMessage(sm);
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                }
                block();
            }
        });
    }

    private void parseMessage(SimpleMessage sm) {
        switch (sm.getEvent()) {
            case DefaultAgentMessages.DESTROY: {
                System.out.println(vehicleId + "is going down");
                doDelete();
                break;
            }
        }
    }
}

