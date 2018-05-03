package utils;

import java.io.Serializable;

public class SimpleMessage implements Serializable {

    String aid;
    int event;

    public SimpleMessage(String aid, int event) {
        this.aid = aid;
        this.event = event;
    }

    public String getAid() {
        return aid;
    }

    public int getEvent() {
        return event;
    }
}
