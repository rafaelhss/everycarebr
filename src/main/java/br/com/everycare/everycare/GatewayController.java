package br.com.everycare.everycare;

import br.com.everycare.everycare.gateway.service.UpdateService;
import br.com.everycare.everycare.gateway.telegram.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class GatewayController {

    @Autowired
    public UpdateService updateService;

    @RequestMapping(method= RequestMethod.POST, value="/update")
    public void ReceberUpdate(@RequestBody Update update) throws IOException {
        try {
            updateService.processUpdate(update);
        } catch (Exception e) {
            System.out.println("Excexxao ao processar coisa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
