package agents;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TraCIException;
import jade.core.Agent;

public class SimulationAgent extends Agent {

    static String sumo_bin = "/opt/local/bin/sumo-gui";
    static final String config_file = "/Users/tobiao/dev/projects/TrafficMAS/src/main/simulation/config.sumo.cfg";

    protected void setup() {
        parseArguments();
        setupBehaviours();

        //        //start Simulation
        SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
//
//        //set some options
        conn.addOption("step-length", "0.1"); //timestep 100 ms

        try{

            //start TraCI
            conn.runServer();

            //load routes and initialize the simulation
            conn.do_timestep();

            for(int i=0; i<3600; i++){

                //current simulation time
                int simtime = (int) conn.do_job_get(Simulation.getCurrentTime());

                conn.do_job_set(Vehicle.add("veh"+i, "car", "s1", simtime, 0, 13.8, (byte) 1));
                conn.do_job_set(Vehicle.setMinGap("veh"+i, 5));

                conn.do_timestep();

                Object v = conn.do_job_get(Vehicle.getMaxSpeed("veh" + i));
                System.out.println(v);

            }

            //stop TraCI
            conn.close();

        } catch (TraCIException traCIException) {
            traCIException.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parseArguments() {
        Object[] args = getArguments();
        if (args != null) {
//            latitude = (double) args[1];
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
//        addBehaviour(new TickerBehaviour(this, 1000) {
//            @Override
//            protected void onTick() {
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
////                        LatLong currentCoordinates = new LatLong(current.latitude, current.longitude);
////                        LatLong lastCoordinates = new LatLong(last.latitude, last.longitude);
//////                        mm.drawDot(currentCoordinates);
////                        mm.drawLine(lastCoordinates, currentCoordinates, currentColor);
//                    }
//                });
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
