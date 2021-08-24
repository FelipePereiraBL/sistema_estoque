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
import javafx.scene.control.TextField;
import model.entities.Product;
import model.entities.Sale;
import model.services.ProductService;
import model.services.SaleService;

public class SaleFormController implements Initializable
{
	//Dependencia
	private Sale entity;
	
	private Product productSale;
	
	//Dependencia
	private ProductService service;
	
	//Dependencia
	private SaleService saleService;
	
	//Lista de objetos que receberão a notificação(quando os dados forem atualizados)
	private List<DataChangeListeners>dataChangeListeners=new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNameCliente;
	@FXML
	private TextField txtEnderecoEntrega;
	@FXML
	private TextField txtNomeProduto;

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private Button btProductSale;
	
	private Double salePrice;
	
	@FXML
	private javafx.scene.control.Label labelProduct;
	
	//Injeção da dependencia
	public void setSale(Sale entity)
	{
		this.entity = entity;
	}

	//Injeção da dependencia
	public void setService(ProductService service,SaleService saleService) 
	{
		this.service = service;
		this.saleService=saleService;
	}
	
	//Adiciona objetos na lista para receber a notificação 
	public void subscribleChangeListener(DataChangeListeners listener)
	{
		dataChangeListeners.add(listener);
	}

	@FXML
	public  void onBtSaveAction(ActionEvent event)
	{
		//Verifica se as dependencias foram injetadas
		if(entity==null)
		{
			throw new IllegalStateException("Services was null");
		}
		if(service==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		try 
		{
			entity=getFormData();
			saleService.saveOrUpdate(entity);
			
			productSale.setQuantity(productSale.getQuantity()-1);
			service.saveOrUpdate(productSale);
			
			notifyChangeListeners();
			Utils.currentStage(event).close();
		}
		
		catch (DbException e) 
		{
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	//Notifica cada listener para disparar o evento
	private void notifyChangeListeners() 
	{
		for (DataChangeListeners listener : dataChangeListeners)
		{
			listener.onChanged();		
		}
		
	}
	
	//Pega os dados do formulario
 	private Sale getFormData() 
	{
		Sale obj=new Sale();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
	
		obj.setSaleDate(new Date());
		
		obj.setClientName(txtNameCliente.getText());
		obj.setProductName(txtNomeProduto.getText());
		
		obj.setDeliveryAddress(txtEnderecoEntrega.getText());	
		obj.setProductName(txtNomeProduto.getText());
		obj.setTotal(salePrice);

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
		boolean found=false;
		
		//Conteudo dos alerts
		int productQuantity=0;
		String productName = null;
		
		for (Product product : products)
		{
			if(txtNomeProduto.getText().toString().equals(product.getName().toString()) && found==false)
			{
				salePrice=product.getSalePrice();
				productName=product.getName();
				
				found=true;			
				
				productQuantity=product.getQuantity();
				
				labelProduct.setText(product.toString());
				
				productSale=product;
			}
			
			
		}
		
		if(productQuantity<=3)
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está acabando !", "Após essa venda, terá apenas "+(productQuantity-1)+" "+productName+" no estoque", AlertType.INFORMATION);
		}
		
		if(found==false)
		{
			Alerts.showAlert("Produto não encontrado", "Esse produto não está no estoque ou o nome está incorreto !", null, AlertType.INFORMATION);
			txtNomeProduto.setText("");
		}
	}
	
	private void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtEnderecoEntrega, 50);
		Constraints.setTextFieldMaxLength(txtNameCliente, 30);
		Constraints.setTextFieldMaxLength(txtNomeProduto, 30);
	}
	
	//Carrega os dados do entity  nos testFields do formulario
	public void updateFormData()
	{
		if(entity==null)
		{
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtEnderecoEntrega.setText(entity.getDeliveryAddress());
		txtNameCliente.setText(entity.getClientName());
		
	}


}
