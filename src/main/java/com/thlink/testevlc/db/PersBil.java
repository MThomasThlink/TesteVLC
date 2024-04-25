
package com.thlink.testevlc.db;

import com.portolink.sicentities.validador.TotalizadorServico;
import com.portolink.sicentities.validador.tbLogWiFi;
import com.thlink.testevlc.Props.AppProps;
import com.thlink.testevlc.entities.FrotaLogAnaliseVideo;
import com.thlink.testevlc.entities.FrotaOcorrenciaIMAGENS;
import com.thlink.testevlc.entities.FrotaTipoOcorrenciaIMAGENS;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PersBil 
{
    private static final Logger logger = Logger.getLogger(PersBil.class);
    private static EntityManagerFactory emf;
     
    public static boolean startupPersistence () 
    {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        Logger.getLogger("org.jboss.logging").setLevel(Level.OFF);

        logger.info("[VLC]Iniciando conexao ao banco SQLServer. ");
        try
        {
            Map properties = new HashMap();
            properties.put("javax.persistence.jdbc.driver", "net.sourceforge.jtds.jdbc.Driver");
            properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
            properties.put("hibernate.show_sql", "false");
          //properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("javax.persistence.jdbc.url", "jdbc:jtds:sqlserver://" + AppProps.getBancoServidor()+ "/" + AppProps.getBancoNome());
            
            properties.put("javax.persistence.jdbc.user", AppProps.getBancoLogin());
            properties.put("javax.persistence.jdbc.password", AppProps.getBancoSenha());
            
          /*logger.info("URL:      " + properties.get("javax.persistence.jdbc.url"));
            logger.info("User:     " + properties.get("javax.persistence.jdbc.user"));
            logger.info("Password: " + properties.get("javax.persistence.jdbc.password"));*/
            
            emf = Persistence.createEntityManagerFactory("SIC_BILHETAGEM.PU", properties);
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
        emf.close();
    }
    
    public static List<FrotaTipoOcorrenciaIMAGENS> getListTipoOcorrenciasAtivas ()
    {
        EntityManager em = emf.createEntityManager();
        String sSQL = "SELECT to FROM FrotaTipoOcorrenciaIMAGENS to " + 
                      "WHERE  to.ativo = 1 " +
                      "ORDER  BY to.descricao ";
        try
        {
            Query q = em.createQuery(sSQL);
            List<FrotaTipoOcorrenciaIMAGENS> x = q.getResultList();
            em.close();
            return x;
        } catch (Exception e)
        {
            if (!e.toString().contains("No entity found for query"))
                logger.error("getListTipoOcorrenciasAtivas ERROR: " + e.toString());
            return null;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }
    
    public static boolean registraOcorrencia (FrotaOcorrenciaIMAGENS pOc)
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            em.persist(pOc);
            em.getTransaction().commit();
            return true;
        } catch (Exception e)
        {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            logger.error("registraOcorrencia ERROR: " + e.toString());
            return false;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }
    
    public static TotalizadorServico buscaServico (String pStrIdcarro, Date pDataRef)
    {
        EntityManager em = emf.createEntityManager();
        String sSQL = "SELECT sv FROM TotalizadorServico sv " + 
                      "WHERE  sv.dataHIni <= :pDataHora AND sv.dataHFim >= :pDataHora AND " +
                      "       sv.idVeiculo = :pIdVeiculo "  +
                      "ORDER  BY sv.id DESC ";
        try
        {
            
            Query q = em.createQuery(sSQL);
            q.setMaxResults(1);
            q.setParameter("pDataHora", pDataRef);
            q.setParameter("pIdVeiculo", pStrIdcarro);
            TotalizadorServico x = (TotalizadorServico)q.getSingleResult();
            em.close();
            return x;
        } catch (Exception e)
        {
            if (!e.toString().contains("No entity found for query"))
                logger.error("buscaServico ERROR: " + e.toString());
            return null;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }
            
    public static tbLogWiFi buscaLogWifi (Date pDateRef, String txtIdCarro, String txtIdG100)
    {
        EntityManager em = emf.createEntityManager();
        String sSQL = "SELECT wf FROM tbLogWiFi wf " + 
                      "WHERE  wf.dataHora BETWEEN :pDateMin AND :pDateMax AND " +
                    //"       wf.macWiFi LIKE :pIdG100 AND " +
                      "       wf.idCarro = :pIdCarro "  +
                      "ORDER  BY wf.id DESC ";
        try
        {
            
            Query q = em.createQuery(sSQL);
            q.setMaxResults(1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(pDateRef);
            cal.add(Calendar.MINUTE, -5);
            Date dateMin = cal.getTime();
            q.setParameter("pDateMin", dateMin);
            
            cal.setTime(pDateRef);
            cal.add(Calendar.MINUTE, 5);
            Date dateMax = cal.getTime();
            q.setParameter("pDateMax", dateMax);
            
          //q.setParameter("pIdG100", txtIdG100);
            q.setParameter("pIdCarro", Integer.parseInt(txtIdCarro));
            
            tbLogWiFi x = (tbLogWiFi)q.getSingleResult();
          //em.close();
            return x;
        } catch (Exception e)
        {
            if (!e.toString().contains("No entity found for query"))
                logger.error("buscaLogWifi ERROR: " + e.toString());
            return null;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }

    public static boolean criaLogAnaliseVideo (FrotaLogAnaliseVideo pFlav)
    {
        logger.info("criaLogAnaliseVideo");
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            em.persist(pFlav);
            em.getTransaction().commit();
            return true;
        } catch (Exception e)
        {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            logger.error("criaLogAnaliseVideo ERROR: " + e.toString());
            for (StackTraceElement stackTrace : e.getStackTrace())
                logger.error("\t" + stackTrace);

            return false;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }
    
    public static FrotaLogAnaliseVideo getLastLAV (String pOper)
    {
        logger.info("getLastLAV");
        EntityManager em = emf.createEntityManager();
        String sSQL = "SELECT lav FROM FrotaLogAnaliseVideo lav " + 
                      "WHERE  lav.idOperador = :pLogin " +
                      "ORDER  BY lav.id DESC ";
        try
        {
            Query q = em.createQuery(sSQL);
            q.setMaxResults(1);
            q.setParameter("pLogin", pOper);
            FrotaLogAnaliseVideo x = (FrotaLogAnaliseVideo)q.getSingleResult();
            return x;
        } catch (Exception e)
        {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            logger.error("getLastLAV ERROR: " + e.toString());
            for (StackTraceElement stackTrace : e.getStackTrace())
                logger.error("\t" + stackTrace);

            return null;
        }
        finally 
        {
            if (em != null)
                if (em.isOpen())
                    em.close();
        }
    }
            
    public static boolean operadorMaker (String pLogin, String pSenha)
    {
        AppProps.setLogin(pLogin);
        AppProps.setSenha(pSenha);
        return true;
    }
    
}
