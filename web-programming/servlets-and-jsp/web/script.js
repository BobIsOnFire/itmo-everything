$(document).ready(function() {
    let lightTheme = true;
    $(".X").click(function() {
        $(".X").removeClass("active");
        $(this).addClass("active");
        $("#X_field").val( $(this).val() );
    });

    $(".R").click(function() {
        let html = "";
        let checked = $(".R:checked");
        checked.each( function() {
            let val = $(this).val();
            html += `<option class="radius" value="${val}">${val}</option>`
        } );

        if (checked.length > 0) paint(lightTheme, +checked.val());
        else paint(lightTheme);

        $("#radius-selector").html(html);
    });

    $("#radius-selector").on("change", function(e) {
        paint(lightTheme, +e.target.value);
    });

    $("#main-form").on("submit", function() {
        let text = $("#textfield").val().substr(0, 16);
        if (text === "" || isNaN(text) || +text <= -3 || +text >= 3) {
            $("#message").html("Введите корректное значение Y.");
            return false;
        }

        let radiusSet = false;
        $(".R:checked").each(() => radiusSet = true);
        if (!radiusSet) {
            $("#message").html("Выберите одно или более значение R.");
            return false;
        }
        return true;
    });

    $("#timer").html( "Текущее время: " + new Date().toLocaleString() );
    setInterval(() => {
        $("#timer").html( "Текущее время: " + new Date().toLocaleString());
    }, 1000);

    $("#swapButton").click(function() {
        $("#csslink").attr("href", contextPath + ( lightTheme ? "dark.css" : "light.css" ));
        $(this).val(lightTheme ? "Oh Shit, Go White Again" : "OK, Maybe Black Ain't That Bad");
        $("#areas").css("filter", "invert(" + (lightTheme ? "100%" : "0%") + ")");
        lightTheme = !lightTheme;
        paint(lightTheme);
    });

    paint(lightTheme);
});
