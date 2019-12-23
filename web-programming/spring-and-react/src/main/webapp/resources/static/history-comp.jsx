class HistoryComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            history: this.props.history,
            page: 1
        };

        this.decPage = this.decPage.bind(this);
        this.incPage = this.incPage.bind(this);
        console.log(this.state.history);
    }

    render() {
        const start = this.state.history.length - (this.state.page - 1) * 20;
        const end = Math.max(this.state.history.length - this.state.page * 20, -1);

        const elems = ['X', 'Y', 'R', 'Результат'];
        for (let i = start; i > end; i--) {
            const node = this.state.history[i];
            elems.push(node.x, node.y, node.r, node.result ? 'Попадание!' : 'Промах!');
        }

        return <div>
            <DynamicTable className="bordered-table centered label" cols="4" rows="21"
                          elems={elems} colWidth={['25%', '25%', '25%', '25%']}/>
            <div className="label centered">
                <button className="button" onClick={this.decPage}>  &#8592;  </button>
                Страница #{this.state.page}
                <button className="button" onClick={this.incPage}>  &#8594;  </button>
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
        if (this.state.page >= this.state.history.length / 20) return;
        this.setState({
            history: this.state.history,
            page: this.state.page + 1
        });
    }
}