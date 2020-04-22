package com.bobisonfire.web.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * ManagedBean, отвечающий за валидацию вводимых значений в форме areaForm основной страницы приложения.
 *
 * @author Nikita Akatyev
 * @version 1.0.0
 */
@RequestScoped
@ManagedBean(name = "validator")
public class ValidatorBean implements Serializable {
    /**
     * Проверяет, попадает ли введенное значение x в промежуток [-2; 2].
     * @param context - контекст JSF-приложения
     * @param component - компонент UI, значение которого было введено пользователем
     * @param value - введенное пользователем значение x
     * @throws ValidatorException - при непопадании значения в промежуток
     */
    public void validateX(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        validateBigDecimal(
                component, (BigDecimal) value,

                // -2 <= x <= 2
                number -> number.abs().compareTo( BigDecimal.valueOf(2) ) <= 0
        );
    }
    /**
     * Проверяет, попадает ли введенное значение y в промежуток (-3; 3).
     * @param context - контекст JSF-приложения
     * @param component - компонент UI, значение которого было введено пользователем
     * @param value - введенное пользователем значение y
     * @throws ValidatorException - при непопадании значения в промежуток
     */
    public void validateY(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        validateBigDecimal(
                component, (BigDecimal) value,

                // -3 < y < 3
                number -> number.abs().compareTo( BigDecimal.valueOf(3) ) < 0
        );
    }
    /**
     * Проверяет, попадает ли введенное значение R в промежуток [1; 5].
     * @param context - контекст JSF-приложения
     * @param component - компонент UI, значение которого было введено пользователем
     * @param value - введенное пользователем значение R
     * @throws ValidatorException - при непопадании значения в промежуток
     */
    public void validateR(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        validateBigDecimal(
                component, (BigDecimal) value,

                // 1 <= r <= 5
                number -> number.subtract( BigDecimal.valueOf(3) ).abs().compareTo( BigDecimal.valueOf(2) ) <= 0
        );
    }

    /**
     * Проверяет BigDecimal число на выполнение заданного условия.
     * @param component - компонент UI, значение которого было взято на проверку
     * @param value - проверяемое значение
     * @param validateCondition - условие, которому должно соответствовать проверяемое значение
     * @throws ValidatorException  - при несоответсвии условию
     */
    private void validateBigDecimal(UIComponent component, BigDecimal value, Predicate<BigDecimal> validateCondition) throws ValidatorException {
        if (!validateCondition.test(value)) {
            Object summary = component.getAttributes().get("validatorMessage");
            FacesMessage message = new FacesMessage(
                    summary == null ? "Ошибка валидации" : summary.toString(),
                    "Число не входит в ОДЗ."
            );
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}