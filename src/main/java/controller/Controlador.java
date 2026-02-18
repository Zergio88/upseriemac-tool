package controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import modelo.Notebook;
import services.FirestoreHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class Controlador {

		@FXML private ComboBox<String> consultaComboBoxNotebooks; // Para el ComboBox de consultas
		
		@FXML private ComboBox<String> usuarioComboBoxNotebooks; // 
		private final Map<String, Integer> usuarioMap = new HashMap<>();
		
		
		@FXML private Label usuarioLabel; // Label de usuario
		@FXML private TextField usuarioFieldConsulta; // TextField de usuario
		@FXML private Label fechaLabel; // Label de fecha
		@FXML private TextField fechaFieldConsulta; // TextField de fecha
		@FXML private TextField dayFieldNotebooks;
		@FXML private TextField monthFieldNotebooks;
		@FXML private TextField yearFieldNotebooks;		
		@FXML private Label serieLabel; // Label de serie
		@FXML private TextField serieFieldConsultaNotebooks; // TextField de serie
		@FXML private HBox usuarioBoxNotebooks;
		@FXML private HBox fechaBoxNotebooks;
		@FXML private HBox serieBoxNotebooks;
		

				
		@FXML private TextField serieFieldABM; // Para alta, baja y modificación
		@FXML private TextField macAddressFieldABM; // Para alta y modificación
		@FXML private TextField usuarioFieldABM; // Para alta y modificación
		@FXML private TextField fechaFieldABM; // Para alta y modificación

		@FXML private TableView<Notebook> resultTableNotebooks; // Tabla para mostrar resultados
	
		@FXML private TableColumn<Notebook, Integer> ordenColumnNotebooks; // Columna para el orden
		@FXML private TableColumn<Notebook, String> serieColumnNotebooks; // Columna para serie
		@FXML private TableColumn<Notebook, String> macAddressColumnNotebooks; // Columna para macaddress
		@FXML private TableColumn<Notebook, Integer> usuarioColumnNotebooks; // Columna para usuario
		@FXML private TableColumn<Notebook, String> fechaColumnNotebooks; // Columna para fecha
		
		
		@FXML private ImageView imageView_importarNotebooks, imageView_exportarNotebooks; // Asegúrate de que tienes un ImageView en tu FXML
		
		
		 FirestoreHandler dbHandler = new FirestoreHandler();


		 // Inicialización
	    @FXML
	    public void initialize() {
	        // Configuración de las columnas de la tabla
	    	ordenColumnNotebooks.setCellValueFactory(new PropertyValueFactory<>("orden")); // Columna de orden
	    	serieColumnNotebooks.setCellValueFactory(new PropertyValueFactory<>("serie"));
	    	macAddressColumnNotebooks.setCellValueFactory(new PropertyValueFactory<>("macaddress"));
	        usuarioColumnNotebooks.setCellValueFactory(new PropertyValueFactory<>("usuario"));
	        fechaColumnNotebooks.setCellValueFactory(new PropertyValueFactory<>("fecha"));
	     // Configurar el ComboBox
	        ObservableList<String> options = FXCollections.observableArrayList(
	                "Por usuario",
	                "Por fecha",
	                "Por serie",
	                "Todos"
	        );
	        
	        usuarioMap.put("Aaron", 1);
	        usuarioMap.put("Augusto", 2);
	        usuarioMap.put("Diego", 3);
	        usuarioMap.put("Federico F", 4);
	        usuarioMap.put("Federico P", 5);
	        usuarioMap.put("Guillermo", 6);
	        usuarioMap.put("Julio", 7);
	        usuarioMap.put("Lucas", 8);
	        usuarioMap.put("Melga", 9);
	        usuarioMap.put("Melina", 10);
	        usuarioMap.put("Rocio", 11);
	        usuarioMap.put("Sara", 12);
	        usuarioMap.put("Sergio", 13);

	        usuarioComboBoxNotebooks.setItems(FXCollections.observableArrayList(usuarioMap.keySet()));
	        usuarioComboBoxNotebooks.setValue("Seleccione usuario");
	        consultaComboBoxNotebooks.setItems(options);
	        consultaComboBoxNotebooks.setValue("Seleccione consulta");
	       
	        // Cargar imagen
	        loadImage();
	    }

	    
	    private void loadImage() {
	        try {
	            Image imagen_importar = new Image(getClass().getClassLoader().getResourceAsStream("fondo_importar.png"));
	            Image imagen_exportar = new Image(getClass().getClassLoader().getResourceAsStream("fondo_exportar.png"));
	            imageView_importarNotebooks.setImage(imagen_importar);
	            imageView_exportarNotebooks.setImage(imagen_exportar);
	        } catch (Exception e) {
	            showAlert("Error", "No se pudo cargar la imagen.");
	            e.printStackTrace();
	        }
	    }
	    
    

	        // Secciones de Notebooks
	        @FXML private VBox consultaNotebooksSection;
	        @FXML private VBox abmNotebooksSection;
	        @FXML private VBox exportarNotebooksSection;
	        @FXML private VBox importarNotebooksSection;

	        // Método para mostrar Notebooks
	        public void showConsultaNotebooks() {
	            // Hacer visibles las secciones de consulta Notebooks
	            consultaNotebooksSection.setVisible(true);
	            abmNotebooksSection.setVisible(false);
	            exportarNotebooksSection.setVisible(false);
	            importarNotebooksSection.setVisible(false);
	        }

	        
	        public void showABMNotebooks() {
	            // Hacer visibles las secciones de consulta Notebooks
	            consultaNotebooksSection.setVisible(false);
	            abmNotebooksSection.setVisible(true);
	            exportarNotebooksSection.setVisible(false);
	            importarNotebooksSection.setVisible(false);
	        }

	        public void showExportarNotebooks() {
	            // Hacer visibles las secciones de consulta Notebooks
	            consultaNotebooksSection.setVisible(false);
	            abmNotebooksSection.setVisible(false);
	            exportarNotebooksSection.setVisible(true);
	            importarNotebooksSection.setVisible(false);
	        }
	        
	        
	        public void showImportarNotebooks() {
	            // Hacer visibles las secciones de consulta Notebooks
	            consultaNotebooksSection.setVisible(false);
	            abmNotebooksSection.setVisible(false);
	            exportarNotebooksSection.setVisible(false);
	            importarNotebooksSection.setVisible(true);
	        }
	        
	        
	        
	        
	        
	        // Alta
		@FXML
		public void handleAltaNotebooks() {
		    String serie = serieFieldABM.getText();
		    String macaddress = macAddressFieldABM.getText();
		    String usuarioStr = usuarioFieldABM.getText( );
		  
		    int usuario = Integer.parseInt(usuarioFieldABM.getText());
		    
		    if (serie.isEmpty() || macaddress.isEmpty() || usuarioStr.isEmpty()) {
		        showAlert("Error", "Todos los campos son obligatorios para agregar un registro.");
		        return;
		    }

		    if (!serie.isEmpty() && !macaddress.isEmpty()) {
		        try {
		            	            
		            // Convierte la fecha en un objeto Timestamp de Firestore
		            Timestamp timestamp = Timestamp.now();

		            // Llama al método addRecord con el Timestamp en lugar de la cadena
		            dbHandler.addRecord(serie, macaddress, usuario, timestamp);
		            
		            clearFields();
		            showAlert("Éxito", "Registro insertado correctamente");
		        } catch (Exception e) {
		            showAlert("Error", "Formato de fecha incorrecto. Debe ser dd/MM/yyyy HH:mm:ss.");
		            e.printStackTrace();
		        }
		    } else {
		        showAlert("Error", "Serie y MAC Address son obligatorios.");
		    }
		}

		// Baja
		@FXML
		public void handleBajaNotebooks() {
			String serie = serieFieldABM.getText();

			  if (!serie.isEmpty()) {
			        // Llama al método deleteRecord en dbHandler para eliminar el registro
			        boolean success = dbHandler.deleteRecord(serie);
			        clearFields();
			        
			        if (success) {
			            showAlert("Éxito", "Registro eliminado correctamente.");
			        } else {
			            showAlert("Error", "No se encontró un registro con la serie especificada.");
			        }
			    } else {
			        showAlert("Error", "Serie es obligatorio para eliminar un registro.");
			    }
		}

		// Modificar
		@FXML
		public void handleModificarNotebooks() {
			String serie = serieFieldABM.getText();
			String macaddress = macAddressFieldABM.getText();
			int usuario = Integer.parseInt(usuarioFieldABM.getText());
			String fechaStr = fechaFieldABM.getText();

			if (serie.isEmpty()) {
		        showAlert("Error", "Serie es obligatorio para modificar un registro.");
		        return;
		    }

		    try {
		        // Convierte la cadena de fecha en un objeto Date
		        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		        Date fecha = dateFormat.parse(fechaStr); // Convierte la fecha a Date
		        
		        // Convierte la fecha en un objeto Timestamp de Firestore
		        Timestamp timestamp = Timestamp.of(fecha);

		        // Llama al método updateRecord con el Timestamp en lugar de la cadena
		        dbHandler.updateRecord(serie, macaddress, usuario, timestamp);
		        
		        clearFields();
		        showAlert("Éxito", "Registro actualizado correctamente");
		    } catch (ParseException e) {
		        showAlert("Error", "Formato de fecha incorrecto. Debe ser dd/MM/yyyy HH:mm:ss.");
		        e.printStackTrace();
		    }
		}
		
		// Consultar serie ABM
		@FXML
	    public void handleBuscarNotebooks() {
	        String serie = serieFieldABM.getText(); // Obtener la serie del campo de texto

	        if (!serie.isEmpty()) {
	            ApiFuture<QuerySnapshot> future = dbHandler.getRecordsBySerie(serie); // Método que consulta por serie
	            try {
	                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	                if (!documents.isEmpty()) {
	                    QueryDocumentSnapshot document = documents.get(0); // Obtener el primer documento

	                    // Cargar los datos en los campos de texto
	                    macAddressFieldABM.setText(document.getString("macaddress"));
	                    usuarioFieldABM.setText(String.valueOf(document.getLong("usuario")));
	                    Timestamp fecha = document.getTimestamp("fecha");
	                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    fechaFieldABM.setText(fecha != null ? sdf.format(fecha.toDate()) : "");

	                } else {
	                    showAlert("Información", "No se encontró un registro con la serie especificada.");
	                }
	            } catch (InterruptedException | ExecutionException e) {
	                e.printStackTrace();
	                showAlert("Error", "Error al buscar el registro.");
	            }
	        } else {
	            showAlert("Error", "El campo de serie no puede estar vacío.");
	        }
	    }

		
		// Consultar todos - SECCION CONSULTA
	    @FXML
	    public void handleConsultaTodos() {
	        ApiFuture<QuerySnapshot> future = dbHandler.getAllRecords();
	        resultTableNotebooks.getItems().clear(); // Limpiar la tabla antes de llenarla
	        try {
	            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	            if (documents.isEmpty()) {
	                showAlert("Información", "No se encontraron registros.");
	            } else {
	                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                int orden = 1; // Contador para el orden
	                for (QueryDocumentSnapshot document : documents) {
	                    String serie = document.getString("serie");
	                    String macAddress = document.getString("macaddress");
	                    int usuario =  document.getLong("usuario").intValue();
	                    String fecha = document.getTimestamp("fecha") != null 
	                            ? sdf.format(document.getTimestamp("fecha").toDate())
	                            : "N/A";

	                    // Agregar el registro a la tabla
	                    resultTableNotebooks.getItems().add(new Notebook(serie, macAddress, usuario, fecha, orden));
	                    orden++; // Incrementar el contador
	                }
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    @FXML
	    public void handleConsultaPorFecha() {    	
	    	String day = dayFieldNotebooks.getText();
	    	String month = monthFieldNotebooks.getText();
	    	String year = yearFieldNotebooks.getText();
	    	String fechaStr = year + "-" + month + "-" + day; // Formato yyyy-MM-dd
	    		       
	        if (!fechaStr.isEmpty()) {
	            try {
	                // Convertir la fecha de String a un Timestamp de inicio y fin para el día
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                Date date = sdf.parse(fechaStr);
	                Timestamp startOfDay = Timestamp.ofTimeSecondsAndNanos(date.getTime() / 1000, 0); // Inicio del día
	                Timestamp endOfDay = Timestamp.ofTimeSecondsAndNanos((date.getTime() / 1000) + 86399, 0); // Fin del día

	                // Llamar al método en dbHandler para consultar por el rango de fecha
	                ApiFuture<QuerySnapshot> future = dbHandler.getRecordsByDate(startOfDay, endOfDay);
	                resultTableNotebooks.getItems().clear(); // Limpiar la tabla antes de llenarla

	                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	                if (documents.isEmpty()) {
	                    showAlert("Información", "No se encontraron registros para la fecha: " + fechaStr);
	                } else {
	                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    int orden = 1; // Contador para el orden
	                    for (QueryDocumentSnapshot document : documents) {
	                        String serie = document.getString("serie");
	                        String macAddress = document.getString("macaddress");
	                        int usuarioDb = document.getLong("usuario").intValue();
	                        String fecha = document.getTimestamp("fecha") != null 
	                                ? outputFormat.format(document.getTimestamp("fecha").toDate())
	                                : "N/A";

	                        // Agregar el registro a la tabla
	                        resultTableNotebooks.getItems().add(new Notebook(serie, macAddress, usuarioDb, fecha, orden));
	                        orden++; // Incrementar el contador
	                    }
	                }
	            } catch (ParseException | InterruptedException | ExecutionException e) {
	                showAlert("Error", "Error al procesar la fecha. Asegúrate de que esté en el formato yyyy-MM-dd.");
	                e.printStackTrace();
	            }
	        } else {
	            showAlert("Error", "El campo de fecha no puede estar vacío.");
	        }
	    }
	    
	    
	    @FXML
	    public void handleConsultaPorUsuario() {
	    	String usuarioStr = usuarioComboBoxNotebooks.getValue(); // Obtener el usuario seleccionado del ComboBox

	        if (usuarioStr != null && !usuarioStr.isEmpty()) {
	            Integer usuario = usuarioMap.get(usuarioStr); // Obtener el número de usuario desde el mapa
	            if (usuario != null) {
	                ApiFuture<QuerySnapshot> future = dbHandler.getRecordsByUser(usuario); // Consulta por usuario
	                resultTableNotebooks.getItems().clear(); // Limpiar la tabla antes de llenarla

	                try {
	                    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	                    if (documents.isEmpty()) {
	                        showAlert("Información", "No se encontraron registros para el usuario: " + usuarioStr);
	                    } else {
	                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                        int orden = 1; // Contador para el orden
	                        for (QueryDocumentSnapshot document : documents) {
	                            String serie = document.getString("serie");
	                            String macAddress = document.getString("macaddress");
	                            int usuarioDb = document.getLong("usuario").intValue(); // Obtener el usuario de la base de datos
	                            String fecha = document.getTimestamp("fecha") != null 
	                                    ? sdf.format(document.getTimestamp("fecha").toDate())
	                                    : "N/A";

	                            // Agregar el registro a la tabla
	                            resultTableNotebooks.getItems().add(new Notebook(serie, macAddress, usuarioDb, fecha, orden));
	                            orden++; // Incrementar el contador
	                        }
	                    }
	                } catch (InterruptedException | ExecutionException e) {
	                    e.printStackTrace();
	                }
	            } else {
	                showAlert("Error", "El usuario seleccionado no es válido.");
	            }
	        } else {
	            showAlert("Error", "Debe seleccionar un usuario.");
	        }
	    }
	    
	    
	    // Consultar serie - SECCION CONSULTA
	    @FXML
	    public void handlePorSerie() {
	    	String serie = serieFieldConsultaNotebooks.getText(); // Obtener la serie del campo de texto

	    	if (!serie.isEmpty()) {
	    		ApiFuture<QuerySnapshot> future = dbHandler.getRecordsBySerie(serie); // Consulta por serie
	    		resultTableNotebooks.getItems().clear(); // Limpiar la tabla antes de llenarla

	    		try {
	    			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	    			if (documents.isEmpty()) {
	    				showAlert("Información", "No se encontraron registros para la serie: " + serie);
	    			} else {
	    				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    				int orden = 1; // Contador para el orden
	    				for (QueryDocumentSnapshot document : documents) {
	    					String serieDb = document.getString("serie");
	    					String macAddress = document.getString("macaddress");
	    					int usuarioDb = document.getLong("usuario").intValue(); // Obtener el usuario de la base de datos
	    					String fecha = document.getTimestamp("fecha") != null 
	    							? sdf.format(document.getTimestamp("fecha").toDate())
	    									: "N/A";

	    					// Agregar el registro a la tabla
	    					resultTableNotebooks.getItems().add(new Notebook(serieDb, macAddress, usuarioDb, fecha, orden));
	    					orden++; // Incrementar el contador
	    				}
	    			}
	    		} catch (InterruptedException | ExecutionException e) {
	    			e.printStackTrace();
	    		}
	    	} else {
	    		showAlert("Error", "El campo de serie no puede estar vacío.");
	    	}

	    }

	    
	    /* Importar Excel */
	    @FXML
	    private void handleImportarExcelNotebooks() {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
	        File selectedFile = fileChooser.showOpenDialog(null);

	        if (selectedFile != null) {
	            try (FileInputStream fis = new FileInputStream(selectedFile); Workbook workbook = new XSSFWorkbook(fis)) {
	                // Leer archivo Excel
	                Sheet sheet = workbook.getSheetAt(0); // Leer la primera hoja

	                for (Row row : sheet) {
	                    if (row.getRowNum() == 0) continue; // Saltar la cabecera

	                    // Leer serie (columna 0)
	                    String serie = getCellStringValue(row.getCell(0));

	                    // Leer macaddress (columna 1)
	                    String macaddress = getCellStringValue(row.getCell(1));

	                    // Leer usuario (columna 2)
	                    int usuario = (int) row.getCell(2).getNumericCellValue(); // Asumiendo que es siempre numérico

	                    // Leer fecha (columna 3) como String
	                    String fechaStr = getCellStringValue(row.getCell(3));

	                    // Convertir la fecha de String a Timestamp
	                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    Date parsedDate = sdf.parse(fechaStr);
	                    Timestamp fecha = Timestamp.of(parsedDate);

	                    // Agregar el registro a Firestore
	                    dbHandler.addRecord(serie, macaddress, usuario, fecha);
	                }

	                showAlert("Éxito", "Datos importados correctamente desde Excel.");

	            } catch (IOException | ParseException e) {
	                e.printStackTrace();
	                showAlert("Error", "Error al importar datos: " + e.getMessage());
	            }
	        } else {
	            showAlert("Error", "No se seleccionó ningún archivo.");
	        }
	    }

	    // Método auxiliar para obtener el valor de una celda como String, manejando diferentes tipos
	    private String getCellStringValue(Cell cell) {
	        switch (cell.getCellType()) {
	            case STRING:
	                return cell.getStringCellValue();
	            case NUMERIC:
	                // En caso de que sea numérico, lo convertimos a String
	                if (DateUtil.isCellDateFormatted(cell)) {
	                    // Si es una fecha, la formateamos
	                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    return dateFormat.format(cell.getDateCellValue());
	                } else {
	                    // Si es numérico pero no es fecha, lo convertimos a string
	                    return String.valueOf((int) cell.getNumericCellValue());
	                }
	            case BOOLEAN:
	                return String.valueOf(cell.getBooleanCellValue());
	            default:
	                return "";
	        }
	    }
	    
	    @FXML
	    public void handleExportarNotebooks() {
	        try {
	            // Obtener todos los registros
	            ApiFuture<QuerySnapshot> future = dbHandler.getAllRecords();
	            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

	            // Crear un libro de Excel
	            XSSFWorkbook workbook = new XSSFWorkbook();
	            XSSFSheet sheet = workbook.createSheet("Notebooks");

	            // Crear encabezados
	            String[] headers = {"serie", "macaddress", "usuario", "fecha"};
	            Row headerRow = sheet.createRow(0);
	            for (int i = 0; i < headers.length; i++) {
	                Cell cell = headerRow.createCell(i);
	                cell.setCellValue(headers[i]);
	            }

	            // Llenar datos
	            int rowNum = 1;
	            for (QueryDocumentSnapshot document : documents) {
	                Row row = sheet.createRow(rowNum++);
	                row.createCell(0).setCellValue(document.getString("serie")); // Serie
	                row.createCell(1).setCellValue(document.getString("macaddress")); // MAC Address
	                row.createCell(2).setCellValue(document.getLong("usuario")); // Usuario
	                Timestamp fecha = document.getTimestamp("fecha");
	                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                row.createCell(3).setCellValue(fecha != null ? sdf.format(fecha.toDate()) : ""); // Fecha formateada
	            }

	            // Crear un FileChooser
	            FileChooser fileChooser = new FileChooser();
	            fileChooser.setTitle("Guardar archivo Excel");
	            fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
	                new FileChooser.ExtensionFilter("All Files", "*.*")
	            );

	            // Mostrar el diálogo para seleccionar el archivo
	            File file = fileChooser.showSaveDialog(null);
	            if (file != null) {
	                // Escribir el archivo a disco
	                try (FileOutputStream outputStream = new FileOutputStream(file)) {
	                    workbook.write(outputStream);
	                    showAlert("Éxito", "Datos exportados a " + file.getName());
	                }
	            } else {
	                showAlert("Cancelado", "Exportación cancelada.");
	            }

	            workbook.close();

	        } catch (InterruptedException | ExecutionException | IOException e) {
	            e.printStackTrace();
	            showAlert("Error", "Error al exportar los datos.");
	        }
	    }

	    @FXML
	    private void handleComboBoxSelectionNotebooks() {
	    	String selectedOption = consultaComboBoxNotebooks.getValue();
	        
	        // Oculta todos los contenedores
	        usuarioBoxNotebooks.setVisible(false);
	        fechaBoxNotebooks.setVisible(false);
	        serieBoxNotebooks.setVisible(false);
	        
	        // Muestra solo el contenedor correspondiente a la selección
	        switch (selectedOption) {
	            case "Por usuario":
	                usuarioBoxNotebooks.setVisible(true);
	                break;
	            case "Por fecha":
	            	fechaBoxNotebooks.setVisible(true);
	                break;
	            case "Por serie":
	            	serieBoxNotebooks.setVisible(true);
	                break;
	        }
	       
	    }

	    @FXML
	    public void handleConsultarNotebooks() {
	        String selectedItem = consultaComboBoxNotebooks.getSelectionModel().getSelectedItem();

	        switch (selectedItem) {
	            case "Por usuario":
	                // Lógica para consultar por usuario
	            	handleConsultaPorUsuario();
	                break;
	            case "Por fecha":
	                // Lógica para consultar por fecha
	            	handleConsultaPorFecha();
	                break;
	            case "Por serie":
	                // Lógica para consultar por serie
	            	handlePorSerie();
	                break;
	            case "Todos":
	            	// Logica para consultar por todos los registros
	            	handleConsultaTodos();
	            	break;
	            default:
	                showAlert("Error", "Seleccione un tipo de consulta.");
	                break;
	        }
	    }
	    
	    // Utilidades
		private void clearFields() {
			serieFieldABM.clear();
			macAddressFieldABM.clear();
			usuarioFieldABM.clear();
			fechaFieldABM.clear();
			//serieFieldConsultaNotebooks.clear();
			//usuarioFieldConsulta.clear();
			//fechaFieldConsulta.clear();
		}

		private void showAlert(String title, String message) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		}

	}
