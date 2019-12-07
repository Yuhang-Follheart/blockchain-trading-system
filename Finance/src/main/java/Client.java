import java.math.BigInteger;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fisco.bcos.web3j.crypto.Credentials;

public class Client {
	private ApplicationContext context;
	private Service service;
	static Logger logger = LoggerFactory.getLogger(Client.class);
	private Web3j web3j;
	private Credentials credentials;
	public void init() {
		try {
		//读取配置文件，SDK与区块链节点建立连接
	    context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
	    service = context.getBean(Service.class);
	    service.run(); 
	    ChannelEthereumService channelEthereumService = new ChannelEthereumService();
	    channelEthereumService.setChannelService(service);

		

		logger.debug(" web3j is " + web3j + " ,credentials is " + credentials);
	    //获取Web3j对象
	    web3j = Web3j.build(channelEthereumService, service.getGroupId());
	    //通过Web3j对象调用API接口getBlockNumber
	    BigInteger blockNumber = web3j.getBlockNumber().send().getBlockNumber();
	    System.out.println(blockNumber);}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		Client c = new Client();
		c.init();
	}
}
