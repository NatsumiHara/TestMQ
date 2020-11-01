package jp.co.acom.fehub.mq;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("xml/DF000000000")
public class RizaStub {
	
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	String postMessage(@RequestBody String message) {		
		return message;
		
	}
	

}
