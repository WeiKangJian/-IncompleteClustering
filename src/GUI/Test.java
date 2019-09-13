package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ansj.vec.util.GroupClustring;
import com.kennycason.kumo.Outpicture;
public class Test {
    public static void main(String[] args) throws IOException{
//    	GroupClustring.start();
    	Outpicture a =new Outpicture();
        JFrame f = new JFrame("实时聚类效果图");
        f.setSize(1000, 10000);
        f.setLocation(580, 200);
        f.setLayout(null);
  
        final JLabel l = new JLabel();
  
        ImageIcon i = new ImageIcon("G:/输出图片/0.png");
        l.setIcon(i);
        l.setBounds(0, 0, i.getIconWidth(), i.getIconHeight());
  
//        JButton b = new JButton("隐藏图片");
//        b.setBounds(220, 330, 100, 30);
//  
//        // 给按钮 增加 监听
//        b.addActionListener(new ActionListener() {
//  
//            // 当按钮被点击时，就会触发 ActionEvent事件
//            // actionPerformed 方法就会被执行
//            public void actionPerformed(ActionEvent e) {
//                l.setVisible(false);
//            }
//        });
  
        f.add(l);
//        f.add(b);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
        f.setVisible(true);
    }
}
