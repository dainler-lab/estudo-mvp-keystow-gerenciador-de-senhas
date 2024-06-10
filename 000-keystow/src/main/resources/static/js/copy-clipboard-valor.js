var Keystow = Keystow || {};

Keystow.CopyClipboardValor = (function () {

    function CopyClipboardValor() {
        $(document).on('click', '.js-btn-copiar-valor', copiarClipboardValor.bind(this));
    }

    function copiarClipboardValor(evento) {

        evento.preventDefault();

        let inputAssociado = $(evento.target).closest('.input-group').find('.js-input-clipboard');
        let valorInput = inputAssociado.val();

        navigator.clipboard.writeText(valorInput).then(function () {
            $('.toast .toast-body').text('Valor copiado com sucesso');
            $('.toast').toast('show');
        }, function (err) {
            // console.error('Falha ao copiar o valor: ', err);
        });
    }

    return CopyClipboardValor;

}());

$(function () {
    const copyClipboardValor = new Keystow.CopyClipboardValor();
});
