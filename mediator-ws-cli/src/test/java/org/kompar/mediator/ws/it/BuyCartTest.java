package org.kompar.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kompar.mediator.ws.EmptyCart_Exception;
import org.kompar.mediator.ws.InvalidCartId_Exception;
import org.kompar.mediator.ws.InvalidCreditCard_Exception;
import org.kompar.mediator.ws.InvalidItemId_Exception;
import org.kompar.mediator.ws.InvalidQuantity_Exception;
import org.kompar.mediator.ws.ItemIdView;
import org.kompar.mediator.ws.NotEnoughItems_Exception;
import org.kompar.mediator.ws.Result;
import org.kompar.mediator.ws.ShoppingResultView;

public class BuyCartTest extends BaseIT {

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartNullCartIdTest()
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(null, "123456789");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartEmptyCartIdTest()
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("", "123456789");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartSpaceCartIdTest()
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("           ", "123456789");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartTabCarIdTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\t", "123456789");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartNewLineCarIdTest()
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\n", "123456789");
	}// TODO

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartInvalidCardIdTest()
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("carrinho", "4024007102923926");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void testInexistingCart() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("xyz", "0293");
	}

	@Test(expected = InvalidCreditCard_Exception.class)
	public void BuyCartInvalidCreditCardTest() throws EmptyCart_Exception, InvalidCartId_Exception,
			InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 1);

		mediatorClient.buyCart("carrinho", "1");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void BuyCartEmptyCartTest()
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("carrinho", "4024007102923926");
	}

	@Test
	public void sucessBuyCartWithCart() throws EmptyCart_Exception, InvalidCartId_Exception,
			InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 1);

		ShoppingResultView shop = mediatorClient.buyCart("carrinho", "4024007102923926");
		assertEquals(shop.getId(), "1");
		assertEquals(shop.getResult(), Result.COMPLETE);
		assertEquals(shop.getTotalPrice(), 10);
		assertEquals(shop.getPurchasedItems().size(), 1);
		assertEquals(shop.getDroppedItems().size(), 0);
	}

	@Test
	public void sucessDroppedItemsCart() throws EmptyCart_Exception, InvalidCartId_Exception,
			InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);

		mediatorClient.addToCart("carrinho", item, 10);
		mediatorClient.addToCart("car", item, 10);
		ShoppingResultView shop1 = mediatorClient.buyCart("carrinho", "4024007102923926");
		ShoppingResultView shop2 = mediatorClient.buyCart("car", "4024007102923926");
		assertEquals(shop1.getPurchasedItems().size(), 1);
		assertEquals(shop2.getDroppedItems().size(), 1);
		assertEquals(shop2.getResult(), Result.EMPTY);

	}

	@Test
	public void partialTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		ItemIdView item2 = new ItemIdView();
		item2.setProductId("Y2");
		item2.setSupplierId(SUP1_URL);

		mediatorClient.addToCart("carrinho", item, 10);

		mediatorClient.addToCart("car", item, 5);
		mediatorClient.addToCart("car", item2, 5);

		mediatorClient.buyCart("carrinho", "4024007102923926");
		ShoppingResultView shop1 = mediatorClient.buyCart("car", "4024007102923926");
		assertEquals(shop1.getPurchasedItems().size(), 1);
		assertEquals(shop1.getDroppedItems().size(), 1);
		assertEquals(shop1.getResult(), Result.PARTIAL);

	}

	@Test(expected = InvalidItemId_Exception.class)
	public void failProductIdTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X9");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 1);

	}

	@Test(expected = InvalidQuantity_Exception.class)
	public void failQuantityTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 0);

	}

}
