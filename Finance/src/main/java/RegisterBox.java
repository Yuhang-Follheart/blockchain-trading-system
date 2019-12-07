import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class RegisterBox extends JPanel{
	private FGui jf;
	public RegisterBox(FGui j){
    	this.jf = j;
    	init();
    }
	public void paintComponent (Graphics g)
	{
    	super.paintComponent(g);
	    g.drawImage(jf.icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
	private void init() {
		this.setLayout(null);
		
		JLabel tip = new JLabel("企业注册");
        tip.setFont(new Font(null, Font.PLAIN, 30));  // 设置字体，null 表示使用默认字体
        tip.setBounds(142,5,120,35);
        this.add(tip);
        
        JLabel nameLabel = new JLabel("名称：");
        nameLabel.setFont(new Font(null, Font.PLAIN, 20));  // 设置字体，null 表示使用默认字体
        nameLabel.setBounds(70,50,80,25);
        this.add(nameLabel);
        
        JTextField nameText = new JTextField(15);
        nameText.setBounds(140,50,165,25);
        nameText.setFont(new Font(null, Font.PLAIN, 20));
        this.add(nameText);
        
        JLabel balanceLabel = new JLabel("资产：");
        balanceLabel.setFont(new Font(null, Font.PLAIN, 20));
        balanceLabel.setBounds(70,95,80,25);
        this.add(balanceLabel);
        
        JTextField balanceText = new JTextField(15);
        balanceText.setBounds(140,95,165,25);
        balanceText.setFont(new Font(null, Font.PLAIN, 20));
        this.add(balanceText);

        JLabel typeLabel = new JLabel("类型：");
        typeLabel.setFont(new Font(null, Font.PLAIN, 20));  // 设置字体，null 表示使用默认字体
        typeLabel.setBounds(70,140,60,25);
        this.add(typeLabel);
        // 创建两个单选按钮
        JRadioButton type1 = new JRadioButton("普通企业");
        type1.setFont(new Font(null, Font.CENTER_BASELINE, 15));
        type1.setBounds(140, 140, 90, 20);
        
        JRadioButton type2 = new JRadioButton("银行");
        type2.setFont(new Font(null, Font.CENTER_BASELINE, 15));
        type2.setBounds(230, 140, 60, 20);

        // 创建按钮组，把两个单选按钮添加到该组
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(type1);
        btnGroup.add(type2);
        
        
        // 设置第一个单选按钮选中
        type1.setSelected(true);

        this.add(type1);
        this.add(type2);

        // 创建登录按钮
        JButton registerBtn = new JButton("注册");
        registerBtn.setFont(new Font(null, Font.CENTER_BASELINE, 15));
        registerBtn.setBounds(162, 190, 80, 25);
        // 添加按钮的点击事件监听器
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取到的事件源就是按钮本身
                // JButton btn = (JButton) e.getSource();
            	if(MatchInteger.isInteger(balanceText.getText())) {
            		Boolean isbank = new Boolean(type2.isSelected());
            		BigInteger balance = new BigInteger(balanceText.getText());
            		String name = nameText.getText();
            		jf.register(name, isbank, balance);
            	}
            	else {
            		JOptionPane.showMessageDialog(jf,"请输入正确的注册金额!（非负整数）","注册金额错误",JOptionPane.WARNING_MESSAGE
                    );
            	}
            }
        });
        this.add(registerBtn);
        repaint();
    }
//	public static void main(String args[]) {
//		JFrame jf = new JFrame("测试窗口");
//        jf.setSize(300, 300);
//        jf.setLocationRelativeTo(null);
//        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		RegisterBox r = new RegisterBox(jf);
//		r.init();
//		jf.add(r);
//		jf.setVisible(true);
//	}
}
