package models;

// RF-07 | RF-09 — Entidad DetalleVenta (línea de producto en una venta)
public class DetalleVenta {

    private int    id;
    private int    idVenta;
    private int    idProducto;
    private int    cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta() {}

    public DetalleVenta(int idProducto, int cantidad, double precioUnitario) {
        setIdProducto(idProducto);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
        calcularSubtotal();
    }

    // ── Getters y Setters con validación ─────────────────────────────────────

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdVenta() { return idVenta; }

    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdProducto() { return idProducto; }

    public void setIdProducto(int idProducto) {
        if (idProducto <= 0)
            throw new IllegalArgumentException("El id de producto debe ser mayor a 0.");
        this.idProducto = idProducto;
    }

    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() { return precioUnitario; }

    public void setPrecioUnitario(double precioUnitario) {
        if (precioUnitario <= 0)
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0.");
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() { return subtotal; }

    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    // RF-09 — Calcula el subtotal automáticamente
    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    @Override
    public String toString() {
        return "DetalleVenta{idProducto=" + idProducto + ", cantidad=" + cantidad +
               ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal + "}";
    }
}
