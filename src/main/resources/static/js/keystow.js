var Keystow = Keystow || {};

Keystow.Main = (function () {

    function Main() {

    }

    Main.prototype.iniciar = function () {

    }

    return Main;

}());

Keystow.Security = (function () {
    function Security() {
        this.token = $('input[name=_csrf]').val();
        this.header = $('input[name=_csrf_header]').val();
    }

    Security.prototype.iniciar = function () {
        $(document).ajaxSend(function (event, jqxhr, settings) {
            jqxhr.setRequestHeader(this.header, this.token);
        }.bind(this));
    }
    return Security;
}());

$(function () {

    const keystowMain = new Keystow.Main();
    keystowMain.iniciar();

    const keystowSecurity = new Keystow.Security();
    keystowSecurity.iniciar();

    $('.js-tooltip').tooltip();
    $('[data-bs-toggle="popover"]').popover();
    $('.js-status').bootstrapSwitch();

});
