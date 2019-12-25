class DynamicTable extends React.Component {
    render() {
        // elems, rows, cols, className, colWidth
        const trs = [];
        const elems = this.props.elems;
        const rows = +this.props.rows;
        const cols = +this.props.cols;
        const colStyle = this.props.colWidth.map(value => { return {width: value} });

        let i = 0;
        while (i < rows) {
            const tds = [];
            for (let j = 0; j < cols && i * cols + j < elems.length; j++ ) {
                tds.push(<td style={colStyle[j]} key={j}>{ elems[i * cols + j] }</td>);
            }
            trs.push(<tr key={i}>{tds}</tr>);
            i++;
        }

        return <table className={this.props.className}><tbody>{trs}</tbody></table>
    }
}

class ToolBarTab extends React.Component {
    render() {
        let tab = this.props.tab;
        let styleClass = (tab.selected ? "tab-selected " : "") + "label";
        return <div className={styleClass} onClick={tab.onclick}>
            {tab.name}
        </div>;
    }
}

class Timer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            time: new Date().toLocaleString()
        };
        this.timerId = setInterval( () => this.setState( {time: new Date().toLocaleString()} ), +props.interval );
    }

    render() {
        return <div>Текущее время:<br/>{this.state.time}</div>;
    }

    componentWillUnmount() {
        clearInterval(this.timerId);
    }
}
