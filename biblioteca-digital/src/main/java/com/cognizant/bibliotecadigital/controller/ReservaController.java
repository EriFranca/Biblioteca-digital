
package com.cognizant.bibliotecadigital.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cognizant.bibliotecadigital.model.Emprestimo;
import com.cognizant.bibliotecadigital.model.Livro;
import com.cognizant.bibliotecadigital.model.Mail;
import com.cognizant.bibliotecadigital.model.Reserva;
import com.cognizant.bibliotecadigital.model.Status;
import com.cognizant.bibliotecadigital.model.StatusLivro;
import com.cognizant.bibliotecadigital.model.UnidadeLivro;
import com.cognizant.bibliotecadigital.model.Usuario;
import com.cognizant.bibliotecadigital.service.EmailService;
import com.cognizant.bibliotecadigital.service.EmprestimoService;
import com.cognizant.bibliotecadigital.service.LivroService;
import com.cognizant.bibliotecadigital.service.PapelService;
import com.cognizant.bibliotecadigital.service.ReservaService;
import com.cognizant.bibliotecadigital.service.UnidadeLivroService;
import com.cognizant.bibliotecadigital.service.UsuarioService;

@Controller
public class ReservaController {
	//Serviços chamados
	@Autowired
	private ReservaService reservaService;
	@Autowired
	private LivroService livroService;
	@Autowired
	private UnidadeLivroService unidadeService;
	@Autowired
	private EmprestimoService emprestimoService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private PapelService papelService;

	/* *************************************************************
	 * Faz o mapeamento da página de reservas,
	 * Caso o usuário tenha alguma reserva,
	 * é feita a listagem de todas as reservas dele
	 * Se ele tiver uma reserva, mas também tiver um empréstimo,
	 * ele só poderá retirar o livro reservado, se finalizar o empréstimo,
	 * caso contrário, a reserva ficará "EM_ESPERA" até o prazo da
	 * reserva acabar, ou ele devolver o livro emprestado
	 * Caso a reserva esteja com o status "AGUARDANDO,
	 * ele não poderá fazer o empréstimo do livro
	  ************************************************************* */
	@GetMapping("/reservas")
	public ModelAndView findAll() throws ParseException {
		ModelAndView mv = new ModelAndView("/reserva/reserva");

		List<Reserva> reservas = (List<Reserva>) reservaService.findAll();
		List<Reserva> reservasPorUsuario = new ArrayList<>();
		Usuario usuario = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String email = auth.getName();
			usuario = usuarioService.findByEmail(email).orElse(null);
		}

		if (!reservas.isEmpty()) {
			for (Reserva reserva : reservas) {

				List<Emprestimo> emprestimos = (List<Emprestimo>) emprestimoService
						.emprestimoPorReservaId(reserva.getId());
				if (!emprestimos.isEmpty()) {
					for (Emprestimo emprestimo : emprestimos) {
						if (reserva.getStatus().equals(Status.FINALIZADO)) {
							reserva.setHabilita(true);
							reserva.setHabilitaApagarReserva(true);
							continue;
						}
						Date disponibilidade = calculaDisponibilidade(emprestimo);
						if (emprestimo.getDataDevolucao() == null || reserva.getLivro().getStatusLivro().equals(StatusLivro.EM_ANALISE)) {
							reserva.setStatus(Status.EM_ESPERA);
							reserva.setDataPrevisao(formataData(disponibilidade));
							reserva.setHabilita(true);

						} else {
							reserva.setStatus(Status.AGUARDANDO);
							reserva.setHabilita(false);
						}
					}
					if (reserva.getUsuario().getId().equals(usuario.getId())) {
						reservasPorUsuario.add(reserva);
					}
					reservaService.save(reserva);
				}
			}
		}
		Collections.reverse(reservasPorUsuario);
		mv.addObject("reservas", reservasPorUsuario);

		boolean isAdmin = usuario.getPapeis().contains(papelService.findByNome("ROLE_ADMIN").get());
		mv.addObject("isAdmin", isAdmin);

		return mv;
	}

	// Faz a formatação da data
	private String formataData(Date disponibilidade) {
		String dataFormatada = DateFormatUtils.format(disponibilidade, "yyyy-MM-dd");

		return dataFormatada;
	}

	/* ***************************************************
	 * Exclusão de reservas feitas
	 * Habilita, na View, o botão de excluir uma reserva,
	 * caso a reserva NÃO esteja no estado "FINALIZADO"
	 *************************************************** */
	@PostMapping("/reservas/deletarReserva")
	public ModelAndView deletar(@RequestParam("id") Long id) {

		reservaService.deleteById(id);
		ModelAndView mv = new ModelAndView("redirect:/reservas");

		return mv;
	}

	/* ************************************************
	 * Efetuamento de reserva
	 * Ao efetuar uma reserva, atualiza o BD,
	 * linkando ao usuário que fez a reserva, adiciona a data e
	 * muda o status da reserva para "EM_ESPERA"
	 ************************************************* */
	@PostMapping("/reservas/efetuarReserva")
	public ModelAndView save(@RequestParam("livroId") Long livroId, RedirectAttributes redirectAttributes)
			throws MessagingException, IOException {
		
		Livro livro = livroService.findById(livroId).get();
		
		if(!reservaService.countReservaAguardandoPorUnidadeId(livroId)) {
			return new ModelAndView("redirect:/consulta/" +livro.getId());
		}

		GregorianCalendar dataReserva = new GregorianCalendar();

		Usuario usuario = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String email = auth.getName();
			usuario = usuarioService.findByEmail(email).orElse(null);
		}

		Date dataModificaStatus = new Date();

		Reserva reserva = new Reserva(usuario, dataReserva.getTime(), Status.EM_ESPERA, livro, dataModificaStatus);

		reservaService.save(reserva);

		return new ModelAndView("redirect:/reservas");
	}

	/* *************************************************************
	 * Fazer empréstimo quando o livro reservado estiver disponível
	 * Faz todo o processo normal do empréstimo
	 * Atualiza o status da reserva para "FINALIZADO"
	 * ************************************************************ */
	@PostMapping("/emprestimos/efetuarEmprestimoAposReserva")
	public ModelAndView emprestimoAposReserva(@RequestParam("reservaId") Long reservaId,
			RedirectAttributes redirectAttributes) throws MessagingException, IOException {

		Long unidadeId = reservaService.findUnidadeIdByReservaId(reservaId);

		UnidadeLivro unidade = unidadeService.findById(unidadeId).get();

		GregorianCalendar agora = new GregorianCalendar();

		String template = "email-emprestimo";

		GregorianCalendar prazo = new GregorianCalendar();
		prazo.add(Calendar.DAY_OF_MONTH, 7);

		Usuario usuario = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String email = auth.getName();
			usuario = usuarioService.findByEmail(email).orElse(null);
		}

		Emprestimo emprestimo = new Emprestimo(0L, agora.getTime(), null, prazo.getTime(), unidade, usuario,
				Status.ATIVO);

		unidade.getLivro().setStatusLivro(StatusLivro.COM_EMPRESTIMO);

		String assunto = "O " + emprestimo.getUnidadeLivro().getLivro().getTitulo() + " foi emprestado com sucesso !";
		emprestimoService.save(emprestimo);

		Reserva reserva = reservaService.findById(reservaId).get();

		reserva.setStatus(Status.FINALIZADO);
		reserva.setDataModificaStatus(new Date());

		reservaService.save(reserva);
		Mail email = emailService.enviarEmail(emprestimo.getUsuario(), emprestimo.getUnidadeLivro(), assunto);

		emailService.sendSimpleMessage(email, template);

		return new ModelAndView("redirect:/emprestimos");
	}

	/* *****************************************************************************
	 * Faz o cálculo da data prevista para a disponibilidade do livro reservado
	 * (prevista, pois pode acontecer do usuário que está com o livro entregá-lo antes,
	 * ou atrasar a entrega)
	 * ******************************************************************************/
	private Date calculaDisponibilidade(Emprestimo emprestimo) {

		if (emprestimo == null) {
			return new Date();
		} else if (emprestimo.getDataDevolucao() == null) {
			return emprestimo.getPrazoDevolucao();
		}

		GregorianCalendar data = new GregorianCalendar();

		data.setTime(emprestimo.getDataDevolucao());

		return data.getTime();
	}
}