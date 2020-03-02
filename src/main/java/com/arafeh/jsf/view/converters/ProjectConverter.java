package com.arafeh.jsf.view.converters;

import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Project;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import static com.arafeh.jsf.core.utils.Extensions.floatVal;
import static com.arafeh.jsf.core.utils.Extensions.longVal;

@Named(value = "projectConverter")
public class ProjectConverter implements Converter {
    @Inject
    ProjectBll projectService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            long projectId = longVal(value, -1);
            return projectService.get(projectId).orElse(null);
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(value + " is not a valid Project"), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (!(value instanceof Project)) {
            return "NaN";
        }

        try {
            return String.valueOf(((Project) value).getId());
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(value.toString() + " is not a valid Project"), e);
        }
    }
}
