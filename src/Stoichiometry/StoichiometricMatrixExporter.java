package Stoichiometry;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.pathvisio.gui.CommonActions;
/**
 * This class creates the GUI part for Export Action
 * when ever the Export Button on the Stoichiometry Matrix is clicked.
 * Filtering of File is done as to assure only export can be done in .txt Format
 * and an File is exported to the choosen location.
 * If a File with same Name exists , 
 * it throws an Alert - to indicate whwter to over write the existing File
 * 
 * @author Sravanthi Sinha
 * @version 1.0
 */

class StoichiometricMatrixExporter extends AbstractAction
{
	private static String ICON_PATH = "resources/export.gif";
	protected CommonActions actions;
	static URL url = StoichiometricPlugin.class.getClassLoader().getResource(
			ICON_PATH);
	Component parentComponent;	
	private JFrame eframe;
	public StoichiometricMatrixExporter(Component parentComponent) 
	{
		super("Export", new ImageIcon(url));
		putValue(SHORT_DESCRIPTION, "Export to TXT file");
		this.parentComponent = parentComponent;
	}

	public void actionPerformed(ActionEvent e) 
	{
		createExportDialog();
	}

	private void createExportDialog() {		
		 String dir;
		 String filename;
		JFileChooser c = new JFileChooser();
		TxtFilter txt = new TxtFilter();		
		c.setFileFilter(txt);
		
		int rVal = c.showDialog(parentComponent, "Export");
		if (rVal == JFileChooser.APPROVE_OPTION) {
			filename = c.getSelectedFile().getName();
			dir = c.getCurrentDirectory().toString();
			try 
			{
				createTxtFile(filename, dir);
			} 
			catch (IOException e1) 
			{				
				e1.printStackTrace();
			}
		}
		if (rVal == JFileChooser.CANCEL_OPTION)
		{
			filename = "You pressed cancel";
			dir = "";
		}		
	}

	private void createTxtFile(String filename2, String dir2) throws IOException 
	{		
		File OutFile;
		
		if (filename2.contains(".txt")) 
		{
			OutFile = new File(dir2, filename2);
		} else 
		{
			OutFile = new File(dir2, filename2 + ".txt");
		}
		if (mayOverwrite(OutFile)) 
		{
		Map<Integer, List<String>> ma = StoichiometryAnalysis.stmatrix;
			int m = StoichiometryAnalysis.n;

			BufferedWriter outStream = new BufferedWriter(new FileWriter(OutFile));
			try 
			{
				for (int key : ma.keySet()) 
				{
					for (int i = 0; i < (m + 1); i++) 
					{
						///Strip newlines from node labels before export, so that they don't take up more than one row in the exported matrix
						String field = ma.get(key).get(i);
						field = field.replace ("\n", " ");
						outStream.write(field);
						outStream.write("\t");
					}
					outStream.newLine();
				}
				outStream.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
 
	private boolean mayOverwrite(File f) 
	{
		boolean allow = true;
		if (f.exists()) 
		{
			int status = JOptionPane.showConfirmDialog(eframe,"File " + f.getName() + " already exists, overwrite?","File already exists", JOptionPane.YES_NO_OPTION);
			allow = status == JOptionPane.YES_OPTION;
		}
		return allow;
	}

}
/**
 * This class provides Filtering for Export Action
 * for Stoichiometric Matrix generated to export only in .txt Format
 * 
 * @author Sravanthi Sinha
 * @version 1.0
 */
class TxtFilter extends FileFilter 
{	

/**
 * This method makes it possible to export files
 * only in .txt Format
 * @param f the file which has to be in .txt format
 * @return boolean
 */
	@Override
	public boolean accept(File f) 
	{
		return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
	}
	
	
	@Override
	public String getDescription() 
	{
		return "Text files (*.txt)";
	}
}
