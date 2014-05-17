package Stoichiometry;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;

import Stoichiometry.StoichiometricTableModel;
import Stoichiometry.StoichiometryAnalysis;

public class checkCardinalityTest extends TestCase {

	public void testCalculateMatrix() 
	{		
		Pathway p = new Pathway();
		try {
			p.readFromXml(new File("test-files", "photosynthesis.gpml"), false);
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		StoichiometryAnalysis sa = new StoichiometryAnalysis(p);
		
		StoichiometricTableModel model = sa.calculateMatrix();

		HashSet<String> metabolitesexpected = new HashSet<String>();

	
		metabolitesexpected.add("CO2");		
		metabolitesexpected.add("H2O");
		metabolitesexpected.add("O2");
		metabolitesexpected.add("C6H1206");
 
		List<String> actual = Arrays.asList("CO2","-1");
		List<String> expected = model.getValues("CO2");
		List<String> actual2 = Arrays.asList("H2O","-6");
		List<String> expected2 = model.getValues("H2O");
		List<String> actual3 = Arrays.asList("O2","6");
		List<String> expected3 = model.getValues("O2");
		List<String> actual4 = Arrays.asList("C6H1206","1");
		List<String> expected4 = model.getValues("C6H1206");
		
		assertEquals(actual, expected);
		assertEquals(actual2, expected2);
		assertEquals(actual3, expected3);
		assertEquals(actual4, expected4);
 
		assertEquals(1, model.getColumnCount() - 1);
		assertEquals(4, model.getRowCount());
		assertTrue(metabolitesexpected.containsAll(model.getRownames()));
		

	}
}

