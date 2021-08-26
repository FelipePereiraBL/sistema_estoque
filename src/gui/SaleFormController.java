package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Product;
import model.entities.Sale;
import model.services.ProductService;
import model.services.SaleService;

public class SaleFormController implements Initializable
{
	private List<DataChangeListeners>dataChangeListeners=new ArrayList<>();
	
	private Sale entity;
	
	private Product productSale;
	
	private ProductService service;

	private SaleService saleService;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtClientName;
	@FXML
	private TextField txtCustomerPhone;
	@FXML
	private TextField txtDeliveryAddress;
	@FXML
	private TextField txtProductName;
	@FXML
	private TextField txtProductColor;
	@FXML
	private TextField txtProductBrand;

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private Button btProductSale;
	
	private Double salePrice;
	
	@FXML
	private Label labelProduct;
	@FXML
	private Label labelSaleTotal;
	
	public void setSale(Sale entity)
	{
		this.entity = entity;
	}

	public void setService(ProductService service,SaleService saleService) 
	{
		this.service = service;
		this.saleService=saleService;
	}
	
	public void subscribleChangeListener(DataChangeListeners listener)
	{
		dataChangeListeners.add(listener);
	}

	@FXML
	public  void onBtSaveAction(ActionEvent event)
	{
		
		if(entity==null)
		{
			throw new IllegalStateException("Entity was null");
		}
		if(service==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		if(productSale!=null)
		{
			try 
			{
				if(productSale.getQuantity()>0)
				{
					entity=getFormData();
					saleService.save(entity);
					
					productSale.setQuantity(productSale.getQuantity()-1);
					service.saveOrUpdate(productSale);
					notifyChangeListeners();
					Utils.currentStage(event).close();
				}
				else
				{
					Alerts.showAlert("Alerta de estoque", "Esse produto está esgotado !", "O produto "+productSale.getName()+" da cor "+productSale.getColor()+" está esgotado !", AlertType.INFORMATION);
				}
									
			}
			catch (DbException e) 
			{
				Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
			}
		}
		else 
		{
			Alerts.showAlert("Alerta", "Nenhum produto foi escolhido", "Click em ESCOLHER PRODUTO produto para continuar !", AlertType.INFORMATION);
		}
	}
		
	private void notifyChangeListeners() 
	{
		for (DataChangeListeners listener : dataChangeListeners)
		{
			listener.onChanged();		
		}		
	}
		
	
 	private Sale getFormData() 
	{
		Sale obj=new Sale();
		
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
	
		obj.setSaleDate(new Date());
				
		obj.setClientName(txtClientName.getText());
		obj.setCustomerPhone(txtCustomerPhone.getText());
		
		obj.setSaleProductDescription(productSale.toString());
		
		obj.setDeliveryAddress(txtDeliveryAddress.getText());	
		obj.setSaleValue(salePrice);

		return obj;
	}	
	
	@FXML
	public  void onBtCancelAction(ActionEvent event)
	{
		Utils.currentStage(event).close();
		
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		initializeNodes();
		
	}
	
	public void CheckProductSale()
	{
		List<Product>products=service.findAll();
		
		boolean productFound=false,brandFound=false,colorFound=false;
		
		int productQuantity=0;
		String productName = null,productColor=null,productBrand=null;
		
		for (Product product : products)
		{
			if(txtProductName.getText().toString().equals(product.getName().toString()) && productFound==false)
			{
				productFound=true;
				
				if(txtProductBrand.getText().toString().equals(product.getBrand().toString()) && brandFound==false)
				{
					brandFound=true;
					
					if(txtProductColor.getText().toString().equals(product.getColor().toString())&&colorFound==false)
					{
						colorFound=true;
						
						salePrice=product.getCashSalePrice();
						
						productName=product.getName();
						productQuantity=product.getQuantity();
						productColor=product.getColor();
						productBrand=product.getBrand();
						
						if( txtClientName.getText().trim().equals("") || txtDeliveryAddress.getText().trim().equals(""))
						{
							Alerts.showAlert("Campos nulos", "", "Verifique se todos os campos foram preenchidos e click em Adicionar produto para continuar !", AlertType.INFORMATION);
						}
						else
						{
							productSale=product;
							
							if(productQuantity>0)
							labelProduct.setText(product.toString());
							labelSaleTotal.setText("R$"+salePrice.toString());
						}
						
					
					}					
				}							
			}						
		}
		if(productFound==true && colorFound==true&&brandFound==true&&productQuantity==0)
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está esgotado !", "O produto "+productName+" da marca "+productBrand+" da cor "+productColor+" está esgotado !", AlertType.INFORMATION);
		}
		
		if(productQuantity<=3 && productQuantity>0&& productFound==true)
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está acabando !", "Após essa venda, terá apenas "+(productQuantity-1)+" "+productName+" da marca "+productBrand+" da cor "+productColor+" no estoque", AlertType.INFORMATION);
		}
		
		if(productFound==false)
		{
			Alerts.showAlert("Produto não encontrado", "Esse produto não está no estoque ou o nome do produto está incorreto ou nulo!", null, AlertType.INFORMATION);
			txtClientName.setText("");
		}
		
		if(productFound==true&&brandFound==false)
		{
			Alerts.showAlert("Marca não encontrada", "Essa marca não está no estoque ou o campo da marca está incorreto ou nulo!", null, AlertType.INFORMATION);
			txtProductBrand.setText("");
		}
		
		if(productFound==true&&brandFound==true&&colorFound==false)
		{
			Alerts.showAlert("Cor não encontrada", "Essa cor não está no estoque ou o campo da cor está incorreto ou nulo", null, AlertType.INFORMATION);
			txtProductColor.setText("");
		}
	}
	
	private void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtProductBrand, 50);
		Constraints.setTextFieldMaxLength(txtClientName, 40);
		Constraints.setTextFieldMaxLength(txtProductName, 30);
		Constraints.setTextFieldMaxLength(txtProductColor, 20);
		Constraints.setTextFieldMaxLength(txtProductBrand, 50);
		Constraints.setTextFieldMaxLength(txtCustomerPhone, 13);
	}
}
