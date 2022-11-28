package it.linksmt.assatti.datalayer.comparator;

import java.util.Comparator;

import it.linksmt.assatti.datalayer.domain.MateriaDl33;

public class MateriaDl33Comparator implements Comparator<MateriaDl33> {
    @Override
    public int compare(MateriaDl33 m1, MateriaDl33 m2) {
      return m1.getDenominazione().compareTo(m2.getDenominazione());
    }
} 
