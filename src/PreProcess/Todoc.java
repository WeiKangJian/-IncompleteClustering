package PreProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.util.TermRepresent;

public class Todoc  {
	public static String readInput(String inputFileName)
    {
        //灏唅nputFileName涓殑鍐呭璇诲叆鍒颁竴涓瓧绗︿覆鏁扮粍涓�
//		List<String> ret = new ArrayList<String>();  
		StringBuffer sb =new StringBuffer();
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName),"utf-8"));
            
            String temp;
            while ((temp = br.readLine()) != null)
            {
               sb.append(temp);
               }
            br.close();            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return (sb.toString());
    }
	public static String[] getdoc(){
		List<String> ret = new ArrayList<String>(); 
		for(int i=1;i<9510;i++){
		String trainfile ="G:/新闻分词/"+i+".txt";
		ret.add(Todoc.readInput(trainfile));
		System.out.println(i+"/10000");
		}
		 String[] fileString=new String[ret.size()];
		return (String[])ret.toArray(fileString);
	}
	public static String[] getterm(int k) throws IOException{
		List<String> ret = new ArrayList<String>(); 
        Word2VEC vec = new Word2VEC();
        vec.loadJavaModel("G:/新闻分词训练/"+k+".txt");
       HashMap<String,float[]> map= vec.getWordMap();
       Set<String> set =map.keySet();
       for(String s: set){
    	   if(s.length()!=0)
    	   ret.add(s);
       }
		 String[] fileString=new String[ret.size()];
		return (String[])ret.toArray(fileString);
	}
	public static HashMap<String,Float> getTfIdf(int k,String[] allDocs) throws IOException{
		float Max =0;
		float Min = Integer.MAX_VALUE;
		HashMap<String,Float> re =new HashMap<String,Float>();
		TermRepresent tr =new TermRepresent();
//		String [] allDocs = Todoc.getdoc();
//		System.out.println(allDocs[0]);
		String [] termDic =Todoc.getterm(k);
//		System.out.println(termDic[0]);
		float res[][] = tr.TermRepresentMain(allDocs, termDic);
			//鎵惧嚭鏈�澶ф渶灏忓��
			for(int i=0;i<termDic.length;i++){
	                 Max =Math.max(Max, res[i][k-1]);
	                 Min =Math.min(Min, res[i][k-1]);
			}
			float mid =Max-Min;
		//褰掍竴
			for(int i=0;i<termDic.length;i++){
					   if(res[i][k-1]!=0){
						   res[i][k-1]=(res[i][k-1]-Min)/mid;
					   }
				}
//		System.out.println(res[0][1]);
//		//res[璇嶆暟][鏂囨。鏁癩
			for(int j=0;j<res.length;j++)
			{
				if(res[j][k-1]!=0){
					System.out.println(j+" "+termDic[j]+" "+res[j][k-1]);
					re.put(termDic[j],res[j][k-1]);
				}
			}
			return re;
	}
	static class fuzhu{
		record r1;
		float[] f1;
		public fuzhu(record r1,float[] f1){
			this.r1 =r1;
			this.f1 =f1;
		}
	}
	public static fuzhu finalResult(int a,String[] allDocs) throws IOException{
		//实例化Word2CEV模型
        Word2VEC vec = new Word2VEC();
        //用Word2VEC加载文档文件，生成词向量
        vec.loadJavaModel("G:/新闻分词训练/"+a+".txt");
       HashMap<String,float[]> re1= vec.getWordMap();
       //获取该词向量的TF-IDF值的到权重
		HashMap<String,Float> re2= Todoc.getTfIdf(a,allDocs);
		float [] flag1 = null;
		float flag2;
		float [] flag =new float[200];
		//将词向量和TF-IDF值结合，生成文档向量
		for(String s:re2.keySet()){
			flag1 = re1.get(s);
			flag2 = re2.get(s);
			for(int i=0;i<flag1.length;i++){
				flag1[i]*=flag2;
				flag[i]+=flag1[i];
			}
		}
		for(int i=0;i<flag.length;i++){
			flag[i] = flag[i]/200;
		}
		String strs[] =new String [re2.size()];
		int k=0;
		for(String s:re2.keySet()){
			strs[k] =s;
			k++;
		}
		record r1 =new record(String.valueOf(a),strs);
		fuzhu f =new fuzhu(r1, flag);
		return f;
	}
	public static void writeObjectToFile(Object obj,int i)
    {
        File file =new File("docVector/docVectorNew"+i+".dat");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }
	public static Object readObjectFromFile(int i)
    {
        Object temp=null;
        File file =new File("docVector/docVector"+i+".dat");
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }
//	class HashMap implements Serializable{
//		
//	}
	public static void main(String [] args) throws IOException{
		HashMap<record, float[]> it =new HashMap<record, float[]>();
		String [] allDocs = Todoc.getdoc();
		for(int i=8000;i<9000;i++){
			fuzhu f1 =finalResult(i,allDocs);
			it.put(f1.r1,f1.f1);
			System.out.println(i+"IS OK!");
		}
		writeObjectToFile(it,9000);
	}
	
}
