package com.ansj.vec.util;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import java.util.Set;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.util.WordKmeans.Classes;
import com.kennycason.kumo.Outpicture;

import PreProcess.record;

public class GroupClustring {

	   public static void writeObjectToFile(Object obj,String filename)
	    {
		    //指定序列化文件存储的位置
	        File file =new File(filename+".dat");
	        //生成流文件对象
	        FileOutputStream out;
	        try {
	            out = new FileOutputStream(file);
	            ObjectOutputStream objOut=new ObjectOutputStream(out);
	            objOut.writeObject(obj);
	            //流缓存进行刷新
	            objOut.flush();
	            objOut.close();
	            //反馈流文件存储的结果信息
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

	public static Object readObjectFromFile(String filename)
    {
        Object temp=null;
        File file =new File(filename+".dat");
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
	//进行距离的度量计算，用当前窗口的每一个簇间距离和存储的进行比较，找出距离最小的。二重循环
	public HashMap<Integer, fuzhu> calcuate(Classes[] explain,HashMap<Integer,float[]> finalCenter){
		HashMap<Integer, fuzhu> res =new HashMap<Integer, fuzhu>();
		for(Classes classesInstance:explain){
			double minDistance=Integer.MAX_VALUE;
   			double curDistance =0;
			int finid =0;
    		for(int i=0;i<finalCenter.size();i++){
    			curDistance=classesInstance.distance(finalCenter.get(i));
    				if(curDistance<minDistance){
    					minDistance =curDistance;
    					finid = i;
    				}
    		}
    		fuzhu instance =new fuzhu(finid, minDistance);
    		res.put(classesInstance.id, instance);
    	}
		return res;
	}
	@SuppressWarnings("unchecked")
	public static void start(File f) throws IOException{
		//重设定一个函数，新增参数，每次保存当前为之的前K个簇的簇心位置
//        Word2VEC vec = new Word2VEC();
        GroupClustring group = new GroupClustring();
        HashMap<Integer,float[]> finalCenter = (HashMap<Integer, float[]>) GroupClustring.readObjectFromFile("Distancedata");
        Classes[] finalclass =(Classes[]) GroupClustring.readObjectFromFile("classobject");
       //建立存储可能需要的临时簇
      ArrayList<Classes>  curentclass =new ArrayList<Classes>();
      HashMap<String, float[]> map =new HashMap<String, float[]>();
      map =WordKmeans.getmaps(f);
        WordKmeans wordKmeans = new WordKmeans(map, 65, 30);//第二个参数K的值，第三个迭代的轮次
        Classes[] explain = wordKmeans.explain();//名称和方法不一样，虽然都是explain
      //以上是在一个新的时间窗口内的聚类的簇
     //后面是引进簇间聚类的步骤和函数
     //序列化读取存储的当前每一个簇的中心向量
     //进行距离的度量计算，用当前窗口的每一个簇间距离和存储的进行比较，找出距离最小的。二重循环
        	HashMap<Integer, fuzhu> res =new HashMap<>();
        	res =group.calcuate(explain,finalCenter);
     //测试输出依次为，当前簇的ID，到数据库的历史簇的簇间距离最近的历史簇的ID，之间的距离大小
     for(int i=0,j=49;i<res.size();i++){
    	//制定规则，选择合并簇，参考簇间距离以及簇的大小，动态舍弃簇
    	//如果新的簇的规模大于指定阈值，无条件将其作为新簇
    	 if(explain[i].values.size()>=200){
    		 curentclass.add(explain[i]);
    		 System.out.println(i+" "+res.get(i).id+" "+res.get(i).distance+" "+finalclass[res.get(i).id].values.size()+" "+explain[i].values.size());
    		 continue;
    	 }
    	 if(res.get(i).distance>0.0001){
    	// 距离足够远，但簇聚集的个数较少，舍弃，不做处理
    		 if(explain[i].values.size()<10){
    			 System.out.println(i+" "+res.get(i).id+" "+res.get(i).distance+" "+finalclass[res.get(i).id].values.size()+" "+explain[i].values.size());
    			 continue;
    		 }
    		 if(explain[i].values.size()>=10){
        // 距离足够远，且簇的个数足够多，新生成一个簇替换旧的簇
    			 curentclass.add(explain[i]);
    		 }
    	 }
        //两个簇距离小于阈值,合并掉
    	 else{
    		 int T=res.get(i).id;
    	  //2：对合并后的簇完成中心距离跟新，
    		 finalclass[T].update(explain[i],map);
    	 }
    	 System.out.println(i+" "+res.get(i).id+" "+res.get(i).distance+" "+finalclass[res.get(i).id].values.size()+" "+explain[i].values.size());
     }
     for(int i=0;i<curentclass.size();i++){
    	 System.out.println(curentclass.get(i).id+" "+curentclass.get(i).values.size());
     }
     
  //将添加新元素的旧簇和新生成的簇进行排序。重新序列化写入文件，当前窗口完成
     for(int i=0;i<finalclass.length;i++){
    	 curentclass.add(finalclass[i]);
     }
//     curentclass.add(explain[9]);
     Collections.sort(curentclass, new Comparator<Classes>() {

		@Override
		public int compare(Classes o1, Classes o2) {
			// TODO Auto-generated method stub
			return o2.values.size()-o1.values.size();
		}
	});
     ArrayList<Classes> filter =new ArrayList<>();
     for(int i=0;i<65;i++){
    	 curentclass.get(i).id=i;
    	 filter.add(curentclass.get(i));
    	 System.out.println(curentclass.get(i).id+" "+curentclass.get(i).values.size());
     }
     filter.toArray(finalclass);
     finalCenter.clear();
     for(int i=0;i<65;i++){
    	 finalCenter.put(i, finalclass[i].getCenter());
     }
     HashMap<Integer,float[]> duibi=finalCenter;
     Classes[] duibiclass =finalclass;
     //写入文件
     writeObjectToFile(finalCenter, "Distancedata");
     writeObjectToFile(finalclass,"classobject");
     //3：加入时间因素和生成簇，即词云的初始文件
     ToResult();
	}
	public static void ToResult() throws IOException{
		HashMap<record, float[]> map =(HashMap<record, float[]>) readObjectFromFile("Records");
		Set<record> set =map.keySet();
        Classes[] finalclass =(Classes[]) GroupClustring.readObjectFromFile("classobject");
        for(int i=0;i<30;i++){
        	List<Entry<String,Double>> list = finalclass[i].getTop(30);
        	File f =new File("G:/输出簇文件/"+i+".txt");
        	BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        	for(Entry<String,Double> e:list){
        		String s =e.getKey();
        		for(record r:set){
        			if(r.id.equals(s)){
        				String[] strs = r.themes;
        				if(strs.length>5){
        				String res = strs[0]+strs[1]+strs[2]+strs[3]+strs[4];
        						bw.write(res);
        						bw.newLine();
        				}
        				else
        				{
//    						bw.newLine();
        				}
        	        			break;
        			}
        		}
        	}
        	bw.close();
        }
	}
	public static void GUI(){
        final JFrame f = new JFrame("本科毕业设计展示界面 不完全聚类系统的设计和实现 魏康健");
        f.setSize(1000, 660);
        f.setLocation(520, 110);
        f.setLayout(null);
//        f.setLocationRelativeTo(null);
        JPanel pLeft = new JPanel();
        pLeft.setBounds(50, 50, 300, 60);
  
        pLeft.setBackground(Color.ORANGE);
        FlowLayout fl =new  FlowLayout();
        fl.setVgap(80);
        pLeft.setLayout(fl);
        JLabel ll =new JLabel("簇号：");
        ll.setForeground(Color.red);
        ll.setFont(new Font("黑体", Font.BOLD, 14));
        JButton b1 = new JButton("查看聚类效果");
        JButton b2 = new JButton("关闭窗口");
        JButton b4 =new JButton("选择聚类文档数据");
        JButton NextB =new JButton("查看下一个簇词云");
        final JTextField tfName = new JTextField(7);
        tfName.setHorizontalAlignment(JTextField.CENTER);
        tfName.setText("输入簇号1-30");
//        tfName.setSize(100,100);
        pLeft.add(b4);
        pLeft.add(ll);
        pLeft.add(tfName);
        pLeft.add(b1);
        pLeft.add(NextB);
        pLeft.add(b2);
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
             
            @Override
            public String getDescription() {
                // TODO Auto-generated method stub
                return ".dat";
            }
             
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".dat");
            }
        });
        final JPanel pRight = new JPanel();
        pRight.setLayout(new  FlowLayout());
        final JLabel lPic = new JLabel("");
        pRight.setBounds(10, 150, 300, 60);
        pRight.setBackground(Color.black);

        pRight.add(lPic);
        
 
  
        // 创建一个水平JSplitPane，左边是p1,右边是p2
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pLeft, pRight);
        // 设置分割条的位置
        sp.setDividerLocation(150);
  
        // 把sp当作ContentPane
        f.setContentPane(sp);
  
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
        f.setVisible(true);
			final JDialog j =new JDialog(f, "聚类中 ....",false);
				j.setLocation(1000, 400);
         
        b4.addActionListener(new ActionListener() {
              
            @SuppressWarnings("deprecation")
			@Override
            public void actionPerformed(ActionEvent e) {
                 int returnVal =  fc.showOpenDialog(f);
                 File file = fc.getSelectedFile();
                 if (returnVal == JFileChooser.APPROVE_OPTION) {
                	 try {
 						j.show();
						new GroupClustring().start(file);
//						JOptionPane.showMessageDialog(f, "正在聚类中....请等待");
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
                  	for(int i=0;i<30;i++){
                    	Outpicture a =new Outpicture();
            			try {
							a.out("G:/输出簇文件/"+i+".txt","resources/backgrounds/whale_small.png",i);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                 	}
                     JOptionPane.showMessageDialog(f, "聚类完成,一共30个簇，请输入簇的编号查看聚类效果");
                     j.dispose();
                 }
                  
            }
        });
        b1.addActionListener(new ActionListener() {
              
            @Override
            public void actionPerformed(ActionEvent e) {
                	 String s =tfName.getText();
                	 int num =Integer.parseInt(s);
                	 String c =new String("G:/输出图片/"+num+".png");
                     ImageIcon i = new ImageIcon(c);
                     i.getImage().flush();
                      i = new ImageIcon(c);
                     lPic.setIcon(i);
                     lPic.setBounds(136,5, i.getIconWidth(), i.getIconHeight());
//                     tfName.setText("请输入簇号1-50");
                  
            }
        });
        NextB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s =tfName.getText();
           	 int num =Integer.parseInt(s);
           	 num++;
           	 String c =new String("G:/输出图片/"+num+".png");
                ImageIcon i = new ImageIcon(c);
                i.getImage().flush();
                 i = new ImageIcon(c);
                lPic.setIcon(i);
                lPic.setBounds(136,5, i.getIconWidth(), i.getIconHeight());
                tfName.setText(String.valueOf(num));
                if(num==30){
                	tfName.setText("1");
                }
				// TODO Auto-generated method stub
				
			}
		});
        b2.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
            	f.dispose();
            }
        });
	}
	public static void main(String args[]) throws IOException{
//		   GroupClustring.start();
		GroupClustring.GUI();
    }

//		HashMap<record, float[]> map = new HashMap<record, float[]>();
//		HashMap<record, float[]> map1 = (HashMap<record, float[]>) WordKmeans.readObjectFromFile(2000);
//		HashMap<record, float[]> map2 = (HashMap<record, float[]>) WordKmeans.readObjectFromFile(3000);
//		HashMap<record, float[]> map3 = (HashMap<record, float[]>) WordKmeans.readObjectFromFile(3500);
//		HashMap<record, float[]> map4 = (HashMap<record, float[]>) WordKmeans.readObjectFromFile(4500);
//		map.putAll(map1);
//		map.putAll(map2);
//		map.putAll(map3);
//		map.putAll(map4);
//		writeObjectToFile(map, "Records");
//	}
	public class fuzhu{
		public int id;
		public double distance;
		public fuzhu(int id,double distance){
			this.id =id;
			this.distance =distance;
		}
	}
}
