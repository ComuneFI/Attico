package it.linksmt.assatti.gestatti.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.naming.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

/**
 * Handle the {@link MaxUploadSizeExceededException}.
 * @author Davide Pastore
 *
 */
@ControllerAdvice
public class MaxUploadSizeExceededExceptionHandler {

	private final Logger log = LoggerFactory
			.getLogger(MaxUploadSizeExceededExceptionHandler.class);

	@ExceptionHandler({ MaxUploadSizeExceededException.class,
			SizeLimitExceededException.class, MultipartException.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> handleMaxUploadSizeExceededException(
			Exception ex) {

		Map<String, Object> result = new HashMap<>();
		result.put("timestamp", new Long(System.currentTimeMillis()));
		result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		result.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		result.put("exception", ex.getMessage());
		result.put("message", "Le dimensioni dell'allegato superano i limiti di 30 MB");
		// result.put("path", request.getContextPath());
		log.info("MaxUploadSizeExceededExceptionHandler :: File troppo grande");

		return result;
	}

}
