package model.services;

import java.util.List;

import model.dao.CategoryProductDao;
import model.dao.DaoFactory;
import model.entities.CategoryProduct;

public class CategoryProductService 
{
	private CategoryProductDao dao=DaoFactory.createCategoryProductDao();
	
	public List<CategoryProduct> findAll()
	{
		return dao.findAll();
	}
	
}
