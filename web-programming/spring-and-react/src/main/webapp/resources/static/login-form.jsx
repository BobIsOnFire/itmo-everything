class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        console.log('constructor');
        this.state = {
            login: '',
            password: '',
            message: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleAuthorizeSuccess = this.handleAuthorizeSuccess.bind(this);
    }

    render() {
        return <div className="label">
            <div className="message">{this.state.message}</div>
            <form onSubmit={this.handleSubmit}>
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
        event.preventDefault();
        if (this.state.login === '' || this.state.password === '') {
            this.updateWithMessage('Заполните все необходимые поля.');
            return;
        }

        fetch(`http://localhost:14900/api/user/authorize?userName=${this.state.login}&password=${this.state.password}`)
            .then(res => res.text())
            .then(this.handleAuthorizeSuccess)
            .catch(error => {
                this.updateWithMessage('Ошибка обмена данных с сервером.');
                console.log(error);
            });
    }

    handleAuthorizeSuccess(res) {
        let id = +res;
        if (id === -1) {
            this.updateWithMessage('Пользователь не найден.');
            return;
        }

        if (id === 0) {
            this.updateWithMessage('Неверный пароль.');
            return;
        }

        fetch("http://localhost:14900/api/history/get/" + id)
            .then(res => res.text())
            .then(text => {
                    document.cookie = 'user_id=' + id +'; max-age=86400';
                    ReactDOM.render(<MainApp id={id} history={JSON.parse(text)} />, document.getElementById("root"));
                }
            )
            .catch(error => {
                this.updateWithMessage('Ошибка обмена данных с сервером.');
                console.log(error);
            });
    }

    updateWithMessage(msg) {
        this.setState({
            login: this.state.login,
            password: '',
            message: msg
        });
    }
}
