package com.bobisonfire.web.beans;

import com.bobisonfire.web.HistoryDataAccess;
import com.bobisonfire.web.ORMHistoryNode;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Managed Bean, управляющий блоком с историей попаданий точек.
 *
 * Предоставляет доступ к полям, регулирующим не только данные истории,
 * но и состояние прокрутки таблицы с историей.
 * @author Nikita Akatyev
 * @see HistoryNode
 * @see HistoryDataAccess
 * @version 1.0.0
 */
@ApplicationScoped
@ManagedBean(name = "history")
public class HistoryBean implements Serializable {
    private HistoryNode node;
    private int scrollPage;

    private final String[] availableX = {"-2", "-1,5", "-1", "-0,5", "0", "0,5", "1", "1,5", "2"};
    private final String[] availableR = {"1", "2", "3", "4", "5"};
    private boolean scrollableLeft;
    private boolean scrollableRight;
    private boolean submitted;

    public String[] getAvailableX() {
        return availableX;
    }

    public String[] getAvailableR() {
        return availableR;
    }

    public HistoryNode getNode() {
        return node;
    }

    public void setNode(HistoryNode node) {
        this.node = node;
    }

    public HistoryBean() {
        node = new HistoryNode();
        scrollPage = 0;
        submitted = false;
    }

    /**
     * Получает из БД состояние истории и возвращает преобразованный список.
     * @return преобразованный список истории попаданий
     * @see HistoryDataAccess
     */
    private List<HistoryNode> getNodeList() {
        try (HistoryDataAccess access = new HistoryDataAccess()) {
            return access
                    .getNodes()
                    .stream()
                    .map(HistoryNode::new)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Возвращает данные, которые необходимо отобразить на текущей странице таблицы.
     * Список истории попаданий переворачивается для того, чтобы последние добавленные
     * элементы истории отображались в начале.
     * @return содержание текущей страницы с историей попаданий.
     */
    public List<HistoryNode> getReversedNodeList() {
        List<HistoryNode> temp = getNodeList();
        Collections.reverse(temp);

        scrollableLeft = scrollPage > 0;
        scrollableRight = (scrollPage + 1) * 20 < temp.size();

        return temp.subList(
                scrollPage * 20,
                scrollableRight ? (scrollPage + 1) * 20 : temp.size()
        );
    }

    /**
     * Вызов, происходящий при нажатии кнопки перехода влево в таблице.
     * Обновляет номер страницы, чтобы далее вызов <i>getReversedNodeList</i>
     * возвратил содержимое для этой страницы.
     */
    public void scrollLeft() {
        if (scrollableLeft) scrollPage--;
    }

    /**
     * Вызов, происходящий при нажатии кнопки перехода вправо в таблице.
     * Обновляет номер страницы, чтобы далее вызов <i>getReversedNodeList</i>
     * возвратил содержимое для этой страницы.
     */
    public void scrollRight() {
        if (scrollableRight) scrollPage++;
    }

    public int getScrollPage() {
        return scrollPage;
    }

    public boolean isScrollableLeft() {
        return scrollableLeft;
    }

    public boolean isScrollableRight() {
        return scrollableRight;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * Метод, вызываемый при клике на график или при заполнении формы.
     * После валидации и конвертации данных отвечает за добавление нового
     * элемента истории и обновление таблицы.
     *
     * @see HistoryDataAccess
     */
    public void addNode() {
        if (node.updateHit()) {
            try(HistoryDataAccess access = new HistoryDataAccess()) {
                access.addNode( new ORMHistoryNode(node) );
            }
            node = new HistoryNode();
            scrollPage = 0;
            submitted = true;
        }
    }
}