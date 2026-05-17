package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Producto;

public class ProductoDAO {
    private Connection conexion;

    public ProductoDAO(Connection conn) {
        this.conexion = conn;
    }

    /**
     * RF-02: Inserta un nuevo producto
     */
    public boolean insertar(Producto producto) {
        String query = "INSERT INTO productos (codigo, nombre, categoria, precio, stock, stock_minimo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            ps.setInt(6, producto.getStockMinimo());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
                System.out.println("✓ Producto registrado: " + producto.getNombre() + " [" + producto.getCodigo() + "]");
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("✗ El código ya está en uso: " + producto.getCodigo());
            } else {
                System.err.println("✗ Error al insertar producto: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * RF-03: Actualiza un producto existente
     */
    public boolean actualizar(Producto producto) {
        String query = "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, stock = ?, stock_minimo = ? WHERE id_producto = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getCategoria());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getStockMinimo());
            ps.setInt(6, producto.getIdProducto());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                System.out.println("✓ Producto actualizado: " + producto.getNombre());
                return true;
            } else {
                System.out.println("✗ No se encontró el producto con ID: " + producto.getIdProducto());
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * RF-04: Elimina un producto (solo si no tiene ventas asociadas)
     */
    public boolean eliminar(int idProducto) {
        String query = "DELETE FROM productos WHERE id_producto = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, idProducto);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                System.out.println("✓ Producto eliminado con ID: " + idProducto);
                return true;
            } else {
                System.out.println("✗ No se encontró el producto con ID: " + idProducto);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) { // Foreign key constraint
                System.err.println("✗ No se puede eliminar un producto con ventas asociadas");
            } else {
                System.err.println("✗ Error al eliminar producto: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * RF-05: Busca un producto por código
     */
    public Producto buscarPorCodigo(String codigo) {
        String query = "SELECT * FROM productos WHERE codigo = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo")
                );
            } else {
                System.out.println("✗ No se encontró producto con código: " + codigo);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar producto: " + e.getMessage());
        }
        return null;
    }

    /**
     * RF-05: Busca productos por nombre (búsqueda parcial)
     */
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo")
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar productos por nombre: " + e.getMessage());
        }
        return productos;
    }

    /**
     * RF-11 y RF-13: Lista todos los productos (para reporte de stock)
     */
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos ORDER BY nombre";
        
        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo")
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al listar productos: " + e.getMessage());
        }
        return productos;
    }

    /**
     * RF-13: Obtiene productos con stock bajo (stock <= stock_minimo)
     */
    public List<Producto> obtenerStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE stock <= stock_minimo ORDER BY stock ASC";
        
        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo")
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener productos con stock bajo: " + e.getMessage());
        }
        return productos;
    }

    /**
     * RF-08: Actualiza el stock de un producto (para ventas)
     */
    public boolean actualizarStock(int idProducto, int nuevaCantidad) {
        String query = "UPDATE productos SET stock = ? WHERE id_producto = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idProducto);
            
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }
}