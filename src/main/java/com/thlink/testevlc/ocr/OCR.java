
package com.thlink.testevlc.ocr;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR 
{
    private static Tesseract instance;
    private static boolean horc;

    public boolean isHorc()
    {
        return horc;
    }

    public static void setHorc(boolean pHorc)
    {
        horc = pHorc;
        instance = Tesseract.getInstance();
        instance.setHocr(pHorc);
    }
    
  /*public HOCRFormat getDataFromHOCR (String pData)
    {
        HOCRFormat hOcr = new HOCRFormat();
        System.out.printf("pData: \n%s. \n", pData);
        String toFind = "span class='ocrx_word' id='word_";
        int pIni = 0;
        hOcr.setText("");
        while(true)
        {
            int pos1 = pData.indexOf(toFind, pIni);
            if (pos1 <= 0)
            {
                System.out.printf("getDataFromHOCR: string 1 não encontrado. pos = %d. \n\n%s\n\n", pos1, pData);
                break;
            }
            //System.out.printf("pos1 = %d. \n", pos1);
            String newData = pData.substring(pos1 + toFind.length()+15);
            //System.out.printf("newData = %s. \n", newData);
            int pos2 = newData.indexOf("\">");
            if (pos2 <= 0)
            {
                System.out.printf("getDataFromHOCR: string 2 não encontrado. pos = %d. \n\n%s\n\n", pos2, pData);
                return null;
            }

            String coord = newData.substring(0, pos2);
            //System.out.printf("coord = [%s]. \n", coord);

            String[] partes =  coord.split(" ");
            //Procura xIni
            if (partes.length != 4)
            {
                System.out.printf("Coordenadas não encontradas. %d. \n", partes.length);
                return null;
            }
            hOcr.setxIni(Integer.parseInt(partes[0]));
            hOcr.setyIni(Integer.parseInt(partes[1]));
            hOcr.setxFim(Integer.parseInt(partes[2]));
            hOcr.setyFim(Integer.parseInt(partes[3]));

            int pos3 = newData.indexOf(">");
            int pos4 = newData.indexOf("</span>");
            String text = newData.substring(pos3 + 1, pos4);
            //System.out.printf("1. text= %s. \n", text);
            text = text.replace("<strong>", "");
            text = text.replace("</strong>", "");
            //System.out.printf("2. text= %s. \n", text);
            hOcr.setText(hOcr.getText().concat(text));
            pIni = pos1+1;
        }
        return hOcr;
    }*/
    
    
  /*public OCR ()
    {
        try {
            if (instance == null)
                instance = Tesseract.getInstance();
        } catch (Exception ex)
        {
            System.out.printf("ERRO. %s. \n", ex.toString());
        }
        
    }*/
    
    public String runOCR (String pFile) 
    {
        return runOCR(pFile, null, null);
    }
    public static String runOCR (String pFile, Rectangle pRect, String pLang ) 
    {
        return runOCR (pFile, pRect, pLang, "");
    }
    
    public static String runOCR (String pFile, Rectangle pRect, String pLang, String pWhiteList) 
    {
        String workDir = System.getProperty("user.dir");
        System.out.println("runOCR:user.dir = " + workDir);
        System.setProperty("jna.library.path", workDir);
        File imageFile = new File(pFile);
        if (!imageFile.exists())
        {
            System.out.printf("Arquivo de imagem não existe.\n");
            return null;
        }
        if (instance == null)
        {
            System.out.printf("instance == null. ERRO.\n");
            return null;
        }
            
        String result;
        try
        {
            IIORegistry registry = IIORegistry.getDefaultInstance();  
            registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());  
            registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());  
            if (pLang != null)
                instance.setLanguage(pLang);

            if (!pWhiteList.equals(""))
            {
                //System.out.printf("instance.setTessVariable(tessedit_char_whitelist, %s)\n", pWhiteList);
                instance.setTessVariable("tessedit_char_whitelist", pWhiteList);
            }
            else
                instance.setTessVariable("tessedit_char_whitelist", "");
            
            if (pRect != null)
            {
                System.out.printf("runOCR at %4.2f, %4.2f - %4.2f / %4.2f. \n", pRect.getX(), pRect.getY(), pRect.getWidth(), pRect.getHeight());
                result = instance.doOCR(imageFile, pRect);
            }
            else
                result = instance.doOCR(imageFile);
            
            
            //System.out.printf("1. result = %s. \n", result);
            result = cleanInvalidChars(result);
            //System.out.printf("2. result = %s. \n", result);
            return result;
        } catch (TesseractException ex)
        {
            System.out.printf("Tess4J ERROR: %s.\n", ex.toString());
            instance = null;
        }
        return null;
    }    
    
    
    public static BufferedImage getBIFromFile ( String pFile ) 
    {
      //BufferedImage img = null;
        try {
            return  ImageIO.read(new File(pFile));
        } catch (IOException e) 
        {
            System.out.printf("Erro na leitura do arquivo %s. [%s].\n", pFile, e.toString());
            return null;
        }
    }
            
    public String runOCROnBI (BufferedImage bi) 
    {
        return runOCROnBI(bi, null, null);
    }
    public String runOCROnBI (BufferedImage bi, Rectangle pRect, String pLang ) 
    {
        return runOCROnBI (bi, pRect, pLang, "");
    }
    
    public static String runOCROnBI (BufferedImage bi, Rectangle pRect, String pLang, String pWhiteList) 
    {
        String workDir = System.getProperty("user.dir");
        System.out.println("runOCROnBI:user.dir = " + workDir);
        System.setProperty("jna.library.path", workDir);
        
        if (instance == null)
        {
            System.out.printf("instance == null. ERRO.\n");
            return null;
        }
            
        String result;
        try
        {
            IIORegistry registry = IIORegistry.getDefaultInstance();  
            registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());  
            registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());  
            if (pLang != null)
                instance.setLanguage(pLang);

            if (!pWhiteList.equals(""))
            {
                //System.out.printf("instance.setTessVariable(tessedit_char_whitelist, %s)\n", pWhiteList);
                instance.setTessVariable("tessedit_char_whitelist", pWhiteList);
            }
            else
                instance.setTessVariable("tessedit_char_whitelist", "");
            
            if (pRect != null)
            {
                System.out.printf("   runOCROnBI at %4.2f, %4.2f - %4.2f / %4.2f. \n", pRect.getX(), pRect.getY(), pRect.getWidth(), pRect.getHeight());
                result = instance.doOCR(bi, pRect);
            }
            else
                result = instance.doOCR(bi);
            
            
            //System.out.printf("1. result = %s. \n", result);
            result = cleanInvalidChars(result);
            //System.out.printf("2. result = %s. \n", result);
            return result;
        } catch (TesseractException ex)
        {
            System.out.printf("Tess4J ERROR: %s.\n", ex.toString());
            instance = null;
        }
        return null;
    }    
    
    
    public static String cleanInvalidChars (String pText)
    {
        char lChar;
        int lVal;
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < pText.length(); i++)
        {
            lChar = pText.charAt(i);    
            lVal = lChar;
            
            if(lChar < 65533)
            {
                if (lChar != 0x0A && lChar != '.' && lChar != '-')  //&& lChar != ' ' deixa o espaço
                    sb.append(lChar);
            }
            else
            {
                //System.out.printf("lChar=%s. lVal = %d\n", lChar, lVal);
                //sb.append("*");
            }
        }
        return sb.toString();
    }
    
    public String getName (String pText) 
    {
        String lName;
        int lPos = pText.indexOf("Aluno:");
        if (lPos > 0)
        {
            lName = pText.substring(lPos+6);
            lPos = lName.indexOf("Matr");
            if (lPos > 0)
            {
                lName = lName.substring(0, lPos-1);
                if (lName.charAt(0) == ' ')
                    lName = lName.substring(1);
                System.out.printf("1.Name = %s.\n", lName);
                lName = lName.replaceAll("\n", "");
                System.out.printf("2.Name = %s.\n", lName);
                lName = lName.replaceAll("\r", "");
                System.out.printf("3.Name = %s.\n", lName);
                lName = lName.replaceAll("\t", "");
                System.out.printf("4.Name = %s.\n", lName);
                return lName;
            }
        }
        return "";
    }
  
    public String getMatricula (String pText) 
    {
        String lName;
        int lPos = pText.indexOf("cula:");
        if (lPos > 0)
        {
            lName = pText.substring(lPos+6);
            lPos = lName.indexOf("Disciplin");
            if (lPos > 0)
            {
                lName = lName.substring(0, lPos-1);
                if (lName.charAt(0) == ' ')
                    lName = lName.substring(1);
                System.out.printf("1.Name = %s.\n", lName);
                lName = lName.replaceAll("\n", "");
                System.out.printf("2.Name = %s.\n", lName);
                lName = lName.replaceAll("\r", "");
                System.out.printf("3.Name = %s.\n", lName);
                lName = lName.replaceAll("\t", "");
                System.out.printf("4.Name = %s.\n", lName);
                return lName;
            }
        }
        return "";
    }
    
    
}
