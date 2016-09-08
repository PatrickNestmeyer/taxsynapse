package BagOfWords;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import ReducedInvoice.*;

public class WordBag {
	
	private List<String> Bag;
	private List<String> IllegalWords;
	private List<String> IllegalTokens;
	
	public WordBag() throws FileNotFoundException{
		
		this.Bag = new ArrayList<String>();
		this.IllegalWords = new ArrayList<String>();
		this.IllegalTokens = new ArrayList<String>();
	}
	
	public boolean setPaths(String IllegalWordsPath, String IllegalTokensPath){
		try{
			fillStringarrayFromFile(IllegalWordsPath, IllegalWords);
			fillStringarrayFromFile(IllegalTokensPath, IllegalTokens);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private void fillStringarrayFromFile(String Path, List<String> WordArray) throws FileNotFoundException{
		File file = new File(Path);
		Scanner s = new Scanner(file);
		while(s.hasNextLine()){
			WordArray.add(s.nextLine());
		}
		s.close();
	}
	
	public void addWord(String word){
		if(word.contains(" "))
			this.addSentence(word);
		String clearedWord = this.clearWord(word);
		Boolean legal = true;
		for(String illWord : IllegalWords){
			if(clearedWord.toLowerCase().equals(illWord.toLowerCase())){
				legal = false;
				break;
			}
		}
		if(legal && !(this.Bag.contains(clearedWord.toLowerCase())))
			this.Bag.add(clearedWord.toLowerCase());
	}
	
	public void removeWord(String word){
		this.Bag.remove(word);
	}
	
	public Boolean hasWord(String word){
		if(this.Bag.contains(word))
			return true;
		else 
			return false;
	}
	
	public List<String> getBag(){
		return this.Bag;
	}
	
	public void addSentence(Position p){
		this.addSentence(p.getDescription());
	}
	
	public void addSentence(String sentence){
		String[] words = sentence.split(" ");
		for (String word : words) {
			this.addWord(word);
		}
	}
	
	private String clearWord(String word){
		
		for(String token : IllegalTokens)
		{
			word = word.replace(token, "");
		}

		return word;
	}
	
	private void clearWordBag()
	{
		for(int i = 0; i < Bag.size(); i++)
		{
			if(Bag.get(i).contains("\n"))
			{
				Bag.set(i, Bag.get(i).replace("\n", "").replace("\r", ""));
			}
			if(Bag.get(i).equals(""))
			{
				Bag.remove(i);
			}
		}
	}
	
	public void createWordBag(List<AInvoice> ReducedInvoiceList,String pathToInvalidWords, String pathToInvalidTokens)
	{
		setPaths(pathToInvalidWords, pathToInvalidTokens);
		for(AInvoice ai : ReducedInvoiceList)
		{
			for(int i = 0; i < ai.getPositionsLength(); i++)
			{
				addSentence(ai.getPosition(i).getDescription());
			}
		}
		clearWordBag();
	}
}