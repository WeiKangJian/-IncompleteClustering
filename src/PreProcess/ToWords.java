package PreProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import com.ansj.vec.Learn;


public class ToWords {
	public static String[] readInput(String inputFileName)
    {
        //灏唅nputFileName涓殑鍐呭璇诲叆鍒颁竴涓瓧绗︿覆鏁扮粍涓�
		List<String> ret = new ArrayList<String>();        
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName),"gb2312"));
            
            String temp;
            while ((temp = br.readLine()) != null)
            {
                ret.add(temp);
            }
            br.close();            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        String[] fileString=new String[ret.size()];
        return (String[]) ret.toArray(fileString);
    }
	public static void writeOutput(String[] outputContent,String outputFileName)
    {
       //灏唎utputContent涓殑鍐呭鍐欏叆鏂囦欢outputFileName涓�
	  //闇�瑕佷慨鏀癸紝姣忕瘒鏂囨。鍗曠嫭鍙栧嚭
		File f = new File(outputFileName);
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(int i=0;i<outputContent.length;i++)
			{
				bw.write(outputContent[i]);
				bw.newLine();				
			}
			bw.close();
		}
		catch (Exception ex)
	    {
	            ex.printStackTrace();
	    }		
    }
	//鍒濇鐨勫鐞嗭紝灏嗘湁鏍囩鐨勬枃妗ｄ腑鐨勬枃瀛楁彁鍙栧嚭鏉�
	public static void first() throws IOException{
		for(int j=11;j<159;j++){
			File f =new File("G:/News/news.allsites."+j+"0806.txt");
			if(!f.exists()){
				continue;
			}
			Document document = Jsoup.parse(f,"GBK");
			Elements res = document.getElementsByTag("content");
			String [] strings =new String[res.size()];
			for(int i=0;i<res.size();i++){
				Element a =res.get(i);
//				System.out.println(a.text());
				strings[i] =a.text();	
			  }
			ToWords.writeOutput(strings, "G:/NewsF/"+j+".txt");
			}
	}
//鍒嗚瘝澶勭悊鐨勫伐鍏风被
	public static String[] preProcessMain(String[] InputDocs)throws IOException {
		
		String[] OutputDocs=new String[InputDocs.length];
		String row="";
		String t = null;
		int i=0;
		while(i<InputDocs.length) {
			t=InputDocs[i];
			IKSegmentation ikSeg = new IKSegmentation(new StringReader(t) ,true);

			Lexeme l = null;
			while( (l = ikSeg.next()) != null)
			{
				//灏咰JK_NORMAL绫荤殑璇嶅啓鍏ョ洰鏍囨枃浠�
				if(l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL)	
				{
					//鍚庣画鍦ㄦ娣诲姞鍒ゆ柇姝よ瘝鏄惁涓哄仠鐢ㄨ瘝锛岃嫢涓嶆槸鍒欏啓鍏ョ洰鏍囨枃浠朵腑
					row+=' ' + l.getLexemeText();
				}
			}
			OutputDocs[i]=row;
			i++;
			row="";
//			System.out.println("1");
		}
		return OutputDocs;
	}
	public static void second() throws IOException{
		//寰楀埌鍒嗚瘝鍚庣殑缁撴灉
		int k=1;
		for(int i=1;i<10000;i++){
			File f =new File("G:/新闻/文件"+i+".txt");
			if(!f.exists()){
				System.out.println("444");
				continue;
			}
			String [] ss =ToWords.readInput(f.getPath());
			
			if(ss[0].length()<5){
				System.out.println("444");
				continue;
			}
				
			ToWords.writeOutput(ToWords.preProcessMain(ss), "G:/新闻分词/"+k+".txt");
			k++;
		}
	}
	public static void third(){
		//鐢ㄦ潵璁粌鏈�鍚庣殑WORD2VEC鐨勮鏂�
		int k=1;
		for(int i=1;i<10000;i++){
			File f =new File("G:/新闻分词/"+i+".txt");
			if(!f.exists()){
				continue;
			}
		 Learn learn = new Learn();
		    long start = System.currentTimeMillis();
		    try {
				learn.learnFile(f);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				continue;
			}
		    System.out.println("use time " + (System.currentTimeMillis() - start));
		    learn.saveModel(new File("G:/新闻分词训练/"+k+".txt"));
		    k++;
		}

	}
	public static void main(String args[]) throws IOException{
//		ToWords.first();//绗竴娆″鐞嗗幓鏍囩
//		ToWords.second();//绗簩娆″鐞嗗垎璇�
		ToWords.third();//绗笁娆ord2vec杩涜璁粌
	}
}
