import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSession
{
    String model;
    String osName;
    long sessionStartTime;
    List<UserAction> actions = new ArrayList<>();

    public UserSession(JsonFile jsonFile)
    {
        //TODO: consider initializing remaining params of session from the json
        model = jsonFile.model;
        osName = jsonFile.OSName;
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
        return model + " (" + actions.size() + " actions)";
    }
}
