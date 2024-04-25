
package com.thlink.testevlc;

import com.portolink.sicentities.geral.OperadorAB;
import com.sun.jna.NativeLibrary;
import com.thlink.testevlc.Props.AppProps;
import com.thlink.testevlc.Props.CfgHelper;
import com.thlink.testevlc.db.PersBil;
import com.thlink.testevlc.db.PersMaker;
import com.thlink.testevlc.entities.FR_Usuario;
import com.thlink.testevlc.login.LoginDialog;
import com.thlink.testevlc.ocr.OCR;
import java.awt.Rectangle;
import java.io.File;
import uk.co.caprica.vlcj.binding.RuntimeUtil;

public class App
{
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "./Tools/VLC";

    public static void testOCR ()
    {
      //OCR ocr = new OCR();
        String fileName = "C:\\WorkDir\\TesteVLC\\Midia\\Embarcado\\2707_1499992_20180717_032726_000002.tif";
        OCR.setHorc(false);
        Rectangle rect = new Rectangle(63, 0, 62, 24);
        
        String result = OCR.runOCR(fileName, rect, "ENG", "149");
        System.out.println("result = " + result);
        System.exit(0);
    }
    public static void main (String[] args)
    {
      //testOCR();
        System.setProperty("jna.library.path", NATIVE_LIBRARY_SEARCH_PATH);
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        System.setProperty("jna.dump_memory", "true");
    
        File folderAmostra = new File("./Amostras");
        if (!folderAmostra.exists())
            folderAmostra.mkdirs();
        
        CfgHelper cH = new CfgHelper();
        if (!cH.loadCFG())
        {
            System.out.println("Erro na leitura das propriedades.");
            return;
        }
        
        if (!PersMaker.startupPersistence())
        {
            System.out.println("Erro na conexão ao banco.");
            return;
        }

        if (false)
        {
            LoginDialog lD = new LoginDialog(null, true);
            OperadorAB o;

            while (true)
            {
                o = lD.solicita();
                if (o != null)
                {
                    FR_Usuario u = PersMaker.avaliaCredencial(o);
                    if (u != null)
                    {
                        AppProps.setLogin(o.getNome());
                        AppProps.setSenha(o.getSenhaCry());
                        break;
                    }
                }
                System.out.println("Tentar outra vez.");
            }
            
        }
        else
        {
            OperadorAB o = new OperadorAB();
            o.setIdOper(218);
            o.setNome("tho");
            o.setSenhaCry("123456");
            FR_Usuario u = PersMaker.avaliaCredencial(o);
            if (u == null)
            {
                System.out.println("Erro no login automático.");
                System.exit(0);
            }
            AppProps.setLogin(o.getNome());
            AppProps.setSenha(o.getSenhaCry());
        }

        PersMaker.shutdownPersistence();
        
        if (!PersBil.startupPersistence())
        {
            System.out.println("Erro na conexao ao banco.");
            return;
        }

        final V2 v2 = new V2();
        v2.go();
    }
}
