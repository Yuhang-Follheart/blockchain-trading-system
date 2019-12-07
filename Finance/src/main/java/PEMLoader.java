import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.fisco.bcos.channel.client.PEMManager;

public class PEMLoader extends PEMManager{
	public void load() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, NoSuchProviderException, IOException{
		File f= new File(getPemFile());
		InputStream input = new FileInputStream(f);
        load(input);
    }
}
