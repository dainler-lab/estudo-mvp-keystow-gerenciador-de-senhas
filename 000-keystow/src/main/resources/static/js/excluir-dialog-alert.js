var Keystow = Keystow || {};

Keystow.DialogoExcluir = (function () {

    function DialogoExcluir() {
        this.exclusaoBtn = $('.js-excluir-btn')
        this.btnExcluirPermanentemente = $('.js-btn-excluir-permanentemente');
    }

    DialogoExcluir.prototype.iniciar = function () {
        this.exclusaoBtn.on('click', onExcluirClicado.bind(this));
        this.btnExcluirPermanentemente.on('click', onExcluirClicado.bind(this));
    }

    function onExcluirClicado(evento) {
        evento.preventDefault();
        let btnClick = $(evento.currentTarget);
        let url = btnClick.data('url');
        let objeto = btnClick.data('objeto');

        Swal.fire({
            position: 'top',
            icon: "question",
            iconColor: "#c6a11b",
            title: 'Tem certeza?',
            html: '<div class="text-center my-1">Você vai excluir "' + objeto + '"?</div>' +
                '<div class="text-center">Você não poderá reverter isso!</div> ',
            showCancelButton: true,
            confirmButtonColor: '#1b60c6',
            cancelButtonColor: "#d33",
            confirmButtonText: 'Sim, exclua agora!'
        }).then((result) => {
            if (result.isConfirmed) {
                onExcluirConfirmado.bind(this, url)();
            }
        });
    }

    function onExcluirConfirmado(url) {
        $.ajax({
            url: url,
            method: 'DELETE',
            success: onExcluidoComSucesso.bind(this),
            error: onErroAoExcluir.bind(this),
        });
    }

    function onExcluidoComSucesso() {
        Swal.fire({
            position: 'top',
            title: 'Pronto!',
            text: 'Excluído com sucesso!',
            icon: 'success'
        }).then(() => {
            let urlAtual = window.location.href;
            urlAtual = urlAtual.replace(/&excluido.*/, ''); // Remove '&excluido' e tudo após ele da URL
            setTimeout(function () {
                // window.location.href = '/pasta';
                window.location.reload();
            }, 1);
        });
    }

    function onErroAoExcluir(e) {
        Swal.fire({
            position: 'top',
            icon: 'error',
            title: 'Oops!',
            html: '<div class="alert alert-danger alert-dismissible text-center mt-3" role="alert">' + e.responseText + '</div>',
            confirmButtonText: 'Fechar'
        });
    }

    return DialogoExcluir;

}());

$(function () {
    const dialogo = new Keystow.DialogoExcluir();
    dialogo.iniciar();
});
