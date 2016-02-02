package main;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;




public class VM {

	CodeScanner reader;
	Scanner system;
	byte[] mem = new byte[100000000];
	int stackPointer = 1000;
/*
	public static void main(String[] args){
		if (args.length != 1)
			System.err.println("usage: VM file");
		else{
			try{
				VM vm = new VM(args[0]);
				vm.execute();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
*/
	public VM(String fileName) throws Exception{
		reader = new CodeScanner(fileName);
		system = new Scanner(System.in);
	}

	public void execute() throws Exception{
		String operand1,operand2,operand3;
		OperandType type;
		Opcode opcode;
		while (reader.hasNext()){
			String s = reader.next();
			opcode = opcodeof(s);
			//System.out.println(s);
			switch (opcode){
			case PLUS:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intOperand(operand1)+intOperand(operand2)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatOperand(operand1)+floatOperand(operand2)).array());
					break;
				default:
					break;	
				}
				break;
			case MINUS:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intOperand(operand1)-intOperand(operand2)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatOperand(operand1)-floatOperand(operand2)).array());
					break;
				default:
					break;	
				}
				break;
			case MUL:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intOperand(operand1)*intOperand(operand2)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatOperand(operand1)*floatOperand(operand2)).array());
					break;
				default:
					break;	
				}
				break;
			case DIV:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intOperand(operand1)/intOperand(operand2)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatOperand(operand1)/floatOperand(operand2)).array());
					break;
				default:
					break;	
				}
				break;
			case MOD:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intOperand(operand1)%intOperand(operand2)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatOperand(operand1)%floatOperand(operand2)).array());
					break;
				default:
					break;	
				}
				break;
			case AND:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case BOOL:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((boolOperand(operand1)&&boolOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;	
				}				
				break;
			case OR:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case BOOL:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((boolOperand(operand1)||boolOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;	
				}				
				break;
			case L:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((intOperand(operand1)<intOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((floatOperand(operand1)<floatOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;	
				}
				break;
			case G:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((intOperand(operand1)>intOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((floatOperand(operand1)>floatOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;	
				}
				break;
			case LE:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
		
					if(intOperand(operand1)<=intOperand(operand2)){
					}
					//System.out.println(intOperand(operand1) +  " "+ intOperand(operand2) + addressOperand(operand3)) ;
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((intOperand(operand1)<=intOperand(operand2)) ? (byte)1 : (byte)0).array());
		
					
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((floatOperand(operand1)<=floatOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;	
				}
				break;
			case GE:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((intOperand(operand1)>=intOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((floatOperand(operand1)>=floatOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;	
				}
				break;
			case E:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((intOperand(operand1)==intOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((floatOperand(operand1)==floatOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;	
				case BOOL:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((boolOperand(operand1)==boolOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;
				}
				break;
			case NE:
				operand1 = reader.next();
				operand2 = reader.next();
				operand3 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((intOperand(operand1)!=intOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((floatOperand(operand1)!=floatOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;	
				case BOOL:
					writetomem(addressOperand(operand3), 1,
							ByteBuffer.allocate(1).put((boolOperand(operand1)!=boolOperand(operand2)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;
				}
				break;
			case NOT:
				operand1 = reader.next();
				operand2 = reader.next();
				type = typeof(operand1);
				switch (type){
				case BOOL:
					writetomem(addressOperand(operand2), 1,
							ByteBuffer.allocate(1).put((!boolOperand(operand1)) ? (byte)1 : (byte)0).array());
					break;
				default:
					break;
				}
				break;
			case UM:
				operand1 = reader.next();
				operand2 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand2), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(-intOperand(operand1)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand2), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(-floatOperand(operand1)).array());
					break;
				default:
					break;	
				}
				break;
			case ASSIGN:
				operand1 = reader.next();
				operand2 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					//System.out.println("ana in vm assign" + operand1);
					writetomem(addressOperand(operand2), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intOperand(operand1)).array());
					break;
				case FLOAT:
					writetomem(addressOperand(operand2), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatOperand(operand1)).array());
					break;	
				case BOOL:
					writetomem(addressOperand(operand2), 1,
							ByteBuffer.allocate(1).put(boolOperand(operand1) ? (byte)1 : (byte)0).array());
					break;	
				case CHAR:
					writetomem(addressOperand(operand2), 1,
							ByteBuffer.allocate(1).put(charOperand(operand1).getBytes()[0]).array());
					break;	
				case STRING:
					writetomem(addressOperand(operand2), 100,
							ByteBuffer.allocate(100).put(stringOperand(operand1).getBytes()).array());
					break;
				case FREE:
					byte[] bytes = readfrommem(specialAddressOperand(operand1), Integer.parseInt(operand1.split("_")[1]));
					writetomem(specialAddressOperand(operand2), bytes.length,
							bytes);
					break;	
				}
				break;
			case WI:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					//System.out.println(addressOperand(operand1));
					System.out.print(intOperand(operand1) + " ");
					break;
				default:
					break;
				}
				break;
			case WF:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case FLOAT:
					System.out.print(floatOperand(operand1) + " ");
					break;
				default:
					break;
				}
				break;
			case WT:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case CHAR:
					System.out.print(charOperand(operand1));
					break;
				case STRING:
					System.out.print(stringOperand(operand1));
					break;
				default:
					break;
				}
				break;
			case RI:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand1), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(system.nextInt()).array());
					break;
				default:
					break;
				}
				break;
			case RF:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case FLOAT:
					writetomem(addressOperand(operand1), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(system.nextFloat()).array());
					break;
				default:
					break;
				}
				break;
			case SP:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					writetomem(addressOperand(operand1), 4,
							ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(stackPointer).array());
					break;
				default:
					break;
				}
				break;
			case ASP:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					//System.out.println("ana in JVM ASP");
					stackPointer = intOperand(operand1);
					//System.out.println(stackPointer);
					break;
				default:
					break;
				}
				break;
			
			case ISP:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					stackPointer += intOperand(operand1);
					break;
				default:
					break;
				}
				break;
			case DSP:
				operand1 = reader.next();
				type = typeof(operand1);
				switch (type){
				case INT:
					stackPointer -= intOperand(operand1);
					break;
				default:
					break;
				}
				break;
			case JMP:
				operand1 = reader.next();
				type = typeof(operand1);
				reader.jump(intOperand(operand1));
				break;
			case JZ:
				operand1 = reader.next();
				operand2 = reader.next();
				type = typeof(operand1);
				switch(type){
				case INT:
				
					if(intOperand(operand1) == 0){
						reader.jump(intOperand(operand2));
					}
					break;
				case BOOL:
					if(boolOperand(operand1) == false)
						reader.jump(intOperand(operand2));
					break;
				default:
					break;
				}
			default:
				break;		
			}
		}
		reader.close();
	}


	void err(String e) throws Exception{
		throw new Exception(e);
	}

	int specialAddressOperand(String operand){
		int addressOfMem = Integer.parseInt(operand.split("_")[2]);
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		return addressOfMem;
	}
	
	int addressOperand(String operand){
		int addressOfMem = Integer.parseInt(operand.substring(5));
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		//System.out.println("ana in vm " + "operand : " + operand + " address " + addressOfMem);
		return addressOfMem;
	}

	int intOperand(String operand){
		if (operand.charAt(0) == 'i')
			return (int)Integer.parseInt(operand.substring(5));
		int addressOfMem = Integer.parseInt(operand.substring(5));
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		return ByteBuffer.wrap(readfrommem(addressOfMem, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
	}

	float floatOperand(String operand){
		if (operand.charAt(0) == 'i')
			return (float)Float.parseFloat(operand.substring(5));
		int addressOfMem = Integer.parseInt(operand.substring(5));
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		return ByteBuffer.wrap(readfrommem(addressOfMem, 4)).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get();
	}

	boolean boolOperand(String operand){
		if (operand.charAt(0) == 'i')
			return (boolean)Boolean.parseBoolean(operand.substring(5));
		int addressOfMem = Integer.parseInt(operand.substring(5));
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		return readfrommem(addressOfMem, 1)[0]!=0;
	}
	
	String skip(String string){
		String ret = "";
		for (int i=0;i<string.length();i++){
			char c = string.charAt(i);
			if (c == '\\'){
				i++;
				c = string.charAt(i);
				if (c=='n'){
					ret+='\n';
				}
				else if (c=='t')
					ret+='\t';
				else
					ret+=c;
			}else if (c == '_'){
					i++;
					c = string.charAt(i);
					if (c=='-')
						ret+='_';
					else 
						ret+=' ';
			}else
				ret+=c;
		}
		return ret;
		
	}
	
	String charOperand(String operand){
		if (operand.charAt(0) == 'i')
			return skip(operand.substring(5));
		int addressOfMem = Integer.parseInt(operand.substring(5));
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		return new String(readfrommem(addressOfMem, 1));
	}
	
	String stringOperand(String operand){
		if (operand.charAt(0) == 'i'){
			String s = skip(operand.substring(5));
			s = s.replace('$', ' ');
			s = s.replace('^', '\n');
			//return skip(operand.substring(5));
			return s + "";
		}
		int addressOfMem = Integer.parseInt(operand.substring(5));
		if (operand.charAt(1) == 'i')
			addressOfMem = ByteBuffer.wrap(readfrommem(addressOfMem+stackPointer, 4)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
		if (operand.charAt(0) ==  'l')
			addressOfMem += stackPointer;
		return new String(readfrommem(addressOfMem, 100));
	}

	byte[] readfrommem(int index, int size){
		//System.out.println("ana in vm read mem " + index);
		byte[] ret = new byte[size];
		for (int i=0;i<size;i++)
			ret[i]=mem[index+i];
		return ret;
	}
	void writetomem(int index, int size, byte[] buffer){
		//System.out.println();
			//System.out.println(" ana in vm addresses of memory " + index + " " + size) ;
		for (int i=0;i<size;i++){
			mem[index+i]=buffer[i];
			//if(index < 1000)
			//System.out.println(buffer[i]);
		}
	}

	Opcode opcodeof(String opcode){
		if (opcode.equals("+"))
			return Opcode.PLUS;
		if (opcode.equals("-"))
			return Opcode.MINUS;
		if (opcode.equals("*"))
			return Opcode.MUL;
		if (opcode.equals("/"))
			return Opcode.DIV;
		if (opcode.equals("%"))
			return Opcode.MOD;
		if (opcode.equals("&&"))
			return Opcode.AND;
		if (opcode.equals("||"))
			return Opcode.OR;
		if (opcode.equals("<"))
			return Opcode.L;
		if (opcode.equals(">"))
			return Opcode.G;
		if (opcode.equals("<="))
			return Opcode.LE;
		if (opcode.equals(">="))
			return Opcode.GE;
		if (opcode.equals("=="))
			return Opcode.E;
		if (opcode.equals("!="))
			return Opcode.NE;
		if (opcode.equals("!"))
			return Opcode.NOT;
		if (opcode.equals("u-"))
			return Opcode.UM;
		if (opcode.equals(":="))
			return Opcode.ASSIGN;
		if (opcode.equals("wi"))
			return Opcode.WI;
		if (opcode.equals("wf"))
			return Opcode.WF;
		if (opcode.equals("wt"))
			return Opcode.WT;
		if (opcode.equals("ri"))
			return Opcode.RI;
		if (opcode.equals("rf"))
			return Opcode.RF;
		if (opcode.equals(":=sp"))
			return Opcode.SP;
		if (opcode.equals("sp:="))
			return Opcode.ASP;
		if (opcode.equals("+sp"))
			return Opcode.ISP;
		if (opcode.equals("-sp"))
			return Opcode.DSP;
		if(opcode.equals("jmp"))
			return Opcode.JMP;
		if(opcode.equals("jz"))
			return Opcode.JZ;
		return null;
	}

	OperandType typeof(String operand){
		if (operand.charAt(3) == 'i')
			return OperandType.INT;
		if (operand.charAt(3) == 'f')
			return OperandType.FLOAT;
		if (operand.charAt(3) == 'b')
			return OperandType.BOOL;
		if (operand.charAt(3) == 'c')
			return OperandType.CHAR;
		if (operand.charAt(3) == 's')
			return OperandType.STRING;
		return OperandType.FREE;
	}

}
