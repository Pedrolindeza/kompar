package org.kompar.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kompar.mediator.ws.InvalidItemId_Exception;
import org.kompar.mediator.ws.ItemView;

public class GetItemTest extends BaseIT {

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemNullTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void ItemIdEmptyTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void ItemIdSpaceTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("     ");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void ItemIdNewLineTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void ItemIdtabTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}

	@Test
	public void success() throws InvalidItemId_Exception {
		List<ItemView> item = mediatorClient.getItems("Y2");
		assertEquals(1, item.size());
		assertEquals(20, item.get(0).getPrice());
		assertEquals("Baseball", item.get(0).getDesc());
		assertEquals(SUP1_URL, item.get(0).getItemId().getSupplierId());
		assertEquals("Y2", item.get(0).getItemId().getProductId());
	}

	@Test
	public void OrderTest() throws InvalidItemId_Exception {
		List<ItemView> list = mediatorClient.getItems("X1");
		assertEquals(2, list.size());

		assertTrue(list.get(0).getPrice() < list.get(1).getPrice());
	}

	@Test
	public void getItemCaseSensitiveTest() throws InvalidItemId_Exception {
		List<ItemView> itemLst = mediatorClient.getItems("x1");
		assertEquals(0, itemLst.size());
	}
	@Test
	public void testSingleExistingItem() throws InvalidItemId_Exception {
		// Testing all item properties only in this test
		List<ItemView> items = mediatorClient.getItems("Y2");
		assertEquals(1, items.size());

		assertEquals("Y2", items.get(0).getItemId().getProductId());
		assertEquals("Baseball", items.get(0).getDesc());
		assertEquals(20, items.get(0).getPrice());
		assertEquals(SUP1_URL, items.get(0).getItemId().getSupplierId());
	}
	
	@Test
	public void getItemNoItemTest() throws InvalidItemId_Exception {
		List<ItemView> itemLst = mediatorClient.getItems("X8");
		assertEquals(0, itemLst.size());
	}

}
