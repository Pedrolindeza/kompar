package org.kompar.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.kompar.mediator.ws.InvalidCartId_Exception;
import org.kompar.mediator.ws.InvalidItemId_Exception;
import org.kompar.mediator.ws.InvalidQuantity_Exception;
import org.kompar.mediator.ws.ItemIdView;
import org.kompar.mediator.ws.NotEnoughItems_Exception;
import org.kompar.supplier.ws.BadProductId_Exception;
import org.kompar.supplier.ws.BadProduct_Exception;

public class AddToCartTest extends BaseIT {

	private ItemIdView item = new ItemIdView();
	private ItemIdView item2 = new ItemIdView();

	@Override
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		super.setUp();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void cartIdNullTest() throws InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception,
			InvalidItemId_Exception {
		mediatorClient.addToCart(null, item, 9);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void cartIdEmptyTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("", item, 9);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void cartIdSpaceTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("      ", item, 9);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void cartIdTabTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("\t", item, 9);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void cartIdNewLineTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("\n", item, 9);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void itemIdNullTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("carrinho", item2, 9);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void itemIdNullProductTest() throws InvalidItemId_Exception, InvalidCartId_Exception,
			InvalidQuantity_Exception, NotEnoughItems_Exception {
		item.setProductId(null);
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 9);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void itemIdNullSupplierIdTest() throws InvalidItemId_Exception, InvalidCartId_Exception,
			InvalidQuantity_Exception, NotEnoughItems_Exception {
		item.setProductId("X1");
		item.setSupplierId(null);
		mediatorClient.addToCart("carrinho", item, 9);
	}

	@Test(expected = InvalidQuantity_Exception.class)
	public void Quantity0Test() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("carrinho", item2, 0);
	}

	@Test
	public void success() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("carrinho", item, 9);

		assertEquals(1, mediatorClient.listCarts().size());
		assertEquals("carrinho", mediatorClient.listCarts().get(0).getCartId());
		assertEquals(1, mediatorClient.listCarts().get(0).getItems().size());
		assertEquals(9, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());
	}

	@Test
	public void successIncQuantity() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();

		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("cart1", item, 1);
		assertEquals(1, mediatorClient.listCarts().size());
		assertEquals(1, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());

		mediatorClient.addToCart("cart1", item, 3);
		assertEquals(1, mediatorClient.listCarts().size());
		assertEquals(4, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());

		item.setProductId("Y2");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("cart1", item, 5);
		assertEquals(1, mediatorClient.listCarts().size());
		assertEquals(2, mediatorClient.listCarts().get(0).getItems().size());
		assertEquals(5, mediatorClient.listCarts().get(0).getItems().get(1).getQuantity());

	}

	@Test(expected = NotEnoughItems_Exception.class)
	public void moreQuantityTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		mediatorClient.addToCart("carrinho", item, 11);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void failProductIdTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		item.setProductId("X9");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 11);
	}

	@Test(expected = NotEnoughItems_Exception.class)
	public void testExceedingQuantity2() throws InvalidCartId_Exception, InvalidItemId_Exception,
			InvalidQuantity_Exception, NotEnoughItems_Exception {
		// addToCart should not decrease available quantity,
		// but should check if a single cart has more items than
		// the available ones.
		ItemIdView id = new ItemIdView();
		id.setProductId("Y2");
		id.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", id, 10);
		mediatorClient.addToCart("carrinho", id, 1);
	}

}
