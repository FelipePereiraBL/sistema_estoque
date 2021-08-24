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
	private String code;
	private double factoryPrice;
	private double salePrice;
	
	private CategoryProduct category;

	public Product() 
	{
		
	}

	public Product(Integer id, String name, String brand,Integer quantity, String color, String code, double factoryPrice,
		double salePrice, CategoryProduct category) {
		super();
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.quantity=quantity;
		this.color = color;
		this.code = code;
		this.factoryPrice = factoryPrice;
		this.salePrice = salePrice;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getFactoryPrice() {
		return factoryPrice;
	}

	public void setFactoryPrice(double factoryPrice) {
		this.factoryPrice = factoryPrice;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
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
		return name+"/"+brand+"/"+color+"/R$"+String.format("%.2f", salePrice);
	}

	
}
