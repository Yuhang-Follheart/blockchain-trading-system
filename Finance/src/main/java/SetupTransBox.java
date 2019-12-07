import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SetupTransBox extends JInternalFrame{
	private static final long serialVersionUID = 1L;
	private FGui jf;
	private JLabel receiver;
	private JLabel transType;
	private JLabel balance;
	private JComboBox<String> namebox;
	private JComboBox<String> Ttype;
	private JTextField balanceText;
	private String[] T = {"余额交易", "欠条交易", "应收账款交易", "贷款"};
	private JPanel showPanel;
	private JButton goTrans;
	private IdMesseageBox idm;
	public void refresh() {
		Vector<String> names = jf.getNames();
		ComboBoxModel<String> model = new DefaultComboBoxModel<String>(names);
		namebox.setModel(model);
	}
	
	SetupTransBox(FGui f, IdMesseageBox idm){
		super( "发起交易",  // title
                true,       // resizable
                true,       // closable
                true,       // maximizable
                true        // iconifiable
              );
		this.jf = f;
		this.idm = idm;
		showPanel = new JPanel();
		receiver = new JLabel("交易对象:");
		receiver.setFont(new Font(null, Font.PLAIN, 20));
		receiver.setBounds(20, 10, 100, 30);
		showPanel.add(receiver);
		
		namebox = new JComboBox<String>();
		namebox.setBounds(110, 10, 150, 30);
		namebox.setFont(new Font(null, Font.PLAIN, 20));
		showPanel.add(namebox);
		
		transType = new JLabel("交易类型:");
		transType.setFont(new Font(null, Font.PLAIN, 20));
		transType.setBounds(20, 55, 100, 30);
		showPanel.add(transType);
		
		Ttype = new JComboBox<String>(T);
		Ttype.setBounds(110, 55, 150, 30);
		Ttype.setFont(new Font(null, Font.PLAIN, 20));
		showPanel.add(Ttype);
		
		balance = new JLabel("交易金额:");
		balance.setFont(new Font(null, Font.PLAIN, 20));
		balance.setBounds(20, 100, 100, 30);
		showPanel.add(balance);
		
		balanceText = new JTextField(10);
		balanceText.setBounds(110, 100, 150, 30);
		balanceText.setFont(new Font(null, Font.PLAIN, 20));
        showPanel.add(balanceText);
        
        goTrans = new JButton("发起交易");
        goTrans.setBounds(70, 150, 140, 30);
        goTrans.setFont(new Font(null, Font.PLAIN, 20));
        goTrans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int t = Ttype.getSelectedIndex();
            	String target = (String)namebox.getSelectedItem();
            	if(target == null) {
            		JOptionPane.showMessageDialog(jf,"你没有可交易的对象!","无交易对象",JOptionPane.WARNING_MESSAGE
                            );
            		return;
            	}
            	if(!MatchInteger.isInteger(balanceText.getText())) {
            		JOptionPane.showMessageDialog(jf,"请输入正确的交易金额!（非负整数）","交易金额错误",JOptionPane.WARNING_MESSAGE
                            );
            		return;
            	}
            	BigInteger money = new BigInteger(balanceText.getText());
            	if(t == 0) {
            		if(jf.getBalance().compareTo(money) < 0) {
            			JOptionPane.showMessageDialog(jf,"你的余额不足","交易金额错误",JOptionPane.WARNING_MESSAGE
                                );
            			return;
            		}
            		jf.payByBalance(target, money);
            	}else if(t == 1) {
            		jf.payByNewRe(target, money);
            	}
            	else {
            		//System.out.println(jf.getReAmount());
            		if(jf.getReAmount().compareTo(money) < 0) {
            			JOptionPane.showMessageDialog(jf,"你的应收账款余额不足","交易金额错误",JOptionPane.WARNING_MESSAGE
                                );
            			return;
            		}
            		if(t == 2){
	            		jf.payByRe(target, money);
	            	}else {
	            		if(jf.isBank(target)) {
	            			jf.loan(target, money);
	            		}
	            		else {
	            			JOptionPane.showMessageDialog(jf,"你只能向银行贷款!","交易目标错误",JOptionPane.WARNING_MESSAGE
	                                );
	            			return;
	            		}
	            	}
            	}
            	idm.refresh();
            }
        });
        showPanel.add(goTrans);
        showPanel.setLayout(null);
        this.add(showPanel);
        this.setVisible(true);
        this.setPreferredSize(new Dimension(300, 250));
        // 设置窗口的显示位置
        //this.setLocation(150, 0);
        refresh();
	}
}
