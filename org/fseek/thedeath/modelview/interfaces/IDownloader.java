package org.fseek.thedeath.modelview.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public interface IDownloader
{
    public InputStream downloadWithProgress(URLConnection con)throws IOException;
    public File getCacheDir();
    public boolean isCachingEnabled();
    public String getContentPath();
}
