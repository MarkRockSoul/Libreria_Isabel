package models;

// RF-02 | RF-03 | RF-04 | RF-05 | RF-08 | RF-11 | RF-13 — Entidad Producto
public class Producto {

    private int    id;
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int    stockActual;
    private int    stockMinimo;

    public Producto() {}

    public Producto(int id, String codigo, String nombre, String categoria,
                    double precio, int stockActual, int stockMinimo) {
        setId(id);
        setCodigo(codigo);
        setNombre(nombre);
        setCategoria(categoria);
        setPrecio(precio);
        setStockActual(stockActual);
        setStockMinimo(stockMinimo);
    }

    // ── Getters y Setters con validación ─────────────────────────────────────

    public int getId() { return id; }

    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("El id no puede ser negativo.");
        this.id = id;
    }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty())
            throw new IllegalArgumentException("El código del producto no puede estar vacío.");
        this.codigo = codigo.trim();
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        this.nombre = nombre.trim();
    }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecio() { return precio; }

    public void setPrecio(double precio) {
        if (precio <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        this.precio = precio;
    }

    public int getStockActual() { return stockActual; }

    public void setStockActual(int stockActual) {
        if (stockActual < 0)
            throw new IllegalArgumentException("El stock actual no puede ser negativo.");
        this.stockActual = stockActual;
    }

    public int getStockMinimo() { return stockMinimo; }

    public void setStockMinimo(int stockMinimo) {
        if (stockMinimo < 0)
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        this.stockMinimo = stockMinimo;
    }

    // RF-13 — Devuelve true si el stock actual está en nivel de alerta
    public boolean tieneAlertaStock() {
        return stockActual <= stockMinimo;
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", codigo=" + codigo + ", nombre=" + nombre +
               ", precio=" + precio + ", stock=" + stockActual + "/" + stockMinimo + "}";
    }
}
