package org.komparator.mediator.ws;

import java.util.Date;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;
import org.komparator.security.SecuritySingleton;

public class LifeProof extends Thread {

	String secURL = "http://localhost:8072/mediator-ws/endpoint";
	MediatorPortType ligacao = null;
	private MediatorEndpointManager endpoint;
	private static final int TEMPO = 5;

	public LifeProof(MediatorEndpointManager endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void run() {

		if (SecuritySingleton.getInstance().getWsI() == 1) {

			// MediatorClient ligacao = new MediatorClient (secURL);

			try {
				MediatorClient ligacao = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
				while (true) {
					ligacao.imAlive(); // De 5 em 5 segundos (valor
										// configurável)
					// chama o método imAlive() no servidor
					// através do cliente
					nap(TEMPO);
					System.out.println("imAlive");
				}
			} catch (MediatorClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Cria um cliente de mediador que aponta para o mediador secundário

		} else if (SecuritySingleton.getInstance().getWsI() == 2) {
			while (SecuritySingleton.getInstance().getDate() == null) {
				nap(1);
			}
			while (((new Date().getTime() - SecuritySingleton.getInstance().getDate().getTime()) / 1000) < TEMPO + 1) {
				nap(TEMPO);
			}
			try {
				endpoint.publishToUDDI();
				endpoint.awaitConnections();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			System.out.println("Erro no life proof, mediator nao reconhecido");
		}

	}

	private void nap(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in the nap");
		}
	}
}
