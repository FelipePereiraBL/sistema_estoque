package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SaleDao;
import model.entities.Sale;

public class SaleService 
{
	private SaleDao dao=DaoFactory.createSaleDao();
	
	public List<Sale> findAll()
	{
		return dao.findAll();
	}
	
	public void save(Sale obj)
	{
		if(obj.getId()==null)
		{
			dao.insert(obj);
		}
	}
	
	public void remove(Sale obj)
	{
		dao.deleteById(obj.getId());
	}
	
}
