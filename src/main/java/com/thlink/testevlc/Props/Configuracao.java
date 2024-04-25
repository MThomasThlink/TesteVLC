package com.thlink.testevlc.Props;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Configuracao {
    String FileName;
    Properties props = new Properties()
    {
        private static final long serialVersionUID = 4112578634029874840L;

        @Override
        public synchronized Enumeration<Object> keys() {
            Comparator<Object> byCaseInsensitiveString = Comparator.comparing(Object::toString,
                            String.CASE_INSENSITIVE_ORDER);

            Supplier<TreeSet<Object>> supplier = () -> new TreeSet<>(byCaseInsensitiveString);

            TreeSet<Object> sortedSet = super.keySet().stream()
                            .collect(Collectors.toCollection(supplier));

            return Collections.enumeration(sortedSet);
        }
    };

    public String getProp (String pProp, String pDefault) 
    {
        String temp = props.getProperty(pProp);
        if (temp == null)
        {
            System.out.printf("[%s] nao encontrada\n", pProp);
            setProp(pProp, pDefault);
            return pDefault;
        }
        else
            return temp;
    }
    
    public void setProp (String pProp, String pValue)
    {
        props.setProperty(pProp, pValue);
        //grava os dados  no arquivo
        try (FileOutputStream fos = new FileOutputStream(getFileName()))
        {
            //grava os dados  no arquivo
            System.out.printf("props.store[%s] = %s. \n", pProp, pValue);
            props.store(fos, "Configurações do SIC-SGG");
        } catch (FileNotFoundException ex)
        {
            System.out.printf("setProp FileNotFoundException %s. \n", ex.toString());
        } catch (IOException ex)
        {
            System.out.printf("setProp IOException %s. \n", ex.toString());
        }
    }
    
    public Boolean setFileName(String pFileName) 
    {
        if (!(new File(pFileName)).exists())
        {
            return Boolean.FALSE;
        }
        this.FileName = pFileName;
        try
        {
            props.load(new FileInputStream(this.FileName));
            return Boolean.TRUE;
        } catch (IOException ex)
        {
            System.out.printf("Configuracao.setFileName ERROR: %s. \n", ex.toString());
            return Boolean.FALSE;
        }
    }
    public String getFileName() {
        return FileName;
    }

    
    public Boolean createPropFile (String pFileName)
    {
        File file = new File(pFileName);
        try
        {
            try (FileOutputStream fos = new FileOutputStream(file))
            {
                props.store(fos, "Configurações do SICApp");
            }
            setFileName(pFileName);
            return Boolean.TRUE;
        }
        catch (IOException ex)
        {
            return Boolean.FALSE;
        }
    }

}
