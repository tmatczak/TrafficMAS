package agents;

import de.tudresden.sumo.cmd.*;
import de.tudresden.ws.container.SumoLink;
import de.tudresden.ws.container.SumoLinkList;
import de.tudresden.ws.container.SumoNextTLS;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.*;

public class VehicleAgent extends Agent {

    private static final double REACTION_TIME = 1.0; // in seconds
    private static final double FRICTION_COEFFICIENT = 0.8;

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
        TickerBehaviour tickerBehaviour = new TickerBehaviour(this, 10) {
            @Override
            protected void onTick() {
                try {
                    SumoStringList v = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
                    if (v.contains(vehicleId)) {

                        String roadId = (String) conn.do_job_get(Vehicle.getRoadID(vehicleId));
                        int ownLaneIndex = (int) conn.do_job_get(Vehicle.getLaneIndex(vehicleId));
                        String ownLaneId = (String) conn.do_job_get(Vehicle.getLaneID(vehicleId));
                        double ownPositionOnLane = (double) conn.do_job_get(Vehicle.getLanePosition(vehicleId));
                        SumoStringList vehiclesIds = (SumoStringList) conn.do_job_get(Edge.getLastStepVehicleIDs(roadId));

                        findNeighbours(vehiclesIds, ownLaneIndex, ownPositionOnLane);

                        //TODO: tutaj dalsza implementacja

                        if (frontVehicles[RoadPosition.CENTER.ordinal()] != null) {
//                            String frontVehId = frontVehicles[RoadPosition.CENTER.ordinal()];
//                            double frontVehiclePosition = (double) conn.do_job_get(Vehicle.getLanePosition(frontVehId));
//                            double distanceBetweenVehicles = Math.abs(frontVehiclePosition - ownPositionOnLane);
//
//                            double tempSpeed = (double) conn.do_job_get(Vehicle.getSpeed(vehicleId));
//                            double tempAccel = (double) conn.do_job_get(Vehicle.getAccel(vehicleId)); // useless
//
//                            double timeToCollision = distanceBetweenVehicles / tempSpeed;
//
//                            double stopDistance = calculateStopDistance(tempSpeed);
//
//                            double calculatedVelocity = (stopDistance <= distanceBetweenVehicles) ? calculateMaximumVelocity(distanceBetweenVehicles) : 0.0;
//                            conn.do_job_set(Vehicle.setSpeed(vehicleId, calculatedVelocity));
//
//                            System.out.println("Stop distance for vehicle: " + vehicleId + " is equal to " + stopDistance + " meters. Maximum velocity for given distance is " + calculatedVelocity);
                        } else {
                            LightPhase phase = getLightPhaseOfTrafficlightOnLane(ownLaneId);
//                            System.out.println(phase);
                        }

                        resetNeighbours();

                    } else {
                        myAgent.removeBehaviour(this);
                    }
                } catch (IllegalStateException i) {
//                    System.out.println("Connection is closed");
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        CyclicBehaviour cyclicBehaviour = new CyclicBehaviour(this) {
            public void action() {
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

                        //TODO: tutaj dalsza implementacja

                        if (frontVehicles[RoadPosition.CENTER.ordinal()] != null) {
                            String frontVehId = frontVehicles[RoadPosition.CENTER.ordinal()];
                            double frontVehiclePosition = (double) conn.do_job_get(Vehicle.getLanePosition(frontVehId));
                            double distanceBetweenVehicles = Math.abs(frontVehiclePosition - ownPositionOnLane);
//                            System.out.println(frontVehId + " is in front of: " + vehicleId + ". Distance between:" + distanceBetweenVehicles + " meters.");

                            double tempSpeed = (double) conn.do_job_get(Vehicle.getSpeed(vehicleId));





                        } else {
                            double laneLength = (double) conn.do_job_get(Lane.getLength(ownLaneId));
                            double remainingDistance = laneLength - ownPositionOnLane;

                        }

                        resetNeighbours();

                    } else {
                        myAgent.removeBehaviour(this);
                    }
                } catch (IllegalStateException i) {
//                    System.out.println("Connection is closed");
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        addBehaviour(tickerBehaviour); //TODO: tutaj

        addBehaviour(prepareReceiverBehaviour());
    }

    private CyclicBehaviour prepareReceiverBehaviour() {
        return new CyclicBehaviour(this) {
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
        };
    }

    private void parseMessage(SimpleMessage sm) {
        switch (sm.getEvent()) {
            case DefaultAgentMessages.DESTROY: {
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

//            System.out.println();
//            System.out.print(vehicleId + " is on lane with index: " + ownLaneIndex + " and is circled by: " + Arrays.toString(frontVehicles)
//            + ", " + Arrays.toString(backVehicles));
//            System.out.println();

        } catch (IllegalStateException i) {
//                    System.out.println("Connection is closed");
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetNeighbours() {
        frontVehicles = new String[5];
        backVehicles = new String[5];
    }

    private double calculateStopDistance(double velocity) {
        return velocity * REACTION_TIME + (Math.pow(velocity, 2) / (2 * FRICTION_COEFFICIENT * Constants.STANDARD_GRAVITY));
    }

    private double calculateMaximumVelocity(double stopDistance) {

        double delta = Math.pow(REACTION_TIME, 2) - 4 * (1 / (2 * FRICTION_COEFFICIENT * Constants.STANDARD_GRAVITY)) * (-stopDistance);

        if (delta < 0) {
            return 0.0;
        } else if (delta == 0) {
            return ((- REACTION_TIME)/(2 * FRICTION_COEFFICIENT * Constants.STANDARD_GRAVITY));
        } else {

            double velocity1 = (- REACTION_TIME - Math.sqrt(delta))/(2 * (1 / (2 * FRICTION_COEFFICIENT * Constants.STANDARD_GRAVITY)));
            double velocity2 = (- REACTION_TIME + Math.sqrt(delta))/(2 * (1 / (2 * FRICTION_COEFFICIENT * Constants.STANDARD_GRAVITY)));

            if (velocity1 > 0) {
                return velocity1;
            } else if (velocity2 > 0) {
                return  velocity2;
            } else {
                return 0.0;
            }
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

    private String findTrafficlightIdForLane(String laneId) throws Exception {
        SumoStringList trafficlightsIds = (SumoStringList) conn.do_job_get(Trafficlight.getIDList());
        for (String tlId : trafficlightsIds) {
            SumoStringList controlledLanesIds =  (SumoStringList) conn.do_job_get(Trafficlight.getControlledLanes(tlId));
            if (controlledLanesIds.contains(laneId)) {
                return tlId;
            }
        }

        return null;
    }

    private LightPhase getLightPhaseOfTrafficlightOnLane(String laneId) throws  Exception { //TODO:
        String trafficlightId = findTrafficlightIdForLane(laneId);
        if (trafficlightId != null) {
            String state = (String) conn.do_job_get(Trafficlight.getRedYellowGreenState(trafficlightId));

            SumoNextTLS tls = (SumoNextTLS) conn.do_job_get(Vehicle.getNextTLS(vehicleId));

            for(SumoNextTLS.NextTLS temp: tls.ll) {
                System.out.println(temp.tlsID);
            }
//            SumoLinkList links = (SumoLinkList) conn.do_job_get(Lane.getLinks(laneId));
//            for (SumoLink link : links) {
//                System.out.println(link.isOpen);
//            }

            System.out.println("Current phase index: " + tls);
            switch (state) {
//                case 0:
//                    return LightPhase.GREEN;
//                case 1:
//                    return LightPhase.YELLOW;
//                case 2:
//                    return LightPhase.RED;
                default:
                    return LightPhase.OTHER;
            }
        } else {
            return LightPhase.OTHER;
        }
    }
}
