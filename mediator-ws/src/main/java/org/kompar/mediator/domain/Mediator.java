package org.komparator.mediator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.komparator.mediator.ws.ShoppingResultView;

public class Mediator {
	private Map<String, Cart> listcart;
	private List<ShoppingResultView> purchaseList;
	private AtomicInteger purchaseIdCounter = new AtomicInteger(0);

	public Mediator() {
		listcart = new ConcurrentHashMap<>();
		purchaseList = new ArrayList<ShoppingResultView>();
	}

	public ArrayList<Cart> getCarts() {
		return new ArrayList<Cart>(listcart.values());
	}

	public void addCart(String cartid, Cart carts) {
		listcart.put(cartid, carts);
	}

	public Cart getCart(String cartId) {
		return listcart.get(cartId);
	}

	public boolean cartExists(String cartId) {
		return getCart(cartId) != null;
	}

	public String generatePurchaseId(String pid) {
		// relying on AtomicInteger to make sure assigned number is unique
		int purchaseId = purchaseIdCounter.incrementAndGet();
		return Integer.toString(purchaseId);
	}

	public synchronized void addPurchase(ShoppingResultView srv) {
		purchaseList.add(srv);
	}

	public List<ShoppingResultView> getPurchaseList() {
		return purchaseList;
	}

	public void setPurchaseList(List<ShoppingResultView> srv) {
		purchaseList = srv;
	}
}
