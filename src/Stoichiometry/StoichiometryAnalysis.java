package Stoichiometry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Port;
import org.sbgn.bindings.Sbgn;

/**
 * This class does all the Calculation part of the Stoichiometry Matrix to be
 * generated for Current pathway.
 * 
 * @author Sravanthi Sinha
 * @version 1.0
 */
public class StoichiometryAnalysis {
	public static int n = 0;

	private Map<Port, Glyph> portParents = new HashMap<Port, Glyph>();

	// necessary because arc's don't have id's: keep a map of which element goes
	// with which arc

	private String sgr, egr, linetype;

	private Set<String> metabolitenames = new HashSet<String>();
	private Set<String> metabolites = new HashSet<String>();
	private HashSet<String> pids = new HashSet<String>();
	private Map<String, String> glyphnames = new HashMap<String, String>();
	private Map<Integer, List<String>> portmatrix = new HashMap<Integer, List<String>>();
	private Map<Integer, List<String>> statematrix = new HashMap<Integer, List<String>>();
	private Map<Integer, List<String>> Lineportmatrix = new HashMap<Integer, List<String>>();
	private Map<Integer, List<String>> stoichioMatrix = new HashMap<Integer, List<String>>();
	public Map<Integer, List<String>> Matrix = new HashMap<Integer, List<String>>();
	private Map<Integer, List<String>> LineMatrix = new HashMap<Integer, List<String>>();

	// create flat lists of Arcs and Glyphs
	List<Glyph> glyphFlat = new ArrayList<Glyph>();
	List<Arc> arcFlat = new ArrayList<Arc>();
	File currentPathwayFile;

	static Map<Integer, List<String>> stmatrix;
	private final Sbgn sbgn;

	public org.sbgn.bindings.Map map;

	public StoichiometryAnalysis(Sbgn activeSbgn) {
		sbgn = activeSbgn;

	}

	/**
	 * The current pathway is saved in the temporary directory and pathway
	 * contained in it is used to do the calculation of Stoichiometric Matrix
	 * and the TableModel of it is returned
	 * 
	 * @param activePathway
	 * @return StoichiometricTableModel
	 */
	public StoichiometricTableModel calculateMatrix() {
		map = sbgn.getMap();
		getstiochiometriccolumn();
		getstiochiometricrow();
		makeLineMatrix();
		makeStates();
		makePorts();
		makeLinePortsMatrix();
		createStoichiometricSparseMatrix();
		stmatrix = createStoichiometricMatrix();
		n = portParents.size() / 2;
		return new StoichiometricTableModel(stmatrix, n);
	}

	/**
	 * Creates the StoichiometricMatrix for current pathway
	 * 
	 * @return the Stoichiometric Matrix in the form of Map
	 */
	protected Map<Integer, List<String>> createStoichiometricMatrix() {
		int j = 0;
		for (String me : metabolitenames) {
			
				iterateStochiometricMatrix(me, j);
				//System.out.println(me);
				j++;
			
		}
		return Matrix;
	}

	private void iterateStochiometricMatrix(String mname, int j) {
		int i = 0;
		List<String> matrixf2 = new ArrayList<String>();
		matrixf2.add(mname);
		for (String pname : pids) {
			for (Entry<Integer, List<String>> entryp : stoichioMatrix
					.entrySet()) {
				String eltxt = glyphnames.get((entryp.getValue().get(0)));
				egr = entryp.getValue().get(1);
				if ((egr.equalsIgnoreCase(pname))
						&& (mname.equalsIgnoreCase(eltxt))) {
					matrixf2.add(entryp.getValue().get(2));
					i++;
				}
			}
			if (i == 0) {
				matrixf2.add("0");
			}
			i = 0;
		}
		Matrix.put(j, matrixf2);
	}

	/**
	 * Creates the sparseStoichiometricMatrix for current pathway (i.e) notes
	 * the cardinality of an line linked with particular metabolite and
	 * processnode
	 * 
	 */
	protected void createStoichiometricSparseMatrix() {
		int i = 0;

		for (String gid : metabolites) {
			for (Entry<Integer, List<String>> entrylp : Lineportmatrix
					.entrySet()) {
				sgr = entrylp.getValue().get(0);
				linetype = entrylp.getValue().get(1);
				egr = entrylp.getValue().get(2);
				String pgref = entrylp.getValue().get(3);
				String lineid = entrylp.getValue().get(4);
				if ((gid.equalsIgnoreCase(sgr) || gid.equalsIgnoreCase(egr))) {
					createSparseMatrix(gid, linetype, pgref, lineid, i);
					i++;
				}
			}
		}
	}

	private void createSparseMatrix(String gid, String linetype, String pgref,
			String lineid, int i) {
		List<String> listf = new ArrayList<String>();
		listf.add(gid);
		listf.add(pgref);// add end graph ref

		if (checkproductiontype(linetype)
				|| (linetype.equalsIgnoreCase("production"))) {
			// listf.add("1");
			if (getlabel(lineid) != null) {
				listf.add(getlabel(lineid));
			} else {
				listf.add("1");
			}

		} else if (checkconsumptiontype(linetype)
				|| (linetype.equalsIgnoreCase("consumption"))) {
			// listf.add("1");
			if (getlabel(lineid) != null) {
				listf.add("-" + getlabel(lineid));
			} else {
				listf.add("-1");
			}
		}
		stoichioMatrix.put(i, listf);
	}

	/**
	 * 
	 * @param lineid
	 *            the lineid whose label has to be returned
	 * @return label of the line with id :'lineid'
	 */
	private String getlabel(String lineid) {
		for (Entry<Integer, List<String>> entry : statematrix.entrySet()) {
			if (lineid.equalsIgnoreCase(entry.getValue().get(0))) {
				return entry.getValue().get(1).toString();
			}
		}
		return null;
	}

	/**
	 * method to check the type is consumption (ie line under basic interaction
	 * can be used as consumption hence makes no differnece)
	 * 
	 * @return boolean
	 */
	private boolean checkconsumptiontype(String type) {
		if (type.equalsIgnoreCase("SBGN-Consumption")
				|| type.equalsIgnoreCase("Line")) {
			return true;
		}
		return false;
	}

	/**
	 * method to check the type is production (ie line with arrow wnd line type
	 * under basic interaction can be used as consumption hence makes no
	 * differnece)
	 * 
	 * @return boolean
	 */
	private boolean checkproductiontype(String type) {
		if (type.equalsIgnoreCase("SBGN-Production")
				|| type.equalsIgnoreCase("Arrow")) {
			return true;
		}
		return false;
	}

	/**
	 * This method requires line Map and process node Map to generate a Map of
	 * Line and connected ProcessNode (i.e) Map of- Line :Starting Graph
	 * Reference,GraphId,Ending Graph Reference. connected ProcessNode:
	 * GraphRefernce,AnchorId
	 */
	protected void makeLinePortsMatrix() {
		int i = 0;
		for (Entry<Integer, List<String>> entry : LineMatrix.entrySet()) {
			List<String> list2 = new ArrayList<String>();
			String srf = entry.getValue().get(2).toString();
			String erf = entry.getValue().get(3).toString();
			list2.add(srf);// srf
			list2.add(entry.getValue().get(1).toString());// line type
			list2.add(erf); // grf
			for (Entry<Integer, List<String>> entry2 : portmatrix.entrySet()) {
				String graphref = entry2.getValue().get(1);
				String anchorid = entry2.getValue().get(2);
				checkLineToPort(srf, erf, graphref, anchorid);
				if (checkLineToPort(srf, erf, graphref, anchorid)) {
					list2.add(graphref);// graph ref
					break;
				}
			}
			list2.add(entry.getValue().get(0).toString());// lineid
			Lineportmatrix.put(i, list2);
			i++;
		}
	}

	private boolean checkLineToPort(String srf, String erf, String gref,
			String aid) {
		if (gref.equalsIgnoreCase(srf) || gref.equalsIgnoreCase(erf)
				|| aid.equalsIgnoreCase(srf) || aid.equalsIgnoreCase(erf)) {
			return true;
		}
		return false;
	}

	/**
	 * Creates the Map for each ProcessNode (i.e) Map of each ProcessNode with
	 * its COnnected line-GraphId, starting Graph Reference,
	 */
	protected void makePorts() {
		int i = 0;
		for (Glyph n : glyphFlat) {
			if (n.getClazz().equalsIgnoreCase("process")){
			for (Port p : n.getPort()) {
				portParents.put(p, n);
			}
			}
		}
		for (Glyph g : glyphFlat) {

			if (g.getClazz().equalsIgnoreCase("process")) {
				// / removed list3.add(g.getId()); // line graph id
				List<String> list3 = new ArrayList<String>();
				list3.add(g.getId());
				list3.add(g.getId());// graph ref
				list3.add(g.getPort().get(0).getId());// anchor- connected line
														// id
				portmatrix.put(i, list3);
				i++;
				List<String> list = new ArrayList<String>();
				list.add(g.getId());
				list.add(g.getId());// graph ref
				list.add(g.getPort().get(1).getId());// anchor- connected line
														// id
				portmatrix.put(i, list);
				i++;
			}
		}

	}

	private void makeStates() {
		int i = 0;

		for (Arc pe : arcFlat) {
			if (checktype(pe.getClass().toString())) {
				List<String> list3 = new ArrayList<String>();

				if (pe.getGlyph().size() > 0) {

					list3.add(pe.getId());// graph ref
					list3.add(pe.getGlyph().get(0).getLabel().getText());// label
					// System.out.println(pe.getGraphRef() + pe.getTextLabel());
					statematrix.put(i, list3);
					i++;
				}

			}
		}
	}

	/**
	 * Creates the Map for each Line (i.e) Map of each Line with its starting
	 * Graph Reference, Line-GraphId, Ending GraphReference
	 */
	protected void makeLineMatrix() {

		int i = 0;
		arcFlat.addAll(map.getArc());
		for (Arc a : arcFlat) {
			if (checktype(a.getClazz().toString())) {
				createLineModel(a.getClazz(), a, i);
				i++;
			}
		}
	}

	private boolean checktype(String type) {
		if ((type.equalsIgnoreCase("production") || (type
				.equalsIgnoreCase("consumption")))
				&& (!(type.equalsIgnoreCase("catalysis")))) {
			return true;
		}
		return false;

	}

	private String getId(Object o) {
		if (o == null)
			throw new NullPointerException();
		if (o instanceof Glyph)
			return ((Glyph) o).getId();
		if (o instanceof Port)
			return ((Port) o).getId();
		return "";
	}

	private void createLineModel(String type, Arc a, int i) {
		List<String> lineList = new ArrayList<String>();
		lineList.add(a.getId());
		lineList.add(type);
		if (a.getSource() == null) {
			lineList.add("hey");
		} else {
			lineList.add(getId(a.getSource()));
		}
		lineList.add(getId(a.getTarget()));
		LineMatrix.put(i, lineList);
	}

	/**
	 * @return the number of reactions inculded in pathway
	 * 
	 */
	protected int getstiochiometriccolumn() {
		int column = 0;

		glyphFlat.addAll(map.getGlyph());
		for (Glyph reactions : glyphFlat) {
			if (reactions.getClazz().toString().equalsIgnoreCase("process")) {
				pids.add(reactions.getId());
				column++;
			}
		}
		return column;
	}
	
	protected void getstiochiometricrow() {

		for (Glyph metabolite : glyphFlat) {
			if (!metabolite.getClazz().toString().equalsIgnoreCase("process")
					&& !metabolite.getClazz().toString()
							.equalsIgnoreCase("macromolecule")) {
				metabolitenames.add(metabolite.getLabel().getText());
				metabolites.add(metabolite.getId());
				glyphnames.put(metabolite.getId(), metabolite.getLabel()
						.getText());
			}
		}

	}

}