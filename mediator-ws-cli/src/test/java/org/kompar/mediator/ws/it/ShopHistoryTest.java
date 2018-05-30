package org.kompar.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.kompar.mediator.ws.EmptyCart_Exception;
import org.kompar.mediator.ws.InvalidCartId_Exception;
import org.kompar.mediator.ws.InvalidCreditCard_Exception;
import org.kompar.mediator.ws.InvalidItemId_Exception;
import org.kompar.mediator.ws.InvalidQuantity_Exception;
import org.kompar.mediator.ws.ItemIdView;
import org.kompar.mediator.ws.NotEnoughItems_Exception;
import org.kompar.mediator.ws.ShoppingResultView;

public class ShopHistoryTest extends BaseIT {
	@Test
	public void sucess() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 1);

		ShoppingResultView shop1 = mediatorClient.buyCart("carrinho", "4024007102923926");
		mediatorClient.addToCart("car", item, 1);
		ShoppingResultView shop2 = mediatorClient.buyCart("car", "4024007102923926");
		List<ShoppingResultView> srv = mediatorClient.shopHistory();
		assertEquals(srv.size(), 2);
		assertEquals(srv.get(0).getId(), shop1.getId());
		assertEquals(srv.get(0).getDroppedItems().size(), shop1.getDroppedItems().size());
		assertEquals(srv.get(0).getPurchasedItems().size(), shop1.getPurchasedItems().size());
		assertEquals(srv.get(0).getResult(), shop1.getResult());
		assertEquals(srv.get(0).getTotalPrice(), shop1.getTotalPrice());
		assertEquals(srv.get(1).getId(), shop2.getId());
		assertEquals(srv.get(1).getDroppedItems().size(), shop2.getDroppedItems().size());
		assertEquals(srv.get(1).getPurchasedItems().size(), shop2.getPurchasedItems().size());
		assertEquals(srv.get(1).getResult(), shop2.getResult());
		assertEquals(srv.get(1).getTotalPrice(), shop2.getTotalPrice());

	}
}
