var Keystow = Keystow || {};

Keystow.AutocompleteGrupoUsuarioPesquisa = (function () {

    const divListaContainerGup = $('.js-div-container-list-gup');
    const sessionUUID = $('#sessionUUID').val();

    function AutocompleteGrupoUsuarioPesquisa() {
        this.inputNomeOuEmail = $('.js-input-nome-ou-email-usuario-gup');
        this.emitter = $({});
        this.on = this.emitter.on.bind(this.emitter);
    }

    AutocompleteGrupoUsuarioPesquisa.prototype.iniciar = function () {
        let options = {
            getValue: 'nome',
            minCharNumber: 3,
            requestDelay: 300,
            ajaxSettings: {
                contentType: 'application/json'
            },
            url: function (nomeOuEmail) {
                return this.inputNomeOuEmail.data('url') + '?nomeOuEmail=' + nomeOuEmail;
            }.bind(this),
            template: {
                type: 'custom',
                method: usuarioTemplate.bind(this)
            },
            list: {
                onChooseEvent: onGupSelecionado.bind(this)
            }
        };

        this.inputNomeOuEmail.easyAutocomplete(options);
        this.on('gup-selecionado', onAddGup.bind(this));
        $(document).on('click', '.js-excluir-gup', onExcluirGup.bind(this));
    }

    function onAtualizarListaContainer(response) {
        divListaContainerGup.html(response);
    }

    function onAddGup(evento, grupoUsuarioPesquisaDto) {
        grupoUsuarioPesquisaDto.sessionUUID = sessionUUID;
        fazerRequestAjax('POST', 'usuarios', JSON.stringify(grupoUsuarioPesquisaDto))
            .then(response => {
                onAtualizarListaContainer(response)
            });
    }

    function onExcluirGup(evento) {
        let usuarioId = $(evento.target).closest('.js-excluir-gup').data('usuario-id');
        let grupoUsuarioPesquisaDto = {
            sessionUUID: sessionUUID,
            id: usuarioId,
        }
        fazerRequestAjax('DELETE', 'usuarios', JSON.stringify(grupoUsuarioPesquisaDto))
            .then(response => {
                onAtualizarListaContainer(response)
            });
    }

    function usuarioTemplate(nome, usuario) {
        let template = $('#template-autocomplete-gup').prop('outerHTML');
        let $template = $(template);
        $template.removeClass('d-none');
        $template.find('.nome').text(usuario.nome);
        $template.find('.email').text(usuario.email);
        return $template.prop('outerHTML');
    }

    function onGupSelecionado() {
        this.emitter.trigger('gup-selecionado', this.inputNomeOuEmail.getSelectedItemData());
        this.inputNomeOuEmail.val('');
        this.inputNomeOuEmail.focus();
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

    return AutocompleteGrupoUsuarioPesquisa;

}());

$(function () {
    const autocompleteGrupoUsuarioPesquisa = new Keystow.AutocompleteGrupoUsuarioPesquisa();
    autocompleteGrupoUsuarioPesquisa.iniciar();
});
