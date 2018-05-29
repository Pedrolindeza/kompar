package org.kompar.mediator.ws;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/** End point manager */
public class MediatorEndpointManager {

	/** UDDI naming server location */
	private String uddiURL = null;
	/** Web Service name */
	private String wsName = null;
	
	private Timer timer;

	/** Get Web Service UDDI publication name */
	public String getWsName() {
		return wsName;
	}

	/** Web Service location to publish */
	private String wsURL = null;

	/** Port implementation */
	private MediatorPortImpl portImpl = new MediatorPortImpl(this);

	/** Obtain Port implementation */
	public MediatorPortType getPort() {
		return portImpl;
	}
	
	public MediatorPortImpl getPortImpl(){
		return portImpl;
	}

	/** Web Service endpoint */
	private Endpoint endpoint = null;
	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming = null;

	/** Get UDDI Naming instance for contacting UDDI server */
	UDDINaming getUddiNaming() {
		return uddiNaming;
	}

	/** output option **/
	private boolean verbose = true;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided UDDI location, WS name, and WS URL */
	public MediatorEndpointManager(String uddiURL, String wsName, String wsURL) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		this.wsURL = wsURL;
	}

	/** constructor with provided web service URL */
	public MediatorEndpointManager(String wsURL) {
		if (wsURL == null)
			throw new NullPointerException("Web Service URL cannot be null!");
		this.wsURL = wsURL;
	}

	/* end point management */

	public void start() throws Exception {
		try {
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop end point
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
		unpublishFromUDDI();
	}

	/* UDDI */

	void publishToUDDI() throws Exception {
		
		
		try {
			// publish to UDDI
			
			if (uddiURL != null) {
				if (verbose) {
					
				}
				uddiNaming = new UDDINaming(uddiURL);
				if(uddiNaming.lookup("A54_Mediator") == null) {
					System.out.printf("Publishing '%s' to UDDI at %s%n", wsName, uddiURL);
					System.out.println("Mediator Primario");
					portImpl.setIsPrim(true);
				}
				else {
					
					System.out.println("Mediator Secundario");

					portImpl.setIsPrim(false);
					return;

				}
				uddiNaming.rebind(wsName, wsURL);
			}
			
			
				
		} catch (Exception e) {
			uddiNaming = null;
			if (verbose) {
				System.out.printf("Caught exception when binding to UDDI: %s%n", e);
			}
			throw e;
		}
	}

	void unpublishFromUDDI() {
		try {
			if (uddiNaming != null) {
				// delete from UDDI
				uddiNaming.unbind(wsName);
				if (verbose) {
					System.out.printf("Unpublished '%s' from UDDI%n", wsName);
				}
				uddiNaming = null;
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when unbinding: %s%n", e);
			}
		}
		
		this.getTimer().cancel();
		this.getTimer().purge();
	}
	
	public void checkIfAlive(){
		
		Date now = new Date();
		if (now.getTime() - portImpl.date.getTime() > 6000 )
			try {
				
				System.out.println("Primary Mediator Offline");

				this.getTimer().cancel();
				this.getTimer().purge();
				
				uddiNaming.rebind(wsName, wsURL);
				System.out.println("Assuming RollPLay");
			} catch (UDDINamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	} 

	}
