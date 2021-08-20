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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.CategoryProduct;
import model.entities.Product;
import model.exceptions.ValidationException;
import model.services.CategoryProductService;
import model.services.ProductService;

public class InventoryFormController implements Initializable
{
	//Dependencia
	private Product entity;
	
	//Dependencia
	private ProductService service;
	
	//Dependencia
	private CategoryProductService categoryService;
	
	//Lista de objetos que receberão a notificação(quando os dados forem atualizados)
	private List<DataChangeListeners>dataChangeListeners=new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtMarca;
	@FXML
	private TextField txtCor;
	@FXML
	private TextField txtCodigo;
	@FXML
	private TextField txtPrecoFabrica;
	@FXML
	private TextField txtPrecoVenda;
	@FXML
	private ComboBox<CategoryProduct> comboBoxCategory;
	
	private ObservableList<CategoryProduct> obsList;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	
	//Injeção da dependencia
	public void setProduct(Product entity)
	{
		this.entity = entity;
	}

	//Injeção da dependencia
	public void setService(ProductService service,CategoryProductService categoryService) 
	{
		this.service = service;
		this.categoryService=categoryService;
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
	private Product getFormData() 
	{
		Product obj=new Product();
		
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
		
		if(txtMarca.getText()==null||txtMarca.getText().trim().equals(""))
		{
			exception.addErrors("brand", "Esse campo não pode ficar vazio");
		}
		obj.setBrand(txtMarca.getText());
		
		if(txtCor.getText()==null||txtCor.getText().trim().equals(""))
		{
			exception.addErrors("color", "Esse campo não pode ficar vazio");
		}
		obj.setColor(txtCor.getText());
		
		obj.setCode(Utils.tryParseToInt(txtCodigo.getText()));
		
		if(txtPrecoFabrica.getText()==null||txtPrecoFabrica.getText().trim().equals(""))
		{
			exception.addErrors("factoryPrice", "Esse campo não pode ficar vazio");
		}
		obj.setFactoryPrice(Utils.tryParseToDouble( txtPrecoFabrica.getText()));
		
		obj.setSalePrice(Utils.tryParseToDouble( txtPrecoVenda.getText()));
		obj.setCategory(comboBoxCategory.getValue());
		
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
		
		initializeComboBoxCategory();
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
		
		if(entity.getCategory()==null)
		{
			comboBoxCategory.getSelectionModel().selectFirst();
		}
		else
		{
			comboBoxCategory.setValue(entity.getCategory());
		}
	}
	
	public void loadAssociateObjects()
	{
		if(categoryService==null)
		{
			throw new IllegalStateException("DepartmentService was null");
		}
		List<CategoryProduct> list=categoryService.findAll();
		
		obsList=FXCollections.observableArrayList(list);
		
		comboBoxCategory.setItems(obsList);
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
	
	private void initializeComboBoxCategory() 
	{
		Callback<ListView<CategoryProduct>, ListCell<CategoryProduct>> factory = lv -> new ListCell<CategoryProduct>() 
		{
			@Override
			protected void updateItem(CategoryProduct item, boolean empty) 
			{
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxCategory.setCellFactory(factory);
		comboBoxCategory.setButtonCell(factory.call(null));
	}

}
