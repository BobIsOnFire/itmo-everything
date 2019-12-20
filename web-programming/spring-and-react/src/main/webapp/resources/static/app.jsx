class ToolBarTab extends React.Component {
    render() {
        let tab = this.props.tab;
        let styleClass = (tab.selected ? "tab-selected " : "") + "label";
        return (
            <td>
            <div className={styleClass} onClick={tab.onclick}>
            {tab.name}
            </div>
            </td>
    );
    }
}

class HorizontalToolBar extends React.Component {
    render() {
        let model = this.props.tabs.map(tab => <ToolBarTab tab={tab} />);
        return <table className="toolbar"><tr>{model}</tr></table>;
    }
}

class LoginApp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            currentTab: 0
        };
    }

    render() {
        let tabs = [
            {
                selected: this.state.currentTab === 0,
                name: "Авторизация",
                onclick: () => this.handleClick(0)
            },
            {
                selected: this.state.currentTab === 1,
                name: "Регистрация",
                onclick: () => this.handleClick(1)
            }
        ];
        let form;
        if (this.state.currentTab === 0)
            form = <LoginForm />;
        else
            form = <RegisterForm />;

        return <div>
            <Header />
                <div className="login-app absolute-center centered">
                    <HorizontalToolBar tabs={tabs} />
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

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        console.log('constructor');
        this.state = {
            login: '',
            password: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        return <div className="label">
            <form name="login-form" onSubmit={this.handleSubmit}>
            <label>
            Логин:<br/>
        <input type="text" className="textfield" name="login" value={this.state.login} onChange={this.handleChange}/>
        </label>
        <br/><br/>
        <label>
        Пароль:<br/>
        <input type="password" className="textfield" name="password" value={this.state.password} onChange={this.handleChange}/>
        </label>
        <br/><br/>
        <input type="submit" className="button" value="Вход"/>
            </form>
            </div>
    }

    handleChange(event) {
        let state = this.state;
        state[event.target.name] = event.target.value;
        this.setState(state);
    }

    handleSubmit(event) {
        console.log(this.state.login);
        console.log(this.state.password);
        event.preventDefault();
    }
}

class RegisterForm extends React.Component {
    constructor(props) {
        super(props);
        console.log('constructor');
        this.state = {
            login: '',
            password: '',
            passwordAgain: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        return <div className="label">
            <form name="login-form" onSubmit={this.handleSubmit}>
            <label>
            Логин:<br/>
        <input type="text" className="textfield" name="login" value={this.state.login} onChange={this.handleChange}/>
        </label>
        <br/><br/>
        <label>
        Пароль:<br/>
        <input type="password" className="textfield" name="password" value={this.state.password} onChange={this.handleChange}/>
        </label>
        <br/><br/>
        <label>
        Пароль еще раз:<br/>
        <input type="password" className="textfield" name="passwordAgain" value={this.state.passwordAgain} onChange={this.handleChange}/>
        </label>
        <br/><br/>
        <input type="submit" className="button" value="Регистрация"/>
            </form>
            </div>
    }

    handleChange(event) {
        let state = this.state;
        state[event.target.name] = event.target.value;
        this.setState(state);
    }

    handleSubmit(event) {
        console.log(this.state.login);
        console.log(this.state.password === this.state.passwordAgain);
        event.preventDefault();
    }
}

class Header extends React.Component {
    render() {
        return <table className="header label centered">
            <tr>
            <td><div>Акатьев Никита Львович группа P3211<br/>Лабораторная №4</div></td>
        <td><Timer interval="1000" /></td>
            </tr>
            </table>;
    }
}

class Timer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            time: new Date().toLocaleString()
        };
        setInterval( () => this.setState( {time: new Date().toLocaleString()} ), +props.interval );
    }

    render() {
        return <div>Текущее время:<br/>{this.state.time}</div>;
    }
}

ReactDOM.render(<LoginApp />, document.getElementById("root"));