package model.entities;

import java.io.Serializable;

public class SaleItens implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer quantity;
	private Double price;
	
	private Product product;
	
	public SaleItens()
	{
		
	}
	public SaleItens(Integer quantity, Double price,Product product) 
	{
		super();
		this.quantity = quantity;
		this.price = price;
		this.product=product;
	}
	
	public Integer getQuantity() 
	{
		return quantity;
	}
	public void setQuantity(Integer quantity) 
	{
		this.quantity = quantity;
	}
	
	public Double getPrice()
	{
		return price;
	}
	public void setPrice(Double price)
	{
		this.price = price;
	}
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SaleItens other = (SaleItens) obj;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity == null) 
		{
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		return true;
	}
	
	public Double subTotal()
	{
		return price*quantity;
	}
	
}
