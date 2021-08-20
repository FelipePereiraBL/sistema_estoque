package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import model.entities.CategoryProduct;
import model.exceptions.ValidationException;
import model.services.CategoryProductService;

public class CategoryProductFormController implements Initializable
{
	//Dependencia
	private CategoryProduct entity;
	
	//Dependencia
	private CategoryProductService service;
	
	//Lista de objetos que receberão a notificação(quando os dados forem atualizados)
	private List<DataChangeListeners>dataChangeListeners=new ArrayList<>();
	
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
			service.saveOrUpdate(entity);
			notifyChangeListeners();
			Utils.currentStage(event).close();
		}
		catch(ValidationException e)
		{
			setErrorMessages(e.getErrors());
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
	private CategoryProduct getFormData() 
	{
		CategoryProduct obj=new CategoryProduct();
		
		//Exceção persolazidada
		ValidationException exception=new ValidationException("Validate Exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		//Verifica se o campo nome esta vazio
		if(txtName.getText()==null||txtName.getText().trim().equals(""))
		{
			exception.addErrors("name", "Esse campo não pode ficar vazio");
		}
		
		obj.setName(txtName.getText());
		
		//Se ouver mais de um erro na lista de essro a excessao sera lançada
		if(exception.getErrors().size()>0)
		{
			throw exception;
		}
	
		
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
	
	//Escreve a mensagem de erro na label de ErrorName
	private void setErrorMessages(Map<String,String>errors)
	{
		Set<String>fields=errors.keySet();
		
		if(fields.contains("name"))
		{
			labelErrorName.setText(errors.get("name"));
		}
		
	}
	

}
