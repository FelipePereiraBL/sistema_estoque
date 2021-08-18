package model.dao;

import java.util.List;

import model.entities.CategoryProduct;

public interface CategoryProductDao 
{
	void insert(CategoryProduct obj);
	void update(CategoryProduct obj);
	void deleteById(Integer id);
	CategoryProduct findById(Integer id);
	List<CategoryProduct> findAll();

}
