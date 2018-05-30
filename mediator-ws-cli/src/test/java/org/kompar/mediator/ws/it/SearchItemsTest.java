package org.kompar.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.kompar.mediator.ws.InvalidText_Exception;
import org.kompar.mediator.ws.ItemView;

public class SearchItemsTest extends BaseIT {

	@Test(expected = InvalidText_Exception.class)
	public void SearchItemsNullTest() throws InvalidText_Exception {
		mediatorClient.searchItems(null);
	}

	@Test(expected = InvalidText_Exception.class)
	public void SearchItemsEmptyTest() throws InvalidText_Exception {
		mediatorClient.searchItems("");
	}

	@Test(expected = InvalidText_Exception.class)
	public void SearchItemsSpaceTest() throws InvalidText_Exception {
		mediatorClient.searchItems("     ");
	}

	@Test(expected = InvalidText_Exception.class)
	public void SearchItemsNewLineTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\n");
	}

	@Test(expected = InvalidText_Exception.class)
	public void SearchItemsTabsTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\t");
	}

	@Test
	public void SearchItemsNotFoundItemTest() throws InvalidText_Exception {
		List<ItemView> item = mediatorClient.searchItems("NOT_FOUND");
		assertEquals(0, item.size());
	}

	@Test
	public void sucessSearchItem() throws InvalidText_Exception {
		List<ItemView> item = mediatorClient.searchItems("Baseball");
		assertEquals(1, item.size());
		assertEquals(20, item.get(0).getPrice());
		assertEquals("Baseball", item.get(0).getDesc());
		assertEquals("A45_Supplier1", item.get(0).getItemId().getSupplierId());
		assertEquals("Y2", item.get(0).getItemId().getProductId());
	}// TODO

	@Test
	public void OrderTest() throws InvalidText_Exception {
		List<ItemView> item = mediatorClient.searchItems("ball");

		assertEquals(20, item.get(0).getPrice());
		assertEquals("T1", item.get(0).getItemId().getProductId());

		assertEquals(80, item.get(1).getPrice());
		assertEquals("T1", item.get(1).getItemId().getProductId());

		assertEquals(20, item.get(2).getPrice());
		assertEquals("T3", item.get(2).getItemId().getProductId());

	}
}
