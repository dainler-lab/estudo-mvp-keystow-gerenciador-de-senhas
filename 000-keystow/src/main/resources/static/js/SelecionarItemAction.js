Keystow = Keystow || {};

Keystow.SelecionarItemAction = (function () {

    function SelecionarItemAction() {
        this.btnAppHomeMarcarTodos = $('.js-btn-home-marcar-todos');
        this.btnAppHomeDesmarcarTodos = $('.js-btn-home-desmarcar-todos');

        this.btnAppHomeExcluirMarcados = $('.js-btn-home-excluir-selecionados');

        this.btnAppHomeFavoritar = $('.js-btn-app-home-favoritar');
        // this.inputFavorito = $('.js-input-check-app-home-favorito');

        this.btnAction = $('.js-action-btn');
        this.checkboxSelecionarItemId = $('.js-checkbox-selecionar-item-id');
        this.checkboxSelecionarTodosAll = $('.js-checkbox-selecionar-todos');
        this.btnExcluirSelecionados = $('.js-excluir-selecionados-btn');
    }

    SelecionarItemAction.prototype.iniciar = function () {
        $(document).ready(function () {
            let msg = sessionStorage.getItem('toastMessage');
            if (msg) {
                $('.toast .toast-body').text(msg);
                $('.toast').toast('show');
                sessionStorage.removeItem('toastMessage');
            }
        });

        this.btnAppHomeMarcarTodos.on('click', onMarcarTodosClicadoAppHome.bind(this));
        this.btnAppHomeDesmarcarTodos.on('click', onDesmarcarTodosClicadoAppHome.bind(this));
        this.btnAppHomeExcluirMarcados.on('click', onExcluirSelecionadosBtnClicado.bind(this));
        this.btnAppHomeFavoritar.on('click', onFavoritarBtnClicado.bind(this));

        this.btnAction.on('click', onActionBtnClicado.bind(this));
        this.checkboxSelecionarTodosAll.on('click', onSelecaoTodosClicado.bind(this));
        this.checkboxSelecionarItemId.on('click', onSelecaoClicado.bind(this));
        this.btnExcluirSelecionados.on('click', onExcluirSelecionadosBtnClicado.bind(this));
    }

    const fazerRequestAjax = async (method, url, data) => {
        try {
            return await $.ajax({
                contentType: 'application/json',
                type: method,
                url: url,
                data: JSON.stringify(data),
                // success: (response) => {
                //     console.log(JSON.stringify(data));
                //     console.log(response);
                //     console.log('TRY - FUNÇÃO SUCCESS DO AJAX: \n\n', data);
                // },
                // error: (jqXHR, textAction, errorThrown, response) => {
                //     console.log(JSON.stringify(data));
                //     console.log(response);
                //     console.log('TRY - ERROR DO AJAX: \n\n', textAction, errorThrown);
                // }
            });
        } catch (error) {
            console.error('CATCH - ERROR NA CHAMADA AJAX: ', error);
        }
    };

    // -------------------------------------- EXCLUIR SELECIONADOS --------------------------------------\\

    function onExcluirSelecionadosBtnClicado(event) {
        event.preventDefault();
        let btnExcluir = $(event.currentTarget);
        let url = btnExcluir.data('url');

        let checkboxSelecionados = this.checkboxSelecionarItemId.filter(':checked');
        let ids = $.map(checkboxSelecionados, function (i) {
            return $(i).data('id').toString();
        });

        let excluirItensSelecionadosRequestAjax = {
            ids: ids
        }

        if (ids.length > 0) {

            Swal.fire({
                position: 'top',
                icon: "question",
                iconColor: "#c6a11b",
                title: 'Tem certeza?',
                html: '<div class="text-center my-1">Você vai excluir o item ou os itens selecionados?</div>' +
                    '<div class="text-center">Você não poderá reverter isso!</div> ',
                showCancelButton: true,
                confirmButtonColor: '#1b60c6',
                cancelButtonColor: "#d33",
                confirmButtonText: 'Sim, exclua agora!'
            }).then((result) => {
                if (result.isConfirmed) {
                    onExcluirConfirmado.call(this, url, excluirItensSelecionadosRequestAjax);
                }
            });

        }
    }

    function onExcluirConfirmado(url, excluirItensSelecionadosRequestAjax) {
        fazerRequestAjax('DELETE', url, excluirItensSelecionadosRequestAjax)
            .then(() => {
                // console.log('THEN - FUNÇÃO SUCCESS DO AJAX: \n\n', excluirItensSelecionadosRequestAjax);
                swalSuccessExcluir('Itens excluídos com sucesso!');
            })
            .catch((error) => {
                swalErrorExcluir(error.responseText);
            });
    }

    function swalSuccessExcluir(message) {
        Swal.fire({
            position: 'top',
            title: 'Pronto!',
            text: message,
            icon: 'success'
        }).then(() => {
            setTimeout(function () {
                window.location.reload();
            }, 1);
        });
    }

    function swalErrorExcluir(message) {
        Swal.fire({
            position: 'top',
            icon: 'error',
            title: 'Oops!',
            html: '<div class="alert alert-danger alert-dismissible text-center my-4" role="alert">' + message.responseText + '</div>',
            confirmButtonText: 'Fechar'
        });
    }

    // -------------------------------------- ACTION SELECIONADOS --------------------------------------\\

    function onMarcarTodosClicadoAppHome() {
        this.checkboxSelecionarItemId.prop('checked', true);
    }

    function onDesmarcarTodosClicadoAppHome() {
        this.checkboxSelecionarItemId.prop('checked', false);
    }

    function onFavoritarBtnClicado(event) {
        event.preventDefault();
        let btnFavoritar = $(event.currentTarget);
        let url = btnFavoritar.data('url');
        let inputCheckboxFavorito = btnFavoritar.find('.js-input-check-app-home-favorito');
        let id = inputCheckboxFavorito.data('id');
        let status = inputCheckboxFavorito.prop('checked') ? 'DESATIVAR' : 'ATIVAR';
        let itensFavoritosUpdateRequestAjax = {
            ids: [id],
            action: status
        };

        fazerRequestAjax('PUT', url, itensFavoritosUpdateRequestAjax).then(() => {
            let msg = 'Favorito atualizado com sucesso';
            sessionStorage.setItem('toastMessage', msg);
            window.location.reload();
        });
    }

    function onActionBtnClicado(event) {
        let btnClicarAction = $(event.currentTarget);
        let status = btnClicarAction.data('status');
        let url = btnClicarAction.data('url');
        let checkboxSelecionados = this.checkboxSelecionarItemId.filter(':checked');
        let ids = $.map(checkboxSelecionados, function (i) {
            return $(i).data('id').toString();
        });

        let itensFavoritosUpdateRequestAjax = {
            ids: ids,
            action: status
        };

        if (ids.length > 0) {
            fazerRequestAjax('PUT', url, itensFavoritosUpdateRequestAjax).then(() => {
                setTimeout(function () {
                    window.location.reload();
                }, 1);
            });
        }
    }

    function onSelecaoTodosClicado() {
        let status = this.checkboxSelecionarTodosAll.prop('checked');
        this.checkboxSelecionarItemId.prop('checked', status);
        btnAction.call(this, status);
    }

    function onSelecaoClicado() {
        let checkboxSelecionadosChecked = this.checkboxSelecionarItemId.filter(':checked');
        this.checkboxSelecionarTodosAll.prop('checked', checkboxSelecionadosChecked.length >= this.checkboxSelecionarItemId.length);
        btnAction.call(this, checkboxSelecionadosChecked.length);
    }

    function btnAction(ativar) {
        ativar ? this.btnAction.removeClass('disabled') : this.btnAction.addClass('disabled');
        ativar ? this.btnExcluirSelecionados.removeClass('disabled') : this.btnExcluirSelecionados.addClass('disabled');
    }

    return SelecionarItemAction;

}());

$(function () {
    let SelecionarItemAction = new Keystow.SelecionarItemAction();
    SelecionarItemAction.iniciar();
});
