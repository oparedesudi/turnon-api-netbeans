package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.TurnoDAO;
import com.turnon.turnonapi.model.Cliente;
import com.turnon.turnonapi.model.Turno;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/turno")
public class TurnoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("actualizarEstado".equalsIgnoreCase(accion)) {
            actualizarEstadoTurno(request, response);
            return;
        }

        String nombre = request.getParameter("nombre");
        String cedula = request.getParameter("cedula");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String idServicio = request.getParameter("id_servicio");
        String ventanilla = request.getParameter("ventanilla");

        boolean esTurnoPersonalizado =
                !estaVacio(nombre)
                && !estaVacio(cedula)
                && !estaVacio(telefono)
                && !estaVacio(correo)
                && !estaVacio(idServicio)
                && !estaVacio(ventanilla);

        if (esTurnoPersonalizado) {
            crearTurnoPersonalizado(request, response);
        } else {
            crearTurnoTecnico(request, response);
        }
    }

    private void crearTurnoPersonalizado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String nombre = request.getParameter("nombre");
        String cedula = request.getParameter("cedula");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String idServicioStr = request.getParameter("id_servicio");
        String ventanilla = request.getParameter("ventanilla");

        if (estaVacio(nombre) || estaVacio(cedula) || estaVacio(telefono)
                || estaVacio(correo) || estaVacio(idServicioStr) || estaVacio(ventanilla)) {
            out.print("{\"ok\":false,\"mensaje\":\"Todos los campos son obligatorios para generar el turno\"}");
            return;
        }

        try {
            int idServicio = Integer.parseInt(idServicioStr);

            Cliente cliente = new Cliente();
            cliente.setNombre(nombre.trim());
            cliente.setCedula(cedula.trim());
            cliente.setTelefono(telefono.trim());
            cliente.setCorreo(correo.trim());

            TurnoDAO dao = new TurnoDAO();
            Turno turno = dao.crearTurnoPersonalizado(cliente, idServicio, ventanilla.trim());

            if (turno == null) {
                out.print("{\"ok\":false,\"mensaje\":\"Error al crear el turno personalizado\"}");
                return;
            }

            String mensaje = "Turno generado correctamente. Señor(a) "
                    + turno.getNombreCliente()
                    + ", su turno "
                    + turno.getCodigoTurno()
                    + " para el servicio "
                    + turno.getNombreServicio()
                    + " quedó registrado en estado "
                    + turno.getEstadoTurno()
                    + ". La confirmación quedó asociada al correo "
                    + turno.getCorreoCliente()
                    + " y al teléfono "
                    + turno.getTelefonoCliente()
                    + ".";

            StringBuilder json = new StringBuilder();
            json.append("{")
                    .append("\"ok\":true,")
                    .append("\"mensaje\":\"").append(escapeJson(mensaje)).append("\",")
                    .append("\"turno\":").append(turnoToJson(turno))
                    .append("}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            out.print("{\"ok\":false,\"mensaje\":\"El servicio seleccionado no es válido\"}");
        }
    }

    private void crearTurnoTecnico(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String codigoTurno = request.getParameter("codigo_turno");
        String idClienteStr = request.getParameter("id_cliente");
        String idServicioStr = request.getParameter("id_servicio");
        String idEstadoTurnoStr = request.getParameter("id_estado_turno");
        String ventanilla = request.getParameter("ventanilla");

        if (estaVacio(codigoTurno) || estaVacio(idClienteStr) || estaVacio(idServicioStr)
                || estaVacio(idEstadoTurnoStr) || estaVacio(ventanilla)) {
            out.print("Todos los campos del turno son obligatorios");
            return;
        }

        try {
            int idCliente = Integer.parseInt(idClienteStr);
            int idServicio = Integer.parseInt(idServicioStr);
            int idEstadoTurno = Integer.parseInt(idEstadoTurnoStr);

            Turno turno = new Turno();
            turno.setCodigoTurno(codigoTurno.trim());
            turno.setIdCliente(idCliente);
            turno.setIdServicio(idServicio);
            turno.setIdEstadoTurno(idEstadoTurno);
            turno.setVentanilla(ventanilla.trim());

            TurnoDAO dao = new TurnoDAO();
            boolean resultado = dao.crearTurno(turno);

            if (resultado) {
                out.print("Turno creado correctamente");
            } else {
                out.print("Error al crear turno");
            }

        } catch (NumberFormatException e) {
            out.print("Error en los datos numéricos del turno");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        TurnoDAO dao = new TurnoDAO();
        List<Turno> turnos = dao.listarTurnos();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < turnos.size(); i++) {
            json.append(turnoToJson(turnos.get(i)));

            if (i < turnos.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        out.print(json.toString());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        actualizarEstadoTurno(request, response);
    }

    private void actualizarEstadoTurno(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idTurnoStr = request.getParameter("id_turno");
        String idEstadoTurnoStr = request.getParameter("id_estado_turno");

        if (estaVacio(idTurnoStr) || estaVacio(idEstadoTurnoStr)) {
            out.print("Parámetros insuficientes para actualizar estado");
            return;
        }

        try {
            int idTurno = Integer.parseInt(idTurnoStr);
            int idEstadoTurno = Integer.parseInt(idEstadoTurnoStr);

            TurnoDAO dao = new TurnoDAO();
            boolean resultado = dao.actualizarEstadoTurno(idTurno, idEstadoTurno);

            if (resultado) {
                out.print("Estado del turno actualizado correctamente");
            } else {
                out.print("Error al actualizar el estado del turno");
            }

        } catch (NumberFormatException e) {
            out.print("Error en los datos numéricos para actualizar estado");
        }
    }

    private String turnoToJson(Turno turno) {
        StringBuilder json = new StringBuilder();

        json.append("{")
                .append("\"id_turno\":").append(turno.getIdTurno()).append(",")
                .append("\"codigo_turno\":\"").append(escapeJson(turno.getCodigoTurno())).append("\",")
                .append("\"fecha\":\"").append(escapeJson(String.valueOf(turno.getFecha()))).append("\",")
                .append("\"id_cliente\":").append(turno.getIdCliente()).append(",")
                .append("\"id_servicio\":").append(turno.getIdServicio()).append(",")
                .append("\"id_estado_turno\":").append(turno.getIdEstadoTurno()).append(",")
                .append("\"ventanilla\":\"").append(escapeJson(turno.getVentanilla())).append("\",")
                .append("\"nombre_cliente\":\"").append(escapeJson(turno.getNombreCliente())).append("\",")
                .append("\"cedula_cliente\":\"").append(escapeJson(turno.getCedulaCliente())).append("\",")
                .append("\"telefono_cliente\":\"").append(escapeJson(turno.getTelefonoCliente())).append("\",")
                .append("\"correo_cliente\":\"").append(escapeJson(turno.getCorreoCliente())).append("\",")
                .append("\"nombre_servicio\":\"").append(escapeJson(turno.getNombreServicio())).append("\",")
                .append("\"estado_turno\":\"").append(escapeJson(turno.getEstadoTurno())).append("\"")
                .append("}");

        return json.toString();
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String escapeJson(String texto) {
        if (texto == null || "null".equalsIgnoreCase(texto)) {
            return "";
        }

        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}