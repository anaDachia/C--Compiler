package main;
import java.util.ArrayList;


public class Descriptor {

	public boolean aux = false; // a descriptor for internal use to show type or id in some stages of program
	public String name;
	public Variable_types type;  
	public int size ;
	public String value ;
	public AddressingMode addrMode ; 
	public int address ;
	public boolean isArray; 
	public int [] arr_dims ; 
	public SymbolTable struct_vars ;
	public String structType;
	public SymbolTable [] array_struct_vars = null;
	public Descriptor pointTo = null; // this is added for situations like this : Ana a[5]; a[4].x  where after computing
							   // a[4] we have a pointer to it not it self.
	
	public boolean is_function;
	public ArrayList<Descriptor> func_params;
	public SymbolTable func_st;
	//public Variable_types func_ret_type;
	public String func_ret_struct_type;
	public int func_pc;
	public int func_size;
	public boolean has_return = false;
	public boolean should_return = false;
	
	public Descriptor() {
	}
	
	public Descriptor get_copy(int adr) throws Exception{
		Descriptor des = new Descriptor("descopy "+ name + " ", false);
		des.type = this.type;
		des.size = this.size;
		des.value = this.value;
		des.addrMode = this.addrMode;
		des.address = adr;
		des.isArray = this.isArray;
		des.arr_dims = this.arr_dims;
		if(this.struct_vars != null)
			des.struct_vars = this.struct_vars.get_copy(adr);
		des.structType = this.structType;
		return des;
	}
	public int get_elm_size(){
		if ( !isArray){
			return size ;
		}
		
		return size/mul_amnt_in_arrDcl(-1); 
	}
	
	public Descriptor(String name, boolean aux){
		super();
		this.name = name;
		this.aux = aux;
	}

	public Descriptor(Variable_types type, int size, int address) {
		super();
		this.type = type;
		this.size = size;
		this.address = address;
	}
	public Descriptor(Variable_types type , int size){
		this.size = size ; 
		this.type = type ;
	}
	
	public static Descriptor getImmidiateDescriptor(int num){
    	Descriptor a = new Descriptor(Variable_types._int, 4);
    	a.value = num + "" ;
    	a.addrMode = AddressingMode.Immidiate ; 
    	return a ; 
    }
	
	public static Variable_types String_to_VarType (String type) {
    	String [] types = { "int" , "float" , "boolean" , "string" , "char", "void" } ; 
    	for (int i = 0; i < types.length; i++) {
			if ( type.equals(types[i]))
				return Variable_types.valueOf("_" + types[i]); 
		}
		return Variable_types._struct;
	}
	public static String VarType_to_String(Variable_types t){
		return (t.toString().split("_")[1]);
	}

	public int mul_amnt_in_arrDcl(int dim_use) {
		int result = 1 ;		
		for (int i = dim_use +1; i < arr_dims.length; i++) {
			result *= arr_dims[i] ;
		}
		return result;
	}

}
