package com.generation.blogpessoal.controller;


import com.generation.blogpessoal.Repository.UsuarioRepository;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @BeforeAll
    void start() {

        usuarioRepository.deleteAll();

        usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", "https://i0.wp.com/viciados.net/wp-content/uploads/2022/11/Naruto-Shippuden-Boruto-2023.webp?w=1920&ssl=1"));

    }

    @Test
    @DisplayName("Cadastrar Usuario")
    public void deveCriarUmUsuario() {
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo antunes", "paulo_antunes@gmail.com.br", "13465278", "https://conteudo.imguol.com.br/c/entretenimento/dc/2022/12/30/victoria-lamas-foi-vista-entrando-no-carro-de-leonardo-dicaprio-1672399613064_v2_3x4.jpg"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());

    }

    @Test
    @DisplayName("Não deve permitir duplicação de Usuario")
    public void naoDeveDuplicarUsuario() {

        usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://animes.olanerd.com/wp-content/uploads/2022/10/1666833923_612_A-reputacao-de-Sakura-Haruno-como-o-personagem-mais-fraco.jpg"));
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://animes.olanerd.com/wp-content/uploads/2022/10/1666833923_612_A-reputacao-de-Sakura-Haruno-como-o-personagem-mais-fraco.jpg"));
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());

    }

    @Test
    @DisplayName("Atualizar um Usuário")
    public void deveAtualizarUmUsuario() {
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123", "https://animes.olanerd.com/wp-content/uploads/2022/10/1666833923_924_A-reputacao-de-Sakura-Haruno-como-o-personagem-mais-fraco.jpg"));

        Usuario usuarioUpgrade = new Usuario(usuarioCadastrado.get().getId(), "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123", "https://animes.olanerd.com/wp-content/uploads/2022/10/1666833923_924_A-reputacao-de-Sakura-Haruno-como-o-personagem-mais-fraco.jpg");

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpgrade);

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/cadastrar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
    }

    @Test
    @DisplayName("Listar todos os Usuários")
    public void deveMostrarTodosUsuarios() {
        usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "https://criticalhits.com.br/wp-content/uploads/2022/11/naruto-sorrindo-mae-768x435.jpg"));
        usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "https://criticalhits.com.br/wp-content/uploads/2022/10/naruto-momento-polemico-768x432.jpg"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    }
}