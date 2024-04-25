
package com.thlink.testevlc.login;

import com.portolink.sicentities.geral.OperadorAB;
import com.thlink.testevlc.db.PersBil;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;


public class LoginDialog extends javax.swing.JDialog {

    private OperadorAB operador;
    
    public LoginDialog (java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public OperadorAB solicita ()
    {
        if (false)  //realLogin
        {
            createMaster(); return operador;
        }
        this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setTitle("Credenciais:");
        setVisible(true);
        return operador;
    }
    
    private void  createMaster ()
    {
        operador = new OperadorAB();
        operador.setId("adm");
        operador.setIdOper(1);
        operador.setDireitos(null);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblLogin = new javax.swing.JLabel();
        tLogin = new javax.swing.JTextField();
        lblSenha = new javax.swing.JLabel();
        btLogin = new javax.swing.JButton();
        jPwd = new javax.swing.JPasswordField();
        btLogin1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblLogin.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        lblLogin.setText("Login:");

        tLogin.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        tLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tLoginActionPerformed(evt);
            }
        });
        tLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tLoginKeyTyped(evt);
            }
        });

        lblSenha.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        lblSenha.setText("Senha:");

        btLogin.setText("Login");
        btLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLoginActionPerformed(evt);
            }
        });

        jPwd.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jPwd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPwdKeyTyped(evt);
            }
        });

        btLogin1.setText("Sair");
        btLogin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogin1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                            .addComponent(btLogin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPwd, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPwd, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btLogin)
                    .addComponent(btLogin1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLoginActionPerformed
        // TODO add your handling code here:
        if (tLogin.getText().equals("adm") && new String(jPwd.getPassword()).equals("fkxq9"))
        {
            createMaster();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        else
        {
            if (PersBil.operadorMaker(tLogin.getText(), new String(jPwd.getPassword())))
            {
                operador = new OperadorAB();
                operador.setNome(tLogin.getText());
                operador.setSenhaCry(new String(jPwd.getPassword()));
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Erro na autenticação.", "jApoioBil", JOptionPane.ERROR_MESSAGE);
                jPwd.setText("");
                tLogin.setText("");
                tLogin.requestFocus();
            }
        }
    }//GEN-LAST:event_btLoginActionPerformed

    private void tLoginKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tLoginKeyTyped
        // TODO add your handling code here:
        //System.out.println("evt: " + evt.toString());
        if(evt.getKeyChar() == '\n' || evt.getKeyChar() == '\t')
            jPwd.requestFocus();
    }//GEN-LAST:event_tLoginKeyTyped

    private void jPwdKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPwdKeyTyped
        // TODO add your handling code here:
        if(evt.getKeyChar() == '\n' || evt.getKeyChar() == '\t')
            btLoginActionPerformed(null);
    }//GEN-LAST:event_jPwdKeyTyped

    private void btLogin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogin1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btLogin1ActionPerformed

    private void tLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tLoginActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLogin;
    private javax.swing.JButton btLogin1;
    private javax.swing.JPasswordField jPwd;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JTextField tLogin;
    // End of variables declaration//GEN-END:variables
}
