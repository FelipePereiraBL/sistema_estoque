package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Sale implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date saleDate;
	private String clientName;
	private String customerPhone;
	private String deliveryAddress;
	private String typeOfSale;
	private Double saleValue;
	
	public  Sale ()
	{
		
	}
	public Sale(Integer id, Date saleDate, String clientName, String customerPhone, String deliveryAddress, String typeOfSale,Double saleValue) 
	{
		super();
		this.id = id;
		this.saleDate = saleDate;
		this.clientName = clientName;
		this.customerPhone = customerPhone;
		this.deliveryAddress = deliveryAddress;
		this.typeOfSale=typeOfSale;
		this.saleValue = saleValue;
	}
	
	public Integer getId() 
	{
		return id;
	}
	public void setId(Integer id) 
	{
		this.id = id;
	}
	
	public Date getSaleDate() 
	{
		return saleDate;
	}
	public void setSaleDate(Date saleDate) 
	{
		this.saleDate = saleDate;
	}
	
	public String getClientName()
	{
		return clientName;
	}
	public void setClientName(String clientName) 
	{
		this.clientName = clientName;
	}
	
	public String getCustomerPhone()
	{
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone)
	{
		this.customerPhone = customerPhone;
	}
	
	public String getDeliveryAddress()
	{
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) 
	{
		this.deliveryAddress = deliveryAddress;
	}
	
	public String getTypeOfSale()
	{
		return typeOfSale;
	}
	public void setTypeOfSale(String typeOfSale) 
	{
		this.typeOfSale = typeOfSale;
	}
	
	public Double getSaleValue()
	{
		return saleValue;
	}
	public void setSaleValue(Double saleValue) 
	{
		this.saleValue = saleValue;
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
	@Override
	public String toString() {
		return "Sale [id=" + id + ", saleDate=" + saleDate + ", clientName=" + clientName + ", customerPhone="
				+ customerPhone + ", deliveryAddress=" + deliveryAddress + ", saleValue=" + saleValue + "]";
	}
	

}
