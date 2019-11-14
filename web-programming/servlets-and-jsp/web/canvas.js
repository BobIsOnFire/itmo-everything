const MARGIN = 25;
let radius = null;

function paint(lightTheme, r=null) {
    radius = r;

    let canvas = $("#canvas");
    canvas.unbind();
    let context = canvas.get(0).getContext("2d");

    let bgColor = lightTheme ? "#FFF" : "#000";
    let gridColor = lightTheme ? "#BBB" : "#444";
    let fgColor = lightTheme ? "#39F" : "#780";
    let axisColor = lightTheme ? "#000" : "#FFF";
    let possibleAreaColor = lightTheme ? "#DDD" : "#222";

    let coords = canvas.get(0).getBoundingClientRect();
    let width = coords.right - coords.left;
    let height = coords.bottom - coords.top;

    let centerX = width / 2;
    let centerY = height / 2;

    let pointDistance = ( Math.min(width, height) - 2 * MARGIN ) / 6; // distance between two closest points on axis

    let arrowLength = pointDistance / 3;
    let arrowWidth = pointDistance / 10;

    let textSize = pointDistance / 5;
    let textMarginX = textSize / 2;
    let textMarginY = textSize;

    context.fillStyle = bgColor;
    context.fillRect(0, 0, width, height);

    // DRAWING possible areas for x and y and setting click event
    if (radius != null) {
        let [left, bottom] = convertCleanToCanvasCoordinates(-4, -3, radius, centerX, centerY, pointDistance);
        let [right, top] = convertCleanToCanvasCoordinates(4, 3, radius, centerX, centerY, pointDistance);

        if (left == null) left = MARGIN;
        if (top == null) top = MARGIN;
        if (right == null) right = width - MARGIN;
        if (bottom == null) bottom = height - MARGIN;

        context.fillStyle = possibleAreaColor;
        context.fillRect(MARGIN, MARGIN, width - 2 * MARGIN, height - 2 * MARGIN);
        context.fillStyle = bgColor;
        context.fillRect(left, top, right - left, bottom - top);

        canvas.click( function(e) {
            let coords = canvas.get(0).getBoundingClientRect();
            let canvasX = e.clientX - coords.left;
            let canvasY = e.clientY - coords.top;
            console.log(canvasX + " " + canvasY);

            if (canvasX < left || canvasX > right || canvasY <= top || canvasY >= bottom) {
                $("#message").html("Точка не входит в ОДЗ.");
                return;
            }

            let [x, y] = convertCanvasToCleanCoordinates(canvasX, canvasY, radius, centerX, centerY, pointDistance);
            let form = document.createElement("form");
            form.action = "area-check";
            form.method = "POST";

            form.innerHTML = `
                <input name="X" value="${Math.round(x)}">
                <input name="Y" value="${y}">
                <input name="R" value="${radius}">
                <input type="submit">
            `;

            document.body.appendChild(form);
            form.submit();
        } );
    }

    // DRAWING grid
    context.strokeStyle = gridColor;
    context.beginPath();
    for (let i = 0; i < 13; i++) {
        for (let j = 0; j < 13; j++) {
            let positionX = centerX + (i - 6) * pointDistance / 2;
            context.moveTo(positionX, MARGIN);
            context.lineTo(positionX, height - MARGIN);

            let positionY = centerY + (i - 6) * pointDistance / 2;
            context.moveTo(MARGIN, positionY);
            context.lineTo(width - MARGIN, positionY);
        }
    }
    context.stroke();

    // DRAWING areas
    context.fillStyle = fgColor;
    context.beginPath();

    context.moveTo(centerX, centerY);
    context.lineTo(centerX + 2 * pointDistance, centerY);
    context.lineTo(centerX + 2 * pointDistance, centerY + pointDistance);
    context.lineTo(centerX, centerY + pointDistance);
    context.arc(centerX, centerY, pointDistance, Math.PI / 2, Math.PI, false);
    context.lineTo(centerX - 2 * pointDistance, centerY);
    context.lineTo(centerX, centerY - 2 * pointDistance);
    context.fill();

    // DRAWING axis and numbers
    context.strokeStyle = axisColor;
    context.fillStyle = axisColor;
    context.font = textSize + "px monospace";
    context.beginPath();

    // X axis (with arrows!)
    context.moveTo(MARGIN, centerY);
    context.lineTo(width - MARGIN, centerY);
    context.lineTo(width - MARGIN - arrowLength, centerY - arrowWidth);
    context.moveTo(width - MARGIN, centerY);
    context.lineTo(width - MARGIN - arrowLength, centerY + arrowWidth);
    context.fillText("X", width - MARGIN + textMarginX, centerY);

    // Y axis (with arrows too!)
    context.moveTo(centerX, height - MARGIN);
    context.lineTo(centerX, MARGIN);
    context.lineTo(centerX - arrowWidth, MARGIN + arrowLength);
    context.moveTo(centerX, MARGIN);
    context.lineTo(centerX + arrowWidth, MARGIN + arrowLength);
    context.fillText("Y", centerX + textMarginX, MARGIN);

    // points and texts on both axis
    let text;
    if (radius == null) text = ["-R", "-R/2", "0", "R/2", "R"];
    else text = [-radius, -radius / 2, 0, radius / 2, radius];

    for (let i = 0; i < 5; i++) {
        let positionX = centerX + pointDistance * (i - 2);
        context.moveTo(positionX, centerY - arrowWidth);
        context.lineTo(positionX, centerY + arrowWidth);
        context.fillText(text[i], positionX + textMarginX, centerY + textMarginY);

        let positionY = centerY + pointDistance * (i - 2);
        context.moveTo(centerX - arrowWidth, positionY);
        context.lineTo(centerX + arrowWidth, positionY);
        context.fillText(text[4 - i], centerX + textMarginX, positionY + textMarginY);
    }

    context.stroke();

    // DRAWING history dots
    if (radius == null) return;
    let historyX = [];
    $(".history-x").each(function() { historyX.push( +$(this).text().substring(0, 15) ) });

    let historyY = [];
    $(".history-y").each(function() { historyY.push( +$(this).text().substring(0, 15) ) });

    context.beginPath();
    for (let i = 0; i < historyX.length; i++) {
        let [dotX, dotY] = convertCleanToCanvasCoordinates(historyX[i], historyY[i], radius, centerX, centerY, pointDistance);
        if (dotX == null || dotY == null) continue;

        context.moveTo(dotX, dotY);
        context.arc(dotX, dotY, 5, 0, Math.PI * 2);
    }
    context.fill();
}

function convertCleanToCanvasCoordinates(x, y, r, centerX, centerY, pointDistance) {
    let ratioX = x / r * 2;
    let ratioY = y / r * 2;

    let canvasX, canvasY;
    if (ratioX < -3 || ratioX > 3) canvasX = null;
    else canvasX = centerX + pointDistance * ratioX;

    if (ratioY < -3 || ratioY > 3) canvasY = null;
    else canvasY = centerY - pointDistance * ratioY;

    return [canvasX, canvasY];
}

function convertCanvasToCleanCoordinates(canvasX, canvasY, r, centerX, centerY, pointDistance) {
    let ratioX = ( canvasX - centerX ) / pointDistance;
    let ratioY = ( centerY - canvasY ) / pointDistance;

    let x, y;
    if (ratioX < -3 || ratioX > 3) x = null;
    else x = ratioX / 2 * r;

    if (ratioY < -3 || ratioY > 3) y = null;
    else y = ratioY / 2 * r;

    return [x, y];
}