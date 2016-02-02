package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import main.CodeGenerator;

public class SymbolTable {
	
	Map<String, Descriptor> map  = new HashMap<String , Descriptor>() ; 
	

	public String scopeName ; 
	
	public SymbolTable(String string) {
		this.scopeName = string ; 
	}
/*
	public void remove(int scope) {
		for (Entry<String, Descriptor> a: map.entrySet()){
			if(a.getValue().scop_num == scope){
				a.getValue().isValid = false ; 
			}
		}	
	}
*/
	public int getSize(String name) {
		return map.get(name).size ; 
	}
	
	@Override
	public String toString() {
		String str = "";
		for (Entry<String, Descriptor> entry : map.entrySet()) {
			String key = entry.getKey();
		    Descriptor d = entry.getValue();
		    str = str +  (" key : " + key + " des : " + d.size + "\n");
		}
		return str;

	}
	public SymbolTable get_copy(int address) throws Exception{
		SymbolTable s = new SymbolTable("stcopy " + this.scopeName+ " ");
		int tmp_adr = address;
		for (Entry<String, Descriptor> entry : map.entrySet()) {
			String key = entry.getKey();
		    Descriptor d = entry.getValue();
		    //d = d.get_copy(CodeGenerator.local_rel_add(d.size));
		    d = d.get_copy(tmp_adr);
		    tmp_adr += d.size;
		    s.add(key, d);
		}
		return s;
		
	}

	public void add(String name, Descriptor desc) throws Exception{
		if ( map.containsKey(name))
			throw new Exception("This " + name + " is previously declared in this scope") ;
		
		map.put(name, desc);
	} 

	public Descriptor find_desc(String name){
		if ( map.get(name) != null)
			return map.get(name);
		return null ;
		
	}

}
