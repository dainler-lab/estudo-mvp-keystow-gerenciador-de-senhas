var Keystow = Keystow || {};

Keystow.AutocompleteCollectionItem = (function () {

    const divTabelaCgp = $('.js-div-container-tabela-itens');
    const sessionUUID = $('#sessionUUID').val();

    function AutocompleteCollectionItem() {
        this.nome = $('.js-input-item-nome');
        this.emitter = $({});
        this.on = this.emitter.on.bind(this.emitter);
    }

    AutocompleteCollectionItem.prototype.iniciar = function () {
        let options = {
            getValue: 'nome',
            minCharNumber: 3,
            requestDelay: 300,
            ajaxSettings: {
                contentType: 'application/json'
            },
            url: function (nome) {
                return this.nome.data('url') + '?nome=' + nome;
            }.bind(this),
            template: {
                type: 'custom',
                method: itemTemplate.bind(this)
            },
            list: {
                onChooseEvent: onItemSelecionado.bind(this)
            }
        };

        this.nome.easyAutocomplete(options);
        this.on('item-selecionado', onAddItemSession.bind(this));
        $(document).on('click', '.js-excluir-collection-item', onExcluirItemSession.bind(this));
    }

    function onAtualizarTabelaItemSession(response) {
        divTabelaCgp.html(response);
    }

    function onAddItemSession(evento, itemCollectionPesquisaDto) {
        itemCollectionPesquisaDto.sessionUUID = sessionUUID;
        fazerRequestAjax('POST', 'item', JSON.stringify(itemCollectionPesquisaDto))
            .then(response => {
                onAtualizarTabelaItemSession(response)
            });
    }

    function onExcluirItemSession(evento) {
        let itemId = $(evento.target).closest('.js-excluir-collection-item').data('item-id');
        let itemCollectionPesquisaDto = {
            sessionUUID: sessionUUID,
            id: itemId,
        }
        fazerRequestAjax('DELETE', 'item', JSON.stringify(itemCollectionPesquisaDto))
            .then(response => {
                onAtualizarTabelaItemSession(response)
            });
    }

    function itemTemplate(nome, item) {
        let template = $('#template-autocomplete-item-ci-tabela-session').prop('outerHTML');
        let $template = $(template);
        $template.removeClass('d-none');
        $template.find('.nome').text(item.nome);
        return $template.prop('outerHTML');
    }

    function onItemSelecionado() {
        this.emitter.trigger('item-selecionado', this.nome.getSelectedItemData());
        this.nome.val('');
        this.nome.focus();
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
            console.error('CATCH - ERROR NA CHAMADA AJAX: \n\n', error);
            throw error;
        }
    };

    return AutocompleteCollectionItem;

}());

$(function () {
    const autocompleteCollectionItem = new Keystow.AutocompleteCollectionItem();
    autocompleteCollectionItem.iniciar();
});
