package main;

import java.io.BufferedWriter;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Stack;

import javax.management.ImmutableDescriptor;

//import main.Descriptor.AddressingMode;
//import main.Descriptor.VarType;


class Loop_jumps{
	ArrayList<Integer> breaks = new ArrayList<>();
	ArrayList<Integer> continues = new ArrayList<>();
	int loop_start;
}


public class CodeGenerator 
{
    Scanner scanner; // This was my way of informing CG about Constant Values detected by Scanner, you can do whatever you like    
    private ArrayList<Instruction> ins_list = new ArrayList<Instruction>() ; 
    Stack<Descriptor> seman_stack = new Stack<Descriptor>() ;
    SymbolTableStack stStack = new SymbolTableStack() ; 
    int global_arr_dic_dim; 
    private int globalAddress = 0;
    private static int curr_local_adr = 12 ; 
    private int curr_struct_rel_add ; 
    private boolean in_struct_decl ; 
    boolean Global = true ; 
	public SymbolTable allTypes;
    int tempCount = 0 ; 
    private int main_adr = -1;
    private boolean glob_set = false;
    
    ArrayList<Integer> decl_dim = new ArrayList<Integer>();
    ArrayList<Integer> wait_pc = new ArrayList<Integer>();
    ArrayList<Loop_jumps> loop_stack = new ArrayList<Loop_jumps>();
    ArrayList<Descriptor>passed_arg = new ArrayList<>();
    ArrayList<Integer> local_addreses = new ArrayList<>();
    
    public CodeGenerator(Scanner scanner) throws Exception
    {
        this.scanner = scanner;
        stStack.push(new SymbolTable("global"));
        make_init_commands();
    }

    
   
	public void Generate(String sem) throws Exception
    {
			if ( sem.equals("NoSem")) return ; 
			else if (sem.equals("@push")) push() ;
			else if( sem.equals("@struct_begin")) struct_beg() ; 
            else if( sem.equals("@struct_end")) struct_end() ; 
			else if( sem.equals("@unary_minus")) unary_minus();
            else if( sem.equals("@not")) not();
            else if( sem.equals("@or")) or();
            else if( sem.equals("@and")) and();
            else if( sem.equals("@equal")) equal();
            else if( sem.equals("@notEqual")) notEqual();
            else if( sem.equals("@grt")) grt();
            else if( sem.equals("@le")) le();
            else if( sem.equals("@less")) less();
            else if( sem.equals("@ge")) ge();
            else if( sem.equals("@add")) add();
            else if( sem.equals("@minus")) minus();
            else if( sem.equals("@mod")) mod();
            else if( sem.equals("@div")) div();
            else if( sem.equals("@mult")) mult();
            else if( sem.equals("@arr_begin")) array_begin() ;
            else if( sem.equals("@block_begin")) block_begin() ; 
            else if( sem.equals("@block_end")) block_end() ;
            else if( sem.equals("@readint")) read_int();
            else if( sem.equals("@readfloat")) read_float();
            else if( sem.equals("@writefloat")) writeFloat();
            else if( sem.equals("@writeint")) writeInt();
            else if( sem.equals("@writetext")) writeText();
            else if( sem.equals("@location_push")) push_location();
            else if( sem.equals("@check_struct_type")) check_if_struct() ;
            else if( sem.equals("@push_int")) literal_push_int() ;
            else if( sem.equals("@push_float")) literal_push_float() ;
            else if( sem.equals("@push_bool")) literal_push_bool() ;
            else if( sem.equals("@push_string")) literal_push_string() ;
            else if( sem.equals("@push_char")) literal_push_char() ;
            else if( sem.equals("@var_decl")) decl() ; 
            else if( sem.equals("@var_decl_camma")) decl_camma() ;
            else if( sem.equals("@arr_decl")) array_decl(); 
            else if( sem.equals("@arr_decl_camma")) array_decl_camma() ;
            else if( sem.equals("@arr_dim")) array_dim() ; 
            else if( sem.equals("@location_arr")) loc_arr_end();
            else if( sem.equals("@arr_begin_loc")) loc_arr_begin();
            else if( sem.equals("@check_arr_dim")) check_arr_dim();
            else if( sem.equals("@add_arr_dim")) loc_arr_dim();
            else if( sem.equals("@chain_end")) dot_chain_end();
            else if( sem.equals("@assignment")) assign() ;
			
			
            else if( sem.equals("@jump_zero")) jump_zero() ;
            else if( sem.equals("@complete_jz")) comp_jz();
            else if( sem.equals("@if_jump")) if_jump();
            else if( sem.equals("@complete_jump")) comp_jump();
			
            else if( sem.equals("@beg_while")) beg_while();
            else if( sem.equals("@complete_jz_while")) comp_jz_while();
			
            else if( sem.equals("@break")) break_loop();
            else if( sem.equals("@continue")) continue_loop();
			
            else if( sem.equals("@new_method")) new_method();
            else if( sem.equals("@push_param")) push_param();
            else if( sem.equals("@end_param")) end_param();
            else if( sem.equals("@next_param")) next_param();
            else if( sem.equals("@end_method")) end_method();
            else if( sem.equals("@return_void_jmp")) return_void_jmp();
            else if( sem.equals("@return_jmp")) return_jmp();
			
            else if( sem.equals("@method_call")) method_call();
            else if( sem.equals("@run_method")) run_method();
            else if( sem.equals("@run_method_ret")) run_method_ret();
            else if( sem.equals("@set_param")) set_param();
            else if( sem.equals("@next_param_call")) next_param_call();
            else if( sem.equals("@jump_call")) jump_call();
			
            else if( sem.equals("@set_main_adr")) set_main_adr();
            else if( sem.equals("@finish_main")) finish_main();
			
            else if( sem.equals("@push_cond")) push_cond();
            else if( sem.equals("@for_expr")) for_expr();
            else if( sem.equals("@insert_x")) insert_x();
            else if( sem.equals("@remove_x")) remove_x();
            else if( sem.equals("@complete_for")) comp_for();             
    }


////////////////////////////////for :D //////////////////////////////////////	

ArrayList< Integer> forStack = new ArrayList<>();
	
	private void push_cond() {
		Loop_jumps lj = new Loop_jumps();
		loop_stack.add(lj);
		//forStack.add(ins_list.size() - 1);
	}
	
	
	
	private void for_expr() throws Exception {
		forStack.add(ins_list.size());
		Descriptor cond = seman_stack.pop();
		if(cond.aux == true){
			try{
				cond = stStack.getDescriptor(cond.name);
			}
			catch (Exception e){
				gen_error("variable u used as the condition in for is not defined before");
			}
		}
		if(cond.type != Variable_types._boolean)
			gen_error("second argument in for should be a boolean expression");
		/////////////////////////////////////////
		Instruction ins = create_incomp_jz(cond);
		ins_list.add(ins);
		loop_stack.get(loop_stack.size() - 1).loop_start = ins_list.size() - 1;
		wait_pc.add(ins_list.size() -1 );
		
		ins = create_incomp_jp();
		ins_list.add(ins);
		wait_pc.add(ins_list.size() -1 );
	}
	
	
	
	private void insert_x() {
		forStack.add(ins_list.size()); //aval e assign akhar!
		//System.out.println("ana in inser" + ins_list.size() );
	}
	
	
	
	private void remove_x() throws Exception {
		Descriptor d1 =  new Descriptor();
		int tmp = forStack.remove(forStack.size()- 1) ;
		d1.value = forStack.remove(forStack.size()- 1) - 1 + "";
		//System.out.println(d1.value);
		d1.addrMode = AddressingMode.Immidiate;
		d1.type = Variable_types._int;
		make_one_opr_ins(d1, Inst_types.JUMP);
		
		Instruction ins = ins_list.get(wait_pc.remove(wait_pc.size() - 1));
		ins.opr1 = new My_oprand();
		ins.opr1.value = ins_list.size() + "";
		ins.opr1.type = Variable_types._int;
		ins.opr1.addressingMode = AddressingMode.Immidiate;
		forStack.add(tmp);
			
	}
	
	
	
	private void comp_for() throws Exception {
		//System.out.println("ana in comp_for-----------");
		int final_assign_pc = forStack.remove(forStack.size() - 1);
		//System.out.println(final_assign_pc);
		//System.out.println(forStack.get(forStack.size() - 1));
		
		Descriptor d1 =  new Descriptor();
		d1.value = final_assign_pc + "";
		d1.addrMode = AddressingMode.Immidiate;
		d1.type = Variable_types._int;
		make_one_opr_ins(d1, Inst_types.JUMP);
		
		int incop_jz_pc = wait_pc.remove(wait_pc.size() - 1);
		Instruction ins = ins_list.get(incop_jz_pc);

		ins.opr2 = new My_oprand();
		ins.opr2.value = ins_list.size() + "";
		ins.opr2.type = Variable_types._int;
		ins.opr2.addressingMode = AddressingMode.Immidiate;
		
		
		Loop_jumps lj = loop_stack.remove(loop_stack.size() - 1);
		for (int i = 0; i < lj.breaks.size(); i++) {
			ins  = ins_list.get(lj.breaks.get(i));
			ins.opr1 = new My_oprand();
			ins.opr1.value = ins_list.size() + "";
			ins.opr1.type = Variable_types._int;
			ins.opr1.addressingMode = AddressingMode.Immidiate;
		}
		
		for (int i = 0; i < lj.continues.size(); i++) {
			ins  = ins_list.get(lj.continues.get(i));
			ins.opr1 = new My_oprand();
			ins.opr1.value = final_assign_pc + "";
			ins.opr1.type = Variable_types._int;
			ins.opr1.addressingMode = AddressingMode.Immidiate;
		}
		
		}


	private void beg_while() {
		Loop_jumps lj = new Loop_jumps();
		lj.loop_start = ins_list.size() - 1;
		loop_stack.add(lj);
	}

	private void return_jmp() throws Exception {
		
		//check errors
		Descriptor return_val = seman_stack.pop();
		if(return_val.aux == true){
			try{
				return_val = stStack.getDescriptor(return_val.name);
			}
			catch (Exception e){
				gen_error("returned value is not defined in this scope");
			}
				
		}
		
		Descriptor func = seman_stack.peek();
		if(func.type == Variable_types._void)
			gen_error("cannot return value from a void function");
		else if(return_val.type != func.type){
			gen_error("the function " + func.name + " should return a value of type " + return_val.type);
		}
		else
			func.has_return = true;
		
		//return value
		Descriptor tmp = new Descriptor();
		tmp.address = globalAddress;
		tmp.addrMode = AddressingMode.Global_direct;
		tmp.size = func.size;
		tmp.structType = func.structType;
		tmp.type = func.type; 
		tmp.struct_vars = func.struct_vars;
		
		
		make_two_opr_ins(return_val, Inst_types.ASSIGNMNET
				, tmp);
		
		
		//jump to pc
		Descriptor jmp = new Descriptor();
		jmp.addrMode = AddressingMode.Local_direct;
		jmp.address = 4;
		jmp.type = Variable_types._int;
		jmp.size = 4;
		make_one_opr_ins(jmp, Inst_types.JUMP);
		
	}



	private void return_void_jmp() throws Exception {
		Descriptor func = seman_stack.peek();
		if (func.name .equals("$main"))
			return;
		Descriptor jmp = new Descriptor();
		jmp.addrMode = AddressingMode.Local_direct;
		jmp.address = 4;
		jmp.type = Variable_types._int;
		jmp.size = 4;
		make_one_opr_ins(jmp, Inst_types.JUMP);
		func.has_return = true;
	}



	private void finish_main() {
		//Instruction ins  = ins_list.get(0);
		//ins.opr1.value = curr_local_adr + "";
		
	}

	private void set_main_adr() throws Exception {
		make_one_opr_ins(Descriptor.getImmidiateDescriptor(globalAddress + 4), Inst_types.ASSIGN_SP);
		curr_local_adr = 0;
		main_adr = ins_list.size()-1;
		seman_stack.pop(); //pop void
		Descriptor d = new Descriptor("$main", false);
		d.type = Variable_types._void;
		seman_stack.push(d);
	}



	private void method_call() throws Exception {
		
		passed_arg.clear();
		Descriptor method_name = seman_stack.pop();
		if(method_name.aux == true){
			
			method_name.aux = false;
			try{
				method_name = stStack.getDescriptor(method_name.name);
			
				seman_stack.push(method_name);
			}
			catch (Exception e){
				gen_error("the function" + method_name.name +" is not defined");
			}
		}
		else
			seman_stack.push(method_name);
	}

	private void set_param() throws Exception {
		
		Descriptor pass_param = seman_stack.pop();
		if(pass_param.aux){
			pass_param.aux = false;
			try{
				pass_param = stStack.getDescriptor(pass_param.name);	
				//seman_stack.push(pass_param);
			}
			catch (Exception e){
				gen_error("the operand" + pass_param.name +" is not defined");
			}
		}	
		String str_type = Descriptor.VarType_to_String(pass_param.type);
		if (pass_param.type == Variable_types._struct)
			str_type = pass_param.structType;
		//System.out.println(str_type + " " + pass_param.name + " " + seman_stack.peek().name + " " + passed_arg.size());
		Descriptor lhs = make_temp_desc(str_type);
		make_two_opr_ins(pass_param, Inst_types.ASSIGNMNET, lhs);
		
		passed_arg.add(lhs);
		if(passed_arg.size() > seman_stack.peek().func_params.size())
			gen_error("you passed excessive arguments to the function :" + seman_stack.peek().name);
		if(pass_param.type != seman_stack.peek().func_params.get(passed_arg.size() - 1).type)
			gen_error("the type of " + passed_arg.size() + "th parameter is wrong");
	
		
	}



	private void jump_call() throws Exception {
		
		
		Descriptor method = seman_stack.peek();
		
		if(passed_arg.size() < method.func_params.size())
			gen_error("this function requires " + method.func_params.size() + " parameters to be passed but you passed " 
		+ passed_arg.size());
		//////////////////////////////////////////////////////////////////////////////
		Descriptor saveSP = make_temp_desc("int");
		make_one_opr_ins(saveSP, Inst_types.SP_VALUE); // save sp at the first step
		
		//sp assign add jmp 
		Descriptor imm_PC = Descriptor.getImmidiateDescriptor(ins_list.size() + 4 + passed_arg
				.size());
		
		//save pc
		Descriptor savePC = make_temp_desc("int");
		Instruction ins  = new Instruction(Inst_types.ASSIGNMNET); 
		ins.opr1 = new My_oprand(imm_PC);
		ins.opr2 = new My_oprand(savePC);
		ins_list.add(ins);
		
		
		//new sp : 12 for hammin 3 ta balaE ha!
		Descriptor new_SP = make_temp_desc("int");
		make_three_opr_inst(saveSP, 
				Descriptor.getImmidiateDescriptor(curr_local_adr - 12), Inst_types.ADD, new_SP);
		
		
		for (int i = 0; i < passed_arg.size(); i++) {
			String type_str = Descriptor.VarType_to_String(passed_arg.get(i).type);
			if(passed_arg.get(i).type == Variable_types._struct){
				type_str = passed_arg.get(i).structType;
			}
			make_two_opr_ins(passed_arg.get(i), Inst_types.ASSIGNMNET, 
					make_temp_desc(type_str));
		}
		
		//set new sp
		make_one_opr_ins(new_SP, Inst_types.ASSIGN_SP);
	
		//jump to function
		int jmp_address = seman_stack.peek().func_pc;
		Descriptor jmp_adr_desc = Descriptor.getImmidiateDescriptor(jmp_address);
		make_one_opr_ins(jmp_adr_desc, Inst_types.JUMP);
		
		//retrieve sp
		Descriptor retrieveSP = new Descriptor("_sp", false);
		retrieveSP.address = 0;
		retrieveSP.addrMode = AddressingMode.Local_direct;
		retrieveSP.type = Variable_types._int;
		retrieveSP.size = 4;
		make_one_opr_ins(retrieveSP , Inst_types.ASSIGN_SP);
		
		local_addreses.add(curr_local_adr);
		
	}


//check kardan tedad o ina...
	private void next_param_call() {
		
	}



	private Descriptor make_temp_desc(String type){
		Descriptor des = new Descriptor(get_tmpName(),false);
		des.address = local_rel_add(allTypes.getSize(type));
		des.addrMode = AddressingMode.Local_direct;
		des.size = allTypes.getSize(type); 
		des.type = Descriptor.String_to_VarType(type);
		des.structType = type;
		if(des.type == Variable_types._struct)
			des.struct_vars = allTypes.find_desc(type).struct_vars;
		return des;
		
	}
	
	private void make_one_opr_ins(Descriptor d1, Inst_types type) throws Exception {
	Instruction ins = new Instruction(type) ;
	ins.opr1 = new My_oprand(d1);
	ins_list.add(ins);	
}
	
	//in ke agar void bashad kolan kari nakonad ezafe shod!
	private void run_method() throws Exception {
		if(seman_stack.peek().type == Variable_types._void)
			return;
		Descriptor res = make_temp_desc(seman_stack.peek().func_ret_struct_type);
		Descriptor saved_res = new Descriptor();
		saved_res.address = globalAddress;
		saved_res.addrMode=AddressingMode.Global_direct;
		saved_res.type = seman_stack.peek().type; //func_ret_type;
		saved_res.structType = seman_stack.peek().structType;
		saved_res.size = seman_stack.peek().size;
		saved_res.struct_vars = seman_stack.peek().struct_vars;
		seman_stack.pop();//function desc
		make_two_opr_ins(saved_res, Inst_types.ASSIGNMNET, res);
		//seman_stack.push(res);
	}


	private void run_method_ret() throws Exception {
		if(seman_stack.peek().type == Variable_types._void)
			return;
		Descriptor res = make_temp_desc(seman_stack.peek().func_ret_struct_type);
		Descriptor saved_res = new Descriptor();
		saved_res.address = globalAddress;
		saved_res.addrMode=AddressingMode.Global_direct;
		saved_res.type = seman_stack.peek().type; //func_ret_type;
		saved_res.structType = seman_stack.peek().structType;
		saved_res.size = seman_stack.peek().size;
		saved_res.struct_vars = seman_stack.peek().struct_vars;
		seman_stack.pop();//function desc
		make_two_opr_ins(saved_res, Inst_types.ASSIGNMNET, res);
		seman_stack.push(res);
		
	}



	private void new_method() throws Exception {
		Descriptor method_name = seman_stack.pop();
		if(method_name.aux == true){
			try{
				method_name = stStack.getDescriptor(method_name.name);
				gen_error("this function name already exists");
			}
			catch (Exception e){
				curr_local_adr = 12; //pc,sp,excessive //TODO check this
				
				method_name.aux = false;
				String ret_type = seman_stack.pop().name;
				method_name.is_function = true;
				//method_name.func_ret_type = Descriptor.String_to_VarType(ret_type);
				method_name.type = Descriptor.String_to_VarType(ret_type);
				method_name.func_ret_struct_type = ret_type;
				method_name.structType = ret_type;
				method_name.func_params = new ArrayList<Descriptor>();
				method_name.func_pc = ins_list.size();
				if(method_name.type == Variable_types._struct)
					method_name.struct_vars = allTypes.find_desc(ret_type).struct_vars;
				method_name.size = allTypes.getSize(ret_type);
				
				if(method_name.type != Variable_types._void)
					method_name.should_return = true;
				seman_stack.push(method_name);
				stStack.pushGlobal(method_name.name, method_name);
				
				//pushing param symboltable
				SymbolTable func_st = new SymbolTable("_func:" + method_name.name); 
				stStack.push(func_st);
				method_name.func_st = func_st; 
			}
		}
	}


	private void push_param() throws Exception {
		String param_name = Scanner.CV;
		String type = seman_stack.pop().name;
		Descriptor var = new Descriptor(param_name, false);
		var.size = allTypes.getSize(type);
		var.address  = local_rel_add(var.size);  
		var.type = Descriptor.String_to_VarType(type);
		if(var.type == Variable_types._struct){
			var.structType = type;
			var.struct_vars = allTypes.find_desc(type).struct_vars;
		}
		var.addrMode = AddressingMode.Local_direct;
		stStack.peek().add(param_name, var);
		stStack.getGlobalSymbolTable().find_desc(seman_stack.peek().name).func_params.add(var);
	}



	private void end_param() {	
	}


//felan hichi...
	private void next_param() {
		
	}


	//TODO addresslist ra dorost konam
	private void end_method() throws Exception {	
		Descriptor func = seman_stack.peek();
		if(func.should_return && !func.has_return)
			gen_error("this funcion should return a value");
		
		if(func.type == Variable_types._void ){
			if(!func.has_return){
				Descriptor ret = new Descriptor();
				ret.address = 4;
				ret.addrMode = AddressingMode.Local_direct;
				ret.type = Variable_types._int;
				make_one_opr_ins(ret, Inst_types.JUMP);
			}
		}
		else
		{
			Descriptor err = new Descriptor();
			err.value= 3 + "";
			err.addrMode = AddressingMode.Immidiate;
			err.type = Variable_types._int;
			make_one_opr_ins(err, Inst_types.JUMP);	
		}
		
		stStack.pop();
		seman_stack.pop().func_size = curr_local_adr; 
	}



	private void continue_loop() {
		Instruction ins = new Instruction(Inst_types.JUMP);
		ins_list.add(ins);
		loop_stack.get(loop_stack.size() - 1).continues.add(ins_list.size() - 1);
	}



	private void break_loop() {
		Instruction ins = new Instruction(Inst_types.JUMP);
		ins_list.add(ins);
		loop_stack.get(loop_stack.size() - 1).breaks.add(ins_list.size() - 1);
	}


	private void do_nothing_ins(My_oprand m){
		Instruction ins = new Instruction(Inst_types.ASSIGNMNET);
		ins.opr1 = new My_oprand();
		ins.opr1.address = 0;
		ins.opr1.addressingMode = AddressingMode.Global_direct;
		ins.opr1.type = Variable_types._int;
		
		ins.opr2 = new My_oprand();
		ins.opr2.address = 0;
		ins.opr2.addressingMode = AddressingMode.Global_direct;
		ins.opr2.type = Variable_types._int;
		if(m != null){
			ins.opr1 = m;
			ins.opr2 = null;
		}
		ins_list.add(ins);
	}
	private void comp_jz_while() {
		
		// adding the jump command and also compeleting jz. a null command is added after the loop just to control the flow
		int jz_pc = wait_pc.get(wait_pc.size() - 1); //on top of wait_pc is the jz of while loop and we jump to it at the end
		Instruction ins = new Instruction(Inst_types.JUMP);
		ins.opr1 = new My_oprand();
		ins.opr1.value = loop_stack.get(loop_stack.size() - 1).loop_start +1  + "";
		ins.opr1.addressingMode = AddressingMode.Immidiate;
		ins.opr1.type = Variable_types._int;
		ins_list.add(ins);
		do_nothing_ins(null);
		
		
		int ind = wait_pc.get(wait_pc.size() - 1);
		Instruction to_be_comp = ins_list.get(wait_pc.remove(wait_pc.size() - 1));
		int supposed_pc = ins_list.size() - 1 ;
		to_be_comp.opr2 = new My_oprand();
		to_be_comp.opr2.value = supposed_pc +"";
		to_be_comp.opr2.addressingMode = AddressingMode.Immidiate;
		to_be_comp.opr2.type = Variable_types._int; 
		//System.out.println("intu umad while jz");
		
		
		//doing all the related things with breaks and continues
		Loop_jumps lj = loop_stack.remove(loop_stack.size() - 1);
		supposed_pc = ins_list.size() - 1 ;
		
		for (int i = 0; i < lj.breaks.size(); i++) {
			ins  = ins_list.get(lj.breaks.get(i));
			ins.opr1 = new My_oprand();
			ins.opr1.value = supposed_pc + "";
			ins.opr1.type = Variable_types._int;
			ins.opr1.addressingMode = AddressingMode.Immidiate;
		}
		
		for (int i = 0; i < lj.continues.size(); i++) {
			ins  = ins_list.get(lj.continues.get(i));
			ins.opr1 = new My_oprand();
			ins.opr1.value = lj.loop_start +1 + "";
			//System.out.println(ins.opr1.value);
			//System.out.println(lj.loop_start + " " );
			ins.opr1.type = Variable_types._int;
			ins.opr1.addressingMode = AddressingMode.Immidiate;
		}
		
	}



	private void jump_zero() throws Exception {
		Descriptor expr = seman_stack.pop();
		if(expr.isArray || expr.type != Variable_types._boolean)
			gen_error("the expression inside if should be of type boolean");
		else{
			Instruction ins = create_incomp_jz(expr);
			ins_list.add(ins);
			wait_pc.add(ins_list.size() - 1);
		}	
	}

	private Instruction create_incomp_jz(Descriptor expr) {
		Instruction ins = new Instruction(Inst_types.JUMP_ZERO);
		ins.opr1 = new My_oprand(expr);
		return ins;
	}
// here i changed the instruction to print correct value of address (by using "isAddressValue")
	private void comp_jz() {
		Instruction to_be_comp = ins_list.get(wait_pc.remove(wait_pc.size() - 1));
		int supposed_pc = ins_list.size();
		if (scanner.CV.equals("else"))
			supposed_pc++;
		to_be_comp.opr2 = new My_oprand();
		to_be_comp.opr2.value = supposed_pc + "";
		to_be_comp.opr2.type = Variable_types._int;
		to_be_comp.opr2.addressingMode = AddressingMode.Immidiate;
				
	}

	private void if_jump() {
		Instruction ins = create_incomp_jp();
		ins_list.add(ins);
		wait_pc.add(ins_list.size() - 1);
	}

	private Instruction create_incomp_jp() {
		Instruction ins = new Instruction(Inst_types.JUMP);
		return ins;
	}

	private void comp_jump() {
		Instruction to_be_comp = ins_list.get(wait_pc.remove(wait_pc.size() - 1));
		int supposed_pc = ins_list.size();
		to_be_comp.opr1 = new My_oprand();
		to_be_comp.opr1.value = supposed_pc + "";
		to_be_comp.opr1.addressingMode = AddressingMode.Immidiate;
		to_be_comp.opr1.type = Variable_types._int;
		
	}



	private void push(){
		Descriptor aux = new Descriptor(Scanner.CV, true); //type or id
		seman_stack.push(aux);
    }

	
	/**
	 * every time a dot is ended its sym table is popped 
	 */
	private void dot_chain_end() {
		stStack.pop();
	}
	
	/**
	 * we suppose that struct name has been pushed in the seman stack!
	 * checks if the id is of struct type and then pushes its symbol table on the seman stack
	 * @throws Exception
	 */
	private void check_if_struct() throws Exception {
		//System.out.println("!!!!!!!!!!-----------------------------------------------------------");
		Descriptor struct_desc = seman_stack.peek();
		/** this is used when in "a.b" a is pushed by a simple push not location_push*/
		if(struct_desc.aux == true){
			struct_desc = stStack.getDescriptor(struct_desc.name);
			seman_stack.pop();
			seman_stack.push(struct_desc);
		
		}
		if(struct_desc.type != Variable_types._struct)
			gen_error("dot is only acceptble in front of an struct type");
		else{
			if(struct_desc.pointTo == null){
				stStack.push(struct_desc.struct_vars);
			//	System.out.println("inja pushs shod " + struct_desc.name +" "+ struct_desc.struct_vars);
			}
			else{

				int atrina; // null nabud yani az loc_arr_end haman pointer dar stack mande ast!!!
				//in fln ghalat ast yani baraye hame khane haye array yek struct var gerefte am!!
				stStack.push(struct_desc.pointTo.struct_vars);
				
			}
		}
	}

	/**
	 * should push the new descriptor and pop previous (push a.b and pop a)!
	 * we suppose that the struct symbol table is on top of stStack when this func is called
	 * @throws Exception
	 */
	private void push_location() throws Exception {
		
    	String struct_var_name = Scanner.CV;
    	
  
    	Descriptor struct_var_des = stStack.peek().find_desc(struct_var_name);
    	if(struct_var_des == null)
    		gen_error(String.format("the variable %s is not declared in this struct type", struct_var_name));
    	else{

    		Descriptor base = seman_stack.pop();
    		if(base.type != Variable_types._struct || base.is_function){
    			
    			seman_stack.push(base);
    			seman_stack.push(struct_var_des);
    			return;
    		}
    		//System.out.println(base.name + " base.name " + base.address);
    			
    		Descriptor final_addr = new Descriptor(get_tmpName(), false);
    		final_addr.size = struct_var_des.size;
    		final_addr.address = local_rel_add(4);
    		final_addr.type = Variable_types._int;
    		final_addr.structType = struct_var_des.structType;
    		final_addr.addrMode = AddressingMode.Local_direct;
    		final_addr.struct_vars = struct_var_des.struct_vars;
    		final_addr.isArray = struct_var_des.isArray;
    		final_addr.arr_dims = struct_var_des.arr_dims;
    		
    	
    		Descriptor first_des = new Descriptor();
    		
    		if (base.addrMode == AddressingMode.Local_direct || base.addrMode == AddressingMode.Global_direct)
    			first_des = Descriptor.getImmidiateDescriptor(base.address);
    		
    		else if(base.addrMode == AddressingMode.Local_indirect || base.addrMode == AddressingMode.Global_indirect){
    			first_des.type = Variable_types._int;
    			first_des.size = 4;
    			first_des.address = base.address;
    			first_des.addrMode = AddressingMode.Local_direct;
    		}
    		
    		make_three_opr_inst(first_des, 
    				Descriptor.getImmidiateDescriptor(struct_var_des.address), Inst_types.ADD, final_addr);
    		
    		final_addr.type = struct_var_des.type;
    		if(base.addrMode == AddressingMode.Global_direct || base.addrMode == AddressingMode.Global_indirect)
    			final_addr.addrMode = AddressingMode.Global_indirect;
    		else if(base.addrMode == AddressingMode.Local_direct || base.addrMode == AddressingMode.Local_indirect)
    			final_addr.addrMode = AddressingMode.Local_indirect;
    		
    		//System.out.println(final_addr.name);
    		seman_stack.push(final_addr);
    
    	
    	}
	}
	
	
	/**
	 * we push the vars sym table to let declarations happen there
	 * @throws Exception
	 */
    private void struct_beg() throws Exception{
		Descriptor desc = new Descriptor();
		desc.type = Variable_types._struct;    	
		desc.struct_vars = new SymbolTable("struct: " + seman_stack.peek().name) ; 
		allTypes.add(seman_stack.peek().name, desc) ;
		//TODO remember to adjust name in descriptor whereever needed!!!!!!
		stStack.push(desc.struct_vars);
		in_struct_decl = true ;
		curr_struct_rel_add = 0;
		 
	}

	private void struct_end(){
		in_struct_decl = false ;
		String name = seman_stack.pop().name ;
		allTypes.find_desc(name).size = curr_struct_rel_add ;
		curr_struct_rel_add = 0;
		stStack.pop();
	}



	
	/**
	 * set name address size type admod aux struct_type
	 * @return
	 * @throws Exception
	 */
	private String decl() throws Exception{
		
		Descriptor new_desc = new Descriptor(seman_stack.pop().name, false);
		String new_var_name = new_desc.name;
		String type = seman_stack.pop().name ;
		int size = allTypes.getSize(type) ;
		int address = set_add_decl(size);
		//////////set var ///////
		new_desc.size = size;
		new_desc.address = address;
		new_desc.type = Descriptor.String_to_VarType(type);
		
		if(stStack.peek().scopeName .equals("global")){
			new_desc.addrMode = AddressingMode.Global_direct;
		}
		else new_desc.addrMode = AddressingMode.Local_direct;
		
		if ( new_desc.type == Variable_types._struct){
			int a;
			//System.out.println("ana in decl ast va " + allTypes.find_desc(type).struct_vars);
			new_desc.struct_vars = allTypes.find_desc(type).struct_vars;//.get_copy(new_desc.address);
			new_desc.structType = type ;
		}	
		
		/**here current symbol table is struct vars in its descriptor! or global sym table or block symT */
		stStack.peek().add(new_var_name, new_desc); // TODO check if these are added to alltypes desc.structvars too
		//new_desc.scop_num = scopeCount ; //TODO check if this is needed.
		return type ; 
	}

	
	private int set_add_decl(int size){
    	int address = 0;
		if(in_struct_decl){
			address = curr_struct_rel_add;
			curr_struct_rel_add += size;
			
		}
		else if(stStack.peek().scopeName.equals("global")){
			address = globalAddress;
			globalAddress += size;
		}
		else
			return local_rel_add(size);
		return address;
    }
	
  
     
    public static int local_rel_add( int size){
    	int address = curr_local_adr;
    	curr_local_adr = curr_local_adr + size;
    	return address; 
    }
    
    private void block_begin(){
    	SymbolTable st  = new SymbolTable("block");
    	stStack.push(st);
    	//scopeNum.add(scopeCount) ; // TODO see if this is necessary! if yes check last project!
    }
    
    private void block_end(){
    	stStack.pop();
    }
    

	private void literal_push_char() throws Exception {
		String name = get_tmpName();
		Descriptor desc = new Descriptor(Scanner.CV, false);
		desc.type = Variable_types._string;
		desc.addrMode = AddressingMode.Immidiate;
		desc.value = Scanner.CV.charAt(0) + " ";
		desc.size = 1;
		//desc.address = local_rel_add(desc.size);
		stStack.peek().add(name, desc);
		seman_stack.push(desc);		
	}



	private void literal_push_bool() throws Exception {
		String name = get_tmpName();
		Descriptor desc = new Descriptor(Scanner.CV, false);
		desc.type = Variable_types._boolean;
		desc.addrMode = AddressingMode.Immidiate;
		desc.value = Scanner.CV;
		desc.size = 1;
		//desc.address = local_rel_add(desc.size);
		stStack.peek().add(name, desc);
		seman_stack.push(desc);
		
	}



	private void literal_push_float() throws Exception {
		String name = get_tmpName();
		Descriptor desc = new Descriptor(Scanner.CV, false);
		desc.type = Variable_types._float;
		desc.addrMode = AddressingMode.Immidiate;
		desc.value = Scanner.CV;
		desc.size = 4;
		//desc.address = local_rel_add(desc.size);
		stStack.peek().add(name, desc);
		seman_stack.push(desc);
	}


/**
 * type size admod value name
 * @throws Exception
 */
	private void literal_push_int() throws Exception {
		String name = get_tmpName() ;
		Descriptor desc = new Descriptor(Scanner.CV, false); //TODO check this
		desc.type = Variable_types._int;
		desc.addrMode = AddressingMode.Immidiate;
		desc.value = Scanner.CV;
		desc.size = 4;
		//desc.address = local_rel_add(desc.size);
		stStack.peek().add(name, desc);
		seman_stack.push(desc);
	}
	
	private void literal_push_string() throws Exception {
		String name = get_tmpName();
		Descriptor desc = new Descriptor(Scanner.CV, false);
		desc.type = Variable_types._string;
		desc.addrMode = AddressingMode.Immidiate;
		desc.value = Scanner.CV;
		desc.size = 100;
		//desc.address = local_rel_add(desc.size);
		stStack.peek().add(name, desc);
		seman_stack.push(desc);
	}

    
    
	private void gen_error(String string) throws Exception {
		throw new Exception( String.format("error in line %d--> %s \n", Scanner.line, string));
	}



	private void assign() throws Exception{
		//System.out.println("in assignment ---------------------------------------------------------------");
		// TODO check if right hand side is array or pointer and so on
		Descriptor rhs_des = seman_stack.pop();
		if(rhs_des.aux == true){
			rhs_des = stStack.getDescriptor(rhs_des.name);
		}
		Descriptor lhs_des = seman_stack.pop();		

		if(lhs_des.aux == true){
			try {
				lhs_des = stStack.getDescriptor(lhs_des.name);
			} catch (Exception e) {
				lhs_des = new Descriptor(lhs_des.name, false);
				lhs_des.type = rhs_des.type;
				if(rhs_des.type == Variable_types._void)
					gen_error("void function has no result to be assigned");
				lhs_des.size = rhs_des.size;
				lhs_des.addrMode = AddressingMode.Local_direct;
				lhs_des.address = local_rel_add(rhs_des.size);
				if(rhs_des.isArray){
					lhs_des.isArray = true;
					lhs_des.arr_dims = rhs_des.arr_dims;
				}
				if(rhs_des.type == Variable_types._struct){
					int b;
					lhs_des.struct_vars = rhs_des.struct_vars;//.get_copy(lhs_des.address);
					
					lhs_des.structType = rhs_des.structType;
				}
				stStack.peek().add(lhs_des.name, lhs_des);
			}
			
		}
		
		checkAssignment(rhs_des, lhs_des); 
		int copy_size = get_copy_size (rhs_des);
		Instruction ins  = new Instruction(Inst_types.ASSIGNMNET); 
		

		//	System.out.println(rhs_des.name + " " + lhs_des.name + " " + rhs_des.addrMode + " " + lhs_des.addrMode 
			//		+ " " + rhs_des.address + " " + lhs_des.address);
		
		//TODO az inja be baad taghirat e sakht e instruction dade nashode ast!
		if ( copy_size ==0){
			ins = new Instruction(Inst_types.ASSIGNMNET) ;	
			ins.opr1 = new My_oprand(rhs_des);
		   	ins.opr2 = new My_oprand(lhs_des);
		   	ins_list.add(ins);
		}	
		else{
			assign_copy(rhs_des, lhs_des);
		}
	}

	
	private void assign_copy(Descriptor rhs_des, Descriptor lhs_des) throws Exception{
		
		Instruction ins;
		if(rhs_des.isArray){
			
			Variable_types tmp = rhs_des.type;
			
			ins = new Instruction(Inst_types.ASSIGNMNET);
			rhs_des.type = Variable_types._struct; // inja kalak rashT zadam ke array ro ba kolle sizesh copy konam //
			lhs_des.type = Variable_types._struct;
			ins.opr1 = new My_oprand(rhs_des);
			ins.opr2 = new My_oprand(lhs_des);
			ins_list.add(ins);
			
			rhs_des.type = tmp;
			lhs_des.type = tmp;
				
		}
			
		else if(rhs_des.type == Variable_types._struct){
			ins = new Instruction(Inst_types.ASSIGNMNET);
			ins.opr1 = new My_oprand(rhs_des);
			ins.opr2 = new My_oprand(lhs_des);
			ins_list.add(ins);
			
		}

	}
	

	private void writeInt() throws Exception {
		Descriptor desc = seman_stack.pop();
		if(desc.aux == true)
			desc = stStack.getDescriptor(desc.name);
		
		if(desc.type != Variable_types._int)
			gen_error("the expression is not of int type to be printed as int it of type " + desc.type);
		Instruction ins ; 
		ins = new Instruction(Inst_types.WRITE_INTERGER);
		ins.opr1 = new My_oprand(desc);
		ins_list.add(ins);
	}
	private void writeFloat() throws Exception {
		Descriptor desc = seman_stack.pop();
		if(desc.aux == true)
			desc = stStack.getDescriptor(desc.name);
		if(desc.type != Variable_types._float)
			gen_error("the expression is not of float type to be printed as float");
		Instruction ins ; 
		ins = new Instruction(Inst_types.WRITE_FLOAT);
		ins.opr1 = new My_oprand(desc);
		ins_list.add(ins);
	}
	
    /////////////////////////////////////////////////array///////////////////////////////////////////
	private void array_begin(){
		decl_dim.clear();  
    }
	private void array_dim() throws Exception{
		int dim= Integer.parseInt(Scanner.CV);  
    	if (  dim <=0 ) {
    		gen_error("Array dimenson should be a positive integer");
    	}
    	decl_dim.add(dim);
    }
    

 //TODO(done) here I have supposed that name and type are always aux : check if this is true!
	/**
	 * here for array A a[] in which A is a struct we have saved A's vars in a!!
	 * @return
	 * @throws Exception
	 */
	private String array_decl() throws Exception{
		Descriptor new_desc = new Descriptor(seman_stack.pop().name,false);
		String type = seman_stack.pop().name;
		int arr_size = 1;
		int [] dims = new int[decl_dim.size()];
		for (int i = 0; i < decl_dim.size(); i++) {
			dims[i] = decl_dim.get(i);
			arr_size *= dims[i];
		}
		int size = allTypes.getSize(type) * arr_size ;
		int address = set_add_decl(size);
		
		new_desc.size = size;
		new_desc.address = address;
		new_desc.type = Descriptor.String_to_VarType(type);
		new_desc.isArray = true;
		new_desc.arr_dims = dims;
		
		if(stStack.peek().scopeName .equals("global")) //TODO(done) check if the names of sym tables are correct("Global")
			new_desc.addrMode = AddressingMode.Global_direct;
		else new_desc.addrMode = AddressingMode.Local_direct;
		
		if(new_desc.type == Variable_types._struct){
			int ana;
			/**here I create a list of struct variables for each cell of the array*/
			/*
			new_desc.array_struct_vars = new SymbolTable[arr_size];
			for (int i = 0; i < arr_size; i++) {
				new_desc.array_struct_vars[i] = allTypes.find_desc(type).struct_vars.get_copy(new_desc.address);
			}*/
			new_desc.struct_vars = allTypes.find_desc(type).struct_vars;//.get_copy(address);
			new_desc.structType = type;
		}
		
		stStack.peek().add(new_desc.name, new_desc); 
		return type ; 
	}
	
	private void array_decl_camma() throws Exception {
    	String type = array_decl();
    	Descriptor desc = new Descriptor(type, false);
    	seman_stack.push(desc) ; 
	}

	private void decl_camma() throws Exception {
		String type = decl();
    	Descriptor desc = new Descriptor(type, false);
    	seman_stack.push(desc) ; 
	}
	
	 
////////////////////////////////////////////////array use ////////////////////////////////////////////
	private void check_arr_dim() throws Exception {
	}
	
	
	private int dim_use;
	Descriptor loc_arr_in_use;
	
	private void loc_arr_begin() throws Exception {
		//System.out.println("//loc_arr_beg----------------------------------------------------------------");
		Descriptor arr_name_des = seman_stack.peek();
		//System.out.println("location arr shuru shod va esm e arr: " + arr_name_des.name);
		if(arr_name_des.aux == true){
			arr_name_des = stStack.getDescriptor(arr_name_des.name);
			seman_stack.pop();
			seman_stack.push(arr_name_des);
		}
		//System.out.println(arr_name_des.name);
		if(arr_name_des.isArray == false)
			gen_error(String.format("the variable %s is not of type array and cannot be indexed", arr_name_des.name));
		loc_arr_in_use = arr_name_des;
		/////////////////
		Descriptor temp_ind_addr = new Descriptor(get_tmpName(), false);
		temp_ind_addr.type = Variable_types._int;
		temp_ind_addr.size = 4;
		temp_ind_addr.addrMode = AddressingMode.Local_direct;
		temp_ind_addr.address = local_rel_add(4);
		
		//////////////////
		Instruction ins = new Instruction(Inst_types.ASSIGNMNET);
		Descriptor zero_num = new Descriptor("_zero", false);
		zero_num.addrMode = AddressingMode.Immidiate;
		zero_num.type = Variable_types._int;
		zero_num.value = 0 + "";
		ins.opr1 = new My_oprand(zero_num);
    	ins.opr2 = new My_oprand(temp_ind_addr); 
    	ins_list.add(ins); 	
		/////////////////
		dim_use = 0;
		seman_stack.push(temp_ind_addr);
    	
	}
	
    private void detect_index_out_bound(Descriptor passed_dim, int dim_limit) throws Exception{
    	Descriptor band_desc = Descriptor.getImmidiateDescriptor(dim_limit);
    	//Descriptort = getImmidiateDescriptor(upperBound);
    	Descriptor bool = new Descriptor();
    	bool.type = Variable_types._boolean;
    	bool.address = local_rel_add(1);
    	bool.addrMode = AddressingMode.Local_direct;
    	make_three_opr_inst(passed_dim, band_desc, Inst_types.LESS_THAN, bool);
    	make_two_opr_ins(bool, Inst_types.JUMP_ZERO, Descriptor.getImmidiateDescriptor(1));
    	////////////////////////////////////////////////////////////////////////////////////////
    	bool = new Descriptor();
    	bool.type = Variable_types._boolean;
    	bool.address = local_rel_add(1);
    	bool.addrMode = AddressingMode.Local_direct;
    	make_three_opr_inst(passed_dim, Descriptor.getImmidiateDescriptor(0), Inst_types.GREATER_THAN_EQUAL,bool );
    	make_two_opr_ins(bool,  Inst_types.JUMP_ZERO, Descriptor.getImmidiateDescriptor(1));
    }
	private void loc_arr_dim() throws Exception {
		
		Descriptor dim_des = seman_stack.pop();
		if(dim_des.aux == true){
			try {
				dim_des = stStack.getDescriptor(dim_des.name);
			} catch (Exception e) {
				gen_error("This variable is not defined before...");
			}
		}
		//System.out.println("varede dim shod va dim: " + dim_des.value);
		
		if(dim_des.type != Variable_types._int)
			gen_error("Indexing in array should use an integer value");
		
		if(dim_des.addrMode == AddressingMode.Immidiate){//TODO index out of bound in runtime!!!
			if(Integer.parseInt(dim_des.value) <0 || Integer.parseInt(dim_des.value) >= loc_arr_in_use.arr_dims[dim_use])
				gen_error(String.format("index out of bound error for array %s and index %d",loc_arr_in_use.name,Integer.parseInt(dim_des.value)));
		}
		else
			detect_index_out_bound(dim_des, loc_arr_in_use.arr_dims[dim_use]);
		Descriptor temp_ind_addr = seman_stack.pop();
		int rest_dim_mul = loc_arr_in_use.mul_amnt_in_arrDcl(dim_use);
	
		//System.out.println("dar haman dim va natije zarb baghie: " + rest_dim_mul);
		Descriptor rest_dim_mul_des = Descriptor.getImmidiateDescriptor(rest_dim_mul);
		

		Descriptor mult_temp_des = new Descriptor(get_tmpName(), false);
		mult_temp_des.type = Variable_types._int;
		mult_temp_des.size = 4;
		mult_temp_des.addrMode = AddressingMode.Local_direct;
		mult_temp_des.address = local_rel_add(4);
		

		make_three_opr_inst(dim_des,rest_dim_mul_des, Inst_types.MULTIPLY, mult_temp_des);
		make_three_opr_inst(temp_ind_addr, mult_temp_des, Inst_types.ADD, temp_ind_addr);
		dim_use++ ; 
		seman_stack.push(temp_ind_addr);
	}


	private void loc_arr_end() throws Exception {
		//System.out.println(loc_arr_in_use.name);
		//System.out.println("in loc arr end----------------------------------------------------------");
		Descriptor temp_ind_addr = seman_stack.pop();
		if(loc_arr_in_use.arr_dims.length != dim_use)
			gen_error(String.format("Array %s number of dimensions doesn't match entered dimensions", loc_arr_in_use.name));
		
		int el_size = loc_arr_in_use.get_elm_size();
	
		Descriptor size_des = Descriptor.getImmidiateDescriptor(el_size);
		
		Descriptor start_adr_des;
		Variable_types tmp = loc_arr_in_use.type;// inja ham kalak e rashT savar shode ast va khode in desc ra agar indirect bud int mikonim
		AddressingMode tmp2 = loc_arr_in_use.addrMode;
		
		if(loc_arr_in_use.addrMode == AddressingMode.Global_direct || loc_arr_in_use.addrMode == AddressingMode.Local_direct)
			start_adr_des = Descriptor.getImmidiateDescriptor(loc_arr_in_use.address);
		else{
			loc_arr_in_use.addrMode = AddressingMode.Local_direct;
			loc_arr_in_use.isArray = false;
			loc_arr_in_use.type = Variable_types._int;
			start_adr_des = loc_arr_in_use;
		}
	
		make_three_opr_inst(temp_ind_addr, size_des, Inst_types.MULTIPLY, temp_ind_addr);
		
		
		/////////////////ta inja fasele az start mohasebe shod///////////////////
		Descriptor final_addr = new Descriptor(get_tmpName(), false);
		final_addr.size = 4;
		final_addr.address = local_rel_add(4);
		final_addr.type = tmp;
		final_addr.structType = loc_arr_in_use.structType;
		final_addr.addrMode = AddressingMode.Local_direct;
		final_addr.struct_vars = loc_arr_in_use.struct_vars;
		int kasra;
		//final_addr.pointTo = loc_arr_in_use;
		
		
		make_three_opr_inst(temp_ind_addr, start_adr_des, Inst_types.ADD, final_addr);
		

		//eslahie
		loc_arr_in_use.isArray = true;
		loc_arr_in_use.type = tmp;
		loc_arr_in_use.addrMode = tmp2;
		
		if(loc_arr_in_use.addrMode == AddressingMode.Global_direct || loc_arr_in_use.addrMode == AddressingMode.Global_indirect)
			final_addr.addrMode = AddressingMode.Global_indirect;
		else if(loc_arr_in_use.addrMode == AddressingMode.Local_direct || loc_arr_in_use.addrMode == AddressingMode.Local_indirect)
			final_addr.addrMode = AddressingMode.Local_indirect;
		
		seman_stack.pop();
		seman_stack.push(final_addr);//TODO dge tuye symbol table nazashtamesh!!
	}
	
    

	private void read_float() throws Exception {
		Descriptor desc = seman_stack.pop();
		if(desc.type != Variable_types._float)
			gen_error("input argument is not of type float");
		Instruction ins = new Instruction(Inst_types.READ_FLOAT);
		ins.opr1 = new My_oprand(desc);
		ins_list.add(ins);
	}


//TODO check reads!
	private void read_int() throws Exception {
		Descriptor desc = seman_stack.pop();
		if(desc.type != Variable_types._int)
			gen_error("input argument is not of type int");
		Instruction ins = new Instruction(Inst_types.READ_INTEGER);
		ins.opr1 = new My_oprand(desc);
		ins_list.add(ins);
	}
	
	private void writeText() {
		Descriptor txt_des = seman_stack.pop();
		String txt = txt_des.name ;
		Instruction ins = new Instruction(Inst_types.WRITE_TEXT);
		ins.opr1.type = Variable_types._string;
		ins.opr1.value = txt.replace(' ', '$') ;
		ins.opr1.value = ins.opr1.value.replace('\n', '^');
		ins.opr1.addressingMode = AddressingMode.Immidiate;
		ins_list.add(ins);	
	}

	private void make_three_opr_inst(Descriptor d1 , Descriptor d2 , Inst_types type , Descriptor t) throws Exception {
		Instruction ins = new Instruction(type) ; 
		checkTypes_byDesc(ins, d1, d2);
		ins.opr1 = new My_oprand(d1);
		ins.opr2 = new My_oprand(d2);
		ins.opr3 = new My_oprand(t);
		ins_list.add(ins);
	}	

	private void make_desc_three_opr(String type_str) throws Exception{
	Inst_types type = Instruction.str_to_insType(type_str);
	Descriptor d2 = seman_stack.pop();
	if(d2.aux == true){
		d2 = stStack.getDescriptor(d2.name);
	}
	Descriptor d1 = seman_stack.pop();
	if(d1.aux == true){
		d1 = stStack.getDescriptor(d1.name);
	}
	Descriptor tmp_res = new Descriptor(get_tmpName(), false);
	tmp_res.addrMode= AddressingMode.Local_direct;
	if(type.ordinal() <= 4){
		tmp_res.type = d2.type;
		tmp_res.size = d2.size;
	}
	else{
		tmp_res.type = Variable_types._boolean;
		tmp_res.size = 1;
	}
	tmp_res.address = local_rel_add(tmp_res.size);
	stStack.peek().add(tmp_res.name, tmp_res); //TODO check if this is needed
	seman_stack.push(tmp_res);
	make_three_opr_inst(d1, d2, type, tmp_res);
}



	private void make_desc_two_opr(String type_str) throws Exception{
	 	Inst_types type = Inst_types.UNARY_MINUS;
	 	if ( type_str.equals("-"))
			type = Inst_types.UNARY_MINUS ;
	 	else if ( type_str.equals("!"))
			type = Inst_types.LOGICAL_NOT ;	 	
	 	Descriptor d1 = seman_stack.pop();
	 	if(d1.aux == true){
			d1 = stStack.getDescriptor(d1.name);
		}

		Descriptor tmp_res = new Descriptor(get_tmpName(), false);
		tmp_res.addrMode= AddressingMode.Local_direct;
		if(type == Inst_types.LOGICAL_NOT){
			tmp_res.type = Variable_types._boolean;
			tmp_res.size = 1;
		}
		else{
			tmp_res.type = d1.type;
			tmp_res.size = d1.size;
		}
		tmp_res.address = local_rel_add(tmp_res.size);
		stStack.peek().add(tmp_res.name, tmp_res); //TODO check if this is needed
		seman_stack.push(tmp_res);
		make_two_opr_ins(d1,type,tmp_res);
	}
	
	private void make_two_opr_ins(Descriptor d1, Inst_types type, Descriptor t ) throws Exception {
	Instruction ins = new Instruction(type) ;
	check_two_opr(ins, d1, t);
	ins.opr1 = new My_oprand(d1);
	ins.opr2 = new My_oprand(t);
	ins_list.add(ins);	
}

    //////////////////////////////////////////////////////////////////////////////////////////////
	private void check_two_opr(Instruction ins, Descriptor d1, Descriptor d2) throws Exception{
		if(ins.type == Inst_types.JUMP_ZERO && d1.type != Variable_types._boolean)
			gen_error("a jump zero instruction with not boolean condition");
		else
			return;
		if(d1.isArray || d2.isArray || (d1.type != d2.type))
			gen_error("type mismatch");
		
		if ( ins.type == Inst_types.LOGICAL_NOT){
			if ( d1.type != Variable_types._boolean )
				gen_error( "Type mismatch (should be boolean) " + d1.type) ;
		}
		else if ( ins.type == Inst_types.UNARY_MINUS){
			if(d1.type != Variable_types._int && d1.type != Variable_types._float)
				gen_error("unary minus can operate on int or float not " + d1.type);
		}	
	}

	private void checkTypes_byDesc(Instruction ins, Descriptor d1, Descriptor d2) throws Exception{
		
		if(d1.type != d2.type)
			gen_error("type mismatch type1 = " + d1.type + " type 2 =  " + d2.type);
    	if(ins.isComparison()) {
    		if(d1.isArray || d2.isArray)
    			gen_error("cannot campare two array operand");
    		else if(d1.type != Variable_types._int && d1.type != Variable_types._float)
    			gen_error("can only campare operands of type int and float with each other");

    	}
    	if ( ins.isEquality()) {
    		if(d1.isArray || d2.isArray)
    			gen_error("operands should bot be array");
    	}
    	if(ins.isLogical()) {

    		if(d1.isArray || d2.isArray)
    			gen_error("operands should bot be array");
    	}
    	if ( ins.isOperation()){

    		if(d1.isArray || d2.isArray)
    			gen_error("in operation operands should bot be array");
    		if(d1.type != Variable_types._int && d1.type != Variable_types._float)
	    		gen_error("in operation operands should be int or float");
    	} 
	}
	
	private int get_copy_size(Descriptor d1) {
		if(d1.isArray)
			return d1.size;
		else if ( d1.type == Variable_types._struct  ){
			return d1.size; 
		}
		return 0 ; 
				
	}

	//-----------------------------------------------------------------------------------------------------------------------

	private void grt() throws Exception{
		make_desc_three_opr(">");
		return;
		
	}

	private void le() throws Exception{
		make_desc_three_opr("<=");
		return;
		
	}

	private void less() throws Exception{
		make_desc_three_opr("<");
		return;
		
	}

	private void ge() throws Exception{
		make_desc_three_opr(">=");
		return;
		
	}

	private void add() throws Exception{
		make_desc_three_opr("+");
		return;
		
	}

	private void minus() throws Exception{
		make_desc_three_opr("-");
		return;
		
	}

	private void mod() throws Exception{
		make_desc_three_opr("%");
		return;
		
	}

	private void div() throws Exception{
		make_desc_three_opr("/");
		return;
		
	}

	private void mult() throws Exception{
		make_desc_three_opr("*");
		return;
		
	}
	private void not()throws Exception {
		make_desc_two_opr("!");
		return;
		
	}

	private void unary_minus() throws Exception{
		make_desc_two_opr("-");
		return;
		
	}
	
	private void notEqual() throws Exception{
		make_desc_three_opr("!=");
		 return;
		
	}

	private void equal() throws Exception{
		make_desc_three_opr("==");
		return;
	}

	private void and() throws Exception{
		make_desc_three_opr("&&");
		return;
	}


	private void or() throws Exception{
		make_desc_three_opr("||");
		
	}

    public void WriteOutput(String filename) throws Exception 
    {
    	
    	FileWriter fstream = new FileWriter(filename);
    	BufferedWriter out = new BufferedWriter(fstream);
    	 //out.write("sp:= im_i_" + curr_local_adr + "\n");
    	for (int i = 0; i < ins_list.size(); i++) {
    		//System.out.println( ins_list.get(i).toString());
    		out.write( ins_list.get(i).toString());
			
    	}
    	out.close();
    	VM v = new VM(filename);
    	v.execute();
    	
    }

    //TODO check if main does not exists!
	public void FinishCode() {
		
		do_nothing_ins(null);
		Instruction ins  = ins_list.get(0);
		//ins.opr1.value = curr_local_adr + "";
		//	System.out.println(ins);
		ins = ins_list.get(0);
		ins.opr1.value = main_adr + "";
		ins = ins_list.get(2);
		ins.opr1.value = ins_list.size() - 1 + "";
		ins = ins_list.get(4);
		ins.opr1.value = ins_list.size() - 1 + "";
		
			
			
	}
		


    private String get_tmpName(){
    	return "_tmp" + (tempCount++) ; 
    }
    
    //sp set aval ra bardashtam va hamchenin dar main ham comment kardam
    private void make_init_commands() throws Exception{
    	
    	//sp
    	Descriptor sp = new Descriptor();
    	sp.address = -1;
    	sp.addrMode = AddressingMode.Immidiate;
    	sp.type = Variable_types._int;
    	//make_one_opr_ins(sp, Inst_types.ASSIGN_SP);
    	
    	//jmp to main
    	Descriptor main_adr = new Descriptor();
    	main_adr.address = -1;
    	main_adr.addrMode = AddressingMode.Immidiate;
    	main_adr.type = Variable_types._int;
    	make_one_opr_ins(main_adr, Inst_types.JUMP);
    	
    	//array index out of bound
    	Descriptor txt = new Descriptor();
    	txt.type = Variable_types._string;
    	txt.addrMode = AddressingMode.Immidiate;
    	txt.value = "Array$index$out$of$bound";
    	make_one_opr_ins(txt, Inst_types.WRITE_TEXT);
    	//jmp to end
    	Descriptor end = new Descriptor();
    	end.address = -1;
    	end.addrMode = AddressingMode.Immidiate;
    	end.type= Variable_types._int;
    	make_one_opr_ins(end, Inst_types.JUMP);
    	//////////////////////////////////////////////////////
    	//control out of function
    	Descriptor txt2 = new Descriptor();
    	txt2.type = Variable_types._string;
    	txt2.addrMode = AddressingMode.Immidiate;
    	txt2.value = "control$has$fallen$out$of$function";
    	make_one_opr_ins(txt2, Inst_types.WRITE_TEXT);
    	//jmp to end

    	Descriptor end2 = new Descriptor();
    	end2.address = -1;
    	end2.addrMode = AddressingMode.Immidiate;
    	end2.type= Variable_types._int;
    	make_one_opr_ins(end2, Inst_types.JUMP);
    	
    	
    	
    }
    
	private boolean dim_Check(Descriptor d1, Descriptor d2){
		if ( d1.arr_dims.length != d2.arr_dims.length)
			return false; 
		for (int i = 0; i < d1.arr_dims.length; i++) 
			if ( d1.arr_dims[i] != d2.arr_dims[i])
				return false ; 
		return true ;
	}

	public void checkAssignment(Descriptor d1, Descriptor d2) throws Exception {
	//	System.out.println(d1.name + " " + d2.name + d1.structType + " " + d2.structType);
		if ( d1.isArray != d2.isArray)
			gen_error("cannot copy an array to a non array element") ;
		if ( d1.type != d2.type){
			gen_error(" type mismatch for assingment " + "type1 = " + d1.type+ " type2 " + d2.type);
		}
		if ( d1.type == Variable_types._struct){
			if ( !d1.structType.equals(d2.structType)){
				gen_error("cannot assign a var of type "+ d1.type + " to a var of type "+ d2.type);
			}
		}
		if ( d1.isArray && !dim_Check(d1, d2))
			gen_error("dimension of arrays should be the same!!!"); 
	}
}
