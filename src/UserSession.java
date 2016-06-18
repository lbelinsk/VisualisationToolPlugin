import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSession
{
    String model;
    String OSName;
    String vendor;
    String OSVer;
    String netType;
    String carrier;
    long sessionStartTime;
    List<UserAction> actions = new ArrayList<>();

    public UserSession(JsonFile jsonFile)
    {
        //TODO: consider initializing remaining params of session from the json
        model = jsonFile.model;
        OSName = jsonFile.OSName;
        vendor = jsonFile.vendor;
        OSVer = jsonFile.OSVer;
        carrier = jsonFile.carrier;
        netType = jsonFile.netType;
        sessionStartTime = jsonFile.sessionStartTime;
        actions.addAll(jsonFile.actions);
        Collections.sort(actions);
    }

    public void mergeWith(List<UserAction> otherActions){
        //TODO: may there be 2 actions of same session with same uaseq? if yes, consider removing duplicate
        this.actions.addAll(otherActions);
        Collections.sort(actions);
    }

    @Override
    public String toString()
    {
        //TODO: consider adding session's identifier (for example date/time)
        String action = (actions.size() == 1) ? " action)" : " actions)";
        return model + " (" + actions.size() + action;
    }
}
