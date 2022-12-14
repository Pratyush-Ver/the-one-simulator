package routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import routing.util.RoutingInfo;

import util.Tuple;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;

public class NRouter extends ActiveRouter {
    // number of nodes contacted
    private Map<DTNHost, Integer> contacts;
    public NRouter(Settings s) {
		super(s);
	}

    protected NRouter(NRouter r) {
		super(r);
        initContacts();
	}

    private void initContacts() {
		this.contacts = new HashMap<DTNHost, Integer>();
	}
    @Override
	public void changedConnection(Connection con) {
		super.changedConnection(con);

		if (con.isUp()) {
			DTNHost otherHost = con.getOtherNode(getHost());
			if (contacts.containsKey(otherHost)) {
				//do nothing
			}
			else {
			contacts.put(otherHost, 1);
			}
		
		}
	}
	@Override
	public void update() {
		super.update();
		if (!canStartTransfer() ||isTransferring()) {
			return; // nothing to transfer or is currently transferring
		}

		// try messages that could be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			return;
		}

		tryOtherMessages();
	}

	private Tuple<Message, Connection> tryOtherMessages() {
		List<Tuple<Message, Connection>> messages =
			new ArrayList<Tuple<Message, Connection>>();

		Collection<Message> msgCollection = getMessageCollection();

		/* for all connected hosts collect all messages that have a higher
		   probability of delivery by the other host */
		for (Connection con : getConnections()) {
			DTNHost other = con.getOtherNode(getHost());
			NRouter othRouter = (NRouter)other.getRouter();

			if (othRouter.isTransferring()) {
				continue; // skip hosts that are transferring
			}

			for (Message m : msgCollection) {
				if (othRouter.hasMessage(m.getId())) {
					continue; // skip messages that the other one has
				}
				if (othRouter.contacts.size() > contacts.size()) {
					// the other node has higher probability of delivery
					messages.add(new Tuple<Message, Connection>(m,con));
				}
			}
		}

		if (messages.size() == 0) {
			return null;
		}
		return tryMessagesForConnected(messages);	// try to send messages
	}



    @Override
	public MessageRouter replicate() {
		NRouter r = new NRouter(this);
		return r;
	}
}

//declare a private array and at con up append all dtnhost to this array
//later check if new dtnhost is already in the array to avoid repeats
//to transfer check if size of this array in present node is less than target host