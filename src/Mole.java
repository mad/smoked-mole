import mole.eg3;
import mole.syntaxtree.Start;
import mole.compiler.Compiler;

public class Mole {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    new eg3(System.in);
	    try
	    {
	      eg3 eg3 = new eg3(new java.io.FileInputStream(args[0]));
	      Compiler compiler = new Compiler();
	      compiler.visit(eg3.Start());
	    }
	    catch (Exception e)
	    {
	      System.out.println("Oops.");
	      System.out.println(e.getMessage());
	    }

	}

}
