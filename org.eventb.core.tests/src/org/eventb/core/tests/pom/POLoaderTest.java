package org.eventb.core.tests.pom;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.tests.BuilderTest;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.RodinDBException;

/**
 * This class contains unit tests to test the proof loader.
 * 
 * <p>
 * Tests Done :
 * <ul>
 * <li> The global and local hypotheses are correctly loaded.
 * <li> The global and local type environments are correctly loaded.
 * <li> The assumed WD lemmas for the goal and hyps are correctly generated. 
 * </ul> 
 * </p>
 * 
 * TODO : Test that selection hints are properly used.
 * 
 * @author Farhad Mehta
 *
 */
public class POLoaderTest extends BuilderTest {

	/**
	 * Creates a new type environment from the given strings. The given strings
	 * are alternatively an identifier name and its type.
	 * 
	 * @param strings
	 *            an even number of strings
	 * @return a new type environment
	 */
	public ITypeEnvironment mTypeEnvironment(String... strings) {
		// even number of strings
		assert (strings.length & 1) == 0;
		final ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < strings.length; i += 2) {
			final String name = strings[i];
			final String typeString = strings[i+1];
			final IParseResult pResult = factory.parseType(typeString);
			assertTrue("Parsing type failed for " + typeString,
					pResult.isSuccess());
			final Type type = pResult.getParsedType(); 
			result.addName(name, type);
		}
		return result;
	}

	private IPOFile createPOFile() throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.getRodinFile("x.bpo");
		poFile.create(true, null);
		IPOPredicateSet hyp0 = POUtil.addPredicateSet(poFile, "hyp0", null,
				mTypeEnvironment("x", "ℤ"),
				"1=1", "2=2", "x∈ℕ"
		);
		IPOPredicateSet hyp1 = POUtil.addPredicateSet(poFile, "hyp1", hyp0,
				mTypeEnvironment("y", "ℤ"),
				"1=1", "2=2", "y∈ℕ", "x÷x = 1"
		);
		// Empty local type environment and local hyps
		POUtil.addSequent(poFile, "PO1", 
				"x ≠ 0",
				hyp0, 
				mTypeEnvironment()
		);
		// With local type environment and local hyps
		POUtil.addSequent(poFile, "PO2", 
				"x ≠ 0",
				hyp0, 
				mTypeEnvironment("z", "ℤ"),
				"z=3"
		);
		// With goal with implicit WD asumption
		POUtil.addSequent(poFile, "PO3", 
				"x÷x = 1",
				hyp0, 
				mTypeEnvironment()
		);
		// With goal, global hyp & local hyp with implicit WD assumption
		// WD assumption for goal contains a conjunction that needs to be split
		POUtil.addSequent(poFile, "PO4", 
				"x÷(x+y) = 1 ∧ x÷(y+x) = 1", 
				hyp1, 
				mTypeEnvironment(),
				"y÷y = 1"
		);
		poFile.save(null, true);
		return poFile;
	}

	
	
	public final void testReadPO() throws CoreException {
		
		IPOFile poFile = createPOFile();
		
		final IPOSequent[] poSequents = poFile.getSequents();
				
		String[] expectedSequents = {
			"{x=ℤ}[][1=1, 2=2, x∈ℕ][] |- x≠0",
			"{z=ℤ, x=ℤ}[][1=1, 2=2, x∈ℕ, z=3][] |- x≠0",
			"{x=ℤ}[][1=1, 2=2, x∈ℕ, x≠0][] |- x ÷ x=1",
			"{y=ℤ, x=ℤ}[][1=1, 2=2, x∈ℕ, y∈ℕ, x ÷ x=1, x≠0, y ÷ y=1, y≠0, x+y≠0, x ÷ (x+y)=1⇒y+x≠0][] |- x ÷ (x+y)=1∧x ÷ (y+x)=1"
		};
		
		assertEquals("Wrong number of POs in PO file",poSequents.length, expectedSequents.length);

		for (int i = 0; i < expectedSequents.length; i++) {
			
			final IPOSequent poSequent = poSequents[i];
			assertTrue("POSequent "+ poSequent.getElementName() +" does not exist",poSequent.exists());
			IProverSequent seq = POLoader.readPO(poSequent);
			assertNotNull("Error generating prover sequent", seq);
			assertEquals("Sequents for " + poSequent.getElementName() + " do not match",
					expectedSequents[i],seq.toString());
		}
		
	}
	
	public static String[] mp(String... strings) {
		return strings;
	}
	
	public static String[] mh(String... strings) {
		return strings;
	}

}
