package arianzhang.sample.mqtt;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.xenqtt.message.PubMessage;
import net.sf.xenqtt.message.QoS;
import net.sf.xenqtt.message.SubscribeMessage;
import net.sf.xenqtt.mockbroker.Client;
import net.sf.xenqtt.mockbroker.MockBroker;
import net.sf.xenqtt.mockbroker.MockBrokerHandler;

/**
 * Fires up a mock broker that specializes in routing data of the 'Glam' variety. This particular broker has a special handler that rejects any and all attempts
 * to interact with country music.
 */
public class GlamNoCountryBroker {

	public static void main(String... args) throws InterruptedException {
		MockBrokerHandler handler = new GlamBrokerHandler();
		MockBroker broker = new MockBroker(handler);

		broker.init(); // Blocks until startup is complete.

		// At this point the broker is online. Clients can connect to it, publish messages, subscribe, etc.
		Thread.sleep(60000);

		// We are done. Shutdown the broker. Wait forever (> 0 means wait that many milliseconds).
		broker.shutdown(0);
	}

	private static final class GlamBrokerHandler extends MockBrokerHandler {

		@Override
		public boolean publish(Client client, PubMessage message) throws Exception {
			String payload = new String(message.getPayload(), Charset.forName("UTF-8"));
			if (payload.indexOf("Country Music") > -1) {
				// We don't do that stuff here! Return true to suppress processing of the message
				return true;
			}

			return super.publish(client, message);
		}

		/**
		 * @see net.sf.xenqtt.mockbroker.MockBrokerHandler#subscribe(net.sf.xenqtt.mockbroker.Client, net.sf.xenqtt.message.SubscribeMessage)
		 */
		@Override
		public boolean subscribe(Client client, SubscribeMessage message) throws Exception {
			String[] topics = message.getTopics();
			QoS[] qoses = message.getRequestedQoSes();
			List<String> allowedTopics = new ArrayList<String>();
			List<QoS> allowedQoses = new ArrayList<QoS>();
			int index = 0;
			for (String topic : topics) {
				// Only allow topic subscriptions for topics that don't include country music.
				if (!topic.matches("^.*(?i:country).*$")) {
					allowedTopics.add(topic);
					allowedQoses.add(qoses[index]);
				}

				index++;
			}
			message = new SubscribeMessage(message.getMessageId(), allowedTopics.toArray(new String[0]), allowedQoses.toArray(new QoS[0]));
			return super.subscribe(client, message);
		}

	}

}