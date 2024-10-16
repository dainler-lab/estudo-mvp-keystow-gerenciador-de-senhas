package com.keystow.app.controller;

import com.keystow.app.controller.form.UsuarioFormDto;
import com.keystow.app.controller.list.CredencialListAppHomeDto;
import com.keystow.app.model.Credencial;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.CredencialRepository;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.UsuarioService;
import com.keystow.app.validation.group.ValidationGroupSequence;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class AppController {

    private final UsuarioService usuarioService;

    private final CredencialRepository credencialRepository;

    private final ModelMapper modelMapper;

    // ---------------- SITE ----------------\\

    @GetMapping("/")
    public String index() {
        return "site/index";
    }

    @GetMapping("/dica")
    public String dicaDeSenha() {
        return "site/dica";
    }

    @GetMapping("/cadastro")
    public ModelAndView viewFormCadastro(UsuarioFormDto usuarioFormDto) {
        ModelAndView modelAndView = new ModelAndView("site/cadastro");
        modelAndView.addObject("usuarioFormDto", usuarioFormDto);
        return modelAndView;
    }

    @PostMapping("/cadastro")
    public ModelAndView saveUsuario(@Validated(ValidationGroupSequence.class) UsuarioFormDto usuarioFormDto, BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return viewFormCadastro(usuarioFormDto);
        }

        Usuario usuario = usuarioService.toModel(usuarioFormDto);
        usuario.setDono(usuario);
        usuarioService.save(usuario);

//        redirectAttributes.addFlashAttribute("type", "success");
//        redirectAttributes.addFlashAttribute("bi", "check-circle");
//        redirectAttributes.addFlashAttribute("message", "Cadastro realizado com sucesso");

        return new ModelAndView("redirect:/app");
    }

    @GetMapping("/login")
    public String viewFormLogin(@AuthenticationPrincipal User user) {
        if (user == null) {
            return "site/login";
        }
        return "redirect:/app";
    }

    // ---------------- APP ----------------\\

    @GetMapping("/cofre")
    public String cofreLogin() {
        return "app/cofre-login";
    }

    @GetMapping("/app")
    public ModelAndView home(@AuthenticationPrincipal AppUser appUser) {

        List<Credencial> credencialList = credencialRepository.findByDonoOrPermissionsAndNotInTrash(appUser.getUsuario().getId());

        List<CredencialListAppHomeDto> credencialListAppHomeDtoList =
                credencialList.stream().map(credencial -> modelMapper.map(credencial, CredencialListAppHomeDto.class)).toList();

        ModelAndView modelAndView = new ModelAndView("app/home");
        modelAndView.addObject("itensCredencialList", credencialListAppHomeDtoList);
        return modelAndView;
    }

}
