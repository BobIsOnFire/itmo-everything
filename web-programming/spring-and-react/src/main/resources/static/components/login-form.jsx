class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            message: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        return <div className="label">
            <div className="message">{this.state.message}</div>
            <form onSubmit={this.handleSubmit} action="/login" method="post">
                <input type="hidden" name={this.props._csrf.parameter} value={this.props._csrf.token}/>
                <label>
                    Логин:<br/>
                    <input type="text" className="textfield" name="username" value={this.state.username} onChange={this.handleChange}/>
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
        if (this.state.username === '' || this.state.password === '') {
            this.updateWithMessage('Заполните все необходимые поля.');
            event.preventDefault();
            return false;
        }

        return true;
    }

    updateWithMessage(msg) {
        this.setState({
            username: this.state.username,
            password: '',
            message: msg
        });
    }
}
