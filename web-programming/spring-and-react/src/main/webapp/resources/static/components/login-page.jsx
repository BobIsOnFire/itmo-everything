class LoginPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            currentTab: 0
        };
    }

    _csrf = {
        parameter: document.querySelector("meta[name='_csrf_parameter']").getAttribute('content'),
        token: document.querySelector("meta[name='_csrf']").getAttribute('content')
    };

    render() {
        const tabs = [
            <ToolBarTab tab={
                {
                    selected: this.state.currentTab === 0,
                    name: "Авторизация",
                    onclick: () => this.handleClick(0)
                }
            }/>,
            <ToolBarTab tab={
                {
                    selected: this.state.currentTab === 1,
                    name: "Регистрация",
                    onclick: () => this.handleClick(1)
                }
            }/>
        ];

        const header = [
            <div>Акатьев Никита Львович группа P3211<br/>Лабораторная №4</div>,
            <Timer interval="1000" /> // todo theme switch button?
        ];

        const form = this.state.currentTab ? <RegisterForm _csrf={this._csrf} /> : <LoginForm _csrf={this._csrf} />;

        return <div>
            <DynamicTable className="header label centered" elems={header} rows="1" cols="2" colWidth={['50%', '50%']}/>
            <div className="login-app absolute-center centered">
                <DynamicTable className="toolbar centered" elems={tabs} rows="1" cols="2" colWidth={['50%', '50%']} />
                {form}
            </div>
        </div>
    }

    handleClick(i) {
        this.setState({
            currentTab: i
        });
    }
}
