package gui;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
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
	private TableView<Product> tableViewProducts;
	@FXML
	private TableColumn<Product, Integer > tableColumId;
	@FXML
	private TableColumn<Product, String > tableColumName;
	@FXML
	private TableColumn<Product, Integer > tableColumCategory;
	@FXML
	private TableColumn<Product, Integer > tableColumQiantity;
	@FXML
	private TableColumn<Product, String > tableColumBrand;
	@FXML
	private TableColumn<Product, String > tableColumColor;
	@FXML
	private TableColumn<Product, String > tableColumReference;
	@FXML
	private TableColumn<Product, Double > tableColumSpotCostPrice;
	@FXML
	private TableColumn<Product, Double > tableColumForwardCostPrice;
	@FXML
	private TableColumn<Product, Double > tableColumCashSalePrice;
	@FXML
	private TableColumn<Product, Double > tableColumForwardSellingPrice;
	
	@FXML
	private TableColumn<Product, Product > tableColumnSELL;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtClientName;
	@FXML
	private TextField txtCustomerPhone;
	@FXML
	private TextField txtDeliveryAddress;

	@FXML
	private TextField txtSearchProduct;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private Button btnSearchProduct;
	@FXML
	private Button btnShowAll;
	@FXML
	private Button btnUpdateLabelTotalSale;
	
	private Double salePrice;
	
	@FXML
	private Label labelProduct;
	
	@FXML
	private Label labelSaleTotal;
		
	@FXML
	private ComboBox<String> comboBoxTypeOfSale;
	
	private ObservableList<String> obsList;
	private ObservableList<Product> obsListResultSearch;
	
	public void setServices(ProductService services)
	{
		this.service = services;
	}
	
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
				if(txtClientName.getText().equals("")||txtDeliveryAddress.getText().equals(""))
				{
					Alerts.showAlert("Alerta", "Campos nulos", "Preencher o nome do cliente e o endereço de entrega é obrigatorio !", AlertType.INFORMATION);
				}
				else
				{
					if(txtCustomerPhone.getText().equals(""))
					{
						txtCustomerPhone.setText("0");
						
					}
					if(salePrice==null)
					{
						Alerts.showAlert("Alerta", "Valor da venda nulo", "Selecione se a venda é á vista ou a prazo e click em OK !", AlertType.INFORMATION);
					}
					else
					{
						entity=getFormData();
						saleService.save(entity);
						
						productSale.setQuantity(productSale.getQuantity()-1);
						service.saveOrUpdate(productSale);
						notifyChangeListeners();
						Utils.currentStage(event).close();	
					}					
					
				}
											
			}
			catch (DbException e) 
			{
				Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
			}
		}
		else 
		{
			Alerts.showAlert("Alerta", "Nenhum produto foi escolhido", "Procure pelo produto e click no botão SELECIONAR para continuar !", AlertType.INFORMATION);
		}
	}
	
	public void SearchProducts()
	{
		if(service==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		List<Product>list=service.findAll();
		List<Product>resultSearch=new ArrayList<Product>();
		
		for (Product product : list) 
		{
			String serachParameter= Normalizer.normalize(
					product.getCategory().getName()+", "+ product.getName()+", "+product.getBrand()+", "+product.getColor()+", "+product.getReference()
					,  Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			
			if(serachParameter.toUpperCase().contains(txtSearchProduct.getText().toString().toUpperCase()))
			{
				resultSearch.add(product);
			}
		
			obsListResultSearch = FXCollections.observableArrayList(resultSearch);
			tableViewProducts.setItems(obsListResultSearch);
		 initSELLButton();
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
		obj.setCustomerPhone(Utils.tryParseToInt(txtCustomerPhone.getText()));
		obj.setProduct(productSale);
		
		obj.setDeliveryAddress(txtDeliveryAddress.getText());	
		obj.setSaleValue(Utils.tryParseToDouble(salePrice.toString()));

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
		
		 tableViewProducts.prefHeightProperty().add(200);
         setServices(new ProductService());
		
	}
	
	private void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtClientName, 40);
		Constraints.setTextFieldInteger(txtCustomerPhone);
		Constraints.setTextFieldMaxLength(txtCustomerPhone, 11);
		
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
		tableColumBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		tableColumQiantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumColor.setCellValueFactory(new PropertyValueFactory<>("color"));
		tableColumReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
		tableColumSpotCostPrice.setCellValueFactory(new PropertyValueFactory<>("spotCostPrice"));
		tableColumForwardCostPrice.setCellValueFactory(new PropertyValueFactory<>("forwardCostPrice"));
		tableColumCashSalePrice.setCellValueFactory(new PropertyValueFactory<>("cashSalePrice"));
		tableColumForwardSellingPrice.setCellValueFactory(new PropertyValueFactory<>("forwardSellingPrice"));
	
		initializeComboBoxTypeOfSale();
		
	}
	
	private void initializeComboBoxTypeOfSale()
	{
		List<String>list=Arrays.asList("Á Vista", "A Prazo");
		obsList=FXCollections.observableArrayList(list);
		
		comboBoxTypeOfSale.setItems(obsList);
		
		comboBoxTypeOfSale.getSelectionModel().selectFirst();
		
		Callback<ListView<String>, ListCell<String>> factory = lv -> new ListCell<String>() 
		{
			@Override
			protected void updateItem(String item, boolean empty) 
			{
				super.updateItem(item, empty);
				setText(empty ? "" : item.toString());
			}
		};
		comboBoxTypeOfSale.setCellFactory(factory);
		comboBoxTypeOfSale.setButtonCell(factory.call(null));
		
	}
	
	public void Sell(Product product)
	{	
		if(product.getQuantity()<=3 && product.getQuantity()>0)
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está acabando !", "Após essa venda, terá apenas "+(product.getQuantity()-1)+" "+product.getName()+" da marca "+product.getBrand()+" da cor "+product.getColor()+" no estoque", AlertType.INFORMATION);
		}

		tableViewProducts.setItems(null);
		
		if(product.getQuantity()==0)
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está esgotado !", "O produto "+product.getName()+"da marca "+product.getBrand()+" da cor "+product.getColor()+" está esgotado !", AlertType.INFORMATION);
		}
		else
		{
			productSale=product;
			labelProduct.setText(productSale.toString());
		}
		
	}
	
	public void ShowAll()
	{
		txtSearchProduct.setText("");
		SearchProducts();
	}
	public void UpdateLabelTotalSale()
	{
		if(comboBoxTypeOfSale.getValue().contains("Á Vista"))
		{
			salePrice=productSale.getCashSalePrice();
		}
		else
		{
			salePrice=productSale.getForwardSellingPrice();
		}	
		labelSaleTotal.setText(salePrice.toString());
	}
	
	private void initSELLButton()
	{ 
		tableColumnSELL.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnSELL.setCellFactory(param -> new TableCell<Product, Product>()
		
		{ 
		 private final Button button = new Button("Vender"); 
		 
		 @Override
		 protected void updateItem(Product obj, boolean empty) 
		 { 
		   super.updateItem(obj, empty); 
		 
		   if (obj == null) 
		   { 
		     setGraphic(null); 
		     return; 
		   } 
		   setGraphic(button); 
		 
		   button.setOnAction(event -> Sell(obj));

		  } 
		  }); 
		}

}
