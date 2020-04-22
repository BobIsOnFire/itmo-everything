package com.bobisonfire.web.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.Map;
/**
 * Managed Bean, ответственный за работу с куки light-theme.
 *
 * Возвращает/устанавливает значение темы, меняет тему со светлой на темную.
 * По умолчанию на странице устанавливается светлая тема.
 *
 * @author Nikita Akatyev
 * @version 1.0.0
 */
@ViewScoped
@ManagedBean(name = "cookies")
public class CookieBean implements Serializable {
    private boolean lightTheme;

    public CookieBean() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> cookies = context.getRequestCookieMap();
        if (!cookies.containsKey("light-theme")) {
            lightTheme = true;
        } else {
            Cookie cookie = (Cookie) cookies.get("light-theme");
            lightTheme = cookie.getValue().equals("true");
        }
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void setLightTheme(boolean lightTheme) {
        this.lightTheme = lightTheme;
    }

    public void switchTheme() {
        setLightTheme( !lightTheme );
    }
}