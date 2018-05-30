package org.kompar.mediator.domain;

import java.util.ArrayList;
import java.util.List;



import org.kompar.mediator.ws.CartItemView;

public class Cart {
	 	private String cartId;
	    private List<CartItemView> items;
	    
	    public Cart(String cartId){
	    	this.cartId = cartId;
	    }

	    public String getCartId() {
	        return cartId;
	    }

	    public void addItem(CartItemView cav){
	    	this.items.add(cav);
	    }
	    public synchronized void setCartId(String value) {
	        this.cartId = value;
	    }
	    
	    public List<CartItemView> getCartItemViewList() {
	        if (items == null) {
	            items = new ArrayList<CartItemView>();
	        }
	        return this.items;
	    }
}
