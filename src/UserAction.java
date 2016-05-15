import java.nio.file.Path;

/**
 * Created by Sandro on 5/7/2016.
 */
class UserAction implements Comparable<UserAction>
{
    String name;
    long id;
    Path imagePath;
    int duration;
    int uaSeq;
    long startTime;
    String ctxName;
    Object info;

    @Override
    public int compareTo(UserAction otherAction) {
        if (this.uaSeq < otherAction.uaSeq) {
            return -1;
        } else if (this.uaSeq > otherAction.uaSeq) {
            return 1;
        } else return 0;
    }
}
