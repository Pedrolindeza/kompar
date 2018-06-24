package org.magazyn.wharehouse.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jws.HandlerChain;
import javax.jws.WebService;

import org.magazyn.wharehouse.domain.Product;
import org.magazyn.wharehouse.domain.Purchase;
import org.magazyn.wharehouse.domain.QuantityException;
import org.magazyn.wharehouse.domain.Supplier;

@WebService(endpointInterface = "org.magazyn.wharehouse.ws.SupplierPortType", wsdlLocation = "wharehouse.1_0.wsdl", name = "SupplierWebService", portName = "SupplierPort", targetNamespace = "http://ws.wharehouse.magazyn.org/", serviceName = "SupplierService")
public class SupplierPortImpl implements SupplierPortType {

	// end point manager
	private SupplierEndpointManager endpointManager;

	public SupplierPortImpl(SupplierEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
 
	// Main operations -------------------------------------------------------
	@Override
	public ProductView getProduct(String productId) throws BadProductId_Exception {
		System.out.println("\nStore is looking for " + productId + "\n");
		
		// check product id
		if (productId == null) {
			throwBadProductId("Product identifier cannot be null!");
		}
		productId = productId.trim();
		if (productId.length() == 0) {
			throwBadProductId("Product identifier cannot be empty or whitespace!");
		}

		// retrieve product
		Supplier supplier = Supplier.getInstance();
		Product p = supplier.getProduct(productId);
		if (p != null) {
			ProductView pv = newProductView(p);
			// product found!
			return pv;
		}
		// product not found
		return null;
	}

	@Override
	public String buyProduct(String productId, int quantity)
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {

		if (!acceptProductId(productId)) {
			throwBadProductId("Invalid product ID");
		}

		// Trims the productId string, returning a copy of this string with
		// leading and trailing white space removed
		String trimmedProductId = productId.trim();
		String purchaseId = null;

		Supplier supplier = Supplier.getInstance();

		if (!acceptQuantity(quantity)) {
			throwBadQuantity("Quantity to buy must be bigger than 0");
		}

		if (!supplier.productExists(trimmedProductId)) {
			throwBadProductId("Product doesn't exist");
		} else {
			try {
				purchaseId = supplier.buyProduct(trimmedProductId, quantity);
			}

			catch (QuantityException qe) {
				throwInsufficientQuantity("There are not enough items to sell");
			}
		}
		System.out.println("ProductID " + productId + " bought " + quantity + " time(s)" );
		return purchaseId;
	}

	// Auxiliary operations --------------------------------------------------

	private Boolean acceptProductId(String productId) {
		return productId != null && !"".equals(productId);
	}

	private Boolean acceptQuantity(int quantity) {
		return quantity > 0;
	}

	@Override
	public String ping(String name) {
		if (name == null || name.trim().length() == 0) {
			name = "friend";
		}

		String wsName = "Wharehouse";
		System.out.println("Received Ping() from " + name);
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(name);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	@Override
	public void clear() {
		Supplier.getInstance().reset();
	} 

	@Override
	public void createProduct(ProductView productToCreate) throws BadProductId_Exception, BadProduct_Exception {
		// check null
		if (productToCreate == null) {
			throwBadProduct("Product view cannot be null!");
		}
		// check id
		String productId = productToCreate.getId();
		if (productId == null) {
			throwBadProductId("Product identifier cannot be null!");
		}
		productId = productId.trim();
		if (productId.length() == 0) {
			throwBadProductId("Product identifier cannot be empty or whitespace!");
		}
		// check description
		String productDesc = productToCreate.getDesc();
		if (productDesc == null) {
			productDesc = "";
		}
		// check quantity
		int quantity = productToCreate.getQuantity();
		if (quantity <= 0) {
			throwBadProduct("Quantity must be a positive number!");
		}
		// check price
		int price = productToCreate.getPrice();
		if (price <= 0) {
			throwBadProduct("Price must be a positive number!");
		}

		// create new product
		Supplier s = Supplier.getInstance();
		s.registerProduct(productId, productDesc, quantity, price);
	}

	@Override
	public List<ProductView> listProducts() {
		Supplier supplier = Supplier.getInstance();
		List<ProductView> pvw = new ArrayList<ProductView>();
		for (String pid : supplier.getProductsIDs()) {
			ProductView pro = newProductView(supplier.getProduct(pid));
			pvw.add(pro);
		}
		return pvw;
	}


	// View helpers ----------------------------------------------------------

	private ProductView newProductView(Product product) {
		ProductView view = new ProductView();
		view.setId(product.getId());
		view.setDesc(product.getDescription());
		view.setQuantity(product.getQuantity());
		view.setPrice(product.getPrice());
		return view;
	}

	// Exception helpers -----------------------------------------------------

	/** Helper method to throw new BadProductId exception */
	private void throwBadProductId(final String message) throws BadProductId_Exception {
		BadProductId faultInfo = new BadProductId();
		faultInfo.message = message;
		throw new BadProductId_Exception(message, faultInfo);
	}

	/** Helper method to throw new BadProduct exception */
	private void throwBadProduct(final String message) throws BadProduct_Exception {
		BadProduct faultInfo = new BadProduct();
		faultInfo.message = message;
		throw new BadProduct_Exception(message, faultInfo);
	}

	/** Helper method to throw new BadQuantity exception */
	private void throwBadQuantity(final String message) throws BadQuantity_Exception {
		BadQuantity faultInfo = new BadQuantity();
		faultInfo.message = message;
		throw new BadQuantity_Exception(message, faultInfo);
	}

	/** Helper method to throw new InsufficientQuantity exception */
	private void throwInsufficientQuantity(final String message) throws InsufficientQuantity_Exception {
		InsufficientQuantity faultInfo = new InsufficientQuantity();
		faultInfo.message = message;
		throw new InsufficientQuantity_Exception(message, faultInfo);
	}

}
