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
package org.netbeans.test.subversion.main.delete;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.Operator.DefaultStringComparator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.test.subversion.operators.SourcePackagesNode;
import org.netbeans.test.subversion.operators.CheckoutWizardOperator;
import org.netbeans.test.subversion.operators.CommitOperator;
import org.netbeans.test.subversion.operators.RepositoryStepOperator;
import org.netbeans.test.subversion.operators.VersioningOperator;
import org.netbeans.test.subversion.operators.WorkDirStepOperator;
import org.netbeans.test.subversion.utils.MessageHandler;
import org.netbeans.test.subversion.utils.RepositoryMaintenance;
import org.netbeans.test.subversion.utils.TestKit;

/**
 *
 * @author pvcs
 */
public class RefactoringTest extends JellyTestCase {

    public static final String TMP_PATH = "/tmp";
    public static final String REPO_PATH = "repo";
    public static final String WORK_PATH = "work";
    public static final String PROJECT_NAME = "JavaApp";
    public File projectPath;
    public PrintStream stream;
    String os_name;
    Operator.DefaultStringComparator comOperator;
    Operator.DefaultStringComparator oldOperator;
    static Logger log;

    /**
     * Creates a new instance of RefactoringTest
     */
    public RefactoringTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        System.out.println("### " + getName() + " ###");
        if (log == null) {
            log = Logger.getLogger(TestKit.LOGGER_NAME);
            log.setLevel(Level.ALL);
            TestKit.removeHandlers(log);
        } else {
            TestKit.removeHandlers(log);
        }

    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(RefactoringTest.class).addTest(
                "testRefactoring").enableModules(".*").clusters(".*"));
    }

    public void testRefactoring() throws Exception {

        MessageHandler mh = new MessageHandler("Checking out");
        log.addHandler(mh);

        TestKit.closeProject(PROJECT_NAME);
        if (TestKit.getOsName().indexOf("Mac") > -1) {
            new NewProjectWizardOperator().invoke().close();
        }

        JTableOperator table;
        stream = new PrintStream(new File(getWorkDir(), getName() + ".log"));
        VersioningOperator vo = VersioningOperator.invoke();
        comOperator = new Operator.DefaultStringComparator(true, true);
        oldOperator = (DefaultStringComparator) Operator.getDefaultStringComparator();
        Operator.setDefaultStringComparator(comOperator);
        CheckoutWizardOperator co = CheckoutWizardOperator.invoke();
        Operator.setDefaultStringComparator(oldOperator);
        RepositoryStepOperator rso = new RepositoryStepOperator();

        //create repository...
        File work = new File(TMP_PATH + File.separator + WORK_PATH + File.separator + "w" + System.currentTimeMillis());
        new File(TMP_PATH).mkdirs();
        work.mkdirs();
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + REPO_PATH));
        RepositoryMaintenance.createRepository(TMP_PATH + File.separator + REPO_PATH);
        RepositoryMaintenance.loadRepositoryFromFile(TMP_PATH + File.separator + REPO_PATH, getDataDir().getCanonicalPath() + File.separator + "repo_dump");
        rso.setRepositoryURL(RepositoryStepOperator.ITEM_FILE + RepositoryMaintenance.changeFileSeparator(TMP_PATH + File.separator + REPO_PATH, false));

        rso.next();
        WorkDirStepOperator wdso = new WorkDirStepOperator();
        wdso.setRepositoryFolder("trunk/" + PROJECT_NAME);
        wdso.setLocalFolder(work.getCanonicalPath());
        wdso.checkCheckoutContentOnly(false);
        wdso.finish();
        //open project

        TestKit.waitText(mh);

        NbDialogOperator nbdialog = new NbDialogOperator("Checkout Completed");
        JButtonOperator open = new JButtonOperator(nbdialog, "Open Project");
        open.push();

        TestKit.waitForScanFinishedSimple();

        mh = new MessageHandler("Refreshing");
        TestKit.removeHandlers(log);
        log.addHandler(mh);
        Node node = new Node(new SourcePackagesNode(PROJECT_NAME), "");
        node.performPopupAction("Subversion|Show Changes");

        TestKit.waitText(mh);

        node = new Node(new SourcePackagesNode(PROJECT_NAME), "javaapp");
        node.select();
        node.performPopupActionNoBlock("Refactor|Rename...");
        NbDialogOperator dialog;
        new EventTool().waitNoEvent(5000);

        dialog = new NbDialogOperator("Rename");

        new EventTool().waitNoEvent(5000);
        JTextFieldOperator txt = new JTextFieldOperator(dialog);
        txt.setText("javaapp_ren");
        JButtonOperator btn = new JButtonOperator(dialog, "Refactor");
        btn.push();
        dialog.waitClosed();
        Thread.sleep(2000);

        vo = VersioningOperator.invoke();
        String[] expected = new String[]{"Main.java", "Main.java", "javaapp", "javaapp_ren"};
        String[] actual = new String[vo.tabFiles().getRowCount()];
        for (int i = 0; i < vo.tabFiles().getRowCount(); i++) {
            actual[i] = vo.tabFiles().getValueAt(i, 0).toString().trim();
        }
        int result = TestKit.compareThem(expected, actual, false);
        assertEquals("Wrong files in Versioning View", 4, result);

        expected = new String[]{"Locally Deleted", "Locally Added", "Locally Deleted", "Locally Added"};
        actual = new String[vo.tabFiles().getRowCount()];
        for (int i = 0; i < vo.tabFiles().getRowCount(); i++) {
            actual[i] = vo.tabFiles().getValueAt(i, 1).toString().trim();
        }
        result = TestKit.compareThem(expected, actual, false);
        assertEquals("Wrong status in Versioning View", 4, result);

//            mh = new MessageHandler("Refreshing");
//            TestKit.removeHandlers(log);
//            log.addHandler(mh);

        node = new SourcePackagesNode(PROJECT_NAME);
        CommitOperator cmo = CommitOperator.invoke(node);

//            TestKit.waitText(mh);

        mh = new MessageHandler("Committing");
        TestKit.removeHandlers(log);
        log.addHandler(mh);

        expected = new String[]{"Main.java", "Main.java", "javaapp", "javaapp_ren"};
        actual = new String[cmo.tabFiles().getRowCount()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = cmo.tabFiles().getValueAt(i, 1).toString();
        }
        result = TestKit.compareThem(expected, actual, false);
        assertEquals("Wrong files in Commit dialog", 4, result);

        expected = new String[]{"Locally Deleted", "Locally Added", "Locally Deleted", "Locally Added"};
        actual = new String[cmo.tabFiles().getRowCount()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = cmo.tabFiles().getValueAt(i, 2).toString();
        }
        result = TestKit.compareThem(expected, actual, false);
        assertEquals("Wrong status in Commit dialog", 4, result);
        cmo.commit();

        TestKit.waitText(mh);

        Exception e = null;
        try {
            Thread.sleep(2000);
            vo = VersioningOperator.invoke();
            table = vo.tabFiles();
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull("Unexpected behavior - Versioning view should be empty!!!", e);

        /*
         * e = null; try { node = new Node(new SourcePackagesNode(PROJECT_NAME),
         * "javaapp|Main.java"); node.select(); } catch (Exception ex) { e = ex;
         * } assertNotNull("Unexpected behavior - File shouldn't be in
         * explorer!!!", e);
         *
         */

        TestKit.closeProject(PROJECT_NAME);

    }
}
