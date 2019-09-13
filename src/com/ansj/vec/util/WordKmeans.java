package com.ansj.vec.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import com.ansj.vec.Word2VEC;
import PreProcess.*;

/**
 * keanmeans聚类
 * 
 * @author 魏康健
 * 
 */
//重点！根据簇包含的数量多少来判断热点
//WORD2VEC的维数限定是200
public class WordKmeans {

    public static void main(String[] args) throws IOException {
//        WordKmeans wordKmeans = new WordKmeans(vec.getWordMap(), 50, 10);//第二个参数K的值，第三个迭代的轮次
        WordKmeans wordKmeans = new WordKmeans(WordKmeans.getmaps(new File("docVector/docVectorNew2000.dat")), 65, 30);
        Classes[] explain = wordKmeans.explain();//名称和方法不一样，虽然都是explain
        ArrayList<Classes> res = new ArrayList<Classes>();
        for (int i = 0; i < explain.length; i++) {
        	res.add(explain[i]);
        }
        Collections.sort(res);//按照簇大小进行簇的排序
        for(int i=0;i<res.size();i++){
        	explain[i] =res.get(i);
        	explain[i].id =i;
        }
        for (int i=0;i<explain.length;i++) {
        	finalCenter.put(explain[i].id, explain[i].getCenter());
        }
        HashMap<Integer, float[]> duibi=finalCenter;
       writeObjectToFile(finalCenter,"Distancedata");
        writeObjectToFile(explain,"classobject");
        for(int i=0;i<res.size();i++){
          System.out.println("--------" + i + "----"+res.get(i).values.size());
          System.out.println(res.get(i).getTop(30));//每个类别取前三十个
        }


    }

    private HashMap<String, float[]> wordMap = null;

    private int iter;

    private Classes[] cArray = null;
    
    private static HashMap<Integer, float[]> finalCenter=new HashMap<>();
    
    public static HashMap<String, float[]> getmaps(File f){
    	HashMap<String, float[]> maps =new HashMap<String, float[]>();
    	@SuppressWarnings("unchecked")
		HashMap<record, float[]> map = (HashMap<record, float[]>) WordKmeans.readObjectFromFile(f);
		for(record r:map.keySet()){
			maps.put(r.id, map.get(r));
			if(map.get(r)==null)
				System.out.println("ERROR");
		}
		return maps;
    }
    
    public static void writeObjectToFile(Object obj,String filename)
    {
        File file =new File(filename+".dat");
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
    public static Object readObjectFromFile(File file)
    {
        Object temp=null;
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

    public WordKmeans(HashMap<String, float[]> wordMap, int clcn, int iter) {
    	//获得待聚类的文档的集合
        this.wordMap = wordMap;
       //迭代次数
        this.iter = iter;
      //clen是K-MEANS的K，选取K个点，以CLasses的形式存储结果簇
        cArray = new Classes[clcn];
    }

    public Classes[] explain() {
        //first 取前clcn个点
        Iterator<Entry<String, float[]>> iterator = wordMap.entrySet().iterator();
        for (int i = 0; i < cArray.length; i++) {
            Entry<String, float[]> next = iterator.next();
            cArray[i] = new Classes(i, next.getValue());
        }

        for (int i = 0; i < iter; i++) {
            for (Classes classes : cArray) {
                classes.clean();
            }

            iterator = wordMap.entrySet().iterator();
            while (iterator.hasNext()) {
            	//对所有的词向量进行遍历，用K个聚类中心进行比较
                Entry<String, float[]> next = iterator.next();
                double miniScore = Double.MAX_VALUE;
                double tempScore;
                int classesId = 0;
              //K个中心点依次何和所有的数据进行比较，选取距离最近的点
                for (Classes classes : cArray) {
                    tempScore = classes.distance(next.getValue());
                    if (miniScore > tempScore) {
                        miniScore = tempScore;
                        classesId = classes.id;
                    }
                }
                //将距离最近的文档，放到其对应的簇中
                 cArray[classesId].putValue(next.getKey(), miniScore);
             }

            for (Classes classes : cArray) {
                classes.updateCenter(wordMap);//更新中心距
            }
            System.out.println("iter " + i + " ok!");
        }
        return cArray;
    }

    public static class Classes implements Comparable<Classes>,Serializable {
        public  int id;

        private float[] center;

        public float[] getCenter(){
        	return center;
        }
        public Classes(int id, float[] center) {
            this.id = id;
            this.center = center.clone();
        }

        Map<String, Double> values = new HashMap<>();//词名和该单词到中心距离
        
        
        public double distance(float[] value) {
            double sum = 0;
            for (int i = 0; i < value.length; i++) {
                sum += (center[i] - value[i])*(center[i] - value[i]) ;
            }
            return sum ;
        }

        public void putValue(String word, double score) {
            values.put(word, score);
        }

        /**
         * 重新计算中心�?
         * @param wordMap
         */
        public void updateCenter(HashMap<String, float[]> wordMap) {
        	//先将旧的质心清零
            for (int i = 0; i < center.length; i++) {
                center[i] = 0;
            }
            float[] value = null;
            //遍历该簇中所有文档，对其向量求和
            for (String keyWord : values.keySet()) {
                value = wordMap.get(keyWord);
                for (int i = 0; i < value.length; i++) {
                    center[i] += value[i];
                }
            }
            //文档向量的和除以文档数目得出质心
            for (int i = 0; i < center.length; i++) {
                center[i] = center[i] / values.size();
            }
        }
        public void update(Classes goal,HashMap<String, float[]> map) {
//        	System.out.println("大小是————————"+values.size());
            for (int i = 0; i < center.length; i++) {
                center[i] = center[i]*values.size();
            }
            float[] value = null;
            for (String keyWord : goal.values.keySet()) {
            	putValue(keyWord, goal.values.get(keyWord));
                value = map.get(keyWord);
                for (int i = 0; i < value.length; i++) {
                    center[i] += value[i];
                }
            }
            for (int i = 0; i < center.length; i++) {
                center[i] = center[i] / values.size();
            }
        }

        /**
         * 清空历史结果
         */
        public void clean() {
            // TODO Auto-generated method stub
            values.clear();
        }

        /**
         * 取得每个类别的前n个结�?
         * @param n
         * @return 
         */
        public List<Entry<String, Double>> getTop(int n) {
            List<Map.Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>(
                values.entrySet());
            Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                    // TODO Auto-generated method stub
                	Entry<String, Double> x1 =o1;
                	Entry<String, Double> x2 =o2;
                    return o1.getValue() .doubleValue()==o2.getValue().doubleValue() ? 0 : (o1.getValue().doubleValue() >o2.getValue().doubleValue()?1:-1);
                }
            });
            int min = Math.min(n, arrayList.size());
            if(min<1)return Collections.emptyList() ;
            return arrayList.subList(0, min);
        }

		@Override
		public int compareTo(Classes o) {
			// TODO Auto-generated method stub
			
			return o.values.size()-this.values.size();
		}

    }

}
