import java.awt.Color;
import java.awt.Font;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabbedBox extends JTabbedPane{
	private static final long serialVersionUID = 1L;
	private FGui jf;
	TabbedBox(FGui f){
		this.jf = f;
		this.setFont(new Font(null, Font.ROMAN_BASELINE, 20));
		this.setBackground(Color.white);
		this.addTab("企业信息", new IdMesseageBox(jf));
		this.addTab("待处理交易", new TransBox(jf));
		this.addTab("债务信息", new DebtBox(jf));
		this.addTab("应收账款信息", new ReBox(jf));
		this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	((ShowComponent)((TabbedBox)e.getSource()).getSelectedComponent()).refresh();
            }
        });
	}
}