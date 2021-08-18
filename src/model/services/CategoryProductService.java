package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.CategoryProduct;

public class CategoryProductService 
{
	public List<CategoryProduct> findAll()
	{
		// Dados mockados
		List<CategoryProduct> list = new ArrayList<>();
		list.add(new CategoryProduct(1, "Guarda Roupa-3 Portas"));
		list.add(new CategoryProduct(2, "Guarda Roupa-4 Portas"));
		list.add(new CategoryProduct(3, "Guarda Roupa-6 Portas"));
		list.add(new CategoryProduct(4, "Guarda Roupa-8 Portas"));

		return list;
	}
	
}
