package br.com.fiap.pet_saude.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.fiap.pet_saude.exception.AcessoNegadoException;
import br.com.fiap.pet_saude.exception.RegistroNaoEncontradoException;
import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;

/** Converte exceções em uma única página de erro amigável (templates/erro/pagina.html). */
@ControllerAdvice
public class GerenciadorDeErros {

    private static final Logger LOG = LoggerFactory.getLogger(GerenciadorDeErros.class);

    private final AtributosGlobaisController atributosGlobais;

    public GerenciadorDeErros(AtributosGlobaisController atributosGlobais) {
        this.atributosGlobais = atributosGlobais;
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ModelAndView tratarAcessoNegado(AcessoNegadoException ex, HttpServletRequest request) {
        return paginaDeErro(request, HttpStatus.FORBIDDEN, "Acesso negado", ex.getMessage(), "bi-shield-lock");
    }

    @ExceptionHandler({ RegistroNaoEncontradoException.class, NoResourceFoundException.class })
    public ModelAndView tratarNaoEncontrado(Exception ex, HttpServletRequest request) {
        String mensagem = ex instanceof RegistroNaoEncontradoException
                ? ex.getMessage()
                : "A página que você procurou não existe.";
        return paginaDeErro(request, HttpStatus.NOT_FOUND, "Não encontrado", mensagem, "bi-search");
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ModelAndView tratarRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest request) {
        return paginaDeErro(request, HttpStatus.CONFLICT, "Não foi possível concluir", ex.getMessage(),
                "bi-exclamation-triangle");
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, HttpRequestMethodNotSupportedException.class })
    public ModelAndView tratarRequisicaoInvalida(Exception ex, HttpServletRequest request) {
        return paginaDeErro(request, HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O endereço acessado não é válido. Use os botões e menus da aplicação.", "bi-signpost-split");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView tratarErroInesperado(Exception ex, HttpServletRequest request) {
        LOG.error("Erro inesperado em {}", request.getRequestURI(), ex);
        return paginaDeErro(request, HttpStatus.INTERNAL_SERVER_ERROR, "Algo deu errado",
                "Tivemos um problema inesperado. Tente novamente em instantes.", "bi-bug");
    }

    private ModelAndView paginaDeErro(HttpServletRequest request, HttpStatus status,
                                      String titulo, String mensagem, String icone) {
        ModelAndView mv = new ModelAndView("erro/pagina");
        mv.setStatus(status);
        mv.addObject("titulo", titulo);
        mv.addObject("mensagem", mensagem);
        mv.addObject("icone", icone);
        try {
            atributosGlobais.preencher(mv.getModel(), request);
        } catch (RuntimeException falhaAoMontarMenu) {
            LOG.warn("Não foi possível montar o menu da página de erro", falhaAoMontarMenu);
        }
        return mv;
    }
}
