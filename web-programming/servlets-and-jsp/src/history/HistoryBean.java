package history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryBean implements Serializable {
    private List<HistoryNode> nodeList;

    public HistoryBean() {
        nodeList = new ArrayList<>();
    }

    public List<HistoryNode> getNodeList() {
        return nodeList;
    }
}
