package it.linksmt.assatti.bpm.dto;

import java.util.ArrayList;
import java.util.List;


public class TipoListaTaskDTO {

    protected List<TipoTaskDTO> task;

    public List<TipoTaskDTO> getTask() {
        if (task == null) {
            task = new ArrayList<TipoTaskDTO>();
        }
        return this.task;
    }

}
