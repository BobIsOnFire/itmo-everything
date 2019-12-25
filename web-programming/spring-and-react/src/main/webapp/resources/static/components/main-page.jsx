class MainPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            mode: this.getMode(window.innerWidth),
            currentTab: 0
        }
    }

    _csrf = {
        parameter: document.querySelector("meta[name='_csrf_parameter']").getAttribute('content'),
        token: document.querySelector("meta[name='_csrf']").getAttribute('content')
    };

    render() {
        const header = [
            <div>Акатьев Никита Львович группа P3211<br/>Лабораторная №4</div>,
            <Timer interval="1000" />,
            <LogoutButton/>
        ];


        const canvasComp = <CanvasComponent history={this.props.history} _csrf={this._csrf} />;
        const historyComp = <HistoryComponent history={this.props.history} />;

        if (this.state.mode === 2)
            return <table style={{width: '100%'}}>
                <caption>
                    <DynamicTable className="header label centered" elems={header} rows="1" cols="3" colWidth={['40%', '40%', '20%']}/>
                </caption>
                <tbody><tr>
                    <td style={{width: '50%'}}>{canvasComp}</td>
                    <td style={{width: '50%'}}>{historyComp}</td>
                </tr></tbody>
            </table>;

        const toolbar = [
            <ToolBarTab tab={
                {
                    selected: this.state.currentTab === 0,
                    name: "Проверка",
                    onclick: () => this.handleTabClick(0)
                }
            }/>,
            <ToolBarTab tab={
                {
                    selected: this.state.currentTab === 1,
                    name: "История",
                    onclick: () => this.handleTabClick(1)
                }
            }/>,
            <LogoutButton/>
        ];

        if (this.state.mode === 1)
            return <table style={{width: '100%'}}>
                <tbody><tr>
                    <td style={{width: '25%', height: '100%'}}>
                        <DynamicTable className="label centered bordered-table" elems={toolbar} rows="3" cols="1" colWidth={['100%']} />
                    </td>
                    <td style={{width: '75%'}}>
                        {this.state.currentTab ? historyComp : canvasComp}
                    </td>
                </tr></tbody>
            </table>;

        return <div>
            <DynamicTable className="label centered bordered-table" elems={toolbar} rows="1" cols="3" colWidth={['40%', '40%', '20%']}/><br/>
            {this.state.currentTab ? historyComp : canvasComp}
        </div>;
    }

    getMode(width) {
        if (width >= 1210) return 2;
        if (width >= 674) return 1;
        return 0;
    }

    handleResize = () => {
        const newMode = this.getMode(window.innerWidth);
        if (this.state.mode === newMode) return;

        this.setState({
            mode: newMode,
            currentTab: this.state.currentTab
        });
    };

    handleTabClick = (index) => {
        this.setState({
            mode: this.state.mode,
            currentTab: index
        })
    };

    componentDidMount() {
        window.addEventListener('resize', this.handleResize);
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.handleResize);
    }
}

class LogoutButton extends React.Component {
    render() {
        return <form action="/logout" method="get">
            <input type="image" width="100" src="/img/logout.png" alt="Вернуться назад" onClick={this.handleClick}/>
        </form>
    }
}
