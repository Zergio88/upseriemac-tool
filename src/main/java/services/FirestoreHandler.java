package services;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreHandler {

    private Firestore db;

    public FirestoreHandler() {
        try {
            // Autenticación usando prini-tino.json
            FileInputStream serviceAccount = new FileInputStream("prini-tino.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Alta
    public void addRecord(String serie, String macaddress, int usuario, Timestamp fecha) {
        Map<String, Object> data = new HashMap<>();
        data.put("serie", serie); 
        data.put("macaddress", macaddress);
        data.put("usuario", usuario);
        data.put("fecha", fecha);

        ApiFuture<DocumentReference> future = db.collection("notebooks").add(data);
        try {
            System.out.println("Alta realizada en: " + future.get().getId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Baja
    public boolean deleteRecord(String serie) {
        try {
            // Realiza una consulta para encontrar el documento con la serie especificada
            Query query = db.collection("notebooks").whereEqualTo("serie", serie);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            
            // Si encuentra el documento, procede a eliminarlo
            if (!documents.isEmpty()) {
                for (QueryDocumentSnapshot document : documents) {
                    document.getReference().delete();
                }
                System.out.println("Registro con serie " + serie + " eliminado.");
                return true; // Eliminación exitosa
            } else {
                System.out.println("No se encontró ningún registro con la serie especificada.");
                return false; // No se encontró el registro
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modificar
    public void updateRecord(String serie, String macaddress, int usuario, Timestamp fecha) {
    	 // Crear el mapa de actualizaciones
        Map<String, Object> updates = new HashMap<>();
        updates.put("macaddress", macaddress);
        updates.put("usuario", usuario);
        updates.put("fecha", fecha);

        // Buscar el documento por el campo "serie"
        ApiFuture<QuerySnapshot> future = db.collection("notebooks").whereEqualTo("serie", serie).get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (!documents.isEmpty()) {
                // Obtener el primer documento encontrado (si la serie es única, debería ser solo uno)
                DocumentReference docRef = documents.get(0).getReference();
                
                // Actualizar el documento con el ID encontrado
                ApiFuture<WriteResult> writeFuture = docRef.update(updates);
                System.out.println("Registro modificado a las: " + writeFuture.get().getUpdateTime());
            } else {
                System.out.println("No se encontró ningún registro con la serie especificada.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Consultar todos los registros
    public ApiFuture<QuerySnapshot> getAllRecords() {
    	// Consulta todos los documentos en la colección 'notebooks' y ordena por la fecha
        Query query = db.collection("notebooks").orderBy("fecha"); // Ordenar por fecha
        return query.get();
    }
    
    
    // Consultar por Usuario
    public ApiFuture<QuerySnapshot> getRecordsByUser(int usuario) {
        // Consulta para obtener registros que coincidan con el usuario
        Query query = db.collection("notebooks").whereEqualTo("usuario", usuario);
        return query.get();
    }
    
    // Consultar por fecha
    public ApiFuture<QuerySnapshot> getRecordsByDate(Timestamp startOfDay, Timestamp endOfDay) {
        // Consulta para obtener registros en el rango de tiempo de la fecha
        Query query = db.collection("notebooks")
                        .whereGreaterThanOrEqualTo("fecha", startOfDay)
                        .whereLessThanOrEqualTo("fecha", endOfDay);
        return query.get();
    }
    
    
    // Consultar por Serie
    public ApiFuture<QuerySnapshot> getRecordsBySerie(String serie) {
        // Consulta para obtener registros que coincidan con la serie
        Query query = db.collection("notebooks").whereEqualTo("serie", serie);
        return query.get();
    }
}
