package fruit.banana.crypto;

import static org.junit.Assert.*;

import org.junit.Test;

public class SecureMessageTest {

	@Test
	public void test() {
		SecureMessage sm1=new SecureMessage();
		sm1.createKey();
	}

}
