package Stoichiometry;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.Engine;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;


/**
 * This class adds action to toolbar .
 * Toolbar action is  diabled (greyed out), when no Pathway is opened.
 * When opened it gets enabled and provides an option to generate a StoichiometricMarix
 * and even an Option to Export it in .txt File
 * 
 * @author Sravanthi Sinha
 * @version 1.0
 */
public class StoichiometricPlugin implements Plugin,Engine.ApplicationEventListener, ActionListener 
{
	private PvDesktop desktop;
	private JPanel StoichiometricPanel;
	private JFrame frame;
	public void init(PvDesktop desktop) 
	{
		this.desktop = desktop;

		// add our action (defined below) to the toolbar
		desktop.getSwingEngine().getApplicationPanel().addToToolbar(toolbarAction);

		// register a listner so we get notified when a pathway is opened
		desktop.getSwingEngine().getEngine().addApplicationEventListener(this);

		// set the initial enabled / disabled state of the action
		updateState();
	}

	/**
	 * Checks if a pathway is open or not. If there is no open pathway, the
	 * toolbar button is greyed out.
	 */
	public void updateState() 
	{
		toolbarAction.setEnabled(desktop.getSwingEngine().getEngine().hasVPathway());
	}

	private final MyToolbarAction toolbarAction = new MyToolbarAction();

	private class MyToolbarAction extends AbstractAction 
	{
		private String ICON_PATH = "resources/minifesto2.png";

		MyToolbarAction() 
		{
			// Short description will be the mouse tooltip label
			putValue(SHORT_DESCRIPTION, "Create Stoichiometric Matrix");

			// icon in the toolbar. Use a 16x16 gif or png image.
			// The resource should be in the class path
			URL url = StoichiometricPlugin.class.getClassLoader().getResource(ICON_PATH);
			if (url == null)
				throw new IllegalStateException("Could not load resource "
						+ ICON_PATH
						+ ", please check that it is in the class-path");
			putValue(SMALL_ICON, new ImageIcon(url));
		}

		public void actionPerformed(ActionEvent e) 
		{
			createAndShowGUI();
		}
	}

	private void createAndShowGUI() 
	{
		 frame = new JFrame("STOICHIOMETRIC MATRIX");
		// Create and set up the content pane.
		StoichiometricPanel = new StoichiometricPanel(desktop.getSwingEngine());
		JButton button = new JButton("OK");
		button.setActionCommand("ok");
		button.addActionListener(this);		
		
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JButton bn = new JButton(new StoichiometricMatrixExporter(frame));
		
		c.gridwidth = GridBagConstraints.REMAINDER;			
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;		
		frame.add(StoichiometricPanel,c);		
		
		c.weighty = 0.0;
		c.weightx=0.5;
		c.fill=GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		JPanel p = new JPanel();
		p.add(bn);
		p.add(button);
		frame.add(p,c);
		//frame.setResizable(false)	;	
	    frame.pack();
	    frame.setVisible(true);
	}
	
	public void done() 
	{
	}

	/**
	 * This is called when a Pathway is opened or closed.
	 */
	public void applicationEvent(ApplicationEvent e) 
	{
		updateState();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ("ok".equals(e.getActionCommand()))
		{ 
			frame.dispose();
		}
	}
}