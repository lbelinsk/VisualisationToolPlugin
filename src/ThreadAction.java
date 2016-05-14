import java.nio.file.Path;

/**
 * Created by Sandro on 5/7/2016.
 */
class ThreadAction
{
    String name;
    long id;
    Path imagePath;
    int duration;
    int uaSeq;
    long startTime;
    String ctxName;
    Object info;

    public int getUaSeq()
    {
        return this.uaSeq;
    }

}
