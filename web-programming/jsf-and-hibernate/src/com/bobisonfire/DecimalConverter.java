package com.bobisonfire;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;

@FacesConverter("decimalConverter")
public class DecimalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            if (s.contains(".") || s.contains(",")) {
                return new BigDecimal(s.replace(',', '.'));
            }
            return new BigDecimal(s + ".0");
        } catch (NumberFormatException exc) {
            Object summary = uiComponent.getAttributes().get("validatorMessage");
            FacesMessage message = new FacesMessage(
                    summary == null ? "Ошибка конвертации" : summary.toString(),
                    "Значение переменной не является десятичным числом."
            );
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(message);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object bigDecimal) {
        return bigDecimal.toString().replace('.', ',');
    }
}
