package com.nttdata.semana01.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
 
import com.nttdata.semana01.Entity.TypeCredits;
import com.nttdata.semana01.Service.TypeCreditsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/typeCredits")
public class TypeCreditsController {

	private final TypeCreditsService typeCreditsService;

	private Integer codigoValidator;

	@PostMapping
	public Mono<TypeCredits> createTypeCredits(@RequestBody TypeCredits typeCredits) {

		boolean validationvalue = this.ValidationRegisterTypeCreditsRequest(typeCredits);

		if (validationvalue) {

			try {

				var typeCreditsList = this.typeCreditsService.getTypeCreditsbyId(typeCredits.getId());

				List<TypeCredits> list1 = new ArrayList<>();

				typeCreditsList.flux().collectList().subscribe(list1::addAll);

				Thread.sleep(5 * 1000);

				log.info("Obtener valor para validar Id --->" + list1);

				codigoValidator = this.Validardor(list1);

				if (codigoValidator != 0 && codigoValidator.equals(typeCredits.getId())) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Id de Tipo Credito ya existe"));
				} else {
					return this.typeCreditsService.createTypeCredits(typeCredits);
				}

			} catch (Exception e) {
				System.out.println(e);
				return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
			}

		} else {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}

	}
	
	@GetMapping
    public Mono<ResponseEntity<Flux<TypeCredits>>>getAllCustomerType() {
        Flux<TypeCredits> list = this.typeCreditsService.getAllTypeCredits();
        return  Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(list)); 
    }

	public boolean ValidationRegisterTypeCreditsRequest(TypeCredits typeCredits) {

		boolean validatorTypeBankAccounts;

		if (typeCredits.getId() == null || typeCredits.getId() == 0) {
			validatorTypeBankAccounts = false;
		} else if (typeCredits.getDescription() == null || typeCredits.getDescription() == "") {
			validatorTypeBankAccounts = false;
		} else {
			validatorTypeBankAccounts = true;
		}

		return validatorTypeBankAccounts;
	}

	public Integer Validardor(List<TypeCredits> list1) {

		if (list1.isEmpty()) {
			codigoValidator = 0;
		} else {
			codigoValidator = list1.get(0).getId();
		}

		return codigoValidator;
	}

}
