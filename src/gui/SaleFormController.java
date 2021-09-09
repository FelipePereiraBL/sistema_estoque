package gui;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import db.DbIntegrityException;
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
import model.entities.ItenSale;
import model.entities.Product;
import model.entities.Sale;
import model.services.ItenSaleService;
import model.services.ProductService;
import model.services.SaleService;

public class SaleFormController implements Initializable
{
	//Lista de classes que implementam DataChangeListeners e serão notificadas quando um determinado evento for disparado
	private List<DataChangeListeners>dataChangeListeners=new ArrayList<>();
	
	private Sale entity;
	private Product product;
	private ItenSale itenSale;
	
	List<ItenSale>listItenSale=new ArrayList<>();
	
	private ProductService productService;
	private SaleService saleService;
	private ItenSaleService itenSaleService ;
	
	//TableView de busca de produtos
	@FXML
	private TableView<Product> tableViewProducts;
	@FXML
	private TableColumn<Product, Integer > tableColumId;
	@FXML
	private TableColumn<Product, String > tableColumName;
	@FXML
	private TableColumn<Product, Integer > tableColumCategory;
	@FXML
	private TableColumn<Product, Integer > tableColumQuantity;
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
	private Label labelSaleTotal;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private Button btnSearchProduct;
	@FXML
	private Button btnShowAll;
	@FXML
	private Button btnTypeOfSale;

	//TableView de Itens da venda
	@FXML
	private TableView<ItenSale> tableViewItenSale;
	@FXML
	private TableColumn<ItenSale, Double > tableColumPrice;
	@FXML
	private TableColumn<ItenSale, Integer > tableColumQuantityItenSale;
	@FXML
	private TableColumn<ItenSale, Double > tableColumSubTotal;
	@FXML
	private TableColumn<ItenSale, String > tableColumProduct;	
	@FXML
	private TableColumn<ItenSale, ItenSale > tableColumnREMOVEPRODUCT;
		
	@FXML
	private ComboBox<String> comboBoxTypeOfSale;
	
	private ObservableList<String> obsList;
	private ObservableList<Product> obsListResultSearch;
	private ObservableList<ItenSale> obsListItenSale;
	
	private Double salePrice=0.0;
	
	//Quantidade de produtoas adicionados em determinadoiten de venda
	private Integer quantitySelected = 0;
	
	//Injeçoes de dependencia
	public void setSale(Sale entity)
	{
		this.entity = entity;
	}
	public void setItenSaleService(ItenSaleService itenSaleService)
	{
		this.itenSaleService=itenSaleService;
	}
	public void setService(ProductService productService,SaleService saleService) 
	{
		this.productService = productService;
		this.saleService=saleService;		
	}
	
	//Adicina uma classe que implementa DataChangeListeners a lista para receber a notificação quando um determinado evento for disparado
	public void subscribleChangeListener(DataChangeListeners listener)
	{
		dataChangeListeners.add(listener);
	}

	//Salvar venda
	@FXML
	public  void onBtSaveAction(ActionEvent event)
	{
		
		if(entity==null)
		{
			throw new IllegalStateException("Entity was null");
		}
		if(saleService==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		if(itenSale!=null)
		{
			try 
			{
				if(txtClientName.getText().equals("")||txtDeliveryAddress.getText().equals(""))
				{
					Alerts.showAlert("Alerta", "Campos nulos", "Preencher o nome do cliente e o endereço de entrega é obrigatorio !", AlertType.INFORMATION);
				}
				else
				{
					entity = getFormData();
					saleService.save(entity);
					
					SaveItenSale(entity);
					
					RemoveProductInventory();
					
					//Notifica cada classe da lista  quando o evento do metodo 'onBtSaveAction' for disparado 
					notifyChangeListeners();
					Utils.currentStage(event).close();

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

	//Salva os itens de venda
	private void SaveItenSale(Sale obj)
	{
		for (ItenSale iten : listItenSale) 
		{
			iten.setSale(obj);
			
			itenSaleService.saveOrUpdate(iten);
		}
	}
	
	//Procurar produtos especificos no estoque
	public void SearchProducts()
	{
		if(productService==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		List<Product>list=productService.findAll();
		List<Product>resultSearch=new ArrayList<Product>();
		
		for (Product product : list) 
		{
			String serachParameter= Normalizer.normalize(
					product.getCategory().getName()+", "+ product.getName()+", "+product.getBrand()+", "+product.getColor()+", "+product.getReference()
					,  Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			
			if (serachParameter.toUpperCase().contains(txtSearchProduct.getText().toString().toUpperCase())&&product.getQuantity()>0)
					resultSearch.add(product);
		
			obsListResultSearch = FXCollections.observableArrayList(resultSearch);
			tableViewProducts.setItems(obsListResultSearch);
			initSELLButton();
		}
	}
	
	// Mostra todos os produtos no estoque
	public void ShowAll()
	{
		txtSearchProduct.setText("");
		SearchProducts();
	}
	
	//Ação realizada ao receber a notificação implementada em cada classe da lista
	private void notifyChangeListeners() 
	{
		for (DataChangeListeners listener : dataChangeListeners)
		{
			listener.onChanged();		
		}		
	}
	
	//Pega os dados do formulario e passa para o objeto venda
 	private Sale getFormData() 
	{
		Sale obj=new Sale();		
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
	
		obj.setSaleDate(new Date());
				
		obj.setClientName(txtClientName.getText());
		
	    obj.setCustomerPhone(txtCustomerPhone.getText());
			
		obj.setTypeOfSale(comboBoxTypeOfSale.getValue());
		
		obj.setSaleValue(salePrice);
		
		obj.setDeliveryAddress(txtDeliveryAddress.getText());	

		return obj;
	}
 	
 	//Criar novos itens de venda com os produtos selecionados
 	private void CreateItenSale(Product obj)
 	{		
		itenSale = new ItenSale();
		
		itenSale.setId(Utils.tryParseToInt(txtId.getText()));

		//Pega o valor com o qual o produto está sendo vendido
		if (comboBoxTypeOfSale.getValue().contains("Á Vista")) 
		{
			itenSale.setPrice(obj.getCashSalePrice());
		} 
		else
		{
			itenSale.setPrice(obj.getForwardSellingPrice());
		}
		
		itenSale.setProduct(obj);


		//Se o iten de venda com o produto passado como argumento foi encontrado na liste de itens de venda
 		boolean itenFound = false;
 		
 		//Se já existir um iten de vendo com esse produto, vai adicionar mais um a quantidade
 		for (ItenSale iten : listItenSale) 
 		{
 			if( itenFound==false&&  iten.getProduct()==itenSale.getProduct() && iten.getQuantity()<obj.getQuantity())
 			{
 				itenFound=true;

 				iten.setQuantity(iten.getQuantity()+1);
 				tableViewItenSale.refresh();
 			}		
 		
		}	
 		
 		itenSale.setQuantity(1);

 			if(itenFound!=true )
 	 		{
 	 			listItenSale.add(itenSale);
 	 		}	
 						
 			obsListItenSale = FXCollections.observableArrayList(listItenSale);
 			tableViewItenSale.setItems(obsListItenSale);
 			
 			//Soma o subTotal de cada item de pedido e passa para o valor totat da venda 
 			double sum =0;
 			for (ItenSale iten : listItenSale) 
 			{
 				sum += iten.getSubTotal();
 			}
 			
 			salePrice=sum;
 		    labelSaleTotal.setText(Double.toString(sum));					
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
		setService(new ProductService() ,new SaleService());
		setItenSaleService(new ItenSaleService());
		
	}

	private void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtClientName, 40);
		Constraints.setTextFieldMaxLength(txtCustomerPhone,14);

		
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
		tableColumBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		tableColumQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumColor.setCellValueFactory(new PropertyValueFactory<>("color"));
		tableColumReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
		tableColumSpotCostPrice.setCellValueFactory(new PropertyValueFactory<>("spotCostPrice"));
		tableColumForwardCostPrice.setCellValueFactory(new PropertyValueFactory<>("forwardCostPrice"));
		tableColumCashSalePrice.setCellValueFactory(new PropertyValueFactory<>("cashSalePrice"));
		tableColumForwardSellingPrice.setCellValueFactory(new PropertyValueFactory<>("forwardSellingPrice"));
		
		tableColumPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		tableColumQuantityItenSale.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
		tableColumSubTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
	
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
	
	//Selecionar produtos para venda
	public void SelectProductSale(Product product)
	{  
		//A quantidade de produtos selecionados para venda começa como 0
		quantitySelected =0;
	
		//Variavel auxiliar para saber se um iten de pedido foi ou não encontrado
		boolean foundIten = false; 
		
		//Verifica se já existe um iten de venda para o produto selecionado
		for (ItenSale iten : listItenSale)
		{
			if(foundIten==false &&iten.getProduct()==product)
			{
				foundIten=true;
				itenSale=iten;
			}		
		}
		
		//Se já existe um iten de venda para esse produto significa que estamos adicionando mais um do mesmo produto no iten de venda e a quantidade de produtos no iten vai ser igual a ela mesma + 1
		if(foundIten==true)
		{
			quantitySelected=itenSale.getQuantity()+1;
			foundIten = false;
		}
		////Se nao existe um iten de venda para esse produto significa que estamos adicionando outro produto e a quantidade de produtos no iten de venda vai receber + 1
		else
		{
			quantitySelected += 1;
		}		
		
		//Quando houver menos de 3 unidades do produto selecionado no estoque 
		if(product.getQuantity()<=3 && quantitySelected<=product.getQuantity())
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está acabando !", "Após essa venda, terá apenas "+(product.getQuantity()-quantitySelected)+" "+product.getName()+" da marca "+product.getBrand()+" da cor "+product.getColor()+" no estoque", AlertType.INFORMATION);
		}
		
		//Quando todas as unidades do produto disponives no estoque forem adicionadas ao iten de venda
		if(quantitySelected>product.getQuantity())
		{
			Alerts.showAlert("Alerta de estoque", "Não disponivel no estoque !", "Voçê já adicionou "+(quantitySelected-1)+" "+product.getName()+" da marca "+product.getBrand()+" da cor "+product.getColor()+" à essa venda !", AlertType.INFORMATION);
			quantitySelected=product.getQuantity();
		}
		else
		{
			 CreateItenSale(product);
				
			 initRemoveButtons();
		}
		
	}
	
	//Remover produtos vendidos do inventario
	private void RemoveProductInventory()
	{
		
		for(ItenSale iten:listItenSale)
		{
			product=iten.getProduct();
			product.setQuantity(product.getQuantity()-iten.getQuantity());
			
			productService.saveOrUpdate(product);
		}		
	}
	
	//Remove produtos do iten de venda 
	private void RemoveProductItensale(ItenSale obj)
	{
		obj.setQuantity(obj.getQuantity()-1);
		
		tableViewItenSale.refresh();
		
		double sum =0;
		for (ItenSale iten : listItenSale) 
		{
			sum += iten.getSubTotal();
		}
		
		salePrice=sum;
	    labelSaleTotal.setText(Double.toString(sum));
	    quantitySelected-=1;
	    //Remove o produto da lista de itens de venda quando a quantidade for 0
		if(obj.getQuantity()==0)
		{
			removeEntity(obj);
		}
		
	}
	
	//Ativa os botoes para mostrar a lista de produtos apos decidir se a venda é a vista ou a prazo
	public void TypeOfSale()
	{
		btnSearchProduct.setDisable(false);
		btnShowAll.setDisable(false);
		comboBoxTypeOfSale.setDisable(true);
	}
	
	//Inserir botao de selecionar em cada produto mostrado na lista
	private void initSELLButton()
	{ 
		tableColumnSELL.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnSELL.setCellFactory(param -> new TableCell<Product, Product>()
		
		{ 
		 private final Button button = new Button("Selecionar"); 
		 
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
		 
		   button.setOnAction(event -> SelectProductSale(obj));

		  } 
		  }); 
		}
		
	//Inserir botao de Remover iten em cada iten de pedido mostrado na lista 
	private void initRemoveButtons() 
	{ 
		tableColumnREMOVEPRODUCT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVEPRODUCT.setCellFactory(param -> new TableCell<ItenSale, ItenSale>() 
		{ 
		 private final Button button = new Button("Remover"); 
		 @Override
		 protected void updateItem(ItenSale obj, boolean empty) 
		 { 
		 super.updateItem(obj, empty); 
		 if (obj == null) 
		 { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction(event -> RemoveProductItensale(obj)); 
		 } 
		 }); 
		}

	//Remove o Iten de pedido da lista
	private void removeEntity(ItenSale obj) 

	{		
			if(itenSaleService==null)
			{
				throw new IllegalStateException("Service was null");
			}
			
			try
			{
				listItenSale.remove(obj);
				
				obsListItenSale = FXCollections.observableArrayList(listItenSale);
				tableViewItenSale.setItems(obsListItenSale);				
				
			} 
			catch (DbIntegrityException e)
			{
				Alerts.showAlert("Erro ao remover produto", null, e.getMessage(), AlertType.ERROR);
			}		
	}
		
}
