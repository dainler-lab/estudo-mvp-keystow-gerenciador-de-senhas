var Keystow = Keystow || {};

Keystow.AutocompleteCollectionGrupoPermission = (function () {

    const divTabelaCgp = $('.js-div-container-tabela-cgp');
    const sessionUUID = $('#sessionUUID').val();

    function AutocompleteCollectionGrupoPermission() {
        this.grupoNome = $('.js-input-grupo-nome-cgp');
        this.emitter = $({});
        this.on = this.emitter.on.bind(this.emitter);
    }

    AutocompleteCollectionGrupoPermission.prototype.iniciar = function () {
        let options = {
            getValue: 'nome',
            minCharNumber: 3,
            requestDelay: 300,
            ajaxSettings: {
                contentType: 'application/json'
            },
            url: function (grupoNome) {
                return this.grupoNome.data('url') + '?grupoNome=' + grupoNome;
            }.bind(this),
            template: {
                type: 'custom',
                method: grupoTemplate.bind(this)
            },
            list: {
                onChooseEvent: onGrupoSelecionado.bind(this)
            }
        };

        this.grupoNome.easyAutocomplete(options);
        this.on('grupo-selecionado', onAdicionarCgpSession.bind(this));
        $(document).on('change', '#cgp', onAlterarCgpSession.bind(this));
        $(document).on('click', '.js-excluir-collection-grupo-permission', onExcluirCgpSession.bind(this));
    }

    function onAtualizarTabelaCgpSession(response) {
        divTabelaCgp.html(response);
    }

    function onAdicionarCgpSession(evento, collectionGrupoPermissionDto) {
        collectionGrupoPermissionDto.sessionUUID = sessionUUID;
        fazerRequestAjax('POST', 'cgp', JSON.stringify(collectionGrupoPermissionDto))
            .then(response => {
                onAtualizarTabelaCgpSession(response)
            });
    }

    function onAlterarCgpSession(evento) {
        let collectionGrupoPermissionDto = {
            sessionUUID: sessionUUID,
            grupoId: $(evento.target).data('grupo-id'),
            permission: $(evento.target).val(),
        }
        fazerRequestAjax('PUT', 'cgp', JSON.stringify(collectionGrupoPermissionDto))
            .then(response => {
                onAtualizarTabelaCgpSession(response)
            });
    }

    function onExcluirCgpSession(evento) {
        let grupoId = $(evento.target).closest('.js-excluir-collection-grupo-permission').data('grupo-id');
        let collectionGrupoPermissionDto = {
            sessionUUID: sessionUUID,
            grupoId: grupoId,
        }
        fazerRequestAjax('DELETE', 'cgp', JSON.stringify(collectionGrupoPermissionDto))
            .then(response => {
                onAtualizarTabelaCgpSession(response)
            });
    }

    function grupoTemplate(nome, grupo) {
        let template = $('#template-autocomplete-grupo-cgp-tabela-session').prop('outerHTML');
        let $template = $(template);
        $template.removeClass('d-none');
        $template.find('.nome').text(grupo.grupoNome);
        return $template.prop('outerHTML');
    }

    function onGrupoSelecionado() {
        this.emitter.trigger('grupo-selecionado', this.grupoNome.getSelectedItemData());
        this.grupoNome.val('');
        this.grupoNome.focus();
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
            console.error('TRY/CATCH - ERROR NA CHAMADA AJAX: \n\n', error);
            throw error;
        }
    };

    return AutocompleteCollectionGrupoPermission;

}());

$(function () {
    const collectionGrupoPermission = new Keystow.AutocompleteCollectionGrupoPermission();
    collectionGrupoPermission.iniciar();
});
