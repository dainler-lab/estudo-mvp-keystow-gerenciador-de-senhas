var Keystow = Keystow || {};

Keystow.GeradorDeSenha = (() => {

    const selectors = {

        btnInputGroupGerarSenha: $('.js-input-group-gerar-senha'),

        tipoGerador: $('input[name="tipoGerador"]'),
        categoriaDoGerador: $('input[name="categoriaDoGerador"]'),

        inputTamanho: $('.js-form-tamanho'),
        inputTamanhoRange: $('.js-form-range-tamanho'),

        checkboxMaiusculas: $('.js-checkbox-maiusculas'),
        checkboxMinusculas: $('.js-checkbox-minusculas'),
        checkboxNumeros: $('.js-checkbox-numeros'),
        checkboxCaracteresEspeciais: $('.js-checkbox-caracteres-especiais'),

        inputNumerosMinimos: $('.js-form-numeros-minimos'),
        inputCaracteresEspeciaisMinimos: $('.js-form-caracteres-especiais-minimos'),

        checkboxEvitarCaractersAmbiguos: $('.js-checkbox-caracteres-ambiguos'),

        btnGerarSenha: $('.js-btn-gerar-senha'),
        btnCopiarSenhaGerador: $('.js-btn-copiar-senha'),

        divSenhaMascara: $('.senhaMascara'),

        tagIconOlhoGerador: $('.js-icon-tag-olho-gerador'),
        btnIconOlhoGerador: $('.js-btn-olho-gerador'),
        btnCopiarValorGerador: $('.js-btn-copiar-valor-gerador'),

        btnHistoricoDeSenhas: $('.js-btn-historico-senhas'),
        containerModalHistoricoDeSenhas: $('.js-historico-senha-container'),
        btnCopiarSenhaHistorico: $('.js-btn-copiar-senha-historico'),
        btnLimparSenhasHistorico: $('.js-btn-limpar-senhas-historico'),

        btnAddSenhaInput: $('.js-btn-add-senha-input'),
        formInputAddSenha: $('.js-form-input-add-senha')
    };

    const bindEvents = () => {
        selectors.btnInputGroupGerarSenha.on('click', gerarSenha);
        selectors.btnGerarSenha.on('click', gerarSenha);

        selectors.inputTamanho.on('input', syncInputsTamanhoRangeSenha);
        selectors.inputTamanhoRange.on('input', syncInputsTamanhoRangeSenha);

        selectors.btnHistoricoDeSenhas.on('click', getListaDeHistoricoDeSenhas);
        selectors.btnCopiarSenhaHistorico.on('click', copiarSenhaHistorico);
        selectors.btnLimparSenhasHistorico.on('click', eventLimparSenhasHistorico);

        selectors.btnAddSenhaInput.on('click', setSenhaFormInputAdd);
    };

    const setSenhaFormInputAdd = async (event) => {
        event.preventDefault();
        const geradorDeSenhaFormRequest = criarGeradorDeSenhaFormRequest();
        const senhaResponse = await fazerRequestAjax('POST', '/senha/generate', geradorDeSenhaFormRequest);
        selectors.formInputAddSenha.val(senhaResponse);
    }

    function errorLimparHistoricoDeSenhas() {
        Swal.fire({
            position: 'top',
            icon: 'error',
            title: 'Oops!',
            html: '<div class="text-center my-1">Erro ao limpar o histórico de senhas.</div>' +
                '<div class="text-center">Por favor, tente novamente mais tarde.</div>'
        });
    }

    function excluidoListaDeSenhasComSucesso() {
        Swal.fire({
            position: 'top',
            icon: 'success',
            title: 'Pronto!',
            html: '<div class="text-center my-1">Histórico de senhas limpo com sucesso!</div>'
        }).then(() => {
            setTimeout(function () {
                window.location.reload();
            }, 1);
        });
    }

    const eventLimparSenhasHistorico = (event) => {
        event.preventDefault();
        Swal.fire({
            position: 'top',
            icon: "question",
            iconColor: "#c6a11b",
            title: 'Tem certeza?',
            html: '<div class="text-center my-1">Você vai limpar o histórico de senhas?</div>' +
                '<div class="text-center">Você não poderá reverter isso!</div> ',
            showCancelButton: true,
            confirmButtonColor: '#1b60c6',
            cancelButtonColor: "#d33",
            confirmButtonText: 'Sim, limpar agora!'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/senha/history',
                    method: 'DELETE',
                    success: () => {
                        excluidoListaDeSenhasComSucesso();
                    },
                    error: (jqXHR, textStatus, errorThrown) => {
                        console.log('Erro ao limpar o histórico de senhas: ', textStatus, errorThrown);
                        errorLimparHistoricoDeSenhas();
                    }
                });
            }
        });
    }

    const copiarSenhaHistorico = (event) => {
        // Encontrar o botão mais próximo do elemento que foi clicado
        const btn = $(event.target).closest('.js-btn-copiar-senha-historico');

        // Recuperar a senha armazenada no botão
        const senha = btn.data('senhaHistorico').trim();

        navigator.clipboard.writeText(senha).then(() => {
            $('.toast .toast-body').text('Senha copiada com sucesso');
            $('.toast').toast('show');
        }, (err) => {
            console.error('Falha ao copiar o valor: ', err);
        });
    }

    const atualizarListaDeHistoricoDeSenhas = (responseHTML) => {
        $(document).ready(function () {
            // Selecionar todos os elementos <span> que contêm senhas
            const senhas = $('.js-senha-historico');

            // Iterar sobre cada elemento de senha
            senhas.each(function () {
                // Obter o valor da senha (texto do elemento)
                const senha = $(this).text();

                // Aplicar a formatação da senha
                const senhaFormatada = formatarSenhaComCores(senha);

                // Substituir o texto do elemento pela senha formatada
                $(this).html(senhaFormatada);

                // Armazenar a senha real em um atributo de dados no botão de cópia correspondente
                $(this).next('.js-btn-copiar-senha-historico').data('senhaHistorico', senha);
            });

            // Associar o evento de clique a cada botão de cópia
            $('.js-btn-copiar-senha-historico').on('click', copiarSenhaHistorico);
        });

        selectors.containerModalHistoricoDeSenhas.html(responseHTML);
    }

    const getListaDeHistoricoDeSenhas = async () => {
        const response = await fazerRequestAjax('GET', '/senha/history', null);
        atualizarListaDeHistoricoDeSenhas(response);
    }

    const formatarSenhaComCores = (senha) => {

        let senhaFormatada = '';

        for (let i = 0; i < senha.length; i++) {
            let caractere = senha[i];
            let span = '<span>';

            // Verificar se o caractere é um dígito
            if (!isNaN(caractere)) {
                span += caractere + '</span>';
                senhaFormatada += '<span style="color: green;">' + span + '</span>';
            }
            // Verificar se o caractere é um caractere especial
            else if (caractere.match(/[!"#$%&' ()*+, -./:;<=>?@[\]_{|}~]/)) {
                span += caractere + '</span>';
                senhaFormatada += '<span style="color: red;">' + span + '</span>';
            }
            // Verificar se o caractere é maiúsculo
            else if (caractere === caractere.toUpperCase()) {
                span += caractere + '</span>';
                senhaFormatada += '<span style="color: blue;">' + span + '</span>';
            }
            // Verificar se o caractere é minúsculo
            else if (caractere === caractere.toLowerCase()) {
                span += caractere + '</span>';
                senhaFormatada += '<span style="color: black;">' + span + '</span>';
            }
            // Para outros caracteres, como espaços ou símbolos não especificados
            else {
                span += caractere + '</span>';
                senhaFormatada += span;
            }
        }

        return senhaFormatada;
    }

    const mascararSenha = (senhaResponse) => {

        selectors.divSenhaMascara.data('senha-visivel', false);

        // Armazenar a senha real em um atributo de dados
        selectors.divSenhaMascara.data('senhaReal', senhaResponse);

        function toggleSenhaVisibilidade() {
            let senhaReal = selectors.divSenhaMascara.data('senhaReal');
            let senhaVisivel = selectors.divSenhaMascara.data('senha-visivel');

            if (senhaVisivel) {
                let mascara = senhaReal.replace(/./g, '*');
                selectors.divSenhaMascara.text(mascara);
                selectors.divSenhaMascara.data('senha-visivel', false);
                selectors.tagIconOlhoGerador.removeClass('bi-eye').addClass('bi-eye-slash');
            } else {
                let senhaFormatada = formatarSenhaComCores(senhaReal);
                selectors.divSenhaMascara.html(senhaFormatada);
                selectors.divSenhaMascara.data('senha-visivel', true);
                selectors.tagIconOlhoGerador.removeClass('bi-eye-slash').addClass('bi-eye');
            }
        }

        function copiarSenhaParaAreaDeTransferencia() {
            let senhaReal = selectors.divSenhaMascara.data('senhaReal');
            navigator.clipboard.writeText(senhaReal).then(() => {
                $('.toast .toast-body').text('Senha copiada com sucesso');
                $('.toast').toast('show');
            }, (err) => {
                console.error('Falha ao copiar o valor: ', err);
            });
        }

        // Inicialmente, mostrar a senha mascarada
        // let mascara = senha.replace(/./g, '*');
        // selectors.divSenhaMascara.text(mascara);
        // selectors.tagIconOlhoGerador.removeClass('bi-eye').addClass('bi-eye-slash');

        // Inicialmente, mostrar a senha formatada
        selectors.tagIconOlhoGerador.removeClass('bi-eye-slash').addClass('bi-eye');
        selectors.divSenhaMascara.html(formatarSenhaComCores(senhaResponse));

        selectors.btnIconOlhoGerador.off('click').on('click', toggleSenhaVisibilidade);
        selectors.btnCopiarValorGerador.off('click').on('click', copiarSenhaParaAreaDeTransferencia);
        selectors.btnCopiarSenhaGerador.off('click').on('click', copiarSenhaParaAreaDeTransferencia);
    }

    const atualizarSenha = (senhaResponse) => {
        mascararSenha(senhaResponse);
    }

    const criarGeradorDeSenhaFormRequest = () => {
        return {
            tipoGerador: selectors.tipoGerador.val(),
            geradorDeSenhaCategoria: selectors.categoriaDoGerador.val(),
            tamanho: selectors.inputTamanho.val(),
            maiusculas: selectors.checkboxMaiusculas.is(':checked'),
            minusculas: selectors.checkboxMinusculas.is(':checked'),
            numeros: selectors.checkboxNumeros.is(':checked'),
            especiais: selectors.checkboxCaracteresEspeciais.is(':checked'),
            numerosMinimos: selectors.inputNumerosMinimos.val(),
            especiaisMinimos: selectors.inputCaracteresEspeciaisMinimos.val(),
            evitarAmbiguidade: selectors.checkboxEvitarCaractersAmbiguos.is(':checked')
        };
    }

    const gerarSenha = async (event) => {
        event.preventDefault();
        const geradorDeSenhaFormRequest = criarGeradorDeSenhaFormRequest();
        const response = await fazerRequestAjax('POST', '/senha/generate', geradorDeSenhaFormRequest);
        atualizarSenha(response);
    };

    const fazerRequestAjax = async (method, url, data) => {
        try {
            return await $.ajax({
                contentType: 'application/json',
                type: method,
                url: url,
                data: JSON.stringify(data)
            });
        } catch (error) {
            console.error('Erro na chamada AJAX: ', error);
        }
    };

    const syncInputsTamanhoRangeSenha = (event) => {
        const value = $(event.target).val();
        selectors.inputTamanho.val(value);
        selectors.inputTamanhoRange.val(value);
    }

    bindEvents();

    return {
        // Funções públicas aqui...
    };

})();
