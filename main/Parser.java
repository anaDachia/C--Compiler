package main;


import java.util.Stack;



//import main.Descriptor.VarType;

public class Parser 
{
	Scanner scanner;
	CodeGenerator cg;
	PTBlock[][] parseTable;
	Stack<Integer> parseStack = new Stack<Integer>();
	String[] symbols;
	SymbolTable symbolTable ;
	SymbolTable var_types ; 
	Stack<SymbolTable> symbolStack ; 

	public Parser(String inputFile, String[] symbols, PTBlock[][] parseTable)
	{
		try
		{
			this.parseTable = parseTable;
			this.symbols = symbols;
	
			scanner = new Scanner(inputFile);
			cg = new CodeGenerator(scanner);
			createTypeTable() ; 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createTypeTable() throws Exception {
		var_types = new SymbolTable("TypeTable") ; 
		String [] types = {"string"  , "int" , "float" , "boolean" , "char", "void" } ; 
		int [] sizes = { 100 , 4 , 4 , 1 , 1, 0 } ; 
		for (int i = 0; i < sizes.length; i++) {
			Descriptor d = new Descriptor(Variable_types.valueOf("_" + types[i]) , sizes[i]) ; 
			var_types.add(types[i], d) ; 
			cg.allTypes = this.var_types ; 
		}
	}

	public int LineNumber()
	{
		return scanner.line; // Or any other name you used in your Scanner
	}

	public void Parse()
	{
		try
		{
			int tokenId = nextTokenID();
			
			int curNode = 0;
			boolean notAccepted = true;
			while (notAccepted)
			{
				String token = symbols[tokenId];
	            PTBlock ptb = parseTable[curNode][tokenId];
				switch (ptb.getAct())
				{
	                case PTBlock.ActionType.Error:
	                    {
		                        throw new Exception(String.format("Compile Error (" + token + ") at line " + scanner.line
		                        		+ " @ " + curNode));
	                    }
					case PTBlock.ActionType.Shift:
						{
							cg.Generate(ptb.getSem());
							tokenId = nextTokenID();
							curNode = ptb.getIndex();
						}
						break;
	
					case PTBlock.ActionType.PushGoto:
						{
							parseStack.push(curNode);
							curNode = ptb.getIndex();
						}
						break;
	
					case PTBlock.ActionType.Reduce:
						{
							if (parseStack.size() == 0)
	                        {
		                        throw new Exception(String.format("Compile Error (" + token + ") at line " + scanner.line + " @ " + curNode));
	                        }
	
							curNode = parseStack.pop();
							ptb = parseTable[curNode][ptb.getIndex()];
							cg.Generate(ptb.getSem());
							curNode = ptb.getIndex();
						}
						break;
	
					case PTBlock.ActionType.Accept:
						{
							notAccepted = false;
						}
						break;
						
				}
	        }
	        cg.FinishCode();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	int nextTokenID()
	{
		String t = "";
		try
		{
			Token tok = scanner.NextToken();
			t = tok.kind;
		//	t = scanner.NextToken();
			//System.out.printf("Ana t in pars  %s %s \n", t, scanner.CV);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		int i;
		
		for (i = 0; i < symbols.length; i++)
			if (symbols[i].equals(t))
				return i;
		(new Exception("Undefined token: " + t)).printStackTrace();
		return 0;
	}
	
	public void WriteOutput(String outputFile) throws Exception
	{
        	cg.WriteOutput(outputFile);
	}
}


