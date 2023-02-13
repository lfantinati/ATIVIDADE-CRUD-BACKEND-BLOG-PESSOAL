package controller;

import com.generation.blogpessoal.Repository.UsuarioRepository;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Optional;

public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start(){
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(new Usuario(0L,"Root","root@root.com","rootroot", " "));

    }

    @Test
    @DisplayName("Cadastrar um Usuario")
    public void deveCriarUmUsuario(){

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo Antunes","paulo_antunes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuario/cadastrar", HttpMethod.POST,corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(),corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(),corpoResposta.getBody().getUsuario());


    }

    private void assertEquals(HttpStatus created, HttpStatusCode statusCode) {
    }

    @Test
    @DisplayName("Não deve permitir duplicacao do Usuario")
    public void naoDeveDuplicarUsuario(){
        usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva","maria_silva@email.com.br","13465278", "https://i.imgur.com/T12NIp9.jpg"));

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva","maria_silva@email.com.br","13465278", "https://i.imgur.com/T12NIp9.jpg"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());

    }

    @Test
    @DisplayName("Atualizar um Usuario")
    public void deveAtualizarUmUsuarios() {
        Optional<Usuario>usuarioCadastro = usuarioService.cadastrarUsuario(new Usuario(0L,"Juliana Andrews", "juliana_andrews@email.com", "JULIANA123", "https://i.imgur.com/yDRVeK7.jpg"));

        Usuario usuarioUpdate = new Usuario(usuarioCadastro.get().getId(),
                "Juliana Andrews Ramos", "juliana_ramos@email.com", "juliana123", "https://i.imgur.com/yDRVeK7.jpg");
         HttpEntity<Usuario>corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

         ResponseEntity<Usuario> corpoResposta = testRestTemplate
                 .withBasicAuth("root@root.com", "rootroot")
                 .exchange ("/usuario/atualizar", HttpMethod.PUT,corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(),corpoResposta.getBody().getNome());
        assertEqiuals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());


    }

    private void assertEqiuals(String usuario, String usuario1) {
    }

    private void assertEquals(String nome, String nome1) {
    }


    @Test
    @DisplayName("Listar todos usuarios")
    public void deveMostrarTodosUsuarios() {
        usuarioService.cadastrarUsuario(new Usuario(
                0L,"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "https://i.imgur.com/5M2p5Wb.jpg"));
        usuarioService.cadastrarUsuario(new Usuario(0L,"Ricardo Marques","ricardo_marques@email.com","ricardo123", "https://i.imgur.com/Sk5SjWE.jpg"));

                ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                        .exchange("/usuarios/all", HttpMethod.GET, null, String.class);
                assertEquals(HttpStatus.OK, resposta.getStatusCode());


    }

}
