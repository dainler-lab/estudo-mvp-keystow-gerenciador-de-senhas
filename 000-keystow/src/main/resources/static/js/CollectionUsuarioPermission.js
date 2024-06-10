Keystow.CollectionUsuarioPermission = (function () {

    const sessionUUID = $('#sessionUUID').val();
    const divTabelaCup = $('.js-div-container-tabela-cup');

    function CollectionUsuarioPermission(autocomplete) {
        this.autocomplete = autocomplete;
    }

    CollectionUsuarioPermission.prototype.iniciar = function () {
        this.autocomplete.on('usuario-selecionado', onAdicionarCupSession.bind(this));
        $(document).on('change', '#cup', onAlterarCupSession.bind(this));
        $(document).on('click', '.js-excluir-collection-usuario-permission', onExcluirCupSession.bind(this));
    }

    function onAtualizarTabelaServidorSession(response) {
        divTabelaCup.html(response);
    }

    function onAdicionarCupSession(evento, collectionUsuarioPermissionDto) {
        collectionUsuarioPermissionDto.sessionUUID = sessionUUID;
        fazerRequestAjax('POST', 'cup', JSON.stringify(collectionUsuarioPermissionDto))
            .then(response => {
                onAtualizarTabelaServidorSession(response)
            });
    }

    function onAlterarCupSession(evento) {
        let collectionUsuarioPermissionDto = {
            sessionUUID: sessionUUID,
            usuarioId: $(evento.target).data('usuario-id'),
            permission: $(evento.target).val(),
        }
        fazerRequestAjax('PUT', 'cup', JSON.stringify(collectionUsuarioPermissionDto))
            .then(response => {
                onAtualizarTabelaServidorSession(response)
            });
    }

    function onExcluirCupSession(evento) {
        let usuarioId = $(evento.target).closest('.js-excluir-collection-usuario-permission').data('usuario-id');
        let collectionUsuarioPermissionDto = {
            sessionUUID: sessionUUID,
            usuarioId: usuarioId
        }
        fazerRequestAjax('DELETE', 'cup', JSON.stringify(collectionUsuarioPermissionDto))
            .then(response => {
                onAtualizarTabelaServidorSession(response)
            });
    }

    const fazerRequestAjax = async (method, url, data) => {
        try {
            return await $.ajax({
                contentType: 'application/json',
                type: method,
                url: url,
                data: data
                // success: function (response) {
                //     console.log('TRY - SUCESSO NA CHAMADA AJAX: \n\n', response);
                //     console.log("DATA \n\n", data);
                //     successCallback(response);
                // },
                // error: function (response, textStatus, error, errorThrown, jqXHR) {
                //     console.log('TRY - ERRO NA CHAMADA AJAX: \n\n', response, textStatus, error, errorThrown, jqXHR);
                //     console.log("DATA \n\n", data);
                //     // errorCallback(response);
                // }
            });
        } catch (error) {
            console.error('CATCH - ERROR NA CHAMADA AJAX: \n\n', error);
            throw error;
        }
    };

    return CollectionUsuarioPermission;

}());

$(function () {

    let autocomplete = new Keystow.AutocompletePesquisarUsuarios();
    autocomplete.iniciar();

    let collectionUsuarioPermission = new Keystow.CollectionUsuarioPermission(autocomplete);
    collectionUsuarioPermission.iniciar();

});
