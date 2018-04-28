package main;

import agents.HelloAgent;
import managers.AgentsEnvironmentManager;

public class Main {

    private static AgentsEnvironmentManager aem = new AgentsEnvironmentManager();

    public static void main(String[] args) {
        startMAS();
    }

    private static void startMAS() {
        try {
            aem.startContainer();
            aem.addRemoteMonitoringAgent();
        } catch (Exception e) {
            System.out.println("Agents container failure");
            System.exit(0);
        } finally {
            aem.addAgentToMainContainer("Hello agent", HelloAgent.class.getName(), null);
        }
    }
}
