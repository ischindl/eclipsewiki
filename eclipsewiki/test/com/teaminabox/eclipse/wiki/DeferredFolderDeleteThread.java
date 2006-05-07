package com.teaminabox.eclipse.wiki;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class DeferredFolderDeleteThread extends Thread
{
    private ArrayList folderList = new ArrayList();
    
    public synchronized void add(File fileOrFolder)
    {
        folderList.add(fileOrFolder);
    }
    
    public void run()
    {
        synchronized (this)
        {
            Iterator iterator = folderList.iterator();
            while (iterator.hasNext())
            {
                File fileOrFolder = (File)iterator.next();
                FolderUtils.deleteFileStructure(fileOrFolder);
                iterator.remove();
            }
        }
    }
}
