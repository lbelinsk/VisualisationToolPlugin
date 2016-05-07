import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandro on 5/7/2016.
 */
class ThreadInfo
{
    String model;
    String osName;
    long sessionStartTime;
    List<ThreadAction> actions;

    ThreadInfo()
    {
        this.model = "";
        this.osName = "";
        this.sessionStartTime = 0;
        this.actions = new ArrayList<>();
    }
}
