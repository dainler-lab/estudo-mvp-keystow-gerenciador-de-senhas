var Keystow = Keystow || {};

Keystow.BuscarCollectionMembroUserModalDialog = (() => {

    const divModal = $('#addMembroUsuarioModal');
    const form = divModal.find('form');
    const inputNome = $('.js-input-nome-membro-usuario-modal');
    const btnSubmitPesquisar = divModal.find('.js-btn-submit-pesquisar-modal');
    const divTabelaContainerMembroUsuario = $('.js-tabela-container-membro-usuario-modal');
    const divAlertMsgErro = $('.js-div-alert-erro-modal');
    const spanMsgErro = $('.js-span-alert-erro-modal');
    const url = form.attr('action');

    const iniciar = () => {
        form.on('submit', event => event.preventDefault());
        divModal.on('shown.bs.modal', onModalShow);
        divModal.on('hide.bs.modal', onModalClose);
        btnSubmitPesquisar.on('click', onBtnPesquisarClick);
    }

    const fazerRequestAjax = async (method, url, data) => {
        try {
            return await $.ajax({
                contentType: 'application/json',
                type: method,
                url: url,
                data: data
            });
        } catch (error) {
            console.error('CATCH - ERROR NA CHAMADA AJAX: ', error);
            throw error;
        }
    };

    const onModalShow = () => {
        inputNome.focus();
    }

    const onModalClose = () => {
        inputNome.val('');
        divAlertMsgErro.addClass('d-none');
        form.find('.form-control').removeClass('is-invalid');
        divTabelaContainerMembroUsuario.addClass('d-none');
    }

    const onBtnPesquisarClick = async () => {
        const nome = inputNome.val().trim();
        if (nome.length < 2) {
            inputNome.addClass('is-invalid');
            form.find('.invalid-feedback').text('O nome deve ter no mínimo 3 caracteres.');
            return;
        }

        try {
            const response = await fazerRequestAjax('POST', url, nome);
            atualizarTabelaUsuarios(response);
        } catch (error) {
            onErroPesquisarUsuario(error);
        }
    }

    const atualizarTabelaUsuarios = responseHTML => {
        divAlertMsgErro.addClass('d-none');
        form.find('.form-control').removeClass('is-invalid');
        divTabelaContainerMembroUsuario.removeClass('d-none');
        divTabelaContainerMembroUsuario.html(responseHTML);
    }

    const onErroPesquisarUsuario = error => {
        divAlertMsgErro.removeClass('d-none');
        spanMsgErro.text(error.responseText);
        form.find('.form-control').addClass('is-invalid');
        form.find('.invalid-feedback').text(error.responseText);
    }

    return {
        iniciar
    };

})();

$(function () {
    Keystow.BuscarCollectionMembroUserModalDialog.iniciar();
});
