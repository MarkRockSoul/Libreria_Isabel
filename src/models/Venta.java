package models;

import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int    id;
    private int    idCliente;
    private int    idUsuario;
    private String fecha;
    private String hora;
    private double total;
    private List<DetalleVenta> detalles;

    public Venta() { this.detalles = new ArrayList<>(); }

    public Venta(int idCliente, int idUsuario, String fecha, String hora) {
        this.detalles = new ArrayList<>();
        setIdCliente(idCliente);
        setIdUsuario(idUsuario);
        setFecha(fecha);
        setHora(hora);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) {
        if (idCliente <= 0) throw new IllegalArgumentException("idCliente inválido.");
        this.idCliente = idCliente;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) {
        if (idUsuario <= 0) throw new IllegalArgumentException("idUsuario inválido.");
        this.idUsuario = idUsuario;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) throw new IllegalArgumentException("Fecha vacía.");
        this.fecha = fecha;
    }

    public String getHora() { return hora; }
    public void setHora(String hora) {
        if (hora == null || hora.isEmpty()) throw new IllegalArgumentException("Hora vacía.");
        this.hora = hora;
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<DetalleVenta> getDetalles() { return detalles; }

    public void agregarDetalle(DetalleVenta d) {
        this.detalles.add(d);
        this.total += d.getSubtotal();
    }

    @Override
    public String toString() {
        return "Venta{id=" + id + ", idCliente=" + idCliente + ", idUsuario=" + idUsuario
               + ", fecha=" + fecha + ", total=S/." + String.format("%.2f", total) + "}";
    }
}
