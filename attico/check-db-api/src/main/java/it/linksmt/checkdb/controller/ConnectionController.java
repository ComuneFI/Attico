package it.linksmt.checkdb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.checkdb.exception.BasicException;
import it.linksmt.checkdb.service.ConnectionService;
import it.linksmt.checkdb.util.ConnectionConstants;

@RestController
@RequestMapping(value = "/connection")
class ConnectionController {

	@Autowired
	private ConnectionService connectionService;

	private Logger log = LoggerFactory.getLogger(ConnectionController.class);

	@GetMapping("/vipCheck3307")
	public ResponseEntity<String> vipCheck3307() {
		try {
			return ResponseEntity.ok(connectionService.checkDB(ConnectionConstants.DB_PORT_3307, ConnectionConstants.DB_VIP));
		} catch (BasicException e) {
			log.error("ConnectionController :: vipCheck3307 ", e);
		}
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/vipCheck3306")
	public ResponseEntity<String> vipCheck3306() {
		try {
			return ResponseEntity.ok(connectionService.checkDB(ConnectionConstants.DB_PORT_3306, ConnectionConstants.DB_VIP));
		} catch (BasicException e) {
			log.error("ConnectionController :: vipCheck3306 ", e);
		}
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/nodo1Check3307")
	public ResponseEntity<String> nodo1Check3307() {
		try {
			return ResponseEntity.ok(connectionService.checkDB(ConnectionConstants.DB_PORT_3307, ConnectionConstants.DB_NODO_1));
		} catch (BasicException e) {
			log.error("ConnectionController :: nodo1Check3307 ", e);
		}
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/nodo1Check3306")
	public ResponseEntity<String> nodo1Check3306() {
		try {
			return ResponseEntity.ok(connectionService.checkDB(ConnectionConstants.DB_PORT_3306, ConnectionConstants.DB_NODO_1));
		} catch (BasicException e) {
			log.error("ConnectionController :: nodo1Check3306 ", e);
		}
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/nodo2Check3307")
	public ResponseEntity<String> nodo2Check3307() {
		try {
			return ResponseEntity.ok(connectionService.checkDB(ConnectionConstants.DB_PORT_3307, ConnectionConstants.DB_NODO_2));
		} catch (BasicException e) {
			log.error("ConnectionController :: nodo2Check3307 ", e);
		}
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/nodo2Check3306")
	public ResponseEntity<String> nodo2Check3306() {
		try {
			return ResponseEntity.ok(connectionService.checkDB(ConnectionConstants.DB_PORT_3307, ConnectionConstants.DB_NODO_2));
		} catch (BasicException e) {
			log.error("ConnectionController :: nodo2Check3306 ", e);
		}
		return ResponseEntity.badRequest().build();
	}
}