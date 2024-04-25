
package com.thlink.testevlc;

import java.awt.Canvas;
import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;

public class VideoInterface extends javax.swing.JFrame
{
  //private final EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
  //private String mrl;
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private VideoSurface videoSurface;
  //private Canvas videoCanvas;

    
    public VideoInterface() 
    {
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

        videoCanvas = new Canvas();
        videoCanvas.setBackground(Color.red);
        videoCanvas.setSize(720, 350);
        
        initComponents();
        
        videoSurface = factory.videoSurfaces().newVideoSurface(videoCanvas);
        mediaPlayer.videoSurface().set(videoSurface);
        lblNomeArquivo.setText("C:\\WorkDir\\SimpleJavaMovieEditor\\src\\main\\resources\\Swipe_3F_UpDown_SlideShow.avi");
        
    }
    
    public void go ()
    {
        //setContentPane(panMedia);
        setTitle("Teste");
        setLocation(100, 100);
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        //panMedia = mediaPlayerComponent;
    }
    
    private File carregaArquivo  ()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new File("./"));
        int i = fileChooser.showOpenDialog(null);
        if (i == 1)
            return null;
        File arquivo = fileChooser.getSelectedFile();
        return arquivo;
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btCarregavideo = new javax.swing.JButton();
        lblNomeArquivo = new javax.swing.JLabel();
        videoCanvas = new java.awt.Canvas();
        panButtons = new javax.swing.JPanel();
        btPlay = new javax.swing.JToggleButton();
        btPause = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btCarregavideo.setText("Carrega Video");
        btCarregavideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCarregavideoActionPerformed(evt);
            }
        });

        lblNomeArquivo.setText("-");

        videoCanvas.setBackground(new java.awt.Color(102, 255, 255));
        videoCanvas.setMaximumSize(new java.awt.Dimension(600, 600));
        videoCanvas.setMinimumSize(new java.awt.Dimension(600, 600));
        videoCanvas.setName(""); // NOI18N
        videoCanvas.setPreferredSize(new java.awt.Dimension(600, 600));

        panButtons.setBackground(new java.awt.Color(204, 255, 153));

        btPlay.setText("Play");
        btPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPlayActionPerformed(evt);
            }
        });
        panButtons.add(btPlay);

        btPause.setText("Pause");
        btPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPauseActionPerformed(evt);
            }
        });
        panButtons.add(btPause);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNomeArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(videoCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btCarregavideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btCarregavideo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNomeArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(videoCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCarregavideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCarregavideoActionPerformed
        File mediaFile = carregaArquivo();
        if (mediaFile == null)
            return;
        lblNomeArquivo.setText(mediaFile.getName());
    }//GEN-LAST:event_btCarregavideoActionPerformed

    private void btPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPlayActionPerformed
        mediaPlayer.media().play(lblNomeArquivo.getText());
    }//GEN-LAST:event_btPlayActionPerformed

    private void btPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPauseActionPerformed
        mediaPlayer.controls().pause();
    }//GEN-LAST:event_btPauseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCarregavideo;
    private javax.swing.JButton btPause;
    private javax.swing.JToggleButton btPlay;
    private javax.swing.JLabel lblNomeArquivo;
    private javax.swing.JPanel panButtons;
    private java.awt.Canvas videoCanvas;
    // End of variables declaration//GEN-END:variables
}
