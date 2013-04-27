package org.fseek.thedeath.modelview;

import org.fseek.thedeath.modelview.interfaces.IDownloader;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

class FileLoader extends Thread
{
    private IDownloader downloader;
    
    private FileRequester requester;
    private int id;
    private Object origin;
    private URL url;
    private String cacheFileName;
    private File cacheFile;
    private URLConnection wowheadstream;
    

    FileLoader(int i, FileRequester r, String f, IDownloader downloader)
    {
        this.downloader = downloader;
        if(downloader == null){
            throw new NullPointerException("You have to define the downloader first. (e.g. FileLoader.setDownloader(IDownloader))");
        }
        cacheFileName = f;
        this.origin = r;
        this.requester = r;
        this.id = i;
    }

    FileLoader(int i, Object o, FileRequester r, String f, IDownloader downloader)
    {
        this.downloader = downloader;
        if(downloader == null){
            throw new NullPointerException("You have to define the downloader first. (e.g. FileLoader.setDownloader(IDownloader))");
        }
        cacheFileName = f;
        this.origin = o;
        this.requester = r;
        this.id = i;
    }
    
    private void setURL(String path, boolean force) throws FileNotFoundException
    {
        InputStream openStream;
        try
        {
            String urlFile = path.replace(" ", "%20");
            this.cacheFileName = path;
            boolean flag = false;
            if(cacheFile == null)
            {
                if(downloader.isCachingEnabled())
                {
                    cacheFile = new File(downloader.getCacheDir().getAbsolutePath() + File.separator + cacheFileName); 
                }
            }
            if(cacheFile != null)
            {
                flag = cacheFile.exists();
            }
            if(flag == false || force == true)
            {
                url = new URL(downloader.getContentPath() + urlFile);
                URLConnection openConnection = url.openConnection();
                this.wowheadstream = openConnection;
            }
        } 
        catch (FileNotFoundException ex)
        {
            throw ex;
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run()
    {
        InputStream stream = null;
        try
        {
            
            try
            {
                stream = getStream(!downloader.isCachingEnabled());
                this.requester.RequestComplete(this.origin, this.id, stream, this.cacheFileName);
            }
            catch(FileNotFoundException ex)
            {
                throw new Exception("Req failed...: " + ex.getMessage());
            }
            catch(Exception ex)
            {
                if(id == 1004)
                {
                    throw new Exception("Try new download...");
                }
                ex.printStackTrace();
                if(stream != null)stream.close();
                stream = getStream(true);
                this.requester.RequestComplete(this.origin, this.id, stream, this.cacheFileName);
            }
        } 
        catch (Exception ex)
        {
            if(stream != null)
            {
            try
            {
                stream.close();
            } catch (IOException ex1)
            {
                Logger.getLogger(FileLoader.class.getName()).log(Level.SEVERE, null, ex1);
            }
            }
            this.requester.RequestFailed(this.origin, this.id, this.cacheFileName);
            ex.printStackTrace();
        }
    }

    private InputStream getStream(boolean force) throws IOException
    {  
        if(force == true)
        {
            return getWoWHeadStream();
        }
        cacheFile = new File(downloader.getCacheDir().getAbsolutePath() + File.separator + cacheFileName);
        InputStream fileStream = cacheFile(cacheFile, null);
        if (fileStream == null)
        {
            fileStream = cacheFile(cacheFile, getWoWHeadStream());
        }
        return fileStream;
    }
    
    private InputStream getWoWHeadStream() throws IOException
    {
        if(wowheadstream == null)
        {
            setURL(cacheFileName, true);
        }
        InputStream downloadWithProgress = downloader.downloadWithProgress(wowheadstream);
        System.out.println("Downloading file: " + url.toString());
        return downloadWithProgress;
    }

    private void checkCache(File cacheFile)
    {
        String absolutePath = cacheFile.getAbsolutePath();
        int index = absolutePath.lastIndexOf(File.separator);
        File dirs = null;
        if (index != -1)
        {
            String path = absolutePath.substring(0, index);
            dirs = new File(path);
        }
        if (dirs != null && dirs.exists() == false)
        {
            dirs.mkdirs();
        }
    }

    private InputStream cacheFile(File cacheFile, InputStream stream) throws IOException
    {
        
        checkCache(cacheFile);
        if (cacheFile.exists())
        {
            System.out.println("Loading file from cache: " + cacheFile.getAbsolutePath());
            return new FileInputStream(cacheFile);
        }
        else
        {
            if (stream != null)
            {
                System.out.println("Copy file into cache: " + cacheFile.getAbsolutePath());
                //create FileOutputStream object for destination file
                FileOutputStream fout = new FileOutputStream(cacheFile);
                IOUtils.copy(stream, fout);
                //close the streams
                fout.flush();
                stream.close();
                fout.close(); 
                return cacheFile(cacheFile, null);
            }
        }
        return null;
    }
}