package de.escidoc.core.common.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

public class ScapeIngestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IntellectualEntity entity;
        try {
            entity = SCAPEMarshaller.getInstance().deserialize(IntellectualEntity.class, req.getInputStream());
        }
        catch (Exception e) {
            throw new IOException("unable to deserialize intellectual entity");
        }
        resp.setContentType("text/plain");
        resp.getOutputStream().write(entity.getIdentifier().getValue().getBytes());
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
        resp.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getOutputStream().write("scapeage".getBytes());
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
        resp.flushBuffer();
    }
}
