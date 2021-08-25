package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
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
import model.exceptions.ValidationException;
import model.services.ProductService;
import model.services.SaleService;

public class SaleFormController implements Initializable
{
	//Dependencia
	private Sale entity;
	
	//Objeto produto vazio
	private Product productSale;
	
	//Dependencia
	private ProductService service;
	
	//Dependencia
	private SaleService saleService;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNameCliente;
	@FXML
	private TextField txtEnderecoEntrega;
	@FXML
	private TextField txtNomeProduto;
	@FXML
	private TextField txtProdutoColor;
	@FXML
	private TextField txtProdutoBrand;

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
	@FXML
	private Label labelErrorClientName;
	
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
		
		if(productSale!=null)
		{
			try 
			{
				if(productSale.getQuantity()>0)
				{
					entity=getFormData();
					saleService.saveOrUpdate(entity);
					
					//Atualiza a quantidade do produto no inventario
					productSale.setQuantity(productSale.getQuantity()-1);
					service.saveOrUpdate(productSale);
					
					Utils.currentStage(event).close();
				}
				else
				{
					Alerts.showAlert("Alerta de estoque", "Esse produto está esgotado !", "O produto "+productSale.getName()+" da cor "+productSale.getColor()+" está esgotado !", AlertType.INFORMATION);
				}
					
				
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
		else 
		{
			Alerts.showAlert("Alerta", "Nenhum produto foi escolhido", "Click em Adicionar produto para continuar !", AlertType.INFORMATION);

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
	
	//Pega o produto no inventario a partir do nome digitado
	public void CheckProductSale()
	{
		List<Product>products=service.findAll();
		
		//Produto encontrado ?
		boolean productFound=false,brandFound=false,colorFound=false;
		
		//Conteudo dos alerts
		int productQuantity=0;
		String productName = null;
		
		for (Product product : products)
		{
			if(txtNomeProduto.getText().toString().equals(product.getName().toString()) && productFound==false)
			{
				productFound=true;
				
				if(txtProdutoBrand.getText().toString().equals(product.getBrand().toString()) && brandFound==false)
				{
					brandFound=true;
					
					if(txtProdutoColor.getText().toString().equals(product.getColor().toString())&&colorFound==false)
					{
						colorFound=true;
						
						salePrice=product.getSalePrice();
						productName=product.getName();

						productQuantity=product.getQuantity();
						
						labelProduct.setText(product.toString());
						labelSaleTotal.setText("R$"+salePrice.toString());
						
						//Produto buscado no inventario e injetado no produto vazio para o valor da quaantidade ser atualizado no metodo onBtSaveAction
						productSale=product;
					}
					
				}
			
				
			}
			
			
		}
		if(productQuantity==0 && productFound==true && colorFound==true)
		{
			Alerts.showAlert("Alerta de estoque", "Esse está esgotado !", "O produto "+productSale.getName()+" da cor "+productSale.getColor()+" está esgotado !", AlertType.INFORMATION);
		}
		
		if(productQuantity<=3 && productQuantity>0&& productFound==true)
		{
			Alerts.showAlert("Alerta de estoque", "Esse produto está acabando !", "Após essa venda, terá apenas "+(productQuantity-1)+" "+productName+" da cor "+productSale.getColor()+" no estoque", AlertType.INFORMATION);
		}
		
		if(productFound==false)
		{
			Alerts.showAlert("Produto não encontrado", "Esse produto não está no estoque ou o nome está incorreto !", null, AlertType.INFORMATION);
			txtNomeProduto.setText("");
		}
		
		if(productFound==true&&brandFound==false)
		{
			Alerts.showAlert("Marca não encontrada", "Essa marca não está no estoque ou o nome da marca está incorreto !", null, AlertType.INFORMATION);
			txtProdutoBrand.setText("");
		}
		
		if(productFound==true&&brandFound==true&&colorFound==false)
		{
			Alerts.showAlert("Cor não encontrada", "Essa cor não está no estoque ou o nome da cor está incorreto", null, AlertType.INFORMATION);
			txtProdutoColor.setText("");
		}
	}
	
	private void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtEnderecoEntrega, 50);
		Constraints.setTextFieldMaxLength(txtNameCliente, 30);
		Constraints.setTextFieldMaxLength(txtNomeProduto, 30);
		Constraints.setTextFieldMaxLength(txtProdutoColor, 10);
		Constraints.setTextFieldMaxLength(txtProdutoBrand, 50);
	}
	
	private void setErrorMessages(Map<String,String>errors)
	{
		Set<String>fields=errors.keySet();
		
//		lab.setText(fields.contains("name")?errors.get("name"):"");		
//		labelErrorMarca.setText(fields.contains("brand")?errors.get("brand"):"");	
//		labelErrorQuantidade.setText(fields.contains("quantity")?errors.get("quantity"):"");
//		labelErrorCor.setText(fields.contains("color")?errors.get("color"):"");		
//		labelErrorCode.setText(fields.contains("code")?errors.get("code"):"");
//		labelErrorPrecoFabrica.setText(fields.contains("factoryPrice")?errors.get("factoryPrice"):"");
		
	}

}
