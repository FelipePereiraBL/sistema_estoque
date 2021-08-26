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
	private Product entity;
	
	private ProductService service;
	
	private CategoryProductService categoryService;
	
	private List<DataChangeListeners>dataChangeListeners=new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtBrand;
	@FXML
	private TextField txtQuantity;
	@FXML
	private TextField txtColor;
	@FXML
	private TextField txtReference;
	@FXML
	private TextField txtSpotCostPrice;
	@FXML
	private TextField txtForwardCostPrice;
	@FXML
	private TextField txtCashSalePrice;
	@FXML
	private TextField txtForwardSellingPrice;

	@FXML
	private ComboBox<CategoryProduct> comboBoxCategory;
	
	private ObservableList<CategoryProduct> obsList;
	
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorBrand;
	@FXML
	private Label labelErrorQuantity;
	@FXML
	private Label labelErrorColor;
	@FXML
	private Label labelErrorReference;
	@FXML
	private Label labelErrorSpotCostPrice;
	@FXML
	private Label labelErrorCashSalePrice;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	
	public void setProduct(Product entity)
	{
		this.entity = entity;
	}

	public void setService(ProductService service,CategoryProductService categoryService) 
	{
		this.service = service;
		this.categoryService=categoryService;
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
	
	private void notifyChangeListeners() 
	{
		for (DataChangeListeners listener : dataChangeListeners)
		{
			listener.onChanged();		
		}
		
	}
	
 	private Product getFormData() 
	{
		Product obj=new Product();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		ValidationException exception=new ValidationException("Validate Exception");

		if(txtName.getText()==null||txtName.getText().trim().equals(""))
		{
			exception.addErrors("name", "Esse campo não pode ficar vazio");
		}		
		obj.setName(txtName.getText());
		
		
		if(txtBrand.getText()==null||txtBrand.getText().trim().equals(""))
		{
			exception.addErrors("brand", "Esse campo não pode ficar vazio");
		}
		obj.setBrand(txtBrand.getText());
		
		
		if(txtQuantity.getText()==null||txtQuantity.getText().trim().equals(""))
		{
			exception.addErrors("quantity", "Esse campo não pode ficar vazio");
		}
		obj.setQuantity(Utils.tryParseToInt(txtQuantity.getText()));
		
		
		if(txtColor.getText()==null||txtColor.getText().trim().equals(""))
		{
			exception.addErrors("color", "Esse campo não pode ficar vazio");
		}
		obj.setColor(txtColor.getText());
		
		
		if(txtReference.getText()==null||txtReference.getText().trim().equals(""))
		{
			exception.addErrors("reference", "Esse campo não pode ficar vazio");
		}
		obj.setReference(txtReference.getText());
		
		
		if(txtSpotCostPrice.getText()==null||txtSpotCostPrice.getText().trim().equals(""))
		{
			exception.addErrors("spotCostPrice", "Esse campo não pode ficar vazio");
		}
		obj.setSpotCostPrice(Utils.tryParseToDouble(txtSpotCostPrice.getText()));
		
		
		obj.setForwardCostPrice(Utils.tryParseToDouble( txtForwardCostPrice.getText()));
		
		
		if(txtCashSalePrice.getText()==null||txtCashSalePrice.getText().trim().equals(""))
		{
			exception.addErrors("cashSalePrice", "Esse campo não pode ficar vazio");
		}	
		obj.setCashSalePrice(Utils.tryParseToDouble( txtCashSalePrice.getText()));
		
		
		obj.setForwardSellingPrice(Utils.tryParseToDouble(txtForwardSellingPrice.getText()));
		
		
		obj.setCategory(comboBoxCategory.getValue());		

		
		if (exception.getErrors().size() > 0) 
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
		Constraints.setTextFieldMaxLength(txtBrand, 20);
		Constraints.setTextFieldInteger(txtQuantity);
		Constraints.setTextFieldMaxLength(txtColor, 20);
		Constraints.setTextFieldMaxLength(txtReference, 20);
		Constraints.setTextFieldDouble(txtSpotCostPrice);
		Constraints.setTextFieldDouble(txtForwardCostPrice);
		Constraints.setTextFieldDouble(txtCashSalePrice);
		Constraints.setTextFieldDouble(txtForwardSellingPrice);
		
		initializeComboBoxCategory();
	}
	
	public void updateFormData()
	{
		if(entity==null)
		{
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtBrand.setText(entity.getBrand());
		txtQuantity.setText(String.valueOf(entity.getQuantity()));
		txtColor.setText(entity.getColor());
		txtReference.setText((entity.getReference()));
		txtSpotCostPrice.setText(entity.getSpotCostPrice().toString());
		txtForwardCostPrice.setText(entity.getForwardCostPrice().toString());
		txtCashSalePrice.setText(entity.getCashSalePrice().toString());
		txtForwardSellingPrice.setText(entity.getForwardSellingPrice().toString());
		
		
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
			throw new IllegalStateException("CategoryProductService was null");
		}
		List<CategoryProduct> list=categoryService.findAll();
		
		obsList=FXCollections.observableArrayList(list);
		
		comboBoxCategory.setItems(obsList);
	}
	
	private void setErrorMessages(Map<String,String>errors)
	{
		Set<String>fields=errors.keySet();
		
		labelErrorName.setText(fields.contains("name")?errors.get("name"):"");		
		labelErrorBrand.setText(fields.contains("brand")?errors.get("brand"):"");	
		labelErrorQuantity.setText(fields.contains("quantity")?errors.get("quantity"):"");
		labelErrorColor.setText(fields.contains("color")?errors.get("color"):"");		
		labelErrorReference.setText(fields.contains("reference")?errors.get("reference"):"");
		labelErrorSpotCostPrice.setText(fields.contains("spotCostPrice")?errors.get("spotCostPrice"):"");
		labelErrorCashSalePrice.setText(fields.contains("cashSalePrice")?errors.get("cashSalePrice"):"");
		
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
