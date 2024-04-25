
package com.thlink.testevlc.Props;

import org.apache.log4j.Logger;

public class CfgHelper 
{
    private Configuracao cfgSIC;
    public static final String APP_PROPS = "TesteVLC.properties";
  //private final SicServicosCfg cfg = new SicServicosCfg();
    private static final Logger logger = Logger.getLogger(CfgHelper.class);
    
    public boolean loadCFG ()
    {
        //String sTemp;
        cfgSIC = new Configuracao();
        String lFile = System.getProperty("user.dir") + "/" + APP_PROPS;

        if (!cfgSIC.setFileName(lFile))
        {
            if (!cfgSIC.createPropFile(lFile))
                return false;
            //setDefaultSettings(cfgSIC);
        }
        AppProps.setId(Integer.parseInt(cfgSIC.getProp("idTesteVLC", "0")));
        logger.info(String.format("Inicializando TesteVLC: id=%d.", AppProps.getId()));
        
        AppProps.setBancoServidor(cfgSIC.getProp("ServidorBanco", "."));
        AppProps.setBancoNome    (cfgSIC.getProp("NomeBanco", "Bilhetagem"));
        AppProps.setBancoLogin   (cfgSIC.getProp("LoginBanco", "sa"));
        AppProps.setBancoSenha   (cfgSIC.getProp("SenhaBanco", "sbdpu2001"));

        return true;
    }
}
