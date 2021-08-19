package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.CategoryProduct;
import model.exceptions.ValidationException;
import model.services.CategoryProductService;

public class CategoryProductFormController implements Initializable
{
	//Dependencia
	private CategoryProduct entity;
	
	//Dependencia
	private CategoryProductService service;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	
	//Injeção da dependencia
	public void setCategoryProduct(CategoryProduct entity)
	{
		this.entity = entity;
	}

	//Injeção da dependencia
	public void setService(CategoryProductService service) 
	{
		this.service = service;
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
			service.saveOrUpdate(entity);
			Utils.currentStage(event).close();
		}
		catch (DbException e) 
		{
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	//Pega os dados do formulario
	private CategoryProduct getFormData() 
	{
		CategoryProduct obj=new CategoryProduct();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		
		obj.setName(txtName.getText());
	
		
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
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	//Carrega os dados do entity  nos testFields do formulario
	public void updateFormData()
	{
		if(entity==null)
		{
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	

}
