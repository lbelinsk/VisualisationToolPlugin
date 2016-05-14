import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandro on 5/7/2016.
 */
class ThreadInfo implements Comparable<ThreadInfo> {
    String model;
    String osName;
    long sessionStartTime;
    List<ThreadAction> actions;

    ThreadInfo() {
        this.model = "";
        this.osName = "";
        this.sessionStartTime = 0;
        this.actions = new ArrayList<>();
    }

    public long getSessionStartTime() {
        return this.sessionStartTime;
    }

    public List<ThreadAction> getActions()
    {
        return this.actions;
    }

    @Override
    public int compareTo(ThreadInfo t) {
        if (this.sessionStartTime > t.getSessionStartTime()) {
            return 1;
        } else if (this.sessionStartTime < t.getSessionStartTime()) {
            return -1;
        }
        // must verify that it is enough to compare only the first uaSeq action in each json
        return this.actions.get(0).getUaSeq() < t.getActions().get(0).getUaSeq() ? 1 :
              (this.actions.get(0).getUaSeq() > t.getActions().get(0).getUaSeq() ? -1 : 0);
    }
}