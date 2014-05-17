package Stoichiometry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdom.JDOMException;
import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.ApplicationEvent.Type;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.core.view.VPathwayEvent;
import org.pathvisio.core.view.VPathwayListener;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.sbgn.SbgnExportHelper;
import org.pathvisio.sbgn.SbgnFormat;
import org.pathvisio.sbgn.SbgnImportHelper;
import org.sbgn.ConvertMilestone1to2;
import org.sbgn.SbgnVersionFinder;
import org.sbgn.bindings.Sbgn;
import org.xml.sax.SAXException;

/**
 * This class creates the conetent in the Popup Dialog (where the Stoichiometric Matrix appears ultimately).
 * When ever a pathway is opened, the current pathway is stored in the temporary Directory as an xml File.
 * Pathway is taken From the Stored file and all the calculation Part for 
 * the generation of Stoichiometry Matrix is Done by StoichiometryAnlysis class 
 * and A TableModel is generated based on the Matrix generated above in  
 * StoichiometricTableModel, which is used as source for table(ui) and is
 * added to scrollPane by stPanel class which is added to PopUpDiaog
 * 
 * @author Sravanthi Sinha
 * @version 1.0
 */
public class StoichiometricPanel extends JPanel implements VPathwayListener,ApplicationEventListener 
{
	
	private SwingEngine eng;
	private JPanel drawPanel;	
	final Pathway path = new Pathway();	
	public StoichiometricPanel(SwingEngine eng) 
	{
		this(eng, eng.getApplicationPanel().getScrollPane());
	}
 
	public StoichiometricPanel(SwingEngine eng, JScrollPane pathwayScrollPane) 
	{
		this.eng = eng;
		setLayout(new BorderLayout());		
		drawPanel = new stPanel();		
		add(drawPanel);
		eng.getEngine().addApplicationEventListener(this);
	}

	private Sbgn sbgn;
	
	 private Sbgn createPathway()	
	 {	
		File currentPathwayFile= new File(System.getProperty("java.io.tmpdir"),"SExportedPathway.sbgn");
		Pathway currentPathway = eng.getEngine().getActivePathway();
		if(!exportPathwayToXML(currentPathwayFile, currentPathway)) return null;
			
			
			
				try {
					sbgn = readFromFile(currentPathwayFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	
		return sbgn;
	}
	
	public Sbgn readFromFile(File inputFile) throws SAXException, FileNotFoundException, IOException,
			JDOMException, JAXBException
	{
		File targetFile = inputFile;
		int version = SbgnVersionFinder.getVersion(inputFile);
		//System.out.println ("Detected version: " + version);
		if (version == 1)
		{
			targetFile = File.createTempFile(inputFile.getName(), ".sbgn");
		//	System.out.println ("Converted to " + targetFile);
			ConvertMilestone1to2.convert (inputFile, targetFile);
		}

		ClassLoader cl = org.sbgn.bindings.ObjectFactory.class.getClassLoader();
		JAXBContext context = JAXBContext.newInstance("org.sbgn.bindings", cl);
		Unmarshaller unmarshaller = context.createUnmarshaller() ;
		
		Sbgn sbgn = (Sbgn)unmarshaller.unmarshal (targetFile);
		return sbgn;
	}


	/**export the current unsaved/saved pathway to a file(.sbgn)*/
	
	private boolean  exportPathwayToXML(File currentPathwayFile, Pathway currentPathway)
	{
		try 
		{
		 new SbgnFormat().doExport(currentPathwayFile, currentPathway);
			
			//GpmlFormat.writeToXml (currentPathway,currentPathwayFile, true);
		} catch (Exception e) 
		{
			//System.out.println("sbgn conversion Exception");
			e.printStackTrace();			
			return false;
		}
		return true;
	}
	
	@Override
	public void applicationEvent(ApplicationEvent e) 
	{
		if (e.getType() == Type.VPATHWAY_OPENED|| e.getType() == Type.VPATHWAY_NEW) 
		{
		}
	}

	@Override
	public void vPathwayEvent(VPathwayEvent e) 
	{
		
	}
	/**
	 * The panel creates the ui part of the Stoichimetric Matrix 
	 * based on the TableModel generated for the current pathway
	 * 
	 */
	public class stPanel extends JPanel 
	{
		
		private int AUTO_RESIZE_OFF = 0;	

		public stPanel() 
		{
			 super(new GridLayout(1,0));
			 
			VPathway path = eng.getEngine().getActiveVPathway();
			if (path == null) 
			{
				return;
			}
			
			createPathway();
			JTable table = new JTable();
			StoichiometryAnalysis sa = new StoichiometryAnalysis(sbgn);
			table.setModel(sa.calculateMatrix());
			
			TableColumn column = table.getColumnModel().getColumn(0);
		    column.setPreferredWidth(150);   
		
		    table.setFillsViewportHeight(true);     		      
				   
			JScrollPane scrollPane = new JScrollPane(table);
			table.setAutoResizeMode(AUTO_RESIZE_OFF);
			int w = table.getPreferredSize().width +
				scrollPane.getVerticalScrollBar().getPreferredSize().width + 5;
			int h = table.getPreferredSize().height +
				scrollPane.getHorizontalScrollBar().getPreferredSize().height + 5;
			w = Math.min(400, w);
			h = Math.min(300, h);
			setPreferredSize(new Dimension(w, h));
			
			// Add the scroll pane to this panel.
			add(scrollPane,BorderLayout.CENTER);
		}
	}
}
