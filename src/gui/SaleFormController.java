package gui;

import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Product;
import model.entities.Sale;
import model.services.ProductService;
import model.services.SaleService;

public class SaleFormController implements Initializable
{
	//Dependencia
	private Sale entity;
	
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
	private Button btListProductSale;
	
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
			 CheckNameProduct();
			entity=getFormData();
			saleService.saveOrUpdate(entity);
			
			notifyChangeListeners();
			Utils.currentStage(event).close();
		}
		
		catch (DbException e) 
		{
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void CheckNameProduct()
	{
		List<Product>products=service.findAll();
		
		for (Product product : products)
		{
			if(txtNomeProduto.getText()==product.getName())
			{
				product.setQuantity(product.getQuantity()-1);
				service.saveOrUpdate(product);
			}
			
		}
	}
	
	public void onBtListProductSale(ActionEvent event)
	{
		loadView("/gui/SaleProducstList.fxml", Utils.currentStage(event));
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
		
		obj.setDeliveryAddress(txtEnderecoEntrega.getText());	
		obj.setProductName(txtNomeProduto.getText());
		
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
		txtNomeProduto.setText(entity.getProductName());
		
	}
	
	private synchronized  <T> void loadView(String absoluteName, Stage parentStage)
	{
		try
		{
		   FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
		   Pane pane=loader.load();
		   
		   
		    Stage dialogStage=new Stage();
			dialogStage.setTitle("");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		   
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Alerts.showAlert("IOExeption", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}


}
