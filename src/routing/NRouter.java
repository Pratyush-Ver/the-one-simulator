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
    /** the value of nrof seconds in time unit -setting */
	private int secondsInTimeUnit;
    // number of nodes contacted
    private Map<DTNHost, Double> contacts;
    public NRouter(Settings s) {
		super(s);
	}

    protected NRouter(NRouter r) {
		super(r);
        initContacts();
	}

    private void initContacts() {
		this.contacts = new HashMap<DTNHost, Double>();
	}
    @Override
	public void changedConnection(Connection con) {
		super.changedConnection(con);

		if (con.isUp()) {
			DTNHost otherHost = con.getOtherNode(getHost());
			updateDeliveryPredFor(otherHost);
			updateTransitivePreds(otherHost);
		}
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