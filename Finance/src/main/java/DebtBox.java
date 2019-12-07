import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DebtBox extends JPanel implements ShowComponent{
	private static final long serialVersionUID = 1L;
	private FGui jf;
	private JComboBox<BigInteger> cbox;
	private JTable table;
	private JScrollPane scrollPane = new JScrollPane();
	private Object[] thead = {"债主", "欠款金额", "债务编号"};
	private Object[][] tabledata;
	public void paintComponent (Graphics g)
	{
    	super.paintComponent(g);
	    g.drawImage(jf.icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
	public void refresh() {
		Vector<BigInteger> debtsIndex = jf.getDebtsIndex();
		ComboBoxModel<BigInteger> model = new DefaultComboBoxModel<BigInteger>(debtsIndex);
		cbox.setModel(model);
		tabledata = new Object[debtsIndex.size()][3];
		for(int i = 0; i < debtsIndex.size(); i++) {
			tabledata[i] = jf.getDebt(debtsIndex.get(i));
		}
		table = new JTable(tabledata, thead);
		table.setPreferredScrollableViewportSize(new Dimension(600, 300));
		table.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		table.setRowHeight(20);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();//单元格渲染器
		tcr.setHorizontalAlignment(JLabel.CENTER);//居中显示
		table.setDefaultRenderer(Object.class, tcr);
		// 创建滚动面板，把 表格 放到 滚动面板 中（表头将自动添加到滚动面板顶部）
		this.remove(scrollPane);
		scrollPane = new JScrollPane(table);
		this.add(scrollPane);
		repaint();
	}
	public DebtBox(FGui f){
		this.jf = f;
		this.setPreferredSize(new Dimension(700, 400));
		JLabel tip = new JLabel();
        tip.setText("选择要处理的债务编号:");
        tip.setFont(new Font(null, Font.PLAIN, 20));  // 设置字体，null 表示使用默认字体
		this.add(tip);
		
		cbox = new JComboBox<BigInteger>();
		cbox.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		this.add(cbox);
		
		JButton payBtn = new JButton("偿还债务");
		payBtn.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		payBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	BigInteger id = (BigInteger)cbox.getSelectedItem();
            	if(id == null) {
            		JOptionPane.showMessageDialog(jf,"你没有需要偿还的债务!","无债一身轻",JOptionPane.WARNING_MESSAGE
                            );
            		return;
            	}
            	int index = cbox.getSelectedIndex();
            	BigInteger balance = (BigInteger)tabledata[index][1];
            	if(jf.getBalance().compareTo(balance) < 0) {
            		JOptionPane.showMessageDialog(jf,"你的余额不足!","支付债款失败",JOptionPane.WARNING_MESSAGE
                            );
            		return;
            	}
            	jf.payDebt(id);
            	refresh();
            }
        });
		this.add(payBtn);
		refresh();
	}
}
