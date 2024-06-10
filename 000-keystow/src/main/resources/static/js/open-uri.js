var Keystow = Keystow || {};

Keystow.OpenUris = (function () {

    function OpenUris() {
        $(document).on('click', '.js-btn-abrir-uri', abrirUri.bind(this));
    }

    function abrirUri(evento) {

        evento.preventDefault();

        let uriInput = $(evento.target).closest('.input-group').find('.js-uri-input-valor');
        let valorUri = uriInput.val();

        if (!valorUri.startsWith('http://') && !valorUri.startsWith('https://')) {
            valorUri = 'https://' + valorUri;
        }

        if (isValidURL(valorUri)) {
            window.open(valorUri, '_blank', 'noopener');
        } else {
            let msgErro = 'URL inválida: ' + valorUri;
            $('.toast .toast-body').text(msgErro);
            $('.toast').toast('show');
        }
    }

    function isValidURL(string) {

        let url;

        try {
            url = new URL(string);
        } catch (_) {
            try {
                url = new URL('http://' + string);
            } catch (_) {
                return false;
            }
        }

        return url.hostname.includes('.com');
    }

    // function isValidURL(string) {
    //     let res = string.match(/(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/);
    //     return (res !== null)
    // }

    return OpenUris;

}());

$(function () {
    const openUri = new Keystow.OpenUris();
});
