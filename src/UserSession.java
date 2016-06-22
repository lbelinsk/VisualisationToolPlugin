import java.text.SimpleDateFormat;
import java.util.*;

public class UserSession
{
    String model;
    String OSName;
    String vendor;
    String OSVer;
    String netType;
    long sessionStartTime;
    List<UserAction> actions = new ArrayList<>();

    public UserSession(JsonFile jsonFile)
    {
        model = jsonFile.model;
        OSName = jsonFile.OSName;
        vendor = jsonFile.vendor;
        OSVer = jsonFile.OSVer;
        netType = jsonFile.netType;
        sessionStartTime = jsonFile.sessionStartTime;
        actions.addAll(jsonFile.actions);
        Collections.sort(actions);
    }

    public void mergeWith(List<UserAction> otherActions){
        this.actions.addAll(otherActions);
        Collections.sort(actions);
    }

    @Override
    public String toString()
    {
        Date date = new Date(sessionStartTime);
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy (H:mm:ss)");
        return ft.format(date);
    }
}
