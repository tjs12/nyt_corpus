package classify;

import java.util.ArrayList;
import java.util.List;

import files.Files;

import com.nytlabs.corpus.*;

import java.io.*;

public class Classify {
	public static void main(String[] args)
	{
		Files f = new Files();
		ArrayList<NYTCorpusDocument> docs = f.getDocuments("C:\\Users\\Francis\\Desktop\\nyt_corpus\\data", 0, 2000);
		ArrayList<Integer[]> vec = f.getVectors(docs);
		ArrayList<ArrayList<Integer>> cat = f.getCategory();
		int size = vec.size();
		/*try {
			BufferedWriter out = new BufferedWriter(new FileWriter("c:\\Users\\Francis\\Desktop\\nyt_corpus\\out.txt"));
			
			int cla = 0;
			
			for (int i = 0; i < size; i++) {
				boolean flag = false;
				for (Integer c : cat.get(i)) {
					if (c != cla) continue;
					out.write(c.toString() + " ");
					for (int j = 0; j < vec.get(i).length; j++)
						if (vec.get(i)[j] != 0)
							out.write(Integer.toString(j + 1) + ":" + Integer.toString(vec.get(i)[j]) + " ");
					out.newLine();
					flag = true;
				}
				if (!flag) {
					out.write("1 ");
					for (int j = 0; j < vec.get(i).length; j++)
						if (vec.get(i)[j] != 0)
							out.write(Integer.toString(j + 1) + ":" + Integer.toString(vec.get(i)[j]) + " ");
					out.newLine();
				}
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		int catNum = f.categoryIntMap().size();
		//ArrayList<Integer[]>
		//knn_test((ArrayList<Integer[]>)vec.subList(1000, vec.size()), (ArrayList<ArrayList<Integer>>)cat.subList(1000, vec.size()), (ArrayList<Integer[]>)vec.subList(0, 1000),  (ArrayList<ArrayList<Integer>>)cat.subList(0, 1000), catNum);
		knn_test(vec.subList(1800, 1900/*vec.size()*/), cat.subList(1800, 1900/*vec.size()*/), vec.subList(0, 1000),  cat.subList(0, 1000), catNum);
	}
	
	//public static void knn_test(ArrayList<Integer[]> test, ArrayList<ArrayList<Integer>> testcat, ArrayList<Integer[]> train, ArrayList<ArrayList<Integer>> traincat,  int catNum)
	public static void knn_test(List<Integer[]> test, List<ArrayList<Integer>> testcat, List<Integer[]> train, List<ArrayList<Integer>> traincat,  int catNum)
	{
		List<ArrayList<Integer>> result = knn(50, test, train, traincat, catNum, 5);
		double right = 0;
		boolean flag;
		for (int i = 0; i < result.size(); i++) {
			flag = false;
			ArrayList<Integer> predict = result.get(i);
			ArrayList<Integer> truth = testcat.get(i);
			for (int j = 0; j < predict.size(); j++) {
				for (int k = 0; k < truth.size(); k++) {
					if (predict.get(j) == truth.get(k)) {
						//right++;
						flag = true;
						break;
					}
					if (flag) break;
				}
				if (flag) break;
			}
			if (flag) right++;
		}
		System.out.println((right / result.size()));
	}
	
	//public static ArrayList<ArrayList<Integer>> knn(int k, ArrayList<Integer[]> in, ArrayList<Integer[]> train, ArrayList<ArrayList<Integer>> traincat, int catNum, int lower_lim)
	public static ArrayList<ArrayList<Integer>> knn(int k, List<Integer[]> in, List<Integer[]> train, List<ArrayList<Integer>> traincat, int catNum, int lower_lim)
	{
		ArrayList<Integer> ret;
		ArrayList<ArrayList<Integer>> ret1 = new ArrayList<ArrayList<Integer>>();
		int []flag = new int[catNum];
		
		for (Integer []vec : in) {
			int nn = -1;
			ret = new ArrayList<Integer>();
			for (int i = 0; i < catNum; i++) flag[i] = 0;
			for (int i = 0; i < k; i++) {
				nn = _findnearest(vec, train, nn);
				for (Integer j : traincat.get(nn)) {
					flag[j]++;
				}
			}
			for (int i = 0; i < catNum; i++) {
				if (flag[i] > lower_lim) ret.add(i);
			}
			ret1.add(ret);
		}
		
		return ret1;
	}
	
	//private static int _findnearest(Integer[] v, ArrayList<Integer[]> vecs, int lim_n)
	private static int _findnearest(Integer[] v, List<Integer[]> vecs, int lim_n)
	{
		int n = -1;
		double lim = lim_n == -1 ? -1 :dist(v, vecs.get(lim_n)), min = -1;
		for (int i = 0; i < vecs.size(); i++) {
			double d = dist(v, vecs.get(i));
			if (min == -1 || d < min && d > lim) {
				n = i;
				min = d;
			}
		}
		return n;
	}
	
	private static double dist(Integer[] x, Integer[] y)
	{
		assert(x.length == y.length);
		double res = 0, xl = 0, yl = 0;
		for (int i = 0; i < x.length; i++) {
			xl += x[i] * x[i];
			yl += y[i] * y[i];
			res += x[i] * y[i];
		}
		res = res / Math.sqrt(xl * yl);
		return 1 - res;
	}
	
	
}
