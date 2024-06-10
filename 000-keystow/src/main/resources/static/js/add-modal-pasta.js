var Keystow = Keystow || {};

Keystow.PastaCadastroModal = (function () {

    function PastaCadastroModal() {
        this.divModal = $('#addPastaModal');
        this.form = this.divModal.find('form');

        this.btnSalvar = this.divModal.find('.js-btn-submit-modal-pasta-salvar');
        this.url = this.form.attr('action');
        this.inputNomePasta = $('.js-input-nome-pasta-modal');
        this.divAlertMsgErro = $('.js-div-msg-nome-pasta-modal');
    }

    PastaCadastroModal.prototype.iniciar = function () {

        this.form.on('submit', function (event) {
            event.preventDefault()
        });

        this.divModal.on('shown.bs.modal', onModalShow.bind(this));
        this.divModal.on('hide.bs.modal', onModalClose.bind(this));
        this.btnSalvar.on('click', onBtnSalvarClick.bind(this));
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

    function onModalShow() {
        this.inputNomePasta.focus();
    }

    function onModalClose() {
        this.inputNomePasta.val('');
        this.divAlertMsgErro.addClass('d-none');
        this.form.find('.form-control').removeClass('is-invalid');
    }

    function onBtnSalvarClick() {
        let pastaFormDto = {
            nome: this.inputNomePasta.val().trim()
        }

        // fazerRequestAjax('POST', this.url, pastaFormDto)
        //     .then((response) => {
        //         console.log("RESPONSE", response);
        //         onPastaSalva().bind(this);
        //     })
        //     .catch((error) => {
        //         onErroSalvandoPasta(error);
        //     });

        $.ajax({
            contentType: 'application/json',
            method: 'POST',
            url: this.url,
            data: JSON.stringify(pastaFormDto),
            success: onPastaSalva.bind(this),
            error: onErroSalvandoPasta.bind(this)
        });
    }

    function onPastaSalva(response) {
        const comboPasta = $('#pasta');
        comboPasta.append('<option value=' + response.id + '>' + response.nome + '</option>');
        comboPasta.val(response.id);
        this.divModal.modal('hide');
        // this.divModal.hide();
        // $(".modal-backdrop").remove();
        // showMessage(response.type, response.message, response.bi);
        onPastaSalvaComSucesso();
    }

    function onPastaSalvaComSucesso() {
        Swal.fire({
            position: 'top',
            title: 'Pronto!',
            text: 'Pasta salva com sucesso!',
            icon: 'success'
        })
    }

    function onErroSalvandoPasta(obj) {
        const msgErro = obj.responseText;
        this.divAlertMsgErro.removeClass('d-none');
        this.divAlertMsgErro.html('<span>' + msgErro + '</span>');
        this.form.find('.form-control').addClass('is-invalid');
    }

    function alertMessageShow(type, message, bi) {
        let alertElement = document.querySelector('.js-ajax-message');
        if (alertElement) {
            alertElement.classList.remove('d-none');
            alertElement.className = `alert alert-${type} alert-dismissible fade show text-center col-4 m-auto my-4`;
            let iconElement = alertElement.querySelector('i');
            if (iconElement) {
                iconElement.className = `bi bi-${bi} me-2`;
            }
            // Encontra o elemento da mensagem ou cria um novo se não existir
            let messageElement = alertElement.querySelector('.js-message-content');
            if (!messageElement) {
                messageElement = document.createElement('span');
                messageElement.className = 'js-message-content';
                alertElement.appendChild(messageElement);
            }
            // Atualiza o conteúdo do elemento da mensagem
            messageElement.textContent = message;
        } else {
            console.error('Element with class .js-ajax-message not found');
        }
    }

    return PastaCadastroModal;

}());

$(function () {
    const pastaCadastroModal = new Keystow.PastaCadastroModal();
    pastaCadastroModal.iniciar();
});
