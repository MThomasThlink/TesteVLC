
package com.thlink.testevlc.db;

import com.portolink.sicentities.geral.OperadorAB;
import com.thlink.testevlc.Props.AppProps;
import com.thlink.testevlc.entities.FR_Usuario;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.log4j.Logger;

public class PersMaker 
{
    private static final Logger logger = Logger.getLogger(PersBil.class);
    private static EntityManagerFactory emf;
    
    
    public static boolean startupPersistence () 
    {

        logger.info("[MAK]Iniciando conexao ao banco SQLServer. ");
        try
        {
            Map properties = new HashMap();
            properties.put("javax.persistence.jdbc.driver", "net.sourceforge.jtds.jdbc.Driver");
            properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
            properties.put("hibernate.show_sql", "false");
          //properties.put("hibernate.hbm2ddl.auto", "update");
          //properties.put("javax.persistence.jdbc.url", "jdbc:jtds:sqlserver://" + AppProps.getBancoServidor()+ "/" + AppProps.getBancoNome());
            properties.put("javax.persistence.jdbc.url", "jdbc:jtds:sqlserver://" + AppProps.getBancoServidor()+ "/" + "sgbMaker3");
            properties.put("javax.persistence.jdbc.user", AppProps.getBancoLogin());
            properties.put("javax.persistence.jdbc.password", AppProps.getBancoSenha());
            
          /*logger.info("URL:      " + properties.get("javax.persistence.jdbc.url"));
            logger.info("User:     " + properties.get("javax.persistence.jdbc.user"));
            logger.info("Password: " + properties.get("javax.persistence.jdbc.password"));*/
            
            emf = Persistence.createEntityManagerFactory("sgbMaker3.PU", properties);
            logger.info("Conexao ok");

            return emf.isOpen();
        } catch (PersistenceException e)
        {
            logger.error("startupPersistence ERROR = " + e.toString());
            return false;
        }
    }

    public static void shutdownPersistence() 
    {
        logger.info("shutdownPersistence@sgbMaker3");
        emf.close();
    }

    private static String md5 (Integer pCodigo, String pSenha)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            String s = String.format("%d%s", pCodigo, pSenha);
            m.update(s.getBytes(), 0, s.length());
            StringBuilder sb = new StringBuilder();
            byte[] res = m.digest();
            for (int i = 0; i < res.length; i++)
                sb.append(String.format("%02X", res[i]));
            return sb.toString();
        } catch (Exception e)
        {
            logger.error("md5 ERROR: " + e.toString());
        }
        return null;
    }
    
    public static FR_Usuario avaliaCredencial (OperadorAB pOp)
    {
        EntityManager em = emf.createEntityManager();
        String sSQL = "SELECT u FROM FR_Usuario u " + 
                      "WHERE  u.USR_LOGIN = :pLogin  ";
        try
        {
            Query q = em.createQuery(sSQL);
            q.setMaxResults(1);
            q.setParameter("pLogin", pOp.getNome());
            FR_Usuario x = (FR_Usuario)q.getSingleResult();
            String usrPwd = md5(x.getUSR_CODIGO(), pOp.getSenhaCry());
            
            if (x.getUSR_SENHA().toUpperCase().equals(usrPwd.toUpperCase()))
                return x;
            return null;
        } catch (Exception e)
        {
            if (!e.toString().contains("No entity found for query"))
                logger.error("avaliaCredencial ERROR: " + e.toString());
            return null;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }

}
