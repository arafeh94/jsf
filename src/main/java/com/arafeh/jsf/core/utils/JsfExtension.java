package com.arafeh.jsf.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

import static com.sun.imageio.plugins.jpeg.JPEG.version;

public class JsfExtension {
    public static Logger LOG = LoggerFactory.getLogger(JsfExtension.class);

    public static void throwOnce(String title, String content) {
        boolean throed = false;
        for (FacesMessage message : FacesContext.getCurrentInstance().getMessageList()) {
            if (message.getSummary().equals(title) && message.getDetail().equals(content)) {
                throed = true;
            }
        }
        if (!throed) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, title, content);
            throw new ValidatorException(facesMessage);
        }
    }

    public static void throwMsg(FacesMessage.Severity severity, String title, String content) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, content));
    }

    public static void throwMsg(String title, String content) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, title, content));
    }

    public static void redirect(String page, HashMap<String, Object> params) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        StringBuilder url = new StringBuilder(context.getRequestContextPath());
        url.append("/").append(page);
        if (params != null && !params.isEmpty()) {
            url.append("?");
            params.forEach((key, val) -> {
                url.append(key).append("=").append(val).append("&");
            });
            url.deleteCharAt(url.length() - 1);
        }
        try {
            context.redirect(url.toString());
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    public static void redirect(String page) {
        redirect(page, null);
    }

    public static void refresh() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(((HttpServletRequest) ec.getRequest())
                    .getRequestURI());
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }
}