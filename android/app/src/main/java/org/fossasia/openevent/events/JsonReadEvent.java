package org.fossasia.openevent.events;

/**
 * Created by Manan Wason on 13/07/16.
 */
public class JsonReadEvent {
    private String name;
    private String json;

    public JsonReadEvent(String name, String json) {
        this.name = name;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
