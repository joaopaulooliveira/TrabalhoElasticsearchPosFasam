package br.com.fasam.pos.bigdata.MoviesSearchPos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.fasam.pos.bigdata.MoviesSearchPos.model.Filme;
import br.com.fasam.pos.bigdata.MoviesSearchPos.repository.Filmes;

@Controller
public class IndexController {
	@Autowired
	private Filmes filmes;

	@GetMapping(path = { "/", "/index", "/home" })
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("/index");
		List<Filme> topFilmes = filmes.getTopFilmes();
		mav.addObject("listaFilmes", topFilmes);
		System.out.println("Passando por index");
		return mav;
	}

	@GetMapping("/consulta")
	public ModelAndView consulta(@RequestParam("titulo") String titulo, @RequestParam("desc") String desc, @RequestParam("ano") Integer ano) {
		ModelAndView mav = new ModelAndView("/consulta");
		List<Filme> findedFilmes = filmes.getSearchFilmes(titulo, desc, ano);
		mav.addObject("listaFilmes", findedFilmes);
		System.out.println("Passando por consulta");
		return mav;
	}

}
