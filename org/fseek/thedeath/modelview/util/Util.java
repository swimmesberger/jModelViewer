package org.fseek.thedeath.modelview.util;

import java.awt.Component;
import java.awt.Dimension;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.fseek.thedeath.modelview.ModelViewer;
import org.fseek.thedeath.modelview.views.ModelViewerFrame;

public class Util
{   
    private static File mainFile;
    public static File getMainFile()
    {
        if(Util.mainFile != null)return Util.mainFile;
        try
        {
            String path = Util.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File mainFileT = new File(decodedPath);
            String absolutePath = null;
            try
            {
                absolutePath = mainFileT.getCanonicalPath();
                if (absolutePath.contains(".jar"))
                {
                    int index = absolutePath.lastIndexOf(File.separator);
                    absolutePath = absolutePath.substring(0, index);
                }
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage());
                System.exit(1);
            }
            Util.mainFile = new File(absolutePath);
            return Util.mainFile;
        } 
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        catch(Exception ex)
        {
            return new File(".");
        }
        return new File(".");
    }
       
    private static File fileOut;
    public static void loadNativeLibarys()
    {
        File[] nativeFile = getNativeLibJarFile();
        try
        {
            //JarResources gluegenFile = new JarResources(nativeFile[0].getAbsolutePath());
            //JarResources joglFile = new JarResources(nativeFile[1].getAbsolutePath());
            //extract native libs
            getFilesFromJarInput(nativeFile[0]);
            getFilesFromJarInput(nativeFile[1]);
        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "The native libarys can't be loaded this can happen if another instance tries to access the libarys.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static File[] getFilesFromJarInput(File jarFile) throws Exception
    {
        return unzipFileIntoDirectory(jarFile, fileOut);
    }
    
    public static File[] unzipFileIntoDirectory(File archive, File destinationDir) throws Exception
    {

        final int BUFFER_SIZE = 1024;
        ArrayList<File> files = new ArrayList<File>();
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(archive);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        File destFile;
        while ((entry = zis.getNextEntry()) != null)
        {

            String name = entry.getName();
            if(!name.endsWith(".dll") && !name.endsWith(".so") && !name.endsWith(".jnilib"))
            {
                continue;
            }
            destFile = new File(destinationDir, name);

            if (entry.isDirectory())
            {
                destFile.mkdirs();
                continue;
            } else
            {
                int count;
                byte data[] = new byte[BUFFER_SIZE];

                destFile.getParentFile().mkdirs();

                FileOutputStream fos = new FileOutputStream(destFile);
                dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1)
                {
                    dest.write(data, 0, count);
                }

                dest.flush();
                dest.close();
                fos.close();
            }
            files.add(destFile);
        }
        return files.toArray(new File[files.size()]);
    }

    public static File[] getNativeLibJarFile()
    {
        String arch = System.getProperty("os.arch");
        File[] glueJoglFile = new File[2];
        String nativeDir;
        try
        {
            nativeDir = System.getProperty("java.library.path");
        }catch(Exception ex)
        {
            nativeDir = "";
        }
        fileOut = new File(nativeDir);
        if (OSDetector.isWindows() && arch.equals("x86"))
        {
            glueJoglFile[0] = new File(nativeDir + File.separator + "gluegen-rt-natives-windows-i586.jar");
            glueJoglFile[1] = new File(nativeDir + File.separator + "jogl-all-natives-windows-i586.jar");
        } else if (OSDetector.isWindows() && (arch.equals("amd64") || arch.equals("x86_64")))
        {
            glueJoglFile[0] = new File(nativeDir + File.separator + "gluegen-rt-natives-windows-amd64.jar");
            glueJoglFile[1] = new File(nativeDir + File.separator + "jogl-all-natives-windows-amd64.jar");
        } else if (OSDetector.isLinux() && (arch.equals("i386") || arch.equals("x86")))
        {
            glueJoglFile[0] = new File(nativeDir + File.separator + "gluegen-rt-natives-linux-i586.jar");
            glueJoglFile[1] = new File(nativeDir + File.separator + "jogl-all-natives-linux-i586.jar");
        } else if (OSDetector.isLinux() && (arch.equals("amd64") || arch.equals("x86_64")))
        {
            glueJoglFile[0] = new File(nativeDir + File.separator + "gluegen-rt-natives-linux-amd64.jar");
            glueJoglFile[1] = new File(nativeDir + File.separator + "jogl-all-natives-linux-amd64.jar");
        } else if (OSDetector.isMac() && (arch.equals("i386") || arch.equals("x86_64")))
        {
            glueJoglFile[0] = new File(nativeDir + File.separator + "gluegen-rt-natives-macosx-universal.jar");
            glueJoglFile[1] = new File(nativeDir + File.separator + "jogl-all-natives-macosx-universal.jar");
        } else
        {
            throw new UnsupportedOperationException("Unsupported OS !");
        }
        return glueJoglFile;
    }
    
      
    public static ModelViewerFrame createModelViewer(Component parent, int id)
    {
        String idS = String.valueOf(id);
        if(id == -1)
        {
            idS = null;
        }
        final ModelViewerFrame modelViewer = new ModelViewerFrame("http://static.wowhead.com/modelviewer/", idS, "#181818", null, 8, false, null, null, null, null, null, null, null, "ChannelCastDirected");
        modelViewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        modelViewer.setSize(new Dimension(600, 400));
        return modelViewer;
    }
    
    public static ModelViewerFrame showModelViewer(Component parent, int id)
    {
        try
        {
            ModelViewerFrame modelViewer = Util.createModelViewer(parent, id);
            modelViewer.init();
            modelViewer.setLocationRelativeTo(parent); 
            modelViewer.setVisible(true);
            return modelViewer;
        }
        catch(java.lang.NoClassDefFoundError ex)
        {
            JOptionPane.showMessageDialog(null, "Libarys not found!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        catch(UnsatisfiedLinkError ex)
        {
            JOptionPane.showMessageDialog(null, "Native Libary path not set (start argument).", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return null;
    }
    
    public static ModelViewerFrame showModelViewer(Component parent)
    {
        return showModelViewer(parent, -1);
    }
}
