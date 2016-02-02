package main;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CodeScanner {
	public ArrayList<String> Code;
	public String Line;
	int Counter;
	Scanner sLine;
	
	public CodeScanner(String fileName) throws FileNotFoundException{
		Code = new ArrayList<String>();
		Scanner reader = new Scanner(new File(fileName));
		while(reader.hasNext()){
			Code.add(reader.nextLine());
		}
		reader.close();
		Counter = 0;
		Line = Code.get(0);
		sLine = new Scanner(Line);
	}
	public String next(){
		if(sLine.hasNext())
			return sLine.next();
		else if(Counter < Code.size()){
			sLine.close();
			Counter ++;
			Line = Code.get(Counter);
			sLine = new Scanner(Line);
			return sLine.next();
			}
		return null;
	}
	public boolean hasNext(){
		if(sLine.hasNext())
			return true;
		else if(Counter < Code.size() - 1){
			return true;
		}
		return false;
	}
	public void jump(int lineNumber){
		if(lineNumber < Code.size()){
			Counter = lineNumber;
			Line = Code.get(Counter);
			sLine.close();
			sLine = new Scanner(Line);
		}
	}
	public void close(){
		return;
	}
}
