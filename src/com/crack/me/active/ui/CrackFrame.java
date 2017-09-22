package com.crack.me.active.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.crack.me.active.Crack;
import com.crack.me.active.Crackable;
import com.crack.me.active.RSAKey;
import com.crack.me.active.ui.core.MeReplacer;
import com.crack.me.active.ui.core.PropertyWriter;

/**
 * Cracker.JFrame
 * @author FK.Terminator
 */
public class CrackFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = -4884505908308759981L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private StringBuffer helpMessage = new StringBuffer()//
	    .append("Fllow Orders.\n")//
	    .append("0. Close MyEclipse Application(if you wanna replace jar file).\n")//
	    .append("1. Fill usercode\n")//
	    .append("2. Fill systemid\n")//
	    .append("    *. myeclipse active dialog show\n")//
	    .append("    *. Press Button to Generate id\n")//
	    .append("3. Tools->RebuildKey {Create or Replace [private/public]Key.bytes (if not exists in current folder).}\n")//
	    .append("4. Press Active Buttom\n")//
	    .append("belows not options\n")//
	    .append("5. Tools->ReplaceJarFile\n")//
	    .append("    *. choose MyEclipseFolder->Common->plugins \n")//
	    .append("    *. it will replace SignatureVerifier.class and publicKey.bytes in jars under plugins folder, opreation will do together.\n")//
	    .append("6. Tools->SaveProperites\n")//
	    .append("    *. MyEclipse Startup Will Read This File to Active Product.\n")//
	    .append("7. Open MyEclipse Application.\n\n")//
    ;

    JComboBox cbLicenceType = new JComboBox(new Object[] {//
	    "PROFESSIONAL", //
		    "STANDARD", //
		    "BLUE",//
		    "SPRING"//
	    });

    JTextField tfUsercode = new JTextField();
    JTextField tfSystemId = new JTextField();
    JButton jbGenerateSi = new JButton("SystemId...");
    JButton jbActive = new JButton("Active");
    final JTextArea taInfo = new JTextArea(helpMessage.toString());

    JMenuItem generateSystemIdMenu = new JMenuItem("GenerateSystemId...");
    JMenuItem runMenu = new JMenuItem("RunActive");
    JMenuItem rebuildKeyMenu = new JMenuItem("0.RebuildKey...");
    JMenuItem replaceClassMenu = new JMenuItem("1.ReplaceJarFile...");
    JMenuItem saveProperitiesMenu = new JMenuItem("2.SaveProperities");
    JMenuItem exitMenu = new JMenuItem("Exit");

    private javax.swing.filechooser.FileFilter dirctoryFileFilter = new javax.swing.filechooser.FileFilter() {
	public boolean accept(File f) {
	    return f.isDirectory();
	}

	public String getDescription() {
	    return "Dirctory";
	}
    };

    private Loggable log = new Loggable() {
	public void log(String message) {
	    taInfo.append(message + "\n");
	}
    };

    private Crackable crack = new Crack();
    private MeReplacer meReplacer = new MeReplacer(log);

    private RSAKey rsaKey = new RSAKey();

    private PropertyWriter propertyWriter = new PropertyWriter(log);

    private String[][] lastCrackData = null;

    public CrackFrame() {
	super("MyEclipse 9.x Crack");
	initFrame();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(WIDTH, HEIGHT);
	setVisible(true);
    }

    public void initFrame() {
	initMenu();
	initTopPanel();
	initCenterPanel();
    }

    public void initMenu() {
	JMenuBar menubar = new JMenuBar();

	JMenu menu1 = new JMenu("Opreate");
	JMenu menu2 = new JMenu("Tools");

	menu1.add(generateSystemIdMenu);
	menu1.add(runMenu);
	menu1.addSeparator();
	menu1.add(exitMenu);

	menu2.add(rebuildKeyMenu);
	menu2.addSeparator();
	menu2.add(replaceClassMenu);
	menu2.add(saveProperitiesMenu);

	generateSystemIdMenu.addActionListener(this);
	runMenu.addActionListener(this);
	exitMenu.addActionListener(this);
	rebuildKeyMenu.addActionListener(this);
	replaceClassMenu.addActionListener(this);
	saveProperitiesMenu.addActionListener(this);

	menubar.add(menu1);
	menubar.add(menu2);

	setJMenuBar(menubar);
    }

    public void initCenterPanel() {
	JScrollPane jsp = new JScrollPane(taInfo);
	jsp.setBounds(100, 90, 450, 300);
	add(jsp, "Center");
    }

    public void initTopPanel() {
	JPanel toppanel = new JPanel();
	toppanel.setLayout(null);
	JLabel lUserCode = new JLabel("Usercode:", JLabel.RIGHT);
	JLabel lSystemId = new JLabel("SystemId:", JLabel.RIGHT);

	lUserCode.setBounds(10, 10, 85, 30);
	tfUsercode.setBounds(100, 10, 300, 30);
	cbLicenceType.setBounds(410, 10, 170, 30);
	lSystemId.setBounds(10, 50, 85, 25);
	tfSystemId.setBounds(100, 50, 300, 30);
	jbGenerateSi.setBounds(410, 50, 100, 30);
	jbActive.setBounds(500, 50, 80, 30);

	jbGenerateSi.addActionListener(this);
	jbActive.addActionListener(this);

	toppanel.add(lUserCode);
	toppanel.add(tfUsercode);
	toppanel.add(cbLicenceType);
	toppanel.add(lSystemId);
	toppanel.add(tfSystemId);
	toppanel.add(jbGenerateSi);
	toppanel.add(jbActive);

	JPanel tmpPanel = new JPanel();
	tmpPanel.add(toppanel, "Center");

	toppanel.setPreferredSize(new Dimension(600, 90));
	add(tmpPanel, "North");

    }

    @SuppressWarnings("deprecation")
    public void actionPerformed(ActionEvent paramActionEvent) {
	Object source = paramActionEvent.getSource();
	if (source == jbGenerateSi || source == generateSystemIdMenu) {
	    try {
		tfSystemId.setText(crack.getSystemId());
	    } catch (Throwable e) {
		e.printStackTrace();
		taInfo.append(new Date().toLocaleString() + "\n" + e.getCause().getMessage() + "\n\n");
	    }
	} else if (source == jbActive || source == runMenu) {
	    String uid = tfUsercode.getText().trim();
	    String systemId = tfSystemId.getText().trim();
	    if ("".equals(uid) || "".equals(systemId)) {
		taInfo.append(new Date().toLocaleString() + "\nusercode or systemid is empty!\n\n");
		return;
	    }
	    int licenceType = getLicenceType();
	    int licenceNumber = 0;
	    String ret = "";
	    try {
		lastCrackData = crack.crackme(uid, licenceType, licenceNumber, systemId);
		ret = showStringArray(lastCrackData);
	    } catch (Throwable e) {
		e.printStackTrace();
		ret = e.getMessage() + "\n";
	    }
	    taInfo.append(new Date().toLocaleString() + "\n" + ret + "\n");
	} else if (source == replaceClassMenu) {
	    JFileChooser jf = new JFileChooser();
	    jf.setDialogTitle("Please Choose Myeclipse --> Plugin Folder");
	    jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    jf.setMultiSelectionEnabled(false);
	    jf.setFileFilter(dirctoryFileFilter);
	    int returnVal = jf.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = jf.getSelectedFile();
		meReplacer.replace(file);
	    }
	} else if (source == saveProperitiesMenu) {
	    if (lastCrackData != null) {
		propertyWriter.write(lastCrackData);
	    }
	} else if (source == exitMenu) {
	    System.exit(0);
	} else if (source == rebuildKeyMenu) {
	    log.log("rebuild privateKey.bytes");
	    log.log("rebuild publicKey.bytes");
	    rsaKey.generateKeyFile();
	}
    }

    public String showStringArray(String[][] arrs) {
	if (arrs == null || arrs.length == 0) {
	    return "";
	}
	StringBuffer sb = new StringBuffer();
	for (String[] string : arrs) {
	    // String rs = String.format("%1$-30c%2$-10c", string[0], string[1])
	    // + "\n";
	    sb.append(string[0] + "\n\t" + string[1] + "\n");
	    // sb.append(rs);
	}
	return sb.toString();
    }

    public int getLicenceType() {
	String selectValue = cbLicenceType.getSelectedItem().toString();
	if ("STANDARD".equals(selectValue)) {
	    return 0;
	} else if ("PROFESSIONAL".equals(selectValue)) {
	    return 1;
	} else if ("BLUE".equals(selectValue)) {
	    return 2;
	} else if ("SPRING".equals(selectValue)) {
	    return 3;
	} else {
	    return 1;
	}
    }

    public static void main(String[] args) throws Exception {
	UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	new CrackFrame();
    }
}
