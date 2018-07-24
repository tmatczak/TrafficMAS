package agents;

import de.tudresden.sumo.cmd.*;
import de.tudresden.ws.container.SumoPosition2D;
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
import utils.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class VehicleAgent extends Agent {

    private String vehicleId;
    private SumoTraciConnection conn;
    private boolean isSlowing = false;
    private String[] frontVehicles = new String[5];
    private String[] backVehicles = new String[5];
    private static int enterCount = 0;
    private static int exitCount = 0;

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
        enterCount++;
        System.out.println();
        System.out.println("Liczba pojazdów dodanych do symulacji: " + enterCount);
        System.out.println();
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
        TickerBehaviour tickerBehaviour = new TickerBehaviour(this, 20) {
            @Override
            protected void onTick() {
                try {
                    SumoStringList v = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
                    if (v.contains(vehicleId)) {
                        String roadId = (String) conn.do_job_get(Vehicle.getRoadID(vehicleId));

//                        SumoPosition2D ownPosition = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(vehicleId));
                        String ownLaneId = (String) conn.do_job_get(Vehicle.getLaneID(vehicleId));
                        int ownLaneIndex = (int) conn.do_job_get(Vehicle.getLaneIndex(vehicleId));
                        double ownPositionOnLane = (double) conn.do_job_get(Vehicle.getLanePosition(vehicleId));
                        SumoStringList vehiclesIds = (SumoStringList) conn.do_job_get(Edge.getLastStepVehicleIDs(roadId));

//                        System.out.println(vehicleId + " is on road with id: " + roadId + " on lane: " + ownLaneId + ", " + ownLaneIndex + " at " + ownPositionOnLane + " meter");

                        findNeighbours(vehiclesIds, ownLaneIndex, ownPositionOnLane);

//                        System.out.println();
//                        for (String name: vehiclesIds) {
//                            if (!name.equals(vehicleId)) {
//                                SumoPosition2D position = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(name));
//                                double distance = Utils.distance(ownPosition, position);
//                                System.out.print("Distance between " + vehicleId + " and " + name + " is equal: " + distance);
//                                System.out.println();
//                            }
//                        }
//                        System.out.println();

                    } else {
                        myAgent.removeBehaviour(this);
                    }
//                    String roadId = (String) conn.do_job_get(Vehicle.getRoadID(vehicleId));
////                    String roadId = (String) conn.do_job_get(Vehicle.getLeader(vehicleId, 100));
//                    System.out.println(vehicleId + "is on road with id: " + roadId);
////                    SumoStringList vehiclesIds = (SumoStringList) conn.do_job_get(Edge.getLastStepVehicleIDs(roadId));
////                    System.out.println();
////                    for (String name: vehiclesIds) {
////                        System.out.print(name + ", ");
////                    }
//
////                    SumoPosition2D position = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(vehicleId));
////                    System.out.println(vehicleId + " is at " + position.toString());
////                    double currentSpeed = (double) conn.do_job_get(Vehicle.getSpeed(vehicleId));
////
////                    if (currentSpeed > 10.0 && !isSlowing) {
////                        System.out.println(vehicleId + " zwalnia z " + currentSpeed);
////                        conn.do_job_set(Vehicle.slowDown(vehicleId, 5, 1000));
////                    } else if (currentSpeed < 5.1) {
////                        System.out.println(vehicleId + " przyspiesza ");
////                        isSlowing = false;
////                    }
                } catch (IllegalStateException i) {
//                    System.out.println("Connection is closed");
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        addBehaviour(tickerBehaviour);

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
//                System.out.println(vehicleId + "is going down");
//                exitCount++;
//                System.out.println("Liczba pojazdów usunuetych z symulacji: " + exitCount);
//                System.out.println();
                doDelete();
                break;
            }
        }
    }

    private void findNeighbours(SumoStringList vehiclesIds, int ownLaneIndex, double ownPositionOnLane) {
        try {
            double[] distanceFront = new double[5];
            double[] distanceBack = new double[5];

            int secondLeftLaneIndex = ownLaneIndex - 2;
            int firstLeftLaneIndex = ownLaneIndex - 1;
            int firstRightLaneIndex = ownLaneIndex + 1;
            int secondRightLaneIndex = ownLaneIndex + 2;

            int[] lanesIndexes = { secondLeftLaneIndex, firstLeftLaneIndex, ownLaneIndex, firstRightLaneIndex, secondRightLaneIndex };

            for (String name: vehiclesIds) {
                if (!name.equals(vehicleId)) {
                    String laneId = (String) conn.do_job_get(Vehicle.getLaneID(name));
                    int laneIndex = (int) conn.do_job_get(Vehicle.getLaneIndex(name));
                    double positionOnLane = (double) conn.do_job_get(Vehicle.getLanePosition(name));

                    for (int i = 0; i < 5; i++) {
                        int tempLaneIndex = lanesIndexes[i];
                        if (tempLaneIndex >= 0 && tempLaneIndex == laneIndex) {
                            LaneDistance updatedDelta = getUpdatedNearestVehicleDistance(name, i, positionOnLane, ownPositionOnLane, distanceFront[i], distanceBack[i]);
                            distanceBack[i] = updatedDelta.back;
                            distanceFront[i] = updatedDelta.front;
                        }
                    }
                }
            }

            System.out.println();
            System.out.print(vehicleId + " is on lane with index: " + ownLaneIndex + " and is circled by: " + Arrays.toString(frontVehicles)
            + ", " + Arrays.toString(backVehicles));
            System.out.println();

            frontVehicles = new String[5];
            backVehicles = new String[5];

        } catch (IllegalStateException i) {
//                    System.out.println("Connection is closed");
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LaneDistance getUpdatedNearestVehicleDistance(String vehicleId, int lanePosition, double positionOnLane, double ownPositionOnLane, double distanceFront, double distanceBack) {
        double delta =  positionOnLane - ownPositionOnLane;
        if (delta > 0) {
            if (distanceFront == 0 || distanceFront > delta) {
                frontVehicles[lanePosition] = vehicleId;
                return new LaneDistance(delta, 0);
            }
        } else {
            if (distanceBack == 0 || distanceBack < delta) {
                backVehicles[lanePosition] = vehicleId;
                return new LaneDistance(0, delta);
            }
        }

        return new LaneDistance(0, 0);
    }
}
