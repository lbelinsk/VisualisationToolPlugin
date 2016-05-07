import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandro on 5/7/2016.
 */
public class ThreadInfo
{
    public String model;
    public String osName;
    public long sessionStartTime;
    public List<ThreadAction> actions;

    public ThreadInfo()
    {
        this.model = "";
        this.osName = "";
        this.sessionStartTime = 0;
        this.actions = new ArrayList<>();
    }
}
