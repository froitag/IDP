import java.io.FileNotFoundException;
import java.util.Arrays;

import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.dummy.JenaIO;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

/**
 * Class containing examples for how to use the interfaces IO and Database V-1
 * @author prasser
 */
public class Test {

	public static void main(String[] args) throws FileNotFoundException, FSException{
		
		IO io = buildIO();
		
		System.out.println("\nEXAMPLES FOR QUERYING");
		System.out.println("*********************\n");
		queryDataset(io);
		
		System.out.println("\nEXAMPLES FOR WRITING TRIPLES");
		System.out.println("****************************\n");
		writeToDataset(io);
		
		System.out.println("\nEXAMPLES FOR RULES");
		System.out.println("******************\n");
		executeRule(io);
	}

	/**
	 * Example for building up a dummy federation
	 * @return
	 * @throws FileNotFoundException
	 */
	private static IO buildIO() throws FileNotFoundException {
		
		// EXAMPLE 1:
		// Build a dummy federation
		JenaIO io = new JenaIO();
		io.register(new JenaDatabase("Test1", "database/test.nt"));
		io.register(new JenaDatabase("Test2", "database/test.nt"));
		io.register(new JenaDatabase("Test3", "database/test.nt"));
		io.register(new JenaDatabase("Test4", "database/test.nt"));
		io.register(new JenaDatabase("Test5", "database/test.nt"));
		return io;
	}
	
	/**
	 * Example for querying a database
	 * @param io
	 * @throws FSException 
	 */
	private static void queryDataset(IO io) throws FSException {
		
		// EXAMPLE 2:
		// Prints all triples in database "Test1"
		System.out.println("Triples in database 1:");
		Database db1 = io.getDatabaseByName("Test1");
		FSResultSet rs = io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}", db1);
		System.out.println(Arrays.toString(rs.getHeader()));
		while (rs.hasNext()){
			String[] tuple = rs.next();
			System.out.println(	tuple[rs.column("s")]+" | "+
								tuple[rs.column("p")]+" | "+
								tuple[rs.column("o")]);	
		}
		rs.close();
		System.out.println(	"\nNumber of triples in database 1: " + 
							io.getSize(db1));

		// EXAMPLE 3:
		// Prints the triples from all databases
		// Returns the same as example 2 because all databases contain
		// the same triples
		System.out.println("\nTriples in all databases");
		rs = io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}");
		System.out.println(Arrays.toString(rs.getHeader()));
		while (rs.hasNext()){
			String[] tuple = rs.next();
			System.out.println(	tuple[rs.column("s")]+" | "+
								tuple[rs.column("p")]+" | "+
								tuple[rs.column("o")]);	
		}
		rs.close();
	}

	/**
	 * Example for creating and writing to a database
	 * @param io
	 * @throws FSException 
	 */
	private static void writeToDataset(IO io) throws FSException {
		
		// EXAMPLE 4:
		// Create database
		Database db1 = io.createDatabase(Database.Type.IN_MEMORY);
		
		// And write a triple
		io.writeTriple(db1, 	"<http://www.in.tum.de>", 
								"<http://example.org#websiteOf>", 
								"\"Fakultät für Informatik\"^^<http://www.w3.org/2001/XMLSchema#string>");
		
		// Prints all triples in database 
		System.out.println("Triples in temporary database:");
		FSResultSet rs = io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}", db1);
		System.out.println(Arrays.toString(rs.getHeader()));
		while (rs.hasNext()){
			String[] tuple = rs.next();
			System.out.println(	tuple[rs.column("s")]+" | "+
								tuple[rs.column("p")]+" | "+
								tuple[rs.column("o")]);	
		}
		rs.close();
	}


	/**
	 * Example rule that creates all possible combinations of resources
	 * @param io
	 * @throws FSException 
	 */
	private static void executeRule(IO io) throws FSException {
		
		// EXAMPLE 5:
		// Create database
		Database db1 = io.createDatabase(Database.Type.IN_MEMORY);
		
		// Read all combinations of names
		FSResultSet rs = io.execute("SELECT ?s1 ?s2 WHERE {" +
									"?s1 <http://example.org#firstname> ?name1." +
									"?s2 <http://example.org#firstname> ?name2. }");
		
		// Parse and write
		while (rs.hasNext()){
			
			String[] tuple = rs.next();
			io.writeTriple(	db1, 
							tuple[rs.column("s1")], 
							"<http://www.example.org#combination>",
							tuple[rs.column("s2")]);
		}
		rs.close();
		
		// Prints all triples in database 
		System.out.println("Triples in temporary database:");
		rs = io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}", db1);
		System.out.println(Arrays.toString(rs.getHeader()));
		while (rs.hasNext()){
			String[] tuple = rs.next();
			System.out.println(	tuple[rs.column("s")]+" | "+
								tuple[rs.column("p")]+" | "+
								tuple[rs.column("o")]);	
		}
	}
}
