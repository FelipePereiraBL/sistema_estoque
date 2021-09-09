package model.dao;

import java.util.List;

import model.entities.ItenSale;

public interface ItenSaleDao 
{
	void insert(ItenSale obj);
	void deleteById(Integer id);
	List<ItenSale> findAll();

}
