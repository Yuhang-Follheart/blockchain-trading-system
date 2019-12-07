import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LoginBox extends JPanel{
	private static final long serialVersionUID = 1L;
	private FGui fgui;
	private static String path = "";
	public void paintComponent (Graphics g)
	{
    	super.paintComponent(g);
	    g.drawImage(fgui.icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
	public LoginBox(FGui f){
		this.fgui = f;
		this.setLayout(null);
		
		JLabel tip = new JLabel("区块链账户登录");
        tip.setFont(new Font(null, Font.PLAIN, 30));  // 设置字体，null 表示使用默认字体
        tip.setBounds(100, 25 , 210, 35);
        this.add(tip);
        
        JTextArea fPath = new JTextArea();
        fPath.setEditable(false);
        fPath.setColumns(20);
        fPath.setBounds(170, 95, 160, 20);
        fPath.setFont(new Font(null, Font.PLAIN, 15));
        this.add(fPath);
        
        JButton selectBtn = new JButton("选择私钥:");
        selectBtn.setFont(new Font(null, Font.PLAIN, 15));
        selectBtn.setBounds(40, 90, 120, 35);
        this.add(selectBtn);
        selectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile(fgui, fPath);
            }
        });
        
        JButton btn = new JButton("登录");
        btn.setBounds(160, 170, 80, 40);
        btn.setFont(new Font(null, Font.PLAIN, 15));
        this.add(btn);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(path.equals("")) {
            		JOptionPane.showMessageDialog(fgui,"请选择正确的私钥文件!","登录失败",JOptionPane.WARNING_MESSAGE
                            );
              		return;
            	}
                fgui.login(path);
                path = "";
            }
        });
        repaint();
	}
	
	private static void selectFile(Component parent, JTextArea msgTextArea) {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();

        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));

        // 设置文件选择的模)式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 设置是否允许多选
        fileChooser.setMultiSelectionEnabled(false);

        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        //fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("key(*.pem)", "zip", "rar"));
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("key(*.pem)", "pem"));

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            File file = fileChooser.getSelectedFile();
            msgTextArea.setText(file.getAbsolutePath());
            path = file.getAbsolutePath();
            if(path.length() <= 10) {
            	msgTextArea.setText(path);
            }
            else {
            	msgTextArea.setText("..." + path.substring(path.length() - 17, path.length()));
            }
           
        }
    }
}
