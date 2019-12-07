import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class TestGui extends JPanel{
	private static final long serialVersionUID = 1L;
	//private FGui fgui;
	public TestGui(){
		//this.fgui = f;
		this.setLayout(null);
		
		JLabel tip = new JLabel("区块链账户登录");
        tip.setFont(new Font(null, Font.PLAIN, 30));  // 设置字体，null 表示使用默认字体
        tip.setBounds(100, 25 , 210, 35);
        this.add(tip);
		
		JLabel greet = new JLabel();
        greet.setText("选择私钥:");
        greet.setFont(new Font(null, Font.PLAIN, 20));  // 设置字体，null 表示使用默认字体
        
        this.add(greet);
        
        JTextArea fPath = new JTextArea();
        fPath.setEditable(false);
        fPath.setColumns(10);
        fPath.setBounds(160, 95, 160, 20);
        fPath.setFont(new Font(null, Font.PLAIN, 15));
        this.add(fPath);
       
        JButton selectBtn = new JButton("选择私钥:");
        selectBtn.setFont(new Font(null, Font.PLAIN, 15));
        selectBtn.setBounds(30, 90, 120, 35);
        this.add(selectBtn);

       
        JButton btn = new JButton("登录");
        btn.setBounds(140, 170, 80, 40);
        btn.setFont(new Font(null, Font.PLAIN, 15));
        this.add(btn);
        
        repaint();
	}
	
	public static void main(String args[]) {
		JFrame jf = new JFrame("测试窗口");
		jf.setSize(400, 400);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		TestGui t = new TestGui();
		jf.setContentPane(t);
		t.setBorder(BorderFactory.createLineBorder(Color.red, 3));
		jf.setVisible(true);
		System.out.println(t.getWidth());
	}
	
}
