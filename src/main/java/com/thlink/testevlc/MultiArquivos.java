
package com.thlink.testevlc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiArquivos 
{
    private File folder = null;
    private List<File> folderFiles = new ArrayList<>();
    private int inx = 0;
    private boolean endASAP;
    
    public int getInx() {
        return inx;
    }
    private boolean over;
      
    public File getFolder() 
    {
        return folder;
    }

    public void setFolder (File folder) 
    {
        this.folder = folder;
        if (folder.isDirectory())
        {
            List<String> lstExt = new ArrayList<>();
            lstExt.add("avi");
             lstExt.add("MP4");
            folderFiles = listFilesFromPath(folder, lstExt);
            over = false;
            endASAP = false;
            debugList("INI", folderFiles);
        }
        else
        {
            folderFiles.add(folder);
            over = true;
        }
        inx = 0;
        
    }
    private void debugList (String pMsg, List<File> pList)
    {
        System.out.printf("[%s]:\n", pMsg);
        for (int i = 0; i < pList.size(); i++)
        {
            System.out.printf("\t%s. \n", pList.get(i).getName());
        }
        System.out.printf("\n");
    }
    
    private boolean eliminaAnterior (String pFileName)
    {
        System.out.println("eliminaAnterior " + pFileName);
        for (int i = 0; i < folderFiles.size(); i++)
        {
            File f = folderFiles.get(0);
            if (f.getName().equals(pFileName))
            {
                folderFiles.remove(inx);
                debugList("RES", folderFiles);
                return true;
            }
            folderFiles.remove(inx);
            debugList("DEL", folderFiles);
            
        }
        return true;
    }
    
    public boolean resumeList (String pFileName)
    {
        String pasta = pFileName.substring(0, pFileName.lastIndexOf("\\")+1);
        String name = pFileName.replace(pasta, "");
        File fPasta = new File(pasta);
        if (fPasta.exists() && fPasta.isDirectory())
        {
            setFolder(fPasta);
            eliminaAnterior(name);
            return true;
        }
        return false;
    }
    
    public File getNext ()
    {
        if (inx < folderFiles.size())
        {
            File toWatch = folderFiles.get(inx);
            System.out.printf("[%s][%s] toWatch: %s.\n", over ? "S" : "N", inx, toWatch.getName());
            return toWatch;
        }
        System.out.println("OVER!");
        over = true;
        return null;
            
    }
    
    public File getLast ()
    {
        if (inx > 0)
        {
            inx--;
            File toWatch = folderFiles.get(inx);
            System.out.printf("[%s][%s] toWatch: %s.\n", over ? "S" : "N", inx, toWatch.getName());
            return toWatch;
        }
        return null;
    }

    public int getSize ()
    {
        return folderFiles.size();
    }
    
    public void inc ()
    {
        inx++;
        if (inx >= folderFiles.size())
        {
            inx = folderFiles.size()-1;
            over = true;
        }
    }
    
    public void dec ()
    {
        inx--;
        if (inx < 0)
            inx = 0;
    }
    
    public boolean isBOF ()
    {
        return inx == 0;
    }
    public boolean isEOF ()
    {
      //return inx >= folderFiles.size()-1;
        return over;
    }

    //Funcoes
    public List<File> listFilesFromPath (File pPath, List<String> lstExt)
    {
        List<File> lst = new ArrayList<>();
        try
        {
          //File diretorio = new File(pPath);
            File fList[] = pPath.listFiles();
            if (fList == null)
                return lst;
            else
            {
                for (File file : fList)
                {
                    for (String ext : lstExt)
                    {
                        if (file.getName().toLowerCase().endsWith(ext.toLowerCase()))
                            lst.add(file);
                    }
                }
            }
            debugList("PRE", lst);
            Collections.sort(lst);
            debugList("POS", lst);
        } catch (Exception e)
        {
            System.out.println("listFilesFromPath " + pPath + " ERROR: " + e.toString());
        }
        return lst;
    }

    public boolean isEndASAP() {
        return endASAP;
    }

    public void setEndASAP(boolean endASAP) {
        this.endASAP = endASAP;
    }
    
    
}
