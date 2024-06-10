var Keystow = Keystow || {};

Keystow.CadastrarPastaModalDialog = (() => {

    const divModal = $('#addPastaModal');
    const form = divModal.find('form');
    const btnSalvar = divModal.find('.js-btn-submit-modal-pasta-salvar');
    const url = form.attr('action');
    const inputNomePasta = $('.js-input-nome-pasta-modal');
    const divAlertMsgErro = $('.js-div-msg-nome-pasta-modal');
    const spanMsgErro = $('.js-span-erro-msg-nome-pasta-modal');

    const iniciar = () => {
        form.on('submit', event => event.preventDefault());
        divModal.on('shown.bs.modal', onModalShow);
        divModal.on('hide.bs.modal', onModalClose);
        btnSalvar.on('click', onBtnSalvarClick);
    }

    const fazerRequestAjax = async (method, url, data) => {
        try {
            return await $.ajax({
                contentType: 'application/json',
                type: method,
                url: url,
                data: JSON.stringify(data)
            });
        } catch (error) {
            console.error('CATCH - ERROR NA CHAMADA AJAX: ', error);
            throw error;
        }
    };

    const onModalShow = () => {
        inputNomePasta.focus();
    }

    const onModalClose = () => {
        inputNomePasta.val('');
        divAlertMsgErro.addClass('d-none');
        form.find('.form-control').removeClass('is-invalid');
    }

    const onBtnSalvarClick = async () => {
        let pastaFormDto = {
            nome: inputNomePasta.val().trim()
        }

        try {
            const response = await fazerRequestAjax('POST', url, pastaFormDto);
            onPastaSalva(response);
        } catch (error) {
            onErroSalvandoPasta(error);
        }
    }

    const onPastaSalva = response => {
        const comboPasta = $('#pasta');
        comboPasta.append('<option value=' + response.id + '>' + response.nome + '</option>');
        comboPasta.val(response.id);
        divModal.modal('hide');
        onPastaSalvaComSucesso();
    }

    const onPastaSalvaComSucesso = () => {
        Swal.fire({
            position: 'top',
            title: 'Pronto!',
            text: 'Pasta salva com sucesso!',
            icon: 'success'
        })
    }

    const onErroSalvandoPasta = error => {
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
    Keystow.CadastrarPastaModalDialog.iniciar();
});
