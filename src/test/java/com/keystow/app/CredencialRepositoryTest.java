package com.keystow.app;

import com.keystow.app.model.Credencial;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.CredencialRepository;
import com.keystow.app.repository.filter.CredencialFilterListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CredencialRepositoryTest {

    @Autowired
    private CredencialRepository credencialRepository;

    @Test
    public void testFiltroWithGroups() {
//        1. A consulta deve retornar itens para o usuario em que ele for dono e na onde ele não for dono mais tem permissão
//        2. A consulta deve retornar itens para o usuario em que ele não for dono, mas tem permissão
//        3. A consulta não deve retornar itens para o usuario em que ele não for dono e não tem permissão
        CredencialFilterListDto credencialFilterListDto = new CredencialFilterListDto();

        Usuario dono = new Usuario();
        dono.setId("a9d75974-46a4-4fa5-8c15-fb1175e13386");
        credencialFilterListDto.setDono(dono); // usuário dono dos itens
        Page<Credencial> page = credencialRepository.findByDonoOrPermissionsAndNotInTrash(dono.getId(), PageRequest.of(0, 10));
        assertFalse(page.isEmpty(), "1 - A página não deve estar vazia para um usuário dono dos itens e se tem permissão de qual não é dono");

        Usuario userPermission1 = new Usuario();
        userPermission1.setId("92b2e8a2-4cda-438a-a3e3-246794946f70");
        credencialFilterListDto.setDono(userPermission1); // usuário com permissão para ver os itens
        page = credencialRepository.findByDonoOrPermissionsAndNotInTrash(userPermission1.getId(), PageRequest.of(0, 10));
        assertFalse(page.isEmpty(), "2 - A página não deve estar vazia para usuários que não são donos mas tem permissões");

        Usuario userPermission2 = new Usuario();
        userPermission2.setId("9ecfc10f-a61f-4ab8-811c-3f9793579191");
        credencialFilterListDto.setDono(userPermission2); // usuário que não tem permissão para ver os itens
        page = credencialRepository.findByDonoOrPermissionsAndNotInTrash(userPermission2.getId(), PageRequest.of(0, 10));
        assertTrue(page.isEmpty(), "3 - A página deve estar vazia para um usuário que não é dono e não tem permissão para ver os itens");
    }

}
