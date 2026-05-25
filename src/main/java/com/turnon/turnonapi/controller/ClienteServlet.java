package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.ClienteDAO;
import com.turnon.turnonapi.model.Cliente;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> clientes = dao.listarClientes();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);

            json.append("{")
                .append("\"id_cliente\":").append(c.getIdCliente()).append(",")
                .append("\"nombre\":\"").append(escapeJson(c.getNombre())).append("\",")
                .append("\"cedula\":\"").append(escapeJson(c.getCedula())).append("\",")
                .append("\"telefono\":\"").append(escapeJson(c.getTelefono())).append("\",")
                .append("\"correo\":\"").append(escapeJson(c.getCorreo())).append("\",")
                .append("\"fecha_registro\":\"").append(escapeJson(String.valueOf(c.getFechaRegistro()))).append("\"")
                .append("}");

            if (i < clientes.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        out.print(json.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String nombre = request.getParameter("nombre");
        String cedula = request.getParameter("cedula");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");

        if (estaVacio(nombre) || estaVacio(cedula) || estaVacio(telefono) || estaVacio(correo)) {
            out.print("Todos los campos del cliente son obligatorios");
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre.trim());
        cliente.setCedula(cedula.trim());
        cliente.setTelefono(telefono.trim());
        cliente.setCorreo(correo.trim());

        ClienteDAO dao = new ClienteDAO();
        Cliente resultado = dao.registrarOBuscarCliente(cliente);

        if (resultado != null) {
            out.print("Cliente registrado o actualizado correctamente. ID cliente: " + resultado.getIdCliente());
        } else {
            out.print("Error al registrar cliente");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String escapeJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}