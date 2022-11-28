package it.linksmt.assatti.service;


import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import it.linksmt.assatti.datalayer.domain.Beneficiario;


/**
 * Service class for managing users.
 */
@Service
@Transactional
public class BeneficiarioService {
	@Inject
    private SpringTemplateEngine templateEngine;
	
	private static final String IT = "it";

	private final Logger log = LoggerFactory.getLogger(BeneficiarioService.class);

	public String getHtmlFromBeneficiari(List<Beneficiario> beneficiari){
		Locale locale = Locale.ITALY;
		Context context = new Context(locale);
        context.setVariable("beneficiari", beneficiari);
        return templateEngine.process("trascrizioneBeneficiari", context);
	}
	
}
