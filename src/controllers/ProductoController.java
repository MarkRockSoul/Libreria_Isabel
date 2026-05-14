package controllers;

import dao.ConexionDB;
import dao.ProductoDAO;
import models.Producto;
import java.sql.Connection;
import java.util.List;

public class ProductoController {

    private ProductoDAO productoDAO;

    public ProductoController() {
        Connection conn = ConexionDB.getConexion();
        this.productoDAO = new ProductoDAO(conn);
    }

    // RF-02 — Registrar producto con validación previa
    public String registrar(String codigo, String nombre, String categoria,
                            String precioStr, String stockStr, String minimoStr) {
        if (codigo.isBlank() || nombre.isBlank() || precioStr.isBlank()
                || stockStr.isBlank() || minimoStr.isBlank())
            return "ERROR: Complete todos los campos obligatorios.";
        try {
            double precio = Double.parseDouble(precioStr);
            int stock    = Integer.parseInt(stockStr);
            int minimo   = Integer.parseInt(minimoStr);
            if (precio <= 0) return "ERROR: El precio debe ser mayor a 0.";
            if (stock  <  0) return "ERROR: El stock no puede ser negativo.";
            if (minimo <  0) return "ERROR: El stock mínimo no puede ser negativo.";

            Producto p = new Producto(0, codigo.trim(), nombre.trim(),
                                      categoria.trim(), precio, stock, minimo);
            return productoDAO.insertar(p)
                ? "OK: Producto '" + nombre + "' registrado correctamente."
                : "ERROR: El código '" + codigo + "' ya está en uso.";
        } catch (NumberFormatException e) {
            return "ERROR: Precio y stock deben ser valores numéricos.";
        }
    }

    // RF-03 — Actualizar producto
    public String actualizar(String codigoOriginal, String codigo, String nombre,
                             String categoria, String precioStr, String stockStr, String minimoStr) {
        if (codigoOriginal.isBlank()) return "ERROR: Seleccione un producto para editar.";
        try {
            double precio = Double.parseDouble(precioStr);
            int stock    = Integer.parseInt(stockStr);
            int minimo   = Integer.parseInt(minimoStr);
            Producto p = new Producto(0, codigo.trim(), nombre.trim(),
                                      categoria.trim(), precio, stock, minimo);
            return productoDAO.actualizar(codigoOriginal, p)
                ? "OK: Producto actualizado."
                : "ERROR: El código '" + codigo + "' ya está en uso.";
        } catch (NumberFormatException e) {
            return "ERROR: Precio y stock deben ser valores numéricos.";
        }
    }

    // RF-04 — Eliminar producto
    public String eliminar(String codigo) {
        if (codigo.isBlank()) return "ERROR: Seleccione un producto.";
        return productoDAO.eliminar(codigo)
            ? "OK: Producto eliminado."
            : "ERROR: No se puede eliminar (tiene ventas asociadas o no existe).";
    }

    // RF-05 — Buscar por código
    public Producto buscarPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }

    // RF-05 — Buscar por nombre (lista)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }

    // RF-11 — Listar todos los productos
    public List<Producto> listarTodos() {
        return productoDAO.listar();
    }

    // RF-13 — Listar con alerta de stock
    public List<Producto> listarConAlerta() {
        return productoDAO.listarConAlertaStock();
    }
}
