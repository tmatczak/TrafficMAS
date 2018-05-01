package agents;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TraCIException;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import javafx.application.Platform;
import managers.AgentsEnvironmentManager;

import java.io.IOException;
import java.util.ArrayList;

public class SimulationAgent extends Agent {

    static String sumo_bin = "/opt/local/bin/sumo-gui";
    static final String config_file = "/Users/tobiao/dev/projects/TrafficMAS/src/main/simulation/config.sumo.cfg";

    private AgentsEnvironmentManager aem;
    private ArrayList<String> agentsIds = new ArrayList<>();
    //start Simulation
    private SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
    private int finalStep = 6000;
    private int currentStep = 0;

    protected void setup() {
        parseArguments();
        setupSimulation();
        setupBehaviours();
    }

    private void parseArguments() {
        Object[] args = getArguments();
        if (args != null) {
            aem = (AgentsEnvironmentManager) args[0];
//            longitude = (double) args[2];
        }
    }

    private void setupSimulation() {
        //        set some options
        conn.addOption("step-length", "0.01"); //timestep 100 ms

        try {

            //start TraCI
            conn.runServer();

            //load routes and initialize the simulation
            conn.do_timestep();

        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
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

        addBehaviour(new TickerBehaviour(this, 1) {
            @Override
            protected void onTick() {
                try {

                    //current simulation time
                    int simtime = (int) conn.do_job_get(Simulation.getCurrentTime());

                    conn.do_job_set(Vehicle.add("veh" + currentStep, "car", "s1", simtime, 0, 11.7, (byte) 1));
                    aem.addAgentToMainContainer("veh"+currentStep, HelloAgent.class.getName(), null);

                    conn.do_timestep();

                    conn.do_job_set(Vehicle.setSpeed("veh"+currentStep, 2));

                    try {
                        SumoStringList v = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
                        for (String name: v) {
                            double currentSpeed = (double) conn.do_job_get(Vehicle.getSpeed(name));
                            System.out.println(name + " pedzi: " + currentSpeed);
                        }
                        System.out.println();
                    } catch (TraCIException traCIException) {
//                    traCIException.printStackTrace();
                    }

                    //stop TraCI
                    if (currentStep == finalStep) {
                        conn.close();
                    } else {
                        currentStep++;
                    }

                } catch (IOException ioexception) {
                    ioexception.printStackTrace();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


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
