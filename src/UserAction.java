import org.jetbrains.annotations.NotNull;
import sun.plugin.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

class UserAction implements Comparable<UserAction>
{
    String name;
    long id;
    //Path imagePath;
    //ImageIcon image;
    Image image;
    int duration;
    int uaSeq;
    long startTime;
    String ctxName;
    Object info;

    @Override
    public int compareTo(@NotNull UserAction otherAction) {
        if (this.uaSeq < otherAction.uaSeq) {
            return -1;
        } else if (this.uaSeq > otherAction.uaSeq) {
            return 1;
        } else return 0;
    }

    @Override
    public String toString()
    {
        String title;
        if (ctxName.isEmpty() && name.isEmpty())
            title = "title unknown";
        else
            title = ctxName + ": " + name;

        return title;
    }

    //@SuppressWarnings("UndesirableClassUsage")
    //TODO: consider storing small icons as members for faster rendering. (maybe the delay is from BufferedImage usage)
//    ImageIcon getSmallIcon()
//    {
//        ImageIcon newIcon;
//        if (image == null)
//        {
//            BufferedImage emptyImg = new BufferedImage(30, 40, BufferedImage.TYPE_INT_ARGB);
//            newIcon = new ImageIcon(emptyImg);
//        }
//        else
//        {
//            Image newImg = image.getImage().getScaledInstance(30, 40, java.awt.Image.SCALE_SMOOTH);
//            newIcon = new ImageIcon(newImg);
//        }
//        return newIcon;
//    }

    //TODO: improve the getIcon methods.
//    ImageIcon getIcon()
//    {
//        if (image == null)
//        {
//            BufferedImage emptyImg = new BufferedImage(30, 40, BufferedImage.TYPE_INT_ARGB);
//            return new ImageIcon(emptyImg);
//        }
//        else
//        {
//            return image;
//        }
//    }

}
