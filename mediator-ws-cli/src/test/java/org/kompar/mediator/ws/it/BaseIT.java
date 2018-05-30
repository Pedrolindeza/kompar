package org.kompar.mediator.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kompar.mediator.ws.cli.MediatorClient;
import org.kompar.supplier.ws.BadProductId_Exception;
import org.kompar.supplier.ws.BadProduct_Exception;
import org.kompar.supplier.ws.ProductView;
import org.kompar.supplier.ws.cli.SupplierClient;

public class BaseIT {

	protected static final String SUP2_URL = "http://localhost:8082/supplier-ws/endpoint";

	protected static final String SUP1_URL = "http://localhost:8081/supplier-ws/endpoint";
	protected static SupplierClient sup1, sup2;

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
		} else {
			mediatorClient = new MediatorClient(wsURL);
		}
		sup1 = new SupplierClient(SUP1_URL);
		sup2 = new SupplierClient(SUP2_URL);
	}

	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {

		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(10);
			sup1.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			sup1.createProduct(product);
			product.setPrice(20);
			sup2.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer balls");
			product.setPrice(30);
			product.setQuantity(30);
			sup2.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("T3");
			product.setDesc("1 ball");
			product.setPrice(20);
			product.setQuantity(1);
			sup2.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("T1");
			product.setDesc("2 ball");
			product.setPrice(20);
			product.setQuantity(5);
			sup1.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("T1");
			product.setDesc("3 ball");
			product.setPrice(80);
			product.setQuantity(1);
			sup2.createProduct(product);
		}
	}

	@AfterClass
	public static void cleanup() {
	}

	@After
	public void tearDown() {
		mediatorClient.clear();
		sup1.clear();
		sup2.clear();
	}

}
