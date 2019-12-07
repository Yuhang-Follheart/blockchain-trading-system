import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IdMesseageBox extends JPanel implements ShowComponent{
	private static final long serialVersionUID = 1L;
	private FGui jf;
	private String name = "名称: ";
	private String balance = "余额: ";
	private String debet = "欠款总额: ";
	private String re = "应收账款: ";
	private JLabel nameLabel;
	private JLabel balanceLabel;
	private JLabel debetLabel;
	private JLabel reLabel;
	private SetupTransBox setTrans;
	public void refresh() {
		ArrayList<String> idm = jf.getIdMesseage();
		nameLabel.setText(name + idm.get(0));
		balanceLabel.setText(balance + idm.get(1));
		debetLabel.setText(debet + idm.get(2));
		reLabel.setText(re + idm.get(3));
		setTrans.refresh();
	}
	
	public void paintComponent (Graphics g)
	{
    	super.paintComponent(g);
	    g.drawImage(jf.icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
	
	public IdMesseageBox(FGui f){
		this.jf = f;
		nameLabel = new JLabel("名称: null");
		nameLabel.setFont(new Font(null, Font.PLAIN, 20));
		nameLabel.setBounds(50, 50, 250, 30);
		
		balanceLabel = new JLabel("余额: 21354465487");
		balanceLabel.setFont(new Font(null, Font.PLAIN, 20));
		balanceLabel.setBounds(50, 90, 250, 30);
		
		debetLabel = new JLabel("欠款总额: 13454689");
		debetLabel.setFont(new Font(null, Font.PLAIN, 20));
		debetLabel.setBounds(50, 130, 250, 30);
		
		reLabel = new JLabel("应收账款: 123456789");
		reLabel.setFont(new Font(null, Font.PLAIN, 20));
		reLabel.setBounds(50, 170, 250, 30);
		
		JLabel messeage = new JLabel("企业信息");
		messeage.setFont(new Font(null, Font.PLAIN, 30));
		messeage.setBounds(140, 5, 120, 35);
		
		this.setLayout(null);
		//this.setSize(400, 400);
		this.setPreferredSize(null);
		this.add(messeage);
		this.add(nameLabel);
		this.add(balanceLabel);
		this.add(debetLabel);
		this.add(reLabel);
		
		JButton transBtn = new JButton("发起交易");
		transBtn.setBounds(50, 210, 100, 50);
		transBtn.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		
		setTrans = new SetupTransBox(jf, this);
		JPanel con = new JPanel();
		con.setBounds(350, 30, 300, 260);
		con.setVisible(true);
		//con.setLayout(null);
		con.add(setTrans);
		this.add(con);
        transBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 con.setVisible(!setTrans.isVisible());
            	 setTrans.setVisible(!setTrans.isVisible());
            	 setTrans.refresh();
            }
        });
        this.add(transBtn);
		refresh();
		repaint();
	}
//	public static void main(String args[]) {
//		JFrame jf = new JFrame();
//		jf.setLocationRelativeTo(null);
//		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		jf.setSize(400,400);
//		IdMesseageBox im = new IdMesseageBox();
//		jf.add(im);
//		jf.setVisible(true);
//	}
}
