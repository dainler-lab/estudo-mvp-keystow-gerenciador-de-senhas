var Keystow = Keystow || {};

Keystow.AutocompletePesquisarUsuarios = (function () {

    function AutocompletePesquisarUsuarios() {
        this.inputNomeOuEmail = $('.js-input-nome-ou-email-usuario-cup');
        this.emitter = $({});
        this.on = this.emitter.on.bind(this.emitter);
    }

    AutocompletePesquisarUsuarios.prototype.iniciar = function () {
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
                method: template.bind(this)
            },
            list: {
                onChooseEvent: onUsuarioSelecionado.bind(this)
            }
        };
        this.inputNomeOuEmail.easyAutocomplete(options);
    }

    function template(nome, usuario) {
        let template = $('#template-autocomplete-usuario-cup-tabela-session').prop('outerHTML');
        let $template = $(template);
        $template.removeClass('d-none');
        $template.find('.nome').text(usuario.usuarioNome);
        $template.find('.email').text(usuario.usuarioEmail);
        return $template.prop('outerHTML');
    }

    function onUsuarioSelecionado() {
        this.emitter.trigger('usuario-selecionado', this.inputNomeOuEmail.getSelectedItemData());
        this.inputNomeOuEmail.val('');
        this.inputNomeOuEmail.focus();
    }

    return AutocompletePesquisarUsuarios

}());
