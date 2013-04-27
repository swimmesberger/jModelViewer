// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ModelViewerApplet.java

package org.fseek.thedeath.modelview;

import java.io.InputStream;

interface FileRequester
{

    public abstract void RequestComplete(Object obj, int i, InputStream inputstream, String s);

    public abstract void RequestFailed(Object obj, int i, String s);
}