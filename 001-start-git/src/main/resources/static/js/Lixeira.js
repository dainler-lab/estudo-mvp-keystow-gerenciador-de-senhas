var Keystow = Keystow || {};

Keystow.Lixeira = (function () {

    const btnExcluirItensSelecionados = $('.js-btn-excluir-itens-selecionados');
    const btnEsvaziarLixeira = $('.js-btn-esvaziar-lixeira');
    const btnRestaurarTodos = $('.js-btn-restaurar-todos-itens-all');
    const btnRestaurarItensSelecionados = $('.js-btn-restaurar-itens-selecionados');
    const checkboxSelecionarTodos = $('input.js-checkbox-selecionar-todos');
    const checkboxSelecionarItemId = $('input.js-checkbox-selecionar-item-id');

    function Lixeira() {
        //
    }

    Lixeira.prototype.iniciar = function () {

        $(document).ready(getMessageFromSessionStorage);

        $(document).on('click', 'thead .js-clickable-row-table', onTheadClicado);
        $(document).on('click', 'tbody .js-clickable-row-table', onTrowClicado);
        checkboxSelecionarTodos.on('click', onCheckboxTheadClicado);

        checkboxSelecionarItemId.on('click', onCheckboxClicado);
        btnExcluirItensSelecionados.on('click', onExcluirItensSelecionadosClicado);
        btnEsvaziarLixeira.on('click', onEsvaziarLixeiraClicado);
        btnRestaurarTodos.on('click', onRestaurarTodosClicado);

        btnRestaurarItensSelecionados.on('click', onRestaurarItensSelecionadosClicado);
    }

    function onExcluirItensSelecionadosClicado(event) {
        event.preventDefault();
        let btnExcluir = $(event.currentTarget);
        let url = btnExcluir.data('url');
        let ids = checkboxSelecionarItemId.filter(':checked').map(function () {
            return $(this).data('id');
        }).get();

        if (ids.length > 0) {
            Swal.fire({
                position: 'top',
                icon: "question",
                iconColor: "#c6a11b",
                title: 'Tem certeza?',
                html: '<div class="text-center my-1">Você vai excluir o item ou os itens selecionados' +
                    ' permanentemente?</div>' +
                    '<div class="text-center">Você não poderá reverter isso!</div> ',
                showCancelButton: true,
                confirmButtonColor: '#1b60c6',
                cancelButtonColor: "#d33",
                confirmButtonText: 'Sim, exclua agora!'
            }).then((result) => {
                if (result.isConfirmed) {
                    fazerRequestAjax(('DELETE'), url, ids).then(() => {
                        swalSuccessExcluir('Itens excluídos com sucesso');
                    }).catch((error) => {
                        swalErrorExcluir(error.responseText);
                    });
                }
            });
        }
    }

    function onEsvaziarLixeiraClicado(event) {
        event.preventDefault();
        let btnEsvaziar = $(event.currentTarget);
        let url = btnEsvaziar.data('url');
        let message = 'Você vai esvaziar a lixeira permanentemente?';
        console.log('URL: ', url);

        Swal.fire({
            position: 'top',
            icon: "question",
            iconColor: "#c6a11b",
            title: 'Tem certeza?',
            html: '<div class="text-center my-1">' + message + '</div>' +
                '<div class="text-center">Você não poderá reverter isso!</div> ',
            showCancelButton: true,
            confirmButtonColor: '#1b60c6',
            cancelButtonColor: "#d33",
            confirmButtonText: 'Sim, exclua agora!'
        }).then((result) => {
            if (result.isConfirmed) {
                fazerRequestAjax(('DELETE'), url).then(() => {
                    swalSuccessExcluir('Lixeira limpa com sucesso');
                }).catch((error) => {
                    swalErrorExcluir(error.responseText);
                });
            }
        });
    }

    function swalSuccessExcluir(message) {
        Swal.fire({
            position: 'top',
            icon: "success",
            title: 'Sucesso!',
            text: message,
        }).then(() => {
            window.location.reload();
        }, 1500);
    }

    function swalErrorExcluir(message) {
        Swal.fire({
            position: 'top',
            icon: "error",
            title: 'Oops!',
            html: '<div class="alert alert-danger alert-dismissible text-center my-4" role="alert">' +
                message.responseText + '</div>',
            confirmButtonText: 'Fechar'
        });
    }

    function onRestaurarTodosClicado(event) {
        event.preventDefault();
        let btnRestaurarTodos = $(event.currentTarget);
        let url = btnRestaurarTodos.data('url');
        fazerRequestAjax(('PUT'), url).then(() => {
            let message = 'Todos os itens foram restaurados';
            sessionStorage.setItem('message', message);
            window.location.reload();
        });
    }

    function onRestaurarItensSelecionadosClicado(event) {
        event.preventDefault();
        let btnRestaurar = $(event.currentTarget);
        let url = btnRestaurar.data('url');
        let ids = checkboxSelecionarItemId.filter(':checked').map(function () {
            return $(this).data('id');
        }).get();

        if (ids.length > 0) {
            fazerRequestAjax(('PUT'), url, ids).then(() => {
                let message = 'Todos os itens selecionados foram restaurados';
                sessionStorage.setItem('message', message);
                window.location.reload();
            });
        }
    }

    function onTheadClicado(event) {
        let linhaClicada = $(event.currentTarget);
        if (!$(event.target).is('input[type=checkbox]') && !$(event.target).closest('a, i').length) {
            let checkbox = linhaClicada.find('input.js-checkbox-selecionar-todos');
            checkbox.prop('checked', !checkbox.prop('checked'));
            checkboxSelecionarItemId.prop('checked', checkbox.prop('checked'));
            verificarTodosCheckboxes();
        }
    }

    function onTrowClicado(event) {
        let linhaClicada = $(event.currentTarget);
        if (!$(event.target).is('input[type=checkbox]') && !$(event.target).closest('a, i').length) {
            let checkbox = linhaClicada.find('input.js-checkbox-selecionar-item-id');
            checkbox.prop('checked', !checkbox.prop('checked'));
            verificarTodosCheckboxes();
        }
    }

    function onCheckboxTheadClicado(event) {
        let checkbox = $(event.currentTarget);
        let status = checkbox.prop('checked');
        checkboxSelecionarItemId.prop('checked', status);
        verificarTodosCheckboxes();
    }

    function onCheckboxClicado(event) {
        event.stopPropagation();
        verificarTodosCheckboxes();
    }

    function verificarTodosCheckboxes() {
        let allCheckboxes = checkboxSelecionarItemId;
        let checkedCheckboxes = allCheckboxes.filter(':checked');
        let status = allCheckboxes.length === checkedCheckboxes.length;
        checkboxSelecionarTodos.prop('checked', status);
        toggleBtnRestaurarItensSelecionados(checkedCheckboxes.length > 0);
    }

    function toggleBtnRestaurarItensSelecionados(status) {
        status ? btnRestaurarItensSelecionados.removeClass('disabled') : btnRestaurarItensSelecionados.addClass('disabled');
        status ? btnExcluirItensSelecionados.removeClass('disabled') : btnExcluirItensSelecionados.addClass('disabled');
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
        }
    };

    function getMessageFromSessionStorage() {
        let message = sessionStorage.getItem('message');
        if (message) {
            $('.toast .toast-body').text(message);
            $('.toast').toast('show');
            sessionStorage.removeItem('message');
        }
    }

    return Lixeira;

}());

$(function () {
    const lixeira = new Keystow.Lixeira();
    lixeira.iniciar();
});
