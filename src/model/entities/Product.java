package model.entities;

import java.io.Serializable;

public class Product implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String brand;
	private String color;
	private Integer code;
	private Double factoryPrice;
	private Double salePrice;
	
	private CategoryProduct category;
	
	public  Product () 
	{
		
	}
	public Product(Integer id, String name, String brand, String color, Integer code, Double factoryPrice,	Double salePrice, CategoryProduct category)
	{
		super();
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.color = color;
		this.code = code;
		this.factoryPrice = factoryPrice;
		this.salePrice = salePrice;
		this.category = category;
	}
	
	public Integer getId()
{
		return id;
	}
	public void setId(Integer id) 
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getBrand() 
	{
		return brand;
	}
	public void setBrand(String brand)
	{
		this.brand = brand;
	}
	
	public String getColor() 
	{
		return color;
	}
	public void setColor(String color)
	{
		this.color = color;
	}
	
	public Integer getCode() 
	{
		return code;
	}
	public void setCode(Integer code) 
	{
		this.code = code;
	}
	
	public Double getFactoryPrice() 
	{
		return factoryPrice;
	}
	public void setFactoryPrice(Double factoryPrice) 
	{
		this.factoryPrice = factoryPrice;
	}
	
	public Double getSalePrice() 
	{
		return salePrice;
	}
	public void setSalePrice(Double salePrice)
	{
		this.salePrice = salePrice;
	}
	
	public CategoryProduct getCategory() 
	{
		return category;
	}
	public void setCategory(CategoryProduct category) 
	{
		this.category = category;
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		
		return result;
	}
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Product other = (Product) obj;
		
		if (brand == null)
		{
			if (other.brand != null)
				return false;
		} else if (!brand.equals(other.brand))
			return false;
		
		if (category == null) 
		{
			if (other.category != null)
				return false;
		} 
		else if (!category.equals(other.category))
			return false;
		
		if (code == null)
		{
			if (other.code != null)
				return false;
		} 
		else if (!code.equals(other.code))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} 
		else if (!color.equals(other.color))
			return false;
		
		if (id == null) 
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		
		if (name == null) 
		{
			if (other.name != null)
				return false;
		} 
		else if (!name.equals(other.name))
			return false;
		
		return true;
	}
	
	

}
