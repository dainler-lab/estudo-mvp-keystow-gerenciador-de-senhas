package com.keystow.app.controller.form;

import com.keystow.app.service.enuns.StatusUsuario;
import lombok.Data;

import java.util.Collection;

@Data
public class StatusUpdateRequestAjax {

	private Collection<String> ids; // private String[] ids;

	private StatusUsuario status;

}
