Keystow = Keystow || {};

Keystow.MultiSelecaoUsuario = (function () {

    function MultiSelecaoUsuario() {
        this.btnStatus = $('.js-status-btn');
        this.checkboxSelecionarUserId = $('.js-checkbox-selecionar-usuario-id');
        this.checkboxSelecionarTodosAll = $('.js-checkbox-selecionar-todos');
        this.btnExcluirSelecionados = $('.js-excluir-selecionados-btn');
    }

    MultiSelecaoUsuario.prototype.iniciar = function () {
        this.btnStatus.on('click', onStatusBtnClicado.bind(this));
        this.checkboxSelecionarTodosAll.on('click', onSelecaoTodosClicado.bind(this));
        this.checkboxSelecionarUserId.on('click', onSelecaoClicado.bind(this));
        this.btnExcluirSelecionados.on('click', onExcluirSelecionadosBtnClicado.bind(this));
    }

    function onExcluirSelecionadosBtnClicado(event) {
        event.preventDefault();
        let btnExcluir = $(event.currentTarget);
        let url = btnExcluir.data('url');

        let checkboxSelecionados = this.checkboxSelecionarUserId.filter(':checked');
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
                html: '<div class="text-center my-1">Você vai excluir o(s) usuário(s) selecionado(s)?</div>' +
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
                swalSuccessExcluir('Usuarios excluídos com sucesso!');
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

    function onStatusBtnClicado(event) {
        let btnClicarStatus = $(event.currentTarget);

        let status = btnClicarStatus.data('status');
        let url = btnClicarStatus.data('url');

        let checkboxSelecionados = this.checkboxSelecionarUserId.filter(':checked');
        let ids = $.map(checkboxSelecionados, function (i) {
            return $(i).data('id').toString();
        });

        let statusUpdateRequestAjax = {
            ids: ids,
            status: status
        };

        if (ids.length > 0) {
            fazerRequestAjax('PUT', url, statusUpdateRequestAjax).then(() => {
                console.log('THEN - FUNÇÃO SUCCESS DO AJAX: \n\n', statusUpdateRequestAjax);
                window.location.reload();
            });
        }
    }

    function onSelecaoTodosClicado() {
        let status = this.checkboxSelecionarTodosAll.prop('checked');
        this.checkboxSelecionarUserId.prop('checked', status);
        btnStatusAction.call(this, status);
    }

    function onSelecaoClicado() {
        let checkboxSelecionadosChecked = this.checkboxSelecionarUserId.filter(':checked');
        this.checkboxSelecionarTodosAll.prop('checked', checkboxSelecionadosChecked.length >= this.checkboxSelecionarUserId.length);
        btnStatusAction.call(this, checkboxSelecionadosChecked.length);
    }

    function btnStatusAction(ativar) {
        ativar ? this.btnStatus.removeClass('disabled') : this.btnStatus.addClass('disabled');
        ativar ? this.btnExcluirSelecionados.removeClass('disabled') : this.btnExcluirSelecionados.addClass('disabled');
    }

    const fazerRequestAjax = async (method, url, data) => {
        try {
            return await $.ajax({
                contentType: 'application/json',
                type: method,
                url: url,
                data: JSON.stringify(data),
                success: (response) => {
                    console.log(JSON.stringify(data));
                    console.log(response);
                    console.log('TRY - FUNÇÃO SUCCESS DO AJAX: \n\n', data);
                },
                error: (jqXHR, textStatus, errorThrown, response) => {
                    console.log(JSON.stringify(data));
                    console.log(response);
                    console.log('TRY - ERROR DO AJAX: \n\n', textStatus, errorThrown);
                }
            });
        } catch (error) {
            console.error('CATCH - ERROR NA CHAMADA AJAX: ', error);
        }
    };

    return MultiSelecaoUsuario;

}

());

$(function () {
    let multiSelecaoUsuario = new Keystow.MultiSelecaoUsuario();
    multiSelecaoUsuario.iniciar();
});
