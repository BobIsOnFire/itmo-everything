const MARGIN = 25;
function paint(lightTheme, radius=null) {
    let canvas = document.getElementById("canvas");
    let context = canvas.getContext("2d");

    let bgColor = lightTheme ? "#FFF" : "#000";
    let gridColor = lightTheme ? "#DDD" : "#222";
    let fgColor = lightTheme ? "#39F" : "#780";
    let axisColor = lightTheme ? "#000" : "#FFF";

    let coords = canvas.getBoundingClientRect();
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
        context.fillText(text[i], centerX + textMarginX, positionY + textMarginY);
    }

    context.stroke();
}
