package routing;

import java.util.ArrayList;
import java.util.List;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;

public class NHopsRouter extends ActiveRouter{
    public static final String NROF_COPIES = "nrofHops";
    public static final String SPRAYANDWAIT_NS = "NHopsRouter";
	/** Message property key */
	public static final String MSG_COUNT_PROPERTY = SPRAYANDWAIT_NS + "." +
		"copies";
    protected int initialNrofCopies;
    public NHopsRouter(Settings s) {
		super(s);
		Settings nhopSettings = new Settings(SPRAYANDWAIT_NS);

		initialNrofCopies = nhopSettings.getInt(NROF_COPIES);
        //use NHopRouter.nrofHops as setting in settings.txt
		
	}
    
}
