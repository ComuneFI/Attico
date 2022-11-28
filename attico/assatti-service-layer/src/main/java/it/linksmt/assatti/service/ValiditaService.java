package it.linksmt.assatti.service;

import org.springframework.stereotype.Service;

import it.linksmt.assatti.datalayer.domain.QValidita;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

@Service
public class ValiditaService {

	
	public BooleanExpression createPredicate(QValidita validita) {
		//LocalDate right = new LocalDate();
		//BooleanExpression b = validita.validodal.isNull().or( validita.validodal.goe(right));
		//BooleanExpression b2 = validita.validoal.isNull().or( validita.validoal.loe(right));
		BooleanExpression b =validita.validoal.isNull();
		return b;
	}
	
	public BooleanExpression validAoo(QValidita validita){
		BooleanExpression b =validita.validoal.isNotNull();
		return b;
	}
	
	 
}
