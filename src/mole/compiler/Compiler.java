package mole.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import mole.syntaxtree.*;

public class Compiler {

	private final ClassWriter fClassWriter;
	private MethodVisitor mCurrentVisitor;
	
	public Compiler() {
		fClassWriter = new ClassWriter(0);

		fClassWriter.visit(V1_6, ACC_PUBLIC, "RunnableMath", null, "java/lang/Object", null);
		fClassWriter.visitSource("RunnableMath.java", null);
		
	    MethodVisitor mv;
	    mv = fClassWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
	    if (mv != null) {
		  mv.visitCode();
		  mv.visitVarInsn(ALOAD, 0);
		  mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		  mv.visitInsn(RETURN);
		  mv.visitMaxs(1, 1);
		  mv.visitEnd();
	    }
	}
	
	private void writeClass()
	{
	    fClassWriter.visitEnd();
	    
	    FileOutputStream file;
		try {
			file = new FileOutputStream(new File("RunnableMath.class"));
			file.write(fClassWriter.toByteArray());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object visit(Start node) throws Exception {
		int mathExpressionCount = 0;
		
		if (node.f0.size() > 0) {
			Iterator<INode> statement = node.f0.elements();
			
			while (statement.hasNext()) {
				mCurrentVisitor = fClassWriter.visitMethod(ACC_PUBLIC, "math" + mathExpressionCount, "()I", null, null);
				mCurrentVisitor.visitCode();
				mCurrentVisitor.visitInsn(ICONST_0);
				mCurrentVisitor.visitVarInsn(ISTORE, 1);
				
				this.visit((NodeSequence) statement.next());
				
				mCurrentVisitor.visitVarInsn(ISTORE, 1);
				mCurrentVisitor.visitVarInsn(ILOAD, 1);
				mCurrentVisitor.visitInsn(IRETURN);
				mCurrentVisitor.visitMaxs(20, 20);
				mCurrentVisitor.visitEnd();
				mathExpressionCount++;
			 }
		}
		
		writeClass();
		return null;
	}
	
	public Object visit(NodeSequence node) throws Exception {
		
		// Expression ;
		if (node.size() == 2) {
			this.visit((Expression)node.nodes.get(0));
		}
		
		// ( Expression )
		if (node.size() == 3) {
			this.visit((Expression)node.nodes.get(1));
		}
		
		return null;
	}
	
	public Object visit(Expression node) throws Exception {
		this.visit(node.f0);
		return null;
	}
	
	public Object visit(AdditiveExpression node) throws Exception {
		this.visit(node.f0);
		
		Iterator<INode> statment = node.f1.elements();
		
		while (statment.hasNext()) {
			NodeSequence ns = (NodeSequence) statment.next();
			NodeChoice nc = (NodeChoice) ns.nodes.get(0);
			
			this.visit((MultiplicativeExpression) ns.nodes.get(1));
			
			if (nc.choice.toString().equals("+")) {
				mCurrentVisitor.visitInsn(IADD);
			} else if (nc.choice.toString().equals("-")) {
				mCurrentVisitor.visitInsn(ISUB);
			}
		}
		
		return null;
	}
	
	public Object visit(MultiplicativeExpression node) throws Exception {
		this.visit(node.f0);
		
		Iterator<INode> statment = node.f1.elements();
		
		while (statment.hasNext()) {
			NodeSequence ns = (NodeSequence) statment.next();
			NodeChoice nc = (NodeChoice) ns.nodes.get(0);
			
			this.visit((UnaryExpression) ns.nodes.get(1));

			if (nc.choice.toString().equals("*")) {
				mCurrentVisitor.visitInsn(IMUL);
			} else if (nc.choice.toString().equals("/")) {
				mCurrentVisitor.visitInsn(IDIV);
			} else if (nc.choice.toString().equals("%")) {
				mCurrentVisitor.visitInsn(IREM);
			}
		}
		
		return null;
	}
	
	public Object visit(UnaryExpression node) throws Exception {
		
		if (node.f0.choice instanceof NodeSequence) {
			return this.visit((NodeSequence) node.f0.choice);
		} else if (node.f0.choice instanceof Identifier) {
			return this.visit((Identifier) node.f0.choice);
		} else if(node.f0.choice instanceof MyInteger) {
			return this.visit((MyInteger) node.f0.choice);
		}
		
		return null;
	}
	
	public Object visit(Identifier node) throws Exception {
		return null;
	}
	
	public Object visit(MyInteger node) throws Exception {
		mCurrentVisitor.visitLdcInsn(Integer.parseInt(node.f0.tokenImage));
		//mCurrentVisitor.visitVarInsn(LDC, Integer.parseInt(node.f0.tokenImage));
		//mCurrentVisitor.visitVarInsn(ISTORE, 1);
		return null;
	}
}
