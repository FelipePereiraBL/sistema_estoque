package model.entities;

public class ItenSale 
{
	private Integer id;
	private Double price;
	private Integer quantity;
	private Product product;
	private Sale sale;

	
	public ItenSale()
	{
		
	}
	public ItenSale(Integer id,Double price, Integer quantity, Double subTotal,Product product,Sale sale) 
	{
		super();
		this.id=id;
		this.price = price;
		this.quantity = quantity;
		this.product = product;
		this.sale=sale;
	}

	public Integer getId() 
	{
		return id;
	}
	public void setId(Integer id) 
	{
		this.id = id;
	}

	public Double getPrice()
	{
		return price;
	}
	public void setPrice(Double price) 
	{
		this.price = price;
	}

	public Integer getQuantity() 
	{
		return quantity;
	}
	public void setQuantity(Integer quantity) 
	{
		this.quantity = quantity;
	}
	
	public Product getProduct()
	{
		return product;
	}
	public void setProduct(Product product) 
	{
		this.product = product;
	}
	
	public Sale getSale() 
	{
		return sale;
	}
	public void setSale(Sale sale) 
	{
		this.sale = sale;
	}

	public Double getSubTotal()
	{
		return subTotal(price ,quantity);
	}
	
	public Double subTotal(Double price,Integer quantity)
	{
		return price*quantity;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ItenSale other = (ItenSale) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ItenSale [id=" + id + ", price=" + price + ", quantity=" + quantity + ", product=" + product + ", sale="
				+ sale + "]";
	}
}
