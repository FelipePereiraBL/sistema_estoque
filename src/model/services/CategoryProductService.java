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
	
	public void saveOrUpdate(CategoryProduct obj)
	{
		if(obj.getId()==null)
		{
			dao.insert(obj);
		}
		else
		{
			dao.update(obj);
		}
	}
	
	public void remove(CategoryProduct obj)
	{
		dao.deleteById(obj.getId());
	}
	
}
