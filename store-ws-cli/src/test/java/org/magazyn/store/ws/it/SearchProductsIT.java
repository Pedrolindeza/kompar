package org.magazyn.store.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.magazyn.wharehouse.ws.*;

/**
 * Test suite
 */
@SuppressWarnings("unused")
public class SearchProductsIT extends BaseIT {
 
	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception{
		// clear remote service state before all tests
		store.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			store.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			store.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			store.createProduct(product);
		}
		
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		store.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}
	 
	// main test

	@Test
	public void searchProductSuccess() throws BadProductId_Exception{
		
		ProductView pw = store.getProduct("X1");
		if (pw != null){
				System.out.println("\n" + pw.getDesc() + " exists with quantity of : " + pw.getQuantity() + "\n"); return;}
		else{
			System.out.println("\nProduct not found!\n");
		}
	}
}
