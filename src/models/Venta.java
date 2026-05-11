package models;

import java.util.ArrayList;
import java.util.List;

// RF-07 | RF-09 | RF-10 — Entidad Venta
public class Venta {

    private int    id;
    private int    idCliente;
    private int idUsuario;
    private String fecha;   
    private String hora;   
    private double total;

    // Relación 1 a muchos con DetalleVenta
    private List<DetalleVenta> detalles;

    public Venta() {
        this.detalles = new ArrayList<>();
    }

    public Venta(int idCliente, int idUsuario, String fecha, String hora) {
        this.detalles = new ArrayList<>();
        setIdCliente(idCliente);
        setIdUsuario(idUsuario);
        setFecha(fecha);
        setHora(hora);
    }

    // ── Getters y Setters con validación ─────────────────────────────────────

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }

    public void setIdCliente(int idCliente) {
        if (idCliente <= 0)
            throw new IllegalArgumentException("El id de cliente debe ser mayor a 0.");
        this.idCliente = idCliente;
    }

    public int getIdUsuario() { return idUsuario; }

    public void setIdUsuario(int idUsuario) {
        if (idUsuario <= 0)
            throw new IllegalArgumentException("El id de usuario debe ser mayor a 0.");
        this.idUsuario = idUsuario;
    }

    public String getFecha() { return fecha; }

    public void setFecha(String fecha) {
        if (fecha == null || fecha.isEmpty())
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        this.fecha = fecha;
    }

    public String getHora() { return hora; }

    public void setHora(String hora) {
        if (hora == null || hora.isEmpty())
            throw new IllegalArgumentException("La hora no puede estar vacía.");
        this.hora = hora;
    }

    public double getTotal() { return total; }

    public void setTotal(double total) { this.total = total; }

    public List<DetalleVenta> getDetalles() { return detalles; }

    // RF-09 — Agrega un detalle y recalcula el total automáticamente
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        this.total += detalle.getSubtotal();
    }

    @Override
    public String toString() {
        return "Venta{id=" + id + ", idCliente=" + idCliente + ", fecha=" + fecha +
               ", hora=" + hora + ", total=S/." + String.format("%.2f", total) + "}";
    }
}
