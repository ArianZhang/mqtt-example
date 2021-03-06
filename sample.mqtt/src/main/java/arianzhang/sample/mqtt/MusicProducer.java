package arianzhang.sample.mqtt;
import net.sf.xenqtt.client.MqttClient;
import net.sf.xenqtt.client.MqttClientListener;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.client.SyncMqttClient;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import org.apache.log4j.Logger;

/**
 * Produces hit music from days gone by.
 */
public class MusicProducer {

	private static final Logger log = Logger.getLogger(MusicProducer.class);

	public static void main(String... args) throws Throwable {
		MqttClientListener listener = new MqttClientListener() {
			@Override
			public void publishReceived(MqttClient client, PublishMessage message) {
				log.warn("Received a message when no subscriptions were active. Check your broker ;)");
			}
			@Override
			public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {
				if (cause != null) {
					log.error("Disconnected from the broker due to an exception.", cause);
				} else {
					log.info("Disconnected from the broker.");
				}

				if (reconnecting) {
					log.info("Attempting to reconnect to the broker.");
				}
			}
		};

		// Build your client. This client is a synchronous one so all interaction with the broker will block until said interaction completes.
		MqttClient client = new SyncMqttClient("tcp://mqtt-broker:1883", listener, 5);
		try {
			ConnectReturnCode returnCode = client.connect("musicProducer", false, "music-user", "music-pass");
			if (returnCode != ConnectReturnCode.ACCEPTED) {
				log.error("Unable to connect to the broker. Reason: " + returnCode);
				return;
			}

			// Publish a musical catalog
			client.publish(new PublishMessage("grand/funk/railroad", QoS.AT_MOST_ONCE, "On Time"));
			client.publish(new PublishMessage("grand/funk/railroad", QoS.AT_MOST_ONCE, "E Pluribus Funk"));
			client.publish(new PublishMessage("jefferson/airplane", QoS.AT_MOST_ONCE, "Surrealistic Pillow"));
			client.publish(new PublishMessage("jefferson/airplane", QoS.AT_MOST_ONCE, "Crown of Creation"));
			client.publish(new PublishMessage("seventies/prog/rush", QoS.AT_MOST_ONCE, "2112"));
			client.publish(new PublishMessage("seventies/prog/rush", QoS.AT_MOST_ONCE, "A Farewell to Kings"));
			client.publish(new PublishMessage("seventies/prog/rush", QoS.AT_MOST_ONCE, "Hemispheres"));
		} catch (Exception ex) {
			log.error("An exception prevented the publishing of the full catalog.", ex);
		} finally {
			// We are done. Disconnect.
			if (!client.isClosed()) {
				client.disconnect();
			}
		}
	}

}