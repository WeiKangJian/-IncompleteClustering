package GUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.ansj.vec.util.GroupClustring;
import com.kennycason.kumo.Outpicture;
public class GuiToPresent {
	   public static void main(String[] args) {
		   
	        final JFrame f = new JFrame("LoL");
	        f.setSize(2000, 800);
	        f.setLocation(0, 0);
	  
	        f.setLayout(null);
	  
	        JPanel pLeft = new JPanel();
	        pLeft.setBounds(50, 50, 300, 60);
	  
	        pLeft.setBackground(Color.LIGHT_GRAY);
	  
	        pLeft.setLayout(new FlowLayout());
	  
	        JButton b1 = new JButton("查看聚类效果");
	        JButton b2 = new JButton("提莫");
	        JButton b3 = new JButton("安妮");
	        JButton b4 =new JButton("选择数据");
	        final JTextField tfName = new JTextField("");
	        tfName.setText("请输入簇号1-50");
//	        tfName.setSize(100,100);
	        pLeft.add(b4);
	        pLeft.add(tfName);
	        pLeft.add(b1);
	        pLeft.add(b2);
	        pLeft.add(b3);
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
	        JPanel pRight = new JPanel();
	        final JLabel lPic = new JLabel("");
	        ImageIcon i = new ImageIcon("e:/project/j2se/gareen.jpg");
	        lPic.setIcon(i);       
	  
	        pRight.add(lPic);
	  
	        pRight.setBackground(Color.lightGray);
	        pRight.setBounds(10, 150, 300, 60);
	  
	        // 创建一个水平JSplitPane，左边是p1,右边是p2
	        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pLeft, pRight);
	        // 设置分割条的位置
	        sp.setDividerLocation(150);
	  
	        // 把sp当作ContentPane
	        f.setContentPane(sp);
	  
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  
	        f.setVisible(true);
	         
	        switchPic(b2,"teemo",lPic);
	        switchPic(b3,"annie",lPic);
	        b4.addActionListener(new ActionListener() {
	              
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                 int returnVal =  fc.showOpenDialog(f);
	                 File file = fc.getSelectedFile();
	                 if (returnVal == JFileChooser.APPROVE_OPTION) {
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
	                 }
	                  
	            }
	        });
	        b1.addActionListener(new ActionListener() {
	              
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                	 String s =tfName.getText();
	                	 int num =Integer.parseInt(s);
	                     ImageIcon i = new ImageIcon("G:/输出图片/"+num+".png");
	                     lPic.setIcon(i);
	                     lPic.setBounds(0,0, i.getIconWidth(), i.getIconHeight());
	                     tfName.setText("请输入簇号1-50");
	                  
	            }
	        });
	        
	    }
	 
	    private static void switchPic(JButton b1, final String fileName, final JLabel lPic) {
	        b1.addActionListener(new ActionListener() {
	             
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                ImageIcon i = new ImageIcon("e:/project/j2se/"+fileName+".jpg");
	                lPic.setIcon(i);   
	            }
	        });
	         
	    }
}
