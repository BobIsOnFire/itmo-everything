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
        return <table className="toolbar centered"><tr>{model}</tr></table>;
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

class MainApp extends React.Component {
    render() {
        return <div>User id: {this.props.id}<br/>History: {this.props.history}</div>
    }
}

function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([.$?*|{}()\[\]\\\/+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : null;
}

// todo put in different script file
(function() {
        let id = getCookie("user_id");
        let history = null;
        if (id != null)
            fetch("http://localhost:14900/api/history/get/" + id)
                .then(
                    res => history = res,
                    error => console.log(error)
                );

        if (id == null || history == null)
            ReactDOM.render(<LoginApp/>, document.getElementById("root"));
        else
            ReactDOM.render(<MainApp id={id} history={history} />, document.getElementById("root"));
    }
)();