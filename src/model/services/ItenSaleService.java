package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ItenSaleDao;
import model.entities.ItenSale;

public class ItenSaleService 
{
	private ItenSaleDao dao=DaoFactory.createItenSaleDao();
	
	public List<ItenSale> findAll()
	{
		return dao.findAll();
	}
	
	public void saveOrUpdate(ItenSale obj)
	{
		if(obj.getId()==null)
		{
			dao.insert(obj);
		}
	}
	
	public void remove(ItenSale obj)
	{
		dao.deleteById(obj.getId());
	}

}
