import java.util.ArrayList;
import java.util.List;

class JsonFile implements Comparable<JsonFile> {
    String model;
    String vendor;
    String OSName;
    String OSVer;
    String carrier;
    String netType;
    String infuServer;
    String channel;
    String appVerName;
    String appVerCode;
    long sentTime;
    long sessionStartTime;
    long version;
    String deviceId;
    String rumAppKey;
    List<UserAction> actions = new ArrayList<>();

    public List<UserAction> getActions()
    {
        return this.actions;
    }

    @Override
    public int compareTo(JsonFile otherFile) {
        if (this.sessionStartTime < otherFile.sessionStartTime) {
            return 1;
        } else if (this.sessionStartTime > otherFile.sessionStartTime) {
            return -1;
        } else return 0;
    }
}