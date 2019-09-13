package com.ansj.vec.util;
import java.util.*;

//此模块的功能是将预处理后的训练集或待分类文本集，根据特征词典，使用tf*idf算法对文本进行特征表示
public class TermRepresent {
	//private String inputTextFile;//待处理的文本集文件
	//private String inputTermDic;//特征词典文件
	//private static String outputTermRepresent;//文本集的特征表示文件
	
	private static String[] docs;//读入的文本集
	private static String[] terms;//读入的特征词典
	private int numDocs=0;//读入的文本集中文本数
	private int numTerms=0;//读入的特征词典的特征词个数
	private int[][] termFreq;
	private float[][] termWeight;
	private int[] maxTermFreq;
	private int[] docFreq;
	
	
	private Dictionary wordsIndex=new Hashtable();//程序使用的特征词典
	private String[] trDocs;//文本集的特征表示；
	
	
	//2基于特征词典对文本集进行特征表示放在另一个String[]中	
	public float[][] TermRepresentMain(String[] allDocs,String[] termDic){
		
		docs=allDocs;
		terms=termDic;
		numDocs=docs.length;
		numTerms=terms.length;
		maxTermFreq=new int[numDocs] ;
		docFreq=new int[numTerms] ;
		termFreq =new int[numTerms][] ;
		termWeight=new float[numTerms][] ;
		
		//2.1将所有的特征词加入到Hashtable中
		for(int i=0; i < terms.length ; i++)			
		{
			termWeight[i]=new float[numDocs] ;
			termFreq[i]=new int[numDocs] ;

			AddElement(wordsIndex, terms[i], i);			
		}
		//2.2计算tf (特征词在某个文档中出现的次数)
		GenerateTermFrequency();
		//2.3计算weight,并存放在二维数组中		
		GenerateTermWeight();
		return termWeight;
		/*
		//将二维数组表示转为KNN所要求的格式
		System.out.println(termWeight.length);
		System.out.println(termWeight[0].length);
		String[] result=new String[docs.length];
		for(int i=0; i<termWeight[0].length;i++)
		{
			result[i]=docs[i].substring(0, 1);
			for(int j=0;j<termWeight.length;j++)
			{
				//if(termWeight[j][i]!=0)
					result[i]+=" "+termWeight[j][i];
			}
		}
		System.out.println(result.length);
		*/
		//将二维数组表示转为libsvm所要求的格式
		
//		System.out.println(termWeight.length);
//		System.out.println(termWeight[0].length);
//		String[] result=new String[docs.length];
//		for(int i=0; i<termWeight[0].length;i++)
//		{
//			result[i]=docs[i].substring(0, 1);
//			for(int j=0;j<termWeight.length;j++)
//			{
//				if(termWeight[j][i]!=0)
//					result[i]+=" "+j+":"+termWeight[j][i];
//			}
//		}
//		System.out.println(result.length);
//		
//		//String[] result=new String[docs.length];
//		
//		return result; 
	}
	
	//2.1将全部特征词放入到Hashtable中
	private static Object AddElement(Dictionary collection, Object key, Object newValue)
	{
		Object element=collection.get(key);
		collection.put(key, newValue);
		return element;
	}
	
	//2.2计算tf (特征词在某个文档中出现的次数)
	private void GenerateTermFrequency()
	{
		for(int i=0; i < numDocs  ; i++)
		{								
			String curDoc=docs[i];
			Dictionary freq=GetWordFrequency(curDoc);
			Enumeration enums=freq.keys();
			
			while(enums.hasMoreElements()){
				String word=(String) enums.nextElement();
				int wordFreq=(Integer)freq.get(word);
				int termIndex=GetTermIndex(word);
                if(termIndex == -1)
                    continue;
				termFreq [termIndex][i]=wordFreq;
				docFreq[termIndex] ++;

				if (wordFreq > maxTermFreq[i]) maxTermFreq[i]=wordFreq;	
			}			
			maxTermFreq[i]=Integer.MIN_VALUE ;
		}
	}
	private Dictionary GetWordFrequency(String input)
	{
		String convertedInput=input.toLowerCase() ;	       
        String r="([ ])";//每行代表的文档原始格式是：类别|词1|词2|...
        String[] words = convertedInput.split(r);
		Arrays.sort(words);
		
		String[] distinctWords=GetDistinctWords(words);
					
		Dictionary result=new Hashtable();
		for (int i=0; i < distinctWords.length; i++)
		{
			Object tmp;
			tmp=CountWords(distinctWords[i], words);
			result.put(distinctWords[i], tmp);
			
		}
		
		return result;
	}
	private static String[] GetDistinctWords(String[] input)
	{				
		if (input == null)			
			return new String[0];			
		else
		{
            List<String> list = new ArrayList<String>();
			
			for (int i=0; i < input.length; i++)
				if (!list.contains(input[i])) 			
					list.add(input[i]);
			String[] v=new String[list.size()];
			return (String[]) list.toArray(v);
		}
	}
	private int CountWords(String word, String[] words)
	{
		int itemIdx=Arrays.binarySearch(words, word);
		
		if (itemIdx > 0)			
			while (itemIdx > 0 && words[itemIdx].equals(word))				
				itemIdx--;				
					
		int count=0;
		while (itemIdx < words.length && itemIdx >= 0)
		{
			if (words[itemIdx].equals(word)) count++;				
			
			itemIdx++;
			if (itemIdx < words.length)				
				if (!words[itemIdx].equals(word)) break;					
			
		}
		
		return count;
	}
	private int GetTermIndex(String term)
	{
		Object index=wordsIndex.get(term);
		if (index == null) return -1;
		return (Integer)index;
	}
	//2.3计算weight
	private void GenerateTermWeight()
	{			
		for(int i=0; i < numTerms   ; i++)
		{
			for(int j=0; j < numDocs ; j++)				
				termWeight[i][j]=ComputeTermWeight (i, j);				
		}
	}
	private float ComputeTermWeight(int term, int doc)
	{
		float tf=GetTermFrequency (term, doc);
		float idf=GetInverseDocumentFrequency(term);
		return tf * idf;
	}
	private float GetTermFrequency(int term, int doc)
	{			
		int freq=termFreq [term][doc];
		int maxfreq=maxTermFreq[doc];			
		
		return ( (float) freq/(float)maxfreq );
	}
	private float GetInverseDocumentFrequency(int term)
	{
		int df=docFreq[term];
		//20110509添加
		if (df==0)
			return 0;
		else
		//20110509添加
		return Log((float) (numDocs) / (float) df );
	}
	private float Log(float num)
	{
		return (float) Math.log(num) ;//log2
	}
	
}
