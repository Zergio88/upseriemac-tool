package modelo;

public class Notebook {

	    private String serie;
	    private String macaddress;
	    private int usuario;
	    private String fecha;
	    private int orden; // Nuevo atributo para el orden

	    public Notebook(String serie, String macaddress, int usuario, String fecha, int orden) {
	        this.serie = serie;
	        this.macaddress = macaddress;
	        this.usuario = usuario;
	        this.fecha = fecha;
	        this.orden = orden; // Inicializa el orden
	    }

	    // Getters
	    public String getSerie() { return serie; }
	    public String getMacaddress() { return macaddress; }
	    public int getUsuario() { return usuario; }
	    public String getFecha() { return fecha; }
	    public int getOrden() { return orden; } // Getter para el orden
	
}
