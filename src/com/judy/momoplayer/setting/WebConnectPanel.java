/*
 * WebConnectPanel.java
 *
 * Created on 2007年12月25日, 下午7:32
 */
package com.judy.momoplayer.setting;

import javax.swing.JOptionPane;

import com.judy.momoplayer.util.Config;

/**
 *
 * @author  judy
 */
public class WebConnectPanel extends javax.swing.JPanel implements Initable {

    /**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 8340424654555375353L;
	/** Creates new form WebConnectPanel */
    public WebConnectPanel() {
        initComponents();
        init();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        noProxy = new javax.swing.JRadioButton();
        useProxy = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        host = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        port = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        userName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        pwd = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(Config.getResource("WebConnectPanel.proxyServer"))); // NOI18N

        buttonGroup1.add(noProxy);
        noProxy.setSelected(true);
        noProxy.setText(Config.getResource("WebConnectPanel.noProxy")); // NOI18N
        noProxy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noProxyActionPerformed(evt);
            }
        });

        buttonGroup1.add(useProxy);
        useProxy.setText(Config.getResource("WebConnectPanel.useProxy")); // NOI18N
        useProxy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useProxyActionPerformed(evt);
            }
        });

        jLabel5.setText(Config.getResource("WebConnectPanel.server")); // NOI18N

        host.setEnabled(false);

        jLabel6.setText(Config.getResource("WebConnectPanel.port")); // NOI18N

        port.setEnabled(false);

        jLabel7.setText(Config.getResource("WebConnectPanel.userName")); // NOI18N

        userName.setEnabled(false);

        jLabel8.setText(Config.getResource("WebConnectPanel.pwd")); // NOI18N

        pwd.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noProxy)
                    .addComponent(useProxy)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userName))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(host, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pwd, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(port)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(noProxy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useProxy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(host, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(userName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(pwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton9.setText(Config.getResource("save")); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton9);

        jButton10.setText(Config.getResource("reset")); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton10);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void noProxyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noProxyActionPerformed
        // TODO add your handling code here:
        host.setEnabled(false);
        port.setEnabled(false);
        userName.setEnabled(false);
        pwd.setEnabled(false);
    }//GEN-LAST:event_noProxyActionPerformed

    private void useProxyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useProxyActionPerformed
        // TODO add your handling code here:
        host.setEnabled(true);
        port.setEnabled(true);
        userName.setEnabled(true);
        pwd.setEnabled(true);
    }//GEN-LAST:event_useProxyActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        doSave();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        init();
        
    }//GEN-LAST:event_jButton10ActionPerformed

    private void doSave() {
        Config config = Config.getConfig();
        @SuppressWarnings("unused")
		int portNumber = -1;
        try {
            if (useProxy.isSelected()) {
                portNumber = Integer.parseInt(port.getText().trim());
            }
        } catch (Exception exe) {
            JOptionPane.showMessageDialog(null, Config.getResource("setting.invalidInput"));
            return;
        }
        if (noProxy.isSelected()) {
            config.setUseProxy(false);
        } else if (useProxy.isSelected()) {
            config.setUseProxy(true);
            config.setProxyHost(host.getText().trim());
            config.setProxyPort(port.getText().trim());
            config.setProxyUserName(userName.getText() == null || userName.getText().trim().equals("") ? null : userName.getText().trim());
            String psd = new String(pwd.getPassword());
            config.setProxyPwd(psd == null || psd.trim().equals("") ? null : psd.trim());
        }
        if (config.isAutoCloseDialogWhenSave()) {
            config.getOptionDialog().setVisible(false);
        }
    }

    public void init() {
        Config config = Config.getConfig();
        noProxy.setSelected(!config.isUseProxy());
        useProxy.setSelected(config.isUseProxy());
        host.setText(config.getProxyHost());
        port.setText(config.getProxyPort());
        userName.setText(config.getProxyUserName());
        pwd.setText(config.getProxyPwd());
        boolean b = config.isUseProxy();
        host.setEnabled(b);
        port.setEnabled(b);
        userName.setEnabled(b);
        pwd.setEnabled(b);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField host;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton noProxy;
    private javax.swing.JTextField port;
    private javax.swing.JPasswordField pwd;
    private javax.swing.JRadioButton useProxy;
    private javax.swing.JTextField userName;
    // End of variables declaration//GEN-END:variables
}
