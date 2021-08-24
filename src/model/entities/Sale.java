package model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sale implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date saleDate;
	private String clientName;
	private String deliveryAddress;
	private Double total;
	
	private List<SaleItens> saleItens=new ArrayList<SaleItens>();
	
	public  Sale ()
	{
		
	}

	public Sale(Integer id, Date saleDate, String clientName, String deliveryAddress, Double total)
	{
		super();
		this.id = id;
		this.saleDate = saleDate;
		this.clientName = clientName;
		this.deliveryAddress = deliveryAddress;
		this.total=total;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public List<SaleItens> getSaleItens() {
		return saleItens;
	}

	public void setSaleItens(List<SaleItens> saleItens) {
		this.saleItens = saleItens;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total)
	{
		this.total = totalPrice();
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
		Sale other = (Sale) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	public Double totalPrice()
	{
		Double sum=0.0;
		
		for (SaleItens item : saleItens) 
		{
			sum=item.subTotal();
			
		}
		return sum;
	}

}
