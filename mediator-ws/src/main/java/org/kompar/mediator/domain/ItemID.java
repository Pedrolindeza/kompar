package org.komparator.mediator.domain;

public class ItemID {
	 	private String productId;
	    private String supplierId;
		private Object itemID;

	    public ItemID(String productId, String supplierId){
	    	this.productId=productId;
	    	this.supplierId=supplierId;   	
	    }
	    public String getProductId() {
	        return productId;
	    }

	    public void setProductId(String value) {
	        this.productId = value;
	    }

	    public String getSupplierId() {
	        return supplierId;
	    }

	    public void setSupplierId(String value) {
	        this.supplierId = value;
	    }
	    public boolean equals(Object obj){
	    	ItemID itemID = (ItemID) obj;
	    	return this.itemID.equals(itemID.getSupplierId()) && this.supplierId.equals(itemID.getProductId());
	    	
	    }	
}
