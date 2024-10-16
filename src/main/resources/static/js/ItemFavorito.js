var Keystow = Keystow || {};

Keystow.ItemFavorito = (function () {

    function ItemFavorito() {
        this.btnFavoritar = $('.js-favoritar-btn');
        this.inputFavorito = $('.js-input-check-favorito');
    }

    ItemFavorito.prototype.iniciar = function () {
        this.btnFavoritar.on('click', onFavoritarBtnClicado.bind(this));
        verificarEstadoInicial.call(this);
    }

    function verificarEstadoInicial() {
        let i = this.btnFavoritar.find('i');
        if (this.inputFavorito.prop('checked')) {
            i.addClass('bi-star-fill').removeClass('bi-star');
        } else {
            i.addClass('bi-star').removeClass('bi-star-fill');
        }
    }

    function onFavoritarBtnClicado(event) {
        event.preventDefault();
        let btnFavoritar = $(event.currentTarget);
        let i = btnFavoritar.find('i');
        i.toggleClass('bi-star bi-star-fill');
        this.inputFavorito.prop('checked', i.hasClass('bi-star-fill'));
    }

    return ItemFavorito;

}());

$(function () {
    const itemFavorito = new Keystow.ItemFavorito();
    itemFavorito.iniciar();
});
