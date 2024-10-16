var Keystow = Keystow || {};

Keystow.AddCampoPersonalizado = (function () {

    const ITEM_FORM_UUID = $('#itemFormUUID').val();
    const SELECTOR_CAMPO_CONTAINER = '.js-div-campos-personalizados-container';
    const SELECTOR_ADICIONAR_BTN = '.js-add-campo-personalizado';
    const SELECTOR_TIPO_CAMPO = '.js-select-tipo-de-campo-personalizado';
    const SELECTOR_EXCLUIR_BTN = '.js-excluir-campo-personalizado';
    const SELECTOR_INPUTS = '.js-input-id, .js-input-nome, .js-input-valor';

    function AddCampoPersonalizado() {
        this.itemFormUUID = ITEM_FORM_UUID;
        this.divCampoContainer = $(SELECTOR_CAMPO_CONTAINER);
        this.btnAdicionarCampoPersonalizado = $(SELECTOR_ADICIONAR_BTN);
        this.selectTipoCampo = $(SELECTOR_TIPO_CAMPO);
    }

    AddCampoPersonalizado.prototype.iniciar = function () {
        this.btnAdicionarCampoPersonalizado.on('click', adicionarCampo.bind(this));
        this.divCampoContainer.on('change', SELECTOR_INPUTS, alterarCampo.bind(this));
        this.divCampoContainer.on('click', SELECTOR_EXCLUIR_BTN, excluirCampo.bind(this));
    }

    function CampoPersonalizadoFormDto(itemFormUUID, id, nome, valor, tipoDeCampoPersonalizado) {
        this.itemFormUUID = itemFormUUID;
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.tipoDeCampoPersonalizado = tipoDeCampoPersonalizado;
    }

    function criarItemCampoPersonalizado(itemFormUUID, id, nome, valor, tipoDeCampoPersonalizado) {
        return new CampoPersonalizadoFormDto(itemFormUUID, id, nome, valor, tipoDeCampoPersonalizado);
    }

    function criarItemCampoPersonalizadoVazio(itemFormUUID, selectTipoCampo) {
        return criarItemCampoPersonalizado(itemFormUUID, '', '', '', selectTipoCampo.val() || "TEXTO");
    }

    function criarItemCampoPersonalizadoDoEvento(evento, itemFormUUID) {
        let row = $(evento.target).closest('.row');
        let inputs = row.find(SELECTOR_INPUTS).get();
        let id = $(inputs[0]).val();
        let nome = $(inputs[1]).val();
        let valor;
        if ($(inputs[2]).attr('type') === 'checkbox') {
            valor = $(inputs[2]).is(':checked');
        } else {
            valor = $(inputs[2]).val();
        }

        return criarItemCampoPersonalizado(itemFormUUID, id, nome, valor);
    }

    function atualizarCampoNoServidor(response) {
        this.divCampoContainer.html(response);
        const itemSenhaVisibilidade = new Keystow.ItemSenhaVisibilidade();
        itemSenhaVisibilidade.iniciar();
    }

    function adicionarCampo(evento) {
        evento.preventDefault();
        let campoPersonalizadoFormDto = criarItemCampoPersonalizadoVazio(this.itemFormUUID, this.selectTipoCampo);
        fazerRequestAjax.call(this, 'POST', 'item-campo-personalizado', campoPersonalizadoFormDto, atualizarCampoNoServidor.bind(this));
    }

    function alterarCampo(evento) {
        evento.preventDefault();
        let campoPersonalizadoFormDto = criarItemCampoPersonalizadoDoEvento(evento, this.itemFormUUID);
        fazerRequestAjax.call(this, 'PUT', '/credencial/item-campo-personalizado', campoPersonalizadoFormDto);
    }

    function excluirCampo(evento) {
        evento.preventDefault();
        let id = $(evento.target).closest(SELECTOR_EXCLUIR_BTN).data('id-item-campo-personalizado');
        fazerRequestAjax.call(this, 'DELETE', 'item-campo-personalizado/' + this.itemFormUUID + '/' + id, null, atualizarCampoNoServidor.bind(this));
    }

    function fazerRequestAjax(method, url, data, successCallback) {
        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                // console.log(JSON.stringify(data));
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

    return AddCampoPersonalizado;

}());

$(function () {
    const addCampoPersonalizado = new Keystow.AddCampoPersonalizado();
    addCampoPersonalizado.iniciar();
});
