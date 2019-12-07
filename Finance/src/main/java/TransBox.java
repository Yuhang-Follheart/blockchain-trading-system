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

public class TransBox extends JPanel implements ShowComponent{
	private static final long serialVersionUID = 1L;
	private FGui jf;
	private JComboBox<BigInteger> cbox;
	private JTable table;
	private JScrollPane scrollPane = new JScrollPane();
	private Object[] thead = {"交易发起人", "交易接受者", "现金交易", "金额", "交易代号"};
	private Object[][] tabledata;
	public void refresh() {
		Vector<BigInteger> transId = jf.getTransIndex();
		ComboBoxModel<BigInteger> model = new DefaultComboBoxModel<BigInteger>(transId);
		cbox.setModel(model);
		tabledata = new Object[transId.size()][5];
		for(int i = 0; i < transId.size(); i++) {
			tabledata[i] = jf.getTran(transId.get(i));
		}
		table = new JTable(tabledata, thead);
		table.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		table.setRowHeight(20);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();//单元格渲染器
		tcr.setHorizontalAlignment(JLabel.CENTER);//居中显示
		table.setDefaultRenderer(Object.class, tcr);
		table.setPreferredScrollableViewportSize(new Dimension(600, 300));
		// 创建滚动面板，把 表格 放到 滚动面板 中（表头将自动添加到滚动面板顶部）
		this.remove(scrollPane);
		scrollPane = new JScrollPane(table);
		this.add(scrollPane);
		repaint();
	}
	public void paintComponent (Graphics g)
	{
    	super.paintComponent(g);
	    g.drawImage(jf.icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
	TransBox(FGui f){
		this.jf = f;
		this.setPreferredSize(new Dimension(700, 400));
		JLabel tip = new JLabel();
        tip.setText("选择要处理的交易编号:");
        tip.setFont(new Font(null, Font.PLAIN, 20));  // 设置字体，null 表示使用默认字体
		this.add(tip);
		cbox = new JComboBox<BigInteger>();
		cbox.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		this.add(cbox);
		JButton accBtn = new JButton("接受交易");
		accBtn.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		accBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int index = cbox.getSelectedIndex();
            	if((tabledata[index][1]).equals(jf.name)) {
            		jf.accTran(cbox.getSelectedItem());
            	}
            	else {
            		JOptionPane.showMessageDialog(jf,"你不是这场交易的接受者!","接受交易错误",JOptionPane.WARNING_MESSAGE
                            );
            	}
            	refresh();
            }
        });
		JButton cancelBtn = new JButton("取消/拒绝交易");
		cancelBtn.setFont(new Font(null, Font.CENTER_BASELINE, 15));
		cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int index = cbox.getSelectedIndex();
            	if((tabledata[index][0]).equals(jf.name)) {
            		jf.cancelTran(cbox.getSelectedItem());
            	}
            	else {
            		jf.rejectTran(cbox.getSelectedItem());
            	}
            	refresh();
            }
        });
		this.add(accBtn);
		this.add(cancelBtn);
		refresh();
	}
//	public static void main(String args[]) {
//		TransBox t = new TransBox();
//		JFrame jf = new JFrame();
//		jf.setLocationRelativeTo(null);
//		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		//jf.setSize(400,400);
//		jf.add(t);
//		jf.pack();
//		jf.setVisible(true);
//	}
}
