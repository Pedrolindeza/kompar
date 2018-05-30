package org.komparator.mediator.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.Oneway;
import javax.jws.WebService;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.komparator.mediator.domain.Cart;
import org.komparator.mediator.domain.Mediator;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;
import org.komparator.security.SecuritySingleton;
import org.komparator.security.handler.MessageIdHandler;
//import org.komparator.supplier.domain.Supplier;
//import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

// TODO annotate to bind with WSDL
// TODO implement port type interface
@HandlerChain(file = "/MediatorService_handler.xml")
@WebService(endpointInterface = "org.komparator.mediator.ws.MediatorPortType", wsdlLocation = "mediator.wsdl", name = "Mediator", portName = "MediatorPort", targetNamespace = "http://ws.mediator.komparator.org/", serviceName = "MediatorService")

public class MediatorPortImpl implements MediatorPortType {

	String secURL = "http://localhost:8072/mediator-ws/endpoint";
	private static final String A45_SUPPLIER = "A45_Supplier";

	private Date date;

	private Map<String, Object> ids = new HashMap<String, Object>();

	private Mediator mediator = new Mediator();
	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	public static final String CLASS_NAME = MediatorPortImpl.class.getSimpleName();
	public static final String TOKEN = "server";

	@Resource
	private WebServiceContext webServiceContext;

	// retrieve message context

	// Main operations -------------------------------------------------------

	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		String supplierId;
		String description;
		int price;

		if (!acceptItemId(productId)) {
			throwInvalidItemId("Invalid item ID");
		}

		// Reaches active suppliers. Returns supplier clients to each supplier.
		List<ItemView> itemViewList = new ArrayList<ItemView>();

		List<UDDIRecord> supplierClients = getAvaliableSupplierClients(); // Can
		// send

		for (UDDIRecord record : supplierClients) {

			supplierId = getSupplierId(record);

			ItemIdView itemIdView = new ItemIdView();
			itemIdViewSetAll(itemIdView, productId, supplierId);

			try {
				ProductView productView;
				// tenho de criar um supplier atraves da lista do UDDIRecord
				SupplierClient s = new SupplierClient(record.getUrl());
				productView = s.getProduct(productId); // Can send
				// BadProductId_Exception
				if (productView != null) {
					price = productView.getPrice();
					description = productView.getDesc();

					ItemView itemView = new ItemView();
					itemViewSetAll(itemView, itemIdView, description, price);
					itemViewList.add(itemView);
				}

			} catch (BadProductId_Exception e) {
				throwInvalidItemId("Invalid item ID");
			} catch (SupplierClientException e) {
				e.printStackTrace();
			}

		}

		// Ordenar lista de items, por preço
		itemViewList.sort(new Comparator<ItemView>() {
			@Override
			public int compare(ItemView i1, ItemView i2) {
				int preco1 = i1.getPrice();
				int preco2 = i2.getPrice();

				// Menor preco primeiro
				if (preco1 > preco2) {
					return 1;
				} else if (preco1 == preco2) {
					return 0;
				} else {
					return -1;
				}
			}

		});

		return itemViewList;
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {

		List<ItemView> lista = new ArrayList<ItemView>();
		List<SupplierClient> supplierClients;
		List<UDDIRecord> supplierClients1 = getAvaliableSupplierClients();

		String productId;
		String supplierId;
		String desc;
		int price;

		if (!acceptDesc(descText)) {
			throwInvalidText("Invalid desc for product");
		}

		for (UDDIRecord record : supplierClients1) {
			List<ProductView> listaprod = new ArrayList<ProductView>();
			try {
				SupplierClient s = new SupplierClient(record.getUrl());
				listaprod = s.searchProducts(descText);
			} catch (BadText_Exception e) {
				throwInvalidText("Invalid desc for product");
			} catch (SupplierClientException e) {
				e.printStackTrace();
			}

			supplierId = record.getOrgName();
			for (ProductView p : listaprod) {

				// Gets ItemIdView from product id
				ItemIdView itemIdView = new ItemIdView();
				productId = p.getId();
				itemIdViewSetAll(itemIdView, productId, supplierId);

				// Gets ItemView from ItemIdView, desc and price
				ItemView itemView = new ItemView();
				desc = p.getDesc();
				price = p.getPrice();
				itemViewSetAll(itemView, itemIdView, desc, price);

				lista.add(itemView);
			}
		}

		// Sorts the itemView list alphabetically and then for price
		lista.sort(new Comparator<ItemView>() {
			@Override
			public int compare(ItemView i1, ItemView i2) {
				int preco1 = i1.getPrice();
				int preco2 = i2.getPrice();
				String Id1 = i1.getItemId().getProductId();
				String Id2 = i2.getItemId().getProductId();
				int ordemId;

				ordemId = Id1.compareTo(Id2);
				if (ordemId == 0) {
					if (preco1 > preco2) {
						return 1;
					} else if (preco1 == preco2) {
						return 0;
					} else {
						return -1;
					}
				} else {
					return ordemId;
				}
			}

		});

		return lista;
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception, // TODO
			// sincronizacao
			// excecoes
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

		MessageContext messageContext = webServiceContext.getMessageContext();

		// *** #6 ***
		// get token from message context
		String propertyValue = (String) messageContext.get(MessageIdHandler.REQUEST_PROPERTY);
		System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, propertyValue);

		Object obj = ids.get(propertyValue);
		if (obj != null) {
			return;
		}
		if (!acceptCart(cartId)) {
			throwInvalidCartId("Invalid cart id");
		}

		if (!acceptQuantity(itemQty)) {
			throwInvalidQuantity("Invalid cart id");
		}

		if (!acceptItemIdView(itemId)) {
			throwInvalidItemId("Invalid item id");
		}

		if (!mediator.cartExists(cartId)) {
			mediator.addCart(cartId, new Cart(cartId));
		}
		// For each cart active view in the selected cartId

		for (CartItemView cartItemView : mediator.getCart(cartId).getCartItemViewList()) {

			// The item we are trying to find, exists in the cart
			if (cartItemView.getItem().getItemId().getProductId().equals(itemId.getProductId())
					&& cartItemView.getItem().getItemId().getSupplierId().equals(itemId.getSupplierId())) {
				SupplierClient sC = getSupplierClientFromCartView(cartItemView);
				try {
					ProductView pv = sC.getProduct(itemId.getProductId());
					// If it exists, check if the ammount required is not
					// greater than the stock
					if (cartItemView.getQuantity() + itemQty > pv.getQuantity()) {
						throwNotEnoughItems("Not enough items");
						return;
					}

				} catch (BadProductId_Exception e) {
					throwInvalidItemId("Invalid Item id");
				}

				cartItemView.setQuantity(cartItemView.getQuantity() + itemQty);

				if (SecuritySingleton.getInstance().getWsI() == 1) {
					CartItemView civ = new CartItemView();
					civ.setQuantity(cartItemView.getQuantity());

					String supId = itemId.getSupplierId();
					String prodId = itemId.getProductId();
					SupplierClient client = getSupplierClient(supId);
					try {
						ProductView prodView = client.getProduct(prodId);
						String description = prodView.getDesc();
						int price = prodView.getPrice();

						ItemView iv = new ItemView();
						itemViewSetAll(iv, itemId, description, price);

						civ.setItem(iv);
						CartView result = new CartView();
						result.getItems().add(civ);
						result.setCartId(cartId);

						try {
							MediatorClient ligacao = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
							ligacao.updateCart(result, 1);
							ids.put(propertyValue, cartId);
						} catch (MediatorClientException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (BadProductId_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				return;

			}
		}

		// If the item is not in the cart

		CartItemView civ = new CartItemView();
		civ.setQuantity(itemQty);

		String supId = itemId.getSupplierId();
		String prodId = itemId.getProductId();
		SupplierClient client = getSupplierClient(supId);

		try {
			ProductView prodView = client.getProduct(prodId);
			if (prodView == null) {
				throwInvalidItemId("Invalid item id");
			}
			// There are not enough items of that kind in the supplier

			if (itemQty > prodView.getQuantity()) {
				throwNotEnoughItems("Not enough items");
				return;
			}
			String description = prodView.getDesc();
			int price = prodView.getPrice();

			ItemView iv = new ItemView();
			itemViewSetAll(iv, itemId, description, price);

			civ.setItem(iv);

			// Add new cartItemView to the mediator
			mediator.getCart(cartId).addItem(civ);

			if (SecuritySingleton.getInstance().getWsI() == 1) {
				CartView result = new CartView();
				result.getItems().add(civ);
				result.setCartId(cartId);

				try {
					MediatorClient ligacao = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
					ligacao.updateCart(result, 0);
					ids.put(propertyValue, cartId);
				} catch (MediatorClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		catch (BadProductId_Exception e) {
			throwInvalidItemId("Invalid item id");

		}

		// Closes for cycle

	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {

		MessageContext messageContext = webServiceContext.getMessageContext();

		String propertyValue = (String) messageContext.get(MessageIdHandler.REQUEST_PROPERTY);
		System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, propertyValue);

		Object obj = ids.get(propertyValue);
		if (obj != null) {
			return (ShoppingResultView) obj;
		}

		if (!acceptCart(cartId)) {
			throwInvalidCartId("null");
		}

		if (!acceptCardId(creditCardNr)) {
			throwInvalidCreditCard("Invalid credit card id");
		}

		boolean purchaseFinalized = false;
		boolean uncomplete = false;

		ShoppingResultView srv = new ShoppingResultView();

		// None pretended item was purchased so far
		srv.setResult(Result.EMPTY);
		try {
			Cart cart = mediator.getCart(cartId);
			if (cart == null) {
				throwInvalidCartId("Invalid cart id");

			}
			CreditCardClient ccc = new CreditCardClient("http://ws.sd.rnl.tecnico.ulisboa.pt:8080/cc");
			if (ccc.validateNumber(creditCardNr)) {

				// If the credit card is valid, proced to purchase
				for (CartItemView cartItemView : cart.getCartItemViewList()) {
					try {
						SupplierClient client = new SupplierClient(
								endpointManager.getUddiNaming().lookup(getSupplierIdFromCartItemView(cartItemView)));
						client.buyProduct(cartItemView.getItem().getItemId().getProductId(),
								cartItemView.getQuantity());
						srv.getPurchasedItems().add(cartItemView);
						int cost = cartItemView.getQuantity() * cartItemView.getItem().getPrice();
						srv.setTotalPrice(srv.getTotalPrice() + cost);

						purchaseFinalized = true;

					} catch (SupplierClientException e) {
						srv.getDroppedItems().add(cartItemView);
						uncomplete = true;
					} catch (UDDINamingException u) {
						srv.getDroppedItems().add(cartItemView);
						uncomplete = true;
					} catch (BadProductId_Exception e) {
						srv.getDroppedItems().add(cartItemView);
						uncomplete = true;

					} catch (BadQuantity_Exception e) {
						srv.getDroppedItems().add(cartItemView);
						uncomplete = true;

					} catch (InsufficientQuantity_Exception e) {
						srv.getDroppedItems().add(cartItemView);
						uncomplete = true;

					}

				}
			} else {
				throwInvalidCreditCard("Invalid credit card");
			}
		} catch (CreditCardClientException e) {
			throwInvalidCreditCard("Invalid CC");
		}

		// The purchase was taken to its end
		if (purchaseFinalized) {
			if (uncomplete) {
				srv.setResult(Result.PARTIAL);
			} else {
				srv.setResult(Result.COMPLETE);
			}
		}
		srv.setId(mediator.generatePurchaseId(cartId));
		mediator.addPurchase(srv);

		if (SecuritySingleton.getInstance().getWsI() == 1) {

			try {
				MediatorClient ligacao = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
				ligacao.updateShopHistory(srv);
				ids.put(propertyValue, srv);
			} catch (MediatorClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return srv;

	}

	@Override
	@Oneway
	public void imAlive() {
		System.out.println("Checking if alive");
		int primary = SecuritySingleton.getInstance().getWsI();
		Date date = new Date();
		if (primary == 1) {
			return;
		} else {
			SecuritySingleton.getInstance().setDate(date);
		}
	}

	@Override
	@Oneway
	public void updateShopHistory(ShoppingResultView result) {
		mediator.addPurchase(result);
	}

	@Override
	@Oneway
	public void updateCart(CartView result, int opcao) {

		if (opcao == 0) {
			if (!mediator.cartExists(result.getCartId())) {
				mediator.addCart(result.getCartId(), new Cart(result.getCartId()));
			}
			mediator.getCart(result.getCartId()).getCartItemViewList().add(result.getItems().get(0));
		}
		if (opcao == 1) {

			for (CartItemView cartItemView : mediator.getCart(result.getCartId()).getCartItemViewList()) {

				if (cartItemView.getItem().getItemId().getProductId()
						.equals(result.getItems().get(0).getItem().getItemId().getProductId())
						&& cartItemView.getItem().getItemId().getSupplierId()
								.equals(result.getItems().get(0).getItem().getItemId().getSupplierId())) {

					cartItemView.setQuantity(result.getItems().get(0).getQuantity());
				}
			}

		}

	}
	// Auxiliary operations --------------------------------------------------

	public List<UDDIRecord> getAvaliableSupplierClients() {
		List<UDDIRecord> supplierClients = new ArrayList<UDDIRecord>();

		try {
			// UDDIRecord tem o url e o nome
			supplierClients = (List<UDDIRecord>) endpointManager.getUddiNaming().listRecords(A45_SUPPLIER + "%");
		} catch (UDDINamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return supplierClients;
	}

	// Gets unique identifier of supplier, through the wsURL
	String getSupplierId(UDDIRecord sC) {
		return sC.getUrl();
	}

	@Override
	public String ping(String arg0) {
		String url = "";

		for (UDDIRecord record : getAvaliableSupplierClients()) {
			try {
				if (url != null) {
					SupplierClient client = new SupplierClient(record.getUrl());
					url += "\n" + client.ping("Mediator");
				}
			} catch (SupplierClientException exp) {
				exp.printStackTrace();
			}
		}
		return url;
	}

	@Override
	public void clear() { // TODO confirm
		if (SecuritySingleton.getInstance().getWsI() == 1) {
			for (UDDIRecord record : getAvaliableSupplierClients()) {
				try {
					// percorro a lista, crio clientes para os eliminar
					SupplierClient sC = new SupplierClient(record.getUrl());
					sC.clear();
				} catch (SupplierClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MediatorService service = new MediatorService();
			MediatorPortType ligacao = service.getMediatorPort();

			BindingProvider bindingProvider = (BindingProvider) ligacao;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, secURL);
			ligacao.clear();
		}

		mediator = new Mediator();
	}

	public boolean acceptItemId(String productId) {
		return !(productId == null || productId.trim().length() == 0);
	}

	public boolean acceptItemIdView(ItemIdView itemIdView) {
		return !(itemIdView == null || itemIdView.getSupplierId() == null || itemIdView.getProductId() == null
				|| itemIdView.getProductId().length() == 0 || itemIdView.getSupplierId().length() == 0);
	}

	public boolean acceptDesc(String descText) {
		return !(descText == null || descText.trim().length() == 0);
	}

	public boolean acceptCart(String cartId) {
		return !(cartId == null || cartId.trim().length() == 0);
	}

	public boolean acceptCardId(String cardId) { // TODO testar so alfanumericos
		return !(cardId == null || cardId.trim().length() == 0);
	}

	public boolean acceptQuantity(int quant) { // TODO
		return quant > 0;
	}

	public void itemIdViewSetAll(ItemIdView itemIdView, String prod, String supplier) {
		itemIdView.setProductId(prod);
		itemIdView.setSupplierId(supplier);
	}

	public void itemViewSetAll(ItemView itemView, ItemIdView itemId, String description, int price) {
		itemView.setItemId(itemId);
		itemView.setDesc(description);
		itemView.setPrice(price);
	}

	public String getSupplierIdFromCartItemView(CartItemView cart) {
		return cart.getItem().getItemId().getSupplierId();
	}

	public SupplierClient getSupplierClient(String supId) {
		SupplierClient sC = null;
		try {
			sC = new SupplierClient(endpointManager.getUddiNaming().lookup(supId));
		} catch (SupplierClientException | UDDINamingException e) {
			// TODO Auto-generated catch block
		}
		return sC;
	}

	public SupplierClient getSupplierClientFromCartView(CartItemView cart) {
		SupplierClient sC = null;
		try {
			sC = new SupplierClient(endpointManager.getUddiNaming().lookup(getSupplierIdFromCartItemView(cart)));
		} catch (SupplierClientException | UDDINamingException e) {
			// TODO Auto-generated catch block
		}
		return sC;
	}

	// View helpers -----------------------------------------------------
	@Override
	public List<CartView> listCarts() {
		List<CartView> lista = new ArrayList<CartView>();
		for (Cart c : mediator.getCarts()) {
			CartView cart = new CartView();
			cart.setCartId(c.getCartId());
			for (CartItemView civ : c.getCartItemViewList()) {
				cart.getItems().add(civ);
			}
			lista.add(cart);
		}
		return lista;
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		return mediator.getPurchaseList();
	}

	// Exception helpers -----------------------------------------------------
	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}

	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}

	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}

	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}

	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}

	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}

	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}

}