import java.util.ArrayList;
import java.util.List;

class JsonFile implements Comparable<JsonFile> {
    String model;
    String osName;
    long sessionStartTime;
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