package Stoichiometry;

import java.io.File;
import java.util.HashSet;
import junit.framework.TestCase;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;

import Stoichiometry.StoichiometricTableModel;
import Stoichiometry.StoichiometryAnalysis;

public class StoichiometryTest extends TestCase {

	public void testCalculateMatrix() 
	{		
		Pathway p = new Pathway();
		try {
			p.readFromXml(new File("test-files", "ppp.gpml"), false);
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		StoichiometryAnalysis sa = new StoichiometryAnalysis(p);
		
		StoichiometricTableModel model = sa.calculateMatrix();

		HashSet<String> metabolitesexpected = new HashSet<String>();

		metabolitesexpected.add("D-sedoheptulose 7-phosphate");
		metabolitesexpected.add("NADP+");
		metabolitesexpected.add("NADPH");
		metabolitesexpected.add("6-phosphogluconolactone");
		metabolitesexpected.add("ribose-5-phosphate");
		metabolitesexpected.add("D-glucose 6-phosphate");
		metabolitesexpected.add("erythrose-4-phosphate");
		metabolitesexpected.add("fructose-6-phosphate");
		metabolitesexpected.add("6-phosphogluconate");
		metabolitesexpected.add("CO2");
		metabolitesexpected.add("D-glyceraldehyde 3-phosphate");
		metabolitesexpected.add("H+");
		metabolitesexpected.add("xylulose-5-phosphate");
		metabolitesexpected.add("D-ribulose 5-phosphate");
		metabolitesexpected.add("H20");

		assertEquals(8, model.getColumnCount() - 1);
		assertEquals(15, model.getRowCount());
		assertTrue(metabolitesexpected.containsAll(model.getRownames()));
		

	}
}
