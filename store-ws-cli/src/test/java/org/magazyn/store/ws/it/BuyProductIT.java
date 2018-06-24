package org.magazyn.store.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception{
		// clear remote service state before all tests
		store.clear();
 
		// fill-in test products
		// (since getProduct and SearchProduct are read-only the initialization below
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

	// main tests

	@Test
	public void buyProducts() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		System.out.println();
		for(ProductView a : store.listProducts()){
			System.out.println("Product: " + a.getId() + "\tQuantity: " + a.getQuantity() );
		}
		
		System.out.println();
		store.buyProduct("X1", 1);
		store.buyProduct("Y2", 2);
		store.buyProduct("Z3", 3);
		System.out.println();
		
		for(ProductView a : store.listProducts()){
			System.out.println("Product: " + a.getId() + "\tQuantity: " + a.getQuantity() );
		}
		System.out.println();
	}
	
}
