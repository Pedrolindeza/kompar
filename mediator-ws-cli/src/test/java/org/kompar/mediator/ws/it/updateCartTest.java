package org.kompar.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.kompar.mediator.ws.EmptyCart_Exception;
import org.kompar.mediator.ws.InvalidCartId_Exception;
import org.kompar.mediator.ws.InvalidCreditCard_Exception;
import org.kompar.mediator.ws.InvalidItemId_Exception;
import org.kompar.mediator.ws.InvalidQuantity_Exception;
import org.kompar.mediator.ws.ItemIdView;
import org.kompar.mediator.ws.NotEnoughItems_Exception;
import org.kompar.mediator.ws.cli.MediatorClient;
import org.kompar.mediator.ws.cli.MediatorClientException;
import org.kompar.supplier.ws.BadProductId_Exception;
import org.kompar.supplier.ws.BadProduct_Exception;

public class updateCartTest extends BaseIT {
	private ItemIdView item = new ItemIdView();

	@Override
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		super.setUp();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
	}

	@Test
	public void sucess() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

		try {
			MediatorClient ligacao = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			item.setProductId("X1");
			item.setSupplierId(SUP1_URL);
			mediatorClient.addToCart("cart1", item, 1);
			assertEquals(1, mediatorClient.listCarts().size());
			assertEquals(1, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());

			assertEquals(mediatorClient.listCarts().size(), ligacao.listCarts().size());
			assertEquals(mediatorClient.listCarts().get(0).getItems().get(0).getQuantity(),
					ligacao.listCarts().get(0).getItems().get(0).getQuantity());

		} catch (MediatorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
