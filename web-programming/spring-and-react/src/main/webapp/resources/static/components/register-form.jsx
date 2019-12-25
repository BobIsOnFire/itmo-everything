class RegisterForm extends React.Component {
    constructor(props) {
        super(props);
        console.log('constructor');
        this.state = {
            username: '',
            password: '',
            passwordAgain: '',
            message: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        return <div className="label">
            <div className="message">{this.state.message}</div>
            <form onSubmit={this.handleSubmit} action="/register" method="post">
                <input type="hidden" name={this.props._csrf.parameter} value={this.props._csrf.token}/>
                <label>
                    Логин:<br/>
                    <input type="text" id="username" className="textfield" name="username" value={this.state.username} onChange={this.handleChange}/>
                </label>
                <br/><br/>
                <label>
                    Пароль:<br/>
                    <input type="password" id="password" className="textfield" name="password" value={this.state.password} onChange={this.handleChange}/>
                </label>
                <br/><br/>
                <label>
                    Пароль еще раз:<br/>
                    <input type="password" id="passwordAgain" className="textfield" value={this.state.passwordAgain} onChange={this.handleChange}/>
                </label>
                <br/><br/>
                <input type="submit" className="button" value="Регистрация"/>
            </form>
        </div>
    }

    handleChange(event) {
        let state = this.state;
        state[event.target.id] = event.target.value;
        this.setState(state);
    }

    handleSubmit(event) {
        if (this.state.username === '' || this.state.password === '') {
            this.updateWithMessage('Заполните все необходимые поля.');
            event.preventDefault();
            return false;
        }

        if (this.state.password !== this.state.passwordAgain) {
            this.updateWithMessage('Пароли различаются.');
            event.preventDefault();
            return false;
        }

        return true;
    }

    updateWithMessage(msg) {
        this.setState({
            username: this.state.username,
            password: '',
            passwordAgain: '',
            message: msg
        });
    }
}
