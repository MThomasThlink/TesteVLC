
package com.thlink.testevlc;

import com.portolink.sicentities.validador.TotalizadorServico;
import com.portolink.sicentities.validador.tbLogWiFi;
import com.thlink.testevlc.Props.AppProps;
import com.thlink.testevlc.db.PersBil;
import com.thlink.testevlc.entities.FrotaLogAnaliseVideo;
import com.thlink.testevlc.entities.FrotaOcorrenciaIMAGENS;
import com.thlink.testevlc.entities.FrotaTipoOcorrenciaIMAGENS;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import static uk.co.caprica.vlcj.player.base.State.STOPPED;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;

public class V2 extends javax.swing.JFrame 
{
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private VideoSurface videoSurface;
    private static final Logger logger = Logger.getLogger(PersBil.class);
    private final int CANVAS_WIDTH = 720;   //600;
    private final int CANVAS_HEIGTH = 480;  //600;
    private boolean mousePressedPlaying = false;
    private ScheduledExecutorService executorService;
    private boolean isRegistering = false, isCortando = false;
    private FrotaOcorrenciaIMAGENS oc;
    private final String[] standardMediaOptions = 
    {
        "--rate=1",
        "--audio-filter=normvol",
        "--norm-buff-size=10",
        "--norm-max-level=1"
    };
    private final Color COLOR_BOF = new Color(255,255,204);
    private final Color COLOR_EOF = new Color(255,102,102);
    private final Color COLOR_REG = new Color(240,240,240);
    
    private final MultiArquivos ma = new MultiArquivos();
    long mTempoIni, mTempoFim;
    private int newRate = -1;
    
    public V2() 
    {
        videoCanvas = new Canvas();
        videoCanvas.setBackground(Color.red);
        videoCanvas.setBounds(4, 45, CANVAS_WIDTH, CANVAS_HEIGTH);
      //videoCanvas.setSize(CANVAS_WIDTH, CANVAS_HEIGTH);

        initComponents();
        panRight.setEnabled(false);
        montaComboOcorrencias(PersBil.getListTipoOcorrenciasAtivas());
        ChangeListener listener = new ChangeListener()
        {
            @Override
            public void stateChanged (ChangeEvent event)
            {
               // update text field when the slider value changes
               JSlider source = (JSlider) event.getSource();
             //System.out.println("Changed!! " + source.getValue());
               if (source.getValue() == 0)
               {
                   System.out.println("0");
                   source.setValue(1);
               }
            }
        };
        rateSlider.addChangeListener(listener);
      //logger.info("Fim do construtor");
    }

    public void go ()
    {
        String v = this.getClass().getPackage().getSpecificationVersion();
        if (v != null)
            AppProps.setVersao(v);
        else 
            AppProps.setVersao("AB.CD");
        String strTitle = String.format("Aplicativo de controle de video V: %s Usuário: [%s]", AppProps.getVersao(), AppProps.getLogin());
        setTitle(strTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
      //lblNomeArquivo.setText("C:\\WorkDir\\TesteVLC\\Midia\\2014-05-18 19.47.36.mp4");
      //btPlayActionPerformed(null);
    }
     
    private void iniPlayer (int pRate)
    {
      //logger.info("iniPlayer");
        if (mediaPlayer != null)
        {
          //logger.error("DIFERENTE!!");
            mediaPlayer.release();
            mediaPlayer = null;
          //executorService.shutdown();
        }
        newRate = pRate;
        standardMediaOptions[0] = String.format("--rate=%d", pRate);
        factory = new MediaPlayerFactory(standardMediaOptions);
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
        videoSurface = factory.videoSurfaces().newVideoSurface(videoCanvas);
        mediaPlayer.videoSurface().set(videoSurface);
        mediaPlayer.audio().setMute(false);
        
        timeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(mediaPlayer.status().isPlaying()) {
                    mousePressedPlaying = true;
                    mediaPlayer.controls().pause();
                }
                else {
                    mousePressedPlaying = false;
                }
                setSliderBasedPosition();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setSliderBasedPosition();
                updateUIState();
            }
        });
        executorService = Executors.newSingleThreadScheduledExecutor();
      //executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 1L, TimeUnit.SECONDS);
        if (pRate == 0)
        {
            pRate = 1;
            rateSlider.setValue(pRate);
        }
        executorService.schedule(new UpdateRunnable(mediaPlayer), 1000/pRate, TimeUnit.MILLISECONDS);
    }

    private boolean montaComboOcorrencias (List<FrotaTipoOcorrenciaIMAGENS> pList)
    {
        logger.info("montaComboOcorrencias");
        try
        {
            jCboOcorrencias.removeAllItems();
            for (FrotaTipoOcorrenciaIMAGENS oc : pList)
            {
                jCboOcorrencias.addItem(oc);
            }
            btRegistraOcorrencia.setEnabled(true);
            return true;
        } catch (Exception e)
        {
            btRegistraOcorrencia.setEnabled(false);
        }
        return false;
    }
   
    private File carregaArquivo ()
    {
        logger.info("carregaArquivo");
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filtAVI = new FileNameExtensionFilter("AVI File", "avi", 
                                                         "MP4 File", "mp4",
                                                         "MP3 File", "mp3");
        fc.addChoosableFileFilter(filtAVI);
      //fc.setFileFilter(filtAVI);
        //FileFilter filtMP4 = new FileNameExtensionFilter("MP4 File", "mp4");
        //fc.addChoosableFileFilter(filtMP4);
        //fc.setFileFilter(filtMP4);
    
        fc.setCurrentDirectory(new File("./Midia"));
        int i = fc.showOpenDialog(null);
        if (i == 1)
            return null;
        File arquivo = fc.getSelectedFile();
        return arquivo;
    }
    
    private File carregaPasta ()
    {
        logger.info("carregaPasta");
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        fc.setCurrentDirectory(new File("./Midia"));
        int i = fc.showOpenDialog(null);
        if (i == 1)
            return null;
        File arquivo = fc.getSelectedFile();
        return arquivo;
    }

    private void preencheCampos (File mediaFile)
    {
        if (mediaFile == null)
        {
            txtIdCarro.setText("");
            txtIdLinha.setText("");
            txtData.setText("");
            txtIdG100.setText("");
            txtMatMot.setText("");
            txtTabela.setText("");
            txtHora.setText("");
            chkTs.setSelected(false);
            chkWiFi.setSelected(false);
            return;
        }

        String parts[] = mediaFile.getName().split("_");
        if (parts.length == 4)
        {
            txtIdCarro.setText(String.format("%s", parts[0]));
            txtIdG100.setText(parts[1]);

            txtData.setText(parts[2].substring(6, 8).concat("/").
                     concat(parts[2].substring(4, 6)).concat("/").
                     concat(parts[2].substring(0, 4)));
            txtHora.setText(parts[3].substring(0, 2).concat(":").
                     concat(parts[3].substring(2, 4)).concat(":").
                     concat(parts[3].substring(4, 6)));
        }
        else if (parts.length == 3)
        {
            txtIdCarro.setText("?");
            txtIdG100.setText(parts[0]);
            txtData.setText(parts[1].substring(6, 8).concat("/").
                     concat(parts[1].substring(4, 6)).concat("/").
                     concat(parts[1].substring(0, 4)));
            txtHora.setText(parts[2].substring(0, 2).concat(":").
                     concat(parts[2].substring(2, 4)).concat(":").
                     concat(parts[2].substring(4, 6)));
        }
    }
    
    private boolean preparaArquivo (File mediaFile, boolean playIt)
    {
        if (mediaFile == null)
            return false;
      //logger.info("preparaArquivo: " + mediaFile.getName());
        try
        {
            btCarregaPasta.setText(String.format("Arquivos (%d de %d)", ma.getInx()+1, ma.getSize()));
            lblNomeArquivo.setText(mediaFile.getAbsolutePath());
            //Tenta extrair do nome atributos como idCarro, linha, tabela, idG100, etc.
            preencheCampos(mediaFile);
            //Tenta atualizar a partir de tbLogWiFi
            Date dateRef = convertStrDateTime(txtData.getText(), txtHora.getText());
          //logger.info("Busca tbLogWiFi");
            tbLogWiFi wf = PersBil.buscaLogWifi(dateRef, txtIdCarro.getText(), txtIdG100.getText());
            if (wf != null)
            {
                chkWiFi.setSelected(true);
                if (wf.getIdLinha() != null)
                    txtIdLinha.setText(String.format("%s", wf.getIdLinha()));
                if (wf.getIdTabela() != null)
                    txtTabela.setText(String.format("%s", wf.getIdTabela()));
            }
          //logger.info("Busca Serviço");
            TotalizadorServico sv = PersBil.buscaServico(txtIdCarro.getText(), dateRef);
            if (sv != null)
            {
                chkTs.setSelected(true);
                if (sv.getMatMotorista() != null)
                    txtMatMot.setText(String.format("%d", sv.getMatMotorista()));
            }
            panRight.setEnabled(true);
            if (playIt)
                btPlayActionPerformed(null);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }
    
    private Date convertStrDateTime (String pDate, String pTime)
    {
        try
        {
            Date date1  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(pDate + " " + pTime);  
            return date1;
        } catch (Exception e)
        {
            logger.error("convertStrDateTime ERROR: " + e.toString());
            return null;
        }
    }
    private Date converteStrTimeIntoDate (String pTime, String pPattern)
    {
        try
        {
            Date date1  = new SimpleDateFormat(pPattern).parse(pTime);  
            return date1;
        } catch (Exception e)
        {
            return null;
        }
    }
    private boolean temInfoEssencial ()
    {
        return txtIdLinha.getText().isEmpty() || txtIdCarro.getText().isEmpty() || txtMatMot.getText().isEmpty();
    }
    
    private int getTimeInSecs ()
    {
        int h = Integer.parseInt(timeLabel.getText().substring(0, 2));
        int m = Integer.parseInt(timeLabel.getText().substring(3, 5));
        int s = Integer.parseInt(timeLabel.getText().substring(6, 8));
        return s + m*60 + h*3600;
    }
    private void setSliderBasedPosition() 
    {
        if(!mediaPlayer.status().isSeekable()) {
            return;
        }
        float positionValue = timeSlider.getValue() / 1000.0f;
        // Avoid end of file freeze-up
        if(positionValue > 0.99f) {
            positionValue = 0.99f;
        }
        mediaPlayer.controls().setPosition(positionValue);
    }
    
    private void updateUIState () 
    {
        if(!mediaPlayer.status().isPlaying()) 
        {
            // Resume play or play a few frames then pause to show current position in video
            mediaPlayer.controls().play();
            if(!mousePressedPlaying) {
                try {
                    // Half a second probably gets an iframe
                    Thread.sleep(500);
                }
                catch(InterruptedException e) {
                    // Don't care if unblocked early
                }
                mediaPlayer.controls().pause();
            }
        }
        long time = mediaPlayer.status().time();
        int position = (int)(mediaPlayer.status().position() * 1000.0f);
      //int chapter = mediaPlayer.chapters().chapter();
      //int chapterCount = mediaPlayer.chapters().count();
        updateTime(time);
        updatePosition(position);
      //updateChapter(chapter, chapterCount);
    }
    
    private void updateTime(long millis) {
        String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        timeLabel.setText(s);
    }
    
    private void updatePosition(int value) {
        timeSlider.setValue(value);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panCarga = new javax.swing.JPanel();
        btCarregaPasta = new javax.swing.JButton();
        btCarregaArquivo = new javax.swing.JButton();
        lblNomeArquivo = new javax.swing.JLabel();
        btStop = new javax.swing.JButton();
        btStart = new javax.swing.JButton();
        btLast = new javax.swing.JButton();
        btNext = new javax.swing.JButton();
        chkAuto = new java.awt.Checkbox();
        panRight = new javax.swing.JPanel();
        panData = new javax.swing.JPanel();
        lblCarro = new javax.swing.JLabel();
        txtIdCarro = new javax.swing.JTextField();
        lblLinha = new javax.swing.JLabel();
        txtIdLinha = new javax.swing.JTextField();
        lblData = new javax.swing.JLabel();
        txtData = new javax.swing.JTextField();
        lblG100 = new javax.swing.JLabel();
        txtIdG100 = new javax.swing.JTextField();
        lblMat = new javax.swing.JLabel();
        txtMatMot = new javax.swing.JTextField();
        lblTabela = new javax.swing.JLabel();
        txtTabela = new javax.swing.JTextField();
        lblHora = new javax.swing.JLabel();
        txtHora = new javax.swing.JTextField();
        chkWiFi = new javax.swing.JCheckBox();
        chkTs = new javax.swing.JCheckBox();
        panControls = new javax.swing.JPanel();
        btPlay = new javax.swing.JButton();
        btPause = new javax.swing.JButton();
        rateSlider = new javax.swing.JSlider();
        timeSlider = new javax.swing.JSlider();
        timeLabel = new javax.swing.JLabel();
        btPrintScreen = new javax.swing.JButton();
        btCortar = new javax.swing.JButton();
        panOcorrencias = new javax.swing.JPanel();
        btRegistraOcorrencia = new javax.swing.JButton();
        jCboOcorrencias = new javax.swing.JComboBox();
        jCboCamera = new javax.swing.JComboBox();
        panSair = new javax.swing.JPanel();
        btSair = new javax.swing.JButton();
        panCanvas = new javax.swing.JPanel();
        videoCanvas = new java.awt.Canvas();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panCarga.setBackground(new java.awt.Color(204, 204, 255));
        panCarga.setMaximumSize(new java.awt.Dimension(1070, 82));
        panCarga.setMinimumSize(new java.awt.Dimension(1070, 82));

        btCarregaPasta.setText("Carrega Pasta");
        btCarregaPasta.setMaximumSize(new java.awt.Dimension(100, 30));
        btCarregaPasta.setMinimumSize(new java.awt.Dimension(100, 30));
        btCarregaPasta.setPreferredSize(new java.awt.Dimension(100, 30));
        btCarregaPasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCarregaPastaActionPerformed(evt);
            }
        });

        btCarregaArquivo.setText("Carrega Video");
        btCarregaArquivo.setMaximumSize(new java.awt.Dimension(100, 30));
        btCarregaArquivo.setMinimumSize(new java.awt.Dimension(100, 30));
        btCarregaArquivo.setPreferredSize(new java.awt.Dimension(100, 30));
        btCarregaArquivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCarregaArquivoActionPerformed(evt);
            }
        });

        lblNomeArquivo.setBackground(new java.awt.Color(255, 255, 255));
        lblNomeArquivo.setOpaque(true);

        btStop.setText("STOP");
        btStop.setMaximumSize(new java.awt.Dimension(100, 30));
        btStop.setMinimumSize(new java.awt.Dimension(100, 30));
        btStop.setPreferredSize(new java.awt.Dimension(100, 30));
        btStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStopActionPerformed(evt);
            }
        });

        btStart.setText("START");
        btStart.setMaximumSize(new java.awt.Dimension(100, 30));
        btStart.setMinimumSize(new java.awt.Dimension(100, 30));
        btStart.setPreferredSize(new java.awt.Dimension(100, 30));
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });

        btLast.setText("<<");
        btLast.setMaximumSize(new java.awt.Dimension(100, 30));
        btLast.setMinimumSize(new java.awt.Dimension(100, 30));
        btLast.setPreferredSize(new java.awt.Dimension(100, 30));
        btLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLastActionPerformed(evt);
            }
        });

        btNext.setText(">>");
        btNext.setMaximumSize(new java.awt.Dimension(100, 30));
        btNext.setMinimumSize(new java.awt.Dimension(100, 30));
        btNext.setPreferredSize(new java.awt.Dimension(100, 30));
        btNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNextActionPerformed(evt);
            }
        });

        chkAuto.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        chkAuto.setLabel("Automático");

        javax.swing.GroupLayout panCargaLayout = new javax.swing.GroupLayout(panCarga);
        panCarga.setLayout(panCargaLayout);
        panCargaLayout.setHorizontalGroup(
            panCargaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCargaLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panCargaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btCarregaPasta, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .addComponent(btCarregaArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panCargaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNomeArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panCargaLayout.createSequentialGroup()
                        .addComponent(btStop, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btStart, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btLast, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btNext, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkAuto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panCargaLayout.setVerticalGroup(
            panCargaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCargaLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panCargaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCarregaPasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btStop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(panCargaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNomeArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCarregaArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panRight.setBackground(new java.awt.Color(204, 204, 255));
        panRight.setMaximumSize(new java.awt.Dimension(336, 488));
        panRight.setMinimumSize(new java.awt.Dimension(336, 488));
        panRight.setPreferredSize(new java.awt.Dimension(336, 488));

        lblCarro.setText("Carro:");
        lblCarro.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                lblCarroComponentRemoved(evt);
            }
        });

        lblLinha.setText("Linha:");

        lblData.setText("Data:");

        lblG100.setText("G100:");

        lblMat.setText("Mot:");

        lblTabela.setText("Tab.:");

        lblHora.setText("Hora:");

        txtHora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHoraActionPerformed(evt);
            }
        });

        chkWiFi.setText("WF");

        chkTs.setText("TS");

        javax.swing.GroupLayout panDataLayout = new javax.swing.GroupLayout(panData);
        panData.setLayout(panDataLayout);
        panDataLayout.setHorizontalGroup(
            panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDataLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCarro, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(lblLinha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblG100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtData, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdLinha, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdCarro, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdG100, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHora, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTabela, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMatMot, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panDataLayout.createSequentialGroup()
                        .addComponent(chkWiFi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkTs, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panDataLayout.setVerticalGroup(
            panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCarro)
                    .addComponent(txtIdCarro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMat)
                    .addComponent(txtMatMot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLinha)
                    .addComponent(txtIdLinha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTabela)
                    .addComponent(txtTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblData)
                    .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblHora)
                        .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdG100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblG100)
                    .addComponent(chkWiFi)
                    .addComponent(chkTs))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btPlay.setText("Play");
        btPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPlayActionPerformed(evt);
            }
        });

        btPause.setText("Pause");
        btPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPauseActionPerformed(evt);
            }
        });

        rateSlider.setMajorTickSpacing(2);
        rateSlider.setMaximum(20);
        rateSlider.setMinorTickSpacing(1);
        rateSlider.setPaintLabels(true);
        rateSlider.setPaintTicks(true);
        rateSlider.setSnapToTicks(true);
        rateSlider.setValue(10);
        rateSlider.setMaximumSize(new java.awt.Dimension(231, 41));
        rateSlider.setMinimumSize(new java.awt.Dimension(231, 41));
        rateSlider.setPreferredSize(new java.awt.Dimension(231, 41));

        timeSlider.setMaximum(1000);
        timeSlider.setValue(0);

        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel.setText("-");

        btPrintScreen.setText("PrintScreen");
        btPrintScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintScreenActionPerformed(evt);
            }
        });

        btCortar.setText("Cortar video");
        btCortar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCortarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panControlsLayout = new javax.swing.GroupLayout(panControls);
        panControls.setLayout(panControlsLayout);
        panControlsLayout.setHorizontalGroup(
            panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panControlsLayout.createSequentialGroup()
                        .addComponent(btPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(btPause, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panControlsLayout.createSequentialGroup()
                        .addComponent(btPrintScreen, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(btCortar, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                    .addComponent(rateSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(timeSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panControlsLayout.setVerticalGroup(
            panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPause, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(rateSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(timeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btPrintScreen, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCortar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        btRegistraOcorrencia.setText("Ocorrência");
        btRegistraOcorrencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRegistraOcorrenciaActionPerformed(evt);
            }
        });

        jCboOcorrencias.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jCboCamera.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "1", "2", "3", "4" }));
        jCboCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCboCameraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panOcorrenciasLayout = new javax.swing.GroupLayout(panOcorrencias);
        panOcorrencias.setLayout(panOcorrenciasLayout);
        panOcorrenciasLayout.setHorizontalGroup(
            panOcorrenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panOcorrenciasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panOcorrenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCboOcorrencias, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panOcorrenciasLayout.createSequentialGroup()
                        .addComponent(btRegistraOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jCboCamera, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panOcorrenciasLayout.setVerticalGroup(
            panOcorrenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panOcorrenciasLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panOcorrenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btRegistraOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCboCamera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jCboOcorrencias, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btSair.setText("Sair");
        btSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panSairLayout = new javax.swing.GroupLayout(panSair);
        panSair.setLayout(panSairLayout);
        panSairLayout.setHorizontalGroup(
            panSairLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSairLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btSair, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panSairLayout.setVerticalGroup(
            panSairLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSairLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btSair, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panRightLayout = new javax.swing.GroupLayout(panRight);
        panRight.setLayout(panRightLayout);
        panRightLayout.setHorizontalGroup(
            panRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRightLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panOcorrencias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panControls, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panRightLayout.setVerticalGroup(
            panRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRightLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(panData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(panControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(panOcorrencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(panSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panCanvas.setBackground(new java.awt.Color(153, 255, 153));
        panCanvas.setFocusable(false);
        panCanvas.setMaximumSize(new java.awt.Dimension(728, 488));
        panCanvas.setMinimumSize(new java.awt.Dimension(728, 488));
        panCanvas.setPreferredSize(new java.awt.Dimension(728, 488));

        videoCanvas.setMaximumSize(new java.awt.Dimension(720, 480));
        videoCanvas.setMinimumSize(new java.awt.Dimension(720, 480));
        videoCanvas.setPreferredSize(new java.awt.Dimension(720, 480));

        javax.swing.GroupLayout panCanvasLayout = new javax.swing.GroupLayout(panCanvas);
        panCanvas.setLayout(panCanvasLayout);
        panCanvasLayout.setHorizontalGroup(
            panCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCanvasLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(videoCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(4, Short.MAX_VALUE))
        );
        panCanvasLayout.setVerticalGroup(
            panCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCanvasLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(videoCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(4, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panRight, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(panCarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCarregaArquivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCarregaArquivoActionPerformed
        logger.info("btCarregaArquivo");
        preencheCampos(null);
        try
        {
          //btCarregaArquivo.setEnabled(false);
            chkWiFi.setSelected(false);
            chkTs.setSelected(false);

            File mediaFile = carregaArquivo();
            if (mediaFile == null)
                return;
            ma.setFolder(mediaFile);
            preparaArquivo(mediaFile, true);
          //btPlayActionPerformed(null);
        } catch (Exception e)
        {
            logger.error("btCarregaArquivoActionPerformed ERROR: " + e.toString());
        }
      //btCarregaArquivo.setEnabled(true);
    }//GEN-LAST:event_btCarregaArquivoActionPerformed

    private void btCarregaPastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCarregaPastaActionPerformed
        ma.setFolder(carregaPasta());
        if (ma.getSize() == 0)
        {
            JOptionPane.showMessageDialog(this, "Sem videos nesta pasta.", "", JOptionPane.ERROR_MESSAGE);
            ma.setFolder(null);
            return;
        }
        chkAuto.setState(true);
        btCarregaPasta.setText(String.format("Arquivos (%d de %d)", ma.getInx()+1, ma.getSize()));
        btCarregaPasta.setBackground(COLOR_BOF);
        preparaArquivo(ma.getNext(), true);
        chkAuto.setState(true);
    }//GEN-LAST:event_btCarregaPastaActionPerformed

    private void btSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSairActionPerformed
        if (mediaPlayer != null)
        {
            System.out.println("DIFERENTE!!");
            mediaPlayer.release();
            mediaPlayer = null;
        }
        System.exit(0);
    }//GEN-LAST:event_btSairActionPerformed

    private void jCboCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCboCameraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCboCameraActionPerformed
    
    private void btRegistraOcorrenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRegistraOcorrenciaActionPerformed
        logger.info("btRegistraOcorrenciaActionPerformed");
        if (lblNomeArquivo.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "É necessário ter um video selecionado", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!temInfoEssencial())
        {
            JOptionPane.showMessageDialog(this, "Sem informações essenciais (linha, carro, matMot)", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FrotaTipoOcorrenciaIMAGENS tpOc = (FrotaTipoOcorrenciaIMAGENS)jCboOcorrencias.getSelectedItem();
        if (tpOc == null)
        {
            JOptionPane.showMessageDialog(this, "É necessário selecionar um tipo de ocorrência", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isRegistering)
        {
            logger.info("btRegistraOcorrenciaActionPerformed registrando.");
            isRegistering = true;
            btRegistraOcorrencia.setText("Registrando...");
            btRegistraOcorrencia.setBackground(Color.red);
            oc = new FrotaOcorrenciaIMAGENS();
            oc.setIdOperador(AppProps.getLogin());

            if (!txtIdCarro.getText().isEmpty())
                oc.setCarro(Integer.parseInt(txtIdCarro.getText()));
            oc.setLinha(txtIdLinha.getText());
            oc.setNomeArquivo(lblNomeArquivo.getText());
            if (!jCboCamera.getSelectedItem().toString().equals("-"))
                oc.setIdCam(Integer.parseInt(jCboCamera.getSelectedItem().toString()));
            oc.setHoraIni(converteStrTimeIntoDate(timeLabel.getText(), "hh:MM:ss"));
        }
        else
        {
            logger.info("btRegistraOcorrenciaActionPerformed salvando.");
            btRegistraOcorrencia.setText("Salvando...");
            btRegistraOcorrencia.setBackground(new Color(240, 240, 240));
            oc.setDataHoraInsercao(Calendar.getInstance().getTime());
            oc.setHoraFim(converteStrTimeIntoDate(timeLabel.getText(), "hh:MM:ss"));
            oc.setData(converteStrTimeIntoDate(txtData.getText(), "dd/MM/yyyy"));
            oc.setCodOcorrencia(tpOc.getCodigo());
            if (!txtMatMot.getText().isEmpty())
                oc.setMatMot(Integer.parseInt(txtMatMot.getText()));

            if (!txtTabela.getText().isEmpty())
                oc.setTabela(Integer.parseInt(txtTabela.getText()));

            if (!PersBil.registraOcorrencia(oc))
            {
                JOptionPane.showMessageDialog(this, "Erro no registro da ocorrência", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Ocorrência registrada com sucesso", "", JOptionPane.INFORMATION_MESSAGE);

            isRegistering = false;
            btRegistraOcorrencia.setText("Registra Ocorrência");
        }

    }//GEN-LAST:event_btRegistraOcorrenciaActionPerformed

    private void lblCarroComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_lblCarroComponentRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_lblCarroComponentRemoved

    private void btCortarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCortarActionPerformed
        logger.info("btCortarActionPerformed");
        if (!isCortando)
        {
            mTempoIni = getTimeInSecs();
            System.out.println("mTempoIni: " + mTempoIni);
            btCortar.setBackground(COLOR_EOF);
        }
        else
        {
            mTempoFim = getTimeInSecs();
            System.out.println("mTempoFim: " + mTempoFim);
            if (!lblNomeArquivo.getText().isEmpty())
            {
                File f = new File(lblNomeArquivo.getText());
                if (f.exists())
                {
                    MediaPlayerFactory lFactory = new MediaPlayerFactory();
                    MediaPlayer lMP = lFactory.mediaPlayers().newMediaPlayer();

                    String mediaFileName = "./Amostras/".concat(lblNomeArquivo.getText().substring(lblNomeArquivo.getText().lastIndexOf("\\")+1, lblNomeArquivo.getText().lastIndexOf(".")-1));
                    String mediaFileExt  = lblNomeArquivo.getText().substring(lblNomeArquivo.getText().lastIndexOf("."));

                    String arg0 = String.format(":start-time=%d", mTempoIni);
                    String arg1 = String.format(":stop-time=%d",  mTempoFim);
                    String arg2 = String.format(":sout=#file{dst=%s_%04d_%04d%s}", mediaFileName, mTempoIni, mTempoFim, mediaFileExt);
                  //String arg3 = "--audio-filter=normvol";
                    System.out.println(arg0);
                    System.out.println(arg1);
                    System.out.println(arg2);
                    lMP.audio().setMute(false);
                    if (!lMP.media().play(lblNomeArquivo.getText(), arg0, arg1, arg2))
                        JOptionPane.showMessageDialog(null, "Erro na geração do video.", "", JOptionPane.ERROR_MESSAGE);
                    btCortar.setBackground(COLOR_REG);
                    }
                }
            }
            isCortando = !isCortando;
    }//GEN-LAST:event_btCortarActionPerformed

    private void btPrintScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintScreenActionPerformed
        String snapFileName = lblNomeArquivo.getText().substring(lblNomeArquivo.getText().lastIndexOf("\\")+1);
            snapFileName = "./Amostras/".concat(snapFileName).concat("_").concat(timeLabel.getText().replace(":", "")).concat(".png");
            //System.out.println("snapFileName: " + snapFileName);
            File snapFile = new File(snapFileName);
            if (!mediaPlayer.snapshots().save(snapFile))
            System.out.println("Erro ao salvar arquivo: " + snapFileName);
    }//GEN-LAST:event_btPrintScreenActionPerformed

    private void btPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPauseActionPerformed
        mediaPlayer.controls().pause();
    }//GEN-LAST:event_btPauseActionPerformed

    private void btPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPlayActionPerformed
        if (!lblNomeArquivo.getText().isEmpty())
        {
            File f = new File(lblNomeArquivo.getText());
            if (f.exists())
            {
                iniPlayer(rateSlider.getValue());
                mediaPlayer.media().play(lblNomeArquivo.getText());
            }
        }
    }//GEN-LAST:event_btPlayActionPerformed

    private void txtHoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHoraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHoraActionPerformed

    private void btStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStopActionPerformed
        chkAuto.setState(false);
        ma.setEndASAP(true);
        btStop.setBackground(COLOR_EOF);
    }//GEN-LAST:event_btStopActionPerformed

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        FrotaLogAnaliseVideo lastVideo = PersBil.getLastLAV(AppProps.getLogin());
        if (lastVideo != null)
        {
            ma.resumeList(lastVideo.getNomeArquivo());
            if (!ma.isEOF())
            {
                chkAuto.setState(true);
                btNextActionPerformed(null);
            }
        }
        else
            JOptionPane.showMessageDialog(null, "Sem arquivos para continuar.", "", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_btStartActionPerformed

    private void btLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLastActionPerformed
        ma.dec();
        if (!ma.isEOF())
        {
            logger.info("Last!");
            preparaArquivo(ma.getNext(), true);
        } 
    }//GEN-LAST:event_btLastActionPerformed

    private void btNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNextActionPerformed
        //ma.inc();
        if (!ma.isEOF())
        {
            logger.info("Next!");
            preparaArquivo(ma.getNext(), true);
        } 
    }//GEN-LAST:event_btNextActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCarregaArquivo;
    private javax.swing.JButton btCarregaPasta;
    private javax.swing.JButton btCortar;
    private javax.swing.JButton btLast;
    private javax.swing.JButton btNext;
    private javax.swing.JButton btPause;
    private javax.swing.JButton btPlay;
    private javax.swing.JButton btPrintScreen;
    private javax.swing.JButton btRegistraOcorrencia;
    private javax.swing.JButton btSair;
    private javax.swing.JButton btStart;
    private javax.swing.JButton btStop;
    private java.awt.Checkbox chkAuto;
    private javax.swing.JCheckBox chkTs;
    private javax.swing.JCheckBox chkWiFi;
    private javax.swing.JComboBox jCboCamera;
    private javax.swing.JComboBox jCboOcorrencias;
    private javax.swing.JLabel lblCarro;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblG100;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblLinha;
    private javax.swing.JLabel lblMat;
    private javax.swing.JLabel lblNomeArquivo;
    private javax.swing.JLabel lblTabela;
    private javax.swing.JPanel panCanvas;
    private javax.swing.JPanel panCarga;
    private javax.swing.JPanel panControls;
    private javax.swing.JPanel panData;
    private javax.swing.JPanel panOcorrencias;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel panSair;
    private javax.swing.JSlider rateSlider;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JSlider timeSlider;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtHora;
    private javax.swing.JTextField txtIdCarro;
    private javax.swing.JTextField txtIdG100;
    private javax.swing.JTextField txtIdLinha;
    private javax.swing.JTextField txtMatMot;
    private javax.swing.JTextField txtTabela;
    private java.awt.Canvas videoCanvas;
    // End of variables declaration//GEN-END:variables

    private final class UpdateRunnable implements Runnable {

        private final MediaPlayer embMediaPlayer;

        private UpdateRunnable(MediaPlayer embMediaPlayer) {
            this.embMediaPlayer = embMediaPlayer;
        }

        private Integer cnvStrToInt (String pString)
        {
            try
            {
                return Integer.parseInt(pString);
            } catch (Exception e)
            {
                return null;
            }
        }
        
        @Override
        public void run() 
        {
            final long time = mediaPlayer.status().time();
            final int position = (int)(mediaPlayer.status().position() * 1000.0f);
            SwingUtilities.invokeLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                  //System.out.println("mediaPlayer.status().state() = " + mediaPlayer.status().state());
                    if (mediaPlayer.status().state() == STOPPED)
                    {
                        System.out.println("STOPPED");
                        FrotaLogAnaliseVideo flav = new FrotaLogAnaliseVideo();
                        flav.setIdOperador(AppProps.getLogin());
                        flav.setNomeArquivo(lblNomeArquivo.getText());
                        flav.setLinha(cnvStrToInt(txtIdLinha.getText()));
                        flav.setCarro(cnvStrToInt(txtIdCarro.getText()));
                        flav.setTabela(cnvStrToInt(txtTabela.getText()));
                        flav.setMatMot(cnvStrToInt(txtMatMot.getText()));
                        flav.setRate(rateSlider.getValue());
                        flav.setDataHoraInsercao(Calendar.getInstance().getTime());
                        if (!PersBil.criaLogAnaliseVideo(flav))
                        {
                            JOptionPane.showMessageDialog(null, "Erro na geração do log.", "", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        executorService.shutdownNow();
                        if (ma.isEndASAP())
                        {
                            logger.info("Finalizando.");
                            btStop.setBackground(COLOR_REG);
                            return;
                        }
                        ma.inc();
                        if (!ma.isEOF() && chkAuto.getState())
                        {
                            logger.info("RECALL!");
                            preparaArquivo(ma.getNext(), true);
                        }
                        else
                        {
                            btCarregaPasta.setText("Carrega Pasta");
                            btCarregaPasta.setBackground(COLOR_REG);
                        }
                        return;
                    }
                    
                    if(mediaPlayer.status().isPlaying()) 
                    {
                        if (newRate != rateSlider.getValue())
                        {
                            newRate = rateSlider.getValue();
                            mediaPlayer.controls().setRate(newRate);
                            logger.info("newRate: " + newRate);
                        }
                      //System.out.println("mediaPlayer.status().length() = " + mediaPlayer.status().length());
                      //System.out.println("mediaPlayer.status().time() = " + mediaPlayer.status().time());
                        updateTime(time);
                        updatePosition(position);
                      /*if (mediaPlayer.status().time() > mediaPlayer.status().length())
                        {
                            System.out.println("shutdown");
                            executorService.shutdown();
                          //mInx++;
                          //btCarregaPastaActionPerformed(null);
                        }*/
                      //updateChapter(chapter, chapterCount);
                    }
                  //else
                  //    System.out.println("!playing");
                  //executorService.schedule(new UpdateRunnable(mediaPlayer), 1L, TimeUnit.SECONDS);
                    if (rateSlider.getValue() == 0)
                    {
                        rateSlider.setValue(1);
                    }
                    executorService.schedule(new UpdateRunnable(mediaPlayer), 1000/rateSlider.getValue(), TimeUnit.MILLISECONDS);
                }
            });
        }
    }
}
