class CanvasComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            xQuery: '0',
            yQuery: '',
            rQuery: '0',
            message: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleHistoryAddSuccess = this.handleHistoryAddSuccess.bind(this);
    }

    render() {
        const range = ['-3', '-2', '-1', '0', '1', '2', '3', '4', '5'];
        const xRadios = range.map(value =>
            <label>
                <input
                    type="radio"
                    name="xQuery"
                    value={value}
                    checked={this.state.xQuery === value}
                    onChange={this.handleChange}/>
                {value}
            </label>
        );

        const rRadios = range.map(value =>
            <label>
                <input
                    type="radio"
                    name="rQuery"
                    value={value}
                    checked={this.state.rQuery === value}
                    onChange={this.handleChange}/>
                {value}
            </label>
        );

        return <div className="label centered">
            <canvas id="canvas" height="500" width="500">Damn yo browser is old man...</canvas> <!-- todo add canvas interaction -->
            <div className="message">{this.state.message}</div>
            <form onSubmit={this.handleSubmit}>
                <div>X:</div>
                <DynamicTable className="label centered bordered-table" elems={xRadios} rows="3" cols="3" colWidth={['33%', '33%', '33%']}/>
                <br/>

                <label>
                    Y:<br/>
                    <input type="text" className="textfield" name="yQuery" value={this.state.yQuery}
                           onChange={this.handleChange} autoComplete="off" placeholder="(-3; 3)"/>
                </label>
                <br/>

                <div>R:</div>
                <DynamicTable className="label centered bordered-table" elems={rRadios} rows="3" cols="3" colWidth={['33%', '33%', '33%']}/>
                <br/><br/>

                <input type="submit" className="button" value="Проверить точку"/>
            </form>
        </div>
    }

    handleChange(event) {
        let state = this.state;
        state[event.target.name] = event.target.value;
        this.setState(state);
    }

    handleSubmit(event) {
        event.preventDefault();

        if (this.state.yQuery === '') {
            this.updateWithMessage('Введите значение Y.');
            return;
        }

        if (this.state.rQuery === '0') {
            this.updateWithMessage('Сначала выберите ненулевой R.');
            return;
        }

        fetch(`http://localhost:14900/api/history/add/${this.props.id}?` +
            `xQuery=${this.state.xQuery}&yQuery=${this.state.yQuery}&rQuery=${this.state.rQuery}`)
            .then(
                res => res.json(),
                error => {
                    this.updateWithMessage('Ошибка обмена данных с сервером.');
                    console.log(error);
                }
            )
            .then(this.handleHistoryAddSuccess)

    }

    handleHistoryAddSuccess(history) {
        if (history == null) {
            document.cookie = "user_id=0; max-age=0";
            ReactDOM.render(<LoginApp/>, document.getElementById("root"));
            return;
        }

        const nulls = [];
        if (history.x == null) nulls.push('X');
        if (history.y == null) nulls.push('Y');
        if (history.r == null) nulls.push('R');

        if (nulls.length > 0) {
            const poly = (singular, plural) => (nulls.length > 1 ? plural : singular);
            const msg = `Значени${poly('е', 'я')} переменн${poly('ой', 'ых')} ${nulls.join(', ')} некорректн${poly('о', 'ы')}.`;
            console.log(msg);
            this.updateWithMessage(msg);
            return;
        }

        fetch("http://localhost:14900/api/history/get/" + this.props.id)
            .then(
                res => res.json(),
                error => console.log(error)
            )
            .then(
                history => ReactDOM.render(
                    history == null ? <LoginApp/> : <MainApp id={this.props.id} history={history} />,
                    document.getElementById("root")
                )
            );
    }

    updateWithMessage(msg) {
        this.setState({
            xQuery: this.state.xQuery,
            yQuery: this.state.yQuery,
            rQuery: this.state.rQuery,
            message: msg
        });
    }
}