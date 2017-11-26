/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package examples.texteditor;

/** About dialog is used to show information about this application.
 */
public class About extends javax.swing.JDialog {

    /** About constructor.
     * It creates modal dialog and displays it.
     */
    public About(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jTextField1 = new javax.swing.JTextField();

        setTitle("About");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        getAccessibleContext().setAccessibleName("About Dialog");
        getAccessibleContext().setAccessibleDescription("About dialog.");
        jTextField1.setEditable(false);
        jTextField1.setText("Ted the Text Editor.");
        getContentPane().add(jTextField1, java.awt.BorderLayout.CENTER);
        jTextField1.getAccessibleContext().setAccessibleName("About Text");
        jTextField1.getAccessibleContext().setAccessibleDescription("About text.");

    }//GEN-END:initComponents

    /** This method is called when the dialog is closed.
     * @param evt WindowEvent instance passed from windowClosing event.
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
