package Stoichiometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * This class creates the StoichiometricTableModel 
 * Based on the Map Created by the StoichiometricAnalysis(where all Calculation part is Done)
 * 
 * Using this class we can get the 
 * Complete information Regarding the Contents of the TableModel
 * (i.e) names of the Metabolites ,no.of metabolites involved,
 * 		no.of Reactions(Process) Involved.  
 * 
 * @author Sravanthi Sinha
 * @version 1.0
 */
public class StoichiometricTableModel implements TableModel 
{
	private Map<Integer, List<String>> resources;
	private Object[][] tableData;
	private String[] columnNames;
	private int m;

	/**
	 * This method creates the StoichiometricTableModel
	 *  @param resources the stoichiometric Matrix as Map
	 *   generated after the calculation of the matrix
	 * @param m  no.of reactions the current pathway
	 */
	public StoichiometricTableModel(Map<Integer, List<String>> resources, int m) 
	{
		this.resources = resources;
		this.m = m;
		tableData = new Object[resources.keySet().size()][m + 1];
		columnNames = new String[m + 1];
		int index = 0;

		for (int key : resources.keySet()) 
		{
			for (int i = 0; i < (m + 1); i++) 
			{
				tableData[index][i] = resources.get(key).get(i);
			}
			// and so forth
			index++;
		}
	}

	/**
	 * @return number of columns (reactions)
	 */
	public int getColumnCount() 
	{
		return columnNames.length;
	}

	/**
	 * @return number of rows (metabolites involved)
	 */
	public int getRowCount() 
	{
		return resources.size();
	}

	/**
	 * @param col the index of column
	 * @return name of column
	 */
	public String getColumnName(int col)
	{
		if (col == 0) 
		{
			return "metabolite";
		}
		 else
			return "r" + col;
	}

	public Object getValueAt(int row, int col) 
	{
		return resources.get(row).get(col);
	}

	public Class getColumnClass(int c) {
		
		return getValueAt(0, c).getClass();
	}

	/**
	 * @param row  row index of tableModel
	 * @param col  columnindex of tableModel
	 */
	public boolean isCellEditable(int row, int col) 
	{
		return false;
	}

	@Override
	public void addTableModelListener(TableModelListener l) 
	{

	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{

	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{

	}
		/** 
		 * 
		 * @return all the metabolite names
		 */
	public List<String> getRownames()
	{
		List<String> row = new ArrayList<String>();
		for (int key : resources.keySet()) {
			row.add(resources.get(key).get(0));
		}
		return row;
	}
			/**
			 * 
			 * @param metabolite the particular metabolite whose index in table has to be found
			 * @return the row index of the Particular metabolite
			 */
	public int getRowIndex(String metabolite) 
	{
		int index = 0;
		for (int key : resources.keySet())
		{
			for (int i = 0; i < getColumnCount(); i++)
			{
				if (metabolite.equalsIgnoreCase(resources.get(key).get(0)))
				{
					index = key;
					break;
				}
			}

		}
		return index;
	}

	public List<String> getValues(String metabolite)
	{
		int i = getRowIndex(metabolite);
		List<String> cardinality = resources.get(i);
		return cardinality;

	}
}
