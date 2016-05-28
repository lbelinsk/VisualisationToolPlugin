import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

class ThreadsBlobParser
{
    private static final String FAILED_EXTRACTING = "Failed to extract threads info.";
    private static ThreadsBlobParser Instance = new ThreadsBlobParser();

    static ThreadsBlobParser getInstance()
    {
        return Instance;
    }

    List<Thread> parseBlob(long userActionId, String threadsBlob)
    {
        List<Thread> threads = new ArrayList<>();
        String threadsJsonStr = getInfo(threadsBlob);
        if (threadsJsonStr.equals(FAILED_EXTRACTING))
            return null;

        JSONArray threadsArray = new JSONArray(threadsJsonStr);
        for (int i = 0; i < threadsArray.length(); i++)
        {
            JSONObject obj = threadsArray.getJSONObject(i);
            String key = obj.keys().next();
            int threadId = Integer.parseInt(key);
            Thread newThread = new Thread(threadId);

            JSONArray threadActionsArray = obj.getJSONArray(key);
            for (int j = 0; j < threadActionsArray.length(); j++)
            {
                String actionStr = threadActionsArray.getString(j);
                ThreadAction newThreadAction = initThreadAction(userActionId, threadId, actionStr);
                newThread.addAction(newThreadAction);
            }
            threads.add(newThread);
        }

        return threads;
    }

    private String getInfo(String blob)
    {
        String decompressedString = "";
        try
        {
            byte[] blobBytes = decodeBase64(blob);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(blobBytes);
            GZIPInputStream zipStream = new GZIPInputStream(byteStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(zipStream, "UTF-8"));

            String line;
            while ((line = bufferReader.readLine()) != null)
            {
                decompressedString += line;
            }

            zipStream.close();
            byteStream.close();
            bufferReader.close();
        }
        catch (Exception e)
        {
            return FAILED_EXTRACTING;
        }

        return decompressedString;
    }

    private ThreadAction initThreadAction(long userActionId, int threadId, String str)
    {
        String[] items = str.split(";;");
        switch (items[0])
        {
            case "METHOD":
                int methodStart = Integer.parseInt(items[1]);
                int methodDuration = Integer.parseInt(items[2]);
                String methodName = items[3];

                return new ThreadAction(
                        ThreadActionType.METHOD,
                        userActionId,
                        threadId,
                        methodStart,
                        methodDuration,
                        methodName);

            case "NET":
                int netStart = Integer.parseInt(items[1]);
                int netDuration = Integer.parseInt(items[2]);
                String netName = items[3];
                String url = items[4];
                int respStart = Integer.parseInt(items[5]);
                int respCode = Integer.parseInt(items[5]);

                return new ThreadAction(
                        ThreadActionType.NETWORK,
                        userActionId,
                        threadId,
                        netName,
                        netStart,
                        netDuration,
                        url,
                        respStart,
                        respCode
                );

            case "BLOCKING":
                int blockStart = Integer.parseInt(items[1]);
                int blockDuration = Integer.parseInt(items[2]);
                String blockName = items[3];

                return new ThreadAction(
                        ThreadActionType.BLOCKING,
                        userActionId,
                        threadId,
                        blockStart,
                        blockDuration,
                        blockName);
            default:
                throw new IllegalArgumentException("Unknown thread operation: " + items[0]);

        }
    }


}
