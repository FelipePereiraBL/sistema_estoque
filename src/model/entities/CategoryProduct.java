package model.entities;

import java.io.Serializable;

public class CategoryProduct implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	
	public  CategoryProduct()
	{
		
	}
	public CategoryProduct(Integer id, String name) 
	{
		super();
		this.id = id;
		this.name = name;
	}
	
	public int getid() 
	{
		return id;
	}
	public void setid(Integer id) 
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
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		
		CategoryProduct other = (CategoryProduct) obj;
		
		if (id != other.id)
			return false;
		
		return true;
	}
}
