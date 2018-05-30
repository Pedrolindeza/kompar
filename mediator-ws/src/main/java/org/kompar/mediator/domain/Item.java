package org.komparator.mediator.domain;



public class Item {
		private ItemID itemId;
	    private String desc;
	    private int price;
	    
	    public Item(ItemID itemId, String desc,int price){
	    	this.itemId=itemId;
	    	this.desc=desc;
	    	this.price=price;   	
	    }

	    public ItemID getItemId() {
	        return itemId;
	    }

	    public void setItemId(ItemID value) {
	        this.itemId = value;
	    }


	    public String getDesc() {
	        return desc;
	    }
	    public boolean equals(Object obj){
	    	Item item = (Item) obj;
	    	return this.itemId.equals(item.getItemId()) && this.price == item.getPrice() && this.desc.equals(item.getDesc());
	    	
	    }	


	    public void setDesc(String value) {
	        this.desc = value;
	    }

	    public int getPrice() {
	        return price;
	    }


	    public void setPrice(int value) {
	        this.price = value;
	    }

}
