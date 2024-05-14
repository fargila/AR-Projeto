package threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import main.ProjectAR;

public class ImageDownloadingThread extends Thread {

    private final String[] urls, names;
    private final String name;
    private final int index;
    private HttpURLConnection conn;
    private InputStream is;
    private BufferedInputStream bis;
    private URL aURL;
    private Bitmap bm, tmp;
    private File f;
    private final String savedData;
    private boolean sequencial;

    public ImageDownloadingThread(String[] paths, String filename,
                                  String savingPath, int indice) {
        super();
        urls = paths;
        name = filename;
        tmp = null;
        f = null;
        index = indice;
        savedData = savingPath;
        names = null;
    }

    public ImageDownloadingThread(String[] paths, String filename,
                                  String savingPath) {
        super();
        urls = paths;
        name = filename;
        tmp = null;
        f = null;
        index = 0;
        savedData = savingPath;
        sequencial = true;
        names = null;
    }

    public ImageDownloadingThread(String[] paths, String[] filenames,
                                  String savingPath) {
        super();
        urls = paths;
        name = filenames[0];
        names = filenames;
        tmp = null;
        f = null;
        index = 0;
        savedData = savingPath;
        sequencial = false;
    }

    @Override
    public void run() {
        int i = 0;
        String absolutePath;
        for (String url : urls)
        {
            if (urls.length == 1)
                absolutePath = savedData + name + ".png";
            else
            {
                if (sequencial)
                    absolutePath = savedData + name + i + ".png";
                else {
                    assert names != null;
                    absolutePath = savedData + names[i] + ".png";
                }
            }

            f = new File(absolutePath);

            if (!f.exists() && url != null)
            {

                try {
                    tmp = getImageBitmap(url);

                    FileOutputStream fileOutputStream = new FileOutputStream(
                            absolutePath);

                    BufferedOutputStream bos = new BufferedOutputStream(
                            fileOutputStream);

                    tmp.compress(Bitmap.CompressFormat.PNG, 50, bos);

                    bos.flush();

                    bos.close();

                    Message m = new Message();
                    m.arg2 = index;
                    ProjectAR.getInstance().imageHandler.sendMessage(m);

                } catch (Exception e) {
                    f.delete();
                } finally {
                    if (tmp != null)
                        tmp.recycle();
                    tmp = null;
                    System.gc();
                }

            }
            else if (f.exists())
            {
                Message m = new Message();
                m.arg2 = index;
                ProjectAR.getInstance().imageHandler.sendMessage(m);
            }
            i++;

        }

    }

    private Bitmap getImageBitmap(String url) {
        bm = null;
        try {

            aURL = new URL(url);
            conn = (HttpURLConnection) aURL.openConnection();
            conn.setConnectTimeout(0);
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();

        } catch (IOException e) {
            Log.e("Image", "Error getting bitmap", e);
            bis = null;
            is = null;
            conn = null;
            aURL = null;
            bm = null;
        } finally {
            assert conn != null;
            conn.disconnect();
        }

        return bm;
    }

}
