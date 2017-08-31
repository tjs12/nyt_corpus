package files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.nytlabs.corpus.*;

public class Files {
	
	private ArrayList<String> filenames = new ArrayList<String>();
	
	private void _getfiles(String path, int number)
	{
		if (number != 0 && filenames.size() > number) return;
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			String filepath = listOfFiles[i].getAbsolutePath();
		   	if (listOfFiles[i].isFile()) {
		   		if (filepath.substring(filepath.length() - 3).compareTo("xml") == 0)
		   			filenames.add(filepath);
		   	} else if (listOfFiles[i].isDirectory()) {
		   		_getfiles(filepath, number);
		  	}
		}
	}
	
	public ArrayList<NYTCorpusDocument> getDocuments(String path, int start, int number)
	{
		ArrayList<NYTCorpusDocument> ret = new ArrayList<NYTCorpusDocument>();
		NYTCorpusDocumentParser parser = new NYTCorpusDocumentParser(); 
		if (filenames.isEmpty()) 
			_getfiles(path, number);
		if (number == 0)
			number = filenames.size() - start;
		for (int i = start; i < start + number; i++) {
			File f = new File(filenames.get(i));
			ret.add(parser.parseNYTCorpusDocumentFromFile(f, false));
		}
		return ret;
	}
	
	private HashSet<String> /*cat = new HashSet<String>(), */words = new HashSet<String>();
	private HashMap<String, Integer> cat = new HashMap<String, Integer>();
	private ArrayList<String> invertedcat = new ArrayList<String>();
	//private HashMap<String, Integer> words;
	//private ArrayList<String> orderedwords;
	private ArrayList<HashMap<String, Integer>> sets = new ArrayList<HashMap<String, Integer>>();
	private ArrayList<ArrayList<String>> doccat = new ArrayList<ArrayList<String>>();
	
	private int size;
	
	public ArrayList<Integer[]> getVectors(ArrayList<NYTCorpusDocument> docs)
	{
		size = docs.size();
		//Get all the classes and words
		for (int i = 0; i < size; i++) {
			List<String> l = docs.get(i).getTaxonomicClassifiers();
			for (String s : l) {
				//if (!cat.contains(s)) cat.add(s);
				if (!cat.keySet().contains(s)) {
					cat.put(s, cat.size());
					invertedcat.add(s);
				}
			}
			doccat.add((ArrayList<String>) l);
			
			String[] w = docs.get(i).getBody().split(" ");
			HashMap<String, Integer> set = new HashMap<String, Integer>(); 
			for (String s : w) {
				String s1 = s.toLowerCase();
				if (isword(s1)) {
					if (!words.contains(s1)) {
						words.add(s1);
					}
					if (set.containsKey(s1)) {
						Integer f0 = set.get(s1);
						set.put(s1, f0 + 1);
					}
					else
						set.put(s1, new Integer(1));
				}
			}
			sets.add(set);
		}
		
		ArrayList<Integer[]> vecs = new ArrayList<Integer[]>();
		for (int i = 0; i < size; i++) {
			Integer []arr = new Integer[words.size()];
			vecs.add(arr);
		}
		
		int i = 0;
		for (String w : words) {
			for (int j = 0; j < size; j++) {
				if (sets.get(j).containsKey(w)) {
					vecs.get(j)[i] = sets.get(j).get(w);
				}
				else {
					vecs.get(j)[i] = 0;
				}
			}
			i++;
		}
		return vecs;
	}
	
	private boolean isword(String s)
	{
		if (s.length() == 0) return false;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) < 'a' || s.charAt(i) > 'z')
				return false;
		}
		return true;
	}
	
	public ArrayList<ArrayList<Integer>> getCategory()
	{
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<String> i : doccat) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			for (String j : i) {
				l.add(cat.get(j));
			}
			ret.add(removeClassDuplication(l));
			//ret.add(l);
		}
		return ret;
	}
	
	public HashMap<String, Integer> categoryIntMap()
	{
		return cat;
	}
	
	public ArrayList<Integer> removeClassDuplication(ArrayList<Integer> original)
	{
		boolean []flag = new boolean[cat.size()];
		for (int i = 0; i < cat.size(); i++) flag[i] = false;
		for (int i = 0; i < original.size(); i++) {
			for (int j = 0; j < original.size(); j++) {
				if (i != j && invertedcat.get(i).contains(invertedcat.get(j))) {
					flag[j] = true;
				}
			}
		}
		int removed = 0;
		for (int i = 0; i < original.size(); i++) {
			if (flag[i]) {
				original.remove(i - removed);
				removed++;
			}
		}
		return original;
	}
	
}
