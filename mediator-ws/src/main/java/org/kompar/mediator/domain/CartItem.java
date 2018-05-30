package org.komparator.mediator.domain;

import org.komparator.mediator.ws.ItemView;

public class CartItem {
	   private ItemView item;
	    private int quantity;

	    public ItemView getItem() {
	        return item;
	    }

	    public void setItem(ItemView value) {
	        this.item = value;
	    }

	    public int getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(int value) {
	        this.quantity = value;
	    }


}
