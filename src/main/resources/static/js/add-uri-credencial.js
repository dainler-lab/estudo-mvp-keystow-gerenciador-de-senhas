var Keystow = Keystow || {};

Keystow.AddUriCredencial = (function () {

    const ITEM_FORM_UUID = $('#itemFormUUID').val();
    const SELECTOR_CAMPO_CONTAINER = '.js-div-uris-container';
    const SELECTOR_ADICIONAR_BTN = '.js-add-uri';
    const SELECTOR_EXCLUIR_URI_BTN = '.js-excluir-uri';
    const SELECTOR_INPUTS = '.js-uri-input-id, .js-uri-input-valor';
    const SELECTOR_URI_ROW = '.js-uri-row';

    function AddUriCredencial() {
        this.itemFormUUID = ITEM_FORM_UUID;
        this.divUriContainer = $(SELECTOR_CAMPO_CONTAINER);
        this.btnAddUri = $(SELECTOR_ADICIONAR_BTN);
    }

    AddUriCredencial.prototype.iniciar = function () {
        this.btnAddUri.on('click', addUri.bind(this));
        this.divUriContainer.on('change', SELECTOR_INPUTS, alterarUri.bind(this));
        this.divUriContainer.on('click', SELECTOR_EXCLUIR_URI_BTN, excluirUri.bind(this));
    }

    function UriFormDto(itemFormUUID, id, valor) {
        this.itemFormUUID = itemFormUUID;
        this.id = id;
        this.valor = valor;
    }

    function criarUriFormDto(itemFormUUID, id, valor) {
        return new UriFormDto(itemFormUUID, id, valor);
    }

    function criarUriFormDoEvento(evento, itemFormUUID) {
        let row = $(evento.target).closest(SELECTOR_URI_ROW);
        let inputs = row.find(SELECTOR_INPUTS).get();
        let id = $(inputs[0]).val();
        let valor = $(inputs[1]).val();
        return criarUriFormDto(itemFormUUID, id, valor);
    }

    function atualizarItemUriNoServidor(responseHTML) {
        this.divUriContainer.html(responseHTML);
    }

    function addUri(evento) {
        evento.preventDefault();
        let uriFormDto = criarUriFormDto(this.itemFormUUID, '', '');
        fazerRequestAjax.call(this, 'POST', 'uri', uriFormDto, atualizarItemUriNoServidor.bind(this));
    }

    function alterarUri(evento) {
        evento.preventDefault();
        let uriFormDto = criarUriFormDoEvento(evento, this.itemFormUUID);
        fazerRequestAjax.call(this, 'PUT', '/credencial/uri', uriFormDto);
    }

    function excluirUri(evento) {
        evento.preventDefault();
        let id = $(evento.target).closest(SELECTOR_EXCLUIR_URI_BTN).data('uri-id');
        fazerRequestAjax.call(this, 'DELETE', 'uri/' + this.itemFormUUID + '/' + id, null, atualizarItemUriNoServidor.bind(this));

    }

    function fazerRequestAjax(method, url, data, successCallback) {
        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                // console.log(JSON.stringify(data));
                // console.log('Chamada AJAX realizada com sucesso: \n\n', response);
                if (successCallback) {
                    successCallback(response);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(JSON.stringify(data));
                console.log('Erro na chamada AJAX: \n\n', textStatus, errorThrown);
            }
        });
    }

    return AddUriCredencial;

}());

$(function () {
    const addUriCredencial = new Keystow.AddUriCredencial();
    addUriCredencial.iniciar();
});
