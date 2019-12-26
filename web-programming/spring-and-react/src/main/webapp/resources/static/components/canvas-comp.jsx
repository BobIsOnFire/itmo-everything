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
        this.updateWithMessage = this.updateWithMessage.bind(this);
        this.handleCanvasClick = this.handleCanvasClick.bind(this);
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
            <canvas id="canvas" height="500" width="500" onClick={this.handleCanvasClick}>Damn yo browser is old man...</canvas>
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
                <br/><br/>

                <div>R:</div>
                <DynamicTable className="label centered bordered-table" elems={rRadios} rows="3" cols="3" colWidth={['33%', '33%', '33%']}/>
                <br/>

                <input type="submit" className="button" value="Проверить точку"/>
            </form>
        </div>
    }

    handleChange(event) {
        let state = this.state;
        state[event.target.name] = event.target.value.substring(0, 20);
        this.setState(state);
    }

    handleSubmit(event) {
        event.preventDefault();

        if (this.state.yQuery === '') {
            this.updateWithMessage('Введите значение Y.');
            return;
        }

        if (this.state.rQuery === '0' || this.state.rQuery === 0) {
            this.updateWithMessage('Сначала выберите ненулевой R.');
            return;
        }

        const options = {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body:
                `${this.props._csrf.parameter}=${this.props._csrf.token}&` +
                `x=${this.state.xQuery}&y=${this.state.yQuery}&r=${this.state.rQuery}`
        };

        fetch('http://se.ifmo.ru:14900/history', options)
            .then(res => {
                if (res.ok) {
                    this.updateWithMessage('');
                    this.props.onUpdate();
                } else {
                    if (res.status === 403) {
                        window.location = '/login';
                        return;
                    }
                    if (res.status === 400) {
                        res.json().then(json => this.updateWithMessage(json.errors[0].defaultMessage));
                    }
                }
            });

    }

    updateWithMessage(msg) {
        this.setState({
            xQuery: '0',
            yQuery: '',
            rQuery: this.state.rQuery,
            message: msg
        });
    }

    componentDidMount() {
        this.paint.call(this);
    }

    componentDidUpdate() {
        this.paint.call(this);
    }

    paint() {
        const lightTheme = true;
        const radius = +this.state.rQuery;
        const canvas = document.getElementById('canvas');
        const context = canvas.getContext("2d");

        const bgColor = lightTheme ? "#FFF" : "#000";
        const gridColor = lightTheme ? "#BBB" : "#444";
        const areaColor = lightTheme ? "#39F" : "#780";
        const fgColor = lightTheme ? "#000" : "#FFF";

        const hitColor = "#0F0";
        const missColor = "#F00";
        const margin = 25;

        const coords = canvas.getBoundingClientRect();
        const width = coords.right - coords.left;
        const height = coords.bottom - coords.top;

        const centerX = width / 2;
        const centerY = height / 2;

        const pointDistance = Math.sign(radius + 0.5) * ( Math.min(width, height) - 2 * margin ) / 6; // distance between two closest points on axis

        const arrowLength = pointDistance / 3;
        const arrowWidth = pointDistance / 10;

        context.fillStyle = bgColor;
        context.fillRect(0, 0, width, height);

        // DRAWING areas
        context.fillStyle = areaColor;
        context.beginPath();

        // const startAngle = radius >= 0 ? Math.PI * 3 / 2 : Math.PI / 2;
        // const endAngle = radius >= 0 ? 0 : Math.PI;

        context.moveTo(centerX, centerY);
        context.lineTo(centerX, centerY - pointDistance);
        context.arcTo(centerX + pointDistance, centerY - pointDistance, centerX + pointDistance, centerY, Math.abs(pointDistance));
        context.lineTo(centerX, centerY + 2 * pointDistance);
        context.lineTo(centerX - 2 * pointDistance, centerY + 2 * pointDistance);
        context.lineTo(centerX - 2 * pointDistance, centerY);
        context.lineTo(centerX, centerY);
        context.fill();

        // DRAWING grid
        context.strokeStyle = gridColor;
        context.beginPath();
        for (let i = 0; i < 13; i++) {
            for (let j = 0; j < 13; j++) {
                let positionX = centerX + (i - 6) * pointDistance / 2;
                context.moveTo(positionX, margin);
                context.lineTo(positionX, height - margin);

                let positionY = centerY + (i - 6) * pointDistance / 2;
                context.moveTo(margin, positionY);
                context.lineTo(width - margin, positionY);
            }
        }
        context.stroke();

        const textSize = Math.abs(pointDistance) / 5;
        const textMarginX = textSize / 2;
        const textMarginY = textSize;

        // DRAWING axis and numbers
        context.strokeStyle = fgColor;
        context.fillStyle = fgColor;
        context.font = textSize + "px monospace";
        context.beginPath();

        // X axis (with arrows!)
        let lineStartX = margin;
        let lineEndX = width - margin;
        if (radius < 0) [lineStartX, lineEndX] = [lineEndX, lineStartX];

        context.moveTo(lineStartX, centerY);
        context.lineTo(lineEndX, centerY);
        context.lineTo(lineEndX - arrowLength, centerY - arrowWidth);
        context.moveTo(lineEndX, centerY);
        context.lineTo(lineEndX - arrowLength, centerY + arrowWidth);
        context.fillText("X", lineEndX + textMarginX, centerY);

        // Y axis (with arrows too!)
        let lineStartY = height - margin;
        let lineEndY = margin;
        if (radius < 0) [lineStartY, lineEndY] = [lineEndY, lineStartY];

        context.moveTo(centerX, lineStartY);
        context.lineTo(centerX, lineEndY);
        context.lineTo(centerX - arrowWidth, lineEndY + arrowLength);
        context.moveTo(centerX, lineEndY);
        context.lineTo(centerX + arrowWidth, lineEndY + arrowLength);
        context.fillText("Y", centerX + textMarginX, lineEndY);

        // points and texts on both axis
        let text = radius ?  [-radius, -radius / 2, 0, +radius / 2, +radius] : ['-R', '-R/2', '0', 'R/2', 'R'];

        for (let i = 0; i < 5; i++) {
            let positionX = centerX + Math.abs(pointDistance) * (i - 2);
            context.moveTo(positionX, centerY - arrowWidth);
            context.lineTo(positionX, centerY + arrowWidth);
            context.fillText(text[i], positionX + textMarginX, centerY + textMarginY);

            let positionY = centerY + Math.abs(pointDistance) * (i - 2);
            context.moveTo(centerX - arrowWidth, positionY);
            context.lineTo(centerX + arrowWidth, positionY);
            context.fillText(text[4 - i], centerX + textMarginX, positionY + textMarginY);
        }

        context.stroke();

        // DRAWING possible areas for x and y
        let [left, bottom] = this.convertCleanToCanvasCoordinates(-3, -3, Math.abs(radius), centerX, centerY, pointDistance);
        let [right, top] = this.convertCleanToCanvasCoordinates(5, 3, Math.abs(radius), centerX, centerY, pointDistance);

        if (radius < 0) [left, right, top, bottom] = [right, left, bottom, top];

        if (left == null) left = margin;
        if (top == null) top = margin;
        if (right == null) right = width - margin;
        if (bottom == null) bottom = height - margin;

        context.fillStyle = fgColor;
        context.globalAlpha = 0.2;

        context.fillRect(margin, margin, width - margin * 2, top - margin); // top box
        context.fillRect(margin, bottom, width - margin * 2, height - bottom - margin); // bottom box

        context.fillRect(margin, top, left - margin, bottom - top); // left box
        context.fillRect(right, top, width - right - margin, bottom - top); // right box

        context.globalAlpha = 1;

        // DRAWING history dots (or writing message for entering valid R)
        if (!radius) {
            context.font = pointDistance / 4 + "px monospace";
            context.fillText('Выберите ненулевой R', margin, margin + pointDistance / 3);
            return;
        }

        for (let i = 0; i < this.props.history.length; i++) {
            const node = this.props.history[i];
            let [dotX, dotY] = this.convertCleanToCanvasCoordinates(node.x, node.y, Math.abs(radius), centerX, centerY, pointDistance);
            if (dotX == null || dotY == null) continue;

            context.beginPath();
            context.moveTo(dotX, dotY);
            context.arc(dotX, dotY, 5, 0, Math.PI * 2);

            context.fillStyle = this.calculateHit(node.x, node.y, Math.abs(radius)) ? hitColor : missColor;
            context.fill();
        }
    }

    handleCanvasClick(event) {
        let r = +this.state.rQuery;
        if (!r) {
            this.updateWithMessage('Сначала выберите ненулевой R.');
            return;
        }

        let canvas = event.target;
        let coords = canvas.getBoundingClientRect();

        let canvasX = event.clientX - coords.left;
        let canvasY = event.clientY - coords.top;

        let width = coords.right - coords.left;
        let height = coords.bottom - coords.top;
        let centerX = width / 2;
        let centerY = height / 2;
        let pointDistance = ( Math.min(width, height) - 2 * 25 ) / 6;

        let [x, y] = this.convertCanvasToCleanCoordinates(canvasX, canvasY, r, centerX, centerY, pointDistance);

        this.state = {
            xQuery: x,
            yQuery: y,
            rQuery: r,
            message: ''
        };

        this.handleSubmit(event);
    }

    convertCleanToCanvasCoordinates(x, y, r, centerX, centerY, pointDistance) {
        let ratioX = x / r * 2;
        let ratioY = y / r * 2;

        let canvasX, canvasY;
        if (ratioX < -3 || ratioX > 3) canvasX = null;
        else canvasX = centerX + pointDistance * ratioX;

        if (ratioY < -3 || ratioY > 3) canvasY = null;
        else canvasY = centerY - pointDistance * ratioY;

        return [canvasX, canvasY];
    }

    convertCanvasToCleanCoordinates(canvasX, canvasY, r, centerX, centerY, pointDistance) {
        let ratioX = ( canvasX - centerX ) / pointDistance;
        let ratioY = ( centerY - canvasY ) / pointDistance;

        let x, y;
        if (ratioX < -3 || ratioX > 3) x = null;
        else x = ratioX / 2 * r;

        if (ratioY < -3 || ratioY > 3) y = null;
        else y = ratioY / 2 * r;

        return [x, y];
    }

    calculateHit(x, y, r) {
        if (x < 0) {
            if (y > 0) return false;
            return x >= -r && y >= -r;
        }

        if (y < 0) return y >= 2 * x - r;
        return x*x + y*y <= r*r / 4;
    }

}