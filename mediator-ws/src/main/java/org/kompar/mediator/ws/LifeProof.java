package org.kompar.mediator.ws;

import java.util.TimerTask;

import org.kompar.mediator.ws.cli.MediatorClient;
import org.kompar.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask {

	private boolean _prim;
	private MediatorEndpointManager _endpointmanager;
	MediatorClient client;

	public LifeProof(MediatorEndpointManager endpoint) {

		MediatorPortImpl portImpl = endpoint.getPortImpl();
		_prim = portImpl.getIsPrim();
		_endpointmanager = endpoint;

	}

	@Override
	public void run() {
		if (_prim) {

			try {
				client = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			} catch (MediatorClientException e) {
				System.out.println("Error.");
			}

			try {
				System.out.println("Am I ALive?");
				client.imAlive();
				System.out.println("Yes");
			} catch (Exception e) {
				System.out.println("Secondary mediator not online.");
			}

		} else {
			_endpointmanager.checkIfAlive();
		}
	}

}
