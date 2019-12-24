class HistoryComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            history: props.history,
            page: 1
        };

        this.decPage = this.decPage.bind(this);
        this.incPage = this.incPage.bind(this);
    }

    componentWillReceiveProps(props) {
        this.setState({
            history: props.history,
            page: 1
        });
    }

    render() {
        const start = this.state.history.length - 1 - (this.state.page - 1) * 30;
        const end = Math.max(this.state.history.length - this.state.page * 30, -1);

        const elems = ['X', 'Y', 'R', 'Результат'];

        for (let i = start; i > end; i--) {
            const node = this.state.history[i];
            elems.push(node.x, node.y, node.r, node.result ? 'Попадание!' : 'Промах!');
        }

        return <div>
            <DynamicTable className="bordered-table centered label" cols="4" rows="31"
                          elems={elems} colWidth={['25%', '25%', '25%', '25%']}/>
            <div className="label centered">
                <span onClick={this.decPage}>  &#8592;  </span>
                Страница #{this.state.page}
                <span onClick={this.incPage}>  &#8594;  </span>
            </div>
        </div>
    }

    decPage() {
        if (this.state.page === 1) return;
        this.setState({
            history: this.state.history,
            page: this.state.page - 1
        });
    }

    incPage() {
        if (this.state.page >= this.state.history.length / 30) return;
        this.setState({
            history: this.state.history,
            page: this.state.page + 1
        });
    }
}