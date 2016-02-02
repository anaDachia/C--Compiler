package main;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;


class Token{
	String kind = "";
	int line;
	int col;
	int val;
	String str = "";
}

public class Scanner {
	
	
	//keywords:
	static final int base = 30;
	static final String[] keywords =new String[] {"boolean", "break", "continue", "else", "false",
					 "float", "for", "if", "int", "readfloat", "readint", "return", "string",
					"struct", "true", "void","while" ,"writefloat", "writeint", "writetext"}; 
	
	/////////////////////////////////////////////
	private String filename;
	
	
	private File sourceFile;
	static public String CV;
	static private FileInputStream in;
	static int line,col;
	static char ch;
	static final int eofCh = '\u0080';//returned char at end of file

// public methods////////////////////////////////
	public Scanner(String filename) throws Exception {
		super();
		this.filename = filename;
		init();
	}
	
	public Token NextToken() throws Exception{
		while(Character.isWhitespace(ch)) nextCh(); //blanks. tabs,eols ???????
		Token t = new Token(); 
		t.line= line;
		t.col = col;
		
		char lastchar = ch;
		CV = "" + ch;
		String invaliderror = String.format("invalid character at line %d, col %d", line, col);
		switch(ch){
			//////id or keyword
			case 'a':case 'b':case 'c':case 'd':case 'e':case 'f':
			case 'g':case 'h':case 'i':case 'j':case 'k':case 'l':
			case 'm':case 'n':case 'o':case 'p':case 'q':case 'r':
			case 's':case 't':case 'u':case 'v':case 'w':case 'x':
			case 'y':case 'z': case'_':
				
			case 'A':case 'B':case 'C':case 'D':case 'E':case 'F':
			case 'G':case 'H':case 'I':case 'J':case 'K':case 'L':
			case 'M':case 'N':case 'O':case 'P':case 'Q':case 'R':
			case 'S':case 'T':case 'U':case 'V':case 'W':case 'X':
			case 'Y':case 'Z':
				readName(t);
				break;
				
			///////number///////////////////////////////////////////////
			case '0':case '1':case '2':case '3':case '4':case '5':
			case '6':case '7':case '8':case '9':
				
					return (readNumber(t));
				
			////////dot//////////////////////////////////////////////////
			case '.': nextCh();
				CV = ".";
				while (Character.isDigit(ch)) {
					CV += ch;
					nextCh();
				}
				if (CV.equals(".")){
					t.kind = ".";
					t.str = ".";
					return t;
				}
				t.kind = "float_literal";
				t.str = CV;
				return t;
			//t.kind= dot;break;
			///////string///////////////////////////////////////////////
			case '\"':
					return readString(t);
			//////char //////////////////////////////////////////////////
			case '\'':
			
					return readCharCon(t);
			//////one state operator////////////////////////////////////	
			case '-':nextCh();t.kind= "-"; return t;
			case '+':nextCh();t.kind = "+"; return t;
			case '*':nextCh();t.kind = "*";return t;
			case '%':nextCh();t.kind= "%"; return t;
			case ';':nextCh();t.kind=  ";"; return t;
			case ',':nextCh();t.kind=  ","; return t;
			
			case '[':nextCh();t.kind = "["; return t;
			case ']':nextCh();t.kind = "]";return t;
			
			case '(':nextCh();t.kind = "("; return t;
			case ')':nextCh();t.kind = ")";return t;
			case '{':nextCh();t.kind = "{";return t;
			case '}':nextCh();t.kind = "}";return t;
			case eofCh: t.kind = "$"; return t;
			
			//////multiple state operator////////////////////////////////
			case '=':nextCh();
				if(ch == '='){
					nextCh(); t.kind = "==";					
				}
				else
					t.kind  = "=";
				return t;
			////////////////////	
			case '<':nextCh();
				if(ch == '='){
					nextCh(); t.kind = "<=";
				}
				else
					t.kind = "<";
				return t;
			/////////////////////
			case '>':nextCh();
				if(ch == '='){
					nextCh();t.kind = ">=";
				}
				else
					t.kind = ">";
				return t;
			/////////////////////
			case '!':nextCh();
				if(ch == '='){
					nextCh();t.kind = "!=";
				}
				else
					t.kind = "!";
				return t;
			/////////////////////
			case '&': nextCh();
				if(ch == '&'){
					nextCh(); t.kind = "&&";
				}
				else
					t.kind = "&";
				return t;
					//throw new Exception(invaliderror);
			/////////////////////
			case '|': nextCh();
			if(ch == '|'){
				nextCh(); t.kind = "||"; return t;
			}
			else{
				t.kind = "|";
				//throw new Exception(invaliderror);
			}
			return t;
			
			/////////////////////
			case '/':nextCh();
				if(ch=='/'){
					nextCh();
					while(ch!= '\n' && ch!= eofCh){
						nextCh();
						CV+=  ch;
					}
					t.kind = "";
					return NextToken();
					
				}
				else if(ch == '*'){
					CV = "";
					nextCh();
					t.kind = "";
					while(ch != eofCh){
						if(ch == '*'){
							
							nextCh();
							if(ch == '/'){
								
								nextCh();
								t.kind = "";
								return NextToken()	;
								
							}
						}
						else
							nextCh();
					}
					return NextToken();
					
				}
				else{
					t.kind = "/";
					return t;
				}
			case 0 :
				t.kind = "$";
				return t;
			
			/////////////////////
			default: nextCh(); t.kind = "none";
			throw new Exception(String.format("character %c is not recognized in line %d and col %d", ch, line, col));
			//break;
		}
		return t;
	}
	
//private methods////////////////////////////////	
	private void init() throws Exception{
		
			sourceFile = new File(filename);
			if (!sourceFile.exists())
				throw new Exception("File does not exist: " + sourceFile);
			if (!sourceFile.isFile())
				throw new Exception("Should not be a directory: " + sourceFile);
			if (!sourceFile.canRead())
				throw new Exception("Can not read inputsourceFileile: "
						+ sourceFile);
			in = new FileInputStream(sourceFile);
			
			
	//		InputStream s = new FileInputStream(this.filename);		
//			in = new InputStreamReader(s);
			
			line = 1;
			col = 0;
			
			nextCh();
	}
	private static void nextCh(){
		try {
			ch = (char) in.read(); 
			col++;
			if(ch == '\n'){
				line++;
				col= 0;
			}
			else if(ch == '\uffff')
				ch = eofCh;
		} catch (IOException e) {
			ch = eofCh;
		}
	}
	
	
	// TODO check if the nextCh is correct, check CV vs Str
	private static Token readName(Token t){

		String tmp = "";
		tmp += ch;
		nextCh();
		while(Character.isLetterOrDigit(ch) || ch == '_'){
			tmp += ch;
			nextCh();
		}
		CV = tmp;
		t.str = tmp;
		
		
		int ind = Arrays.binarySearch(keywords,0, keywords.length, tmp);
		
		
		if(tmp.equals( "true" )|| tmp.equals("false"))
			t.kind = "bool_literal";
		else if (ind >= 0){
		
			t.kind = keywords[ind];
		}
		else if(tmp.equals( "main")){
			
			t.kind = "mainId";
		}
		else
			t.kind = "id";
		t.str = CV;
		return t;
			
	}
	private static Token readNumber(Token t){
		CV = "" + ch;
		char firstchar = ch;
		nextCh();
		if(firstchar == '0' && ch == 'x'){ // handling hexadecimal numbers ???ba 0x ya bedune ??
			CV = "0x";
			nextCh();
			while (Character.isDigit(ch)|| ('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F')) {
				CV += ch;
				nextCh();
			}
			
			t.kind = "hex_literal";
			t.str = CV;
			return t;
		}
		while (Character.isDigit(ch)) {
			CV += ch;
			nextCh();
		}
		if (ch == '.') {
			CV += ch;
			nextCh();
			while (Character.isDigit(ch)) {
				CV += ch;
				nextCh();
			}
			t.kind = "float_literal";
			t.str = CV;
			return t;
		}
		t.kind = "int_literal";
		t.str = CV;
		return t;
	}
	
	// TODO check this function
	private static Token readCharCon(Token t) throws Exception{

		CV = "";
		String tmp = "";
		nextCh();
		String error = String.format("Bad declaration of char: line %d, col %d", t.line, t.col);
		
		if (ch == '\"' || ch == '\''){
			nextCh();
			throw new Exception(error);
		}
		
		else if (ch == '\\'){
			nextCh();
			if (ch == '\'') // c = '\''
				CV += '\'';
			else if(ch == '\"'){ //c = '\"'
				CV += '\"';
			}
			else if(ch == 'n'){
				CV+="\\n";
			}
			else if (ch == 't')
				CV += "\\t";
			
			else if (ch == '\\'){
				CV += "\\";
			}
			else
				throw new Exception(error);
		}
		else
			CV += ch;
		nextCh();
		if(ch != '\'')
			throw new Exception(error);
		nextCh();
		t.kind = "char_literal";
		t.str = CV;
		return t;
		
	}
	private static Token readString(Token t) throws Exception{
		nextCh();
		CV = "";
		while (ch != '\"' && ch != eofCh) { 
			if (ch == '\\') {
				nextCh();
				
				if (ch == 'n')
					CV += '\n';
				
				else if (ch == '"')
					CV += '\"';
				else if (ch == 't')
					CV += '\t';
				else	CV += ch;
				
			} 
			else
				CV += ch;
			nextCh();
		}
		if (ch != '\"')
			throw new Exception("Expected \" at the end of the string");
		nextCh();
		t.kind = "string_literal";
		t.str = CV;
		return t;
	}
	/*
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner("E:/sharif/term6/compiler/projectCode/myCompiler/src/main/inp.L");
		Token check = new Token();
		int x = 0;
		while (!check.kind.equals("$")) {
			try{
				check = sc.NextToken();
				System.out.println( sc.CV +"  " + check.kind);
				x++;}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
			
		}
	}*/
	
}
