package model.entities;

import java.io.Serializable;

public class Product implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String brand;
	private Integer quantity;
	private String color;
	private String reference;
	private double spotCostPrice;
	private double forwardCostPrice;
	private double cashSalePrice;
	private double forwardSellingPrice;
	
	private CategoryProduct category;

	public Product() 
	{
		
	}

	

	public Product(Integer id, String name, String brand, Integer quantity, String color, String reference,
			Double spotCostPrice, Double forwardCostPrice, Double cashSalePrice, Double forwardSellingPrice,
			CategoryProduct category) 
	{
		super();
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.quantity = quantity;
		this.color = color;
		this.reference = reference;
		this.spotCostPrice = spotCostPrice;
		this.forwardCostPrice = forwardCostPrice;
		this.cashSalePrice = cashSalePrice;
		this.forwardSellingPrice = forwardSellingPrice;
		this.category = category;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public Double getSpotCostPrice() {
		return spotCostPrice;
	}

	public void setSpotCostPrice(Double spotCostPrice) {
		this.spotCostPrice = spotCostPrice;
	}

	public Double getForwardCostPrice() {
		return forwardCostPrice;
	}

	public void setForwardCostPrice(Double forwardCostPrice) {
		this.forwardCostPrice = forwardCostPrice;
	}

	public Double getCashSalePrice() {
		return cashSalePrice;
	}

	public void setCashSalePrice(Double cashSalePrice) {
		this.cashSalePrice = cashSalePrice;
	}

	public Double getForwardSellingPrice() {
		return forwardSellingPrice;
	}

	public void setForwardSellingPrice(Double forwardSellingPrice) {
		this.forwardSellingPrice = forwardSellingPrice;
	}

	public CategoryProduct getCategory() {
		return category;
	}

	public void setCategory(CategoryProduct category) {
		this.category = category;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name+"/"+brand+"/"+color;
	}

	
}
