package main;
//import main.Descriptor.AddressingMode;
//import main.Descriptor.VarType;

enum Inst_types
{ ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD , LOGICAL_AND, LOGICAL_OR, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL, EQUAL, NOT_EQUAL, ASSIGNMENT2 ,//3operands
	LOGICAL_NOT , UNARY_MINUS, ASSIGNMNET , JUMP_ZERO, JUMP_NOT_ZERO , 
	JUMP, JUMPI ,  WRITE_INTERGER, WRITE_FLOAT , WRITE_TEXT, READ_INTEGER, READ_FLOAT , PC_VALUE , SP_VALUE , ASSIGN_SP ,ASSIGN_SP_I, ERROR,//1 operands (28)
	RETURN, END,XX };
enum AddressingMode {Global_direct , Global_indirect , Local_direct , Local_indirect , Immidiate } ;
enum Variable_types {_int, _float , _boolean , _string , _char , _struct, _void};


class My_oprand {
	Variable_types type ; 
	AddressingMode addressingMode ; 
	int address ;
	int size;
	String value ;
	public boolean isAddressValue = false;
	private String [] abbr_type = { "i" , "f" , "b" , "s" , "c" , "struct","i"} ; 
	private String [] abbr_addMod = { "gd" , "gi" , "ld" , "li" , "im" } ;
	
	public My_oprand(){}
	
	public My_oprand(Descriptor desc) {
		this.type = desc.type ;
		this.size = desc.size;
    	this.address = desc.address ; 
    	this.addressingMode = desc.addrMode ;
    	this.value = desc.value ;
	}
	
	public String toString(){
		if(this.addressingMode == null || this.type == null){ 
			return "";
		}
		else if(type == Variable_types._struct)
			return (abbr_addMod[addressingMode.ordinal()] + "_" 
		+ this.size + "_" + (addressingMode == AddressingMode.Immidiate ? value : address  )) ;
		
		else 
			return (abbr_addMod[addressingMode.ordinal()] + "_" 
		+ abbr_type[type.ordinal()] + "_" + (addressingMode == AddressingMode.Immidiate ? value : address  )) ; 
		
	}
    

}
	
public class Instruction {

	private String [] operations = { "+" , "-" , "*" , "/" , "%" , "&&" , "||" , "<" , ">" ,"<=" , ">=" , "==" , "!=" , ":=c" , 
		"!" , "u-" , ":=" , "jz" , "jnz" ,"jmp" , "jmpi"  , "wi" , "wf" , "wt" , "ri" , "rf" , ":=pc" , ":=sp" , "sp:=" , "sp:=i" , "err", "ret" , "end","xx"} ;
	Inst_types type ;
	
	My_oprand opr1 , opr2 , opr3 ;
	private int Op_num ; 
	public Instruction(Inst_types type){
		this.type = type ;
		int ins_type_ind = type.ordinal();
		if(ins_type_ind <= 13){
			
			Op_num = 3;
			opr1 = new My_oprand();
			opr2 = new My_oprand();
			opr3 = new My_oprand();
		}
		else if (ins_type_ind <= 18){
			Op_num = 2;
			opr1 = new My_oprand();
			opr2 = new My_oprand();
		}
		else if(ins_type_ind <= 30){
			Op_num = 1;
			opr1 = new My_oprand();
		}
		else
			Op_num = 0;
		
	}
	
	//TODO yekar ba ina bokon!!!
	public boolean isComparison(){
		return ( this.type.ordinal() >=7 && type.ordinal()<=10);
	}
	
	public boolean isLogical(){
		return (this.type == Inst_types.LOGICAL_AND || this.type == Inst_types.LOGICAL_OR);
	}
	
	public boolean isOperation(){
		return ( type.ordinal() <=4) ; 
	}
	
	public boolean isEquality(){
		return (type == Inst_types.EQUAL || type == Inst_types.NOT_EQUAL) ;
	}
	
	public String toString(){
//		System.out.println(this.type.toString());		
		return operations[type.ordinal()] + " " 
						 + (opr1 == null? "" : opr1.toString() ) 
						 + (opr2!= null ? (" " +opr2.toString()) : "" ) 
						 + (opr3!= null ? (" " +opr3.toString()) : "" ) + "\n"; 
	}
	
	static public Inst_types str_to_insType(String op){
    	String [] operators = { "<" , ">" , "<=" , ">=" , "==" , "!=" , "+" , "-" , "*" , "/" , "%" , "&&" , "||"} ; 
    	Inst_types [] t = {Inst_types.LESS_THAN , Inst_types.GREATER_THAN , Inst_types.LESS_THAN_EQUAL , Inst_types.GREATER_THAN_EQUAL , Inst_types.EQUAL , Inst_types.NOT_EQUAL 
    				 , Inst_types.ADD , Inst_types.SUBTRACT , Inst_types.MULTIPLY , Inst_types.DIVIDE , Inst_types.MOD , Inst_types.LOGICAL_AND , Inst_types.LOGICAL_OR} ; 
    	for (int i = 0; i < operators.length; i++) {
			if ( op.equals(operators[i]))
				return t[i];
		}
    	return null ;
    }		
}
