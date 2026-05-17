package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta {
    private int idVenta;
    private Date fecha;
    private double total;
    private int idCliente;
    private int idUsuario;
    
    // Composición: Una venta contiene detalles
    private List<DetalleVenta> detalles;

    // Constructores
    public Venta() {
        this.fecha = new Date();
        this.detalles = new ArrayList<>();
    }

    public Venta(int idVenta, Date fecha, double total, int idCliente, int idUsuario) {
        setIdVenta(idVenta);
        setFecha(fecha);
        setTotal(total);
        setIdCliente(idCliente);
        setIdUsuario(idUsuario);
        this.detalles = new ArrayList<>();
    }

    // Getters y Setters
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        if (total < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo");
        }
        this.total = total;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    /**
     * Agrega un detalle a la venta (relación de composición)
     */
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        calcularTotal();
    }

    /**
     * Calcula el total de la venta sumando todos los subtotales
     */
    public void calcularTotal() {
        this.total = 0;
        for (DetalleVenta detalle : detalles) {
            this.total += detalle.getSubtotal();
        }
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", fecha=" + fecha +
                ", total=" + String.format("%.2f", total) +
                ", idCliente=" + idCliente +
                ", idUsuario=" + idUsuario +
                ", cantidadDetalles=" + detalles.size() +
                '}';
    }
}