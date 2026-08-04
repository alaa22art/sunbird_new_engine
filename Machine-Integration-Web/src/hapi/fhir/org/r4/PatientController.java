/**
 * 
 */
package hapi.fhir.org.r4;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.core.common.helper.FilterablePageRequest;

/**
 * @author AHimour
 *
 */
@RestController
@RequestMapping("/hapi/fhir")
public class PatientController {
	
	@RequestMapping(value = "/patient.srvc", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getMachinePage(@RequestBody FilterablePageRequest filterablePageRequest) {

		//		log.info("##############  Application  ############## 11111 in machine controller");
		//		ActorSystem system = ActorSystem.create("Main");
		//		system.actorOf(Props.create(Master.class), "Master");
		//		log.info("##############  Application  ############## 22222");

		/*Map<String, Object> result = new HashMap<>();
		Page<Machine> pageable = machineService.getMachinePage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());*/

		//return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		
		return null;
	}
	
	@RequestMapping(value = "/runTest.srvc", method = RequestMethod.POST)
	public void test() {
		System.out.println("run test success!");
	}

}
