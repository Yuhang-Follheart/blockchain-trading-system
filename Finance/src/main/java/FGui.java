import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple10;
import org.fisco.bcos.web3j.tuples.generated.Tuple5;
import org.fisco.bcos.web3j.tuples.generated.Tuple9;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FGui extends JFrame{
	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
	private Service service;
	private Web3j web3j;
	private Credentials credentials;
	private static BigInteger gasPrice = new BigInteger("30000000");
	private static BigInteger gasLimit = new BigInteger("30000000");
	private String contractAddr;
	public String name = "";
	private TabbedBox tbox;
	public ImageIcon icon= new ImageIcon("src/main/resources/background.jpg");
	FGui(){
		super("区块链金融系统");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void init() {
		connect();
		showLogin();
		//this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
	}
	private void showLogin() {
		this.setSize(400, 400);
		this.setContentPane(new LoginBox(this));
	}
	
	private boolean isRegisted() {
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Boolean result = finance.isRegisted().send();
			if (result) {
				System.out.println("The account has registed!");
			} else {
				System.out.println("The account has not registed!");
			}
			return result;
		} catch (Exception e) {
			e.getMessage();
		}
		return false;
	}
	
	private void loadFinance() {
		try {
			contractAddr = loadFinanceAddr();
			System.out.println("Load the contract successfully, the address is " + contractAddr);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showRegisterBox() {
		this.setSize(400, 300);
		RegisterBox rb = new RegisterBox(this);
		this.setContentPane(rb);
		this.setVisible(true);
	}
	
	private void showMainBox() {
		tbox = new TabbedBox(this);
		this.setContentPane(tbox);
		this.pack();
	}
	
	private void initMesseage() {
		loadFinance();
		if(isRegisted()) {
			showMainBox();
			//System.out.println();
		}else {
			showRegisterBox();
		}
		
	}
	public String deployFinance() {
		try {
			Finance finance = Finance.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();
			System.out.println(" deploy Asset success, contract address is " + finance.getContractAddress());
			recordFinanceAddr(finance.getContractAddress());
			return finance.getContractAddress();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return "";
	}
	
	public String loadFinanceAddr() throws Exception {
		// load Asset contact address from contract.properties
		Properties prop = new Properties();
		final Resource contractResource = new ClassPathResource("contract.properties");
		prop.load(contractResource.getInputStream());

		String contractAddress = prop.getProperty("address");
		if (contractAddress == null || contractAddress.trim().equals("")) {
			contractAddress = deployFinance();
		}
		try {
			Finance finance = Finance.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
		}
		catch(Exception e) {
			return deployFinance();
		}
		return contractAddress;
	}
	
	public void recordFinanceAddr(String address) throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.setProperty("address", address);
		final Resource contractResource = new ClassPathResource("contract.properties");
		FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
		prop.store(fileOutputStream, "contract address");
	}
	
	
	public static void main(String args[]) {
		FGui f = new FGui();
		f.init();
	}
	
	public void connect() {
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		    service = context.getBean(Service.class);
		    service.run(); 
		    ChannelEthereumService channelEthereumService = new ChannelEthereumService();
		    channelEthereumService.setChannelService(service);
		    //获取Web3j对象
		    web3j = Web3j.build(channelEthereumService, service.getGroupId());
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
		System.out.println("Connect to the fisco-bcos chain!");
	}
	
	public void login(String keyPath){
		System.out.println(keyPath);
		PEMLoader pem = new PEMLoader();
		pem.setPemFile(keyPath);
		try {
			pem.load();
			ECKeyPair pemKeyPair = pem.getECKeyPair();
			//生成web3sdk使用的Credentials
			credentials = GenCredential.create(pemKeyPair.getPrivateKey().toString(16));
			System.out.println("PEM Address: " + credentials.getAddress());
			initMesseage();
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"请选择正确的私钥文件!","登录失败",JOptionPane.WARNING_MESSAGE
                    );
		}
	}

	public ArrayList<String> getIdMesseage() {
		try {
			ArrayList<String> res = new ArrayList<String>();
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Tuple10<Boolean, String, String, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = finance.enterpriseList(credentials.getAddress()).send();
			res.add(result.getValue3());
			res.add(result.getValue5().toString());
			res.add(result.getValue9().toString());
			res.add(result.getValue7().toString());
			name = new String(result.getValue3());
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public Vector<BigInteger> getTransIndex() {
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Vector<BigInteger> res  = new Vector<BigInteger>((List<BigInteger>)finance.getTransIndex().send());
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public void register(String name, Boolean isBank, BigInteger balance) {
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			TransactionReceipt receipt = finance.register(name, isBank, balance).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"注册失败!用户名可能已被使用","注册失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"注册成功!","注册成功",JOptionPane.INFORMATION_MESSAGE
                        );
				showMainBox();
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	public Object[] getTran(BigInteger id) {
		try {
			Object[] item = new Object[5];
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Tuple9<Boolean, BigInteger, Boolean, Boolean, String, String, String, String, BigInteger> result = finance.transactionsList(id).send();
			item[0] = result.getValue6();
			item[1] = result.getValue8();
			if(result.getValue3().booleanValue()) {
				item[2] = new String("是");
			}
			else {
				item[2] = new String("否");
			}
			item[3] = result.getValue9();
			item[4] = id;
			return item;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public void accTran(Object id) {
		if(confirm() > 0) {
			return;
		}
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			TransactionReceipt receipt = finance.acceptTrans((BigInteger)id).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"该交易已经关闭!","接受交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"接受交易成功!","接受交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		
	}

	public void cancelTran(Object id) {
		if(confirm() > 0) {
			return;
		}
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			TransactionReceipt receipt = finance.cancelTrans((BigInteger)id).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"该交易已经关闭!","取消交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"取消交易成功!","取消交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		
	}

	public void rejectTran(Object id) {
		if(confirm() > 0) {
			return;
		}
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			TransactionReceipt receipt = finance.refuseTrans((BigInteger)id).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"该交易已经关闭!","拒绝交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"拒绝交易成功!","拒绝交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		
	}

	public Vector<String> getNames() {
		// TODO Auto-generated method stub
		BigInteger enterpriseNum;
		Vector<String> res = new Vector<String>();
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			enterpriseNum = finance.enterpriseNum().send();
			for(int i = 0; i < enterpriseNum.intValue(); i++) {
				String n = finance.names(BigInteger.valueOf(i)).send();
				if(!n.equals(name))
					res.add(n);
			}
			return res;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public BigInteger getBalance() {
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			BigInteger b = finance.getBalance().send();
			return b;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public BigInteger getReAmount() {
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			BigInteger b = finance.getReAmount().send();
			return b;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public void payByBalance(String target, BigInteger money) {
		if(confirm() > 0) {
			return;
		}
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			String addr = finance.name2addr(target).send();
			TransactionReceipt receipt = finance.payByBalance(addr, money).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"发起交易失败!","发起交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"发起交易成功!","发起交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
	}

	public void payByRe(String target, BigInteger money) {
		if(confirm() > 0) {
			return;
		}
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			String addr = finance.name2addr(target).send();
			TransactionReceipt receipt = finance.payByRe(addr, money).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"发起交易失败!","发起交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"发起交易成功!","发起交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
	}

	public boolean isBank(String target) {
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			String addr = finance.name2addr(target).send();
			Tuple10<Boolean, String, String, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = finance.enterpriseList(addr).send();
			return result.getValue4();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return false;
	}

	public void loan(String target, BigInteger money) {
		if(confirm() > 0) {
			return;
		}
	    try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			String addr = finance.name2addr(target).send();
			TransactionReceipt receipt = finance.loan(addr, money).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"发起贷款交易失败!","发起交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"发起贷款交易成功!","发起交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}

	}

	public void payByNewRe(String target, BigInteger money) {
		if(confirm() > 0) {
			return;
		}
	    try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			String addr = finance.name2addr(target).send();
			TransactionReceipt receipt = finance.payByNewRe(addr, money).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"签发欠条交易失败!","发起交易失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"签发欠条交易成功!","发起交易成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
	}

	public Vector<BigInteger> getDebtsIndex() {
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Vector<BigInteger> res  = new Vector<BigInteger>((List<BigInteger>)finance.getDebtsIndex().send());
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public Object[] getDebt(BigInteger id) {
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Tuple5<Boolean, BigInteger, String, String, BigInteger> result  = finance.debtsList(id).send();
			Object[] res = new Object[3];
			res[0] = finance.getName(result.getValue4()).send();
			res[1] = BigInteger.valueOf(result.getValue5().longValue());
			res[2] = id;
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public void payDebt(BigInteger index) {
		// TODO Auto-generated method stub
		if(confirm() > 0) {
			return;
		}
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			TransactionReceipt receipt = finance.payForDebt(index).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"支付债务失败!该债务可能已经关闭","支付债务失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"支付债务成功!","支付债务成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		
	}

	public Vector<BigInteger> getReIndex() {
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Vector<BigInteger> res  = new Vector<BigInteger>((List<BigInteger>)finance.getReIndex().send());
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public Object[] getRe(BigInteger id) {
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			Tuple5<Boolean, BigInteger, String, String, BigInteger> result  = finance.debtsList(id).send();
			Object[] res = new Object[3];
			res[0] = finance.getName(result.getValue3()).send();
			res[1] = BigInteger.valueOf(result.getValue5().longValue());
			res[2] = id;
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}
		return null;
	}

	public void askPayForDebt(BigInteger id) {
		// TODO Auto-generated method stub
		try {
			Finance finance = Finance.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
			TransactionReceipt receipt = finance.askPayForDebt(id).send();
			System.out.println(receipt.getStatus());
			if(!receipt.getStatus().equals("0x0")) {
				JOptionPane.showMessageDialog(this,"讨债失败!\n可能未到支付期限或者债务已结清!","讨债失败",JOptionPane.WARNING_MESSAGE
                        );
			}
			else {
				JOptionPane.showMessageDialog(this,"讨债成功!","讨债成功",JOptionPane.INFORMATION_MESSAGE
                        );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
		}

	}
	
	public int confirm() {
		int result = JOptionPane.showConfirmDialog(
                this,
                "确认执行这个操作吗？",
                "提示",
                JOptionPane.YES_NO_OPTION
        );
		System.out.println("choose the " + result);
		return result;
	}
	
}
