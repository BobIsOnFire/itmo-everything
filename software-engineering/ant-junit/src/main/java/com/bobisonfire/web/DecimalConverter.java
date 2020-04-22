package com.bobisonfire.web;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;

/**
 * Класс-конвертер, преобразующий содержимое полей формы в
 * BigDecimal и наоборот.
 * @author Nikita Akatyev
 * @version 1.0.0
 */
@FacesConverter("decimalConverter")
public class DecimalConverter implements Converter {

    /**
     * Преобразует строковое значение в BigDecimal.
     *
     * В случае, если строковое значение не является числом, происходит
     * ошибка конвертации - соответствующее сообщение выводится в UI.
     * @param facesContext контекст JSF-приложения
     * @param uiComponent компонент, в котором находится искомая строка
     * @param s строка, которую необходимо преобразовать
     * @return преобразованное число
     * @throws ConverterException если произошла ошибка валидации
     */
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

    /**
     * Преобразует BigDecimal в строку. Разделитель дробной части - запятая.
     * @param facesContext контекст JSF-приложения
     * @param uiComponent компонент, в который выводится число
     * @param bigDecimal число, которое необходимо преобразовать
     * @return преобразованная строка
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object bigDecimal) {
        return bigDecimal.toString().replace('.', ',');
    }
}