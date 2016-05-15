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
        //TODO init all params of session from json
        model = jsonFile.model;
        osName = jsonFile.osName;
        sessionStartTime = jsonFile.sessionStartTime;
        actions.addAll(jsonFile.actions);
        Collections.sort(actions);
    }

    public void mergeWith(List<UserAction> otherActions){
        //TODO handle duplicate actions
        this.actions.addAll(otherActions);
        Collections.sort(actions);
    }
}
