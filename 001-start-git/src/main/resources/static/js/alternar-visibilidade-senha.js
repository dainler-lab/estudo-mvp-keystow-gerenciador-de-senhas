var Keystow = Keystow || {};

Keystow.ItemSenhaVisibilidade = (function () {

    function ItemSenhaVisibilidade() {
        this.senhaInput = $('.js-input-visibilidade');
        this.btnOlho = $('.js-btn-olho');
        this.olhoIcon = $('.js-icon-tag-olho');
    }

    ItemSenhaVisibilidade.prototype.iniciar = function () {
        $(document).off('click', '.js-btn-olho');
        $(document).on('click', '.js-btn-olho', trocarVisibilidade.bind(this));
        this.senhaInput.attr('type', 'password');
        this.olhoIcon.removeClass('bi-eye').addClass('bi-eye-slash');
    }

    function trocarVisibilidade(event) {
        let senhaInput = $(event.target).closest('.input-group').find('.js-input-visibilidade');
        let olhoIcon = $(event.target).closest('.js-btn-olho').find('.js-icon-tag-olho');
        if (senhaInput.attr('type') === 'password') {
            senhaInput.attr('type', 'text');
            olhoIcon.removeClass('bi-eye-slash').addClass('bi-eye');
        } else {
            senhaInput.attr('type', 'password');
            olhoIcon.removeClass('bi-eye').addClass('bi-eye-slash');
        }
    }

    return ItemSenhaVisibilidade;

}());

$(function () {
    const itemSenhaVisibilidade = new Keystow.ItemSenhaVisibilidade();
    itemSenhaVisibilidade.iniciar();
});
