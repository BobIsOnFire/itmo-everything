$(document).ready(function() {
    $(".X").click(function() {
        $(".X").removeClass("active");
        $(this).addClass("active");
        $("#X_field").val( $(this).val() );
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

    let lightTheme = true;
    $("#swapButton").click(function() {
        $("#csslink").attr("href", contextPath + ( lightTheme ? "dark.css" : "light.css" ));
        $(this).val(lightTheme ? "Oh Shit, Go White Again" : "OK, Maybe Black Ain't That Bad");
        $("#areas").css("filter", "invert(" + (lightTheme ? "100%" : "0%") + ")");
        lightTheme = !lightTheme;
    })
});
