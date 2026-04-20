package com.turnon.turnonapi.controller;

import com.turnon.turnonapi.dao.TurnoDAO;
import com.turnon.turnonapi.model.Turno;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/turno")
public class TurnoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("actualizarEstado".equalsIgnoreCase(accion)) {
            actualizarEstadoDesdePost(request, response);
            return;
        }

        String codigo = request.getParameter("codigo_turno");
        String idClienteStr = request.getParameter("id_cliente");
        String idServicioStr = request.getParameter("id_servicio");
        String idEstadoStr = request.getParameter("id_estado_turno");
        String ventanilla = request.getParameter("ventanilla");

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (codigo == null || codigo.trim().isEmpty()
                || idClienteStr == null || idClienteStr.trim().isEmpty()
                || idServicioStr == null || idServicioStr.trim().isEmpty()
                || idEstadoStr == null || idEstadoStr.trim().isEmpty()
                || ventanilla == null || ventanilla.trim().isEmpty()) {
            out.print("Todos los campos del turno son obligatorios");
            return;
        }

        try {
            int idCliente = Integer.parseInt(idClienteStr);
            int idServicio = Integer.parseInt(idServicioStr);
            int idEstado = Integer.parseInt(idEstadoStr);

            Turno turno = new Turno();
            turno.setCodigoTurno(codigo);
            turno.setIdCliente(idCliente);
            turno.setIdServicio(idServicio);
            turno.setIdEstadoTurno(idEstado);
            turno.setVentanilla(ventanilla);

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

        TurnoDAO dao = new TurnoDAO();
        List<Turno> lista = dao.listarTurnos();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < lista.size(); i++) {
            Turno t = lista.get(i);

            json.append("{")
                .append("\"id_turno\":").append(t.getIdTurno()).append(",")
                .append("\"codigo_turno\":\"").append(escapeJson(t.getCodigoTurno())).append("\",")
                .append("\"fecha\":\"").append(escapeJson(String.valueOf(t.getFecha()))).append("\",")
                .append("\"id_cliente\":").append(t.getIdCliente()).append(",")
                .append("\"id_servicio\":").append(t.getIdServicio()).append(",")
                .append("\"id_estado_turno\":").append(t.getIdEstadoTurno()).append(",")
                .append("\"ventanilla\":\"").append(escapeJson(t.getVentanilla())).append("\"")
                .append("}");

            if (i < lista.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        out.print(json.toString());
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idTurnoStr = request.getParameter("id_turno");
        String idEstadoTurnoStr = request.getParameter("id_estado_turno");

        if (idTurnoStr == null || idEstadoTurnoStr == null) {
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

    private void actualizarEstadoDesdePost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idTurnoStr = request.getParameter("id_turno");
        String idEstadoTurnoStr = request.getParameter("id_estado_turno");

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idTurnoStr == null || idEstadoTurnoStr == null) {
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

    private String escapeJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}