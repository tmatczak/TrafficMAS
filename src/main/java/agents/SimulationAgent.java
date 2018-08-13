package agents;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TraCIException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import managers.AgentsEnvironmentManager;
import utils.DefaultAgentMessages;
import utils.DefaultAgentName;
import utils.SimpleMessage;

import java.io.IOException;
import java.util.ArrayList;

public class SimulationAgent extends Agent {

    // OSX PATHS
//    static String sumo_bin = "/opt/local/bin/sumo-gui";
//    static final String config_file = "/Users/tobiao/dev/projects/TrafficMAS/src/main/simulation/config.sumo.cfg";

    // WINDOWS PATHS
    static String sumo_bin = "C:/Users/tobia/pmag/sumo-0.32.0/bin/sumo-gui";
    static final String config_file = "C:/Users/tobia/pmag/TrafficMAS/src/main/simulation/config.sumo.cfg";

    private AgentsEnvironmentManager aem;
    private ArrayList<String> agentsIds = new ArrayList<>();
    //start Simulation
    private SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
    private int finalStep = 4000;
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
        conn.addOption("step-length", "0.02"); //timestep 200 ms

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

        addBehaviour(new TickerBehaviour(this, 25) {
            @Override
            protected void onTick() {
                try {

                    //current simulation time
                    int simtime = (int) conn.do_job_get(Simulation.getCurrentTime());

                    if (currentStep !=  finalStep && currentStep % 100 == 0) {

                        String vehId = "veh" + currentStep;
                        agentsIds.add(vehId);

                        conn.do_job_set(Vehicle.add(vehId, "car", "s1", simtime, 0, 11.7, (byte) 0));

                        Object[] parameters = { vehId, conn };

                        aem.addAgentToMainContainer(vehId, VehicleAgent.class.getName(), parameters);

//                        conn.do_job_set(Vehicle.setSpeed(vehId, 2));
//                        conn.do_job_set(Vehicle.changeLane(vehId, (byte) 1, 1000));

//                        conn.do_job_set(Vehicle.slowDown(vehId, 2, 1000));
                    }

                    conn.do_timestep();

                    try {
                        SumoStringList v = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
//                        for (String name: v) {
//                            double currentSpeed = (double) conn.do_job_get(Vehicle.getSpeed(name));
//                            System.out.println(name + " pedzi: " + currentSpeed);
//                        }


                        ArrayList<String> temp = new ArrayList<>();
                        temp.addAll(agentsIds);
                        temp.removeAll(v);

                        if (!temp.isEmpty()) {
                            for (String agentName: temp) {
                                System.out.println("Do usuniecia: " + agentName);
                                sendMessage(agentName, DefaultAgentMessages.DESTROY);
                            }
                            agentsIds.removeAll(temp);
                        }

                    } catch (TraCIException traCIException) {
//                    traCIException.printStackTrace();
                    }

                    //stop TraCI
                    if (currentStep == finalStep) {
                        conn.close();
                        myAgent.doDelete();
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
//                                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
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

    private void sendMessage(String agentName, int event) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//                    SimpleMessage sm = new SimpleMessage(this.getAgent().getAID().getLocalName(), event);
                    SimpleMessage sm = new SimpleMessage("hehe", event); //TODO: change message object
                    msg.setContentObject(sm);
                    msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                    send(msg);
                } catch (IOException e) {
                    System.out.println("Exception in SimulationAgent ");
                }
            }
        });
    }
}
