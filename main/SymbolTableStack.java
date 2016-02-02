package main;

import java.util.Stack;

public class SymbolTableStack {
	private Stack<SymbolTable> stack = new Stack<SymbolTable>();
	
	public SymbolTable peek(){
		return stack.peek() ; 
	}
	
	public void push(SymbolTable table){
		stack.push(table);
	}
	
	public Descriptor getDescriptor ( String name) throws Exception{
		Descriptor result = null ;
		int count = stack.size()-1 ; 
		while ( result == null){
			if ( count <0){
				//return null;
				throw new Exception ("var " + name + " is not declared : (line" + Scanner.line + ")");
			}
			result = stack.get(count).find_desc(name);
			count -- ;
		}
		return result ; 
	}
	
	public SymbolTable pop(){
		return stack.pop() ; 
	}

	public SymbolTable getGlobalSymbolTable(){
		return stack.get(0);
	}

	public void pushGlobal(String nameOfMethod, Descriptor desc) throws Exception {
		stack.get(0).add(nameOfMethod, desc); 
		
	}
}
