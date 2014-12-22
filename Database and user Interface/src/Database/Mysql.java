package Database;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import org.apache.mahout.cf.taste.recommender.RecommendedItem;
/**
 * UTF-8
 * Need to create database bigdata in mysql first.
 * User_Id = 9999
 * */
public class Mysql {
	private static JFrame frame;
	static private String driver;
	static private String url;
	static private String user;
	static private String pass;
	static Connection conn;
	
	
	public static void initParem(String paramFile) throws Exception{
		
		Properties props =  new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driver");
		user = props.getProperty("user");
		url = props.getProperty("url");
		pass = props.getProperty("pass");
		System.out.println(driver + " " + user + " " + url + " " +pass);
		
	}
	
	public static void connectMysql()throws Exception{
		Class.forName(driver);
		conn = DriverManager.getConnection(url, user, pass);
	}
	
	public static void createTable(String sql)throws Exception{
		try{
		Statement stmt = conn.createStatement();
	    stmt.executeUpdate(sql);
	    System.out.println("create table :" +sql);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	private static void insert_info_to_rec_info() throws Exception{
		File csv = new File("output1.csv"); 
		BufferedReader br  = new BufferedReader(new FileReader(csv));
		String line="";
		String sql_i = "insert into rec_info(user_id, movie_id, point) values(?,?,?)";
		String sql_q = "select movie_id from movie_info where movie_name=?";
	    PreparedStatement pstmt_i = conn.prepareStatement(sql_i);
	    PreparedStatement pstmt_q = conn.prepareStatement(sql_q);
	    String[] info = new String[3];
	    ResultSet rs = null;
		while ((line = br.readLine()) != null) { 
			//System.out.println(i+line);
			StringTokenizer st = new StringTokenizer(line, ",");
			info[0] = st.nextToken();
			info[1] = st.nextToken();
			info[2] = st.nextToken();
			pstmt_q.setString(1, info[1]);
			rs = pstmt_q.executeQuery();
			System.out.println(sql_q+" "+ info[1]);
			rs.next();
			info[1] = rs.getString(1);
			
			pstmt_i.setString(1, info[0]);
			pstmt_i.setString(2, info[1]);
			pstmt_i.setString(3, info[2]);
			pstmt_i.executeUpdate();
			
		}
		System.out.println("insert rec_info");
		br.close();
		
	}
	
	private static void createTable_rec_info() throws Exception{		
		
		String ct = "create table rec_info " 
					+ "(info_id int auto_increment primary key, "
					+ "user_id int, "
					+ "movie_id int, "
					+ "point int) DEFAULT CHARSET=utf8;";
					
		createTable(ct);
		System.out.println("Success!");
	}
	
	private static void createTable_movie() throws Exception{		
		
		String ct = "create table movie_info " 
					+ "(movie_id int auto_increment primary key, "
					+ "movie_name varchar(255) unique) DEFAULT CHARSET=utf8;";
					
		createTable(ct);
		System.out.println("Success!");
	}
	
	private static void deleteTable(String s) throws SQLException{
		Statement stmt = conn.createStatement();
		String sql = "drop table "+s;
		try{
	    stmt.executeUpdate(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void insert_movie_info() throws Exception{
		File csv = new File("output1.csv"); 
		BufferedReader br = new BufferedReader(new FileReader(csv));
		String line="";

		String sql = "insert into movie_info(movie_name) values(?)";
	    PreparedStatement pstmt = conn.prepareStatement(sql);
	    String movie = new String();
	    int all = 0, fail = 0;
	    //line = br.readLine();
	    int i = 0;
		while ((line = br.readLine()) != null) { 
			i++;
			StringTokenizer st = new StringTokenizer(line, ",");
			st.nextToken();
			movie = st.nextToken();
			pstmt.setString(1, movie);

			try{
				all++;
				pstmt.executeUpdate();
			}catch(Exception e){
				e.printStackTrace();
				fail++;
				System.out.println("i:"+i);
			}
			
		}
		System.out.println("Successfully add "+(all-fail)+".\n"+fail + "Failed"); 
		br.close();
	}

	public static void create_csv() throws Exception{
		File csv = new File("output2.csv"); 
		BufferedWriter br = new BufferedWriter(new FileWriter(csv));
		Statement stmt = conn.createStatement();
		String sql = "select user_id, movie_id, point from rec_info;";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			write_line(br, rs.getString(1), rs.getString(2), rs.getString(3));
		}
		
		br.close();
	}
	public static void write_line(BufferedWriter br, String user_id, String movie_id, String point) throws Exception{
		br.write(user_id +",");
		br.write(movie_id +",");
		br.write(point + "\r\n");
	}
	private static void initialize ()throws Exception {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
//		panel.setBackground(new Color(0, 153, 255));
		frame.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("************************************Please select your favourite movies**************************************");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 28));
		panel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();	
//		panel_1.setLayout(BorderLayout.EAST);
		panel_1.setBounds(10, 170, 980, 10);
		panel_1.setBackground(Color.RED);
		JPanel panel_2 = new JPanel();		
		panel_2.setLayout(null);
		panel_2.setBounds(10, 10, 980, 150);
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 260, 980, 200);
		panel_3.setLayout(null);
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(10, 240, 980, 10);
		panel_4.setBackground(Color.RED);
		frame.getContentPane().add(panel_2);	
		final JCheckBox checkBox[] = new JCheckBox[7];
		checkBox[0] = new JCheckBox("Fury");
		checkBox[0].setBounds(20, 110, 70, 20);
		Icon i = new ImageIcon("Pictures/Fury.jpg");
		JLabel j_Fury = new JLabel(i);
		j_Fury.setBounds(10, 10, 100, 100);
		Icon i1 = new ImageIcon("Pictures/American.jpg");
		checkBox[1] = new JCheckBox("Captain American");		
		JLabel j_USA = new JLabel(i1);
		j_USA.setBounds(140, 10, 100, 100);
		checkBox[1].setBounds(120, 110, 150, 20);
		Icon i3 = new ImageIcon("Pictures/X_Men.jpg");
		checkBox[2] = new JCheckBox("X-Men: Days of Future Past");	
		checkBox[2].setBounds(250, 110, 180, 20);
		JLabel j_X_Men = new JLabel(i3);
		j_X_Men.setBounds(280, 10, 100, 100);
		Icon i4 = new ImageIcon("Pictures/Edge of Tomorrow.jpg");
		JLabel j_eot = new JLabel(i4);
		j_eot.setBounds(440, 10, 100, 100);
		checkBox[3] = new JCheckBox("Edge of Tomorrow");
		checkBox[3].setBounds(440, 110, 150, 20);
		Icon i5 = new ImageIcon("Pictures/Lucy.jpg");
		JLabel j_l = new JLabel(i5);
		j_l.setBounds(590, 10, 100, 100);
		checkBox[4] = new JCheckBox("Lucy");
		checkBox[4].setBounds(600, 110, 65, 20);
		Icon i6 = new ImageIcon("Pictures/Life of Pi.jpg");
		JLabel j_P = new JLabel(i6);
		j_P.setBounds(710, 10, 100, 100);
		checkBox[5] = new JCheckBox("Life of Pi");
		checkBox[5].setBounds(710, 110, 90, 20);
		Icon i7 = new ImageIcon("Pictures/The Artist.jpg");
		JLabel j_T = new JLabel(i7);
		j_T.setBounds(850, 10, 70, 100);
		checkBox[6] = new JCheckBox("The Artist");
		checkBox[6].setBounds(840, 110, 90, 20);
		JLabel label[] = new JLabel[5];
		JLabel title = new JLabel("Recommendation:");
		for (int j = 0; j < label.length; j++)
		{
			label[j] = new JLabel("hello world");
		}
		title.setBounds(20, 5, 200, 20);
		label[0].setBounds(100, 30, 200, 20);
		label[1].setBounds(100, 60, 200, 20);
		label[2].setBounds(100, 90, 200, 20);
		label[3].setBounds(100, 120, 200, 20);
		label[4].setBounds(100, 150, 200, 20);
		panel_3.add(title);
		panel_3.add(label[0]);
		panel_3.add(label[1]);
		panel_3.add(label[2]);
		panel_3.add(label[3]);
		panel_3.add(label[4]);
		for (int j = 0; j < label.length; j++)
		{
			label[j].setVisible(false);
		}
		frame.getContentPane().add(panel_2);
		frame.getContentPane().add(panel_1);
		frame.getContentPane().add(panel_3);
		frame.getContentPane().add(panel_4);
		panel_2.add(j_Fury);
		panel_2.add(checkBox[0]);
		panel_2.add(j_USA);
		panel_2.add(checkBox[1]);
		panel_2.add(j_X_Men);
		panel_2.add(checkBox[2]);
		panel_2.add(j_eot);
		panel_2.add(checkBox[3]);
		panel_2.add(j_l);
		panel_2.add(checkBox[4]);
		panel_2.add(j_P);
		panel_2.add(checkBox[5]);
		panel_2.add(j_T);
		panel_2.add(checkBox[6]);
		
		JPanel panel_5 = new JPanel();
		JButton jb = new JButton("submit");
		String filePath = "output1.csv";
		FileWriter fw = new FileWriter(filePath, true);  
	    BufferedWriter bw = new BufferedWriter(fw);  
		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)  {
				// TODO Auto-generated method stub
				for(int i = 0; i < checkBox.length; i++)
				{
					if (checkBox[i].isSelected())
					{
						String name = checkBox[i].getText();
						String user_id = "9999";
						String point = "5";
						
						
						try {
														
							System.out.println(name);
							write_line(bw, user_id, name, point);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				
						
					}
				}
				try {
					bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try{
				initParem("mysql.init");
				connectMysql();
				deleteTable("rec_info");
				createTable_rec_info();		
				deleteTable("movie_info");
				createTable_movie();
				insert_movie_info();		
				insert_info_to_rec_info();
				create_csv();
				ItemBasedRecommender ir = new ItemBasedRecommender();
				List<RecommendedItem> recommendations = ir.Recommend();
				int j = 0;
				String sql = "select movie_name from movie_info where movie_id=?";
				PreparedStatement pstmt_q = conn.prepareStatement(sql);
				ResultSet rs = null;
				
				
			    for (RecommendedItem recommendation : recommendations)
			    {
			    	
			    	long a = recommendation.getItemID();
			    	
			    	pstmt_q.setString(1, Long.toString(a));
			    	rs = pstmt_q.executeQuery();
			    	rs.next();					
			    	label[j].setText(rs.getString(1));
			    	System.out.println(rs.getString(1));
			    	label[j].setVisible(true);
			    	j++;
			    	System.out.println(recommendation);
			    }
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				System.out.println("Hello world");	
			}
		});
		//bw.close();
		panel_5.add(jb);
		panel_5.setBounds(10, 190, 980, 40);
		frame.getContentPane().add(panel_5);
		frame.setBounds(100, 100, 1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception{
		
		//Initialize	
		initialize();
	}
	
}


