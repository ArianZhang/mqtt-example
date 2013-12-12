package arianzhang.sample.mqtt;

import net.sf.xenqtt.mockbroker.MockBroker;
import net.sf.xenqtt.mockbroker.MockBrokerHandler;

/**
 * Fires up a mock broker that specializes in routing data of the 'Glam' variety.
 */
public class GlamBroker {

	public static void main(String... args) throws InterruptedException {
		MockBrokerHandler handler = new MockBrokerHandler();
		MockBroker broker = new MockBroker(handler);

		broker.init(); // Blocks until startup is complete.

		// At this point the broker is online. Clients can connect to it, publish messages, subscribe, etc.
		Thread.sleep(60000);

		// We are done. Shutdown the broker. Wait forever (> 0 means wait that many milliseconds).
		broker.shutdown(0);
	}

}
